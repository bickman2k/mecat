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

import net.sourceforge.mecat.catalog.filesystem.Result;
import net.sourceforge.mecat.catalog.gui.features.FeaturePanel;
import net.sourceforge.mecat.catalog.gui.features.RomFileListFeaturePanel;
import net.sourceforge.mecat.catalog.gui.features.desktop.FeatureDesktop;
import net.sourceforge.mecat.catalog.medium.features.AbstractFeature;
import net.sourceforge.mecat.catalog.medium.features.BadCondition;
import net.sourceforge.mecat.catalog.medium.features.menu.RomFileListMenu;
import net.sourceforge.mecat.catalog.medium.hidden.Rom;
import net.sourceforge.mecat.catalog.option.Options;

public abstract class AbstractRomFileList extends AbstractFeature implements RomFileList {

    static {
        RomFileListMenu.registerMenu();        
    }
    
    public AbstractRomFileList(Rom rom) {
        super(rom);
    }
    
    @Override
    public FeaturePanel getPanel(FeatureDesktop desktop, boolean border) {
        return new RomFileListFeaturePanel(this, desktop, border);
    }

    public boolean validate(String condition) throws BadCondition {
        return false;
    }

    public String getText() {
        if (!hasResult())
            return Options.getI18N(AbstractRomFileList.class).getString("There is no file list stored for this medium.");
        Result result = getResult();
        switch (result.getNumDirs()) {
        // There has to be at least the root directory
        // Therefor we do not need to check for 0 directories
        case 1 :
            switch (result.getNumDirs()) {
            case 0:
                return Options.getI18N(AbstractRomFileList.class).getString("There is only the root directory.");
            case 1:
                return Options.getI18N(AbstractRomFileList.class).getString("There is one file in the root directory.");
            default:
                return Options.getI18N(AbstractRomFileList.class).getString("There are [FILES] files in the root directory.").replaceAll("\\[FILES\\]", "" + result.getNumFiles());
            }
        default :
            switch (result.getNumDirs()) {
            case 0:
                return Options.getI18N(AbstractRomFileList.class).getString("There are [DIRS] directories, but no file.").replaceAll("\\[DIRS\\]", "" + result.getNumDirs());
            case 1:
                return Options.getI18N(AbstractRomFileList.class).getString("There are [DIRS] directories and one file.").replaceAll("\\[DIRS\\]", "" + result.getNumDirs());
            default:
                return Options.getI18N(AbstractRomFileList.class).getString("There are [DIRS] directories and [FILES] files.").replaceAll("\\[DIRS\\]", "" + result.getNumDirs()).replaceAll("\\[FILES\\]", "" + result.getNumFiles());
            }
        }
    }

    public String getShortText() {
        if (!hasResult())
            return Options.getI18N(AbstractRomFileList.class).getString("No file list.");
        return null;
    }

    public String getTextHTML(int availableWidth) {
        if (!hasResult())
            return Options.getI18N(AbstractRomFileList.class).getString("There is no file list stored for this medium.");
        Result result = getResult();
        switch (result.getNumDirs()) {
        // There has to be at least the root directory
        // Therefor we do not need to check for 0 directories
        case 1 :
            switch (result.getNumFiles()) {
            case 0:
                return Options.getI18N(AbstractRomFileList.class).getString("There is only the root directory.");
            case 1:
                return Options.getI18N(AbstractRomFileList.class).getString("There is one file in the root directory.");
            default:
                return Options.getI18N(AbstractRomFileList.class).getString("There are [FILES] files in the root directory.").replaceAll("\\[FILES\\]", "<strong>" + result.getNumFiles() + "</strong>");
            }
        default :
            switch (result.getNumFiles()) {
            case 0:
                return Options.getI18N(AbstractRomFileList.class).getString("There are [DIRS] directories, but no file.").replaceAll("\\[DIRS\\]", "<strong>" + result.getNumDirs() + "</strong>");
            case 1:
                return Options.getI18N(AbstractRomFileList.class).getString("There are [DIRS] directories and one file.").replaceAll("\\[DIRS\\]", "<strong>" + result.getNumDirs() + "</strong>");
            default:
                return Options.getI18N(AbstractRomFileList.class).getString("There are [DIRS] directories and [FILES] files.").replaceAll("\\[DIRS\\]", "<strong>" + result.getNumDirs() + "</strong>").replaceAll("\\[FILES\\]", "<strong>" + result.getNumFiles() + "</strong>");
            }
        }
    }

    public String getShortTextHTML() {
        if (!hasResult())
            return Options.getI18N(AbstractRomFileList.class).getString("No file list.");
        return null;
    }

    @Override
    public boolean hasValue() {
        return hasResult();
    }

}
