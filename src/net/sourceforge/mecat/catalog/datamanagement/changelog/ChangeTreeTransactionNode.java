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

import java.util.List;
import java.util.Vector;

import net.sourceforge.mecat.catalog.datamanagement.Catalog;

public class ChangeTreeTransactionNode extends Vector<ChangeTreeNode> implements ChangeTreeNode {

    final Transaction transaction;
    final ChangeTreeTransactionNode parent;

    public ChangeTreeTransactionNode(final ChangeTreeTransactionNode parent, final Transaction transaction) {
        this.parent = parent;
        this.transaction = transaction;
    }

    public Transaction getTransaction() {
        return transaction;
    }
    
    public String toString() {
        return transaction.toString();
    }

    public ChangeTreeTransactionNode getParentNode() {
        return parent;
    }

    public int getNumberSteps() {
        int num = 0;
        for (ChangeTreeNode node : this)
            num += node.getNumberSteps();
        
        if (transaction.atom)
            return Math.min(num, 1);
        
        return num;
    }

    public boolean hasUserInvoked(int start) {
        // If this transaction is not user invoked
        // then we don't need to investigage further
        if (!transaction.isUserInvoked())
            return false;
        
        // If the transaction is an atom transaction
        // then this transaction is a user invoked
        // step itself
        if (transaction.isAtom())
            return true;

        // This leaves transactions that are user invoked
        // but not atom, those depend on the children.
        // If any of the children has a user invoked step
        // then the transaction has a user invoked step.
        int pos = 0;
        for (ChangeTreeNode child : this) {
            int num = child.getNumberSteps();
            
            if (pos + num > start)
                if (child.hasUserInvoked(start - pos))
                    return true;
            
            pos += num;
        }
        
        return false;
    }

    public List<Catalog> getCatalogs() {
        return getCatalogs(0);
    }
    
    public List<Catalog> getCatalogs(int start) {
        Vector<Catalog> ret = new Vector<Catalog>();

        // If this transactio is atom
        // then get all catalogs from the children
        if (transaction.isAtom()) {
            for (ChangeTreeNode child : this)
                ret.addAll(child.getCatalogs());
            
            return ret;
        }
        
        // This leaves transactions that are user invoked
        // but not atom, those depend on the children.
        // If any of the children has a user invoked step
        // then the transaction has a user invoked step.
        int pos = 0;
        for (ChangeTreeNode child : this) {
            int num = child.getNumberSteps();
            
            if (pos + num > start)
                ret.addAll(child.getCatalogs(start - pos));
            
            pos += num;
        }
        
        return ret;
    }
    
    
}
