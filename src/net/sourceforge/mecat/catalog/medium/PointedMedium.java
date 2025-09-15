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
 * Created on Nov 13, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.medium;

import net.sourceforge.mecat.catalog.datamanagement.Entry;

public class PointedMedium<T extends Medium>{
    T medium;
    Entry entry;

    public PointedMedium(T medium, Entry entry) {
        this.medium = medium;
        this.entry = entry;
    }
    public Entry getEntry() {
        return entry;
    }
    public void setEntry(Entry entry) {
        this.entry = entry;
    }
    public T getMedium() {
        return medium;
    }
    public void setMedium(T medium) {
        this.medium = medium;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PointedMedium))
            return false;
        return medium.equals(((PointedMedium)obj).getMedium());
    }
    @Override
    public int hashCode() {
        return medium.hashCode();
    }
    @Override
    public String toString() {
        return medium.toString();
    }
    
    
    
}
