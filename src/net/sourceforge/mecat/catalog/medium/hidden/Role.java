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
import net.sourceforge.mecat.catalog.medium.features.impl.Actor;
import net.sourceforge.mecat.catalog.medium.features.impl.Ident;
import net.sourceforge.mecat.catalog.medium.features.person.Name;

public class Role extends Medium {
	
	public Role(Entry entry, Listing listing) {
		super(entry, listing);
        addFeature(new Actor(this));	
        addFeature(new Name(this)); 
	}

    @Override
    public String displayName() {
        String ret = "";
        
        Person actor = getFeature(Actor.class).getSubEntryMedium();
        if (actor != null) {
            if (((TextFeature)actor.getFeature(Name.class)).get() != null)
                ret += ((TextFeature)actor.getFeature(Name.class)).get() + " -> ";
        }

        if (((TextFeature)getFeature(Name.class)).get() != null)
            ret += ((TextFeature)getFeature(Name.class)).get();

        return ret;
    }

    public static boolean roleNameEquals(String name1, String name2) {
        if (name1 == null)
            name1 = "";
        if (name2 == null)
            name2 = "";
        if (!name1.equalsIgnoreCase(name2))
            return false;
        return true;
   }

    public static boolean actorEquals(Person actor1, Person actor2) {
        if (actor1 == null)
            if (actor2 == null)
                return true;
            else
                return false;
        
        if (actor2 == null)
            return false;
        
        return actor1.equals(actor2);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Role))
            return false;
        
        Role role = (Role) obj;

        if (getFeature(Ident.class).getUUID().equals(role.getFeature(Ident.class).getUUID()))
            return true;
        
        String name1 = getFeature(Name.class).get();
        String name2 = role.getFeature(Name.class).get();
        
        if (!roleNameEquals(name1, name2))
            return false;

        Person actor1 = getFeature(Actor.class).getSubEntryMedium();
        Person actor2 = role.getFeature(Actor.class).getSubEntryMedium();

        return actorEquals(actor1, actor2);
    }
	
    
    
}
