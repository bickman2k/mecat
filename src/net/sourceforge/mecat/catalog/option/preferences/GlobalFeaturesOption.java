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
 * Created on Jan 12, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.option.preferences;

import net.sourceforge.mecat.catalog.datamanagement.Util;
import net.sourceforge.mecat.catalog.medium.features.Feature;


public class GlobalFeaturesOption extends AbstractFeaturesOption {

    public void setOverriden(Class <? extends Feature> featureClass, boolean override) {
        // If the feature gets overrriden takes the Global settings
        if (override && !overriden.get(featureClass)) {
            FeaturesOption fo = DefaultPreferences.defaultPreferences.getFeaturesOption();
            
            options.put(featureClass, Util.copyPTE(fo.getOption(featureClass)));
        }
        if (!override && overriden.get(featureClass))
            options.put(featureClass, null);
        // If the feature is no longer overriden removes the option
        overriden.put(featureClass, override);
    }

}
