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
 * Created on Oct 30, 2006
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.medium.features.impl;

import java.util.List;
import java.util.Set;
import java.util.Vector;

import net.sourceforge.mecat.catalog.medium.Listing;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.PointedMedium;
import net.sourceforge.mecat.catalog.medium.features.SubEntryListFeature;
import net.sourceforge.mecat.catalog.medium.features.option.SubEntryFeatureOption;
import net.sourceforge.mecat.catalog.medium.features.person.IMDBPersonNumber;
import net.sourceforge.mecat.catalog.medium.features.person.Name;
import net.sourceforge.mecat.catalog.medium.hidden.Person;
import net.sourceforge.mecat.catalog.medium.hidden.Role;

public class Roles extends SubEntryListFeature<Role> {

	/**
	 * @param medium
	 * @param attributeName
	 * @param entryType
	 */
	public Roles(Medium medium) {
		super(medium, "Roles", Role.class);
	}

    public void addRole(int actorId, String actorName, String roleName) {
        List<PointedMedium<Role>> media = getMedia();
        
        if (actorId <= 0 && actorName == null)
            return;

        // Search for a role that already has those information
        // if we find one, we wont add a new one.
        // Here  we check the IMDB id and the roleName
        if (actorId > 0)
            for (PointedMedium<Role> role : media) {
                if (!Role.roleNameEquals(role.getMedium().getFeature(Name.class).get(), actorName))
                    continue;
                Person actor = role.getMedium().getFeature(Actor.class).getSubEntryMedium();
                if (actor == null)
                    continue;
                
                Integer roleActorId = actor.getFeature(IMDBPersonNumber.class).getInt();
                if (roleActorId == null)
                    continue;
                
                if (actorId == ((int) roleActorId))
                    return;
            }
        
        Role role = newMedium().getMedium();
        // Set role Name
        role.getFeature(Name.class).set(roleName);

        // Look for an already existing person with
        // the same id
        Actor actor = role.getFeature(Actor.class);
        SubEntryFeatureOption subEntryFeatureOption = actor.getSubEntryFeatureOption();
        if (subEntryFeatureOption.isPreferLink()) {
            Listing listing = null;
            if (subEntryFeatureOption.isPreferExternCatalog())
                listing = subEntryFeatureOption.getListing(actor.getChangeLog());
            else
                listing = medium.getListing();

            if (listing != null) {
                List<Person> set = new Vector<Person>((Set)listing.getMediaByType(Person.class));
                for (Person person : set) {
                    Integer personId = person.getFeature(IMDBPersonNumber.class).getInt();
                    if (personId == null)
                        continue;
                    
                    if (actorId == ((int) personId)) {
                        actor.linkExisting(person);
                        return;
                    }
                }
            }
        }

        // At this point we need to create a new
        Person person = actor.getSubEntryMedium();
        if (person == null)
            person = actor.newMediumCatalog();
        if (person == null)
            return;
        if (actorId > 0)
            person.getFeature(IMDBPersonNumber.class).set(actorId);
        if (actorName != null)
            person.getFeature(Name.class).set(actorName);
    }

}
