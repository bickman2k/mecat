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

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.Util;
import net.sourceforge.mecat.catalog.medium.features.BadCondition;

public class FilterUtils {
    
    static public EntryFilter copyFilter(final Filter filter, final Entry entry) {
        String condition = filter.getCondition();
        if (filter instanceof EntryFilter) {
            EntryFilter entryFilter = ( EntryFilter ) filter;
            Util.copyEntry(entryFilter.entry, entry);
        }
        Parser parse = new Parser(condition);            
        try {
            return new EntryFilter(entry, parse.parse());
        } catch (BadCondition e) {
            e.printStackTrace();
            return null;
        }
    }
    static public Filter copyFilter(final Filter filter) {
        if (filter == null)
            return TrueFilter.TRUE;
        String condition = filter.getCondition();
        Parser parse = new Parser(condition);            
        try {
            return parse.parse();
        } catch (BadCondition e) {
            e.printStackTrace();
            return TrueFilter.TRUE;
        }
    }
    
    static public boolean isSatisfiable(final Set<SortedSet<Filter>> knf) {
        int initialSize;

        if (knf.isEmpty())
            return true;
        
        for (SortedSet<Filter> set : knf) 
            if (set.isEmpty())
                return false;
        
        for (SortedSet<Filter> set : new Vector<SortedSet<Filter>>(knf)) 
            for (Filter filter : new Vector<Filter>(set)) {
                if (filter == TrueFilter.TRUE) {
                    knf.remove(set);
                    break;
                }
                if (filter == FalseFilter.FALSE) {
                    set.remove(filter);
                }
                
            }

        do {
            initialSize = knf.size();
            Vector<SortedSet<Filter>> conc = new Vector<SortedSet<Filter>>(knf);
            for (SortedSet<Filter> set0 : conc) 
                for (SortedSet<Filter> set1 : conc) 
                    if (!set0.equals(set1)) {
                        SortedSet<Filter> res = resolution(set0, set1);
                        if (res != null) {
                            knf.add(res);
                            if (res.isEmpty())
                                return false;
                        }
                    }
            
        } while (initialSize != knf.size());
        
        return true;
    }
    
    static private SortedSet<Filter> resolution(SortedSet<Filter> set0, SortedSet<Filter> set1) {
        SortedSet<Filter> pos = new TreeSet<Filter>();
        SortedSet<Filter> neg = new TreeSet<Filter>();
        for (Filter filter : set0) 
            if (filter instanceof NotFilter)
                neg.add(((NotFilter)filter).getFilter());
            else
                pos.add(filter);
        for (Filter filter : set1) 
            if (filter instanceof NotFilter)
                neg.add(((NotFilter)filter).getFilter());
            else
                pos.add(filter);

        neg.retainAll(pos);

        if (neg.size() != 1)
            return null;
        
        SortedSet<Filter> ret = new TreeSet<Filter>();
        ret.addAll(set0);
        ret.addAll(set1);
        ret.remove(neg.first());
        ret.remove(new NotFilter(neg.first()));
        
        return ret;
    }
    static private boolean resolvable(SortedSet<Filter> set0, SortedSet<Filter> set1) {
        return false;
    }

    // if (x && !y) or (y && !x) is satisfiable there are not equivalent
    static public boolean equivalent(final Filter filter0, final Filter filter1) {
        SortedSet<SortedSet<Filter>> knfs = new TreeSet<SortedSet<Filter>>(new FilterSetComapartor());
        
        knfs.addAll(filter0.getKNF());
        knfs.addAll(filter1.getNKNF());
        
//        System.out.println("[KNFS] " + knfs);
        
        if (isSatisfiable(knfs)) {
//            System.err.println("[KNFS] " + knfs);
            return false;
        }
        
        knfs.clear();
        knfs.addAll(filter1.getKNF());
        knfs.addAll(filter0.getNKNF());

//        System.out.println("[KNFS] " + knfs);

        if (isSatisfiable(knfs))
            return false;
        
        return true;
    }
    

    static public SortedSet<SortedSet<Filter>> getKNFSets(final Filter filter) {
        Filter knf = getKNF(filter);
        return getKNFSets(knf, new TreeSet<SortedSet<Filter>>(new FilterSetComapartor()));
    }
    
    static public SortedSet<SortedSet<Filter>> getKNFSets(final Filter filter, final SortedSet<SortedSet<Filter>> list) {
        if (filter instanceof AndFilter) {
            AndFilter andFilter = ( AndFilter ) filter;
            getKNFSets(andFilter.left, list);
            getKNFSets(andFilter.right, list);
            return list;
        }
        list.add(getKNFSetsNoMoreAnd(filter, new TreeSet<Filter>()));
        
        return list;
    }

    static public SortedSet<Filter> getKNFSetsNoMoreAnd(final Filter filter, final SortedSet<Filter> list) {
        if (filter instanceof OrFilter) {
            OrFilter orFilter = ( OrFilter ) filter;
            getKNFSetsNoMoreAnd(orFilter.left, list);
            getKNFSetsNoMoreAnd(orFilter.right, list);
            return list;
        }
        list.add(filter);
        
        return list;
    }
   
    static public Filter getKNF(final Filter filter) {
        Filter nnf = filter.NNF();
        return getKNFfromNNF(nnf);
    }
    
    static private Filter getKNFfromNNF(final Filter nnf) {
        if (nnf instanceof OrFilter) {
            OrFilter orFilter = ( OrFilter ) nnf;
            Filter leftKNF = getKNFfromNNF(orFilter.left);
            Filter rightKNF = getKNFfromNNF(orFilter.right);

            // Use distributiv
            // x || (y && z) => (x || y) && (x || z)
            // (x && y) || z => (x || z) && (y || z)
            // Use shortcut if possible
            // ((x1 && x2) || (y1 && y2))
            // => ((x1 && x2) || y1) && ((x1 && x2) || y2)
            // => ((x1 || y1) && (x2 || y1)) && ((x1 || y2) && (x2 || y2))
            if ((leftKNF instanceof AndFilter) && (rightKNF instanceof AndFilter)) {
                AndFilter and0 = (AndFilter) leftKNF;
                AndFilter and1 = (AndFilter) rightKNF;
                OrFilter or0 = new OrFilter(and0.left, and1.left);
                OrFilter or1 = new OrFilter(and0.left, and1.right);
                OrFilter or2 = new OrFilter(and0.right, and1.left);
                OrFilter or3 = new OrFilter(and0.right, and1.right);
                
                return new AndFilter(new AndFilter(getKNFfromNNF(or0), getKNFfromNNF(or1)), new AndFilter(getKNFfromNNF(or2), getKNFfromNNF(or3)));
            }
            if (leftKNF instanceof AndFilter) {
                AndFilter and = (AndFilter) leftKNF;
                OrFilter or0 = new OrFilter(and.left, rightKNF);
                OrFilter or1 = new OrFilter(and.right, rightKNF);
                
                return new AndFilter(getKNFfromNNF(or0), getKNFfromNNF(or1));
            }
            if (rightKNF instanceof AndFilter) {
                AndFilter and = (AndFilter) rightKNF;
                OrFilter or0 = new OrFilter(leftKNF, and.left);
                OrFilter or1 = new OrFilter(leftKNF, and.right);
                
                return new AndFilter(getKNFfromNNF(or0), getKNFfromNNF(or1));
            }
            return new OrFilter(leftKNF, rightKNF);
        }
        
        if (nnf instanceof AndFilter) {
            AndFilter andFilter = ( AndFilter ) nnf;
        
            return new AndFilter(getKNFfromNNF(andFilter.left), getKNFfromNNF(andFilter.right));
        }        
        
        return copyFilter(nnf);
    }
}
