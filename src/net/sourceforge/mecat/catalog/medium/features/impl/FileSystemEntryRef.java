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
 * Created on Jun 7, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.medium.features.impl;

import java.util.UUID;

import net.sourceforge.mecat.catalog.filesystem.FileSystemEntry;
import net.sourceforge.mecat.catalog.gui.features.FeaturePanel;
import net.sourceforge.mecat.catalog.gui.features.FileSystemEntryRefPanel;
import net.sourceforge.mecat.catalog.gui.features.desktop.FeatureDesktop;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.BadCondition;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.medium.features.AbstractFeature;
import net.sourceforge.mecat.catalog.medium.hidden.Rom;

public class FileSystemEntryRef extends AbstractFeature {


    /**
     * Is the rom information cached ?
     */
    boolean cachedRom = false;
    /**
     * What rom is connected to this.
     * This information contains a valid information
     * if cachedRom == true
     */
    Rom rom = null;

    
    boolean cachedFileSystemEntry = false;
    FileSystemEntry fileSystemEntry = null;
    
    
    public FileSystemEntryRef(Medium medium) {
        super(medium);
//        (new Exception(this.hashCode() + ".FileSystemEntryRef(" + medium.hashCode() + ")")).printStackTrace();
    }

    @Override
    public FeaturePanel getPanel(FeatureDesktop desktop, boolean border) {
        return new FileSystemEntryRefPanel(this, desktop, border);
    }

/*    @Override
    public boolean hasOptions() {
        return false;
    }*/

    public boolean validate(String condition) throws BadCondition {
        return false;
    }

    public String getText() {
        return getShortText();
    }

    public String getShortText() {
        String ret;
        
        Rom rom = getRefRom();
        
        if (rom == null)
            return "";
        
        ret = rom.toString();
        
        FileSystemEntry entry = getFileSystemEntry();
        
        if (entry == null)
            return ret;

        ret += " - " + entry;
        
        return null;
    }

    public String getTextHTML(int availableWidth) {
        return getText();
    }

    public String getShortTextHTML() {
        return getShortTextHTML();
    }

    @Override
    public void copyTo(Feature feature) {
        if (!(feature instanceof FileSystemEntryRef))
            return;
        FileSystemEntryRef fileSystemEntryRef = (FileSystemEntryRef) feature;
        fileSystemEntryRef.setRefRomUUID(getRefRomUUID());
        fileSystemEntryRef.setRefPosition(getRefPosition());
    }

    @Override
    public boolean hasValue() {
        return getFileSystemEntry() != null;
        
    }

    synchronized public FileSystemEntry getFileSystemEntry() {
        if (cachedFileSystemEntry)
            return fileSystemEntry;
        
        Rom rom = getRefRom();

        // If there is no rom then there is no FileSystemEntry
        if (rom == null) {
            this.fileSystemEntry = null;
            this.cachedFileSystemEntry = true;
            return null;
        }

        String refPosition = getRefPosition();
        
        if (refPosition == null || refPosition.equals("") || !rom.getRomFileList().hasResult()) {
            this.fileSystemEntry = null;
            this.cachedFileSystemEntry = true;
            return null;
        }
        
        
        // TODO find solution for problem
        // - Need to optain a reference for the position
        // - RomFileList != VirtualRomFileList
        this.fileSystemEntry = rom.getRomFileList().getResult().getFileSystemEntryForRef(getRefPosition());
        this.cachedFileSystemEntry = true;
        
        return fileSystemEntry;
    }
    
    synchronized public void setFileSystemEntry(FileSystemEntry fileSystemEntry) {
        this.fileSystemEntry = fileSystemEntry;
        if (fileSystemEntry == null)
            setRefPosition(null);
        else
            setRefPosition(getRefRom().getRomFileList().getResult().getRefForFileSystemEntry(fileSystemEntry));
        this.cachedFileSystemEntry = true;
    }

    
    protected String getRefRomUUID() {
        return medium.entry.getAttribute("RefMediumUUID");
    }
    
    protected void setRefRomUUID(String refMediumUUID) {
        medium.entry.setAttribute("RefMediumUUID", refMediumUUID);
    }
    
    protected String getRefPosition() {
        return medium.entry.getAttribute("RefPosition");
    }
    
    protected void setRefPosition(String refPosition) {
        medium.entry.setAttribute("RefPosition", refPosition);
    }
    
    synchronized public void setRefRom(Rom rom) {
        if (rom == null)
            setRefRomUUID(null);
        else 
            setRefRomUUID(rom.getFeature(Ident.class).getUUID().toString());

        // Cache the information
        this.rom = rom;
        this.cachedRom = true;

        // Remove existing fileSystemEntry if exist
        setRefPosition(null);
        this.fileSystemEntry = null;
        this.cachedFileSystemEntry = true;
    }
    
    synchronized public Rom getRefRom() {
        if (cachedRom)
            return this.rom;
        
        String mediumUUID = getRefRomUUID();
        
        if (mediumUUID == null || mediumUUID.equals("")) {
            cachedRom = true;
            this.rom = null;
            return this.rom;
        }

        Medium rom = medium.getListing().getMediumByUUID(UUID.fromString(mediumUUID));

        if (rom == null || !(rom instanceof Rom)){
            cachedRom = true;
            this.rom = null;
            return this.rom;
        }
        
        this.rom = (Rom) rom;
        return this.rom;
    }


}
