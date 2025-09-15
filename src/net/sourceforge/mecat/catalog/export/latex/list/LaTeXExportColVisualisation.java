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
package net.sourceforge.mecat.catalog.export.latex.list;

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

public class LaTeXExportColVisualisation extends ExportColVisualisation<LaTeXExportDefType, LaTeXExportDef, LaTeXExportCol>{

    public LaTeXExportColVisualisation(LaTeXExportCol exportCol, boolean nextEnabled, boolean prevEnabled, boolean killEnabled, ShowListing showListing) {
        super(LaTeXExportDefType.class, exportCol, nextEnabled, prevEnabled, killEnabled, showListing);
    }

    static Map<LaTeXExportDefType, ImageIcon> icons = new EnumMap<LaTeXExportDefType, ImageIcon>(LaTeXExportDefType.class);
    
    static {
        for (LaTeXExportDefType type : LaTeXExportDefType.values())
            icons.put(type, ToolBarUtils.loadImage(LaTeXExportDefType.class, type.toString(), 1, ""));
    }

    static ExportColListCellRenderer<LaTeXExportDefType, LaTeXExportDef> exportColListCellRenderer = new ExportColListCellRenderer<LaTeXExportDefType, LaTeXExportDef>(icons);
    
    @Override
    protected ListCellRenderer getListCellRenderer() {
        return exportColListCellRenderer;
    }

    @Override
    protected Map<LaTeXExportDefType, ImageIcon> getIcons() {
        return icons;
    }

    @Override
    public LaTeXExportDef aquireExportDef(LaTeXExportDefType t) {
        LaTeXExportDef def = null;
        double len = 0;
        switch (t) {
            case LATEX :
                String inputValue = JOptionPane.showInputDialog(LaTeXExportColVisualisation.this, res.getString("Please input tex-command/text."));
                def = new LaTeXExportDef(inputValue);
                break;
            case SHORT : 
            case FULL :
                Class<? extends Feature> feature = SelectFeatureClass.showSelectFeature(this, showListing.getSource());
                if (feature == null)
                    return null;
                def = new LaTeXExportDef(feature, t);
                break;
            default:
                def = new LaTeXExportDef(t);
        }
        return def;
    }

}
