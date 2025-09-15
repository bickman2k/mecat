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

public class NotFilter extends AbstractFilter implements BooleanFilter, FilterListener {

    Filter filter;
    
    public Filter getFilter() {
        return filter;
    }


    public void setFilter(final Filter filter) {
        if (this.filter != null)
            this.filter.removeFilterListener(this);
        
        this.filter = filter;
        fireFilterChange(new FilterListenerEvent(this, FilterListenerEventType.STRUCTURE_CHANGED));
        
        if (filter != null)
            filter.addFilterListener(this);
    }


    public NotFilter(final Filter filter){
        this.filter = filter;
    }
    
    
    public String getCondition() {
        return "( !" + filter.getCondition() + " )";
    }

    public boolean eval(final Medium medium) throws BadCondition {
        return !(filter.eval(medium));
    }
    
    public String toString() {
        return "not" + " " + filter;
    }

    public JComponent visualisation() {
        JLabel label = new JLabel();
        if (ToolBarUtils.exists(NotFilter.class, "Not"))
            label.setIcon(ToolBarUtils.loadImage(NotFilter.class, "Not", 1, "Not"));
        else
            label.setText("Not");
        return label;
    }


    public void changed(FilterListenerEvent event) {
        fireFilterChange(event);
    }

    public Filter NNF() {
        Filter filter = this.filter;
        while (filter instanceof EntryFilter)
            filter = ((EntryFilter)filter).filter;
        
        if (filter instanceof NotFilter) {
            NotFilter notFilter = ( NotFilter ) filter;
            return notFilter.filter.NNF();
        }
        if (filter instanceof AndFilter) {
            AndFilter andFilter = ( AndFilter ) filter;
            // No copy of the left and the right is made
            // we expect the later NNF calls to do so
            NotFilter negLeft = new NotFilter(andFilter.left);
            NotFilter negRight = new NotFilter(andFilter.right);
            return new OrFilter(negLeft.NNF(), negRight.NNF());
        }
        if (filter instanceof OrFilter) {
            OrFilter orFilter = ( OrFilter ) filter;
            // No copy of the left and the right is made
            // we expect the later NNF calls to do so
            NotFilter negLeft = new NotFilter(orFilter.left);
            NotFilter negRight = new NotFilter(orFilter.right);
            return new AndFilter(negLeft.NNF(), negRight.NNF());
        }
        if (filter instanceof FalseFilter) {
            FalseFilter falseFilter = ( FalseFilter ) filter;
            return TrueFilter.TRUE;
        }
        if (filter instanceof TrueFilter) {
            TrueFilter trueFilter = ( TrueFilter ) filter;
            return FalseFilter.FALSE;
        }
        return (new NotFilter(filter.NNF()));
   }
    public int compareTo(Filter filter) {
        if (!(filter instanceof NotFilter))
            return getClass().getName().compareTo(filter.getClass().getName());

        NotFilter notFilter = ( NotFilter ) filter;
        
        return this.filter.compareTo(notFilter.filter);
    }
}
