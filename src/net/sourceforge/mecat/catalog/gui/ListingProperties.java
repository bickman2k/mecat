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
 * Created on Oct 24, 2005
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.gui;

import java.util.Locale;

import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.PersistentThroughEntry;
import net.sourceforge.mecat.catalog.datamanagement.Util;
import net.sourceforge.mecat.catalog.filter.AndFilter;
import net.sourceforge.mecat.catalog.filter.EntryFilter;
import net.sourceforge.mecat.catalog.filter.Filter;
import net.sourceforge.mecat.catalog.filter.FilterListing;
import net.sourceforge.mecat.catalog.filter.FilterUtils;
import net.sourceforge.mecat.catalog.filter.TrueFilter;
import net.sourceforge.mecat.catalog.medium.features.Languages;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.sort.Comparing;
import net.sourceforge.mecat.catalog.sort.ConfigurableComparator;
import net.sourceforge.mecat.catalog.sort.SortedListing;

public class ListingProperties implements PersistentThroughEntry {

    // The featureSortOption contains the current selected order
    ConfigurableComparator sorting = null;
    // The filterDesigner contains the selected Filter
    // A filter null will become an always true.
    Filter filter = null;
    // If Locale is null this usally means to take the 
    // currentLocale from Options.
    Locale languageSelection = null;
    
    public ListingProperties(ShowListing showListing) {
        this(showListing, null);
    }
    public ListingProperties(ShowListing showListing, final Locale locale) {
        this(showListing.getSortedListing(), locale);
    }
    public ListingProperties(SortedListing sortedListing) {
        this(sortedListing, null);
    }
    public ListingProperties(SortedListing sortedListing, final Locale locale) {
        this(sortedListing.getFilterListing());
        sorting = sortedListing.getComparator().getCopy();
    }
    public ListingProperties(FilterListing filterListing) {
        this(filterListing, null);
    }
    public ListingProperties(FilterListing filterListing, final Locale locale) {
        this(locale);
        filter = FilterUtils.copyFilter(filterListing.getFilter());
    }
    public ListingProperties() {
        this((Locale)null);
    }
    public ListingProperties(final Locale locale) {
        languageSelection = locale;
    }

    public boolean activeFilter() {
        return filter != null && filter != TrueFilter.TRUE;
    }
    
    public boolean activeSorting() {
        if (sorting == null)
            return false;
        if (sorting instanceof Comparing)
            return ((Comparing)sorting).size() > 0;
        return true;
    }
    
    public boolean activeLanguage() {
        return languageSelection != null;
    }
    
    public Filter getFilter() {
        return filter;
    }
    
    public ConfigurableComparator getSorting() {
        return sorting;
    }
    
    public Locale getLanguage() {
        return languageSelection;
    }
    
    public void setFilter(final Filter filter) {
        this.filter = filter;
    }
    
    public void setSorting(final ConfigurableComparator cc) {
        sorting = cc;
    }
    
    public void setLanguage(final Locale locale) {
        languageSelection = locale;
    }
    
    public String getHTML() {
        StringBuffer buffer = new StringBuffer();
        
        buffer.append("<HTML><BODY>");
        if (activeFilter()) {
            buffer.append("<H3>");
            buffer.append(Options.getI18N(Filter.class).getString("Filter"));
            buffer.append("</H3>");
            buffer.append(filter);
            buffer.append("<br>");
        } else
            buffer.append(Options.getI18N(ListingProperties.class).getString("No filter") + "<br>");
        if (activeSorting()) {
            buffer.append("<H3>");
            buffer.append(Options.getI18N(MainFrameBackend.class).getString("Sorting"));
            buffer.append("</H3>");
            if (sorting instanceof Comparing)
                for (ConfigurableComparator cc : (Comparing)sorting) {
                    buffer.append(cc.toString());
                    buffer.append("<br>");
                }
            else
                buffer.append(sorting.toString());
        } else
            buffer.append(Options.getI18N(ListingProperties.class).getString("No sorting") + "<br>");
        if (activeLanguage()) {
            buffer.append("<H3>");
            buffer.append(Options.getI18N(Languages.class).getString("Language"));
            buffer.append("</H3>");
            buffer.append(languageSelection.getDisplayName(Options.getCurrentLocale()));
            buffer.append("<br>");
        } else
            buffer.append(Options.getI18N(ListingProperties.class).getString("Current language") + "<br>");
        
        buffer.append("</BODY></HTML>");
        
        return buffer.toString();
    }
    public boolean loadFromEntry(Entry entry) {
        if (entry.getSubEntry("Filter") != null) {
            EntryFilter entryFilter = new EntryFilter(entry.getSubEntry("Filter"));
            filter = entryFilter;
        }
        if (entry.getSubEntry("Sorting") != null){
            Entry comparingEntry = entry.getSubEntry("Sorting");
            PersistentThroughEntry pte = Util.loadFromEntry(comparingEntry);
            if (pte instanceof ConfigurableComparator)
                sorting = (ConfigurableComparator) pte;
        }
        if (entry.getAttribute("Language") != null) {
            languageSelection = new Locale(entry.getAttribute("Language"));
        }
        return true;
    }
    public void saveToEntry(Entry entry) {
        if (activeFilter()) 
            FilterUtils.copyFilter(filter, entry.createSubEntry("Filter"));
        if (activeSorting())
            Util.saveToEntry(sorting, entry.createSubEntry("Sorting"));
        if (activeLanguage())
            entry.setAttribute("Language", languageSelection.getLanguage());
    }
    
    public ListingProperties getCopy() {
        ListingProperties ret = new ListingProperties();
        
        if (sorting == null)
            ret.sorting = null;
        else
            ret.sorting = sorting.getCopy();
 
        if (filter == null)
            ret.filter = filter;
        else
            ret.filter = FilterUtils.copyFilter(filter);
        
        ret.languageSelection = languageSelection;
        
        return ret;
    }
    
    public ShowListing addFilterAndUseSorting(ShowListing listing) {
        // Construct listing from listDefinitition.listProperties
        FilterListing filterListe = new FilterListing(listing.getSource());
        Filter orgFilter = listing.getSortedListing().getFilterListing().getFilter();
        if (orgFilter == null)
            orgFilter = TrueFilter.TRUE;
        Filter filter;
        if (activeFilter())
            filter = new AndFilter(orgFilter, getFilter());
        else
            filter = orgFilter;
        filterListe.setFilter(filter);
        
        SortedListing sortedListe = new SortedListing(filterListe);
        if (activeSorting())
            sortedListe.setComparator(getSorting());
        else
            sortedListe.setComparator(listing.getSortedListing().getComparator());
        
        return new ShowListing(sortedListe);
    }
}
