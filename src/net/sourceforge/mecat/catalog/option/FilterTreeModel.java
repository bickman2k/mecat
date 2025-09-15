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
package net.sourceforge.mecat.catalog.option;

import java.util.Vector;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import net.sourceforge.mecat.catalog.filter.LanguageFilter;
import net.sourceforge.mecat.catalog.filter.MediumFilter;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalObject;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.BadCondition;

public class FilterTreeModel implements TreeModel {

    final Object PREDEFINED = new SimpleLocalObject(Options.getI18N(FilterTreeModel.class), "Predefined");
    final Object PERSONEL = new SimpleLocalObject(Options.getI18N(FilterTreeModel.class), "Personal");
    final Object RECENT = new SimpleLocalObject(Options.getI18N(FilterTreeModel.class), "Recent");
    final Object FILTERS = new SimpleLocalObject(Options.getI18N(FilterTreeModel.class), "Filters");
    
    final Object HAS_MEDIA = new SimpleLocalObject(Options.getI18N(Medium.class), "Medium");
    final Object HAS_LANGUAGE = new SimpleLocalObject(Options.getI18N(FilterTreeModel.class), "Language");

    final Vector<Object> DYNAMIC_PRE = new Vector<Object>(){{ add(HAS_MEDIA); add(HAS_LANGUAGE); }};
    
    public Object getRoot() {
        return FILTERS;
    }

    public Object getChild(Object node, int pos) {
        if (node == FILTERS) {
            switch (pos) {
            case 0 : return PREDEFINED;
            case 1 : return PERSONEL;
            case 2 : return RECENT;
            default: return null;
            }
        }
        if (node == PREDEFINED) 
            if (pos < DYNAMIC_PRE.size())
                return DYNAMIC_PRE.elementAt(pos);
            else
                return Options.predefFilter.elementAt(pos - DYNAMIC_PRE.size());
        if (node == HAS_MEDIA)
            return new MediumFilter(Options.media.elementAt(pos));
        if (node == HAS_LANGUAGE)
            try {
                return new LanguageFilter(Options.languages[pos]);
            } catch (BadCondition bad) {
                bad.printStackTrace();
                return null;
            }
        if (node == RECENT)
            return Options.recent_filters.get(pos);
        return null;
    }

    public int getChildCount(Object node) {
        if (node == FILTERS)
            return 3;
        if (node == PREDEFINED)
            return DYNAMIC_PRE.size() + Options.predefFilter.size();
        if (node == HAS_MEDIA)
            return Options.media.size();
        if (node == HAS_LANGUAGE)
            return Options.languages.length;
        if (node == RECENT)
            return Options.recent_filters.size();
        return 0;
    }

    public boolean isLeaf(Object node) {
        if (node == FILTERS) 
            return false;
        if (node == PREDEFINED)
            return false;
        if (node == RECENT && !Options.recent_filters.isEmpty())
            return false;
        if (DYNAMIC_PRE.contains(node))
            return false;
        return true;
    }

    public void valueForPathChanged(TreePath tree, Object node) {
        fireTreeChanged(new TreeModelEvent(node, tree));
    }

    public int getIndexOfChild(Object root, Object node) {
        if (root == null)
            return -1;
        if (node == null)
            return -1;
        if (root == FILTERS) {
            if (node == PREDEFINED)
                return 0;
            if (node == PERSONEL)
                return 1;
            if (node == RECENT)
                return 2;
            return -1;
        }
        if (root == PREDEFINED) {
            if (Options.predefFilter.contains(node))
                return Options.predefFilter.indexOf(node) + DYNAMIC_PRE.size();
            else if (DYNAMIC_PRE.contains(node))
                return DYNAMIC_PRE.indexOf(node);
        }
        if (node == HAS_MEDIA)
            return Options.media.indexOf(node);
        if (node == HAS_LANGUAGE)
            for (int i = 0; i < Options.languages.length; i++)
                if (node == Options.languages[i])
                    return i;
        if (root == RECENT && Options.recent_filters.contains(node))
            return Options.recent_filters.indexOf(node);
        return -1;
    }

    Vector<TreeModelListener> treeModelListeners = new Vector<TreeModelListener>();
    
    protected void fireTreeChanged(TreeModelEvent treeModelEvent) {
        for (TreeModelListener listener : treeModelListeners)
            listener.treeNodesChanged(treeModelEvent);
    }
    
    public void addTreeModelListener(TreeModelListener listener) {
        treeModelListeners.add(listener);
    }

    public void removeTreeModelListener(TreeModelListener listener) {
        treeModelListeners.remove(listener);
    }
    
}