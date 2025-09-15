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
 *  Created on Jul 26, 2004
 *  @author Stephan Richard Palm
 */

package net.sourceforge.mecat.catalog.datamanagement;

import java.awt.Component;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import net.sourceforge.mecat.catalog.i18n.fromCatalog.CatalogResource;
import net.sourceforge.mecat.catalog.option.Options;

import static net.sourceforge.mecat.catalog.datamanagement.CatalogEvent.CatalogEventType.*;

// TODO give warning when Options.getCurrentLocale() != this.language
// TODO give warning when using setDescription/Name while
// 	           Options.getCurrentLocale() != this.language and
//             getDescription/Name(this.language) == null
// TODO mark Strings that are not in the language Options.getCurrentLocale() with color
/**
 * For the class catalog there could be given a parameter language
 * and if you don't it means I take the Global language for it (fallback). That is 
 * in conflict with the way Entry handels this. If you don't give the
 * language setting for an Attribute of an Entry it means this
 * Attribute has no languagesetting (no fallback).
 *
 * @author Stephan Richard Palm
 * 
 * 
 * 
 * 
 */
public abstract class Catalog {

	
	/**
	 * After invoking this function an invokation of 
	 * getSaveCatalogConnection should return null.
	 * This function is necessary in order to load a 
	 * catalog and not saying the user where it comes 
	 * from. An Example is the creation of a new catalog,
	 * in order to fill it with defaults, we load the default catalog
	 * and use this function.
	 *
	 */
	public abstract void forgetSaveCatalogConnection();
	
	/**
	 * The implementation of this function should save the Catalog submitted through the parameter catalog
	 * on a position allready known. 
	 * If the Catalog was successfully saved the function should return true.
	 * If it is not possible to save the Catalog
	 * it should return false.
	 * 
	 * @return true if the Catalog is now saved
	 * @see #saveCatalog
	 */
	public abstract boolean saveCatalog();

	/**
	 * The implementation of this function should save the Catalog submitted through the parameter catalog
	 * on the position submitted by the parameter connection. 
	 * If the Catalog was successfully saved the function should return true.
	 * If it is not possible to save the Catalog on the specified position/connection
	 * it should return false.
	 * 
	 * @param connection The Connection trough wich the Catalog should be saved
	 * @return true if the Catalog is now saved
	 * @see #saveAsCatalog
	 */
	public abstract boolean saveAsCatalog(Connection connection);

	/**
	 * The implementation oft this function should predict if a {@link #saveCatalog}
	 * will be successfull. Usualy this function should return false for a newly created
	 * filebases-catalogs. After an {@link CatalogFactory#openCatalog} or {@link #saveAsCatalog} it should return a true.
	 * 
	 * @return Can be saved.
	 */
	public abstract boolean canSave();

    /**
     * After invoking this function the function unsavedChanges should return false.
     *
     */
    public abstract void setUnchanged();
	/**
	 * The implementation of this function should return true if there
	 * has been any modification to the catalog that has not been changed.
	 * 
	 * @return true if changes have been made that are not saved.
	 */
	public abstract boolean unsavedChanges();

	/** The implementation of this function should gather all information
	 * neccessary to store the given Catalog. The connection returned
	 * can be eithter the place where the Catalog allready belongs or a
	 * new place where now Catalog is till then. The descision wich of both
	 * cases is returned should be done through user interaction. Most likely
	 * this will turn out to be a SaveFileDialog.
	 * @return the (new) home for the Catalog
	 */
	public abstract Connection getSaveCatalogConnection(Component parentComponent);
	
	

    /**
     * The implementation of this function should return the
     * connection used to store or load this catalog. If 
     * there exists no such connection this function 
     * must return null.
     * @return the current connection if there is any, else returns null
     */
	public abstract Connection getConnection();
	
	
    /**
     * This function replace  previous functions
     * setLanguage, getLanugage, getDescription, setDescription,
     * setName, getName
     * @return general catalog information entry
     */
	public abstract Entry getGeneralInformationEntry();
	
	
	/**
	 * Sets the language for the Catalog.
	 * This definis with language the catalog has.
	 * This could be used later on to give the user
	 * the opertunity to selected the languange of
	 * the software to that from the catalog.
	 * 
	 * Has no effect on the behavior of the software at the moment.
	 * 
	 * @param language The Language to be set
	 */
//	public abstract void setLanguage(Locale language) ;
	/**
	 * Retrieves the language the catalog has.
	 * @return Language of the Catalog
	 */
	public Locale getLanguage(){
	    Entry general = getGeneralInformationEntry();
        if (general == null)
            return Options.getCurrentLocale();
        String lStr = general.getAttribute("Language");
        if (lStr == null)
            return Options.getCurrentLocale();
        return new Locale(lStr);
    }

	/**
	 * sets the Description of the catalog to the value of the parameter Description.
	 * The description is stored as description in the current Language of the Software.
	 * It takes the current Language of the Programm from Options.getCurrentLocale().
	 *
	 * As you now propably already have guest, there can be set a Description
	 * in several languages.
	 *  
	 * The description should describe (as the name allready says) what is stored in
	 * the Catalog. This was planed as a description the user reads to remind himself
	 * what the Cataog was about.
	 * 
	 * In case you write an implementation for this class, you have to 
	 * override the function setDescription(java.lang.String, java.util.Locale)
	 * in order to make this function work.
	 * 
	 * @param Description Description that shall be set for the Catalog
	 * @see #setDescription(java.lang.String, java.util.Locale)
	 * @see #getDescription()
	 * @see #getDescription(java.util.Locale)
	 */
//	final public void setDescription(String Description) {
//		setDescription(Description, Options.getCurrentLocale());	
//	}
	
	/**
	 * sets the Description of the catalog to the value of the parameter Description.
	 * The description is stored as description in the Language obmitted.
	 * 
	 * As you now propably already have guest, there can be set a Description
	 * in several languages.
	 *  
	 * The description should describe (as the name allready says) what is stored in
	 * the Catalog. This was planed as a description the user reads to remind himself
	 * what the Cataog was about.
	 * 
	 * @param Description Description that shall be set for the Catalog
	 * @param language Language of the description.
	 * @see #setDescription(java.lang.String)
	 * @see #getDescription()
	 * @see #getDescription(java.util.Locale)
	 */	
//	public abstract void setDescription(String Description, Locale language);
	
	/**
	 * Gets the description of the Catalog for the current Language of the 
	 * Programm.
	 * It takes the current Language of the Programm from Options.getCurrentLocale().
	 * 
	 * The description should describe (as the name allready says) what is stored in
	 * the Catalog. This was planed as a description the user reads to remind himself
	 * what the Cataog was about.
	 * 
	 * In case you write an implementation for this class, you have to 
	 * override the function #getDescription(java.util.Locale)
	 * in order to make this function work.
	 * 
	 * @return the Description of the Catalog for the current Language
	 * @see #setDescription(java.lang.String)
	 * @see #setDescription(java.lang.String, java.util.Locale)
	 * @see #getDescription(java.util.Locale)
	 */
//	final public String getDescription() {
//		return getDescription(Options.getCurrentLocale());
//	}
	
	/**
	 * Gets the description of the Catalog for the specified Language.
	 * 
	 * The description should describe (as the name allready says) what is stored in
	 * the Catalog. This was planed as a description the user reads to remind himself
	 * what the Cataog was about.
	 * 
	 * @param language Language for the description.
	 * @return the Description of the Catalog for the specified Language
     * if no description exists the function returns null
	 * @see #setDescription(java.lang.String)
	 * @see #setDescription(java.lang.String, java.util.Locale)
	 */
//	public abstract String getDescription(Locale language);

	/**
	 * This function should store the name for this catalog. 
	 * The description is stored as name for the current Language of the Software.
	 * It takes the current Language of the Programm from Options.getCurrentLocale().
	 * 
	 * In case you write an implementation for this class, you have to 
	 * override the function setName(java.lang.String, java.util.Locale)
	 * in order to make this function work.
	 * 
	 * @param name the name of the catalog to set for the current language
	 */
//	final public void setName(String name) {
//		setName(name, Options.getCurrentLocale());	
//	}
	/**
	 * This function should store the name for this catalog. 
	 * The description is stored as name for the current Language of the Software.
	 * It takes the current Language of the Programm from Options.getCurrentLocale().
	 * 
	 * @param name the name of the catalog to set for the specified language
	 * @param language language of the name
	 */
//	public abstract void setName(String name, Locale language);

	/**
	 * This function return the name of the Catalog for the current Language.
	 * It takes the current Language of the Programm from Options.getCurrentLocale().
	 * 
	 * In case you write an implementation for this class, you have to 
	 * override the function getName(java.util.Locale)
	 * in order to make this function work.
	 * 
	 * @return the Name of the Catalog for the current Language
	 */
//	public final String getName() {
//		return getName(Options.getCurrentLocale());
//	}
	/**
	 * This function return the name of the Catalog for the specified Language.
	 * 
	 * @param language the Language for the Name
	 * @return the Name of the Catalog for the specified Language
	 */
//	public abstract String getName(Locale language);

	/**
	 * The implementation of this function should return a new Entry with the type
	 * given by the parameter Type. The new Entry should be part of the Catalog.
	 * 
	 * @param type Type of the new Entry. The type for an entry is the name of
	 * the class representing the entry in the Software. For an example:
	 * "net.sourceforge.mecat.catalog.medium.impl.dvd"
	 * 
	 * @return a new Entry within the catalog
	 */
	public abstract Entry createEntry(String type);
	/**
	 * The implementation of this function should remove the entry
	 * given by the parameter entry from the catalog.
	 * 
	 * @param entry Entry that shall be removed from the catalog.
	 */
	public abstract void removeEntry(Entry entry);
	
	/**
	 * The implemenation of this function should return an Iterator over the 
	 * entrys of the catalog.
	 * 
	 * @return an Iterator over the entrys
	 */
	public abstract Iterator<? extends Entry> getIterator();

	
	/**
	 * The implementation of this function should return a new Entry with 
	 * given Name. The new Entry should be an Option for the Catalog.
	 * 
	 * @param name The name of the new Option. 
	 * @return a new Option for the catalog
	 */
	public abstract Entry createOption(String name);
	/**
	 * The implementation of this function should remove an option 
	 * given by the parameter option from the catalog.
	 * 
	 * @param option Option that shall be removed from the catalog.
	 */
	public abstract void removeOption(Entry option);
	/**
	 * The implementation of this function should remove all options
	 * with the given name from the catalog.
	 * 
	 * @param name The Name of the Option(s) to be removed from the catalog.
	 */
	public abstract void removeOption(String name);
	
	/**
	 * The implementation of this function should return a vector with all
	 * options with the given name.
	 * @param name The name for the options.
	 * @return a list of all options
	 */
	public abstract List<? extends Entry> getOptions(String name);
	
	/**
	 * The implementation of this function should return the first
	 * options with the given name.
	 * @param name The name for the options.
	 * @return the option entry with the name "name"
	 */
	public abstract Entry getOption(String name);

	/**
	 * The implemenation of this function should return an Iterator over the 
	 * options for the catalog.
	 * 
	 * @return an Iterator over the entrys
	 */
	public abstract Iterator<? extends Entry> getOptionIterator();
	
    public Entry get(Identifier identifier) {
        switch (identifier.getType()){
        case Option:
            return getOption(identifier);
        case Entry:
            return getEntry(identifier);
        case GeneralInformation:
            return getGeneralInformationEntry();
        }
        return null;
    }
    
    public abstract Entry getEntry(Identifier identifier);
    public abstract Entry getOption(Identifier identifier);
    
	/**
	 * This function returns a Catalog Resource for this catalog.
	 * The result should be the same as using this catalog to create a Catalog Resource.
	 * The benefit is that for every catalog there is only one CatalogResource for 
	 * every catalogResourceName and that should be much faster. (If I ever implement this feature).
	 * 
	 * The statements
	 * CatalogResource res = net.sourceforge.mecat.catalog.getCatalogResource("Genres_Resource");
	 * should be equal to
	 * CatalogResource res = new ResourceCatalog(catalog, "Genres_Resource");
	 * @param catalogResourceName is the name of the Resource
	 * @return catalog resource for the name "catalogResourceName"
	 */
	public CatalogResource getCatalogResource(final String catalogResourceName){
		return new CatalogResource(this, catalogResourceName);
	}
    
    
    
    Vector<CatalogListener> catalogListeners = new Vector<CatalogListener>();
    
    public void addCatalogListener(final CatalogListener catalogListener) {
        catalogListeners.add(catalogListener);
    }
    
    public void removeCatalogListener(final CatalogListener catalogListener) {
        catalogListeners.remove(catalogListener);
    }
    
    public void fireEntryAdded(final Entry entry, String name){
        for (CatalogListener catalogListener : catalogListeners)
            catalogListener.entryAdded(new CatalogEvent(EntryAdded, entry, name));
    }
    
    public void fireEntryRemoved(final Entry entry){
        for (CatalogListener catalogListener : catalogListeners)
            catalogListener.entryRemoved(new CatalogEvent(EntryRemoved, entry, null));
    }

    public void fireEntriesRemoved(final String name){
        for (CatalogListener catalogListener : catalogListeners)
            catalogListener.entriesRemoved(new CatalogEvent(EntriesRemoved, null, name));
    }
    
    public void fireEntryChanged(final EntryEvent event){
        for (CatalogListener catalogListener : catalogListeners)
            catalogListener.entryChanged(event);
    }

    public void fireOptionAdded(final Entry entry, String name){
        for (CatalogListener catalogListener : catalogListeners)
            catalogListener.optionAdded(new CatalogEvent(OptionAdded, entry, name));
    }
    
    public void fireOptionRemoved(final Entry entry){
        for (CatalogListener catalogListener : catalogListeners)
            catalogListener.optionRemoved(new CatalogEvent(OptionRemoved, entry, null));
    }

    public void fireOptionsRemoved(final String name){
        for (CatalogListener catalogListener : catalogListeners)
            catalogListener.optionsRemoved(new CatalogEvent(OptionsRemoved, null, name));
    }

    /**
     * This function should return the value set by {@link #setVersion(int)}.
     * If no value has been set, the version 0 has to be assumed.
     * 
     * @return the version of the catalog
     */
    public abstract int getVersion();

    /**
     * Sets the version of the catalog. 
     * This function should only be invoked by the class Util
     * after the util did an update to the catalog.
     * 
     * @param i The new version number
     * @see #getVersion()
     */
    public abstract void setVersion(int i);

    public abstract void setConnection(Connection connection);

}