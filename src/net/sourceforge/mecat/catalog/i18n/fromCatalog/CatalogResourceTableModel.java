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
 * Created on Sep 21, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.i18n.fromCatalog;

import java.util.Locale;

import javax.swing.table.AbstractTableModel;

import net.sourceforge.mecat.catalog.gui.MainFrameBackend;
import net.sourceforge.mecat.catalog.option.LocalListener;
import net.sourceforge.mecat.catalog.option.LocalListenerEvent;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.WeakLocalListener;

public class CatalogResourceTableModel extends AbstractTableModel implements LocalListener {
    final LayeredResourceBundle res = new LayeredResourceBundle(Options.getI18N(CatalogResourcePanel.class), Options.getI18N(MainFrameBackend.class));

    final CatalogResource catalogResource;
    final boolean editable;
    
    CatalogResourceTableModel(CatalogResource catalogResource, boolean editable){
        this.catalogResource = catalogResource;
        this.editable = editable;

        Options.addLocalListener(new WeakLocalListener(this));
    }

    public void stateChanged(LocalListenerEvent event) {
        fireTableStructureChanged();
    }

    public String getColumnName(int columnIndex) {
        Locale locale = getLocale(columnIndex);
        if (columnIndex == 0)
            return res.getString("Key");
        if (locale == null)
            return res.getString("ERROR");
        return locale.getDisplayLanguage(Options.getCurrentLocale());
    }
      public boolean isCellEditable(int rowIndex, int columnIndex) { 
        if (!editable)
              return false;
        if (columnIndex == 0)
            return false;
        return true;
      }
      public Class<?> getColumnClass() { return String.class;}
    public int getColumnCount() { 
        return 1 + catalogResource.all_langs.size(); 
    }
    public int getRowCount() { 
        return catalogResource.keys.size();
    }
    public Object getValueAt(int row, int col) {
        String key = catalogResource.keys.elementAt(row);
        Locale locale = getLocale(col);
        if (col == 0)
            return key;
        if (locale == null)
            return null;
        return catalogResource.translations.get(locale).get(key); 
    }
    public void setValueAt(Object aValue, int rowIndex, int col){
        Locale locale = getLocale(col);
        if (locale == null)
            return;
        
        if (catalogResource.setTranslation((String)getValueAt(rowIndex, 0), locale, (String)aValue)) {
            fireTableStructureChanged();
            fireTableDataChanged();
        }
    }

    public Locale getLocale(int col) {
        if (col == 0)
            return null;
        if (col <= catalogResource.all_langs.size())
            return new Locale(catalogResource.all_langs.elementAt(col-1));
        return null;
    }
    
}
