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
 * Created on Sep 21, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.sort.design;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.mecat.catalog.export.ExportChoice;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalButton;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalTitledBorder;
import net.sourceforge.mecat.catalog.medium.Listing;
import net.sourceforge.mecat.catalog.option.LocalListener;
import net.sourceforge.mecat.catalog.option.LocalListenerEvent;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.WeakLocalListener;
import net.sourceforge.mecat.catalog.sort.Comparing;
import net.sourceforge.mecat.catalog.sort.ConfigurableComparator;

public class SortManager extends JDialog implements LocalListener {

    enum SortManagerStatus {
        cancel, accept, create
    }

    SortManagerStatus status = SortManagerStatus.cancel;
    
    JList predefinedList = new JList();
    JList recentList = new JList();
    JList currentList = new JList();

    JPanel predefinedAndRecent = new JPanel();
    JPanel allListsRecent = new JPanel();
    
    // The initial comparator
    protected ConfigurableComparator comparator;

    // Buttons 
    final JButton okButton = new SimpleLocalButton(Options.getI18N(ExportChoice.class), "OK");
    final JButton cancelButton = new SimpleLocalButton(Options.getI18N(ExportChoice.class), "Cancel");
    final JButton createNewButton = new SimpleLocalButton(Options.getI18N(SortManager.class), "Create new");
    
    public SortManager(ConfigurableComparator comparator) throws HeadlessException {
        this.comparator = comparator;
        init();
    }

    public SortManager(Frame frame, ConfigurableComparator comparator) throws HeadlessException {
        super(frame);
        this.comparator = comparator;
        init();
    }

    public SortManager(Dialog dialog, ConfigurableComparator comparator) throws HeadlessException {
        super(dialog);
        this.comparator = comparator;
        init();
    }

    private void init() {
        // If the comparator is an empty comparing
        // it's the same as comparator = null
        if (comparator instanceof Comparing) {
            Comparing comparing = (Comparing) comparator;
            if (comparing.size() == 0)
                comparator = null;
        }
        
        okButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                status = SortManagerStatus.accept;
                SortManager.this.setVisible(false);
            }
        });
        okButton.setEnabled(false);
        cancelButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                SortManager.this.setVisible(false);
            }
        });
        createNewButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                status = SortManagerStatus.create;
                SortManager.this.setVisible(false);
            }
        });
        
        ResourceBundle res = Options.getI18N(SortManager.class);
        predefinedList.setBorder(new SimpleLocalTitledBorder(res, "Predefined sortings:"));
        recentList.setBorder(new SimpleLocalTitledBorder(res, "Recent sortings:"));
        currentList.setBorder(new SimpleLocalTitledBorder(res, "Current used sortings:"));
        
        predefinedAndRecent.setLayout(new GridLayout(0,1));
        predefinedAndRecent.add(predefinedList);
        predefinedAndRecent.add(recentList);
        
        allListsRecent.setLayout(new BorderLayout());
        allListsRecent.add(predefinedAndRecent);
        if (comparator != null)
            allListsRecent.add(currentList, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(createNewButton);
        buttonPanel.add(cancelButton);
        
        setLayout(new BorderLayout());
        add(allListsRecent);
        add(buttonPanel, BorderLayout.SOUTH);
        
        predefinedList.setListData(Options.predefComparing);
        recentList.setListData(Options.recent_comparings);
        currentList.setListData(new Vector<ConfigurableComparator>(){{add(comparator);}});
        
        predefinedList.addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent event) {
                if (predefinedList.getSelectedIndices().length == 0)
                    return;
                
                currentList.setSelectedIndices(new int[]{});
                recentList.setSelectedIndices(new int[]{});
                checkOkButton();
            }
        });
        recentList.addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent event) {
                if (recentList.getSelectedIndices().length == 0)
                    return;

                currentList.setSelectedIndices(new int[]{});
                predefinedList.setSelectedIndices(new int[]{});
                checkOkButton();
            }
        });
        currentList.addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent event) {
                if (currentList.getSelectedIndices().length == 0)
                    return;

                predefinedList.setSelectedIndices(new int[]{});
                recentList.setSelectedIndices(new int[]{});
                checkOkButton();
            }
        });


        pack();
        setSize(480, 640);

        // If there was a sorting befor
        // then preselect it and focus the ok button
        if (comparator != null) {
            currentList.setSelectedIndex(0);
            okButton.requestFocus();
        }
        
        
        Options.addLocalListener(new WeakLocalListener(this));
        // Set labels to the current locale
        setLabels();
    }

    public void stateChanged(LocalListenerEvent event) {
        setLabels();
    }
    
    public void setLabels() {
        setTitle(Options.getI18N(SortManager.class).getString("Sorting manager"));
    }

    protected void checkOkButton() {
        boolean enabled = false;
        
        if (predefinedList.getSelectedValue() instanceof ConfigurableComparator)
            enabled = true;

        if (recentList.getSelectedValue() instanceof ConfigurableComparator)
            enabled = true;
        
        if (currentList.getSelectedValue() instanceof ConfigurableComparator)
            enabled = true;

        
        okButton.setEnabled(enabled);
    }
    
    public ConfigurableComparator getConfigurableComparator() {
        if (predefinedList.getSelectedValue() instanceof ConfigurableComparator)
            return (ConfigurableComparator) predefinedList.getSelectedValue();

        if (recentList.getSelectedValue() instanceof ConfigurableComparator)
            return (ConfigurableComparator) recentList.getSelectedValue();
        
        if (currentList.getSelectedValue() instanceof ConfigurableComparator)
            return (ConfigurableComparator) currentList.getSelectedValue();

        return null;
    }

    public static ConfigurableComparator showSortManager(Component component, ConfigurableComparator comparator, Listing listing) {
        SortManager dialog;
        
        while (component != null && !(component instanceof Frame) && !(component instanceof Dialog))
            component = component.getParent();

        if (component == null)
            dialog = new SortManager(comparator);
        else if (component instanceof Dialog)
            dialog = new SortManager((Dialog)component, comparator);
        else
            dialog = new SortManager((Frame)component, comparator);


        dialog.setModal(true);
        dialog.setVisible(true);

        if (dialog.status == SortManagerStatus.cancel)
            return null;
        
        if (dialog.status == SortManagerStatus.create) 
            return CreateSortDialog.showCreateSortDialog(component, listing);
        
        return dialog.getConfigurableComparator();
    }
}
