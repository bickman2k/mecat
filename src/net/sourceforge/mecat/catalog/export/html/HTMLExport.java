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
 * Created on Aug 1, 2005
 * @author Stephan Richard Palm
 * Copyright by Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.export.html;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.JOptionPane;

import net.sourceforge.mecat.catalog.datamanagement.xml.XMLCatalog;
import net.sourceforge.mecat.catalog.export.Export;
import net.sourceforge.mecat.catalog.export.ExportProfile;
import net.sourceforge.mecat.catalog.export.html.list.HTMLExportCol;
import net.sourceforge.mecat.catalog.export.html.list.HTMLExportDef;
import net.sourceforge.mecat.catalog.export.html.list.HTMLListDefinition;
import net.sourceforge.mecat.catalog.gui.ShowListing;
import net.sourceforge.mecat.catalog.gui.features.desktop.view.ViewDesktop;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.medium.features.impl.Ident;
import net.sourceforge.mecat.catalog.option.LocalListener;
import net.sourceforge.mecat.catalog.option.LocalListenerEvent;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.WeakLocalListener;

public class HTMLExport extends Export implements LocalListener {

    ResourceBundle res = Options.getI18N(HTMLExport.class);
    
    public HTMLExport() {
        Options.addLocalListener(new WeakLocalListener(this));
    }

    public void stateChanged(LocalListenerEvent event) {
/*        // Set res to actual local _ uses allready the actual locale
        res = Options.getI18N(HTMLExport.class);*/
    }
    
//    PrintWriter index = null;
    
    boolean exportStopped = false;
    class ExportStoppedException extends Exception{};
    
    public void stopExport() {
        exportStopped = true;
    }

    static public String TO_ASCII(String str) {
        StringBuffer buf = new StringBuffer();
        
        for (char ch : str.toCharArray())
            buf.append(TO_ASCII(ch));
        
        return buf.toString();
    }
    
    static public String TO_ASCII(final char ch) {
        if ((ch >= 'a') && (ch <= 'z'))
            return ""+ch;
        if ((ch >= 'A') && (ch <= 'Z'))
            return ""+ch;
        if ((ch >= '0') && (ch <= '9'))
            return ""+ch;
        
        if (ch == '\u00C6')
            return "AE";
        if (ch == '\u00C4')
            return "Ae";
        if ((ch >= '\u00C0') && (ch <= '\u00C5'))
            return "A";
        if (ch == '\u00C7')
            return "C";
        if ((ch >= '\u00C8') && (ch <= '\u00CB'))
            return "E";
        if ((ch >= '\u00CC') && (ch <= '\u00CF'))
            return "I";
        if (ch == '\u00D0')
            return "ETH";
        if (ch == '\u00D0')
            return "N";
        if ((ch >= '\u00D2') && (ch <= '\u00D5'))
            return "0";
        if (ch == '\u00D6')
            return "Oe";
        if ((ch >= '\u00D9') && (ch <= '\u00DB'))
            return "U";
        if (ch == '\u00DC')
            return "Ue";
        if (ch == '\u00DD')
            return "Y";
        if (ch == '\u00DE')
            return "THORN";
        if (ch == '\u00DF')
            return "ss";
        if ((ch == '\u00E4') || (ch == '\u00E6'))
            return "ae";
        if ((ch >= '\u00E0') && (ch <= '\u00E5'))
            return "a";
        if (ch == '\u00E7')
            return "c";
        if ((ch >= '\u00E8') && (ch <= '\u00EB'))
            return "e";
        if ((ch >= '\u00EC') && (ch <= '\u00EF'))
            return "i";
        if (ch == '\u00F0')
            return "eth";
        if (ch == '\u00F1')
            return "n";
        if ((ch >= '\u00F2') && (ch <= '\u00F5'))
            return "o";
        if (ch == '\u00F6')
            return "oe";
        if (ch == '\u00F8')
            return "o";
        if ((ch >= '\u00F9') && (ch <= '\u00FB'))
            return "u";
        if (ch == '\u00FC')
            return "ue";
        if (ch == '\u00FD')
            return "y";
        if (ch == '\u00FE')
            return "thorn";
        if (ch == '\u00FF')
            return "y";

        return "_";
    }
    
    
    
    public void exportList(String path, String indexName, HTMLListDefinition listDefinition, ShowListing list, Set<Medium> mediaWithinList) throws IOException, ExportStoppedException {
        OutputStreamWriter listIndex = new OutputStreamWriter(new FileOutputStream(new File(path + indexName + ".html")), "UTF8");
        
        listIndex.write("<html><head>" 
                + "<META http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">"
                + "<title>" + res.getString("Export from MeCat") + "</title></head><body>");
        listIndex.write("<table><colgroup>");
        for (HTMLExportCol exportCol : listDefinition.columns) {
            listIndex.write("<col width=\"");
            switch (exportCol.measurement.type) {
            case FIXEDLEN:
                listIndex.write("" + exportCol.measurement.collen);
                break;
            case PERCENT:
                listIndex.write(exportCol.measurement.collen + "%");
                break;
            case PROPORTION:
                listIndex.write(exportCol.measurement.collen + "*");
                break;
            }
            listIndex.write("\"/>");
            
        }
        listIndex.write("</colgroup>");
        
        // Update status
        fireExportUpdate(1, 0, list.getSize());
        fireExportProgessSetMessage(1, res.getString("[1] of [2]").replaceAll("\\[1\\]", "0").replaceAll("\\[2\\]", "" + list.getSize()));
        
        for (int i = 0; i < list.getSize(); i++) {
            /*
             * Stop the export process on demand
             */
            if (exportStopped)
                throw new ExportStoppedException();
            
            Medium medium = (Medium)list.getElementAt(i);
            listIndex.write("<tr>");
            for (HTMLExportCol exportCol : listDefinition.columns) {
                listIndex.write("<th>");
                for (HTMLExportDef exportDef : exportCol){
                    switch (exportDef.type) {
                    case SHORT:
                        if (medium.getFeature(exportDef.feature) != null) {
                            String text = medium.getFeature(exportDef.feature).getShortText();
                            if (text != null)
                                listIndex.write(XMLCatalog.convert(text));
                        }
                        break;
                    case FULL:
                        if (medium.getFeature(exportDef.feature) != null) {
                            String text = medium.getFeature(exportDef.feature).getText();
                            if (text != null)
                                listIndex.write(XMLCatalog.convert(text));
                        }
                        break;
                    case HTML:
                        listIndex.write(exportDef.html_cmd);
                        break;
                    case MEDIUM:
                        listIndex.write(Options.getI18N(medium.getClass()).getString(medium.getClass().getSimpleName()));
                        break;
                    case NAME:
                        listIndex.write(medium.displayName());
                        break;
                    }
                }
                listIndex.write("</th>");
            }
            listIndex.write("</tr>");
            mediaWithinList.add(medium);
            
            // Update status
            fireExportUpdate(1, i + 1, list.getSize());
            fireExportProgessSetMessage(1, res.getString("[1] of [2]").replaceAll("\\[1\\]", "" + (i + 1)).replaceAll("\\[2\\]", "" + list.getSize()));
        }
        
        listIndex.write("</table>");
        listIndex.write("</body></html>");
        listIndex.flush();
        listIndex.close();
    }
    
    @Override
    public void export() {
       
        // Store the information on the current language
        Locale oldLocale = Options.getCurrentLocale();

        exportStopped = false;

        try {
            
            // This has to come after exportStopped = false
            // Update status
            fireExportStarted(2);
            fireExportProgessSetTitle(0, res.getString("Export process"));
            fireExportProgessSetMessage(0, res.getString("Export initialisation"));
            
            String path = profile.getPath();
            Options.ensureDirectory(path);
            if (!path.endsWith(System.getProperty("file.separator")))
                path += System.getProperty("file.separator");
            
            OutputStreamWriter index = new OutputStreamWriter(new FileOutputStream(new File(path + "index.html")), "UTF8");

            index.write("<html><head>" 
                    + "<META http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">"
                    + "<title>" + res.getString("Export from MeCat") + "</title></head><body><ul>");
            
            Set<Medium> mediaWithinList = new HashSet<Medium>();
            Set<String> indexNames = new HashSet<String>();

            int listNum = 0;
            for (HTMLListDefinition listDefinition : profile.listDefinitions){
                /*
                 * Stop the export process on demand
                 */
                if (exportStopped)
                    throw new ExportStoppedException();
                
                // Update status
                fireExportProgessSetMessage(0, res.getString("Creating list [1] of [2]").replaceAll("\\[1\\]", "" + (listNum + 1)).replaceAll("\\[2\\]", "" + profile.listDefinitions.size()));
                fireExportUpdate(0, listNum, profile.listDefinitions.size() + 1);
                fireExportProgessSetTitle(1, listDefinition.listName);
                fireExportProgessSetMessage(1, res.getString("Preparing list"));
                fireExportUpdate(1, 0, 1);
                
                listNum++;

                String asciiListName = TO_ASCII(listDefinition.listName);
                String indexName = asciiListName;
                if (indexNames.contains(indexName)) {
                    int count = 1;
                    do {
                        count++;
                        indexName = asciiListName + "_" + count;
                    } while (indexNames.contains(indexName));
                }
                indexNames.add(indexName);
                
                index.write("<li><a href=\"");
                index.write(indexName);
                index.write(".html\">");
                index.write(XMLCatalog.convert(listDefinition.listName));
                index.write("</a></li>");
                
                ShowListing list = null;
                
                if (listDefinition.isOverride()) {
                    
                    // Set language from listDefinitition.listProperties
                    if (listDefinition.listProperties.activeLanguage())
                        Options.setCurrentLocale(listDefinition.listProperties.getLanguage());
                    else
                        Options.setCurrentLocale(oldLocale);
                    
//                    System.out.println("Start size " + this.list.getSize());
                    // Construct listing from listDefinitition.listProperties
                    list = listDefinition.listProperties.addFilterAndUseSorting(this.list);
//                    System.out.println("Filtered size " + list.getSize());
                } else {
                    Options.setCurrentLocale(oldLocale);
                    list = this.list;
                }

                try {
                    this.exportList(path, indexName, listDefinition, list, mediaWithinList);
                } catch (IOException e) {
                    e.printStackTrace();

                    // Restore language from before Export
                    Options.setCurrentLocale(oldLocale);
                }
            }
            
            
            index.write("</ul></body></html>");
            index.flush();
            index.close();
            
            // Restore language from before Export
            Options.setCurrentLocale(oldLocale);

            fireExportUpdate(0, listNum, profile.listDefinitions.size() + 1);
            fireExportProgessSetMessage(0, res.getString("Create detailed information page."));
            fireExportProgessSetTitle(1, res.getString("Create detailed information page."));
            fireExportUpdate(1, 0, 1);

            int i = 0;
            for (Medium medium : mediaWithinList) {
                /*
                 * Stop the export process on demand
                 */
                if (exportStopped)
                    throw new ExportStoppedException();

                i++;
                createHTMLFromMedium(medium);

                // Update status
                fireExportUpdate(1, i, mediaWithinList.size());
                fireExportProgessSetMessage(1, res.getString("[1] of [2]").replaceAll("\\[1\\]", "" + i).replaceAll("\\[2\\]", "" + mediaWithinList.size()));
            }
            
            fireExportUpdate(0, 1, 1);
            fireExportProgessSetTitle(1, null);
            fireExportProgessSetMessage(0, "Export finished");
            fireExportFinished();
        } catch (ExportStoppedException e) {
            // Restore language from before Export
            Options.setCurrentLocale(oldLocale);
            
            fireExportStopped();
        } catch (UnsupportedEncodingException e) {
            // Restore language from before Export
            Options.setCurrentLocale(oldLocale);
            
            JOptionPane.showMessageDialog(parent, res.getString("UTF8 is not supported."));
            fireExportFinished();
        } catch (FileNotFoundException e) {
            // Restore language from before Export
            Options.setCurrentLocale(oldLocale);
            
            JOptionPane.showMessageDialog(parent, res.getString("Could not open file:") + System.getProperty("line.separator") + e.getMessage());
            fireExportFinished();
        } catch (IOException e) {
            // Restore language from before Export
            Options.setCurrentLocale(oldLocale);
            
            JOptionPane.showMessageDialog(parent, res.getString("Exception while writing file:") + System.getProperty("line.separator") + e.getMessage());
            fireExportFinished();
        }
    }

    protected void createHTMLFromMedium(final Medium medium) throws IOException {
        String path = profile.getPath();
        if (!path.endsWith(System.getProperty("file.separator")))
            path += System.getProperty("file.separator");
        OutputStreamWriter html = new OutputStreamWriter(new FileOutputStream(new File(path + medium.getFeature(Ident.class).getText() + ".html")), "UTF8");

        // Get Resources for i18n
        html.write("<html><head>" 
                + "<META http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">"
                + "<title>" + medium.toString() + "</title></head><body>");//<h1>" + medium.toString() + "</h1>");
//        for (Feature feature : medium.getFeatures()) 
//            if (feature.hasValue() && (!(feature instanceof Ident))) {
//                html.write("<h2>" + Options.getI18N(feature.getClass()).getString(feature.getClass().getSimpleName()) + ":</h2>");
//                html.write(feature.getTextHTML(-1));
//            }
        html.write(ViewDesktop.getHTML(medium, -1, 0));
        html.write("</body></html>");
        html.flush();
        html.close();
    }

    protected HTMLExportProfile profile = new HTMLExportProfile();
    
    @Override
    public boolean setProfile(ExportProfile profile) {
        if (!(profile instanceof HTMLExportProfile))
            return false;
    
        this.profile = (HTMLExportProfile)profile;
        
        return true;
    }

    @Override
    public ExportProfile getProfile() {
        return profile;
    }

}
