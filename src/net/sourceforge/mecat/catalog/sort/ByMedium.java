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
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.option.Options;

public class ByMedium implements ConfigurableComparator {

/*	Vector<Class> media;
	public ByMedium() {
		media = new Vector<Class>(Options.media.length);
		for (Class c : Options.media)
			media.add(c);
	}*/
	
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Medium arg0, Medium arg1) {

		Class<? extends Medium> c0 = arg0.getClass(); Class<? extends Medium> c1 = arg1.getClass();
		
		for (Class<? extends Medium> c : Options.media)
			if ((c == c0) && (c == c1))
				return 0;
			else if (c == c0)
				return -1;
			else if (c == c1)
				return 1;			
			
		return 0;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.sort.ConfigurableComparator#getOptions()
	 */
	public JPanel getOptions() {
		return null;
	}
	
	public String toString() {
		return Options.getI18N(Medium.class).getString("Medium");

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
	}

    public ByMedium getCopy() {
        return new ByMedium();
    }

    @Override
    public boolean equals(Object obj) {
        // If the obj we compare against is a comparing
        // then let the comparing do the compare for equality
        if (obj instanceof Comparing)
            return obj.equals(this);

        if (!(obj instanceof ByMedium)) 
            return false;
        
        return true;
    }

}
