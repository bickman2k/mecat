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
 * Created on Sep 27, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.srp.utils;

import javax.swing.BoundedRangeModel;
import javax.swing.JProgressBar;

import sun.net.ProgressEvent;

public class ProgressBarWithListener extends JProgressBar implements net.sourceforge.mecat.srp.utils.ProgressListener, sun.net.ProgressListener {

	public ProgressBarWithListener() {
		super();
	}

	public ProgressBarWithListener(int arg0) {
		super(arg0);
	}

	public ProgressBarWithListener(int arg0, int arg1) {
		super(arg0, arg1);
	}

	public ProgressBarWithListener(int arg0, int arg1, int arg2) {
		super(arg0, arg1, arg2);
	}

	public ProgressBarWithListener(BoundedRangeModel arg0) {
		super(arg0);
	}

	public void handleProgress(Object source, double progress) {
		setValue((int)(progress * (getMaximum()- getMinimum())) + getMinimum());
	}

	public void progressStart(ProgressEvent event) {
		setMinimum(0);
		setMaximum(event.getExpected());
	}

	public void progressUpdate(ProgressEvent event) {
		setValue(event.getProgress());
	}

	public void progressFinish(ProgressEvent arg0) {
		setValue(getMaximum());
	}

}
