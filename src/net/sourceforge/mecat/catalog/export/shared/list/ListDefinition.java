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
 * Created on Oct 25, 2005
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.export.shared.list;

import java.util.Locale;
import java.util.Vector;

import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.PersistentThroughEntry;
import net.sourceforge.mecat.catalog.datamanagement.Util;
import net.sourceforge.mecat.catalog.gui.ListingProperties;
import net.sourceforge.mecat.catalog.medium.features.AbstractFeature;
import net.sourceforge.mecat.catalog.medium.features.MultiChoiceFeature;
import net.sourceforge.mecat.catalog.medium.features.impl.Genre;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.srp.utils.NiceClass;

public class ListDefinition<TYPE extends Enum, EXPORTDEF extends ExportDef<TYPE>, EXPORTCOL extends ExportCol<?, EXPORTDEF>> implements PersistentThroughEntry{

    public final Vector<EXPORTCOL> columns = new Vector<EXPORTCOL>();
    
    boolean override = false;
    
    public ListingProperties listProperties = new ListingProperties();
    NiceClass<MultiChoiceFeature> mcfeature = new NiceClass<MultiChoiceFeature>(Genre.class);

    int maxChoices = 5;
    int selectedCol = -1;
    public String listName = "List";

    public ListDefinition() {
    }


    public void moveColumnBackwards(EXPORTCOL column) {
        int index = columns.indexOf(column);
        columns.remove(index);
        columns.add(index - 1, column);
    }
    
    public void moveColumnForward(EXPORTCOL column) {
        int index = columns.indexOf(column);
        columns.remove(index);
        if (index >= columns.size() - 1) 
            columns.add(column);
        else 
            columns.add(index + 1, column);
    }
    
    public void removeColumn(EXPORTCOL column) {
        columns.remove(column);
    }

    public void addColumn(EXPORTCOL exportCol) {
        columns.add(selectedCol + 1, exportCol);
        selectedCol++;
    }
    
    public void addExportDefToSelectedExportCol(int index, EXPORTDEF exportDef) {
        EXPORTCOL column = columns.elementAt(selectedCol);

        if (index == -1)
            column.add(exportDef);
        else
            column.add(index + 1, exportDef);
    }
    
    public void removeExportDefFromSelectedExportCol(int index) {
        EXPORTCOL column = columns.elementAt(selectedCol);
        if (column != null && index != -1)
            column.remove(index);
    }
    
    public boolean isOverride() {
        return override;
    }

    public int getMaxChoices() {
        return maxChoices;
    }

    public NiceClass<MultiChoiceFeature> getMcfeature() {
        return mcfeature;
    }


    public boolean loadFromEntry(Entry entry) {
        // Get the tex start code
        String title = entry.getAttribute("Title", Options.getCurrentLocale());
        if (title == null)
            title = entry.getAttribute("Title");
        for (Locale locale : entry.getAttributeLanguages("Title"))
            if (entry.getAttribute("Title", locale) != null) {
                title = entry.getAttribute("Title", locale);
                break;
            }
        if (title != null) 
            listName = title;
        
        // General options
        String MultiChoiceFeatureName = entry.getAttribute("MultiChoiceFeature");
        if (MultiChoiceFeatureName != null) {
            try {
                Class c = Class.forName(AbstractFeature.getRealClassName(MultiChoiceFeatureName));
                if (c != null)
                    mcfeature = new NiceClass<MultiChoiceFeature>(c, MultiChoiceFeatureName);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (entry.getAttribute("MultiChoiceFeatureMaxChoices") != null){
            try {
                maxChoices = Integer.valueOf(entry.getAttribute("MultiChoiceFeatureMaxChoices"));
                if (maxChoices < 0)
                    maxChoices = 0;
            } catch (Exception e) {}
        }
        
        // Export settings
        for (Entry col : entry.getSubEntries("Column"))
            columns.add((EXPORTCOL)Util.loadFromEntry(col));

        String overrideListSettings = entry.getAttribute("OverrideListSettings");
        if (overrideListSettings != null) 
            override = overrideListSettings.equalsIgnoreCase("true");

        Entry listProps = entry.getSubEntry("ListProperties");
        if (listProps != null) {
            PersistentThroughEntry pte = Util.loadFromEntry(listProps);
            if (pte instanceof ListingProperties)
                listProperties = ( ListingProperties ) pte;
        }
       
        return true;
    }
    public void saveToEntry(Entry entry) {
        // TeX start code
        entry.setAttribute("Title", listName, Options.getCurrentLocale());
        
        // General options
        if (mcfeature != null)
            entry.setAttribute("MultiChoiceFeature", AbstractFeature.getEasyClassName(mcfeature.getClasstype().getName()));
        entry.setAttribute("MultiChoiceFeatureMaxChoices", String.valueOf(maxChoices));
        
        // Export settings
        for (EXPORTCOL col : columns)
            Util.saveToEntry(col, entry.createSubEntry("Column"));
        
        if (override)
            entry.setAttribute("OverrideListSettings", "true");
        
        Util.saveToEntry(listProperties, entry.createSubEntry("ListProperties"));
    }
}
