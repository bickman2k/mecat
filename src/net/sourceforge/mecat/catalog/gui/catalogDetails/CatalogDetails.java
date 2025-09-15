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
 * Created on Dec 16, 2006
 * @author Stephan Richard Palm
 * 
 * This class is intendet to show extra information about the catalog.
 * A name, a description, ...
 * 
 * The user has the posibility to accept the changes he made,
 * reset them for a new try or forget the changes
 * 
 * 
 */
package net.sourceforge.mecat.catalog.gui.catalogDetails;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.sourceforge.mecat.catalog.datamanagement.Catalog;
import net.sourceforge.mecat.catalog.datamanagement.Connection;
import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.Util;
import net.sourceforge.mecat.catalog.datamanagement.changelog.ChangeLog;
import net.sourceforge.mecat.catalog.export.ExportChoice;
import net.sourceforge.mecat.catalog.gui.features.desktop.hardcoded.HardCodedDesktop;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalButton;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalTitledBorder;
import net.sourceforge.mecat.catalog.medium.Listing;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.impl.Description;
import net.sourceforge.mecat.catalog.medium.features.person.Name;
import net.sourceforge.mecat.catalog.option.LocalListener;
import net.sourceforge.mecat.catalog.option.LocalListenerEvent;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.WeakLocalListener;

public class CatalogDetails extends JDialog implements LocalListener {

    final Listing listing;
    final Catalog dummyCatalog = Options.getDummyCatalog();
    Entry dummyEntry = dummyCatalog.createEntry("GeneralInformationDummy");
    final HardCodedDesktop desktop = new HardCodedDesktop();
    
    
    final JButton accept = new SimpleLocalButton(Options.getI18N(ExportChoice.class), "OK");
    final JButton reset = new SimpleLocalButton(Options.getI18N(ExportChoice.class), "Reset");
    final JButton cancel = new SimpleLocalButton(Options.getI18N(ExportChoice.class), "Cancel");

    public static void showCatalogDetails(Component component, Listing listing) {
        CatalogDetails details;

        while (component != null && !(component instanceof Frame) && !(component instanceof Dialog))
            component = component.getParent();

        if (component == null)
            details = new CatalogDetails(listing);
        else if (component instanceof Dialog)
            details = new CatalogDetails((Dialog)component, listing);
        else
            details = new CatalogDetails((Frame)component, listing);
        
        details.setVisible(true);
    }

    public CatalogDetails(Listing listing) throws HeadlessException {
        super();
        this.listing = listing;
        init();
    }

    public CatalogDetails(Dialog arg0, Listing listing) throws HeadlessException {
        super(arg0);
        this.listing = listing;
        init();
    }

    public CatalogDetails(Frame arg0, Listing listing) throws HeadlessException {
        super(arg0);
        this.listing = listing;
        init();
    }

    protected void setMediumFromDummyEntry() {
        desktop.setMedium(new Medium(dummyEntry, null){

            {
                addFeature(new Name(this, true));
                addFeature(new Description(this));
            }
            
            @Override
            // Do not add an ident
            protected void addIdent() {}

            @Override
            public String displayName() {
                return getFeature(Name.class).get();
            }
            
        });
    }
    
    private void init() {
        
        accept.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                CatalogDetails.this.setVisible(false);
                ChangeLog changeLog = listing.getChangeLog();
                int transId = -1;
                if (changeLog != null)
                    transId = changeLog.openTransaction(Options.getI18N(CatalogDetails.class).getString("Set general catalog information"), false, true);
                
                listing.catalog.getGeneralInformationEntry().clear();
                Util.copyEntry(dummyEntry, listing.catalog.getGeneralInformationEntry());
                
                if (changeLog != null)
                    changeLog.closeTransaction(transId);
                
            }
        });
        
        reset.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                dummyEntry.clear();
                Util.copyEntry(listing.catalog.getGeneralInformationEntry(), dummyEntry);
                setMediumFromDummyEntry();
            }
        });

        cancel.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                CatalogDetails.this.setVisible(false);
            }
        });
        
        // Copy general information to dummy
        Util.copyEntry(listing.catalog.getGeneralInformationEntry(), dummyEntry);
        setMediumFromDummyEntry();
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(accept);
        buttonPanel.add(reset);
        buttonPanel.add(cancel);

        
        setLayout(new BorderLayout());
        JLabel connectionLabel = new JLabel("."){

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(300, super.getPreferredSize().height);
            }

            @Override
            public void paint(Graphics g) {
                
                Connection con = listing.catalog.getConnection();

                if (con != null) {
                    String str = con.getNameCutToSize(this.getWidth(), g);
                    this.setText(str);
                }
                
                super.paint(g);
            }
        };
        JPanel connectionPanel = new JPanel();
        connectionPanel.setBorder(new SimpleLocalTitledBorder(Options.getI18N(CatalogDetails.class), "Position"));
        connectionPanel.setLayout(new BorderLayout());
        connectionPanel.add(connectionLabel);
        
        add(connectionPanel, BorderLayout.NORTH);
        add(new JScrollPane(desktop.getDesktop()));
        add(buttonPanel, BorderLayout.SOUTH);
    
        setSize(new Dimension(640,480));
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
        setTitle(Options.getI18N(CatalogDetails.class).getString("General catalog information"));
    }

    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING)  {
            dummyCatalog.removeEntry(dummyEntry);
        }
            
    }
}
