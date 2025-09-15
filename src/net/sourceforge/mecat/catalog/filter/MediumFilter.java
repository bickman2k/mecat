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

import javax.swing.JComponent;
import javax.swing.JLabel;

import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.gui.ToolBarUtils;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.BadCondition;
import net.sourceforge.mecat.catalog.option.Options;

public class MediumFilter extends AbstractFilter implements Filter {

    final Class<? extends Medium> medium;
    
    final static public String IDENTIFIER = "Medium";
    
    public MediumFilter(final String name) throws BadCondition {
        try {
            Class c = Class.forName(Entry.getRealClassName(name));
            if (Medium.class.isAssignableFrom(c))
                medium = (Class< ? extends Medium>)c;
            else
                throw new BadCondition(Options.getI18N(Medium.class).getString("Could not find Medium").replaceAll("\\[MEDIUM\\]", name));
        } catch (java.lang.ClassNotFoundException e) {
            throw new BadCondition(Options.getI18N(Medium.class).getString("Could not find Medium").replaceAll("\\[MEDIUM\\]", name));
        }
    }

    public MediumFilter(final Class<? extends Medium> medium) {
        this.medium = medium;
    }
    
    public String getCondition() {
        return "( " + IDENTIFIER + "(" + Entry.getEasyClassName(medium.getName()) + ") )" ;
    }
    
    public String toString() {
        return Options.getI18N(Medium.class).getString("Medium") + " = " + Options.getI18N(medium).getString(medium.getSimpleName());
    }

    public boolean eval(final Medium medium) throws BadCondition {
        // !!!caution!!! this.medium != medium
        return medium.getClass().equals(this.medium);
    }

    public JComponent visualisation() {
        String easyName = medium.getSimpleName();
        JLabel label = new JLabel();
        label.setOpaque(false);
        if (ToolBarUtils.exists(medium, easyName))
            label.setIcon(ToolBarUtils.loadImage(medium, easyName, 1, easyName));
        else
            label.setText(easyName);
        return label;
    }
    public int compareTo(Filter filter) {
        if (!(filter instanceof MediumFilter))
            return getClass().getName().compareTo(filter.getClass().getName());

        MediumFilter mediumFilter = ( MediumFilter ) filter;
        
        return this.medium.getName().compareTo(mediumFilter.medium.getName());
    }
}
