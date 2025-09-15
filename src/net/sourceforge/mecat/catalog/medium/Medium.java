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
 */
package net.sourceforge.mecat.catalog.medium;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import net.sourceforge.mecat.catalog.datamanagement.AttributeEvent;
import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.EntryClearedEvent;
import net.sourceforge.mecat.catalog.datamanagement.EntryListener;
import net.sourceforge.mecat.catalog.datamanagement.SetAttributeEvent;
import net.sourceforge.mecat.catalog.datamanagement.SubEntryEvent;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.medium.features.impl.Ident;
import net.sourceforge.mecat.catalog.option.Options;

abstract public class Medium {

    protected Map<Class<? extends Feature>, Feature> featureMap = new LinkedHashMap<Class<? extends Feature>, Feature>();
	protected List<Feature> features = new Vector<Feature>();
    protected Listing listing = null;
	public String type;
    
    Medium parentMedium = null;
    public void setParentMedium(Medium parentMedium) {
        this.parentMedium = parentMedium;
    }
    public Medium getParentMedium() {
        return parentMedium;
    }
    
    
    public Listing getListing() {
        return listing;
    }

    public void setListing(Listing listing) {
        this.listing = listing;
    }

    public void addFeature(final int pos, final Feature feature) {
        featureMap.put(feature.getClass(), feature);
        features.add(pos, feature);
    }
    
    public void addFeature(final Feature feature) {
        featureMap.put(feature.getClass(), feature);
        features.add(feature);
    }
    
    public List<Feature> getFeatures(){
        return features;
    }
    
	public String getName() {
        
	    //  In the initial construction there was one resource file for all Medium classes and there could have been
        // two Medium class with the same name for this reason I used a unique Name for the getString method from the resource
	    //		return Options.getI18N(getClass()).getString(Entry.getEasyClassName(getClass().getName()));
        // Since now the resource for the class depends upon the class we can easly use the SimpleName from the Class class.
        return Options.getI18N(getClass()).getString(getClass().getSimpleName());
	}

	public Entry entry = null;
	
	public Medium(Entry entry, Listing listing) {
		this.entry = entry;
		this.listing = listing;
        // Adding Ident with a function allows to override it
        // and to create a medium without an ident.
        addIdent();
        
        entry.addEntryListener(new EntryListener(){
            public void attributeSet(AttributeEvent event) {
                fireMediumChanged();
            }

            public void setAttributeAdded(SetAttributeEvent event) {
                fireMediumChanged();
            }

            public void setAttributeRemoved(SetAttributeEvent event) {
                fireMediumChanged();
            }

            public void subEntryCreated(SubEntryEvent event) {
                fireMediumChanged();
            }

            public void subEntryRemoved(SubEntryEvent event) {
                fireMediumChanged();
            }

            public void subEntriesRemoved(SubEntryEvent event) {
                fireMediumChanged();
            }

            public void setAttributeCleared(SetAttributeEvent event) {
                fireMediumChanged();
            }

            public void entryCleared(EntryClearedEvent event) {
                fireMediumChanged();
            }
        });
	}
    
    protected void addIdent() {
        addFeature(new Ident(this));    
    }

	public Medium getCopy() {
		return getCopy(listing);
	}
	public Medium getCopy(Listing listing) {
		Medium medium;
		// We need a listing to Copy it into
		if (listing == null)
			return null;

		medium = listing.create(getClass());
		copyTo(medium);
		return medium;
	}
	
	/**
	 * If you overload this function. 
	 * The returned String should start with the super.toString.
	 */
	public String toString(){
		// TODO Raum f?r mehr Optionen schaffen		
		if (Options.isShowMediumTypeInList())
			return getName() + " : " + displayName();	
		else
			return displayName();
	}

    public abstract String displayName();
    

	/**
	 * Find the first instance of the feature that is an instanceof featureType
	 * @param featureType The wanted feature. 
	 * @return the first feature that matches the type featureType iff there is at least one feature of the type featureType 
	 * else returns null
	 */
	public <F extends Feature>  F getFeature(Class<F> featureType) {
		if (featureType == null)
			return null;
			
//		for (Feature f : features)
//			if (featureType.isInstance(f))
//				return (T)f;
				
		return (F)featureMap.get(featureType);
	}
	
	/**
	 * 
	 * 
	 * Same as features.indexOf(getFeature(featureType))
     * Returns the index of the feature with type featureType
     * out of the list of all features of the medium.
     * 
	 * @param featureType type of the feature
	 * @return Index of the feature
	 */
	public int getFeaturePosition(Class<? extends Feature> featureType) {
		return features.indexOf(getFeature(featureType));
	}

 
    
    Vector<MediumListener> mediumListeners = new Vector<MediumListener>();
    
    public void addMediumListener(final MediumListener mediumListener){
        mediumListeners.add(mediumListener);
    }
   
    public void removeMediumListener(final MediumListener mediumListener){
        mediumListeners.remove(mediumListener);
    }

    
    String oldName = null;
    
    protected void fireMediumChanged(){
        String str = toString();
        if ((oldName == null) || (!oldName.equals(str))) {
            fireNameChanged();
            oldName = str;
        }
        for (MediumListener mediumListener : new Vector<MediumListener>(mediumListeners))
            mediumListener.mediumChanged(this);
    }

    protected void fireNameChanged(){
        for (MediumListener mediumListener : new Vector<MediumListener>(mediumListeners))
            mediumListener.nameChanged(this);
    }

    public Iterator<Feature> getAllFeatures() {

        return new Iterator<Feature>(){

            // Iterate over directly linked features
            Iterator<? extends Feature> directFeatures = features.iterator();

            // Iterate over the media connected to the directly linked features
            Iterator<? extends Medium> subMedia = null;
            
            // Iterate over the features from the media connected to the directly linked features
            Iterator<? extends Feature> subMediumFeatures = null;

            public boolean hasNext() {
                // If there is no sub Media, 
                // this means the next from direct features will 
                // be evaluated and if there is a next we get a next
                if (subMedia == null) 
                    return directFeatures.hasNext();

                // If there is no more Sub Medium Features Iterator
                // try to refill it and start recursive
                if (subMediumFeatures == null) {
                    if (!subMedia.hasNext()) {
                        subMedia = null;
                        // We could return hasNext() too
                        // but this is faster and does the same
                        return directFeatures.hasNext();
                    }
                    subMediumFeatures = subMedia.next().getAllFeatures();
                    return hasNext();
                }

                // If there is no more feature in the Sub Medium Features Iterator
                // try to get a new one
                if (!subMediumFeatures.hasNext()) {
                    subMediumFeatures = null;
                    return hasNext();
                }
                
                // We now have a subMediumFeature with hasNext true
                // therefor there is a next
                return true;
            }

            public Feature next() {
                // First return the feature itself
                if (subMedia == null) {
                    Feature feature = directFeatures.next();

                    subMedia = feature.getSubMedia();
                    
                    return feature;
                }

                // If there is no more Sub Medium Features Iterator
                // try to refill it and start recursive
                if (subMediumFeatures == null) {
                    if (!subMedia.hasNext()) {
                        subMedia = null;
                        return next();
                    }
                    subMediumFeatures = subMedia.next().getAllFeatures();
                    return next();
                }

                // If there is no more feature in the Sub Medium Features Iterator
                // try to get a new one
                if (!subMediumFeatures.hasNext()) {
                    subMediumFeatures = null;
                    return next();
                }
                
                return subMediumFeatures.next();
            }

            public void remove() {
                // there will be no remove
            }
            
        };
    }

    public void copyTo(Medium medium) {
        for (Feature feature : features) 
            feature.copyTo(medium.getFeature(feature.getClass()));
    }
    
    public void copyToUseMapping(Medium medium, Map<Medium, Map<Listing, Medium>> mapping) {
        for (Feature feature : features) 
            feature.copyToUseMapping(medium.getFeature(feature.getClass()), mapping);
    }

    
    
    
    
}
