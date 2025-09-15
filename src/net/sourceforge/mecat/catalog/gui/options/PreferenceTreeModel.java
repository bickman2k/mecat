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
 * Created on Jan 6, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.gui.options;

import java.util.Collections;
import java.util.Vector;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalObject;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.preferences.AbstractMediaOption;
import net.sourceforge.mecat.catalog.option.preferences.DefaultPreferences;
import net.sourceforge.mecat.srp.utils.NiceClass;
import net.sourceforge.mecat.srp.utils.NiceClassDisplayNameComparator;

public class PreferenceTreeModel implements TreeModel {
    
    Object generalNode = new SimpleLocalObject(Options.getI18N(PreferenceTreeModel.class), "General");
    Object mediaNode = new SimpleLocalObject(Options.getI18N(Medium.class), "Media");
    Object featureNode = new SimpleLocalObject(Options.getI18N(Feature.class), "Features");
    
    Vector<NiceClass<Medium>> media = new Vector<NiceClass<Medium>>();
    Vector<NiceClass<Feature>> features = new Vector<NiceClass<Feature>>();
    
    
    public PreferenceTreeModel() {
        for (Class<? extends Medium> medium_class : AbstractMediaOption.getMedia()) 
            media.add(new NiceClass<Medium>(medium_class));
        Collections.sort(media, NiceClassDisplayNameComparator.displayNameComparator);

        for (Class<? extends Feature> featureClass : AbstractMediaOption.getFeatures()) 
            if (DefaultPreferences.defaultPreferences.getFeaturesOption().getOption(featureClass) != null)
                features.add(new NiceClass<Feature>(featureClass));
        Collections.sort(features, NiceClassDisplayNameComparator.displayNameComparator);
    }

    public Object getRoot() {
        return this;
    }

    public Object getChild(Object parent, int index) {
        if (parent == this) {
            switch (index) {
            case 0: return generalNode;
            case 1: return mediaNode;
            case 2: return featureNode;
            default: return null;
            }
        }
        if (parent == mediaNode) {
            return media.elementAt(index);
        }

        if (parent == featureNode) {
            return features.elementAt(index);
        }
        
        return null;
    }

    public int getChildCount(Object parent) {
        if (parent == this)
            return 3;

        if (parent == mediaNode)
            return media.size();
        
        if (parent == featureNode)
            return features.size();
        
        return 0;
    }

    public boolean isLeaf(Object node) {
        // Root node is no leaf
        if (node == this)
            return false;
        
        if (node == generalNode)
            return true;
        if (node == mediaNode)
            return false;
        if (node == featureNode)
            return false;
        
        return true;
    }

    public TreePath getPath(Object node) {
        if (node == this) {
            return new TreePath(this);
        }

        if (node == mediaNode) {
            return new TreePath(new Object[]{this, mediaNode});
        }

        if (node == featureNode) {
            return new TreePath(new Object[]{this, featureNode});
        }
        
        if (node instanceof NiceClass) {
            Class c = (( NiceClass ) node).getClasstype();
            if (Feature.class.isAssignableFrom(c))
                return new TreePath(new Object[]{this, featureNode, node});
            if (Medium.class.isAssignableFrom(c))
                return new TreePath(new Object[]{this, mediaNode, node});
        }
        
        return new TreePath(this);
    }
    
    public void valueForPathChanged(TreePath tree, Object node) {
        fireTreeChanged(new TreeModelEvent(node, tree));
    }

    public int getIndexOfChild(Object parent, Object child) {
        if (parent == this) {
            if (child == generalNode)
                return 0;
            if (child == mediaNode)
                return 1;
            if (child == featureNode)
                return 2;
            return -1;
        }

        if (parent == mediaNode) {
            return media.indexOf(child);
        }

        if (parent == featureNode) {
            return features.indexOf(child);
        }

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
