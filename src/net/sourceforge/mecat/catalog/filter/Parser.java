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
 * Created on Sep 2, 2005
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.filter;

import net.sourceforge.mecat.catalog.medium.features.BadCondition;

public class Parser {
    final String filter;
    int pos;
    
    public static boolean checkFilter(String filter) {
        Parser parser = new Parser(filter);
        try {
            parser.parse();
        } catch (BadCondition bad) {
            return false;
        }
        return true;
    };

    public Parser(final String filter) {
        this.filter = filter;
    }

    void jumpWhiteSpace() {
        if (pos == filter.length())
            return;
        while ((filter.charAt(pos) == ' ') 
                || (filter.charAt(pos) == '\t') 
                || (filter.charAt(pos) == '\n') 
                || (filter.charAt(pos) == '\r')) {
            pos++;
            if (pos == filter.length())
                return;
        }
    }
    
    void jumpWhiteSpaceNoEnd() throws BadCondition {
        jumpWhiteSpace();
        if (pos == filter.length())
            throw new BadCondition("FilterString ended unexpected.");
    }

    public Filter parse() throws BadCondition {
        if (filter == null || filter.trim().length() == 0)
            return TrueFilter.TRUE;
        return parse_intern();
    }

    
    public static String escape(String str) {
        return str.replaceAll("\\\\", "\\\\\\\\").replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)");
    }
    
    public static String unEscape(String str) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '\\') 
                buf.append(str.charAt(++i));
            else
                buf.append(str.charAt(i));
        }
        
        return buf.toString();
    }
    
    /**
     * Does the same as indexOf of the class String
     * does for normal Strings but for 
     * backslash escaped String.
     * 
     * @param str
     * @param search
     */
    public static int indexOf(String str, String search) {
        return indexOf(str, search, 0);
    }

    /**
     * Does the same as indexOf of the class String
     * does for normal Strings but for 
     * backslash escaped String.
     * 
     * @param str
     * @param search
     * @param pos
     */
    public static int indexOf(String str, String search, int pos) {
        int i = str.indexOf(search, pos);

        // If it had not be found it does not exist 
        if (i < 0)
            return i;

        // Count the number of backslashes before the found element
        int count = 0;
        while (i - count - 1 >= 0 && str.charAt(i - count -1) == '\\')
            count++;
        
        // Only with an equal amount of backslashes before the found position
        // it is a valid result
        if (count % 2 == 0)
            return i;
        
        return indexOf(str, search, i + 1);
    }
    
    private Filter parse_intern() throws BadCondition {

        Filter child_X, child_Y;
        
        jumpWhiteSpaceNoEnd();

        switch (filter.charAt(pos)) {
            // Error
            case ')' :
                throw new BadCondition("Bracket closed that has not been opened.");
            // Subnode: (x)
            // gets the whole subnode    
            case '(' :
                pos++;
                child_X = parse();
                jumpWhiteSpaceNoEnd();
                if (filter.charAt(pos) != ')') 
                    throw new BadCondition("Bracket not closed that should have been.");
                pos++;
                break;
            // negative Subnode: !(x)
            case '!' :
                boolean bracket_opened = false;
                pos++;
                jumpWhiteSpaceNoEnd();
                if (filter.charAt(pos) != '(') 
//                  throw new BadCondition("Bracket not opened that should have been.");
                    // Allow bad style negation !x, but !x and y => !(x and y)
                    bracket_opened = true;

                pos++;
                child_X = parse();
                child_X = new NotFilter(child_X); 

                // See if brackets have been opened
                if (bracket_opened) {
                    jumpWhiteSpaceNoEnd();
                    if (filter.charAt(pos) != ')')
                        throw new BadCondition("Bracket not closed that should have been.");
                    pos++;
                } else {
                    jumpWhiteSpace();  // Propabliy unnecessary
                }
                break;
            // val_node we should find the following: x(y)
            default:
                
                String rest = filter.substring(pos);
                if (rest.matches(/* Find true */ "\\s*[Tt][Rr][Uu][Ee]\\s*" + /* end a condition end '|', '&' or ')' or nothing more */ "([\\|\\&\\)].*)+")) {
                    child_X = TrueFilter.TRUE;
                    pos += 4;
                    break;
                }
                if (rest.matches(/* Find true */ "\\s*[Ff][Aa][Ll][Ss][Ee]\\s*" + /* end a condition end '|', '&' or ')' or nothing more */ "([\\|\\&\\)].*)+")) {
                    child_X = FalseFilter.FALSE;
                    pos += 5;
                    break;
                }
                    
                int value_pos = indexOf(filter, "(", pos);
                int value_end = indexOf(filter, ")", pos);
                if ((value_pos < 0) || (value_end < 0))
                    throw new BadCondition("At position " + pos + " should be a clause but there is none.");
                if (value_end < value_pos)
                    throw new BadCondition("Wild Brackets. x)y(z ");
                if (filter.substring(pos, value_pos).trim().compareToIgnoreCase(MediumFilter.IDENTIFIER) == 0)
                    child_X = new MediumFilter(filter.substring(value_pos+1, value_end).trim());
                else if (filter.substring(pos, value_pos).trim().compareToIgnoreCase(LanguageFilter.IDENTIFIER) == 0)
                    child_X = new LanguageFilter(filter.substring(value_pos+1, value_end).trim());
                else
                    child_X = new FeatureFilter(filter.substring(pos, value_pos).trim(), unEscape(filter.substring(value_pos+1, value_end).trim()));
                pos = value_end + 1;
        }

        jumpWhiteSpace();

        if (pos == filter.length())
            return child_X;

        if (filter.charAt(pos) == ')')
            return child_X;

        if ((filter.charAt(pos) == '&')
            && (filter.charAt(pos + 1) == '&')) {
            pos = pos + 2;
            Filter tmp = parse();
            jumpWhiteSpace();
            return new AndFilter(child_X, tmp);
        }
        
        if ((filter.charAt(pos) == '|')
            && (filter.charAt(pos + 1) == '|')) {
            pos = pos + 2;
            Filter tmp = parse();
            jumpWhiteSpace();
            return new OrFilter(child_X, tmp);
        }
            
        throw new BadCondition("Parser has made an internal error. Help!!! \r\n" 
                + " If you're using the last version of the Software then e-mail me this filtersetting."
                + " I'm at position " + pos + " from " + filter);
    }
}
