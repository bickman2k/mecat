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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import net.sourceforge.mecat.catalog.datamanagement.Catalog;
import net.sourceforge.mecat.catalog.datamanagement.Connection;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalCheckBox;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalTitledBorder;
import net.sourceforge.mecat.catalog.medium.features.option.SubEntryListFeatureOption;
import net.sourceforge.mecat.catalog.option.Options;

public class SubEntryListFeatureOptionPanel extends FeatureOptionPanel {

    JCheckBox shortViewBox = new SimpleLocalCheckBox(Options.getI18N(SubEntryListFeatureOptionPanel.class), "Use short view for view mode.", "Only one line will be shown in view mode.");
	JCheckBox preferLinkBox = new SimpleLocalCheckBox(Options.getI18N(SubEntryListFeatureOptionPanel.class), "Prefer link instead of direct storage.", "This allows to reuse information.");
    JCheckBox preferExternCatalogBox = new SimpleLocalCheckBox(Options.getI18N(SubEntryListFeatureOptionPanel.class), "Prefer link to external catalog.", "This allows to reuse information between catalogs.");
    JButton externCatalogButton = new JButton();
    
    
    // The feature option this instance is related to
    final SubEntryListFeatureOption subEntryListFeatureOption;

    public SubEntryListFeatureOptionPanel(final SubEntryListFeatureOption subEntryListFeatureOption) {
		this.subEntryListFeatureOption = subEntryListFeatureOption;

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(new SimpleLocalTitledBorder(Options.getI18N(SubEntryListFeatureOptionPanel.class), "External catalog."));
        buttonPanel.add(externCatalogButton);
        
        shortViewBox.setSelected(subEntryListFeatureOption.isUseShortVersionForView());
        preferLinkBox.setSelected(subEntryListFeatureOption.isPreferLink());
        preferExternCatalogBox.setSelected(subEntryListFeatureOption.isPreferExternCatalog());
        if (subEntryListFeatureOption.getExternCatalog() != null)
            externCatalogButton.setText(subEntryListFeatureOption.getExternCatalog().toString());
        checkEnable();
        
        shortViewBox.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                subEntryListFeatureOption.setUseShortVersionForView(shortViewBox.isSelected());
            }
        });
        
        preferLinkBox.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                subEntryListFeatureOption.setPreferLink(preferLinkBox.isSelected());
                checkEnable();
            }
        });
        
        preferExternCatalogBox.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                subEntryListFeatureOption.setPreferExternCatalog(preferExternCatalogBox.isSelected());
                checkEnable();
            }
        });
        
        externCatalogButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                Catalog catalog = Options.stdFactory.createCatalog(SubEntryListFeatureOptionPanel.this);
                Connection connection = catalog.getSaveCatalogConnection(SubEntryListFeatureOptionPanel.this);
                subEntryListFeatureOption.setExternCatalog(connection);
                externCatalogButton.setText(connection.toString());
            }});
        
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.gridwidth = GridBagConstraints.REMAINDER;

        add(shortViewBox, c);
        add(preferLinkBox, c);
        add(preferExternCatalogBox, c);
        add(buttonPanel, c);
        
        c.weighty = 1;
        add(new JPanel(), c);
    }

    protected void checkEnable() {
        preferExternCatalogBox.setEnabled(preferLinkBox.isSelected());
        externCatalogButton.setEnabled(preferLinkBox.isSelected() && preferExternCatalogBox.isSelected());
    }
    
}
