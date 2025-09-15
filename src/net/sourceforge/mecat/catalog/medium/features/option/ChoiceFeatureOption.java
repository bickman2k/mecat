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

import java.awt.Component;

import net.sourceforge.mecat.catalog.datamanagement.Catalog;
import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.Util;
import net.sourceforge.mecat.catalog.datamanagement.Util.Argument;
import net.sourceforge.mecat.catalog.i18n.fromCatalog.CatalogResource;
import net.sourceforge.mecat.catalog.option.Options;

public class ChoiceFeatureOption implements FeatureOption {

    final String resourceName;
    Catalog dummyCatalog = Options.stdFactory.createCatalog((Component)null);
    Entry entry = dummyCatalog.createEntry("ChoiceFeatureOption");
    CatalogResource catalogResource;
    
    public ChoiceFeatureOption(final String resourceName) {
        this.resourceName = resourceName;
        catalogResource = new CatalogResource(entry, resourceName);
        
    }
    
    public ChoiceFeatureOption(final String resourceName, CatalogResource catalog) {
        this(resourceName);
        catalog.copyTo(catalogResource);
    }
    
    public boolean loadFromEntry(Entry entry) {
        CatalogResource cr = new CatalogResource(entry, resourceName);
        cr.copyTo(catalogResource);
        return true;
    }

    public void saveToEntry(Entry entry) {
        Util.addArgument(entry, new Argument(0, String.class, resourceName));
        CatalogResource cr = new CatalogResource(entry, resourceName);
        catalogResource.copyTo(cr);
    }

    public CatalogResource getCatalogResource() {
        return catalogResource;
    }
    
    public String getResourceName() {
        return resourceName;
    }
    
    public String toString(){
        StringBuffer buffer = new StringBuffer();

        buffer.append(resourceName + ":" + System.getProperty("line.separator"));
        buffer.append(catalogResource.toString());
        
        return buffer.toString();
    }
}
