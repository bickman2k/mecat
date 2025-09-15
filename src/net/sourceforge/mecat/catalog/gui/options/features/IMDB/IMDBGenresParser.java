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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTML.*;

import static javax.swing.text.html.HTML.Tag.*;
import static javax.swing.text.html.HTML.Attribute.*;

import net.sourceforge.mecat.catalog.medium.hidden.BagImage;
import net.sourceforge.mecat.catalog.medium.hidden.Person;
import net.sourceforge.mecat.catalog.medium.hidden.Role;
import net.sourceforge.mecat.catalog.medium.features.impl.Actor;
import net.sourceforge.mecat.catalog.medium.features.impl.BagImageFeature;
import net.sourceforge.mecat.catalog.medium.features.impl.ExtraImagesBag;
import net.sourceforge.mecat.catalog.medium.features.impl.Genre;
import net.sourceforge.mecat.catalog.medium.features.impl.Roles;
import net.sourceforge.mecat.catalog.medium.features.impl.Runtime;
import net.sourceforge.mecat.catalog.medium.features.impl.Title;
import net.sourceforge.mecat.catalog.medium.features.impl.Year;
import net.sourceforge.mecat.catalog.medium.features.option.impl.IMDBFeatureOption;
import net.sourceforge.mecat.catalog.medium.features.person.IMDBPersonNumber;
import net.sourceforge.mecat.catalog.medium.features.person.Name;
import net.sourceforge.mecat.catalog.medium.hidden.Movie;
import net.sourceforge.mecat.catalog.option.Options;

public class IMDBGenresParser extends HTMLEditorKit.ParserCallback {

	boolean genreFound = false;
    boolean posterFound = false;
	boolean titleTag = false;
	final Movie movie;
    final IMDBFeatureOption imdbFeatureOption;
    

	final boolean searchForYear;
	final boolean searchForGenres;
	final boolean searchForTitle;
	final boolean searchForAkas;
    final boolean searchForFrontImageLnk;
    final boolean searchForActors;
    final boolean searchForRuntime;

    // Look out for B-Tags to find akas
//    boolean withinB = false;
    boolean withinH5 = false;
    boolean aka = false;

    boolean cast = false;
    /**
     * 0 : at the beginning of a role entry
     * 1 : at the image
     * 2 : at the name of the actor
     * 3 : at the "..."
     * 4 : at the name of the role
     */
    int castPos = -1;
    boolean castInTD = false;
    boolean castNameHrefOpen = false;
//    Role role = null;
    
    int actorId = -1;
    String actorName = null;
    String roleName = null;
    
    
    // Found a <b ...> Runtime: </b>
    boolean foundRuntime = false;
    
    static Set<String> castTitles = new LinkedHashSet<String>(){{
        add("Cast overview");
        add("Complete credited cast");
        add("Credited cast");
        add("Series Cast");
        add("Series Credited cast");
    }};
    
    public IMDBGenresParser(final Movie movie, final IMDBFeatureOption imdbFeatureOption) {
		this.movie = movie;
        this.imdbFeatureOption = imdbFeatureOption;
		searchForYear = true;
		searchForGenres = true;
		searchForTitle = true;
        searchForAkas = true;
        searchForFrontImageLnk = true;
        searchForActors = true;
        searchForRuntime = true;
	}
	
	public IMDBGenresParser(final Movie movie, final IMDBFeatureOption imdbFeatureOption, 
            final boolean searchForYear, final boolean searchForGenres, 
            final boolean searchForTitle, final boolean searchForAkas,
            final boolean searchForFrontImageLnk, final boolean searchForActors,
            final boolean searchForRuntime) {
		this.movie = movie;
        this.imdbFeatureOption = imdbFeatureOption;
		this.searchForYear = searchForYear;
		this.searchForGenres = searchForGenres;
		this.searchForTitle = searchForTitle;
        this.searchForAkas = searchForAkas;
        this.searchForFrontImageLnk = searchForFrontImageLnk;
        this.searchForActors = searchForActors;
        this.searchForRuntime = searchForRuntime;
        
        if (searchForGenres) 
            movie.getFeature(Genre.class).clear();
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
		if (tag == Tag.A) {
			genreFound = false;
            posterFound = false;
            castNameHrefOpen = false;
        }
		if (tag == Tag.TITLE)
			titleTag = false;
//        if (tag == Tag.B)
//            withinB = false;
        if (tag == Tag.H5)
            withinH5 = false;
        if (tag == Tag.TR) {
            if (searchForActors) {
                if (actorId != -1 && castPos >= 2) 
                    movie.getFeature(Roles.class).addRole(actorId, actorName, roleName);

                actorId = -1;
                actorName = null;
                roleName = null;
            }
            castPos = -1;
        }
        if (tag == Tag.TD)
            castInTD = false;
	}
	public void handleError(String arg0, int position) {
	}
	public void handleSimpleTag(Tag tag, MutableAttributeSet attributes, int position) {
        if (tag == Tag.HR) {
            cast = false;
        }
            
        if (tag == IMG) {
            String src = attributes.getAttribute(SRC).toString();
            // Need only to work with images that hava an src attribute
            if (src != null && src.length() > 0) {
                if (src.endsWith("header_cast.gif"))
                    cast = true;
                else if (posterFound) {
                    src = src.replaceAll("m\\.jpg", "f.jpg");

                    URL url = null;
                    try {
                        url = new URL(src);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }


                    if (url != null && searchForFrontImageLnk) {
                        BagImage bagImage = movie.getFeature(ExtraImagesBag.class).createBagImage();
                        bagImage.getFeature(BagImageFeature.class).setImageLocation(url);
                    }
                }
                
            }
        }
	}
	public void handleStartTag(Tag tag, MutableAttributeSet attributes, int position) {
		if (((tag == A) && attributes.getAttribute(HREF) != null)
			 && (attributes.getAttribute(HREF).toString().indexOf("/Sections/Genres/") != -1)) {
			genreFound = true;
		}
        if (tag == A && attributes.getAttribute(NAME) != null 
                && attributes.getAttribute(NAME).toString().equalsIgnoreCase("poster"))
            posterFound = true;
		if (tag == Tag.TITLE)
			titleTag = true;
//        if (tag == Tag.B) 
//            withinB = true;
        if (tag == Tag.H5) 
            withinH5 = true;
        if (cast && tag == Tag.TR) {
            castPos = 0;
        }
        if (cast && tag == Tag.TD) {
            castPos++;
            castInTD = true;
        }
        if (searchForActors) 
            if (((tag == A) && attributes.getAttribute(HREF) != null) && castInTD && castPos == 2) {
//                role = movie.getFeature(Roles.class).newMedium().getMedium();
                String href = attributes.getAttribute(HREF).toString();
                String split[] = href.split("/");
                if (split.length > 1) {
                    String nameId = split[split.length - 1];
                    // Remove nm
                    if (nameId.length() > 2) {
                        nameId = nameId.substring(2);
                        try {
                            actorId = Integer.valueOf(nameId);
//                            Person actor = role.getFeature(Actor.class).getSubEntryMedium();
//                            actor.getFeature(IMDBPersonNumber.class).set(id);
                        } catch (Exception e) {
                            System.err.println(href);
                            e.printStackTrace();
                        }
                    }
                }
                castNameHrefOpen = true;
            }
	}
	public void handleText(char[] text, int position) {
        String txt = new String(text);
		if (genreFound && searchForGenres)
			movie.getFeature(Genre.class).add(txt.trim());
		if (titleTag && searchForYear) {
			Integer i = IMDBTitleSearchParser.findYear(txt);
			if (i != null)
				movie.getFeature(Year.class).set(i);
		}
		if (titleTag && searchForTitle) {
			String split[] = txt.split("\\(");
//			movie.getFeature(Title.class).set(split[0].trim(), Locale.ENGLISH);
            addTitle(Locale.ENGLISH, split[0].trim());
		}
        // If within a H5 Tag there is Also Known As
        // then the following text may contain akas
        // If there is a B Tag without Also Known As
        // every aka list that have begun before is
        // at an end.
        if (withinH5) {
            aka = txt.contains("Also Known As");
// After change on imdb this will no longer work            
// The cast has moved            
//            cast = false;
//            for (String title : castTitles) 
//                if (txt.toLowerCase().startsWith(title.toLowerCase()))
//                    cast = true;
            foundRuntime = txt.contains("Runtime");
        }

        if (aka) {
            if (Options.DEBUG)
                System.out.print("Aka >>" + txt + "<<");

            String split[] = txt.split("(.*\\[)|(\\].*)");
            if (split.length == 2) {
                Locale l = new Locale(split[1]);
                if (Options.DEBUG)
                    System.out.println(" >>" + new Locale(split[1]) + "<<");
                // Get the part before the first ( or [ and cut of all unnecessary spaces
                if (searchForAkas && !l.equals(Locale.ENGLISH)) {
                    if (Options.DEBUG)
                        System.out.println("Add aka(" + l + ") " + txt.split("\\(|\\[")[0].trim());
                    addTitle(l, txt.split("\\(|\\[")[0].trim());
                }
            }
            else
                if (Options.DEBUG)
                    System.out.println();
        }
        
        if (foundRuntime && txt.contains("min") && searchForRuntime) {
            // Cut the min
            String split[] = txt.split("min");
            String start = split[0].trim();
            // Find the first position that is part of the number
            int pos = start.length();
            while (pos > 0 && start.charAt(pos - 1) >= '0' && start.charAt(pos - 1) <= '9')
                pos--;
            // Build runtime String
            if (pos < start.length()) {
                String runtimeStr = start.substring(pos);
                try {
                    movie.getFeature(Runtime.class).set(Integer.parseInt(runtimeStr));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        else if (foundRuntime && txt.contains("min")) {
            System.out.println("Mins " + txt.contains("min"));
            System.out.println("Search " + searchForRuntime);
            (new Exception()).printStackTrace();
        }

        if (searchForActors)
            if (castNameHrefOpen) {
//                Person actor = role.getFeature(Actor.class).getSubEntryMedium();
//                actor.getFeature(Name.class).set(txt);
                actorName = txt;
            }
        
        if (searchForActors) 
            if (cast && castInTD && castPos == 4)
//                role.getFeature(Name.class).set(txt);
                roleName = txt;
	}
    
    final static String[] artikel = {
            // Bestimmte Artikel
            "der", "die", "das", "des", "dem", "den",
            // Unbestimmte Artikel
            "ein", "eine", "einer", "eines", "einem", "einen"
    };

    public static String title(String title, IMDBFeatureOption imdbFeatureOption) {
        if (!imdbFeatureOption.removeQuotes)
            return title;
        
        if (title.startsWith("\"") && title.endsWith("\""))
            return title(title.substring(1, title.length() - 1), imdbFeatureOption);

        if (title.startsWith("\'") && title.endsWith("\'"))
            return title(title.substring(1, title.length() - 1), imdbFeatureOption);
        
        return title;
    }
    
    public static String germanTitle(String orgTitle, IMDBFeatureOption imdbFeatureOption) {
        String title = title(orgTitle, imdbFeatureOption);
        
        if (!imdbFeatureOption.articleReposition)
            return title;
        
        String split[] = title.split(","); 
        if (split.length < 2)
            return title;
        
        String moeglicherArtikel = split[split.length - 1].trim(); 

        for (String art : artikel) 
            if (moeglicherArtikel.equalsIgnoreCase(art)) 
                return moeglicherArtikel + " " + title.substring(0, title.lastIndexOf(",")).trim();
        
        return title;
    }
    
    public static void addTitle(Movie movie, IMDBFeatureOption imdbFeatureOption, Locale l, String title) {

        if (l.equals(Locale.GERMAN)) {
            movie.getFeature(Title.class).set(germanTitle(title, imdbFeatureOption), l);
            return;
        }
            
        
        movie.getFeature(Title.class).set(title(title, imdbFeatureOption), l);
    }


    protected void addTitle(Locale l, String title) {
        addTitle(movie, imdbFeatureOption, l, title);
    }

}
