package com.got.nioserver.print;

import com.got.nioserver.MessageServer;

public class MainMessagegServer {
	public static void main(String[] args) {
		PrintAcceptableKeyHanlder pa = new PrintAcceptableKeyHanlder();
		PrintReadableKeyHanlder pr = new PrintReadableKeyHanlder();
		PrintWritableKeyHanlder pw = new PrintWritableKeyHanlder();		
		try {
			MessageServer s = new MessageServer(8888, pa, pr, pw);
			s.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
