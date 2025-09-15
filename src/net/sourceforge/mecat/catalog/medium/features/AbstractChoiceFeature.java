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
package net.sourceforge.mecat.catalog.medium.features;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JPanel;

import net.sourceforge.mecat.catalog.datamanagement.Catalog;
import net.sourceforge.mecat.catalog.i18n.fromCatalog.CatalogResource;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.option.ChoiceFeatureOption;
import net.sourceforge.mecat.catalog.medium.features.option.FeatureOption;
import net.sourceforge.mecat.catalog.option.Options;

public abstract class AbstractChoiceFeature extends AbstractFeature {

    
	public String attributeName;
	private Vector<String> choices = null;
	
    protected static JMenu getOptionMenu() {
        JMenu menu = new JMenu(Options.getI18N(AbstractChoiceFeature.class).getString("Choices"));
        menu.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                
            }});
        return menu;
    }

    /**
	 * 
	 * @param medium
	 * This is the medium that uses this feature.
	 * @param attributeName
	 * This is a descriptive Name of the Attribute. 
	 * @param choiceResource
	 * This is the Resource that is goint to be used
	 * to translate the names of the choices.
	 */
	public AbstractChoiceFeature(Medium medium,
			String attributeName,
			Vector<String> choices) {
		super(medium);
		this.attributeName = attributeName;
		this.choices = choices;
	}

	public AbstractChoiceFeature(Medium medium,
			String attributeName) {
		super(medium);
		this.attributeName = attributeName;
	}
	
	@Override
    public JPanel getFeatureOptionPanel(FeatureOption featureOption) {
        if  (featureOption == null)
            return super.getFeatureOptionPanel(null);
        ChoiceFeatureOption choiceFeatureOption = (ChoiceFeatureOption) featureOption;
        return choiceFeatureOption.getCatalogResource().getPanel();
    }

    public Vector<String> getChoices(){
		if (choices != null)
			return choices;
		else
			return getChoiceCatalogResource().keys;
	}
	
	public ResourceBundle getChoiceResourceBundle() {
		return getChoiceCatalogResource().getBundle(Options.getCurrentLocale());
	}
	
	public CatalogResource getChoiceCatalogResource() {
		Catalog catalog = medium.entry.getCatalog();
        ChoiceFeatureOption choiceFeatureOption = (ChoiceFeatureOption) getFeatureOption();
        return choiceFeatureOption.getCatalogResource();
    }

    public abstract void moveKeys(Map<String, String> map);
}
