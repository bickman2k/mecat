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
 *
 * Created on Jan 2, 2006
 * @author Stephan Richard Palm
 *
 * This class was build upon the public domain class from 
 * Werner Randelshofer.
 * 
 * http://www.randelshofer.ch/oop/javasplash/javasplash.html
 */

package net.sourceforge.mecat.catalog.gui.splasher;

import javax.swing.JOptionPane;

public class Splasher {
    /**
     * Shows the splash screen, launches the application and then disposes
     * the splash screen.
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        try {
            Class.forName("org.apache.xerces.parsers.DOMParser");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, 
                    "Xerces XML parser missing." + System.getProperty("line.separator") +
                    "The file XercesImpl.jar should be in the application classpath.", 
                    "Xerces XML parser missing.", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        SplashWindow.splash(Splasher.class.getResource("Logo.png"));
        SplashWindow.invokeMain("net.sourceforge.mecat.catalog.ApplicationMain", args);
        SplashWindow.disposeSplash();
    }
    
}
