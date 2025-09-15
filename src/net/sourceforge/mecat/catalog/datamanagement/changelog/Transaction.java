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

public class Transaction {

    /**
     * Unique id for the transaction
     */
    final int id;
    
    /**
     * Descriptive name for the transaction
     */
    final String name;
    
    /**
     * Flag that indicates if the whole transaction
     * has to be invoked all at once and thereby undone all at once
     */
    final boolean atom;

    /**
     * Flag that indicates whether the transaction has
     * been triggered by the user or is done in background,
     * for example as update from older version.
     * 
     * From a hierachy perspektive, all child nodes of a 
     * not user invoked transaction will be used as if they are not 
     * user invoked.
     * This allows to only set those transactions as not user invoked
     * that are definitly not user invoked and all child transactions
     * are not user invoked either.
     * 
     */
    final boolean userInvoked;
    
    public Transaction(final int id, final String name, final boolean atom, final boolean userInvoked) {
        this.id = id;
        this.name = name;
        this.atom = atom;
        this.userInvoked = userInvoked;
    }
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public boolean isAtom() {
        return atom;
    }
    public boolean isUserInvoked() {
        return userInvoked;
    }
    
    public String toString() {
        return getName();
    }
}
