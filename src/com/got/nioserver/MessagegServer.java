package com.got.nioserver;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 消息服务。
 * @author zzwu
 *
 */
public class MessagegServer {
	
	private AcceptableKeyHanlder acceptableKeyHanlder;
	private ReadableKeyHanlder readableKeyHanlder;
	private WritableKeyHanlder writableKeyHanlder;
	private boolean stop;
	
	public MessagegServer(AcceptableKeyHanlder acceptableKeyHanlder, ReadableKeyHanlder readableKeyHanlder, WritableKeyHanlder writableKeyHanlder) {
		this.acceptableKeyHanlder = acceptableKeyHanlder;
		this.readableKeyHanlder = readableKeyHanlder;
		this.writableKeyHanlder = writableKeyHanlder;
	}

	public void start(int port) throws Exception {
		//init channel and selector
		Selector selector = Selector.open();
		ServerSocketChannel channel = ServerSocketChannel.open();
		channel.configureBlocking(false);
		InetSocketAddress socket = new InetSocketAddress(port);
		channel.socket().bind(socket);
		//??? TODO channel.register(selector, SelectionKey.OP_ACCEPT | SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		channel.register(selector, SelectionKey.OP_ACCEPT);
		
		//The number of keys, possibly zero, whose ready-operation sets were updated 
		while(selector.select() > 0 && !stop) {
			Set<SelectionKey> selectedKeySet = selector.selectedKeys();
			Iterator<SelectionKey> keyIterator = selectedKeySet.iterator();
			//handle keys
			while (keyIterator.hasNext()) {
				SelectionKey key = keyIterator.next();
				if (key.isAcceptable()) {
					acceptableKeyHanlder.handle(key, selector);
				} else if (key.isReadable())  {
					readableKeyHanlder.handle(key, selector);
				} else if (key.isWritable()) {
					writableKeyHanlder.handle(key, selector);
				} else {
					System.out.println("key.interestOps():" + key.interestOps());
				}
			}
		}
	}
	
	public void stop() {
		stop = true;
	}
}
