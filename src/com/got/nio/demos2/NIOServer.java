package com.got.nio.demos2;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;

//�����ļ����ص�NIOServer
public class NIOServer {

	// ������ͻ��˵Ľ���
	private class HandleClient {
		protected FileChannel channel;
		protected ByteBuffer buffer;

		public HandleClient() throws IOException {
			// �ļ�ͨ��
			this.channel = new FileInputStream(filename).getChannel();
			// ������
			this.buffer = ByteBuffer.allocate(BLOCK);
		}

		public ByteBuffer readBlock() {
			try {
				// ������壬׼��д
				buffer.clear();
				// ע���ļ���ʲô���ı��뷽ʽ����������������ʲô���ı�����
				int count = channel.read(buffer);
				// ��׼��
				buffer.flip();
				// ��ȡ���ֽ���������Ϊ�㣬�����ͨ���ѵ�������ĩβ���򷵻� -1
				if (count < 0) {
					System.out.println("�ļ��Ѷ���...");
					// �����ر��ļ�ͨ��
					close();
					// ����󷴻�null
					return null;
				} else if (count == 0) {
					System.out.println("δ��ȡ���ļ����ݣ�����...");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return buffer;
		}

		public void close() {
			try {
				// �ر��ļ�ͨ��
				channel.close();
				System.out.println("�ļ�ͨ���ѹر�...");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	static int BLOCK = 1024 * 4;// ���С
	protected Selector selector;// ѡ����
	protected String filename = "/Users/zzwu/test/kklog/hjdkkserver.log"; // ���ļ�
	protected ByteBuffer clientBuffer = ByteBuffer.allocate(BLOCK);//
	protected CharsetDecoder decoder;// �ַ���������

	public NIOServer(int port) throws IOException {
		selector = this.getSelector(port);
		// UTF-8�ַ���
		Charset charset = Charset.forName("UTF-8");
		decoder = charset.newDecoder();
	}

	// ��ȡSelector
	protected Selector getSelector(int port) throws IOException {
		// �򿪷������׽���ͨ��
		ServerSocketChannel server = ServerSocketChannel.open();
		// ��һ��ѡ����
		Selector sel = Selector.open();
		// ��ͨ���󶨵�ָ����IP��˿���
		server.socket().bind(
				new InetSocketAddress(InetAddress.getLocalHost(), port));
		// ������ͨ��������ģʽΪ�첽
		server.configureBlocking(false);
		// ��ָ����ѡ����ע���ͨ��������Ȥ�¼�Ϊ�׽��ֽ��ܲ���
		server.register(sel, SelectionKey.OP_ACCEPT);
		return sel;
	}

	// �����˿�
	public void listen() {

		while (true) {
			// �Ѹ�����׼�������������ļ�����Ŀ������Ŀ����Ϊ��
			try {
				if (selector.select() == 0) {
					continue;
				}
			} catch (IOException e1) {

				e1.printStackTrace();
			}
			Iterator iter = selector.selectedKeys().iterator();
			while (iter.hasNext()) {
				SelectionKey key = (SelectionKey) iter.next();
				iter.remove();// �����ɾ��
				try {
					handleKey(key);
				} catch (IOException e) {
					HandleClient handle = (HandleClient) key.attachment();
					if (handle != null) {
						handle.close();
					}
					key.cancel();
					e.printStackTrace();
				}
			}
		}

	}

	// �����¼�
	protected void handleKey(SelectionKey key) throws IOException {
		if (key.isAcceptable()) { // ��������
			doAccept(key);
		} else if (key.isReadable()) { // ����Ϣ
			doRead(key);
		} else if (key.isWritable()) { // д�¼�
			doWrite(key);
		}
	}

	private void doWrite(SelectionKey key) {
		// ������ǰkey�ĸ��Ӷ���
		SocketChannel channel = (SocketChannel) key.channel();
		HandleClient handle = (HandleClient) key.attachment();
		// �Ӵ��ļ��ж�ȡһС���ٷ����ͻ���
		ByteBuffer block = handle.readBlock();
		// ������������ݣ������ͻ���
		if (block != null) {
			try {
				if (block.limit() > 0) {
					channel.write(block);
				}
			} catch (IOException e) {
				// �ͻ���ͨ�������쳣���رտͻ���ͨ��
				try {
					channel.close();
					System.out.println("�ͻ���ͨ���쳣��ͨ���ر�...");
					handle.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		} else {// ������ݶ���
			try {
				// handle.close();
				// �����ļ���ر�����Ӧ�Ŀͻ���ͨ��
				channel.close();
				System.out.println("�ļ����꣬�ͻ���ͨ���ر�...");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void doRead(SelectionKey key) throws IOException,
			CharacterCodingException, ClosedChannelException {
		SocketChannel channel = (SocketChannel) key.channel();

		// ��ȡ���ֽ���������Ϊ�㣬�����ͨ���ѵ�������ĩβ���򷵻� -1
		int count = channel.read(clientBuffer);
		// �����������
		if (count > 0) {
			// ׼����
			clientBuffer.flip();
			// ʹ���ַ����������뻺���е��ֽ�����
			CharBuffer charBuffer = decoder.decode(clientBuffer);
			System.out.println("Client >>" + charBuffer.toString());

			// �����ͻ������ݺ�ע���д�¼������������ͻ���д����
			SelectionKey wKey = channel.register(selector,
					SelectionKey.OP_WRITE);
			// �������Ķ��󸽼ӵ��˼��� ֮���ͨ�� attachment ���������Ѹ��ӵĶ���һ��ֻ�ܸ���һ������
			// ���ô˷����ᵼ�¶���������ǰ�ĸ��Ӷ���ͨ������ null �ɶ�����ǰ�ĸ��Ӷ���
			wKey.attach(new HandleClient());

		} else if (count < 0) {
			channel.close();
			System.out.println("��ȡ�ͻ��������쳣��ͨ���ر�...");
		} else {
			System.out.println("δ��ȡ������...");
		}
		clientBuffer.clear();
	}

	private void doAccept(SelectionKey key) throws IOException,
			ClosedChannelException {
		ServerSocketChannel server = (ServerSocketChannel) key.channel();

		// ���������ӵ��׽���ͨ�������������ͨ�����ڷ�����ģʽ����û��Ҫ���ܵĿ������ӣ��򷵻� null
		SocketChannel channel = server.accept();
		if (channel == null) {
			return;
		}
		System.out.println("�ѷ���һ���ͻ���...");
		// �첽�������ͻ���ͨ��
		channel.configureBlocking(false);
		// ׼����
		channel.register(selector, SelectionKey.OP_READ);
		// �жϴ�ͨ�����Ƿ����ڽ������Ӳ���
		if (channel.isConnectionPending()) {
			// ����׽���ͨ�������ӹ���
			channel.finishConnect();
		}
	}

	public static void main(String[] args) {
		int port = 8088;
		try {
			NIOServer server = new NIOServer(port);
			System.out.println("Listernint on " + port);
			while (true) {
				server.listen();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}