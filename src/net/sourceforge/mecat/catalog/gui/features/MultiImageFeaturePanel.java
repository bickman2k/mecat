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
 * Created on Aug 28, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.gui.features;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileFilter;

import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.gui.features.desktop.FeatureDesktop;
import net.sourceforge.mecat.catalog.gui.options.features.imagefeature.ImageInformation;
import net.sourceforge.mecat.catalog.gui.options.features.imagefeature.ImageInformationServer;
import net.sourceforge.mecat.catalog.gui.utils.PopupMouseListener;
import net.sourceforge.mecat.catalog.medium.features.Feature;
import net.sourceforge.mecat.catalog.medium.features.ImageFeature;
import net.sourceforge.mecat.catalog.medium.features.MultiImageFeature;
import net.sourceforge.mecat.catalog.medium.hidden.BagImage;
import net.sourceforge.mecat.catalog.medium.features.impl.BagImageFeature;
import net.sourceforge.mecat.catalog.medium.features.option.MultiImageFeatureOption;
import net.sourceforge.mecat.catalog.option.Options;

public class MultiImageFeaturePanel extends FeaturePanel<MultiImageFeature> {

//    static String path = System.getProperty("user.home");
    int width = -1;

    final JEditorPane editPane = new JEditorPane(){
        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }
    };
    
	public MultiImageFeaturePanel(final MultiImageFeature feature, FeatureDesktop desktop, final boolean border) 
	{
		super(feature, desktop, border, feature.attrName);

        editPane.setContentType("text/html");
        JPopupMenu popup = getJPopupMenu();
        PopupMouseListener pml = new PopupMouseListener() {
            @Override
            public JPopupMenu getPopupMenu() {
                return MultiImageFeaturePanel.this.getJPopupMenu();
            }
        };
        addMouseListener(pml);
        editPane.addMouseListener(pml);
                
		add(editPane);
        
        width = getWidth();
        
        addComponentListener(new ComponentListener() {

            public void componentResized(ComponentEvent event) {
                if (width != getWidth()) {
                    width = getWidth();
                    rebuildEditPane();
                }
            }

            public void componentMoved(ComponentEvent arg0) {
            }

            public void componentShown(ComponentEvent arg0) {
                rebuildEditPane();
            }

            public void componentHidden(ComponentEvent arg0) {
            }
            
        });
        
        rebuildEditPane();
	}
  
    public void rebuildEditPane() {
        if (getSize().width == 0){
            editPane.setText("<HTML><BODY><H3> " + res.getString("loading...") + " </H3></BODY></HTML>");
            return;
        }
        
        StringBuffer html = new StringBuffer();
        html.append("<HTML><BODY>");
        MultiImageFeature mif = (MultiImageFeature) feature;
        Map<ImageFeature, ImageInformation> infos = new HashMap<ImageFeature, ImageInformation>();
        int width = 0;
        for (ImageFeature img : mif.getImgFeatures()) {
            ImageInformation info = ImageInformationServer.getDefaultImageInformationServer().getInfo(feature.medium, img.getImageURL());
            if (info != null)
                infos.put(img, info);
            if (info != null)
                width += infos.get(img).getWidth();
        }
        if (infos.keySet().size() == 0) {
            editPane.setText("<HTML><BODY><H3> " + res.getString("No Image available.") + " </H3></BODY></HTML>");
            return;
        }
//        html.append("<H3>" + Options.getI18N(feature.getClass()).getString(feature.getClass().getSimpleName()) + ":</H3>");
        
        int internalPreferredWidth = this.getSize().width - editPane.getInsets().left - editPane.getInsets().right - this.getInsets().left - this.getInsets().right;
        
        double scale = ((double) internalPreferredWidth) / ((double) width);
        scale = Math.min(scale, feature.getMaxScale());
        for (ImageFeature img : mif.getImgFeatures()) {
            ImageInformation info = infos.get(img);
            if (info != null) {
                html.append("<img src=\"" + img.getImageURL() + "\" width=\"" + ((int)(info.getWidth() * scale)) + "\" height=\"" + ((int)(info.getHeight() * scale)) + "\">");
            }
        }
        html.append("</BODY></HTML>");
        editPane.setText(html.toString());
    }
    

    static FileFilter JPEGFileFilter = new FileFilter(){

        @Override
        public boolean accept(File file) {
            if (file.isDirectory())
                return true;
            if (file.getName().endsWith(".jpeg"))
                return true;
            if (file.getName().endsWith(".jpg"))
                return true;
            return false;
        }

        @Override
        public String getDescription() {
            return Options.getI18N(MultiImageFeaturePanel.class).getString("JPEG-Image");
        }

    };
    
    
    protected void setPath(String path){
        
        Entry option = Options.persistent.getOption("MultiImageFeaturePanel");
        if (option == null)
            option = Options.persistent.createOption("MultiImageFeaturePanel");

        option.setAttribute("Path", path);
    }
    
    protected String getPath() {
        Entry option = Options.persistent.getOption("MultiImageFeaturePanel");

        // If the option entry does not exist then use home folder
        if (option == null)
            return System.getProperty("user.home");

        // If the attribute has not yet been set then use home folder
        String path = option.getAttribute("Path");
        if (path == null)
            return System.getProperty("user.home");
         
        return path;
    }
    
    @Override
    protected JPopupMenu getJPopupMenu() {
        MultiImageFeatureOption mifOption = (MultiImageFeatureOption) feature.getFeatureOption();

        JPopupMenu menu = super.getJPopupMenu();
        if (feature.getImgFeatures().isEmpty() && !mifOption.isBag())
            return menu;
        
        menu.addSeparator();
        for (final ImageFeature ifeature : feature.getImgFeatures())
            if (!(ifeature instanceof BagImageFeature))
                menu.add(new JMenuItem(ifeature.getAttributeName()){{addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0) {
                    JFileChooser fileChooser = new JFileChooser(getPath());
                    fileChooser.setFileFilter(JPEGFileFilter);
                    int ret = fileChooser.showOpenDialog(MultiImageFeaturePanel.this);
                    if (ret == JFileChooser.APPROVE_OPTION) {
                        try {
                            ifeature.setImageLocation(fileChooser.getSelectedFile().toURL());

                            rebuildEditPane();

                            fireRebuild();

                            // Remember the current directory for later image set operations
                            setPath(fileChooser.getCurrentDirectory().toString());

                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }
                }});}});
        
        if (mifOption.isBag() /*&& !mifOption.getBagOption().isDirStorage()*/) {
            menu.add(new JMenuItem(res.getString("New")){{addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0) {
                JFileChooser fileChooser = new JFileChooser(getPath());
                fileChooser.setFileFilter(JPEGFileFilter);
                int ret = fileChooser.showOpenDialog(MultiImageFeaturePanel.this);
                if (ret == JFileChooser.APPROVE_OPTION) {
                    try {
                        BagImage bagImage = feature.createBagImage();
                        bagImage.getFeature(BagImageFeature.class).setImageLocation(fileChooser.getSelectedFile().toURL());

                        rebuildEditPane();

                        fireRebuild();

                        // Remember the current directory for later image set operations
                        setPath(fileChooser.getCurrentDirectory().toString());

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            }});}});
        }
        
        menu.addSeparator();
        for (final ImageFeature ifeature : feature.getImgFeatures()) {
            
            if (ifeature instanceof BagImageFeature)
                continue;
            
            if (!ifeature.isDirStorage())
                continue;
            
            String menuName = res.getString("[NAME] => picture directory").replaceAll("\\[NAME\\]", ifeature.getAttributeName());
            menu.add(new JMenuItem(menuName){{addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0) {
                JFileChooser fileChooser = new JFileChooser(getPath());
                fileChooser.setFileFilter(JPEGFileFilter);
                int ret = fileChooser.showOpenDialog(MultiImageFeaturePanel.this);
                if (ret == JFileChooser.APPROVE_OPTION) {
                    try {
                        ifeature.storeImage(fileChooser.getSelectedFile().toURL());

                        rebuildEditPane();

                        fireRebuild();
                        
                        // Remember the current directory for later image store operation
                        setPath(fileChooser.getCurrentDirectory().toString());
                        
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            }});}});
        }

        if (mifOption.isBag() && mifOption.getBagOption().isDirStorage()) {
            menu.add(new JMenuItem(res.getString("Store new")){{addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0) {
                JFileChooser fileChooser = new JFileChooser(getPath());
                fileChooser.setFileFilter(JPEGFileFilter);
                int ret = fileChooser.showOpenDialog(MultiImageFeaturePanel.this);
                if (ret == JFileChooser.APPROVE_OPTION) {
                    try {
                        BagImage bagImage = feature.createBagImage();
                        bagImage.getFeature(BagImageFeature.class).storeImage(fileChooser.getSelectedFile().toURL());

                        rebuildEditPane();

                        fireRebuild();

                        // Remember the current directory for later image set operations
                        setPath(fileChooser.getCurrentDirectory().toString());

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            }});}});
        }
        
        return menu;
    }

    // Set the preferred width to null in order to be downscalable
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(0, super.getPreferredSize().height);
    }

    public void featureValueChanged(Feature source) {
        // TODO Auto-generated method stub
        
    }
}
