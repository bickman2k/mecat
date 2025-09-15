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
package net.sourceforge.mecat.catalog.sort;

import javax.swing.JPanel;

import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.Util;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.TextFeature;
import net.sourceforge.mecat.catalog.option.Options;

public class ByTextFromTextFeature implements ConfigurableComparator {

	Class<? extends TextFeature> feature = null;

	public ByTextFromTextFeature(Class<? extends TextFeature> c) {
		if (c == null)
			return;
		
//		if (TextFeature.class.isAssignableFrom(c))
			feature = c;
	}


	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Medium medium0, Medium medium1) {

		// check if initialized with non null value
		if (feature == null)
			return 0;
			
		// get the features
		TextFeature feature0 = (TextFeature)medium0.getFeature(feature); 
		TextFeature feature1 = (TextFeature)medium1.getFeature(feature); 

		// If one of the two does not has the feature he is smaller
		if (feature0 == null)
			if (feature1 == null)
				return 0;
			else
				return 1;
		
		if (feature1 == null)
			return -1;

		// If for one the feature is not used he is smaller
		if (feature0.get() == null)
			if (feature1.get() == null)
				return 0;
			else
				return -1;
		
		if (feature1.get() == null)
			return 1;

		// Compare the String values			
		return feature0.get().compareTo(feature1.get()); 
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.sort.ConfigurableComparator#getOptions()
	 */
	public JPanel getOptions() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String toString() {
		return Options.getI18N(feature).getString(feature.getSimpleName());
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.datamanagement.PersistentThroughEntry#loadFromEntry(net.sourceforge.mecat.catalog.datamanagement.Entry)
	 */
	public boolean loadFromEntry(Entry entry) {
		return true;
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.datamanagement.PersistentThroughEntry#saveToEntry(net.sourceforge.mecat.catalog.datamanagement.Entry)
	 */
	public void saveToEntry(Entry entry) {
		Util.addArgument(entry,new Util.Argument(0,null,feature));
	}


    public ByTextFromTextFeature getCopy() {
        return new ByTextFromTextFeature(feature);
    }

    @Override
    public boolean equals(Object obj) {
        // If the obj we compare against is a comparing
        // then let the comparing do the compare for equality
        if (obj instanceof Comparing)
            return obj.equals(this);

        if (!(obj instanceof ByTextFromTextFeature)) 
            return false;
        
        ByTextFromTextFeature other = (ByTextFromTextFeature) obj;
        
        return feature.equals(other.feature);
    }
}
