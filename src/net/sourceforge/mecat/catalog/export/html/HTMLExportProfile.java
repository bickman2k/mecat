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
 * 
 */
package net.sourceforge.mecat.catalog.export.html;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.Util;
import net.sourceforge.mecat.catalog.export.Export;
import net.sourceforge.mecat.catalog.export.ExportProfile;
import net.sourceforge.mecat.catalog.export.ExportToFilePanel;
import net.sourceforge.mecat.catalog.export.html.list.HTMLExportCol;
import net.sourceforge.mecat.catalog.export.html.list.HTMLExportDef;
import net.sourceforge.mecat.catalog.export.html.list.HTMLExportDefType;
import net.sourceforge.mecat.catalog.export.html.list.HTMLListDefinition;
import net.sourceforge.mecat.catalog.export.html.list.HTMLMeasurement;
import net.sourceforge.mecat.catalog.export.html.list.HTMLMeasurementType;
import net.sourceforge.mecat.catalog.export.latex.LaTeXExportProfile;
import net.sourceforge.mecat.catalog.export.shared.list.AbstractMultiList;
import net.sourceforge.mecat.catalog.gui.ShowListing;
import net.sourceforge.mecat.catalog.medium.features.impl.Ident;

public class HTMLExportProfile extends AbstractMultiList<HTMLListDefinition> implements ExportProfile {

    static HTMLExportProfileVisualisation hTMLExportProfileVisualisation = null;
    
    ExportToFilePanel filePanel = new ExportToFilePanel((String)null, JFileChooser.DIRECTORIES_ONLY);

    public HTMLExportProfile(){
        // Instanciate at the latest possible time
        // and keep it in order to minimize reinstanciation times
        if (hTMLExportProfileVisualisation == null)
            hTMLExportProfileVisualisation = new HTMLExportProfileVisualisation();
    }
    
    public Export getExport() {
        HTMLExport export = new HTMLExport();
        export.setProfile(this);
        return export;
    }

    public boolean hasOptions(){
        return true;
    }
    
    public JPanel options(final ShowListing showListing) {
        hTMLExportProfileVisualisation.setHTMLExportProfile(this, showListing);
        return hTMLExportProfileVisualisation;
    }

    public JPanel customize() {
        return filePanel;
    }

    @Override
    public boolean loadFromEntry(Entry entry) {
        filePanel = LaTeXExportProfile.loadExportToFilePanelFromEntry(entry, "PATH", filePanel);
        // Load list definitions
        return super.loadFromEntry(entry);
    }

    @Override
    public void saveToEntry(Entry entry) {
        Util.saveToEntry(filePanel, entry.createSubEntry("PATH"));
        // Save list definitions
        super.saveToEntry(entry);
    }

    public String getPath() {
        return filePanel.getFileName();
    }

    @Override
    public HTMLListDefinition getListDefinition() {
        HTMLListDefinition ret = new HTMLListDefinition();
        
        HTMLExportCol ec = new HTMLExportCol(new HTMLMeasurement(HTMLMeasurementType.PROPORTION, 1.0));
        ret.addColumn(ec);
        ec.add(new HTMLExportDef("<a href=\""));
        ec.add(new HTMLExportDef(Ident.class, HTMLExportDefType.SHORT));
        ec.add(new HTMLExportDef(".html\">"));
        ec.add(new HTMLExportDef(HTMLExportDefType.NAME));
        ec.add(new HTMLExportDef("</a>"));
        
        return ret;
    }

}
