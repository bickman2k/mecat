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
 * Created on Jan 25, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.medium.features.validators;

import java.awt.BorderLayout;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;

import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.Util;
import net.sourceforge.mecat.catalog.datamanagement.Util.Argument;
import net.sourceforge.mecat.catalog.medium.features.AbstractChoiceFeature;
import net.sourceforge.mecat.catalog.medium.features.option.ChoiceFeatureOption;
import net.sourceforge.mecat.catalog.option.LocalListener;
import net.sourceforge.mecat.catalog.option.LocalListenerEvent;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.WeakLocalListener;
import net.sourceforge.mecat.catalog.option.preferences.TotalPreferences;

public class ChoiceFeatureValidator extends JPanel implements FeatureValidator, LocalListener {

    final JComboBox comboBox = new JComboBox();
    final Class<? extends AbstractChoiceFeature> featureClass;
    Vector<String> keys = null; 
    ChoiceFeatureOption choiceFeatureOption = null;
    
    public ChoiceFeatureValidator(final Class<? extends AbstractChoiceFeature> featureClass) {
        this.featureClass = featureClass;
        
        Options.addLocalListener(new WeakLocalListener(this));
        
        setLayout(new BorderLayout());
        add(comboBox);
    }
    
    public void stateChanged(LocalListenerEvent event) {
        // Stop if there is not yet been a setValidation
        // and therefore no real data
        if (choiceFeatureOption == null)
            return;
        
        setListData();
    }

    public JPanel getPanel() {
        return this;
    }

    public void setValidation(final String val, final TotalPreferences totalPreferences) {
        // Get feature option
         choiceFeatureOption = ( ChoiceFeatureOption ) totalPreferences.getFeaturesOption().getOption(featureClass);
         
         // Get keys
         keys = choiceFeatureOption.getCatalogResource().keys;

         setListData();
         if (keys.contains(val))
             comboBox.setSelectedIndex(keys.indexOf(val));
         
    }

    public void setListData() {
        // Get Resources for the i18n of the Choices
        ResourceBundle choiceResources = choiceFeatureOption.getCatalogResource().getBundle(Options.getCurrentLocale());

        // Clear JComboBox
        comboBox.removeAllItems();
        
        // Insert i18n-list of the Choices into JComboBox
        for (int i = 0; i < keys.size(); i++) 
            comboBox.insertItemAt(choiceResources.getString(keys.elementAt(i)), i);
    }
    
    
    public String getValidation() {
        // Get index once 
        int index = comboBox.getSelectedIndex();
        
        // If there is nothing selected return null
        if (index == -1)
            return null;
        
        // Else return the key
        return keys.elementAt(index);
    }

    public boolean loadFromEntry(Entry entry) {
        return true;
    }

    public void saveToEntry(Entry entry) {
        Util.addArgument(entry, new Argument(0, Class.class, featureClass));
    }
    
}