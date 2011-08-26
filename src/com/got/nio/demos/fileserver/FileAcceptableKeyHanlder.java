package com.got.nio.demos.fileserver;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import com.got.nioserver.AcceptableKeyHanlder;

public class FileAcceptableKeyHanlder implements AcceptableKeyHanlder {

	@Override
	public void handle(SelectionKey key, Selector selector) {
		try {
			ServerSocketChannel serverSocket = (ServerSocketChannel)key.channel();
			SocketChannel socketChannel = serverSocket.accept();
			if (null == socketChannel) return;
			socketChannel.configureBlocking(false);
			socketChannel.register(selector, SelectionKey.OP_READ);
			System.out.println("a acceptable key from:" + socketChannel.socket());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
