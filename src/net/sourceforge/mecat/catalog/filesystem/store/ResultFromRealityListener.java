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
 * Created on May 27, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filesystem.store;

public interface ResultFromRealityListener {

    public void directoryFound(ResultFromRealityEvent event);
    public void fileFound(ResultFromRealityEvent event);
    public void sizeChanged(ResultFromRealityEvent event);

    public void md5Started(ResultFromRealityEvent event);
    public void md5Finished(ResultFromRealityEvent event);
    public void md5FileFinished(ResultFromRealityEvent event);
    
    public void md5SizeChanged(ResultFromRealityEvent event);
    public void md5IOException(ResultFromRealityEvent event);
    public void md5Interupted(ResultFromRealityEvent event);
    
    public void finishingStarted(ResultFromRealityEvent event);
    public void finishingSteped(ResultFromRealityEvent event);
    public void finishingFinished(ResultFromRealityEvent event);

    public void resultComputed(ResultFromRealityEvent event);
    public void interupted(ResultFromRealityEvent event);
    public void logEntry(ResultFromRealityEvent event);
    
    public void findTagStarted(ResultFromRealityEvent event);
    public void findTagFileFinished(ResultFromRealityEvent event);
    public void findTagFinished(ResultFromRealityEvent event);
}
