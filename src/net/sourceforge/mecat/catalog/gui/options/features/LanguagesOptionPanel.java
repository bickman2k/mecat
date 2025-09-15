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
 * Created on Jul 18, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.gui.options.features;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JScrollPane;

import net.sourceforge.mecat.catalog.option.Options;

public class LanguagesOptionPanel extends FeatureOptionPanel {

	String choices[] = new String[Locale.getISOLanguages().length];

	JList selection, choice;
	JCheckBox autoSelect = new JCheckBox("TODO Text = Autoselect");

	public LanguagesOptionPanel(){
		
		this.setLayout(new BorderLayout());
		for (int i = 0; i < Locale.getISOLanguages().length; i++) {
			choices[i] = (new Locale(Locale.getISOLanguages()[i])).getDisplayLanguage(Options.getCurrentLocale());
		}
		choice = new JList(choices);
		selection =  new JList();
		
		autoSelect.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				choice.setEnabled(!autoSelect.isSelected());
//				choice.setEnabled(!autoSelect.isSelected());
			}
		});
		
		add(autoSelect, BorderLayout.NORTH);
		add(new JScrollPane(selection), BorderLayout.WEST);
		add(new JScrollPane(choice), BorderLayout.EAST);

	}

/*	public void rearrange() {
		this.removeAll();
		int max_width = checkboxes[0].getWidth();
		for (int i = 1; i < Locale.getISOLanguages().length; i++) 
			if (checkboxes[i].getWidth() > max_width)
				max_width = checkboxes[i].getWidth();

		int cols = getWidth() / max_width;
		int rows = (Locale.getISOLanguages().length-1) / cols + 1;
		this.setLayout(new GridLayout(rows, cols));
		for (int i = 0; i < Locale.getISOLanguages().length; i++) 
			add(checkboxes[i]);
	}*/

}
