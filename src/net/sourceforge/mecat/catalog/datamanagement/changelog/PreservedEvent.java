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
 * Created on Nov 2, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.datamanagement.changelog;

import java.util.Locale;

import net.sourceforge.mecat.catalog.datamanagement.AttributeEvent;
import net.sourceforge.mecat.catalog.datamanagement.Catalog;
import net.sourceforge.mecat.catalog.datamanagement.CatalogEvent;
import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.datamanagement.EntryClearedEvent;
import net.sourceforge.mecat.catalog.datamanagement.EntryEvent;
import net.sourceforge.mecat.catalog.datamanagement.Identifier;
import net.sourceforge.mecat.catalog.datamanagement.SetAttributeEvent;
import net.sourceforge.mecat.catalog.datamanagement.SubEntryEvent;
import net.sourceforge.mecat.catalog.option.Options;

public class PreservedEvent {

    /**
     * This is for the CatalogEvent type EntryAdded.
     * and for the CatalogEvent type EntriesRemoved.
     */
    String entryName = null;
    
    /**
     * Name of the attribute in question
     */
    String attributeName = null;
    Locale attributeLanguage = null;
    String attributeOldValue = null;
    
    /**
     * Contains the new value for AttributeEvent.
     */
    String attributeNewValue = null;
    
    /**
     * Contains the attribute value for the SetAttributeEvent.
     */
    String attributeValue = null;
    
    /**
     * The identifier of the entry that has been
     * removed from the catalog.
     * 
     * This is for the CatalogEvent type EntryRemoved.
     */
    Identifier entry = null;
    
    /**
     * Sub entry name for Entry_SubEntry_Create and Entry_SubEntry_RemoveAll
     */
    String subEntryName = null;
    
    Identifier subEntryId = null;
    
    public enum PreservedEventType {
        Catalog_EntryAdded, 
        Catalog_EntryRemoved, 
        Catalog_EntriesRemoved, 
        Catalog_OptionAdded, 
        Catalog_OptionRemoved, 
        Catalog_OptionsRemoved, 
//        Catalog_EntryChanged, Not needed because it is handled with Entry_ cases
        Entry_Cleared,
        Entry_AttributeEvent,
        Entry_SetAttribute_Add, 
        Entry_SetAttribute_Remove, 
        Entry_SetAttribute_Clear,
        Entry_SubEntry_Create, 
        Entry_SubEntry_Remove, 
        Entry_SubEntry_RemoveAll
    }
    
    final PreservedEventType type;
    
    public PreservedEvent(PreservedEvent event) {
        this.entryName = event.entryName;
        this.attributeName = event.attributeName;
        this.attributeLanguage = event.attributeLanguage;
        this.attributeOldValue = event.attributeOldValue;
        this.attributeNewValue = event.attributeNewValue;
        this.entry = event.entry;
        this.type = event.type;
    }
    
    public PreservedEvent(CatalogEvent catalogEvent) {
       switch (catalogEvent.getType()) {
       case EntryAdded:
           type = PreservedEventType.Catalog_EntryAdded;
           entryName = catalogEvent.getName();
           break;
       case EntryRemoved:
           type = PreservedEventType.Catalog_EntryRemoved;
           entry = catalogEvent.getEntry().getIdentifier();
           break;
       case EntriesRemoved:
           type = PreservedEventType.Catalog_EntriesRemoved;
           entryName = catalogEvent.getName();
           break;
       case OptionAdded:
           type = PreservedEventType.Catalog_OptionAdded;
           entryName = catalogEvent.getName();
           break;
       case OptionRemoved:
           type = PreservedEventType.Catalog_OptionRemoved;
           entry = catalogEvent.getEntry().getIdentifier();
           break;
       case OptionsRemoved:
           type = PreservedEventType.Catalog_OptionsRemoved;
           entryName = catalogEvent.getName();
           break;
       default:
           System.err.println(Options.getI18N(PreservedEvent.class).getString("Log error. Catalog event type [TYPE] is not handled properly.")
                   .replaceAll("\\[TYPE\\]", "" + catalogEvent.getType()));
           type = null;
       }
       
       
    }
    
    public PreservedEvent(EntryEvent entryEvent) {
        if (entryEvent instanceof EntryClearedEvent) {
            EntryClearedEvent entryClearedEvent = (EntryClearedEvent) entryEvent;
            
            type = PreservedEventType.Entry_Cleared;
            entry = entryClearedEvent.getSource().getIdentifier();
            
            return;
        }
        
        if (entryEvent instanceof AttributeEvent) {
            AttributeEvent attributeEvent = (AttributeEvent) entryEvent;
            
            type = PreservedEventType.Entry_AttributeEvent;
            entry = attributeEvent.getSource().getIdentifier();
            attributeName = attributeEvent.getName();
            attributeLanguage = attributeEvent.getLanguage();
            attributeOldValue = attributeEvent.getOldValue();
            attributeNewValue = attributeEvent.getNewValue();
            
            return;
        }
        if (entryEvent instanceof SetAttributeEvent) {
            SetAttributeEvent setAttributeEvent = (SetAttributeEvent) entryEvent;
                
            switch (setAttributeEvent.getType()) {
            case Add:
                type = PreservedEventType.Entry_SetAttribute_Add;
                entry = setAttributeEvent.getSource().getIdentifier();
                attributeName = setAttributeEvent.getName();
                attributeLanguage = setAttributeEvent.getLanguage();
                attributeValue = setAttributeEvent.getValue();
                break;
            case Remove:
                type = PreservedEventType.Entry_SetAttribute_Remove;
                entry = setAttributeEvent.getSource().getIdentifier();
                attributeName = setAttributeEvent.getName();
                attributeLanguage = setAttributeEvent.getLanguage();
                attributeValue = setAttributeEvent.getValue();
                break;
            case Clear:
                type = PreservedEventType.Entry_SetAttribute_Clear;
                entry = setAttributeEvent.getSource().getIdentifier();
                attributeName = setAttributeEvent.getName();
                attributeLanguage = setAttributeEvent.getLanguage();
                break;
            default:
                System.err.println(Options.getI18N(PreservedEvent.class).getString("Log error. Set attribute event type [TYPE] is not handled properly.")
                        .replaceAll("\\[TYPE\\]", "" + setAttributeEvent.getType()));
                type = null;
                
            }
            
            return;
        }
        if (entryEvent instanceof SubEntryEvent) {
            SubEntryEvent subEntryEvent = (SubEntryEvent) entryEvent;
                
            switch (subEntryEvent.getType()) {
            case Create:
                type = PreservedEventType.Entry_SubEntry_Create;
                entry = subEntryEvent.getSource().getIdentifier();
                subEntryName = subEntryEvent.getName();
                break;
            case Remove:
                type = PreservedEventType.Entry_SubEntry_Remove;
                entry = subEntryEvent.getSource().getIdentifier();
                subEntryId = subEntryEvent.getEntry().getIdentifier();
                break;
            case RemoveAll:
                type = PreservedEventType.Entry_SubEntry_RemoveAll;
                entry = subEntryEvent.getSource().getIdentifier();
                subEntryName = subEntryEvent.getName();
                break;
            default:
                System.err.println(Options.getI18N(PreservedEvent.class).getString("Log error. Sub entry event type [TYPE] is not handled properly.")
                        .replaceAll("\\[TYPE\\]", "" + subEntryEvent.getType()));
                type = null;
                
            }

            return;
        }
        System.err.println(Options.getI18N(PreservedEvent.class).getString("Log error. Catalog event [EVENT] is not handled properly.")
                .replaceAll("\\[TYPE\\]", "" + entryEvent));
        type = null;
        
    }
    
    
    public String toString() {
        String ret = type.toString();
        switch (type) {
        case Catalog_EntryAdded:
            ret = Options.getI18N(PreservedEvent.class).getString("Catalog entry added [NAME].").replaceAll("\\[NAME\\]", entryName);
            break;
        case Catalog_EntryRemoved:
            ret = Options.getI18N(PreservedEvent.class).getString("Catalog entry removed [ENTRY].").replaceAll("\\[ENTRY\\]", "" + entry);
            break;
        case Catalog_EntriesRemoved:
            ret = Options.getI18N(PreservedEvent.class).getString("Catalog entries with the name [NAME] removed.").replaceAll("\\[NAME\\]", entryName);
            break;
        case Catalog_OptionAdded:
            ret = Options.getI18N(PreservedEvent.class).getString("Catalog option added [NAME].").replaceAll("\\[NAME\\]", entryName);
            break;
        case Catalog_OptionRemoved:
            ret = Options.getI18N(PreservedEvent.class).getString("Catalog option removed [ENTRY].").replaceAll("\\[ENTRY\\]",  "" + entry);
            break;
        case Catalog_OptionsRemoved:
            ret = Options.getI18N(PreservedEvent.class).getString("Catalog options with the name [NAME] removed.").replaceAll("\\[NAME\\]", entryName);
            break;
        case Entry_Cleared:
            ret = Options.getI18N(PreservedEvent.class).getString(
                    "The entry [ENTRY] has been cleared.")
                    .replaceAll("\\[ENTRY\\]", "" + entry);
            break;
        case Entry_AttributeEvent:
            if (attributeLanguage != null)
                ret = Options.getI18N(PreservedEvent.class).getString(
                        "Attribute [ATTRIBUTE] for the entry [ENTRY] has changed for the language [LANGUAGE] from [OLD] to [NEW].")
                        .replaceAll("\\[ATTRIBUTE\\]", attributeName)
                        .replaceAll("\\[ENTRY\\]", "" + entry)
                        .replaceAll("\\[LANGUAGE\\]", "" + attributeLanguage)
                        .replaceAll("\\[OLD\\]", "" + attributeOldValue)
                        .replaceAll("\\[NEW\\]", "" + attributeNewValue);
            else
                ret = Options.getI18N(PreservedEvent.class).getString(
                        "Attribute [ATTRIBUTE] for the entry [ENTRY] has changed from [OLD] to [NEW].")
                        .replaceAll("\\[ATTRIBUTE\\]", attributeName)
                        .replaceAll("\\[ENTRY\\]", "" + entry)
                        .replaceAll("\\[OLD\\]", "" + attributeOldValue)
                        .replaceAll("\\[NEW\\]", "" + attributeNewValue);
            break;
        case Entry_SetAttribute_Add:
            ret = Options.getI18N(PreservedEvent.class).getString(
                    "The value [VALUE] has been added to the attribute [ATTRIBUTE] for the entry [ENTRY].")
                    .replaceAll("\\[ATTRIBUTE\\]", attributeName)
                    .replaceAll("\\[ENTRY\\]", "" + entry)
                    .replaceAll("\\[VALUE\\]", "" + attributeValue);
            break;
        case Entry_SetAttribute_Remove:
            ret = Options.getI18N(PreservedEvent.class).getString(
                    "The value [VALUE] has been removed from the attribute [ATTRIBUTE] for the entry [ENTRY].")
                    .replaceAll("\\[ATTRIBUTE\\]", attributeName)
                    .replaceAll("\\[ENTRY\\]", "" + entry)
                    .replaceAll("\\[VALUE\\]", "" + attributeValue);
            break;
        case Entry_SetAttribute_Clear:
            ret = Options.getI18N(PreservedEvent.class).getString(
                    "Set Attribute [ATTRIBUTE] for the entry [ENTRY] has been cleared.")
                    .replaceAll("\\[ATTRIBUTE\\]", attributeName)
                    .replaceAll("\\[ENTRY\\]", "" + entry);
            break;
        case Entry_SubEntry_Create:
            ret = Options.getI18N(PreservedEvent.class).getString("Sub entry for [ENTRY] with the name [SUBENTRY] created.")
                    .replaceAll("\\[ENTRY\\]", "" + entry)
                    .replaceAll("\\[SUBENTRY\\]", subEntryName);
            break;
        case Entry_SubEntry_Remove:
            ret = Options.getI18N(PreservedEvent.class).getString("Sub entry [SUBID] removed from [ENTRY].")
                    .replaceAll("\\[SUBID\\]", "" + subEntryId)
                    .replaceAll("\\[ENTRY\\]", "" + entry);
            break;
        case Entry_SubEntry_RemoveAll:
            ret = Options.getI18N(PreservedEvent.class).getString("All sub entries with the name [SUBENTRY] removed from [ENTRY].")
                    .replaceAll("\\[ENTRY\\]", "" + entry)
                    .replaceAll("\\[SUBENTRY\\]", subEntryName);
            break;
        }
        return ret;
    }

    public void execute(Catalog catalog) {
        Entry entry = null;
        if (this.entry != null)
            entry = catalog.get(this.entry);

        switch (type) {
        case Catalog_EntryAdded:
            catalog.createEntry(entryName);
            break;
        case Catalog_EntryRemoved:
            catalog.removeEntry(entry);
            break;
        case Catalog_EntriesRemoved:
// Does not exists yet            
//            catalog.removeEntry(entryName);
            break;
        case Catalog_OptionAdded:
            catalog.createOption(entryName);
            break;
        case Catalog_OptionRemoved:
            catalog.removeOption(entry);
            break;
        case Catalog_OptionsRemoved:
            catalog.removeOption(entryName);
            break;
        case Entry_Cleared:
            entry.clear();
            break;
        case Entry_AttributeEvent:
            entry.setAttribute(attributeName, attributeNewValue, attributeLanguage);
            break;
        case Entry_SetAttribute_Add:
            entry.addSetAttribute(attributeName, attributeValue, attributeLanguage);
            break;
        case Entry_SetAttribute_Remove:
            entry.removeSetAttribute(attributeName, attributeValue, attributeLanguage);
            break;
        case Entry_SetAttribute_Clear:
            entry.clearSetAttribute(attributeName, attributeLanguage);
            break;
        case Entry_SubEntry_Create:
            entry.createSubEntry(subEntryName);
            break;
        case Entry_SubEntry_Remove:
            entry.removeSubEntry(catalog.getEntry(subEntryId));
            break;
        case Entry_SubEntry_RemoveAll:
            entry.removeSubEntries(subEntryName);
            break;
        }
    }
    
    
}
