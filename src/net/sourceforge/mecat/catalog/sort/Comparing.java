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

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;

import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.PersistentThroughEntry;
import net.sourceforge.mecat.catalog.datamanagement.Util;
import net.sourceforge.mecat.catalog.medium.Medium;

public class Comparing extends Vector<ConfigurableComparator> implements ConfigurableComparator{

    long all = 0;

    public long getTime() {
        return all;
    }
    
    public Comparing() {
    }
    
    public Comparing(final Collection<ConfigurableComparator> comparator){
        super(comparator);
    }
    
	public int compare(Medium arg0, Medium arg1) {
        long enter = System.currentTimeMillis();
		for (ConfigurableComparator c : this)
			if (c.compare(arg0,arg1) != 0) {
                all += System.currentTimeMillis() - enter;
//                System.out.println("Time in milliseconds " + all);
				return c.compare(arg0,arg1);
            }
				
        all += System.currentTimeMillis() - enter;
//        System.out.println("Time in milliseconds " + all);
		return 0;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.sort.ConfigurableComparator#getOptions()
	 */
	public JPanel getOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean loadFromEntry(Entry entry) {
		List<? extends Entry> entries = entry.getSubEntries("Comparator");
		
		for (Entry e : entries) {
			PersistentThroughEntry pte = Util.loadFromEntry(e);
			if (pte != null)
				if (pte instanceof ConfigurableComparator)
					add((ConfigurableComparator)pte);
		}
		
		return true;
	}

	public void saveToEntry(Entry entry) {
		for (ConfigurableComparator cc : this){
			Entry e = entry.createSubEntry("Comparator");
			Util.saveToEntry(cc, e);
		}
			
	}

    public Comparing getCopy() {
        Comparing ret = new Comparing();
        for (ConfigurableComparator cc : this)
            ret.add(cc.getCopy());
        return ret;
    }

    @Override
    public synchronized boolean equals(Object o) {
        if (!(o instanceof ConfigurableComparator))
            return false;
        
        if (!(o instanceof Comparing)) {
            if (size() != 1)
                return false;
            return firstElement().equals(o);
        }
        
        return super.equals(o);
    }

    
    
}
