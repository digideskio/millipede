package org.opendedup.collections.threads;

import org.opendedup.collections.AbstractMap;

public class SyncThread implements Runnable {
	AbstractMap map;

	public SyncThread(AbstractMap m) {
		map = m;
		Thread th = new Thread(this);
		th.start();
	}

	@Override
	public void run() {
		while (!map.isClosed()) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				break;
			}
			try {
				map.sync();
			} catch (Exception e) {
			}

		}
	}

}
