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
 * Created on Oct 6, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.gui.features;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.mecat.catalog.gui.ToolBarUtils;
import net.sourceforge.mecat.catalog.gui.features.desktop.FeatureDesktop;
import net.sourceforge.mecat.catalog.gui.features.desktop.hardcoded.HardCodedDesktop;
import net.sourceforge.mecat.catalog.gui.features.desktop.hardcoded.ShowHardCodedDesktop;
import net.sourceforge.mecat.catalog.gui.utils.PopupMouseListener;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.PointedMedium;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.medium.features.SubEntryListFeature;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.srp.utils.ButtonBorder;

public class SubEntryListFeaturePanel extends FeaturePanel<SubEntryListFeature> {

	JList list;
    
    ButtonBorder noEditBorder = null; 
    ButtonBorder editBorder = null; 

	public SubEntryListFeaturePanel(SubEntryListFeature feature, FeatureDesktop desktop, boolean border) 
	{
		super(feature, desktop, border, feature.attributeName);

        if (border){
            TitledBorder titledBorder = (TitledBorder) getBorder();

            JButton buttons[] = new JButton[2];
            buttons[0] = ToolBarUtils.makeMiniButton("edit", "", "", "", new ActionListener(){
                public void actionPerformed(ActionEvent arg0) {
                    editEntry();
                }
            });
            buttons[1] = ToolBarUtils.makeMiniButton("plus", "", "", "", new ActionListener(){
                public void actionPerformed(ActionEvent arg0) {
                    addEntry();
                }
            });
            editBorder = new ButtonBorder(null, titledBorder, buttons);
            
            buttons = new JButton[1];
            buttons[0] = ToolBarUtils.makeMiniButton("plus", "", "", "", new ActionListener(){
                public void actionPerformed(ActionEvent arg0) {
                    addEntry();
                }
            });
            noEditBorder = new ButtonBorder(null, titledBorder, buttons);
        }
        
		
		Vector<PointedMedium<? extends Medium>> media = feature.getMedia();
		list = new JList(media);
        
        // Make an upgrade to the allready existing border
        if (border) 
            list.addListSelectionListener(new ListSelectionListener(){
                public void valueChanged(ListSelectionEvent arg0) {
                    checkBorder();
                }
            });

		list.addMouseListener(new PopupMouseListener(){
            public JPopupMenu getPopupMenu() {
                return SubEntryListFeaturePanel.this.getJPopupMenu();
            }
        });
        list.addMouseListener(new MouseListener(){

            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2)
                    editEntry();
            }
            public void mouseEntered(MouseEvent arg0) {}
            public void mouseExited(MouseEvent arg0) {}
            public void mousePressed(MouseEvent arg0) {}
            public void mouseReleased(MouseEvent arg0) {}
        });
		
		add(list);
        checkBorder();
	}

    public void checkBorder() {
        // If there is no border
        // then there is no need to check
        if (!border) 
            return;

        if (list.getSelectedIndex() == -1) {
            editBorder.changeDestinationComponent(null);
            noEditBorder.changeDestinationComponent(this);
        } else {
            noEditBorder.changeDestinationComponent(null);
            editBorder.changeDestinationComponent(this);
        }
    }
    
	public void addEntry() {
        startTransaction("Add Entry", false, true);
        
		// Create new Medium
        PointedMedium medium = feature.newMedium();

		// Show new Medium
        HardCodedDesktop desktop = new HardCodedDesktop();
        desktop.setMedium(medium.getMedium());

        
//		int ret = JOptionPane.showConfirmDialog(SubEntryListFeaturePanel.this, desktop.getDesktop(), "New " + feature.attributeName,  JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
//		if (ret != JOptionPane.OK_OPTION)
        boolean accepted = ShowHardCodedDesktop.showHardCodedDesktop(this, desktop, Options.getI18N(SubEntryListFeaturePanel.class).getString("New [TYPE]"));
        if (!accepted)
			feature.remove(medium);

		// Update the visual
		list.setListData(feature.getMedia());
		list.updateUI();
		fireRebuild();

        stopTransaction();
    }
	
	public void removeEntry() {
		// Remove Medium
		feature.remove((PointedMedium)list.getSelectedValue());
		
		// Update the visual
		list.setListData(feature.getMedia());
		list.updateUI();
		fireRebuild();
	}
	
	public void editEntry() {
        // Don't go any further if there is no entry selected
        if (list.getSelectedIndex() == -1)
            return;
        
		// Edit Medium
        HardCodedDesktop desktop = new HardCodedDesktop();

		// Show the medium that shall be editet.
        desktop.setMedium(((PointedMedium)list.getSelectedValue()).getMedium());
//		JOptionPane.showConfirmDialog(SubEntryListFeaturePanel.this, desktop.getDesktop(), "New " + feature.attributeName,  JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        ShowHardCodedDesktop.showHardCodedDesktop(this, desktop, Options.getI18N(SubEntryListFeaturePanel.class).getString("Edit [TYPE]"), false);

		// Update the visual
		list.updateUI();
	}
	
	
	protected JPopupMenu getJPopupMenu() {
		JPopupMenu menu = super.getJPopupMenu();
        
        final JMenuItem add = new JMenuItem(Options.getI18N(SubEntryListFeaturePanel.class).getString("Add"));
        final JMenuItem remove = new JMenuItem(Options.getI18N(SubEntryListFeaturePanel.class).getString("Remove"));
        final JMenuItem edit = new JMenuItem(Options.getI18N(SubEntryListFeaturePanel.class).getString("Edit"));

        add.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {/*LOOK*/ addEntry(); /*HERE*/}});
		remove.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {/*LOOK*/ removeEntry(); /*HERE*/}});
		edit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {/*LOOK*/ editEntry(); /*HERE*/}});
        
        remove.setEnabled(list != null && (list.getSelectedIndex() >= 0));
        edit.setEnabled(list != null && (list.getSelectedIndex() >= 0));

        menu.addSeparator();
		menu.add(add);
		menu.add(edit);
		menu.add(remove);
		
		return menu;
	}
    public void requestFocus() {
        list.requestFocus();
    }
    
    public boolean hasFocus() {
        return list.hasFocus();
    }

    public void featureValueChanged(Feature source) {
        list.setListData(feature.getMedia());
        list.updateUI();
        fireRebuild();
    }
}
