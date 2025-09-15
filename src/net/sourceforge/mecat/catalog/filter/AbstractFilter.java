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
 * Created on Sep 4, 2005
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filter;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.SortedSet;
import java.util.Vector;

public abstract class AbstractFilter implements Filter {

    
    Vector<FilterListener> filterListeners = new Vector<FilterListener>();
    
    public void addFilterListener(final FilterListener filterListener) {
        filterListeners.add(filterListener);
    }

    public void removeFilterListener(final FilterListener filterListener) {
        filterListeners.remove(filterListener);
    }
    
    protected void fireFilterChange(final FilterListenerEvent event) {
        knf = null;
        nknf = null;
        for (FilterListener filterListener: filterListeners)
            filterListener.changed(event);
    }

    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{    
                DataFlavor.stringFlavor
        };
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(DataFlavor.stringFlavor);
    }

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (!isDataFlavorSupported(flavor))
            throw new UnsupportedFlavorException(flavor);
        return getCondition();
    }

    SortedSet<SortedSet<Filter>> knf = null;
    SortedSet<SortedSet<Filter>> nknf = null;
    
    public SortedSet<SortedSet<Filter>> getNKNF() {
        if (nknf == null) {
            nknf = FilterUtils.getKNFSets(new NotFilter(this));
            FilterUtils.isSatisfiable(nknf);
        }

        return nknf;
    }

    public SortedSet<SortedSet<Filter>> getKNF() {
        if (knf == null) {
            knf = FilterUtils.getKNFSets(this);
            FilterUtils.isSatisfiable(knf);
        }

        return knf;
    }

    public Filter NNF() {
        return FilterUtils.copyFilter(this);
    }
    
    
}
