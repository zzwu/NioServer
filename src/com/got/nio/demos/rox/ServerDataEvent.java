package com.got.nio.demos.rox;
import java.nio.channels.SocketChannel;

/**
 * 服务器要发送的数据。
 * @author zzwu
 *
 */
class ServerDataEvent {
	public NioServer server;
	public SocketChannel socket;
	public byte[] data;
	
	public ServerDataEvent(NioServer server, SocketChannel socket, byte[] data) {
		this.server = server;
		this.socket = socket;
		this.data = data;
	}
}