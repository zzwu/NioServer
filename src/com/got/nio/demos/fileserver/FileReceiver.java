package com.got.nio.demos.fileserver;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileReceiver {
	
	private String srcPath;
	private String objPath;
	
	public FileReceiver(String srcPath, String objPath) {
		this.srcPath = srcPath;
		this.objPath = objPath;
	}

	public void requestFile() throws Exception {
		// Socket
		Socket socket = new Socket(InetAddress.getLocalHost(), 8888);
		OutputStream os = socket.getOutputStream();
		System.out.println("request file : " + srcPath);
		byte[] fileNameLengthBytes = new byte[4];
		byte[] fileNameBytes = srcPath.getBytes();
		int fileNameLength = fileNameBytes.length;
		BruteForceCoding.encodeIntBigEndian(fileNameLengthBytes, fileNameLength, 0, 4);
		os.write(fileNameLengthBytes);
		os.write(fileNameBytes);
		FileChannel to = new FileOutputStream(objPath).getChannel();
		InputStream in = socket.getInputStream();
		
		byte[] lengthBytes = new byte[8];
		in.read(lengthBytes); 
		long length = BruteForceCoding.decodeIntBigEndian(lengthBytes, 0, 8);
		
		long count = 0;
		byte[] buf = new byte[1024];
		int len = 0;
		while ((len = in.read(buf)) != -1 && count < length) {
			to.write(ByteBuffer.wrap(buf));
			count += len;
		}
		socket.close();
	}
}
