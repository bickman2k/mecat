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
 * Created on Oct 12, 2005
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.gui;

import java.util.List;
import java.util.Vector;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import net.sourceforge.mecat.catalog.medium.Listing;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.MediumListener;
import net.sourceforge.mecat.catalog.sort.SortedListing;
import net.sourceforge.mecat.catalog.sort.SortedListingListener;

public class ShowListing implements ListModel, SortedListingListener {

    Vector<ListDataListener> listDataListeners = new Vector<ListDataListener>();
    final SortedListing sortedListing;
    

    MediumListener mediumListener = new MediumListener(){
        public void nameChanged(Medium medium) {
            for (int i = 0; i < sortedListing.getSize(); i++) 
                if (sortedListing.getElementAt(i).equals(medium)) {
                    for (ListDataListener listDataListener : listDataListeners)
                        listDataListener.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, i, i));
                    return;
                }
        }
        public void mediumChanged(Medium medium) {
            // nothing to do
        }
    };
    
    public ShowListing(final SortedListing sortedListing) {
        this.sortedListing = sortedListing;
        sortedListing.addSortedListingListener(this);// .addListDataListener(this);
        contentsChanged(sortedListing, 0, sortedListing.getSize() - 1);
    }

    public int getSize() {
        return sortedListing.getSize();
    }

    public Object getElementAt(int index) {
        return sortedListing.getElementAt(index);
    }

    public void addListDataListener(ListDataListener listDataListener) {
        listDataListeners.add(listDataListener);
    }

    public void removeListDataListener(ListDataListener listDataListener) {
        listDataListeners.remove(listDataListener);
    }

/*    public void intervalAdded(ListDataEvent e) {
        for (int i = e.getIndex0(); i <= e.getIndex1(); i++) {
            Medium medium = sortedListing.getElementAt(i);
            // Add mediumListener
            medium.addMediumListener(mediumListener);
        }
        for (ListDataListener listDataListener : listDataListeners)
            listDataListener.intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, e.getIndex0(), e.getIndex1()));
    }

    public void intervalRemoved(ListDataEvent e) {
        for (ListDataListener listDataListener : listDataListeners)
            listDataListener.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, e.getIndex0(), e.getIndex1()));
    }*/

/*    public void contentsChanged(ListDataEvent e) {
        for (int i = e.getIndex0(); i <= e.getIndex1(); i++) {
            Medium medium = sortedListing.getElementAt(i);

            // If previously a listener has been added
            // then add a new one
            medium.removeMediumListener(mediumListener);

            // Add mediumListener
            medium.addMediumListener(mediumListener);
        }
        
        for (ListDataListener listDataListener : listDataListeners)
            listDataListener.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, e.getIndex0(), e.getIndex1()));
    }*/
    
    public SortedListing getSortedListing() {
        return sortedListing;
    }
    
    public Listing getSource() {
        return sortedListing.getSource();
    }

    public void contentsChanged(SortedListing listing, int index0, int index1) {
        for (int i = 0; i < sortedListing.getSize(); i++) {
            Medium medium = sortedListing.getElementAt(i);

            // If previously a listener has been added
            // then add a new one
            medium.removeMediumListener(mediumListener);

            // Add mediumListener
            medium.addMediumListener(mediumListener);
        }
        
        for (ListDataListener listDataListener : listDataListeners)
            listDataListener.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, sortedListing.getSize() - 1));
    }

    public void mediumMoved(SortedListing listing, int prevPosition, int newPosition) {
        for (ListDataListener listDataListener : listDataListeners)
            listDataListener.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, prevPosition, newPosition));
    }

    public void mediaRemoved(SortedListing listing, List<Integer> positions) {
        if (positions.size() == 0)
            return;
        
        if (positions.size() == 1) {
            for (ListDataListener listDataListener : listDataListeners)
                listDataListener.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, positions.iterator().next(), positions.iterator().next()));
            return;
        }
        
        for (ListDataListener listDataListener : listDataListeners)
            listDataListener.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, sortedListing.getSize() - 1));
    }

    public void mediaAdded(SortedListing listing, List<Integer> positions) {
        if (positions.size() == 0)
            return;
        
        for (int i : positions) {
            Medium medium = sortedListing.getElementAt(i);

            // If previously a listener has been added
            // then add a new one
            medium.removeMediumListener(mediumListener);

            // Add mediumListener
            medium.addMediumListener(mediumListener);
        }
        
        if (positions.size() == 1) {
            for (ListDataListener listDataListener : listDataListeners)
                listDataListener.intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, positions.iterator().next(), positions.iterator().next()));
            return;
        }
        
        for (ListDataListener listDataListener : listDataListeners)
            listDataListener.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, sortedListing.getSize() - 1));
    }

    public boolean contains(Object selection) {
        return sortedListing.contains(selection);
    }
    
    

}
