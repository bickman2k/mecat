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
 * Created on Aug 4, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.medium.features.impl;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.gui.features.FeaturePanel;
import net.sourceforge.mecat.catalog.gui.features.desktop.FeatureDesktop;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.BadCondition;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.medium.features.AbstractFeature;
import net.sourceforge.mecat.catalog.medium.features.TextFeature;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.sort.ConfigurableComparator;

public class Volume extends AbstractFeature {

    public static class VolumeComparator implements ConfigurableComparator{

        public JPanel getOptions() {
            return null;
        }

        public int compare(Medium m1, Medium m2) {
//            if (!(arg0 instanceof Medium))
//                return 0;
//            if (!(arg1 instanceof Medium))
//                return 0;
//            Medium m1 = (Medium) arg0;
//            Medium m2 = (Medium) arg0;
            
            SeriesName n1 = m1.getFeature(SeriesName.class);
            SeriesName n2 = m2.getFeature(SeriesName.class);
            
            if ((n1 == null) && (n2 == null))
                return 0;
            
            if (n1 == null)
                return -1;
            
            if (n2 == null)
                return 1;

            // Get series names strings
            String _n1 = n1.get();
            String _n2 = n2.get();

            // Null and empty String are the same
            if (_n1 == null)
                _n1 = "";
            if (_n2 == null)
                _n2 = "";

            // Trim both names for better comparing
            _n1 = _n1.trim(); _n2 = _n2.trim();

            // If the series names are different
            // so is the Volume.
            if (_n1.compareToIgnoreCase(_n2) != 0)
                return _n1.compareToIgnoreCase(_n2);

            Volume v1 = m1.getFeature(Volume.class);
            Volume v2 = m2.getFeature(Volume.class);
            
            if ((v1 == null) && (v2 == null))
                return 0;
            
            int i1, i2;
            
            // TODO relation is not always transitiv
            // a.volumNumber == null
            // a.Year > b.Year
            // b.volumNumber > c.volumNumber
            // c.Year > a.Year
            // => a > b > c > a ...
            try {
                i1 = Integer.parseInt(v1.volumeNumber.get());
                i2 = Integer.parseInt(v2.volumeNumber.get());
            } catch (Exception e) {
                return Year.getComparator().compare(m1, m2);
            }
            
            if (i1 == i2)
                return Year.getComparator().compare(m1, m2);
            
            return i1 - i2;
        }

        public boolean loadFromEntry(Entry entry) {
            return true;
        }

        public void saveToEntry(Entry entry) {
        }
        
        public String toString() {
            return Options.getI18N(Volume.class).getString(Volume.class.getSimpleName());
        }

        public VolumeComparator getCopy() {
            return new VolumeComparator();
        }
        
    }

    
	class VolumeNumber extends TextFeature {
		Volume volumeFeature;
		
		public VolumeNumber(Medium medium, Volume volumeFeature) {
			super(medium, "VolumeNumber", true, false);
			this.volumeFeature = volumeFeature;
		}

		public boolean set(String pos) {
			if ((pos == null) || (pos.length() == 0)) {
				medium.entry.setAttribute(attributeName, null);
				return true;
			}
			try {
				int position = Integer.valueOf(pos).intValue();
				medium.entry.setAttribute(attributeName, String.valueOf(position));
				return true;
			} catch (Exception e) {
				return false;
			}
		}

		public String get() {
			return medium.entry.getAttribute(attributeName);
		}

		
		public String getText() {
			if (get() == null)
				return "";
			return "The Medium is the Volume " + get();
		}
        // This function is unused.
        public String getTextHTML(int availableWidth) { return null;};
        
		
//		public boolean hasOptions() { return volumeFeature.hasOptions(); }
//		public JPanel getOptionPanel() { return volumeFeature.getOptionPanel(); }
	}
	class SeriesLength extends TextFeature {
		Volume volumeFeature;

		public SeriesLength(Medium medium, Volume volumeFeature) {
			super(medium, "SeriesLength", true, false);
			this.volumeFeature = volumeFeature;
		}

		public boolean set(String pos) {
			if ((pos == null) || (pos.length() == 0)) {
				medium.entry.setAttribute(attributeName, null);
				return true;
			}
			try {
				int position = Integer.valueOf(pos).intValue();
				medium.entry.setAttribute(attributeName, String.valueOf(position));
				return true;
			} catch (Exception e) {
				return false;
			}
		}

		public String get() {
			return medium.entry.getAttribute(attributeName);
		}

		
		public String getText() {
			if (get() == null)
				return "";
			return " of " + get() + " Volumes in this Serie.";
		}
        // This function is unused.
        public String getTextHTML(int availableWidth) { return null;};

//		public boolean hasOptions() { return volumeFeature.hasOptions(); }
//		public JPanel getOptionPanel() { return volumeFeature.getOptionPanel(); }
	}

	protected final VolumeNumber volumeNumber;
    protected final SeriesLength seriesLength;

	public Volume(Medium medium) {
		super(medium);
		volumeNumber = new VolumeNumber(medium, this);
		seriesLength = new SeriesLength(medium, this);
	}
	
    public boolean hasValue() {
        return (volumeNumber.hasValue() || seriesLength.hasValue());
    }
	
	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.medium.features.Feature#getPanel(FeatureDesktop desktop)
	 */
    @Override
	public FeaturePanel getPanel(FeatureDesktop desktop, boolean border) {
		FeaturePanel<Volume> panel = new FeaturePanel<Volume>(this, desktop, true, "Volume"){

            public void featureValueChanged(Feature source) {
                // TODO Auto-generated method stub
                
            }
            
        };

		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0; c.fill = GridBagConstraints.BOTH;
		panel.add(volumeNumber.getPanel(desktop, false), c);
		panel.add(new JLabel(" of "));
		panel.add(seriesLength.getPanel(desktop, false), c);
		
		return panel;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.medium.features.Feature#validate(java.lang.String)
	 */
	public boolean validate(String condition) throws BadCondition {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.medium.features.Feature#getText()
	 */
	public String getText() {
        if (seriesLength.getShortText() == null)
            return Options.getI18N(Volume.class).getString("The number of the volume is [NUMBER].").replaceAll("\\[NUMBER\\]", volumeNumber.get());
        if (volumeNumber.getShortText() == null)
            return Options.getI18N(Volume.class).getString("The series has [LENGTH] volumes.").replaceAll("\\[LENGTH\\]", seriesLength.get());
        return Options.getI18N(Volume.class).getString("This is volume [NUMBER] of [LENGTH].").replaceAll("\\[LENGTH\\]", seriesLength.get()).replaceAll("\\[NUMBER\\]", volumeNumber.get());
	}
    public String getTextHTML(int availableWidth) {
        if (seriesLength.getShortText() == null)
            return Options.getI18N(Volume.class).getString("The number of the volume is [NUMBER].").replaceAll("\\[NUMBER\\]", "<strong>" + volumeNumber.get() + "</strong>");
        if (volumeNumber.getShortText() == null)
            return Options.getI18N(Volume.class).getString("The series has [LENGTH] volumes.").replaceAll("\\[LENGTH\\]", "<strong>" + seriesLength.get() + "</strong>");
        return Options.getI18N(Volume.class).getString("This is volume [NUMBER] of [LENGTH].").replaceAll("\\[LENGTH\\]", "<strong>" + seriesLength.get() + "</strong>").replaceAll("\\[NUMBER\\]", "<strong>" + volumeNumber.get() + "</strong>");
    }

	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.medium.features.Feature#getShortText()
	 */
	public String getShortText() {
        if (seriesLength.getShortText() == null)
            return volumeNumber.get();
        if (volumeNumber.getShortText() == null)
            return Options.getI18N(Volume.class).getString("X of [LENGTH]").replaceAll("\\[LENGTH\\]", seriesLength.get());
        return Options.getI18N(Volume.class).getString("[NUMBER] of [LENGTH]").replaceAll("\\[LENGTH\\]", seriesLength.get()).replaceAll("\\[NUMBER\\]", volumeNumber.get());
	}
    public String getShortTextHTML() {
        if (seriesLength.getShortText() == null)
            return volumeNumber.get();
        if (volumeNumber.getShortText() == null)
            return Options.getI18N(Volume.class).getString("X of [LENGTH]").replaceAll("\\[LENGTH\\]", "<strong>" + seriesLength.get() + "</strong>");
        return Options.getI18N(Volume.class).getString("[NUMBER] of [LENGTH]").replaceAll("\\[LENGTH\\]", "<strong>" + seriesLength.get() + "</strong>").replaceAll("\\[NUMBER\\]", "<strong>" + volumeNumber.get() + "</strong>");
    }

	public void copyTo(Feature feature) {
		volumeNumber.copyTo(((Volume)feature).volumeNumber);
		seriesLength.copyTo(((Volume)feature).seriesLength);
	}
	
	public boolean hasOptions() {
		return true;
	}

    static public ConfigurableComparator getComparator(){
        return new VolumeComparator();
    }
    
}
