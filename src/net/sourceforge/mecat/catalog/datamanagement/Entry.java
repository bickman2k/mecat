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
 *  
 */

/**
 *  Created on July 26, 2004
 *  @author Stephan Richard Palm
 *  
 *  
 * Implementations of this class are the connection
 * between the implementation of catalog and medium.
 * 
 * Every instance of a medium-implementation needs an
 * entry to store the value into an catalog-implementation.
 * 
 * This class is important if you intend to implement or change
 * a datamanagement for storing catalog values. If you do so,
 * you do need to implement the classes #net.sourceforge.mecat.catalog.Catalog and
 * #net.sourceforge.mecat.catalog.Factory as well. I suggest that you implement the
 * three classes in the following order.
 * 1. CatalogFactory
 * 2. Entry
 * 3. Catalog
 * 
 * Why do I suggest to implement the entry before the Catalog?
 * I is possible that you can reuse some of the code that u wrote
 * for the entry in the class catalog but not the other way around.
 *  
 * If you indend to add a new Class of items to the programm
 * you have to implement #net.sourceforge.mecat.catalog.medium.Medium.
 * If you indend to add a new Attribute/Feature to an existing
 * Class of items you need to implement #net.sourceforge.mecat.catalog.feature.Feature.
 * 
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.datamanagement;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public abstract class Entry {
	
	public abstract Catalog getCatalog();
    
    /**
     * This function returns a identifier that allows
     * to identify the entry from which it is invoked.
     * 
     * @return the identifier for the entry
     */
    public abstract Identifier getIdentifier();

	public abstract Entry createSubEntry(final String name);
    /**
     * This function returns the first sub entry with the given name.
     * This function is a shortcut for getSubEntrys(name).first().
     * @param name is the name of the sub entry to search for
     * @return the first sub entry with the name "name".
     */
	public abstract Entry getSubEntry(final String name);
    /**
     * Get all sub entries matching the name.
     * Implementation of this function have to return a
     * Vector no matter what. null is no possible return value.
     * @param name Only return sub entries matchin this name
     * @return all sub entries matching the parameter name
     */
	public abstract List<? extends Entry> getSubEntries(final String name);
    /**
     * Get all sub entries from this entry
     * @return all sub entries
     */
    public abstract List<? extends Entry> getSubEntries();
    /** 
     * Remove the sub entry given by the parameter entry.
     * @param entry the entry that shall be removed
     * @return true if the entry was there and is now gone
     */
	public abstract boolean removeSubEntry(final Entry entry);
	
    /**
     * Remove all sub entries with the given name.
     * @param name The name of the sub entries that shall be deleted.
     */
    public abstract void removeSubEntries(String name);

    /**
	 * The implementation of this function should return the classname
	 * of the Class representing the Entry in the programm.
     * If the entry is an option. The function will return the name of the option.
	 * Ex: net.sourceforge.mecat.catalog.medium.impl.dvd
	 * 
	 * If you want to implement this function you should take a look at
	 * #getEasyClassName(java.lang.String) and #getRealClassName(java.lang.String).
	 * Using this functions could make the storage more human readable.
	 * 
	 * @return the classname for the entry's representation
	 * @see #getEasyClassName(java.lang.String)
	 * @see #getRealClassName(java.lang.String)
	 */
	public abstract String getTypeClassName();


    /**
     * This function returns an Object for synchronization with the attribute
     * change process. While you synchronize with this object, there will be no
     * change of the attribut.
     * 
     * The object can be different for any attribut or not.
     * Neither is garanteed.
     * 
     * @param Name
     * @return synchronization object for threading
     */
    public abstract Object getAttributeSynchronizationObject(String Name);
    
	/**
	 * Sets an Attribute with the name given by "Name" to
	 * the value given by "Value". The value is language independent,
	 * i.e. the value is the same for every language.
     * After setting the new value, an event is thrown.
     * The events contains the old and the new value.
     * This requires implementation of this function to be thread safe,
     * else the oldvalue could be wrong.
	 * 
	 * It is strongly advised to take different names for different features 
	 * because the alternative is error prone.
	 * 
	 * @param Name Name of the Attribute
	 * @param Value New Value of the Attribute
     * @return the oldvalue of the Attribute.
	 * @see #setAttribute(java.lang.String, java.lang.String, java.util.Locale)
	 * @see #getAttribute(java.lang.String)
     * 
	 */
	public abstract String setAttribute(String Name, String Value);

	/**
	 * Sets an Attribute with the name given by "Name" to
	 * the value given by "Value" for the language specified.
     * After setting the new value, an event is thrown.
     * The events contains the old and the new value.
     * This requires implementation of this function to be thread safe,
     * else the oldvalue could be wrong.
	 * 
	 * An attribute can have different values for different languages.
	 * An example for a feature where we need this ability is the
	 * title for a book or a movie. The Book or movie has different
	 * names in different languages.
	 * 
	 * It is strongly advised to take different names for different features 
	 * because the alternative is error prone.
	 * 
	 * @param Name Name of the Attribute
	 * @param Value New Value of the Attribute
	 * @param language The language of the Value
	 * @see #setAttribute(java.lang.String, java.lang.String)
	 * @see #getAttribute(java.lang.String, java.util.Locale)
	 */
	public abstract String setAttribute(String Name, String Value, Locale language);

	/**
	 * Returns the Value of the Attribute with the Name given by "Name".
	 * This function only returns the values of attributes that are 
	 * language indepentend. If you ask for an attribute that depends
	 * on the language the result should be null. Nevertheless it is 
	 * strongly advised to take different names for different features 
	 * because the alternative is error prone. I.e. do not take the same
	 * Name for an attribute without language dependencie and for another
	 * attribute without language dependencie.
	 * 
	 * @param Name Name of the Attribute
	 * @return value of the Atribute if it exists, null else
	 * @see #setAttribute(java.lang.String, java.lang.String)
	 * @see #getAttribute(java.lang.String, java.util.Locale)
	 */
	public abstract String getAttribute(String Name);

	/**
	 * Returns the Value of the Attribute with the Name given by "Name"
	 * with the language given be "language".
	 * This function only returns the values of attributes that depend on the  
	 * language. If you ask for an attribute that not depends
	 * on the language the result should be null. Nevertheless it is 
	 * strongly advised to take different names for different features 
	 * because the alternative is error prone. I.e. do not take the same
	 * Name for an attribute without language dependencie and for another
	 * attribute without language dependencie.
	 * 
	 * @param Name Name of the Attribute
	 * @param language Language of the Attribute
	 * @return value of the Atribute if it exists, null else
	 * @see #setAttribute(java.lang.String, java.lang.String, java.util.Locale)
	 * @see #getAttribute(java.lang.String)
	 */
	public abstract String getAttribute(String Name, Locale language);

    /**
     * This function returns an Object for synchronization with the set-attribute
     * change process. While you synchronize with this object, there will be no
     * change of the set-attribut.
     * 
     * The object can be different for any attribut or not.
     * Neither is garanteed.
     * 
     * @param Name
     * @return synchronization object for threading
     */
    public abstract Object getSetAttributeSynchronizationObject(String Name);

    /**
	 * This functions adds a new language indepenedent value to a SetAttribute.
	 * 
	 * A SetAttribute is an Attribute, that can have more then one value.
	 * Therefore one accesses a setAttribute through add, remove, exists and getIterator.
	 * The result of mixing an SetAttribute with an normal attribute is undefined, i.e.
	 * don't !!!! mix them, don't !!!! have an attribute and an SetAttribute with the same name.
	 * 
	 * @param Name Name of the attribute
	 * @param Value Value to add to the attribute.
     * @return true if the value has changed
	 * @see #addSetAttribute(java.lang.String, java.lang.String, java.util.Locale)
	 */
	public abstract boolean addSetAttribute(String Name, String Value);

	/**
	 * This functions adds a new language depenedent value to a SetAttribute.
	 * 
	 * A SetAttribute is an Attribute, that can have more then one value.
	 * Therefore one accesses a setAttribute through add, remove, exists and getIterator.
	 * The result of mixing an SetAttribute with an normal attribute is undefined, i.e.
	 * don't !!!! mix them, don't !!!! have an attribute and an SetAttribute with the same name.
	 * 
	 * I'm still seeking for a good application for a language depended SetAttribute.
	 * 
	 * @param Name Name of the attribute
	 * @param Value Value to add to the attribute.
	 * @param language Language of the value
     * @return true if the value has changed
	 * @see #addSetAttribute(java.lang.String, java.lang.String)
	 */
	public abstract boolean addSetAttribute(String Name, String Value, Locale language);

	/**
	 * This function returns an Iterator over all language independent values of a SetAttribute.
	 * 
	 * @param Name Name of the attribute
	 * @return an Iterator over the values
	 * @see #getSetIterator(java.lang.String, java.util.Locale)
	 */
	public abstract Iterator<String> getSetIterator(String Name);
	/**
	 * This function returns an Iterator over all language dependent values of a SetAttribute.
	 * 
	 * @param Name Name of the attribute
	 * @param language Language of the values
	 * @return an Iterator over the values
	 * @see #getSetIterator(java.lang.String)
	 */
	public abstract Iterator<String> getSetIterator(String Name, Locale language);

	/**
	 * This functions removes a language indepenedent value from a SetAttribute.
	 * 
	 * @param Name Name of the attribute
	 * @param Value Value to add to the attribute.
	 * @return true if there has been a value that now is erased
	 * @see #removeSetAttribute(java.lang.String, java.lang.String, java.util.Locale)
	 */
	public abstract boolean removeSetAttribute(String Name, String Value);
	/**
	 * This functions removes a language depenedent value form a SetAttribute.
	 * 
	 * I'm still seeking for a good application for a language depended SetAttribute.
	 * 
	 * @param Name Name of the attribute
	 * @param Value Value to add to the attribute.
	 * @param language Language of the value
	 * @return true if there has been a value that now is erased
	 * @see #addSetAttribute(java.lang.String, java.lang.String)
	 */
	public abstract boolean removeSetAttribute(String Name, String Value, Locale language);

	
	/**
	 * This functions removes all values form a SetAttribute.
	 * 
	 * A SetAttribute is an Attribute, that can have more then one value.
	 * Therefore one accesses a setAttribute through add, remove, exists and getIterator.
	 * 
	 * I'm still seeking for a good application for a language depended SetAttribute.
	 * 
	 * @param Name Name of the attribute
     * 
	 */
	public abstract void clearSetAttribute(String Name);
	/**
	 * This functions removes all values form a SetAttribute.
	 * This functions works for both language dependend and independend Attributes.
	 * 
	 * A SetAttribute is an Attribute, that can have more then one value.
	 * Therefore one accesses a setAttribute through add, remove, exists and getIterator.
	 * 
	 * I'm still seeking for a good application for a language depended SetAttribute.
	 * 
	 * @param Name Name of the attribute
	 * @param language Language of the value
     * 
	 */
	public abstract void clearSetAttribute(String Name, Locale language);
	
	/**
	 * This functions checkes if a language indepenedent value given by "value"
	 * is set for from a SetAttribute.
	 * 
	 * A SetAttribute is an Attribute, that can have more then one value.
	 * Therefore one accesses a setAttribute through add, remove, exists and getIterator.
	 * The result of mixing an SetAttribute with an normal attribute is undefined, i.e.
	 * don't !!!! mix them, don't !!!! have an attribute and an SetAttribute with the same name.
	 * 
	 * @param Name Name of the attribute
	 * @param Value Value to add to the attribute.
	 * @return true if there exists this value for the attribute
	 * @see #existsSetAttribute(java.lang.String, java.lang.String, java.util.Locale)
	 */
	public abstract boolean existsSetAttribute(String Name, String Value);
	/**
	 * This functions checkes if a language depenedent value given by "value"
	 * is set for from a SetAttribute.
	 * 
	 * A SetAttribute is an Attribute, that can have more then one value.
	 * Therefore one accesses a setAttribute through add, remove, exists and getIterator.
	 * The result of mixing an SetAttribute with an normal attribute is undefined, i.e.
	 * don't !!!! mix them, don't !!!! have an attribute and an SetAttribute with the same name.
	 * 
	 * @param Name Name of the attribute
	 * @param Value Value to add to the attribute.
	 * @param language Language of the value
	 * @return true if there exists this value for the attribute
	 * @see #existsSetAttribute(java.lang.String, java.lang.String)
	 */
	public abstract boolean existsSetAttribute(String Name, String Value, Locale language);

    
    /**
     * This function returns a list of all Attributes that have one value.
     * @return all single attributes
     */
    public abstract Set<String> getAttributes(); 
    public abstract Set<Locale> getAttributeLanguages(final String name);
    
    public abstract Set<String> getSetAttributes();
    public abstract Set<Locale> getSetAttributeLanguages(final String name);

    
    /**
     * Remove every shred of information stored in the entry.
     *
     */
    public abstract void clear();
    

	// Helpfunktions that could be usefull for more then one implementation
	/**
	 * Path of the implementations of the medium class.
	 * This variable is used for the function
	 * 
	 * #cutForEasyClassName(java.lang.String)
	 * #getRealClassName(java.lang.String)
	 */
	protected static String cutForEasyClassName = "net.sourceforge.mecat.catalog.medium.impl.";
	/**
	 * This function is used to reduce the complexity of the
	 * name of implementations of the medium class. For
	 * medium implementations in the directory #cutForEasyClassName
	 * it return only the name of the class without the path.
	 * For other classes it return "#className#".
	 * 
	 * Ex.: 
	 * the result for 
	 * getEasyClassName("net.sourceforge.mecat.catalog.medium.impl.dvd")
	 * is "dvd".
	 * the result for
	 * getEasyClassName("net.sourceforge.mecat.catalog.datamanagement.Entry")
	 * is "#net.sourceforge.mecat.catalog.datamanagement.Entry#"
	 * 
	 * 
	 * TODO move this Helpfunction to the class Medium
	 * 
	 * @param name Name to be shorted
	 * @return short Class Name for medium-implementations
	 * @see #getRealClassName(java.lang.String)
	 */
	public static String getEasyClassName(String name) {
		if (name.startsWith(cutForEasyClassName))
			return name.substring(cutForEasyClassName.length());
		return "#" + name + "#";
	}
	
	/**
	 * This function is the reverse function for #getEasyClassName(java.lang.String).
	 * 
	 * Ex.
	 * getEasyClassName("dvd") returns
	 * "net.sourceforge.mecat.catalog.datamanagement.Entry"
	 * and getEasyClassName("#net.sourceforge.mecat.catalog.datamanagement.Entry#")
	 * return "net.sourceforge.mecat.catalog.datamanagement.Entry"
	 * 
	 * @param name to infalted
	 * @return returns the orignal classname before getEasyClassName application.
	 * @see #getEasyClassName(java.lang.String)
	 */
	public static String getRealClassName(String name) {
		if (name.startsWith("#"))
			return name.substring(1, name.length()-1);
		return cutForEasyClassName + name;
	}

    Vector<EntryListener> entryListeners = new Vector<EntryListener>();
    Map<String, Vector<AttributeListener>> entryListenersForAttribute = new HashMap<String, Vector<AttributeListener>>();
    Map<String, Vector<SetAttributeListener>> entryListenersForSetAttribute = new HashMap<String, Vector<SetAttributeListener>>();
    Map<String, Vector<SubEntryListener>> entryListenersForSubEntry = new HashMap<String, Vector<SubEntryListener>>();
    
    public void addEntryListener(final EntryListener entryListener) {
        entryListeners.add(entryListener);
    }
    
    public void removeEntryListener(final EntryListener entryListener) {
        entryListeners.remove(entryListener);
    }
    
    public void addEntryListenerForAttribute(final String name, final AttributeListener attributeListener) {
        Vector<AttributeListener> listeners = entryListenersForAttribute.get(name);
        if (listeners == null) {
            listeners = new Vector<AttributeListener>();
            entryListenersForAttribute.put(name, listeners);
        }
        listeners.add(attributeListener);
    }
    
    public void removeEntryListenerForAttribute(final String name, final AttributeListener attributeListener) {
        Vector<AttributeListener> listeners = entryListenersForAttribute.get(name);
        if (listeners == null)
            return;
        listeners.remove(attributeListener);
    }
    
    public void addEntryListenerForSetAttribute(final String name, final SetAttributeListener setAttributeListener) {
        Vector<SetAttributeListener> listeners = entryListenersForSetAttribute.get(name);
        if (listeners == null) {
            listeners = new Vector<SetAttributeListener>();
            entryListenersForSetAttribute.put(name, listeners);
        }
        listeners.add(setAttributeListener);
    }
    
    public void removeEntryListenerForSetAttribute(final String name, final SetAttributeListener setAttributeListener) {
        Vector<SetAttributeListener> listeners = entryListenersForSetAttribute.get(name);
        if (listeners == null)
            return;
        listeners.remove(setAttributeListener);
    }
    
    public void addEntryListenerForSubEntry(final String name, final SubEntryListener subEntryListener) {
        Vector<SubEntryListener> listeners = entryListenersForSubEntry.get(name);
        if (listeners == null) {
            listeners = new Vector<SubEntryListener>();
            entryListenersForSubEntry.put(name, listeners);
        }
        listeners.add(subEntryListener);
    }
    
    public void removeEntryListenerForSubEntry(final String name, final SubEntryListener subEntryListener) {
        Vector<SubEntryListener> listeners = entryListenersForSubEntry.get(name);
        if (listeners == null)
            return;
        listeners.remove(subEntryListener);
    }
    
    public void fireClearEntryCreated(){
        EntryClearedEvent event = new EntryClearedEvent(this);

        // Catalog event has to be first since the changelog 
        // depends on catalog events and the changelog has to 
        // be informed first, else the event history will be in wrong order
        getCatalog().fireEntryChanged(event);

        for (EntryListener entryListener : entryListeners)
            entryListener.entryCleared(event);
    }

    public void fireSubEntryCreated(final String name, final Entry entry){
        SubEntryEvent event = new SubEntryEvent(this, name, entry);

        // Catalog event has to be first since the changelog 
        // depends on catalog events and the changelog has to 
        // be informed first, else the event history will be in wrong order
        getCatalog().fireEntryChanged(event);

        for (EntryListener entryListener : entryListeners)
            entryListener.subEntryCreated(event);
        Vector<SubEntryListener> listeners = entryListenersForSubEntry.get(name);
        if (listeners != null)
            for (SubEntryListener subEntryListener : listeners)
                subEntryListener.subEntryCreated(event);
    }
    public void fireSubEntryRemoved(final Entry entry){
        SubEntryEvent event = new SubEntryEvent(this, entry);

        // Catalog event has to be first since the changelog 
        // depends on catalog events and the changelog has to 
        // be informed first, else the event history will be in wrong order
        getCatalog().fireEntryChanged(event);

        for (EntryListener entryListener : entryListeners)
            entryListener.subEntryRemoved(event);
        Vector<SubEntryListener> listeners = entryListenersForSubEntry.get(entry.getTypeClassName());
        if (listeners != null)
            for (SubEntryListener subEntryListener : listeners)
                subEntryListener.subEntryRemoved(event);
    }
    /**
     * This function should be invoked only if there has been a change,
     * i.e. the list entries has to contain at least one element.
     * 
     * @param name
     * @param entries
     */
    public void fireSubEntriesRemoved(final String name, final List<? extends Entry> entries){
        SubEntryEvent event = new SubEntryEvent(this, name, entries);

        // Catalog event has to be first since the changelog 
        // depends on catalog events and the changelog has to 
        // be informed first, else the event history will be in wrong order
        getCatalog().fireEntryChanged(event);

        for (EntryListener entryListener : entryListeners)
            entryListener.subEntriesRemoved(event);
        Vector<SubEntryListener> listeners = entryListenersForSubEntry.get(entries.get(0).getTypeClassName());
        if (listeners != null)
            for (SubEntryListener subEntryListener : listeners)
                subEntryListener.subEntryRemoved(event);
    }

    public void fireAttributeSet(final String name, final Locale language, final String oldValue, final String newValue){
        AttributeEvent event = new AttributeEvent(this, name, language, oldValue, newValue);
        // Check if the attribute has changed
        if (oldValue == null && newValue == null)
            return;
        if (oldValue != null && oldValue.equals(newValue))
            return;

        // Catalog event has to be first since the changelog 
        // depends on catalog events and the changelog has to 
        // be informed first, else the event history will be in wrong order
        getCatalog().fireEntryChanged(event);

        // Notify all since it has changed
        for (EntryListener entryListener : entryListeners)
            entryListener.attributeSet(event);
        Vector<AttributeListener> listeners = entryListenersForAttribute.get(name);
        if (listeners != null)
            for (AttributeListener attributeListener : listeners)
                attributeListener.attributeSet(event);
    }
    public void fireSetAttributeAdded(final String name, final Locale language, final String value){
        SetAttributeEvent event = new SetAttributeEvent(SetAttributeEvent.SetAttributeEventType.Add, this, name, language, value);

        // Catalog event has to be first since the changelog 
        // depends on catalog events and the changelog has to 
        // be informed first, else the event history will be in wrong order
        getCatalog().fireEntryChanged(event);

        for (EntryListener entryListener : entryListeners)
            entryListener.setAttributeAdded(event);
        Vector<SetAttributeListener> listeners = entryListenersForSetAttribute.get(name);
        if (listeners != null)
            for (SetAttributeListener setAttributeListener : listeners)
                setAttributeListener.setAttributeAdded(event);
    }
    public void fireSetAttributeRemoved(final String name, final Locale language, final String value){
        SetAttributeEvent event = new SetAttributeEvent(SetAttributeEvent.SetAttributeEventType.Remove, this, name, language, value);

        // Catalog event has to be first since the changelog 
        // depends on catalog events and the changelog has to 
        // be informed first, else the event history will be in wrong order
        getCatalog().fireEntryChanged(event);

        for (EntryListener entryListener : entryListeners)
            entryListener.setAttributeRemoved(event);
        Vector<SetAttributeListener> listeners = entryListenersForSetAttribute.get(name);
        if (listeners != null)
            for (SetAttributeListener setAttributeListener : listeners)
                setAttributeListener.setAttributeRemoved(event);
    }
    public void fireSetAttributeCleared(final String name, final Locale language){
        SetAttributeEvent event = new SetAttributeEvent(SetAttributeEvent.SetAttributeEventType.Clear, this, name, language, null);

        // Catalog event has to be first since the changelog 
        // depends on catalog events and the changelog has to 
        // be informed first, else the event history will be in wrong order
        getCatalog().fireEntryChanged(event);

        for (EntryListener entryListener : entryListeners)
            entryListener.setAttributeCleared(event);
        Vector<SetAttributeListener> listeners = entryListenersForSetAttribute.get(name);
        if (listeners != null)
            for (SetAttributeListener setAttributeListener : listeners)
                setAttributeListener.setAttributeCleared(event);
    }
}
