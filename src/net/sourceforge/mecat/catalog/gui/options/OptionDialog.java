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
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import net.sourceforge.mecat.catalog.export.ExportChoice;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalButton;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.option.LocalListener;
import net.sourceforge.mecat.catalog.option.LocalListenerEvent;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.WeakLocalListener;
import net.sourceforge.mecat.catalog.option.preferences.AbstractPreferences;
import net.sourceforge.mecat.srp.utils.NiceClass;

public class OptionDialog extends JDialog implements LocalListener{

    PreferenceTreeModel treeModel = new PreferenceTreeModel();
    JPanel optionPanel = new JPanel();
    JTree tree = new JTree(treeModel);
    final AbstractPreferences preferences;
    boolean accepted = false;
    
    public boolean isAccepted() {
        return accepted;
    }
    
    public OptionDialog(final AbstractPreferences preferences) {
        this.preferences = preferences;
        
        tree.setRootVisible(false);
        tree.expandPath(new TreePath(new Object[]{treeModel, treeModel.mediaNode}));
        tree.expandPath(new TreePath(new Object[]{treeModel, treeModel.featureNode}));
        tree.addTreeSelectionListener(new TreeSelectionListener(){
            public void valueChanged(TreeSelectionEvent arg0) {
                treeSelectionChanged();
            }
        });
        
        optionPanel.setLayout(new BorderLayout());
        
        setLayout(new BorderLayout());
        
        JSplitPane splitPane = new JSplitPane();
        splitPane.add(new JScrollPane(tree), JSplitPane.LEFT);
        splitPane.add(optionPanel, JSplitPane.RIGHT);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(new SimpleLocalButton(Options.getI18N(ExportChoice.class), "OK"){{
            addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent arg0) {
                    accepted = true;
                    ok();
                }
            });
        }});
        buttonPanel.add(new SimpleLocalButton(Options.getI18N(ExportChoice.class), "Cancel"){{
            addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent arg0) {
                    cancel();
                }
            });
        }});
        
        
        add(splitPane);
        add(buttonPanel, BorderLayout.SOUTH);
        
        setSize(600, 600);
        setModal(true);
        setResizable(false);

        Options.addLocalListener(new WeakLocalListener(this));
        // Set labels to the current locale
        setLabels();
    }

    public void stateChanged(LocalListenerEvent event) {
        setLabels();
    }
    
    public void setLabels() {
        setTitle(Options.getI18N(preferences.getClass()).getString("Preferences"));
    }

    protected void treeSelectionChanged() {
        Object selection = tree.getSelectionPath().getLastPathComponent();
        if (selection instanceof NiceClass) {
            NiceClass nc = (NiceClass) selection;
            final Class c = nc.getClasstype();
            if (Medium.class.isAssignableFrom(c)) {
                optionPanel.removeAll();
                optionPanel.add(new CatalogMediumOptionPanel((Class<? extends Medium>)c, preferences.getMediaOption()));
                optionPanel.updateUI();
            }
            if (Feature.class.isAssignableFrom(c)) {
                optionPanel.removeAll();
                optionPanel.add(new CatalogFeatureOptionPanel((Class<? extends Feature>)c, preferences.getFeaturesOption()));
                optionPanel.updateUI();
            }
        }
        if (selection == treeModel.mediaNode) {
            optionPanel.removeAll();
            optionPanel.add(new CatalogMediaOptionPanel(preferences.getMediaOption()));
            optionPanel.updateUI();
        }
        if (selection == treeModel.generalNode) {
            optionPanel.removeAll();
            optionPanel.add(new CatalogGeneralOptionPanel(preferences.getGeneralOption()));
            optionPanel.updateUI();
        }
    }

    protected void ok(){
        setVisible(false);
    }
    protected void cancel(){
        setVisible(false);
    }

    public void setSelection(Object selection) {
        tree.setSelectionPath(treeModel.getPath(selection));
    }



    public static void setEnabled(Component component, boolean enabled) {
        component.setEnabled(enabled);
        if (component instanceof JComponent)
            for (Component comp : ((JComponent)component).getComponents())
                setEnabled(comp, enabled);
    }

}
