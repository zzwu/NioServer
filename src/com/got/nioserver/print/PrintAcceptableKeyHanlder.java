package com.got.nioserver.print;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import com.got.nioserver.AcceptableKeyHanlder;

public class PrintAcceptableKeyHanlder implements AcceptableKeyHanlder {

	@Override
	public void handle(SelectionKey key, Selector selector) {
		try {
			ServerSocketChannel serverSocket = (ServerSocketChannel)key.channel();
			SocketChannel socketChannel = serverSocket.accept();
			socketChannel.configureBlocking(false);
			socketChannel.register(selector, SelectionKey.OP_READ);
			System.out.println("a acceptable key from:" + socketChannel.socket());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
