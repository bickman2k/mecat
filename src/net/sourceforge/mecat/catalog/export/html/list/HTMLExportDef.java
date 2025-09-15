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
 * Created on Dec 2, 2005
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.export.html.list;

import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.Util;
import net.sourceforge.mecat.catalog.export.shared.list.ExportDef;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.option.Options;

public class HTMLExportDef extends ExportDef<HTMLExportDefType> {
    
    public Class<? extends Feature> feature = null;
    public String html_cmd = null;
    
    public HTMLExportDef() {
        super(HTMLExportDefType.MEDIUM);
    }

    /**
     * This function shall not be used for SHORT or FULL
     * It is intendet for MEDIUM, NAME
     *  @param type
     */
    public HTMLExportDef(HTMLExportDefType type) {
        super(type);
    }

    public HTMLExportDef(Class<? extends Feature> feature, HTMLExportDefType type) {
        super(type);
        this.feature = feature;
    }
    
    public HTMLExportDef(String html_cmd) {
        super(HTMLExportDefType.HTML);
        this.html_cmd = html_cmd;
    }
    
    public String toString() {
        if (type == HTMLExportDefType.NAME)
            return Options.getI18N(Medium.class).getString("Name");
        if (type == HTMLExportDefType.MEDIUM)
            return Options.getI18N(Medium.class).getString("Medium");
        if (type == HTMLExportDefType.HTML)
            return html_cmd;
        return Options.getI18N(feature).getString(feature.getSimpleName());
    }

    /* (non-Javadoc)
     * @see net.sourceforge.mecat.catalog.datamanagement.PersistentThroughEntry#loadFromEntry(net.sourceforge.mecat.catalog.datamanagement.Entry)
     */
    public boolean loadFromEntry(Entry entry) {
        return true;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.mecat.catalog.datamanagement.PersistentThroughEntry#saveToEntry(net.sourceforge.mecat.catalog.datamanagement.Entry)
     */
    public void saveToEntry(Entry entry) {
        switch (type) {
        case NAME:
            Util.addArgument(entry, new Util.Argument(0, null, type));
            break;
        case HTML:
            Util.addArgument(entry, new Util.Argument(0, null, html_cmd));
            break;
        case MEDIUM:
            break;
        case SHORT:
        case FULL:
            Util.addArgument(entry, new Util.Argument(0, null, feature));
            Util.addArgument(entry, new Util.Argument(1, null, type));
        }
    }

}
