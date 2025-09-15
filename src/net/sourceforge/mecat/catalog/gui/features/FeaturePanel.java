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
 * Created on Jul 16, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.gui.features;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import net.sourceforge.mecat.catalog.datamanagement.changelog.ChangeLog;
import net.sourceforge.mecat.catalog.gui.features.desktop.FeatureDesktop;
import net.sourceforge.mecat.catalog.gui.features.desktop.RebuildListener;
import net.sourceforge.mecat.catalog.gui.utils.PopupMouseListener;
import net.sourceforge.mecat.catalog.i18n.fromCatalog.LayeredResourceBundle;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalTitledBorder;
import net.sourceforge.mecat.catalog.medium.Listing;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.medium.features.listener.FeatureListener;
import net.sourceforge.mecat.catalog.medium.features.listener.WeakFeatureListener;
import net.sourceforge.mecat.catalog.option.Options;


public abstract class FeaturePanel<T extends Feature> extends JPanel implements FeatureListener {

	protected final T feature;
	protected ResourceBundle res;
	/* TODO  remove public*/
    public Vector<RebuildListener> rebuildListeners = new Vector<RebuildListener>();
    final FeatureDesktop desktop;
    final boolean border;
    
    public T getFeature() {
        return feature;
    }
    
	public void addRebuildListener(RebuildListener listener) {
		rebuildListeners.add(listener);
	}
    public void removeRebuildListener(RebuildListener listener) {
        rebuildListeners.remove(listener);
    }
	
	protected void fireRebuild() {
		for (RebuildListener listener : rebuildListeners)
			listener.Rebuild();
	}
	
    int transactionId = -1;
    
    public void startTransaction(String name, boolean atom, boolean userInvoked) {
        ChangeLog log = getChangeLog();
        if (log == null)
            return;
        transactionId = log.openTransaction(Options.getI18N(feature.getClass()).getString(feature.getClass().getSimpleName()) + ": " + name, atom, userInvoked);
    }

    public void stopTransaction() {
        ChangeLog log = getChangeLog();
        if (log == null)
            return;
        log.closeTransaction(transactionId);
    }
    
    public ChangeLog getChangeLog() {
        return feature.getMedium().getListing().getChangeLog();
    }
    
    public FeaturePanel(final T feature, final FeatureDesktop desktop, final boolean border, final String label) {
        this(feature, desktop, border, label, null);
    }
	public FeaturePanel(final T feature, final FeatureDesktop desktop, final boolean border, final String label, final LayeredResourceBundle extraResources) {
		setLayout(new BorderLayout());
		this.feature = feature;
        this.desktop = desktop;
        this.border = border;
		// Workaround for bug in java 5.0 beta-2
//		feat = feature;
		
		// Get Resources for i18n
        if (extraResources != null)
            res = new LayeredResourceBundle(extraResources, Options.getI18N(getClass()), Options.getI18N(feature.getClass()));
        else
            res = new LayeredResourceBundle(Options.getI18N(getClass()), Options.getI18N(feature.getClass()));

		// Set a border with a descriptive label
		if (border) 
            setBorder(new SimpleLocalTitledBorder(res, label));

        JPopupMenu popup = getJPopupMenu();
		if (popup.getSubElements().length > 0)
			addMouseListener(new PopupMouseListener(){
			    // The popupmenu has to be created on time since otherwise it 
                // can not depend on the current situation and will be misleading
                public JPopupMenu getPopupMenu() {
			        return FeaturePanel.this.getJPopupMenu();
                }
            });

        // Add listener with weak link
        // this allows to cut the FeaturePanel loose if it 
        // is no longer needed. This allows the GC to do his job.
        feature.addFeatureListener(new WeakFeatureListener(this));
	}

    protected void addPopupMenu(JComponent component) {
        JPopupMenu popup = getJPopupMenu();
        if (popup.getSubElements().length > 0)
            component.addMouseListener(new PopupMouseListener(){
                // The popupmenu has to be created on time since otherwise it 
                // can not depend on the current situation and will be misleading
                public JPopupMenu getPopupMenu() {
                    return FeaturePanel.this.getJPopupMenu();
                }
            });
    }
    
	
	protected JPopupMenu getJPopupMenu() {
		JPopupMenu menu = new JPopupMenu();

        Listing listing = feature.getMedium().getListing();
		if (listing != null && listing.getTotalPreferences().getFeaturesOption().getOption(feature.getClass()) != null) {
		    JMenuItem menu_Options = new JMenuItem(res.getString("Options"));
		    menu_Options.addActionListener(new ActionListener() {
		        public void actionPerformed(ActionEvent arg0) {
		            feature.showOptions();
		        }});
		    menu.add(menu_Options);
		}
		
		return menu;
	}

}