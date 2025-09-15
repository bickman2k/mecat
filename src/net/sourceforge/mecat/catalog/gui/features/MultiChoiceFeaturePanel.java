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
 * Created on Jan 9, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.gui.features;

import java.awt.BorderLayout;

import net.sourceforge.mecat.catalog.gui.features.desktop.FeatureDesktop;
import net.sourceforge.mecat.catalog.i18n.fromCatalog.LayeredResourceBundle;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.medium.features.MultiChoiceFeature;

import static net.sourceforge.mecat.catalog.gui.features.MultiChoiceFeaturePanelType.*;

public class MultiChoiceFeaturePanel extends FeaturePanel<MultiChoiceFeature>  {

    MultiChoiceFeaturePanelType type = CheckBoxes;
    FeaturePanel<MultiChoiceFeature> panel = null;
    final LayeredResourceBundle extraResources;
    
    public MultiChoiceFeaturePanel(MultiChoiceFeature feature, FeatureDesktop desktop) {
        this(feature, desktop, null);
    }
    public MultiChoiceFeaturePanel(MultiChoiceFeature feature, FeatureDesktop desktop, final LayeredResourceBundle extraResources) {
        this(feature, desktop, extraResources, CheckBoxes);
    }
    public MultiChoiceFeaturePanel(MultiChoiceFeature feature, FeatureDesktop desktop, final LayeredResourceBundle extraResources, MultiChoiceFeaturePanelType type) {
        this(feature, desktop, true, extraResources, type);
    }
    
    public MultiChoiceFeaturePanel(MultiChoiceFeature feature, FeatureDesktop desktop, boolean border) {
        this(feature, desktop, border, CheckBoxes);
    }
    public MultiChoiceFeaturePanel(MultiChoiceFeature feature, FeatureDesktop desktop, boolean border, MultiChoiceFeaturePanelType type) {
        this(feature, desktop, border, null, type);
    }
    public MultiChoiceFeaturePanel(final MultiChoiceFeature feature, FeatureDesktop desktop, boolean border, final LayeredResourceBundle extraResources) {
        this(feature, desktop, border, extraResources, CheckBoxes);
    }
    public MultiChoiceFeaturePanel(final MultiChoiceFeature feature, FeatureDesktop desktop, boolean border, final LayeredResourceBundle extraResources, MultiChoiceFeaturePanelType type) {
        super(feature, desktop, border, feature.attributeName, extraResources);
        this.extraResources = extraResources;

        this.type = type;
        setLayout(new BorderLayout());

        buildVisual();
        
    }
    
    public void buildVisual() {
        if (panel != null)
            this.remove(panel);
        switch (type) {
        case CheckBoxes : 
            panel = new MultiChoiceFeatureCheckBoxPanel(feature, desktop, false, extraResources);
            break;
        case SingleList : 
            panel = new MultiChoiceFeatureSingleListPanel(feature, desktop, false, extraResources);
            break;
        default : 
            panel = new MultiChoiceFeatureCheckBoxPanel(feature, desktop, false, extraResources);
        }
        add(panel);
    }

    public void requestFocus() {
        panel.requestFocus();
    }
    
    public boolean hasFocus() {
        return panel.hasFocus();
    }
    public void featureValueChanged(Feature source) {
        // Is done in the panel instance
    }
}
