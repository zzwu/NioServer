package com.got.nio.demos.demoserver;

import java.io.IOException;

public class HandleClient {
	private String strGreeting = "welcome to VistaQQ";

	public HandleClient() throws IOException {
	}

	public String readBlock() {// ��������
		return strGreeting;
	}

	public void close() {

	}
}