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
 * Created on Oct 24, 2005
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;

import net.sourceforge.mecat.catalog.filter.Filter;
import net.sourceforge.mecat.catalog.filter.design.FilterDesigner;
import net.sourceforge.mecat.catalog.gui.options.LanguageSelection;
import net.sourceforge.mecat.catalog.medium.features.Languages;
import net.sourceforge.mecat.catalog.option.LocalListener;
import net.sourceforge.mecat.catalog.option.LocalListenerEvent;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.WeakLocalListener;
import net.sourceforge.mecat.catalog.sort.design.FeatureSortOptions;

public class ListingPropertiesVisualisation extends JTabbedPane implements LocalListener {

    // The featureSortOption contains the current selected order
    final FeatureSortOptions featureSortOptions = new FeatureSortOptions();
    // The filterDesigner contains the selected Filter
    // A filter null will become an always true.
    final FilterDesigner filterDesigner = new FilterDesigner();
    // If Locale is null this usally means to take the 
    // currentLocale from Options.
    final LanguageSelection languageSelection = new LanguageSelection();

    // This showListing is used to see what media types are used and the preferences, ...
    ShowListing showListing = null;
    
    ListingProperties listingProperties;
    
    public void setListingProperties(final ListingProperties listingProperties) {
        this.listingProperties = listingProperties;

        featureSortOptions.setSorting(listingProperties.getSorting());
        filterDesigner.setFilter(listingProperties.getFilter());
        languageSelection.setLocale(listingProperties.getLanguage());
    }
    

    public void setShowListing(final ShowListing showListing) {
        this.showListing = showListing;
        filterDesigner.setTotalPreferences(showListing.getSource().getTotalPreferences());
        featureSortOptions.setListing(showListing.getSource());
    }

    /**
     * 
     * @param showListing This will be used to help the use make decissions
     */
    public ListingPropertiesVisualisation() {
        ImageIcon funel = ToolBarUtils.loadImage(ToolBarUtils.class, "funel", 2, Options.getI18N(Filter.class).getString("Filter"));
        ImageIcon number = ToolBarUtils.loadImage(ToolBarUtils.class, "number", 2, Options.getI18N(MainFrameBackend.class).getString("Sorting"));

        featureSortOptions.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                listingProperties.setSorting(featureSortOptions.getSorting());
            }
        });
        filterDesigner.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                listingProperties.setFilter(filterDesigner.getDesignedFilter());
            }
        });
        languageSelection.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                listingProperties.setLanguage(languageSelection.getLocale());
            }
        });
        
        
        addTab(Options.getI18N(Filter.class).getString("Filter"), funel, filterDesigner);
        addTab(Options.getI18N(MainFrameBackend.class).getString("Sorting"), number, featureSortOptions);
        addTab(Options.getI18N(Languages.class).getString("Languages"), null, languageSelection);

        Options.addLocalListener(new WeakLocalListener(this));
        // Set labels to the current locale
        setLabels();
    }

    public void stateChanged(LocalListenerEvent event) {
        setLabels();
    }
    
    public void setLabels() {
        for (int i = 0; i < this.getTabCount(); i++) {
            Component component = this.getComponentAt(i);
            if (component == filterDesigner) {
                this.setTitleAt(i, Options.getI18N(Filter.class).getString("Filter"));
            } else if (component == featureSortOptions) {
                this.setTitleAt(i, Options.getI18N(MainFrameBackend.class).getString("Sorting"));
            } else if (component == languageSelection) {
                this.setTitleAt(i, Options.getI18N(Languages.class).getString("Languages"));
            } else
                System.err.println("Unexpected component " + component);
        }
    }
}
