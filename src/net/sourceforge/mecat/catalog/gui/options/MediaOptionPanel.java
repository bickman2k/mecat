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
 * Created on Jan 7, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.gui.options;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalTitledBorder;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.preferences.AbstractMediaOption;
import net.sourceforge.mecat.catalog.option.preferences.MediaOption;
import net.sourceforge.mecat.srp.utils.NiceClass;
import net.sourceforge.mecat.srp.utils.NiceClassDisplayNameComparator;

public class MediaOptionPanel extends JPanel {

    final /*Abstract*/MediaOption mediaOption;
    final Vector<NiceClass<Medium>> media = new Vector<NiceClass<Medium>>();
    
    public MediaOptionPanel(final /*Abstract*/MediaOption mediaOption) {
        this.mediaOption = mediaOption;
        JPanel visibilityMedia = new JPanel();
        visibilityMedia.setLayout(new GridLayout(0, 1));
        visibilityMedia.setBorder(new SimpleLocalTitledBorder(Options.getI18N(MediaOptionPanel.class), "Visibility of media"));

        for (Class<? extends Medium> medium_class : AbstractMediaOption.getMedia()) 
            media.add(new NiceClass<Medium>(medium_class));
        Collections.sort(media, NiceClassDisplayNameComparator.displayNameComparator);
        
        for (final NiceClass<Medium> medium : media) {
            final JCheckBox checkBox = new JCheckBox(medium.toString(), mediaOption.isWanted(medium.getClasstype()));
            checkBox.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent arg0) {
                    if (mediaOption instanceof AbstractMediaOption)
                        ((AbstractMediaOption)mediaOption).setWanted(medium.getClasstype(), checkBox.isSelected());
                }});
            visibilityMedia.add(checkBox);
        }
        
        setLayout(new BorderLayout());
        add(visibilityMedia, BorderLayout.NORTH);
        add(new JPanel());
    }

}
