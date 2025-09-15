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
 * Created on Jul 28, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.gui.options.filter;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sourceforge.mecat.catalog.filter.Filter;
import net.sourceforge.mecat.catalog.filter.Parser;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalButton;
import net.sourceforge.mecat.catalog.medium.features.BadCondition;
import net.sourceforge.mecat.catalog.option.Options;

public class CreateFilter extends JPanel {

	protected JTextField filter_txt = new JTextField();
	protected JButton verify = new SimpleLocalButton(Options.getI18N(CreateFilter.class), "Verify");
    
	public CreateFilter(final Filter filter) {
		this.filter_txt.setText(filter.getCondition());
		setLayout(new BorderLayout());
		add(BorderLayout.EAST, verify);
		add(this.filter_txt);
		verify.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if (!Parser.checkFilter(filter_txt.getText())) {
					JOptionPane.showMessageDialog(CreateFilter.this, Options.getI18N(CreateFilter.class).getString("The filter you chose is broken."), Options.getI18N(CreateFilter.class).getString("Broken Filter."), JOptionPane.ERROR_MESSAGE );
				}
			}});
	}
	
	public Filter getFilter() throws BadCondition {
        Parser parse = new Parser(filter_txt.getText());
		return parse.parse();
	}
	
}
