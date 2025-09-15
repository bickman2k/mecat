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
 * Created on Jun 5, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filesystem.gui;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;

import net.sourceforge.mecat.catalog.gui.features.desktop.ChainableFeatureDesktop;
import net.sourceforge.mecat.catalog.gui.features.desktop.FeatureDesktop;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.impl.RomFileList;
import net.sourceforge.mecat.catalog.medium.hidden.Rom;
import net.sourceforge.mecat.catalog.option.LocalListener;
import net.sourceforge.mecat.catalog.option.LocalListenerEvent;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.WeakLocalListener;

public class ExplorerDesktop implements FeatureDesktop, LocalListener {

    Medium medium = null;
    final JLabel none = new JLabel();
    JComponent component = none;
    JPanel desktop = new JPanel();
    Explorer explorer = new Explorer();
    
    final Class<? extends RomFileList> c;
    
    public ExplorerDesktop(ChainableFeatureDesktop chainableDesktop, Class<? extends RomFileList> c) {
        this.c = c;
        
        desktop.setLayout(new BorderLayout());
        desktop.add(component);
        
        chainableDesktop.addDesktop(this);
        Options.addLocalListener(new WeakLocalListener(this));
        // Set labels to the current locale
        setLabels();
    }

    public void stateChanged(LocalListenerEvent event) {
        setLabels();
    }
    
    public void setLabels() {
        none.setText(Options.getI18N(ExplorerDesktop.class).getString("No exploration possible."));
    }
    
    public void requestFocus() {
    }

    public JComponent getDesktop() {
        return desktop;
    }

    public void setMedium(Medium medium) {
        this.medium = medium;
        if (medium instanceof Rom) {
            Rom rom = (Rom) medium;
            RomFileList fileList = rom.getFeature(c);
            if (fileList.hasResult()) {
                explorer.setResult(fileList.getResult());
                desktop.remove(component);
                component = explorer;
                desktop.add(component);
                desktop.revalidate();
                return;
            }
        }
        
        if (component == none)
            return;
        
        desktop.remove(component);
        component = none;
        desktop.add(component);
    }

    public Explorer getExplorer() {
        return explorer;
    }
    
    public void saveSettings() {
        // TODO Auto-generated method stub
        
    }

    public void loadSettings() {
        // TODO Auto-generated method stub
        
    }

    public void setPreferredDesktopWidth(int width) {
        // TODO Auto-generated method stub
        
    }

    public void Rebuild() {
        // TODO Auto-generated method stub
        
    }

}
