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
import ch.cyberduck.core.serializer.Deserializer;
import ch.cyberduck.core.serializer.DeserializerFactory;
//import ch.cyberduck.ui.cocoa.foundation.*;
//
//import org.rococoa.Rococoa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class PlistDeserializer implements Deserializer { //<NSDictionary> {

    public static void register() {
        DeserializerFactory.addFactory(Factory.NATIVE_PLATFORM, new Factory());
    }

    private static class Factory extends DeserializerFactory {//<NSDictionary> {
        @Override
        protected Deserializer create() {
//            return new PlistDeserializer(NSMutableDictionary.dictionary());
        	return new PlistDeserializer(new HashMap<Object, Object>());
        }

	@Override
	protected Deserializer create(Object dict) {
		
		return new PlistDeserializer((Map<Object, Object>) dict);
	}

//        @Override
//        protected Deserializer create(Map dict) {
//            return new PlistDeserializer(dict);
//        }
    }

//    final NSDictionary dict;
//  final NSMutableDictionary dict;
    /**
	 * @uml.property  name="dict"
	 * @uml.associationEnd  qualifier="key:java.lang.String java.util.List"
	 */
    Map<Object, Object> dict = null;

    public PlistDeserializer(Map<Object, Object> dict) {
        this.dict = dict;
    }

    public String stringForKey(String key) {
//        final NSObject value = dict.objectForKey(key);
        final Object value = dict.get(key);
    	if(null == value) {
            return null;
        }
        return value.toString();
    }

    public Map<Object, Object> objectForKey(String key) {
//        final NSObject value = dict.objectForKey(key);
        final Object value = dict.get(key);
    	if(null == value) {
            return null;
        }
//        return Rococoa.cast(value, NSDictionary.class);
    	return (Map<Object, Object>)value;
    }

    public List listForKey(String key) {
    	final List value = (List) dict.get(key);
    	if(null == value) {
            return null;
        }
//        final List<NSDictionary> list = new ArrayList<NSDictionary>();
//        final NSArray array = Rococoa.cast(value, NSArray.class);
//        final NSEnumerator enumerator = array.objectEnumerator();
//        NSObject next;
//        while((next = enumerator.nextObject()) != null) {
//            list.add(Rococoa.cast(next, NSDictionary.class));
//        }
        return value;
    }

}