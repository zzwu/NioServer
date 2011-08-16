package com.got.nioserver.print;

import com.got.nioserver.MessagegServer;

public class MainMessagegServer {
	public static void main(String[] args) {
		PrintAcceptableKeyHanlder pa = new PrintAcceptableKeyHanlder();
		PrintReadableKeyHanlder pr = new PrintReadableKeyHanlder();
		PrintWritableKeyHanlder pw = new PrintWritableKeyHanlder();
		
		MessagegServer s = new MessagegServer(pa, pr, pw);
		try {
			s.start(8888);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
