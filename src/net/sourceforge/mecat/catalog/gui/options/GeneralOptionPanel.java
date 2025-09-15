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
package net.sourceforge.mecat.catalog.gui.options;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalTitledBorder;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.preferences.GeneralOption;

public class GeneralOptionPanel extends JPanel {

    final GeneralOption generalOption;
    
    public GeneralOptionPanel(final GeneralOption generalOption) {
        this.generalOption = generalOption;
        
        LanguagesOptionPanel languagesOptionPanel = new LanguagesOptionPanel(generalOption.getLanguagesOption());
        
        languagesOptionPanel.setBorder(new SimpleLocalTitledBorder(Options.getI18N(GeneralOptionPanel.class), "General languages option:"));
        
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 0;
        c.weightx = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;

        add(languagesOptionPanel, c);
        
        c.weighty = 1;
        c.gridheight = GridBagConstraints.REMAINDER;
        add(new JPanel(), c);
    }
    
}
