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
 * Created on Jun 22, 2005
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.mecat.catalog.datamanagement.Catalog;
import net.sourceforge.mecat.catalog.export.ExportChoice;
import net.sourceforge.mecat.catalog.filter.Filter;
import net.sourceforge.mecat.catalog.gui.MainFrameBackend.Display;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalButton;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.option.LocalListener;
import net.sourceforge.mecat.catalog.option.LocalListenerEvent;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.WeakLocalListener;
import net.sourceforge.mecat.catalog.sort.ConfigurableComparator;

public class SelectMediaFrontend extends JDialog implements Display, LocalListener {

    SelectMediaBackend selectMediaBackend = newBackend();
    final JButton accept = new SimpleLocalButton(Options.getI18N(ExportChoice.class), "OK");
    final JButton cancel = new SimpleLocalButton(Options.getI18N(ExportChoice.class), "Cancel");

    Medium result = null;
    
    public Medium getResult() {
        return result;
    }
    
    // The reason for this function is
    // to allow extension to override the functionality
    public SelectMediaBackend newBackend(){
        return new SelectMediaBackend(this);
    }
    
    public static Medium showSelectMediaFrontend(Component component, Catalog catalog, Filter filterLevel1) {
        return showSelectMediaFrontend(component, catalog, filterLevel1, (Filter)null);
    }
    public static Medium showSelectMediaFrontend(Component component, Catalog catalog, Filter filterLevel1, ConfigurableComparator sorting) {
        return showSelectMediaFrontend(component, catalog, filterLevel1, null, sorting);
    }
    public static Medium showSelectMediaFrontend(Component component, Catalog catalog, Filter filterLevel1, Filter filterLevel2) {
        return showSelectMediaFrontend(component, catalog, filterLevel1, filterLevel2, null);
    }
    public static Medium showSelectMediaFrontend(Component component, Catalog catalog, Filter filterLevel1, Filter filterLevel2, ConfigurableComparator sorting) {
        return showSelectMediaFrontend(component, catalog, filterLevel1, filterLevel2, sorting, true);
    }
    public static Medium showSelectMediaFrontend(Component component, Catalog catalog, Filter filterLevel1, Filter filterLevel2, ConfigurableComparator sorting, boolean level0FilterEnabled) {
        SelectMediaFrontend selector;

        while (component != null && !(component instanceof Frame) && !(component instanceof Dialog))
            component = component.getParent();

        if (component == null)
            selector = new SelectMediaFrontend();
        else if (component instanceof Dialog)
            selector = new SelectMediaFrontend((Dialog)component);
        else
            selector = new SelectMediaFrontend((Frame)component);
        
        if (!level0FilterEnabled)
            selector.getBackend().setLevel0FilterEnabled(false);
        
        selector.setCatalog(catalog);
        selector.setFilter(filterLevel1);
        selector.setSearch(filterLevel2);
        selector.setSorting(sorting);

        selector.setVisible(true);

        return selector.getResult();
    }

    public SelectMediaFrontend() {
        init();
    }

    public SelectMediaFrontend(Dialog dialog) {
        super(dialog);
        init();
    }
        
    public SelectMediaFrontend(Frame frame) {
        super(frame);
        init();
    }
        
    private void init() {
            
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        this.setSize(new Dimension(640, 480));
        this.setModal(true);

        accept.setEnabled(false);
        getBackend().list.addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent arg0) {
                accept.setEnabled(getMedium() != null);
            }
        });
        
        accept.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                result = getMedium();
                setVisible(false);
            }
        });
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                setVisible(false);
            }
        });
        
        Container backEnd = getContentPane();
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(accept);
        buttonPanel.add(cancel);
        
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(backEnd);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(contentPane);
        
        getBackend().splitListAndFeatures.setDividerLocation(300);

        Options.addLocalListener(new WeakLocalListener(this));
        // Set labels to the current locale
        setLabels();
    }

    public void stateChanged(LocalListenerEvent event) {
        setLabels();
    }
    
    public void setLabels() {
        this.setTitle(Options.getI18N(SelectMediaFrontend.class).getString("Choose medium"));
    }

    public SelectMediaBackend getBackend() {
        return selectMediaBackend;
    }

    public Window getWindow() {
        return this;
    }

    public void setExtendedState(int state) {
    }

    public int getExtendedState() {
        return 0;
    }

    public Medium getMedium() {
        return getBackend().getMedium();
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
