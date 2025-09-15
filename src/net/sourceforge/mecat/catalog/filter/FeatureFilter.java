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
 * Created on Sep 1, 2005
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filter;

import javax.swing.JComponent;
import javax.swing.JLabel;

import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.BadCondition;
import net.sourceforge.mecat.catalog.medium.features.AbstractFeature;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.option.Options;

public class FeatureFilter extends AbstractFilter implements Filter {
    final String value;
    final Class<? extends Feature> feature;

    
    public FeatureFilter(final String name, final String value) throws BadCondition {
        this.value = value;
        try {
            Class c = Class.forName(AbstractFeature.getRealClassName(name));
            if (Feature.class.isAssignableFrom(c))
                feature = (Class< ? extends Feature>)c;
            else
                throw new BadCondition(Options.getI18N(Feature.class).getString("Could not find feature").replaceAll("\\[FEATURE\\]", name));
        } catch (java.lang.ClassNotFoundException e) {
            throw new BadCondition(Options.getI18N(Feature.class).getString("Could not find feature").replaceAll("\\[FEATURE\\]", name));
        }
    }

    public FeatureFilter(final Class<? extends Feature> feature, final String value) throws BadCondition {
        this.value = value;
        this.feature = feature;
    }

    public String getCondition() {
        return "( " + AbstractFeature.getEasyClassName(feature.getName()) + "( " + Parser.escape(value) + " )" + " )";
    }

    public boolean eval(Medium medium) throws BadCondition {
        Feature feat = medium.getFeature(feature);
        if (feat == null)
            return false;
        return feat.validate(value);
    }
    
    public String toString() {
        return Options.getI18N(feature).getString(feature.getSimpleName()) + " = " + value;
//        return Feature.getEasyClassName(feature.getName()) + " = " + value;
    }


    public JComponent visualisation() {
        JLabel label = new JLabel("<html>" + Options.getI18N(feature).getString(feature.getSimpleName()) + ":<br>" + value + "</html>");
//        JLabel label = new JLabel("<html>" + Feature.getEasyClassName(feature.getName()) + ":<br>" + value + "</html>");
        return label;
    }

    public int compareTo(Filter filter) {
        if (!(filter instanceof FeatureFilter))
            return getClass().getName().compareTo(filter.getClass().getName());

        FeatureFilter featureFilter = ( FeatureFilter ) filter;
        
        int res = value.compareTo(featureFilter.value);
        if (res != 0)
            return res;
        return this.feature.getClass().getName().compareTo(featureFilter.feature.getClass().getName());
    }
    
    public Class<? extends Feature> getFeatureClass() {
        return feature;
    }
    
    public String getValue() {
        return value;
    }
}
