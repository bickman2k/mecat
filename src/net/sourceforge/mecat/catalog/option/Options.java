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
 * Created on Jul 15, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.option;

import java.awt.Component;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import net.sourceforge.mecat.catalog.datamanagement.Catalog;
import net.sourceforge.mecat.catalog.datamanagement.CatalogFactory;
import net.sourceforge.mecat.catalog.datamanagement.Connection;
import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.PersistentThroughEntry;
import net.sourceforge.mecat.catalog.datamanagement.Util;
import net.sourceforge.mecat.catalog.datamanagement.xml.XMLCatalogFactory;
import net.sourceforge.mecat.catalog.datamanagement.xml.XMLConnection;
import net.sourceforge.mecat.catalog.export.Export;
import net.sourceforge.mecat.catalog.export.ExportProfile;
import net.sourceforge.mecat.catalog.export.NamedExportProfile;
import net.sourceforge.mecat.catalog.filesystem.mime.TagFinder;
import net.sourceforge.mecat.catalog.filter.EntryFilter;
import net.sourceforge.mecat.catalog.filter.Filter;
import net.sourceforge.mecat.catalog.filter.FilterUtils;
import net.sourceforge.mecat.catalog.gui.features.desktop.FeatureDesktop;
import net.sourceforge.mecat.catalog.gui.options.features.imagefeature.ImageInformationServer;
import net.sourceforge.mecat.catalog.gui.options.modules.Module;
import net.sourceforge.mecat.catalog.gui.options.modules.Modules;
import net.sourceforge.mecat.catalog.gui.splasher.SplashWindow;
import net.sourceforge.mecat.catalog.i18n.fromCatalog.CatalogResource;
import net.sourceforge.mecat.catalog.i18n.fromCatalog.LayeredResourceBundle;
import net.sourceforge.mecat.catalog.importCatalog.Import;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.medium.features.AbstractFeature;
import net.sourceforge.mecat.catalog.medium.features.impl.Location;
import net.sourceforge.mecat.catalog.medium.features.impl.Position;
import net.sourceforge.mecat.catalog.medium.features.impl.Title;
import net.sourceforge.mecat.catalog.medium.features.impl.Year;
import net.sourceforge.mecat.catalog.option.preferences.GlobalPreferences;
import net.sourceforge.mecat.catalog.sort.ByMedium;
import net.sourceforge.mecat.catalog.sort.Comparing;
import net.sourceforge.mecat.catalog.sort.ConfigurableComparator;
import net.sourceforge.mecat.catalog.templates.Template;
import net.sourceforge.mecat.srp.utils.NiceClass;

public class Options implements Serializable {

    public static int verbosity = FromCommandLine.verbosity;
    public static boolean DEBUG = FromCommandLine.debug;
    
    /**
     * Directory where all information about the current configuration of MeCat for the current user is located.
     */
    public static String USER_OPTION_DIR = System.getProperty("user.home") + System.getProperty("file.separator", "/") + "MeCat" + System.getProperty("file.separator", "/");

    /**
     * In this catalog all information about the application
     * will be stored. That is the language, the size of the frame, ...
     */
	public static Catalog persistent = null;
	private static Entry general = null;
    // Temp variable in order to aquire information in the right order
    private static Entry prefs = null;

    /**
     * This catalog should be used if one need a dummy catalog.
     * The main reason to use a dummy catalog is to create a loose entry.
     */
    private static Catalog dummyCatalog = null;

    

    static public GlobalPreferences AppPrefs = null;

    final static Map<Class, Catalog> classCatalogs = new HashMap<Class, Catalog>();
    final static Map<Class, CatalogResource> classI18N = new HashMap<Class, CatalogResource>();
    final static Map<Class, LayeredResourceBundle> classI18NLayer = new HashMap<Class, LayeredResourceBundle>();
    
	final static Map<Class<? extends Feature>, CatalogResource> defaultCatalogResourceForFeature = new HashMap<Class<? extends Feature>, CatalogResource>();
    final static Map<Class<? extends Medium>, CatalogResource> defaultCatalogResourceForMedium = new HashMap<Class<? extends Medium>, CatalogResource>();

    final static Map<Class<? extends Medium>, CatalogResource> userCatalogResourceForMedium = new HashMap<Class<? extends Medium>, CatalogResource>();
    final static Map<Class<? extends Feature>, CatalogResource> userCatalogResourceForFeature = new HashMap<Class<? extends Feature>, CatalogResource>();
	
    /**
     * If useUserCatalogsForFeature is set to true then
     * MeCat checks for resourcce catalogs in USER_OPTION_DIR/Features.
     * Existing resource catalogs will be used as overlay to the default ones.
     */
	static boolean useUserCatalogsForFeature = true;
    /**
     * If useUserCatalogsForMedium is set to true then
     * MeCat checks for resourcce catalogs in USER_OPTION_DIR/Media.
     * Existing resource catalogs will be used as overlay to the default ones.
     */
	static boolean useUserCatalogsForMedium = true;
    /**
     * If useUserCatalogForDefaults is set to true then
     * MeCat checks for a resource catalog USER_OPTION_DIR/default.catalog.xml.
     * Existing resource catalog will be used as overlay to the default one.
     * The default one is located in the package net.sourceforge.mecat.catalog.option.
     * 
     * TODO make function that uses the overlay.
     */
    static boolean useUserCatalogForDefaults = true;

	
	protected static <T> boolean EnsureCatalog(Class<? extends T> cls, 
			                                 Map<Class<? extends T>, CatalogResource> mapping,
			                                 final URL url){
		if (mapping.get(cls) != null)
            return true;

        return AquireCatalog(cls, mapping, url);
	}
    /**
     * AquireCatalog recieves a mapping from Classes to resource catalogs.
     * It adds a new resource catalog for the given class cls.
     * The resource catalog added will be searched with the url URL.
     * The function returns true if it has found the Catalog and added it to the mapping.
     * The function is required to fill the mapping from classes to resource catalogs.
     * 
     * @param cls Class the resource catalog is used for
     * @param mapping Mapping where the results will be included
     * @param url URL of the resource catalog.
     * @return true if there was a readable catalog resource with the given specification otherwise return false
     */
    protected static <T> boolean AquireCatalog(Class<? extends T> cls, Map<Class<? extends T>, CatalogResource> mapping, final URL url){
        if (url == null)
            return false;
		Connection connection = null;
		try {
            connection = new XMLConnection(url);
            Catalog catalog = connection.getCatalogFactory().openCatalog(connection);

            // If there is a catalog resource at the given position then we have a Catalog in the mapping
            // otherwise it maps to null and the catalog could not be aquired
            if (catalog != null) {
                mapping.put(cls, new CatalogResource(catalog, AbstractFeature.getEasyClassName(cls.getName()) + "_Resource" ));
                return true;
            }
		} catch (Exception e) {
			System.err.println(connection.toString());
			System.err.println(cls.getName());
			e.printStackTrace();
		}
        return false;
	}
	
    public static Catalog getCatalog(final Class c) {
        Catalog catalog = classCatalogs.get(c);
        if (catalog != null)
            return catalog;
        
        if (catalog == null){
            Connection connection = null;
            try {
//                URL url = ClassLoader.getSystemResource(c.getName().replaceAll("\\.", "/") + ".resource.xml");
                // c.getResource is better because it takes the classloader from the class
                URL url = c.getResource(c.getSimpleName() + ".resource.xml");
                if (url != null) {
                    connection = new XMLConnection(url);
                    if (connection != null) {
                        catalog = connection.getCatalogFactory().openCatalog(connection);
                        classCatalogs.put(c, catalog);
                        return catalog;
                    }
                }
            } catch (Exception e) {
                System.err.println(connection.toString());
                System.err.println(c.getName());
                e.printStackTrace();
            }
        }
        
        return null;
    }
    
    public static LayeredResourceBundle getI18N(final Class org) {
        if (classI18NLayer.get(org) != null)
            return classI18NLayer.get(org);
        
        Class c = org;
        LinkedHashSet<CatalogResource> bundles = new LinkedHashSet<CatalogResource>();
        while (c != null && c.getName().startsWith("net.sourceforge.mecat")) {
            CatalogResource i18nResource = classI18N.get(c);
            if (!classI18N.containsKey(c)){ 
                Catalog catalog = getCatalog(c);
                if (catalog != null) {
                    i18nResource = catalog.getCatalogResource("I18N");
                    classI18N.put(c, i18nResource);
                } else {
                    if (DEBUG)
                        System.err.println("No resource for the class:" + c.getName());
                    classI18N.put(c, null);
                }
            }
            
            if (i18nResource != null)
                bundles.add(i18nResource);
            
            for (Class interf : c.getInterfaces()) {
                LayeredResourceBundle interfaceBundle = getI18N(interf);
                if (interfaceBundle != null)
                    bundles.addAll(interfaceBundle.getBundles());
            }                

            c = c.getSuperclass();
        } 
        
        LayeredResourceBundle ret = new LayeredResourceBundle(bundles);
        classI18NLayer.put(org, ret);
        
        return ret;
    }
    
    
	/**
	 * Catalog Factory used to save and load the General options.
	 */
	protected static CatalogFactory saveOptionFactory = new XMLCatalogFactory();
	/**
	 * Connection to the Catalog for the General Options.
	 */
	protected static Connection saveOptionConnection;
    /**
     * Connection to the Catalog for the default Image Information Server.
     */
    protected static Connection defaultImageInformationServerConnection;

    public static Catalog getSimpleCatalog() {
        try {
            URL url = null;
            Catalog catalog = null;

            if (useUserCatalogForDefaults) {
                url = new URL("file:" + USER_OPTION_DIR + "default.catalog.xml");
                Connection connection = new XMLConnection(url);
                catalog = connection.getCatalogFactory().openCatalog(connection);
            }
            if (catalog == null) {
                url = ClassLoader.getSystemResource("net/sourceforge/mecat/catalog/option/default.catalog.xml");
                Connection connection = new XMLConnection(url);
                catalog = connection.getCatalogFactory().openCatalog(connection);
            }
            if (catalog == null) 
                // Warning: giving null to the createCatalog as parentComponent
                // is alright in this case because the XMLCatalog does not use the
                // parentComponent, but this shouldn't be done if it is not the
                // XMLCatalog
                return stdFactory.createCatalog((Component)null);

            catalog.forgetSaveCatalogConnection();
            return catalog;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
	public static Catalog getDefaultCatalog(Component component){
        
        // Take the one with the expected title
        for (Template template : templates) {
            if (template.getTitle(Locale.ENGLISH).compareToIgnoreCase("List for media") == 0)
                return template.getCatalog(component);
        }
        
        // Take the one with media in the title
        for (Template template : templates) {
            if (template.getTitle(Locale.ENGLISH).toUpperCase().contains("media".toUpperCase()))
                return template.getCatalog(component);
        }
        
        // Take the first availabe catalog
        if (templates.size() > 0)
            return templates.get(0).getCatalog(component);
        
        // Take fallback if no template can not be found
        // THIS IS ONLY FOR STABILITY WHILE PROGRAMMING
        // THIS CODE SHALL NEVER BE REACHED IN NORMAL USE
        return getSimpleCatalog();
	}

    /**
     * 
     * This function returns a dummy catalog.
     * This catalog can be used to create loose entries.
     * 
     * @return
     */
    public static Catalog getDummyCatalog() {
        if (dummyCatalog == null)
            dummyCatalog = stdXMLFactory.createCatalog();
        return dummyCatalog;
    }
    

    /**
     * List of all export profiles that can be used
     */
    public final static Vector<NamedExportProfile> profiles = new Vector<NamedExportProfile>();

    /**
     * This function finds all export profiles that can be used.
     *
     */
    static void findProfiles() {
        

        profiles.clear();
        for (NiceClass<? extends Export> export : Options.exports) {
            findProfiles(export);
            Options.time("Options: Export profiles find " + export.getClasstype().getSimpleName());
        }

        List<? extends Entry> profileEntries = persistent.getOptions("ExportProfile");
        for (Entry entry : profileEntries)
            addProfile(entry);
        
        Options.time("Options: Export profiles add");
        
        Connection connection = new XMLConnection(defaultPrint);
        Catalog defaultPrintCatalog = connection.getCatalogFactory().openCatalog(connection);
        Entry entry = defaultPrintCatalog.getOption("Print");
        PersistentThroughEntry pte = Util.loadFromEntry(entry);
        if ((pte instanceof ExportProfile))
            addProfile(entry);

        Options.time("Options: Export profiles add default");
    }

    /**
     * This function searches all export profiles from one particular export.
     * If there exists a catalog with the name of the class from the export
     * ended with ".profiles.xml" then all profiles that are stored in this file
     * will be taken.
     * If there exists no such file the export class is asked for a default profile.
     * @param export
     */
    static void findProfiles(final NiceClass<? extends Export> export) {
        URL url = ClassLoader.getSystemResource(export.getClasstype().getName().replaceAll("\\.", "/") + ".profiles.xml");
        if (url == null) {
            addProfile(export);
            return;
        }
        Connection connection = new XMLConnection(url);
        Catalog catalog = connection.getCatalogFactory().openCatalog(connection);
        if (catalog == null) {
            addProfile(export);
            return;
        }
        List<? extends Entry> profileEntries = catalog.getOptions("ExportProfile");
        for (Entry entry : profileEntries) 
            addProfile(entry);
   }

    // Addes an export profile from a export class
    // this is used if there are no explicit export profiles
    static void addProfile(final NiceClass<? extends Export> exportClass) {
        Export export = null;
        try {
            export = (Export)(exportClass.getClasstype().newInstance());
            if (export.getProfile() != null)
                profiles.add(new NamedExportProfile(export.getProfile(), exportClass.toString()));
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
            return;
        } catch (java.lang.InstantiationException ex) {
            ex.printStackTrace();
            return;
        }
    }
    
    // Addes an export Profile from an entry out of a catalog
    public static void addProfile(final Entry entry) {
        // Restore the entry
        PersistentThroughEntry pte = Util.loadFromEntry(entry);
        // Check if it is an Named Export Profile
        if (!(pte instanceof NamedExportProfile))
            return;
        // Cast it into an Export Profile
        NamedExportProfile nep = (NamedExportProfile) pte;
        // Add it to the list of export profiles
        profiles.add(nep);
    }
    
    public static void removeProfile(NamedExportProfile profile) {
        profiles.remove(profile);
        persistent.removeEntry(profile.getEntry());
    }
    
    public static NamedExportProfile replaceProfile(NamedExportProfile oldProfile, NamedExportProfile newProfile) {
        // Find the position of the old profile and remember it
        int index = profiles.indexOf(oldProfile);
        
        // Remove the old profile from the profile list and the options persistent storage
        profiles.remove(oldProfile);
        persistent.removeOption(oldProfile.getEntry());

        // Copy the wanted new profile to the options persistent storage
        // and create new named export profile connected to the options persistent storage
        Entry entry = persistent.createOption("ExportProfile");
        Util.saveToEntry(newProfile, entry);
        NamedExportProfile ret = (NamedExportProfile) Util.loadFromEntry(entry);
        
        // Add the new profile to the list of profiles
        profiles.add(index, ret);

        // return the profile resulting from the new profile
        return ret;
    }
    
    
    public static URL defaultConfig = ClassLoader.getSystemResource("net/sourceforge/mecat/catalog/option/default.config.xml");
    public static URL defaultPrint = ClassLoader.getSystemResource("net/sourceforge/mecat/catalog/option/default.print.xml");
    
    
    public static Export getPrintExport() {
        ExportProfile ep = getPrintExportProfile().exportProfile;
        return ep.getExport();
    }
    public static NamedExportProfile getPrintExportProfile() {
        Connection connection = new XMLConnection(defaultPrint);
        Catalog defaultPrintCatalog = connection.getCatalogFactory().openCatalog(connection);
        Entry entry = defaultPrintCatalog.getOption("Print");
        PersistentThroughEntry pte = Util.loadFromEntry(entry);
        if (!(pte instanceof ExportProfile))
            return null;

        ExportProfile ep = (ExportProfile) pte;
        return new NamedExportProfile(ep, "Print", entry);
    }
    
	/**
	 * This functions saves the options.
	 * 
	 * @return The Catalog where the options will be saved. 
	 * An {@link net.sourceforge.mecat.catalog.datamanagement.Catalog.saveCatalog} operation should be succesfull.
	 *
	 */
	static public void saveOptions() {
		if (persistent == null)
			return;

		saveToEntry(general);

/*		Vector<? extends Entry> cons = persistent.getOptions("Connection");
		for (Entry e : cons)
			persistent.removeOption(e);*/
		persistent.removeOption("Connection");
		for (Connection c : recent_connections)
			Util.saveToEntry(c, persistent.createOption("Connection"));

        persistent.removeOption("RecentSorting");
        for (ConfigurableComparator c : recent_comparings)
            Util.saveToEntry(c, persistent.createOption("RecentSorting"));
//
//        persistent.removeOption("Filter");
//        for (Filter filter : recent_filters)
//            Util.saveToEntry(filter, persistent.createOption("Filter"));

        persistent.removeOption("Preferences");
        Util.saveToEntry(AppPrefs, persistent.createOption("Preferences"));
        
		persistent.saveCatalog();
        
        // Save Image Information
        Catalog catalog = saveOptionFactory.createCatalog((Component)null);
        ensureDirectory(USER_OPTION_DIR);
        ImageInformationServer.getDefaultImageInformationServer().saveTo(catalog);
        catalog.saveAsCatalog(defaultImageInformationServerConnection);
	}

	/**
	 * This option restores the option at the start of the application.
	 *
	 */
	static void loadOptions() {
		persistent = saveOptionFactory.openCatalog(saveOptionConnection);
		
		if (persistent == null) {
			System.out.println("Could not open Optionsfile. Trying to create it.");
            // Warning: giving null to the createCatalog as parentComponent
            // is alright in this case because the XMLCatalog does not use the
            // parentComponent, but this shouldn't be done if it is not the
            // XMLCatalog
			persistent = saveOptionFactory.createCatalog((Component)null);
            ensureDirectory(USER_OPTION_DIR);
			persistent.saveAsCatalog(saveOptionConnection);
            // Load the default config information
            Connection connection = new XMLConnection(defaultConfig);
            Catalog defaultConfig = connection.getCatalogFactory().openCatalog(connection);
            if (defaultConfig != null)
                Util.copyCatalog(defaultConfig, persistent, false);
		}
		if (persistent == null) {
			System.out.println("Could not create it. Configuration changes can not be saved persistent!");
			return;
		}
		general = persistent.getOption("General");
		if (general == null)
			general = persistent.createOption("General");
		else
			loadFromEntry(general);
        
        prefs = persistent.getOption("Preferences");
/*        if (prefEntry != null) {
            PersistentThroughEntry prefs =  Util.loadFromEntry(prefEntry);
            if (prefs instanceof Preferences)
                AppPrefs = (Preferences) prefs;
        }*/
        

		List<? extends Entry> cons = persistent.getOptions("Connection");
		for (Entry e : cons) {
			PersistentThroughEntry connection = Util.loadFromEntry(e);
			if (connection instanceof Connection)
			    recent_connections.add((Connection)connection);
		}

        cons = persistent.getOptions("RecentSorting");
        for (Entry e : cons) {
            PersistentThroughEntry comparator = Util.loadFromEntry(e);
            if (comparator instanceof ConfigurableComparator)
                recent_comparings.add((ConfigurableComparator)comparator);
        }
        
        List<? extends Entry> fs = persistent.getOptions("Filter");

        for (Entry e : fs) 
            recent_filters.add(new EntryFilter(e));
	}
    
/*    public Filter addRecentFilter(final Filter filter) {
        if (filter instanceof EntryFilter) {
            EntryFilter ef = (EntryFilter) filter;
            return ef.copyFilter(persistent.createOption("Filter"));
        }
        EntryFilter ret = new EntryFilter(persistent.createOption("Filter"));
        ret.setCondition(filter.getCondition());
        return ret;
    }*/
	
	
	/**
	 * Saves the general Options to the entry given.
	 * @param entry The entry in wich the genral Options will be placed.
	 */
	private static void saveToEntry(Entry entry){
		entry.clearSetAttribute("languages");
		for (Locale l : languages) 
			entry.addSetAttribute("languages", l.getLanguage());
		
		entry.setAttribute("currentLocale", currentLocale.getLanguage());
	}
	private static void loadFromEntry(Entry entry){

		Vector<Locale> langs = new Vector<Locale>();
		Iterator<String> i = entry.getSetIterator("languages");
		while (i.hasNext()) 
			langs.add(new Locale(i.next()));
		if (langs.size() > 0) {
			languages = new Locale[langs.size()];
			langs.toArray(languages);
		}
		
		if (entry.getAttribute("currentLocale") != null)
			setCurrentLocale(new Locale(entry.getAttribute("currentLocale")));
	}
	
	/**
	 * If this Variable is set to true, 
	 * then the options will be saved on programm exit.
	 */
	private static boolean FallBack_SaveOnExit = true;
	public static void setSaveOnExit(boolean saveOnExit) {
		if (general != null)
			general.setAttribute("saveOnExit", (saveOnExit)?"True":"False");
		else
			FallBack_SaveOnExit = saveOnExit;
	}
	public static boolean isSaveOnExit() {
		if (general == null)
			return FallBack_SaveOnExit;
		if (general.getAttribute("saveOnExit") == null)
			return FallBack_SaveOnExit;

		return general.getAttribute("saveOnExit").compareTo("True") == 0;
	}
	
	public static Set<Connection> recent_connections = new CopyOnWriteArraySet<Connection>();
    public static Vector<EntryFilter> recent_filters = new Vector<EntryFilter>();
    public static Vector<ConfigurableComparator> recent_comparings = new Vector<ConfigurableComparator>();
    
    protected static XMLCatalogFactory stdXMLFactory = new XMLCatalogFactory();
	/**
	 * Here the standart CatalogFactory defined used to create and open Catalogs.
	 * If you use str+N or str+O u're gone use this Factory.
	 */
	public static CatalogFactory stdFactory = stdXMLFactory;
    
    public static final String filterEnding = ".filter.xml";
    public static final String templateEnding = ".template.xml";
	
	private static FileFilter classFilter = new FileFilter(){public boolean accept(File file) {return file.getName().endsWith(".class") && (file.getName().indexOf("$") == -1);}};
    private static FileFilter filterFilter = new FileFilter(){public boolean accept(File file) {return file.getName().endsWith(filterEnding);}};
    private static FileFilter templateFilter = new FileFilter(){public boolean accept(File file) {return file.getName().endsWith(templateEnding);}};
	public static FileFilter dirFilter = new FileFilter(){public boolean accept(File file) {return file.isDirectory();}};
    public static FileFilter fileFilter = new FileFilter(){public boolean accept(File file) {return file.isFile();}};
    public static FileFilter imgFilter = new FileFilter(){public boolean accept(File file) {return (file.getName().endsWith(".gif") || file.getName().endsWith(".jpeg") || file.getName().endsWith(".jpg") || file.getName().endsWith(".png"));}};
	
	protected static Locale currentLocale = Locale.GERMAN;
    
    protected static Vector<LocalListener> localListeners = new Vector<LocalListener>();

    public static void addLocalListener(final LocalListener localListener) {
        localListeners.add(localListener);
    }
    
    public static void removeLocalListener(final LocalListener localListener) {
        localListeners.remove(localListener);
    }
    
    public static void setCurrentLocale(final Locale locale) {
        // Don't alarm everyone if nothing has changed
        if (currentLocale.equals(locale))
            return;
        Locale oldLocale = currentLocale;
        currentLocale = locale;
        JOptionPane.setDefaultLocale(locale);
        for (LocalListener localListener : new Vector<LocalListener>(localListeners))
            localListener.stateChanged(new LocalListenerEvent(LocalListenerEvent.LocalListenerEventType.LocaleChanged, oldLocale, locale));
    }
    
    public static Locale getCurrentLocale() {
        return currentLocale;
    }
    
	public static Locale languages[] = {
		Locale.GERMAN, Locale.ENGLISH, Locale.FRENCH, Locale.JAPANESE,
		new Locale("es"), Locale.ITALIAN, new Locale("sv"), new Locale("bg"),
		new Locale("cs")
	};
	
	public static String[] getLanguages() {
		String ret[] = new String[languages.length];
		for (int i = 0; i < languages.length; i++)
			ret[i] = languages[i].getLanguage();
		return ret;
	}

	private static boolean FallBack_ShowMediumTypeInList = true;
	public static void setShowMediumTypeInList(boolean showMediumTypeInList) {
		if (general != null)
			general.setAttribute("showMediumTypeInList", (showMediumTypeInList)?"True":"False");
		else
			FallBack_ShowMediumTypeInList = showMediumTypeInList;
	}
	
	public static boolean isShowMediumTypeInList() {
		// If the information is not accessable than return the default
		if (general == null)
			return FallBack_ShowMediumTypeInList;
		if (general.getAttribute("showMediumTypeInList") == null)
			return FallBack_ShowMediumTypeInList;

		return general.getAttribute("showMediumTypeInList").compareTo("True") == 0;
	}

    /**
     * List of all catalogs for catalog templtes.
     * This templates will be shown on creating a new catalog
     */
    public static final List<Catalog> templateCatalogs = new Vector<Catalog>();
    public static Vector<Template> templates = new Vector<Template>();

    public static final Vector<Class<? extends Import>> imports = new Vector<Class<? extends Import>>();
    public static final Vector<Class<? extends Medium>> media = new Vector<Class<? extends Medium>>();
	public static final Vector<NiceClass<Feature>> features = new Vector<NiceClass<Feature>>();
	public static final Vector<NiceClass<CatalogFactory>> catalogFactories = new Vector<NiceClass<CatalogFactory>>();
	public static final Vector<NiceClass<Export>> exports = new Vector<NiceClass<Export>>();
    public static final Vector<NiceClass<FeatureDesktop>> desktops = new Vector<NiceClass<FeatureDesktop>>();
    public static final Vector<Class<? extends TagFinder>> tagFinder = new Vector<Class<? extends TagFinder>>();
	
	public static final Vector<Comparing> predefComparing = new Vector<Comparing>();
	public static final Vector<Filter> predefFilter = new Vector<Filter>();
//    public static final Vector<String[]> predefFilter = new Vector<String[]>();
	
	// OptionMessagePrefix
	static String OMP = "[Options] ";
	static String dirFeatures = "net/sourceforge/mecat/catalog/medium/features/impl";
	static String dirMedia = "net/sourceforge/mecat/catalog/medium";
    static String dirTagFinder = "net/sourceforge/mecat/catalog/filesystem/mime";
    static String dirFilters = "net/sourceforge/mecat/catalog/filter";
	static String dirDatamanagement = "net/sourceforge/mecat/catalog/datamanagement";
	static String dirExport = "net/sourceforge/mecat/catalog/export";
    static String dirDesktop = "net/sourceforge/mecat/catalog/gui/features/desktop";
    static String dirTemplates = "net/sourceforge/mecat/catalog/templates";
    static String dirImports = "net/sourceforge/mecat/catalog/importCatalog";
    
    public static FilterTreeModel filterTreeModel = new FilterTreeModel();
	
	static void investigateArchive(JarFile jfile, Module module) {
		int numFeature = 0;
		int numMedia = 0;
        int numTagFinder = 0;
		int numExport = 0;
		int numDatamanagement = 0;
        int numDesktop = 0;
        int numTemplates = 0;
        int numImports = 0;
		Enumeration<? extends ZipEntry> entries = jfile.entries();

		while (entries.hasMoreElements()) {
			ZipEntry zentry = entries.nextElement();
            if (DEBUG && verbosity >= 4)
                System.out.println("[!DEBUG!] " + zentry.getName());
			if (zentry.getName().startsWith(dirFeatures)) {
				if (investigateZipEntryForFeature(zentry, module))
					numFeature++;
			} else if (zentry.getName().startsWith(dirMedia)) {
				if (investigateZipEntryForMedia(zentry, module))
					numMedia++;
            } else if (zentry.getName().startsWith(dirTagFinder)) {
                if (investigateZipEntryForTagFinder(zentry, module))
                    numTagFinder++;
			} else if (zentry.getName().startsWith(dirExport)) {
				if (investigateZipEntryForExport(zentry, module))
					numExport++;
			} else if (zentry.getName().startsWith(dirDatamanagement)) {
				if (investigateZipEntryForDatamanagement(zentry, module))
					numDatamanagement++;
            } else if (zentry.getName().startsWith(dirDesktop)) {
                if (investigateZipEntryForDesktop(zentry, module))
                    numDesktop++;
            } else if (zentry.getName().startsWith(dirTemplates)) {
                if (investigateZipEntryForTemplates(zentry, module))
                    numTemplates++;
            } else if (zentry.getName().startsWith(dirImports))
                if (investigateZipEntryForImports(zentry, module))
                    numImports++;
		}
        if (verbosity >= 1)
		System.out.println(OMP + getI18N(Options.class).getString("FoundClasses")
		                .replaceAll("\\[NUM\\_FEATURE\\]", String.valueOf(numFeature))
		                .replaceAll("\\[NUM\\_MEDIA\\]", String.valueOf(numMedia))
                        .replaceAll("\\[NUM\\_TAGFINDER\\]", String.valueOf(numTagFinder))
		                .replaceAll("\\[NUM\\_EXPORT\\]", String.valueOf(numExport))
                        .replaceAll("\\[NUM\\_IMPORT\\]", String.valueOf(numImports))
		                .replaceAll("\\[NUM\\_DATAMANAGEMENT\\]", String.valueOf(numDatamanagement))
                        .replaceAll("\\[NUM\\_DESKTOP\\]", String.valueOf(numDesktop))
                        .replaceAll("\\[NUM\\_TEMPLATES\\]", String.valueOf(numTemplates))
                        );
	}
	
	static boolean investigateZipEntryForFeature(ZipEntry zentry, Module module) {
		
		if (zentry.isDirectory())
			return false;
		
		if (zentry.getName().indexOf(System.getProperty("file.separator", "/"), dirFeatures.length() + 1) != -1)
			return false;

		if (!zentry.getName().endsWith(".class"))
			return false;
		
		String onlyName = zentry.getName().substring(dirFeatures.length() + 1, zentry.getName().length() -6);
		
		try {
            String className = "net.sourceforge.mecat.catalog.medium.features.impl." + onlyName;
            Class c = null;
            if (module == null)
                c = Class.forName(className);
            else  {
                  ClassLoader cl = new URLClassLoader(module.getClasspathsURLs(), ClassLoader.getSystemClassLoader());
                  c = cl.loadClass(className);
            }
                
            int modifieres = c.getModifiers();
            if (Modifier.isAbstract(modifieres))
                return false;
            if (!Modifier.isPublic(modifieres))
                return false;
            if (Feature.class.isAssignableFrom(c))
                features.add(new NiceClass<Feature>((Class<? extends Feature>)c));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;

	}

    static boolean investigateZipEntryForTagFinder(ZipEntry zentry, Module module) {
        Class<? extends TagFinder> cls = investigateZipEntryForClassImplementation(zentry, TagFinder.class, module);
        if (cls != null) 
            return tagFinder.add(cls);
        else
            return false;
    }
    
	static boolean investigateZipEntryForMedia(ZipEntry zentry, Module module) {
        Class<? extends Medium> cls = investigateZipEntryForClassImplementation(zentry, Medium.class, module);
        if (cls != null) 
            return media.add(cls);
        else
            return false;
	}
	
    static boolean investigateZipEntryForImports(ZipEntry zentry, Module module) {
        Class<? extends Import> cls = investigateZipEntryForClassImplementation(zentry, Import.class, module);
        if (cls != null) 
            return imports.add(cls);
        else
            return false;
    }
    
    static <T> Class<? extends T> investigateZipEntryForClassImplementation(ZipEntry zentry, Class<T> cls, Module module) {
        if (DEBUG && verbosity >= 3)
            System.out.println("[!DEBUG!] Search for " + cls.getSimpleName() + " investigate " + zentry.getName());

        if (zentry.isDirectory())
            return null;
        
        if (!zentry.getName().endsWith(".class"))
            return null;
        
        try {
            String className = zentry.getName().replaceAll("\\/|\\\\", "\\.");
            className = className.substring(0, className.length() - 6);
            Class c = null;
            if (module == null)
                c = Class.forName(className);
            else  {
                  ClassLoader cl = new URLClassLoader(module.getClasspathsURLs(), ClassLoader.getSystemClassLoader());
                  c = cl.loadClass(className);
            }
            int modifieres = c.getModifiers();
            if (Modifier.isAbstract(modifieres))
                return null;
            if (!Modifier.isPublic(modifieres))
                return null;
            if (cls.isAssignableFrom(c))
                return c;
        } catch (ClassNotFoundException e) {
            if (verbosity > 1)
                if (DEBUG)
                    e.printStackTrace();
                else
                    System.err.println(e.getMessage());
        }
        return null;
    }
    
    static boolean investigateZipEntryForFilter(ZipEntry zentry, Module module) {
        if (zentry.isDirectory())
            return false;
        
        if (zentry.getName().indexOf(System.getProperty("file.separator", "/"), dirFilters.length() + 1) != -1)
            return false;

        if (!zentry.getName().endsWith(filterEnding))
            return false;

        String onlyName = zentry.getName().substring(dirFilters.length() + 1);
        
        URL url = null;
        if (module == null)
            url = ClassLoader.getSystemResource(zentry.getName());
        else  {
              ClassLoader cl = new URLClassLoader(module.getClasspathsURLs(), ClassLoader.getSystemClassLoader());
              url = cl.getResource(zentry.getName());
        }
        if (url == null)
            return false;

        XMLConnection connection = new XMLConnection(url);
        Catalog catalog = connection.getCatalogFactory().openCatalog(connection);
        for (Entry entry : catalog.getOptions("Filter")) 
            predefFilter.add(new EntryFilter(entry));

        return true;
    }
    
	static boolean investigateZipEntryForDatamanagement(ZipEntry zentry, Module module) {

		NiceClass<CatalogFactory> nc = investigateZipEntryForDirectory(zentry, dirDatamanagement, "net.sourceforge.mecat.catalog.datamanagement.", CatalogFactory.class, module);
		if (nc == null)
			return false;
		catalogFactories.add(nc);
		return true;
	}

	static boolean investigateZipEntryForExport(ZipEntry zentry, Module module) {

		NiceClass<Export> nc = investigateZipEntryForDirectory(zentry, dirExport, "net.sourceforge.mecat.catalog.export.", Export.class, module);
		if (nc == null)
			return false;
		exports.add(nc);
		return true;
	}

    static boolean investigateZipEntryForDesktop(ZipEntry zentry, Module module) {

        NiceClass<FeatureDesktop> nc = investigateZipEntryForDirectory(zentry, dirDesktop, "net.sourceforge.mecat.catalog.gui.features.desktop.", FeatureDesktop.class, module);
        if (nc == null)
            return false;
        desktops.add(nc);
        return true;
    }

    static boolean investigateZipEntryForTemplates(ZipEntry zentry, Module module) {
        if (zentry.isDirectory())
            return false;
        
        if (zentry.getName().indexOf(System.getProperty("file.separator", "/"), dirTemplates.length() + 1) != -1)
            return false;

        if (!zentry.getName().endsWith(templateEnding))
            return false;

        String onlyName = zentry.getName().substring(dirTemplates.length() + 1);
        
        URL url = null;
        if (module == null)
            url = ClassLoader.getSystemResource(zentry.getName());
        else  {
              ClassLoader cl = new URLClassLoader(module.getClasspathsURLs(), ClassLoader.getSystemClassLoader());
              url = cl.getResource(zentry.getName());
        }
        if (url == null)
            return false;

        XMLConnection connection = new XMLConnection(url);
        Catalog catalog = connection.getCatalogFactory().openCatalog(connection);
        if (catalog != null)
            templateCatalogs.add(catalog);

        return true;
    }

    
	/**
	 * Search a desginated class in subdirs. Helps finding modules in archives
	 * @param zentry File to eval
	 * @param dir The searched Class will be in a subdir of pkg_dir
	 * @param pkg_dir The searched Class will be in a subdir of pkg_dir
	 * @param wanted_type The searched Class must have this supertype
	 * @return 
	 */
	static <T> NiceClass<T> investigateZipEntryForDirectory(ZipEntry zentry, String dir, String pkg_dir, 
			Class<T> wanted_type, Module module) {
		
		// Can not be a directory
		if (zentry.isDirectory())
			return null;

		int dirPos = zentry.getName().indexOf(/*System.getProperty("file.separator", */"/"/*)*/, dir.length() + 1);
		
		// Has to be in an subdirectory
		if (dirPos == -1)
			return null;

		// Not further than one directory down the tree
		if (dirPos != zentry.getName().lastIndexOf(/*System.getProperty("file.separator", */"/"/*)*/))
			return null;

		// Has to be a class
		if (!zentry.getName().endsWith(".class"))
			return null;
		
		String onlyPath = zentry.getName().substring(dir.length() + 1, dirPos);
		String onlyName = zentry.getName().substring(dirPos + 1, zentry.getName().length() - 6);
		
		try {
            String className = pkg_dir + onlyPath + "." + onlyName; 
            Class candidate = null;
            if (module == null)
                candidate = Class.forName(className);
            else  {
                  ClassLoader cl = new URLClassLoader(module.getClasspathsURLs(), ClassLoader.getSystemClassLoader());
                  candidate = cl.loadClass(className);
            }
            int modifieres = candidate.getModifiers();
            if (Modifier.isAbstract(modifieres))
                return null;
            if (!Modifier.isPublic(modifieres))
                return null;
			if (wanted_type.isAssignableFrom(candidate)) 
				return new NiceClass<T>((Class <? extends T>)candidate, onlyPath);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}
	
	static void investigateFeaturesDirectory(String name) {
		int num = 0;
		String dirName = name + System.getProperty("file.separator", "/") + dirFeatures;
        if (verbosity >= 1)
            System.out.println(OMP + getI18N(Options.class).getString("DirForFeature").replaceAll("\\[DIR\\]", dirName)); 

		File dir = new File(dirName);
		if (!dir.exists()) {
            if (verbosity >= 1)
                System.out.println(OMP + getI18N(Options.class).getString("DirDoesNotExist").replaceAll("\\[DIR\\]", dirName));
			return;
		}
		if (!dir.isDirectory()){
            if (verbosity >= 1)
                System.out.println(OMP + getI18N(Options.class).getString("FileNoDir").replaceAll("\\[FILE\\]", dirName));
			return;
		}
		File featuresFiles[] = dir.listFiles(classFilter);
		
		for (File f : featuresFiles)
			try {
				String onlyName = f.getName().substring(0, f.getName().length() - 6);
                Class c = Class.forName("net.sourceforge.mecat.catalog.medium.features.impl." + onlyName);
                int modifieres = c.getModifiers();
                if (Modifier.isAbstract(modifieres))
                    continue;
                if (!Modifier.isPublic(modifieres))
                    continue;
                if (!Feature.class.isAssignableFrom(c))
                    continue;
				features.add(new NiceClass<Feature>((Class<? extends Feature>)c));
				num++;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
            if (verbosity >= 1)
                System.out.println(OMP + getI18N(Options.class).getString("FoundFeature").replaceAll("\\[NUMBER\\]", String.valueOf(num)));

	}
    static void investigateMediaDirectory(String prefix) {
        investigateMediaDirectory(prefix, dirMedia);
    }
	static void investigateMediaDirectory(String prefix, String directory) {
	    investigateDirectoryForClass(prefix, directory, Medium.class, media, "DirForMedia", "FoundMedia");
	}
    static void investigateImportDirectory(String prefix) {
        investigateImportDirectory(prefix, dirImports);
    }
    static void investigateImportDirectory(String prefix, String directory) {
        investigateDirectoryForClass(prefix, directory, Import.class, imports, "DirForImports", "FoundImport");
    }
    static <T> void investigateDirectoryForClass(String prefix, String directory, 
                Class<T> cls, List<Class<? extends T>> vec,
                String dirForMsgKey, String foundMsgKey) {
        int num = 0;
        String dirName = prefix + System.getProperty("file.separator", "/") + directory;
        if (verbosity >= 1)
            System.out.println(OMP + getI18N(Options.class).getString(dirForMsgKey).replaceAll("\\[DIR\\]", dirName)); 

        File dir = new File(dirName);
        if (!dir.exists()) {
            if (verbosity >= 1)
                System.out.println(OMP + getI18N(Options.class).getString("DirDoesNotExist").replaceAll("\\[DIR\\]", dirName));
            return;
        }
        if (!dir.isDirectory()){
            if (verbosity >= 1)
                System.out.println(OMP + getI18N(Options.class).getString("FileNoDir").replaceAll("\\[FILE\\]", dirName));
            return;
        }
        File mediaFiles[] = dir.listFiles(classFilter);
            
        for (File f : mediaFiles)
            try {
                Class c = Class.forName(directory.replaceAll("\\/|\\\\", "\\.") + "." + f.getName().substring(0, f.getName().length() - 6) );
                int modifieres = c.getModifiers();
                if (Modifier.isAbstract(modifieres))
                    continue;
                if (!Modifier.isPublic(modifieres))
                    continue;
                if (!cls.isAssignableFrom(c))
                    continue;
                vec.add((Class<T>)c);
                num++;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (verbosity >= 1)
                System.out.println(OMP + getI18N(Options.class).getString(foundMsgKey).replaceAll("\\[NUMBER\\]", String.valueOf(num)));
            
        File dirFiles[] = dir.listFiles(dirFilter);
        for (File f : dirFiles) {
            String subDirectory = directory + System.getProperty("file.separator", "/") + f.getName();
            investigateDirectoryForClass(prefix, subDirectory, cls, vec, dirForMsgKey, foundMsgKey);
        }
        
            
    }
    static void investigateTagFinderDirectory(String prefix) {
        investigateTagFinderDirectory(prefix, dirTagFinder);
    }
    static void investigateTagFinderDirectory(String prefix, String directory) {
        int num = 0;
        String dirName = prefix + System.getProperty("file.separator", "/") + directory;
        if (verbosity >= 1)
            System.out.println(OMP + getI18N(Options.class).getString("DirForTagFinder").replaceAll("\\[DIR\\]", dirName)); 

        File dir = new File(dirName);
        if (!dir.exists()) {
            if (verbosity >= 1)
                System.out.println(OMP + getI18N(Options.class).getString("DirDoesNotExist").replaceAll("\\[DIR\\]", dirName));
            return;
        }
        if (!dir.isDirectory()){
            if (verbosity >= 1)
                System.out.println(OMP + getI18N(Options.class).getString("FileNoDir").replaceAll("\\[FILE\\]", dirName));
            return;
        }
        File mediaFiles[] = dir.listFiles(classFilter);
            
        for (File f : mediaFiles)
            try {
                Class c = Class.forName(directory.replaceAll("\\/|\\\\", "\\.") + "." + f.getName().substring(0, f.getName().length() - 6) );
                int modifieres = c.getModifiers();
                if (Modifier.isAbstract(modifieres))
                    continue;
                if (!Modifier.isPublic(modifieres))
                    continue;
                if (!TagFinder.class.isAssignableFrom(c))
                    continue;
                tagFinder.add((Class<? extends TagFinder>)c);
                num++;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (verbosity >= 1)
                System.out.println(OMP + getI18N(Options.class).getString("FoundTagFinder").replaceAll("\\[NUMBER\\]", String.valueOf(num)));
            
        File dirFiles[] = dir.listFiles(dirFilter);
        for (File f : dirFiles) {
            String subDirectory = directory + System.getProperty("file.separator", "/") + f.getName();
            investigateTagFinderDirectory(prefix, subDirectory);
        }
        
            
    }
    
    
    static void investigateFilterDirectory(String name) {
        int num = 0;
        String dirName = name + System.getProperty("file.separator", "/") + dirFilters;
        if (verbosity >= 1)
            System.out.println(OMP + getI18N(Options.class).getString("DirForFilter").replaceAll("\\[DIR\\]", dirName)); 

        File dir = new File(dirName);
        if (!dir.exists()) {
            if (verbosity >= 1)
                System.out.println(OMP + getI18N(Options.class).getString("DirDoesNotExist").replaceAll("\\[DIR\\]", dirName));
            return;
        }
        if (!dir.isDirectory()){
            if (verbosity >= 1)
                System.out.println(OMP + getI18N(Options.class).getString("FileNoDir").replaceAll("\\[FILE\\]", dirName));
            return;
        }
        File filterFiles[] = dir.listFiles(filterFilter);
            
        for (File f : filterFiles)
            try {
                String onlyName = f.getName().substring(0, f.getName().length() - filterEnding.length());
                XMLConnection connection = new XMLConnection(f.toURL());
                Catalog catalog = connection.getCatalogFactory().openCatalog(connection);
                for (Entry entry : catalog.getOptions("Filter")) {
                    predefFilter.add(new EntryFilter(entry));
                    num++;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            if (verbosity >= 1)
                System.out.println(OMP + getI18N(Options.class).getString("FoundFilter").replaceAll("\\[NUMBER\\]", String.valueOf(num)));
    }
    
    
    static void investigateTemplateDirectory(String name) {
        int num = 0;
        String dirName = name + System.getProperty("file.separator", "/") + dirTemplates;
        if (verbosity >= 1)
            System.out.println(OMP + getI18N(Options.class).getString("DirForTemplates").replaceAll("\\[DIR\\]", dirName)); 

        File dir = new File(dirName);
        if (!dir.exists()) {
            if (verbosity >= 1)
                System.out.println(OMP + getI18N(Options.class).getString("DirDoesNotExist").replaceAll("\\[DIR\\]", dirName));
            return;
        }
        if (!dir.isDirectory()){
            if (verbosity >= 1)
                System.out.println(OMP + getI18N(Options.class).getString("FileNoDir").replaceAll("\\[FILE\\]", dirName));
            return;
        }
        File templateFiles[] = dir.listFiles(templateFilter);
            
        for (File f : templateFiles)
            try {
                String onlyName = f.getName().substring(0, f.getName().length() - templateEnding.length());
                XMLConnection connection = new XMLConnection(f.toURL());
                Catalog catalog = connection.getCatalogFactory().openCatalog(connection);
                if (catalog != null) {
                    templateCatalogs.add(catalog);
                    num++;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            if (verbosity >= 1)
                System.out.println(OMP + getI18N(Options.class).getString("FoundTemplates").replaceAll("\\[NUMBER\\]", String.valueOf(num)));
    }
    
    
	static void investigateDatamanagementDirectory(String name) {
		investigateDirectoryForDirectory(name, dirDatamanagement, "net.sourceforge.mecat.catalog.datamanagement.", CatalogFactory.class, catalogFactories);
	}
	static void investigateExportDirectory(String name) {
		investigateDirectoryForDirectory(name, dirExport, "net.sourceforge.mecat.catalog.export.", Export.class, exports);
	}
    static void investigateDesktopDirectory(String name) {
        investigateDirectoryForDirectory(name, dirDesktop, "net.sourceforge.mecat.catalog.gui.features.desktop.", FeatureDesktop.class, desktops);
    }

	static <T> void investigateDirectoryForDirectory(String name, String dir_name, String pkg_dir, Class<T> wanted_type, Vector<NiceClass<T>> vec) {
		int num = 0;
		String dirName = name + System.getProperty("file.separator", "/") + dir_name;
        if (verbosity >= 1)
            System.out.println(OMP + getI18N(Options.class).getString("DirForWanted")
                .replaceAll("\\[DIR\\]", dirName)
                .replaceAll("\\[WANTED\\]", getI18N(wanted_type).getString(wanted_type.getSimpleName()))); 

		File dir = new File(dirName);
		if (!dir.exists()) {
            if (verbosity >= 1)
                System.out.println(OMP + getI18N(Options.class).getString("DirDoesNotExist").replaceAll("\\[DIR\\]", dirName));
			return;
		}
		if (!dir.isDirectory()){
            if (verbosity >= 1)
                System.out.println(OMP + getI18N(Options.class).getString("FileNoDir").replaceAll("\\[FILE\\]", dirName));
			return;
		}
		File dirs[] = dir.listFiles(dirFilter);
			
		if (dirs != null)
		for (File f : dirs) {
			File findit[] = new File(f.toString()).listFiles(classFilter);
			if (findit != null)
				for (File find : findit)
					try {
						Class candidate =
							Class.forName(pkg_dir + f.getName() + "." 
									+ find.getName().substring(0, find.getName().length() - 6) );
                        int modifieres = candidate.getModifiers();
                        if (Modifier.isAbstract(modifieres))
                            continue;
                        if (!Modifier.isPublic(modifieres))
                            continue;
						if (wanted_type.isAssignableFrom(candidate)) {
							vec.add(new NiceClass<T>((Class<? extends T>)candidate, f.getName()));
							num++;
						}
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
		}
        if (verbosity >= 1)
            System.out.println(OMP + getI18N(Options.class).getString("FoundWanted")
                .replaceAll("\\[NUMBER\\]", String.valueOf(num))
                .replaceAll("\\[WANTED\\]", getI18N(wanted_type).getString(wanted_type.getSimpleName())));
	}
	
	public final static String ignoreJars[] = { "xercesImpl.jar", "junit.jar", "jdbc-mysql.jar" };
    
	static void SearchForImplentations() {
	
		String pathes[] = System.getProperty("java.class.path").split(System.getProperty("path.separator", ":"));
        if (verbosity >= 1)
            System.out.println(OMP + getI18N(Options.class).getString("SearchImplementation"));
        for (String s : pathes) {
            File file = new File(s);
            if (file.isFile()) 
                investigateArchive(s, null);
            if (file.isDirectory()) 
                investigateDirectory(s);
        }
        for (Module module : modules.getModules())
            investigateArchive(module.getMain(), module);
	}

    static void investigateDirectory(String directoryPath) {
        if (verbosity >= 1)
            System.out.println(OMP + Options.getI18N(Options.class).getString("Investigating directory") + ": " + directoryPath); 
        investigateFeaturesDirectory(directoryPath);
        investigateMediaDirectory(directoryPath);
        investigateTagFinderDirectory(directoryPath);
        investigateFilterDirectory(directoryPath);
        investigateExportDirectory(directoryPath);
        investigateDatamanagementDirectory(directoryPath);
        investigateDesktopDirectory(directoryPath);
        investigateTemplateDirectory(directoryPath);
        investigateImportDirectory(directoryPath);
    }
    
    static void investigateArchive(String archivePath, Module module){
        try {
            if (verbosity >= 1)
                System.out.print(OMP + Options.getI18N(Options.class).getString("Investigating file:") + " " + archivePath); 
            for (String str : Options.ignoreJars)
                if (archivePath.endsWith(str)) {
                    if (verbosity >= 1)
                        System.out.println(" (" + Options.getI18N(Options.class).getString("will not be investigated") + ")");
                    return;
                }
            if (verbosity >= 1)
                System.out.println();
            JarFile jfile = new JarFile(archivePath);
            investigateArchive(jfile, module);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The properties contain the amount of time each element needs to start.
     * This allows to make a realistic startup progress bar.
     */
    static Properties prop = null;
    
    /**
     * Starting time of the startup
     */
    static long startTimeCode = -1;
    
    /**
     * The starting time of the last started element of the startup.
     * With this variable it will be possible to calculate how long
     * the an element of the startup took to compute.
     * It is only relevant for debuging. Since it is not used unless
     * in debug mode.
     */
    static long lastTimeCode;
    
    /**
     * Stores the amount of time since the starting time was needed
     * to arrive at a certain element. This information combined
     * with the total length of the startup allows to deduce the relative
     * amount of time each step needs.
     */
    static Map<String, Long> timeing = new LinkedHashMap<String, Long>();

    /**
     * Start the timeing for the progress bar.
     *
     */
    static protected void startTimeing() {
        // Allready running
        if (startTimeCode != -1)
            return;
        
        startTimeCode = lastTimeCode = System.currentTimeMillis();
        prop = new Properties();
        try {
            prop.load(new FileInputStream(USER_OPTION_DIR + "startupTimeing.prop"));
        } catch (Exception e) {
            if (DEBUG && verbosity > 1)
                System.out.println(e.getMessage());
            URL defaultStartupTimeingURL = Options.class.getResource("startupTimeing.prop");
            try {
                if (defaultStartupTimeingURL != null)
                    prop.load(defaultStartupTimeingURL.openStream());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * Make step in the progress bar
     * @param str The name of the step
     */
    static public void time(String str) {
        // Not running => do nothing
        if (startTimeCode == -1)
            return;
        
        long newTimeCode = System.currentTimeMillis();
        if (verbosity > 2)
            System.out.println("[Timeing] " + str + ": " +  (newTimeCode - lastTimeCode) + " ms");
        
        // Store the amount of time since the beginning of the startup
        timeing.put(str, (newTimeCode - startTimeCode));
        lastTimeCode = newTimeCode;

        Object o = prop.get(str);
        if (o ==  null)
            return;
        if (!(o instanceof String))
            return;
        try {
            double progress = Double.valueOf((String) o);
            SplashWindow.setProgress(progress);
        } catch (Exception e) {
            return;
        }
        
    }    
    
    /**
     * End the timeing for the progess bar.
     *  At this point the startup is considered as finished
     *
     */
    static public void endTimeing() {
        // Not running
        if (startTimeCode == -1)
            return;
        
        // Calculate the time it took for MeCat to start
        long all = System.currentTimeMillis() - startTimeCode;
        if (verbosity > 2)
            System.out.println("[Timeing] " + "All: " + all);

        try {
            // Clear previous startup times
            prop.clear();
            // Calculate all startup times.
            for (Map.Entry<String, Long> entry : timeing.entrySet()) {
                prop.put(entry.getKey(), "" + (((double)entry.getValue()) / all) );
            }
            // Write new startup times, they will be used as estimation for the next startup
            prop.store(new FileOutputStream(USER_OPTION_DIR + "startupTimeing.prop"), "There is no good reason to manipulate this file");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // We are finished calculating the startup
        // With startTimeCode set to -1
        // the function time will be out of commission
        startTimeCode = -1;
    }

    final static Modules modules;
    
	static {

        startTimeing();
        
        System.setOut(new PrintStream(System.out) {

            @Override
            public void print(String str) {
                int i = 0;
                if (str.contains("ToolTipText"))
                    i++;
                    
                super.print(str);
            }

            @Override
            public void println(String str) {
                int i = 0;
                if (str.contains("ToolTipText"))
                    i++;
                    
                super.println(str);
            }
            
            
        });
        
        addLocalListener(new LocalListener(){
            public void stateChanged(LocalListenerEvent event) {
                if (verbosity > 2)
                    System.out.println(OMP + "Locale = " + currentLocale.getDisplayLanguage(currentLocale) );
                filterTreeModel = new FilterTreeModel();
            }
        });

        time("Options: Add local listener");
        
		final String sep = System.getProperty("file.separator", "/");
		try {
			saveOptionConnection = new XMLConnection(new URL("file:" + USER_OPTION_DIR + "config.xml"));

        } catch (Exception e){
			e.printStackTrace();
			saveOptionConnection = null;
		}
        
        time("Options: Connection to option file");
        
        try {
            defaultImageInformationServerConnection = new XMLConnection(new URL("file:" + USER_OPTION_DIR + "images.xml"));

        } catch (Exception e){
            e.printStackTrace();
            defaultImageInformationServerConnection = null;
        }

        time("Options: Connection to image server file");

        Thread t = new Thread() { public void run() {
            Catalog imageCatalog = saveOptionFactory.openCatalog(defaultImageInformationServerConnection);
            if (imageCatalog != null)
                ImageInformationServer.getDefaultImageInformationServer().loadFrom(imageCatalog);
        }};
        t.start();
        
        time("Options: Start image server");
                
        loadOptions();
        
        time("Options: Load modules");
        
        modules = new Modules();
        
        time("Options: Application option");

        // Search in classpath for implementations of Features, Media and Datamanagement systems
        SearchForImplentations();

        time("Options: Search for implementation");

        Comparing comp;

		comp = new Comparing();
		comp.add(Title.getComparator());
		predefComparing.add(comp);
			
		comp = new Comparing();
		comp.add(Location.getComparator());
		comp.add(Position.getComparator());
		predefComparing.add(comp);

		comp = new Comparing();
		comp.add(Year.getComparator());
		predefComparing.add(comp);

		comp = new Comparing();
		comp.add(new ByMedium());
		predefComparing.add(comp);

        time("Options: Predefined comparing");
        
        // This has to be done after SearchForImplementations()
        // because it depends on the information gathered with SearchForImplementation()
        if (prefs != null) {
            if (verbosity >= 1)
                System.out.println(OMP + getI18N(Options.class).getString("Loading preferences"));
            PersistentThroughEntry pte =  Util.loadFromEntry(prefs);
            if (pte instanceof GlobalPreferences)
                AppPrefs = (GlobalPreferences) pte;
        }
        if (AppPrefs == null)
            AppPrefs = new GlobalPreferences();

        time("Options: Preferences of media and features");
        
        // Search for export profiles for the found exports
        findProfiles();
        if (verbosity >= 1)
            System.out.println(OMP + getI18N(Options.class).getString("FoundExportProfiles").replaceAll("\\[NUMBER\\]", String.valueOf(profiles.size())));

        time("Options: Export profiles");
        
        // Prepare templates
        for (Catalog template : Options.templateCatalogs) {
            templates.add(new Template(template));
        }
        
        time("Options: Templates for catalogs");

    }
    
    
    public static boolean ensureDirectory(String path) {
        return ensureDirectory(new File(path));
    }

    public static boolean ensureDirectory(File path) {
        if (path.exists() && !path.isDirectory()) {
            System.err.println(OMP + getI18N(Options.class).getString("FileNoDir").replaceAll("\\[FILE\\]", ""+path));
            if (DEBUG)
                new Exception().printStackTrace();
            return false;
        }
        if (!path.exists())
            if (!path.mkdirs()){
                System.err.println(OMP + getI18N(Options.class).getString("CouldNotCreateDir").replaceAll("\\[DIR\\]", ""+path));
                if (DEBUG)
                    new Exception().printStackTrace();
                return false;
            }
        return true;
    }

    public static boolean existsFilter(final Filter filter) {
         return existsVisitor(filter, filterTreeModel.getRoot());
    }
    
    public static boolean existsVisitor(final Filter filter, final Object node) {
        if (filterTreeModel.isLeaf(node)) {
            if (node instanceof Filter) {
                Filter f = ( Filter ) node;
                if (FilterUtils.equivalent(filter, f))
                    return true;
            }
            return false;
        }
        
        for (int i = 0; i < filterTreeModel.getChildCount(node); i++) 
            if (existsVisitor(filter, filterTreeModel.getChild(node, i)))
                return true;
        
        return false;
    }
    
    
    public static EntryFilter addRecentFilter(final Filter filter) {
        if (!existsFilter(filter)) {
            Entry entry = persistent.createOption("Filter");
            EntryFilter entryFilter = FilterUtils.copyFilter(filter, entry);
            recent_filters.add(entryFilter);
            return entryFilter;
        }
        
        return null;
    }

    public static void addRecentSorting(final ConfigurableComparator comparator) {
        // Don't store predefined comparings in recent comparings
        if (predefComparing.contains(comparator))
            return;
        
        recent_comparings.remove(comparator);
        if (recent_comparings.size() == 30)
            recent_comparings.remove(0);
        recent_comparings.add(comparator);
    }
}
