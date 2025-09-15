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
 * Created on Sep 14, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.medium.features.option.merger;

public class MergeUserInterfaceEvent {

    public static enum MergeUserInterfaceEventType {
        StatusChanged
    };
    
    final MergeUserInterface source;
    final MergeUserInterfaceEventType type;
    final MergeUserInterfaceStatus oldStatus;
    final MergeUserInterfaceStatus newStatus;
    
    
    
    public MergeUserInterfaceEvent(final MergeUserInterface source, final MergeUserInterfaceStatus oldStatus, final MergeUserInterfaceStatus newStatus) {
        this.source = source;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.type = MergeUserInterfaceEventType.StatusChanged;
    }

    public MergeUserInterface getSource() {
        return source;
    }
    public MergeUserInterfaceEventType getType() {
        return type;
    }

    public MergeUserInterfaceStatus getNewStatus() {
        return newStatus;
    }

    public MergeUserInterfaceStatus getOldStatus() {
        return oldStatus;
    }
    
    
    
}
