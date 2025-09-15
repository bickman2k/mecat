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
 * Created on Sep 29, 2005
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.gui.features.desktop.image;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import net.sourceforge.mecat.catalog.medium.Listing;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.srp.utils.StatusBar;

public class ImageDesktopFrame extends JFrame implements MediumFocusListener {
    
    final StatusBar statusBar = new StatusBar(2);
    final ImageDesktopCanvas canvas;
    final JLabel statusText = new JLabel();
    
    public ImageDesktopFrame(final Listing listing) {
        canvas = new ImageDesktopCanvas(listing);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new JScrollPane(canvas));
        getContentPane().add(statusBar, BorderLayout.SOUTH);
        
        canvas.addMediumFocusListener(this);
        statusBar.getStatusPanel(1).add(statusText);
    }

    public void FocusChanged(final Medium medium) {
        if (medium != null)
            statusText.setText(medium.toString());
    }
}
