package com.got.nioserver.print;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import com.got.nioserver.ReadableKeyHanlder;

public class PrintReadableKeyHanlder implements ReadableKeyHanlder {
	
	// The buffer into which we'll read data when it's available
	private ByteBuffer readBuffer = ByteBuffer.allocate(8192);

	@Override
	public void handle(SelectionKey key) {
		try {
			SocketChannel socketChannel = (SocketChannel) key.channel();
			// Clear out our read buffer so it's ready for new data
			readBuffer.clear();
			// Attempt to read off the channel
			int numRead;
			try {
				numRead = socketChannel.read(this.readBuffer);
			} catch (IOException e) {
				// The remote forcibly closed the connection, cancel
				// the selection key and close the channel.
				key.cancel();
				socketChannel.close();
				return;
			}
			if (numRead == -1) {
				// Remote entity shut the socket down cleanly. Do the
				// same from our end and cancel the channel.
				key.channel().close();
				key.cancel();
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
