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
 * Created on Jul 28, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filesystem.mime.mp3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import net.sourceforge.mecat.catalog.filesystem.mime.mp3.frames.AttachedPictureFrame_2_2;
import net.sourceforge.mecat.catalog.filesystem.mime.mp3.frames.AttachedPictureFrame_2_3;
import net.sourceforge.mecat.catalog.filesystem.mime.mp3.frames.CommentsFrame_2_2;
import net.sourceforge.mecat.catalog.filesystem.mime.mp3.frames.CommentsFrame_2_3;
import net.sourceforge.mecat.catalog.filesystem.mime.mp3.frames.Frame;
import net.sourceforge.mecat.catalog.filesystem.mime.mp3.frames.Frame_2_2;
import net.sourceforge.mecat.catalog.filesystem.mime.mp3.frames.Frame_2_3;
import net.sourceforge.mecat.catalog.filesystem.mime.mp3.frames.Frame_2_4;
import net.sourceforge.mecat.catalog.filesystem.mime.mp3.frames.GeneralEncapsulatedObjectFrame_2_2;
import net.sourceforge.mecat.catalog.filesystem.mime.mp3.frames.GeneralEncapsulatedObjectFrame_2_3;
import net.sourceforge.mecat.catalog.filesystem.mime.mp3.frames.InvolvedPeopleListFrame_2_2;
import net.sourceforge.mecat.catalog.filesystem.mime.mp3.frames.InvolvedPeopleListFrame_2_3;
import net.sourceforge.mecat.catalog.filesystem.mime.mp3.frames.MusikCDIdentifierFrame_2_2;
import net.sourceforge.mecat.catalog.filesystem.mime.mp3.frames.MusikCDIdentifierFrame_2_3;
import net.sourceforge.mecat.catalog.filesystem.mime.mp3.frames.PlayCounterFrame_2_2;
import net.sourceforge.mecat.catalog.filesystem.mime.mp3.frames.PlayCounterFrame_2_3;
import net.sourceforge.mecat.catalog.filesystem.mime.mp3.frames.PopularimeterFrame_2_3;
import net.sourceforge.mecat.catalog.filesystem.mime.mp3.frames.PrivateFrame_2_3;
import net.sourceforge.mecat.catalog.filesystem.mime.mp3.frames.TextFrame_2_2;
import net.sourceforge.mecat.catalog.filesystem.mime.mp3.frames.TextFrame_2_3;
import net.sourceforge.mecat.catalog.filesystem.mime.mp3.frames.URLFrame_2_2;
import net.sourceforge.mecat.catalog.filesystem.mime.mp3.frames.URLFrame_2_3;
import net.sourceforge.mecat.catalog.filesystem.mime.mp3.frames.UniqueFileIdentifierFrame_2_2;
import net.sourceforge.mecat.catalog.filesystem.mime.mp3.frames.UniqueFileIdentifierFrame_2_3;
import net.sourceforge.mecat.catalog.filesystem.mime.mp3.frames.UserDefinedTextFrame_2_2;
import net.sourceforge.mecat.catalog.filesystem.mime.mp3.frames.UserDefinedTextFrame_2_3;
import net.sourceforge.mecat.catalog.filesystem.mime.mp3.frames.UserDefinedURLFrame_2_2;
import net.sourceforge.mecat.catalog.filesystem.mime.mp3.frames.UserDefinedURLFrame_2_3;
import net.sourceforge.mecat.catalog.option.Options;

public class KnownID3V2Frames {
    
    static KnownID3V2Frames knownID3V2Frames;
    
    // See at http://www.id3.org/id3v2-00.txt for documentation
    final List<Frame_2_2> knownFrames_2_2;
    final Map<String, Frame_2_2> knownFramesMap_2_2 = new HashMap<String, Frame_2_2>();

    final List<Frame_2_3> knownFrames_2_3;
    final Map<String, Frame_2_3> knownFramesMap_2_3 = new HashMap<String, Frame_2_3>();

    final List<Frame_2_4> knownFrames_2_4;
    final Map<String, Frame_2_4> knownFramesMap_2_4 = new HashMap<String, Frame_2_4>();

    final Map<String, ? extends Frame>[] knownFramesMap = new Map[5];
    
    ResourceBundle res = Options.getI18N(KnownID3V2Frames.class);

    KnownID3V2Frames() {
        // Frames for version 2.2
        knownFrames_2_2 = new Vector<Frame_2_2>();
        for (String identifier : Options.getI18N(TextFrame_2_2.class).getKeySet()) 
            knownFrames_2_2.add(new TextFrame_2_2(identifier));
        for (String identifier : Options.getI18N(URLFrame_2_2.class).getKeySet()) 
            knownFrames_2_2.add(new URLFrame_2_2(identifier));

        knownFrames_2_2.add(new UserDefinedTextFrame_2_2());
        knownFrames_2_2.add(new UserDefinedURLFrame_2_2());
        knownFrames_2_2.add(new UniqueFileIdentifierFrame_2_2());
        knownFrames_2_2.add(new MusikCDIdentifierFrame_2_2());
        knownFrames_2_2.add(new CommentsFrame_2_2());
        knownFrames_2_2.add(new InvolvedPeopleListFrame_2_2());
        knownFrames_2_2.add(new AttachedPictureFrame_2_2());
        knownFrames_2_2.add(new GeneralEncapsulatedObjectFrame_2_2());
        knownFrames_2_2.add(new PlayCounterFrame_2_2());
        
        for (Frame_2_2 frame : knownFrames_2_2) 
            knownFramesMap_2_2.put(frame.getIdentifier(), frame);
        knownFramesMap[2] = knownFramesMap_2_2;

        System.out.println(res.getString("[NUM] known Frames for version 2.2").replaceAll("\\[NUM\\]", "" + knownFrames_2_2.size()));
    
        // Frames for version 2.3
        knownFrames_2_3 = new Vector<Frame_2_3>();
        for (String identifier : Options.getI18N(TextFrame_2_3.class).getKeySet()) 
            knownFrames_2_3.add(new TextFrame_2_3(identifier));
        for (String identifier : Options.getI18N(URLFrame_2_3.class).getKeySet()) 
            knownFrames_2_3.add(new URLFrame_2_3(identifier));

        knownFrames_2_3.add(new UserDefinedTextFrame_2_3());
        knownFrames_2_3.add(new UserDefinedURLFrame_2_3());
        knownFrames_2_3.add(new UniqueFileIdentifierFrame_2_3());
        knownFrames_2_3.add(new MusikCDIdentifierFrame_2_3());
        knownFrames_2_3.add(new CommentsFrame_2_3());
        knownFrames_2_3.add(new InvolvedPeopleListFrame_2_3());
        knownFrames_2_3.add(new AttachedPictureFrame_2_3());
        knownFrames_2_3.add(new GeneralEncapsulatedObjectFrame_2_3());
        knownFrames_2_3.add(new PlayCounterFrame_2_3());
        
        // Frame new in version 2.3
        knownFrames_2_3.add(new PrivateFrame_2_3());
        // Frame new in version 2.4
        // they are added to the frames of version 2.3 because 
        // some implementation put them into version 2.3 frames
        // and this way they are shown properly
        knownFrames_2_3.add(new PopularimeterFrame_2_3());
        
        for (Frame_2_3 frame : knownFrames_2_3) 
            knownFramesMap_2_3.put(frame.getIdentifier(), frame);
        knownFramesMap[3] = knownFramesMap_2_3;

        System.out.println(res.getString("[NUM] known Frames for version 2.3").replaceAll("\\[NUM\\]", "" + knownFrames_2_3.size()));

        // Frames for version 2.4
        knownFrames_2_4 = new Vector<Frame_2_4>();
        // Contains deprecated TDAT, TIME, TORY, TRDA, TSIZ and TYER frames
        // deprecated frames are still read since reading those false frames
        //  does not help and this way the information is shown
        for (String identifier : Options.getI18N(TextFrame_2_3.class).getKeySet()) 
            knownFrames_2_4.add(new TextFrame_2_3(identifier));
        for (String identifier : Options.getI18N(URLFrame_2_3.class).getKeySet()) 
            knownFrames_2_4.add(new URLFrame_2_3(identifier));

        knownFrames_2_4.add(new UserDefinedTextFrame_2_3());
        knownFrames_2_4.add(new UserDefinedURLFrame_2_3());
        knownFrames_2_4.add(new UniqueFileIdentifierFrame_2_3());
        knownFrames_2_4.add(new MusikCDIdentifierFrame_2_3());
        knownFrames_2_4.add(new CommentsFrame_2_3());
        // The frame Involved People List is decprecated since 2.4
        // deprecated frames are still read since reading those false frames
        //  does not help and this way the information is shown
        knownFrames_2_4.add(new InvolvedPeopleListFrame_2_3());
        knownFrames_2_4.add(new AttachedPictureFrame_2_3());
        knownFrames_2_4.add(new GeneralEncapsulatedObjectFrame_2_3());
        knownFrames_2_4.add(new PlayCounterFrame_2_3());
        
        // Frame new in version 2.3
        knownFrames_2_4.add(new PrivateFrame_2_3());
        // Frame new in version 2.4
        // they are added to the frames of version 2.3 because 
        // some implementation put them into version 2.3 frames
        knownFrames_2_4.add(new PopularimeterFrame_2_3());
        
        for (Frame_2_4 frame : knownFrames_2_4) 
            knownFramesMap_2_4.put(frame.getIdentifier(), frame);
        knownFramesMap[4] = knownFramesMap_2_4;


        System.out.println(res.getString("[NUM] known Frames for version 2.4").replaceAll("\\[NUM\\]", "" + knownFrames_2_4.size()));
    }
    
    public static KnownID3V2Frames getDefaultKnownID3V2Frames() {
        if (knownID3V2Frames == null)
            knownID3V2Frames = new KnownID3V2Frames();
        return knownID3V2Frames;
    }
}
