package com.got.nio.demos;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

/**
 * InetSocketAddress
 * SocketChannel
 * @author zzwu
 *
 */
public class SocketChannels {
	public static void main(String[] args) throws Exception {
		String host = "";
		InetSocketAddress socketAddress = new InetSocketAddress(host, 80);
		
		/**
		 * Once you have the InetSocketAddress, that's where life changes. 
		 * Instead of reading from the socket's input stream and writing to the output stream, 
		 * you need to open a SocketChannel and connect it to the InetSocketAddress:
		 */
		SocketChannel channel = SocketChannel.open();
		channel.connect(socketAddress);
		
		/**
		 * Once connected, you can read from or write to the channel with ByteBuffer objects. 
		 * For instance, you can wrap a String in a CharBuffer with the help of an CharsetEncoder to send an HTTP request:
		 */
		Charset charset = Charset.forName("ISO-8859-1");
		CharsetEncoder encoder = charset.newEncoder();
		String request = "GET / \r\n\r\n";
		channel.write(encoder.encode(CharBuffer.wrap(request)));
		
		/**
		 * You can then read the response from the channel. 
		 * Since the response for this HTTP request will be text, 
		 * you'll need to convert that response into a CharBuffer through a CharsetDecoder. 
		 * By creating just a CharBuffer to start, 
		 * you can keep reusing the object to avoid unnecessary garbage collection between reads:
		 */
		ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
		CharBuffer charBuffer = CharBuffer.allocate(1024);
		CharsetDecoder decoder = charset.newDecoder();
		while ((channel.read(buffer)) != -1) {
		  buffer.flip();
		  decoder.decode(buffer, charBuffer, false);
		  charBuffer.flip();
		  System.out.println(charBuffer);
		  buffer.clear();
		  charBuffer.clear();
		}
	}
}
