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
 * Created on Jun 9, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.gui;

import java.awt.AWTEvent;
import java.awt.Window;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import net.sourceforge.mecat.catalog.gui.MainFrameBackend.Display;

public class MainFrameFrontend  extends JFrame implements Display {

    MainFrameBackend mainFrameBackend = new MainFrameBackend(this);
    
    public MainFrameFrontend() {
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    }

    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            if (!mainFrameBackend.exit())
                return;
            super.processWindowEvent(e);
            mainFrameBackend.executeExit();
        }
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_OPENED) {
            mainFrameBackend.setGUIoptions();
        }
    }

    public Window getWindow() {
        return this;
    }

}
