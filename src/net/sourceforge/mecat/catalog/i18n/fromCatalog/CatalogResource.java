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
 * Created on Aug 27, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.i18n.fromCatalog;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JPanel;

import net.sourceforge.mecat.catalog.datamanagement.Catalog;
import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.Util;
import net.sourceforge.mecat.catalog.option.Options;

public class CatalogResource extends ResourceBundle{

    public static final String NOKEY = "There is no translation for the key \"[KEY]\". Using the key itself instead.";
    public static final String NOTRANS = "There is no translation for the key \"[KEY]\". Using the key itself instead.";
    
    // There are two posibilities to store
    // a catalog resource, the first is a
    // set of option within one catalog
	Catalog catalog = null;
    // The other is an entry with subentries
    Entry entry = null;

    // The name allows to have more then one CatalogResource
    // within one entry or one catalog
	String name;

    // Languages that can be used because for every
    // key there is a translation
	public Vector<String> available = new Vector<String>();
    // Languages where not all keys have a translation
	public Vector<String> not_available = new Vector<String>();
    // All languages
	public Vector<String> all_langs = new Vector<String>();
	
	public Map<Locale, Map<String, String>> translations = null;
	public Vector<String> keys = new Vector<String>();
	Map<String, Entry> entries = new HashMap<String, Entry>();

    // Language for the CatalogResource
	Locale language = null;

    
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        if (catalog != null)
            buffer.append(getCatalog().getConnection().toString() + " - " + name + System.getProperty("line.separator"));
        else
            if (entry != null)
                buffer.append(entry.getTypeClassName() + " - " + name + System.getProperty("line.separator"));
            else
                buffer.append(name + System.getProperty("line.separator"));
                
        
        // Column for keys
        buffer.append(Options.getI18N(CatalogResourcePanel.class).getString("Key") + "\t");
        // Print Header for languages
        for (String lan : all_langs)
            buffer.append(lan + "\t");
        buffer.append(System.getProperty("line.separator"));

        for (String key : keys) {
            buffer.append(key + "\t");
            for (String lan : all_langs)
                buffer.append(translations.get(new Locale(lan)).get(key) + "\t");
            buffer.append(System.getProperty("line.separator"));
        }
        
        
        return buffer.toString();
    }
    
    
	
	private CatalogResource(CatalogResource cr, Locale l) {
		this.catalog = cr.catalog;
        this.entry = cr.entry;
        
		this.name = cr.name;
		this.available = cr.available;
		this.not_available = cr.not_available;
		this.all_langs = cr.all_langs;
		this.translations = cr.translations;
		this.keys = cr.keys;
		this.entries = cr.entries;
		this.language = l;
	}

	public void setLanguage(Locale language)  {
		this.language = language;
	}
	
	public CatalogResource getBundle(Locale locale) {
		return new CatalogResource(this, locale);
	}
	
	/**
	 * Make a new CatalogResource that takes the information out of the option part
	 * from the given Catalog. The parameter name is there to distinguish between
	 * different Resources within one catalog.
	 * @param catalog
	 * @param name
	 */
	public CatalogResource(Catalog catalog, String name) {
		this.catalog = catalog;
		this.name = name;

        init();
	}
    
    /**
     * Make a new CatalogResource that takes the information out of the entry
     * given. The parameter name is there to distinguish between
     * different Resources within one entry.
     * @param catalog
     * @param name
     */
    public CatalogResource(Entry entry, String name) {
        this.entry = entry;
        this.name = name;

        init();
    }
    
    protected void init() {
        // Set everything to empty
        available.clear();
        not_available.clear();
        all_langs.clear();
        translations = null;
        keys.clear();
        entries.clear();
        
        // Build every information
        supposedLanguages();
        checkLanguages();
        getTranslations();
    }

    /*
     * Funtion 1 of 6 for transparent use of
     * catalog or entry.
     */
    private List<? extends Entry> getAllTranslationEntries() {
        if (isCatalogBased())
            return catalog.getOptions(name);
        return entry.getSubEntries(name);
    }
    
    /*
     * Funtion 2 of 6 for transparent use of
     * catalog or entry.
     */
    private Entry createTranslationEntry() {
        if (isCatalogBased())
            return catalog.createOption(name);
        return entry.createSubEntry(name);
    }

    /*
     * Funtion 3 of 6 for transparent use of
     * catalog or entry.
     */
    private void removeTranslationEntry(Entry entry) {
        if (isCatalogBased())
            catalog.removeOption(entry);
        else
            this.entry.removeSubEntry(entry);
    }

    /*
     * Funtion 4 of 6 for transparent use of
     * catalog or entry.
     */
    private Entry getLanguagesEntry() {
        if (isCatalogBased())
            return catalog.getOption(name + "_Languages");
        return entry.getSubEntry(name + "_Languages");
    }
    
    /*
     * Funtion 5 of 6 for transparent use of
     * catalog or entry.
     */
    private Entry createLanguagesEntry() {
        if (isCatalogBased())
            return catalog.createOption(name + "_Languages");
        return entry.createSubEntry(name + "_Languages");
    }
    
    /*
     * Funtion 6 of 6 for transparent use of
     * catalog or entry.
     */
    public void clear() {
        if (isCatalogBased()) {
            catalog.removeOption(name + "_Languages");
            catalog.removeOption(name);
        } else {
            Entry languagesEntry = entry.getSubEntry(name + "_Languages");
            if (languagesEntry != null)
                entry.removeSubEntry(languagesEntry);
            for (Entry trns : entry.getSubEntries(name))
                entry.removeSubEntry(trns);
        }
        init();
        fireCompleteChange();
    }
    
	private void getTranslations() {
		List<? extends Entry> vec = getAllTranslationEntries();

		translations = new HashMap<Locale, Map<String, String>>(all_langs.size()*5);
			
		for (String s : all_langs) 
			translations.put(new Locale(s), new HashMap<String, String>(vec.size()*3));
			
		for (Entry e : vec) {
			String title = e.getAttribute("Title");
			if (title != null) {
				keys.add(title);
				entries.put(title, e);
				for (String s : all_langs) 
					translations.get(new Locale(s)).put(title, e.getAttribute("Translation", new Locale(s)));
			}
		}
	}
	
	private void checkLanguages() {
		available.addAll(not_available);
		not_available.clear();
		
		List<? extends Entry> vec = getAllTranslationEntries();
		for (Entry e : vec) 
			while (!checkLanguagesEntry(e))
				;
	}
	
	/**
	 * Returns false if there was one available language
	 * moved to the not_available. This should be
	 * repeated until it returns true.
	 * @return
	 */
	private boolean checkLanguagesEntry(Entry e) {
		String title = e.getAttribute("Title");
		if (title != null)
			for (String s : available) {
				String translation = e.getAttribute("Translation", new Locale(s));
				if ((translation == null) || (translation.length() == 0)) {
					available.remove(s);
					not_available.add(s);
					return false;
				}
			}
		return true;
	}

	private boolean supposedLanguages() {
		Entry entry = getLanguagesEntry();
		if (entry == null)
			return false;
		
		Iterator<String> i = entry.getSetIterator("Languages");
		while (i.hasNext()) {
			String lang = i.next();
			available.add(lang);
			all_langs.add(lang);
		}
		
		return true;
	}

	/* (non-Javadoc)
	 * @see java.util.ResourceBundle#handleGetObject(java.lang.String)
	 */
	public Object handleGetObject(String key) {
        if (key == null)
            return null;
        
		Locale l = findMatchingLanguage();

        if (l == null){
            if (key != NOKEY && key != NOTRANS) 
                System.err.println(Options.getI18N(CatalogResource.class).getString(NOTRANS).replaceAll("\\[KEY\\]", key));
            if (Options.DEBUG)
                (new Exception()).printStackTrace();
            return key;
        }
        
        String ret = translations.get(l).get(key);
        
        if (ret != null)
            return ret;

        
        if (key != NOKEY && key != NOTRANS) 
            System.err.println(Options.getI18N(CatalogResource.class).getString(NOKEY).replaceAll("\\[KEY\\]", key));
        if (Options.DEBUG)
            (new Exception()).printStackTrace();
		return key;
	}

	Locale findMatchingLanguage() {
        // If there is no translation at all return null.
        if (all_langs.size() == 0)
            return null;

        // If this is possible restrict to complet languages
        Vector<String> useLangs = available;
        if (available.size() == 0)
            useLangs = all_langs;
        
        // Use the selected language if it is complete
        // or no complete language exists and the selected language exists
		if (language != null)
			if (useLangs.contains(language.getLanguage()))
				return language;

        // Use the current language if complete or no complete 
        // language exists and the current language exists
		if (language != Options.getCurrentLocale())
			if (useLangs.contains(Options.getCurrentLocale().getLanguage()))
				return Options.getCurrentLocale();

        // Try to use any other language selected for the application
		for (Locale lang : Options.AppPrefs.getGeneralOption().getLanguagesOption())
			if (useLangs.contains(lang.getLanguage()))
				return lang;
		
        // Use the first best thing there is
		return new Locale(useLangs.firstElement());
	}
	
	/* (non-Javadoc)
	 * @see java.util.ResourceBundle#getKeys()
	 */
	public Enumeration<String> getKeys() {
		if (findMatchingLanguage() != null)
			return keys.elements();
		return new Enumeration<String>(){
            public boolean hasMoreElements() {
                return false;
            }
            public String nextElement() {
                return null;
            }
        };
	}
	
	public boolean addLanguage(final Locale language){
		if (language == null)
			return false;
		String lang = language.getLanguage();
		if (all_langs.contains(lang))
			return false;

		// Write into catalog
		Entry entry = getLanguagesEntry();
		if (entry == null)
			entry = createLanguagesEntry();
		if (entry == null)
			return false;
		entry.addSetAttribute("Languages", lang);
		
		// Store into this class
		// The following decision makes it 
		// unesseccary to invoke checkLanguages
		// and therefore is much faster
		if (keys.size() > 0)
			available.add(lang);
		else
			not_available.add(lang);
		all_langs.add(lang);
		translations.put(language, new HashMap<String, String>(keys.size()*3));

        fireAddedLanguage(language);
		
		return true;
	}
	
	public boolean setTranslation(final String key, final Locale language, final String translation) {
//		String lang = language.getLanguage();
		Entry e = entries.get(key);
		if (e == null)
			return false;
		if (translations.get(language) == null)
			return false;
		e.setAttribute("Translation", translation, language);
		translations.get(language).put(key, translation);
		checkLanguages();
        
        fireAddedTranslation(key, language, translation);
        
		return true;
	}
	
	
    public boolean addKey(final String key) {
        return addKey(key, keys.size());
    }

    public boolean addKey(final String key, int index) {
		if (key == null)
			return false;
		if (key.equals(""))
			return false;
		if (keys.contains(key))
			return false;
		
		Entry e = createTranslationEntry();
		if (e == null)
			return false;
		
		e.setAttribute("Title", key);
		keys.add(index, key);
		entries.put(key, e);
		
		not_available.addAll(available);
		available.clear();
        
        fireAddedKey(key);
		
		return true;
	}
	
    public void removeKey(final String key) {
        if (key == null)
            return;
        if (!keys.contains(key))
            return;
        
        keys.remove(key);
        removeTranslationEntry(entries.get(key));
        entries.remove(key);
        
        checkLanguages();
        
        fireRemovedKey(key);
    }
    
	/**
	 * Returns an Konfiguration-Panel for this
	 * Resource. I.e. with this Panel you can and and remove
	 * entrys from the Resource.
	 * @return an OptionPanel for this Resource
	 */
	public JPanel getPanel() {
		CatalogResourcePanel crp = new CatalogResourcePanel(this);
//        addCatalogResourceListener(crp);
        return crp;
	}
    

    public void copyTo(final CatalogResource destination) {
        Entry entryLangugesSource = getLanguagesEntry();
        if (entryLangugesSource != null) {
            Entry entryLangugesDestination = destination.getLanguagesEntry();
            if (entryLangugesDestination == null)
                entryLangugesDestination = destination.createLanguagesEntry();
            Util.copyEntry(entryLangugesSource, entryLangugesDestination);
        }
        for (Entry entry : getAllTranslationEntries()){
            Entry entryDest = destination.createTranslationEntry();
            Util.copyEntry(entry, entryDest);
        }
        destination.init();
        destination.fireCompleteChange();
        
    }

    public boolean isCatalogBased() {
        return (catalog != null);
    }
    
    public Catalog getCatalog(){
        return catalog;
    }

    public boolean isEntryBased() {
        return (entry != null);
    }
    
    public Entry getEntry() {
        return entry;
    }
    
    
    
    
    
    
    Vector<CatalogResourceListener> catalogResourceListeners = new Vector<CatalogResourceListener>();
    
    public void addCatalogResourceListener(CatalogResourceListener catalogResourceListener) {
        catalogResourceListeners.add(catalogResourceListener);
    }
    
    public void removeCatalogResourceListener(CatalogResourceListener catalogResourceListener){
        catalogResourceListeners.remove(catalogResourceListener);
    }
    
    
    
    public void fireCompleteChange(){
        for (CatalogResourceListener catalogResourceListener : catalogResourceListeners)
            catalogResourceListener.completeChange(this);
    }
    public void fireAddedLanguage(final Locale language){
        for (CatalogResourceListener catalogResourceListener : catalogResourceListeners)
            catalogResourceListener.addedLanguage(this, language);
    }

    public void fireRemovedLanguage(final Locale language){
        for (CatalogResourceListener catalogResourceListener : catalogResourceListeners)
            catalogResourceListener.removedLanguage(this, language);
    }

    public void fireAddedKey(final String key){
        for (CatalogResourceListener catalogResourceListener : catalogResourceListeners)
            catalogResourceListener.addedKey(this, key);
    }

    public void fireRemovedKey(final String key){
        for (CatalogResourceListener catalogResourceListener : catalogResourceListeners)
            catalogResourceListener.removedKey(this, key);
    }

    public void fireAddedTranslation(final String key, final Locale language, final String translation){
        for (CatalogResourceListener catalogResourceListener : catalogResourceListeners)
            catalogResourceListener.addedTranslation(this, key, language, translation);
    }

    public void fireRemovedTranslation(final String key, final Locale language, final String translation){
        for (CatalogResourceListener catalogResourceListener : catalogResourceListeners)
            catalogResourceListener.removedTranslation(this, key, language, translation);
    }
    
}
