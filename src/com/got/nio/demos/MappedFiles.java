package com.got.nio.demos;

import java.io.FileInputStream;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * There is one specialized form of direct ByteBuffer known as a
 * MappedByteBuffer. This class represents a buffer of bytes mapped to a file.
 * To map a file to a MappedByteBuffer, you first must get the channel for a
 * file. A channel represents a connection to something, such as a pipe, socket,
 * or file, that can perform I/O operations. In the case of a FileChannel, you
 * can get one from a FileInputStream, FileOutputStream, or RandomAccessFile
 * through the getChannel method. Once you have the channel, you map it to a
 * buffer with map, specifying the mode and portion of the file you want to map.
 * The file channel can be opened with one of the FileChannel. MapMode
 * constants: read-only (READ_ONLY), private/copy-on-write (PRIVATE), or
 * read-write (READ_WRITE).
 * 
 * @author zzwu
 * 
 */
public class MappedFiles {

	public static void main(String[] args) throws Exception {
		String filename = "";
		FileInputStream input = new FileInputStream(filename);
		FileChannel channel = input.getChannel();
		int fileLength = (int) channel.size();
		//Once the MappedByteBuffer has been created, you can access it like any other ByteBuffer. 
		MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, fileLength);
		
		// ISO-8859-1  is ISO Latin Alphabet #1
		Charset charset = Charset.forName("ISO-8859-1");
		CharsetDecoder decoder = charset.newDecoder();
		CharBuffer charBuffer = decoder.decode(buffer);
	}

}
