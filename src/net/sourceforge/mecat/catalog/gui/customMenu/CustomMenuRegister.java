/*
 *  MeCat - Media Catalog more information on
 *  http://mecat.sourceforge.net/
 *  Copyright (C) 2005, Stephan Richard Palm, All Rights Reserved.
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
 * Created on Jun 13, 2005
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.gui.customMenu;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import net.sourceforge.mecat.catalog.gui.MainFrameBackend;
import net.sourceforge.mecat.catalog.medium.Listing;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.preferences.AbstractMediaOption;

public class CustomMenuRegister {

	protected final static Map<Class<? extends Feature>, Set<MenuFactory>> features = new LinkedHashMap<Class<? extends Feature>, Set<MenuFactory>>();
	protected final static Map<Class<? extends Medium>, Set<MenuFactory>> media = new LinkedHashMap<Class<? extends Medium>, Set<MenuFactory>>();
	
    protected final static Set<MenuFactory> allTimeFeatures = new HashSet<MenuFactory>();
    protected final static Set<MenuFactory> allTimeMedia = new HashSet<MenuFactory>();
    
	public static void addFeatureMenu(final Class<? extends Feature> feature, final MenuFactory menu){
        if (!features.containsKey(feature))
            features.put(feature, new HashSet<MenuFactory>());
        features.get(feature).add(menu);
        if (menu.allwaysVisible())
            allTimeFeatures.add(menu);
	}
	public static void addMediumMenu(final Class<? extends Medium> medium, final MenuFactory menu){
        if (!media.containsKey(medium))
            media.put(medium, new HashSet<MenuFactory>());
        media.get(medium).add(menu);
        if (menu.allwaysVisible())
            allTimeMedia.add(menu);
	}
	
    public static JMenu getFeatureMenu(final Listing listing) {
        ResourceBundle menuResources = Options.getI18N(MainFrameBackend.class);
        // Get all features that can be reached from media in the listing
        Set<Class<? extends Medium>> mediumTypes = listing.getTypes();
        Set<Class<? extends Feature>> featureTypes = AbstractMediaOption.getFeatures(mediumTypes);
        Set<MenuFactory> factories = new HashSet<MenuFactory>();
        for (Class<? extends Feature> feature : featureTypes)
            if (features.containsKey(feature))
                factories.addAll(features.get(feature));
        // Add all features that must be shown anyway
        factories.addAll(allTimeFeatures);

        return getMenu(listing, factories, menuResources.getString("Features"));
    }
    
    public static JMenu getMediumMenu(final Listing listing) {
        ResourceBundle menuResources = Options.getI18N(MainFrameBackend.class);
        // Get all features that can be reached from media in the listing
        Set<Class<? extends Medium>> mediumTypes = listing.getTypes();
        Set<MenuFactory> factories = new HashSet<MenuFactory>();
        for (Class<? extends Medium> medium : mediumTypes)
            if (media.containsKey(medium))
                factories.addAll(media.get(medium));
        // Add all features that must be shown anyway
        factories.addAll(allTimeMedia);
        
        return getMenu(listing, factories, menuResources.getString("Media"));
    }
    
    protected static JMenu getMenu(Listing listing, Set<MenuFactory> factories, String title) {
        // Create Menus from factories
        List<JMenuItem> menus = new Vector<JMenuItem>();
        for (MenuFactory factory : factories)
            menus.add(factory.createMenu(listing));
        // Sort menus by name
        Collections.sort(menus, new Comparator<JMenuItem>(){
            public int compare(JMenuItem menu0, JMenuItem menu1) {
                return menu0.getText().compareTo(menu1.getText());
            }
        });
        
        // Merge menu
        JMenu menu = new JMenu(title);
        for (JMenuItem menuItem : menus)
            menu.add(menuItem);
        
        return menu;
    }
    
/*	private static JMenuItem getFeatureMenu(final Class<? extends Feature> feature, final Listing listing) {
        if (features.get(feature) != null)
            return features.get(feature).createMenu(listing);
        return null;
	}
	
	private static JMenuItem getMediumMenu(final Class<? extends Medium> medium, final Listing listing) {
		JMenuItem ret = media.get(medium).createMenu(listing);
		try {
			Medium dummy = medium.getConstructor(new Class[]{Entry.class, Listing.class}).newInstance(new Object[]{null, null});
			for (Feature feature : dummy.getFeatures())
                if (features.get(feature) != null)
                    ret.add(features.get(feature.getClass()).createMenu(listing));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}		
		return ret;
	}*/
	

}
