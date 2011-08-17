package com.got.nio.demos.demoserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;
import java.util.LinkedList;

public class SelectorServer {
	private static int DEFAULT_SERVERPORT = 6018;// 默认端口
	private static int DEFAULT_BUFFERSIZE = 1024;// 默认缓冲区大小为1024字节
	private static String DEFAULT_CHARSET = "GB2312";// 默认码集
	private static String DEFAULT_FILENAME = "bigfile.dat";
	private ServerSocketChannel channel;
	private LinkedList<SocketChannel> clients;
	private Selector selector;// 选择器
	private ByteBuffer buffer;// 字节缓冲区
	private int port;
	private Charset charset;// 字符集
	private CharsetDecoder decoder;// 解码器

	public SelectorServer(int port) throws IOException {
		this.port = port;
		this.clients = new LinkedList<SocketChannel>();
		this.channel = null;
		this.selector = Selector.open();// 打开选择器
		this.buffer = ByteBuffer.allocate(DEFAULT_BUFFERSIZE);
		this.charset = Charset.forName(DEFAULT_CHARSET);
		this.decoder = this.charset.newDecoder();

	}

	protected void handleKey(SelectionKey key) throws IOException {// 处理事件
		if (key.isAcceptable()) { // 接收请求
			ServerSocketChannel server = (ServerSocketChannel) key.channel();// 取出对应的服务器通道
			SocketChannel channel = server.accept();
			channel.configureBlocking(false);
			channel.register(selector, SelectionKey.OP_READ);//客户socket通道注册读操作
		} else if (key.isReadable()) { // 读信息
			SocketChannel channel = (SocketChannel) key.channel();
			int count = channel.read(this.buffer);
			if (count > 0) {
				this.buffer.flip();
				CharBuffer charBuffer = decoder.decode(this.buffer);
				System.out.println("Client >>" + charBuffer.toString());
				SelectionKey wKey = channel.register(selector, SelectionKey.OP_WRITE);//为客户sockt通道注册写操作
				wKey.attach(new HandleClient());
			} else {// 客户已经断开
				channel.close();
			}
			this.buffer.clear();// 清空缓冲区
		} else if (key.isWritable()) { // 写事件
			SocketChannel channel = (SocketChannel) key.channel();
			HandleClient handle = (HandleClient) key.attachment();// 取出处理者
			ByteBuffer block = ByteBuffer.wrap(handle.readBlock().getBytes());
			channel.write(block);
		}
	}

	public void listen() throws IOException { // 服务器开始监听端口，提供服务
		channel = ServerSocketChannel.open(); // 打开通道
		channel.socket().bind(new InetSocketAddress(port)); // 将scoket榜定在制定的端口上
		// 配置通到使用非阻塞模式，在非阻塞模式下，可以编写多道程序同时避免使用复杂的多线程
		channel.configureBlocking(false);
		channel.register(selector, SelectionKey.OP_ACCEPT);
		try {
			while (true) {// 与通常的程序不同，这里使用channel.accpet()接受客户端连接请求，而不是在socket对象上调用accept(),这里在调用accept()方法时如果通道配置为非阻塞模式,那么accept()方法立即返回null，并不阻塞
				selector.select();
				Iterator iter = selector.selectedKeys().iterator();
				while (iter.hasNext()) {
					SelectionKey key = (SelectionKey) iter.next();
					iter.remove();
					handleKey(key);
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		System.out.println("服务器启动");
		SelectorServer server = new SelectorServer(SelectorServer.DEFAULT_SERVERPORT);
		server.listen(); // 服务器开始监听端口，提供服务
	}

}
