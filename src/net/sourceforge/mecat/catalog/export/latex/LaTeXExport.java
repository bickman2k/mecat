/*
 *  MeCat - Media Catalog more information on
 *  http://mecat.sourceforge.net/
 *  Copyright (C) 2004, Stephan Richard Palm, All Rights Reserved.
 *
 *  This program is free software; you can redistribute it and/or modify 
 *  it under the terms of the GNU General Public License as published by 
 *  the Free Software Foundation; either version 2 of the License, or 
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but 
 *  WITHOUT ANY WARRANTY; without even the implied warranty of 
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 *  General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License 
 *  along with this program; if not, write to the Free Software Foundation, 
 *  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

/**  
 * Created on Jul 30, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.export.latex;

import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.nio.channels.FileChannel;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.ServiceUI;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.ResolutionSyntax;
import javax.print.attribute.standard.PrinterResolution;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import net.sourceforge.mecat.catalog.export.Export;
import net.sourceforge.mecat.catalog.export.ExportProfile;
import net.sourceforge.mecat.catalog.export.latex.list.LaTeXExportCol;
import net.sourceforge.mecat.catalog.export.latex.list.LaTeXExportDef;
import net.sourceforge.mecat.catalog.export.latex.list.LaTeXListDefinition;
import net.sourceforge.mecat.catalog.gui.ShowListing;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.medium.features.MultiChoiceFeature;
import net.sourceforge.mecat.catalog.option.LocalListener;
import net.sourceforge.mecat.catalog.option.LocalListenerEvent;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.WeakLocalListener;
import net.sourceforge.mecat.latex.LaTeXErrorException;
import net.sourceforge.mecat.latex.LaTeXOutputExecption;
import net.sourceforge.mecat.latex.LaTeXStreamGobbler;
import net.sourceforge.mecat.latex.LaTeXTimeoutException;
import net.sourceforge.mecat.latex.NoAnswerException;

public class LaTeXExport /*extends JPanel*/ extends Export implements LocalListener  {

    final static Map<Character, String> omega = new LinkedHashMap<Character, String>(){{
        put('\\', "$\\backslash$");
        put('{', "\\{");
        put('}', "\\}");
        put('^', "\\^{}");
        put('~', "\\~{}");
        put('_', "\\_");
        put('&', "\\&");
        put('#', "\\#");
        put('$', "\\$");
        put('%', "\\%");
    }};

    private static final String REG_SZ = "REG_SZ";

    public static String getMiKTeXPath() {
        try {
          Process process = Runtime.getRuntime().exec("reg query \"HKLM\\SOFTWARE\\MiK\\MiKTeX\\CurrentVersion\\MiKTeX\" /v \"Install Root\"");
          InputStream stream = process.getInputStream();
          BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

          String line;
          
          while (true) {
              line = reader.readLine();
              if (line == null)
                  return null;
              
              int pos = line.indexOf(REG_SZ);
              
              if (pos != -1)
                  return line.substring(pos + REG_SZ.length()).trim();
          }
        }
        catch (Exception e) {
          return null;
        }
      }
    
    class StreamGobbler extends Thread
	{
		InputStream is;
		String type;
		OutputStream os;
		
		StreamGobbler(InputStream is, String type, OutputStream redirect)
		{
			this.is = is;
			this.type = type;
			this.os = redirect;
		}
		
		public void run()
		{
			try
			{
				PrintWriter pw = new PrintWriter(os);
				
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line=null;
				while ( (line = br.readLine()) != null)
					pw.println(type + line);    
				
				pw.flush();
				
			} catch (IOException ioe)
			{
				ioe.printStackTrace();  
			}
		}
	}
	
	
/*	class ExportCol extends Vector<ExportDef>{
		public Measurement measurement;
		
		public ExportCol(Measurement measurement) {
			this.measurement = measurement;
		}
	}*/
	
//	ListModel list;
    ResourceBundle res = Options.getI18N(LaTeXExport.class);

	LaTeXExportProfile profile = new LaTeXExportProfile();

	LaTeXStreamGobbler latex = null;

    public LaTeXExport() {
        Options.addLocalListener(new WeakLocalListener(this));
    }

    public void stateChanged(LocalListenerEvent event) {
/*        // Set res to actual local _ uses allready the actual locale
        res = Options.getI18N(LaTeXExport.class);*/
    }

    
    public void giveProcessOutput(Process proc, String type) {
        // any error message?
        StreamGobbler errorGobbler = new
            StreamGobbler(proc.getErrorStream(), "[" + type + "-ERR] ", System.out);            
        
        // any output?
        StreamGobbler outputGobbler = new
            StreamGobbler(proc.getInputStream(), "[" + type + "-OUT] ", System.out);
            
        // kick them off
        errorGobbler.start();
        outputGobbler.start();
                                
        // any error???
        try {
            int exitVal = proc.waitFor();
            if (Options.verbosity >= 1)
                System.out.println("[" + type + "-END] " + res.getString("ExitValue") + ": " + exitVal);        
        }
        catch (InterruptedException e) {
        	e.printStackTrace();
        }
	}

    class PrintThread extends Thread {
        // Variables used if printing
        DocFlavor flavorPOSTSCRIPT = DocFlavor.INPUT_STREAM.POSTSCRIPT;
        DocFlavor flavorJPEG = DocFlavor.INPUT_STREAM.JPEG;
        PrintService prn_serv = null;
        final PrintRequestAttributeSet aset_ps = new HashPrintRequestAttributeSet();
        final PrintRequestAttributeSet aset_jpg = new HashPrintRequestAttributeSet();

        public PrintThread(PaperSize paperSize) {
            aset_ps.add(paperSize.mediaSizeName);
            aset_jpg.add(paperSize.mediaSizeName);
            PrinterResolution printerResolution = new PrinterResolution(300, 300, ResolutionSyntax.DPI);
            aset_jpg.add(printerResolution);
        }

        boolean broken = false;
    }
    
    boolean exportStopped = false;
    public class ExportStoppedException extends Exception{};
    
    public void stopExport() {
        exportStopped = true;
    }
    
	public void export() {
        int pagecount = 1;
        String MiKTeX_HOME = null;
        final boolean windows = System.getProperty("os.name").startsWith("Windows");
/*        if (windows) {
            MiKTeX_HOME = getMiKTeXPath();
            if (MiKTeX_HOME == null) { //TODO make windows print better
                JOptionPane.showMessageDialog(parent, res.getString("Necessary software MiKTeX could not be found."));
                return;
            }
        }*/
        
        final File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        
        // Store the information on the current language
        Locale oldLocale = Options.getCurrentLocale();

        exportStopped = false;
	    try {
	        if (list == null)
	            return;
            
            // Update status
            if (profile.listDefinitions.size() <= 1)
                fireExportStarted(1);
            else
                fireExportStarted(2);
            fireExportProgessSetTitle(0, res.getString("Export process"));
            fireExportProgessSetMessage(0, res.getString("Export initialisation"));
            
            // Check for unicode support
            try {
                if (Options.verbosity >= 2)
                    System.out.println(res.getString("Check for Unicode support."));
                latex = new LaTeXStreamGobbler(tmpDir);
                if (Options.DEBUG) {
                    latex.addMix(System.out);
                    latex.addInNoLen(System.out);
                }
                latex.start();
                latex.executeCommand("\\documentclass{" + profile.style + "}");
                latex.executeCommand("\\usepackage{ucs}");
                latex.executeCommand("\\begin{document}");
                latex.executeCommand("\\end{document}");
                latex.cautiousJoin();
            } catch (LaTeXOutputExecption e) {
                JOptionPane.showMessageDialog(parent, res.getString("Could not open output file for LaTeX."));
                fireExportFinished();
                return;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(parent, res.getString("Could not find unicode for latex."));
                fireExportFinished();
                return;
            }
            boolean utf8x = false;
            // Check for utf8 input encoding
            try {
                if (Options.verbosity >= 2)
                    System.out.println(res.getString("Check for utf8 input encoding."));
                latex = new LaTeXStreamGobbler(tmpDir);
                if (Options.DEBUG) {
                    latex.addMix(System.out);
                    latex.addInNoLen(System.out);
                }
                latex.start();
                latex.executeCommand("\\documentclass{" + profile.style + "}");
                latex.executeCommand("\\usepackage{ucs}");
                latex.executeCommand("\\usepackage[utf8]{inputenc}");
                latex.executeCommand("\\begin{document}");
                latex.executeCommand("\\end{document}");
                latex.cautiousJoin();
            } catch (Exception e) {
                utf8x = true;
                if (Options.verbosity >= 2)
                    System.out.println(res.getString("Try alternative utf8x definition."));
                try {
                    latex = new LaTeXStreamGobbler(tmpDir);
                    if (Options.DEBUG) {
                        latex.addMix(System.out);
                        latex.addInNoLen(System.out);
                    }
                    latex.start();
                    latex.executeCommand("\\documentclass{" + profile.style + "}");
                    latex.executeCommand("\\usepackage{ucs}");
                    latex.executeCommand("\\usepackage[utf8x]{inputenc}");
                    latex.executeCommand("\\begin{document}");
                    latex.executeCommand("\\end{document}");
                    latex.cautiousJoin();
                } catch (Exception e1) {
                    JOptionPane.showMessageDialog(parent, res.getString("Could not find input encoding for utf8."));
                    fireExportFinished();
                    return;
                }
            }
            
	        
	        latex = new LaTeXStreamGobbler(tmpDir);

            if (Options.DEBUG) {
                latex.addMix(System.out);
                latex.addInNoLen(System.out);
            }
	        
	        // If latex Output is an option than
	        // we have to write down all excuted steps
	        Writer writer = null;
	        if (profile.isActiveLaTeX()) {
	            try {
	                writer = new OutputStreamWriter(new FileOutputStream(new File(profile.getLaTeXFileName())), "utf8");
	            } catch (FileNotFoundException e) {
	                System.err.println("[ ERROR ] " + res.getString("Could not create file").replaceAll("\\[FILE\\]", profile.getLaTeXFileName()));
	            } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
	            latex.addInNoLen(writer);
	        }
	        
            final PaperSize paperSize = profile.paperSize;

            PrintThread printThread = null;
            
            if (profile.isPrint()) {
                printThread = new PrintThread(paperSize) {
                    public void run() {
                        LaTeXExport.this.fireExportShowsPrintOption();
                        final Set<PrintService> print_services = new java.util.HashSet<PrintService>();
                        final PrintService[] prn_services_ps =  PrintServiceLookup.lookupPrintServices (flavorPOSTSCRIPT, aset_ps);
                        if (prn_services_ps != null)
                            for (PrintService printService : prn_services_ps)
                                print_services.add(printService);
/*                        if (windows) { TODO make windows print better
                            final PrintService[] prn_services_jpg =  PrintServiceLookup.lookupPrintServices (flavorJPEG, aset_jpg);
                            if (prn_services_jpg != null)
                                for (PrintService printService : prn_services_jpg)
                                    print_services.add(printService);
                        }*/
                        if (print_services.isEmpty()) {
                            JOptionPane.showMessageDialog(parent, res.getString("There is no suitable printer accessible."));
                            fireExportFinished();
                            return;
                        }
                        final PrintService[] prn_services = new PrintService[print_services.size()];
                        print_services.toArray(prn_services);
                        prn_serv = ServiceUI.printDialog(null, 50, 50, prn_services, null, /*flavorPOSTSCRIPT*/ null, aset_ps);
                        if (prn_serv == null)
                            broken = true;
                        LaTeXExport.this.fireExportStoppedShowingPrintOption();
                    }
                };
                printThread.start();
            }
	        
	        latex.start();
	        latex.executeCommand("\\documentclass["  + ((paperSize.LaTeX_known)? paperSize + ", ":"") + profile.fontSize + "]{" + profile.style + "}");
	        latex.executeCommand("\\usepackage{colortbl}");
//	        latex.executeCommand("\\usepackage{hhline}");
//	        latex.executeCommand("\\usepackage{german}");
//	        latex.executeCommand("\\usepackage{isolatin1}");
	        // TODO Automaticaly choose the right language settings.
	        if (Options.getCurrentLocale() == Locale.GERMAN) {
	            latex.executeCommand("% German typesetting");
	            latex.executeCommand("\\usepackage{german}");
	        }
	        latex.executeCommand("\\usepackage{ucs}");
	        latex.executeCommand("\\usepackage[utf8" + ((utf8x)?"x":"") +"]{inputenc}");
	        latex.executeCommand("% European output encoding");
	        latex.executeCommand("\\usepackage[T1]{fontenc}");
	        latex.executeCommand("\\usepackage{textcomp}");
	        latex.executeCommand("\\special{papersize=" + paperSize.width + " " + paperSize.unit + "," + paperSize.height + " " + paperSize.unit + "}");
	        if (!paperSize.LaTeX_known) {
	            // LaTeX adds one inch to every margin, we have to adjust for that.
	            Double LaTeX_corrected_OddMargin = (paperSize.getOddSideMargin() - ((PaperSizeUnit.millimeter == paperSize.unit)?PaperSizeUnit.MillimeterPerInch:1));
	            Double LaTeX_corrected_EvenMargin = (paperSize.getEvenSideMargin() - ((PaperSizeUnit.millimeter == paperSize.unit)?PaperSizeUnit.MillimeterPerInch:1));
	            Double LaTeX_corrected_TopMargin = (paperSize.getTopMargin() - ((PaperSizeUnit.millimeter == paperSize.unit)?PaperSizeUnit.MillimeterPerInch:1));
	            latex.executeCommand("\\setlength{\\oddsidemargin}{" + LaTeX_corrected_OddMargin + " " + paperSize.unit + "}");
	            latex.executeCommand("\\setlength{\\evensidemargin}{" + LaTeX_corrected_EvenMargin + " " + paperSize.unit + "}");
	            latex.executeCommand("\\setlength{\\topmargin}{" + LaTeX_corrected_TopMargin + " " + paperSize.unit + "}");
	            latex.executeCommand("\\setlength{\\headheight}{" + paperSize.getHeadHeight()  + " " + paperSize.unit + "}");
	            latex.executeCommand("\\setlength{\\headsep}{" + paperSize.getHeadSep()  + " " + paperSize.unit + "}");
	            latex.executeCommand("\\setlength{\\topskip}{" + paperSize.getTopSkip()  + " " + paperSize.unit + "}");
	            latex.executeCommand("\\setlength{\\textheight}{" + paperSize.getTextHeight()  + " " + paperSize.unit + "}");
	            latex.executeCommand("\\setlength{\\textwidth}{" + paperSize.getTextWidth()  + " " + paperSize.unit + "}");
	            latex.executeCommand("\\setlength{\\footskip}{" + paperSize.getFootSkip()  + " " + paperSize.unit + "}");
	            latex.executeCommand("\\setlength{\\paperheight}{" + paperSize.getPaperHeight()  + " " + paperSize.unit + "}");
	            latex.executeCommand("\\setlength{\\paperwidth}{" + paperSize.getPaperWidth()  + " " + paperSize.unit + "}");
	        }
	        latex.executeCommand("\\begin{document}");
//	        latex.executeCommand("\\definecolor{Gray}{gray}{0.8}");
	        latex.executeCommand("\\begin{flushleft}");
	        
	        double textheight = latex.getLen("\\textheight");

	        /*
             * Stop the export process on demand
	         */
            if (exportStopped)
                throw new ExportStoppedException();
            
	        
	        int count = 0;
	        double overhead = 0;
	        for (LaTeXListDefinition listDefinition : profile.listDefinitions) {
                // Update status
                if (profile.listDefinitions.size() <= 1)
                    fireExportProgessSetMessage(0, res.getString("Preparing list"));
                else {
                    fireExportProgessSetMessage(0, res.getString("Creating list [1] of [2]").replaceAll("\\[1\\]", "" + (count + 1)).replaceAll("\\[2\\]", "" + profile.listDefinitions.size()));
                    fireExportUpdate(0, count, profile.listDefinitions.size());

                    fireExportProgessSetTitle(1, listDefinition.listName);
                    fireExportProgessSetMessage(1, res.getString("Preparing list"));
                    fireExportUpdate(1, 0, 1);
                }
                
                
                count++;
                if (Options.verbosity >= 2)
                    System.out.println("[TeX-Exp] " + res.getString("CreateListNr").replaceAll("\\[NUMBER\\]", String.valueOf((count))));
	            
	            // If the page is more then half full make new page
	            if (overhead > 0) { // (textheight/2)/2) {
	                latex.executeCommand("\\newpage");
	                overhead = 0;
                    pagecount++;
	            }
	            
	            overhead += latex.getheight("\\huge{" + tr(listDefinition.listName) + "}");
	            if (profile.style.equals("book"))
	                latex.executeCommand("\\chapter*{" + tr(listDefinition.listName) + "}");
	            else
	                latex.executeCommand("\\section*{" + tr(listDefinition.listName) + "}");
	            
                ShowListing list = null;
                
                if (listDefinition.isOverride()) {
                
                    // Set language from listDefinitition.listProperties
                    if (listDefinition.listProperties.activeLanguage())
                        Options.setCurrentLocale(listDefinition.listProperties.getLanguage());
                    else
                        Options.setCurrentLocale(oldLocale);
	            
//                // Construct listing from listDefinitition.listProperties
//	            FilterListing filterListe = new FilterListing(this.list.getSortedListing().getFilterListing().getListing());
//	            Filter orgFilter = this.list.getSortedListing().getFilterListing().getFilter();
//	            if (orgFilter == null)
//	                orgFilter = TrueFilter.TRUE;
//	            Filter filter;
//	            if (listDefinition.listProperties.activeFilter())
//	                filter = new AndFilter(orgFilter, listDefinition.listProperties.getFilter());
//	            else
//	                filter = orgFilter;
//	            filterListe.setFilter(filter);
//	            
//	            SortedListing sortedListe = new SortedListing(filterListe);
//	            if (listDefinition.listProperties.activeSorting())
//	                sortedListe.setComparator(listDefinition.listProperties.getSorting());
//	            else
//	                sortedListe.setComparator(this.list.getSortedListing().getComparator());
//	            
//	            ShowListing list = new ShowListing(sortedListe);

	            // Construct listing from listDefinitition.listProperties
                    list = listDefinition.listProperties.addFilterAndUseSorting(this.list);
                } else {
                    Options.setCurrentLocale(oldLocale);
                    list = this.list;
                }
                   
	            
	            String last, cmd;
	            
	            int start = 0;
	            
	            int min, max = 1, test;
	            
                // Update status
                if (profile.listDefinitions.size() <= 1) {
                    fireExportUpdate(0, 0, list.getSize());
                    fireExportProgessSetMessage(0, res.getString("[1] of [2]").replaceAll("\\[1\\]", "0").replaceAll("\\[2\\]", "" + list.getSize()));
                } else {
                    fireExportUpdate(1, 0, list.getSize());
                    fireExportProgessSetMessage(1, res.getString("[1] of [2]").replaceAll("\\[1\\]", "0").replaceAll("\\[2\\]", "" + list.getSize()));
                }

                boolean firstRound = true;
                while (start < list.getSize()){

                    /*
                     * Stop the export process on demand
                     */
                    if (exportStopped)
                        throw new ExportStoppedException();
	                
                    if (!firstRound) {
                        latex.executeCommand("\\newpage");
                        overhead = 0;
                        pagecount++;
                        // If the page is more then 3/4 full from the title or title + rest of last list 
                        // then make a new page
                    } else if (overhead > (textheight/2)*3/4) {
	                    latex.executeCommand("\\newpage");
	                    overhead = 0;
                        pagecount++;
	                }
	                
	                min = 0;
	                
	                // Max will only as big as the list allows it to
	                if (start + max > list.getSize() - 1)
	                    max = list.getSize() - 1 - start;
	                
	                while ((min != max) && (latex.getheight(getCommand(start, start + max, listDefinition, list)) + overhead < textheight/2)) {
	                    min = max;
	                    max *= 2;
	                    // Max will only as big as the list allows it to
	                    if (start + max > list.getSize() - 1)
	                        max = list.getSize() - 1 - start;
	                }
	                
	                while (min < max - 1) {
	                    // Next test will be in the middle between 
	                    // min - we know this still fits - and
	                    // max - we know this doesn't fit anymore				
	                    test = (min + max) / 2;
	                    // Make the test is neither min nor max
	                    if (test == min)
	                        test++;
	                    if (test == max)
	                        test--;
	                    // Check wether the test fits on the page
	                    // and change min or max accordingly
	                    if (latex.getheight(getCommand(start, start + test, listDefinition, list)) + overhead < textheight/2)
	                        min = test;
	                    else
	                        max = test;
	                }
	                
                    if (Options.verbosity >= 2)
                        System.out.println("[TeX-Exp] " + res.getString("entries on page.").replaceAll("\\[NUMBER\\]", String.valueOf((min + 1))));
	                overhead = latex.getheight(getCommand(start, start + min, listDefinition, list));
	                latex.executeCommand(getCommand(start, start + min, listDefinition, list));
                    
                    // Update status
                    if (profile.listDefinitions.size() <= 1) {
                        fireExportUpdate(0, start + min + 1, list.getSize());
                        fireExportProgessSetMessage(0, res.getString("[1] of [2]").replaceAll("\\[1\\]", "" + (start + min + 1)).replaceAll("\\[2\\]", "" + list.getSize()));
                    } else {
                        fireExportUpdate(1, start + min + 1, list.getSize());
                        fireExportProgessSetMessage(1, res.getString("[1] of [2]").replaceAll("\\[1\\]", "" + (start + min + 1)).replaceAll("\\[2\\]", "" + list.getSize()));
                    }

                    start = start + min + 1;
	                // With 2/3 the result is expected in the middle of the first two steps.
	                // Means the first of the two while loops should be entered exactly one time 
	                max = (min * 2)/3;
                    
                    firstRound = false;
	            }
	        }

            // Restore language from before Export
	        Options.setCurrentLocale(oldLocale);
	        
            if (profile.listDefinitions.size() <= 1) {
                fireExportProgessSetMessage(0, res.getString("Finishing"));
            }
            else {
                fireExportProgessSetMessage(0, res.getString("Finishing"));
                fireExportUpdate(0, 1, 1);
                fireExportProgessSetTitle(1, null);
            }

            latex.executeCommand("\\end{flushleft}");
	        latex.executeCommand("\\end{document}");
            latex.cautiousJoin();
	        
	        if (profile.isActiveLaTeX()) {
	            try {
	                writer.flush(); 
	                writer.close();
	            } catch (IOException e) {
	                System.err.println("[ ERROR ] " + res.getString("Unexpected Exception while writing file").replaceAll("\\[FILE\\]", profile.getLaTeXFileName()));
	            }
	        }

            // Make a list of all Processes
            // this is necessary in order to 
            // multithread the different processes
            Vector<Process> processes = new Vector<Process>();
            
	        // Create all wanted files
	        if (profile.isActivePDF())
	            try {
                    if (Options.verbosity >= 2)
                        System.out.println("[TeX-Exp] " +System.getProperty("os.name"));
	                // Depending on the Operation system, we'll start dvipdf or dvipdfm
	                
	                String cmds[];
	                if (System.getProperty("os.name").toLowerCase().startsWith("win"))
	                    cmds = new String[]{"dvipdfm", "-o" , profile.getPDFFileName(), "texput.dvi"};
	                else
	                    cmds = new String[]{"dvipdf", "texput.dvi", profile.getPDFFileName()};
	                
                    if (Options.verbosity >= 1) {
                        System.out.print("[TeX-Exp] ");
                        for (String s : cmds)
                            System.out.print(s + " ");
                        System.out.println();
                    }
	                
	                Process proc = Runtime.getRuntime().exec(cmds, null, tmpDir);
                    if (Options.verbosity >= 2)
                        giveProcessOutput(proc, "PDF");
                    processes.add(proc);

	            } catch (IOException e) {
	                System.err.println("[ ERROR ] " + res.getString("Unexpected Exception while writing file").replaceAll("\\[FILE\\]", profile.getPDFFileName()));
	            }
            if (profile.isActivePS() || profile.isPrint())
                try {
                    String cmds[] = {"dvips",  "texput.dvi", "-o", 
                            (profile.isActivePS())?
                                    profile.getPSFileName():
                    (new File(tmpDir, "texput.ps")).getPath()};
                    
                    if (Options.verbosity >= 1) {
                        System.out.print("[TeX-Exp] ");
                        for (String s : cmds)
                            System.out.print(s + " ");
                        System.out.println();
                    }
                    
                    Process proc = Runtime.getRuntime().exec(cmds, null, tmpDir);
                    if (Options.verbosity >= 1)
                        giveProcessOutput(proc, " PS");
                    processes.add(proc);
                    
                } catch (IOException e) {
                    System.err.println("[ ERROR ] " + res.getString("Unexpected Exception while writing file").replaceAll("\\[FILE\\]", profile.getPSFileName()));
                }
            if (profile.isActiveDVI()) {
                File fileSrc = new File(tmpDir, "texput.dvi");
                File fileDest = new File(profile.getDVIFileName());
                if (!fileSrc.renameTo(fileDest)) {
                    try {
                        FileChannel chnSrc = new FileInputStream(fileSrc).getChannel();
                        FileChannel chnDest = new FileOutputStream(fileDest).getChannel();
                        
                        chnDest.transferFrom(chnSrc, 0, chnSrc.size());
                        
                        chnSrc.close();
                        chnDest.close();
                    } catch (IOException e) {
                        System.err.println("[ ERROR ] " + res.getString("Unexpected Exception while writing file").replaceAll("\\[FILE\\]", profile.getDVIFileName()));
                    }                    
                }
            }
            if (profile.isPrint()) {
                // Wait for all processes to be terminated before
                // processing with print
                for (Process proc : processes)
                    try {
                        proc.waitFor();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                
                try {
                    printThread.join();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                if (printThread.prn_serv != null) {
                    final DocPrintJob prn_job = printThread.prn_serv.createPrintJob();
                    if (prn_job.getPrintService().isDocFlavorSupported(printThread.flavorPOSTSCRIPT)/* && !windows*/)
                        try {
                            FileInputStream ps_stream = new FileInputStream((profile.isActivePS()) ? new File(profile.getPSFileName()) : new File(tmpDir, "texput.ps"));
                            Doc doc = new SimpleDoc (ps_stream, printThread.flavorPOSTSCRIPT, null);
                            prn_job.print( doc, printThread.aset_ps);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (PrintException e) {
                            e.printStackTrace();
                        }
                    else { // TODO else is windows and is inactive at this time
                        int dpi = 300;
/*                        PrinterResolution printerResolution = (PrinterResolution) prn_job.getAttributes().get(PrinterResolution.class);
                        if (printerResolution != null) {
                            int res[] = printerResolution.getResolution(PrinterResolution.DPI);
                            dpi = Math.max(res[0], res[1]);
                            if (dpi < 100)
                                dpi = 100;
                        }*/
                        final int DPI = dpi;

                        for (int i = 0; i < pagecount; i++){
                        
                            String cmds_psselect[] = {"psselect", "-p" + (i + 1), 
                                    (profile.isActivePS())?
                                            profile.getPSFileName():
                                                (new File(tmpDir, "texput.ps")).getPath(),
                                                (new File(tmpDir, "single_page_" + (i + 1) +".ps")).getPath()
                            };
                            String cmds_mgs[] = {"mgs", "-sDEVICE=jpeg", "-dBATCH", "-dNOPAUSE", "-r" + dpi, "-sOutputFile=" + (new File(tmpDir, "single_page_" + (i + 1) +".jpeg")).getPath(), 
                                                (new File(tmpDir, "single_page_" + (i + 1) +".ps")).getPath()};
                            
                            try {
                                if (Options.verbosity >= 1) {
                                    System.out.print("[TeX-Exp] ");
                                    for (String s : cmds_psselect)
                                        System.out.print(s + " ");
                                    System.out.println();
                                }

                                Process proc = Runtime.getRuntime().exec(cmds_psselect, null, tmpDir);
                                if (Options.verbosity >= 1)
                                    giveProcessOutput(proc, "sel");
                                processes.add(proc);
                                proc.waitFor();
                                
                                if (Options.verbosity >= 1) {
                                    System.out.print("[TeX-Exp] ");
                                    for (String s : cmds_mgs)
                                        System.out.print(s + " ");
                                    System.out.println();
                                }

                                proc = Runtime.getRuntime().exec(cmds_mgs, null, new File(MiKTeX_HOME, "ghostscript/base"));
                                if (Options.verbosity >= 1)
                                    giveProcessOutput(proc, "jpg");
                                processes.add(proc);
                                proc.waitFor();

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                System.err.println("[ ERROR ] " + res.getString("Unexpected Exception while writing file").replaceAll("\\[FILE\\]", "single_page_" + (i + 1) +".jpeg"));
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                        
                        try {
                            final int pages = pagecount;
                            Pageable jpegs = new Pageable() {

                                public int getNumberOfPages() {
                                    return pages;
                                }

                                public PageFormat getPageFormat(int num) throws IndexOutOfBoundsException {
                                    Paper paper = new Paper();
                                    paper.setSize(paperSize.getWidthInInches() * DPI, paperSize.getHeightInInches() * DPI);
                                    paper.setImageableArea(0, 0, paperSize.getWidthInInches() * DPI, paperSize.getHeightInInches() * DPI);
                                    PageFormat pf = new PageFormat();
                                    pf.setPaper(paper);
                                    return pf;
                                }

                                public Printable getPrintable(int num) throws IndexOutOfBoundsException {
                                    return new Printable() {
                                        public int print(Graphics g, PageFormat pf, int num) throws PrinterException {
                                            System.out.println(pf.getWidth() + ", " + pf.getHeight() + ", " + pf.getImageableWidth() + ", " + pf.getImageableHeight());
                                            try {
                                                ImageIcon image = new ImageIcon(new File(tmpDir, "single_page_" + (num + 1) +".jpeg").toURL());
                                                g.drawImage(image.getImage(), 0, 0, null);
                                                return Printable.PAGE_EXISTS;
                                            } catch (MalformedURLException e) {
                                                e.printStackTrace();
                                                return Printable.NO_SUCH_PAGE;
                                            }
                                        }
                                    };
                                }
                                
                            };
                            
                            Doc doc = new SimpleDoc (jpegs, DocFlavor.SERVICE_FORMATTED.PAGEABLE, null);
                            prn_job.print( doc, printThread.aset_jpg);
                        } catch (PrintException e) {
                            e.printStackTrace();
                        }
                        
                    }
                        
                }
            }
            
            // Wait for all processes to be terminated before
            // deleting the files they depend upon
            for (Process proc : processes)
                try {
                    proc.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
            //Remove all intermediate files
            (new File(tmpDir, "texput.dvi")).delete();
            (new File(tmpDir, "texput.aux")).delete();
            (new File(tmpDir, "texput.log")).delete();
            (new File(tmpDir, "texput.jpeg")).delete();
            if (!profile.isActivePS() && profile.isPrint())
                (new File(tmpDir, "texput.ps")).delete();
            
            fireExportProgessSetMessage(0, res.getString("Export finished"));
            fireExportFinished();
        } catch (LaTeXOutputExecption e) {
            JOptionPane.showMessageDialog(parent, res.getString("Could not open output file for LaTeX."));
            fireExportFinished();
            return;
	    } catch (NoAnswerException e){
            JOptionPane.showMessageDialog(parent, res.getString("Timeout while waiting for expected response."));
	        e.printStackTrace();
            fireExportFinished();
        } catch (LaTeXTimeoutException e){
            JOptionPane.showMessageDialog(parent, res.getString("Timeout while waiting for LaTeX process."));
            e.printStackTrace();
            fireExportFinished();
	    } catch (LaTeXErrorException e) {
            JOptionPane.showMessageDialog(parent, res.getString("LaTeX Error: [ERROR]".replaceAll("\\[ERROR\\]", e.getMessage())));
            e.printStackTrace();
            fireExportFinished();
        } catch (ExportStoppedException e) {
            // Restore language from before Export
            Options.setCurrentLocale(oldLocale);

            //Remove all intermediate files
            (new File("texput.dvi")).delete();
            (new File("texput.aux")).delete();
            (new File("texput.log")).delete();
            if (!profile.isActivePS() && profile.isPrint())
                (new File("texput.ps")).delete();
            
            fireExportStopped();
        }
	}

	public void cutLowest(SortedMap<String, Integer> map) {
		Set<String> kset = map.keySet();
		int min = Collections.min(map.values()).intValue();
		for(String key : kset)
			if (map.get(key).intValue() == min) {
				map.remove(key);
				return;
			}
	}
	
	  final public String nf(int i, String f) {
	    StringBuffer s = new StringBuffer("");
	    for (int j = 0; j < i; j++)
	      s.append(f);

	    return (s.toString());
	  }
	  static void replace_all(StringBuffer Str, String Search, String Replace){
	    int i;
	    while ((i = Str.toString().indexOf(Search)) != -1)
	      Str.replace(i, Search.length()+i, Replace);
	  }
	  final public static String tr(String input) {
          StringBuffer buf = new StringBuffer();
          for (int i = 0; i < input.length(); i++) {
              if (omega.get(input.charAt(i)) != null)
                  buf.append(omega.get(input.charAt(i)));
              else
                  buf.append(input.charAt(i));
          }
          return buf.toString();
	  }

	public String getColumnsCmd(Medium medium, LaTeXListDefinition listDefinition) {
		StringBuffer cmd = new StringBuffer();
		for (LaTeXExportCol column : listDefinition.columns) {
			cmd.append(getColumnCmd(medium, column));
			cmd.append(" & ");
		}
		return cmd.toString();
	}

	public double getColumnMaxWidth(LaTeXExportCol column, ShowListing list) throws NoAnswerException, LaTeXErrorException {
		return getColumnMaxWidth(column, 0, list.getSize(), list);
	}
	public double getColumnMaxWidth(LaTeXExportCol column, int start, int end, ShowListing list) throws NoAnswerException, LaTeXErrorException {
		return getColumnWidths(column, start, end, list).last().doubleValue();
	}
	
	public double getColumnAverageWidth(LaTeXExportCol column, int start, int end, ShowListing list) throws NoAnswerException, LaTeXErrorException {
		return getAverage(getColumnWidths(column, start, end, list));
	}
	
	public double getColumnNormalizedAverageWidth(LaTeXExportCol column, int start, int end, double cutFactor, ShowListing list) throws NoAnswerException, LaTeXErrorException {
		return getNormalizedAverage(getColumnWidths(column, start, end, list), cutFactor);
	}
	
	public double getNormalizedAverage(Collection<Double> col, double cutFactor) {
		if (cutFactor >= 0.5)
			return getAverage(col); 

		int cut = (int)(cutFactor*col.size());
		double sum = 0;
		Double colarray[] = col.toArray(new Double[col.size()]);
		for (int i = cut; i < col.size() - cut; i++)
			sum += colarray[i].doubleValue();
		return sum / (col.size() - cut*2);
	}
	
	public double getAverage(Collection<Double> col) {
		double sum = 0;
		for (Double d : col)
			sum += d.doubleValue();
		return sum / col.size();
	}
	
	public SortedSet<Double> getColumnWidths(LaTeXExportCol column, int start, int end, ShowListing list) throws NoAnswerException, LaTeXErrorException {
		SortedSet<Double> set = new TreeSet<Double>();
		for (int j = start; j <= end; j++) 
			set.add(latex.getwidth(getColumnCmd((Medium)list.getElementAt(j), column)));
		return set;
	}
	
	public String getColumnCmd(Medium medium, LaTeXExportCol column) {
		StringBuffer cmd = new StringBuffer();
		Feature f;
		for (LaTeXExportDef feature : column) {
			switch (feature.type) {
				case SHORT: 
					f = (Feature)medium.getFeature(feature.feature);
					if (f == null)
						break;
					if (f.getShortText() == null)
						break;
					cmd.append(tr(f.getShortText()));
					break;
				case FULL: 
					f = (Feature)medium.getFeature(feature.feature);
					if (f == null)
						break;
					if (f.getText() == null)
						break;
					cmd.append(tr(f.getText()));
					break;
				case LATEX: 
					if (feature.latex_cmd == null)
						break;
					cmd.append(feature.latex_cmd);
					break;
				case MEDIUM:
					cmd.append(medium.getName());
                case NAME:
                    cmd.append(medium.displayName());
				}
		}
		return cmd.toString();
	}

	public Double[] computeActualColumnsWidths(double colwidth, int start, int end, LaTeXListDefinition listDefinition, ShowListing list) throws NoAnswerException, LaTeXErrorException {
		Vector<Double> cols = new Vector<Double>();
		double fix = 0;
		double var = 0;
		
		for (LaTeXExportCol column : listDefinition.columns) {
			switch (column.measurement.type) {
				case NOLINEBREAK:
					fix += getColumnMaxWidth(column, start, end, list);
					break;
				case FIXEDLEN:
					fix += column.measurement.collen;
					break;
				case VARLEN:
					var += getColumnNormalizedAverageWidth(column, start, end, 0.05, list);
					break;
			}
		}
		
		double varrest = colwidth - fix;
		double factor = (var > 0)?varrest / var:0;
		
		
		for (LaTeXExportCol column : listDefinition.columns) {
			switch (column.measurement.type) {
				case NOLINEBREAK:
					cols.add(getColumnMaxWidth(column, start, end, list));
					break;
				case FIXEDLEN:
					cols.add(column.measurement.collen);
					break;
				case VARLEN:
					cols.add(getColumnNormalizedAverageWidth(column, start, end, 0.05, list)*factor);
					break;
			}
		}
		
		return cols.toArray(new Double[cols.size()]);		
	}
	
	
	public String getCommand(int start, int end, LaTeXListDefinition listDefinition, ShowListing list) throws NoAnswerException, LaTeXErrorException {

		String gnrs[] = getChoicesMap(start, end, listDefinition, list);
		Double tablessizes[] = new Double[listDefinition.columns.size()];
		for (int i = 0; i < listDefinition.columns.size(); i++)
			tablessizes[i] = 100.0;
		String cmd = makeTableDef(tablessizes, gnrs);
		// And we have to put in an entry so it will be drawn fully
		cmd += nf(gnrs.length + listDefinition.columns.size(), "&") + "\\\\ \\end{tabular}";
		
		double overhead = latex.getwidth(cmd.toString()) - listDefinition.columns.size()*100;
		double textwidth = latex.getLen("\\textwidth");

//		System.out.println(overhead);
//		System.out.println(textwidth);
		
		double columnswidth = textwidth - overhead - 1;

/*		for (int i = 0; i < columns.size(); i++)
			tablessizes[i] = columnswidth/columns.size();*/
		StringBuffer cmd2 = new StringBuffer(makeTableDef(computeActualColumnsWidths(columnswidth, start, end, listDefinition, list), gnrs));
		boolean colored = false;
		for (int j = start; j <= end; j++, colored = !colored) {
			MultiChoiceFeature mcf = (MultiChoiceFeature)((Medium)(list.getElementAt(j))).getFeature(listDefinition.getMcfeature().getClasstype());
			if (colored)
				cmd2.append("\\rowcolor[gray]{.8}");
			
			cmd2.append(getColumnsCmd((Medium)list.getElementAt(j), listDefinition));
			
			if ((mcf == null) && (gnrs.length != 0)){
				cmd2.append("\\multicolumn{" + gnrs.length + "}{" );
				if (colored)
					cmd2.append(">{\\columncolor[gray]{.8}}");
				cmd2.append("c|}{$\\!\\!\\!\\!$");
				cmd2.append(nf(gnrs.length, "--"));
				cmd2.append("$\\!\\!\\!\\!$}");
			}
	        else for (int i = 0; i < gnrs.length; i++) {
	        	if (mcf.exists(gnrs[i]))
	        		cmd2.append("$\\!\\!\\textnormal{x}\\!\\!\\!$");
	        	if (i < gnrs.length - 1)
	        		cmd2.append("& ");
	        }	

	        cmd2.append("\\\\");
		}
		cmd2.append("\\end{tabular}");
		
		return cmd2.toString();
	}

	public String[] getChoicesMap(int start, int end, LaTeXListDefinition listDefinition, ShowListing list) {
		SortedMap<String, Integer> ChoicesMap = new TreeMap<String, Integer>();
		
		if (listDefinition.getMcfeature() == null)
			return new String[]{};
		
		// Fill ChoicesMap with the occuring number of Choices.
		for (int j = start; j <= end; j++) {
			MultiChoiceFeature mcf = (MultiChoiceFeature)((Medium)(list.getElementAt(j))).getFeature(listDefinition.getMcfeature().getClasstype());
			if (mcf != null) {
				Vector<String> choices = mcf.getChoices();
				for (int i = 0; i < choices.size(); i++)
					if (mcf.exists(choices.elementAt(i)))
						if (ChoicesMap.containsKey(choices.elementAt(i)))
							ChoicesMap.put(choices.elementAt(i), ChoicesMap.get(choices.elementAt(i))+1);
						else
							ChoicesMap.put(choices.elementAt(i), 1);
			}
		}

		
		// If there are to much Choices cut those with the lowest frequency
		while (ChoicesMap.size() > listDefinition.getMaxChoices())
			cutLowest(ChoicesMap);
		
		return ChoicesMap.keySet().toArray(new String[ChoicesMap.size()]);
	}
		
		
	public String makeTableDef(Double colsize[], String gnrs[]) {

		if (gnrs.length == 0) {
			StringBuffer cmd = new StringBuffer("\\begin{tabular}{");
			for (int i = 0; i < colsize.length; i++)
				cmd.append("p{"+colsize[i]+"pt}");
			cmd.append("l}");
			return cmd.toString();
		}
		
		// Create some abriviations
		int total = gnrs.length;
		int lines = (int)(total / 2);
		boolean center = (lines * 2 != total);
		int cl = colsize.length;

		// Make the definition of the table
		StringBuffer cmd = new StringBuffer("\\begin{tabular}{");
		for (int i = 0; i < colsize.length; i++)
			cmd.append("p{"+colsize[i]+"pt}");
		cmd.append("|");
		for (int i = 0; i < total; i++)
			cmd.append("c|");
		cmd.append("l}");

		// nf(cl-1, "&") Start with the last columns designated to text
		// \\multicolumn{1}{c}{} disabels the first vertical line 
		// nf(gnrs[lines].length()*2, "$\\!$") will bring the size of the entry new to 0
		// this is necessary for a tiny total like 3, otherwise the table will be
		// disordered. The last column would be to large
		//
		if (center)
			cmd.append(nf(cl-1, "&") + "\\multicolumn{1}{c}{} &\\multicolumn{" 
					+ total + "}{c}{" +nf(gnrs[lines].length()*2, "$\\!$") + gnrs[lines]+nf(gnrs[lines].length()*2, "$\\!$") + "}& \\\\");

		cmd.append("\\cline{" + cl + "-"+ (cl + lines) +"}\\cline{" + (total + cl + 1 - lines) + "-"+ (total + cl + 1)+"}");
		for (int i = 0; i < lines; i++) {
			cmd.append(nf(cl-1, "&") + "\\multicolumn{"+ (lines + 1 - i) +"}{r|}{"
	                  + gnrs[lines - i - 1]
	                  + "}"
	                  + nf((i+1)*2 - ((center)?0:1) , "&" )
	                  + "\\multicolumn{"+ (lines + 1 - i) +"}{l}{"
	                  + gnrs[ total - lines + i]
	                  + "}\\\\"
	                  );
			cmd.append("\\cline{" + cl + "-"+ (lines+cl-1-i) +"}\\cline{" + (total + cl+2 - lines + i) + "-"+ (total + cl+1)+"}");
		}
		cmd.append(nf(cl+total, "&") + "\\\\");

		return cmd.toString();
	}
	

/*	public JPanel options(final TotalPreferences totalPreferences) {
		return profile.options(totalPreferences);
	}*/
	
	public boolean setProfile(ExportProfile profile) {
		if (!(profile instanceof LaTeXExportProfile))
			return false;
	
		this.profile = (LaTeXExportProfile)profile;
		
		return true;
	}

	public ExportProfile getProfile() {
		return profile;
	}

}
