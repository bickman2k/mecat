/*
 *  MeCat - Media Catalog more information on
 *  http://mecat.sourceforge.net/
 *  Copyright (C) 2005, Stephan Richard Palm, All Rights Reserved.
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
 * Created on Jun 22, 2005
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.gui;

import net.sourceforge.mecat.catalog.medium.Medium;

public class SelectMediaBackend extends MainFrameBackend {
    
    public SelectMediaBackend(final Display display) {
        super(display);
        setStatusbarEnabled(false);
    }
    
    // do not make menu
    @Override
    void InitM() {}
    
    // do not make toolbar
    @Override
    void makeToolBar() {}

    @Override
	public void reOpen() {}
    @Override
	protected boolean exit() { return true; }
    @Override
	protected void executeExit() { }
    
    protected Medium getMedium() {
        Object o = list.getSelectedValue();
        if (!(o instanceof Medium))
            return null;
        return (Medium) o;
        
    }
	
}
