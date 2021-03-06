package org.torrent.internal.transfer;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import org.merapi.helper.handlers.BarUpdateRequestHandler;
import org.merapi.helper.messages.BarUpdateRespondMessage;
import org.torrent.internal.data.BTPart;
import org.torrent.internal.data.TorrentMetaInfo;
import org.torrent.internal.data.TorrentMetaInfo.Piece;
import org.torrent.internal.peer.connection.BTConnection;
import org.torrent.internal.service.BasicContentStateService;
import org.torrent.internal.service.BasicEventDispatcherService;
import org.torrent.internal.service.ContentState;
import org.torrent.internal.service.ContentStateListener;
import org.torrent.internal.service.ContentStateService;
import org.torrent.internal.service.EventDispatcherService;
import org.torrent.internal.service.event.ContentStateEvent;
import org.torrent.internal.util.Bits;
import org.torrent.internal.util.Partitions;
import org.torrent.internal.util.Range;
import org.torrent.internal.util.SparseArray;
import org.torrent.internal.util.Time;
import org.torrent.internal.util.Validator;

public class ContentWatcher extends BTSessionListenerAdapter {
	private static final Logger LOG = Logger.getLogger(ContentWatcher.class
			.getName());

	private SparseArray<ContentState> content;
	private Bits bitField;
	private Collection<ContentStateListener> contentListeners = new CopyOnWriteArrayList<ContentStateListener>();
	private final TorrentMetaInfo contentInfo;

	private EventDispatcherService disp = new BasicEventDispatcherService();

	public ContentWatcher(TorrentMetaInfo contentInfo) {
		Validator.nonNull(contentInfo);
		this.contentInfo = contentInfo;
		content = new Partitions<ContentState>(Range.getRangeByLength(0,
				contentInfo.getLength()), ContentState.UNKOWN);
		bitField = new Bits(contentInfo.getPiecesCount());
		
	}

	private synchronized boolean hasRequired(Piece p) {
		Validator.nonNull(p);
		return content.findFirst(p.getRange(), ContentState.REQUIRED) != null;
	}

	private synchronized boolean isAvailable(int pieceIndex) {
		Range range = contentInfo.getPiece(pieceIndex).getRange();
		return range.equals(content.findFirst(range, ContentState.AVAILABLE));
	}

	private synchronized boolean isChecked(int pieceIndex) {
		return content.getRange().equals(
				content.findFirst(contentInfo.getPiece(pieceIndex).getRange(),
						ContentState.VALIDATED));
	}

	public synchronized boolean isCompleted() {
		return content.getRange().equals(
				content.findFirst(ContentState.VALIDATED));
	}

	public synchronized boolean isRequired(BTPart pi) {
		Time time = new Time();
		try {
			return content.findFirst(contentInfo.getAbsoluteRange(pi),
					ContentState.REQUIRED) != null;
		} finally {
			if (time.delta() > 100) {
				LOG.info("isRequired() time taken: " + time.delta());
			}
		}
	}

	/**
	 * Called to mark a part available. If all parts of a piece are available,
	 * an event will be dispatched.
	 * 
	 * @param pi
	 */
	public synchronized void markAvailable(BTPart pi) {
		LOG.finest("Marking available: " + pi.getIndex() + " "
				+ pi.getRange().getStart() + " " + pi.getRange().getEnd());
		content.put(contentInfo.getAbsoluteRange(pi), ContentState.AVAILABLE);
		bitField.set(pi.getIndex(), false);
		if (isAvailable(pi.getIndex())) {
			fireAvailablePiece(contentInfo.getPiece(pi.getIndex()));
		}
	}

	public synchronized void markChecked(Piece piece) {
		LOG.finest("Marking checked: " + piece);
		content.put(piece.getRange(), ContentState.VALIDATED);
		bitField.set(piece.getIndex(), true);
		fireCheckedPiece(piece);
	}

	public synchronized void markRequired(Piece piece) {
		LOG.finest("Marking required: " + piece);
		content.put(piece.getRange(), ContentState.REQUIRED);
//		if (piece.getIndex() < 100) {
//			return;
//		}
//		else {
			bitField.set(piece.getIndex(), false);
			fireRequiredRange(piece);
					
//		}

	}

	@Override
	public void removedConnection(BTConnection con) {
	}

	private void fireAvailablePiece(Piece piece) {
		LOG.info("index available: " + piece.getIndex());
		// fireEvent(new ContentStateEvent((ContentStateService) this, piece,
		// ContentState.AVAILABLE));
		
//		funzt
//		fireEvent(new ContentStateEvent(new BasicContentStateService(this.disp,
//				this.contentInfo.getPiecesCount()), piece,
//				ContentState.AVAILABLE));
		fireEvent(new ContentStateEvent(new BasicContentStateService(this.disp,
				piece.getContentInfo().getPiecesCount()), piece,
				ContentState.AVAILABLE));
		
		
		
//out to test message overflow
		BarUpdateRequestHandler.sendUpdateBarData(
				BarUpdateRespondMessage.UPDATE_BAR_DATA,
				piece.getContentInfo().getInfoHash().asHexString(),
				piece.getIndex(), piece.getRange().getLength(), 0);
		
		
		
		// BarUpdateRequestHandler.getInstance().sendUpdateBarData(BarUpdateRespondMessage.UPDATE_BAR_DATA,
		// "fireAvailablePiece, ContentWatcher", piece.getIndex(),
		// piece.getRange().getLength());
	}

	private void fireCheckedPiece(Piece piece) {
		LOG.info("index checked: " + piece.getIndex());
		// fireEvent(new ContentStateEvent((ContentStateService) this, piece,
		// ContentState.VALIDATED));
		System.out.println(this.disp);
//		funzt
//		fireEvent(new ContentStateEvent(new BasicContentStateService(this.disp,
//				this.contentInfo.getPiecesCount()), piece,
//				ContentState.VALIDATED));
		fireEvent(new ContentStateEvent(new BasicContentStateService(this.disp,
				piece.getContentInfo().getPiecesCount()), piece,
				ContentState.VALIDATED));
		
		
//out to test message overflow
//		BarUpdateRequestHandler.sendUpdateBarData(
//				BarUpdateRespondMessage.UPDATE_BAR_DATA,
//				piece.getContentInfo().getInfoHash().asHexString(),
//				piece.getIndex(), piece.getRange().getStart());
	}

	private void fireRequiredRange(Piece piece) {
		LOG.info("index required: " + piece.getIndex());
		// fireEvent(new ContentStateEvent((ContentStateService) this, piece,
		// ContentState.REQUIRED));
		
//		if (piece.getIndex() > 100) {
		fireEvent(new ContentStateEvent(new BasicContentStateService(this.disp,
				piece.getContentInfo().getPiecesCount()), piece,
				ContentState.REQUIRED));
		}
//		else {
//			fireEvent(new ContentStateEvent(new BasicContentStateService(this.disp,
//					piece.g), piece,
//					ContentState.UNKOWN));
//				
//		}
//		return;
		// BarUpdateRequestHandler.getInstance().sendUpdateBarData(BarUpdateRespondMessage.UPDATE_BAR_DATA,
		// piece.getContentInfo().getInfoHash().asHexString(), piece.getIndex(),
		// 1);
//	}

	private void fireEvent(final ContentStateEvent contentEvent) {
		LOG.info("fireEvent: " + contentEvent.getState());
		disp.invokeLater(new Runnable() {

			@Override
			public void run() {
				LOG.info("ContentListeners: " + contentListeners.size());
				for (ContentStateListener l : contentListeners) {
					// l.stateChanged(contentEvent);
					// l.requiresPiece(contentEvent);
					// switch (contentEvent.getState()) {
					// case REQUIRED: {
					// l.requiresPiece(contentEvent);
					// break;
					// }
					// case AVAILABLE: {
					// // l.receivedPiece(contentEvent);
					// l.stateChanged(contentEvent);
					// break;
					// }
					// case UNKOWN: {
					// break;
					// }
					// case VALIDATED: {
					// // l.verifiedPiece(contentEvent);
					// // l.stateChanged(contentEvent);
					// break;
					// }
					//
					// }

//					working
//					if (contentEvent.getState() == ContentState.REQUIRED) {
//						l.requiresPiece(contentEvent);
//						break;
//					} else if (contentEvent.getState() == ContentState.AVAILABLE) {
//						l.stateChanged(contentEvent);
////						l.verifiedPiece(contentEvent); //no up changed
//						break;
//					} else if (contentEvent.getState() == ContentState.UNKOWN) {
//						break;
//					} else if (contentEvent.getState() == ContentState.VALIDATED) {
//						
//						break;
//					}
					
//					l.stateChanged(contentEvent);
//					if (contentEvent.getState() == ContentState.REQUIRED) {
//						l.requiresPiece(contentEvent);
//						break;
//					} else if (contentEvent.getState() == ContentState.AVAILABLE) {
//						l.receivedPiece(contentEvent);
//						l.stateChanged(contentEvent);
//						break;
//					} else if (contentEvent.getState() == ContentState.UNKOWN) {
//						break;
//					} else if (contentEvent.getState() == ContentState.VALIDATED) {
//						l.verifiedPiece(contentEvent);
//						l.stateChanged(contentEvent);
//						break;
//					}
					
					
//					l.requiresPiece(contentEvent);
//					l.stateChanged(contentEvent);
					if (contentEvent.getState() == ContentState.REQUIRED) {
						l.requiresPiece(contentEvent);
						l.stateChanged(contentEvent);
						break;
					} else if (contentEvent.getState() == ContentState.AVAILABLE) {
						l.receivedPiece(contentEvent);
						l.stateChanged(contentEvent);
						break;
					} else if (contentEvent.getState() == ContentState.UNKOWN) {
						break;
					} else if (contentEvent.getState() == ContentState.VALIDATED) {
						l.verifiedPiece(contentEvent);
						l.stateChanged(contentEvent);
						break;
					}
				}
			}
		});
	}

	public Bits getBits() {
		return bitField.unmodifableBits();
	}

	@Override
	public String toString() {
		return "ContentWatcher " + content;
	}

	@Override
	public void addContentStateListener(ContentStateListener listener) {
		Validator.nonNull(listener);
		contentListeners.add(listener);

		for (ContentStateListener list : contentListeners) {
			LOG.info("ContentStateListener added");
			LOG.info(list.toString());
		}
	}

	@Override
	public void removeContentStateListener(ContentStateListener listener) {
		Validator.nonNull(listener);
		contentListeners.remove(listener);
	}

	@Override
	public void setAvailable(final BTPart part) {
		if (disp.isEventDispatchThread()) {
			markAvailable(part);
		} else {
			disp.invokeLater(new Runnable() {
				@Override
				public void run() {
					markAvailable(part);
				}
			});
		}
	}

	@Override
	public void setRequired(final Piece piece) {
		if (disp.isEventDispatchThread()) {
			markRequired(piece);
		} else {
			disp.invokeLater(new Runnable() {
				@Override
				public void run() {
					markRequired(piece);
				}
			});
		}
	}

	@Override
	public void setValidated(final Piece piece) {
		if (disp.isEventDispatchThread()) {
			markChecked(piece);
		} else {
			disp.invokeLater(new Runnable() {
				@Override
				public void run() {
					markChecked(piece);
				}
			});
		}
	}

	@Override
	public boolean isAvailable(BTPart part) {
		Range range = contentInfo.getAbsoluteRange(part);
		return range.equals(content.findFirst(range, ContentState.AVAILABLE));
	}

	@Override
	public boolean isRequired(Piece piece) {
		return hasRequired(piece);
	}

	@Override
	public boolean isValidated(Piece piece) {
		return isChecked(piece.getIndex());
	}
}
