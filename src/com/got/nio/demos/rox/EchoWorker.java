package com.got.nio.demos.rox;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;


/**
 * ��������Ϣ�����̡߳�
 * @author zzwu
 *
 */
public class EchoWorker implements Runnable {
	
	private List<ServerDataEvent> queue = new LinkedList<ServerDataEvent>();
	
	public void processData(NioServer server, SocketChannel socket, byte[] data, int count) {
		byte[] dataCopy = new byte[count];
		System.arraycopy(data, 0, dataCopy, 0, count);
		synchronized(queue) {
			queue.add(new ServerDataEvent(server, socket, dataCopy));
			queue.notify();
		}
	}
	
	public void run() {
		ServerDataEvent dataEvent;
		while(true) {
			// Wait for data to become available
			synchronized(queue) {
				while(queue.isEmpty()) {
					try {
						queue.wait();
					} catch (InterruptedException e) {
					}
				}
				dataEvent = queue.remove(0);
			}
			
			// Return to sender
			dataEvent.server.send(dataEvent.socket, dataEvent.data);
		}
	}
}