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
 * Created on Sep 15, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.medium.features.option.merger.choiceFeature;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import net.sourceforge.mecat.catalog.i18n.fromCatalog.CatalogResource;
import net.sourceforge.mecat.catalog.i18n.fromCatalog.CatalogResourceTableModel;

public class MergedOptionCellRenderer implements TableCellRenderer{

    JTable dummy = new JTable();

    final ChoiceFeatureMergeUI mergeUI;
    final CatalogResource mergeCatalog;
    final CatalogResourceTableModel model;
    
    final Color LIGHTRED = new Color(255, 100, 100);
    
    
    public MergedOptionCellRenderer(final ChoiceFeatureMergeUI mergeUI, final CatalogResourceTableModel model) {
        this.mergeUI = mergeUI;
        this.model = model;
        mergeCatalog = mergeUI.getMergeResult().getCatalogResource();
    }



    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component component = dummy.getDefaultRenderer(String.class).getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        ChoiceConflictType conflictType = null;
        
        if (column == 0) 
            conflictType = mergeUI.conflictsKey.get(mergeCatalog.keys.get(row));
        else
            // Get the model from the enclosing CatalogResourcePanel in order to get the right language
            conflictType = mergeUI.conflictsTranslation.get(model.getLocale(column)).get(mergeCatalog.keys.get(row));
        
        switch (conflictType){
        case NoConflict:
            if (isSelected)
                component.setBackground(Color.GREEN.darker());
            else
                component.setBackground(Color.GREEN);
            break;
        case HarmlessConflict:
            if (isSelected)
                component.setBackground(Color.YELLOW.darker());
            else
                component.setBackground(Color.YELLOW);
            break;
        case BadConflict:
            if (isSelected)
                component.setBackground(LIGHTRED.darker());
            else
                component.setBackground(LIGHTRED);
            break;
        case RenamedKey:
            if (isSelected)
                component.setBackground(Color.CYAN.darker());
            else
                component.setBackground(Color.CYAN);
            break;
        }

        return component;
    }
    
}