package com.got.nioserver;

/**
 * ��Ϣ����
 * @author zzwu
 *
 */
public class MessagegServer {
	
	private AcceptableKeyHanlder acceptableKeyHanlder;
	private ReadableKeyHanlder readableKeyHanlder;
	private WritableKeyHanlder writableKeyHanlder;
	
	public MessagegServer(AcceptableKeyHanlder acceptableKeyHanlder, ReadableKeyHanlder readableKeyHanlder, WritableKeyHanlder writableKeyHanlder) {
		this.acceptableKeyHanlder = acceptableKeyHanlder;
		this.readableKeyHanlder = readableKeyHanlder;
		this.writableKeyHanlder = writableKeyHanlder;
	}

	public void start(int port) {
		
	}
	
	
}
