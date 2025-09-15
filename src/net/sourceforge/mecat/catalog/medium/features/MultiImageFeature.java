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
package net.sourceforge.mecat.catalog.medium.features;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JPanel;

import net.sourceforge.mecat.catalog.datamanagement.Entry;
import net.sourceforge.mecat.catalog.gui.features.FeaturePanel;
import net.sourceforge.mecat.catalog.gui.features.MultiImageFeaturePanel;
import net.sourceforge.mecat.catalog.gui.features.desktop.FeatureDesktop;
import net.sourceforge.mecat.catalog.gui.options.features.MultiImageFeatureOptionPanel;
import net.sourceforge.mecat.catalog.gui.options.features.imagefeature.ImageInformation;
import net.sourceforge.mecat.catalog.gui.options.features.imagefeature.ImageInformationServer;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.impl.BagImageFeature;
import net.sourceforge.mecat.catalog.medium.features.option.FeatureOption;
import net.sourceforge.mecat.catalog.medium.features.option.MultiImageFeatureOption;
import net.sourceforge.mecat.catalog.medium.hidden.BagImage;
import net.sourceforge.mecat.catalog.option.Options;
import net.sourceforge.mecat.srp.utils.EmptyIterator;

/**
 * A set of images.
 * 
 * The set of images is split into two disjunct sets.
 * One containg a fixed number of images all with a name.
 * The other set is a bag containing an unbound number of images with no fixed name.
 * 
 * For option purposes the hole bag of images has one setting.
 * 
 * 
 * Created on Sep 10, 2006
 *
 * @author Stephan Richard Palm
 *
 */
abstract public class MultiImageFeature extends AbstractFeature {
	
    
    // Get special option if split on 
    // and sharedOption if split off
    class PartialImageFeature extends ImageFeature {

        public PartialImageFeature(Medium medium, String attributeName, String optionsName) {
            super(medium, attributeName, optionsName);
        }

        @Override
        public FeatureOption getFeatureOption() {
             FeatureOption featureOption = MultiImageFeature.this.getFeatureOption();
             if (!(featureOption instanceof MultiImageFeatureOption))
                 return null;
             
             MultiImageFeatureOption multiImageFeatureOption = ( MultiImageFeatureOption ) featureOption;
             
             if (multiImageFeatureOption.isSplitOption())   
                 return multiImageFeatureOption.getMap().get(attributeName);
             else
                 return multiImageFeatureOption.getSharedOption();
        }

    }
    

    
	public String attrName;
	private Vector<ImageFeature> imgFeatures = new Vector<ImageFeature>();
    Vector<BagImage> bagImages = null;
    
    /**
     * 
     * 
     * @param medium
     * @param attrNames
     * @param attrName
     */
	public MultiImageFeature(Medium medium, Vector<String> attrNames, String attrName) {
		super(medium);
		this.attrName = attrName;

		for (String attr : attrNames)
			imgFeatures.add(new PartialImageFeature(medium, attr, attrName));
        
       
	}

    
    public BagImage createBagImage() {
        if (bagImages == null)
            loadBagImages();

        MultiImageFeatureOption option = ( MultiImageFeatureOption ) getFeatureOption();
        if (!option.isBag())
            return null;
        
        startTransaction(Options.getI18N(MultiImageFeature.class).getString("Create image entry"), true, true);
        
        BagImage bagImage = new BagImage(medium.entry.createSubEntry("BagImage"), medium.getListing());
        bagImage.setOwner(this);
        bagImages.add(bagImage);
        imgFeatures.add(bagImage.getFeature(BagImageFeature.class));

        stopTransaction();
        
        return bagImage;
    }
    
    public List<BagImage> getBagImages() {
        if (bagImages == null)
            loadBagImages();
        
        return bagImages;
    }
    
    private void loadBagImages() {
        bagImages = new Vector<BagImage>();
        MultiImageFeatureOption option = ( MultiImageFeatureOption ) getFeatureOption();
        if (!option.isBag())
            return;

        List<? extends Entry> entries = medium.entry.getSubEntries("BagImage");
            
        for (Entry entry : entries)
            try {
                BagImage bagImage = new BagImage(entry, medium.getListing());
                bagImage.setOwner(this);
                bagImages.add(bagImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        for (BagImage bagImage : bagImages) 
            imgFeatures.add(bagImage.getFeature(BagImageFeature.class));

    }
    
    public Vector<ImageFeature> getImgFeatures() {
        if (bagImages == null)
            loadBagImages();
        
        return imgFeatures;
    }
    
    
    
	@Override
    public JPanel getFeatureOptionPanel(FeatureOption featureOption) {
        if (!(featureOption instanceof MultiImageFeatureOption))
            return null;
        MultiImageFeatureOption multiImageFeatureOption = ( MultiImageFeatureOption ) featureOption;

        return new MultiImageFeatureOptionPanel(multiImageFeatureOption, Options.getI18N(getClass()));
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.mecat.catalog.medium.features.Feature#getPanel(FeatureDesktop desktop)
	 */
    @Override
	public FeaturePanel getPanel(FeatureDesktop desktop, boolean border) {
		return new MultiImageFeaturePanel(this, desktop, border);
	}
	
/*	public JPanel getOptionPanel() {
		return null;
	}*/
	public boolean hasOptions() {
		return true;
	}

/*    static public boolean storeOptions(Entry entry) {
		return false;
	}*/
	public boolean validate(String condition) throws BadCondition {
		throw new BadCondition();
	}
	public String getText() {
		return null;
	}
	public String getShortText() {
		return null;
	}
    
    public double getMaxScale() {
        MultiImageFeatureOption option = ( MultiImageFeatureOption ) getFeatureOption();
        
        if (option == null)
            return 1.0;
        
        return option.getMaxScale();
    }
    
    public String getTextHTML(int availableWidth) {
        StringBuffer ret = new StringBuffer();
        Map<ImageFeature, ImageInformation> infos = new HashMap<ImageFeature, ImageInformation>();
        int width = 0;
        for (ImageFeature img : imgFeatures) {
            ImageInformation info = ImageInformationServer.getDefaultImageInformationServer().getInfo(medium, img.getImageURL());
            if (info != null)
                infos.put(img, info);
            if (info != null)
                width += infos.get(img).getWidth();
        }
        if (infos.keySet().size() == 0)
            return null;

        double scale = ((double) availableWidth) / ((double) width);
        scale = Math.min(scale, getMaxScale());

        for (ImageFeature img : imgFeatures) {
            ImageInformation info = infos.get(img);
            if (info != null) 
                if (availableWidth == -1)
                    ret.append("<img src=\"" + img.getImageURL() + "\" width=\"" + ((double)(info.getWidth() * 100))/((double)(width))  + "%\" >");
                else
                    ret.append("<img src=\"" + img.getImageURL() + "\" width=\"" + ((int)(info.getWidth() * scale)) + "\" height=\"" + ((int)(info.getHeight() * scale)) + "\">");
        }
        return ret.toString();
    }
    public String getShortTextHTML() {
        return null;
    }
	private ImageFeature getImageFeatureByAttrName(String attrName) {
		for (ImageFeature iFeature : imgFeatures) 
			if (iFeature.attributeName.equals(attrName))
				return iFeature;
		return null;
	}
	
	public void copyTo(Feature feature) {
        if (bagImages == null)
            loadBagImages();

        if (!getClass().equals(feature.getClass()))
			return;
		MultiImageFeature mif = (MultiImageFeature)feature;
		
		for (ImageFeature iFeature : imgFeatures)
            if (iFeature instanceof PartialImageFeature)
                iFeature.copyTo(mif.getImageFeatureByAttrName(iFeature.attributeName));

        if (((MultiImageFeatureOption)mif.getFeatureOption()).isBag())
            for (BagImage bagImage : getBagImages()) {
                bagImage.getFeature(BagImageFeature.class).getImageURL();
                BagImage newBagImage = mif.createBagImage();
                newBagImage.getFeature(BagImageFeature.class).setImage( bagImage.getFeature(BagImageFeature.class).getImageURL() );
            }
        
	}

	
    public boolean hasValue() {
        if (bagImages == null)
            loadBagImages();

        for (ImageFeature iFeature : imgFeatures) 
            if (iFeature.hasValue())
                return true;
        return false;
    }


    public Iterator<BagImage> getSubMedia() {
        if (bagImages == null)
            return new EmptyIterator<BagImage>();
        return bagImages.iterator();
    }
    
    
}
