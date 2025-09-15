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
 * Created on Sep 24, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.export.shared.list;

import java.util.List;
import java.util.Vector;

import java.awt.Component;

import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.PersistentThroughEntry;
import net.sourceforge.mecat.catalog.datamanagement.Util;

public abstract class ExportCol<MEASUREMENT, EXPORTDEF extends ExportDef> extends Vector<EXPORTDEF> implements PersistentThroughEntry{
	public MEASUREMENT measurement;
    
	public ExportCol(MEASUREMENT measurement) {
		this.measurement = measurement;
	}
    
    public abstract void editColumn(Component parentComponent);

	public boolean loadFromEntry(Entry entry) {

		List<? extends Entry> entries = entry.getSubEntries("Item");
		for (Entry e : entries) 			
			add((EXPORTDEF)Util.loadFromEntry(e));
				
		return true;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.datamanagement.PersistentThroughEntry#saveToEntry(net.sourceforge.mecat.catalog.datamanagement.Entry)
	 */
	public void saveToEntry(Entry entry) {
		Util.addArgument(entry, new Util.Argument(0, null, measurement));
		
		for (PersistentThroughEntry pte : this) 			
			Util.saveToEntry(pte, entry.createSubEntry("Item"));
		
	}

}
