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

import java.util.Vector;
import net.sourceforge.mecat.catalog.medium.features.option.FeatureOption;

public abstract class AbstractMergeUserInterface<T extends FeatureOption> implements MergeUserInterface<T> {

    Vector<MergeUserInterfaceListener> mergeUserInterfaceListeners = new Vector<MergeUserInterfaceListener>();
    
    public void addMergeUserInterfaceListener(MergeUserInterfaceListener mergeUserInterfaceListener) {
        mergeUserInterfaceListeners.add(mergeUserInterfaceListener);
    }
    
    public void removeMergeUserInterfaceListener(MergeUserInterfaceListener mergeUserInterfaceListener) {
        mergeUserInterfaceListeners.add(mergeUserInterfaceListener);
    }

    // Merge type of the user interface, this is fixed
    MergeType type;
    
    // Status of the user interface, this changes depending on how the user interacts with the gui does
    MergeUserInterfaceStatus status;

    // This gives a standart repository place for the return option
    T mergeResult;
    
    
    
    public T getMergeResult() {
        return mergeResult;
    }

    public void setMergeResult(T mergeResult) {
        this.mergeResult = mergeResult;
    }

    public MergeType getType() {
        return type;
    }

    public void setType(MergeType type) {
        this.type = type;
    }

    public void setStatus(MergeUserInterfaceStatus newStatus) {
        MergeUserInterfaceStatus oldStatus = status;
        if (oldStatus == newStatus)
            return;
        status = newStatus;
        fireStatusChanged(oldStatus, newStatus);
    }
    
    public MergeUserInterfaceStatus getStatus() {
        return status;
    }
    
    
    public void fireStatusChanged(MergeUserInterfaceStatus oldStatus, MergeUserInterfaceStatus newStatus) {
        for (MergeUserInterfaceListener mergeUserInterfaceListener : mergeUserInterfaceListeners)
            mergeUserInterfaceListener.statusChanged(new MergeUserInterfaceEvent(this, oldStatus, newStatus));
    }
    
}
