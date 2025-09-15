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
 * Created on Jan 21, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.option.preferences;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Locale;

import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.PersistentThroughEntry;

public class LanguagesOption extends LinkedHashSet<Locale> implements PersistentThroughEntry {
    
    public boolean loadFromEntry(Entry entry) {
        for (Iterator<String> i = entry.getSetIterator("Language"); i.hasNext(); ) {
            String str = i.next();
            try {
                add(new Locale(str));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public void saveToEntry(Entry entry) {
        for (Locale l : this) 
            entry.addSetAttribute("Language", l.toString());
    }

}
