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
 * Created on July 26, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.filter;

import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Vector;
import java.util.List;

import javax.swing.event.ListDataEvent;

import net.sourceforge.mecat.catalog.medium.Listing;
import net.sourceforge.mecat.catalog.medium.ListingListener;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.MediumListener;
import net.sourceforge.mecat.catalog.medium.features.BadCondition;

public class FilterListing implements /*ListModel,*/ List<Medium>, ListingListener, MediumListener {
	Vector<ListingListener> listDataListeners = new Vector<ListingListener>();

    // Gefilterte Liste
    Vector<Medium> media = new Vector<Medium>();

    // Nicht gefilterte Liste
    Vector<Medium> org = new Vector<Medium>();
//    Map<Medium, Integer> mapping = new HashMap<Medium, Integer>();
    
	Filter filter = TrueFilter.TRUE;
//    String filter;
    java.util.List<Medium> listModel;

    public Listing getSource() {
        if (listModel instanceof Listing)
            return (Listing) listModel;
        if (listModel instanceof FilterListing)
            return ((FilterListing) listModel).getSource();
        return null;
    }
    
    public List<Medium> getList(){
        return listModel;
    }
    
	public void setFilter(final String filter_str) throws BadCondition {
		Parser parser = new Parser(filter_str);
        setFilter(parser.parse());
	}
    
    public void setFilter(Filter filter) {
        if (filter == null)
            filter = TrueFilter.TRUE;

        // Only change the filter if the filter is different
        if (FilterUtils.equivalent(filter, this.filter))
            return;
        
        this.filter = filter;
//        this.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, listModel.getSize()-1));
        refreshed();
    }
    
    
	public Filter getFilter() {
		return filter;
	}
	

	public FilterListing(Listing listModel) {
		this.listModel = listModel;
		listModel.addListingListener(this);
//		this.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, listModel.getSize()-1));
        refreshed();
	}

    public FilterListing(FilterListing listModel) {
        this.listModel = listModel;
        listModel.addListingListener(this);
//      this.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, listModel.getSize()-1));
        refreshed();
    }

	public void addListingListener(ListingListener arg0) {
		listDataListeners.add(arg0);
	}
	public void removeListingListener(ListingListener arg0) {
		listDataListeners.remove(arg0);
	}

/*	public void contentsChanged(ListDataEvent event) {
        int oldSize = media.size();

        // Find the position in the filtered list
        // for the changed contents
        // if there is none then the new stuff will
        // be filled at the end
        Integer position = null;
        for (int i = event.getIndex0(); position == null && i < org.size(); i++) 
            position = mapping.get(org.get(i));
        
        int newIndex0;
        if (position == null)
            newIndex0 = media.size();
        else
            newIndex0 = position;

        int numberBefore = 0;
        
        // Remove the old media from the list "media".
        // Therefore iterate backwards over all media in the list "org"
        // and if the higher index is greater then the size - 1 of "org"
        // start from the last element of "org". 
        // Look for those filtered to the list "media" and remove them from "media".
        for (int i = Math.min(event.getIndex1(), org.size() - 1); i >= event.getIndex0(); i--) {
            Integer pos = mapping.get(org.get(i));
            if (pos != null) {
                media.remove(pos);
                numberBefore++;
            }
        }

        int numberAfter = 0;
        
        for (int i = event.getIndex0(); i <= event.getIndex1(); i++) {
            try {
                if (filter == null || filter.eval((Medium)listModel.getElementAt(i))) {
                    
                    numberAfter++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        }

        // If the number of filtered elements in the changed part has changed too
        // then everything since the changed part has to be checked.
        if (numberBefore != numberAfter) {
            for (ListingListener l : listDataListeners)
                l.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, newIndex0, Math.max(oldSize, media.size())));
        }
        

//        // If there was no medium that passed the filter after the beginning of 
//        // the changed contents extend the search to the part before the changed contents
//        if (position == null) {
//            for (int i = event.getIndex0() -1; position == null && i >= 0; i--)
//                position = mapping.get(org.get(i));
//            if (position != null)
//                position++;
//        }
        
        

        media = new Vector<Medium>(listModel.getSize());
		try {
			for (int i = 0; i < listModel.getSize(); i++) 
				if (filter == null || filter.eval((Medium)listModel.getElementAt(i)))
					media.add((Medium)listModel.getElementAt(i));
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (ListingListener l : listDataListeners)
			l.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, media.size()-1));
	}*/

    public void refreshed() {
        // Remove listener for all in original list
        for (Medium medium : org)
            medium.removeMediumListener(this);
        
        // Create new media lists
        media = new Vector<Medium>(listModel.size());
        org = new Vector<Medium>(listModel);
        
        // Add listener for all in original list
        for (Medium medium : org)
            medium.addMediumListener(this);

        // Search for all media that pass the filter
        for (int i = 0; i < listModel.size(); i++)  
//            try {
                if (/*filter == null || filter.*/eval((Medium)listModel.get(i))) {
                    // Add element to list of media that pass the filter
                    media.add(listModel.get(i));
                }
/*            } catch (Exception e) {
                e.printStackTrace();
            }*/

        // Tell depend lists that the list has been refreshed
        for (ListingListener l : listDataListeners)
            l.refreshed();
    }   
    
    
    public void intervalAdded(ListDataEvent event) {
//        System.out.print("F");
        // Find the position in the filtered list
        // where the interval should be added
        // if there is none then the new stuff will
        // be filled at the end
        int position = -1;
        for (int i = event.getIndex0(); position == -1 && i < org.size(); i++) 
            position = media.indexOf(org.get(i));
        
        int newIndex0;
        if (position == -1)
            newIndex0 = media.size();
        else
            newIndex0 = position;

        int number = 0;
        for (int i = event.getIndex0(); i <= event.getIndex1(); i++) {
//            try {
                if (/*filter == null || filter.*/eval(listModel.get(i))) {
//                    System.out.print("+");
                    if (position == -1) {
                        media.add(listModel.get(i));
 //                       mapping.put(listModel.getElementAt(i), media.size() - 1);
                    } else {
                        media.add(position + number, listModel.get(i));
//                        mapping.put(listModel.get(i), position + number);
                    }
                        
                    number++;
                }
/*            } catch (Exception e) {
                e.printStackTrace();
            }*/
        }

        // Adapt orginal list
        if (position == -1)
            org.addAll(listModel.subList(event.getIndex0(), event.getIndex1() + 1));
        else
            org.addAll(position, listModel.subList(event.getIndex0(), event.getIndex1() + 1));

        // Add listener for all new media
        for (Medium medium : listModel.subList(event.getIndex0(), event.getIndex1() + 1))
            medium.addMediumListener(this);
        
        // Only notify everyone if there has been a change
        if (number == 0) 
            return;
        
        for (ListingListener l : listDataListeners)
            l.intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, newIndex0, newIndex0 + number - 1));
    }
        
	public void intervalRemoved(ListDataEvent event) {
	    int index0 = -1;
        int index1 = -1;

        for (int i = event.getIndex0(); i <= event.getIndex1(); i++) {
            int position = media.indexOf(org.get(i));
            if (position == -1)
                continue;
            index1 = position;
            if (index0 == -1)
                index0 = position;
        }
        
        // If the index0 is null then
        // there has been no element removed
        // that has passed the filter in the first place
        // therefore no change => stop here
        if (index0 == -1)
            return;
        
        // Remove all elements from the list that do not
        // belong there anymore
        for (int i = index1; i >= index0; i--) {
            media.remove(i);
        }
        for (int i = event.getIndex1(); i >= event.getIndex0(); i--) {
//            mapping.remove(org.get(i));
            org.get(i).removeMediumListener(this);
            org.remove(i);
        }
        
        for (ListingListener l : listDataListeners)
            l.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index0, index1));
	}

    public void nameChanged(Medium medium) {
        // Nothing to do
    }

    /**
     * 
     * @return true if the filter actually returns no for a medium
     */
    protected boolean filterEnabled() {
        if (filter == null)
            return false;
        if (filter == TrueFilter.TRUE)
            return false;
        
        return true;
    }
    
    /**
     * 
     * 
     * @param medium
     * @return true if the filter allows the medium
     */
    protected boolean eval(Medium medium) {
        if (!filterEnabled())
            return true;
        try {
            return filter.eval(medium);
        } catch (BadCondition e) {
            e.printStackTrace();
            return true;
        }
    }
    
    public void mediumChanged(Medium medium) {
        if (!filterEnabled())
            return;
        
//        try {
            boolean pass = /*filter.*/eval(medium);
            boolean allreadyPassed = media.contains(medium);
            if (pass == allreadyPassed)
                return;
            if (pass) {
                // Find the position in the filtered list 
                // where the medium should be added if there 
                // is none it will be filled at the end
                int position = -1;
                for (int i = org.indexOf(medium); position == -1 && i < org.size(); i++) 
                    position = media.indexOf(org.get(i));
                
                if (position == -1) {
                    media.add(medium);
                    position = media.size() - 1;
                } else {
                    media.add(position, medium);
                }
                for (ListingListener l : listDataListeners)
                    l.intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, position, position));
            } else {
                // Get position of the media
                int i = media.indexOf(medium);
                // Remove the media
                media.remove(i);
                // Tell the dependent list the remove index
                for (ListingListener l : listDataListeners)
                    l.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, i, i));
            }
/*        } catch (BadCondition e) {
            e.printStackTrace();
            return;
        }*/
        
    }

    public Iterator<Medium> iterator() {
        return new Iterator<Medium>(){
            
            Iterator<Medium> i = media.iterator();

            public boolean hasNext() {
                return i.hasNext();
            }

            public Medium next() {
                return i.next();
            }

            public void remove() {
                // This operation is not allowed
            }
            
        };
    }

    
    // The following List functions have been implemented
    // in order to allow multi level filtering

    /**
     * It is not good style to use this function.
     * The function {@link Listing#add(Medium)} from the instance {@link #getSource()} should be used instead.
     * At this point the function {@link Listing#add(Medium)} is not implemented, since it does not make any sense.
     * 
     * @return true if the Medium has been added
     */
    public boolean add(Medium o) {
        return listModel.add(o);
    }

    /**
     * It is not good style to use this function.
     * The function {@link Listing#add(int, Medium)} from the instance {@link #getSource()} should be used instead.
     * At this point the function {@link Listing#add(int, Medium)} is not implemented, since it does not make any sense.
     */
    public void add(int index, Medium element) {
        listModel.add(index, element);
    }

    /**
     * It is not good style to use this function.
     * The function {@link Listing#addAll(Collection)} from the instance {@link #getSource()} should be used instead.
     * At this point the function {@link Listing#addAll(Collection)} is not implemented, since it does not make any sense.
     * 
     * 
     * @return true if the media have been added
     */
    public boolean addAll(Collection< ? extends Medium> c) {
        return listModel.addAll(c);
    }

    /**
     * It is not good style to use this function.
     * The function {@link Listing#addAll(int, Collection)} from the instance {@link #getSource()} should be used instead.
     * At this point the function {@link Listing#addAll(int, Collection)} is not implemented, since it does not make any sense.
     * 
     * @return true if the media have been added
     */
    public boolean addAll(int index, Collection< ? extends Medium> c) {
        return listModel.addAll(index, c);
    }

    /**
     * It is not good style to use this function.
     * The function {@link Listing#clear()} from the instance {@link #getSource()} should be used instead.
     * At this point the function {@link Listing#clear()} is not implemented, since it is not needed.
     */
    public void clear() {
        listModel.clear();
    }

    /**
     * Does the filtered list contains the medium.
     * 
     * @return true if the filtered list contains the medium given with o.
     */
    public boolean contains(Object o) {
        return media.contains(o);
    }

    /**
     * Does the filtered list contains all the media from the collection c.
     * 
     * @return true if the filtered list contains all the media from the collection c
     */
    public boolean containsAll(Collection< ? > c) {
        return media.containsAll(c);
    }

    /**
     * @return the medium at position index from the filtered list.
     */
    public Medium get(int index) {
        return media.get(index);
    }

    /**
     * Index of the medium in the filtered list.
     */
    public int indexOf(Object o) {
        return media.indexOf(o);
    }

    public boolean isEmpty() {
        return media.isEmpty();
    }

    public int lastIndexOf(Object o) {
        return media.lastIndexOf(o);
    }

    public ListIterator<Medium> listIterator() {
        // TODO Auto-generated method stub
        return null;
    }

    public ListIterator<Medium> listIterator(int index) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * It is not good style to use this function.
     * The function {@link Listing#remove(Object)} from the instance {@link #getSource()} should be used instead.
     * At this point the function {@link Listing#remove(Object)} is not implemented, since it does not make any sense.
     * 
     * @return true if the Medium has been removed
     */
    public boolean remove(Object o) {
        return listModel.remove(o);
    }

    /**
     * It is not good style to use this function.
     * The function {@link Listing#remove(int)} from the instance {@link #getSource()} should be used instead.
     * At this point the function {@link Listing#remove(int)} is not implemented, since it does not make any sense.
     * 
     * @return the Medium which has been removed
     */
    public Medium remove(int index) {
        return listModel.remove(index);
    }

    /**
     * It is not good style to use this function.
     * The function {@link Listing#removeAll(Collection)} from the instance {@link #getSource()} should be used instead.
     * At this point the function {@link Listing#removeAll(Collection)} is not implemented, since it does not make any sense.
     * 
     * @return true if the media from the collection c have been removed
     */
    public boolean removeAll(Collection< ? > c) {
        return listModel.removeAll(c);
    }

    /**
     * It is not good style to use this function.
     * The function {@link Listing#retainAll(Collection)} from the instance {@link #getSource()} should be used instead.
     * At this point the function {@link Listing#retainAll(Collection)} is not implemented, since it does not make any sense.
     * 
     * @return true if the media from the collection c have been retained (only the media from this collection are still in the list)
     */
    public boolean retainAll(Collection< ? > c) {
        return listModel.retainAll(c);
    }

    /**
     * This function is not implemented
     */
    public Medium set(int index, Medium element) {
        return null;
    }

    /**
     * @return the size of the filtered list.
     */
    public int size() {
        return media.size();
    }

    /**
     * This function returns a sublist containing the wanted media, 
     * but it is no filtered listing.
     */
    public List<Medium> subList(int fromIndex, int toIndex) {
        return media.subList(fromIndex, toIndex);
    }

    public Object[] toArray() {
        return media.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return media.toArray(a);
    }
}
