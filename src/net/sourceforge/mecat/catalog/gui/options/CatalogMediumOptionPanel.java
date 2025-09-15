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
 * Created on Jan 11, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.gui.options;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalCheckBox;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.preferences.AbstractMediaOption;
import net.sourceforge.mecat.catalog.option.preferences.CatalogMediaOption;
import net.sourceforge.mecat.catalog.option.preferences.DefaultPreferences;
import net.sourceforge.mecat.catalog.option.preferences.GlobalMediaOption;
import net.sourceforge.mecat.catalog.option.preferences.MediaOption;

public class CatalogMediumOptionPanel extends JPanel {

    final Class<? extends Medium> mediumClass;
    final AbstractMediaOption mediaOption;
    final JCheckBox checkBox;

    // While the checkBox is unselected we going to show he general options
    // if it is selected we going to show the catalog options for this reason
    // we need a switch panel where once the general and once the catalog options
    // can be shown
    final JPanel switchPanel = new JPanel();

    public CatalogMediumOptionPanel(final Class<? extends Medium> mediumClass, final AbstractMediaOption mediaOption) {
        this.mediumClass = mediumClass;
        this.mediaOption = mediaOption;

        checkBox = new SimpleLocalCheckBox(Options.getI18N(CatalogMediumOptionPanel.class), "Override the medium's option for this catalog", mediaOption.isMediumOptionOverriden(mediumClass));
        checkBox.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                mediaOption.setMediumOptionOverrriden(mediumClass, checkBox.isSelected());
                setCorrectPanel();
            }
        });
        switchPanel.setLayout(new BorderLayout());
        setCorrectPanel();
        
        setLayout(new BorderLayout());
        add(checkBox, BorderLayout.NORTH);
        add(switchPanel);
    }

    protected MediaOption getDefaultOption() {
        if (mediaOption instanceof CatalogMediaOption) {
            if (Options.AppPrefs.getMediaOption().isMediumOptionOverriden(mediumClass))
                return Options.AppPrefs.getMediaOption();
            else
                return DefaultPreferences.defaultPreferences.getMediaOption();
        }
        
        if (mediaOption instanceof GlobalMediaOption)
            return DefaultPreferences.defaultPreferences.getMediaOption();
        
        return null;
    }

    void setCorrectPanel() {
        switchPanel.removeAll();
        MediumOptionPanel mop = null;
        if (mediaOption.isMediumOptionOverriden(mediumClass))
            mop = new MediumOptionPanel((Class<? extends Medium>)mediumClass, mediaOption);
        else {
            mop = new MediumOptionPanel((Class<? extends Medium>)mediumClass, getDefaultOption());
            OptionDialog.setEnabled(mop, false);
        }
        switchPanel.add(mop);
        switchPanel.updateUI();
    }
}
