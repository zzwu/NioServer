package com.got.nio.demos.fileserver;

import java.io.File;
import java.io.FileInputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class FileSender implements Runnable {
	
	private SocketChannel sc;
	private String path;
	
	public FileSender(SocketChannel sc, String path) {
		this.sc = sc;
		this.path = path;
	}

	@Override
	public void run() {
		try {
			File f = new File(path);
			FileChannel fc = new FileInputStream(f).getChannel();
			FileUtils.copy(fc, sc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
