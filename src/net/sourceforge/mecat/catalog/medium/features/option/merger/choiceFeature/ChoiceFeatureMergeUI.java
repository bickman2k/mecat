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
 * Created on Sep 14, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.medium.features.option.merger.choiceFeature;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.swing.JComponent;

import net.sourceforge.mecat.catalog.datamanagement.Util;
import net.sourceforge.mecat.catalog.i18n.fromCatalog.CatalogResource;
import net.sourceforge.mecat.catalog.medium.Listing;
import net.sourceforge.mecat.catalog.medium.features.AbstractChoiceFeature;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.medium.features.option.ChoiceFeatureOption;
import net.sourceforge.mecat.catalog.medium.features.option.merger.AbstractMergeUserInterface;
import net.sourceforge.mecat.catalog.medium.features.option.merger.MergeType;
import net.sourceforge.mecat.catalog.medium.features.option.merger.MergeUserInterfaceStatus;

public class ChoiceFeatureMergeUI extends AbstractMergeUserInterface<ChoiceFeatureOption> {

    // The option everything depends on
    // we keep them to be able to restore the default
    final ChoiceFeatureOption option1;
    final ChoiceFeatureOption option2;

    // Calculate the intersection
    Vector<String> keysIntersection = null;

    // Get the languages for the translations
    Vector<String> langs1 = null;
    Vector<String> langs2 = null;

    Vector<String> langsAll = null;
    
    // Languages only in option 2
    Vector<String> langsOnly2 = null;
    
    // Get direct access to catalogs for easier programm code
    CatalogResource res1 = null;
    CatalogResource res2 = null;
    
    // Get keys for both options
    Vector<String> keys1 = null;
    Vector<String> keys2 = null;

    public Map<Locale, Map<String, ChoiceConflictType>> conflictsTranslation = null;
    public Map<String, ChoiceConflictType> conflictsKey = null;
    
    // The source of the keys
    public Map<String, Vector<KeySource>> keySourcesMap = null;
    
    public ChoiceFeatureMergeUI(final ChoiceFeatureOption option1, final ChoiceFeatureOption option2) {
        this.option1 = option1;
        this.option2 = option2;

        // The user can go further if he wants
        this.setStatus(MergeUserInterfaceStatus.Finished);
        
        init();
    }
    
    
    
    protected void init() {
        // This should not be atempted, because there is no reason,
        // but still I want to be on safe side
        if ((option1 == null) && (option2 == null)) {
            setType(MergeType.NO_CONFLICT);
            setMergeResult(null);
            return;
        }
        
        // If option 1 is empty then option 2 is the merge option
        if (option1 == null){
            setType(MergeType.NO_CONFLICT);
            setMergeResult(Util.copyPTE(option2));
            return;
        }

        // If option 2 is empty then option 1 is the merge option
        if (option2 == null){
            setType(MergeType.NO_CONFLICT);
            setMergeResult(Util.copyPTE(option1));
            return;
        }

        // Get direct access to catalogs for easier programm code
        res1 = option1.getCatalogResource();
        res2 = option2.getCatalogResource();
        
        // Get keys for both options
        keys1 = res1.keys;
        keys2 = res2.keys;

        // If there are no registered keys for option 1 then take option 2
        if (keys1.isEmpty()){
            setType(MergeType.NO_CONFLICT);
            setMergeResult(Util.copyPTE(option2));
            return;
        }
        
        // If there are no registered keys for option 2 then take option 1
        if (keys2.isEmpty()){
            setType(MergeType.NO_CONFLICT);
            setMergeResult(Util.copyPTE(option1));
            return;
        }
        
        // Calculate the intersection
        keysIntersection = new Vector<String>(keys1);
        keysIntersection.retainAll(keys2);

        // Get the languages for the translations
        langs1 = res1.all_langs;
        langs2 = res2.all_langs;
        langsAll = new Vector<String>(langs1);
        langsAll.addAll(langs2);
        
        // Languages only in option 2
        langsOnly2 = new Vector<String>(langs2);
        langsOnly2.removeAll(langs1);
        
        getMerging();
    }

    
    protected void getMerging() {

        // Initialize the conflicts maps
        conflictsTranslation = new HashMap<Locale, Map<String, ChoiceConflictType>>();
        conflictsKey = new HashMap<String, ChoiceConflictType>();
        
        for (String lang : langsAll) 
            conflictsTranslation.put(new Locale(lang), new HashMap<String, ChoiceConflictType>());
       
        
        // Set default ChoiceConflictType for every entry
        for (String key : keys1) {
            conflictsKey.put(key, ChoiceConflictType.NoConflict);
            for (String lang : langsAll) 
                conflictsTranslation.get(new Locale(lang)).put(key, ChoiceConflictType.NoConflict);
        }
        for (String key : keys2) {
            conflictsKey.put(key, ChoiceConflictType.NoConflict);
            for (String lang : langs1) 
                conflictsTranslation.get(new Locale(lang)).put(key, ChoiceConflictType.NoConflict);
            for (String lang : langsOnly2) 
                conflictsTranslation.get(new Locale(lang)).put(key, ChoiceConflictType.NoConflict);
        }
        
        // Initialize key sources map
        keySourcesMap = new HashMap<String, Vector<KeySource>>();
        
        for (final String key : res1.keys) {
            Vector<KeySource> vec = new Vector<KeySource>();
            keySourcesMap.put(key, vec); 
            vec.add(new KeySource(option1, key));
        }
        for (final String key : res2.keys) {
            Vector<KeySource> vec = keySourcesMap.get(key);
            if (vec == null) {
                vec = new Vector<KeySource>();
                keySourcesMap.put(key, vec); 
            }
            vec.add(new KeySource(option2, key));
        }
        
        
        // This flag indicates if there has been any trouble
        // i.e. if there is a key with different translations for option 1 and option 2
        boolean conflict = false;

        ChoiceFeatureOption retOption = Util.copyPTE(option1);
        CatalogResource retRes = retOption.getCatalogResource();

        // Add languages that are only 2
        for (String lang : langsOnly2)
            retRes.addLanguage(new Locale(lang));

        for (String key : keys2) {
            // Add key from option 2 if it is only in Option 2
            if (!keysIntersection.contains(key)) {
                retRes.addKey(key);
            } else {
                // We assume a harmless conflict unless we know better
                conflictsKey.put(key, ChoiceConflictType.HarmlessConflict);
                for (String lang : langsAll) 
                    conflictsTranslation.get(new Locale(lang)).put(key, ChoiceConflictType.HarmlessConflict);
            }

            // Add all the translations for the key
            for (String lang : langs2) {
                Locale l = new Locale(lang);
                // If the key exists in both options for this language
                if (keysIntersection.contains(key) && !langsOnly2.contains(lang))
                    // check if it is the same
                    if (!res1.translations.get(l).get(key).equals(res2.translations.get(l).get(key))) {
                        // If its not the same the result was not easy solvable
                        conflict = true;
                        conflictsTranslation.get(l).put(key, ChoiceConflictType.BadConflict);
                        // Now we know that the key has a bad conflict
                        conflictsKey.put(key, ChoiceConflictType.BadConflict);
                        // don't override the original setting from the current catalog
                        // such that the default is to keep the setting from the current catalog
                        continue;
                    }
                retRes.setTranslation(key, new Locale(lang), res2.translations.get(new Locale(lang)).get(key));
            }
        }

        // If the languages are the same and no different translations for one key exists then its EASY SOLVABLE
        if (!conflict && langs1.size() == langs2.size() && langsOnly2.isEmpty()) 
            setType(MergeType.EASY_SOLVABLE_CONFLICT);
        else
            // The other case it is solvable but not easy
            setType(MergeType.SOLVABLE_CONFLICT);
        
        setMergeResult(Util.copyPTE(retOption));
    }
    
    public void applyChangeToCatalog1(Listing listing, Class<? extends Feature> featureClass) {
        // if keySourcesMap is null then 
        // there was no need for any key change
        // therefore return
        if (keySourcesMap == null)
            return;
        
        // A mapping from old keys to new keys
        Map<String, String> map = new HashMap<String, String>();

        for (Map.Entry<String, Vector<KeySource>> entry : keySourcesMap.entrySet())
            for (KeySource source : entry.getValue()) {
                // We don't need to consider the keys from option 2
                if (source.getSource() == option2)
                    continue;
                
                // If the keys are the same we don't need to map them
                if (source.getKey().equals(entry.getKey()))
                    continue;
                
                map.put(source.getKey(), entry.getKey());
            }

        for (Feature feature : listing.getAllFeature(featureClass)) {
            if (!(feature instanceof AbstractChoiceFeature)) {
                (new Exception("" + feature.getClass().getName())).printStackTrace();
                return;
            }
            AbstractChoiceFeature choice = (AbstractChoiceFeature) feature;
            
            choice.moveKeys(map);
        }
        
    }



    public void applyChangeToCatalog2(Listing listing, Class<? extends Feature> featureClass) {
        // if keySourcesMap is null then 
        // there was no need for any key change
        // therefore return
        if (keySourcesMap == null)
            return;
        
        // A mapping from old keys to new keys
        Map<String, String> map = new HashMap<String, String>();

        for (Map.Entry<String, Vector<KeySource>> entry : keySourcesMap.entrySet())
            for (KeySource source : entry.getValue()) {
                // We don't need to consider the keys from option 1
                if (source.getSource() == option1)
                    continue;
                
                // If the keys are the same we don't need to map them
                if (source.getKey().equals(entry.getKey()))
                    continue;
                
                map.put(source.getKey(), entry.getKey());
            }

        for (Feature feature : listing.getAllFeature(featureClass)) {
            if (!(feature instanceof AbstractChoiceFeature)) {
                (new Exception("" + feature.getClass().getName())).printStackTrace();
                return;
            }
            AbstractChoiceFeature choice = (AbstractChoiceFeature) feature;
            
            choice.moveKeys(map);
        }
    }

    public void addKey(final KeySource source, String newKey, int index) {
        ChoiceFeatureOption retOption = getMergeResult();
        CatalogResource retRes = retOption.getCatalogResource();

        // Abbreviation
        CatalogResource sourceRes = source.getSource().getCatalogResource();

        // We assume a harmless conflict unless we know better
        conflictsKey.put(newKey, ChoiceConflictType.RenamedKey);
        for (String lang : langsAll) 
            conflictsTranslation.get(new Locale(lang)).put(newKey, ChoiceConflictType.RenamedKey);

        // Add the key
        retRes.addKey(newKey, index);
        
        // Add all translation how they where original
        for (String lang : sourceRes.all_langs) {
            Locale l = new Locale(lang);
            retRes.setTranslation(newKey, l, sourceRes.translations.get(l).get(source.getKey()));
        }
        
        // Add it to the new key
        keySourcesMap.put(newKey, new Vector<KeySource>(){{add(source);}});
    }
    
    protected String nextKey(String key) {
        // Does not end with number
        if (key.charAt(key.length() - 1) < '0' || key.charAt(key.length() - 1) > '9') 
            return key + " 2";
                   
        // Ends with number

        // first Position that we need to consider
        int first = key.length() - 1;
        while (first > 0 && key.charAt(first) == '9')
            first--;

        // first Position is not a number
        // then we need to add a digit
        if (key.charAt(first) < '0' || key.charAt(first) > '9')  {
            first++;
            key = key.substring(0, first) + "0" + key.substring(first);
        }
        
        // increment the first digit
        String ret = key.substring(0, first);
        ret += (key.charAt(first) - '0') + 1;

        // for every remaining nine-digit add a zero
        for (first++; first < key.length(); first++)
            ret += '0';
            first++;
        
        return ret;
    }

    /**
     * This function splits a key that has more then one source.
     * It tries to give all sources they key they originally had or something close.
     * If more then one source have the key given by the parameter key,
     * then we keep the key for the first.
     * Keeping the first gives a more natural expected result.
     * 
     * @param key
     */
    public void split(String key) {
        ChoiceFeatureOption retOption = getMergeResult();
        CatalogResource retRes = retOption.getCatalogResource();

        Vector<KeySource> sources = keySourcesMap.get(key);
        Vector<KeySource> original = new Vector<KeySource>(sources);
        Map<KeySource, String> newKeys = new HashMap<KeySource, String>();
        
        // Get the index of the current key
        int index = retRes.keys.indexOf(key);
        
        // Try to throw sources out that have there unique key
        for (final KeySource source : new Vector<KeySource>(sources)) {
            // Has the same key as the unit
            if (source.getKey().equals(key))
                continue;

            // Has no unique key
            if (retRes.keys.contains(source.key))
                continue;

            // One source we don't need to consider any further
            sources.remove(source);

            // Add the source to another key
//            addKey(source, source.getKey());
            newKeys.put(source, source.getKey());
        }
        
        // If no source is left we are allready done
        if (sources.size() == 0) {
            for (int i = 0; i < original.size(); i++)
                addKey(original.get(i), newKeys.get(original.get(i)), index + i);
            return;
        }
        
        // Try to throw sources out that have there unique key
        for (final KeySource source : new Vector<KeySource>(sources)) {
            // Has the same key as the unit
            if (source.getKey().equals(key))
                continue;


            String newKey = source.getKey();
            while (retRes.keys.contains(newKey))
                newKey = nextKey(newKey);

            // One source we don't need to consider any further
            sources.remove(source);

            // Add the source to another key
//            addKey(source, newKey);
            newKeys.put(source, newKey);
        }
        
        // If no source is left we are allready done
        if (sources.size() == 0) {
            for (int i = 0; i < original.size(); i++)
                addKey(original.get(i), newKeys.get(original.get(i)), index + i);
            return;
        }
        
        // Try to throw sources out that have there unique key
        for (int i = sources.size() - 1; i > 0; i--) {
            final KeySource source = sources.get(i);
            
            String newKey = key; // key == source.getKey()
            while (retRes.keys.contains(newKey))
                newKey = nextKey(newKey);

            // One source we don't need to consider any further
            sources.remove(source);

            // Add the source to another key
//            addKey(source, newKey);
            newKeys.put(source, newKey);
        }

        KeySource source = sources.firstElement();
        sources.remove(source);

        // Recreate remaining entry
        retRes.removeKey(key);
//        addKey(source, key);
        newKeys.put(source, key);

        for (int i = 0; i < original.size(); i++)
            addKey(original.get(i), newKeys.get(original.get(i)), index + i);
    }


    public void restoreDefault() {
        getMerging();        
    }



    public JComponent getGUI() {
        return new ChoiceFeatureMergerGUI(this);
    }



    public void unite(Vector<String> keys) {
        ChoiceFeatureOption retOption = getMergeResult();
        CatalogResource retRes = retOption.getCatalogResource();

        // The first one will be the one where everything gets merged
        String key = keys.firstElement();
        keys.remove(key);

        conflictsKey.put(key, ChoiceConflictType.RenamedKey);
        for (String lang : langsAll)
            conflictsTranslation.get(new Locale(lang)).put(key, ChoiceConflictType.RenamedKey);
        
        for (final String addKey : keys) {
            // merge all sources into the 
            keySourcesMap.get(key).addAll(keySourcesMap.get(addKey));
            
            for (String lang : langsAll) {
                // Abbreviation
                Locale l = new Locale(lang);

                // Only override those translations that are still empty
                if (retRes.translations.get(l).get(key) != null)
                    continue;
                
                retRes.translations.get(l).put(key, retRes.translations.get(l).get(addKey));
                
                // Remove key that has been included
                retRes.removeKey(addKey);
                keySourcesMap.remove(addKey);
            }
            
        }
    }

}
