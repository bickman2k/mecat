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
 * Created on Sep 24, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.export.latex;

import java.awt.GridLayout;

import javax.swing.JPanel;

import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.PersistentThroughEntry;
import net.sourceforge.mecat.catalog.datamanagement.Util;
import net.sourceforge.mecat.catalog.export.Export;
import net.sourceforge.mecat.catalog.export.ExportProfile;
import net.sourceforge.mecat.catalog.export.ExportToFilePanel;
import net.sourceforge.mecat.catalog.export.latex.list.LaTeXListDefinition;
import net.sourceforge.mecat.catalog.export.shared.list.AbstractMultiList;
import net.sourceforge.mecat.catalog.gui.ShowListing;

public class LaTeXExportProfile extends AbstractMultiList<LaTeXListDefinition> implements ExportProfile {

    static LaTeXExportProfileVisualisation laTeXExportProfileVisualisation = null;

    boolean isPrint;

    public PaperSize paperSize;
    public DocumentStyle style;
    public LaTeXStandardFontsize fontSize;
    
    protected ExportToFilePanel PDFExport = new ExportToFilePanel("PDF");
    protected ExportToFilePanel PSExport = new ExportToFilePanel("PostScript", "ps", false);
    protected ExportToFilePanel LaTeXExport = new ExportToFilePanel("LaTeX", "tex", false);
    protected ExportToFilePanel DVIExport = new ExportToFilePanel("DVI", false);

    protected ExportToFilePanel PDFExport_Customized = null;
    protected ExportToFilePanel PSExport_Customized = null;
    protected ExportToFilePanel LaTeXExport_Customized = null;
    protected ExportToFilePanel DVIExport_Customized = null;
    
    public LaTeXExportProfile() {
        // Instanciate at the latest possible time
        // and keep it in order to minimize reinstanciation times
        if (laTeXExportProfileVisualisation == null)
            laTeXExportProfileVisualisation = new LaTeXExportProfileVisualisation();

        paperSize = PaperSize.a4;
        style = DocumentStyle.article;
        fontSize = LaTeXStandardFontsize._10pt;
	}
    
    public LaTeXExportProfile(final Boolean isPrint) {
        this();
        this.isPrint = isPrint;
    }
	
	public Export getExport() {
		LaTeXExport export = new LaTeXExport();
		export.setProfile(this);
		return export;
	}

	public String getPDFFileName() {
		return getFileName(PDFExport, PDFExport_Customized);
	}
	public String getPSFileName() {
		return getFileName(PSExport, PSExport_Customized);
	}
	public String getDVIFileName() {
		return getFileName(DVIExport, DVIExport_Customized);
	}
	public String getLaTeXFileName() {
		return getFileName(LaTeXExport, LaTeXExport_Customized);
	}
	
	String getFileName(ExportToFilePanel std, ExportToFilePanel custom) {
		if (std == null)
			return null;
		if (!std.isActive())
			return null;
		if (custom == null)
			return std.getFileName();
		
		return custom.getFileName();
	}

	
	public boolean isActivePDF() {
		return isActive(PDFExport, PDFExport_Customized);
	}
	public boolean isActivePS() {
		return isActive(PSExport, PSExport_Customized);
	}
	public boolean isActiveDVI() {
		return isActive(DVIExport, DVIExport_Customized);
	}
	public boolean isActiveLaTeX() {
		return isActive(LaTeXExport, LaTeXExport_Customized);
	}
    public boolean isPrint() {
        return isPrint;
    }

	boolean isActive(ExportToFilePanel std, ExportToFilePanel custom) {
		if (std == null)
			return false;
		if (std.isActive() == false)
			return false;
		if (custom == null)
			return std.isActive();
		return custom.isActive();
	}

	
    public boolean hasOptions(){
        return true;
    }
	
	public JPanel options(final ShowListing showListing/*final TotalPreferences totalPreferences*/) {
        laTeXExportProfileVisualisation.setProfile(this, showListing);
//        laTeXExportProfileVisualisation.setTotalPreferences(totalPreferences);
		return laTeXExportProfileVisualisation;
	}
	
	public JPanel customize() {
		PDFExport_Customized = new ExportToFilePanel(PDFExport);
		PSExport_Customized = new ExportToFilePanel(PSExport);
		LaTeXExport_Customized = new ExportToFilePanel(LaTeXExport);
		DVIExport_Customized = new ExportToFilePanel(DVIExport);
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0,1));
		if (PDFExport.isActive())
			panel.add(PDFExport_Customized);
		if (PSExport.isActive())
			panel.add(PSExport_Customized);
		if (LaTeXExport.isActive())
			panel.add(LaTeXExport_Customized);
		if (DVIExport.isActive())
			panel.add(DVIExport_Customized);

		return panel;
	}	
	
	public boolean loadFromEntry(final Entry entry) {
        // Custemize LaTeX
        if (entry.getAttribute("PaperSize") != null)
            paperSize = PaperSize.getPaperSizeForName(entry.getAttribute("PaperSize"));
        if (entry.getAttribute("DocumentClass") != null) 
            style = DocumentStyle.valueOf(entry.getAttribute("DocumentClass"));
        if (entry.getAttribute("FontSize") != null)
            fontSize = LaTeXStandardFontsize.valueOf_(entry.getAttribute("FontSize"));
        
        // Export files settings
        PDFExport = loadExportToFilePanelFromEntry(entry, "PDFExport", PDFExport);
        PSExport = loadExportToFilePanelFromEntry(entry, "PSExport", PSExport);
        LaTeXExport = loadExportToFilePanelFromEntry(entry, "LaTeXExport", LaTeXExport);
        DVIExport = loadExportToFilePanelFromEntry(entry, "DVIExport", DVIExport);
            
        return super.loadFromEntry(entry);
	}

    public static ExportToFilePanel loadExportToFilePanelFromEntry(final Entry entry, final String name, final ExportToFilePanel original){
        Entry subEntry = entry.getSubEntry(name);
        if (subEntry != null) {
            PersistentThroughEntry pte = Util.loadFromEntry(subEntry);
            if (pte instanceof ExportToFilePanel)
                return (ExportToFilePanel)pte;
        }
        return original;
    }
    
	public void saveToEntry(final Entry entry) {
        // Export files settings
        Util.saveToEntry(PDFExport, entry.createSubEntry("PDFExport"));
        Util.saveToEntry(PSExport, entry.createSubEntry("PSExport"));
        Util.saveToEntry(LaTeXExport, entry.createSubEntry("LaTeXExport"));
        Util.saveToEntry(DVIExport, entry.createSubEntry("DVIExport"));

        // Custemize LaTeX
        entry.setAttribute("PaperSize", paperSize.toString());
        entry.setAttribute("DocumentClass", style.toString());
        entry.setAttribute("FontSize", fontSize.toString());
        
        // Print settings
        Util.addArgument(entry, new Util.Argument(0, Boolean.class, isPrint));

        super.saveToEntry(entry);
	}

    @Override
    public LaTeXListDefinition getListDefinition() {
        LaTeXListDefinition ret = new LaTeXListDefinition();
        
        
        
        return ret;
    }

	
	
	
	
	
}
