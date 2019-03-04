package com.zjtzsw.embed.handler;

import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 * 连接处理,释放连接池连接
 * 
 * @author 王
 *
 */
public class IdleConnectionMonitorThread extends Thread {
	public static Log log=LogFactory.getLog(MyResponseHandler.class); 
	public static final long RELEASE_CONNECTION_WAIT_TIME = 5000;// 监控连接间隔
	private static volatile boolean shutdown = false;
	private PoolingHttpClientConnectionManager poolingHttpClientConnectionManager;

	public IdleConnectionMonitorThread(PoolingHttpClientConnectionManager poolingHttpClientConnectionManager) {
		this.poolingHttpClientConnectionManager = poolingHttpClientConnectionManager;
	}

	@Override
	public void run() {
		try {
			while (!shutdown) {
				synchronized (this) {
					//System.out.println("===================HttpClient连接池监控开始===================");
					// Close expired connections
					poolingHttpClientConnectionManager.closeExpiredConnections();
					// that have been idle longer than 30 sec
					poolingHttpClientConnectionManager.closeIdleConnections(2, TimeUnit.MINUTES);
					wait(RELEASE_CONNECTION_WAIT_TIME);
				}
			}
		} catch (InterruptedException ex) {
			log.error("释放连接池连接出错.");
		}
	}

	public void shutdown() {
		shutdown = true;
		synchronized (IdleConnectionMonitorThread.class) {
			notifyAll();
		}
	}

}
