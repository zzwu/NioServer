package com.got.nioserver.print;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import com.got.nioserver.WritableKeyHanlder;

public class PrintWritableKeyHanlder implements WritableKeyHanlder {

	@Override
	public void handle(SelectionKey key, Selector selector) {
		try {
			SocketChannel channel = (SocketChannel) key.channel();
			channel.write(ByteBuffer.wrap("msg from server.".getBytes()));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
