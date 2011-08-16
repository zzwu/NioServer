package com.got.nioserver;

import java.nio.channels.SelectionKey;

public interface ReadableKeyHanlder {
	public void handle(SelectionKey key);
}
