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

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import net.sourceforge.mecat.catalog.filesystem.mime.AbstractTag;
import net.sourceforge.mecat.catalog.filesystem.mime.TagType;
import net.sourceforge.mecat.catalog.filesystem.mime.mp3.frames.FrameValue_2_2;
import net.sourceforge.mecat.catalog.option.Options;

public class TagID3V2 extends AbstractTag {
    
    Vector<FrameValue_2_2> frames = new Vector<FrameValue_2_2>();
    Map<String, Boolean> flags = new LinkedHashMap<String, Boolean>();
        
    enum Position {
        StartOfFile, EndOfFile, BeforeID3V1
    }

    final Position pos;
    int version;
    int revision;
    int length;
    
    public TagID3V2(Position pos) {
        this.pos = pos;
        length = 0;
        version = -1;
        revision = 0;
    }


    public String toString() {
        return Options.getI18N(TagID3V2.class).getString("ID3V2.[VERSION] at [POS].")
                        .replaceAll("\\[VERSION\\]","" + version)
                        .replaceAll("\\[POS\\]", "" + pos);
    }


    public void setLength(int length) {
        this.length = length;
    }
    
    public int getLength() {
        return length;
    }
    
    
    
    
    public int getRevision() {
        return revision;
    }


    public void setRevision(int revision) {
        this.revision = revision;
    }


    public int getVersion() {
        return version;
    }


    public void setVersion(int version) {
        this.version = version;
    }


    public Position getPos() {
        return pos;
    }


    public boolean getFlag(String flagName) {
        if (!flags.containsKey(flagName))
            return false;
        return flags.get(flagName);
    }

    public Set<String> getFlags() {
        Set<String> ret = new LinkedHashSet<String>(flags.keySet());
        
        for (String str : flags.keySet())
            if (!flags.get(ret))
                ret.remove(str);
        
        return ret;
    }

    public void setFlag(String flagName, boolean value) {
        flags.put(flagName, value);
    }

    public void setFalgs(Map<String, Boolean> map) {
        flags.putAll(map);
    }

    
    public static void writeZeros(OutputStream stream, int amount) throws IOException {
        for (int i = 0 ; i < amount; i++)
            stream.write(0);
    }

    /**
     * Only works for tags at the beginning of the file
     * @throws IOException 
     */
    public void copyTag(DataOutputStream ostream) throws IOException {
        // amount of information that must be writen
        // if this amount of data can not be writen
        // a IOException has to be thrown.
        int len = getLength();
        
        if (len == -1)
            return;
        
        if (buffer == null) {
            // Since not writing the whole length could brake the
            // output, this will fill the rest with zeros
            writeZeros(ostream, len);
            return;
        }

        InputStream stream = null;
        DataInputStream idata = null;

        try {
            stream = new ByteArrayInputStream(buffer);
            idata = new DataInputStream(stream);
        } catch (Exception e) {
            e.printStackTrace();
            // If the stream has been open then close it
            try {
                if (stream != null)
                    stream.close();
            } catch (Exception e2) { e2.printStackTrace(); }
            // Since not writing the whole length could brake the
            // output, this will fill the rest with zeros
            writeZeros(ostream, len);
            return;
        }

        byte version[] = new byte[5];

        // "ID3" + version number + revision number
        try {
            idata.read(version);
        } catch (Exception e) {
            e.printStackTrace();
            // If the stream has been open then close it
            try {
                if (stream != null)
                    stream.close();
            } catch (Exception e2) { e2.printStackTrace(); }
            // If the idata-stream has been open then close it
            try {
                if (idata != null)
                    idata.close();
            } catch (Exception e2) { e2.printStackTrace(); }
            // Since not writing the whole length could brake the
            // output, this will fill the rest with zeros
            writeZeros(ostream, len);
            return;
        }

        ostream.write(version);
        len -= 5;

        int flags;
        try {
            flags = idata.readUnsignedByte();
        } catch (Exception e) {
            e.printStackTrace();
            // If the stream has been open then close it
            try {
                if (stream != null)
                    stream.close();
            } catch (Exception e2) { e2.printStackTrace(); }
            // If the idata-stream has been open then close it
            try {
                if (idata != null)
                    idata.close();
            } catch (Exception e2) { e2.printStackTrace(); }
            // Since not writing the whole length could brake the
            // output, this will fill the rest with zeros
            writeZeros(ostream, len);
            return;
        }

        // remove unsynchronisation (1 << 7)
        flags = flags & (255 - (1 << 7));
        ostream.write(flags);
        len -= 1;

        // Skip original size
        try {
            idata.skip(4);
        } catch (Exception e) {
            e.printStackTrace();
            // If the stream has been open then close it
            try {
                if (stream != null)
                    stream.close();
            } catch (Exception e2) { e2.printStackTrace(); }
            // If the idata-stream has been open then close it
            try {
                if (idata != null)
                    idata.close();
            } catch (Exception e2) { e2.printStackTrace(); }
            // Since not writing the whole length could brake the
            // output, this will fill the rest with zeros
            writeZeros(ostream, len);
            return;
        }

        // write length without padding
        ostream.write(getLength() / 128^3);
        ostream.write((getLength() / 128^2) % 128);
        ostream.write((getLength() / 128) % 128);
        ostream.write(getLength() % 128);
        len -= 4;

        byte rest[] = new byte[getLength() - 10];
        try {
            idata.read(rest);
        } catch (Exception e) {
            e.printStackTrace();
            // If the stream has been open then close it
            try {
                if (stream != null)
                    stream.close();
            } catch (Exception e2) { e2.printStackTrace(); }
            // If the idata-stream has been open then close it
            try {
                if (idata != null)
                    idata.close();
            } catch (Exception e2) { e2.printStackTrace(); }
            // Since not writing the whole length could brake the
            // output, this will fill the rest with zeros
            writeZeros(ostream, len);
            return;
        }
        ostream.write(rest);
            
        try {
            idata.close();
            stream.close();
            return;
        } catch (Exception e) {
            e.printStackTrace();
            // If the stream has been open then close it
            try {
                if (stream != null)
                    stream.close();
            } catch (Exception e2) { e2.printStackTrace(); }
            // If the idata-stream has been open then close it
            try {
                if (idata != null)
                    idata.close();
            } catch (Exception e2) { e2.printStackTrace(); }
        }
        
    }


    public void add(FrameValue_2_2 value) {
        frames.add(value);
    }


    public int getStartPosition() {
        return 0;
    }


    public String getHTMLInfo() {
        StringBuffer buf = new StringBuffer();
        buf.append("<h2>");
        buf.append("id3 v2." + this.version);
        buf.append("</h2>");
        for (FrameValue_2_2 frame : frames) {
            // Build content information
            StringBuffer content = new StringBuffer();
            for (Map.Entry<String, Object> entry : frame.entrySet()) 
                if (entry.getValue().getClass().isArray()) 
                    content.append("<i>" + entry.getKey() + ":</i> " + ((byte[])entry.getValue()).length + "<br>");
                else if (entry.getValue().toString().length() > 0)
                    content.append("<i>" + entry.getKey() + ":</i> " + entry.getValue() + "<br>");

            if (content.length() > 0) {
                buf.append("<h3>" + frame.getFrame() + "</h3>");
                buf.append(content);
            }
        }
        
        return buf.toString();
    }

    byte[] buffer = null;

    public void setBuffer(byte[] buffer) {
        this.buffer = buffer;
    }


    public TagType getTagType() {
        return TagType.ID3V2;
    }
    
    
}
