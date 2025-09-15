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
 * Created on Dec 2, 2005
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.export.html.list;

import java.util.EnumMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;

import net.sourceforge.mecat.catalog.export.shared.list.ExportColListCellRenderer;
import net.sourceforge.mecat.catalog.export.shared.list.ExportColVisualisation;
import net.sourceforge.mecat.catalog.gui.SelectFeatureClass;
import net.sourceforge.mecat.catalog.gui.ShowListing;
import net.sourceforge.mecat.catalog.gui.ToolBarUtils;
import net.sourceforge.mecat.catalog.medium.features.Feature;

public class HTMLExportColVisualisation extends ExportColVisualisation<HTMLExportDefType, HTMLExportDef, HTMLExportCol>{

    public HTMLExportColVisualisation(HTMLExportCol exportCol, boolean nextEnabled, boolean prevEnabled, boolean killEnabled, ShowListing showListing) {
        super(HTMLExportDefType.class, exportCol, nextEnabled, prevEnabled, killEnabled, showListing);
    }

    static Map<HTMLExportDefType, ImageIcon> icons = new EnumMap<HTMLExportDefType, ImageIcon>(HTMLExportDefType.class);
    
    static {
        for (HTMLExportDefType type : HTMLExportDefType.values())
            icons.put(type, ToolBarUtils.loadImage(HTMLExportDefType.class, type.toString(), 1, ""));
    }

    static ExportColListCellRenderer<HTMLExportDefType, HTMLExportDef> exportColListCellRenderer = new ExportColListCellRenderer<HTMLExportDefType, HTMLExportDef>(icons);
    
    @Override
    protected ListCellRenderer getListCellRenderer() {
        return exportColListCellRenderer;
    }

    @Override
    protected Map<HTMLExportDefType, ImageIcon> getIcons() {
        return icons;
    }

    @Override
    public HTMLExportDef aquireExportDef(HTMLExportDefType t) {
        HTMLExportDef def = null;
        double len = 0;
        switch (t) {
            case HTML :
                String inputValue = JOptionPane.showInputDialog(HTMLExportColVisualisation.this, res.getString("Please input html-command/text."));
                def = new HTMLExportDef(inputValue);
                break;
            case SHORT : 
            case FULL :
                Class<? extends Feature> feature = SelectFeatureClass.showSelectFeature(this, showListing.getSource());
                if (feature == null)
                    return null;
                def = new HTMLExportDef(feature, t);
                break;
            default:
                def = new HTMLExportDef(t);
        }
        return def;
    }

}
