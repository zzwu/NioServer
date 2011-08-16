package com.got.nio.demos;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;

public class ReadURL {
	public static void main(String args[]) {
		String host = "www.baidu.com";
		SocketChannel channel = null;

		try {

			// Setup
			InetSocketAddress socketAddress = new InetSocketAddress(host, 80);
			Charset charset = Charset.forName("ISO-8859-1");
			CharsetDecoder decoder = charset.newDecoder();
			CharsetEncoder encoder = charset.newEncoder();

			// Allocate buffers
			ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
			CharBuffer charBuffer = CharBuffer.allocate(1024);

			// Connect
			channel = SocketChannel.open();
			channel.connect(socketAddress);

			// Send request
			String request = "GET / \r\n\r\n";
			channel.write(encoder.encode(CharBuffer.wrap(request)));

			// Read response
			while ((channel.read(buffer)) != -1) {
				buffer.flip();
				// Decode buffer
				decoder.decode(buffer, charBuffer, false);
				// Display
				charBuffer.flip();
				System.out.println(charBuffer);
				buffer.clear();
				charBuffer.clear();
			}
		} catch (UnknownHostException e) {
			System.err.println(e);
		} catch (IOException e) {
			System.err.println(e);
		} finally {
			if (channel != null) {
				try {
					channel.close();
				} catch (IOException ignored) {
				}
			}
		}
	}
}