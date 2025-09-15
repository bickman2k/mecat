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
 * Created on Aug 21, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filesystem.mime.jpg.ifd.makerNote;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import net.sourceforge.mecat.catalog.filesystem.mime.jpg.TIFF_TagFinder;
import net.sourceforge.mecat.catalog.filesystem.mime.jpg.ifd.IFD;
import net.sourceforge.mecat.catalog.filesystem.mime.jpg.ifd.IFD_ExifAttributeInformation;
import net.sourceforge.mecat.catalog.filesystem.mime.jpg.ifd.IFD_Liste;
import net.sourceforge.mecat.catalog.filesystem.mime.jpg.ifd.IFD_Type;
import net.sourceforge.mecat.catalog.filesystem.mime.jpg.ifd.IFD_Value;

/**
 * 
 * Created on Aug 21, 2006
 *
 * @author Stephan Richard Palm
 * 
 * http://park2.wakwak.com/~tsuruzoh/Computer/Digicams/exif-e.html
 *
 */
public class MakeNote {

    public static String show(IFD_Value makerNote, IFD_Value make) {
        if (!makerNote.getValue().getClass().isArray())
            return makerNote.valueToString();

        Object array[] = (Object[]) makerNote.getValue();
        byte buffer[] = new byte[array.length];
        IFD_Type.BYTE.storeArray(buffer, 0, array, makerNote.getLittleEndian());

        if (buffer.length >= 8) {
            ByteArrayInputStream stream = new ByteArrayInputStream(buffer);
            BufferedInputStream bStream = new BufferedInputStream(stream);
            try {
                String str = new String(buffer, 0, 8, "ASCII");
                if (str.toUpperCase().startsWith("OLYMP")) {
                    bStream.mark(buffer.length);

                    IFD ifd = TIFF_TagFinder.readIFD(bStream, makerNote.getValOffset(), makerNote.getValOffset() + 7, IFD_Liste.OLYMPUS, makerNote.getLittleEndian());
                    return ifd.getHTMLInfo(make);
                }
                if (str.toUpperCase().startsWith("NIKON")) {
                    bStream.mark(buffer.length);

                    IFD ifd = TIFF_TagFinder.readIFD(bStream, makerNote.getValOffset(), makerNote.getValOffset() + 8, IFD_Liste.NIKON, makerNote.getLittleEndian());
                    return ifd.getHTMLInfo(make);
                }
                if (str.toUpperCase().startsWith("FUJIFILM")) {
                    bStream.mark(buffer.length);
                    bStream.skip(8);

                    // Start of the IFD relative from the start off the MakerNote
                    long offset = (Long)IFD_Type.LONG.get(bStream, 1, makerNote.getLittleEndian());

                    // For FujiFilm the offsets used are realtive to the MakerNote
                    IFD ifd = TIFF_TagFinder.readIFD(bStream, 0, offset, IFD_Liste.FUJIFILM, makerNote.getLittleEndian());
                    return ifd.getHTMLInfo(make);
                }
                if (make.getValue().toString().toUpperCase().startsWith("NIKON")) {
                    bStream.mark(buffer.length);

                    IFD ifd = TIFF_TagFinder.readIFD(bStream, makerNote.getValOffset(), makerNote.getValOffset(), IFD_Liste.NIKONALTERNATIVE, makerNote.getLittleEndian());
                    return ifd.getHTMLInfo(make);
                }
                if (make.getValue().toString().toUpperCase().startsWith("CASIO")) {
                    bStream.mark(buffer.length);

                    IFD ifd = TIFF_TagFinder.readIFD(bStream, makerNote.getValOffset(), makerNote.getValOffset(), IFD_Liste.CASIO, makerNote.getLittleEndian());
                    return ifd.getHTMLInfo(make);
                }
                if (make.getValue().toString().toUpperCase().startsWith("CANON")) {
                    bStream.mark(buffer.length);

                    IFD ifd = TIFF_TagFinder.readIFD(bStream, makerNote.getValOffset(), makerNote.getValOffset(), IFD_Liste.CANON, makerNote.getLittleEndian());

                    StringBuffer ret = new StringBuffer();

                    IFD next = ifd;
                    ret.append("<table border=\"1\">");
                    while (next != null) {
                        for (IFD_Value value : next.getTags()) {
                            if (value.getTagType() != null) {
                                switch ((Canon)value.getTagType()) {
                                case MacroFlashFocusEtc:
                                    ret.append(Canon.getMacroFlashFocusEtcHTML(value));
                                    break;
                                case WhiteBalanceFlashEtc:
                                    ret.append(Canon.getWhiteBalanceFlashEtcHTML(value));
                                    break;
                                case ColorAdjustments:
                                    ret.append(Canon.getColorAdjustmentsHTML(value));
                                }
                                continue;
                            }
                            
                            ret.append("<tr><th>");
                            if (value.getTagType() != null)
                                ret.append(value.getTagType());
                            else
                                ret.append(value.getTagTypeID());
                            ret.append("</th><th>");

                            if (value.getValue() instanceof IFD)
                                ret.append(((IFD)value.getValue()).getHTMLInfo(make));
                            else
                                ret.append(value.valueToString());
                            
                            ret.append("</th></tr>");
                        }
                        next = next.getNext();
                    }
                    ret.append("</table>");
                    
                    return ret.toString();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                // Ignore because this just means we can use the makeNote decoder
            }
            try {
                if (stream != null)
                    stream.close();
                if (bStream != null)
                    bStream.close();
            } catch (Exception e) {}
        }
        
        return makerNote.valueToString();
    }

}
