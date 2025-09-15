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
 *  Created on Jul 15, 2004
 *  @author Stephan Richard Palm
 *  
 *  Center the application frame.
 */

package net.sourceforge.mecat.catalog;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import net.sourceforge.mecat.catalog.gui.MainFrameFrontend;
import net.sourceforge.mecat.catalog.option.FromCommandLine;
import net.sourceforge.mecat.catalog.option.Options;

public class ApplicationMain {
   protected boolean packFrame = false;
		
	public ApplicationMain() {
	    try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
            		MainFrameFrontend frame = new MainFrameFrontend();
            		if (packFrame) {
               			frame.pack();
            		} 	else {
            			frame.validate();
            		}
            		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            		frame.setSize(screenSize.width, screenSize.height);
                    Options.time("ApplicationMain: Finish MainFrameFrontend");
            		frame.setVisible(true);
            		Options.time("ApplicationMain: Show GUI");
                    Options.endTimeing();
                }
            });
        } catch (InterruptedException e) {
            // Ignore: If this exception occurs, we return too early, which
            // makes the splash window go away too early.
            // Nothing to worry about. Maybe we should write a log message.
        } catch (InvocationTargetException e) {
            // Error: Startup has failed badly. 
            // We can not continue running our application.
            InternalError error = new InternalError();
            error.initCause(e);
            throw error;
        }
	}

    /**
     * Main method for starting the application
     * @param -v for verbosity
     * -vn  n-th level of verbosity
     * -d debug modus
     * --screenshot Size of the Window optimal for screenshot
     */
	public static void main(String[] args) {
        for (String arg : args) {
            if (arg.equalsIgnoreCase("--screenshot")) {
                FromCommandLine.screenshot = true;
                continue;
            }
            if (arg.equals("-v")) {
                FromCommandLine.verbosity = 1;
                continue;
            }
            if (arg.startsWith("-v")) {
                try {
                    FromCommandLine.verbosity = Integer.parseInt(arg.substring(2));
                } catch (NumberFormatException e){
                    FromCommandLine.verbosity = 1;
                }
                continue;
            }
            if (arg.equals("-d")) {
                FromCommandLine.debug = true;
                continue;
            }
        }
        
 		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception e) {
			e.printStackTrace();
	 	}
		new ApplicationMain();
	}
}