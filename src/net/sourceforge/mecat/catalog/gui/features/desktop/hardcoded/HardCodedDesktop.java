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
 * Created on Jul 22, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.gui.features.desktop.hardcoded;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collection;
import java.util.Vector;

import javax.swing.BoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.mecat.catalog.gui.features.FeaturePanel;
import net.sourceforge.mecat.catalog.gui.features.desktop.ChainableFeatureDesktop;
import net.sourceforge.mecat.catalog.gui.features.desktop.FeatureDesktop;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.option.LocalListener;
import net.sourceforge.mecat.catalog.option.LocalListenerEvent;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.catalog.option.WeakLocalListener;
import net.sourceforge.mecat.catalog.option.preferences.GlobalMediaOption;

public class HardCodedDesktop implements ChainableFeatureDesktop, ComponentListener, LocalListener {
    
    Medium medium = null;
	JPanel desktop = new JPanel();
    final JScrollPane scrollPane = new JScrollPane(desktop, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    // The following Vector will be replaced (yes points to a totally new Vector)
    // whenever setMedium(final Medium medium) is invoked
    // It starts with an empty Vector for all functions to work correct
    Vector<FeaturePanel> panels = new Vector<FeaturePanel>();
    
    // This variable contains the last known focus
    // if it is null, this means there has not been any focus yet
    // on any previously set medium
    Class<? extends Feature> focusCache = null;
    
    Vector<Class<? extends Feature>> showFeatures = new Vector<Class<? extends Feature>>(GlobalMediaOption.getFeatures().size());

    // If the scrollbar is used then, we need to add an empty panel at the bottom
    // this prevents the other 
    final boolean useScrollBar;
    
    public HardCodedDesktop(){
        this(true);
    }
    public HardCodedDesktop(boolean useScrollBar){
        this(useScrollBar, GlobalMediaOption.getFeatures());
    }

    public HardCodedDesktop(Class<? extends Feature> ... showFeatures) {
        this(true, showFeatures);
    }
    
    public HardCodedDesktop(boolean useScrollBar, Class<? extends Feature> ... showFeatures) {
        this(useScrollBar, Arrays.asList(showFeatures));
    }
    
    public HardCodedDesktop(Collection<Class<? extends Feature>> showFeatures) {
        this(true, showFeatures);
    }
    
    public HardCodedDesktop(boolean useScrollBar, Collection<Class<? extends Feature>> showFeatures) {
        this.showFeatures.addAll(showFeatures);
        this.useScrollBar = useScrollBar;
        Options.addLocalListener(new WeakLocalListener(this));
        scrollPane.setVerticalScrollBarPolicy((useScrollBar)?JScrollPane.VERTICAL_SCROLLBAR_ALWAYS:JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setModel(new BoundedRangeModel(){

            int min = 0;
            int max = 0;
            int val = 0;
            int extent = 0;
            boolean adjusting = false;
            
            public int getMinimum() {
//                System.out.println("getMinimum()->" + min);
                return min;
            }

            public void setMinimum(int min) {
                this.min = min;
//                System.out.println("setMinimum(" + min + ")");
                fireChange();
            }

            public int getMaximum() {
 //               System.out.println("getMaximum()->" + max);
                return max;
            }

            public void setMaximum(int max) {
                this.max = max;
//                System.out.println("setMaximum(" + max + ")");
                fireChange();
            }

            public int getValue() {
//                System.out.println("getValue()->" + val);
                return val;
            }

            public void setValue(int val) {
               this.val = val;
//               System.out.println("setValue(" + val + ")");
               fireChange();
            }

            public void setValueIsAdjusting(boolean adjusting) {
                this.adjusting = adjusting;
//                System.err.println("setValueIsAdjusting(" + adjusting + ")");
            }

            public boolean getValueIsAdjusting() {
//                System.err.println("getValueIsAdjusting()->" + adjusting);
                return adjusting;
            }

            public int getExtent() {
                return extent;
            }

            public void setExtent(int extent) {
                this.extent = extent;
                fireChange();
            }

            public void setRangeProperties(int newValue, int newExtent, int newMin, int newMax, boolean adjusting) {
// There is some part that tries to put the value in the middle on every rebuild and I can't let that happen.
//                this.val = newValue;
                this.extent = newExtent;
                this.min = newMin;
                this.max = newMax;
                this.adjusting = adjusting;
                if (this.val > this.max)
                    this.val = this.max;
//                (new Exception()).printStackTrace();
//                System.out.println("setRangeProperties(" + newValue + ", " + newExtent + ", " + newMin + ", " + newMax + ", " + adjusting + ")");
                fireChange();
            }

            Vector<ChangeListener> changeListeners = new Vector<ChangeListener>();
            
            protected void fireChange(){
                for (ChangeListener changeListener : changeListeners)
                    changeListener.stateChanged(new ChangeEvent(this));
            }
            
            public void addChangeListener(ChangeListener changeListener) {
                changeListeners.add(changeListener);
            }

            public void removeChangeListener(ChangeListener changeListener) {
                changeListeners.remove(changeListener);
            }});
    }
    
    public void stateChanged(LocalListenerEvent event) {
        // In case of a language change just rebuild everything
        fullRebuild();
        
    }

    public Vector<Class<? extends Feature>> getShowedFeatures(){
        return showFeatures;
    }
    
    protected boolean isWanted(Feature feature) {
        if (medium.getListing() == null)
            return true;
        return medium.getListing().getTotalPreferences().getMediaOption().isWanted(medium.getClass(), feature.getClass(), HardCodedDesktop.class);
    }
    
    protected void fullRebuild() {
        for (FeaturePanel panel : panels)
            panel.removeRebuildListener(this);
        
        if (medium != null) {
            Vector<FeaturePanel> tmp = new Vector<FeaturePanel>();
            for (Feature f : medium.getFeatures()) {
                
                if (!isWanted(f))
                    continue;
                
                if (!showFeatures.contains(f.getClass()))
                    continue;

                FeaturePanel panel = f.getPanel(this);
                if (panel != null) {
                    tmp.add(panel);
                    panel.addRebuildListener(this);
                    
                }
            }
            panels = tmp;
        } else
            panels = new Vector<FeaturePanel>();
        
        rebuild();
    }
    
	public void rebuild() {
	    makeDesktop();
	    desktop.updateUI();
	}
	
    public void setMedium(final Medium medium) {
        // Preserve focus information if the desktop itself
        // still holds the focus. 
        // This way the focus will not be lost when a new medium 
        // has not the feature where the focus was set.
        // And if we encounter another medium with the feature
        // and the focus has not be changed in the meantime,
        // the original feature gets the focus again.
        //
        // This is interesting if you have a mixed list
        // and you want to change a feature for more then
        // one medium but the feature is not applicable for
        // all of the mediums in the list.
        if (!desktop.hasFocus())
            focusCache = null;
        
        // Store focus
        for (FeaturePanel panel : panels)
            if (panel.hasFocus()) {
                Feature feature = panel.getFeature();
                if (feature == null)
                    break;
                focusCache = feature.getClass();
                // Don't need to look into the others
                // we already found the focus
                break;
            }
        
        if (medium == this.medium)
            return;

        this.medium = medium;

        fullRebuild();
        
        updateChainedDesktops();

        // Restore focus
        if (focusCache != null) {
            boolean focusPreserved = false;
            for (FeaturePanel panel : panels){
                Feature feature = panel.getFeature();
                if (feature == null)
                    continue;
                if (feature.getClass().equals(focusCache)) {
                    panel.requestFocus();
                    focusPreserved = true;
                    break;
                }
            }
            // If the focus is not applicable for this medium
            // then preserve the focus with the desktop.
            if (!focusPreserved)
                desktop.requestFocus();
        }
    }
	
	public JComponent getDesktop() {
		return scrollPane;	
	}

    
    public void setPreferredDesktopWidth(int width) {
    }

    // DEBUG information
    static Vector<WeakReference<JPanel>> rets = new Vector<WeakReference<JPanel>>();
    static Vector<WeakReference<FeaturePanel>> fps = new Vector<WeakReference<FeaturePanel>>();
    
	protected void makeDesktop() {
        if (Options.DEBUG) {
            System.gc();
            for (WeakReference<FeaturePanel> ref : fps)
                if (ref.get() != null)
                    System.out.println(ref.get().getClass().getName());
        }
        JPanel ret = new JPanel();
        if (Options.DEBUG)
            rets.add(new WeakReference<JPanel>(ret));

        ret.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.gridwidth = GridBagConstraints.REMAINDER;

        if (useScrollBar) {
            for (FeaturePanel panel : panels) 
                ret.add(panel, c);

            c.weighty = 1;
            ret.add(new JPanel(), c);
        } else if (panels.size() > 0) {

            for (FeaturePanel panel : panels) 
                if (!panel.equals(panels.lastElement()))
                    ret.add(panel, c);
    
            c.weighty = 1;
            ret.add(panels.lastElement(), c);
            
        }

        if (desktop.getComponents().length > 0) {
            JPanel old = (JPanel) (desktop.getComponents()[0]);
            if (Options.DEBUG) {
                System.out.println(" Remove " + desktop.getComponents()[0].hashCode());
                for (Component component : old.getComponents())
                    if (component instanceof FeaturePanel)
                        fps.add(new WeakReference<FeaturePanel>((FeaturePanel)component));
            }
            old.removeAll();
            desktop.remove(desktop.getComponents()[0]);
        }

        desktop.setLayout(new BorderLayout());
        desktop.add(ret);

        if (Options.DEBUG) {
            System.gc();
            System.out.println("All remaining rets");
            int remaining = 0;
            for (WeakReference<JPanel> r : rets)
                if (r.get() != null) {
                    System.out.println(r.get().hashCode());
                    remaining++;
                }
            if (remaining > 1)
                System.err.println("Garbage collection of hardcodeddesktop components did not work.");
        }
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.gui.features.desktop.FeatureDesktop#saveSettings()
	 */
	public void saveSettings() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.gui.features.desktop.FeatureDesktop#loadSettings()
	 */
	public void loadSettings() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
	 */
	public void componentResized(ComponentEvent e) {
        rebuild();
		
		System.out.println("HardCoded.componentResized(" + e.getSource().hashCode() + ");");
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
	 */
	public void componentMoved(ComponentEvent arg0) {
		desktop.updateUI();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
	 */
	public void componentShown(ComponentEvent arg0) {
		desktop.updateUI();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
	 */
	public void componentHidden(ComponentEvent arg0) {
		desktop.updateUI();
	}


	/* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.gui.features.desktop.RebuildListener#Rebuild()
	 */
	public void Rebuild() {
//        System.out.print("Hardcoded.");

        rebuild();
        
//		System.out.println("Rebuild();");
	}


    Vector<FeatureDesktop> chainedFeatureDesktops = new Vector<FeatureDesktop>();
    
    public void addDesktop(FeatureDesktop featureDesktop) {
        chainedFeatureDesktops.add(featureDesktop);
    }


    public void removeDesktop(FeatureDesktop featureDesktop) {
        chainedFeatureDesktops.remove(featureDesktop);
    }

    
    protected void updateChainedDesktops() {
        for (FeatureDesktop featureDesktop : new Vector<FeatureDesktop>(chainedFeatureDesktops))
            featureDesktop.setMedium(medium);
    }

    public void requestFocus() {
        if (panels.isEmpty())
            return;
        
        panels.firstElement().requestFocus();
        
        scrollPane.getVerticalScrollBar().setValue(0);
    }
    
}
