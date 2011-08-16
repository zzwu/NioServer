package com.got.nioserver;

import java.nio.channels.SelectionKey;

public interface ReadableKeyHanlder {
	public void hanlde(SelectionKey key);
}
