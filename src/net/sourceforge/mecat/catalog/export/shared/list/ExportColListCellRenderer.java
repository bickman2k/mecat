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
package net.sourceforge.mecat.catalog.export.shared.list;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

public class ExportColListCellRenderer<TYPE extends Enum, EXPORTDEF extends ExportDef> implements ListCellRenderer{
    JList dummy0 = new JList();
    JList dummy1 = new JList();
    
    final Map<TYPE, ImageIcon> icons;
    
    public ExportColListCellRenderer(final Map<TYPE, ImageIcon> icons){
        this.icons = icons;
    }

    public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean selHasFocus) {
        final EXPORTDEF exportDef = (EXPORTDEF) value;
        
        if (icons.get(exportDef.type) == null)
            return dummy0.getCellRenderer().getListCellRendererComponent(list, value, index, isSelected, selHasFocus);
        
        JLabel pic = (JLabel) dummy1.getCellRenderer().getListCellRendererComponent(list, "", index, isSelected, selHasFocus);
        pic.setIcon(icons.get(exportDef.type));
        
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(pic, BorderLayout.WEST);
        panel.add(dummy0.getCellRenderer().getListCellRendererComponent(list, value, index, isSelected, selHasFocus));
        return panel; 
    }
}
