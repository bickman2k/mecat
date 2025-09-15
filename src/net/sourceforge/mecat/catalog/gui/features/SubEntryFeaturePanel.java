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

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.TitledBorder;

import net.sourceforge.mecat.catalog.gui.ToolBarUtils;
import net.sourceforge.mecat.catalog.gui.features.desktop.FeatureDesktop;
import net.sourceforge.mecat.catalog.gui.features.desktop.hardcoded.HardCodedDesktop;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalButton;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.medium.features.SubEntryFeature;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.srp.utils.ButtonBorder;

public class SubEntryFeaturePanel extends FeaturePanel<SubEntryFeature> {

    TitledBorder titledBorder = null;
    ButtonBorder killBorder = null; 
    
    final boolean link;

    HardCodedDesktop hardDesktop = new HardCodedDesktop(false);
    JComponent jDesktop = null;
    
    Medium medium = null;

    SimpleLocalButton useExisting = new SimpleLocalButton(Options.getI18N(SubEntryFeaturePanel.class), "Use existing");
    SimpleLocalButton createNew = new SimpleLocalButton(Options.getI18N(SubEntryFeaturePanel.class), "Create new");
    JPanel buttonPanel = new JPanel();
    
	public SubEntryFeaturePanel(final SubEntryFeature feature, FeatureDesktop desktop, boolean border, boolean link) 
	{
		super(feature, desktop, border, feature.attributeName);
		this.link = link;
        
        if (border){
            titledBorder = (TitledBorder) getBorder();

            JButton buttons[] = new JButton[1];
            buttons[0] = ToolBarUtils.makeMiniButton("kill", "", "", "", new ActionListener(){
                public void actionPerformed(ActionEvent arg0) {
                    feature.delete();
                    checkState();
                }
            });
            killBorder = new ButtonBorder(null, titledBorder, buttons);
        }
        
        useExisting.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                feature.linkExisting(SubEntryFeaturePanel.this);
                checkState();
            }
        });
        
        createNew.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                feature.newMediumCatalog();
                checkState();
            }
        });

        buttonPanel.setLayout(new GridLayout(1,0));
        buttonPanel.add(useExisting);
        buttonPanel.add(createNew);
        
        checkState();
	}

    protected void checkState() {
        medium = feature.getSubEntryMedium();
        if (medium != null) {
            hardDesktop.setMedium(medium);
            removeAll();
            jDesktop = hardDesktop.getDesktop();
            add(jDesktop);
            if (border)
                if (link) {
                    setBorder(killBorder);
                    // Kill border does need to get mouse events
                    killBorder.changeDestinationComponent(this);
                } else 
                    setBorder(titledBorder);
        } else {
            // Kill border does not need to get mouse events
            killBorder.changeDestinationComponent(null);
            removeAll();
            add(buttonPanel);
            if (border)
                setBorder(titledBorder);
        }
        desktop.Rebuild();
            
    }
    
    public void requestFocus() {
        if (medium != null)
            jDesktop.requestFocus();
        else {
            if (link) {
                useExisting.requestFocus();
            } else {
                createNew.requestFocus();
            }
        }
    }
    
    public boolean hasFocus() {
        if (jDesktop != null && jDesktop.hasFocus())
            return true;
        if (useExisting.hasFocus())
            return true;
        if (createNew.hasFocus())
            return true;

        return false;
    }

    public void featureValueChanged(Feature source) {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected JPopupMenu getJPopupMenu() {
        return new JPopupMenu();
    }
    
    
}
