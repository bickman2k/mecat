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
 * Created on May 24, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.medium.features.impl;

import javax.swing.JComponent;

import net.sourceforge.mecat.catalog.filesystem.Result;
import net.sourceforge.mecat.catalog.gui.features.RomFileListFeaturePanel.FinishReadingFileList;
import net.sourceforge.mecat.catalog.medium.features.Feature;

public interface RomFileList extends Feature {

    /**
     * Does this feature currently has a result
     * @return
     */
    public boolean hasResult();
    /**
     * Returns the current result/filelist
     * @return
     */
    public Result getResult();
    
    
    /**
     * If is able to return, this function has to work too.
     * One needs to put "Read file list" in the resource of the
     * feature implementing it.
     * 
     * @param panel
     * @param list 
     */
    public void readFileList(JComponent parent, FinishReadingFileList list);
    public boolean isRemovable();
    public void removeResult();

}
