package com.got.nio.demos;

import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server {
	private static int port = 9999;

	public static void main(String args[]) throws Exception {
		Selector selector = Selector.open();

		ServerSocketChannel channel = ServerSocketChannel.open();
		channel.configureBlocking(false);
		InetSocketAddress isa = new InetSocketAddress(port);
		channel.socket().bind(isa);

		// Register interest in when connection
		channel.register(selector, SelectionKey.OP_ACCEPT);

		// Wait for something of interest to happen
		while (selector.select() > 0) {
			// Get set of ready objects
			Set readyKeys = selector.selectedKeys();
			Iterator readyItor = readyKeys.iterator();

			// Walk through set
			while (readyItor.hasNext()) {

				// Get key from set
				SelectionKey key = (SelectionKey) readyItor.next();

				// Remove current entry
				readyItor.remove();

				if (key.isAcceptable()) {
					// Get channel
					ServerSocketChannel keyChannel = (ServerSocketChannel) key
							.channel();

					// Get server socket
					ServerSocket serverSocket = keyChannel.socket();

					// Accept request
					Socket socket = serverSocket.accept();

					// Return canned message
					PrintWriter out = new PrintWriter(socket.getOutputStream(),
							true);
					out.println("Hello, NIO");
					out.close();
				} else {
					System.err.println("Ooops");
				}

			}
		}
		// Never ends
	}
}