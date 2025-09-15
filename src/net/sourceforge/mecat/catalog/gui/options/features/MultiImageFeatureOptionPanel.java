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
 * Created on Aug 28, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.gui.options.features;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ResourceBundle;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalCheckBox;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalLabel;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalTitledBorder;
import net.sourceforge.mecat.catalog.medium.features.option.ImageFeatureOption;
import net.sourceforge.mecat.catalog.medium.features.option.MultiImageFeatureOption;
import net.sourceforge.mecat.catalog.option.Options;

public class MultiImageFeatureOptionPanel extends FeatureOptionPanel {

    // Max Scale Swing components
	JLabel maxScaleLabel = new SimpleLocalLabel(Options.getI18N(MultiImageFeatureOptionPanel.class), "Maximum scaling for image");
    JTextField maxScaleTxt = new JTextField();

    // Contains labels and panels for max scale
    JPanel bounds = new JPanel();

    // Checkbox for split option
	JCheckBox checkBox = new SimpleLocalCheckBox(Options.getI18N(MultiImageFeatureOptionPanel.class), "Every picture has an option");

    // The feature option this instance is related to
    final MultiImageFeatureOption multiImageFeatureOption;
    final ResourceBundle res;

    JPanel generalOptions = new JPanel();
    
    JPanel optionsPanel = new JPanel();
    JPanel splitPanels = new JPanel();
    JPanel sharedOption = null;

    public MultiImageFeatureOptionPanel(final MultiImageFeatureOption multiImageFeatureOption, final ResourceBundle res) {
		this.multiImageFeatureOption = multiImageFeatureOption;
        this.res = res;

        // Connection between gui and storage of max scale information
        maxScaleTxt.setText(Double.toString(multiImageFeatureOption.getMaxScale()));
        maxScaleTxt.addKeyListener(new KeyListener(){
            public void keyPressed(KeyEvent arg0) {maxScaleChanged();}
            public void keyReleased(KeyEvent arg0) {maxScaleChanged();}
            public void keyTyped(KeyEvent arg0) {maxScaleChanged();}
        });
        
        // Connection between gui and storage of split option
        checkBox.setSelected(multiImageFeatureOption.isSplitOption());
		checkBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				multiImageFeatureOption.setSplitOption(checkBox.isSelected());
				showRightOption();
			}});
        
        sharedOption = new ImageFeatureOptionPanel(multiImageFeatureOption.getSharedOption());
        sharedOption.setBorder(new SimpleLocalTitledBorder(Options.getI18N(MultiImageFeatureOptionPanel.class), "Shared option:"));

        splitPanels.setLayout(new GridLayout(0,1));
        for (final ImageFeatureOption imageFeatureOption : multiImageFeatureOption.getImageFeatureOptions()) {
            JPanel panel = new ImageFeatureOptionPanel(imageFeatureOption);
            
            panel.setBorder(new SimpleLocalTitledBorder(res, imageFeatureOption.getName()));
            
            splitPanels.add(panel);
        }
        if (multiImageFeatureOption.isBag()) {
            JPanel panel = new ImageFeatureOptionPanel(multiImageFeatureOption.getBagOption());
            
            panel.setBorder(new SimpleLocalTitledBorder(Options.getI18N(MultiImageFeatureOptionPanel.class), "Bag images:"));
            
            splitPanels.add(panel);
        }

        bounds.setLayout(new GridLayout(1,2));
        bounds.add(maxScaleLabel);
        bounds.add(maxScaleTxt);
        
        generalOptions.setLayout(new BorderLayout());
        generalOptions.add(bounds, BorderLayout.NORTH);
        generalOptions.add(checkBox);        
        
        optionsPanel.setLayout(new BorderLayout());

        setLayout(new BorderLayout());
        add(generalOptions, BorderLayout.NORTH);
        add(optionsPanel);

        
        showRightOption();   
    }

    protected void maxScaleChanged() {
        try {
            multiImageFeatureOption.setMaxScale(Double.parseDouble(maxScaleTxt.getText()));
        } catch (Exception e) {
            if (Options.DEBUG)
                e.printStackTrace();
        }
    }

    protected void showRightOption() {
        optionsPanel.removeAll();
        if (multiImageFeatureOption.isSplitOption())
            optionsPanel.add(splitPanels, BorderLayout.NORTH);
        else
            optionsPanel.add(sharedOption, BorderLayout.NORTH);
        optionsPanel.add(new JPanel());
        updateUI();
    }
}
