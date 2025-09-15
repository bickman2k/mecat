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
 * Created on Nov 13, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.gui.features.impl;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.sourceforge.mecat.catalog.gui.features.TextFieldFeaturePanel;
import net.sourceforge.mecat.catalog.gui.features.desktop.FeatureDesktop;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalButton;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.medium.features.impl.IMDBTitleNumber;

public class IMDBTitleNumberFeaturePanel extends TextFieldFeaturePanel<IMDBTitleNumber> {

    protected JButton search;
    protected JButton getAkas;
    protected JButton getYear;
    protected JButton getRuntime;
    protected JButton getGenres;
    protected JButton getSummary;
    protected JButton getActors;
    protected JPanel buttonPanel;
    
    
    public IMDBTitleNumberFeaturePanel(IMDBTitleNumber textFeature, FeatureDesktop desktop, boolean border, Locale locale) {
        super(textFeature, desktop, border, locale);
        init();
    }

    public IMDBTitleNumberFeaturePanel(IMDBTitleNumber textFeature, FeatureDesktop desktop, boolean border) {
        super(textFeature, desktop, border);
        init();
    }

    public IMDBTitleNumberFeaturePanel(IMDBTitleNumber textFeature, FeatureDesktop desktop, Locale locale) {
        super(textFeature, desktop, locale);
        init();
    }

    public IMDBTitleNumberFeaturePanel(IMDBTitleNumber textFeature, FeatureDesktop desktop) {
        super(textFeature, desktop);
        init();
    }
    
    protected void init() {
        search = new SimpleLocalButton(res, "Search");
        search.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                feature.search(search);
                fireRebuild();
            }});
        getAkas = new SimpleLocalButton(res, "get Akas");
        getAkas.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                feature.getAkasFromIMDB(getAkas);
                fireRebuild();
            }});
        getYear = new SimpleLocalButton(res, "get Year");
        getYear.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                feature.getYearFromIMDB(getYear);
                fireRebuild();
            }});
        getRuntime = new SimpleLocalButton(res, "get Runtime");
        getRuntime.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                feature.getRuntimeFromIMDB(getRuntime);
                fireRebuild();
            }});
        getGenres = new SimpleLocalButton(res, "get Genres");
        getGenres.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                feature.getGenresFromIMDB(getGenres);
                fireRebuild();
            }});
        getSummary = new SimpleLocalButton(res, "get Summary");
        getSummary.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                feature.getSummaryFromIMDB(getSummary);
                fireRebuild();
            }});
        getActors = new SimpleLocalButton(res, "get cast");
        getActors.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                feature.getActorsFromIMDB(getActors);
                fireRebuild();
            }});
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3,3));
        buttonPanel.add(search);
        buttonPanel.add(getAkas);
        buttonPanel.add(getYear);
        buttonPanel.add(getRuntime);
        buttonPanel.add(getGenres);
        buttonPanel.add(getSummary);
        buttonPanel.add(getActors);
        updateButtonState();

        add(buttonPanel, BorderLayout.SOUTH);
    }

    protected void updateButtonState(){
        if (feature.getInt() != null) {
            getAkas.setEnabled(true);
            getYear.setEnabled(true);
            getRuntime.setEnabled(true);
            getGenres.setEnabled(true);
            getSummary.setEnabled(true);
            getActors.setEnabled(true);
        } else {
            getAkas.setEnabled(false);
            getYear.setEnabled(false);
            getRuntime.setEnabled(false);
            getGenres.setEnabled(false);
            getSummary.setEnabled(false);
            getActors.setEnabled(false);
        }
    }
    
    @Override
    public void featureValueChanged(Feature source) {
        super.featureValueChanged(source);
        updateButtonState();
    }
    

}


