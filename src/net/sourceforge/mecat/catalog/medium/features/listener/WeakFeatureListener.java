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
 * Created on Sep 18, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.medium.features.listener;

import java.lang.ref.WeakReference;

import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.option.Options;

/**
 * 
 * Created on Sep 18, 2006
 *
 * @author Stephan Richard Palm
 *
 * An intermediate listener that uses a weak reference.
 *
 * Links the FeaturePanel to its feature.
 * This way every time the feature changes, the gui gets updated.
 *
 * Listener with weak Link, 
 * this way the listener sees if he is not longer needed and remove itself
 *
 */
public class WeakFeatureListener implements FeatureListener {
    // Weak link to the panel
    final WeakReference<FeatureListener> featurePanel;
    
    public WeakFeatureListener(final FeatureListener featurePanel) {
        this.featurePanel = new WeakReference<FeatureListener>(featurePanel);
    }

    public void featureValueChanged(Feature source) {
        if (Options.DEBUG)
            System.out.println("WeakFeatureListener " + this.hashCode() + " connnects Feature " + source.hashCode() + " to FeaturePanel " + ((featurePanel.get() == null)?"null":featurePanel.get().hashCode()));
        
        FeatureListener panel = featurePanel.get();
        
        if (panel == null) {
            source.removeFeatureListener(this);
            if (Options.DEBUG)
                System.out.println("WeakFeatureListener " + this.hashCode() + " removes itself.");
            return;
        }
        
        panel.featureValueChanged(source);
    }
}
