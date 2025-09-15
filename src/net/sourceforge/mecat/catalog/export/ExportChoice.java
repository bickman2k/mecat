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
 * Created on Aug 16, 2005
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.export;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.sourceforge.mecat.catalog.datamanagement.Catalog;
import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.Util;
import net.sourceforge.mecat.catalog.filter.design.ShowDialog;
import net.sourceforge.mecat.catalog.gui.MainFrameBackend;
import net.sourceforge.mecat.catalog.gui.ShowListing;
import net.sourceforge.mecat.catalog.i18n.fromCatalog.LayeredResourceBundle;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalTitledBorder;
import net.sourceforge.mecat.catalog.option.LocalListener;
import net.sourceforge.mecat.catalog.option.LocalListenerEvent;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.WeakLocalListener;
import net.sourceforge.mecat.srp.utils.ProgressBarWithListener;

public class ExportChoice extends JDialog implements LocalListener {
    final JComboBox profiles = new JComboBox(Options.profiles);
    
    final JPanel profilesPanel = new JPanel();
    final JPanel customizePanel = new JPanel();
    final JPanel buttonPanel = new JPanel();
    
    final JButton copyButton = new JButton();
    final JButton okButton = new JButton();
    final JButton cancelButton = new JButton();
    
    NamedExportProfile currentProfile = null;
    final ShowListing list;
//    final JPanel progressPanel;
    
    LayeredResourceBundle res = new LayeredResourceBundle(Options.getI18N(ExportChoice.class), Options.getI18N(MainFrameBackend.class));
    
    final JButton rename = new JButton(){{addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent arg0) { /*LOOK*/renameProfile();/*HERE*/}});}};
    final JButton delete = new JButton(){{addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent arg0) { /*LOOK*/deleteProfile();/*HERE*/}});}};
    final JButton option = new JButton(){{addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent arg0) { /*LOOK*/options();/*HERE*/}});}};

    
    public ExportChoice(final ShowListing list/*, final JPanel progressPanel*/) {
        this(list, /*progressPanel, */null);
    }
    public ExportChoice(final ShowListing list, /*final JPanel progressPanel,*/ final NamedExportProfile preSelection) {
        final boolean giveChoice = (preSelection == null);
        
        setLocation(50,50);

        this.list = list;
//        this.progressPanel = progressPanel;
        
        
        profiles.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                setChoice((NamedExportProfile)profiles.getSelectedItem());
            }
        });
        if (giveChoice)
            profiles.setSelectedItem(Options.profiles.firstElement());
        else 
            setChoice(preSelection);
        
        copyButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) { /*LOOK*/copyProfile();/*HERE*/}});
        okButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) { /*LOOK*/go();/*HERE*/}});
        cancelButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) { /*LOOK*/close();/*HERE*/}});
        
        
        profilesPanel.setBorder(new SimpleLocalTitledBorder(res, "Export profiles:"));
        customizePanel.setBorder(new SimpleLocalTitledBorder(res, "Customize selected profile:"));
        
        profilesPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        profilesPanel.add(profiles, c);
        c.weightx = 0.0;
        profilesPanel.add(rename, c);
        profilesPanel.add(copyButton, c);
        c.gridwidth = GridBagConstraints.REMAINDER;
        profilesPanel.add(delete, c);

        
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        setLayout(new BorderLayout());
        
        // Show the list of possible profiles if the giveChoice option is set
        if (giveChoice)
            add(profilesPanel, BorderLayout.NORTH);
        add(customizePanel);
        add(buttonPanel, BorderLayout.SOUTH);
        // If there is no choice given then set the title to the selected ExportProfile
        if (giveChoice)
            setTitle(res.getString("Export"));
        else
            setTitle(preSelection.toString());
        
        
        setResizable(false);
        setModal(true);
        
        Options.addLocalListener(new WeakLocalListener(this));
        // Set labels to the current locale
        setLabels();
    }

    public void stateChanged(LocalListenerEvent event) {
/*        // Set res to actual local _ it allready uses the actual local
        res = new LayeredResourceBundle(Options.getI18N(ExportChoice.class), Options.getI18N(MainFrameBackend.class));*/
        
        setLabels();
    }
    
    public void setLabels() {
        rename.setText(res.getString("Rename"));
        delete.setText(res.getString("Delete"));
        option.setText(res.getString("Option"));

        copyButton.setText(res.getString("Copy"));
        copyButton.setToolTipText(res.getString("CopyProfile"));
        okButton.setText(res.getString("OK"));
        cancelButton.setText(res.getString("Cancel"));

        updateRenDelOptionButtons();

        pack();
    }
    
    protected void close() {
        setVisible(false);
    }

    protected void deleteProfile() {
        Options.removeProfile(currentProfile);
        profiles.setSelectedItem(0);
        profiles.updateUI();
        pack();
    }
    
    protected void renameProfile() {
        String str = JOptionPane.showInputDialog(ExportChoice.this, res.getString("Select new name for the profile:"), currentProfile.toString());
        if ((str != null) && (str.length() > 0))
            currentProfile.rename(str);
        profiles.updateUI();
        pack();
    }
    
    protected void copyProfile() {
        Entry entry = Options.persistent.createOption("ExportProfile");
        Util.saveToEntry(currentProfile, entry);
        for (Locale l : entry.getAttributeLanguages("Name"))
            entry.setAttribute("Name", res.getBundle(l).getString("CopyPrefix") + " " + entry.getAttribute("Name", l), l);
        Options.addProfile(entry);
        profiles.setSelectedItem(Options.profiles.lastElement());
    }

    protected void updateRenDelOptionButtons() {
        if (currentProfile == null || (currentProfile.getEntry() == null) || (currentProfile.getEntry().getCatalog() != Options.persistent)){
            rename.setEnabled(false);
            rename.setToolTipText(res.getString("CantRenamePredefined"));
            delete.setEnabled(false);
            delete.setToolTipText(res.getString("CantDeletePredefined"));
            option.setEnabled(false);
            option.setToolTipText(res.getString("CantEditNonpersonalProfile"));
        } else {
            rename.setEnabled(true);
            rename.setToolTipText(res.getString("RenameProfile"));
            delete.setEnabled(true);
            delete.setToolTipText(res.getString("DeleteProfile"));
            option.setEnabled(true);
            option.setToolTipText(res.getString("EditProfile"));
        }
        if (currentProfile == null)
            return;

        if (option.isEnabled())
            option.setEnabled(currentProfile.exportProfile.hasOptions());
        if (!currentProfile.exportProfile.hasOptions())
            option.setToolTipText(res.getString("NoOptions"));
    }
    
    protected void setChoice(final NamedExportProfile selectedProfile) {
        this.currentProfile = selectedProfile;

        updateRenDelOptionButtons();

        customizePanel.removeAll();
        customizePanel.setLayout(new BorderLayout());
        customizePanel.add(option, BorderLayout.NORTH);
        customizePanel.add(currentProfile.exportProfile.customize(), BorderLayout.CENTER);
        
        pack();
    }

    protected void options() {
        // The following steps can take some seconds
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        // Make copy of the current Profile
        Catalog tempCatalog = Options.stdFactory.createCatalog(ExportChoice.this);
        Entry entry = tempCatalog.createOption("ExportProfile");
        Util.saveToEntry(currentProfile, entry);
        NamedExportProfile tempProfile = (NamedExportProfile) Util.loadFromEntry(entry);

        // Get the option Panel from the copy of the current Profile
        JPanel optionPanel = tempProfile.exportProfile.options(/*list.getSortedListing().getFilterListing().getListing().getTotalPreferences(),*/
                list);

        // Make a Dialog containing the option Panel
        final ShowDialog showDialog = new ShowDialog(optionPanel, res.getString("Option"));
        optionPanel.addPropertyChangeListener("size", new PropertyChangeListener(){
            public void propertyChange(PropertyChangeEvent arg0) {
                showDialog.pack();
            }
        });
        showDialog.setModal(true);
        showDialog.pack();
        showDialog.setResizable(false);
        // Waiting is over
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        showDialog.setVisible(true);

        // If the new options are commited then replace the old profile with the changed one
        if (showDialog.isAccepted()){
            currentProfile = Options.replaceProfile(currentProfile, tempProfile);
            setChoice(currentProfile);
            pack();
        }
    }

    void go() {
        setVisible(false);
//        ProgressBarWithListener progressBar = new ProgressBarWithListener();
        final Export export = currentProfile.exportProfile.getExport();
        ExportProgressVisualisation exportProgressVisualisation = new ExportProgressVisualisation(export);
//        export.addProgressListener(progressBar);
        export.list = list;
/*        progressPanel.removeAll();
        progressPanel.add(progressBar, BorderLayout.CENTER);
        progressPanel.updateUI();*/
        export.setParentComponent(this.getParent());
        export.start();
    }
}
