package com.zjtzsw.embed.util.httpcp.config;

import java.nio.charset.CodingErrorAction;

import org.apache.http.Consts;
import org.apache.http.HttpHost;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.routing.HttpRoute;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.zjtzsw.embed.ProxyServlet;

/**
 * HttpClient连接池管理器配置
 * 因部分参数可能会影响性能因此暂不开放完全自定义，如有微调可用set方法设置调整
 * 
 * @author franocris
 *
 */
public class HttpClientPoolManageConfig {

//	final private static Prop prop = PropKit.use("api.properties");
	//socket配置（默认配置 和 某个host的配置
	private SocketConfig socketConfig;
	//HTTP connection相关配置
	private ConnectionConfig connectionConfig;
	//最大连接数
	private int conManagerMaxTotal = 200;
	//默认的每个路由的最大连接数
	private int conManagerDefaultMaxPerRoute = 100;
	//HttpHost
	private HttpHost httpHost;
	//HttpRoute
	private HttpRoute httpRoute;
	//设置到某个路由的最大连接数，会覆盖defaultMaxPerRoute,不可超过最大连接数
	private int maxPerRoute = 100;
	//该方法关闭空闲时间超过timeout的连接，空闲时间从交还给连接池时开始，不管是否已过期，超过空闲时间则关闭。
	private int closeIdleConnectionsTime = 30;	

	/**
	 *	通过配置文件初始化所有参数
	 */
	public HttpClientPoolManageConfig() {
		super();
		Boolean tcpNoDelay = HttpClientProperties.MANAGER_SOCKET_TCPNODELAY;
		Boolean soReuseAddress = HttpClientProperties.MANAGER_SOCKET_SOREUSEADDRESS;
		int soTimeout = HttpClientProperties.MANAGER_SOCKET_SOTIMEOUT;
		int soLinger = HttpClientProperties.MANAGER_SOCKET_SOLINGER;
		Boolean soKeepAlive = HttpClientProperties.MANAGER_SOCKET_SOKEEPALIVE;
		this.socketConfig = getPropSocketConfig(tcpNoDelay, soReuseAddress, soTimeout, soLinger, soKeepAlive);
		int maxHeaderCount = HttpClientProperties.MANAGER_MESSAGE_HEADERCOUNT;
		int maxLineLength = HttpClientProperties.MANAGER_MESSAGE_LINELENGTH;
		this.connectionConfig = getPropConnectionConfig(maxHeaderCount, maxLineLength);
		this.conManagerMaxTotal = HttpClientProperties.MANAGER_MAXTOTAL;
		this.conManagerDefaultMaxPerRoute = HttpClientProperties.MANAGER_DEFAULTMAXPERROUTE;
		this.httpHost = getPropHttpHost(ProxyServlet.apiroute);
		this.httpRoute = new HttpRoute(this.httpHost);
		this.maxPerRoute = HttpClientProperties.MANAGER_MAXPERROUTE;
		this.closeIdleConnectionsTime = HttpClientProperties.MANAGER_CLOSEIDLECONNECTIONSTIME;
	}
	
	/**
	 * socket配置（默认配置 和 某个host的配置
	 */
	private SocketConfig getPropSocketConfig(Boolean tcpNoDelay,Boolean soReuseAddress,int soTimeout,int soLinger,Boolean soKeepAlive) {
		SocketConfig propSocketConfig = SocketConfig.custom()
				.setTcpNoDelay(tcpNoDelay)     //是否立即发送数据，设置为true会关闭Socket缓冲，默认为false
				.setSoReuseAddress(true) //是否可以在一个进程关闭Socket后，即使它还没有释放端口，其它进程还可以立即重用端口
				.setSoTimeout(500)       //接收数据的等待超时时间，单位ms
				.setSoLinger(60)         //关闭Socket时，要么发送完所有数据，要么等待60s后，就关闭连接，此时socket.close()是阻塞的
				.setSoKeepAlive(true)    //开启监视TCP连接是否有效
				.build();
		return propSocketConfig;
	}


	/**
	 * HTTP connection相关配置（默认配置 和 某个host的配置）
	 * 一般不修改HTTP connection相关配置，故不设置
	 */
	private ConnectionConfig getPropConnectionConfig(int maxHeaderCount,int maxLineLength) {
		//消息约束
		MessageConstraints messageConstraints = MessageConstraints.custom()
				.setMaxHeaderCount(maxHeaderCount)
				.setMaxLineLength(maxLineLength)
				.build();
		ConnectionConfig connectionConfig = ConnectionConfig.custom()
				.setMalformedInputAction(CodingErrorAction.IGNORE)
				.setUnmappableInputAction(CodingErrorAction.IGNORE)
				.setCharset(Consts.UTF_8)
				.setMessageConstraints(messageConstraints)
				.build();
		return connectionConfig;
	}

	/**
	 * HttpHost
	 */
	private HttpHost getPropHttpHost(String apiroute) {
		String host = apiroute.split(":")[1].substring(2);
		String port = apiroute.split(":")[2].split("/")[0];
		HttpHost httpHost = new HttpHost(host, Integer.parseInt(port));
		return httpHost;
	}

	/**
	 * @return the socketConfig
	 */
	public SocketConfig getSocketConfig() {
		return socketConfig;
	}

	/**
	 * @param socketConfig the socketConfig to set
	 */
	public void setSocketConfig(SocketConfig socketConfig) {
		this.socketConfig = socketConfig;
	}

	/**
	 * @return the connectionConfig
	 */
	public ConnectionConfig getConnectionConfig() {
		return connectionConfig;
	}

	/**
	 * @param connectionConfig the connectionConfig to set
	 */
	public void setConnectionConfig(ConnectionConfig connectionConfig) {
		this.connectionConfig = connectionConfig;
	}

	/**
	 * @return the conManagerMaxTotal
	 */
	public int getConManagerMaxTotal() {
		return conManagerMaxTotal;
	}

	/**
	 * @param conManagerMaxTotal the conManagerMaxTotal to set
	 */
	public void setConManagerMaxTotal(int conManagerMaxTotal) {
		this.conManagerMaxTotal = conManagerMaxTotal;
	}

	/**
	 * @return the conManagerDefaultMaxPerRoute
	 */
	public int getConManagerDefaultMaxPerRoute() {
		return conManagerDefaultMaxPerRoute;
	}

	/**
	 * @param conManagerDefaultMaxPerRoute the conManagerDefaultMaxPerRoute to set
	 */
	public void setConManagerDefaultMaxPerRoute(int conManagerDefaultMaxPerRoute) {
		this.conManagerDefaultMaxPerRoute = conManagerDefaultMaxPerRoute;
	}

	/**
	 * @return the httpHost
	 */
	public HttpHost getHttpHost() {
		return httpHost;
	}

	/**
	 * @param httpHost the httpHost to set
	 */
	public void setHttpHost(HttpHost httpHost) {
		this.httpHost = httpHost;
	}

	/**
	 * @return the httpRoute
	 */
	public HttpRoute getHttpRoute() {
		return httpRoute;
	}

	/**
	 * @param httpRoute the httpRoute to set
	 */
	public void setHttpRoute(HttpRoute httpRoute) {
		this.httpRoute = httpRoute;
	}

	/**
	 * @return the maxPerRoute
	 */
	public int getMaxPerRoute() {
		return maxPerRoute;
	}

	/**
	 * @param maxPerRoute the maxPerRoute to set
	 */
	public void setMaxPerRoute(int maxPerRoute) {
		this.maxPerRoute = maxPerRoute;
	}

	/**
	 * @return the closeIdleConnectionsTime
	 */
	public int getCloseIdleConnectionsTime() {
		return closeIdleConnectionsTime;
	}

	/**
	 * @param closeIdleConnectionsTime the closeIdleConnectionsTime to set
	 */
	public void setCloseIdleConnectionsTime(int closeIdleConnectionsTime) {
		this.closeIdleConnectionsTime = closeIdleConnectionsTime;
	}
	
	
}
