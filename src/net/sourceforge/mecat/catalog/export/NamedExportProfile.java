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
 * Created on Aug 16, 2005
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.export;

import java.util.Locale;

import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.PersistentThroughEntry;
import net.sourceforge.mecat.catalog.datamanagement.Util;
import net.sourceforge.mecat.catalog.datamanagement.Util.Argument;
import net.sourceforge.mecat.catalog.option.Options;

public class NamedExportProfile implements PersistentThroughEntry {

    final public ExportProfile exportProfile;
    final String name;
    private Entry entry;
    
    public Entry getEntry() {
        return entry;
    }
    
    public NamedExportProfile(final ExportProfile exportProfile, final String name) {
        this(exportProfile, name, null);
    }
    public NamedExportProfile(final ExportProfile exportProfile, final String name, final Entry entry) {
        this.exportProfile = exportProfile;
        this.name = name;
        this.entry = entry;
    }
    
    public String toString() {
        if (entry == null)
            return name;
        
        String localName = entry.getAttribute("Name", Options.getCurrentLocale());
        if (localName != null)
            return localName;
        
        String generalName = entry.getAttribute("Name");
        if (generalName != null)
            return generalName;
        
        return name;
    }
    public void rename(final String str) {
        if (entry == null) 
            return;
        entry.setAttribute("Name", str, Options.getCurrentLocale());
    }
    
    public boolean loadFromEntry(Entry entry) {
        this.entry = entry;
        return true;
    }
    public void saveToEntry(Entry entry) {
        Util.addArgument(entry, new Argument(0, ExportProfile.class, exportProfile));
        Util.addArgument(entry, new Argument(1, null, name));

        if (this.entry == null)
            return;
        
        // Copy the Name information for the languages where it exists
        for (Locale l : this.entry.getAttributeLanguages("Name"))
            entry.setAttribute("Name", this.entry.getAttribute("Name", l), l);
        
//        if (this.entry != null)
//            Util.copyEntry(this.entry, entry);
//        Util.saveToEntry(exportProfile, entry.createSubEntry("ExportProfile"));
//        entry.setAttribute("Name", name);
    }
}
