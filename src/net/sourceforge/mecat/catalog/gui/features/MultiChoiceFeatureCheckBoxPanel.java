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
 * TODO The border and the name perhaps should be in
 *             FeaturePanel to
 */
package net.sourceforge.mecat.catalog.gui.features;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;

import net.sourceforge.mecat.catalog.gui.features.desktop.FeatureDesktop;
import net.sourceforge.mecat.catalog.i18n.fromCatalog.LayeredResourceBundle;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.medium.features.MultiChoiceFeature;



public class MultiChoiceFeatureCheckBoxPanel extends FeaturePanel<MultiChoiceFeature> {

	protected javax.swing.JCheckBox choice_Checkbox[];

	public void choiceChanged(){
		// If Feature does not work
		// we'll stop right here
		if (feature == null)
			return;
		
		for (int i = 0; i < feature.getChoices().size(); i++) {
            boolean featureTmp = feature.exists(feature.getChoices().elementAt(i));
            if (choice_Checkbox[i].isSelected() != featureTmp)
    			if (choice_Checkbox[i].isSelected())
    				feature.add(feature.getChoices().elementAt(i));
    			else
    				feature.remove(feature.getChoices().elementAt(i));
        }
	}

    public MultiChoiceFeatureCheckBoxPanel(MultiChoiceFeature feature, FeatureDesktop desktop) {
        this(feature, desktop, null);
    }
	public MultiChoiceFeatureCheckBoxPanel(MultiChoiceFeature feature, FeatureDesktop desktop, final LayeredResourceBundle extraResources) {
		this(feature, desktop, true, extraResources);
	}
	
	public MultiChoiceFeatureCheckBoxPanel(MultiChoiceFeature feature, FeatureDesktop desktop, boolean border) {
        this(feature, desktop, border, null);
    }
    public MultiChoiceFeatureCheckBoxPanel(final MultiChoiceFeature feature, FeatureDesktop desktop, boolean border, final LayeredResourceBundle extraResources) {
		super(feature, desktop, border, feature.attributeName, extraResources);
		

		// Set to flowlayout if FlowLayout is selected
		if (flow.isSelected())
			setLayout(new FlowLayout());
		else
			// Set Layout to Gridlayout to arrange options bellow each other
			setLayout(new GridLayout(feature.getChoices().size(),1));


		// Allocate an Array for the Checkboxes
		choice_Checkbox = new javax.swing.JCheckBox[feature.getChoices().size()];

		// Get Resources for i18n of the Genre Names
		ResourceBundle choiceResources = feature.getChoiceResourceBundle();

		// Generate, fill the Checkboxes, make them visible and 
		// interactive
		for (int i = 0; i < feature.getChoices().size(); i++) {
			choice_Checkbox[i] = new JCheckBox(choiceResources.getString(feature.getChoices().elementAt(i)));
			if (feature != null)
				choice_Checkbox[i].setSelected(feature.exists(feature.getChoices().elementAt(i)));
			else
				choice_Checkbox[i].setEnabled(false);

			add(choice_Checkbox[i]);
            addPopupMenu(choice_Checkbox[i]);
		}	

		// This should be done last so initialisation does
		// not trigger it.
		for (JCheckBox box : choice_Checkbox)
			box.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e) {choiceChanged();}});
	}

	static JCheckBoxMenuItem flow = new JCheckBoxMenuItem("FlowLayout");
	
	protected JPopupMenu getJPopupMenu() {
		JPopupMenu menu = super.getJPopupMenu();
		menu.addSeparator();
		menu.add(flow);
		return menu;
	}

    public void requestFocus() {
        if (choice_Checkbox.length > 0)
            choice_Checkbox[0].requestFocus();
    }
    
    public boolean hasFocus() {
        for (JCheckBox box : choice_Checkbox)
            if (box.hasFocus())
                return true;
        return false;
    }

    public void featureValueChanged(Feature source) {
        for (int i = 0; i < feature.getChoices().size(); i++) {
            boolean featureTmp = feature.exists(feature.getChoices().elementAt(i));
            if (choice_Checkbox[i].isSelected() != featureTmp)
                choice_Checkbox[i].setSelected(featureTmp);
        }
    }
}
