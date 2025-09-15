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
 * Created on Jan 21, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.gui.options.features;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import net.sourceforge.mecat.catalog.gui.options.OptionDialog;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalCheckBox;
import net.sourceforge.mecat.catalog.medium.features.option.LanguagesFeatureOption;
import net.sourceforge.mecat.catalog.option.Options;

public class LanguagesFeatureOptionPanel extends JPanel {

    final LanguagesFeatureOption languagesFeatureOption;
    net.sourceforge.mecat.catalog.gui.options.LanguagesOptionPanel languagesOptionPanel;
    JCheckBox checkBox = new SimpleLocalCheckBox(Options.getI18N(LanguagesFeatureOptionPanel.class), "Override global languages");
    
    public LanguagesFeatureOptionPanel(final LanguagesFeatureOption languagesFeatureOption) {
        this.languagesFeatureOption = languagesFeatureOption;
        
        languagesOptionPanel = new net.sourceforge.mecat.catalog.gui.options.LanguagesOptionPanel(languagesFeatureOption);
        
        JPanel fill = new JPanel();
        
        setLayout(new BorderLayout());
        fill.setLayout(new BorderLayout());
        
        add(checkBox, BorderLayout.NORTH);
        add(fill);
        
        fill.add(languagesOptionPanel, BorderLayout.NORTH);
        fill.add(new JPanel());
        
        checkBox.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                languagesFeatureOption.setOverrideGlobalLanguages(checkBox.isSelected());
                OptionDialog.setEnabled(languagesOptionPanel, languagesFeatureOption.isOverrideGlobalLanguages());
            }
        });

        checkBox.setSelected(languagesFeatureOption.isOverrideGlobalLanguages());
    }
}
