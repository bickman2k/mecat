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
 * Created on Jun 7, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.medium.features.impl;

import javax.swing.JComponent;
import javax.swing.JPanel;

import net.sourceforge.mecat.catalog.filesystem.Result;
import net.sourceforge.mecat.catalog.filesystem.virtual.VirtualResult;
import net.sourceforge.mecat.catalog.gui.features.FeaturePanel;
import net.sourceforge.mecat.catalog.gui.features.RomFileListFeaturePanel;
import net.sourceforge.mecat.catalog.gui.features.RomFileListFeaturePanel.FinishReadingFileList;
import net.sourceforge.mecat.catalog.gui.features.desktop.FeatureDesktop;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.medium.features.option.FeatureOption;
import net.sourceforge.mecat.catalog.medium.hidden.Rom;

public class VirtualRomFileList extends AbstractRomFileList {

    
    
    public VirtualRomFileList(Rom rom) {
        super(rom);
    }

    @Override
    public JPanel getFeatureOptionPanel(FeatureOption featureOption) {
        return null;
    }

/*    @Override
    public JPanel getOptionPanel() {
        return null;
    }*/

    @Override
    public FeaturePanel getPanel(FeatureDesktop desktop, boolean border) {
        return new RomFileListFeaturePanel(this, desktop, border);
    }

    public Result getResult() {
        return new VirtualResult(medium.getFeature(VirtualRomMounts.class).getMounts());
    }

/*    @Override
    public boolean hasOptions() {
        return false;
    }*/

    public boolean hasResult() {
        return true;
    }

    @Override
    public void copyTo(Feature feature) {
        // TODO Auto-generated method stub
        
    }

    public void readFileList(JComponent parent, FinishReadingFileList list) {
        // Don't need this function        
    }

    public boolean isRemovable() {
        return false;
    }

    public void removeResult() {
        // Don't need this function        
    }
    
    

}
