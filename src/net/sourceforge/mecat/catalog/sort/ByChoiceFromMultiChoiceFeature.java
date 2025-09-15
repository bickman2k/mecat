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
import net.sourceforge.mecat.catalog.medium.features.MultiChoiceFeature;
import net.sourceforge.mecat.catalog.option.Options;

public class ByChoiceFromMultiChoiceFeature implements ConfigurableComparator {

	Class<? extends MultiChoiceFeature> feature;

	public ByChoiceFromMultiChoiceFeature(Class<? extends MultiChoiceFeature> c) {
		
		if (MultiChoiceFeature.class.isAssignableFrom(c))
			feature = c;
		else
			feature = null;
	}


	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Medium medium0, Medium medium1) {

		if (feature == null)
			return 0;
			
		MultiChoiceFeature feature0 = (MultiChoiceFeature)medium0.getFeature(feature); 
		MultiChoiceFeature feature1 = (MultiChoiceFeature)medium1.getFeature(feature); 

		if (feature0 == null)
			if (feature1 == null)
				return 0;
			else
				return 1;
		
		if (feature1 == null)
			return -1;
			
		for(String s: feature0.getChoices())
			if (feature0.exists(s) && !feature1.exists(s))
				return -1;
			else if (!feature0.exists(s) && feature1.exists(s))
				return 1;
		
		return 0;
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


    public ByChoiceFromMultiChoiceFeature getCopy() {
        return new ByChoiceFromMultiChoiceFeature(feature);
    }

    @Override
    public boolean equals(Object obj) {
        // If the obj we compare against is a comparing
        // then let the comparing do the compare for equality
        if (obj instanceof Comparing)
            return obj.equals(this);

        if (!(obj instanceof ByChoiceFromMultiChoiceFeature)) 
            return false;
        
        ByChoiceFromMultiChoiceFeature other = (ByChoiceFromMultiChoiceFeature) obj;
        
        return feature.equals(other.feature);
    }
}
