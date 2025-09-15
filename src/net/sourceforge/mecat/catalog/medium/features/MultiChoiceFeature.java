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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.SetAttributeEvent;
import net.sourceforge.mecat.catalog.datamanagement.SetAttributeListener;
import net.sourceforge.mecat.catalog.gui.features.FeaturePanel;
import net.sourceforge.mecat.catalog.gui.features.MultiChoiceFeaturePanel;
import net.sourceforge.mecat.catalog.gui.features.desktop.FeatureDesktop;
import net.sourceforge.mecat.catalog.medium.Medium;

public class MultiChoiceFeature extends AbstractChoiceFeature implements SetAttributeListener {

	/**
	 * @param medium
	 * @param attributeName
	 * @param choices
	 */
	public MultiChoiceFeature(Medium medium, String attributeName, Vector<String> choices) {
		super(medium, attributeName, choices);
        medium.entry.addEntryListenerForSetAttribute(attributeName, this);
	}

	/**
	 * @param medium
	 * @param attributeName
	 * @param choices
	 */
	public MultiChoiceFeature(Medium medium, String attributeName/*, String[] choices, String choiceResource*/) {
		super(medium, attributeName/*, choices, choiceResource*/);
        medium.entry.addEntryListenerForSetAttribute(attributeName, this);
	}

    @Override
	public FeaturePanel getPanel(FeatureDesktop desktop, boolean border) {
		return new MultiChoiceFeaturePanel(this, desktop, border);
	}
	

	/**
	 * This function adds a choice to the set
	 * of posible choices for its medium. 
	 */
	public void add(String choice) {
		if (medium == null)
			return;
		if (medium.entry == null)
			return;
		medium.entry.addSetAttribute(attributeName, choice);	
	}
	/**
	 * This function removes a choice from the set
	 * of posible choices of its medium. 
	 */
	public void remove(String choice) {
		if (medium == null)
			return;
		if (medium.entry == null)
			return;
		medium.entry.removeSetAttribute(attributeName, choice);
	}
	
	public boolean exists(String choice) {
		if (medium == null)
			return false;
		if (medium.entry == null)
			return false;
		return medium.entry.existsSetAttribute(attributeName, choice);
	}


	static public boolean storeOptions(Entry entry) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * For the class type MultiChoice this 
	 * links to exists
	 * @see #exists(String)
	 */
	public boolean validate(String condition) throws BadCondition {
		return exists(condition);
	}
	
	public String getText(){
		Vector<String> atts = new Vector<String>();
		
        ResourceBundle choiceRes = getChoiceResourceBundle();
        
		for (Iterator<String> i = medium.entry.getSetIterator(attributeName); i.hasNext();)
            atts.add(choiceRes.getString(i.next()));
		
		if (atts.size() == 0)
			return res.getString("The medium has no [ATTRIBUTENAME].").replaceAll("\\[ATTRIBUTENAME\\]", res.getString(attributeName));

		if (atts.size() > 1)
			return res.getString("The medium has the [NUMBER] [ATTRIBUTENAME]s [SELECTION].").replaceAll("\\[ATTRIBUTENAME\\]", res.getString(attributeName)).replaceAll("\\[NUMBER\\]", String.valueOf(atts.size())).replaceAll("\\[SELECTION\\]", Utils.natList(atts));
		else
            return res.getString("The medium has the [ATTRIBUTENAME] [SELECTION].").replaceAll("\\[ATTRIBUTENAME\\]", res.getString(attributeName)).replaceAll("\\[SELECTION\\]", Utils.natList(atts));
	}

	public String getShortText() {
		Vector<String> atts = new Vector<String>();
		
        ResourceBundle choiceRes = getChoiceResourceBundle();
        
		for (Iterator<String> i = medium.entry.getSetIterator(attributeName); i.hasNext();)
            atts.add(choiceRes.getString(i.next()));

		return Utils.natList(atts);
	}

    public String getShortTextHTML() {
        Vector<String> atts = new Vector<String>();
        
        ResourceBundle choiceRes = getChoiceResourceBundle();
        
        for (Iterator<String> i = medium.entry.getSetIterator(attributeName); i.hasNext();)
            atts.add(choiceRes.getString(i.next()));

        return Utils.natList(atts);
    }
    
    public String getTextHTML(int availableWidth) {
        Vector<String> atts = new Vector<String>();
        
        ResourceBundle choiceRes = getChoiceResourceBundle();
        
        for (Iterator<String> i = medium.entry.getSetIterator(attributeName); i.hasNext();)
            atts.add(choiceRes.getString(i.next()));
        
        if (atts.size() == 0)
            return res.getString("The medium has no [ATTRIBUTENAME].").replaceAll("\\[ATTRIBUTENAME\\]", res.getString(attributeName));

        if (atts.size() > 1)
            return res.getString("The medium has the [NUMBER] [ATTRIBUTENAME]s [SELECTION].").replaceAll("\\[ATTRIBUTENAME\\]", res.getString(attributeName)).replaceAll("\\[NUMBER\\]", "<strong>" + atts.size() + "</strong>").replaceAll("\\[SELECTION\\]", "<strong>" + Utils.natList(atts) + "</strong>");
        else
            return res.getString("The medium has the [ATTRIBUTENAME] [SELECTION].").replaceAll("\\[ATTRIBUTENAME\\]", res.getString(attributeName)).replaceAll("\\[SELECTION\\]", "<strong>" + Utils.natList(atts) + "</strong>");
    }

    public boolean hasValue() {
        Iterator<String> i = medium.entry.getSetIterator(attributeName); 
        if (i.hasNext())
            return true;
        
        return false;
    }
    
	public void copyTo(Feature feature) {
        MultiChoiceFeature mcf = (MultiChoiceFeature)feature;
        mcf.clear();
//		for (String s : getChoices())
//			if (exists(s))
//				((MultiChoiceFeature)feature).add(s);
        for (Iterator<String> i = medium.entry.getSetIterator(attributeName); i.hasNext();)
            mcf.add(i.next());
	}

	public List<String> getSelection() {
		Vector<String> selection = new Vector<String>();
        for (Iterator<String> i = medium.entry.getSetIterator(attributeName); i.hasNext();)
            selection.add(i.next());
		return selection;
	}
/*    public List<String> getSelection() {
        Vector<String> selection = new Vector<String>();
        for (String s : getChoices())
            if (exists(s))
                selection.add(s);
        return selection;
    }*/

    public void setAttributeAdded(SetAttributeEvent event) {
        fireFeatureChanged();
    }

    public void setAttributeRemoved(SetAttributeEvent event) {
        fireFeatureChanged();
    }

    public void setAttributeCleared(SetAttributeEvent event) {
        fireFeatureChanged();
    }

    public void clear() {
        medium.entry.clearSetAttribute(attributeName);
    }
/*
	public void setSelection(Vector<Integer> vals) {
		Vector<Integer> ret = new Vector<Integer>();
		for (String choice : choices) {
			if (exists(choice))
				ret.add(choice);
		}
	}

*/

    @Override
    public void moveKeys(Map<String, String> map) {
        Vector<String> replacements = new Vector<String>();
        
        for (String key : getSelection()) 
            if (map.containsKey(key)) {
                replacements.add(map.get(key));
                remove(key);
            }
        
        for (String replacement : replacements)
            add(replacement);
        
    }

}
