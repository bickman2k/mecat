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
 * Created on Jan 5, 2006
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.export.latex.list;

import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.Util;
import net.sourceforge.mecat.catalog.export.shared.list.ExportDef;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.option.Options;

public 	class LaTeXExportDef extends ExportDef<LaTeXExportDefType> {

	public Class<? extends Feature> feature = null;
	public String latex_cmd = null;
	
	public LaTeXExportDef() {
		super(LaTeXExportDefType.MEDIUM);
	}

    /**
     * This function shall not be used for SHORT or FULL
     * It is intendet for MEDIUM, NAME
     *  @param type
     */
    public LaTeXExportDef(LaTeXExportDefType type) {
        super(type);
    }
	
	public LaTeXExportDef(Class<? extends Feature> feature, LaTeXExportDefType type) {
        super(type);
		this.feature = feature;
	}
	
	public LaTeXExportDef(String latex_cmd) {
        super(LaTeXExportDefType.LATEX);
		this.latex_cmd = latex_cmd;
	}
	
	public String toString() {
        if (type == LaTeXExportDefType.NAME)
            return Options.getI18N(Medium.class).getString("Name");
		if (type == LaTeXExportDefType.MEDIUM)
			return Options.getI18N(Medium.class).getString("Medium");
		if (type == LaTeXExportDefType.LATEX)
			return latex_cmd;
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
	    case LATEX:
	        Util.addArgument(entry, new Util.Argument(0, null, latex_cmd));
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
