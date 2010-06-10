package ch.cyberduck.core.serializer;

/*
 * Copyright (c) 2009 David Kocher. All rights reserved.
 * http://cyberduck.ch/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * Bug fixes, suggestions and comments should be sent to:
 * dkocher@cyberduck.ch
 */

import ch.cyberduck.core.Serializable;

import java.util.List;

/**
 * @version $Id: Serializer.java 5013 2009-08-05 15:12:05Z dkocher $
 */
public interface Serializer {

    /**
     * @param value
     * @param key
     */
    void setStringForKey(String value, String key);

    /**
     * @param value
     * @param key
     */
    void setObjectForKey(Serializable value, String key);

    /**
     * @param value
     * @param key
     */
    <T extends Serializable> void setListForKey(List<T> value, String key);

    /**
     * @param <T>
     * @return
     */
    <T> T getSerialized();

}