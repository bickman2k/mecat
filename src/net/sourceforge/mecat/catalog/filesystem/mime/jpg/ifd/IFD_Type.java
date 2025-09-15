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
 * Created on Aug 6, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filesystem.mime.jpg.ifd;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Vector;

import net.sourceforge.mecat.catalog.filesystem.mime.jpg.Rational;

// TIFF6.0 page 15
// the TIFF6.0 implies the use
// of only one function to read BYTE, SHORT and LONG.
// This will lead to the use of longs for all numbers. 
// Grrr (damn java with his missing signed/unsigned modifiers)
// While it was not excplicitly writen I think this
// only works for COUNTING = 1
public enum IFD_Type {
    BYTE(1, 1){
        public Object get(byte buffer[], int count, int valueOffset, boolean littleEndian){
            Long ret[] = new Long[count];
            for (int i = 0; i < count; i++) {
                ret[i] = (long)getUnsignedByte(buffer[valueOffset + i]);
            }
            if (count == 1)
                return ret[0];
            return ret;
        }
        public void store(byte buffer[], int offset, Object value, boolean littleEndian) {
            if (!(value instanceof Long))
                return;
            long val = (Long)value;
            
            // No problem with little or big endian
            // since a byte takes only one byte, how would have guest
            buffer[offset] = (byte)val;
        }
    },
    ASCII(2, 1){
        public Object get(byte buffer[], int count, int offset, boolean littleEndian){
            Vector<String> ret = new Vector<String>();

            StringBuffer current = new StringBuffer();
            int start = 0;
            for (int i = 0; i < count; i++)
                if (buffer[offset + i] == 0)
                    if (start != i)
                        try {
                            ret.add(new String(buffer, start, i - start, "ASCII"));
                            start = i + 1;
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    else
                        // A unallowed zero. stop.
                        return ret.toArray();

            if (start < count)
                try {
                    ret.add(new String(buffer, start, count - start, "ASCII"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            
            if (ret.size() == 0)
                return null;
            
            if (ret.size() == 1)
                return ret.firstElement();
            
            return ret.toArray();
        }
        public void store(byte buffer[], int offset, Object value, boolean littleEndian) {
            if (!(value instanceof String))
                return;
            
            String val = (String) value;
            
            try {
                byte ascii[] = val.getBytes("ASCII");
                for (int i = 0; i < ascii.length; i++)
                    buffer[offset + i] = ascii[i];
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        public int storeArray(byte buffer[], int offset, Object value[], boolean littleEndian){
            int j = 0;
            for (int i = 0; i < value.length; i++) { 
                if (!(value[i] instanceof String))
                    continue;
                
                String val = (String) value[i];

                store(buffer, offset + j, val, littleEndian);
                j += val.length();
                j++;
            }
            
            return j;
        }
    },
    SHORT(3, 2){
        public Object get(byte buffer[], int count, int offset, boolean littleEndian){
            return getLong(buffer, count, offset, littleEndian, 2);
        }
        public void store(byte buffer[], int offset, Object value, boolean littleEndian) {
            storeLong(buffer, offset, value, littleEndian, 2);
        }
    },
    LONG(4, 4){
        public Object get(byte buffer[], int count, int offset, boolean littleEndian){
            return getLong(buffer, count, offset, littleEndian, 4);
        }
        public void store(byte buffer[], int offset, Object value, boolean littleEndian) {
            storeLong(buffer, offset, value, littleEndian, 4);
        }
    },
    RATIONAL(5, 8){
        public Object get(byte buffer[], int count, int valueOffset, boolean littleEndian){
            Rational<Long> ret[] = new Rational[count];

            
            for (int i = 0; i < count; i++) 
                ret[i] = new Rational<Long>((Long)LONG.get(buffer, 1, i*8 + valueOffset, littleEndian), 
                                            (Long)LONG.get(buffer, 1, i*8 + valueOffset + 4, littleEndian));
            
            if (count == 1)
                return ret[0];
            
            return ret;
        }
        public void store(byte buffer[], int offset, Object value, boolean littleEndian) {
            if (!(value instanceof Rational))
                return;
            Rational<Long> val = (Rational)value;
            
            LONG.store(buffer, offset, val.getNumerator(), littleEndian);
            LONG.store(buffer, offset + 4, val.getDenominator(), littleEndian);
        }
        
    },
    SBYTE(6, 1){
        public Object get(byte buffer[], int count, int valueOffset, boolean littleEndian){
            Long ret[] = new Long[count];
            for (int i = 0; i < count; i++) {
                ret[i] = (long)buffer[valueOffset + i];
            }
            if (count == 1)
                return ret[0];
            return ret;
        }
        public void store(byte buffer[], int offset, Object value, boolean littleEndian) {
            if (!(value instanceof Long))
                return;
            long val = (Long)value;
            
            // No problem with little or big endian
            // since a byte takes only one byte, how would have guest
            buffer[offset] = (byte)val;
        }
    },
    UNDEFINED(7, 1){
        public Object get(byte buffer[], int count, int valueOffset, boolean littleEndian){
            return BYTE.get(buffer, count, valueOffset, littleEndian);
        };
        public void store(byte buffer[], int offset, Object value, boolean littleEndian){
            BYTE.store(buffer, offset, value, littleEndian);
        };
    },
    SSHORT(8, 2){
        public Object get(byte buffer[], int count, int valueOffset, boolean littleEndian){
            Long ret[] = new Long[count];

            for (int i = 0; i < count; i++) {
                long l = (Long) SHORT.get(buffer, 1, valueOffset + i * 2, littleEndian);

                // Convert from "signed two's-complement scheme"
                if (l >= 1 << 15)
                    l -= 1 << 16;

                ret[i] = l;
            }

            if (count == 1)
                return ret[0];
            return ret;
        }
        public void store(byte buffer[], int offset, Object value, boolean littleEndian) {
            if (!(value instanceof Long))
                return;
            long val = (Long)value;

            // Convert into "signed two's-complement scheme"
            if (val < 0)
                val += (long)1 << 16;
            
            SHORT.store(buffer, offset, val, littleEndian);
        }
    },
    SLONG(9, 4){
        public Object get(byte buffer[], int count, int valueOffset, boolean littleEndian){
            Long ret[] = new Long[count];

            for (int i = 0; i < count; i++) {
                long l = (Long) LONG.get(buffer, 1, valueOffset + i * 4, littleEndian);

                // Convert from "signed two's-complement scheme"
                if (l >= (long)1 << 31)
                    l -= (long)1 << 32;

                ret[i] = l;
            }

            if (count == 1)
                return ret[0];
            return ret;
        }
        public void store(byte buffer[], int offset, Object value, boolean littleEndian) {
            if (!(value instanceof Long))
                return;
            long val = (Long)value;

            // Convert into "signed two's-complement scheme"
            if (val < 0)
                val += (long)1 << 32;
            
            LONG.store(buffer, offset, val, littleEndian);
        }
    },

    SRATIONAL(10, 8){
        public Object get(byte buffer[], int count, int valueOffset, boolean littleEndian){
            Rational<Long> ret[] = new Rational[count];

            
            for (int i = 0; i < count; i++) 
                ret[i] = new Rational<Long>((Long)SLONG.get(buffer, 1, i*8 + valueOffset, littleEndian), 
                                            (Long)SLONG.get(buffer, 1, i*8 + valueOffset + 4, littleEndian));
            
            if (count == 1)
                return ret[0];
            
            return ret;
        }
        public void store(byte buffer[], int offset, Object value, boolean littleEndian) {
            if (!(value instanceof Rational))
                return;
            Rational<Long> val = (Rational)value;
            
            SLONG.store(buffer, offset, val.getNumerator(), littleEndian);
            SLONG.store(buffer, offset + 4, val.getDenominator(), littleEndian);
        }
        
    },
    FLOAT(11, 4){
        public Object get(byte buffer[], int count, int valueOffset, boolean littleEndian){
            Float ret[] = new Float[count];

            for (int i = 0; i < count; i++) {
                long l = (Long) SLONG.get(buffer, 1, valueOffset + i * 4, littleEndian);

                ret[i] = Float.intBitsToFloat((int)l);
            }

            if (count == 1)
                return ret[0];
            return ret;
        }
        public void store(byte buffer[], int offset, Object value, boolean littleEndian) {
            if (!(value instanceof Float))
                return;
            float val = (Float)value;

            int intBits = Float.floatToIntBits(val);
            
            SLONG.store(buffer, offset, (long)intBits, littleEndian);
        }
    },
    DOUBLE(12, 8){
        public Object get(byte buffer[], int count, int valueOffset, boolean littleEndian){
            Double ret[] = new Double[count];
            try {
                ByteArrayInputStream testStream = new ByteArrayInputStream(buffer);
                DataInputStream data = new DataInputStream(testStream);
                data.skip(valueOffset);
                for (int i = 0; i < count; i++) {

                    ret[i] = data.readDouble();
                    
//                    long l = (Long) getLong(buffer, 1, valueOffset + i * 8, littleEndian, 8);
    
//                    ret[i] = Double.longBitsToDouble(l);
                }
                
                data.close();
                testStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (count == 1)
                return ret[0];
            return ret;

        
        }
        /*
         * 
         * This could be a lot more efficient
         */
        public void store(byte buffer[], int offset, Object value, boolean littleEndian) {
            
            if (!(value instanceof Double))
                return;
            double val = (Double)value;

            try {
                ByteArrayOutputStream testStream = new ByteArrayOutputStream(8);
                DataOutputStream data = new DataOutputStream(testStream);

                data.writeDouble(val);
                
                for (int i = 0; i < 8; i++)
                    buffer[offset + i] = testStream.toByteArray()[i];
                
                data.close();
                testStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

//            long intBits = Double.doubleToLongBits(val);
//            storeLong(buffer, offset, intBits, littleEndian, 8);
        }
    };
    
    // More types making my live easier
    // they are parts of UNDEFINED TYPE
//    ASCII_(-1, 1); // Unterminated ascii, read ascii string, exactly the length of count


    final int markerCode;
    final int size;

    
    public int getSize() {
        return size;
    }

    public void storeLong(byte buffer[], int offset, Object value, boolean littleEndian, int length) {
        if (!(value instanceof Long))
            return;
        long val = (Long)value;

        for (int i = 0, j = length - 1; i < length; i++, j--)
            buffer[offset + ((littleEndian)?j:i)] = (byte) (val >> 8*j);
    }

    public Object getLong(byte buffer[], int count, int offset, boolean littleEndian, int length){
        Long ret[] = new Long[count];

        for (int i = 0; i < count; i++) {
            ret[i] = (long)0;
            for (int k = 0, j = length - 1; k < length; k++, j--) {
                ret[i] <<= 8;
                ret[i] += (long)getUnsignedByte(buffer[offset + i*length + ((littleEndian)?j:k)]);
            }
        }
        
        if (count == 1)
            return ret[0];
        return ret;
    }
    
    
    public static void main(String[] args) {
        byte buffer[] = new byte[100];

        Float floatArrayOrg[] = {(float)1.0, (float)0.1};
        Double doubleArrayOrg[] = {1.051212333333333333424324, 0.1};
        Long longArrayOrg[] = {(long)0, (long)100, (long)255, (long)65535, (long)65536, ((long)1 << 32) - 1};
        Long shortArrayOrg[] = {(long)0, (long)100, (long)255, (long)65535 };
        Long byteArrayOrg[] = {(long)0, (long)100, (long)255};
        
        Long signedLongArrayOrg[] = {(long)0, (long)100, (long)255, (long)65535, (long)65536, (long)-100, (long)-255, (long)-65535, (long)-65536, ((long)1 << 31) - 1, -((long)1 << 31)};
        Long signedShortArrayOrg[] = {(long)0, (long)100, (long)255, (long)-100, (long)-255};
        Long signedByteArrayOrg[] = {(long)0, (long)1, (long)-1, (long)127, (long)-128};

        Rational rationalOrg[] = { new Rational<Long>((long)0, (long)100), 
                                            new Rational<Long>((long)101, (long)255),
                                            new Rational<Long>((long)256, (long)65535),
                                            new Rational<Long>((long)65536, (long)75000)
                };
        Rational signedRationalOrg[] = { new Rational<Long>((long)0, (long)-100), 
                new Rational<Long>((long)-101, (long)255),
                new Rational<Long>((long)256, (long)-65535),
                new Rational<Long>((long)-65536, (long)75000)
        };
        
        String strsOrg[] = { "Hello ", "world" };

        
        Map<IFD_Type, Object[]>  map = new EnumMap<IFD_Type, Object[]>(IFD_Type.class);
        map.put(DOUBLE, doubleArrayOrg);
        map.put(FLOAT, floatArrayOrg);
        map.put(LONG, longArrayOrg);
        map.put(SHORT, shortArrayOrg);
        map.put(SLONG, signedLongArrayOrg);
        map.put(SSHORT, signedShortArrayOrg);
        map.put(RATIONAL, rationalOrg);
        map.put(SRATIONAL, signedRationalOrg);
        map.put(BYTE, byteArrayOrg);
        map.put(SBYTE, signedByteArrayOrg);
        map.put(ASCII, strsOrg);
        
        for (IFD_Type type : values()) {
            if (!map.containsKey(type))
                continue;
            
            System.out.println();
            System.out.println("Test " + type  + " (org / little endian / big endian)");

            Object org[] = (Object[]) map.get(type);
            
            int count = type.storeArray(buffer, 0, org, true);
            Object littleEndian[] = (Object[]) type.get(buffer, count, 0, true);
            count = type.storeArray(buffer, 0, org, false);
            Object bigEndian[] = (Object[]) type.get(buffer, count, 0, false);

            for (int i = 0; i < org.length; i++)
                System.out.println(org[i] + " " + littleEndian[i] + " " + bigEndian[i]);
            
        }
        
/*        try {
            ByteArrayOutputStream testStream = new ByteArrayOutputStream(1024);
            DataOutputStream data = new DataOutputStream(testStream);

            DOUBLE.store(buffer, 0, 13.05, false);
            data.writeDouble(13.05);
            
            for (int i = 0; i < 8; i++)
                System.out.println(buffer[i] + " "+  testStream.toByteArray()[i]);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        
    }
    

    
    protected int getUnsignedByte(byte b) {
        return (int) b & 0xFF;
    }

    public abstract Object get(byte buffer[], int count, int valueOffset, boolean littleEndian);
    public abstract void store(byte buffer[], int offset, Object value, boolean littleEndian);

    public int storeArray(byte buffer[], int offset, Object value, boolean littleEndian){
        if (!value.getClass().isArray()) {
            store(buffer, offset, value, littleEndian);
            return 1;
        }
            
        
        for (int i = 0; i < ((Object[])value).length; i++) 
            store(buffer, offset + size*i, ((Object[])value)[i], littleEndian);
        
        return ((Object[])value).length;
    }
    public int storeArray(byte buffer[], int offset, Object value, boolean littleEndian, int arraySize){
        if (arraySize == 0)
            return 0;
        
        if (!value.getClass().isArray()) {
            store(buffer, offset, value, littleEndian);
            for (int i = 1; i < arraySize; i++)
                for (int j = 0; j < size; j++)
                    buffer[i*size + j] = 0;
            return arraySize;
        }
            
        
        for (int i = 0; i < arraySize; i++) 
            if (i < ((Object[])value).length)
                store(buffer, offset + size*i, ((Object[])value)[i], littleEndian);
            else
                for (int j = 0; j < size; j++)
                    buffer[i*size + j] = 0;
        
        return arraySize;
    }
    public Object get(InputStream istream, int count, boolean littleEndian) throws IOException {
        byte buffer[] = new byte[count * size];
        
        istream.read(buffer);
        
        return get(buffer, count, 0, littleEndian);
    }
    public Object getSecure(BufferedInputStream bStream, int count, long valueOffset, boolean littleEndian) throws IOException {
        return getSecure(bStream, 0, count, valueOffset, littleEndian);
    }
    public Object getSecure(BufferedInputStream bStream, long markOffset, int count, long valueOffset, boolean littleEndian) throws IOException {
        if (size <= 4 && count <= (4 / size)) {
            byte buffer[] = new byte[count * size];
            buffer = new byte[4];
            LONG.store(buffer, 0, valueOffset, littleEndian);
            valueOffset = 0;
            return get(buffer, count, 0, littleEndian);
        }

        bStream.reset();
        bStream.skip(valueOffset - markOffset);
        
        return get(bStream, count, littleEndian);
    }
    
    /**
     * This function converts the valueOffset to a value if it 
     * allready contains the value.
     * 
     * @param buffer
     * @param count
     * @param valueOffset
     * @param littleEndian
     * @return
     */
    public Object getSecure(byte buffer[], int count, int valueOffset, boolean littleEndian){
        if (size <= 4 && count <= (4 / size)) {
            buffer = new byte[4];
            LONG.store(buffer, 0, valueOffset, littleEndian);
            valueOffset = 0;
        }
        return get(buffer, count, valueOffset, littleEndian);
    }
    
    
    IFD_Type(final int markerCode, final int size) {
        this.markerCode = markerCode;
        this.size = size;
        
    }

    public int getMarkerCode() {
        return markerCode;
    }
    
    public boolean isMarker(int markerCode) {
        return markerCode == this.markerCode;
    }

    public static IFD_Type getMarkerFromCode(int markerCode) {
        for (IFD_Type marker : values())
            if (markerCode == marker.markerCode)
                return marker;
        return null;
    }
}
