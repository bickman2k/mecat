/*
 *  MeCat - Media Catalog more information on
 *  http://mecat.sourceforge.net/
 *  Copyright (C) 2005, Stephan Richard Palm, All Rights Reserved.
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
 * Created on Oct 24, 2005
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import net.sourceforge.mecat.catalog.datamanagement.Catalog;
import net.sourceforge.mecat.catalog.export.ExportChoice;
import net.sourceforge.mecat.catalog.filter.Filter;
import net.sourceforge.mecat.catalog.gui.MainFrameBackend.Display;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalButton;
import net.sourceforge.mecat.catalog.option.LocalListener;
import net.sourceforge.mecat.catalog.option.LocalListenerEvent;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.WeakLocalListener;
import net.sourceforge.mecat.catalog.sort.ConfigurableComparator;

public class ShowCatalogFrontend extends JDialog implements Display, LocalListener {

    ShowCatalogBackend showCatalogBackend = newBackend();
    final JButton accept = new SimpleLocalButton(Options.getI18N(ExportChoice.class), "OK");
    
    // The reason for this function is
    // to allow extension to override the functionality
    public ShowCatalogBackend newBackend(){
        return new ShowCatalogBackend(this);
    }
    
    public static void showShowCatalogFrontend(Component component, Catalog catalog, Filter filterLevel1) {
        showShowCatalogFrontend(component, catalog, filterLevel1, (Filter)null);
    }
    public static void showShowCatalogFrontend(Component component, Catalog catalog, Filter filterLevel1, ConfigurableComparator sorting) {
        showShowCatalogFrontend(component, catalog, filterLevel1, null, sorting);
    }
    public static void showShowCatalogFrontend(Component component, Catalog catalog, Filter filterLevel1, Filter filterLevel2) {
        showShowCatalogFrontend(component, catalog, filterLevel1, filterLevel2, null);
    }
    public static void showShowCatalogFrontend(Component component, Catalog catalog, Filter filterLevel1, Filter filterLevel2, ConfigurableComparator sorting) {
        showShowCatalogFrontend(component, catalog, filterLevel1, filterLevel2, sorting, true);
    }
    public static void showShowCatalogFrontend(Component component, Catalog catalog, Filter filterLevel1, Filter filterLevel2, ConfigurableComparator sorting, boolean level0FilterEnabled) {
        ShowCatalogFrontend selector;

        while (component != null && !(component instanceof Frame) && !(component instanceof Dialog))
            component = component.getParent();

        if (component == null)
            selector = new ShowCatalogFrontend();
        else if (component instanceof Dialog)
            selector = new ShowCatalogFrontend((Dialog)component);
        else
            selector = new ShowCatalogFrontend((Frame)component);
        
        if (!level0FilterEnabled)
            selector.getBackend().setLevel0FilterEnabled(false);
        
        selector.setCatalog(catalog);
        selector.setFilter(filterLevel1);
        selector.setSearch(filterLevel2);
        selector.setSorting(sorting);

        selector.setVisible(true);
    }

    public ShowCatalogFrontend() {
        init();
    }

    public ShowCatalogFrontend(Dialog dialog) {
        super(dialog);
        init();
    }
        
    public ShowCatalogFrontend(Frame frame) {
        super(frame);
        init();
    }
        
    private void init() {
            
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        this.setSize(new Dimension(640, 480));
        this.setModal(true);

        accept.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                setVisible(false);
            }
        });
        
        Container backEnd = getContentPane();
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(accept);
        
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(backEnd);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(contentPane);
        
        getBackend().splitListAndFeatures.setDividerLocation(300);

        Options.addLocalListener(new WeakLocalListener(this));
        // Set labels to the current locale
        setLabels();
        
        accept.requestFocus();
    }

    public void stateChanged(LocalListenerEvent event) {
        setLabels();
    }
    
    public void setLabels() {
        this.setTitle(Options.getI18N(Catalog.class).getString("Catalog"));
    }

    public ShowCatalogBackend getBackend() {
        return showCatalogBackend;
    }

    public Window getWindow() {
        return this;
    }

    public void setExtendedState(int state) {
    }

    public int getExtendedState() {
        return 0;
    }
    
    public void setCatalog(Catalog catalog) {
        getBackend().setCatalog(catalog);
    }
    
    public void setFilter(Filter filter) {
        getBackend().setFilter(filter);
    }
    
    public void setSearch(Filter filter) {
        getBackend().setSearch(filter);
    }
    
    public void setSorting(ConfigurableComparator sorting) {
        getBackend().setComparator(sorting);
    }
}
