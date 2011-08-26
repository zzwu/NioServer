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
	private Selector selector;
	private int port;
	
	public MessagegServer(int port, AcceptableKeyHanlder acceptableKeyHanlder, ReadableKeyHanlder readableKeyHanlder, WritableKeyHanlder writableKeyHanlder) throws Exception {
		this.acceptableKeyHanlder = acceptableKeyHanlder;
		this.readableKeyHanlder = readableKeyHanlder;
		this.writableKeyHanlder = writableKeyHanlder;
		this.port = port;
		//init channel and selector
		initSelector();
	}

	private void initSelector() throws Exception {
		selector = Selector.open();
		ServerSocketChannel channel = ServerSocketChannel.open();
		channel.configureBlocking(false);
		InetSocketAddress socket = new InetSocketAddress(port);
		channel.socket().bind(socket);
		channel.register(selector, SelectionKey.OP_ACCEPT);
	}

	public void start() {
		while (!stop) {
			try {
				selector.select();
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
					keyIterator.remove();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void stop() {
		stop = true;
	}
}
