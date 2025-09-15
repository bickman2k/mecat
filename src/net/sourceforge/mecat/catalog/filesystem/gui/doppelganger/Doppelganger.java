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
 * Created on Jun 10, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filesystem.gui.doppelganger;

import java.awt.Color;

import net.sourceforge.mecat.catalog.filesystem.FileEntry;

import static java.awt.Color.*;

public class Doppelganger {
    
    public enum DoppelgangerType {
        Sure(green),
        Guessed(red);

        public final Color color;
        public final Color selectedColor;
        
        private DoppelgangerType(final Color color) {
            this.color = color;
            this.selectedColor = color.darker();
        }
        private DoppelgangerType(final Color color, final Color selectedColor) {
            this.color = color;
            this.selectedColor = selectedColor;
        }
    }
    
    final FileEntry fileEntry;
    final DoppelgangerType type;

    public Doppelganger(FileEntry fileEntry, DoppelgangerType type) {
        this.fileEntry = fileEntry;
        this.type = type;
    }

    public FileEntry getFileEntry() {
        return fileEntry;
    }

    public DoppelgangerType getType() {
        return type;
    }
    
}
