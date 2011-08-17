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
	private static int DEFAULT_SERVERPORT = 6018;// Ĭ�϶˿�
	private static int DEFAULT_BUFFERSIZE = 1024;// Ĭ�ϻ�������СΪ1024�ֽ�
	private static String DEFAULT_CHARSET = "GB2312";// Ĭ���뼯
	private static String DEFAULT_FILENAME = "bigfile.dat";
	private ServerSocketChannel channel;
	private LinkedList<SocketChannel> clients;
	private Selector selector;// ѡ����
	private ByteBuffer buffer;// �ֽڻ�����
	private int port;
	private Charset charset;// �ַ���
	private CharsetDecoder decoder;// ������

	public SelectorServer(int port) throws IOException {
		this.port = port;
		this.clients = new LinkedList<SocketChannel>();
		this.channel = null;
		this.selector = Selector.open();// ��ѡ����
		this.buffer = ByteBuffer.allocate(DEFAULT_BUFFERSIZE);
		this.charset = Charset.forName(DEFAULT_CHARSET);
		this.decoder = this.charset.newDecoder();

	}

	protected void handleKey(SelectionKey key) throws IOException {// �����¼�
		if (key.isAcceptable()) { // ��������
			ServerSocketChannel server = (ServerSocketChannel) key.channel();// ȡ����Ӧ�ķ�����ͨ��
			SocketChannel channel = server.accept();
			channel.configureBlocking(false);
			channel.register(selector, SelectionKey.OP_READ);//�ͻ�socketͨ��ע�������
		} else if (key.isReadable()) { // ����Ϣ
			SocketChannel channel = (SocketChannel) key.channel();
			int count = channel.read(this.buffer);
			if (count > 0) {
				this.buffer.flip();
				CharBuffer charBuffer = decoder.decode(this.buffer);
				System.out.println("Client >>" + charBuffer.toString());
				SelectionKey wKey = channel.register(selector, SelectionKey.OP_WRITE);//Ϊ�ͻ�socktͨ��ע��д����
				wKey.attach(new HandleClient());
			} else {// �ͻ��Ѿ��Ͽ�
				channel.close();
			}
			this.buffer.clear();// ��ջ�����
		} else if (key.isWritable()) { // д�¼�
			SocketChannel channel = (SocketChannel) key.channel();
			HandleClient handle = (HandleClient) key.attachment();// ȡ��������
			ByteBuffer block = ByteBuffer.wrap(handle.readBlock().getBytes());
			channel.write(block);
		}
	}

	public void listen() throws IOException { // ��������ʼ�����˿ڣ��ṩ����
		channel = ServerSocketChannel.open(); // ��ͨ��
		channel.socket().bind(new InetSocketAddress(port)); // ��scoket�����ƶ��Ķ˿���
		// ����ͨ��ʹ�÷�����ģʽ���ڷ�����ģʽ�£����Ա�д�������ͬʱ����ʹ�ø��ӵĶ��߳�
		channel.configureBlocking(false);
		channel.register(selector, SelectionKey.OP_ACCEPT);
		try {
			while (true) {// ��ͨ���ĳ���ͬ������ʹ��channel.accpet()���ܿͻ����������󣬶�������socket�����ϵ���accept(),�����ڵ���accept()����ʱ���ͨ������Ϊ������ģʽ,��ôaccept()������������null����������
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
		System.out.println("����������");
		SelectorServer server = new SelectorServer(SelectorServer.DEFAULT_SERVERPORT);
		server.listen(); // ��������ʼ�����˿ڣ��ṩ����
	}

}
