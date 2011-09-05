package com.got.nio.demos.fileserver;

import com.got.nioserver.MessageServer;

public class FileMessageServer {
	public static void main(String[] args) {
		FileAcceptableKeyHanlder pa = new FileAcceptableKeyHanlder();
		FileReadableKeyHanlder pr = new FileReadableKeyHanlder();
		FileWritableKeyHanlder pw = new FileWritableKeyHanlder();		
		try {
			MessageServer s = new MessageServer(8888, pa, pr, pw);
			s.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
