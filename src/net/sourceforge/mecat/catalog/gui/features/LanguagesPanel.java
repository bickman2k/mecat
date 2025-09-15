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
 * Created on Jul 15, 2004
 * @author Stephan Richard Palm
 * TODO The border and the name perhaps should be in
 *             FeaturePanel to
 * TODO This one is special it can be translated into every Language 
 *             without Resource.
 * TODO Uses Options.languages to know the Languages prefered by the user
 * 
 * 
 */
package net.sourceforge.mecat.catalog.gui.features;


/** 
 * @deprecated
 * 
 */
public class LanguagesPanel{}/* extends FeaturePanel<Languages> {

//	private MainFrame Parent = null;

	private javax.swing.JCheckBox Language_Checkbox[];
//	Languages languages = null;


	ChangeListener LanguageChangeListener = new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
			LanguagesChanged();
		}
	  };
	  
	public void LanguagesChanged(){
		// If Feature does not work
		// we'll stop right here
		if (feature == null)
			return;
		
		for (int i = 0; i < Options.languages.length; i++) 
			if (Language_Checkbox[i].isSelected())
				feature.addLanguage(Options.languages[i]);
			else
				feature.removeLanguage(Options.languages[i]);
	}

	public LanguagesPanel(Languages languages) {
		this(languages, true);
	}
	
	public LanguagesPanel(Languages languages, boolean border) 
	{
//		this.languages = languages;
		super(languages, true, "Languages");

/*		// First of all find the feature that is required
		// to chose languages
		if (medium == null)
			F = null;
		else
			F = medium.getFeatureLanguages();

		// Set Layout to Gridlayout to arrange options bellow each other
		this.setLayout(new GridLayout(Options.languages.length,1));

		// Allocate an Array for the Checkboxes
		Language_Checkbox = new javax.swing.JCheckBox[Options.languages.length];

		// Generate, fill the Checkboxes, make them visible and 
		// interactive
		for (int i = 0; i < Options.languages.length; i++) {			
 			Language_Checkbox[i] = new JCheckBox(Options.languages[i].getDisplayName(Options.getCurrentLocale()));
			if (feature != null) 
				Language_Checkbox[i].setSelected(feature.existsLanguage(Options.languages[i]));
			else
				Language_Checkbox[i].setEnabled(false);
			this.add(Language_Checkbox[i]);
			Language_Checkbox[i].addChangeListener(LanguageChangeListener);
		}
	}

}*/
