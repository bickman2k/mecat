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
 * Created on 27.07.2004
 * @author Stephan Richard Palm
 * 
 * The entrie iterator, iterates over all ELEMENT_NODE's from the children
 * of the node given to the constructor.
 */
package net.sourceforge.mecat.catalog.datamanagement.xml;

import java.util.Iterator;

import org.w3c.dom.Node;

/**
 * @deprecated
 * Created on Nov 2, 2006
 *
 * @author Stephan Richard Palm
 *
 */
public class EntryIterator /*implements Iterator<XMLEntry>*/ {

//	XMLCatalog catalog;
//	Node node, next_node;
//
//    final boolean isFeatureEntryIterator;
//    
//	/**
//	 * Constructor for the EntryIterator. 
//	 * 
//	 * @param node Parent node for the nodes to iterate over.
//	 */
//    public EntryIterator(final XMLCatalog catalog, final Node node) {
//        this(catalog, node, true);
//    }
//    
//	public EntryIterator(final XMLCatalog catalog, final Node node, final boolean isFeatureEntryIterator) {
//        this.isFeatureEntryIterator = isFeatureEntryIterator;
//		this.catalog = catalog;
//		this.node = null; 
//		this.next_node = node.getFirstChild();
//		// Take care that the next_node is a valid node
//		if (!checkNextNode())
//			findNextNode();
//	}
//	
//	private boolean checkNextNode() {
//		if (next_node == null)
//			return false;
//
//		if (next_node.getNodeType() == Node.ELEMENT_NODE)
//			return true;
//
//		return false;
//	}
//
//	// Search the next node
//	// Methode:
//	// Move through the siblings with the
//	// var next_node until we found a match for the set
//	// or there are no siblings any more.
//	// If the iterator already has found all nodes this
//	// methode will do nothing.
//	private void findNextNode() {
//		if (next_node == null)
//			return;
//
//		next_node = next_node.getNextSibling();
//		while (next_node != null) {
//			if (checkNextNode() == true)
//				return;
//			next_node = next_node.getNextSibling();
//		}
//	}
//
//	/* (non-Javadoc)
//	 * @see java.util.Iterator#hasNext()
//	 */
//	public boolean hasNext() {		
//		// If the next_node is already know then 
//		// directly answer
//		if (next_node != node)
//			if (next_node == null)
//				return false;
//			else
//				return true;
//	
//		findNextNode();
//		
//		if (next_node == null)
//			return false;
//			
//		return true;
//	}
//
//	/* (non-Javadoc)
//	 * @see java.util.Iterator#next()
//	 */
//	public XMLEntry next() {
//		hasNext();
//        if (isFeatureEntryIterator)
//            return catalog.getEntry(node = next_node);
//        
//		return new XMLEntry(catalog, node = next_node, this.isFeatureEntryIterator);
//	}
//
//	/* (non-Javadoc)
//	 * @see java.util.Iterator#remove()
//	 */
//	public void remove() {
//		if (node == null)
//			return;
//			
//		if (node.getParentNode() == null)
//			return;
//
//		// So after removing next ist already set to the next;
//		hasNext();
//
//		node.getParentNode().removeChild(node);
//        if (isFeatureEntryIterator)
//            catalog.entries.remove(node);
//		node = null;
//	}

}
