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
 * Created on Sep 14, 2005
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filter.design;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;

import net.sourceforge.mecat.catalog.export.ExportChoice;
import net.sourceforge.mecat.catalog.filter.FeatureFilter;
import net.sourceforge.mecat.catalog.i18n.util.LocalTitledBorder;
import net.sourceforge.mecat.catalog.medium.features.BadCondition;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.medium.features.validators.FeatureValidator;
import net.sourceforge.mecat.catalog.option.LocalListener;
import net.sourceforge.mecat.catalog.option.LocalListenerEvent;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.WeakLocalListener;
import net.sourceforge.mecat.catalog.option.preferences.DefaultPreferences;
import net.sourceforge.mecat.catalog.option.preferences.TotalPreferences;
import net.sourceforge.mecat.srp.utils.NiceClass;

public class SimpleFeatureFilterDesigner extends JDialog implements LocalListener {

    final JComboBox featureComboBox;
    final JPanel featurePanel = new JPanel();
    boolean ok = false;

    FeatureValidator featureValidator = null;
    final TotalPreferences totalPreferences;

    final JButton okButton = new JButton();
    final JButton cancelButton = new JButton();
    
    public SimpleFeatureFilterDesigner(final TotalPreferences totalPreferences) {
        
        this.totalPreferences = totalPreferences;

        Vector<NiceClass<Feature>> features = new Vector<NiceClass<Feature>>();
        for (NiceClass<Feature> feature : Options.features)
            if (DefaultPreferences.defaultPreferences.getFeaturesOption().hasValidator(feature.getClasstype()))
                features.add(feature);
        
        featureComboBox = new JComboBox(features);
        featureComboBox.setBorder(new LocalTitledBorder() {
            protected String getLocalTitle() {
                return Options.getI18N(SimpleFeatureFilterDesigner.class).getString("Feature") + ":";
            }
        });
        featureComboBox.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                updateVisual();
            }
        });
        
        featurePanel.setBorder(new LocalTitledBorder() {
            protected String getLocalTitle() {
                return Options.getI18N(SimpleFeatureFilterDesigner.class).getString("Value") + ":";
            }
        });
        featurePanel.setLayout(new BorderLayout());

        JDialog featureFilterDesigner = new JDialog();
        JPanel panel = new JPanel();
        JPanel buttonPanel = new JPanel();
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                ok = true;
                close();
            }
        });
        buttonPanel.add(okButton);
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                close();
            }
        });
        buttonPanel.add(cancelButton);

        panel.setLayout(new BorderLayout());
        panel.add(featureComboBox, BorderLayout.NORTH);
        panel.add(featurePanel);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        
        setContentPane(panel);

        if (features.size() > 0)
            featureComboBox.setSelectedIndex(0);
//        updateVisual();
    


        
        Options.addLocalListener(new WeakLocalListener(this));
        // Set labels to the current locale
        setLabels();
    }

    public void stateChanged(LocalListenerEvent event) {
        setLabels();
    }
    
    public void setLabels() {
        okButton.setText(Options.getI18N(ExportChoice.class).getString("OK"));
        cancelButton.setText(Options.getI18N(ExportChoice.class).getString("Cancel"));

        setTitle(Options.getI18N(SimpleFeatureFilterDesigner.class).getString("SimpleFeatureFilterDesigner"));
        validate();
        pack();
    }

    public void updateVisual() {
        Object o = featureComboBox.getSelectedItem();
        if (!(o instanceof NiceClass))
            return;
        featureValidator = DefaultPreferences.defaultPreferences.getFeaturesOption().getValidator((Class< ? extends Feature>) ((NiceClass)o).getClasstype());
        featurePanel.removeAll();
        if (featureValidator == null) {
//            featurePanel.updateUI();
            SimpleFeatureFilterDesigner.this.pack();
            return;
        }
        featureValidator.setValidation(null, totalPreferences);
        featurePanel.add(featureValidator.getPanel());
//        featurePanel.updateUI();
        SimpleFeatureFilterDesigner.this.pack();
    }
    
    protected void close() {
        setVisible(false);
    }
    
    public static FeatureFilter getFeatureFilter(TotalPreferences totalPreferences) {
        return getFeatureFilter(totalPreferences, null);
    }
    
    public static FeatureFilter getFeatureFilter(TotalPreferences totalPreferences, FeatureFilter featureFilter) {
        SimpleFeatureFilterDesigner sffd = new SimpleFeatureFilterDesigner(totalPreferences);
        if (featureFilter != null) {
            sffd.featureComboBox.setSelectedItem(new NiceClass<Feature>(featureFilter.getFeatureClass()));
            sffd.featureValidator.setValidation(featureFilter.getValue(), totalPreferences);
        }
        sffd.setModal(true);
        sffd.setVisible(true);
//        sffd.pack();
        
        if (!sffd.ok)
            return null;
        
        Object value = sffd.featureComboBox.getSelectedItem();

        if (!(value instanceof NiceClass))
            return null;

        if (!(Feature.class.isAssignableFrom(((NiceClass)value).getClasstype())))
            return null;

        try {
            return new FeatureFilter(((NiceClass<? extends Feature>)value).getClasstype(), sffd.featureValidator.getValidation());
        } catch (BadCondition e) {
            e.printStackTrace();
            return null;
        }
    }
}
