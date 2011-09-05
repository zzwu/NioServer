package com.got.nio.demos.fileserver;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class RequestHanlder {
	
	private long length = -1;
	private byte[] buf;
	private ByteBuffer lengthByteBuffer = ByteBuffer.allocate(8);
	private ByteBuffer onceByteBuffer = ByteBuffer.allocate(8192);
	private boolean isGotLength;
	
	public RequestHanlder() {
		
	}
	
	public String read(SocketChannel sc) {
		
		try {
			
			if (isGotLength) {
				int len = sc.read(lengthByteBuffer);
				
			}
			
			int onceLength = sc.read(onceByteBuffer);
			if (onceLength > 0) {
				if (lengthByteBuffer.hasRemaining()) {
					
				}
			} else {
				return null;
			}
			
			
			
			while (lengthByteBuffer.hasRemaining()) {
				int length  = sc.read(lengthByteBuffer);
				if (length <= 0) {
					return null;
				}
			}
			
			if (length == -1) {
				length = BruteForceCoding.decodeIntBigEndian(lengthByteBuffer.array(), 0, 8);
			}
			
			
			
			
		} catch (Exception e) {
			
		}
		
		
		
	}

}
