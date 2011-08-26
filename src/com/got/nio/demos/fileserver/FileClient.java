package com.got.nio.demos.fileserver;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.channels.FileChannel;

public class FileClient {
	
	public static void main(String[] args) throws Exception {
		// Socket
		Socket server = new Socket(InetAddress.getLocalHost(), 8888);
		PrintWriter out = new PrintWriter(server.getOutputStream());
		String sayHi = "/Users/zzwu/test/kklog/hjdkkserver.log";
		System.out.println("say hi : " + sayHi);
		out.write(sayHi);
		out.flush();
		FileChannel to = new FileOutputStream("/Users/zzwu/test/kklog/3.log").getChannel();
		server.getInputStream();
		FileUtils.copy(server.getInputStream(), to);
		server.close();
	}
	
}
