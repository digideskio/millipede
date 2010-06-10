package ch.cyberduck;

/*
 *  Copyright (c) 2007 David Kocher. All rights reserved.
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

//import ch.cyberduck.ui.cocoa.application.*;
//import ch.cyberduck.ui.cocoa.foundation.NSArray;
//import ch.cyberduck.ui.cocoa.foundation.NSIndexSet;
//import ch.cyberduck.ui.cocoa.foundation.NSObject;
//import ch.cyberduck.ui.cocoa.foundation.NSURL;

import org.apache.log4j.Logger;
//import org.rococoa.cocoa.foundation.NSInteger;
//import org.rococoa.cocoa.foundation.NSPoint;
//import org.rococoa.cocoa.foundation.NSUInteger;

/**
 * @version $Id$
 */
public abstract class ListDataSource extends ProxyController { // implements NSTableView.DataSource, NSDraggingSource {
    private static Logger log = Logger.getLogger(ListDataSource.class);

//    public void tableView_setObjectValue_forTableColumn_row(NSTableView view, NSObject value, NSTableColumn tableColumn, NSInteger row) {
//        throw new RuntimeException("Not editable");
//    }
//
//    public boolean tableView_writeRowsWithIndexes_toPasteboard(NSTableView view, NSIndexSet rowIndexes, NSPasteboard pboard) {
//        return false;
//    }
//
//    public NSArray tableView_namesOfPromisedFilesDroppedAtDestination_forDraggedRowsWithIndexes(NSTableView view, final NSURL dropDestination, NSIndexSet rowIndexes) {
//        return NSArray.array();
//    }
//
//    public NSUInteger tableView_validateDrop_proposedRow_proposedDropOperation(NSTableView view, NSDraggingInfo draggingInfo, NSInteger row, NSUInteger operation) {
//        return NSDraggingInfo.NSDragOperationNone;
//    }
//
//    public boolean tableView_acceptDrop_row_dropOperation(NSTableView view, NSDraggingInfo draggingInfo, NSInteger row, NSUInteger operation) {
//        return false;
//    }
//
//    public NSUInteger draggingSourceOperationMaskForLocal(boolean flag) {
//        return new NSUInteger(NSDraggingInfo.NSDragOperationMove.intValue() | NSDraggingInfo.NSDragOperationCopy.intValue());
//    }
//
//    public void draggedImage_beganAt(NSImage image, NSPoint point) {
//        log.trace("draggedImage_beganAt");
//    }
//
//    public void draggedImage_endedAt_operation(NSImage image, NSPoint point, NSUInteger operation) {
//        log.trace("draggedImage_endedAt_operation");
//    }
//
//    public void draggedImage_movedTo(NSImage image, NSPoint point) {
//        log.trace("draggedImage_movedTo");
//    }
//
//    public boolean ignoreModifierKeysWhileDragging() {
//        return false;
//    }
//
//    public NSArray namesOfPromisedFilesDroppedAtDestination(final NSURL dropDestination) {
//        return NSArray.array();
//    }
}
