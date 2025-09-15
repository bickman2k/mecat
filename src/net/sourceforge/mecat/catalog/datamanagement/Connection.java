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
 *  
 *  Created on Aug 11, 2004
 *  @author Stephan Richard Palm
 */

/**  
 *  toString, compareTo and equals have to be implemented for Connection
 *  toString will be used for visualisation within the GUI.
 *  compareTo is used in order to see if two connections are the same
 */
package net.sourceforge.mecat.catalog.datamanagement;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

public abstract class Connection implements PersistentThroughEntry, Comparable {

	
	// Tag for the value stored in Position
	final String Entry_Tag_For_Position = "CONNECTION_POSTION";
	
	// Gives the actual position in the catalog that this connection represents
	public int Position = -1;

    // This function returns a CatalogFactory that is able to 
    // use this Connection.
	public abstract CatalogFactory getCatalogFactory();
	

    /**
     * Restores a connection from an Entry.
     * @return true if the connection could be restored from the entry
     */
	public boolean loadFromEntry(Entry con) {
		// Look if there actualy is an entry given
		if (con == null)
			return false;
		
		// Try to acquire the Attributes String holding the Position
		String val = con.getAttribute(Entry_Tag_For_Position);
		if (val == null)
			return false;
		
		try {
			// Cast the value to int
			Position = Integer.valueOf(val).intValue();
			return true;
		} catch (Exception e){
			return false;
		}
	}

    /**
     * This function saves the connection to an entry.
     * @param con the Entry that will hold the information about the connection
     */
	public void saveToEntry(Entry con) {
		con.setAttribute(Entry_Tag_For_Position, String.valueOf(Position));
	}
	
    
    /**
     * This function returns a String that will fit in
     * the given Graphics with the width given by the parameter width.
     * 
     * @param width The width in pixel, that the String can have
     * @return a String that fits in the given width
     */
    public abstract String getNameCutToSize(int width, Graphics g);

    /**
     * Find a String that fits in the graphic "g" with width "width"
     * 
     * Things that will be done to abbreviate the the Strings.
     * 1. Try if both string fit.
     * 2. Abbreviate first string with "..." and show second in full
     * 3. Only show second String
     * 4. Abbreviate the second String
     * 5. Show ...
     * 6. Empty String
     * 
     * @param width
     * @param first The first part of the string that can be abbrivated
     * @param second The second part of the string that can not be abbrivated
     * @return A String with abbrivated "abr" + ... + "rest"
     */
    public static String abbreviation(int width, String first, String second, Graphics g) {
        JPanel tmp = new JPanel();
        
        FontMetrics metrics = g.getFontMetrics();
        
        Rectangle2D all = metrics.getStringBounds(first + second, g);
        if (all.getWidth() < width)
            return first + second;
        
        String restPoints = "..." + second;
        Rectangle2D restWithPoints = metrics.getStringBounds(restPoints, g);
        
        if (restWithPoints.getWidth() > width)
            if (metrics.getStringBounds("...", g).getWidth() > width)
                return "";
            else
                return abbreviation(width, second, "", g);
        
        Double currentWidth = restWithPoints.getWidth();
        int i = 0;
        for (i = 0; i < first.length() && currentWidth < width; i++)
            currentWidth += metrics.charWidth(first.charAt(i));
        
        // TODO work with faster CharacterSequence
        if (metrics.getStringBounds(first.substring(0, i) + restPoints, g).getWidth() < width) {
            // i should not reach the end of the array because of the if (all.getWidht() < width) 
            // condition above
            while (metrics.getStringBounds(first.substring(0, (i + 1)) + restPoints, g).getWidth() < width)
                i++;
        } else {
            while (metrics.getStringBounds(first.substring(0, i) + restPoints, g).getWidth() > width)
                i--;
        }
        return first.substring(0, i) + restPoints;
    }
}
