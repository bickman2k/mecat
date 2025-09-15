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
 * Created on Jul 18, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filesystem.mime.mp3.frames;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import net.sourceforge.mecat.catalog.option.Options;

public abstract class AbstractFrame implements Frame {

    String name = null;
    
    final String identifier;

    Map<String, Boolean> flags = new LinkedHashMap<String, Boolean>();
    
    
    public AbstractFrame(String identifier) {
        this.identifier = identifier;
    }
    
    public String getIdentifier() {
        return identifier;
    }
    
    public String getDescription() {
        return Options.getI18N(this.getClass()).getString(identifier);
    }

    private String extractName() {
        String str = getDescription();
        int i1 = str.indexOf("\'");
        if (i1 == -1)
            return "";
        int i2 = str.indexOf("\'", i1 + 1);
        if (i2 == -1)
            return "";
        return str.substring(i1 + 1, i2);
    }
    
    public String getName() {
        // Construct name on demand
        if (name == null) 
            name = extractName();

        return name;
    }
    
    public String toString() {
        return getName();
    }

    public boolean getFlag(String flagName) {
        if (!flags.containsKey(flagName))
            return false;
        return flags.get(flagName);
    }

    public Set<String> getFlags() {
        Set<String> ret = new LinkedHashSet<String>(flags.keySet());
        
        for (String str : flags.keySet())
            if (flags.get(str) == null || !flags.get(str))
                ret.remove(str);
        
        return ret;
    }

    public void setFlag(String flagName, boolean value) {
        flags.put(flagName, value);
    }

    public void setFalgs(Map<String, Boolean> map) {
        flags.putAll(map);
    }
    
    
}
