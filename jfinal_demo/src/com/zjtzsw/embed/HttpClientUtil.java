package com.zjtzsw.embed;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
//import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zjtzsw.embed.SpUtils.HttpMethod;
import com.zjtzsw.embed.handler.IdleConnectionMonitorThread;
import com.zjtzsw.embed.handler.MyResponseHandler;



public class HttpClientUtil {
	public final static String VERSION = "1.2";

	public static final String[] TEXT_MIMES = new String[] { "text/html",
			"text/plain", "text/xml", "application/json",
			"application/xhtml+xml", "application/xml",
			"application/x-javascript", "application/javascript",
			"text/javascript","text/css" };

	public static final String[] FILE_EXTNAMES = new String[] { "swf", "png",
			"gif", "xls", "csv", "jpg", "pdf", "jar" ,"flv"};

	private static final Pattern FILE_EXTNAMES_PATTERN;
//-------------------------------------------------	
	public static final long RELEASE_CONNECTION_WAIT_TIME = 5000;// 监控连接间隔
	private static PoolingHttpClientConnectionManager httpClientConnectionManager = new PoolingHttpClientConnectionManager();
	private static LaxRedirectStrategy redirectStrategy = null;
	private static MyResponseHandler rh = null;
 
	static {
		StringBuilder sb = new StringBuilder(".+(");
		for (String name : FILE_EXTNAMES) {
			sb.append("\\.").append(name).append("|");
		}
		sb.deleteCharAt(sb.length() - 1).append(")($|\\?)");

		FILE_EXTNAMES_PATTERN = Pattern.compile(sb.toString(),
				Pattern.CASE_INSENSITIVE); // 不区分大小写
	}
	
	public static final int MAX_CONN_PER_ROUTE = 150;
	public static final int MAX_CONN_TOTAL = 2000;
	public static final int MINS = 60 * 1000;
	public static final int SOCKET_TIMEOUT = 5 * MINS;
	public static final int CONNECT_TIMEOUT = 10 * MINS;
	public static final int CONNECTION_REQUEST_TIMEOUT = 5 * MINS;

	/**
	 * 是否文本类型
	 * 
	 * @param mime
	 * @param defaultBoolean
	 *            不能判断，默认返回类型
	 * @return
	 */
	public static boolean isTextMimeType(String mime, boolean defaultBoolean) {
		if (StringUtils.isEmpty(mime))
			return defaultBoolean;
		return ArrayUtils.contains(TEXT_MIMES, mime);
	}

	/**
	 * 是否文本类型
	 * 
	 * @param mime
	 * @param defaultBoolean
	 *            不能判断，默认返回类型
	 * @return
	 */
	public static boolean resolveTextMimeTypeByUrl(String url,
			boolean defaultBoolean) {
		if (StringUtils.isEmpty(url))
			return defaultBoolean;
		return !FILE_EXTNAMES_PATTERN.matcher(url).find();
	}

	/**
	 * @param httpResponse
	 * @param response
	 */
	public static void copyResponse(HttpResponse httpResponse,
			HttpServletResponse response) {
		copyResponseWithoutContent(httpResponse, response);

		copyResponseContent(httpResponse, response);
	}

	/**
	 * 只复制内容
	 * 
	 * @param httpResponse
	 * @param response
	 */
	public static void copyResponseContent(HttpResponse httpResponse,
			HttpServletResponse response) {
		HttpEntity entity = httpResponse.getEntity();

		if (entity == null)
			return; // 304缓存不处理

		// 复制响应内容
		try {
			entity.writeTo(response.getOutputStream());
		} catch (Exception e) {

		
		}
		EntityUtils.consumeQuietly(entity);
	}

	/**
	 * 不复制内容
	 * 
	 * @param httpResponse
	 * @param response
	 */
	public static void copyResponseWithoutContent(HttpResponse httpResponse,
			HttpServletResponse response) {
		copyResponseWithoutContent(httpResponse, response, null);
	}

	public static void copyResponseWithoutContent(HttpResponse httpResponse,
			HttpServletResponse response, String[] excludeHeaders) {
		// 复制headers
		Header[] headers = httpResponse.getAllHeaders();
		for (Header header : headers) {
			String stringHeaderName = header.getName();
			if (HTTP.TRANSFER_ENCODING.equalsIgnoreCase(stringHeaderName)
					|| HTTP.CONTENT_LEN.equalsIgnoreCase(stringHeaderName)
					|| HTTP.TARGET_HOST.equalsIgnoreCase(stringHeaderName)) {
				continue;
			}

			if (excludeHeaders != null) {
				for (String headerName : excludeHeaders) {
					if (headerName.equalsIgnoreCase(stringHeaderName)) {
						stringHeaderName = null;
						break;
					}
				}
			}

			if (stringHeaderName == null)
				continue;

			response.addHeader(stringHeaderName, header.getValue());
		}

		// 设置状态
		response.setStatus(httpResponse.getStatusLine().getStatusCode());
	}

	public static void addParameters(HttpRequestBase httpRequest, String params) {
		addParameters(httpRequest, queryString2Map(params));
	}

	public static Map<String, String> queryString2Map(String query) {
		if (StringUtils.isBlank(query))
			return null;

		Map<String, String> params = new LinkedHashMap<String, String>();

		for (String param : query.split("&")) {
			if (StringUtils.isBlank(param))
				continue;

			String[] entry = param.split("=");
			params.put(entry[0], entry.length > 1 ? entry[1] : null);
		}

		return params;
	}

	/**
	 * 将参数添加到httpRequest中
	 * 
	 * @param httpRequest
	 * @param params
	 */
	public static void addParameters(HttpRequestBase httpRequest,
			Map<String, String> params) {
		addParameters(httpRequest,params,null);
	}

	/**
	 * 将参数添加到httpRequest中
	 * 
	 * @param httpRequest
	 * @param params
	 */
	public static void addParameters(HttpRequestBase httpRequest,
			Map<String, String> params, String charset) {
		charset = charset == null ? WebUtil.getDefaultCharset() : charset;

		if (params == null || params.size() == 0)
			return;

		if (httpRequest instanceof HttpGet) {
			StringBuffer param = new StringBuffer();
			for (Entry<String, String> entry : params.entrySet()) {
				try {
					param.append(entry.getKey())
							.append('=')
							.append(URLEncoder.encode(
									ObjectUtils.toString(entry.getValue()),
									charset)).append("&");
				} catch (UnsupportedEncodingException e) {
					WebUtil.log("参数编码错误：" + entry.getValue());
				}
			}

			String paramStr = param.substring(0, param.length());
			if (StringUtils.isEmpty(paramStr))
				return;

			String url = httpRequest.getURI().toString();
			httpRequest.setURI(URI.create(url
					+ (url.indexOf("?") == -1 ? "?" : "&") + paramStr));
		} else if (httpRequest instanceof HttpEntityEnclosingRequestBase) {
			List<NameValuePair> formParams = new ArrayList<NameValuePair>();
			HttpEntity entity = null;
			try {
				for (Map.Entry<String, String> entry : params.entrySet()) {
					formParams.add(new BasicNameValuePair(entry.getKey(),
							ObjectUtils.toString(entry.getValue())));
				}

				entity = new UrlEncodedFormEntity(formParams, charset);
			} catch (UnsupportedEncodingException e) {
				WebUtil.log("参数编码错误：" + formParams.toString());
			}

			((HttpEntityEnclosingRequestBase) httpRequest).setEntity(entity);
		}
	}

	public static HttpRequestBase createHttpRequest(HttpMethod method,
			String url) {
		HttpRequestBase httpRequest = null;
		switch (method) {
		case GET:
			httpRequest = new HttpGet(url);
			break;
		case POST:
			httpRequest = new HttpPost(url);
			break;
		case DELETE:
			httpRequest = new HttpDelete(url);
			break;
		case HEAD:
			httpRequest = new HttpHead(url);
			break;
		case OPTIONS:
			httpRequest = new HttpOptions(url);
			break;
		case PUT:
			httpRequest = new HttpPut(url);
			break;
		default:
			throw new java.lang.UnsupportedOperationException("尚不支持的请求方法："
					+ method);
		}
		return httpRequest;
	}

	public static HttpRequestBase createHttpRequest(String method, String url) {
		return createHttpRequest(HttpMethod.valueOf(method), url);
	}

	public static HttpRequestBase createProxyRequest(String targetUrl,
			HttpServletRequest request, String defaultCharset) {
		return createProxyRequest(targetUrl, request, defaultCharset, null);
	}

	public static HttpRequestBase createProxyRequest(String targetUrl,
			HttpServletRequest request, String defaultCharset,
			String[] excludeHeaderNames) {
		HttpRequestBase httpRequest = createHttpRequest(request.getMethod()
				.toUpperCase(), targetUrl);

		copyRequest(request, httpRequest, defaultCharset, excludeHeaderNames);

		return httpRequest;
	}

	private static String resolveCharset(HttpServletRequest request,
			String defaultCharset) {
		String charset = request.getCharacterEncoding();
		charset = (charset == null ? defaultCharset : charset);

		if (StringUtils.isEmpty(charset))
			return WebUtil.getDefaultCharset();
		else
			return charset;
	}

	public static void copyRequest(HttpServletRequest request,
			HttpRequestBase httpRequest, String defaultCharset) {
		copyRequest(request, httpRequest, defaultCharset, null);
	}

	public static void copyRequest(HttpServletRequest request,
			HttpRequestBase httpRequest, String defaultCharset,
			String[] excludeHeaderNames) {
		addRequestHeaders(request, httpRequest, excludeHeaderNames);

		if (httpRequest instanceof HttpEntityEnclosingRequestBase) {
			try {
				HttpEntity entity = null;

				if (isMultiPart(request)) {
					WebUtil.log("...判断为文件上传请求，请求长度:"
							+ request.getContentLength());

					// printHeaders(httpRequest);

					entity = new InputStreamEntity(request.getInputStream(),
							request.getContentLength());
				} else {
					String charset = resolveCharset(request, defaultCharset);
					String content = IOUtils.toString(request.getInputStream(),
							charset);

					entity = new StringEntity(content, charset);
				}

				((HttpEntityEnclosingRequestBase) httpRequest)
						.setEntity(entity);
			} catch (Exception e) {
				WebUtil.log("复制Entity失败", e);
			}
		}
	}

	/**
	 * 稳妥但效率低的方式，不同的服务器，请求头的大小写可能有区别
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static boolean isMultiPart(HttpServletRequest request) {
		try {
			Enumeration<String> enumerationOfHeaderNames = request
					.getHeaderNames();
			while (enumerationOfHeaderNames.hasMoreElements()) {
				String stringHeaderName = enumerationOfHeaderNames
						.nextElement();

				if (HTTP.CONTENT_TYPE.equalsIgnoreCase(stringHeaderName)) {
					return StringUtils.indexOfIgnoreCase(
							request.getHeader(stringHeaderName),
							"multipart/form-data") != -1;
				}
			}
		} catch (Throwable t) {
			WebUtil.log("判断multipart失败", t);
			return false;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private static void addRequestHeaders(
			HttpServletRequest httpServletRequest, HttpRequestBase httpRequest,
			String[] excludeHeaderNames) {
		Enumeration<String> enumerationOfHeaderNames = httpServletRequest
				.getHeaderNames();
		while (enumerationOfHeaderNames.hasMoreElements()) {
			String stringHeaderName = enumerationOfHeaderNames.nextElement();

			if (HTTP.TRANSFER_ENCODING.equalsIgnoreCase(stringHeaderName)
					|| HTTP.CONTENT_LEN.equalsIgnoreCase(stringHeaderName)
					|| HTTP.TARGET_HOST.equalsIgnoreCase(stringHeaderName)
					|| WebUtil.HTTP_HEADER_COOKIE
							.equalsIgnoreCase(stringHeaderName)) {
				continue;
			}

			if (excludeHeaderNames != null) {
				for (String headerName : excludeHeaderNames) {
					if (headerName.equalsIgnoreCase(stringHeaderName)) {
						stringHeaderName = null;
						break;
					}
				}
			}

			if (stringHeaderName == null)
				continue;

			Enumeration<String> enumerationOfHeaderValues = httpServletRequest
					.getHeaders(stringHeaderName);
			while (enumerationOfHeaderValues.hasMoreElements()) {
				String value = enumerationOfHeaderValues.nextElement();
				Header header = new BasicHeader(stringHeaderName, value);
				httpRequest.addHeader(header);
			}
		}
	}

	
	public static RequestConfig createDefaultHttpClientConfig() {
		RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(SOCKET_TIMEOUT)
				.setConnectTimeout(CONNECT_TIMEOUT)
				.setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
				.setRedirectsEnabled(false).build();
		return requestConfig;
	}

	public static CloseableHttpClient createDefaultSslHttpClient(
			RequestConfig config) {
		SSLContext sslcontext = null;
		try {
			sslcontext = SSLContext.getInstance("SSL");
			X509TrustManager tm = new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkClientTrusted(X509Certificate[] arg0,
						String arg1) throws CertificateException {
					// checkClientTrusted =============
				}

				@Override
				public void checkServerTrusted(X509Certificate[] arg0,
						String arg1) throws CertificateException {
					// checkServerTrusted
				}
			};
			sslcontext.init(null, new TrustManager[] { tm }, null);

		} catch (KeyManagementException e) {
			WebUtil.log("SSL请求失败", e);
		} catch (NoSuchAlgorithmException e) {
			WebUtil.log("SSL请求失败", e);
		}

		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
				sslcontext,
				SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//---------------------------------------------
		rh = new MyResponseHandler();
		Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("http", new PlainConnectionSocketFactory())//
				.register("https", sslsf)//
				.build();
		
		// 创建httpclient连接池
		httpClientConnectionManager = new PoolingHttpClientConnectionManager(registry);
		// 设置连接池最大数量,这个参数表示所有连接最大数。
		httpClientConnectionManager.setMaxTotal(200);
		// 设置单个路由最大连接数量，表示单个域名的最大连接数，
		// 例如:www.baidu.com.www.google.com表示不同的域名,则连接统一域名下的资源的最大连接数就是该参数,总和是上面的参数。
		httpClientConnectionManager.setDefaultMaxPerRoute(100);
//		HttpHost httpHost = new HttpHost(hostname, port);
//		// 将目标主机的最大连接数增加
//		httpClientConnectionManager.setMaxPerRoute(new HttpRoute(httpHost), maxRoute);
		
		// 请求重试处理
		HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
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
				// 如果请求是幂等的，就再次尝试
				if (!(request instanceof HttpEntityEnclosingRequest)) {
					return true;
				}
				return false;
			}
		};
		
		cleanClient();//启动释放连接的线程
		
		System.out.println("-------开始获取HttpClient--------");
		long start = System.currentTimeMillis();
		// 连接池配置
		CloseableHttpClient httpclient = HttpClients.custom()//
				.setSSLSocketFactory(sslsf)//
				.setConnectionManager(httpClientConnectionManager)//
				.setDefaultRequestConfig(config)//
				.setRedirectStrategy(redirectStrategy)//
				.setRetryHandler(httpRequestRetryHandler)//
				.setMaxConnPerRoute(MAX_CONN_PER_ROUTE).setMaxConnTotal(MAX_CONN_TOTAL)
				.build();
		long usedTime = (System.currentTimeMillis() - start);
		System.out.println("总共用时" + usedTime + " ms");  
		System.out.println("-------------");
//---------------------
		return httpclient;
	}
	
	public static CloseableHttpClient createDefaultHttpClient(
			RequestConfig config) {
		CloseableHttpClient httpclient = HttpClients.custom()
				.setDefaultRequestConfig(config).setMaxConnPerRoute(MAX_CONN_PER_ROUTE)
				.setMaxConnTotal(MAX_CONN_TOTAL).build();
		return httpclient;
	}

	public static void printHeaders(HttpRequestBase httpRequest) {
		WebUtil.log("请求头列表：");
		for (Header header : httpRequest.getAllHeaders()) {
			WebUtil.log(header.toString());
		}
	}

	public static void printHeaders(HttpResponse httpResponse) {
		WebUtil.log("响应头列表：");
		for (Header header : httpResponse.getAllHeaders()) {
			WebUtil.log(header.toString());
		}
	}
	
	
	/**
	 * 将参数URLEncoder编码一次
	 * @param params
	 */
	public static void paramsEncoder(Map<String, String> params,String charset) {
		charset = charset == null ? WebUtil.getDefaultCharset() : charset;
		
		Iterator keyIterator = params.keySet().iterator();
		while (keyIterator.hasNext()) {
			String key = String.valueOf(keyIterator.next());
			String value = String.valueOf(params.get(key));
			try {
				params.put(key, URLEncoder.encode(value,charset));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void cleanClient() {
		// 启动清理连接池链接线程
		Thread idleConnectionMonitorThread = new IdleConnectionMonitorThread(httpClientConnectionManager);
		idleConnectionMonitorThread.setDaemon(true);
		idleConnectionMonitorThread.start();
	}

}
