package com.got.nio.demos.democs;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Iterator;

//模拟文件下载客户端
public class NIOClient {
	static int SIZE = 100;

	//编码器
	static CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();
	//解码器
	static CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();

	static class Download implements Runnable {
		protected int index;//当前线程编号

		public Download(int index) {
			this.index = index;
		}

		public void run() {
			try {
				long start = System.currentTimeMillis();
				//打开一个套接字通道
				SocketChannel client = SocketChannel.open();
				//通道使用非阻塞模式
				client.configureBlocking(false);
				// 打开一个选择器
				Selector selector = Selector.open();
				//注册与服务器连接上的事件
				client.register(selector, SelectionKey.OP_CONNECT);
				//连接此通道的套接字
				client.connect(new InetSocketAddress(InetAddress.getLocalHost(), 8088));
				//这里我们分三次才能读完服务器每次发过来的包
				ByteBuffer buffer = ByteBuffer.allocate(40);
				//接收服务器发过来的文件二进制内容，要全部接收到后解码，不然会出现乱码
				byte[] fileContentBin = new byte[1024*1024*2];
				int total = 0;
				boolean flag = true;

				while (flag) {
					//已更新其准备就绪操作集的键的数目，该数目可能为零 
					if (selector.select() == 0) {
						continue;
					}

					Iterator iter = selector.selectedKeys().iterator();
					while (iter.hasNext()) {
						SelectionKey key = (SelectionKey) iter.next();
						iter.remove();
						SocketChannel channel = (SocketChannel) key.channel();
						if (key.isConnectable()) {//如果连上了服务器

							//判断此通道上是否正在进行连接操作
							if (channel.isConnectionPending()) {
								//完成套接字通道的连接过程
								//channel.finishConnect();
							}
							//与服务器连接上后向服务器发UTF-8的送消息，这里要注意的是，我们只需向服务器端发送一次
							//消息，所以这里就直接发送了，而没有采用注册感兴趣的事件机制
							channel.write(encoder.encode(CharBuffer.wrap("你好 from " + index)));
							//关注此通道上的可读写操作  
							channel
									.register(selector, SelectionKey.OP_READ
											| SelectionKey.OP_WRITE);
						} else if (key.isReadable()) {//通道上有数据可读时
							//读取的字节数，可能为零，如果该通道已到达流的末尾，则返回 -1 
							int count = channel.read(buffer);

							while (count > 0) {
								//System.out.println("count1===" + count);
								buffer.flip();
								buffer.get(fileContentBin, total, count);
								total += count;

								buffer.clear();
								count = channel.read(buffer);
							}
							//System.out.println("count2===" + count);
							if (count == -1) {
								System.out.println("通道异常，关闭...");
								client.close();
								flag = false; //此处flag为最外面的for循环的终止标记
							}
							if (count == 0) {
								//System.out.println("未读到内容，继续...");
							}
						} else if (key.isWritable()) {//可以向通道写数据时  
							//与服务器连接上后向服务器发UTF-8的送消息，
							channel.write(encoder.encode(CharBuffer.wrap("你好 from " + index)));
							//发送信息后去掉写事件
							key.interestOps(SelectionKey.OP_READ);
						}
					}
				}
				try {
					OutputStreamWriter ow = new OutputStreamWriter(new FileOutputStream(new File(
							"e:/tmp/" + this.index + ".txt")), "UTF-8");
					//注：这里一定要等文件读完后解码，不然会出现异常，因为这是从网络流中读取的，可能一个字符的编码还没有补完全传过来，所以最
					//好等文件全传过来后一起解码，当然也可以读一部分解一部分，但要判断是否是完整的流，较烦
					ow.write(decoder.decode(ByteBuffer.wrap(fileContentBin, 0, total)).toString());
					ow.close();
				} catch (CharacterCodingException e) {
					e.printStackTrace();
				}

				double last = (System.currentTimeMillis() - start) * 1.0 / 1000;
				System.out.println("Thread " + index + " downloaded " + total + "bytes in " + last
						+ "s.");

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws IOException {

		for (int index = 0; index < SIZE; index++) {

			new Thread(new Download(index)).start();
		}
	}
}