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
 * Created on Jun 30, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.mecat.catalog.export.ExportChoice;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalButton;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalCheckBox;
import net.sourceforge.mecat.catalog.medium.Listing;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.option.LocalListener;
import net.sourceforge.mecat.catalog.option.LocalListenerEvent;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.WeakLocalListener;
import net.sourceforge.mecat.catalog.option.preferences.AbstractMediaOption;
import net.sourceforge.mecat.srp.utils.NiceClass;

public class SelectFeatureClass extends JDialog implements LocalListener {

    final Listing listing;
    Class<? extends Feature> result;
    
    Vector<NiceClass<Feature>> all = new Vector<NiceClass<Feature>>();
    Vector<NiceClass<Feature>> selection = new Vector<NiceClass<Feature>>();
    JCheckBox showAll = new SimpleLocalCheckBox(Options.getI18N(SelectFeatureClass.class), "Show all features", false);
    
    final JButton accept = new SimpleLocalButton(Options.getI18N(ExportChoice.class), "OK");
    final JButton cancel = new SimpleLocalButton(Options.getI18N(ExportChoice.class), "Cancel");

    final JList list = new JList();
    
    public static Class<? extends Feature> showSelectFeature(Component component, Listing listing) {
        SelectFeatureClass selector;

        while (component != null && !(component instanceof Frame) && !(component instanceof Dialog))
            component = component.getParent();

        if (component == null)
            selector = new SelectFeatureClass(listing);
        else if (component instanceof Dialog)
            selector = new SelectFeatureClass((Dialog)component, listing);
        else
            selector = new SelectFeatureClass((Frame)component, listing);
        
        selector.setVisible(true);

        return selector.getResult();
    }

    public SelectFeatureClass(Listing listing) throws HeadlessException {
        super();
        this.listing = listing;
        init();
    }

    public SelectFeatureClass(Dialog arg0, Listing listing) throws HeadlessException {
        super(arg0);
        this.listing = listing;
        init();
    }

    public SelectFeatureClass(Frame arg0, Listing listing) throws HeadlessException {
        super(arg0);
        this.listing = listing;
        init();
    }
    
    private void init() {
        for (Class<? extends Feature> feature : AbstractMediaOption.getFeatures())
            all.add(new NiceClass<Feature>(feature));
        
        // Only need the features directly accessable 
        // therefor use getFeatures and not getAllFeatures
        for (Class<? extends Feature> feature : AbstractMediaOption.getFeatures(listing.getTypes()))
            selection.add(new NiceClass<Feature>(feature));

        Collections.sort(all, new Comparator<NiceClass<Feature>>(){
            public int compare(NiceClass<Feature> arg0, NiceClass<Feature> arg1) {
                return arg0.toString().compareTo(arg1.toString());
            }
        });
        Collections.sort(selection, new Comparator<NiceClass<Feature>>(){
            public int compare(NiceClass<Feature> arg0, NiceClass<Feature> arg1) {
                return arg0.toString().compareTo(arg1.toString());
            }
        });
        
        list.setListData(selection);
        list.getSelectionModel().addListSelectionListener(new ListSelectionListener(){

            public void valueChanged(ListSelectionEvent arg0) {
                accept.setEnabled(list.getSelectedValue() instanceof NiceClass);
            }
            
        });
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(accept);
        buttonPanel.add(cancel);

        accept.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                result = ((NiceClass<Feature>)list.getSelectedValue()).getClasstype();
                setVisible(false);
            }
        });
        accept.setEnabled(false);
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                setVisible(false);
            }
        });
        
        showAll.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                list.setSelectedIndex(-1);
                if (showAll.isSelected())
                    list.setListData(all);
                else
                    list.setListData(selection);
            }
        });

        setLayout(new BorderLayout());
        add(showAll, BorderLayout.NORTH);
        add(new JScrollPane(list));
        add(buttonPanel, BorderLayout.SOUTH);
    
        setSize(new Dimension(300,400));
        setResizable(false);
        setModal(true);

        Options.addLocalListener(new WeakLocalListener(this));
        // Set labels to the current locale
        setLabels();
    }

    public void stateChanged(LocalListenerEvent event) {
        setLabels();
    }
    
    public void setLabels() {
        setTitle(Options.getI18N(SelectFeatureClass.class).getString("Select feature typ"));
        showAll.setToolTipText(Options.getI18N(SelectFeatureClass.class).getString("Toggle between all feature and only those features used in current catalog."));
    }

    public Class<? extends Feature> getResult() {
        return result;
    }
    
    
}
