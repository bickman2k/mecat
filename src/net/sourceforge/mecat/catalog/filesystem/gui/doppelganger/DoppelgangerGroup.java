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
import java.util.Vector;

import static java.awt.Color.*;

public class DoppelgangerGroup extends Vector<Doppelganger> {

    public enum DoppelgangerGroupType {
        SureOnly(green),   // All elements in this group are for sure the same
        Mixed(yellow),     // Mixed group of elements that are a sure the same and of some that are just guest to be the same
        GuessedOnly(red);  // All elements of the group are bound through guessing

    
        public final Color color;
        public final Color selectedColor;
        
        private DoppelgangerGroupType(final Color color) {
            this.color = color;
            this.selectedColor = color.darker();
        }
        private DoppelgangerGroupType(final Color color, final Color selectedColor) {
            this.color = color;
            this.selectedColor = selectedColor;
        }
    }

    final DoppelgangerGroupType type;

    public DoppelgangerGroup(DoppelgangerGroupType type) {
        this.type = type;
    }

    public DoppelgangerGroupType getType() {
        return type;
    }
    
    
    
}
