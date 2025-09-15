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
 * Created on Oct 6, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.medium.features;

import java.awt.Component;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;

import javax.swing.JPanel;

import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.SubEntryEvent;
import net.sourceforge.mecat.catalog.datamanagement.SubEntryListener;
import net.sourceforge.mecat.catalog.filter.Filter;
import net.sourceforge.mecat.catalog.filter.MediumFilter;
import net.sourceforge.mecat.catalog.filter.Parser;
import net.sourceforge.mecat.catalog.gui.MainFrameBackend;
import net.sourceforge.mecat.catalog.gui.SelectMediaFrontend;
import net.sourceforge.mecat.catalog.gui.features.FeaturePanel;
import net.sourceforge.mecat.catalog.gui.features.SubEntryFeaturePanel;
import net.sourceforge.mecat.catalog.gui.features.desktop.FeatureDesktop;
import net.sourceforge.mecat.catalog.gui.options.features.SubEntryFeatureOptionPanel;
import net.sourceforge.mecat.catalog.medium.Listing;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.impl.Ident;
import net.sourceforge.mecat.catalog.medium.features.option.FeatureOption;
import net.sourceforge.mecat.catalog.medium.features.option.SubEntryFeatureOption;
import net.sourceforge.mecat.catalog.option.Options;

public class SubEntryFeature<T extends Medium> extends AbstractSubEntryFeature implements SubEntryListener {

	public String attributeName;
	Class<T> type;
	
	public SubEntryFeature(Medium medium, String attributeName, Class<T> entryType) {
		super(medium);
		this.attributeName = attributeName;
		type = entryType;
	}

    @Override
	public FeaturePanel getPanel(FeatureDesktop desktop, boolean border) {
        SubEntryFeatureOption subEntryFeatureOption = getSubEntryFeatureOption();
        if (subEntryFeatureOption == null)
            return new SubEntryFeaturePanel(this, desktop, border, false);
		return new SubEntryFeaturePanel(this, desktop, border, subEntryFeatureOption.isPreferLink());
	}

	public boolean hasOptions() {
		return true;
	}

    @Override
    public JPanel getFeatureOptionPanel(FeatureOption featureOption) {
        if (!(featureOption instanceof SubEntryFeatureOption))
            return null;
        SubEntryFeatureOption subEntryFeatureOption = ( SubEntryFeatureOption ) featureOption;

        return new SubEntryFeatureOptionPanel(subEntryFeatureOption);
    }

    public SubEntryFeatureOption getSubEntryFeatureOption() {
        FeatureOption featureOption = getFeatureOption();
        if (!(featureOption instanceof SubEntryFeatureOption))
            return null;
        SubEntryFeatureOption subEntryFeatureOption = ( SubEntryFeatureOption ) featureOption;

        return subEntryFeatureOption;
    }

    public boolean hasValue() {
        List<? extends Entry> entries = medium.entry.getSubEntries(attributeName);

        if (entries.size() > 0)
            return true;
        
        entries = medium.entry.getSubEntries(attributeName + "_Link");

        if (entries.size() == 0)
            return false;
        
        return getMedium() != null;
    }
    
	public boolean validate(String condition) throws BadCondition {
        T medium = getSubEntryMedium();
        
        if (medium == null)
            return false;
        
        Parser parser = new Parser(condition);
        Filter cn = parser.parse();

        return cn.eval(medium);
	}

    public T getSubEntryMedium() {
        // Try to get direct medium
        T ret = getMediumDirect();
        
        if (ret != null)
            return ret;
        
        // try to get linked medium
        ret = getMediumLink();

        if (ret != null)
            return ret;

        SubEntryFeatureOption subEntryFeatureOption = getSubEntryFeatureOption();
        if (subEntryFeatureOption == null)
            return null;
        
        // if no medium is accessable and direct medium is prefered
        // then create newone
        if (!subEntryFeatureOption.isPreferLink())
            return newMediumDirect();
        
        // well there was none and we do not now if the user
        // want's to take a new one or an existing one
        return null;
    }
    
	public T getMediumDirect() {
		List<? extends Entry> entries = medium.entry.getSubEntries(attributeName);

        if (entries.size() == 0) 
            return null;
        
        Entry entry = entries.get(0);
        try {
            Object objects[] = new Object[2];
            objects[0] = entry;
            objects[1] = this.medium.getListing();
            T medium = type.getConstructor(new Class[]{Entry.class, Listing.class}).newInstance(objects);

            medium.setParentMedium(this.medium);

            return medium;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
	}
    public T getMediumLink() {
        List<? extends Entry> entries = medium.entry.getSubEntries(attributeName + "_Link");

        if (entries.size() == 0) 
            return null;
        
        Entry entry = entries.get(0);

        return getMediumLink(entry);
    }
    
    protected T getMediumLink(Entry entry) {
        String link = entry.getAttribute("UUID");        
        if (link == null)
            return null;

        UUID uuid = UUID.fromString(link);
        if (uuid == null)
            return null;

        String externStr = entry.getAttribute("Extern");
        boolean extern = externStr != null && externStr.equalsIgnoreCase("true");

        if (extern) {
            SubEntryFeatureOption subEntryFeatureOption = getSubEntryFeatureOption();
            if (subEntryFeatureOption == null)
                return null;
            Listing listing = subEntryFeatureOption.getListing(medium.getListing().getChangeLog());
            if (listing == null)
                return null;
            
            Medium ret = subEntryFeatureOption.getListing(medium.getListing().getChangeLog()).getMediumByUUID(uuid);
            if (ret != null && type.isAssignableFrom(ret.getClass()))
                return (T) ret;
        } else {
            Medium ret = medium.getListing().getMediumByUUID(uuid);
            if (ret != null && type.isAssignableFrom(ret.getClass()))
                return (T) ret;
        }
        
        return null;
    }
	
	public T newMediumDirect() {
		try {
            startTransaction(Options.getI18N(MainFrameBackend.class).getString("Create new [TYPE].").replaceAll("\\[TYPE\\]", Options.getI18N(type).getString(type.getSimpleName())), true, true);

            Entry entry = this.medium.entry.createSubEntry(attributeName);
			Object objects[] = new Object[2];
			objects[0] = entry;
			objects[1] = this.medium.getListing();
			T medium = type.getConstructor(new Class[]{Entry.class, Listing.class}).newInstance(objects);

            medium.setParentMedium(this.medium);

            stopTransaction();

            return medium;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
    
    public T newMediumCatalog() {
        T ret = null;

        SubEntryFeatureOption subEntryFeatureOption = getSubEntryFeatureOption();
        if (subEntryFeatureOption == null)
            return newMediumCatalogIntern();

        if (subEntryFeatureOption.isPreferExternCatalog()) 
            return newMediumCatalogExtern();
        else
            return newMediumCatalogIntern();
    }

    protected T newMediumCatalogExtern() {

        SubEntryFeatureOption subEntryFeatureOption = getSubEntryFeatureOption();
        if (subEntryFeatureOption == null)
            return null;
        
        Listing listing = subEntryFeatureOption.getListing(medium.getListing().getChangeLog());
        if (listing == null)
            return null;

        startTransaction(res.getString("Create new [TYPE] in catalog [CATALOG] and create a link.")
                .replaceAll("\\[TYPE\\]", Options.getI18N(type).getString(type.getSimpleName()))
                .replaceAll("\\[CATALOG\\]", "" + listing.catalog.getConnection())
                , true, true);

        // Create new entry "type"
        T ret = listing.create(type);
        
        // Remove previous propably broken link
        medium.entry.removeSubEntries(attributeName + "_Link");

        // Link the new entry to this feature
        Entry entry = medium.entry.createSubEntry(attributeName + "_Link");
        entry.setAttribute("UUID", ret.getFeature(Ident.class).getUUID().toString());
        entry.setAttribute("Extern", "true");
        
        stopTransaction();
        
        return ret;
    }
    
    protected T newMediumCatalogIntern() {
        startTransaction(res.getString("Create new [TYPE] and create a link.")
                .replaceAll("\\[TYPE\\]", Options.getI18N(type).getString(type.getSimpleName()))
                , true, true);

        // Create new entry "type"
        T ret = medium.getListing().create(type);

        // Remove previous propably broken link
        // this is the main difference between SubEntryList and SubEntry features.
        medium.entry.removeSubEntries(attributeName + "_Link");

        // Link the new entry to this feature
        Entry entry = medium.entry.createSubEntry(attributeName + "_Link");
        entry.setAttribute("UUID", ret.getFeature(Ident.class).getUUID().toString());

        stopTransaction();

        return ret;
    }
	
    public T linkExisting(Component parent) {
        Listing listing = null;
        SubEntryFeatureOption subEntryFeatureOption = getSubEntryFeatureOption();
        if (subEntryFeatureOption == null)
            return null;

        if (subEntryFeatureOption.isPreferExternCatalog())
            listing = subEntryFeatureOption.getListing(medium.getListing().getChangeLog());
        else
            listing = medium.getListing();
        if (listing == null)
            return null;

        T ret = (T) SelectMediaFrontend.showSelectMediaFrontend(parent, listing.catalog, new MediumFilter(type), null, subEntryFeatureOption.getShowSorting(), false);
        if (ret == null)
            return null;
        
        linkExisting(ret);
        
        return ret;
    }
        
    public void linkExisting(T subMedium) {
        SubEntryFeatureOption subEntryFeatureOption = getSubEntryFeatureOption();

        if (subMedium == null)
            return;
        
        startTransaction(res.getString("Create link to [TYPE].")
                .replaceAll("\\[TYPE\\]", Options.getI18N(type).getString(type.getSimpleName()))
                , true, true);

        // Remove previous propably broken links and override existing direct media
        // This is an important difference between this and the subEntryListFeature.linkExisting
        medium.entry.removeSubEntries(attributeName + "_Link");
        medium.entry.removeSubEntries(attributeName);

        Entry entry = medium.entry.createSubEntry(attributeName + "_Link");
        entry.setAttribute("UUID", subMedium.getFeature(Ident.class).getUUID().toString());
        if (subEntryFeatureOption.isPreferExternCatalog())
            entry.setAttribute("Extern", "true");

        stopTransaction();
    }

    public void delete() {
        medium.entry.removeSubEntries(attributeName);
        medium.entry.removeSubEntries(attributeName + "_Link");
/*        for (Entry entry : medium.entry.getSubEntries(attributeName))
            medium.entry.removeSubEntry(entry);
        for (Entry entry : medium.entry.getSubEntries(attributeName + "_Link"))
            medium.entry.removeSubEntry(entry);*/
	}
	
	public String getText() {
		String ret = attributeName + ": \r\n";
        T medium = getSubEntryMedium();
        
        if (medium == null)
            return null;

        return ret + medium.toString();
	}

	public String getShortText() {
		return getText();
	}
    
    public String getShortTextHTML() {
        return getShortText();
    }

    public String getTextHTML(int availableWidth) {
        return getText();
    }

    public void copyTo(Feature feature) {
        copyToUseMapping(feature, null);
    }
    public void copyToUseMapping(Feature feature, Map<Medium, Map<Listing, Medium>> mapping) {
        if (!(feature instanceof SubEntryFeature))
            return;

        SubEntryFeature<T> subEntryFeature = (SubEntryFeature<T>) feature;

        if (!subEntryFeature.type.equals(type))
            return;

        // Don't use getSubEntryMedium
        // because it could create one just 
        // to have one
        T subMedium = getMediumDirect();

        if (subMedium == null) 
            subMedium = getMediumLink();

        if (subMedium == null)
            return;

        subEntryFeature.createOrLinkEqual(subMedium, mapping);

	}
    
    protected void createOrLinkEqual(T subMedium, Map<Medium, Map<Listing, Medium>> mapping) {
        SubEntryFeatureOption subEntryFeatureOption = getSubEntryFeatureOption();

        // If the sub medium is direct then copy direct
        if (!subEntryFeatureOption.isPreferLink()) {
            if (mapping != null)
                subMedium.copyToUseMapping(getSubEntryMedium(), mapping);
            else
                subMedium.copyTo(getSubEntryMedium());
            return;
        }

        // If the medium shall be a link, then try to find
        // a already existing medium that has the wanted properties.
        Listing listing = null;
        if (subEntryFeatureOption.isPreferExternCatalog())
            listing = subEntryFeatureOption.getListing(getChangeLog());
        else
            listing = medium.getListing();

        if (listing != null) {
            List<T> set = new Vector<T>((Set<T>)listing.getMediaByType(type));
            for (T t : set) {
                if (t.equals(subMedium)) {
                    linkExisting(t);

                    return;
                }
            }
        }

        // Do we have a medium for sub medium "subMedium" for the listing "listing" in the mapping
        // if we have, we do not need to create a new one, we just take the one we already have
        if (mapping != null && mapping.containsKey(subMedium) && mapping.get(subMedium).containsKey(listing)) {
            linkExisting((T) mapping.get(subMedium).get(listing));
            return;
        }
        
        T newMedium = newMediumCatalog();
        // Create new entry in medium
        // and copy the information into this medium
        if (mapping != null) {
            // Since we've created a new medium
            // for the medium subMedium, we add it to the mapping
            if (!mapping.containsKey(subMedium))
                mapping.put(subMedium, new LinkedHashMap<Listing, Medium>());
            mapping.get(subMedium).put(listing, newMedium);
            
            subMedium.copyToUseMapping(newMedium, mapping);
        } else
            subMedium.copyTo(newMedium);
    }
    
    public void subEntryCreated(SubEntryEvent event) {
        fireFeatureChanged();
    }

    public void subEntryRemoved(SubEntryEvent event) {
        fireFeatureChanged();
    }
    
    public void subEntriesRemoved(SubEntryEvent event) {
        fireFeatureChanged();
    }
    
    public Class<? extends Medium> getType() {
        return type;
    }


}
