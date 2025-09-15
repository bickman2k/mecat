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
 * Created on Jun 15, 2005
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.gui.options.features.IMDB;

import static javax.swing.text.html.HTML.Attribute.CLASS;
import static javax.swing.text.html.HTML.Tag.P;

import java.util.Locale;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTML.Tag;

import net.sourceforge.mecat.catalog.medium.features.impl.Description;
import net.sourceforge.mecat.catalog.medium.hidden.Movie;

public class IMDBPlotParser extends HTMLEditorKit.ParserCallback {

	int plotTagOpen = 0;
	
	final Movie movie;
	public IMDBPlotParser(final Movie movie) {
		this.movie = movie;
	}
	
	public void flush() {
	}
	public void handleComment(char[] arg0, int position) {
	}
	public void handleEndOfLineString(String arg0) {
	}
	public void handleEndTag(Tag tag, int position) {
		if (tag == Tag.HTML)
			flush();
		if (plotTagOpen > 0 && tag == P)
			plotTagOpen--;
	}
	public void handleError(String arg0, int position) {
	}
	public void handleSimpleTag(Tag tag, MutableAttributeSet arg1, int position) {
	}
	public void handleStartTag(Tag tag, MutableAttributeSet attributes, int position) {
		if (((tag == P) && attributes.getAttribute(CLASS) != null)
			 && (attributes.getAttribute(CLASS).toString().equalsIgnoreCase("plotpar"))) {
			plotTagOpen = 2;
		}
	}
	public void handleText(char[] text, int position) {
		if (plotTagOpen > 0)
			if (movie.getFeature(Description.class).get(Locale.ENGLISH) == null)
				movie.getFeature(Description.class).set(String.valueOf(text), Locale.ENGLISH);
			else 
				movie.getFeature(Description.class).set(movie.getFeature(Description.class).get(Locale.ENGLISH) + System.getProperty("line.separator") + String.valueOf(text), Locale.ENGLISH);
	}
}
