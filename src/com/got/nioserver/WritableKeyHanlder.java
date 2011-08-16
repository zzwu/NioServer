package com.got.nioserver;

import java.nio.channels.SelectionKey;

public interface WritableKeyHanlder {
	public void hanlde(SelectionKey key);
}
