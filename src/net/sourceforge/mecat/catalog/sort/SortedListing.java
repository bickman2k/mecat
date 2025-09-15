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
 * Created on Jul 22, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.sort;

/*import static java.lang.Math.max;
import static java.lang.Math.min;
import java.util.LinkedHashMap;
import java.util.Map;*/

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.event.ListDataEvent;

import net.sourceforge.mecat.catalog.filter.FilterListing;
import net.sourceforge.mecat.catalog.medium.Listing;
import net.sourceforge.mecat.catalog.medium.ListingListener;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.MediumListener;


public class SortedListing implements ListingListener {

    Vector<SortedListingListener> sortedListingListeners = new Vector<SortedListingListener>();

    Vector<Medium> org = new Vector<Medium>();
	Vector<Medium> media = new Vector<Medium>();

/*    Map<Medium, Integer> mediaPositions = new LinkedHashMap<Medium, Integer>();
    Map<Integer, Integer> mediaProjection = new LinkedHashMap<Integer, Integer>();
    Map<Integer, Integer> mediaProjectionInv = new LinkedHashMap<Integer, Integer>();*/
    
    ConfigurableComparator comparator;
 
    FilterListing filterListing;

    MediumListener mediumListener = new MediumListener(){
        public void mediumChanged(Medium medium) {
            checkMedium(medium);
        }

        public void nameChanged(Medium medium) {
            // Nothing to do
        }
    };
    
    protected void move(final Medium medium, final int pos){
//        final int before = mediaPositions.get(medium);
        final int before = media.indexOf(medium);
        if (before == pos)
            return;

        media.remove(medium);
        media.add(pos, medium);
        
/*        if (before > pos) {
            int swap = mediaProjectionInv.get(before);
            for (int i = before; i > pos; i--)
                mediaProjectionInv.put(i, mediaProjectionInv.get(i - 1));
            mediaProjectionInv.put(pos, swap);
        } else {
            int swap = mediaProjectionInv.get(before);
            for (int i = before; i > pos; i++)
                mediaProjectionInv.put(i, mediaProjectionInv.get(i + 1));
            mediaProjectionInv.put(pos, swap);
        }
        
        int minimum = min(before, pos);
        int maximum = max(before, pos);
        for (int i = minimum; i <= maximum; i++) {
            mediaPositions.put(media.get(i), i);
            mediaProjection.put(mediaProjectionInv.get(i), i);
        }*/
        
        fireMediumMoved(before, pos);
    }
    
/*    protected void calculateProjections() {
        mediaPositions.clear();
        for (int i = 0; i < media.size(); i++)
            mediaPositions.put(media.get(i), i);
        for (int i = 0; i < filterListing.getSize(); i++) {
            Medium medium = filterListing.getElementAt(i); 
            mediaProjection.put(i, mediaPositions.get(medium));
            mediaProjectionInv.put(mediaPositions.get(medium), i);
        }
    }*/
    
    protected void checkMedium(final Medium medium){
//        int pos = mediaPositions.get(medium);
        int pos = media.indexOf(medium);
        
        if (comparator == null) 
            // No comparator, no check
            return;

        if (media.size() == 1)
            // Only one entry => no check 
            return;
        
        if ( ((pos == media.size() - 1) || (comparator.compare(medium, media.get(pos + 1)) <= 0)) &&  
             ((pos == 0) || (comparator.compare(medium, media.get(pos - 1)) >= 0)))
            // Already at correct position
            return;
        
        int start = 0;
        int end = media.size()-1;
        int middle;
        
        while (true) {
            middle = (start + end) / 2; 
            int comp;
            if (middle != pos)
                comp = comparator.compare(medium, media.get(middle));
            else
                comp = comparator.compare(medium, media.get(middle + 1));
            if (comp < 0)
                end = middle - 1;
            if (comp > 0)
                start = middle + 1;
            if (comp == 0)  {
                move(medium, middle);
                return;
            }
            if (end < start) {
                if (start < pos)
                    move(medium, start);
                else
                    move(medium, end);
                return;
            }
        }
    }
    
    // This function can not be used to find a new position for a medium
    // that is in the list if it is used for this purpose. The medium has 
    // to be removed from the list in advance.
    public int findPosition(final Medium medium){
        if (comparator == null) 
            return media.size();
        
        if (media.size() == 0)
            return 0;
        
        int start = 0;
        int end = media.size()-1;
        int middle;

        while (true) {
            middle = (start + end) / 2; 
            int comp;

            comp = comparator.compare(medium, media.get(middle));
            
            if (comp < 0)
                end = middle - 1;
            if (comp > 0)
                start = middle + 1;
            if (comp == 0)  
                return middle;

            if (end < start) 
                return start;
        }
    }
    
    
	public void setComparator(ConfigurableComparator comparator) {
		this.comparator = comparator;
        refreshed();
	}
	
	public ConfigurableComparator getComparator() {
		return comparator;
	}
	

	public SortedListing(final FilterListing filterListing) {
		this.filterListing = filterListing;
		filterListing.addListingListener(this);

        // Copy orginal list
        org = new Vector<Medium>(filterListing.size());
        for (int i = 0; i < filterListing.size(); i++)
            org.add(filterListing.get(i));
        
        refreshed();
	}

	/* (non-Javadoc)
	 * @see javax.swing.ListModel#getSize()
	 */
	public int getSize() {
		return media.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	public Medium getElementAt(int arg0) {
		return media.get(arg0);
	}

    public void addSortedListingListener(SortedListingListener sortedListingListener){
        sortedListingListeners.add(sortedListingListener);
    }
    
    public void removeSortedListingListener(SortedListingListener sortedListingListener){
        sortedListingListeners.remove(sortedListingListener);
    }
    
    public void fireContentsChanged(final int index0, final int index1){
        for (SortedListingListener sortedListingListener : sortedListingListeners)
            sortedListingListener.contentsChanged(this, index0, index1);
    }
    
    public void fireMediumMoved(final int prevIndex, final int newIndex){
        for (SortedListingListener sortedListingListener : sortedListingListeners)
            sortedListingListener.mediumMoved(this, prevIndex, newIndex);
    }
    
    public void fireMediaRemoved(final List<Integer> positions){
        for (SortedListingListener sortedListingListener : sortedListingListeners)
            sortedListingListener.mediaRemoved(this, positions);
    }
    
    public void fireMediaAdded(final List<Integer> positions){
        for (SortedListingListener sortedListingListener : sortedListingListeners)
            sortedListingListener.mediaAdded(this, positions);
    }

    public void refreshed() {
        // Copy orginal list
        org = new Vector<Medium>(filterListing.size());
        for (int i = 0; i < filterListing.size(); i++)
            org.add(filterListing.get(i));

        int oldMediaSize = media.size();
        
        for (Medium medium : media) {
            medium.removeMediumListener(mediumListener);
        }
        media = new Vector<Medium>(filterListing.size());
		
        for (int i = 0; i < filterListing.size(); i++) {
            Medium medium = filterListing.get(i); 
			media.add(medium); 
            medium.addMediumListener(mediumListener);
		}

		if (comparator != null)
			Collections.sort(media, comparator);
//		if (comparator != null)
//			Collections.sort(media, comparator);
		
        // If the list was empty and is now empty again
        // then nothing has changed.
        // There is no usefull event for the situation.
        if (oldMediaSize == 0 && media.size() == 0)
            return;

//        calculateProjections();

        fireContentsChanged(0, Math.max(media.size() - 1, oldMediaSize - 1));
	}

	public void intervalAdded(ListDataEvent arg0) {
        
		if (comparator == null) {
            Vector<Integer> positions = new Vector<Integer>();
			for (int i = arg0.getIndex0(); i <= arg0.getIndex1(); i++) {
                Medium medium = filterListing.get(i);
                medium.addMediumListener(mediumListener);
				media.add(i, medium);
                org.add(i, medium);
                positions.add(i);
            }

            fireMediaAdded(positions);

			return;
		} 
        
        Vector<Integer> positions = new Vector<Integer>();
        for (int i = arg0.getIndex0(); i <= arg0.getIndex1(); i++) {
            Medium medium = filterListing.get(i);
            medium.addMediumListener(mediumListener);
            int pos = findPosition(medium);
            
            if (pos != media.size())
                media.add(pos, medium);
            else
                media.add(medium);
            
            org.add(i, medium);
            positions.add(pos);
        }
//        calculateProjections();

        Collections.sort(positions);
        fireMediaAdded(positions);
	}

	public void intervalRemoved(ListDataEvent event) {
        if (comparator == null) {
            Vector<Integer> positions = new Vector<Integer>();
			for (int i = event.getIndex0(); i <= event.getIndex1(); i++) {
                // Always use index0 
                // the others lower in rank
                org.get(event.getIndex0()).removeMediumListener(mediumListener);
				media.remove(event.getIndex0());
                org.remove(event.getIndex0());

                positions.add(i);
            }

			fireMediaRemoved(positions);            
            
			return;
		}
	
        Vector<Integer> positions = new Vector<Integer>();
        for (int i = event.getIndex0(); i <= event.getIndex1(); i++) {
            Medium medium = org.get(event.getIndex0());
            medium.removeMediumListener(mediumListener);
            positions.add(media.indexOf(medium));
            media.remove(medium);
            org.remove(event.getIndex0());
        }

//        calculateProjections();

        Collections.sort(positions);
        fireMediaRemoved(positions);            
    }
    
    public FilterListing getFilterListing() {
        return filterListing;
    }

    public Listing getSource() {
        return filterListing.getSource();
    }

    public boolean contains(Object selection) {
        return media.contains(selection);
    }

}
