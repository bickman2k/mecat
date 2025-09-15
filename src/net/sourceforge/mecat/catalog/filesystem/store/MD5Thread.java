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
 * Created on Jun 4, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filesystem.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.sourceforge.mecat.catalog.option.Options;

public class MD5Thread extends Thread {

    // DEBUG INFORMATION MEMORY
    long IO_TIME = 0;
    long MD5_TIME = 0;
    long EVENT_TIME = 0;
    
    long NEW_TIME = 0;
    long LAST_TIME = 0;

    static final int bufferlen = 64 * 1024;
    static final int md5Rounds = 1024;
    
    
    MessageDigest md5;
    int amountBuffers = 3;
    int maxBuffers = 1050;
    boolean stop = false;

    
    Queue<BufferWithLen> emptyBuffers = new ConcurrentLinkedQueue<BufferWithLen>();
    Queue<BufferWithLen> fullBuffers = new ConcurrentLinkedQueue<BufferWithLen>();
    
    public static boolean compareMD5IgnoreFalse(byte[] md5, byte[] _md5) {
        if (isNull(md5))
            return true;
        
        if (isNull(_md5))
            return true;
        
        for (int i = 0; i < 16; i++)
            if (md5[i] != _md5[i])
                return false;
        return true;
    }
    
    public static boolean isNull(byte[] md5) {
        for (int i = 0; i < 16; i++)
            if (md5[i] != 0)
                return false;
        return true;
    }
    
    public MD5Thread() throws NoSuchAlgorithmException {
        md5 = MessageDigest.getInstance("MD5");
        for (int i = 0; i < amountBuffers; i++)
            emptyBuffers.add(new BufferWithLen(ByteBuffer.allocate(bufferlen)));
        
            
    }
    
    public BufferWithLen getEmptyBuffer() {
        BufferWithLen buffer = null;
        buffer = emptyBuffers.poll();
        
        if (buffer != null)
            return buffer;

        if (amountBuffers < maxBuffers) {
            amountBuffers++;
            return new BufferWithLen(ByteBuffer.allocate(bufferlen));
        }

//        fireLogEntry("MAX BUFFERS REACHED, CPU SLOWER THAN DRIVE.");
        
        while (buffer == null) {
            yield();
            buffer = emptyBuffers.poll();
        }
        
        return buffer;
    }
    
    public void run() {
        while (true) {
            yield();

            if (stop)
                return;

            // Don't do multiple things with the md5 at the same time
            while (!fullBuffers.isEmpty())
                synchronized (md5) {
                    BufferWithLen buffer = fullBuffers.poll();
                    if (buffer != null) {
                        md5.update(buffer.getBuffer().array(), 0, buffer.getLen());
                        buffer.getBuffer().clear();
                        emptyBuffers.add(buffer);
                    }
                }
        }
    }
    
    public void reset() {
//        md5.reset();
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    
    public void update(BufferWithLen buffer) {
        fullBuffers.add(buffer);
//        notifyAll();
    }

    public byte[] digest() {
        while (!fullBuffers.isEmpty())
            yield();
/*                try {
                wait(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/

        // Don't do multiple things with the md5 at the same time
        synchronized(md5) {
            return md5.digest();
        }
    }

    public void free(BufferWithLen buffer) {
        buffer.getBuffer().clear();
        emptyBuffers.add(buffer);
    }
    

    class MD5InteruptedException extends Exception{};

    public byte[] getMD5SUM(File file) {
        //TODO think about using Fast MD5 from
        // http://www.twmacinta.com/myjava/fast_md5.php

        FileInputStream input = null;
        FileChannel channel = null;
        
        try {

            if (stop)
                throw new MD5InteruptedException();
            
//            MessageDigest md5 = MessageDigest.getInstance("MD5");
            /*md5Thread.*/reset();

            int i = 0;
            
//            ByteBuffer buffer = ByteBuffer.allocate(bufferlen);
            BufferWithLen buffer = /*md5Thread.*/getEmptyBuffer();
            int len;
            
            input = new FileInputStream(file);
            // Use file channel because it is interuptable
            channel = input.getChannel();

            if (Options.DEBUG)
                LAST_TIME = System.currentTimeMillis(); 
            
            while ((len = channel.read(buffer.getBuffer())) != -1) {
                // Compute time needed for IO-Operation
                if (Options.DEBUG) {
                    NEW_TIME = System.currentTimeMillis();
                    IO_TIME += NEW_TIME - LAST_TIME;
                    LAST_TIME = NEW_TIME;
                }
                
                if (stop)
                    throw new MD5InteruptedException();

//                md5.update(buffer.array(), 0, len);
                buffer.setLen(len);
                /*md5Thread.*/update(buffer);
                
                // Compute time needed for MD5-Operation
                if (Options.DEBUG) {
                    NEW_TIME = System.currentTimeMillis();
                    MD5_TIME += NEW_TIME - LAST_TIME;
                    LAST_TIME = NEW_TIME;
                }

//                buffer.clear();
                buffer = /*md5Thread.*/getEmptyBuffer();

                // Compute time needed for IO-Operation
                if (Options.DEBUG) {
                    NEW_TIME = System.currentTimeMillis();
                    IO_TIME += NEW_TIME - LAST_TIME;
                    LAST_TIME = NEW_TIME;
                }

                // Every 1024 times we pass this position we give an event 
                // to indicate the progress. One per megabyte, the user
                // gets an event to indicate the progress
                i++;
                if (i == md5Rounds) {
                    i = 0;
                    fireMD5SizeChanged(channel.position());
                }
                // Compute time needed for IO-Operation
                if (Options.DEBUG) {
                    NEW_TIME = System.currentTimeMillis();
                    EVENT_TIME += NEW_TIME - LAST_TIME;
                    LAST_TIME = NEW_TIME;
                }
            }
            
            /*md5Thread.*/free(buffer);
            
            channel.close();
            input.close();
            return /*md5Thread.*/digest();
//            return md5.digest();
        } catch (Exception e) {
            e.printStackTrace();

            try {
                if (channel != null)
                    channel.close();
            } catch (Exception e1) {}
            try {
                if (input != null)
                    input.close();
            } catch (Exception e1) {}
            
            if (e instanceof IOException) 
                fireIOException((IOException) e, file);
            else if (e instanceof MD5InteruptedException)
                fireInterupted();
            else 
                e.printStackTrace();

            byte b[] = new byte[16];
            return b;
        }
    }
    
    Vector<MD5ThreadListener> MD5ThreadListeners = new Vector<MD5ThreadListener>();
    
    public void addMD5ThreadListener(MD5ThreadListener md5ThreadListener) {
        MD5ThreadListeners.add(md5ThreadListener);
    }
    
    public void removeMD5ThreadListener(MD5ThreadListener md5ThreadListener) {
        MD5ThreadListeners.remove(md5ThreadListener);
    }
    
    protected void fireMD5SizeChanged(long size) {
        for (MD5ThreadListener md5ThreadListener : MD5ThreadListeners)
            md5ThreadListener.sizeChanged(new MD5ThreadEvent(MD5ThreadEvent.MD5ThreadEventType.SizeChanged, size));
    }
    
    protected void fireIOException(IOException ioException, File file) {
        for (MD5ThreadListener md5ThreadListener : MD5ThreadListeners)
            md5ThreadListener.ioException(new MD5ThreadEvent(ioException, file));
    }

    protected void fireInterupted() {
        for (MD5ThreadListener md5ThreadListener : MD5ThreadListeners)
            md5ThreadListener.interupted(new MD5ThreadEvent(MD5ThreadEvent.MD5ThreadEventType.Interupted));
    }

    public long getEVENT_TIME() {
        return EVENT_TIME;
    }

    public long getIO_TIME() {
        return IO_TIME;
    }

    public long getMD5_TIME() {
        return MD5_TIME;
    }

    public long maxBufferUsed() {
        return amountBuffers * bufferlen;
    }

    
    
    
}
