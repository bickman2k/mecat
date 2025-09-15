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
 * Created on Jan 15, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.option.preferences;

import java.net.URL;
import java.util.LinkedHashMap;

import net.sourceforge.mecat.catalog.datamanagement.Catalog;
import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.PersistentThroughEntry;
import net.sourceforge.mecat.catalog.datamanagement.Util;
import net.sourceforge.mecat.catalog.datamanagement.xml.XMLCatalogFactory;
import net.sourceforge.mecat.catalog.datamanagement.xml.XMLConnection;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.medium.features.option.FeatureOption;
import net.sourceforge.mecat.catalog.medium.features.option.merger.FeatureOptionMerger;
import net.sourceforge.mecat.catalog.medium.features.validators.FeatureValidator;

public class DefaultFeaturesOption implements FeaturesOption {

    // Map from a feature class to an entry that contains a validator for this class
    // Validators are used for filters.
    // Contains only the validators for those features that have been investigated with aquireOption
    LinkedHashMap<Class<? extends Feature>, Entry> validators = new LinkedHashMap<Class<? extends Feature>, Entry>();

    // Map from a feature class to an entry that contains a option merger for this class
    // Option Mergers are used to merge options during an import.
    // Contains only the validators for those features that have been investigated with aquireOption
    LinkedHashMap<Class<? extends Feature>, Entry> mergers = new LinkedHashMap<Class<? extends Feature>, Entry>();

    // Map from a feature class to its instance of Feature Options
    // Contains only the feature option for those features that have been investigated with aquireOption
    LinkedHashMap<Class< ? extends Feature>, FeatureOption> options = new LinkedHashMap<Class< ? extends Feature>, FeatureOption>();

    protected void aquireFeatureOption(Class< ? extends Feature> featureClass, Entry entry) {
        if (entry == null)
            return;
        
        PersistentThroughEntry pte = Util.loadFromEntry(entry);
        if (!(pte instanceof FeatureOption))
            return;
        
        options.put(featureClass, (FeatureOption) pte);
    }

    protected void aquireFeatureValidator(Class< ? extends Feature> featureClass, Entry entry) {
        if (entry == null) 
            return;
        
        validators.put(featureClass, entry);        
    }
    
    protected void aquireFeatureOptionMerger(Class< ? extends Feature> featureClass, Entry entry) {
        if (entry == null) 
            return;
        
        mergers.put(featureClass, entry);        
    }
    
    /**
     * Aquire all default information of the feature class given with feature class.
     * This function is going to be invoked at the time the information is needed.
     * 
     * @param featureClass Feature class to aquire all information of
     */
    protected void aquireOption(Class< ? extends Feature> featureClass) {
        // There is no need to check the features for this class again
        // and this way it is clear that for this feature class the check was done
        if (!options.containsKey(featureClass))
            options.put(featureClass, null);

        URL url = featureClass.getResource(featureClass.getSimpleName() + ".preferences.xml");
        if (url == null) 
            return;

        Catalog catalog = XMLCatalogFactory.catalogFactory.openCatalog(new XMLConnection(url));
        if (catalog == null)
            return;
        
        aquireFeatureOption(featureClass, catalog.getOption("FeatureOption"));
        aquireFeatureValidator(featureClass, catalog.getOption("FeatureValidator"));
        aquireFeatureOptionMerger(featureClass, catalog.getOption("FeatureOptionMerger"));
    }
    
    public FeatureOption getOption(Class< ? extends Feature> featureClass) {
        if (!options.containsKey(featureClass)) 
            aquireOption(featureClass);

        return options.get(featureClass);
    }
    
    public boolean hasValidator(Class< ? extends Feature> featureClass) {
        if (!options.containsKey(featureClass)) 
            aquireOption(featureClass);

        return validators.containsKey(featureClass);
    }
    
    public FeatureValidator getValidator(Class< ? extends Feature> featureClass) {
        if (!options.containsKey(featureClass)) 
            aquireOption(featureClass);

        return (FeatureValidator) Util.loadFromEntry(validators.get(featureClass));
    }

    public boolean hasOptionMerger(Class< ? extends Feature> featureClass) {
        if (!options.containsKey(featureClass)) 
            aquireOption(featureClass);

        return mergers.containsKey(featureClass);
    }
    
    public FeatureOptionMerger getOptionMerger(Class< ? extends Feature> featureClass) {
        if (!options.containsKey(featureClass)) 
            aquireOption(featureClass);

        return (FeatureOptionMerger) Util.loadFromEntry(mergers.get(featureClass));
    }

}
