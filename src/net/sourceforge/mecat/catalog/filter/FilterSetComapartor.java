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
 * Created on Sep 13, 2005
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filter;

import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;

public class FilterSetComapartor implements Comparator<SortedSet<Filter>> {

    public int compare(SortedSet<Filter> clause0, SortedSet<Filter> clause1) {
//        System.err.print("Compare " + clause0 + " and " + clause1 + " ");
        Iterator<Filter> i0 = clause0.iterator();
        Iterator<Filter> i1 = clause1.iterator();
        while (i0.hasNext() && i1.hasNext()) {
//            System.err.print(".");
            Filter f0 = i0.next();
            Filter f1 = i1.next();
            if (f0.compareTo(f1) != 0) {
//                System.err.println(" with result " + f0.compareTo(f1));
                return f0.compareTo(f1);
            }
        }
        if (i0.hasNext()){
//            System.err.println(" with result 1");
            return 1;
        }
        if (i1.hasNext()){
//            System.err.println(" with result -1");
            return -1;
        }
//        System.err.println(" with result 0 - Everything was the same");
        return 0;
    }

}
