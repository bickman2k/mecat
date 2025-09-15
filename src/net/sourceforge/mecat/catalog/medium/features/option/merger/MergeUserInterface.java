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
 * Created on Sep 14, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.medium.features.option.merger;

import javax.swing.JComponent;

import net.sourceforge.mecat.catalog.datamanagement.Catalog;
import net.sourceforge.mecat.catalog.medium.Listing;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.medium.features.option.FeatureOption;

public interface MergeUserInterface<T extends FeatureOption> {

    public void addMergeUserInterfaceListener(MergeUserInterfaceListener mergeUserInterfaceListener);
    public void removeMergeUserInterfaceListener(MergeUserInterfaceListener mergeUserInterfaceListener);

    /**
     * Gets the type of the Merge, 
     * this gives an idea how much user interaction
     * is needed / wanted
     * 
     * @return
     */
    public MergeType getType();
    
    /**
     * This indicates if the merge can be continued with
     * the allready known information
     * 
     * @return
     */
    public MergeUserInterfaceStatus getStatus();

    /**
     * This function gives the new options resulting from the merge
     * 
     * @return
     */
    public T getMergeResult();
    
    /**
     * The merge user interface should apply all changes that are necessary
     * before merging the two catalogs to the first catalog (was first there)
     * 
     * @param listing The catalog that will be changed if necessary
     * @param feature The feature for which the change is made
     */
    public void applyChangeToCatalog1(Listing listing, Class<? extends Feature> feature);

    /**
     * The merge user interface should apply all changes that are necessary
     * before merging the two catalogs to the second catalog (the import)
     * 
     * @param listing The catalog that will be changed if necessary
     * @param feature The feature for which the change is made
     */
    public void applyChangeToCatalog2(Listing listing, Class<? extends Feature> feature);
    

    /**
     * Applying this function should restore all information
     * to the information given at the beging of the user interaction.
     * 
     * One could say this is a reset to the original configuration.
     *
     */
    public void restoreDefault();
    
    /**
     * Gives a graphical user interface, where the user can modify the behavior
     * of the option merge. This can only result in a change of the imported information
     * or in the information in the open catalog in order to make them more compatible.
     * 
     * @return graphical user interface
     */
    public JComponent getGUI();
}
