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
 * Created on Sep 15, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.medium.features.option.merger.choiceFeature;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JTabbedPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.mecat.catalog.i18n.fromCatalog.CatalogResource;
import net.sourceforge.mecat.catalog.i18n.fromCatalog.CatalogResourcePanel;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalButton;
import net.sourceforge.mecat.catalog.option.Options;

public class ChoiceFeatureMergerGUI extends JTabbedPane {

    final ChoiceFeatureMergeUI mergeUI;
    ResourceBundle res = Options.getI18N(ChoiceFeatureMergerGUI.class);
    
    public ChoiceFeatureMergerGUI(final ChoiceFeatureMergeUI mergeUI) {
        this.mergeUI = mergeUI;
        
        final CatalogResourcePanel crpOption1 = new CatalogResourcePanel(mergeUI.res1, false);
        addTab(res.getString("Option of the open catalog"), crpOption1);
        final CatalogResourcePanel crpOption2 = new CatalogResourcePanel(mergeUI.res2, false);
        addTab(res.getString("Option of the import catalog"), crpOption2);

        final CatalogResource mergeCatalog = mergeUI.getMergeResult().getCatalogResource();
        final CatalogResourcePanel crpMergeOption = new CatalogResourcePanel(mergeCatalog, true, BorderLayout.SOUTH){{
            table.setDefaultRenderer(String.class, new MergedOptionCellRenderer(mergeUI, model));
            table.setDefaultRenderer(Object.class, new MergedOptionCellRenderer(mergeUI, model));
            
            final JButton split = new SimpleLocalButton(res, "Split");
            split.setEnabled(false);
            split.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    int rows[] = table.getSelectedRows();
                    if (rows.length == 1)
                        mergeUI.split(table.getValueAt(rows[0], 0).toString());
                }
            });
            
            final JButton unite = new SimpleLocalButton(res, "Unite");
            unite.setEnabled(false);
            unite.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    int rows[] = table.getSelectedRows();
                    if (rows.length > 1) {
                        Vector<String> keys = new Vector<String>();
                        for (int row : rows)
                            keys.add(table.getValueAt(row, 0).toString());
                        mergeUI.unite(keys);
                    }
                }
            });
            
            table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){

                public void valueChanged(ListSelectionEvent arg0) {
                    int rows[] = table.getSelectedRows();
                    if (rows.length == 1) {
                        split.setEnabled(mergeUI.keySourcesMap.get(table.getValueAt(rows[0], 0)).size() > 1);
                        unite.setEnabled(false);
                    } else {
                        split.setEnabled(false);
                        unite.setEnabled(true);
                    }
                }});
            
            
            buttons.removeAll();
            buttons.add(split);
            buttons.add(unite);
        }};
    
        addTab(res.getString("Option of the merged catalog"), crpMergeOption);
    }

}
