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
 * Created on Aug 12, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filesystem.mime.jpg.ifd.makerNote;

import java.awt.Color;
import java.awt.color.ColorSpace;
import java.text.Format;
import java.text.NumberFormat;

import net.sourceforge.mecat.catalog.datamanagement.Catalog;
import net.sourceforge.mecat.catalog.filesystem.mime.jpg.ifd.IFD_Enum;
import net.sourceforge.mecat.catalog.filesystem.mime.jpg.ifd.IFD_Type;
import net.sourceforge.mecat.catalog.filesystem.mime.jpg.ifd.IFD_Value;
import net.sourceforge.mecat.catalog.filesystem.mime.jpg.ifd.TagIdentifier;
import net.sourceforge.mecat.catalog.i18n.fromCatalog.CatalogResource;
import net.sourceforge.mecat.catalog.option.Options;

public enum Canon implements IFD_Enum {
    //////////////////////
    // http://park2.wakwak.com/~tsuruzoh/Computer/Digicams/exif-e.html
//  Unknown                  (0x0000, IFD_Type.SHORT,      6),
    MacroFlashFocusEtc       (0x0001, IFD_Type.SHORT,      -1),
//  Unknown                  (0x0003, IFD_Type.SHORT,      4),
    WhiteBalanceFlashEtc     (0x0004, IFD_Type.SHORT,      -1),
    ImageType                (0x0006, IFD_Type.ASCII,      32),
    FirmwareVersion          (0x0007, IFD_Type.ASCII,      24),
    ImageNumber              (0x0008, IFD_Type.LONG,       1),
    OwnerName                (0x0009, IFD_Type.ASCII,      32),
//  Unknown                  (0x000a, IFD_Type.SHORT,      -1),
    CameraSerialNumber       (0x000c, IFD_Type.LONG,       1),
//  Unknown                  (0x000d, IFD_Type.SHORT,      -1),
    CustomFunctions          (0x000f, IFD_Type.SHORT,      -1),
    ColorAdjustments         (0x001d, IFD_Type.SHORT,      -1);
    
    // 001d -> First short contains length in byte like 0001

    final int markerCode;
    final IFD_Type type;
    final int count;
    final Object defaultValue;

    Canon(final int markerCode, final IFD_Type type, final int count) {
        this(markerCode, type, count, null);
    }

    Canon(final int markerCode, final IFD_Type type, final int count, final Object defaultValue) {
        this.markerCode = markerCode;
        this.type = type;
        this.count = count;
        this.defaultValue = defaultValue;
    }

    public int getCount() {
        return count;
    }



    public Object getDefaultValue() {
        return defaultValue;
    }



    public IFD_Type getType() {
        return type;
    }



    public int getMarkerCode() {
        return markerCode;
    }

    public boolean isMarker(int markerCode) {
        return markerCode == this.markerCode;
    }

    public static Canon getMarkerFromCode(int markerCode) {
        for (Canon marker : values())
            if (markerCode == marker.markerCode)
                return marker;
        return null;
    }

    public final static TagIdentifier tagIdentifier = new TagIdentifier(){
        public IFD_Enum getMarkerFromCode(int markerCode) {
            return Canon.getMarkerFromCode(markerCode);
        }
    };

    final static boolean debug = true;//Options.DEBUG;
    
    public static String getMacroFlashFocusEtcHTML(IFD_Value value) {
        
        if (value.getIfdType() != IFD_Type.SHORT)
            return value.valueToString();
        
        if (!value.getValue().getClass().isArray())
            return value.valueToString();

        Long buffer[] = (Long[])(value.getValue());
        
        StringBuffer ret = new StringBuffer();
        Catalog catalog = Options.getCatalog(Canon.class);
        for (int i = 1; i < value.getCount(); i++) {
            CatalogResource i18n = new CatalogResource(catalog, "_" + i);

            // flash details
            if (i == 29) {

                boolean first = true;

                // Iterate over bits
                for (int j = 0; j < 16; j++) {
                    // content of bit
                    int r = ((int)((buffer[i] >> j) % 2));

                    if (i18n.keys.contains("" + j)) {
                        if (first) {
                            ret.append("<tr><th>" + i18n.getString("_Title_") + "</th><th>");
                            first = false;
                        } else
                            ret.append("<br>");

                        ret.append(i18n.getString("" + j));
                        ret.append(": ");
                        if (r == 1)
                            ret.append(Options.getI18N(Canon.class).getString("yes"));
                        else
                            ret.append(Options.getI18N(Canon.class).getString("no"));

                        // Get next bit
                        continue;
                    }


                    if (!debug)
                        continue;

                    if (first) {
                        ret.append("<tr><th>" + i18n.getString("_Title_") + "</th><th>");
                        first = false;
                    } else
                        ret.append("<br>");

                    ret.append("Unknown bit " + j);
                    ret.append(": ");
                    if (r == 1)
                        ret.append(Options.getI18N(Canon.class).getString("yes"));
                    else
                        ret.append(Options.getI18N(Canon.class).getString("no"));
                }
                if (!first)
                    ret.append("</th></tr>");

                // get next short
                continue;
            }

            if (!i18n.keys.isEmpty()) {
                ret.append("<tr><th>" + i18n.getString("_Title_") + "</th><th>");
                if (i18n.keys.contains("" + buffer[i]))
                    ret.append(i18n.getString("" + buffer[i]));
                else
                    ret.append("" + buffer[i]);
                ret.append("</th></tr>");

                // get next short
                continue;
            } 

            if (debug) {
                ret.append("<tr><th> Unknown ("  + i + ")</th><th>");
                ret.append("" + buffer[i]);
                ret.append("</th></tr>");
            }
        }

        return ret.toString();
    }

    public static String getWhiteBalanceFlashEtcHTML(IFD_Value value) {
        if (value.getIfdType() != IFD_Type.SHORT)
            return value.valueToString();
        
        if (!value.getValue().getClass().isArray())
            return value.valueToString();

        Long buffer[] = (Long[])(value.getValue());
        
        StringBuffer ret = new StringBuffer();
        Catalog catalog = Options.getCatalog(Canon.class);
        for (int i = 1; i < value.getCount(); i++) {
            CatalogResource i18n = new CatalogResource(catalog, "__" + i);

            // AF point used
            if (i == 14) {

                boolean first = true;

                ret.append("<tr><th>" + i18n.getString("_Title_") + "</th><th>");
                int num = ((int)(buffer[i] >> 12));
                ret.append(i18n.getString("number of available focus points") + ": " + num);
                
                
                // Iterate over bits
                for (int j = 0; j < 12; j++) {
                    // content of bit
                    int r = ((int)((buffer[i] >> j) % 2));

                    if (i18n.keys.contains("" + j)) {

                        if (r == 1) {
                            ret.append("<br>");
                            ret.append(i18n.getString("" + j));
                        }

                        // Get next bit
                        continue;
                    }


                    if (!debug)
                        continue;


                    if (r == 1) {
                        ret.append("<br>");
                        ret.append("Unknown bit " + j);
                    }
                }

                ret.append("</th></tr>");

                // get next short
                continue;
            }
            
            if (i == 15) {
                long l = buffer[i];
                
                if (l >= 1 << 15)
                    l -= 1 << 16;
                
                ret.append("<tr><th>" + i18n.getString("_Title_") + "</th><th>");
                ret.append("" + (((double)l) / 20) + " EV");
                ret.append("</th></tr>");
                
                continue;                
            }
            
            if (!i18n.keys.isEmpty()) {
                ret.append("<tr><th>" + i18n.getString("_Title_") + "</th><th>");
                if (i18n.keys.contains("" + buffer[i]))
                    ret.append(i18n.getString("" + buffer[i]));
                else
                    ret.append("" + buffer[i]);
                ret.append("</th></tr>");

                // get next short
                continue;
            } 

            if (debug) {
                ret.append("<tr><th> Unknown ("  + i + ")</th><th>");
                ret.append("" + buffer[i]);
                ret.append("</th></tr>");
            }
        }

        return ret.toString();
    }
    
    public static String getColorAdjustmentsHTML(IFD_Value value) {
        if (value.getIfdType() != IFD_Type.SHORT)
            return value.valueToString();
        
        if (!value.getValue().getClass().isArray())
            return value.valueToString();

        Long buffer[] = (Long[])(value.getValue());
        
        StringBuffer ret = new StringBuffer();
        Catalog catalog = Options.getCatalog(Canon.class);
        for (int i = 1; i < value.getCount(); i++) {
            CatalogResource i18n = new CatalogResource(catalog, "_29_" + i);

            if (i == 12 || i == 14)
                continue;
            
            // Use the color for the background
            if (i == 13 || i == 15) {

                // If the color has a value of zero
                // then most certain there was non chosen
                if (buffer[i-1] == 0 && buffer[i] == 0) {

                    // No color adaptation used therefore no color 
                    if (buffer[2] != 7 && buffer[2] != 8)
                        continue;

                    // TODO show color
                    
                    // No color adaptation used where there is a second color 
                    if (buffer[2] != 8 && i == 15)
                        continue;
                    
                    // TODO show color
                }
                
                // First parts of 24bit
                int b1 = (int) (buffer[i - 1] >> 8);
                int b2 = (int) (buffer[i - 1] % 256);
                // Rest in the next value
                int b3 = (int) ((long)buffer[i]);
                

                // Used Color coding is unknown
                /*
                 * b2 is most likely Brightness
                 * 
                 * The coding looks similar to YUV or YIQ
                 * but I could not find the right matrix.
                 * 
                 */
                
                ret.append("<tr><th>" + i18n.getString("_Title_") + "</th>");
/*                ret.append("<th bgcolor=\"#");
                ret.append(Integer.toHexString(red) + Integer.toHexString(green) + Integer.toHexString(blue));
                ret.append("\">");*/
                ret.append("<th>");
                ret.append(i18n.getString("Unkown color code") + " (" + b1 + ", " + b2 + ", " + b3 + ")<br>");
                ret.append("</th></tr>");

                continue;
            }

            if (!i18n.keys.isEmpty()) {
                ret.append("<tr><th>" + i18n.getString("_Title_") + "</th><th>");
                
                if (i18n.keys.contains("" + buffer[i]))
                    ret.append(i18n.getString("" + buffer[i]));
                else
                    ret.append("" + buffer[i]);
                ret.append("</th></tr>");

                // get next short
                continue;
            } 

            if (debug) {
                ret.append("<tr><th> Unknown ("  + i + ")</th><th>");
                ret.append("" + buffer[i]);
                ret.append("</th></tr>");
            }
        }

        return ret.toString();
    }
}
