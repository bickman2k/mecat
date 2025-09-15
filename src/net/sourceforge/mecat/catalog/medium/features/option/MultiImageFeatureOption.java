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
 * Created on Sep 13, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.medium.features.option;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.PersistentThroughEntry;
import net.sourceforge.mecat.catalog.datamanagement.Util;
import net.sourceforge.mecat.catalog.datamanagement.Util.Argument;

public class MultiImageFeatureOption implements FeatureOption {

    Vector<ImageFeatureOption> imgOptions = new Vector<ImageFeatureOption>();
    
    // A map from the key name of the image feature to the options for the image
    // this mapping only stores options for images that are fixed ( not in the bag )
    LinkedHashMap<String, ImageFeatureOption> map = new LinkedHashMap<String, ImageFeatureOption>();

    // Does the options can be set differently
    // for every image (fixed images / those not contained in the bag)
    boolean splitOption = false;
    
    // Options set for all images
    // these options are relevant if splitOption == false
    ImageFeatureOption sharedOption = new ImageFeatureOption("shared");

    // Options for all bag images
    // these options are relevant if splitOption == true
    ImageFeatureOption bagOption = null;
    
    // Maximum scale factor for images.
    // Even if the space would allow a bigger scaling 
    // this is the maximum scaling used
    double maxScale = 1.0;
    
    public MultiImageFeatureOption(final Boolean splitOption) {
        this.splitOption = splitOption;
    }
    public MultiImageFeatureOption() {}
    
    public Vector<ImageFeatureOption> getImageFeatureOptions() {
        return imgOptions;
    }

    /**
     * If the MultiImageFeature allows an unbound number of images it has
     * the bag ability. This function allows to check wether a feature has 
     * the bag ability. The Bag-Ability does not change for a feature.
     * 
     * @return true if the feature is an MultiImageFeature with bag ability
     */
    public boolean isBag() {
        return bagOption != null;
    }
    
    public boolean loadFromEntry(Entry entry) {
        // Get shared information from entry
        Entry sharedEntry = entry.getSubEntry("Shared");
        PersistentThroughEntry pte = Util.loadFromEntry(sharedEntry);
        if (pte instanceof ImageFeatureOption)
            sharedOption = ( ImageFeatureOption ) pte;            

        // Get bag information from entry
        Entry bagEntry = entry.getSubEntry("Bag");
        if (bagEntry != null) {
            pte = Util.loadFromEntry(bagEntry);
            if (pte instanceof ImageFeatureOption)
                bagOption = ( ImageFeatureOption ) pte;
        }
        
        List<? extends Entry> imgEntries = entry.getSubEntries("ImageOption");
        for (Entry imgEntry : imgEntries) {
           pte = Util.loadFromEntry(imgEntry);
           if (!(pte instanceof ImageFeatureOption))
               continue;
           ImageFeatureOption imageFeatureOption = (ImageFeatureOption) pte;
           imgOptions.add(imageFeatureOption);
           map.put(imageFeatureOption.getName(), imageFeatureOption);
        }
        
        String maxScaleStr = entry.getAttribute("MaxScale");
        if (maxScaleStr != null)
            try {
                setMaxScale(Double.parseDouble(maxScaleStr));
            } catch (Exception e) {
                e.printStackTrace();
            }
        
        return true;
    }

    public void saveToEntry(Entry entry) {
        if (splitOption)
            Util.addArgument(entry, new Argument(0, Boolean.class, splitOption));

        for (ImageFeatureOption imageFeatureOption : imgOptions) {
            Entry imgEntry = entry.createSubEntry("ImageOption");
            Util.saveToEntry(imageFeatureOption, imgEntry);
        }
        
        Entry imgEntry = entry.createSubEntry("Shared");
        Util.saveToEntry(sharedOption, imgEntry);

        if (isBag()) {
            imgEntry = entry.createSubEntry("Bag");
            Util.saveToEntry(sharedOption, imgEntry);
        }
    
        if (getMaxScale() != 1.0) 
            entry.setAttribute("MaxScale", Double.toString(getMaxScale()));
    }
    public boolean isSplitOption() {
        return splitOption;
    }
    public void setSplitOption(boolean splitOption) {
        this.splitOption = splitOption;
        
    }
    public ImageFeatureOption getSharedOption() {
        return sharedOption;
    }
    
    public double getMaxScale() {
        return maxScale;
    }
    public void setMaxScale(double maxScale) {
        this.maxScale = maxScale;
    } 

    /**
     * Returns a map that associates a image feature option to every fixed image.
     * @return
     */
    public LinkedHashMap<String, ImageFeatureOption> getMap() {
        return map;
    }
    public ImageFeatureOption getBagOption() {
        return bagOption;
    }
    
    
}
