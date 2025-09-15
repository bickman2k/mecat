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
 * Created on Jun 10, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filesystem.gui.doppelganger;

import java.math.BigInteger;
import java.util.Date;
import java.util.Vector;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import net.sourceforge.mecat.catalog.filesystem.FileEntry;
import net.sourceforge.mecat.catalog.filesystem.gui.ResultFileModel;
import net.sourceforge.mecat.catalog.option.LocalListener;
import net.sourceforge.mecat.catalog.option.LocalListenerEvent;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.WeakLocalListener;

public class GroupListModel implements TableModel, LocalListener {

    final DoppelgangerGroup group;
    
    public GroupListModel(DoppelgangerGroup group) {
        this.group = group;
        
        Options.addLocalListener(new WeakLocalListener(this));
    }


    public int getRowCount() {
        return group.size();
    }

    public int getColumnCount() {
        return 4;
    }

    public String getColumnName(int index) {
        switch (index) {
        case 0:
            return Options.getI18N(ResultFileModel.class).getString("Name");
        case 1:
            return Options.getI18N(ResultFileModel.class).getString("Size");
        case 2:
            return Options.getI18N(ResultFileModel.class).getString("Date");
        case 3:
            return Options.getI18N(ResultFileModel.class).getString("MD5");
        default:
            return "";
        }
    }

    public Class< ? > getColumnClass(int index) {
        return String.class;
    }

    public boolean isCellEditable(int arg0, int arg1) {
        return false;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        FileEntry file = group.get(rowIndex).getFileEntry();
        switch (columnIndex) {
        case 0:
            return file.getName();
        case 1:
            return "" + file.getSize();
        case 2:
            return "" + new Date(file.getDate());
        case 3:
            return (new BigInteger( 1, file.getMD5SUM())).toString(16);
        default:
            return "";
        }
    }

    public void setValueAt(Object arg0, int arg1, int arg2) {}

    Vector<TableModelListener> tableModelListeners = new Vector<TableModelListener>();
    
    public void addTableModelListener(TableModelListener tableModelListener) {
        tableModelListeners.add(tableModelListener);
    }

    public void removeTableModelListener(TableModelListener tableModelListener) {
        tableModelListeners.remove(tableModelListener);
    }

    public void stateChanged(LocalListenerEvent event) {
        for (TableModelListener tableModelListener : tableModelListeners)
            tableModelListener.tableChanged(new TableModelEvent(this, TableModelEvent.HEADER_ROW));
    }
    
}
