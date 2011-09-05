package com.got.nio.demos.fileserver;


public class FileClient {
	
	public static void main(String[] args) throws Exception {
		final String srcPath = "/Users/zzwu/test/kklog/kkk/新建文件夹_1909488.rar"; 
		for (int i = 0; i < 3; i++) {
			final String objPath = "/Users/zzwu/test/kklog/kkk/新建文件夹_1909488_" + i + ".rar";
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						new FileReceiver(srcPath, objPath).requestFile();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}
	
}
