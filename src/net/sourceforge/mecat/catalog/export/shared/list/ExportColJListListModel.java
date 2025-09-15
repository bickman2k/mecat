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
 * Created on Nov 9, 2005
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.export.shared.list;

import java.util.Vector;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class ExportColJListListModel<EXPORTDEF extends ExportDef, EXPORTCOL extends ExportCol<?,EXPORTDEF> > implements ListModel{
    
    final EXPORTCOL exportCol;
    
    public ExportColJListListModel(final EXPORTCOL exportCol) {
        this.exportCol = exportCol;
    }
    
    public int getSize() {
        return exportCol.size();
    }
    
    public Object getElementAt(int index) {
        return exportCol.elementAt(index);
    }
    
    Vector<ListDataListener> listDataListeners = new Vector<ListDataListener>();
    
    public void addListDataListener(ListDataListener listDataListener) {
        listDataListeners.add(listDataListener);
    }
    
    public void removeListDataListener(ListDataListener listDataListener) {
        listDataListeners.remove(listDataListener);
    }
    
    public void fireContentsChanged(int index0, int index1) {
        for (ListDataListener listDataListener : listDataListeners)
            //listDataListener.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, index0, index1));
            listDataListener.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, getSize()));
    }
    public void fireIntervalAdded(int index0, int index1) {
        for (ListDataListener listDataListener : listDataListeners)
//            listDataListener.intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, index0, index1));
        listDataListener.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, getSize()));
    }
    public void fireIntervalRemoved(int index0, int index1) {
        for (ListDataListener listDataListener : listDataListeners)
//            listDataListener.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index0, index1));
        listDataListener.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, getSize()));
    }
    
    public void add(final int index, EXPORTDEF exportDef) {
        exportCol.add(index, exportDef);
        fireIntervalAdded(index, index);
    }

    public void add(EXPORTDEF exportDef) {
        exportCol.add(exportDef);
        fireIntervalAdded(exportCol.size()-1, exportCol.size()-1);
    }

    public void remove(final int index) {
        exportCol.remove(index);
        fireIntervalRemoved(index, index);
    }
    
    public void remove(EXPORTDEF exportDef) {
        int index = exportCol.indexOf(exportDef);
        exportCol.remove(exportDef);
        fireIntervalRemoved(index, index);
    }
    
}

