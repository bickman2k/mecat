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
 * Created on Oct 6, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.medium.hidden;

import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.filesystem.virtual.Mount;
import net.sourceforge.mecat.catalog.medium.Listing;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.impl.FileSystemEntryRef;
import net.sourceforge.mecat.catalog.medium.features.impl.MountPoint;


public class VirtualRomMount extends Medium {

	/**
	 * @param entry
	 * @param listing
	 */
	public VirtualRomMount(Entry entry, Listing listing) {
		super(entry, listing);
        addFeature(new FileSystemEntryRef(this));
        addFeature(new MountPoint(this));
	}
	
    @Override
    public String displayName() {
        String ret = "";
        
        if ((getFeature(FileSystemEntryRef.class)).getShortText() != null) 
            ret += (getFeature(FileSystemEntryRef.class)).getShortText() + ". ";

        if ((getFeature(MountPoint.class)).get() != null)
            ret += (getFeature(MountPoint.class)).getShortText() + " - ";
        
        return ret;
    }

    public boolean isComplete() {
        if (!getFeature(FileSystemEntryRef.class).hasValue())
            return false;

        if (!getFeature(MountPoint.class).hasValue())
            return false;

        return true;
    }
    
    public Mount getMount() {
        return new Mount(getFeature(FileSystemEntryRef.class).getFileSystemEntry(), getFeature(MountPoint.class).get());
    }
    
}
