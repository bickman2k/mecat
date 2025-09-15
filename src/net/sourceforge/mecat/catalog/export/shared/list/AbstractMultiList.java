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
package net.sourceforge.mecat.catalog.export.shared.list;

import java.util.Vector;

import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.PersistentThroughEntry;
import net.sourceforge.mecat.catalog.datamanagement.Util;

public abstract class AbstractMultiList<LISTDEFINITION extends ListDefinition> implements MultiList<LISTDEFINITION> {

    public Vector<LISTDEFINITION> listDefinitions = new Vector<LISTDEFINITION>();

    
    /**
     * The implementation of this function should return a list definition
     * that already gives a good start for a default export. In other 
     * words this should not return a empty Definition but a default definition.
     * 
     * @return
     */
    public abstract LISTDEFINITION getListDefinition();

    // In the following section there are information
    // that are part of the setting of visualisation
    int selectedList = 0;
    
    public AbstractMultiList() {
        addListDefinition(getListDefinition());
	}
    
    public LISTDEFINITION getListDefinition(int selectedList) {
        return listDefinitions.get(selectedList);
    }

    public void more() {
        addListDefinition(getListDefinition());
        selectedList = listDefinitions.size() - 1;
    }

    public void less() {
        removeListDefinition(listDefinitions.get(selectedList));
        if (selectedList >= listDefinitions.size())
            selectedList = listDefinitions.size() - 1;
    }

    public void prev() {
        selectedList -= 1;
    }

    public void next() {
        selectedList += 1;
    }

    public int getListDefinitionsSize() {
        return listDefinitions.size();
    }

    public int getSelectedList() {
        return selectedList;
    }

    protected void removeListDefinition(final LISTDEFINITION listDefinition) {
        listDefinitions.remove(listDefinition);
    }
    
    protected void addListDefinition(final LISTDEFINITION listDefinition) {
        listDefinitions.add(listDefinition);
    }
	
	public boolean loadFromEntry(final Entry entry) {
        listDefinitions.clear();
        // The different cases are used to load old profiles
        // that had only one listDefinition directly integrated
        if (entry.getSubEntries("ListDefinition").size() > 0)
            for (Entry e : entry.getSubEntries("ListDefinition")) {
                PersistentThroughEntry pte = Util.loadFromEntry(e);
//                if (pte instanceof LISTDEFINITION) 
                    addListDefinition( (LISTDEFINITION) pte);
            }
        else {
            listDefinitions.add(getListDefinition());
            listDefinitions.get(0).loadFromEntry(entry);
        }
        if (listDefinitions.size() == 0)
            listDefinitions.add(getListDefinition());
            
        return true;
	}
    
	public void saveToEntry(final Entry entry) {
        // ListDefinitions
        for (LISTDEFINITION listDefinition : listDefinitions)
            Util.saveToEntry(listDefinition, entry.createSubEntry("ListDefinition"));
	}


	
	
	
	
	
}
