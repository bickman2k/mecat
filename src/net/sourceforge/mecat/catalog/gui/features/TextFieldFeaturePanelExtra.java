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
 */
package net.sourceforge.mecat.catalog.gui.features;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

import net.sourceforge.mecat.catalog.export.ExportProgressVisualisation;
import net.sourceforge.mecat.catalog.gui.catalogDetails.CatalogDetails;
import net.sourceforge.mecat.catalog.gui.features.desktop.ChainableFeatureDesktop;
import net.sourceforge.mecat.catalog.gui.features.desktop.hardcoded.HardCodedDesktop;
import net.sourceforge.mecat.catalog.medium.features.TextFeature;
import net.sourceforge.mecat.catalog.option.LocalListener;
import net.sourceforge.mecat.catalog.option.LocalListenerEvent;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.WeakLocalListener;

public class TextFieldFeaturePanelExtra implements LocalListener {

    JButton close = new JButton();
    final ChainableFeatureDesktop desktop;
    final HardCodedDesktop spawnDesktop;
    final ResourceBundle res;
    final TextFeature feature;
    final Display display;

    public static interface Display {

        public void setSize(Dimension dimension);
        public Container getContentPane();
        public void close();
        public void addCloseListener(ActionListener closeListener);
        public void removeCloseListener(ActionListener closeListener);
        public void setTitle(String string) ;
        public void setVisible(boolean visible);
        
    }
    
    public static class DialogDisplay extends JDialog implements Display {

        Vector<ActionListener> closeListeners = new Vector<ActionListener>();
        
        public DialogDisplay() throws HeadlessException {
        }

        public DialogDisplay(Dialog arg0) throws HeadlessException {
            super(arg0);
        }

        public DialogDisplay(Frame arg0) throws HeadlessException {
            super(arg0);
        }

        public void addCloseListener(ActionListener closeListener) {
            closeListeners.add(closeListener);
        }

        public void removeCloseListener(ActionListener closeListener) {
            closeListeners.add(closeListener);
        }

        public void close() {
            this.processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }

        protected void fireClose() {
            for (ActionListener closeListener : closeListeners)
                closeListener.actionPerformed(null);
        }

        protected void processWindowEvent(WindowEvent e) {
            super.processWindowEvent(e);
            if (e.getID() == WindowEvent.WINDOW_CLOSING) 
                fireClose();
        }
    }
    
    public static class FrameDisplay extends JFrame implements Display {

        Vector<ActionListener> closeListeners = new Vector<ActionListener>();
        
        public void addCloseListener(ActionListener closeListener) {
            closeListeners.add(closeListener);
        }

        public void removeCloseListener(ActionListener closeListener) {
            closeListeners.add(closeListener);
        }

        public void close() {
            this.processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }

        protected void fireClose() {
            for (ActionListener closeListener : closeListeners)
                closeListener.actionPerformed(null);
        }

        protected void processWindowEvent(WindowEvent e) {
            super.processWindowEvent(e);
            if (e.getID() == WindowEvent.WINDOW_CLOSING) 
                fireClose();
        }
    }
    
    static public void showTextFieldFeaturePanelExtra(Component component, TextFeature feature, ResourceBundle res, final ChainableFeatureDesktop desktop) {
        Display display = null;
        
        while (component != null && !(component instanceof Frame) && !(component instanceof Dialog))
            component = component.getParent();

        if (component == null)
            display = new DialogDisplay();
        else if (component instanceof Dialog)
            display = new DialogDisplay((Dialog)component);
        else
            display = new FrameDisplay();
        
        TextFieldFeaturePanelExtra extra = new TextFieldFeaturePanelExtra(display, feature, res, desktop);
    }
    
    public TextFieldFeaturePanelExtra (final Display display, TextFeature feature, ResourceBundle res, final ChainableFeatureDesktop desktop) {
        this.feature = feature;
        this.res = res;
        this.desktop = desktop;
        this.display = display;
        display.setSize(new Dimension(640, 480));
        
        spawnDesktop = new HardCodedDesktop(feature.getClass());
        desktop.addDesktop(spawnDesktop);
        spawnDesktop.setMedium(feature.medium);
        
        display.getContentPane().setLayout(new BorderLayout());
        display.getContentPane().add(spawnDesktop.getDesktop());
        display.getContentPane().add(close, BorderLayout.SOUTH);
        
        close.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                display.close();
            }});
        
        display.addCloseListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                desktop.removeDesktop(spawnDesktop);
            }
        });

        Options.addLocalListener(new WeakLocalListener(this));
        // Set labels to the current locale
        setLabels();
        
        display.setVisible(true);
    }
    
    public void stateChanged(LocalListenerEvent event) {
        setLabels();
    }
    
    public void setLabels() {
        close.setText(Options.getI18N(ExportProgressVisualisation.class).getString("Close"));
        display.setTitle(res.getString(feature.getClass().getSimpleName()));
    }    

    public void requestFocus() {
        // Does not need focus
    }
    
    public boolean hasFocus() {
        // Does not has focus
        return false;
    }
}
