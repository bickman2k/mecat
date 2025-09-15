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
 * Created on Sep 19, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.option;

import java.lang.ref.WeakReference;

import javax.swing.event.ChangeEvent;

public class WeakLocalListener implements LocalListener {
    // Weak link to the panel
    final WeakReference<LocalListener> localListener;
    
    public WeakLocalListener(final LocalListener localListener) {
        this.localListener = new WeakReference<LocalListener>(localListener);
    }

    public void stateChanged(LocalListenerEvent event) {
        if (Options.DEBUG)
            System.out.println("WeakLocalListener " + this.hashCode() + " connnects to LocalListener " + ((localListener.get() == null)?"null":localListener.get().hashCode()));
        
        LocalListener listener = localListener.get();
        
        if (listener == null) {
            Options.removeLocalListener(this);
            if (Options.DEBUG)
                System.out.println("WeakLocalListener " + this.hashCode() + " removes itself.");
            return;
        }
        
        listener.stateChanged(event);
    }
}
