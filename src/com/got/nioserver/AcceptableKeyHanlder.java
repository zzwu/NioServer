package com.got.nioserver;

import java.nio.channels.SelectionKey;

public interface AcceptableKeyHanlder {
	public void hanlde(SelectionKey key);
}
