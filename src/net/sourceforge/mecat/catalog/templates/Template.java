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
 * Created on Jul 7, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.templates;

import java.awt.Component;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import net.sourceforge.mecat.catalog.datamanagement.Catalog;
import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.Util;
import net.sourceforge.mecat.catalog.i18n.fromCatalog.CatalogResource;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.preferences.AbstractMediaOption;
import net.sourceforge.mecat.catalog.option.preferences.CatalogPreferences;

public class Template {

    final Catalog catalog;
    final CatalogResource templateResource;
    final Vector<Class<? extends Medium>> media = new Vector<Class<? extends Medium>>();
    
    public Template(Catalog catalog) {
        this.catalog = catalog;
        templateResource = new CatalogResource(catalog, "Template_Resource");


        // Get media information from catalog
        Entry mediaEntry = catalog.getOption("Template_Media");
        Iterator<String> i = mediaEntry.getSetIterator("Medium");
        while (i.hasNext()) {
            String str = i.next();
            try {
                Class<? extends Medium> medium = (Class<? extends Medium>) Class.forName(Entry.getRealClassName(str));
                media.add(medium);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        // Remove the template storage for the media
        catalog.removeOption(mediaEntry);

        // For all medium not in the template, they are not wanted
        CatalogPreferences catalogPreferences = Util.getCatalogPreferences(catalog);
        catalogPreferences.getMediaOption().setMediaOptionOverriden(true);
        for (Class<? extends Medium> medium : AbstractMediaOption.getMedia()) 
            catalogPreferences.getMediaOption().setWanted(medium, false);

        // Add all wanted
        for (Class<? extends Medium> medium : media)
            catalogPreferences.getMediaOption().setWanted(medium, true);

        // Store the information with option style
        catalog.removeOption("Preferences");
        Util.saveToEntry(catalogPreferences, catalog.createOption("Preferences"));
        
    }

    public String toString() {
        return templateResource.getString("Title");
    }
    
    public String getDescription() {
        return templateResource.getString("Description");
    }
    
    public String getTitle() {
        return templateResource.getString("Title");
    }
    
    public String getTitle(Locale l) {
        return templateResource.getBundle(l).getString("Title");
    }
    
    public String getDescriptionHTML() {
        return templateResource.getString("DescriptionHTML");
    }
    
    public String getTitleHTML() {
        return templateResource.getString("TitleHTML");
    }
    
    public Catalog getCatalog(Component component) {
        Catalog ret = Options.stdFactory.createCatalog(component);
        Util.copyCatalog(catalog, ret, false);
        // Remove template information from catalog copy
        new CatalogResource(ret, "Template_Resource").clear();
        return ret;
    }

}
