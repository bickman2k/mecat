/*
 *  MeCat - Media Catalog more information on
 *  http://mecat.sourceforge.net/
 *  Copyright (C) 2005, Stephan Richard Palm, All Rights Reserved.
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
 * Created on Jan 4, 2005
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.gui;

import net.sourceforge.mecat.catalog.medium.Listing;
import net.sourceforge.mecat.catalog.medium.Medium;
import net.sourceforge.mecat.catalog.medium.features.ImageFeature;
import net.sourceforge.mecat.catalog.medium.features.impl.Location;
import net.sourceforge.mecat.catalog.medium.features.impl.Position;
import net.sourceforge.mecat.catalog.sort.ByNumberFromTextFeature;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * 
 * Created on Nov 29, 2006
 *
 * @author Stephan Richard Palm
 *
 * @deprecated
 * 
 * replaced by ImageDesktop
 */
public class ImageDisplay extends Canvas implements ListDataListener/*, ImageObserver*/ {

	private class LocationImage extends Canvas implements Runnable {
		final int location;
		private boolean loading;
		
		MediaTracker tracker;

		int current_height;
		
		public LocationImage(int location){
			this.location = location;
		}

		public void run() {
			try {
				while (true) {
					while (loading) {
						repaint();
						wait(1000);
					}
					repaint();
					wait();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void paint(Graphics g) {
			loading = (tracker.checkAll());
			
			
		}

		public void changeInLocation() {
			loading = true;
			notify();
		}
		
		
	}
	
	private class BlankImage extends Image {

		final Medium medium;
		public /*Buffered*/Image image = null;
		
		public BlankImage(Medium medium) {
			this.medium = medium;
		}
		
		public int getWidth(ImageObserver arg0) {
			return getImageWidth(getLocation(medium));
		}

		public int getHeight(ImageObserver arg0) {
/*			int height = getImageHeight(getLocation(medium));
			if (height == 0)
				return getWidth(arg0);
			return height;*/
			return getImageHeightMax(getLocation(medium));
		}

		public ImageProducer getSource() {
			return null;
		}

		public Graphics getGraphics() {
			if (image == null)
				buildAll();
			return image.getGraphics();
		}
		
		public Image getImage() {
			// If the image does not yet exists
			// then build it
			if (image == null)
				buildAll();

			return image;
		}

		private void createScaleImage() {
			ImageFeature imageFeature = (ImageFeature)medium.getFeature(ImageFeature.class);
			
			image = imageFeature.getImage().getScaledInstance(getImageWidth(getLocation(medium)), -1, Image.SCALE_DEFAULT);
			
			mediaTracker.addImage(image, 1);
		}
		
		private void createBlankImage() {
			image = new BufferedImage(getWidth(null), getHeight(null), BufferedImage.SCALE_DEFAULT);
			Graphics g = image.getGraphics();
			g.setColor(Color.YELLOW);
			g.fillRect(0,0,buffer.getWidth(), buffer.getHeight());
			g.setColor(Color.BLACK);
			g.drawString(medium.toString(), 0, 0);
		}
		
		public Object getProperty(String arg0, ImageObserver arg1) {
			return null;
		}

		public void flush() {
		
		}
		
		public void paint(Graphics g) {
			g.drawImage(image, 0, 0, null);
		}
		
		public void rebuild() {
			image = null;
		}
		
		public void buildScaled() {
			ImageFeature imageFeature = (ImageFeature)medium.getFeature(ImageFeature.class);

			if (image == null)
				if ((imageFeature != null) && (imageFeature.getImage() != null))
					createScaleImage();
		}
		
		public void buildAll() {
			ImageFeature imageFeature = (ImageFeature)medium.getFeature(ImageFeature.class);

			if (image == null)
				if ((imageFeature == null) || (imageFeature.getImage() == null))
					createBlankImage();
				else
					createScaleImage();
		}
		
	}
	
	static final int vertical_space = 3;
	static final int horizontal_space = 3;
	static final Comparator<Medium> byPosition = new ByNumberFromTextFeature(Position.class);

	BufferedImage buffer = null;
	
	/**
	 * The listing where the mediums are from that will be shown.
	 */
	final Listing listing;

	/**
	 * For every medium in the "listing" here should be a map
	 * to an BlankImage that will return an image at the right size
	 * for drawing when invoked with getImage().
	 */
	final Map<Medium, BlankImage> images = new HashMap<Medium, BlankImage>();

	/**
	 * Every Location in the listing should map to a SortedSet of all Medium at this location.
	 * All Mediums that are in no location or don't have the location feature are hold in the location -1
	 */
	final Map<String, SortedSet<Medium>> locations = new HashMap<String, SortedSet<Medium>>();
	
	final Map<String, IncrementalStats> locationWidth = new HashMap<String, IncrementalStats>();
	final Map<String, IncrementalStats> locationHeight = new HashMap<String, IncrementalStats>();

	Map<String, Integer> positionsY = null;//new HashMap<Integer, Integer>();

	/**
	 * Gets all images that have to be loaded before a paint will work
	 */
	final MediaTracker mediaTracker;

	/**
	 * In this set there is a list of all locations that have changed
	 * and need to be rebuild before paint
	 */
	final Set<String> dirtyLocations = new TreeSet<String>();
	/**
	 * In this set there is al list of all images that have changed
	 * and need to be rebuild before paint
	 */
	final Set<Medium> dirtyImages = new HashSet<Medium>();
	
	final Set<Medium> unBlankedImages = new HashSet<Medium>();

	public ImageDisplay(Listing listing) {
		this.listing = listing;
//		setSize(new Dimension(500,500));
		setPreferredSize(new Dimension(500,500));
		
		mediaTracker = new MediaTracker(this);
		
		contentsChanged(new ListDataEvent(listing, ListDataEvent.CONTENTS_CHANGED, 0, listing.getSize()));
	}
    
	public void intervalAdded(ListDataEvent event) {
		rebuildWidth();
		
		// In case someone does something stupid
		if (event.getSource() != listing)
			return;
		
		// repair the map
		for (int i = event.getIndex0(); i < event.getIndex1(); i++)
//			images.put(listing.getElementAt(i), makeRightSizeImage(listing.getElementAt(i)));
			addMedium(listing.getElementAt(i));
		
		rebuildDirtyLocations();
		rebuildDirtyImages();
	}
	public void intervalRemoved(ListDataEvent event) {
		rebuildWidth();
		
		// In case someone does something stupid
		if (event.getSource() != listing)
			return;
		
		// So what
		removeUnnesseccaryImageKeys();

		rebuildDirtyLocations();
		rebuildDirtyImages();
	}
	public void contentsChanged(ListDataEvent event) {
		rebuildWidth();
		
		// In case someone does something stupid
		if (event.getSource() != listing)
			return;

		// Remake the map
		for (int i = event.getIndex0(); i < event.getIndex1(); i++) 
			addMedium(listing.getElementAt(i));

		removeUnnesseccaryImageKeys();
		controleImageMap();

		rebuildDirtyLocations();
		rebuildDirtyImages();
	}
	private synchronized void rebuildDirtyLocations() {
		for (String i : dirtyLocations)
			for (Medium medium : locations.get(i)) 
				dirtyImages.add(medium);
		
		dirtyLocations.clear();
	}
	private synchronized void rebuildDirtyImages() {
		for (Medium medium : dirtyImages) {
			images.get(medium).rebuild();
			images.get(medium).buildScaled();
			unBlankedImages.add(medium);
		}
		dirtyImages.clear();
	}
	private synchronized void addMedium(Medium medium) {
		String location = getLocation(medium);

		SortedSet<Medium> media = locations.get(location);
		if (media == null)
			locations.put(location, media = new TreeSet<Medium>(byPosition));

		// If the medium allready exists, then only rebuild it
		if (media.contains(medium)) 
			dirtyImages.add(medium);
		else {
			media.add(medium);
			images.put(medium, new BlankImage(medium));
		}
		
		// Rebuild the locations where Mediums are added
		dirtyLocations.add(location);
	}
	private synchronized void removeMedium(Medium medium) {
		images.remove(medium);
		String location = getLocation(medium);
		SortedSet<Medium> media = locations.get(location);
		media.remove(medium);
		if (media.size() == 0)
			locations.remove(location);

		// Rebuild the locations where Mediums are removed
		dirtyLocations.add(location);
	}
	private void removeUnnesseccaryImageKeys() {
		Set<Medium> mediums = images.keySet();
		for (Medium medium : mediums)
			if (!listing.exists(medium)) 
				removeMedium(medium);
	}
	private void setBlanksHeigth(){
/*		for (Image image : blanks.keySet())
			image.*/
	}
	private void controleImageMap(){
		if (listing.getSize() != images.keySet().size())
			remakeImageMap();
	}
	private void remakeImageMap() {
		System.err.println("Have to rebuild ImageMap. Why?");
		
		// Clear the map
		images.clear();
		
		// Remake the map
		for (Medium medium : listing)
			images.put(medium, new BlankImage(medium));
		
	
	}
	private void resetTracker() {
		positionsY = null;
		buffer = null;
		
		for (Medium medium : listing)
			if (!(images.get(medium) instanceof BlankImage))
				mediaTracker.addImage(images.get(medium), 0);
		
	}
	public boolean areImagesReady() {
		return (mediaTracker.checkAll());
	}
	private void createBufferedImage() {
		// In case there is no need
		if (buffer != null)
			return;

		if (!areImagesReady())
			return;
		
		if (positionsY == null)
			rebuildPositionsY();
		
		String max = Collections.max(positionsY.keySet());
		
		buffer = new BufferedImage(getWidth(), positionsY.get(max) + getImageHeight(max), BufferedImage.TYPE_INT_RGB);
		Graphics g = buffer.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0,0,buffer.getWidth(), buffer.getHeight());
		g.setColor(Color.BLACK);
		
		for (String i : new TreeSet<String>(locations.keySet())) {
//			Vector<Medium> media = new Vector<Medium>(locations.get(i));
			int width = getImageWidth(i) + vertical_space;
			int X = 0;
			int Y = positionsY.get(i);
			
			for (Medium medium : locations.get(i)) {
				Image image = images.get(medium).getImage();
				int position = getPosition(medium);
				
				System.out.println(medium.toString());
				System.out.println("Width " + image.getWidth(null) + " and Height " + image.getHeight(null));
				System.out.println("X " + X + " and Y " + Y);

				if (image.getWidth(null) > 0)
					if (image.getHeight(null) > 0)
						if (!g.drawImage(image, X, Y, null))
							System.out.println("Couldn't draw image for "+ medium);

				X += width;
			}
		}
		
		
		
		try {
			ImageIO.write(buffer, "png", new File("buffer.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}	

		setPreferredSize(new Dimension(buffer.getWidth(), buffer.getHeight()));
	}
	public void paint(Graphics g) {
		if (buffer == null)
			createBufferedImage();
		g.drawImage(buffer, 0,0, null);
	}
	private void rebuildPositionsY() {
		positionsY = new HashMap<String, Integer>();
		
		int actPos = 0;
		
		for (String i : new TreeSet<String>(locationHeight.keySet())) {
			positionsY.put(i, actPos);
			actPos += getImageHeightMax(i) + horizontal_space;
		}
			
	}
	protected class IncrementalStats {
		int number = 0;
		int extraCount = 0;
		int sum = 0;
		int max = -1;
		int min = -1;
		
		public void addExtra() {
			extraCount++;
		}
		
		public int getNumPlusExtra(){
			return number + extraCount;
		}
		
		public int getNum() {
			return number;
		}
		
		public void add(int Value) {
			if (number == 0) {
				min = Value;
				max = Value;
			}
			
			if (Value < min)
				min = Value;
			
			if (Value > max)
				max = Value;
			
			sum += Value;
			number++;
		}
		
		public int average() {
			if (number == 0)
				return 0;
			
			return sum / number;
		}
		
		public int max() {
			return max;
		}
		
		public int min() {
			return min;
		}
	}
	private void rebuildWidth() {
		locationWidth.clear();
		
		IncrementalStats wStat;
		IncrementalStats hStat;

		
		for(Medium medium : listing) {
			
			String location = getLocation(medium);

			if (locationWidth.get(location) == null) 
				locationWidth.put(location, wStat = new IncrementalStats());
			else 
				wStat = locationWidth.get(location);
			
			wStat.addExtra();
		}
			
	}
	private void rebuildHeight() {
		locationHeight.clear();
		
		IncrementalStats hStat;

		
		for(Medium medium : listing) {
			
			String location = getLocation(medium);

			if (locationHeight.get(location) == null) 
				locationHeight.put(location, hStat = new IncrementalStats());
			else 
				hStat = locationHeight.get(location);
			
			
			Image image = images.get(medium);
			
			if (image instanceof BlankImage) 
				hStat.addExtra();
			else 
				hStat.add(image.getHeight(null) * getImageWidth(location) / image.getWidth(null));
			
		}
	}
	protected int getImageWidth(String location) {
		int num = locations.get(location).size();
//		int num = locationWidth.get(location).getNumPlusExtra();
		return (getWidth() - (num - 1)*vertical_space) / num;
	}
	protected int getImageHeight(String location) {
		return locationWidth.get(location).average();
	}
	protected int getImageHeightMax(String location) {
		int max = locationWidth.get(location).max();
		if (max > 0)
			return max;
		else
			return getImageWidth(location) * 3;
	}
	protected String getLocation(Medium medium) {
		Location locationFeature = (Location)medium.getFeature(Location.class);

		if (locationFeature == null)
			return null;
		else
			return locationFeature.get();
	}
	protected int getPosition(Medium medium) {
		Position positionFeature = (Position)medium.getFeature(Position.class);

		try {
			if (positionFeature == null)
				return -1;
			else
				return Integer.valueOf(positionFeature.get());
		} catch (Exception e) {
			return -1;
		}
	}
}
