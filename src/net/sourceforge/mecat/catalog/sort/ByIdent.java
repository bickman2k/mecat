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
 * Created on Jun 30, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.sort;

import javax.swing.JPanel;

import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.impl.Ident;
import net.sourceforge.mecat.catalog.option.Options;

public class ByIdent implements ConfigurableComparator {

    public JPanel getOptions() {
        // Has no options
        return null;
    }

    public ConfigurableComparator getCopy() {
        return new ByIdent();
    }

    public int compare(Medium arg0, Medium arg1) {
        return arg0.getFeature(Ident.class).getUUID().compareTo(arg1.getFeature(Ident.class).getUUID());
    }

    public boolean loadFromEntry(Entry entry) {
        return true;
    }

    public void saveToEntry(Entry entry) {

    }

    public String toString() {
        return Options.getI18N(Ident.class).getString("Ident");
    }

    @Override
    public boolean equals(Object obj) {
        // If the obj we compare against is a comparing
        // then let the comparing do the compare for equality
        if (obj instanceof Comparing)
            return obj.equals(this);

        if (!(obj instanceof ByIdent)) 
            return false;
        
        return true;
    }

}
