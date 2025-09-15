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
 * Created on Aug 28, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.gui.options.features;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sourceforge.mecat.catalog.export.ExportToFilePanel;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalButton;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalCheckBox;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalLabel;
import net.sourceforge.mecat.catalog.medium.features.option.ImageFeatureOption;
import net.sourceforge.mecat.catalog.option.Options;

public class ImageFeatureOptionPanel extends FeatureOptionPanel {

	JCheckBox checkBox = new SimpleLocalCheckBox(Options.getI18N(ImageFeatureOptionPanel.class), "Store into picture directory");
	JLabel label = new SimpleLocalLabel(Options.getI18N(ImageFeatureOptionPanel.class), "Position of the picture directory:");
	JButton browse = new SimpleLocalButton(Options.getI18N(ExportToFilePanel.class), "Select");
	JTextField text = new JTextField();
	ImageFeatureOption imageFeatureOption;

	public void changed() {
		try {
			imageFeatureOption.setDirLocation(new URL(text.getText()));
		} catch (MalformedURLException e) {
			// I don't care. So it is not stored.
			// Would make not much sense to store 
			// a wrong url would it.
		}
	}
	
	public void setEnables() {
		label.setEnabled(checkBox.isSelected());
		text.setEnabled(checkBox.isSelected());
		browse.setEnabled(checkBox.isSelected());
	}
	
	public ImageFeatureOptionPanel(final ImageFeatureOption imageFeatureOption) {
		this.imageFeatureOption = imageFeatureOption;
		
		checkBox.setSelected(imageFeatureOption.isDirStorage());
		if (imageFeatureOption.getDirLocation() != null)
			text.setText(imageFeatureOption.getDirLocation().toString());
		setEnables();
		
		checkBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				imageFeatureOption.setDirStorage(checkBox.isSelected());
				setEnables();
			}});
		text.addKeyListener(new KeyListener(){
			public void keyPressed(KeyEvent arg0) {changed();}
			public void keyReleased(KeyEvent arg0) {changed();}
			public void keyTyped(KeyEvent arg0) {changed();}
		});
		browse.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser choose = new JFileChooser();
				choose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = choose.showOpenDialog(null);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					try {
						imageFeatureOption.setDirLocation(choose.getSelectedFile().toURL());
						text.setText(choose.getSelectedFile().toURL().toString());
					} catch (MalformedURLException e) {
						return;
					}
				}
			}});
		
		
		this.setLayout(new BorderLayout());
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		panel.add(label, BorderLayout.NORTH);
		panel.add(browse, BorderLayout.EAST);
		panel.add(text, BorderLayout.CENTER);

		this.add(checkBox, BorderLayout.NORTH);
		this.add(panel, BorderLayout.CENTER);
    }
}
