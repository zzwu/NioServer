package com.got.nioserver.print.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class EchoClient {

	public static void main(String[] args) throws Exception {
		// Socket
		Socket server = new Socket(InetAddress.getLocalHost(), 8888);
		PrintWriter out = new PrintWriter(server.getOutputStream());
		BufferedReader br = new BufferedReader(new InputStreamReader(server.getInputStream()));
		String sayHi = "hello!";
		System.out.println("say hi : " + sayHi);
		out.write(sayHi);
		String reply = br.readLine();
		System.out.println("get reply info:" + reply);
		server.close();
	}

}
