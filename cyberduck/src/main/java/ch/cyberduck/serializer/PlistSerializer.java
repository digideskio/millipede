package ch.cyberduck.serializer;

/*
 * Copyright (c) 2009 David Kocher. All rights reserved.
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

import ch.cyberduck.core.Serializable;
import ch.cyberduck.core.serializer.Serializer;
import ch.cyberduck.core.serializer.SerializerFactory;
//import ch.cyberduck.ui.cocoa.foundation.NSDictionary;
//import ch.cyberduck.ui.cocoa.foundation.NSMutableArray;
//import ch.cyberduck.ui.cocoa.foundation.NSMutableDictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @version $Id: PlistSerializer.java 5433 2009-10-06 21:46:16Z dkocher $
*/
public class PlistSerializer implements Serializer {

    public static void register() {
        SerializerFactory.addFactory(Factory.NATIVE_PLATFORM, new Factory());
    }

    private static class Factory extends SerializerFactory {
        @Override
        protected Serializer create() {
            return new PlistSerializer();
        }
    }

//    final NSMutableDictionary dict;
    /**
	 * @uml.property  name="dict"
	 * @uml.associationEnd  qualifier="key:java.lang.String ch.cyberduck.core.Serializable"
	 */
    Map<Object, Object> dict = null;
    
    public PlistSerializer() {
//        this(NSMutableDictionary.dictionary());
    	this(new HashMap<Object, Object>());
    }

    public PlistSerializer(Map<Object, Object> dict) {
//    public PlistSerializer(NSMutableDictionary dict) {
        this.dict = dict;
    }

    public void setStringForKey(String value, String key) {
//        dict.setObjectForKey(value, key);
    	dict.put(key, value);
    }

    public void setObjectForKey(Serializable value, String key) {
//        dict.setObjectForKey(value.<NSDictionary>getAsDictionary(), key);
    	dict.put(key, value);
    }

    public <T extends Serializable> void setListForKey(List<T> value, String key) {
//        final NSMutableArray list = NSMutableArray.array();
//        for(Serializable serializable : value) {
//            list.addObject(serializable.<NSDictionary>getAsDictionary());
//        }
//        dict.setObjectForKey(list, key);
    	final List list = new ArrayList<T>();
    	for(Serializable serializable : value) {
    		list.add(serializable.getAsDictionary());
    	}
    }

    public Map<Object, Object> getSerialized() {
//    public NSDictionary getSerialized() {
        return dict;
    }
}
