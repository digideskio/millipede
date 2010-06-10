package ch.cyberduck.i18n;

/*
 *  Copyright (c) 2009 David Kocher. All rights reserved.
 *  http://cyberduck.ch/
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  Bug fixes, suggestions and comments should be sent to:
 *  dkocher@cyberduck.ch
 */

import ch.cyberduck.core.i18n.Locale;
import ch.cyberduck.core.i18n.LocaleFactory;
//import ch.cyberduck.ui.cocoa.foundation.NSBundle;

import org.apache.commons.lang.StringUtils;

/**
 * @version $Id$
 */
public class BundleLocale extends Locale {

    public static void register() {
        LocaleFactory.addFactory(Factory.NATIVE_PLATFORM, new Factory());
    }

    private static class Factory extends LocaleFactory {
        @Override
        protected Locale create() {
            return new BundleLocale();
        }
    }

	@Override
	public String get(String key, String table) {
		// TODO Auto-generated method stub
		return "";
	}

//    @Override
//    public String get(final String key, final String table) {
//        if(StringUtils.isEmpty(table)) {
//            return NSBundle.localizedString(key, "Localizable");
//        }
//        return NSBundle.localizedString(key, table);
//    }
}