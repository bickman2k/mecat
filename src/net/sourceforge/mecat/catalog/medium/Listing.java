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
 * Created on Jul 15, 2004
 * @author Stephan Richard Palm
 * 
 * Adding and removing does not (yet ;) ) add and remove to the catalog.
 * In general none of the changments to this list are automatically applied to the catalog.
 * Exceptions are the functions 
 * boolean remove(Object o)  
 */
package net.sourceforge.mecat.catalog.medium;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;

import javax.swing.event.ListDataEvent;

import net.sourceforge.mecat.catalog.datamanagement.Catalog;
import net.sourceforge.mecat.catalog.datamanagement.CatalogEvent;
import net.sourceforge.mecat.catalog.datamanagement.CatalogListener;
import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.EntryEvent;
import net.sourceforge.mecat.catalog.datamanagement.Util;
import net.sourceforge.mecat.catalog.datamanagement.changelog.ChangeLog;
import net.sourceforge.mecat.catalog.datamanagement.changelog.UndoEvent;
import net.sourceforge.mecat.catalog.datamanagement.changelog.UndoListener;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.medium.features.impl.Ident;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.preferences.CatalogPreferences;
import net.sourceforge.mecat.catalog.option.preferences.TotalPreferences;

public class Listing implements /*ListModel,*/ List<Medium>/*, Iterable<Medium>*/, CatalogListener, UndoListener {
	
//	private final Class[] constructorType = {};//Entry.class, Listing.class};

    /**
     * Listeners that will be invoked for any change on the list
     */
	Vector<ListingListener> listDataListeners = new Vector<ListingListener>();
    /** 
     * Container for the list. All media are stored here.
     */
	Vector<Medium> media = new Vector<Medium>();
    /**
     * Instances grouped by media typ.
     */
    Map<Class<? extends Medium>, Set<Medium>> mediaByType = new HashMap<Class<? extends Medium>, Set<Medium>>();
    /**
     * Reference map between the entries from the catalog and the list.
     * This allows to see efficient what Medium blongs to an entry.
     * This is necessary for event passthrough.
     */
    Map<Entry, Medium> entries = new  LinkedHashMap<Entry, Medium>();
    /**
     * Reference map from uuids to mediums.
     */
    Map<UUID, Medium> uuids = new  LinkedHashMap<UUID, Medium>();
    /**
     * Catalog where the data is eventualy stored.
     */
	public Catalog catalog;

    /**
     * Information from the list for any class that needs additional list-static information.
     * These information are not stored.
     */
    public Map<Class, Object> listStaticInformation = new HashMap<Class, Object>();

    /**
     * Preferences like they are stored in the catalog.
     */
    CatalogPreferences catalogPreferences = null;

    /**
     * Preferences like they are after merging all preferences together,
     * from the default preferences, general preferences and catalog preferences.
     */
    TotalPreferences totalPreferences = null;
    
    
    /**
     * This filed contains a changelog for the current catalog.
     * If the changelog feature is activated.
     */
    ChangeLog changeLog = null;

    
    /**
	 * Creates a Listing from an already existing catalog
	 * @param catalog
	 */
	public Listing(Catalog catalog) {
		setCatalog(catalog);
	}
	
    public ChangeLog getChangeLog() {
        return changeLog;
    }
    
    public void setChangeLog(ChangeLog changeLog) {
        // We have to make sure that the changeLog gets
        // the events before the listing. 
        // Else the order of events in the changelog would be wrong
        if (catalog != null)
            catalog.removeCatalogListener(this);
        
        if (this.changeLog == changeLog)
            return;
        this.changeLog = changeLog;
        changeLog.addCatalog(catalog);
        changeLog.addUndoListener(catalog, this);

        if (catalog != null)
            catalog.addCatalogListener(this);
    }
    
    public void undone(UndoEvent event){
        setCatalogNoChangeLog(event.getNewCatalog());
        changeLog.addUndoListener(catalog, this);
    }
    
    protected Class<? extends Medium> getMediumClassFromEntry(final Entry entry) {
        try {
            Class c = Class.forName(entry.getTypeClassName());
            if (Medium.class.isAssignableFrom(c))
                return (Class<? extends Medium>)c;
            else
                System.err.println("The class " + entry.getTypeClassName() + " is no medium.");
        } catch (java.lang.ClassNotFoundException e) {
            //TODO message directly to user that he has done something terrible wrong
            System.err.println("The is no class " + entry.getTypeClassName() + ".");
            System.err.println("Your missing a pluging or the catalog is broken.");
            e.printStackTrace();
        } 
        return null;
    }
    
    public void setCatalog(Catalog catalog) {
        int transId = -1;

        if (changeLog != null) {
            changeLog.addCatalog(catalog);
            changeLog.addUndoListener(catalog, this);
            transId = changeLog.openTransaction("Initiate listing", false, false);
        }

        setCatalogNoChangeLog(catalog);

        if (changeLog != null) 
            changeLog.closeTransaction(transId);
    }
	public void setCatalogNoChangeLog(Catalog catalog) {
		media = new Vector<Medium>();
        mediaByType = new HashMap<Class<? extends Medium>, Set<Medium>>();

        catalogPreferences = null;
        totalPreferences = null;
        
		this.catalog = catalog;

		if (catalog == null)
			return;

		for (Iterator<? extends Entry> i = catalog.getIterator(); i.hasNext(); ) {
			Entry entry = i.next();
            create(getMediumClassFromEntry(entry), entry);
		}
        catalog.addCatalogListener(this);
		refresh();
	}
	
    public CatalogPreferences getCatalogPreferences() {
        if (catalogPreferences == null) {
            catalogPreferences = Util.getCatalogPreferences(catalog);
/*            Entry prefEntry = catalog.getOption("Preferences");
            if (prefEntry != null)
                catalogPreferences = (CatalogPreferences) Util.loadFromEntry(prefEntry);
            else
                catalogPreferences = new CatalogPreferences();*/
        }
        return catalogPreferences;
    }
    
    public TotalPreferences getTotalPreferences() {
        if (totalPreferences == null) 
            totalPreferences = new TotalPreferences(this);
        return totalPreferences;
    }
    
    /**
     * This function gets a Preference in order to
     * make sure it will be stored with the catalog
     * 
     * @param catalogPreferences
     */
    public void setPreferences(CatalogPreferences catalogPreferences) {
        int transId = -1;
        if (changeLog != null)
            transId = changeLog.openTransaction("Preferences changed", true, true);
        
        this.catalogPreferences = catalogPreferences;
        // Store the preferences in the catalog
        catalog.removeOption("Preferences");
        Util.saveToEntry(catalogPreferences, catalog.createOption("Preferences"));
        totalPreferences = null;
        
        if (changeLog != null)
            changeLog.closeTransaction(transId);
    }
    
	public boolean remove(Medium o) {
		if (o == null)
            return false;
        if (!media.contains(o))
            return false;
        
        // Remove medium from catalog mapping
        entries.remove(o.entry);
        // Remove medium from uuid mapping
        uuids.remove(o.getFeature(Ident.class).getUUID());
        // Remove medium from underlying catalog
        catalog.removeEntry(o.entry);
		// Get position from medium in the list
        int pos = media.indexOf(o);
        // Remove medium from grouped-by-type list
        mediaByType.get(o.getClass()).remove(o);
        if (mediaByType.get(o.getClass()).isEmpty())
            mediaByType.remove(o.getClass());
		// Remove medium from list
        boolean ret = media.remove(o);
		if (ret)
            fireMediumRemoved(pos);
        else
            System.err.println("Serious system error. Internal inconsistency in Listing.");
		return ret;
	}
	
	public <T extends Medium> T create(Class<T> medium_class) {
        int transId = -1;
        if (changeLog != null)
            transId = changeLog.openTransaction("Create new " + Options.getI18N(medium_class).getString(medium_class.getSimpleName()), true, true);

        Entry entry = catalog.createEntry(medium_class.getName());
		T ret = create(medium_class, entry);

        // End the transaction before exiting the function
        if (changeLog != null)
            changeLog.closeTransaction(transId);

// Firing a medium added event here causes two fired events        
//        fireMediumAdded(media.size() - 1);
		return ret;
	}

    
    private <T extends Medium> T create(Class<T> medium_class, Entry entry) {
        if (entries.containsKey(entry))
            return (T)entries.get(entry);

		if (Medium.class.isAssignableFrom(medium_class)){
			try {
				Object objects[] = new Object[2];
				objects[0] = entry;
				objects[1] = this;
				T medium = (T) (medium_class.getConstructor(new Class[]{Entry.class, Listing.class}).newInstance(objects));

                // Add medium to list
				media.add(medium);
                // Add medium to grouped-by-type list
                if (!mediaByType.containsKey(medium.getClass()))
                    mediaByType.put(medium.getClass(), new HashSet<Medium>());
                mediaByType.get(medium.getClass()).add(medium);
                // Add medium to catalog mapping
                entries.put(entry, medium);
                // Add medium to uuid mapping
                uuids.put(medium.getFeature(Ident.class).getUUID(), medium);

                return medium;
			
			} catch (java.lang.NoSuchMethodException e) {
				System.out.println("The Class " + entry.getTypeClassName() + " has not the required Constructor.");
				e.printStackTrace();
			} catch (java.lang.reflect.InvocationTargetException e) {
				//TODO message directly to user that he has done something terrible wrong
				e.printStackTrace();
			} catch (java.lang.IllegalAccessException e) {
				// TODO tell user he has not the right to access all classes!
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			}				 
		}

		return null;
		
	}
	
	public Iterator<Medium> iterator() {
		return new Iterator<Medium>(){
			final Iterator<Medium> i = media.iterator();
			Medium medium = null;
			public boolean hasNext() {
				return i.hasNext();
			}

			public Medium next() {
				return medium = i.next();
			}

            // This functionality is not necessary
			public void remove() {
/*                This seem not to be right
				if (medium != null && media.contains(medium))
					catalog.removeEntry(medium.entry);
				int pos = media.indexOf(medium);
				i.remove();
                fireMediumRemoved(pos);
                */
			}};
		//media.iterator();
	}
	
	public boolean exists(Medium medium) {
		return media.contains(medium);
	}
	
    protected void fireMediumRemoved(final int index) {
        for (ListingListener l : listDataListeners)
            l.intervalRemoved(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index, index));
    }

    protected void fireMediumAdded(final int index) {
        for (ListingListener l : listDataListeners)
            l.intervalAdded(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, index, index));
    }

    /**
     * This function notifies changes in the behavior of the data.
     * Example: The current language has changed, therefor
     * every filter or sorting has to be reevaluated.
     */
    public void refresh() {
		for (ListingListener l : listDataListeners)
			l.refreshed();
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
	public Medium getElementAt(int index) {
		return media.get(index);
	}

	/* (non-Javadoc)
	 * @see javax.swing.ListModel#addListDataListener(javax.swing.event.ListDataListener)
	 */
	public void addListingListener(ListingListener arg0) {
		listDataListeners.add(arg0);
	}

	/* (non-Javadoc)
	 * @see javax.swing.ListModel#removeListDataListener(javax.swing.event.ListDataListener)
	 */
	public void removeListingListener(ListingListener arg0) {
		listDataListeners.remove(arg0);
	}

    public void entryAdded(CatalogEvent event) {
        Entry entry = event.getEntry();
        if (entries.containsKey(entry))
            return;
        // A new Entry was added outside this list
        // Create a link for list processing
        create(getMediumClassFromEntry(entry), entry);
        // Due to speed consideration we have to fire the event here
        fireMediumAdded(media.size() - 1);
    }

    public void entryRemoved(CatalogEvent event) {
        Entry entry = event.getEntry();
        if (!entries.containsKey(entry))
            return;
        
        Medium medium = entries.get(entry);
        int pos = media.indexOf(medium);
        // Remove medium from uuid mapping
        uuids.remove(medium);
        // Remove medium from list
        media.remove(pos);
        // Remove medium from catalog mapping
        entries.remove(entry);
        // Remove medium from grouped-by-type list
        mediaByType.get(medium.getClass()).remove(medium);
        if (mediaByType.get(medium.getClass()).isEmpty())
            mediaByType.remove(medium.getClass());
        
        fireMediumRemoved(pos);
    }

    public void entriesRemoved(CatalogEvent event) {
        for (Map.Entry<Entry, Medium> entry : entries.entrySet()) {
            if (entry.getKey().getTypeClassName().equals(event.getName())) {
                
                Medium medium = entry.getValue();
                
                int pos = media.indexOf(medium);
                // Remove medium from uuid mapping
                uuids.remove(medium);
                // Remove medium from list
                media.remove(pos);
                // Remove medium from catalog mapping
                entries.remove(entry);
                // Remove medium from grouped-by-type list
                mediaByType.get(medium.getClass()).remove(medium);
                if (mediaByType.get(medium.getClass()).isEmpty())
                    mediaByType.remove(medium.getClass());
            }
        }
        // A lot has changed, therefor just
        // rebuild the sorting and the filtering
        refresh();
    }

    public void optionAdded(CatalogEvent event) {
        // Do nothing
    }

    public void optionRemoved(CatalogEvent event) {
        // Do nothing
    }

    public void optionsRemoved(CatalogEvent event) {
        // Do nothing
    }

    public void entryChanged(EntryEvent event) {
        // Do nothing
    }

    public Medium getMediumForEntry(Entry entry) {
        return entries.get(entry);
    }

    public int size() {
        return media.size();
    }


    public boolean isEmpty() {
        return media.isEmpty();
    }


    public boolean contains(Object o) {
        return media.contains(o);
    }


    public Object[] toArray() {
        return media.toArray();
    }


    public <T> T[] toArray(T[] arg0) {
        return media.toArray(arg0);
    }


    public boolean add(Medium arg0) {
        // TODO Auto-generated method stub
        return false;
    }


    public boolean remove(Object arg0) {
        // TODO Auto-generated method stub
        return false;
    }


    public boolean containsAll(Collection< ? > arg0) {
        return media.containsAll(arg0);
    }


    public boolean addAll(Collection< ? extends Medium> arg0) {
        // TODO Auto-generated method stub
        return false;
    }


    public boolean addAll(int arg0, Collection< ? extends Medium> arg1) {
        // TODO Auto-generated method stub
        return false;
    }


    public boolean removeAll(Collection< ? > arg0) {
        // TODO Auto-generated method stub
        return false;
    }


    public boolean retainAll(Collection< ? > arg0) {
        // TODO Auto-generated method stub
        return false;
    }


    public void clear() {
        // TODO Auto-generated method stub
        
    }


    public Medium get(int index) {
        return media.get(index);
    }


    public Medium set(int arg0, Medium arg1) {
        // TODO Auto-generated method stub
        return null;
    }


    public void add(int arg0, Medium arg1) {
        // TODO Auto-generated method stub
        
    }


    public Medium remove(int index) {
        Medium medium = media.get(index);
        if (remove(medium))
            return medium;
        return null;
    }


    public int indexOf(Object medium) {
        return media.indexOf(medium);
    }


    public int lastIndexOf(Object medium) {
        return media.lastIndexOf(medium);
    }


    public ListIterator<Medium> listIterator() {
        // TODO Auto-generated method stub
        return null;
    }


    public ListIterator<Medium> listIterator(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }


    public List<Medium> subList(int index0, int index1) {
        return media.subList(index0, index1);
    }

    /**
     * Searches for a medium using the mediumUUID.
     * This will only find those medium directly on the top level.
     * This will not find any subentries.
     * 
     * @param mediumUUID the uuid of the medium that shall be returned
     * @return the medium with the uuid "mediumUUID"
     */
    public Medium getMediumByUUID(UUID mediumUUID) {
        return uuids.get(mediumUUID);
    }

    public Set<Medium> getMediaByType(Class<? extends Medium> type) {
        Set<Medium> ret = mediaByType.get(type);
        if (ret != null)
            return ret;
        else
            return new HashSet<Medium>();
    }
    
    public Set<Class< ? extends Medium>> getTypes() {
        return mediaByType.keySet();
    }

    public Iterable<Feature> getAllFeature(final Class< ? extends Feature> featureClass) {
        final Iterator<Feature> allFeatures = getAllFeature().iterator();
        return new Iterable<Feature>() {

            public Iterator<Feature> iterator() {
                return new Iterator<Feature>(){
                    
                    Feature lookahead = null;
                    
                    {
                        getLookahead();
                    }
                    
                    public void getLookahead(){
                        if (!allFeatures.hasNext()) {
                            lookahead = null;
                            return;
                        }
                        lookahead = allFeatures.next();

                        while (!featureClass.isAssignableFrom(lookahead.getClass())) {
                            if (!allFeatures.hasNext()) {
                                lookahead = null;
                                return;
                            }
                            lookahead = allFeatures.next();
                        }
                    }
                    
                    public boolean hasNext() {
                        return lookahead != null;
                    }

                    public Feature next() {
                        Feature ret = lookahead;
                        getLookahead();
                        return ret;
                    }

                    public void remove() {
                        // No need
                    }
                    
                };
            }
            
        };
    }
    public Iterable<Feature> getAllFeature() {
        return new Iterable<Feature>(){

            
            public Iterator<Feature> iterator() {

                /*
                 * Iterator that iterates over all media
                 * and for any media it iterates over all its features
                 */
                return new Iterator<Feature>(){

                    final Iterator<Medium> mediaIterator = Listing.this.iterator();
                    Iterator<Feature> mediumIterator = null;
                    
                    {
                        if (mediaIterator.hasNext())
                            mediumIterator = mediaIterator.next().getAllFeatures();
                    }
                    
                    public boolean hasNext() {
                        // If there is no more mediumIterator then return false
                        if (mediumIterator == null)
                            return false;
                        
                        if (!mediumIterator.hasNext()) {
                            if (!mediaIterator.hasNext()) {
                                mediumIterator = null;
                                return hasNext();
                            }

                            mediumIterator = mediaIterator.next().getAllFeatures();
                            return hasNext();
                        }
                        
                        return true;
                    }

                    public Feature next() {
                        // If there is no more mediumIterator then return false
                        if (mediumIterator == null)
                            return null;
                        
                        if (!mediumIterator.hasNext()) {
                            if (!mediaIterator.hasNext()) {
                                mediumIterator = null;
                                return next();
                            }

                            mediumIterator = mediaIterator.next().getAllFeatures();
                            return next();
                        }
                        
                        return mediumIterator.next();
                    }

                    public void remove() {
                        // there will be no remove
                    }
                    
                };
            }
            
        };
    }
}
