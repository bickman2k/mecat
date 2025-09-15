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
package net.sourceforge.mecat.catalog.medium.features.impl;

import java.net.MalformedURLException;
import java.net.URL;

import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.ImageFeature;
import net.sourceforge.mecat.catalog.medium.features.MultiImageFeature;
import net.sourceforge.mecat.catalog.medium.features.option.MultiImageFeatureOption;
import net.sourceforge.mecat.catalog.medium.features.option.FeatureOption;
import net.sourceforge.mecat.catalog.medium.hidden.BagImage;
import net.sourceforge.mecat.catalog.option.Options;

// Get bagOption if split on 
// and sharedOption if split off
// store as UUID of MultiImageFeature as Directory and UUID of BagImage as Name
public class BagImageFeature extends ImageFeature {
    
//    MultiImageFeature owner = null;
  
    
    
    public BagImageFeature(Medium medium) {
        super(medium, "BagImage", "BagImage");
    }

    /**
     * Has no default option. 
     * Option directly depends on owner.
     */
    @Override
    public FeatureOption getFeatureOption() {
        MultiImageFeature owner = getOwner();
        if (owner == null)
            return null;
        
         FeatureOption featureOption = owner.getFeatureOption();
         if (!(featureOption instanceof MultiImageFeatureOption))
             return null;
         
         MultiImageFeatureOption multiImageFeatureOption = ( MultiImageFeatureOption ) featureOption;
         
         if (multiImageFeatureOption.isSplitOption())   
             return multiImageFeatureOption.getBagOption();
         else
             return multiImageFeatureOption.getSharedOption();
    }

    @Override
    public URL getStorePosition() throws MalformedURLException {
        MultiImageFeature owner = getOwner();
        if (owner == null)
            return null;
        
        URL url = new URL(getImageFeatureOption().getDirLocation(), 
                  owner.medium.getFeature(Ident.class).getShortText() + "/" 
                + BagImageFeature.this.medium.getFeature(Ident.class).getShortText() + ".jpeg");
        Options.ensureDirectory(getImageFeatureOption().getDirLocation().getFile() + owner.medium.getFeature(Ident.class).getShortText() + "/");
        
        return url;
    }

    public MultiImageFeature getOwner() {
        if (!(medium instanceof BagImage))
            return null;
        
        BagImage bagImage = (BagImage) medium;
        return bagImage.getOwner();
    }
    
}
