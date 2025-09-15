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
 * Created on Jul 16, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.medium.features;


import java.util.Map;
import java.util.ResourceBundle;

import net.sourceforge.mecat.catalog.datamanagement.AttributeEvent;
import net.sourceforge.mecat.catalog.datamanagement.AttributeListener;
import net.sourceforge.mecat.catalog.gui.features.ComboBoxFeaturePanel;
import net.sourceforge.mecat.catalog.gui.features.FeaturePanel;
import net.sourceforge.mecat.catalog.gui.features.desktop.FeatureDesktop;
import net.sourceforge.mecat.catalog.medium.Medium;

abstract public class ChoiceFeature extends AbstractChoiceFeature implements AttributeListener {

    String cache = null;
    boolean cachedKey = false;
    
	/**
     * This function is obsolete.
     * Choices now are given through the .preferences.xml file.
     * 
     * @deprecated
     * 
	 * @param medium
	 * @param attributeName
	 * @param choices
	 * /
	public ChoiceFeature(Medium medium, String attributeName, Vector<String> choices) {
		super(medium, attributeName, choices);
        medium.entry.addEntryListenerForAttribute(attributeName, this);
	}*/

	/**
	 * @param medium
	 * @param attributeName
	 */
	public ChoiceFeature(Medium medium, String attributeName) {
		super(medium, attributeName);
        medium.entry.addEntryListenerForAttribute(attributeName, this);
	}

	public enum PanelType { COMBOBOX }
	
    @Override
	public FeaturePanel getPanel(FeatureDesktop desktop, boolean border) {
		return getPanel(desktop, border, PanelType.COMBOBOX);
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.medium.features.Feature#getPanel()
	 */
	public FeaturePanel getPanel(FeatureDesktop desktop, boolean border, PanelType type) {
		switch (type) {
			case COMBOBOX :
				return new ComboBoxFeaturePanel(this, desktop, border);
		}
		return null;
		//TODO implement other Panels
	}

    /**
     * 
     * THis function sets the choice
     * 
     * Selection == -1 means nothing set, and is translated into no attribute with attribute equals null
     * 
     * @param selection
     */
	public void set(String key) {
/*        if (selection == -1) {
            medium.entry.setAttribute(attributeName, null);        
            cache = -1;
            return;
        }
        
		medium.entry.setAttribute(attributeName, getChoices().elementAt(selection));		
        cache = selection;*/
        cache = key;
        cachedKey = true;

        medium.entry.setAttribute(attributeName, key);        
	}
	
    /**
     * This functions return the selected choice
     * 
     * Selection == -1 means nothing set, and is translated into no attribute with attribute equals null
     * 
     * @return the selected choice
     */
	public String get() {
        if (cachedKey)
            return cache;

        cachedKey = true;
        return cache = medium.entry.getAttribute(attributeName);
        
/*		if (medium.entry.getAttribute(attributeName) == null)
			return cache = -1;

		try {
			for (int i = 0; i < getChoices().size(); i++)
				if (getChoices().elementAt(i).compareTo(medium.entry.getAttribute(attributeName)) == 0)
					return cache = i;
			return cache = -1;
		} catch (Exception e) {
			return cache = -1;
		}*/
		
	}

	public boolean validate(String condition) throws BadCondition {
        String value = medium.entry.getAttribute(attributeName);
        if (condition == null)
            if (value == null)
                return true;
            else
                return false;
		return (value != null && value.compareTo(condition) == 0);
	}


    public boolean hasValue() {
        return (medium.entry.getAttribute(attributeName) != null);
    }
    
	public String getText() {
        ResourceBundle choiceRes = getChoiceResourceBundle();
        
		if (medium.entry.getAttribute(attributeName) == null)
			return res.getString("The [ATTRIBUTENAME] is not set.").replaceAll("\\[ATTRIBUTENAME\\]", res.getString(attributeName));
		return res.getString("The [ATTRIBUTENAME] is [CHOICE].").replaceAll("\\[ATTRIBUTENAME\\]", res.getString(attributeName)).replaceAll("\\[CHOICE\\]", choiceRes.getString(medium.entry.getAttribute(attributeName)));
	}
	
	public String getShortText() {
        ResourceBundle choiceRes = getChoiceResourceBundle();
        
		if (medium.entry.getAttribute(attributeName) == null)
			return "";
		return choiceRes.getString(medium.entry.getAttribute(attributeName));
	}
	
    public String getTextHTML(int availableWidth) {
        ResourceBundle choiceRes = getChoiceResourceBundle();
        
        if (medium.entry.getAttribute(attributeName) == null)
            return res.getString("The [ATTRIBUTENAME] is not set.").replaceAll("\\[ATTRIBUTENAME\\]", res.getString(attributeName));
        return res.getString("The [ATTRIBUTENAME] is [CHOICE].").replaceAll("\\[ATTRIBUTENAME\\]", res.getString(attributeName)).replaceAll("\\[CHOICE\\]", "<strong>" + choiceRes.getString(medium.entry.getAttribute(attributeName)) + "</strong>");
    }
    
    public String getShortTextHTML() {
        ResourceBundle choiceRes = getChoiceResourceBundle();
        
        if (medium.entry.getAttribute(attributeName) == null)
            return "";
        return choiceRes.getString(medium.entry.getAttribute(attributeName));
    }
    
	public void copyTo(Feature feature) {
		((ChoiceFeature)feature).set(this.get());
	}

    public void attributeSet(AttributeEvent event) {
        cachedKey = false;
        fireFeatureChanged();
    }

    @Override
    public void moveKeys(Map<String, String> map) {
        String key = medium.entry.getAttribute(attributeName);
        if (key == null)
            return;
        
        if (!map.containsKey(key))
            return;
        
        medium.entry.setAttribute(attributeName, map.get(key));        
    }
    
    
}
