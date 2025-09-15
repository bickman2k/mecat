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
 * Created on Aug 31, 2005
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filter;

import java.util.Locale;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JLabel;

import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.BadCondition;
import net.sourceforge.mecat.catalog.datamanagement.Util;
import net.sourceforge.mecat.catalog.option.Options;

public class EntryFilter extends AbstractFilter implements Filter, FilterListener {
    final Entry entry;
    Filter filter = TrueFilter.TRUE;
    
    public Filter getFilter() {
        return filter;
    }

    public EntryFilter copyFilter(final Entry entry) {
        Util.copyEntry(this.entry, entry);
        return new EntryFilter(entry);
    }
    
    public EntryFilter(final Entry entry) {
        this.entry = entry;
        String condition = entry.getAttribute("Condition");
        if (condition != null) {
            Parser parse = new Parser(condition);
            try {
                setFilter(parse.parse());
            } catch (BadCondition e) {
                e.printStackTrace();
            }
        }
    }

    protected void setFilter(final Filter filter) {
        if (this.filter != null)
            this.filter.removeFilterListener(this);
        if (filter != null)
            filter.addFilterListener(this);
        this.filter = filter;
        changed(null);
    }

    public EntryFilter(final Entry entry, final Filter filter) {
        this.entry = entry;
        setFilter(filter);
    }
    
    public String getCondition() {
        return filter.getCondition();
    }
    
    public void setCondition(String condition) {
        if (condition != null) {
            Parser parse = new Parser(condition);
            try {
                setFilter(parse.parse());
            } catch (BadCondition e) {
                e.printStackTrace();
            }
        }
        entry.setAttribute("Condition", filter.getCondition());
    }
    
    public String toString() {
        String name = entry.getAttribute("Name", Options.getCurrentLocale());
        if (name != null)
            return name;

        Set<Locale> langs = entry.getAttributeLanguages("Name");
        if (!langs.isEmpty())
            return entry.getAttribute("Name", langs.iterator().next());

        if (entry.getAttribute("Name") != null)
            return entry.getAttribute("Name");
        
        return getCondition();
    }
    
    public void setName(final String name) {
        entry.setAttribute("Name", name);
    }
    public void setName(final String name, final Locale locale) {
        entry.setAttribute("Name", name, locale);
    }

    public boolean eval(Medium medium) throws BadCondition {
        if (filter == null)
            return true;
        return filter.eval(medium);
    }

    public JComponent visualisation() {
        JLabel label = new JLabel(this.toString());
        return label;
    }

    public void changed(final FilterListenerEvent event) {
        if (filter != null)
            entry.setAttribute("Condition", filter.getCondition());
        else 
            entry.setAttribute("Condition", null);
    }

    public Filter NNF() {
        return filter.NNF();
    }
    
    public int compareTo(Filter filter) {
        if (!(filter instanceof EntryFilter))
            return getClass().getName().compareTo(filter.getClass().getName());

        EntryFilter entryFilter = ( EntryFilter ) filter;
        
        return this.filter.compareTo(entryFilter.filter);
    }
    
}
