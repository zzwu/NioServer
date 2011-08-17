package com.got.nioserver.print;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import com.got.nio.demos.demoserver.HandleClient;
import com.got.nioserver.ReadableKeyHanlder;

public class PrintReadableKeyHanlder implements ReadableKeyHanlder {
	
	// The buffer into which we'll read data when it's available
	private ByteBuffer buffer = ByteBuffer.allocate(8192);
	private Charset charset;// �ַ���
	private CharsetDecoder decoder;// ������
	private static String DEFAULT_CHARSET = "GB2312";// Ĭ���뼯

	public PrintReadableKeyHanlder() {
		this.charset = Charset.forName(DEFAULT_CHARSET);
		this.decoder = this.charset.newDecoder();
	}
	
	@Override
	public void handle(SelectionKey key, Selector selector) {
		try {
			SocketChannel channel = (SocketChannel) key.channel();
			int count = channel.read(this.buffer);
			if (count > 0) {
				this.buffer.flip();
				CharBuffer charBuffer = decoder.decode(this.buffer);
				System.out.println("Client >>" + charBuffer.toString());
				channel.register(selector, SelectionKey.OP_WRITE);//Ϊ�ͻ�socktͨ��ע��д����
			} else {// �ͻ��Ѿ��Ͽ�
				channel.close();
			}
			this.buffer.clear();// ��ջ�����
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
