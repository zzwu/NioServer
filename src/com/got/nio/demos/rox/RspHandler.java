package com.got.nio.demos.rox;

public class RspHandler {
	private byte[] rsp = null;
	
	public synchronized boolean handleResponse(byte[] rsp) {
		this.rsp = rsp;
		this.notify();
		return true;
	}
	
	public synchronized void waitForResponse() {
		while(rsp == null) {
			try {
				this.wait();
			} catch (InterruptedException e) {
			}
		}
		
		System.out.println(new String(rsp));
	}
}