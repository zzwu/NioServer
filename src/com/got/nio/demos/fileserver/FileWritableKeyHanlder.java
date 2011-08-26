package com.got.nio.demos.fileserver;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

import com.got.nioserver.WritableKeyHanlder;

public class FileWritableKeyHanlder implements WritableKeyHanlder {

	@Override
	public void handle(SelectionKey key, Selector selector) {
		if (null == key.attachment()) {
			System.out.println("key.attachment() is null ... ");
			return;
		}
		FileSender fs = (FileSender)key.attachment();
		new Thread(fs).start();
	}

}
