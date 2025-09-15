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
 * Created on Nov 2, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.datamanagement.changelog;

import java.util.Vector;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class ChangeLogTreeModel implements TreeModel {
    
    Vector<TreeModelListener> treeModelListeners = new Vector<TreeModelListener>();

    final ChangeTreeTransactionNode rootNode;
    
    public ChangeLogTreeModel(ChangeTreeTransactionNode rootNode) {
        this.rootNode = rootNode;
    }
    
    public void addTreeModelListener(TreeModelListener treeModelListener) {
        treeModelListeners.add(treeModelListener);
    }

    public void removeTreeModelListener(TreeModelListener treeModelListener) {
        treeModelListeners.remove(treeModelListener);
    }
/*    public void fireInserted(Object o, Object[] obs) {
        for (TreeModelListener treeModelListener : treeModelListeners)
            treeModelListener.treeNodesInserted(new TreeModelEvent(o, obs));
    }*/
    
    public Object getChild(Object node, int index) {
        if (!(node instanceof ChangeTreeTransactionNode))
            return null;
        ChangeTreeTransactionNode trns = (ChangeTreeTransactionNode) node;
        return trns.get(index);
    }

    public int getChildCount(Object node) {
        if (!(node instanceof ChangeTreeTransactionNode))
            return 0;
        ChangeTreeTransactionNode trns = (ChangeTreeTransactionNode) node;
        if (trns.transaction.isAtom())
            return 0;
        return trns.size();
    }

    public int getIndexOfChild(Object node, Object child) {
        if (!(node instanceof ChangeTreeTransactionNode))
            return -1;
        ChangeTreeTransactionNode trns = (ChangeTreeTransactionNode) node;
        if (trns.transaction.isAtom())
            return -1;
        return trns.indexOf(child);
    }

    public Object getRoot() {
        return rootNode;
    }

    public boolean isLeaf(Object node) {
        if (!(node instanceof ChangeTreeTransactionNode))
            return true;
        ChangeTreeTransactionNode trns = (ChangeTreeTransactionNode) node;
        if (trns.transaction.isAtom())
            return true;
        return false;
    }


    public void valueForPathChanged(TreePath arg0, Object arg1) {
        // Well the user does not change anything
    }

}
