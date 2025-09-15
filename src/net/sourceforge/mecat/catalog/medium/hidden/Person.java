/*
 *  MeCat - Media Catalog more information on
 *  http://mecat.sourceforge.net/
 *  Copyright (C) 2005, Stephan Richard Palm, All Rights Reserved.
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
 * Created on Jun 17, 2005
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.medium.hidden;

import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.medium.Listing;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.TextFeature;
import net.sourceforge.mecat.catalog.medium.features.impl.Ident;
import net.sourceforge.mecat.catalog.medium.features.person.IMDBPersonNumber;
import net.sourceforge.mecat.catalog.medium.features.person.Name;

public class Person extends Medium {
	
	public Person(Entry entry, Listing listing) {
		super(entry, listing);
        addFeature(new Name(this));	
        addFeature(new IMDBPersonNumber(this));
	}

    @Override
    public String displayName() {
        String ret = "";

        if (((TextFeature)getFeature(Name.class)).get() != null)
            ret += ((TextFeature)getFeature(Name.class)).get();

        return ret;
    }
	
    
    
    @Override
    public int hashCode() {
        Integer id1 = getFeature(IMDBPersonNumber.class).getInt();
        
        if (id1 == null)
            return super.hashCode();
        
        return id1;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Person))
            return false;
        
        Person person = (Person) obj;

        if (getFeature(Ident.class).getUUID().equals(person.getFeature(Ident.class).getUUID()))
            return true;
        
        Integer id1 = getFeature(IMDBPersonNumber.class).getInt();
        Integer id2 = person.getFeature(IMDBPersonNumber.class).getInt();
        
        if (id1 == null)
            return super.equals(obj);
        if (id2 == null)
            return super.equals(obj);
        
        // We need to use equals for the type Integer
        return (id1.equals(id2));
    }
}
