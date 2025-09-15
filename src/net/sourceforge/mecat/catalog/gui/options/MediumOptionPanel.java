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
 * Created on Jan 7, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.gui.options;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sourceforge.mecat.catalog.gui.features.desktop.FeatureDesktop;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalTitledBorder;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.preferences.AbstractMediaOption;
import net.sourceforge.mecat.catalog.option.preferences.MediaOption;
import net.sourceforge.mecat.srp.utils.NiceClass;
import net.sourceforge.mecat.srp.utils.NiceClassDisplayNameComparator;

public class MediumOptionPanel extends JPanel {

    final Class<? extends Medium> mediumClass;
    final MediaOption mediaOption;
    final Vector<NiceClass<Feature>> features = new Vector<NiceClass<Feature>>();
    
    final JPanel featureVisibility = new JPanel();
    
    public MediumOptionPanel(final Class<? extends Medium> mediumClass, final MediaOption mediaOption) {
        this.mediumClass = mediumClass;
        this.mediaOption = mediaOption;
        
        // Only need the features directly accessable 
        // therefor use getFeatures and not getAllFeatures
        for (Class<? extends Feature> featureClass : AbstractMediaOption.getFeatures(mediumClass)) 
            features.add(new NiceClass<Feature>(featureClass));
        Collections.sort(features, NiceClassDisplayNameComparator.displayNameComparator);

        featureVisibility.setLayout(new GridLayout(features.size() + 1, Options.desktops.size() + 1));
        featureVisibility.setBorder(new SimpleLocalTitledBorder(Options.getI18N(MediumOptionPanel.class), "Visibility of features"));
        
        featureVisibility.add(new JPanel());
        for (NiceClass<FeatureDesktop> desktop : Options.desktops) 
            featureVisibility.add(new JLabel(desktop.toString()));
        
        for (final NiceClass<Feature> feature : features) {
            featureVisibility.add(new JLabel(feature.toString()));
            for (final NiceClass<FeatureDesktop> desktop : Options.desktops) {
                final JCheckBox checkBox = new JCheckBox("", mediaOption.isWanted(mediumClass, feature.getClasstype(), desktop.getClasstype()));
                checkBox.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent arg0) {
                        if (mediaOption instanceof AbstractMediaOption)
                            ((AbstractMediaOption)mediaOption).setWanted(mediumClass, feature.getClasstype(), desktop.getClasstype(), checkBox.isSelected());
                    }});
                featureVisibility.add(checkBox);
            }
        }
        
        setLayout(new BorderLayout());
        add(featureVisibility, BorderLayout.NORTH);
        add(new JPanel());
    }

    
    

}
