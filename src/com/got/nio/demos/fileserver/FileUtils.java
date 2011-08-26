package com.got.nio.demos.fileserver;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class FileUtils {
	
	public static void copy(ReadableByteChannel from, WritableByteChannel to) throws Exception {
		ByteBuffer bb =ByteBuffer.allocate(1024);
		while (from.read(bb) > 0) {
			bb.flip();
			to.write(bb);
			//bb.reset();
			bb.clear();
		}
	}
	
	public static void copy(InputStream from, WritableByteChannel to) throws Exception {
		ByteBuffer bb =ByteBuffer.allocate(1024);
		byte[] buf = new byte[1024];
		while (from.read(buf) > 0) {
			bb = ByteBuffer.wrap(buf);
			bb.flip();
			to.write(bb);
			//bb.reset();
			bb.clear();
		}
	}
	

}
