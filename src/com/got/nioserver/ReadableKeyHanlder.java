package com.got.nioserver;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

public interface ReadableKeyHanlder {
	public void handle(SelectionKey key, Selector selector);
}
