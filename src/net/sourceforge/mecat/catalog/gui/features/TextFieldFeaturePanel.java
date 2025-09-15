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
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import net.sourceforge.mecat.catalog.gui.features.desktop.ChainableFeatureDesktop;
import net.sourceforge.mecat.catalog.gui.features.desktop.FeatureDesktop;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.medium.features.TextFeature;

public class TextFieldFeaturePanel<T extends TextFeature> extends FeaturePanel<T>{

	protected JTextComponent text = null;
    final Locale locale;

    
	public TextFieldFeaturePanel(T textFeature, FeatureDesktop desktop) 
	{
		this(textFeature, desktop, true, null);
	}
	
    public TextFieldFeaturePanel(T textFeature, FeatureDesktop desktop, Locale locale) {
        this(textFeature, desktop, true, locale);
    }

    public TextFieldFeaturePanel(T textFeature, FeatureDesktop desktop, boolean border) 
	{
        this(textFeature, desktop, border, null);
    }

    public TextFieldFeaturePanel(T textFeature, final FeatureDesktop desktop, boolean border, final Locale locale) 
    {
		super(textFeature, desktop, border, textFeature.attributeName);
		this.locale = locale;

		// Fill the ComboBox with the i18n-list
		if (feature.field) {
		    text = new JTextField();
		    // Make the text visible
		    add(text);
        } else {
			text = new JTextArea();
			setPreferredSize(new Dimension(200,200));
            // Make the text visible
            add(new JScrollPane(text));
		}


		// If there is no feature, there is no sense in editing		
		if (feature == null)
			text.setEnabled(false);
		else
			text.setText(feature.get(locale));
        
        if (textFeature.localized && locale == null && desktop instanceof ChainableFeatureDesktop) {
            JButton more = new JButton("...");
            add(more, BorderLayout.EAST);
            more.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent arg0) {
                    if (!(desktop instanceof ChainableFeatureDesktop))
                        return;
                    
                    ChainableFeatureDesktop chainableFeatureDesktop = ( ChainableFeatureDesktop ) desktop;
                    
                    TextFieldFeaturePanelExtra.showTextFieldFeaturePanelExtra(TextFieldFeaturePanel.this, feature, res, chainableFeatureDesktop);
                }
            });
        }
		

		// Add a listener when the user makes a choice
		// This should be done last so initialisation does
		// not trigger it.
		text.addKeyListener(new KeyListener(){
			public void keyPressed(KeyEvent arg0) {changed();}
			public void keyReleased(KeyEvent arg0) {changed();}
			public void keyTyped(KeyEvent arg0) {changed();}
		});

	}
	
    protected void changed() {
		// If the feature does not work
		// will stop right here.
		if (feature == null)
			return;
        
        // Look if the textfield has changed against
        // the value of the feature
        String featureTxt = feature.get(locale);
        String fieldTxt = text.getText();

        if (featureTxt == null)
            featureTxt = "";

        if (fieldTxt.equals(featureTxt))
            return;

        // If the value of the feature has not been changed
        // to the wanted value, set the textfield to the value
        // from the feature
		if (!feature.set(text.getText(), locale))
		    text.setText(feature.get(locale));
	}
    
    public void requestFocus() {
        text.requestFocus();
    }
    
    public boolean hasFocus() {
        return text.hasFocus();
    }

    public void featureValueChanged(Feature source) {
        String featureTxt = feature.get(locale);
        if (featureTxt != null && featureTxt.length() == 0)
            featureTxt = null;
        if (featureTxt == null) {
            text.setText("");
            return;
        }
        // In order to prevent failures
        // I consider the posibility of the text being null
        // since the later equal would break this posibility
        // is considered here
        if (text.getText() == null) {
            text.setText(featureTxt);
            return;
        }
        if (!featureTxt.trim().equals(text.getText().trim())){
            text.setText(featureTxt);
        }
    }
    
}
