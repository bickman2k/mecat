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
 * TODO make more Parents to chose from
 *             means already chose ComboBox, Select, ...
 */
package net.sourceforge.mecat.catalog.gui.features;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JComboBox;

import net.sourceforge.mecat.catalog.gui.features.desktop.FeatureDesktop;
import net.sourceforge.mecat.catalog.i18n.fromCatalog.LayeredResourceBundle;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalObject;
import net.sourceforge.mecat.catalog.medium.features.ChoiceFeature;
import net.sourceforge.mecat.catalog.medium.features.Feature;

public class ComboBoxFeaturePanel extends FeaturePanel<ChoiceFeature>{

	protected JComboBox comboBox = null;
    SimpleLocalObject choice_i18n[] = null;
    Map<String, SimpleLocalObject> map;

    public ComboBoxFeaturePanel(ChoiceFeature choiceFeature, FeatureDesktop desktop){
        this(choiceFeature, desktop, null);
    }
    
	public ComboBoxFeaturePanel(ChoiceFeature choiceFeature, FeatureDesktop desktop, final LayeredResourceBundle extraResources)
	{
		this(choiceFeature, desktop, true, extraResources);
	}
	
    public ComboBoxFeaturePanel(ChoiceFeature choiceFeature, FeatureDesktop desktop, boolean border) {
        this(choiceFeature, desktop, border, null);
    }
    
	public ComboBoxFeaturePanel(ChoiceFeature choiceFeature, FeatureDesktop desktop, boolean border, final LayeredResourceBundle extraResources) 
	{
		super(choiceFeature, desktop, border, choiceFeature.attributeName, extraResources);

		// Get Resources for the i18n of the Choices
		ResourceBundle choiceResources = feature.getChoiceResourceBundle();

		// Make a i18n-list of the Choices
		choice_i18n = new SimpleLocalObject[feature.getChoices().size()];
        map = new HashMap<String, SimpleLocalObject>();
		for (int i = 0; i < feature.getChoices().size(); i++) {
            String key = feature.getChoices().elementAt(i);
			choice_i18n[i] = new SimpleLocalObject(choiceResources, key);
            map.put(key, choice_i18n[i]);
        }

		// Fill the ComboBox with the i18n-list
		comboBox = new JComboBox(choice_i18n);

		// Make the list visible
		add(comboBox);

		// If there is no feature, there is no sense in editing		
		if (feature == null)
			comboBox.setEnabled(false);
		else 
			if (feature.getChoices().size() > 0)
				comboBox.setSelectedItem(map.get(feature.get()));

		// Add a listener when the user makes a choice
		// This should be done last so initialisation does
		// not trigger it.
		comboBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				setChoice((SimpleLocalObject)comboBox.getSelectedItem());
		  }
		});
	}
	
	public void setChoice(SimpleLocalObject choice) {

		// If the feature does not work
		// will stop right here.
		if (feature == null)
			return;
		
        if (choice == null)
            return;
        
        // The wanted value is already set
        if (feature.get() != null && feature.get().equals(choice.getKey()))
            return;
        
		feature.set(choice.getKey());
	}
    

    public void requestFocus() {
        comboBox.requestFocus();
    }
    
    public boolean hasFocus() {
        return comboBox.hasFocus();
    }

    public void featureValueChanged(Feature source) {
        
        try {
            SimpleLocalObject slo = (SimpleLocalObject) comboBox.getSelectedItem();
            // If both are not set then do nothing
            if (slo == null && getFeature().get() == null) 
                return;
            // If only the feature is set then set this to
            if (slo == null) {
                comboBox.setSelectedItem(map.get(getFeature().get()));
                return;
            }
            // If only the combobox is set then unset it
            if (getFeature().get() == null) {
                comboBox.setSelectedIndex(-1);
                return;
            }

            // If both are set and have the same value then do nothing
            if (getFeature().get().equals(slo.getKey()))
                return;
             
            // Now we know that both have a value and that they are different
            comboBox.setSelectedItem(map.get(getFeature().get()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
