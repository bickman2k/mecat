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
 * Created on Jul 30, 2004
 * @author Stephan Richard Palm
 *
 * An export works like this:
 * It gets an ListModel and uses the information in the ListModel
 * to generate an export.
 * 
 */
package net.sourceforge.mecat.catalog.export;

import java.awt.Component;
import java.util.Vector;

import javax.swing.JPanel;

import net.sourceforge.mecat.catalog.gui.ShowListing;
import net.sourceforge.mecat.catalog.option.Options;

public abstract class Export extends Thread {

	/**
	 * This is the progressframe.
	 */
	JPanel progressPanel;
    protected Component parent;
	
	Vector<ExportProgressListener> exportProgressListeners = new Vector<ExportProgressListener>();

    public void setParentComponent(Component parent) {
        this.parent = parent;
    }
    
	public void addExportProgressListener(ExportProgressListener listener) {
		exportProgressListeners.add(listener);
	}

	public void fireExportStarted(final int depth) {
		for (ExportProgressListener listener : exportProgressListeners)
            listener.ExportStarted(this, depth);
	}
	
    public void fireExportFinished() {
        for (ExportProgressListener listener : exportProgressListeners)
            listener.ExportFinished(this);
    }
    
    public void fireExportStopped() {
        for (ExportProgressListener listener : exportProgressListeners)
            listener.ExportStopped(this);
    }
    
    public void fireExportShowsPrintOption() {
        for (ExportProgressListener listener : exportProgressListeners)
            listener.ExportShowsPrintOption(this);
    }
    
    public void fireExportStoppedShowingPrintOption() {
        for (ExportProgressListener listener : exportProgressListeners)
            listener.ExportStoppedShowingPrintOption(this);
    }
    
    public void fireExportUpdate(final int depth, final int offset, final int size) {
        for (ExportProgressListener listener : exportProgressListeners)
            listener.ExportUpdate(this, depth, offset, size);
    }
    
    public void fireExportProgessSetMessage(final int depth, final String message) {
        for (ExportProgressListener listener : exportProgressListeners)
            listener.ExportProgessSetMessage(this, depth, message);
    }
    
    public void fireExportProgessSetTitle(final int depth, final String title) {
        for (ExportProgressListener listener : exportProgressListeners)
            listener.ExportProgessSetTitle(this, depth, title);
    }
    
/*	
	public void fireFinishedEvent() {
		for (ExportProgressListener listener : exportProgressListeners)
			listener.progressFinish(new ProgressEvent(new sun.net.ProgressSource(null, null), 
					null, "", "", null, list.getSize(), list.getSize()));
//		if (dialog != null)
//			dialog.dispose();
		if (progressPanel != null) {
			progressPanel.removeAll();
			progressPanel.add(new JLabel(Options.getI18N(Export.class).getString("Finished")));
			progressPanel.updateUI();
		}
			
	}*/

	/**
	 * List that will be exported.
	 */
	protected ShowListing list;
	
	/**
	 * Exports the ListModel given by l.
	 * 
	 * @param l ListModel to export
	 */
	public void export(ShowListing l) {
		this.list = l;
		export();
	}
	
    /**
     * Gives signal to the export 
     * to stop/interupt now
     */
    public abstract void stopExport();
    
    
	/**
	 * Exports the current listmodel.
	 *
	 */
	public abstract void export();
	
	/**
	 * Returns a JPanel where this Export can be configured with
	 * @return JPanel to configure the export.
	 */
//	public abstract JPanel options(final TotalPreferences totalPreferences);
	
	/**
	 * Sets the Profile for the export.
	 * Ex. standard place for the export to go.
	 * Ex. amout of information to export
	 * Ex. layout of the information
	 * 
	 * @param profile
	 * @return if the profile is aplicable for the Export.
	 */
	public abstract boolean setProfile(ExportProfile profile);
	
	public abstract ExportProfile getProfile();
	
	public void run() {
		long start = System.currentTimeMillis();
		export();
		long end = System.currentTimeMillis();
        if (Options.verbosity >= 1)
            System.out.println("[Export ] " + Options.getI18N(Export.class).getString("TheExportNeeded").replaceAll("\\[TIME\\]", String.valueOf(end - start)));
	}

}
