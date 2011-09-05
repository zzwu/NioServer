package com.got.nio.demos.fileserver;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import com.got.nioserver.ReadableKeyHanlder;

public class FileReadableKeyHanlder implements ReadableKeyHanlder {
	
	// The buffer into which we'll read data when it's available
	private ByteBuffer bytebuffer = ByteBuffer.allocate(8192);
	private Charset charset;// �ַ���
	private CharsetDecoder decoder;// ������
	private static String DEFAULT_CHARSET = "GB2312";// Ĭ���뼯
	
	public FileReadableKeyHanlder() {
		this.charset = Charset.forName(DEFAULT_CHARSET);
		this.decoder = this.charset.newDecoder();
	}

	@Override
	public void handle(SelectionKey key, Selector selector) {
		try {
			SocketChannel channel = (SocketChannel) key.channel();
			int count = channel.read(bytebuffer);
			if (count > 0) {
				bytebuffer.flip();
				CharBuffer charBuffer = decoder.decode(bytebuffer);
				String filePath = charBuffer.toString();
				System.out.println("Client reuqest name >> " + filePath);
				FileSender sender = new FileSender(filePath);
				channel.register(selector, SelectionKey.OP_WRITE, sender);
			} else {// �ͻ��Ѿ��Ͽ�
				channel.close();
			}
			bytebuffer.clear();// ��ջ�����
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
