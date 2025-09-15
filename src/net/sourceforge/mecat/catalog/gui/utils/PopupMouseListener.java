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
 * Created on Aug 4, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.gui.utils;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPopupMenu;

public abstract class PopupMouseListener implements MouseListener {


    public static class DefaultPopupMouseListener extends PopupMouseListener {
        JPopupMenu popup;
        
        public DefaultPopupMouseListener(JPopupMenu popup) {
            this.popup = popup;
        }
        
        public JPopupMenu getPopupMenu() {
            return popup;
        }
        
    }
    
    public abstract JPopupMenu getPopupMenu();
    
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

    public void mousePressed(MouseEvent e) { maybeShowPopup(e);}
    public void mouseReleased(MouseEvent e) { maybeShowPopup(e);}
    private void maybeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            getPopupMenu().show(e.getComponent(),
                       e.getX(), e.getY());
        }
    }
    
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
