package com.zjtzsw.embed.util.httpcp.factory;

import java.util.concurrent.TimeUnit;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import com.zjtzsw.embed.util.httpcp.config.HttpClientPoolManageConfig;
import com.zjtzsw.embed.util.httpcp.handler.IdleConnectionMonitorThread;
import com.zjtzsw.embed.util.httpcp.service.HttpClientService;


/**
 * @author franocris
 *
 */
public class PoolManagerFactory{
	//连接管理器
	public static PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();

	/**
	 * <p>方法：createPoolManage</p>
	 * <p>描述：创建连接管理器并配置相关参数</p>
	 * @author franocris
	 * 2018年7月12日
	 */
	private static void createPoolManage() {
		//相关参数配置
		HttpClientPoolManageConfig hcpmConfig = new HttpClientPoolManageConfig();
		//最大连接数
		connManager.setMaxTotal(hcpmConfig.getConManagerMaxTotal()); 
		//默认的每个路由的最大连接数
		connManager.setDefaultMaxPerRoute(hcpmConfig.getConManagerDefaultMaxPerRoute()); 
		//设置到某个路由的最大连接数，会覆盖defaultMaxPerRoute
		connManager.setMaxPerRoute(hcpmConfig.getHttpRoute(), hcpmConfig.getMaxPerRoute()); 
		//socket配置（默认配置 和 某个host的配置）
		connManager.setDefaultSocketConfig(hcpmConfig.getSocketConfig());
		//该方法关闭超过连接保持时间的连接，并从池中移除。
		connManager.closeExpiredConnections();
		//该方法关闭空闲时间超过timeout的连接，空闲时间从交还给连接池时开始，不管是否已过期，超过空闲时间则关闭。
		connManager.closeIdleConnections(hcpmConfig.getCloseIdleConnectionsTime(), TimeUnit.SECONDS);		
		//一般不修改HTTP connection相关配置，故不设置
		connManager.setDefaultConnectionConfig(hcpmConfig.getConnectionConfig());
		//connManager.setConnectionConfig(new HttpHost("somehost", 80), ConnectionConfig.DEFAULT);
		
	}
	
	public static void start() {
		createPoolManage();
		HttpClientService.createHttpClient();
		Thread idleConnectionMonitorThread = new IdleConnectionMonitorThread(connManager);
		idleConnectionMonitorThread.setDaemon(true);
		idleConnectionMonitorThread.start();
		//ConnectionMonitorThread cmt = new ConnectionMonitorThread(connManager);
		//cmt.monitorRun();
	}
}
