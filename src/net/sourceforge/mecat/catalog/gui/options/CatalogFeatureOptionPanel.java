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
 * Created on Jan 11, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.gui.options;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalCheckBox;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.medium.features.option.FeatureOption;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.preferences.AbstractFeaturesOption;
import net.sourceforge.mecat.catalog.option.preferences.AbstractMediaOption;
import net.sourceforge.mecat.catalog.option.preferences.CatalogFeaturesOption;
import net.sourceforge.mecat.catalog.option.preferences.DefaultPreferences;
import net.sourceforge.mecat.catalog.option.preferences.GlobalFeaturesOption;

public class CatalogFeatureOptionPanel extends JPanel {

    final Class<? extends Feature> featureClass;
    final AbstractFeaturesOption featuresOption;
    final JCheckBox checkBox = new SimpleLocalCheckBox(Options.getI18N(CatalogFeatureOptionPanel.class), "Override the feature's option for this catalog");

    // While the checkBox is unselected we going to show he general options
    // if it is selected we going to show the catalog options for this reason
    // we need a switch panel where once the general and once the catalog options
    // can be shown
    final JPanel switchPanel = new JPanel();

    public CatalogFeatureOptionPanel(final Class<? extends Feature> featureClass, final AbstractFeaturesOption featuresOption) {
        this.featureClass = featureClass;
        this.featuresOption = featuresOption;

        checkBox.setSelected(featuresOption.overriden.get(featureClass));
        checkBox.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                featuresOption.setOverriden(featureClass, checkBox.isSelected());
                setCorrectPanel();
            }
        });
        switchPanel.setLayout(new BorderLayout());
        setCorrectPanel();
        
        setLayout(new BorderLayout());
        // Only make it possible to override the value of a feature
        // if the feature has some value to be overriden
        if (isOverrideable())
            add(checkBox, BorderLayout.NORTH);
        add(switchPanel);
    }
    
    protected boolean isOverrideable() {
        if (featuresOption instanceof CatalogFeaturesOption)
            return (Options.AppPrefs.getFeaturesOption().getOption(featureClass) != null || DefaultPreferences.defaultPreferences.getFeaturesOption().getOption(featureClass) != null);

        if (featuresOption instanceof GlobalFeaturesOption)
            return (DefaultPreferences.defaultPreferences.getFeaturesOption().getOption(featureClass) != null);
        
        return false;
    }

    protected FeatureOption getDefaultOption() {
        if (featuresOption instanceof CatalogFeaturesOption) {
            if (Options.AppPrefs.getFeaturesOption().isOverriden(featureClass))
                return Options.AppPrefs.getFeaturesOption().getOption(featureClass);
            else
                return DefaultPreferences.defaultPreferences.getFeaturesOption().getOption(featureClass);
        }
        
        if (featuresOption instanceof GlobalFeaturesOption)
            return DefaultPreferences.defaultPreferences.getFeaturesOption().getOption(featureClass);
        
        return null;
    }
    
    void setCorrectPanel() {
        switchPanel.removeAll();
        JPanel fop = null;
        Feature feature = AbstractMediaOption.featuresInstances.get(featureClass);
        if (featuresOption.overriden.get(featureClass))
            fop = feature.getFeatureOptionPanel(featuresOption.options.get(featureClass));
        else {
            fop = feature.getFeatureOptionPanel(getDefaultOption());
            OptionDialog.setEnabled(fop, false);
        }
        switchPanel.add(fop);
        switchPanel.updateUI();
    }
}
