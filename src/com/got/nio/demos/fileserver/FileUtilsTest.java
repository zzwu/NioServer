package com.got.nio.demos.fileserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import org.junit.Test;

public class FileUtilsTest {

	@Test
	public void testCopy() {
		try {
			FileChannel fc1 = new FileInputStream("/Users/zzwu/test/kklog/hjdkkserver.log").getChannel();
			File to = new File("/Users/zzwu/test/kklog/2.log");
			to.createNewFile();
			FileChannel fc2 = new FileOutputStream(to).getChannel();
			FileUtils.copy(fc1, fc2);
			fc1.close();
			fc2.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
