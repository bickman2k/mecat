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
 * Created on Aug 10, 2005
 * @author Stephan Richard Palm
 * 
 */
package net.sourceforge.mecat.catalog.i18n.fromCatalog;

import java.util.Locale;

public interface CatalogResourceListener {

    public void completeChange(final CatalogResource source);
    public void addedLanguage(final CatalogResource source, final Locale language);
    public void removedLanguage(final CatalogResource source, final Locale language);
    public void addedKey(final CatalogResource source, final String key);
    public void removedKey(final CatalogResource source, final String key);
    public void addedTranslation(final CatalogResource source, final String key, final Locale language, final String translation);
    public void removedTranslation(final CatalogResource source, final String key, final Locale language, final String translation);
    
}