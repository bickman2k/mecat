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
 * Created on Aug 13, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.datamanagement;

import java.awt.Component;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;

import net.sourceforge.mecat.catalog.medium.Listing;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.preferences.CatalogPreferences;

public class Util {
    
    final static int currentVersion = 1;
    
    public static CatalogPreferences getCatalogPreferences(Catalog catalog) {
        Entry prefEntry = catalog.getOption("Preferences");
        if (prefEntry != null)
            return (CatalogPreferences) Util.loadFromEntry(prefEntry);
        else
            return new CatalogPreferences();
    }
    
    
    public static <T extends PersistentThroughEntry> T copyPTE(final T source) {
        Catalog tempCatalog = Options.stdFactory.createCatalog((Component)null);
        Entry entry = tempCatalog.createOption("TempEntry");
        Util.saveToEntry(source, entry);
        return (T) Util.loadFromEntry(entry);
    }
    
    public static void copyCatalog(final Catalog source, final Catalog destination){
        copyCatalog(source, destination, true);
    }
    public static void copyCatalog(final Catalog source, final Catalog destination, final boolean copyConnection){
        
//        // Copy the language selected for the catalog
//        if (source.getLanguage() != null)
//            destination.setLanguage(source.getLanguage());
//        
//        // Copy the description for every available language
//        for (Locale locale : Locale.getAvailableLocales())
//            if (source.getDescription(locale) != null)
//                destination.setDescription(source.getDescription(locale), locale);
//        
//        // Copy the name for every available language
//        for (Locale locale : Locale.getAvailableLocales())
//            if (source.getName(locale) != null)
//                destination.setName(source.getName(locale), locale);
        // Copy the general Information 
        copyEntry(source.getGeneralInformationEntry(), destination.getGeneralInformationEntry());
        
        // Copy every entry in the catalog
        for (Iterator<? extends Entry> i = source.getIterator(); i.hasNext();) {
            Entry src_entry = i.next();
            Entry dest_entry = destination.createEntry(src_entry.getTypeClassName());
            copyEntry(src_entry, dest_entry);            
        }

        // Copy every option in the catalog
        for (Iterator<? extends Entry> i = source.getOptionIterator(); i.hasNext();) {
            Entry src_entry = i.next();
            Entry dest_entry = destination.createOption(src_entry.getTypeClassName());
            copyEntry(src_entry, dest_entry);            
        }
        
        if (copyConnection)
            destination.setConnection(source.getConnection());
    }
    
    public static void initCatalog(final Catalog catalog) {
        catalog.setVersion(currentVersion);
    }
    
    public static void updateCatalog(final Catalog catalog) {
        if (catalog.getVersion() == 0) {
            int changes = updateClassNames(catalog, new String[][]{
                    {"net.sourceforge.mecat.catalog.medium.features.TextFeatureValidator", 
                     "net.sourceforge.mecat.catalog.medium.features.validators.TextFeatureValidator"},
                    {"net.sourceforge.mecat.catalog.medium.features.ChoiceFeatureValidator", 
                     "net.sourceforge.mecat.catalog.medium.features.validators.ChoiceFeatureValidator"},
                    {"net.sourceforge.mecat.catalog.medium.features.AbstractChoiceFeature$ChoiceFeatureOption", 
                     "net.sourceforge.mecat.catalog.medium.features.option.ChoiceFeatureOption"},
                    {"net.sourceforge.mecat.catalog.medium.features.ImageFeature$ImageFeatureOption", 
                     "net.sourceforge.mecat.catalog.medium.features.option.ImageFeatureOption"},
                    {"net.sourceforge.mecat.catalog.medium.features.MultiImageFeature$MultiImageFeatureOption", 
                     "net.sourceforge.mecat.catalog.medium.features.option.MultiImageFeatureOption"},
                    {"net.sourceforge.mecat.catalog.medium.features.Languages$LanguagesFeatureOption", 
                     "net.sourceforge.mecat.catalog.medium.features.option.LanguagesFeatureOption"},
                    {"net.sourceforge.mecat.catalog.medium.features.impl.IMDBTitleNumber$IMDBFeatureOption", 
                     "net.sourceforge.mecat.catalog.medium.features.option.impl.IMDBFeatureOption"},
                    {"net.sourceforge.mecat.catalog.medium.features.impl.ImplRomFileList$RomFileListFeatureOption", 
                     "net.sourceforge.mecat.catalog.medium.features.option.impl.RomFileListFeatureOption"}
            });
            catalog.setVersion(1);
            
            if (Options.verbosity > 0)
                if (changes == 1)
                    System.out.println(Options.getI18N(Util.class).getString("Changed one ClassName in the catalog while transforming to version [VERSION].")
                            .replaceAll("\\[VERSION\\]", ""+1));
                else if (changes > 1)
                    System.out.println(Options.getI18N(Util.class).getString("Changed [NUMBER] of ClassNames in the catalog while transforming to version [VERSION].")
                            .replaceAll("\\[NUMBER\\]", ""+changes)
                            .replaceAll("\\[VERSION\\]", ""+1));
        }
    }
    
    /**
     * Updates all classnames with the given mapping and returns the amount of changes done.
     * The mapping has the form [n][2]. Where [n][0] is the source and [n][1] the destination.
     * Any [n][0] in an attribue ClassName is changed to [n][1] for every n.
     * 
     * 
     * @param catalog The catalog that shall be changed
     * @param mapping The mapping that will be used
     * @return the amount of changes.
     */
    public static int updateClassNames(final Catalog catalog, final String[][] mapping) {
        int ret = 0;
        
        // Search in every entry
        for (Iterator<? extends Entry> i = catalog.getIterator(); i.hasNext();) {
            Entry entry = i.next();
            ret += updateClassNames(entry, mapping);
        }

        // Search in every option
        for (Iterator<? extends Entry> i = catalog.getOptionIterator(); i.hasNext();) {
            Entry entry = i.next();
            ret += updateClassNames(entry, mapping);
        }
        
        return ret;
    }
    
    public static int updateClassNames(final Entry entry, final String[][] mapping) {
        int ret = 0;
        
        for (Entry sub : entry.getSubEntries())
            ret += updateClassNames(sub, mapping);

        String className = entry.getAttribute("ClassName");
        if (className == null)
            return ret;
        
        for (String[] strs : mapping) 
            if (className.trim().equals(strs[0])) {
                entry.setAttribute("ClassName", strs[1]);
                if (Options.verbosity > 1)
                    System.out.println(strs[0] + " => " + strs[1]);
                return ret + 1;
            }
        
        return ret;
    }
    
    public static Entry copyEntry(final Entry source) {
        Catalog tempCatalog = Options.stdFactory.createCatalog((Component)null);
        Entry entry = tempCatalog.createOption(source.getTypeClassName());
        Util.copyEntry(source, entry);
        return entry;
    }
    
    public static void copyEntry(final Entry source, final Entry destination) {
        
        // Copy all attributes that contain a single value.
        Set<String> singleAttributes = source.getAttributes();
        for (String name : singleAttributes) {
            Set<Locale> langs = source.getAttributeLanguages(name);
            for (Locale lang : langs) {
                String val = source.getAttribute(name, lang);
                if (val != null)
                    destination.setAttribute(name, val, lang);
            }
            String val = source.getAttribute(name);
            if (val != null)
                destination.setAttribute(name, val);
        }

        // Copy all attributes that contain a multiple value.
        Set<String> multiAttributes = source.getSetAttributes();
        for (String name : multiAttributes) {
            Set<Locale> langs = source.getSetAttributeLanguages(name);
            for (Locale lang : langs) {
                Iterator<String> vals = source.getSetIterator(name, lang);
                while (vals.hasNext()) {
                    String val = vals.next();
                    destination.addSetAttribute(name, val, lang);
                }
            }
            Iterator<String> vals = source.getSetIterator(name);
            while (vals.hasNext()) {
                String val = vals.next();
                destination.addSetAttribute(name, val);
            }
        }

        
        // Copy sub entries from the source entry to the destination entry.
        List<? extends Entry> subs = source.getSubEntries();
        for (Entry src_entry : subs) {
            // Create a new sub entry on the destination entry for every sub entry on the source entry
            Entry dest_entry = destination.createSubEntry(src_entry.getTypeClassName());
            // Copy the information from the sub entry of source to the sub entry of the destination
            copyEntry(src_entry, dest_entry);
        }
    }
    

	public class UnknownType extends Exception {
		
	}
	
	static public class Argument {
		public Argument(int Number, Class Type, Object Value) {
			this.Number = Number;
			if (Type != null)
				this.Type = Type;
			else
				this.Type = Value.getClass();
			
			this.Value = Value;
		}
		protected Argument(){}
		public int Number;
		public Class Type;
		public Object Value;
	}
	
	public static PersistentThroughEntry loadFromEntry(Entry entry){
		Class[] args_Class;
		Object[] args_Values;
		if (getInteger(entry.getAttribute("ArgumentNumber")) != null) {
			List<? extends Entry> args = entry.getSubEntries("Argument");		
			args_Class = new Class[getInteger(entry.getAttribute("ArgumentNumber")).intValue()];
			args_Values = new Object[getInteger(entry.getAttribute("ArgumentNumber")).intValue()];
			for (Entry e : args) {
				Argument arg = restoreArgument(e);
				args_Class[arg.Number] = arg.Type;
				args_Values[arg.Number] = arg.Value;
			}
		} else
		{
			args_Class = new Class[0];
			args_Values = new Object[0];
		}
		
		
		PersistentThroughEntry persist;
		try {
			persist = (PersistentThroughEntry)(Class.forName(entry.getAttribute("ClassName")).getConstructor(args_Class).newInstance(args_Values));
		} catch (InstantiationException e) {
            e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
            e.printStackTrace();
			return null;
		} catch (ClassNotFoundException e) {
            e.printStackTrace();
			return null;
		} catch (NoSuchMethodException e) {
            e.printStackTrace();
			return null;
		} catch (InvocationTargetException e) {
            e.printStackTrace();
			return null;
		}

		if (!persist.loadFromEntry(entry))
			return null;
		
		return persist;
	}
	
	public static void saveToEntry(PersistentThroughEntry pte, Entry con){
		con.setAttribute("ClassName", pte.getClass().getName());
		pte.saveToEntry(con);
	}


	static private void increaseArgumentNumber(Entry entry) {
		Integer prev = getInteger(entry.getAttribute("ArgumentNumber"));
		if (prev == null) {
			entry.setAttribute("ArgumentNumber", "1");
			return;
		}

		entry.setAttribute("ArgumentNumber", String.valueOf(prev.intValue() + 1));
			
	}
	
	static public boolean addArgument(Entry entry, Argument arg) {
		if (entry == null)
			return false;
		if (arg == null)
			return false;
		
		Entry e_arg = entry.createSubEntry("Argument");
		e_arg.setAttribute("Number", String.valueOf(arg.Number));
		e_arg.setAttribute("Type", arg.Type.getName());

		if (arg.Value == null) {
			increaseArgumentNumber(entry);
			return true;
		}
		
		// The two lines actualy should do the same but the second
		// is simpler, at least i think so, but with the first line
		// one has the posibility to store something to a different
		// type then it is.
		if (PersistentThroughEntry.class.isAssignableFrom(arg.Type)) {
//		if (arg.Value instanceof PersistentThroughEntry) {
			Entry instance = e_arg.createSubEntry("Instance");
			PersistentThroughEntry pte = (PersistentThroughEntry)arg.Value;
			saveToEntry(pte, instance);
			
			increaseArgumentNumber(entry);
			return true;
		}
		
		if (Enum.class.isAssignableFrom(arg.Type)) {
			e_arg.setAttribute("Value", arg.Value.toString());
			increaseArgumentNumber(entry);
			return true;
		}
		
		if ((arg.Type.getName().compareToIgnoreCase("java.net.URL") == 0)
		        || (arg.Type.getName().compareToIgnoreCase("java.lang.String") == 0)
		        || (arg.Type.getName().compareToIgnoreCase("java.lang.Integer") == 0)
		        || (arg.Type.getName().compareToIgnoreCase("java.lang.Long") == 0)
		        || (arg.Type.getName().compareToIgnoreCase("java.lang.Float") == 0)
		        || (arg.Type.getName().compareToIgnoreCase("java.lang.Double") == 0)
		        || (arg.Type.getName().compareToIgnoreCase("java.lang.Boolean") == 0)
		        || (arg.Type.getName().compareToIgnoreCase("java.lang.String") == 0)){
			e_arg.setAttribute("Value", arg.Value.toString());
			increaseArgumentNumber(entry);
			return true;
		}
//		if (arg.Type.getName().compareToIgnoreCase("java.lang.String") == 0) {
//			e_arg.setAttribute("Value", arg.Value.toString());
//			increaseArgumentNumber(entry);
//			return true;
//		}
//		if (arg.Type.getName().compareToIgnoreCase("java.lang.Integer") == 0) {
//			e_arg.setAttribute("Value", arg.Value.toString());
//			increaseArgumentNumber(entry);
//			return true;
//		}
//		if (arg.Type.getName().compareToIgnoreCase("java.lang.Boolean") == 0) {
//			e_arg.setAttribute("Value", arg.Value.toString());
//			increaseArgumentNumber(entry);
//			return true;
//		}
		if (arg.Type.getName().compareToIgnoreCase("java.lang.Class") == 0) {
			e_arg.setAttribute("Value", ((Class)(arg.Value)).getName());
			increaseArgumentNumber(entry);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Restores an Argument from an Entry
	 * <ol>
	 * <li> {@link java.lang.String} </li>
	 * <li> {@link java.lang.Boolean} </li>
	 * <li> {@link java.lang.Integer} </li>
	 * <li> {@link java.net.URL} </li>
	 * </ol>
	 * @param arg
	 * @return the restored argument
	 */
	static private Argument restoreArgument(Entry arg) {
		Argument ret = new Argument();
		
		if (arg == null)
			return null;
		if (arg.getAttribute("Number") == null)
			return null;
		if (arg.getAttribute("Type") == null)
			return null;
		if (getInteger(arg.getAttribute("Number")) == null)
			return null;
		ret.Number = getInteger(arg.getAttribute("Number")).intValue();
		try {
			ret.Type = Class.forName(arg.getAttribute("Type"));
		} catch (ClassNotFoundException e) {
			return null;
		}

		Class type = null;
		
		try {
			type = Class.forName(arg.getAttribute("Type"));
		} catch (ClassNotFoundException e) {}

		// HIHI now it is recursive.
		if (PersistentThroughEntry.class.isAssignableFrom(type)) {
			ret.Value = Util.loadFromEntry(arg.getSubEntry("Instance"));
			return ret;
		}
		
		if (Enum.class.isAssignableFrom(type)) {
			try {
//				ret.Value = type.getConstructor(new Class[]{String.class}).newInstance(new Object[]{arg.getAttribute("Value")});
                ret.Value = Enum.valueOf((Class<? extends Enum>)type, arg.getAttribute("Value"));
				return ret;
			} catch (Exception e) {
				return null;
			}
		}
		
		if (arg.getAttribute("Type").compareTo("java.net.URL") == 0) 
		{
			ret.Value = getURL(arg.getAttribute("Value"));
			return ret;
		}
		if (arg.getAttribute("Type").compareTo("java.lang.String") == 0) 
		{	
			ret.Value = arg.getAttribute("Value");
			return ret;
		}
		if (arg.getAttribute("Type").compareTo("java.lang.Integer") == 0) 
		{	
			ret.Value = getInteger(arg.getAttribute("Value"));
			return ret;
		}
        if (arg.getAttribute("Type").compareTo("java.lang.Long") == 0) 
        {   
            ret.Value = getLong(arg.getAttribute("Value"));
            return ret;
        }
        if (arg.getAttribute("Type").compareTo("java.lang.Float") == 0) 
        {   
            ret.Value = getFloat(arg.getAttribute("Value"));
            return ret;
        }
        if (arg.getAttribute("Type").compareTo("java.lang.Double") == 0) 
        {   
            ret.Value = getDouble(arg.getAttribute("Value"));
            return ret;
        }
		if (arg.getAttribute("Type").compareTo("java.lang.Boolean") == 0) 
		{	
			ret.Value = getBoolean(arg.getAttribute("Value"));
			return ret;
		}
		if (arg.getAttribute("Type").compareTo("java.lang.Class") == 0) 
		{	
			ret.Value = getClass(arg.getAttribute("Value"));
			return ret;
		}
		
		// Type is unknown.
		return null;
/*		if (arg.getAttribute("ClassName").compareTo("") == 0) 
		{	
			return ret;
		}
		if (arg.getAttribute("ClassName").compareTo("") == 0) 
		{	
			return ret;
		}
		if (arg.getAttribute("ClassName").compareTo("") == 0) 
		{	
			return ret;
		}
		if (arg.getAttribute("ClassName").compareTo("") == 0) 
		{	
			return ret;
		}
		if (arg.getAttribute("ClassName").compareTo("") == 0) 
		{	
			return ret;
		}
		if (arg.getAttribute("ClassName").compareTo("") == 0) 
		{	
			return ret;
		}
		if (arg.getAttribute("ClassName").compareTo("") == 0) 
		{	
			return ret;
		}
		if (arg.getAttribute("ClassName").compareTo("") == 0) 
		{	
			return ret;
		}
		if (arg.getAttribute("ClassName").compareTo("") == 0) 
		{	
			return ret;
		}
		if (arg.getAttribute("ClassName").compareTo("") == 0) 
		{	
			return ret;
		}
		if (arg.getAttribute("ClassName").compareTo("") == 0) 
		{	
			return ret;
		}
		if (arg.getAttribute("ClassName").compareTo("") == 0) 
		{	
			return ret;
		}
		if (arg.getAttribute("ClassName").compareTo("") == 0) 
		{	
			return ret;
		}
		if (arg.getAttribute("ClassName").compareTo("") == 0) 
		{	
			return ret;
		}
		if (arg.getAttribute("ClassName").compareTo("") == 0) 
		{	
			return ret;
		}
		if (arg.getAttribute("ClassName").compareTo("") == 0) 
		{	
			return ret;
		}
		if (arg.getAttribute("ClassName").compareTo("") == 0) 
		{	
			return ret;
		}
		if (arg.getAttribute("ClassName").compareTo("") == 0) 
		{	
			return ret;
		}*/
		
	}

	static private Class getClass(String s) {
		try {
			return Class.forName(s);
		} catch (Exception e) {
			return null;
		}
	}
	
	static private URL getURL(String s) {
		if (s == null)
			return null;
		try {
			return new URL(s);
		} catch (MalformedURLException e) {
			return null;
		}
	}
	
	static private Integer getInteger(String s)
	{
		if (s == null)
			return null;
		try {
			return Integer.valueOf(s);
		} catch (Exception e)
		{ 
			return null;
		}
	}
	
    static private Long getLong(String s)
    {
        if (s == null)
            return null;
        try {
            return Long.valueOf(s);
        } catch (Exception e)
        { 
            return null;
        }
    }
    
    static private Float getFloat(String s)
    {
        if (s == null)
            return null;
        try {
            return Float.valueOf(s);
        } catch (Exception e)
        { 
            return null;
        }
    }
    
    static private Double getDouble(String s)
    {
        if (s == null)
            return null;
        try {
            return Double.valueOf(s);
        } catch (Exception e)
        { 
            return null;
        }
    }
    
	static private Boolean getBoolean(String s)
	{
		if (s == null)
			return null;
		try {
			return new Boolean(s.compareToIgnoreCase("TRUE") == 0);
		} catch (Exception e)
		{ 
			return null;
		}
	}
	
	
	
	
	
	
}
