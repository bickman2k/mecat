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
 * Created on Sep 2, 2005
 * @author Stephan Richard Palm
 * 
 * The TrueFilter allways returns true and accepting all media.
 * Therefore one could call it the all filter.
 * 
 */
package net.sourceforge.mecat.catalog.filter;

import javax.swing.JComponent;
import javax.swing.JLabel;

import net.sourceforge.mecat.catalog.gui.ToolBarUtils;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.BadCondition;


public class TrueFilter extends AbstractFilter implements Filter {

    /**
     * The filter does not change. Therefore one can take allways
     * the same Filter.
     */
    final public static TrueFilter TRUE = new TrueFilter();
    
    public String getCondition() {
        return "( true )";
    }

    public boolean eval(Medium medium) throws BadCondition {
        return true;
    }
    
    public String toString() {
        return "any";
    }

    public JComponent visualisation() {
        JLabel label = new JLabel();
        if (ToolBarUtils.exists(TrueFilter.class, "True"))
            label.setIcon(ToolBarUtils.loadImage(TrueFilter.class, "True", 1, "True"));
        else
            label.setText("True");
        return label;
    }

    public Filter NNF() {
        return TRUE;
    }

    public int compareTo(Filter filter) {
        if (!(filter instanceof TrueFilter))
            return getClass().getName().compareTo(filter.getClass().getName());

        return 0;
    }
}
