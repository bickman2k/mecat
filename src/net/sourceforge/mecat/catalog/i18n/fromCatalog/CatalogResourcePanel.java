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
 * Created on Aug 26, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.i18n.fromCatalog;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import net.sourceforge.mecat.catalog.gui.MainFrameBackend;
import net.sourceforge.mecat.catalog.gui.options.LanguageSelection;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalButton;
import net.sourceforge.mecat.catalog.option.Options;

public class CatalogResourcePanel extends JPanel implements CatalogResourceListener {
	final CatalogResource catalogResource;
    final LayeredResourceBundle res = new LayeredResourceBundle(Options.getI18N(CatalogResourcePanel.class), Options.getI18N(MainFrameBackend.class));
	protected JTable table;
    final JButton addKey = new SimpleLocalButton(res, "Add Key");
    final JButton addLanguage = new SimpleLocalButton(res, "Add Language");
	public CatalogResourceTableModel model;
    private boolean editable;
    final protected JPanel buttons = new JPanel();
	
	
    public CatalogResourcePanel(final CatalogResource catalogResource) {
        this(catalogResource, true);
    }
    
    public CatalogResourcePanel(final CatalogResource catalogResource, boolean editable) {
        this(catalogResource, editable, BorderLayout.NORTH);
    }

    /**
     * 
     * @param catalogResource
     * @param editable
     * @param buttonPosition Defined in BorderLayout, i.e. BorderLayout.NORTH or BorderLayout.SOUTH
     */
	public CatalogResourcePanel(final CatalogResource catalogResource, boolean editable, String buttonPosition) {
        this.editable = editable;
		this.catalogResource = catalogResource;
		this.setLayout(new BorderLayout());

		model = new CatalogResourceTableModel(catalogResource, editable);
		
        this.table = new JTable(model);

        if (editable) {
            buttons.setLayout(new GridLayout());
            addKey.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    AddNewKey();
                }});
            addLanguage.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    AddNewLanguage();
                }});
            buttons.add(addKey);
            buttons.add(addLanguage);
            this.add(buttons, buttonPosition);
        }
        
		this.add(new JScrollPane(table), BorderLayout.CENTER);
        
        catalogResource.addCatalogResourceListener(this);        
	}
    
    

	@Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        table.setEnabled(enabled);
        addKey.setEnabled(enabled);
        addLanguage.setEnabled(enabled);
    }



    void AddNewLanguage() {
		LanguageSelection panel = new LanguageSelection();
		if (JOptionPane.showConfirmDialog(CatalogResourcePanel.this, panel, res.getString("Select Language"),  JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION)
			return;

		if (catalogResource.addLanguage(panel.getLocale()))
            ;
//			model.fireTableStructureChanged();
	}
	
	void AddNewKey() {
		String key = JOptionPane.showInputDialog(CatalogResourcePanel.this, res.getString("Key for the new Entry."));
		if (catalogResource.addKey(key)) {
//			model.fireTableRowsInserted(catalogResource.keys.size()-1, catalogResource.keys.size()-1);
//			model.fireTableStructureChanged();
//			table.updateUI();
		}
	}



    public void completeChange(CatalogResource source) {
        model.fireTableRowsInserted(catalogResource.keys.size()-1, catalogResource.keys.size()-1);
        model.fireTableStructureChanged();
        table.updateUI();
    }



    public void addedLanguage(CatalogResource source, Locale language) {
        model.fireTableStructureChanged();
    }



    public void removedLanguage(CatalogResource source, Locale language) {
        model.fireTableStructureChanged();
    }



    public void addedKey(CatalogResource source, String key) {
        model.fireTableRowsInserted(catalogResource.keys.size()-1, catalogResource.keys.size()-1);
        model.fireTableStructureChanged();
        table.updateUI();
    }



    public void removedKey(CatalogResource source, String key) {
        model.fireTableRowsInserted(catalogResource.keys.size()-1, catalogResource.keys.size()-1);
        model.fireTableStructureChanged();
        table.updateUI();
    }



    public void addedTranslation(CatalogResource source, String key, Locale language, String translation) {
        // TODO Auto-generated method stub
        
    }



    public void removedTranslation(CatalogResource source, String key, Locale language, String translation) {
        // TODO Auto-generated method stub
        
    }
}
