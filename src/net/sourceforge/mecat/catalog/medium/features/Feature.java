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
 * Created on Jul 16, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.medium.features;

import java.util.Iterator;
import java.util.Map;

import javax.swing.JPanel;

import net.sourceforge.mecat.catalog.gui.features.FeaturePanel;
import net.sourceforge.mecat.catalog.gui.features.desktop.FeatureDesktop;
import net.sourceforge.mecat.catalog.i18n.fromCatalog.LayeredResourceBundle;
import net.sourceforge.mecat.catalog.medium.Listing;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.listener.FeatureListener;
import net.sourceforge.mecat.catalog.medium.features.option.FeatureOption;

public interface Feature {

    public Medium getMedium();
    public LayeredResourceBundle getRes();

    public void addFeatureListener(FeatureListener featureListener);
    public void removeFeatureListener(FeatureListener featureListener);

	public FeaturePanel getPanel(FeatureDesktop desktop);
    public FeaturePanel getPanel(FeatureDesktop desktop, boolean border);
    
	public boolean validate(String condition) throws BadCondition;
	public String getText();
	public String getShortText();
//    public String getTextHTML();
    public String getTextHTML(int availableWidth);
    public String getShortTextHTML();


    /**
     * The StaticListingOption object should contain the non persistent 
     * information needed for the display work of every listing/catalog.
     * For persisten information the use of the option part of the 
     * catalog found trought medium.entry.catalog is suggested.
     * Since it is null at the beginning it should be initialised with
     * @link #setStaticListingOption(Object)
     * @return
     */
    public Object getStaticListingOption();
 
    /**
     * This function should be overriden if there are non persistent static
     * information required.
     * @param o
     * @return
     */
    public Object getFreshStaticListingOption(final Listing listing);

    public JPanel getFeatureOptionPanel(FeatureOption featureOption);
    
    public FeatureOption getFeatureOption();
    
    
	public void copyTo(Feature feature);
    public void copyToUseMapping(Feature feature, Map<Medium, Map<Listing, Medium>> mapping);
    
    /**
     * This function returns true if the feature contains data
     * @return
     */
    public boolean hasValue();


    public void showOptions();
    
    public Iterator<? extends Medium> getSubMedia();


}
