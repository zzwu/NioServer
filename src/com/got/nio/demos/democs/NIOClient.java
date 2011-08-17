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

//ģ���ļ����ؿͻ���
public class NIOClient {
	static int SIZE = 100;

	//������
	static CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();
	//������
	static CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();

	static class Download implements Runnable {
		protected int index;//��ǰ�̱߳��

		public Download(int index) {
			this.index = index;
		}

		public void run() {
			try {
				long start = System.currentTimeMillis();
				//��һ���׽���ͨ��
				SocketChannel client = SocketChannel.open();
				//ͨ��ʹ�÷�����ģʽ
				client.configureBlocking(false);
				// ��һ��ѡ����
				Selector selector = Selector.open();
				//ע��������������ϵ��¼�
				client.register(selector, SelectionKey.OP_CONNECT);
				//���Ӵ�ͨ�����׽���
				client.connect(new InetSocketAddress(InetAddress.getLocalHost(), 8088));
				//�������Ƿ����β��ܶ��������ÿ�η������İ�
				ByteBuffer buffer = ByteBuffer.allocate(40);
				//���շ��������������ļ����������ݣ�Ҫȫ�����յ�����룬��Ȼ���������
				byte[] fileContentBin = new byte[1024*1024*2];
				int total = 0;
				boolean flag = true;

				while (flag) {
					//�Ѹ�����׼�������������ļ�����Ŀ������Ŀ����Ϊ�� 
					if (selector.select() == 0) {
						continue;
					}

					Iterator iter = selector.selectedKeys().iterator();
					while (iter.hasNext()) {
						SelectionKey key = (SelectionKey) iter.next();
						iter.remove();
						SocketChannel channel = (SocketChannel) key.channel();
						if (key.isConnectable()) {//��������˷�����

							//�жϴ�ͨ�����Ƿ����ڽ������Ӳ���
							if (channel.isConnectionPending()) {
								//����׽���ͨ�������ӹ���
								//channel.finishConnect();
							}
							//������������Ϻ����������UTF-8������Ϣ������Ҫע����ǣ�����ֻ����������˷���һ��
							//��Ϣ�����������ֱ�ӷ����ˣ���û�в���ע�����Ȥ���¼�����
							channel.write(encoder.encode(CharBuffer.wrap("��� from " + index)));
							//��ע��ͨ���ϵĿɶ�д����  
							channel
									.register(selector, SelectionKey.OP_READ
											| SelectionKey.OP_WRITE);
						} else if (key.isReadable()) {//ͨ���������ݿɶ�ʱ
							//��ȡ���ֽ���������Ϊ�㣬�����ͨ���ѵ�������ĩβ���򷵻� -1 
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
								System.out.println("ͨ���쳣���ر�...");
								client.close();
								flag = false; //�˴�flagΪ�������forѭ������ֹ���
							}
							if (count == 0) {
								//System.out.println("δ�������ݣ�����...");
							}
						} else if (key.isWritable()) {//������ͨ��д����ʱ  
							//������������Ϻ����������UTF-8������Ϣ��
							channel.write(encoder.encode(CharBuffer.wrap("��� from " + index)));
							//������Ϣ��ȥ��д�¼�
							key.interestOps(SelectionKey.OP_READ);
						}
					}
				}
				try {
					OutputStreamWriter ow = new OutputStreamWriter(new FileOutputStream(new File(
							"e:/tmp/" + this.index + ".txt")), "UTF-8");
					//ע������һ��Ҫ���ļ��������룬��Ȼ������쳣����Ϊ���Ǵ��������ж�ȡ�ģ�����һ���ַ��ı��뻹û�в���ȫ��������������
					//�õ��ļ�ȫ��������һ����룬��ȻҲ���Զ�һ���ֽ�һ���֣���Ҫ�ж��Ƿ��������������Ϸ�
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