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
 * Created on 22.01.2007
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filesystem.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import net.sourceforge.mecat.catalog.gui.features.desktop.ChainableFeatureDesktop;
import net.sourceforge.mecat.catalog.medium.features.impl.RomFileList;

public class ExplorerDesktopFrame extends JFrame {

    final ExplorerDesktop explorerDesktop;
    
    public ExplorerDesktopFrame(final ChainableFeatureDesktop desktop, RomFileList rfl) {
        explorerDesktop = new ExplorerDesktop(desktop, rfl.getClass());
        explorerDesktop.setMedium(rfl.getMedium());

        setLayout(new BorderLayout());
        add(explorerDesktop.getExplorer().getToolBar(this), BorderLayout.PAGE_START);
        add(explorerDesktop.getDesktop());

        setSize(new Dimension(800,600));
    }
    
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_OPENED) 
            explorerDesktop.explorer.split.setDividerLocation(350);

        super.processWindowEvent(e);
    }
}
