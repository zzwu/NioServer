package com.got.nio.demos.fileserver;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import com.got.nioserver.WritableKeyHanlder;

public class FileWritableKeyHanlder implements WritableKeyHanlder {

	@Override
	public void handle(SelectionKey key, Selector selector) {
		if (null == key.attachment()) {
			System.out.println("key.attachment() is null ... ");
			return;
		}
		FileSender sender = (FileSender)key.attachment();
		System.out.println("get write request.attachemnt fileName : " + sender);
		//send file and register read
		sender.send((SocketChannel) key.channel());
	}

}
