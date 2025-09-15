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

import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import net.sourceforge.mecat.catalog.datamanagement.AttributeEvent;
import net.sourceforge.mecat.catalog.datamanagement.AttributeListener;
import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.gui.features.FeaturePanel;
import net.sourceforge.mecat.catalog.gui.features.MultiLanguageTextFieldFeaturePanel;
import net.sourceforge.mecat.catalog.gui.features.TextFieldFeaturePanel;
import net.sourceforge.mecat.catalog.gui.features.desktop.FeatureDesktop;
import net.sourceforge.mecat.catalog.gui.features.desktop.hardcoded.HardCodedDesktop;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.option.Options;

abstract public class TextFeature extends AbstractFeature implements AttributeListener {

    
	public String attributeName;
	/**
	 * Flag that says if the feature is represented by a TextField 
	 * or an TextArea
	 */
	public boolean field;
    
    /**
     * This flag indicated whether the attribute given 
     * by attribute Name is a Attribute or a SetAttribute.
     * If it is localized the feature has up to one
     * value for every local (language).
     */
    public boolean localized;

	public TextFeature(final Medium medium, final String attributeName, final boolean field, final boolean localized) {
		super(medium);
		this.attributeName = attributeName;
		this.field = field;		
        this.localized = localized;
        medium.entry.addEntryListenerForAttribute(attributeName, this);
	}

    @Override
	public FeaturePanel getPanel(FeatureDesktop desktop, boolean border) {
        if (desktop instanceof HardCodedDesktop && ((HardCodedDesktop) desktop).getShowedFeatures().size() == 1)
            return new MultiLanguageTextFieldFeaturePanel(this, desktop, border);
		return new TextFieldFeaturePanel(this, desktop, border);
	}
	

    /**
     * 
     * Set the value of the textfeature to txt
     * for the Locale language. If language
     * is null it does the same as set(txt).
     * 
     * If it is not localized and langauge != null
     * it does nothing and returns false.
     * 
     * @param txt
     * @param language
     * @return
     */
    public boolean set(String txt, Locale language) {
        if (language == null)
            return set(txt);
        
        if (!localized)
            return false;
        
        medium.entry.setAttribute(attributeName, txt, language);
        return true;
    }

    public boolean set(String txt) {
        if (localized) {
            medium.entry.setAttribute(attributeName, txt, Options.getCurrentLocale());
            return true;
        } else {
            medium.entry.setAttribute(attributeName, txt);
            return true;
        }
    }

    public String get() {
        if (localized) {
            if (medium == null)
                return null;
            if (medium.entry == null)
                return null;
                
            return medium.entry.getAttribute(attributeName, Options.getCurrentLocale());
        } else {
            if (medium == null)
                return null;
            if (medium.entry == null)
                return null;
            
            return medium.entry.getAttribute(attributeName);
        }
    }

    
    /**
     * This function returns the languages for which the 
     * feature has stored values. If the feature is not
     * localized then it returns null.
     * @return
     */
    public Set<Locale> getLanguages() {
        if (!localized) 
            return null;

        return medium.entry.getAttributeLanguages(attributeName);
    }
    
    /**
     * This function returns value for the specified languag. 
     * If the feature is not localized then it returns the same value as get().
     * If the language is not null but the feature is not localized then it returns null.
     * 
     * @param language
     * @return
     */
    public String get(Locale language) {
        if (language == null)
            return get();
        
        if (!localized)
            return null;
        
        return medium.entry.getAttribute(attributeName, language);
    }
    
	public boolean hasOptions() {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.medium.features.Feature#storeOptions(net.sourceforge.mecat.catalog.datamanagement.Entry)
	 */
	static public boolean storeOptions(Entry entry) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean validate(String condition) {
        String txt = get();
        if (txt == null)
            return (condition == null);
        if (condition == null)
            return false;
        return Pattern.compile(condition, Pattern.DOTALL/* | Pattern.UNICODE_CASE*/).matcher(txt).matches();
//        return txt.matches(condition);
	}
	
	public String getShortText() {
		return get();
	}
    public String getShortTextHTML() {
        return get();
    }
	
	public void copyTo(Feature feature) {
		((TextFeature)feature).set(this.get());
	}

    public void attributeSet(AttributeEvent event) {
        fireFeatureChanged();
    }
    
    public boolean hasValue() {
        return get() != null;
    }
    
}
