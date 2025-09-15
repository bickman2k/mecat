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
 * Created on Jul 15, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filesystem.mime.mp3;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.ResourceBundle;

import net.sourceforge.mecat.catalog.filesystem.mime.TagFinder;
import net.sourceforge.mecat.catalog.filesystem.mime.mp3.TagID3V2.Position;
import net.sourceforge.mecat.catalog.filesystem.mime.mp3.frames.Field;
import net.sourceforge.mecat.catalog.filesystem.mime.mp3.frames.Frame;
import net.sourceforge.mecat.catalog.filesystem.mime.mp3.frames.FrameValue_2_2;
import net.sourceforge.mecat.catalog.option.Options;

public class ID3V2_TagFinder implements TagFinder {
    
    
    ResourceBundle res = Options.getI18N(ID3V2_TagFinder.class);

    int currentPosition = 0;
    int length = 0;
    DataInputStream data = null;
    
    public TagID3V2 getTag(File file) {
        FileInputStream istream = null;
        try {
            istream = new FileInputStream(file);

            if (file.length() < 150) {
                istream.close();
                return null;
            }

            byte id3_tag[] = new byte[3];

            // Read from start of file
            istream.read(id3_tag);
            
            if ((new String(id3_tag)).equals("ID3")){
                istream.close();
                return readTag(file, Position.StartOfFile);
            }

            // Allready read 3, 10 before end, 128 byte for ID3V1
            istream.skip(file.length() -3 - 10 - 128);
            istream.read(id3_tag);
            
            if ((new String(id3_tag)).equals("3DI")){
                istream.close();
                return readTag(file, Position.BeforeID3V1);
            }

            // Skip rest of previously anticipated ID3V1
            istream.skip(128 - 3);
            istream.read(id3_tag);
            
            istream.close();
            
            if ((new String(id3_tag)).equals("3DI")){
                istream.close();
                return readTag(file, Position.EndOfFile);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            istream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static int UNSYNCHRONISATION = 1 << 7;
    static int COMPRESSION = 1 << 6;
    static int EXTENDED_HEADER = 1 << 6;
    static int CRC = 1 << 15;
    
    private int getSize(int fieldSize) throws IOException {
        int ret = 0;
        for (int i = 0; i < fieldSize; i++)  {
            ret <<= 7;
            ret += data.readUnsignedByte();
            currentPosition++;
        }
        return ret;
    }
    
    private int getNormalSize(int fieldSize) throws IOException {
        int ret = 0;
        for (int i = 0; i < fieldSize; i++)  {
            ret <<= 8;
            ret += data.readUnsignedByte();
            currentPosition++;
        }
        return ret;
    }
    
    private long getNormalSizeLong(int fieldSize) throws IOException {
        long ret = 0;
        for (int i = 0; i < fieldSize; i++)  {
            ret <<= 8;
            ret += data.readUnsignedByte();
            currentPosition++;
        }
        return ret;
    }
    
    private TagID3V2 readTag(File file, Position pos) {
        if (pos != Position.StartOfFile) {
            TagID3V2 ret = new TagID3V2(pos);
            ret.setVersion(4);
            return ret;
        }

        if (file.length() < 150) 
            return null;

        FileInputStream istream = null;
        try {
            istream = new FileInputStream(file);
            TagID3V2 tag = readTag(istream);
            istream.close();
            return tag;
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            istream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public TagID3V2 readTag(InputStream iStream) {
        TagID3V2 ret = new TagID3V2(Position.StartOfFile);
        
        // Initialise values
        currentPosition = 0;
        length = 0;
        
        SynchronisationStream sStream = null;
        BufferedInputStream bStream = null;
        data = null;

        try {
            sStream = new SynchronisationStream(iStream);
            bStream = new BufferedInputStream(sStream);
            bStream.mark(10^6);
            data = new DataInputStream(bStream);

            data.skip(3);
            currentPosition += 3;

            // Read a byte for version
            int version = data.readUnsignedByte();
            currentPosition ++;
            
            // Read a byte for revision
            ret.setVersion(version);
            ret.setRevision(data.readUnsignedByte());
            currentPosition ++;
            
            if (version == 2 || version == 3 || version == 4) {
                // Read flag byte
                int b = data.readUnsignedByte();
                currentPosition ++;

                if ((b | (1 << 7)) == b)
                    ret.setFlag("Unsynchronisation", true);
                
                if (version == 2) {
                    if ((b | (1 << 6)) == b)
                        ret.setFlag("Compression", true);
                } else if (version == 3) {
                    if ((b | (1 << 6)) == b)
                        ret.setFlag("Extended Header", true);
                    // Flage experimental dropped
                }
                
                System.out.println("#### Tag flags : " + b);

                
                
                // Read tag size (tag size without head - tag size minus 10)
                int tagSize = getSize(TagProperties.version[version].getTagSizeSize());

                // After the header is read the de unsynchronisation has to 
                // start if the unsynchronisation is on
                sStream.setDeUnSynchronization(ret.getFlag("Unsynchronisation"));
                
                for (String str : ret.getFlags())
                    System.out.println("#### Tag flag - " + str);
                
                if (ret.getFlag("Extended Header")) {
                    int extendedHeaderRead = 0;
                    int extendedHeaderSize = getSize(TagProperties.version[version].getExtendedHeaderSizeSize());
                    // The size itself is excluded
                    // extendedHeaderRead += TagProperties.version[version].getExtendedHeaderSizeSize();
                    
                    // Read the 2 flag bytes    
                    int flags = data.readUnsignedShort();
                    currentPosition += 2;
                    extendedHeaderRead += 2;
                    
                    int paddingSize = getSize(TagProperties.version[version].getPaddingSizeSize());
                    extendedHeaderRead += TagProperties.version[version].getPaddingSizeSize();
                    System.out.println(res.getString("Padding = [SIZE]").replaceAll("\\[SIZE\\]", "" + paddingSize));

                    boolean containsCRC = (flags & CRC) == flags;
                    byte CRC[] = new byte[4];
                    data.read(CRC);
                    currentPosition += CRC.length;
                    extendedHeaderRead += CRC.length;
                    
                    // Skip rest of extended header if there is any
                    if (extendedHeaderRead < extendedHeaderSize) {
                        data.skip(extendedHeaderSize - extendedHeaderRead);
                        currentPosition += extendedHeaderSize - extendedHeaderRead;
                    }
                        
                    
                }

                if (Options.verbosity == 5)
                    System.out.println(res.getString("Tag size: [SIZE]").replaceAll("\\[SIZE\\]", "" + tagSize));

                // Before reading any frame the length of the 
                // tag is the length of the header
                length = currentPosition;

                // Read frames
                while (currentPosition < tagSize + 10) {
                    byte identBytes[] = new byte[TagProperties.version[version].getFrameIdentifierSize()];

                    // Read frame identifier
                    data.read(identBytes);
                    currentPosition += identBytes.length;
                    
                    String ident = new String(identBytes);
                    
                    Frame frame = KnownID3V2Frames.getDefaultKnownID3V2Frames().knownFramesMap[version].get(ident);
                    if (frame == null) 
                        // If identifier is no valid Frame identifier
                        // and we got the expected length by a bit error I saw
                        // then we can correct the error
                        if (!isIdentifier(ident, version)) {
                            System.out.println(res.getString("Crop unreadable rest (padding)"));
                            break;
                        } else {
                            System.out.println(res.getString("Unknown frame [FRAME].").replaceAll("\\[FRAME\\]", ident));
                        }
                    
                    // Read frame size (frame size without head - frame size minus 6)
                    int frameSize;
                    // For the version 4 the framesize is unsynchronised
                    if (version == 4)
                        frameSize = getSize(TagProperties.version[version].getFrameSizeSize());
                    else
                        frameSize = getNormalSize(TagProperties.version[version].getFrameSizeSize());
//                    currentPosition += TagProperties.version[version].getFrameSizeSize();

                    if (frame == null) {
                        int rest = frameSize + TagProperties.version[version].getFrameHeaderSize()
                                             - TagProperties.version[version].getFrameIdentifierSize()
                                             - TagProperties.version[version].getFrameSizeSize();
                        data.skip(rest);
                        currentPosition += rest;
                        length = currentPosition;
                        continue;
                    }
                    
                    if (version == 3) {
                        int frameFlags = data.readShort();
                        currentPosition += 2;

//                        System.out.println("#### Frame flags : " + frameFlags);
                        
                        if ((frameFlags | (1 << 15)) == frameFlags)
                            frame.setFlag("Tag alter Preservation", true);
                        if ((frameFlags | (1 << 14)) == frameFlags)
                            frame.setFlag("File alter Preservation", true);
                        if ((frameFlags | (1 << 13)) == frameFlags)
                            frame.setFlag("Read only", true);
                        
                        if ((frameFlags | (1 << 7)) == frameFlags)
                            frame.setFlag("Compression", true);
                        if ((frameFlags | (1 << 6)) == frameFlags)
                            frame.setFlag("Encryption", true);
                        if ((frameFlags | (1 << 5)) == frameFlags)
                            frame.setFlag("Grouping identity", true);
                    }
                    
                    

                    if (version == 4) {
                        int frameFlags = data.readShort();
                        currentPosition += 2;
                        
//                        System.out.println("#### Frame flags : " + frameFlags);
                        
                        if ((frameFlags | (1 << 14)) == frameFlags)
                            frame.setFlag("Tag alter Preservation", true);
                        if ((frameFlags | (1 << 13)) == frameFlags)
                            frame.setFlag("File alter Preservation", true);
                        if ((frameFlags | (1 << 12)) == frameFlags)
                            frame.setFlag("Read only", true);
                        
                        if ((frameFlags | (1 << 6)) == frameFlags)
                            frame.setFlag("Grouping identity", true);
                        if ((frameFlags | (1 << 3)) == frameFlags)
                            frame.setFlag("Compression", true);
                        if ((frameFlags | (1 << 2)) == frameFlags)
                            frame.setFlag("Encryption", true);
                        if ((frameFlags | (1 << 1)) == frameFlags)
                            frame.setFlag("Unsynchronisation", true);
                        if ((frameFlags | (1 << 0)) == frameFlags)
                            frame.setFlag("Data length indicator", true);

                    }
                    
                    for (String str : frame.getFlags())
                        System.out.println("#### Frame - " + str);

                    if (Options.verbosity == 5)
                        System.out.println(res.getString("Frame size: [SIZE]").replaceAll("\\[SIZE\\]", "" + frameSize));
                    
                    
//                    if (frame != null) {
                        System.out.println(frame + ":");
                        FrameValue_2_2 value  = getFrame(frame, frameSize);
                        for (Map.Entry<String, Object> entry : value.entrySet()) 
                            if (entry.getValue().getClass().isArray()) 
                                System.out.println("  " + entry.getKey() + ": " + ((byte[])entry.getValue()).length);
                            else
                                System.out.println("  " + entry.getKey() + ": " + entry.getValue());
                        ret.add(value);
//                    }

                    // Length with data that was a frame
                    // padding is not counted
                    length = currentPosition;
                }
            } 
            
            ret.setLength(length);

            bStream.reset();
            byte[] buffer = new byte[length];
            int bufLen = bStream.read(buffer);
            if (bufLen == length)
                ret.setBuffer(buffer);
            else
                System.err.println(res.getString("Could not aquire copy buffer."));
            
            
            data.close();
            sStream.close();
            bStream.close();
            return ret;
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            data.close();
            bStream.close();
            sStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private FrameValue_2_2 getFrame(Frame frame, int frameSize) throws IOException {

        FrameValue_2_2 ret = new FrameValue_2_2(frame);

        // If the frameSize is 0 then there can't be gathered any data
        if (frameSize == 0)
            return ret;
        
        int currentRead = 0;
        
        String encoding = "ISO-8859-1";
        if (frame.isEncoding()) {
            int encodingByte = data.readUnsignedByte();
            currentPosition ++;
            currentRead ++;
            if (currentRead == frameSize)
                return ret;
            
            switch (encodingByte) {
            case 1:
            case 2:
                encoding = "UTF-16";
                break;
            case 3:
                encoding = "UTF-8";
                break;
            case 0:
            default:
                encoding = "ISO-8859-1";
            }
        }
        byte buffer[] = new byte[frameSize];
        for (Field field : frame.getFields()) {
            switch (field.getType()) {
            // A terminated Strings or a String that ends with the frame 
            // is indicated with a size of -1
            // If a String is the last element in a frame it only is as long as the frame
            // In any case the String ends with with the first null byte
            case STRING: {
                // Some special String need allways a fix encoding like URL
                String localEncoding = encoding;
                if (field.getEncodingOverride() != null)
                    localEncoding = field.getEncodingOverride();
                if (field.getSize() != 0) {
                    int pos = 0;
                    if (localEncoding.equals("ISO-8859-1") || localEncoding.equals("UTF-8")) {
                        do {
                            buffer[pos] = data.readByte();
                            currentPosition ++;
                            currentRead ++;
                            pos++;
                            // Stop if
                            // - a 0 has been encountered
                            // - the end of the frame is reached
                            // - the maximal size of the field is reached
                        } while (buffer[pos - 1] != 0 && currentRead < frameSize && pos != field.getSize());
                        ret.put(field.getName(), convertISO_8859_1_or_UTF8(buffer, pos, encoding));
                        // If the end of the frame is reached then return
                        if (currentRead == frameSize)
                            return ret;
                    } else if (localEncoding.equals("UTF-16")) {
                        do {
                            // If we have only one character left in the frame or for the field size
                            // then throw it away, since half a character is no good anyway
                            if (currentRead + 1 == frameSize || pos + 1 == field.getSize()) {
                                data.readByte();
                                currentPosition ++;
                                currentRead ++;
                                break;
                            }
                            buffer[pos] = data.readByte();
                            buffer[pos + 1] = data.readByte();
                            pos += 2;
                            currentPosition += 2;
                            currentRead += 2;
                            // Stop if
                            // - a 0 has been encountered
                            // - the end of the frame is reached
                            // - the maximal size of the field is reached
                        } while ((buffer[pos - 2] != 0 || buffer[pos - 1] != 0) && currentRead < frameSize && pos != field.getSize());
                        ret.put(field.getName(), convertUTF_16(buffer, pos));
                    }
                } else if (field.getSize() == 0) {
                    ret.put(field.getName(), new String());
                } 
            }
            break;
            
            // Binary data
            case BINARY: {
                // Get the wanted size for the binary
                int len = field.getSize();

                // If the length is unlimited or just greater then 
                // the rest of the frame then crop it to the size 
                // of the frame
                if (len == -1 || len > frameSize - currentRead)
                    len = frameSize - currentRead;

                // Make array with the exact size
                byte binary[] = new byte[len];
                
                data.read(binary);
                currentPosition += len;
                currentRead += len;
                
                ret.put(field.getName(), binary);
            }
            break;
                
            // Binary data
            case NUMBER: {
                // Get the wanted size for the number
                int len = field.getSize();

                // If the length is unlimited or just greater then 
                // the rest of the frame then crop it to the size 
                // of the frame
                if (len == -1 || len > frameSize - currentRead)
                    len = frameSize - currentRead;
                    
                long number = getNormalSizeLong(len);
//                currentRead += len;
                    
                ret.put(field.getName(), number);
            }
            break;

            case PICTURETYPE: {
                int picType = data.readUnsignedByte();
                currentPosition ++;
                currentRead ++;
                switch (picType) {
                case 0x00 : ret.put(field.getName(), "Other"); break;
                case 0x01 : ret.put(field.getName(), "32x32 pixels 'file icon' (PNG only)"); break;
                case 0x02 : ret.put(field.getName(), "Other file icon"); break;
                case 0x03 : ret.put(field.getName(), "Cover (front)"); break;
                case 0x04 : ret.put(field.getName(), "Cover (back)"); break;
                case 0x05 : ret.put(field.getName(), "Leaflet page"); break;
                case 0x06 : ret.put(field.getName(), "Media (e.g. lable side of CD)"); break;
                case 0x07 : ret.put(field.getName(), "Lead artist/lead performer/soloist"); break;
                case 0x08 : ret.put(field.getName(), "Artist/performer"); break;
                case 0x09 : ret.put(field.getName(), "Conductor"); break;
                case 0x0A : ret.put(field.getName(), "Band/Orchestra"); break;
                case 0x0B : ret.put(field.getName(), "Composer"); break;
                case 0x0C : ret.put(field.getName(), "Lyricist/text writer"); break;
                case 0x0D : ret.put(field.getName(), "Recording Location"); break;
                case 0x0E : ret.put(field.getName(), "During recording"); break;
                case 0x0F : ret.put(field.getName(), "During performance"); break;
                case 0x10 : ret.put(field.getName(), "Movie/video screen capture"); break;
                case 0x11 : ret.put(field.getName(), "A bright coloured fish"); break;
                case 0x12 : ret.put(field.getName(), "Illustration"); break;
                case 0x13 : ret.put(field.getName(), "Band/artist logotype"); break;
                case 0x14 : ret.put(field.getName(), "Publisher/Studio logotype"); break;
                default   : ret.put(field.getName(), "Unknown");
                }
            }
            break;
             // A list of terminated Strings. This ends with the end of the frame.                
            case STRINGS:
                
//                break;
                
            // Types for "Event timing codes"
            // Time Stamp format
            case TIMESTAMPFORMAT:
                
//                break;
                
            // Event type
            case TYPEOFEVENT:
                
//                break;
            // etc for now this will do
            case CONTENTTYPE:
                
//                break;
                
            default:
                int len = field.getSize();
                if (len > frameSize - currentRead)
                    len = frameSize - currentRead;

                data.skip(len);
                currentPosition += len;
                currentRead += len;
                
            }
            // If the end of the frame is reached then return
            if (currentRead == frameSize)
                return ret;
        }
        // Skip the rest of the frame
        // This only happens if additional information is in the frame
        data.skip(frameSize - currentRead);
        currentPosition += frameSize - currentRead;
        currentRead = frameSize;
        
        return ret;
    }
    
    
    
    private static Object convertISO_8859_1_or_UTF8(byte[] buffer, int len, String encoding) {
        while (len > 0 && buffer[len - 1] == 0)
            len--;
        if (len == 0)
            return "";
        
        int off = 0;
        while (buffer[off] == 0)
            off++;
        
        try {
            return new String(buffer, off, len - off, encoding);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static Object convertUTF_16(byte[] buffer, int len) {
        if (len % 2 != 0)
            len --;
        while (len >= 2 && buffer[len - 2] == 0 && buffer[len - 1] == 0)
            len -= 2;
        if (len == 0)
            return "";
        
        try {
            return new String(buffer, 0, len, "UTF-16");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static boolean isUpperCaseLetter(char ch) {
        return (ch >= 'A' && ch <= 'Z');
    }
    
    private static boolean isDigit(char ch) {
        return (ch >= '0' && ch <= '9');
    }
    
    private static boolean isIdentifier(String ident, int version) {
        if (ident.length() != TagProperties.version[version].getFrameIdentifierSize())
            return false;
        for (int i  = 0; i < TagProperties.version[version].getFrameIdentifierSize(); i++)
            if (!isUpperCaseLetter(ident.charAt(i)) && !isDigit(ident.charAt(i)))
                return false;
        return true;
    }
}
