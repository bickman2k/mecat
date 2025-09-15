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
 * TODO The border and the name perhaps should be in
 *             FeaturePanel to
 */
package net.sourceforge.mecat.catalog.gui.features;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.mecat.catalog.gui.MainFrameBackend;
import net.sourceforge.mecat.catalog.gui.ToolBarUtils;
import net.sourceforge.mecat.catalog.gui.features.desktop.FeatureDesktop;
import net.sourceforge.mecat.catalog.i18n.fromCatalog.LayeredResourceBundle;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.medium.features.MultiChoiceFeature;
import net.sourceforge.mecat.catalog.option.Options;



public class MultiChoiceFeatureSingleListPanel extends FeaturePanel<MultiChoiceFeature> {

    protected JList list = new JList(); 
    protected JPanel buttonPanel = new JPanel();

    // Get resources for i18n of the names
    ResourceBundle choiceResources = null;
    
    
    final JButton addButton = ToolBarUtils.makeButton("plus", "", Options.getI18N(MainFrameBackend.class).getString("Add"), Options.getI18N(MainFrameBackend.class).getString("Add"), new ActionListener(){ 
        public void actionPerformed(ActionEvent arg0) {
            add();
        }});
    final JButton removeButton = ToolBarUtils.makeButton("kill", "", Options.getI18N(MainFrameBackend.class).getString("Remove"), Options.getI18N(MainFrameBackend.class).getString("Remove"), new ActionListener(){ 
        public void actionPerformed(ActionEvent arg0) {
            remove();
        }});

    public MultiChoiceFeatureSingleListPanel(MultiChoiceFeature feature, FeatureDesktop desktop) {
        this(feature, desktop, null);
    }
	public MultiChoiceFeatureSingleListPanel(MultiChoiceFeature feature, FeatureDesktop desktop, final LayeredResourceBundle extraResources) {
		this(feature, desktop, true, extraResources);
	}
	
	public MultiChoiceFeatureSingleListPanel(MultiChoiceFeature feature, FeatureDesktop desktop, boolean border) {
        this(feature, desktop, border, null);
    }
    public MultiChoiceFeatureSingleListPanel(final MultiChoiceFeature feature, FeatureDesktop desktop, boolean border, final LayeredResourceBundle extraResources) {
		super(feature, desktop, border, feature.attributeName, extraResources);

		// Get resources for i18n of the names
		choiceResources = feature.getChoiceResourceBundle();

        /* There is a listener that makes a full rebuild for the hardcodeddesktop
         * every time the location changes
        Options.addLocalListener(new ChangeListener(){
            public void stateChanged(ChangeEvent arg0) {
                // Get resources for i18n of the names
                choiceResources = feature.getChoiceResourceBundle();
                
                addButton.setToolTipText(Options.getI18N(MainFrameBackend.class).getString("Add"));
                removeButton.setToolTipText(Options.getI18N(MainFrameBackend.class).getString("Remove"));

                updateList();
            }
        });*/
        
        removeButton.setEnabled(false);

        list.addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent arg0) {
                removeButton.setEnabled(list.getSelectedValue() instanceof Couple);
            }
        });
        
        updateList();
        
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        
        add(list);
        add(buttonPanel, BorderLayout.SOUTH);
	}

    class Couple implements Comparable {
        public String name;
        public String displayName;

        public Couple(String name, String displayName) {
            // TODO Auto-generated constructor stub
            this.displayName = displayName;
            this.name = name;
        }

        public int compareTo(Object o) {
            Couple c = (Couple) o;
            return displayName.compareTo(c.displayName);
        }
        
        public String toString() {
            return displayName;
        }
    }

    protected void add() {
        Vector<Couple> choices = new Vector<Couple>();
        
        for(String choice : feature.getChoices())
            choices.add(new Couple(choice, choiceResources.getString(choice)));
        Collections.sort(choices);

        Couple c = ( Couple ) JOptionPane.showInputDialog(this, 
                res.getString("Add choice"),
                res.getString("Chose the choice you want to add"), 
                JOptionPane.PLAIN_MESSAGE,
                null, 
                choices.toArray(),
                choices.firstElement());

        if (c != null)
            feature.add(c.name);
    }
    
    protected void remove() {
        Object val = list.getSelectedValue();
        if (!(val instanceof Couple))
            return;
        
        Couple c = ( Couple ) val;
        feature.remove(c.name);
    }
    
    protected void updateList() {

        Vector<Couple> vals = new Vector<Couple>();
        for (String s : feature.getSelection())
            vals.add(new Couple(s, choiceResources.getString(s)));
        Collections.sort(vals);
        
        list.setListData(vals);
//        list.updateUI();
//        revalidate();
    }

    public void requestFocus() {
        list.requestFocus();
    }
    
    public boolean hasFocus() {
        return list.hasFocus();
    }
    public void featureValueChanged(Feature source) {
        updateList();
    }
}
