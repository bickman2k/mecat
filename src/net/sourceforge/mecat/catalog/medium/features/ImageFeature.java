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
 * Created on Aug 27, 2004
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.medium.features;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JPanel;

import net.sourceforge.mecat.catalog.gui.features.FeaturePanel;
import net.sourceforge.mecat.catalog.gui.features.ImageFeaturePanel;
import net.sourceforge.mecat.catalog.gui.features.desktop.FeatureDesktop;
import net.sourceforge.mecat.catalog.gui.options.features.ImageFeatureOptionPanel;
import net.sourceforge.mecat.catalog.gui.options.features.imagefeature.ImageInformation;
import net.sourceforge.mecat.catalog.gui.options.features.imagefeature.ImageInformationServer;
import net.sourceforge.mecat.catalog.i18n.util.SimpleLocalTitledBorder;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.impl.Ident;
import net.sourceforge.mecat.catalog.medium.features.option.FeatureOption;
import net.sourceforge.mecat.catalog.medium.features.option.ImageFeatureOption;
import net.sourceforge.mecat.catalog.option.Options;

public class ImageFeature extends AbstractFeature {

    @Override
    public JPanel getFeatureOptionPanel(FeatureOption featureOption) {
        if (!(featureOption instanceof ImageFeatureOption))
            return null;

        ImageFeatureOption imageFeatureOption = ( ImageFeatureOption ) featureOption;

        JPanel panel = new ImageFeatureOptionPanel(imageFeatureOption);

        if (panel == null)
            return null;
        
        panel.setBorder(new SimpleLocalTitledBorder(Options.getI18N(ImageFeature.class), "Image storage option"));

        return panel;
    }
    
    
    protected ImageFeatureOption getImageFeatureOption() {
        FeatureOption featureOption = getFeatureOption();
        if (!(featureOption instanceof ImageFeatureOption))
            return null;
        return (ImageFeatureOption) featureOption;
    }

    public boolean isDirStorage() {
        ImageFeatureOption imageFeatureOption = getImageFeatureOption();
        if (imageFeatureOption != null)
            return imageFeatureOption.isDirStorage();
        return false;
    }
    
	protected String attributeName;
	String optionsName;
    
    public String getAttributeName() {
        return attributeName;
    }
	
    public String getOptionsName() {
        return optionsName;
    }
	
	public ImageFeature(Medium medium, String attributeName, String optionsName) {
		super(medium);
		this.attributeName = attributeName;
		this.optionsName = optionsName;
        
/*        ImageFeatureStaticInformation info = ( ImageFeatureStaticInformation ) getStaticListingOption();
        if ( !info.optionsNames.contains(optionsName) )
            info.optionsNames.add(optionsName);*/
        
	}
	
/*	@Override
    public Object getFreshStaticListingOption(final Listing listing) {
        return new ImageFeatureStaticInformation(listing);
    }*/

    @Override
    public FeaturePanel getPanel(FeatureDesktop desktop, boolean border) {
		return new ImageFeaturePanel(this, desktop, border);
	}

	public Image getImage() {
        ImageFeatureOption imageFeatureOption = getImageFeatureOption();
		Image image = null;

        // First try to get image from location stored in catalog
        URL url = getImageLocationFromCatalog();
		if (url != null)
			image = Toolkit.getDefaultToolkit().createImage(url);
        if (image != null)// && (image.getWidth(null) > 0))
			return image;

        // If there are no ImageFeatureOption => give up
        if (imageFeatureOption == null)
            return null;
        
        // If there is no Storage Dir => just give up
		if (!imageFeatureOption.isDirStorage())
			return null;
		URL url_base = imageFeatureOption.getDirLocation();

        // If my base URL is null => no chance
		if (url_base == null)
			return null;
		
		try {
//			url = new URL(url_base, medium.getFeature(Ident.class).getShortText() + "/" + attributeName + ".jpeg");
            url = getStorePosition();
			image = Toolkit.getDefaultToolkit().createImage(url);
			if (image != null)// && (image.getWidth(null) > 0))
				return image;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
		
	}
    
    
	
/*	private URL StdFile() {
		
		if (!isDirStorage())
			return null;

		URL location = getDirLocation();
		try {
			String loc_str = location.toString();
			if (!loc_str.endsWith("/"))
				loc_str = loc_str + "/";
			location = new URL( loc_str + medium.getFeature(Ident.class).getShortText() + "/" + attributeName + ".jpeg" );
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			return null;
		}
		
		try {
			URLConnection connection = location.openConnection();
			System.out.print(location);
			if (connection.getContentLength() > 0) {
				System.out.println(" exists.");
				return location;
			} else {
				System.out.println(" doesn't exists.");
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
				
	}*/
	
	private URL getImageLocationFromCatalog() {
		if (medium.entry.getAttribute(attributeName + "URL") == null)
			return null;
		try {
			return new URL(medium.entry.getAttribute(attributeName + "URL"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Searches for an entry in the database. If there is non
	 * and if isDirStorage is true. It looks for the image
	 * in the directory. If none is there it returns null.
     * 
	 * @return the location of the image for the feature or null if there is none
	 */
    public URL getImageURL() {
        ImageFeatureOption imageFeatureOption = getImageFeatureOption();
        Image image = null;

        URL url = getImageLocationFromCatalog();
        if (url != null)
            return url;
 
        // If there are no ImageFeatureOption => give up
        if (imageFeatureOption == null)
            return null;
        
        // If there is no Storage Dir => just give up
        if (!imageFeatureOption.isDirStorage())
            return null;
        URL url_base = imageFeatureOption.getDirLocation();
        
        // If my base URL is null => no chance
        if (url_base == null)
            return null;
        
        try {
/*            url = new URL(url_base, medium.getFeature(Ident.class).getShortText() + "/" + attributeName + ".jpeg");
            return url;*/
            return getStorePosition();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
        
    }
    
    /**
     * Searches for an image link in the database.
     * @return
     */
	private URL getImageLocation() {
		return getImageLocationFromCatalog();
	}

    /**
     * Put the image in the place it belongs
     * if this won't work store at least the url
     * 
     * @param url Source of the image
     */
    public void setImage(URL url) {
        ImageFeatureOption imageFeatureOption = getImageFeatureOption();
        
        // Wenn keine Option gesetzt sind (sollte nicht passieren)
        // dann setzte den Link
        if (imageFeatureOption == null) {
            setImageLocation(url);
            return;
        }

        // Wenn das Verzeichnis benutzt werden soll dann
        // versuche die Datei in das Verzeichnis zu speichern.
        if (imageFeatureOption.isDirStorage()) 
            if (storeImage(url))
                return;
        
        // Should not or could not store to directory
        setImageLocation(url);
    }

    /**
     * Store link to the image in catalog.
     * This should only be used if one know that the directory should not be used.
     * Or knows exactly what he's doing.
     * 
     * @param location
     */
    public void setImageLocation(URL location) {
		if (location == null) {
			medium.entry.setAttribute(attributeName + "URL", null);
            return;
        }

        medium.entry.setAttribute(attributeName + "URL", location.toString());
        ImageInformationServer.getDefaultImageInformationServer().addImage(location);
	}
	
    /**
     * 
     * This funtion stores an image into the imagedirectory.
     * For this to work, there has to be a image directory
     * selected and the option use image directory has to be true.
     * 
     * @param location Source of the image that shall be stored
     * 
     * @return true if the image has been stored in the image directory
     */
    public boolean storeImage(URL location) {
        ImageFeatureOption imageFeatureOption = getImageFeatureOption();
        // If there are no ImageFeatureOption => give up
        if (imageFeatureOption == null)
            return false;

        try {
            URL url = getStorePosition();
            if (url == null)
                return false;
            File outFile = new File(url.getFile());
            outFile.createNewFile();
            
            byte buffer[] = new byte[1024];
            int len = 0;
            
            
            InputStream iStream = location.openStream();
            FileOutputStream oStream = new FileOutputStream(outFile);
//            FileChannel input = new BufferedInputStream(iStream).getChannel();
//            FileChannel input = new FileInputStream(location.getFile()).getChannel();
//            FileChannel output = new FileOutputStream(outFile).getChannel();
//            output.transferFrom(input, 0, input.size());
            
            while ((len = iStream.read(buffer)) != -1)
                oStream.write(buffer, 0, len);
            
            iStream.close();
            oStream.close();
//            input.close();
//            output.close();
            
            ImageInformationServer.getDefaultImageInformationServer().addImage(url);
            
            return true;
        
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    public URL getStorePosition() throws MalformedURLException {
        URL url = new URL(getImageFeatureOption().getDirLocation(), medium.getFeature(Ident.class).getShortText() + "/" + attributeName + ".jpeg");
        Options.ensureDirectory(getImageFeatureOption().getDirLocation().getFile() + medium.getFeature(Ident.class).getShortText() + "/");
        
        return url;
    }
    
	public boolean hasOptions() {
		return true;
	}
	
	public JPanel getOptionPanel() {
		return new ImageFeatureOptionPanel((ImageFeatureOption)getFeatureOption());
	}

/*	public boolean isDirStorage() {
		Catalog catalog = medium.entry.getCatalog();
		Entry entry = catalog.getOption(optionsName + "_ImageOption");
		if (entry == null)
			return false;
        if (entry.getAttribute("isDirStorage") == null)
            return false;
		return entry.getAttribute("isDirStorage").compareToIgnoreCase("True") == 0;
	}
	
	public void setDirStorage(boolean dirStorage) {
		Catalog catalog = medium.entry.getCatalog();
		Entry entry = catalog.getOption(optionsName + "_ImageOption");
		if (entry == null)
			entry = catalog.createOption(optionsName + "_ImageOption");
		entry.setAttribute("isDirStorage", (dirStorage)?"True":"False");
	}

	public URL getDirLocation() {
		Catalog catalog = medium.entry.getCatalog();
        Entry entry = catalog.getOption(optionsName + "_ImageOption");
        if (entry == null || entry.getAttribute("DirLocation") == null)
            return null;
        try {
            return new URL(entry.getAttribute("DirLocation"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
	}
    
	public void setDirLocation(URL location) {
		Catalog catalog = medium.entry.getCatalog();
		Entry entry = catalog.getOption(optionsName + "_ImageOption");
		if (entry == null)
			entry = catalog.createOption(optionsName + "_ImageOption");
		entry.setAttribute("DirLocation", location.toString());
	}*/
	
	
	public boolean validate(String condition) throws BadCondition {
		return false;
	}

	public String getText() {
		return null;
	}

	public String getShortText() {
		return null;
	}

    public String getTextHTML(int availableWidth) {
        return "<img src=\"" + getImageURL() + "\">";
    }

    public String getShortTextHTML() {
        return null;
    }

	public void copyTo(Feature feature) {
		if (!(feature instanceof ImageFeature))
			return;
		
		((ImageFeature)feature).setImageLocation(getImageLocation());		
	}

    @Override
    public boolean hasValue() {
        ImageInformation info = ImageInformationServer.getDefaultImageInformationServer().getInfo(medium, getImageURL());
        return (info != null);
    }



}
