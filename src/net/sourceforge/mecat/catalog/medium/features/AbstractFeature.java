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
 * Created on Jul 16, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.medium.features;

import java.awt.BorderLayout;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sourceforge.mecat.catalog.datamanagement.Util;
import net.sourceforge.mecat.catalog.datamanagement.changelog.ChangeLog;
import net.sourceforge.mecat.catalog.gui.features.FeaturePanel;
import net.sourceforge.mecat.catalog.gui.features.desktop.FeatureDesktop;
import net.sourceforge.mecat.catalog.gui.options.OptionDialog;
import net.sourceforge.mecat.catalog.i18n.fromCatalog.LayeredResourceBundle;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalLabel;
import net.sourceforge.mecat.catalog.medium.Listing;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.listener.FeatureListener;
import net.sourceforge.mecat.catalog.medium.features.option.FeatureOption;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.preferences.AbstractPreferences;
import net.sourceforge.mecat.catalog.option.preferences.CatalogPreferences;
import net.sourceforge.mecat.catalog.option.preferences.GlobalPreferences;
import net.sourceforge.mecat.catalog.sort.ConfigurableComparator;
import net.sourceforge.mecat.srp.utils.NiceClass;

public abstract class AbstractFeature implements Feature {
	public Medium medium;
    // Get Resources for i18n
    LayeredResourceBundle res = Options.getI18N(getClass());

    public Medium getMedium() {
        return medium;
    }
    
    public LayeredResourceBundle getRes() {
        return res;
    }
    
    Vector<FeatureListener> featureListeners = new Vector<FeatureListener>();
    
    public void addFeatureListener(FeatureListener featureListener){
        featureListeners.add(featureListener);
    }
    
    public void removeFeatureListener(FeatureListener featureListener){
        featureListeners.remove(featureListener);
    }

    public void fireFeatureChanged() {
        for (FeatureListener featureListener: new Vector<FeatureListener>(featureListeners))
            featureListener.featureValueChanged(this);
    }
    
    /**
     * @deprecated
    public static boolean isOverridden(final Catalog catalog, final Class<? extends Feature> c) {
        // Look for an option Override to see if the feature will be overriden by the 
        // options in the catalog
        Entry override = catalog.getOption("Override");
        // If there are no override options, the feature is not overriden
        if (override == null)
            return false;
        String overrideFeature = override.getAttribute(Feature.getEasyClassName(c.getName()));
        // If there is no option for this feature or this feature is marked 
        // not to be overriden then we take the general options for this feature
        if (overrideFeature == null || !overrideFeature.equalsIgnoreCase("true"))
            return false;
        // At this point it should be clear that the options are special for this catalog 
        return true;
    }
     */
    
    /**
     * @deprecated
    public static void setOverridden(final Catalog catalog, final Class<? extends Feature> c, final boolean override) {
        // Look for an option Override to see if we use an already existing  entry
        Entry overrideEntry = catalog.getOption("Override");
        // If there are no override options yet, we need to create the Option
        if (overrideEntry == null)
            overrideEntry = catalog.createOption("Override");
        // Set the override Attribute for the feature to the given parameter.
        overrideEntry.setAttribute(Feature.getEasyClassName(c.getName()), ((override)?"true":"false"));
    }
     */
    
    
	public FeaturePanel getPanel(FeatureDesktop desktop) {
		return getPanel(desktop, true);
	}
	
    abstract public FeaturePanel getPanel(FeatureDesktop desktop, boolean border);
    
	public AbstractFeature(Medium medium) {
		this.medium = medium;
        if (medium.getListing() != null && !medium.getListing().listStaticInformation.containsKey(getClass()))
            medium.getListing().listStaticInformation.put(getClass(), getFreshStaticListingOption(medium.getListing()));
	}
	
    /**
     * @deprecated
	// TODO adjustment to i18n to allow new entrys during runtime
	public JPanel getOptionPanel() {
		// Make the "No Options Available Screen"
		JPanel ret = new JPanel();
		ret.setLayout(new BorderLayout());
		ret.add(new JLabel(res.getString("NoOptionsAvailable")));
		return ret;		
	}
    */
    
    /**
     * @deprecated
	abstract public boolean hasOptions();
     */
    
    
    
    /**
     * @deprecated
    static public boolean hasGeneralOptions() {
        return false;
    }
     */

    /**
     * @deprecated
    static public JPanel getGeneralOptionPanel(final Entry entry, final Class<? extends Feature> feature) {
        return null;
    }
     */
    
	
    /**
     * @deprecated
	static public boolean storeOptions(Entry entry){
		return false;
	}
     */

	static public ConfigurableComparator getComparator(){
		return null;
	}
	
//	public abstract boolean validate(String condition) throws BadCondition;
//	public abstract String getText();
//	public abstract String getShortText();
//    public abstract String getTextHTML();
//    public abstract String getShortTextHTML();


	// Helpfunktions that could be usefull for more then one implementation
	protected static String cutForEasyClassName = "net.sourceforge.mecat.catalog.medium.features.impl.";
	public static String getEasyClassName(Class<? extends Feature> feature) {
        return getEasyClassName(feature.getName());
    }
	public static String getEasyClassName(String name) {
		if (name.startsWith(cutForEasyClassName))
			return name.substring(cutForEasyClassName.length());
		return "#" + name + "#";
	}
	public static String getRealClassName(String name) {
		if (name.startsWith("#"))
			return name.substring(1, name.length()-1);
		return cutForEasyClassName + name;
	}

    /**
     * The StaticListingOption object should contain the non persistent 
     * information needed for the display work of every listing/catalog.
     * For persisten information the use of the option part of the 
     * catalog found trought medium.entry.catalog is suggested.
     * Since it is null at the beginning it should be initialised with
     * @link #setStaticListingOption(Object)
     * @return
     */
    public Object getStaticListingOption(){
        return medium.getListing().listStaticInformation.get(this.getClass());
    }
 
    /**
     * This function should be overriden if there are non persistent static
     * information required.
     * @param o
     * @return
     */
    public Object getFreshStaticListingOption(final Listing listing){
        return null;
    }

    public JPanel getFeatureOptionPanel(FeatureOption featureOption) {
        JPanel ret = new JPanel();
        ret.setLayout(new BorderLayout());
        ret.add(new SimpleLocalLabel(res, "NoOptionsAvailable"));
        return ret;     
    }
    
    public FeatureOption getFeatureOption() {
        return medium.getListing().getTotalPreferences().getFeaturesOption().getOption(this.getClass());
    }
    /*
    public FeatureOption getFeatureOption() {
        boolean overriden = medium.getListing().getPreferences().getFeaturesOption().isOverriden(this.getClass());
        
        AbstractPreferences preferences = null;
        if (overriden) {
            preferences = medium.getListing().getPreferences();
        } else {
            preferences = Options.AppPrefs;
        }
        return preferences.getFeaturesOption().options.get(this.getClass());
    }*/
    
    /**
     * This function should be overriden if there are persistent options.
     * @param o
     * @return
     * 
     * Persistent options now are held by the Preferences classes
     * and stored in resource.xml files.
     * They are accessed through the listing.
     * 
     * @deprecated
     */
    public FeatureOption getFreshFeatureOption(){
        return null;
    }
    
	abstract public void copyTo(Feature feature);
    
    /**
     * The standard implementation of {@link #copyToUseMapping(Feature, Map)}
     * will invoke {@link #copyTo}. This way only those features
     * that need a different implementation will override 
     * the function {@link #copyToUseMapping}. 
     * 
     * Every feature that uses a {@link #copyTo} from another medium or features
     * has to override the {@link #copyToUseMapping}. This is necessary because
     * the submedium or subfeatues would lose the no link information.
     */
    public void copyToUseMapping(Feature feature, Map<Medium, Map<Listing, Medium>> mapping) {
        copyTo(feature);
    }
    /**
     * This function returns true if the feature contains data
     * @return
     */
    abstract public boolean hasValue();






    public void showOptions() {
        AbstractPreferences pref = null;
        
        if (medium.getListing().getTotalPreferences().getGeneralOption().isUseCatalogOption())
            pref = Util.copyPTE(medium.getListing().getCatalogPreferences());
        else
            pref = Util.copyPTE(Options.AppPrefs);

        OptionDialog optionDialog = new OptionDialog(pref);
        optionDialog.setSelection(new NiceClass<Feature>(this.getClass()));
        optionDialog.setVisible(true);
        
        if (optionDialog.isAccepted()) {
            if (pref instanceof CatalogPreferences)
                medium.getListing().setPreferences( ( CatalogPreferences) pref);
            if (pref instanceof GlobalPreferences)
                Options.AppPrefs = ( GlobalPreferences ) pref;
        }
    }

    public Iterator< ? extends Medium> getSubMedia() {
        // Empty Iterator
        return new Iterator<Medium>(){
            public boolean hasNext() {  return false; }
            public Medium next() {      return null;  }
            public void remove() {}
        };
    }


    int transactionId = -1;
    
    public void startTransaction(String name, boolean atom, boolean userInvoked) {
        ChangeLog log = getChangeLog();
        if (log == null)
            return;
        transactionId = log.openTransaction(Options.getI18N(getClass()).getString(getClass().getSimpleName()) + ": " + name, atom, userInvoked);
    }

    public void stopTransaction() {
        ChangeLog log = getChangeLog();
        if (log == null)
            return;
        log.closeTransaction(transactionId);
    }
    
    public ChangeLog getChangeLog() {
        return getMedium().getListing().getChangeLog();
    }
    
    

}
