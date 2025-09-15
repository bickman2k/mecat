/*
 *  Apollo Migration Runtime - 
 *  Runtime for the development framework Apollo Migration
 *  More information at http://apollomigration.sourceforge.net/
 *  
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
 * Created on Apr 11, 2005
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.filesystem.gui.doppelganger;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class GroupColorCellRenderer implements TableCellRenderer {

	final DoppelgangerGroup group;
	
	public GroupColorCellRenderer(final DoppelgangerGroup group) {
		this.group = group;
	}

	final static JTable dummy = new JTable();
	
	public Component getTableCellRendererComponent(JTable table,
            Object value,boolean isSelected,boolean hasFocus,
            int row, int column)
        {
			Component ret = dummy.getDefaultRenderer(table.getColumnClass(column))
						.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			
			if (isSelected)
			    ret.setBackground(group.get(row).getType().selectedColor);
			else
			    ret.setBackground(group.get(row).getType().color);
			
			return ret;
        }
}