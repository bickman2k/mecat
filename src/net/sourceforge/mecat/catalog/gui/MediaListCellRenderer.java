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
 * Created on Jun 9, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

public class MediaListCellRenderer implements ListCellRenderer {

    final static Map<Class, ImageIcon> icons = new LinkedHashMap<Class, ImageIcon>();

    JList dummy0 = new JList();
    JList dummy1 = new JList();

    public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean selHasFocus) {
        if (!icons.containsKey(value.getClass())) 
            icons.put(value.getClass(), ToolBarUtils.loadImage(value.getClass(), value.getClass().getSimpleName(), 1, ""));
        if (icons.get(value.getClass()) == null)
            return dummy0.getCellRenderer().getListCellRendererComponent(list, value, index, isSelected, selHasFocus);

        final Component txt = dummy0.getCellRenderer().getListCellRendererComponent(list, value, index, isSelected, selHasFocus);
        final JLabel pic = (JLabel)dummy1.getCellRenderer().getListCellRendererComponent(list, "", index, isSelected, selHasFocus);

        pic.setIcon( icons.get(value.getClass()));
        
        JPanel panel = new JPanel();

        panel.setLayout(new BorderLayout());
        panel.add(txt);
        panel.add(pic, BorderLayout.WEST);

        return panel;
    }

}
