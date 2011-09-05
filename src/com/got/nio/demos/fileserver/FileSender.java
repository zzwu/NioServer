package com.got.nio.demos.fileserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

/**
 * 文件发送。
 * @author zzwu
 *
 */
public class FileSender {
	
	private String path;
	private FileChannel fc;
	private ByteBuffer buf = ByteBuffer.allocate(1024);
	private boolean isSendLength;
	
	public FileSender(String path) throws Exception {
		this.path = path;
		fc = new FileInputStream(path).getChannel();
	}
	
	private void sendLength(SocketChannel sc) {
		isSendLength = true;
		File f = new File(path);
		//文件大小
		long fileLength = f.length();
		byte[] lengthBytes = new byte[8];
		BruteForceCoding.encodeIntBigEndian(lengthBytes, fileLength, 0, 8);
		try {
			sc.write(ByteBuffer.wrap(lengthBytes));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send(SocketChannel sc) {
		if (!isSendLength) {
			sendLength(sc);
		}
		try {
			//发送文件
			buf.clear();
			if (fc.read(buf) != -1) {
				buf.flip();
				sc.write(buf);
			} else {
				sc.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
