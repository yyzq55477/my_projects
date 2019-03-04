package com.zjtzsw.embed.util.httpcp.config;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.protocol.HttpContext;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;

/**
 * HttpClient相关参数配置
 * 
 * @author franocris
 *
 */
public class HttpClientConfig {

//	final private static Prop prop = PropKit.use("api.properties");

	//request请求相关配置
	private RequestConfig requestConfig;
	//重试处理
	private HttpRequestRetryHandler httpRequestRetryHandler;
	//代理服务
	private HttpHost proxyhttpHost;

	/**
	 * 根据配置文件初始化HttpClientConfig
	 */
	public HttpClientConfig() {
		super();
		int connectTimeout = HttpClientProperties.HTTPCLIENT_REQUESTCONFIG_CONNECTTIMEOUT;
		int socketTimeout = HttpClientProperties.HTTPCLIENT_REQUESTCONFIG_SOCKETTIMEOUT;
		int connectionRequestTimeout = HttpClientProperties.HTTPCLIENT_REQUESTCONFIG_CONNECTIONREQUESTTIMEOUT;
		this.requestConfig = getPropRequestConfig(connectTimeout, socketTimeout, connectionRequestTimeout);
		Boolean retryEnable = HttpClientProperties.HTTPCLIENT_RETRY_ENABLE;
		this.httpRequestRetryHandler = getPropHttpRequestRetryHandler(retryEnable);
		Boolean proxyEnable = HttpClientProperties.HTTPCLIENT_PROXY_ENABLED;
		String proxyHostroute = HttpClientProperties.HTTPCLIENT_PROXY_HOSTROUTE;
		this.proxyhttpHost = getPropproxyhttpHost(proxyEnable, proxyHostroute);
	}

	@SuppressWarnings("unused")
	private String getHostRoute(String apiroute) {
		String[] pa = apiroute.split(":");
		String hostname = pa[1].substring(2);
		String port = pa[2].split("/")[0];
		return hostname+port;
	}
	
	/**
	 * @return the proxyhttpHost
	 */
	public HttpHost getProxyhttpHost() {
		return proxyhttpHost;
	}


	/**
	 * @param proxyhttpHost the proxyhttpHost to set
	 */
	public void setProxyhttpHost(HttpHost proxyhttpHost) {
		this.proxyhttpHost = proxyhttpHost;
	}


	/**
	 * @return the requestConfig
	 */
	public RequestConfig getRequestConfig() {
		return requestConfig;
	}


	/**
	 * @param requestConfig the requestConfig to set
	 */
	public void setRequestConfig(RequestConfig requestConfig) {
		this.requestConfig = requestConfig;
	}


	/**
	 * @return the httpRequestRetryHandler
	 */
	public HttpRequestRetryHandler getHttpRequestRetryHandler() {
		return httpRequestRetryHandler;
	}


	/**
	 * @param httpRequestRetryHandler the httpRequestRetryHandler to set
	 */
	public void setHttpRequestRetryHandler(HttpRequestRetryHandler httpRequestRetryHandler) {
		this.httpRequestRetryHandler = httpRequestRetryHandler;
	}

	/**
	 * <p>方法：getPropproxyhttpHost</p>
	 * <p>描述：根据配置文件获得代理服务器相关配置</p>
	 * @param proxyEnable
	 * @param proxyHostroute
	 * @return
	 * @author franocris
	 * 2018年7月12日
	 */
	private HttpHost getPropproxyhttpHost(Boolean proxyEnable,String proxyHostroute) {
		HttpHost proxyHost = null;
		if(proxyEnable) {
			String host = proxyHostroute.split(":")[0];
			String port = proxyHostroute.split(":")[1];
			proxyHost = new HttpHost(host,Integer.parseInt(port));
		}
		return proxyHost;
		
	}

	/**
	 * <p>方法：getPropRequestConfig</p>
	 * <p>描述：根据配置文件获得Request相关配置</p>
	 * @param connectTimeout
	 * @param socketTimeout
	 * @param connectionRequestTimeout
	 * @return
	 * @author franocris
	 * 2018年7月12日
	 */
	private RequestConfig getPropRequestConfig(int connectTimeout,int socketTimeout,int connectionRequestTimeout) {
		RequestConfig defaultRequestConfig = RequestConfig.custom()
			.setConnectTimeout(connectTimeout)         //连接超时时间
			.setSocketTimeout(socketTimeout)          //读超时时间（等待数据超时时间）
			.setConnectionRequestTimeout(connectionRequestTimeout)    //从池中获取连接超时时间
			//过时方法不推荐使用，影响效率
			//.setStaleConnectionCheckEnabled(true)//检查是否为陈旧的连接，默认为true，类似testOnBorrow
			.build();
		return defaultRequestConfig;
	}

	/**
	 * <p>方法：getPropHttpRequestRetryHandler</p>
	 * <p>描述：根据配置文件获得HttpRequest连接失败重试尝试处理对象</p>
	 * @param retryEnable
	 * @return
	 * @author franocris
	 * 2018年7月12日
	 */
	private HttpRequestRetryHandler getPropHttpRequestRetryHandler(Boolean retryEnable) {
		HttpRequestRetryHandler httpRequestRetryHandler;
		if(!retryEnable) {
			//禁用重试(参数：retryCount、requestSentRetryEnabled)
			httpRequestRetryHandler = new DefaultHttpRequestRetryHandler(0, false);
		}else {		
			//自定义重试策略
			httpRequestRetryHandler = new HttpRequestRetryHandler() {
	
				public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
					if (executionCount >= 5) {// 如果已经重试了5次，就放弃
						return false;
					}
					if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
						return true;
					}
					if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
						return false;
					}
					if (exception instanceof InterruptedIOException) {// 超时
						return false;
					}
					if (exception instanceof UnknownHostException) {// 目标服务器不可达
						return false;
					}
					if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
						return false;
					}
					if (exception instanceof SSLException) {// SSL握手异常
						return false;
					}
					HttpClientContext clientContext = HttpClientContext.adapt(context);
					HttpRequest request = clientContext.getRequest();
					boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
					//Retry if the request is considered idempotent
					//如果请求类型不是HttpEntityEnclosingRequest，被认为是幂等的，那么就重试
					//HttpEntityEnclosingRequest指的是有请求体的request，比HttpRequest多一个Entity属性
					//而常用的GET请求是没有请求体的，POST、PUT都是有请求体的
					//Rest一般用GET请求获取数据，故幂等，POST用于新增数据，故不幂等
					if (idempotent) {
						return true;
					}
					return false;
				}
			};
		}
		return httpRequestRetryHandler;
	}
}
