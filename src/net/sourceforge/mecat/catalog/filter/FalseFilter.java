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
 * The FalseFilter allways returns false and accepting no media.
 * Therefore one could call it the none filter.
 * 
 */
package net.sourceforge.mecat.catalog.filter;

import javax.swing.JComponent;
import javax.swing.JLabel;

import net.sourceforge.mecat.catalog.gui.ToolBarUtils;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.BadCondition;


public class FalseFilter extends AbstractFilter implements Filter {

    /**
     * The filter does not change. Therefore one can take allways
     * the same Filter.
     */
    final public static FalseFilter FALSE = new FalseFilter();

    public String getCondition() {
        return "( false )";
    }

    public boolean eval(Medium medium) throws BadCondition {
        return false;
    }
    
    public String toString() {
        return "none";
    }

    public JComponent visualisation() {
        JLabel label = new JLabel();
        if (ToolBarUtils.exists(FalseFilter.class, "False"))
            label.setIcon(ToolBarUtils.loadImage(FalseFilter.class, "False", 1, "False"));
        else
            label.setText("False");
        return label;
    }

    public Filter NNF() {
        return FALSE;
    }

    public int compareTo(Filter filter) {
        if (!(filter instanceof FalseFilter))
            return getClass().getName().compareTo(filter.getClass().getName());

        return 0;
    }
}
