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
 * Created on Sep 13, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.medium.features.option;

import net.sourceforge.mecat.catalog.datamanagement.Connection;
import net.sourceforge.mecat.catalog.datamanagement.Entry;

public class SubEntryListFeatureOption extends AbstractSubEntryFeatureOption {

    /**
     * If this flag is set to true
     * then only a short version 
     * (the same as for the media list)
     * will be shown in the view desktop.
     *
     */
    boolean useShortVersionForView = false;
    
    public SubEntryListFeatureOption() {
    }

    public SubEntryListFeatureOption(Boolean preferLink) {
        super(preferLink);
    }
    public SubEntryListFeatureOption(Boolean preferLink, Boolean useShortVersionForView) {
        super(preferLink);
        this.useShortVersionForView = useShortVersionForView;
    }
    public SubEntryListFeatureOption(Boolean preferLink, Boolean preferExternCatalog, String externCatalog) {
        super(preferLink, preferExternCatalog, externCatalog);
    }
    public SubEntryListFeatureOption(Boolean preferLink, Boolean useShortVersionForView, Boolean preferExternCatalog, String externCatalog) {
        super(preferLink, preferExternCatalog, externCatalog);
        this.useShortVersionForView = useShortVersionForView;
    }
    public SubEntryListFeatureOption(Boolean preferLink, Boolean preferExternCatalog, Connection externCatalog) {
        super(preferLink, preferExternCatalog, externCatalog);
    }
    public SubEntryListFeatureOption(Boolean preferLink, Boolean useShortVersionForView, Boolean preferExternCatalog, Connection externCatalog) {
        super(preferLink, preferExternCatalog, externCatalog);
        this.useShortVersionForView = useShortVersionForView;
    }

    public boolean isUseShortVersionForView() {
        return useShortVersionForView;
    }
    public void setUseShortVersionForView(boolean useShortVersionForView) {
        this.useShortVersionForView = useShortVersionForView;
    }

    
    
    @Override
    public boolean loadFromEntry(Entry entry) {
        if (!super.loadFromEntry(entry))
            return false;
        
        String useShortVersionForViewStr = entry.getAttribute("useShortVersionForView");
        if (useShortVersionForViewStr != null && useShortVersionForViewStr.equalsIgnoreCase("true"))
            useShortVersionForView = true;
        
        return true;
    }

    @Override
    public void saveToEntry(Entry entry) {
        super.saveToEntry(entry);
        if (useShortVersionForView)
            entry.setAttribute("useShortVersionForView", "true");
    }

    
}
