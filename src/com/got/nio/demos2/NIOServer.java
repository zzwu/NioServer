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

//测试文件下载的NIOServer
public class NIOServer {

	// 处理与客户端的交互
	private class HandleClient {
		protected FileChannel channel;
		protected ByteBuffer buffer;

		public HandleClient() throws IOException {
			// 文件通道
			this.channel = new FileInputStream(filename).getChannel();
			// 缓冲器
			this.buffer = ByteBuffer.allocate(BLOCK);
		}

		public ByteBuffer readBlock() {
			try {
				// 清除缓冲，准备写
				buffer.clear();
				// 注，文件是什么样的编码方式，读出来的主就是什么样的编码流
				int count = channel.read(buffer);
				// 读准备
				buffer.flip();
				// 读取的字节数，可能为零，如果该通道已到达流的末尾，则返回 -1
				if (count < 0) {
					System.out.println("文件已读完...");
					// 读完后关闭文件通道
					close();
					// 读完后反回null
					return null;
				} else if (count == 0) {
					System.out.println("未读取到文件内容，继续...");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return buffer;
		}

		public void close() {
			try {
				// 关闭文件通道
				channel.close();
				System.out.println("文件通道已关闭...");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	static int BLOCK = 1024 * 4;// 块大小
	protected Selector selector;// 选择器
	protected String filename = "/Users/zzwu/test/kklog/hjdkkserver.log"; // 大文件
	protected ByteBuffer clientBuffer = ByteBuffer.allocate(BLOCK);//
	protected CharsetDecoder decoder;// 字符集解码器

	public NIOServer(int port) throws IOException {
		selector = this.getSelector(port);
		// UTF-8字符集
		Charset charset = Charset.forName("UTF-8");
		decoder = charset.newDecoder();
	}

	// 获取Selector
	protected Selector getSelector(int port) throws IOException {
		// 打开服务器套接字通道
		ServerSocketChannel server = ServerSocketChannel.open();
		// 打开一个选择器
		Selector sel = Selector.open();
		// 把通道绑定到指定的IP与端口上
		server.socket().bind(
				new InetSocketAddress(InetAddress.getLocalHost(), port));
		// 调整此通道的阻塞模式为异步
		server.configureBlocking(false);
		// 向指定的选择器注册此通道，感兴趣事件为套接字接受操作
		server.register(sel, SelectionKey.OP_ACCEPT);
		return sel;
	}

	// 监听端口
	public void listen() {

		while (true) {
			// 已更新其准备就绪操作集的键的数目，该数目可能为零
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
				iter.remove();// 处理后删除
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

	// 处理事件
	protected void handleKey(SelectionKey key) throws IOException {
		if (key.isAcceptable()) { // 接收请求
			doAccept(key);
		} else if (key.isReadable()) { // 读信息
			doRead(key);
		} else if (key.isWritable()) { // 写事件
			doWrite(key);
		}
	}

	private void doWrite(SelectionKey key) {
		// 检索当前key的附加对象
		SocketChannel channel = (SocketChannel) key.channel();
		HandleClient handle = (HandleClient) key.attachment();
		// 从大文件中读取一小块再发往客户端
		ByteBuffer block = handle.readBlock();
		// 如果读到了数据，则发往客户端
		if (block != null) {
			try {
				if (block.limit() > 0) {
					channel.write(block);
				}
			} catch (IOException e) {
				// 客户端通道出现异常，关闭客户端通道
				try {
					channel.close();
					System.out.println("客户羰通道异常，通道关闭...");
					handle.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		} else {// 如果数据读完
			try {
				// handle.close();
				// 读完文件后关闭所对应的客户端通道
				channel.close();
				System.out.println("文件读完，客户端通道关闭...");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void doRead(SelectionKey key) throws IOException,
			CharacterCodingException, ClosedChannelException {
		SocketChannel channel = (SocketChannel) key.channel();

		// 读取的字节数，可能为零，如果该通道已到达流的末尾，则返回 -1
		int count = channel.read(clientBuffer);
		// 如果读到数据
		if (count > 0) {
			// 准备读
			clientBuffer.flip();
			// 使用字符解码器解码缓冲中的字节内容
			CharBuffer charBuffer = decoder.decode(clientBuffer);
			System.out.println("Client >>" + charBuffer.toString());

			// 读到客户端内容后，注册可写事件，后面可以向客户端写数据
			SelectionKey wKey = channel.register(selector,
					SelectionKey.OP_WRITE);
			// 将给定的对象附加到此键。 之后可通过 attachment 方法检索已附加的对象。一次只能附加一个对象；
			// 调用此方法会导致丢弃所有以前的附加对象。通过附加 null 可丢弃当前的附加对象。
			wKey.attach(new HandleClient());

		} else if (count < 0) {
			channel.close();
			System.out.println("读取客户端数据异常，通道关闭...");
		} else {
			System.out.println("未读取到数据...");
		}
		clientBuffer.clear();
	}

	private void doAccept(SelectionKey key) throws IOException,
			ClosedChannelException {
		ServerSocketChannel server = (ServerSocketChannel) key.channel();

		// 接收新连接的套接字通道，或者如果此通道处于非阻塞模式并且没有要接受的可用连接，则返回 null
		SocketChannel channel = server.accept();
		if (channel == null) {
			return;
		}
		System.out.println("已发现一个客户端...");
		// 异步非阻塞客户端通道
		channel.configureBlocking(false);
		// 准备读
		channel.register(selector, SelectionKey.OP_READ);
		// 判断此通道上是否正在进行连接操作
		if (channel.isConnectionPending()) {
			// 完成套接字通道的连接过程
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