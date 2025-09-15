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
 * Created on Sep 15, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.medium.features.option.merger;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import net.sourceforge.mecat.catalog.datamanagement.Catalog;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.Listing;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.medium.features.option.FeatureOption;
import net.sourceforge.mecat.catalog.option.preferences.AbstractMediaOption;
import net.sourceforge.mecat.catalog.option.preferences.DefaultPreferences;

public class MergeProcess {

    final Listing listing1; 
    final Listing listing2;
    final Component parent;

    // Mapping from Feature to MergerUserInterface
    // there will be a mapping for every feature where the import catalog has an opinion
    // and there exists a FeatureOptionMerger allowing as to import this through option represented opinion.
    Map<Class<? extends Feature>, MergeUserInterface> interfaces = new HashMap<Class<? extends Feature>, MergeUserInterface>();

    Vector<Class<? extends Feature>> showInterface = new Vector<Class<? extends Feature>>();
    
    
    
    public MergeProcess(final Listing listing1, final Listing listing2, final Component parent) {
        this.listing1 = listing1;
        this.listing2 = listing2;
        this.parent = parent;
        
        // Need to merge options from both lists
        
        // First get all used features for the 
        // import catalog
        Set<Class<? extends Feature>> importTypes = AbstractMediaOption.getAllFeatures(listing2.getTypes());
        
        
        for (Class<? extends Feature> feature : /*intersection*/ importTypes) {
            FeatureOption option1 = listing1.getTotalPreferences().getFeaturesOption().getOption(feature);
            FeatureOption option2 = listing2.getCatalogPreferences().getFeaturesOption().getOption(feature);
            
            // If the import has no option, then we don't need to consider any changes
            if (option2 == null)
                continue;
            
            // If the feature does not have a merge this means it does not need to be imported
            if (!DefaultPreferences.defaultPreferences.getFeaturesOption().hasOptionMerger(feature))
                continue;
            
            FeatureOptionMerger merger = DefaultPreferences.defaultPreferences.getFeaturesOption().getOptionMerger(feature);
            MergeUserInterface mui = merger.merge(option1, option2);

            // If there is no Merge User Interface for this feature then,
            // we do not need to proceed any further
            if (mui == null)
                continue;

            interfaces.put(feature, mui);
            
            if (MergeType.NO_CONFLICT != mui.getType())
                showInterface.add(feature);
        }

    }
    
    public void merge() {
        if (!showInterface.isEmpty()) {
            MergeProcessGUI mpGUI = getMergeProcessGUI(parent);
            mpGUI.setModal(true);
            mpGUI.setVisible(true);
            
            // If the user pushed cancel or close the window
            // we don't wont to finish the process.
            if (!mpGUI.finishedCorrectly)
                return;
        }
        
        // Convinience variables
        Catalog catalog1 = listing1.catalog;
        Catalog catalog2 = listing2.catalog;

        // Apply all changes to the catalogs entries
        for (Map.Entry<Class<? extends Feature>, MergeUserInterface> entry : interfaces.entrySet()) {
            entry.getValue().applyChangeToCatalog1(listing1, entry.getKey());
            entry.getValue().applyChangeToCatalog2(listing2, entry.getKey());
        }
        
        // Change the option setting for catalog 1
        for (Map.Entry<Class<? extends Feature>, MergeUserInterface> entry : interfaces.entrySet()) {
            listing1.getCatalogPreferences().getFeaturesOption().setOption(entry.getKey(), entry.getValue().getMergeResult());
            listing2.getCatalogPreferences().getFeaturesOption().setOption(entry.getKey(), entry.getValue().getMergeResult());
        }

        // Make a mapping from all medium that will be imported to the one that are actualy created
        // This is implemented as mapping to mapping because, we allow different media for different listings.
        // Say one person from the import catalog is used for two different features and 
        // both features store the information at diffrent location, say stores it directly in the main catalog (internal)
        // and one stores it in an extra actor catalog (external).
        Map<Medium, Map<Listing, Medium>> mapping = new LinkedHashMap<Medium, Map<Listing, Medium>>();
        
        // Import the entries
        for (Medium medium : listing2) {
            Medium newMedium = listing1.create(medium.getClass());
            Map<Listing, Medium> map = new LinkedHashMap<Listing, Medium>();
            map.put(listing1, newMedium);
            mapping.put(medium, map);
        }
        
        for (Map.Entry<Medium, Map<Listing, Medium>> entry : mapping.entrySet()) {
            entry.getKey().copyToUseMapping(entry.getValue().get(listing1), mapping);
        }
    }




    public MergeProcessGUI getMergeProcessGUI(Component component) {
        MergeProcessGUI dialog = null;
        
        while (component != null && !(component instanceof Frame) && !(component instanceof Dialog))
            component = component.getParent();

        if (component == null)
            dialog = new MergeProcessGUI(this);
        else if (component instanceof Dialog)
            dialog = new MergeProcessGUI((Dialog)component, this);
        else
            dialog = new MergeProcessGUI((Frame)component, this);

        return dialog;
    }
    
}
