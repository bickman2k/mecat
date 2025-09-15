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
 * Created on Jan 21, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.option.preferences;

import net.sourceforge.mecat.catalog.datamanagement.Util;
import net.sourceforge.mecat.catalog.option.Options;

public class CatalogGeneralOption extends AbstractGeneralOption {

    @Override
    public void setOverrideLanguagesOption(boolean override) {
        // Set default from Options if the options
        // becomes available now.
        if (!overrideLanguagesOption && override) {
            GeneralOption go;
            // Decide from where to take the LanguagesOptions to fill the new one
            if (Options.AppPrefs.getGeneralOption().isOverrideLanguagesOption())
                go = Options.AppPrefs.getGeneralOption();
            else
                go = DefaultPreferences.defaultPreferences.getGeneralOption();

            languagesOption = Util.copyPTE(go.getLanguagesOption());
        }
        // Set the new status for overriding media options
        overrideLanguagesOption = override;
    }

}
