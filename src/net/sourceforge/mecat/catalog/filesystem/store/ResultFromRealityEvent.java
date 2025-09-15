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
 * Created on May 27, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filesystem.store;

import java.io.File;
import java.io.IOException;

public class ResultFromRealityEvent {

    enum ResultFromRealityEventType {
        DirectoryFound, FileFound, SizeChanged, 
        MD5Started, MD5Finished, MD5FileFinished, 
        MD5SizeChanged, MD5Interupted, MD5IOException,
        FinishingStarted, FinishingSteped, FinishingFinished, 
        ResultComputed, Interupted, LogEntry, 
        FindTagStarted, FindTagFileFinished, FindTagFinished
    }
    
    final ResultFromRealityEventType type;
    final String log;
    final long position;
    final File file;
    final IOException ioException;
    
    
    public ResultFromRealityEvent(MD5ThreadEvent md5ThreadEvent, long position) {
        this.type = ResultFromRealityEventType.MD5SizeChanged;
        this.log = null;
        this.finishingPosition = 0;
        this.finishingRange = 0;
        if (md5ThreadEvent == null)
            this.position = position;
        else
            this.position = md5ThreadEvent.getPosition() + position;
        this.file = null;
        this.ioException = null;
    }

    public ResultFromRealityEvent(MD5ThreadEvent md5ThreadEvent, File file) {
        this.type = ResultFromRealityEventType.MD5IOException;
        this.log = null;
        this.finishingPosition = 0;
        this.finishingRange = 0;
        this.position = 0;
        this.file = file;
        this.ioException = md5ThreadEvent.getIOException();
    }

    public ResultFromRealityEvent(MD5ThreadEvent md5ThreadEvent) {
        this.type = ResultFromRealityEventType.MD5Interupted;
        this.log = null;
        this.finishingPosition = 0;
        this.finishingRange = 0;
        this.position = -1;
        this.file = null;
        this.ioException = null;
    }

    public ResultFromRealityEvent(String log) {
        type = ResultFromRealityEventType.LogEntry;
        this.log = log;
        this.finishingPosition = 0;
        this.finishingRange = 0;
        this.position = -1;
        this.file = null;
        this.ioException = null;
    }
    
    public ResultFromRealityEvent(ResultFromRealityEventType type, long position) {
        this.type = type;
        this.log = null;
        this.finishingPosition = 0;
        this.finishingRange = 0;
        this.position = position;
        this.file = null;
        this.ioException = null;
    }

    public ResultFromRealityEvent(ResultFromRealityEventType type) {
        this.type = type;
        this.log = null;
        this.finishingPosition = 0;
        this.finishingRange = 0;
        this.position = -1;
        this.file = null;
        this.ioException = null;
    }

    public ResultFromRealityEvent(ResultFromRealityEventType type, int finishingPosition, int finishingRange) {
        this.type = type;
        this.log = null;
        this.finishingPosition = finishingPosition;
        this.finishingRange = finishingRange;
        this.position = -1;
        this.file = null;
        this.ioException = null;
    }

    public ResultFromRealityEventType getType() {
        return type;
    }

    final int finishingPosition;
    final int finishingRange;

    public int getFinishingPosition() {
        return finishingPosition;
    }

    public int getFinishingRange() {
        return finishingRange;
    }

    public String getLog() {
        return log;
    }
    
    public long getPosition() {
        return position;
    }

    public File getFile() {
        return file;
    }

    public IOException getIoException() {
        return ioException;
    }
    
    
}
