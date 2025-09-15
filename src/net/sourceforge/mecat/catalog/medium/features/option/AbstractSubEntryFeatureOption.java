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
 * Created on Sep 13, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.medium.features.option;

import java.net.MalformedURLException;
import java.net.URL;

import net.sourceforge.mecat.catalog.datamanagement.Catalog;
import net.sourceforge.mecat.catalog.datamanagement.Connection;
import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.PersistentThroughEntry;
import net.sourceforge.mecat.catalog.datamanagement.Util;
import net.sourceforge.mecat.catalog.datamanagement.Util.Argument;
import net.sourceforge.mecat.catalog.datamanagement.changelog.ChangeLog;
import net.sourceforge.mecat.catalog.datamanagement.xml.XMLConnection;
import net.sourceforge.mecat.catalog.medium.Listing;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.sort.ConfigurableComparator;

public class AbstractSubEntryFeatureOption implements FeatureOption {

    /**
     * If this flag is set to false. 
     * The sub medium used for the feature will be
     * created as directory child entries.
     * 
     * If this flag is set to true.
     * The sub medium will be stored elsewhere and
     * a child entry containing all information to find
     * the linked sub entry will be stored.
     */
    boolean preferLink = false;
    
    /**
     * If this flag is set to false.
     * Then the sub medium will be stored in the
     * same catalog as the medium from which the feature comes.
     * 
     * If this flag is set to true.
     * Then the sub medium will be stored in the 
     * catalog given with externCatalog.
     */
    boolean preferExternCatalog = false;
    
    /**
     * Storing position for externaly linked sub media.
     */
    Connection externCatalog = null;
    
    /**
     * A cache variable. This variable contains the listing
     * that should be used for accessing the externaly linked media.
     */
    Listing listingCache = null;

    /**
     * This comparator is used when showing a list of existing
     * media to choose from. The listing is shown if the user
     * wants to use an existing medium.
     */
    ConfigurableComparator showSorting = null;
    
    public AbstractSubEntryFeatureOption() {
    }
    public AbstractSubEntryFeatureOption(Boolean preferLink) {
        this.preferLink = preferLink;
    }
    public AbstractSubEntryFeatureOption(Boolean preferLink, Boolean preferExternCatalog, String externCatalog) {
        this.preferLink = preferLink;
        this.preferExternCatalog = preferExternCatalog;
        try {
            this.externCatalog = new XMLConnection(new URL("file", null, Options.USER_OPTION_DIR + externCatalog + ".catalog.xml"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
    public AbstractSubEntryFeatureOption(Boolean preferLink, Boolean preferExternCatalog, Connection externCatalog) {
        this.preferLink = preferLink;
        this.preferExternCatalog = preferExternCatalog;
        this.externCatalog = externCatalog;
    }

    public Listing getListing(ChangeLog changeLog) {
        if (listingCache != null)
            return listingCache;
        
        if (externCatalog == null)
            return null;

        Catalog catalog = null;
        
        if (changeLog != null)
            catalog = changeLog.getCatalog(externCatalog);
        if (catalog == null)
            catalog = externCatalog.getCatalogFactory().openCatalog(externCatalog);

        if (catalog == null) {
            // At this point there should only be used catalogs that can be generated 
            // without user interaction.
            catalog = externCatalog.getCatalogFactory().createCatalog(externCatalog);
            if (catalog == null)
                return null;
        }
        listingCache = new Listing(catalog);

        if (changeLog != null)
            listingCache.setChangeLog(changeLog);

        return listingCache;
    }
    
    public void setExternCatalog(Connection externCatalog) {
        this.externCatalog = externCatalog;
        this.listingCache = null;
    }
    public void setPreferExternCatalog(boolean preferExternCatalog) {
        this.preferExternCatalog = preferExternCatalog;
    }
    public void setPreferLink(boolean preferLink) {
        this.preferLink = preferLink;
    }
    public Connection getExternCatalog() {
        return externCatalog;
    }
    public boolean isPreferExternCatalog() {
        return preferExternCatalog;
    }
    public boolean isPreferLink() {
        return preferLink;
    }
    public ConfigurableComparator getShowSorting() {
        return showSorting;
    }
    
    public boolean loadFromEntry(Entry entry) {
        Entry showExisting = entry.getSubEntry("ShowSorting");
        if (showExisting != null) {
            PersistentThroughEntry pte = Util.loadFromEntry(showExisting);
            if (pte instanceof ConfigurableComparator) 
                showSorting = (ConfigurableComparator) pte;
        }
        
        return true;
    }

    public void saveToEntry(Entry entry) {
        if (!preferLink)
            return;
        
        Util.addArgument(entry, new Argument(0, Boolean.class, preferLink));
        
        if (!preferExternCatalog && externCatalog == null) 
            return;

        Util.addArgument(entry, new Argument(1, Boolean.class, preferExternCatalog));
        Util.addArgument(entry, new Argument(2, Connection.class, externCatalog));
        
        if (showSorting != null)
            Util.saveToEntry(showSorting, entry.createSubEntry("ShowSorting"));
    }

    
}
