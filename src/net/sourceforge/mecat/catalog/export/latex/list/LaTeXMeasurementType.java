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
 * Created on Jan 5, 2006
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.export.latex.list;

public enum LaTeXMeasurementType {
    /**
     * NOLINEBREAK does what it says. This column will have
     * no entrys with a linebreak.
     * 
     * FIXEDLEN the columnwidth is exactly the value of 'collen'
     * 
     * VARLEN over all columns with VARLEN, the rest of the posible len
     * will be split. The size of every segment will be relative to 'collen'.
     */
    NOLINEBREAK, FIXEDLEN, VARLEN
}
