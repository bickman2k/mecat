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
 * Created on Jan 6, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.option.preferences;

import java.awt.Component;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import net.sourceforge.mecat.catalog.datamanagement.Catalog;
import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.PersistentThroughEntry;
import net.sourceforge.mecat.catalog.gui.features.desktop.FeatureDesktop;
import net.sourceforge.mecat.catalog.medium.Listing;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.medium.features.MultiImageFeature;
import net.sourceforge.mecat.catalog.medium.features.SubEntryListFeature;
import net.sourceforge.mecat.catalog.medium.features.impl.Ident;
import net.sourceforge.mecat.catalog.medium.features.option.MultiImageFeatureOption;
import net.sourceforge.mecat.catalog.medium.hidden.BagImage;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.srp.utils.NiceClass;

public abstract class AbstractMediaOption implements PersistentThroughEntry, MediaOption {

    static Catalog dummyCatalog = Options.stdFactory.createCatalog((Component)null);
    static Listing dummyListing = new Listing(dummyCatalog);

    // Information variables
    // they contain no preferences
    
    // All available features
    static Set<Class<? extends Feature>> features = new HashSet<Class<? extends Feature>>();
    // A dummy instance for every medium type
    static LinkedHashMap<Class<? extends Medium>, Medium> media = new LinkedHashMap<Class<? extends Medium>, Medium>();
    // A dummy instance for every feature type
    public static LinkedHashMap<Class<? extends Feature>, Feature> featuresInstances = new LinkedHashMap<Class<? extends Feature>, Feature>();
    // A list of all types directly accessable by a medium
    static LinkedHashMap<Class<? extends Medium>, Collection<Class<? extends Feature>>> mediaFeatures = new LinkedHashMap<Class<? extends Medium>, Collection<Class<? extends Feature>>>();
    // Defines where a feature for a specified medium is wanted
    public static LinkedHashMap<Class<? extends Medium>, List<Feature>> mediumFeatureInstances = new LinkedHashMap<Class<? extends Medium>, List<Feature>>();

    
    
    // Defines what media are wanted
    // true means the medium shall be displayed
    // false means the medium shall not be displayed
    LinkedHashMap<Class<? extends Medium>, Boolean> wantedMedia = new LinkedHashMap<Class<? extends Medium>, Boolean>();
    
    // Defines where a feature for a specified medium is wanted
    LinkedHashMap<Class<? extends Medium>, LinkedHashMap<Class<? extends Feature>, LinkedHashMap<Class<? extends FeatureDesktop>, Boolean>>> wantedFeatures = new LinkedHashMap<Class<? extends Medium>, LinkedHashMap<Class<? extends Feature>, LinkedHashMap<Class<? extends FeatureDesktop>, Boolean>>>();
    
    public AbstractMediaOption () {
        // Initialise with default values
        for (Class<? extends Medium> mediumClass : getMedia()) {
            wantedMedia.put(mediumClass, true);
            LinkedHashMap<Class<? extends Feature>, LinkedHashMap<Class<? extends FeatureDesktop>, Boolean>> mediumMap = new LinkedHashMap<Class<? extends Feature>, LinkedHashMap<Class<? extends FeatureDesktop>, Boolean>>();
            wantedFeatures.put(mediumClass, mediumMap);
            for (Class<? extends Feature> featureClass : getFeatures(mediumClass)) {
                LinkedHashMap<Class<? extends FeatureDesktop>, Boolean> featureMap = new LinkedHashMap<Class<? extends FeatureDesktop>, Boolean>();
                mediumMap.put(featureClass, featureMap);
                for (NiceClass<FeatureDesktop> desktopClass : Options.desktops) {
                    if (featureClass.equals(Ident.class))
                        featureMap.put(desktopClass.getClasstype(), false);
                    else
                        featureMap.put(desktopClass.getClasstype(), true);
                }
            }
        }
        // Nothing is overriden
        for (Class<? extends Medium> mediumClass : getMedia())
            mediumOverriden.put(mediumClass, false);
    }
    
    public boolean isWanted(Class<? extends Medium> mediumClass) {
        return wantedMedia.get(mediumClass);
    }
    
    public boolean isWanted(Class<? extends Medium> mediumClass, Class<? extends Feature> featureClass, Class<? extends FeatureDesktop> desktopClass) {
        return wantedFeatures.get(mediumClass).get(featureClass).get(desktopClass);
    }
    
    public void setWanted(Class<? extends Medium> mediumClass, boolean wanted) {
        wantedMedia.put(mediumClass, wanted);
    }

    public void setWanted(Class<? extends Medium> mediumClass, Class<? extends Feature> featureClass, Class<? extends FeatureDesktop> desktopClass, boolean wanted) {
        wantedFeatures.get(mediumClass).get(featureClass).put(desktopClass, wanted);
    }
    

    
    
    boolean mediaOptionOverriden = false;
    
    public boolean isMediaOptionOverriden() {
        return mediaOptionOverriden;
    }
    
    abstract public void setMediaOptionOverriden(final boolean override);
    
    LinkedHashMap<Class<? extends Medium>, Boolean> mediumOverriden = new LinkedHashMap<Class<? extends Medium>, Boolean>();

    public boolean isMediumOptionOverriden(Class <? extends Medium> mediumClass) {
        return mediumOverriden.get(mediumClass);
    }
    
    abstract public void setMediumOptionOverrriden(Class <? extends Medium> mediumClass, boolean overriden);
    
    
    
    
    
    
    public boolean loadFromEntry(Entry entry) {
        // Get overriding status for media information
        // if there is no status this means not overriden
        String overrideMediaOptionEntry = entry.getAttribute("MediaOptionOverriden");
        if (overrideMediaOptionEntry != null) 
            setMediaOptionOverriden(overrideMediaOptionEntry.equalsIgnoreCase("true"));
        else
            setMediaOptionOverriden(false);
        for (Class<? extends Medium> mediumClass : getMedia()) {
            Entry mediumEntry = entry.getSubEntry(mediumClass.getName());
            if (mediumEntry == null)
                continue;
            String vis = mediumEntry.getAttribute("Visible");
            if (vis != null) 
                wantedMedia.put(mediumClass, vis.equalsIgnoreCase("true"));
            
            // Get overriding status for medium information
            // if there is no status this means not overriden
            String overrideMediumOptionEntry = mediumEntry.getAttribute("MediumOptionOverriden");
            if (overrideMediumOptionEntry != null) 
                setMediumOptionOverrriden(mediumClass, overrideMediumOptionEntry.equalsIgnoreCase("true"));
            else
                setMediumOptionOverrriden(mediumClass, false);

            for (Class<? extends Feature> featureClass : getFeatures(mediumClass)) {
                Entry featureEntry = mediumEntry.getSubEntry(featureClass.getName());
                if (featureEntry == null)
                    continue;
                for (NiceClass<FeatureDesktop> desktopClass : Options.desktops) {
                    String desktopVis = featureEntry.getAttribute(desktopClass.getClasstype().getName());
                    if (desktopVis != null) 
                        wantedFeatures.get(mediumClass).get(featureClass).put(desktopClass.getClasstype(), desktopVis.equalsIgnoreCase("true"));
                }
            }
        }
        return true;
    }

    public void saveToEntry(Entry entry) {
        entry.setAttribute("MediaOptionOverriden", (isMediaOptionOverriden())?"true":"false");
        for (Class<? extends Medium> mediumClass : getMedia()) {
            // If the media Option is not overriden and
            // the preferences for this media are overriden neither
            // then there is no reason to mention this medium at all
            if (!isMediaOptionOverriden() && !mediumOverriden.get(mediumClass))
                continue;
            
            Entry mediumEntry = entry.createSubEntry(mediumClass.getName());
            // Only write the information about the visibilty down
            // if is overriden from Options.AppPrefs
            if (isMediaOptionOverriden())
                mediumEntry.setAttribute("Visible", wantedMedia.get(mediumClass).toString());

            if (isMediumOptionOverriden(mediumClass)) {
                mediumEntry.setAttribute("MediumOptionOverriden", "true");
                for (Class<? extends Feature> featureClass : getFeatures(mediumClass)) {
                    Entry featureEntry = mediumEntry.createSubEntry(featureClass.getName());
                    for (NiceClass<FeatureDesktop> desktopClass : Options.desktops)
                        featureEntry.setAttribute(desktopClass.getClasstype().getName(), wantedFeatures.get(mediumClass).get(featureClass).get(desktopClass.getClasstype()).toString());
                }
            }
        }
    }
    
    
    
    
    
    
    static {
        for (Class<? extends Medium> mediumClass : Options.media) {
            Medium medium = dummyListing.create(mediumClass);
            media.put(mediumClass, medium);
            features.addAll(getFeatures(mediumClass));
        }
    }


    /**
     * Returns a list of all media
     * @return a list of all media
     */
    static public Collection<Class<? extends Medium>> getMedia() {
        return media.keySet();
    }

    static public Medium getMedia(Class<? extends Medium> cls) {
        return media.get(cls);
    }

    /**
     * Returns a list of all reachable features
     * @return a list of all reachable features
     */
    static public Set<Class<? extends Feature>> getFeatures() {
        return features;
    }
    
    /**
     * Returns a list of all features directly reachable from the collection of media given.
     * @param media
     * @return a list of all directly reachable features from "media"
     */
    static public Set<Class<? extends Feature>> getFeatures(Collection<Class<? extends Medium>> media) {
        Set<Class<? extends Feature>> ret = new HashSet<Class<? extends Feature>>();
        for (Class<? extends Medium> medium : media)
            ret.addAll(getFeatures(medium));
        return ret;
    }
    
    /**
     * Returns a list of all features directly reachable from a medium given with mediumClass.
     * @param mediumClass a list of all directly reachable features from "mediumClass"
     * @return a list of directly reachable features from "mediumClass"
     */
    static public Collection<Class<? extends Feature>> getFeatures(Class<? extends Medium> mediumClass) {
        if (!mediaFeatures.containsKey(mediumClass)) {
            Medium medium = media.get(mediumClass);
            if (medium == null)
                mediaFeatures.put(mediumClass, null);
            else {
                Set<Class<? extends Feature>> features = new HashSet<Class<? extends Feature>>();
                Vector<Feature> instances = new Vector<Feature>();
                for (Feature feature : medium.getFeatures()) {
                    features.add(feature.getClass());
                    instances.add(feature);
                    featuresInstances.put(feature.getClass(), feature);
                }
                mediaFeatures.put(mediumClass, features);
                mediumFeatureInstances.put(mediumClass, instances);
            }
        }
        return mediaFeatures.get(mediumClass);
    }
    
    /**
     * Returns a list of all features reachable from the collection of media given.
     * @param media
     * @return a list of all features reachable from "media"
     */
    static public Set<Class<? extends Feature>> getAllFeatures(Collection<Class<? extends Medium>> media) {
        Set<Class<? extends Medium>> m = getReachableMedia(media);
        return getFeatures(m);
    }

    /**
     * Returns a list of all features reachable from a medium given with mediumClass.
     * @param mediumClass
     * @return a list of all features reachable from "mediumClass"
     */
    static public Collection<Class<? extends Feature>> getAllFeatures(Class<? extends Medium> mediumClass) {
        Set<Class<? extends Medium>> media = getReachableMedia(mediumClass);
        return getFeatures(media);
    }

    static public Set<Class<? extends Medium>> getReachableMedia(final Class< ? extends Medium> medium) {
        return getReachableMedia(new Vector<Class<? extends Medium>>(1){{add(medium);}});
    }
    
    /**
     * This function searches all subentry features and gets there media types too
     * @param media
     * @return a list of all media reachable from "media"
     */
    static public Set<Class<? extends Medium>> getReachableMedia(Collection<Class< ? extends Medium>> media) {
        Set<Class<? extends Medium>> ret = new HashSet<Class<? extends Medium>>();

        for (Class<? extends Medium> type : media) {
            ret.add(type);
            for (Class<? extends Feature> feature : AbstractMediaOption.getFeatures(type)){
                if (SubEntryListFeature.class.isAssignableFrom(feature)) {
                    SubEntryListFeature sub = (SubEntryListFeature) featuresInstances.get(feature);
                    ret.add(sub.getType());
                }
                if (MultiImageFeature.class.isAssignableFrom(feature)) {
                    MultiImageFeature mif = (MultiImageFeature) featuresInstances.get(feature);
                    MultiImageFeatureOption option = (MultiImageFeatureOption) mif.getFeatureOption();
                    // Asking the option works because isBag is a very special property
                    // the value is allways the same for one type(class)
                    if (option.isBag()) 
                        ret.add(BagImage.class);
                }
            }
        }
        
        if (ret.size() == media.size())
            return ret;
        
        return getReachableMedia(ret);
    }

    
    
    
/*    static public Feature getFeatureDummyInstance(Class<? extends Feature> feature) {
        return featuresInstances.get(feature);
    }*/
}
