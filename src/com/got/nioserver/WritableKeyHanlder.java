package com.got.nioserver;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

public interface WritableKeyHanlder {
	public void handle(SelectionKey key, Selector selector);
}
