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
 * Created on July 26, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.datamanagement.xml;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import net.sourceforge.mecat.catalog.datamanagement.Catalog;
import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.Identifier;
import net.sourceforge.mecat.catalog.datamanagement.IdentifierType;

import org.w3c.dom.*;

public class XMLEntry extends Entry {

	private boolean debugDirty = false;

    final static String TYPE_SUBNODE = "SUBNODE";
    final static String TYPE_SINGLEVAL = "SINGLEVAL";
    final static String TYPE_MULTIVAL = "MULTIVAL";
    final static String TYPE_ATTRIBUTE = "TYPE";

    /**
     * This ensures that for every node there will be only one XML Entry.
     * And this allows a fast index for the entries.
     */
    Map<String, List<XMLEntry>> subEntryLists = new HashMap<String, List<XMLEntry>>();
    /**
     * This flag indicates if for 
     * {@link #fillSubEntryLists()} 
     * the information is already computed.
     */
    boolean subEntryListsComplete = false;
    
    
    /**
     * Indicates wheter the entry is a feature.
     */
    final boolean isFeature;
	
    /**
     * Representation of the Entry in the dom-xml-tree.
     */
    protected Node node;
    /**
     * This is the index of the entry for the entries of this type.
     * I.e. if the parent entry (or catalog) has n children with the name
     * "Name" then this entry is the "number"th child of the parent.
     */
    protected int number;
    
    /**
     * Reverence to the main doc for the dom-xml-tree.
     */
    Document doc;

    XMLCatalog catalog = null;

    /**
     * This information is stored to calculate the identifier.
     */
    final XMLEntry parent;
    /**
     * This information is stored to calculate the identifier.
     */
    final int depth;


    /**
     * This information is stored in the event of a needed identifier.
     * A identifier resulting from getIdentifier() is going to have this type.
     */
    final IdentifierType type;
    
    @Override
    public XMLIdentifier getIdentifier() {
        XMLIdentifier ret = new XMLIdentifier(depth + 1, type);
        getIdentifierAkku(ret);
        return ret;
    }
    
//    public boolean isOption() {
//        if (parent != null)
//            return parent.isOption();
//        if (catalog.allOptions.contains(this))
//            return true;
//        return false;
//    }
    
    protected void getIdentifierAkku(XMLIdentifier identifier){
        if (parent != null)
            parent.getIdentifierAkku(identifier);
        identifier.add(new XMLIdentifierNode(node.getNodeName(), number));
    }

    protected XMLEntry getEntry(XMLIdentifier identifier) {
        List<XMLEntry> entryList = subEntryLists.get(identifier.get(depth + 1).name);
        if (entryList == null)
            return null;
        if (entryList.size() <= identifier.get(depth + 1).number)
            return null;
        
        XMLEntry entry = entryList.get(identifier.get(depth + 1).number);
        if (identifier.size() == depth + 2)
            return entry;
        
        return entry.getEntry(identifier);
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    // DO NOT I18N because it would break the sytem
    // since Options would rely on XMLEntry and
    // XMLEntry would rely on Options
    private String getDebugPrefix() {
		if (catalog == null)
			return "[XML-Cat][ NOT CONNECTED ] ";
		else
			return "[XML-Cat][" + catalog.hashCode() + "] ";
	}

    /**
     * This function ensures that all sub entry lists are created.
     */
    public void fillSubEntryLists() {
        if (subEntryListsComplete)
            return;
        
        List<Node> all = FilterNodeList(getAllAttr(), TYPE_ATTRIBUTE, TYPE_SUBNODE);

        for (Node n : all)
            if (!subEntryLists.containsKey(n.getNodeName()))
                fillSubEntryList(n.getNodeName());
        
        // Don't compute all this again.
        subEntryListsComplete = true;
    }

    /**
     * This function should allways return a list, never a null.
     * @param name
     * @return
     */
    public List<XMLEntry> fillSubEntryList(String name) {
        List<Node> all = FilterNodeList(getAllAttr(name), TYPE_ATTRIBUTE, TYPE_SUBNODE);
        Vector<XMLEntry> ret = new Vector<XMLEntry>(all.size());
        for (int i = 0; i < all.size(); i++) {
            Node n = all.get(i);
            ret.add(new XMLEntry(catalog, this, n, i, false, type));
        }
        subEntryLists.put(name, ret);
        return ret;
    }

    /**
     * This function should allways return a list, never a null.
     * @param name
     * @return
     */
    public List<XMLEntry> getSubEntryList(String name) {
        List<XMLEntry> ret = subEntryLists.get(name);
        if (ret != null)
            return ret;
        
        return fillSubEntryList(name);
    }
	
	public Entry createSubEntry(final String name){
		Element element = doc.createElement(name);
		element.setAttribute(TYPE_ATTRIBUTE, TYPE_SUBNODE);
		node.appendChild(element);
		
		// created sub entry. => dirty
		if (debugDirty)
			System.out.println(getDebugPrefix() + "createSubEntry(" + name + ")" );
		catalog.dirty = true;

        // Do not use getSubEntryList here because then 
        // it is unpredictable if the entry already is in the list
        List<XMLEntry> all = subEntryLists.get(name);
        XMLEntry entry = null;
        if (all == null) {
            // This will get a list including the new
            all = fillSubEntryList(name);
            // Get last element since this should be the new entry
            entry = all.get(all.size() - 1);
        } else {
            // Create new entry
            entry = new XMLEntry(catalog, this, element, all.size(), false, type);
            // Add it to list which does not yet contain it
            all.add(entry);
        }
        
        fireSubEntryCreated(name, entry);
        return entry;
	}
    
	public XMLEntry getSubEntry(final String name)
	{
		List<XMLEntry> all = getSubEntryList(name);
		if (all.size() == 0)
			return null;

		return all.get(0);
	}
	
	public List<XMLEntry> getSubEntries(final String name){
        return getSubEntryList(name);
	}

    public Vector<Entry> getSubEntries(){
        fillSubEntryLists();
        
        Vector<Entry> ret = new Vector<Entry>();
        for (Map.Entry<String, List<XMLEntry>> entry : subEntryLists.entrySet())
            ret.addAll(entry.getValue());
        return ret;
    }
    
    
	@Override
    public void removeSubEntries(String name) {
        List<XMLEntry> all = getSubEntryList(name);
        
        if (all.isEmpty())
            return;

        for (XMLEntry entry : all)
            node.removeChild(entry.node);
        subEntryLists.put(name, new Vector<XMLEntry>());

        if (debugDirty)
            System.out.println(getDebugPrefix() + "removeSubEntries(" + name + ") " + all.size() );
        // removed sub entry. => dirty
        catalog.dirty = true;
        
        fireSubEntriesRemoved(name, all);
    }

    public boolean removeSubEntry(final Entry entry)
	{
		if (!(entry instanceof XMLEntry))
			return false;

		XMLEntry e = (XMLEntry)entry;

        // Remove from xml document
		node.removeChild(e.node);
        // Get the cache
        List<XMLEntry> all = getSubEntryList(e.node.getNodeName());
        // Get the position in the cache
        int index = e.getNumber(); // equals all.indexOf(e)
        // Remove the entry from the cache
        all.remove(index);
        // Readjust the numbering of the other elements in cache
        for (int i = index; i < all.size(); i++)
            all.get(i).setNumber(i);
        
		
		if (debugDirty)
			System.out.println(getDebugPrefix() + "removeSubEntry(" + entry.hashCode() + ")" );
        // removed sub entry. => dirty
		catalog.dirty = true;

        fireSubEntryRemoved(entry);
        
		return true;
	}
//	public void removeAllSubEntries(String name);

	
    /**
     * An XMLEntry is represented by a node in XML.
     * Therefore it is obligated to submit a node where
     * to place the data. Throught this node the entry
     * thereby is connected to the catalog.
     * 
     * @param node XMLNode respresenting the entry in XML.
     */
    protected XMLEntry(final XMLCatalog catalog, final XMLEntry parent, final Node node, final int number, final IdentifierType type) {
        this(catalog, parent, node, number, true, type);
    }

    /**
	 * An XMLEntry is represented by a node in XML.
	 * Therefore it is obligated to submit a node where
	 * to place the data. Throught this node the entry
	 * thereby is connected to the catalog.
	 * 
	 * @param node XMLNode respresenting the entry in XML.
	 */
	protected XMLEntry(final XMLCatalog catalog, final XMLEntry parent, final Node node, final int number, final boolean isFeature, final IdentifierType type) {
        if (parent == null)
            depth = 0;
        else
            depth = parent.depth + 1;
        this.parent = parent;
        this.isFeature = isFeature;
		this.catalog = catalog;
		this.node = node;
        this.number = number;
        this.type = type;
		doc = node.getOwnerDocument();
	}

	public String getTypeClassName() {
        boolean cl = node.getNodeName().indexOf(".") != -1;
        if (isFeature)
            return getRealClassName(((cl)?"#":"") + node.getNodeName() + ((cl)?"#":""));
        return node.getNodeName();
	}

	/** (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.datamanagement.Entry#setAttribute(java.lang.String, java.lang.Object)
	 */
	public String setAttribute(String name, String value) {
		return setAttribute(name, value, null);
	}

    
    

    /** 
     * @see net.sourceforge.mecat.catalog.datamanagement.Entry#getAttributeSynchronizationObject(java.lang.String)
     */
    @Override
    public Object getAttributeSynchronizationObject(String Name) {
        return node;
    }

    /** (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.datamanagement.Entry#setAttribute(java.lang.String, java.lang.Object, java.util.Locale)
	 */
	public String setAttribute(String name, String value, Locale language) {
        String oldValue = null;
        
		// going to play with attribute. => dirty
		if (debugDirty)
			System.out.println(getDebugPrefix() + "setAttribute(" + name + ", " + value + ", "+ ((language == null)?"Null":language.getDisplayName()) + ")" );
		catalog.dirty = true;

        // protected the node in order to ensure that, the value does not
        // change between getting the old value and placing the new value. 
        // This would break any programm code deepending on the accuracy
        // of the events and returned value.
        synchronized (node) {
        
    		// Look if the attribute already exists and has to be changed
    		// Start with the first child and go through his siblings
    		List<Node> attr = GetTextNodes(getAllAttr(name, language));
    		if (attr.size() > 0) {
                oldValue = attr.get(0).getNodeValue();
                
    			if (value == null)
    				node.removeChild(attr.get(0).getParentNode());
    			else
    				attr.get(0).setNodeValue(value.toString());
                
    		} else {
    		
        		// No need to go any further, because the value is null
                // and shall be set to null.
        		if (value == null)
                    // No need to fire a change because nothing has changed
        			return oldValue;
        		
        		// By reaching this point we can assume
        		// the Attribute is new to this entry
        		// and thereby we create a new instance
        		Element element = doc.createElement(name);
        		element.appendChild(doc.createTextNode(value.toString()));
        		if (language != null)
        			element.setAttribute("language", language.getLanguage());
                element.setAttribute(TYPE_ATTRIBUTE, TYPE_SINGLEVAL);
        		node.appendChild(element);
            }
        
        }
        
        fireAttributeSet(name, language, oldValue, value);
        return oldValue;
	}

/**
 * TODO find out how to do things like this.
 *	public <Locale> Locale getAttribute(String name) {}
 */

	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.datamanagement.Entry#getAttribute(java.lang.String)
	 */
	public String getAttribute(final String name) {
        List<Node> attr = GetTextNodes(FilterNodeList(getAllAttr(name, null), TYPE_ATTRIBUTE, TYPE_SINGLEVAL));
//        List<Node> attr = FilterNodeList(getAttr(name, null), TYPE_ATTRIBUTE, TYPE_SINGLEVAL);
//		List<Node> attr = getAttr(name, null);
		if (attr.size() > 0)
			return attr.get(0).getNodeValue().trim();
		return null;
	}

	public String getAttribute(final String name, final Locale language) {
        List<Node> attr = GetTextNodes(FilterNodeList(getAllAttr(name, language), TYPE_ATTRIBUTE, TYPE_SINGLEVAL));
//        List<Node> attr = FilterNodeList(getAttr(name, language), TYPE_ATTRIBUTE, TYPE_SINGLEVAL);
//		List<Node> attr = getAttr(name, language);
		if (attr.size() > 0)
			return attr.get(0).getNodeValue().trim();
		return null;
	}


    /** 
     * @see net.sourceforge.mecat.catalog.datamanagement.Entry#getSetAttributeSynchronizationObject(java.lang.String)
     */
    @Override
    public Object getSetAttributeSynchronizationObject(String Name) {
        return node;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.datamanagement.Entry#addSetAttribute(java.lang.String, java.lang.String)
	 */
	public boolean addSetAttribute(final String name, final String value) {
	    return addSetAttribute(name, value, null);
    }

	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.datamanagement.Entry#addSetAttribute(java.lang.String, java.lang.String, java.util.Locale)
	 */
	public boolean addSetAttribute(final String name, final String value, final Locale language) {
		// going to play with attribute. => dirty
		if (debugDirty)
			System.out.println(getDebugPrefix() + "addSetAttribute(" + name + ", " + value + ", "+ ((language == null)?"Null":language.getDisplayName()) + ")" );
		catalog.dirty = true;

        // protected the node in order to ensure that, the value does not
        // change between looking if it allready exists and placing the 
        // new value. This would break any programm code deepending on the 
        // accuracy of the events and returned value.
        synchronized (node) {

            // Look if the the value already is part of the set
            List<Node> attr = GetTextNodes(getAllAttr(name, language));
            for (Node n : attr)
                if (n.getNodeValue().trim().compareTo(value) == 0)
                    // Attribut exists therefor nothing has changed
                    // leaf without doing anything. Noone gets notified
                    // either.
                    return false;

            // By reaching this point we can assume
            // the Attribute is new to this set
            // and thereby we create a new instance
            Element element = doc.createElement(name);
            element.appendChild(doc.createTextNode(value.toString()));
            if (language != null)
                element.setAttribute("language", language.getLanguage());
            element.setAttribute(TYPE_ATTRIBUTE, TYPE_MULTIVAL);
            node.appendChild(element);

        }
        
        fireSetAttributeAdded(name, language, value);
        return true;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.datamanagement.Entry#getSetAttribute(java.lang.String)
	 */
	public Iterator<String> getSetIterator(final String name) {
        List<Node> attr = GetTextNodes(FilterNodeList(getAllAttr(name, null), TYPE_ATTRIBUTE, TYPE_MULTIVAL));
//		List<Node> attr = getAttr(name, null);
		List<String> strs = new LinkedList<String>();
		for (Node n : attr) 
			strs.add(n.getNodeValue().trim());
		return strs.iterator();
//		return new SetIterator(node.getFirstChild(), name);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.datamanagement.Entry#getSetAttribute(java.lang.String, java.util.Locale)
	 */
	public Iterator<String> getSetIterator(final String name, final Locale language) {
        List<Node> attr = GetTextNodes(FilterNodeList(getAllAttr(name, language), TYPE_ATTRIBUTE, TYPE_MULTIVAL));
//		List<Node> attr = getAttr(name, language);
		List<String> strs = new LinkedList<String>();
		for (Node n : attr) 
			strs.add(n.getNodeValue().trim());
		return strs.iterator();
//		return new SetIterator(node.getFirstChild(), name, language);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.datamanagement.Entry#removeSetAttribute(java.lang.String, java.lang.String)
	 */
	public boolean removeSetAttribute(final String name, final String value) {
        return removeSetAttribute(name, value, null);
	}

	public boolean removeSetAttribute(String name, String value, Locale language) {
		// going to play with attribute. => dirty
		if (debugDirty)
			System.out.println(getDebugPrefix() + "removeSetAttribute(" + name + ", " + value + ", "+ ((language == null)?"Null":language.getDisplayName()) + ")" );
		catalog.dirty = true;

        // protected the node in order to ensure that, the value does not
        // change between looking if it allready exists and placing the
        // new value. This would break any programm code deepending on the
        // accuracy of the events and returned value.
        synchronized (node) {

            // Look if the the value is part of the set
            List<Node> attr = GetTextNodes(FilterNodeList(getAllAttr(name,
                    language), TYPE_ATTRIBUTE, TYPE_MULTIVAL));

            boolean erased = false;
            
            for (Node n : attr)
                if (n.getNodeValue().trim().compareTo(value) == 0) {
                    node.removeChild(n.getParentNode());
                    erased = true;
                    break;
                }

            if (!erased)
                return false;
            
        }

        fireSetAttributeRemoved(name, language, value);
        return true;
        
	}
	public void clearSetAttribute(String name, Locale language) {
		// going to play with attribute. => dirty
		if (debugDirty)
			System.out.println(getDebugPrefix() + "removeWholeAttribute(" + name + ", " + ((language == null)?"Null":language.getDisplayName()) + ")" );
		catalog.dirty = true;

		boolean changed = false;
        
        // protected the node in order to ensure that, the value does not
        // change between looking if it allready exists and placing the
        // new value. This would break any programm code deepending on the
        // accuracy of the events and returned value.
        synchronized (node) {
            List<Node> attr = GetTextNodes(FilterNodeList(getAllAttr(name, language), TYPE_ATTRIBUTE, TYPE_MULTIVAL));
            if (!attr.isEmpty())
                changed = true;
            for (Node n : attr) 
                node.removeChild(n.getParentNode());
        }
        if (changed)
            fireSetAttributeCleared(name, language);
		
	}
	public void clearSetAttribute(String name) {
        clearSetAttribute(name, null);
        
/*		// going to play with attribute. => dirty
		if (debugDirty)
			System.out.println(getDebugPrefix() + "removeWholeAttribute(" + name + ")" );
		catalog.dirty = true;

        List<Node> attr = FilterNodeList(getAllAttr(name), TYPE_ATTRIBUTE, TYPE_MULTIVAL);
//		List<Node> attr = getAllAttr(name);
		for (Node n : attr) 
			node.removeChild(n);*/
		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.datamanagement.Entry#existsSetAttribute(java.lang.String, java.lang.String)
	 */
	public boolean existsSetAttribute(String name, String value) {
		// Look if the the value already is part of the set
//		List<Node> attr = getAttr(name, null);
        List<Node> attr = GetTextNodes(FilterNodeList(getAllAttr(name, null), TYPE_ATTRIBUTE, TYPE_MULTIVAL));
		for (Node n : attr) 
			if (n.getNodeValue().trim().compareTo(value) == 0) 
				return true;
		
		// By reaching this point we can assume
		// the Attribute is not in the set
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.datamanagement.Entry#existsSetAttribute(java.lang.String, java.lang.String, java.util.Locale)
	 */
	public boolean existsSetAttribute(String name, String value, Locale language) {
		// Look if the the value already is part of the set
        List<Node> attr = GetTextNodes(FilterNodeList(getAllAttr(name, language), TYPE_ATTRIBUTE, TYPE_MULTIVAL));
//		List<Node> attr = getAttr(name, language);
		for (Node n : attr) 
			if (n.getNodeValue().trim().compareTo(value) == 0) 
				return true;
		
		// By reaching this point we can assume
		// the Attribute is not in the set
		return false;
	}

	/**
	 * Returns the first value of the first child that is a textnode,
	 * i.e. if this node has at least one child, that is a textnode, then this
	 * function returns the value of the first child that is a texnode.
	 * @param node node to process
	 * @return value of the first child = TEXT_NODE
	 */
	private Node getTextVal(Node node) {
		NodeList text = node.getChildNodes();
		if (text != null)
		  for (int j=0; j < text.getLength();j++)
			if (text.item(j).getNodeType() == Node.TEXT_NODE)
			  return text.item(j);
		
		return null;
	}

	/** 
	 * This functions gets a list of all values of the attribute with the name 
	 * given by "name". It returns only the values matching the language 
	 * given. If the language parameter is null then it returns all
	 * language independent values.
	 * 
	 * @param name Name of the attribute
	 * @param language Language of the attribute
	 * @return List of all subnodes with the right name and language
	 * @author Stephan Richard Palm
	 *
	 * To change the template for this generated type comment go to
	 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
	 */
	private List<Node> getAttr(String name, Locale language) {
		List<Node> all = FilterNodeList(getAllAttr(name), "language", (language == null)? null: language.getLanguage());
		List<Node> return_list = new LinkedList<Node>();
		for (Node n: all)
			if (getTextVal(n) != null)
				return_list.add(getTextVal(n));
		return return_list;
	}
    
    private List<Node> GetTextNodes(List<Node> all) {
        List<Node> return_list = new LinkedList<Node>();
        for (Node n: all)
            if (getTextVal(n) != null)
                return_list.add(getTextVal(n));
        return return_list;
    }
	
	private List<Node> FilterNodeList(List<Node> all, String name, String value) {
		List<Node> return_list = new LinkedList<Node>();
		for(Node n : all) {
			NamedNodeMap attributes = n.getAttributes();
            // If there exists the attribute with the given name and it has the wanted value
            // then add it to the list
			if (attributes != null && (attributes.getNamedItem(name) != null) && (value != null))  {
				if (attributes.getNamedItem(name).getNodeValue().compareTo(value) == 0) 
					return_list.add(n);
            // If there is no attribute or not the attribute with the given name and the value is null
            // then add it to the list
			} else if ((attributes == null || (attributes.getNamedItem(name) == null)) && (value == null))
				return_list.add(n);
		}
		return return_list;
	}
	
    private List<Node> getAllAttr(final String name, final Locale language) {
        return FilterNodeList(getAllAttr(name), "language", (language == null)? null: language.getLanguage());
    }
	private List<Node> getAllAttr(final String name) {
		List<Node> return_list = new LinkedList<Node>();

		Node child = node.getFirstChild();
		while (child != null) {
			if ((name == null) || (child.getNodeName().compareTo(name) == 0))
				return_list.add(child);
			child = child.getNextSibling();
		}
		
		return return_list;
	}
    private List<Node> getAllAttr() {
       return getAllAttr(null);
/*        List<Node> return_list = new LinkedList<Node>();

        Node child = node.getFirstChild();
        while (child != null) {
            return_list.add(child);
            child = child.getNextSibling();
        }
        
        return return_list;*/
    }

	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.datamanagement.Entry#getCatalog()
	 */
	public Catalog getCatalog() {
		return this.catalog;
	}

    @Override
    public Set<String> getAttributes() {
        Set<String> ret = new HashSet<String>();
        List<Node> all = FilterNodeList(getAllAttr(), TYPE_ATTRIBUTE, TYPE_SINGLEVAL);
        for (Node node : all)
            if (node.getNodeName() != null)
                ret.add(node.getNodeName());
              
        return ret;
    }

    @Override
    public Set<Locale> getAttributeLanguages(final String name) {
        Set<Locale> ret = new HashSet<Locale>();
        List<Node> all = FilterNodeList(getAllAttr(name), TYPE_ATTRIBUTE, TYPE_SINGLEVAL);
        for (Node node : all) {
            NamedNodeMap attributes = node.getAttributes();
            if (attributes != null && attributes.getNamedItem("language") != null)
                ret.add(new Locale(attributes.getNamedItem("language").getNodeValue()));
        }
              
        return ret;
    }

    @Override
    public Set<String> getSetAttributes() {
        Set<String> ret = new HashSet<String>();
        List<Node> all = FilterNodeList(getAllAttr(), TYPE_ATTRIBUTE, TYPE_MULTIVAL);
        for (Node node : all)
            if (node.getNodeName() != null)
                ret.add(node.getNodeName());
              
        return ret;
    }

    @Override
    public Set<Locale> getSetAttributeLanguages(final String name) {
        Set<Locale> ret = new HashSet<Locale>();
        List<Node> all = FilterNodeList(getAllAttr(name), TYPE_ATTRIBUTE, TYPE_MULTIVAL);
        for (Node node : all) {
            NamedNodeMap attributes = node.getAttributes();
            if (attributes != null && attributes.getNamedItem("language") != null)
                ret.add(new Locale(attributes.getNamedItem("language").getNodeValue()));
        }
              
        return ret;
    }

    @Override
    public void clear() {

        while (node.getFirstChild() != null)
            node.removeChild(node.getFirstChild());
        
        fireClearEntryCreated();
    }

/*
 * For every node in the XML Catalog there only
 * exists one XML Entry
    @Override
    public boolean equals(Object arg0) {
        return node.equals(arg0);
    }

    @Override
    public int hashCode() {
        return node.hashCode();
    }*/

}
