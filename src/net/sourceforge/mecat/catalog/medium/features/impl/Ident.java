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
 * Created on Sep 9, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.medium.features.impl;

import java.util.UUID;

import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.changelog.ChangeLog;
import net.sourceforge.mecat.catalog.gui.features.FeaturePanel;
import net.sourceforge.mecat.catalog.gui.features.desktop.FeatureDesktop;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.AbstractFeature;
import net.sourceforge.mecat.catalog.medium.features.BadCondition;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.medium.features.menu.IdentMenu;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.sort.ByIdent;
import net.sourceforge.mecat.catalog.sort.ConfigurableComparator;

public class Ident extends AbstractFeature {

	private static final String AttributName = "UUID";

    static {
        IdentMenu.registerMenu();        
    }
    
	public Ident(Medium medium) {
		super(medium);

		UUID uuid = getUUID();
		if (uuid == null) {
//            int transId = -1;
//            ChangeLog changeLog = medium.getListing().getChangeLog();
//            if (changeLog != null)
//                transId = changeLog.openTransaction(Options.getI18N(Ident.class).getString("Set UUID"), false, false);
            startTransaction(Options.getI18N(Ident.class).getString("Set UUID"), false, false);
			setUUID(UUID.randomUUID());
//            if (changeLog != null)
//                changeLog.closeTransaction(transId);
            stopTransaction();
        }
	}
	
    @Override
	public FeaturePanel getPanel(FeatureDesktop desktop, boolean border) {
		return null;
	}

	public boolean hasOptions() {
		return false;
	}

	public boolean validate(String condition) throws BadCondition {
		return medium.entry.getAttribute(AttributName).compareTo(condition) == 0;
	}

	public String getText() {
		return medium.entry.getAttribute(AttributName);
	}

	public String getShortText() {
		return medium.entry.getAttribute(AttributName);
	}

    public String getTextHTML(int availableWidth) {
        return medium.entry.getAttribute(AttributName);
    }

    public String getShortTextHTML() {
        return medium.entry.getAttribute(AttributName);
    }

	public UUID getUUID() {
		String UUID_str = medium.entry.getAttribute(AttributName);
		UUID uuid = null;
		if (UUID_str != null)
			uuid = UUID.fromString(UUID_str);
		return uuid;
	}
	
	public void setUUID(UUID uuid) {
		medium.entry.setAttribute(AttributName, uuid.toString());
	}
	
    public static ConfigurableComparator getComparator() {
        return new ByIdent(); 
    }
    
    public static void individualizeEntry(final Entry entry) {
        entry.setAttribute(AttributName, UUID.randomUUID().toString());
    }
    
    public boolean hasValue() {
        return true;
    }

    public void copyTo(Feature feature) {
		if (!(feature instanceof Ident))
			return;
		// The ident information will not be copied
        // this would lead to two the same instances.
//		Ident ident = (Ident)feature;
//		ident.setUUID(getUUID());
	}
}
