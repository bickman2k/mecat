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
 * Created on Jan 30, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.gui.features;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import net.sourceforge.mecat.catalog.gui.MainFrameBackend;
import net.sourceforge.mecat.catalog.gui.ToolBarUtils;
import net.sourceforge.mecat.catalog.gui.features.desktop.FeatureDesktop;
import net.sourceforge.mecat.catalog.gui.options.LanguageSelection;
import net.sourceforge.mecat.catalog.i18n.fromCatalog.LayeredResourceBundle;
import net.sourceforge.mecat.catalog.i18n.util.LocalTitledBorder;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.medium.features.TextFeature;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.sort.design.FeatureSortOptions;

public class MultiLanguageTextFieldFeaturePanel extends FeaturePanel<TextFeature> {

    static GridBagConstraints fieldConstraint = new GridBagConstraints();
    static GridBagConstraints removeButtonConstraint = new GridBagConstraints();
    static GridBagConstraints addButtonConstraint = new GridBagConstraints();

    Set<Locale> currentShownLanguages = new HashSet<Locale>();
    
    Vector<FeaturePanel<TextFeature>> panels = new Vector<FeaturePanel<TextFeature>>();
    
    static {
        fieldConstraint.fill = GridBagConstraints.BOTH;
        fieldConstraint.weightx = 1;
        fieldConstraint.weighty = 1;
        
        removeButtonConstraint.fill = GridBagConstraints.BOTH;
        removeButtonConstraint.weightx = 0;
        removeButtonConstraint.gridwidth = GridBagConstraints.REMAINDER;
        removeButtonConstraint.weighty = 1;
        
        addButtonConstraint.fill = GridBagConstraints.BOTH;
        addButtonConstraint.weightx = 1;
        addButtonConstraint.gridwidth = GridBagConstraints.REMAINDER;
        addButtonConstraint.weighty = 1;
        addButtonConstraint.gridheight = GridBagConstraints.REMAINDER;
    }
    
    public MultiLanguageTextFieldFeaturePanel(TextFeature feature, FeatureDesktop desktop) {
        this(feature, desktop, true, null);
    }
    public MultiLanguageTextFieldFeaturePanel(TextFeature feature, FeatureDesktop desktop, boolean border) {
        this(feature, desktop, border, null);
    }
    public MultiLanguageTextFieldFeaturePanel(final TextFeature feature, FeatureDesktop desktop, boolean border, LayeredResourceBundle extraResources) {
        super(feature, desktop, border, feature.attributeName, extraResources);
        
        rebuild();
    }
    
    protected void rebuild() {
        removeAll();
        panels.clear();
        setLayout(new GridBagLayout());
        
        Set<Locale> languages = feature.getLanguages();
        currentShownLanguages.clear();
        currentShownLanguages.addAll(languages);
        for (final Locale l : languages) {
            FeaturePanel<TextFeature> localTextFeaturePanel = new TextFieldFeaturePanel(feature, desktop, false, l);
            panels.add(localTextFeaturePanel);
            localTextFeaturePanel.setBorder(new LocalTitledBorder() {
                protected String getLocalTitle() {
                    return l.getDisplayLanguage(Options.getCurrentLocale());
                }
            });
            add(localTextFeaturePanel, fieldConstraint);
            JButton removeButton = ToolBarUtils.makeButton("kill", null, Options.getI18N(FeatureSortOptions.class).getString("Remove"), Options.getI18N(FeatureSortOptions.class).getString("Remove"), new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    feature.set(null, l);
                    rebuild();
                }
            });
            removeButton.setEnabled(languages.size() > 1);
            add(removeButton, removeButtonConstraint);
        }

        JButton addButton = new JButton(res.getString("Add language"));
        addButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                LanguageSelection panel = new LanguageSelection();
                int ret = JOptionPane.showConfirmDialog(MultiLanguageTextFieldFeaturePanel.this, panel, Options.getI18N(MainFrameBackend.class).getString("Select Language"),  JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (ret != JOptionPane.OK_OPTION)
                    return;
                
                // Stop if no language has been chosen
                if (panel.getLocale() == null)
                    return;
                
                String str = feature.get();
                if (str == null || str.length() == 0)
                    str = "Text";
                    
                feature.set(str, panel.getLocale());
                rebuild();
            }
        });
        add(addButton, addButtonConstraint);
        updateUI();
    }

    public void requestFocus() {
        if (panels.isEmpty())
            return;
        panels.firstElement().requestFocus();
    }
    
    public boolean hasFocus() {
        for (FeaturePanel<TextFeature> panel : panels)
            if (panel.hasFocus())
                return true;
        
        return false;
    }
    public void featureValueChanged(Feature source) {
        Set<Locale> languages = feature.getLanguages();
        if (currentShownLanguages.containsAll(languages) && languages.size() == currentShownLanguages.size())
            return;
        
        rebuild();
    }
}
