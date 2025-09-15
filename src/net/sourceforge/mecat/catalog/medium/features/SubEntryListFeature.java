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

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
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
import net.sourceforge.mecat.catalog.filter.Parser;
import net.sourceforge.mecat.catalog.gui.MainFrameBackend;
import net.sourceforge.mecat.catalog.gui.features.FeaturePanel;
import net.sourceforge.mecat.catalog.gui.features.SubEntryListFeaturePanel;
import net.sourceforge.mecat.catalog.gui.features.desktop.FeatureDesktop;
import net.sourceforge.mecat.catalog.gui.options.features.SubEntryListFeatureOptionPanel;
import net.sourceforge.mecat.catalog.medium.Listing;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.PointedMedium;
import net.sourceforge.mecat.catalog.medium.features.impl.Ident;
import net.sourceforge.mecat.catalog.medium.features.option.FeatureOption;
import net.sourceforge.mecat.catalog.medium.features.option.SubEntryListFeatureOption;
import net.sourceforge.mecat.catalog.option.Options;

public class SubEntryListFeature<T extends Medium> extends AbstractSubEntryFeature implements SubEntryListener {

	public String attributeName;
	Class<T> type;

	
	public SubEntryListFeature(Medium medium, String attributeName, Class<T> entryType) {
		super(medium);
		this.attributeName = attributeName;
		type = entryType;
        medium.entry.addEntryListenerForSubEntry(attributeName, this);
        medium.entry.addEntryListenerForSubEntry(attributeName + "_Link", this);
	}

    @Override
	public FeaturePanel getPanel(FeatureDesktop desktop, boolean border) {
		return new SubEntryListFeaturePanel(this, desktop, border);
	}

	public boolean hasOptions() {
		return true;
	}

    @Override
    public JPanel getFeatureOptionPanel(FeatureOption featureOption) {
        if (!(featureOption instanceof SubEntryListFeatureOption))
            return null;
        SubEntryListFeatureOption subEntryListFeatureOption = ( SubEntryListFeatureOption ) featureOption;

        return new SubEntryListFeatureOptionPanel(subEntryListFeatureOption);
    }

    public SubEntryListFeatureOption getSubEntryListFeatureOption() {
        FeatureOption featureOption = getFeatureOption();
        if (!(featureOption instanceof SubEntryListFeatureOption))
            return null;
        SubEntryListFeatureOption subEntryListFeatureOption = ( SubEntryListFeatureOption ) featureOption;

        return subEntryListFeatureOption;
    }

    public boolean hasValue() {
        List<? extends Entry> entries = medium.entry.getSubEntries(attributeName);

        if (entries.size() > 0)
            return true;
        
        entries = medium.entry.getSubEntries(attributeName + "_Link");

        if (entries.size() == 0)
            return false;
        
        return getMedia().size() > 0;
    }
    
	public boolean validate(String condition) throws BadCondition {
        List<PointedMedium<T>> media = getMedia();
        if (media.isEmpty())
            return false;

        Parser parser = new Parser(condition);
        Filter cn = parser.parse();
        
        for (PointedMedium p : media) 
            if (cn.eval((T) p.getMedium()))
                return true;

        return false;
	}

    public Vector<T> getMediaUnpointed() {
        Vector<T> ret = new Vector<T>();
        for (PointedMedium<T> p : getMedia())
            ret.add(p.getMedium());
        return ret;
    }
    
	public Vector<PointedMedium<T>> getMedia() {
		List<? extends Entry> entriesDirect = medium.entry.getSubEntries(attributeName);
        List<? extends Entry> entriesLink = medium.entry.getSubEntries(attributeName);
		Vector<PointedMedium<T>> media = new Vector<PointedMedium<T>>(entriesDirect.size() + entriesLink.size());
		
		for (Entry entry : entriesDirect)
			try {
				Object objects[] = new Object[2];
				objects[0] = entry;
				objects[1] = this.medium.getListing();
				T medium = type.getConstructor(new Class[]{Entry.class, Listing.class}).newInstance(objects);

                medium.setParentMedium(this.medium);
                 
				media.add(new PointedMedium<T>(medium, entry));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

		for (Entry entry : entriesLink) {
            T medium = getMediumLink(entry);
            if (medium != null)
                media.add(new PointedMedium<T>(medium, entry));
        }
            
		return media;
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
            SubEntryListFeatureOption subEntryListFeatureOption = getSubEntryListFeatureOption();
            if (subEntryListFeatureOption == null)
                return null;
            Listing listing = subEntryListFeatureOption.getListing(medium.getListing().getChangeLog());
            if (listing == null)
                return null;
            
            Medium ret = subEntryListFeatureOption.getListing(medium.getListing().getChangeLog()).getMediumByUUID(uuid);
            if (ret != null && type.isAssignableFrom(ret.getClass()))
                return (T) ret;
        } else {
            Medium ret = medium.getListing().getMediumByUUID(uuid);
            if (ret != null && type.isAssignableFrom(ret.getClass()))
                return (T) ret;
        }
        
        return null;
    }

    public PointedMedium<T> newMedium() {
        SubEntryListFeatureOption subEntryListFeatureOption = getSubEntryListFeatureOption();

        if (subEntryListFeatureOption == null || !subEntryListFeatureOption.isPreferLink())
            return newMediumDirect();

        return newMediumCatalog();
    }
    
    public PointedMedium<T> newMediumDirect() {
		try {
            startTransaction(Options.getI18N(MainFrameBackend.class).getString("Create new [TYPE].").replaceAll("\\[TYPE\\]", Options.getI18N(type).getString(type.getSimpleName())), true, true);
            
			Entry entry = this.medium.entry.createSubEntry(attributeName);
			Object objects[] = new Object[2];
			objects[0] = entry;
			objects[1] = this.medium.getListing();
			T medium = type.getConstructor(new Class[]{Entry.class, Listing.class}).newInstance(objects);

            medium.setParentMedium(this.medium);
            
            stopTransaction();

            return new PointedMedium<T>(medium, entry);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        stopTransaction();
        
        return null;
	}
	
    public PointedMedium<T> newMediumCatalog() {
        T ret = null;

        SubEntryListFeatureOption subEntryListFeatureOption = getSubEntryListFeatureOption();
        if (subEntryListFeatureOption == null)
            return newMediumCatalogIntern();

        if (subEntryListFeatureOption.isPreferExternCatalog()) 
            return newMediumCatalogExtern();
        else
            return newMediumCatalogIntern();
    }

    protected PointedMedium<T> newMediumCatalogExtern() {

        SubEntryListFeatureOption subEntryListFeatureOption = getSubEntryListFeatureOption();
        if (subEntryListFeatureOption == null)
            return null;
        
        Listing listing = subEntryListFeatureOption.getListing(medium.getListing().getChangeLog());
        if (listing == null)
            return null;

        startTransaction(res.getString("Create new [TYPE] in catalog [CATALOG] and create a link.")
                .replaceAll("\\[TYPE\\]", Options.getI18N(type).getString(type.getSimpleName()))
                .replaceAll("\\[CATALOG\\]", "" + listing.catalog.getConnection())
                , true, true);

        // Create new entry "type"
        T ret = listing.create(type);
        
        // Link the new entry to this feature
        Entry entry = medium.entry.createSubEntry(attributeName + "_Link");
        entry.setAttribute("UUID", ret.getFeature(Ident.class).getUUID().toString());
        entry.setAttribute("Extern", "true");
        
        stopTransaction();
        
        return new PointedMedium<T>(ret, entry);
    }
    
    protected PointedMedium<T> newMediumCatalogIntern() {
        startTransaction(res.getString("Create new [TYPE] and create a link.")
                .replaceAll("\\[TYPE\\]", Options.getI18N(type).getString(type.getSimpleName()))
                , true, true);

        // Create new entry "type"
        T ret = medium.getListing().create(type);

        // Link the new entry to this feature
        Entry entry = medium.entry.createSubEntry(attributeName + "_Link");
        entry.setAttribute("UUID", ret.getFeature(Ident.class).getUUID().toString());

        stopTransaction();

        return new PointedMedium<T>(ret, entry);
    }

    public void remove(PointedMedium medium) {
		remove(medium.getEntry());
	}
	
	private void remove(Entry entry) {
		medium.entry.removeSubEntry(entry);
	}
	
	public String getText() {
		String ret = attributeName + ": \r\n";
		List<? extends Entry> entries = medium.entry.getSubEntries(attributeName);

		for (Entry entry : entries)
		try {
			Object objects[] = new Object[2];
			objects[0] = entry;
			objects[1] = this.medium.getListing();
			Medium medium = (Medium) (type.getConstructor(new Class[]{Entry.class, Listing.class}).newInstance(objects));

            medium.setParentMedium(this.medium);

			ret += medium.toString();
		} catch (Exception e) {
			return ret;
		}

		return ret;
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

    public void linkExisting(T subMedium) {
        SubEntryListFeatureOption subEntryListFeatureOption = getSubEntryListFeatureOption();

        if (subMedium == null)
            return;
        
        startTransaction(res.getString("Create link to [TYPE].")
                .replaceAll("\\[TYPE\\]", Options.getI18N(type).getString(type.getSimpleName()))
                , true, true);

        Entry entry = medium.entry.createSubEntry(attributeName + "_Link");
        entry.setAttribute("UUID", subMedium.getFeature(Ident.class).getUUID().toString());
        if (subEntryListFeatureOption.isPreferExternCatalog())
            entry.setAttribute("Extern", "true");

        stopTransaction();
    }
    
    public void copyTo(Feature feature) {
        copyToUseMapping(feature, null);
    }
    public void copyToUseMapping(Feature feature, Map<Medium, Map<Listing, Medium>> mapping) {
        if (!(feature instanceof SubEntryListFeature))
            return;
        SubEntryListFeature subEntryListFeature = (SubEntryListFeature) feature;

        if (!subEntryListFeature.type.equals(type))
            return;

        for (PointedMedium<T> p : getMedia()) 
            subEntryListFeature.createOrLinkEqual(p.getMedium(), mapping);
    }

    protected void createOrLinkEqual(T subMedium, Map<Medium, Map<Listing, Medium>> mapping) {
        SubEntryListFeatureOption subEntryListFeatureOption = getSubEntryListFeatureOption();

        // If the sub medium is direct then copy direct
        if (!subEntryListFeatureOption.isPreferLink()) {
            if (mapping != null) 
                subMedium.copyToUseMapping(newMediumDirect().getMedium(), mapping);
            else
                subMedium.copyTo(newMediumDirect().getMedium());
            return;
        }

        // If the medium shall be a link, then try to find
        // a already existing medium that has the wanted properties.
        Listing listing = null;
        if (subEntryListFeatureOption.isPreferExternCatalog())
            listing = subEntryListFeatureOption.getListing(getChangeLog());
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
        
        T newMedium = newMediumCatalog().getMedium();
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
    /*
    public void copyToUseMapping(Feature feature, Map<Medium, Map<Listing, Medium>> mapping) {
        if (!(feature instanceof SubEntryListFeature))
            return;
        SubEntryListFeature subEntryListFeature = (SubEntryListFeature) feature;

        if (!subEntryListFeature.type.equals(type))
            return;
        
		List<? extends Entry> entriesDirect = medium.entry.getSubEntries(attributeName);
        List<? extends Entry> entriesLink = medium.entry.getSubEntries(attributeName);

		for (Entry entry : entriesDirect)
		try {
			Object objects[] = new Object[2];
			objects[0] = entry;
			objects[1] = feature.getMedium().getListing();
			Medium medium = (Medium) (type.getConstructor(new Class[]{Entry.class, Listing.class}).newInstance(objects));

            PointedMedium<T> newMedium = subEntryListFeature.newMedium();
            medium.copyTo(newMedium.getMedium());
		} catch (Exception e) {
			e.printStackTrace();
		}

        for (Entry entry : entriesLink) {
            T medium = getMediumLink(entry);

            PointedMedium<T> newMedium = subEntryListFeature.newMedium();
            medium.copyTo(newMedium.getMedium());
        }
	}
    */
    
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

    @Override
    public Iterator<T> getSubMedia() {
        return new Iterator<T>(){
            Iterator<PointedMedium<T>> i = getMedia().iterator();

            public boolean hasNext() {
                return i.hasNext();
            }

            public T next() {
                PointedMedium p = i.next();
                if (p == null)
                    return null;
                return (T) p.getMedium();
            }

            public void remove() {
                // Does not exist
            }
        };
    }

}
