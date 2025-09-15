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
 * Created on Sep 22, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.sort.design;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import net.sourceforge.mecat.catalog.export.ExportChoice;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalButton;
import net.sourceforge.mecat.catalog.medium.Listing;
import net.sourceforge.mecat.catalog.option.LocalListener;
import net.sourceforge.mecat.catalog.option.LocalListenerEvent;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.WeakLocalListener;
import net.sourceforge.mecat.catalog.sort.ConfigurableComparator;

public class CreateSortDialog extends JDialog implements LocalListener{
    
    enum CreateSortDialogStatus {
        cancel, accept
    }

    CreateSortDialogStatus status = CreateSortDialogStatus.cancel;

    // Buttons 
    final JButton okButton = new SimpleLocalButton(Options.getI18N(ExportChoice.class), "OK");
    final JButton cancelButton = new SimpleLocalButton(Options.getI18N(ExportChoice.class), "Cancel");
    
    FeatureSortOptions featureSortOptions = null;

    public CreateSortDialog(Listing listing) throws HeadlessException {
        featureSortOptions = new FeatureSortOptions(listing);
        init();
    }

    public CreateSortDialog(Frame frame, Listing listing) throws HeadlessException {
        super(frame);
        featureSortOptions = new FeatureSortOptions(listing);
        init();
    }

    public CreateSortDialog(Dialog dialog, Listing listing) throws HeadlessException {
        super(dialog);
        featureSortOptions = new FeatureSortOptions(listing);
        init();
    }

    public void init() {
        okButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                status = CreateSortDialogStatus.accept;
                CreateSortDialog.this.setVisible(false);
            }
        });
        okButton.setEnabled(false);
        cancelButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                CreateSortDialog.this.setVisible(false);
            }
        });

        featureSortOptions.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                okButton.setEnabled(featureSortOptions.selection.getModel().getSize() > 0);
            }
        }); 
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        setLayout(new BorderLayout());
        add(featureSortOptions);
        add(buttonPanel, BorderLayout.SOUTH);
        
        pack();
        
        Options.addLocalListener(new WeakLocalListener(this));
        // Set labels to the current locale
        setLabels();
    }

    public void stateChanged(LocalListenerEvent event) {
        setLabels();
    }
    
    public void setLabels() {
        setTitle(Options.getI18N(CreateSortDialog.class).getString("Create sorting"));
    }
    
    public static ConfigurableComparator showCreateSortDialog(Component component, Listing listing) {
        CreateSortDialog dialog;
        
        while (component != null && !(component instanceof Frame) && !(component instanceof Dialog))
            component = component.getParent();

        if (component == null)
            dialog = new CreateSortDialog(listing);
        else if (component instanceof Dialog)
            dialog = new CreateSortDialog((Dialog)component, listing);
        else
            dialog = new CreateSortDialog((Frame)component, listing);


        dialog.setModal(true);
        dialog.setVisible(true);

        if (dialog.status == CreateSortDialogStatus.cancel)
            return null;
        
        ConfigurableComparator comparator = dialog.featureSortOptions.getSorting();
        // Add to recent comaprings
        if (comparator != null)
            Options.addRecentSorting(comparator);
        
        return comparator;
    }
}
