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
 * Created on Oct 18, 2005
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.i18n.fromCatalog;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;

import net.sourceforge.mecat.catalog.option.Options;


public class LayeredResourceBundle extends ResourceBundle {
    
    final Vector<CatalogResource> bundles;
    final Map<String, CatalogResource> mapKeyBundle = new HashMap<String, CatalogResource>();
    Enumeration<String> keys;
    
    
    public LayeredResourceBundle(final LayeredResourceBundle... layeredResourceBundles){
        bundles = new Vector<CatalogResource>();
        for (LayeredResourceBundle layeredResourceBundle : layeredResourceBundles)
            bundles.addAll(layeredResourceBundle.bundles);
        for (CatalogResource bundle : bundles) 
            addBundle(bundle);
        keys = Collections.enumeration(mapKeyBundle.keySet());
    }
    
    public LayeredResourceBundle(final Collection<CatalogResource> bundles) {
        this.bundles = new Vector<CatalogResource>(bundles);
        for (CatalogResource bundle : bundles) 
            addBundle(bundle);
        keys = Collections.enumeration(mapKeyBundle.keySet());
    }

    public void add(final CatalogResource bundle) {
        bundles.add(bundle);
        addBundle(bundle);
        keys = Collections.enumeration(mapKeyBundle.keySet());
    }
    
    public void add(final LayeredResourceBundle layeredResourceBundle) {
        bundles.addAll(layeredResourceBundle.bundles);
        for (CatalogResource bundle : layeredResourceBundle.bundles) 
            addBundle(bundle);
        keys = Collections.enumeration(mapKeyBundle.keySet());
    }
    
    private void addBundle(final CatalogResource bundle) {
        for (String key : Collections.list(bundle.getKeys()))
            if (!mapKeyBundle.containsKey(key))
                mapKeyBundle.put(key, bundle);
    }

    public Vector<CatalogResource> getBundles() {
        return bundles;
    }
    
    public LayeredResourceBundle getBundle(Locale locale) {
        Vector<CatalogResource> localizedBundles = new Vector<CatalogResource>();
        for (CatalogResource bundle : bundles)
            localizedBundles.add(bundle.getBundle(locale));
        return new LayeredResourceBundle(localizedBundles);
    }
    
    @Override
    protected Object handleGetObject(String key) {
        if (mapKeyBundle.get(key) == null) {
            if (key != CatalogResource.NOKEY && key != CatalogResource.NOTRANS) 
                System.err.println(Options.getI18N(CatalogResource.class).getString(CatalogResource.NOKEY).replaceAll("\\[KEY\\]", key));
            if (Options.DEBUG)
                (new Exception()).printStackTrace();
            return key;
        }
        return mapKeyBundle.get(key).handleGetObject(key);
    }

    @Override
    public Enumeration<String> getKeys() {
        return keys;
    }
    
    public Set<String> getKeySet() {
        return mapKeyBundle.keySet();
    }
}
