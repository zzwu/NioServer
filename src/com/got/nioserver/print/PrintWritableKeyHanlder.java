package com.got.nioserver.print;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import com.got.nioserver.WritableKeyHanlder;

public class PrintWritableKeyHanlder implements WritableKeyHanlder {

	@Override
	public void handle(SelectionKey key) {
		try {
			SocketChannel socketChannel = (SocketChannel) key.channel();
			socketChannel.write(ByteBuffer.wrap("Hello,socket!".getBytes()));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
