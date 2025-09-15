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
 * Created on Sep 23, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.export;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.PersistentThroughEntry;
import net.sourceforge.mecat.catalog.datamanagement.Util;
import net.sourceforge.mecat.catalog.gui.utils.SimpleFileFilter;
import net.sourceforge.mecat.catalog.i18n.util.LocalTitledBorder;
import net.sourceforge.mecat.catalog.option.LocalListener;
import net.sourceforge.mecat.catalog.option.LocalListenerEvent;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.WeakLocalListener;

public class ExportToFilePanel extends JPanel implements PersistentThroughEntry, LocalListener {

	JCheckBox activated;
	JTextField textfield = new JTextField();
	JButton chooseButton;
    final static String defaultBegin = "export";
	
	String typ, ending, fullEnding;
	boolean allways = false;
    final int mode;
    
	
	@Override
    public Dimension getPreferredSize() {
        Dimension dim = super.getPreferredSize();
        dim.width *= 1.5;
        return dim;
    }

    public ExportToFilePanel(ExportToFilePanel etfp) {
		this(etfp.typ, etfp.mode, etfp.ending, etfp.activated.isSelected(), true);
	}
    
//    public void setFileSelectionMode(final Integer mode) {
//        fileChooseMode = mode;
//    }
//    
//    public int getFileSelectionMode() {
//        return fileChooseMode;
//    }
	
	/**
	 * Same as ExportToFilePanel(typ, true)
	 * @param typ
	 * @see #ExportToFilePanel(String, boolean)
	 */
	public ExportToFilePanel(final String typ) {
		this(typ, true);
	}
    public ExportToFilePanel(final String typ, final Integer mode) {
        this(typ, mode, true);
    }
	
	/**
	 * 
	 * @param typ Set the text to be shown and the file ending. 
	 * 	Uses lowercase version of the parameter for the fileending.
	 * @param activ Defines if the Checkbox will be set on or off
	 * 
	 * @see #ExportToFilePanel(String, String, boolean)
	 */
	public ExportToFilePanel(final String typ, final Boolean activ) {
		this(typ, (typ == null)?null:typ.toLowerCase(), activ);
	}
    public ExportToFilePanel(final String typ, final Integer mode, final Boolean activ) {
        this(typ, mode, (typ == null)?null:typ.toLowerCase(), activ);
    }

	/**
	 * Same as ExportToFile(typ, fileEnding, true)
	 * @param typ
	 * @param fileEnding
	 * @see #ExportToFilePanel(String, String, boolean)
	 */
	public ExportToFilePanel(final String typ, final String fileEnding) {
		this(typ, fileEnding, true);
	}
    public ExportToFilePanel(final String typ, final Integer mode, final String fileEnding) {
        this(typ, mode, fileEnding, true);
    }
	
	/**
	 * 
	 * @param fileEnding Set the ending of the file
	 * @param typ Set the text to be shown
	 * @param activ Defines if the Checkbox will be set on or off
	 * @param allways If true activ is considered true and there is no checkbox to deactived
	 */
	public ExportToFilePanel(final String typ, final String fileEnding, final Boolean activ) {
		this(typ, fileEnding, activ, false);
	}
    public ExportToFilePanel(final String typ, final Integer mode, final String fileEnding, final Boolean activ) {
        this(typ, mode, fileEnding, activ, false);
    }
	
    public ExportToFilePanel(final String typ, final Boolean activ, final Boolean allways) {
        this(typ, typ.toLowerCase(), activ, allways);
    }
    public ExportToFilePanel(final String typ, final Integer mode, final Boolean activ, final Boolean allways) {
        this(typ, typ.toLowerCase(), activ, allways);
    }
    public ExportToFilePanel(final String typ, final String ending, final Boolean activ, final Boolean allways) {
        this(typ, JFileChooser.FILES_ONLY, ending, activ, allways);
    }
    public ExportToFilePanel(final String typ, final Integer mode, final String ending, final Boolean activ, final Boolean allways) {
        final ResourceBundle res = Options.getI18N(ExportToFilePanel.class);
		this.typ = typ; this.mode = mode; this.ending = ending; this.allways = allways;
        if (ending == null)
            fullEnding = "";
        else
            fullEnding = "." + ending;
		this.setLayout(new BorderLayout());
		
		activated = new JCheckBox("", ((allways)?true:activ));
		chooseButton = new JButton();
		textfield.setText(System.getProperty("user.home") + System.getProperty("file.separator") + defaultBegin + fullEnding);
	
		activated.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {  /*LOOK*/setEnables();/*HERE*/  }});
		chooseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) { /*LOOK*/chooseFile();/*HERE*/}});
		
		setEnables();

		if (!allways)
			add(activated, BorderLayout.SOUTH);
		add(chooseButton, BorderLayout.EAST);
		add(textfield, BorderLayout.CENTER);

        setBorder(new LocalTitledBorder() {
            protected String getLocalTitle() {
                return (mode == JFileChooser.DIRECTORIES_ONLY)?res.getString("directory"):(typ + " " + res.getString("file") + ":");
            }
        });

        
        Options.addLocalListener(new WeakLocalListener(this));
        // Set labels to the current locale
        setLabels();
    }

    public void stateChanged(LocalListenerEvent event) {
        setLabels();
    }
    
    public void setLabels() {
        ResourceBundle res = Options.getI18N(ExportToFilePanel.class);

        if (mode == JFileChooser.DIRECTORIES_ONLY)
            activated.setText(res.getString("Export to directory [DIR]").replaceAll("\\[DIR\\]", res.getString("directory")));
        else
            activated.setText(res.getString("Export to [TYP]").replaceAll("\\[TYP\\]", typ));
        chooseButton.setText(res.getString("Select"));
    }
	
	protected void chooseFile() {
		JFileChooser choose = new JFileChooser();
		choose.setSelectedFile(new File(textfield.getText()));
        if (ending != null)
            choose.setFileFilter(new SimpleFileFilter(typ, ending));
        choose.setFileSelectionMode(mode);
		int returnVal = choose.showSaveDialog(null);
		if(returnVal == JFileChooser.APPROVE_OPTION) 
			textfield.setText(choose.getSelectedFile().toString());
		
	}
	
	protected void setEnables() {
		chooseButton.setEnabled(activated.isSelected());
		textfield.setEnabled(activated.isSelected());
	}
	
	public boolean isActive() {
		return activated.isSelected();
	}
	
	public String getFileName() {
		return textfield.getText();
	}

    public boolean loadFromEntry(Entry entry) {
        String fileName = entry.getAttribute("Filename");
        if ((fileName != null) && (fileName.length() > 0))
            textfield.setText(fileName);
//        String fileSelectionModeStr = entry.getAttribute("FileSelectionMode"); 
//        if (fileSelectionModeStr != null)
//        try {
//            fileChooseMode = Integer.valueOf(fileSelectionModeStr);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        
        return true;
    }

    public void saveToEntry(Entry entry) {
        int pos = 0;
        Util.addArgument(entry, new Util.Argument(pos++, String.class, typ));
        if (mode != JFileChooser.FILES_ONLY)
            Util.addArgument(entry, new Util.Argument(pos++, null, mode));
        if (ending != null && ending.compareTo(typ.toLowerCase()) != 0)
            Util.addArgument(entry, new Util.Argument(pos++, String.class, ending));
        if (allways == true) {
            Util.addArgument(entry, new Util.Argument(pos++, Boolean.class, activated.isSelected()));
            Util.addArgument(entry, new Util.Argument(pos++, Boolean.class, allways));
        } else if (activated.isSelected() == false)
            Util.addArgument(entry, new Util.Argument(pos++, Boolean.class, activated.isSelected()));
        if (textfield.getText().compareTo(System.getProperty("user.home") + System.getProperty("file.separator") + defaultBegin + fullEnding) != 0) 
            entry.setAttribute("Filename", textfield.getText());
    }
}
