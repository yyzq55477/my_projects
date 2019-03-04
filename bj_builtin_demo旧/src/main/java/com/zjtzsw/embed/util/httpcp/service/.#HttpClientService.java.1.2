package com.zjtzsw.embed.util.httpcp.service;

import java.util.concurrent.TimeUnit;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.zjtzsw.embed.util.httpcp.config.HttpClientConfig;
import com.zjtzsw.embed.util.httpcp.factory.ApiServiceFactory;
import com.zjtzsw.embed.util.httpcp.factory.PoolManagerFactory;
import com.zjtzsw.embed.util.httpcp.handler.IdleConnectionMonitorThread;


/**
 * @author franocris
 *
 */
public class HttpClientService {

	private final static HttpClientConfig httpClientConfig = new HttpClientConfig();
	
	private static CloseableHttpClient httpClient;
	
	private final static Object syncLock = new Object(); // 相当于线程锁,用于线程安全
	
	public static void createHttpClient() {
		synchronized(syncLock) {
			httpClient =  HttpClients.custom()
					//.setProxy(httpClientConfig.getProxyhttpHost())       //设置代理,暂不启用代理
					.setConnectionManager(PoolManagerFactory.connManager)             //连接管理器	
					.setDefaultRequestConfig(httpClientConfig.getRequestConfig()) //默认请求配置
					.setRetryHandler(httpClientConfig.getHttpRequestRetryHandler())               //重试策略
					.evictIdleConnections(30, TimeUnit.SECONDS)
					.build();
		}
	}
	
	public synchronized static CloseableHttpClient getHttpClient() {
		if(httpClient == null)	 {
			createHttpClient();
		}
		return httpClient;
	}	
	
}
