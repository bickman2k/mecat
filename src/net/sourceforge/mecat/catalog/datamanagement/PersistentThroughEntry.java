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
 * Created on Aug 13, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.datamanagement;

public interface PersistentThroughEntry {

	/**
	 * Do not invoke, it is for internal use only.
     * Should only be invoked by the Util class.
     * 
	 * @param entry The entry that should be loaded
	 * @return true if the load was a success
	 */
	public boolean loadFromEntry(Entry entry);
	/**
	 * Do not invoke, it is for internal use only.
     * Should only be invoked by the Util class.
     * 
	 * @param entry The entry where to save
	 */
	public void saveToEntry(Entry entry);

}
