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

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JPanel;

import net.sourceforge.mecat.catalog.datamanagement.Catalog;
import net.sourceforge.mecat.catalog.gui.features.ComboBoxFeaturePanel;
import net.sourceforge.mecat.catalog.gui.features.FeaturePanel;
import net.sourceforge.mecat.catalog.gui.features.MultiChoiceFeaturePanel;
import net.sourceforge.mecat.catalog.gui.features.desktop.FeatureDesktop;
import net.sourceforge.mecat.catalog.gui.options.features.LanguagesFeatureOptionPanel;
import net.sourceforge.mecat.catalog.i18n.LocaleResourceBundle;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.listener.FeatureListener;
import net.sourceforge.mecat.catalog.medium.features.option.FeatureOption;
import net.sourceforge.mecat.catalog.medium.features.option.LanguagesFeatureOption;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.preferences.LanguagesOption;

public class Languages extends AbstractChoiceFeature {
	
    
	class LanguagesMultiChoice extends MultiChoiceFeature {
		Languages languageFeature;
		public LanguagesMultiChoice(Medium medium, String attrName, Languages languageFeature) {
			super(medium, attrName);
			this.languageFeature  = languageFeature;
            this.res = languageFeature.res;
		}
        
        @Override
        public Vector<String> getChoices() { return languageFeature.getChoices(); }
        @Override
		public ResourceBundle getChoiceResourceBundle() { return languageFeature.getChoiceResourceBundle(); }
        @Override
        public void showOptions() { languageFeature.showOptions(); }
	}

	class LanguagesChoice extends ChoiceFeature {
		Languages languageFeature;
		public LanguagesChoice(Medium medium, String attrName, Languages languageFeature) {
			super(medium, attrName);
			this.languageFeature = languageFeature;
            this.res = languageFeature.res;
		}

        @Override
        public Vector<String> getChoices() { return languageFeature.getChoices(); }
        @Override
		public ResourceBundle getChoiceResourceBundle() { return languageFeature.getChoiceResourceBundle(); }
        @Override
        public void showOptions() { languageFeature.showOptions(); }
	}

	public final AbstractChoiceFeature languageFeature;
	
//	private Set<String> languages = new TreeSet<String>();
//	private String language;
	
	// If  multichoice is true, the medium 
	// can have more than one language
	private boolean multichoice = false;

    
    
    
/*	public Languages(Medium medium) {
		this(medium, false);
	}*/

	public Languages(Medium medium, String attrName, boolean multichoice) {
		super(medium, attrName);
		this.multichoice = multichoice;
		if (multichoice)
			languageFeature = new LanguagesMultiChoice(medium, attrName, this);
		else
			languageFeature = new LanguagesChoice(medium, attrName, this);
        languageFeature.addFeatureListener(new FeatureListener(){
            public void featureValueChanged(Feature source) {
                fireFeatureChanged();
            }
        });
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.medium.features.Feature#getPanel()
	 */
    @Override
	public FeaturePanel getPanel(FeatureDesktop desktop, boolean border) {
		if (multichoice)
			return new MultiChoiceFeaturePanel((MultiChoiceFeature)languageFeature, desktop, border, Options.getI18N(getClass()));
		else
			return new ComboBoxFeaturePanel((ChoiceFeature)languageFeature, desktop, border, Options.getI18N(getClass()));
	}
	
    
    public boolean hasValue() {
        return languageFeature.hasValue();
    }
    
    @Override
    public JPanel getFeatureOptionPanel(FeatureOption featureOption) {
        if (!(featureOption instanceof LanguagesFeatureOption))
            return null;
        
        LanguagesFeatureOption languagesFeatureOption = ( LanguagesFeatureOption ) featureOption;

        return new LanguagesFeatureOptionPanel(languagesFeatureOption);
    }
    

	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.medium.features.Feature#validate(java.lang.String)
	 */
	public boolean validate(String condition) throws BadCondition {
		try {
			return languageFeature.validate(condition);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BadCondition();
		}
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.medium.features.Feature#getText()
	 */
	public String getText() {
		return languageFeature.getText();
	}

	public String getShortText() {
		return languageFeature.getShortText();
	}
	
    public String getTextHTML(int availableWidth) {
        return languageFeature.getTextHTML(availableWidth);
    }

    public String getShortTextHTML() {
        return languageFeature.getShortTextHTML();
    }
    
	public void copyTo(Feature feature) {
		if (!(feature instanceof Languages))
			return;

		Languages languages = (Languages)feature;

		if (multichoice == languages.multichoice) {
			languageFeature.copyTo(languages.languageFeature);
			return;
		}

		if (multichoice) {
			MultiChoiceFeature multiChoice = (MultiChoiceFeature)languageFeature;
			ChoiceFeature choiceFeature = (ChoiceFeature)languages.languageFeature;
			
            List<String> selection = multiChoice.getSelection();
            if (!selection.isEmpty())
                choiceFeature.set(selection.get(0));

			return;
		} else {
			ChoiceFeature choiceFeature = (ChoiceFeature)languageFeature;
			MultiChoiceFeature multiChoice = (MultiChoiceFeature)languages.languageFeature;

			multiChoice.add(choiceFeature.get());
			return;
		}
	}

    
    @Override
    public Vector<String> getChoices() { 
        Catalog catalog = medium.entry.getCatalog();
        LanguagesFeatureOption languagesFeatureOption = (LanguagesFeatureOption) getFeatureOption();
        
        // Get the right set of languages
        LanguagesOption langs = null;
        if (languagesFeatureOption.isOverrideGlobalLanguages())
            langs = languagesFeatureOption;
        else
            langs = medium.getListing().getTotalPreferences().getGeneralOption().getLanguagesOption();

        // Convert the languages to Vector of String(keys) 
        Vector<String> choices = new Vector<String>();
        for (Locale l : langs)
            choices.add(l.toString());

        // Return keys
        return choices;
    }
    
    @Override
    public ResourceBundle getChoiceResourceBundle() {
		return new LocaleResourceBundle(); 
	}

    @Override
    public void moveKeys(Map<String, String> map) {
        languageFeature.moveKeys(map);
    }
}
