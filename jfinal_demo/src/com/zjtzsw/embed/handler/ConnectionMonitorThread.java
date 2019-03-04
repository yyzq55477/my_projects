package com.zjtzsw.embed.handler;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 * Http连接池定时轮询处理类
 * 
 * @author franocris
 *
 */
public class ConnectionMonitorThread extends Thread {

	private final PoolingHttpClientConnectionManager connMgr;

	public ConnectionMonitorThread(PoolingHttpClientConnectionManager connMgr) {
		super();
		this.connMgr = connMgr;
	}
	
	/**
	 * <p>方法：timerCloseConnection</p>
	 * <p>描述：通过定时清理连接池中连接</p>
	 * @param connManager
	 * @author franocris
	 * 2018年7月12日
	 */
	public void monitorRun() {
		final PoolingHttpClientConnectionManager cm = connMgr;
		Timer timer = new Timer();
	    timer.schedule(new TimerTask() {
	        @Override
	        public void run() {
	            System.out.println("=====closeIdleConnections===");
	            cm.closeExpiredConnections();
	            cm.closeIdleConnections(5, TimeUnit.SECONDS);
	        }
	    }, 0, 5 * 1000);
	}

}