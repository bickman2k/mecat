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
 * Created on Sep 1, 2005
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filter;

import javax.swing.JComponent;
import javax.swing.JLabel;

import net.sourceforge.mecat.catalog.filter.FilterListenerEvent.FilterListenerEventType;
import net.sourceforge.mecat.catalog.gui.ToolBarUtils;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.BadCondition;

public class AndFilter extends AbstractFilter implements BooleanFilter, FilterListener {

    Filter left, right;

    public Filter getLeft() {
        return left;
    }


    public void setLeft(final Filter left) {
        if (this.left != null)
            this.left.removeFilterListener(this);
        
        this.left = left;
        fireFilterChange(new FilterListenerEvent(this, FilterListenerEventType.STRUCTURE_CHANGED));
        
        if (left != null)
            left.addFilterListener(this);
   }


    public Filter getRight() {
        return right;
    }


    public void setRight(Filter right) {
        if (this.right != null)
            this.right.removeFilterListener(this);
        
        this.right = right;
        fireFilterChange(new FilterListenerEvent(this, FilterListenerEventType.STRUCTURE_CHANGED));

        if (right != null)
            right.addFilterListener(this);
    }


    public AndFilter(final Filter left, final Filter right){
        this.left = left;
        this.right = right;
    }
    
    
    public String getCondition() {
        return "( " + left.getCondition() + " && " + right.getCondition() + " )";
    }

    public boolean eval(final Medium medium) throws BadCondition {
        return left.eval(medium) && right.eval(medium);
    }
    
    public String toString() {
        return left + " and " + right;
    }

    public JComponent visualisation() {
        JLabel label = new JLabel();
        if (ToolBarUtils.exists(AndFilter.class, "And"))
            label.setIcon(ToolBarUtils.loadImage(AndFilter.class, "And", 1, "And"));
        else
            label.setText("And");
        return label;
    }
    
    public void changed(FilterListenerEvent event) {
        fireFilterChange(event);
    }


    public Filter NNF() {
        return new AndFilter(left.NNF(), right.NNF());
    }


    public int compareTo(Filter filter) {
        if (!(filter instanceof AndFilter))
            return getClass().getName().compareTo(filter.getClass().getName());

        AndFilter andFilter = ( AndFilter ) filter;
        
        int res = left.compareTo(andFilter.left);
        if (res != 0)
            return res;
        return right.compareTo(andFilter.right);
    }
    
    
}
