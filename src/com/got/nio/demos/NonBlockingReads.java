package com.got.nio.demos;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Iterator;
import java.util.Set;

public class NonBlockingReads {
	public static void main(String[] args) throws Exception {
		String host = "";
		InetSocketAddress socketAddress = new InetSocketAddress(host, 80);
		SocketChannel channel = SocketChannel.open();
		channel.configureBlocking(false);
		channel.connect(socketAddress);
		
		/**
		 * The SocketChannel is an example of a SelectableChannel. 
		 * These selectable channels work with a Selector. 
		 * Basically, you register the channel with the Selector,
		 *  tell the Selector what events you are interested in,
		 *   and it notifies you when something interesting happens.
		 */
		//To get a Selector instance, just call the static open method of the class:
		Selector selector = Selector.open();
		
		/**
		 * Registering with the Selector is done through the register method of the channel. 
		 * The events are specified by fields of the SelectionKey class. 
		 * In the case of the SocketChannel class, the available operations are OP_CONNECT, OP_READ, and OP_WRITE. 
		 * So, if you were interested in read and connection operations, you would register as follows:
		 */
		channel.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ);
		
		/**
		 * At this point, you have to wait on the selector to tell you when events of interest happen on registered channels.
		 * The select method of the Selector will block until something interesting happens. 
		 * To find this out, you can put a while (selector.select() > 0) loop in its own thread and then go off and do your own thing while the I/O events are being processed. 
		 * The select method returns when something happens, where the value returned is the count of channels ready to be acted upon.
		 * This value doesn't really matter though.
		 */
		/**
		 * Once something interesting happens, 
		 * you have to figure out what happened and respond accordingly. 
		 * For the channel registered here with the selector, you expressed interest in both the OP_CONNECT and OP_READ operations, 
		 * so you know it can only be one of those events. 
		 * So, what you do is get the Set of ready objects through the selectedKeys method, and iterate. 
		 * The element in the Set is a SelectionKey, and you can check if it isConnectable or isReadable for the two states of interest.
		 */
		
		while (selector.select(500) > 0) {
			// Get set of ready objects
			Set readyKeys = selector.selectedKeys();
			Iterator readyItor = readyKeys.iterator();
			
			// OUTSIDE WHILE LOOP
			Charset charset = Charset.forName("ISO-8859-1");
			CharsetEncoder encoder = charset.newEncoder();
			
			// Walk through set
			while (readyItor.hasNext()) {

				// Get key from set
				SelectionKey key = (SelectionKey) readyItor.next();

				// Remove current entry
				readyItor.remove();

				// Get channel
				SocketChannel keyChannel = (SocketChannel) key.channel();

				if (key.isConnectable()) {
					// INSIDE if (channel.isConnectable())
					// Finish connection
					if (keyChannel.isConnectionPending()) {
					  keyChannel.finishConnect();
					}

					// Send request
					String request = "GET / \r\n\r\n";
					keyChannel.write(encoder.encode(CharBuffer.wrap(request)));

				} else if (key.isReadable()) {

				}
			}
			
			
		}
		
		/**
		 * The remove method call requires a little explanation. 
		 * The ready set of channels can change while you are processing them. 
		 * So, you should remove the one you are processing when you process it. 
		 * Removal does not trigger a ConcurrentModificationException to be thrown. 
		 * There's also a timeout setup here for the select call so it doesn't wait forever if there is nothing to do. 
		 * There's also a call to get the channel from the key in there. 
		 * You'll need that for each operation.
		 */
		
		/**
		 * For the sample program here you're doing the equivalent of reading from an HTTP connection, 
		 * so upon connection you need to send the initial HTTP request. 
		 * Basically, once you know the connection is made, 
		 * you send a GET request for the root of the site.
		 * When the selector reports that the channel is connectable, it may not have finished connecting yet. 
		 * So, you should always check if the connection is pending through isConnectionPending and call finishConnect if it is. 
		 * Once connected, you can write to the channel, but must use a ByteBuffer, not the more familiar I/O streams.
		 * Here's what the connection code looks like:
		 */
		
		
		
//		// OUTSIDE WHILE LOOP
//		CharsetDecoder decoder = charset.newDecoder();
//		ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
//		CharBuffer charBuffer = CharBuffer.allocate(1024);

		
		
	}
}
