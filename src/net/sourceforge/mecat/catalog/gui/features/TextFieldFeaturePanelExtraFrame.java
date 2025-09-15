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
 * Created on Feb 1, 2006
 * @author Stephan Richard Palm
 * 
 * @deprecated
 */
package net.sourceforge.mecat.catalog.gui.features;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JFrame;

import net.sourceforge.mecat.catalog.export.ExportProgressVisualisation;
import net.sourceforge.mecat.catalog.gui.features.desktop.ChainableFeatureDesktop;
import net.sourceforge.mecat.catalog.gui.features.desktop.hardcoded.HardCodedDesktop;
import net.sourceforge.mecat.catalog.medium.features.TextFeature;
import net.sourceforge.mecat.catalog.option.LocalListener;
import net.sourceforge.mecat.catalog.option.LocalListenerEvent;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.WeakLocalListener;

public class TextFieldFeaturePanelExtraFrame extends JFrame implements LocalListener {

    JButton close = new JButton();
    final ChainableFeatureDesktop desktop;
    final HardCodedDesktop spawnDesktop;
    final ResourceBundle res;
    final TextFeature feature;
   
    public TextFieldFeaturePanelExtraFrame (TextFeature feature, ResourceBundle res, ChainableFeatureDesktop desktop) {
        this.feature = feature;
        this.res = res;
        this.desktop = desktop;
        this.setSize(new Dimension(640, 480));
        
        spawnDesktop = new HardCodedDesktop(feature.getClass());
        desktop.addDesktop(spawnDesktop);
        spawnDesktop.setMedium(feature.medium);
        
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(spawnDesktop.getDesktop());
        this.getContentPane().add(close, BorderLayout.SOUTH);
        
        close.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                TextFieldFeaturePanelExtraFrame.this.processWindowEvent(new WindowEvent(TextFieldFeaturePanelExtraFrame.this, WindowEvent.WINDOW_CLOSING));
            }});

        Options.addLocalListener(new WeakLocalListener(this));
        // Set labels to the current locale
        setLabels();
    }

    public void close(){
        TextFieldFeaturePanelExtraFrame.this.processWindowEvent(new WindowEvent(TextFieldFeaturePanelExtraFrame.this, WindowEvent.WINDOW_CLOSING));
    }
    
    public void stateChanged(LocalListenerEvent event) {
        setLabels();
    }
    
    public void setLabels() {
        close.setText(Options.getI18N(ExportProgressVisualisation.class).getString("Close"));
        setTitle(res.getString(feature.getClass().getSimpleName()));
    }    

    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) 
            desktop.removeDesktop(spawnDesktop);
    }
    public void requestFocus() {
        // Does not need focus
    }
    
    public boolean hasFocus() {
        // Does not has focus
        return false;
    }
}
