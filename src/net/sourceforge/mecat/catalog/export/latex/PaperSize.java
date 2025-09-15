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
 * Created on Jun 23, 2005
 * @author Stephan Richard Palm
 */
package net.sourceforge.mecat.catalog.export.latex;

import java.util.HashMap;
import java.util.Map;

import javax.print.attribute.standard.MediaSizeName;

public enum PaperSize {
    a0("a0paper", MediaSizeName.ISO_A0,841, 1189),
	a1("a1paper", MediaSizeName.ISO_A1,594, 841),
	a2("a2paper", MediaSizeName.ISO_A2,420, 594),
    a3("a3paper", MediaSizeName.ISO_A3,297, 420),
	a4("a4paper", MediaSizeName.ISO_A4,210, 297, true),
	a5("a5paper", MediaSizeName.ISO_A5,148, 210, true),
	a6("a6paper", MediaSizeName.ISO_A6,105, 148),
	a7("a7paper", MediaSizeName.ISO_A7,74, 105),
	a8("a8paper", MediaSizeName.ISO_A8,52, 74),
	a9("a9paper", MediaSizeName.ISO_A9,37, 52),
	a10("a10paper", MediaSizeName.ISO_A10,26, 37),
	b0("b0paper", MediaSizeName.ISO_B0,1000, 1414),
	b1("b1paper", MediaSizeName.ISO_B1,707, 1000),
	b2("b2paper", MediaSizeName.ISO_B2,500, 353),
	b3("b3paper", MediaSizeName.ISO_B3,353, 250),
	b4("b4paper", MediaSizeName.ISO_B4,250, 353),
	b5("b5paper", MediaSizeName.ISO_B5,176, 250, true),
	b6("b6paper", MediaSizeName.ISO_B6,125, 176),
	b7("b7paper", MediaSizeName.ISO_B7,88, 125),
	b8("b8paper", MediaSizeName.ISO_B8,62, 88),
	b9("b9paper", MediaSizeName.ISO_B9,44, 62),
	b10("b10paper", MediaSizeName.ISO_B10,31, 22),
	c0("c0paper", MediaSizeName.ISO_C0,917, 1297),
	c1("c1paper", MediaSizeName.ISO_C1,648, 917),
	c2("c2paper", MediaSizeName.ISO_C2,458, 648),
	c3("c3paper", MediaSizeName.ISO_C3,324, 458),
	c4("c4paper", MediaSizeName.ISO_C4,229, 324),
	c5("c5paper", MediaSizeName.ISO_C5,162, 229),
	c6("c6paper", MediaSizeName.ISO_C6,114, 162),
//	c7("c7paper", MediaSizeName.ISO_C7, 81, 114),
//	c8("c8paper", MediaSizeName.ISO_C8,57, 81),
//	c9("c9paper", MediaSizeName.ISO_C9,40, 57),
//	c10("c10paper", MediaSizeName.ISO_C10,28, 40),
	letter("letterpaper", MediaSizeName.NA_LETTER,8.5, 11, true, PaperSizeUnit.inches),
	legal("legalpaper", MediaSizeName.NA_LEGAL,8.5, 14, true, PaperSizeUnit.inches),
	executive("executivepaper", MediaSizeName.EXECUTIVE,7.25, 10.5, true, PaperSizeUnit.inches);
	
	public final String LaTeX;
	public final double height, width;
	public final boolean LaTeX_known;
	public final PaperSizeUnit unit;
    public final MediaSizeName mediaSizeName;
    static Map<String, PaperSize> mapForNames;// = new HashMap<String, PaperSize>();
	
	PaperSize(final String LaTeX, final MediaSizeName mediaSizeName, final double width, final double height) {
		this(LaTeX, mediaSizeName, width, height, false, PaperSizeUnit.millimeter);
	}
	
	PaperSize(final String LaTeX, final MediaSizeName mediaSizeName, final double width, final double height, final boolean LaTeX_known) {
		this(LaTeX, mediaSizeName, width, height, LaTeX_known, PaperSizeUnit.millimeter);
	}
	PaperSize(final String LaTeX, final MediaSizeName mediaSizeName, final double width, final double height, final PaperSizeUnit unit) {
		this(LaTeX, mediaSizeName, width, height, false, unit);
	}
	PaperSize(final String LaTeX, final MediaSizeName mediaSizeName, final double width, final double height, final boolean LaTeX_known, final PaperSizeUnit unit) {
        this.mediaSizeName = mediaSizeName;
		this.LaTeX = LaTeX;
		this.height = height;
		this.width = width;
		this.LaTeX_known = LaTeX_known;
		this.unit = unit;
	}
	
	public String toString(){
		return LaTeX;
	}

	
	/**
	 * If we want to start direct at the begin of the page without marging then
	 * we take a margin of -1 inch. This will be done at the LaTeXExport.
	 * @return
	 */
	public double getOddSideMargin() {
		return width / 10;
	}
	public double getEvenSideMargin() {
		return getOddSideMargin();
	}
	public double getTopMargin() {
		return getOddSideMargin();
	}
	public double getHeadHeight() {
		return 0;
	}
	public double getHeadSep() {
		return 0;
	}
	public double getTopSkip() {
		return 0;
	}
	public double getTextHeight() {
		return height - getTopMargin() * 2;
	}
	public double getTextWidth() {
		return width - getOddSideMargin() * 2;
	}
	public double getFootSkip() {
		return 0;
	}
	public double getPaperHeight() {
		return height;
	}
	public double getPaperWidth() {
		return width;
	}

    public static PaperSize getPaperSizeForName(final String attribute) {
        if (mapForNames == null) {
            mapForNames = new HashMap<String, PaperSize>(values().length);
            for (PaperSize value : values())
                mapForNames.put(value.LaTeX, value);
        }

        return mapForNames.get(attribute);
    }


    public double getHeightInInches() {
        switch (unit) {
        case inches:
            return height;
        case millimeter:
            return height / 25.4;
        default:
            return 0;
        }
    }
    public double getWidthInInches() {
        switch (unit) {
        case inches:
            return width;
        case millimeter:
            return width / 25.4;
        default:
            return 0;
        }
    }
}
