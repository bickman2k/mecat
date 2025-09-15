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

import java.awt.datatransfer.Transferable;
import java.util.SortedSet;

import javax.swing.JComponent;

import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.BadCondition;

public interface Filter extends Transferable, Comparable<Filter> {
    public void addFilterListener(final FilterListener filterListener);
    public void removeFilterListener(final FilterListener filterListener);
    
    public String getCondition();
    public boolean eval(final Medium medium) throws BadCondition;
    public JComponent visualisation();
    
    // This functionality is with the implementation
    // of filters, because they may want to cache or
    // otherwise optimze the process.
    public Filter NNF();
    public SortedSet<SortedSet<Filter>> getKNF();
    public SortedSet<SortedSet<Filter>> getNKNF();
}
