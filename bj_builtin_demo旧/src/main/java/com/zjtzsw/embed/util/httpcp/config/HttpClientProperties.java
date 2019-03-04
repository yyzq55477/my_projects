package com.zjtzsw.embed.util.httpcp.config;

public class HttpClientProperties {
	
//	public static final String APIROUTE = "";
	public static final String APISESSION = "SXG_RSKS_API_SESSION";
	public static final String LOCALSESSION = "SXG_RSKS_ADMIN_SESSION";

	public static final int MANAGER_MAXTOTAL = 200;
	public static final int MANAGER_DEFAULTMAXPERROUTE = 100;
	public static final int MANAGER_MAXPERROUTE = 100;
	public static final int MANAGER_CLOSEIDLECONNECTIONSTIME = 30;

	public static final boolean MANAGER_SOCKET_TCPNODELAY = true;
	public static final boolean MANAGER_SOCKET_SOREUSEADDRESS = true;
	public static final int MANAGER_SOCKET_SOTIMEOUT = 500;
	public static final int	MANAGER_SOCKET_SOLINGER	= 60;
	public static final boolean MANAGER_SOCKET_SOKEEPALIVE = true;

	public static final int	MANAGER_MESSAGE_HEADERCOUNT = 200;
	public static final int	MANAGER_MESSAGE_LINELENGTH = 2000;

	public static final int	HTTPCLIENT_REQUESTCONFIG_CONNECTTIMEOUT = 100000;
	public static final int	HTTPCLIENT_REQUESTCONFIG_SOCKETTIMEOUT = 300000;
	public static final int	HTTPCLIENT_REQUESTCONFIG_CONNECTIONREQUESTTIMEOUT = 300000;

	public static final boolean	HTTPCLIENT_RETRY_ENABLE = true;

	public static final boolean	HTTPCLIENT_PROXY_ENABLED = false;
	public static final String HTTPCLIENT_PROXY_HOSTROUTE = "127.0.0.1:8888"; 
	
}
