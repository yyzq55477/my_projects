package meTest.util;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
 
import javax.net.ssl.SSLException;
 
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.oreilly.servlet.MultipartRequest;

/**
 * 4.5版本 
 */
@SuppressWarnings("all")
public class HttpClientUtil1 {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientUtil1.class);
	public static final long RELEASE_CONNECTION_WAIT_TIME = 5000;// 监控连接间隔
	private static PoolingHttpClientConnectionManager httpClientConnectionManager = null;
	private static LaxRedirectStrategy redirectStrategy = null;
	private static HttpRequestRetryHandler myRetryHandler = null;
	private static SSLConnectionSocketFactory sslConnectionSocketFactory = null;
	private static MyResponseHandler rh = null;
 
	static {
		initHttpClient();
		// 启动清理连接池链接线程
		Thread idleConnectionMonitorThread = new IdleConnectionMonitorThread(httpClientConnectionManager);
		idleConnectionMonitorThread.setDaemon(true);
		idleConnectionMonitorThread.start();
	}
 
	public static void initHttpClient() {
		try {
			rh = new MyResponseHandler();
			// 重定向策略初始化
			redirectStrategy = new LaxRedirectStrategy();
			// 请求重试机制，默认重试3次
			myRetryHandler = new HttpRequestRetryHandler() {
				public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
					if (executionCount >= 3) {
						return false;
					}
					if (exception instanceof InterruptedIOException) {
						return false;
					}
					if (exception instanceof UnknownHostException) {
						return false;
					}
					if (exception instanceof ConnectTimeoutException) {
						return false;
					}
					if (exception instanceof SSLException) {
						// SSL handshake exception
						return false;
					}
					HttpClientContext clientContext = HttpClientContext.adapt(context);
					HttpRequest request = clientContext.getRequest();
					boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
					if (idempotent) {
						// Retry if the request is considered idempotent
						return true;
					}
					return false;
				}
			};
			SSLContextBuilder builder = new SSLContextBuilder();
			builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
			sslConnectionSocketFactory = new SSLConnectionSocketFactory(builder.build(), NoopHostnameVerifier.INSTANCE);
			Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
					.register("http", new PlainConnectionSocketFactory())//
					.register("https", sslConnectionSocketFactory)//
					.build();
			// 创建httpclient连接池
			httpClientConnectionManager = new PoolingHttpClientConnectionManager(registry);
			// 设置连接池最大数量,这个参数表示所有连接最大数。
			httpClientConnectionManager.setMaxTotal(200);
			// 设置单个路由最大连接数量，表示单个域名的最大连接数，
			// 例如:www.baidu.com.www.google.com表示不同的域名,则连接统一域名下的资源的最大连接数就是该参数,总和是上面的参数。
			httpClientConnectionManager.setDefaultMaxPerRoute(100);
		} catch (Exception e) {
			LOGGER.error("初始化httpclient连接池失败.", e);
		}
 
	}
 
	public static CloseableHttpClient getHttpClient() {
		// HttpHost ip = ConfigFileUtil.getRandomIp();
		RequestConfig requestConfig = RequestConfig.custom()//
				.setConnectTimeout(3000)//
				.setSocketTimeout(3000)//
				// 忽略cookie,如果不需要登陆最好去掉,否则修改策略保存cookie即可
				.setCookieSpec(CookieSpecs.IGNORE_COOKIES)//
				.setConnectionRequestTimeout(6000)//
				// .setProxy(ip)//设置代理ip,不设置就用本机
				.build();
		// 连接池配置
		CloseableHttpClient httpClient = HttpClients.custom()//
				.setSSLSocketFactory(sslConnectionSocketFactory)//
				.setConnectionManager(httpClientConnectionManager)//
				.setDefaultRequestConfig(requestConfig)//
				.setRedirectStrategy(redirectStrategy)//
				.setRetryHandler(myRetryHandler)//
				.build();
 
		return httpClient;
	}
 
	/**
	 * get请求 headers表示特殊的请求头
	 */
	public static String getUrlContent(String urlString, Map<String, String>... headers) {
		String html = "";
		HttpGet httpGet = null;
		urlString = urlString.trim();
		if (null == urlString || urlString.isEmpty() || !urlString.startsWith("http")) {
			return html;
		}
		// 转化String url为URI,解决url中包含特殊字符的情况
		try {
			URL url = new URL(urlString);
			URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null);
			httpGet = new HttpGet(uri);
			setCommonHeaders(httpGet);
			// 额外的header
			if (headers != null && headers.length > 0) {
				for (Map.Entry<String, String> header : headers[0].entrySet()) {
					if (httpGet.containsHeader(header.getKey())) {
						httpGet.setHeader(header.getKey(), header.getValue());
					} else {
						httpGet.addHeader(header.getKey(), header.getValue());
					}
				}
			}
			HttpClient httpClient = getHttpClient();
			html = httpClient.execute(httpGet, rh);
		} catch (Exception e) {
			LOGGER.error("请求错误·url=" + urlString);
		} finally {
			httpGet.abort();
		}
		return html;
	}
 
	/**
	 * post请求,params表示参数,headers表示请求头
	 */
	public static String postForEntity(String urlString, Map<String, String> params, Map<String, String>... headers) {
		String html = "";
		HttpPost httpPost = null;
		urlString = urlString.trim();
		try {
			// 参数设置
			if (null == urlString || urlString.isEmpty() || !urlString.startsWith("http")) {
				return html;
			}
			URL url = new URL(urlString);
			URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null);
			httpPost = new HttpPost(uri);
			setCommonHeaders(httpPost);
			// 额外的header
			if (headers != null && headers.length > 0) {
				for (Map.Entry<String, String> header : headers[0].entrySet()) {
					if (httpPost.containsHeader(header.getKey())) {
						httpPost.setHeader(header.getKey(), header.getValue());
					} else {
						httpPost.addHeader(header.getKey(), header.getValue());
					}
				}
			}
			if (params != null && params.size() > 0) {
				List<BasicNameValuePair> nvps = new ArrayList<>();
				for (Map.Entry<String, String> entry : params.entrySet()) {
					nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
				// 这里设置的是返回结果编码,大多数都是UTF-8,如果乱码,可以查看网页的meta中的content的编码.如果是GBK,这里改为GBK即可.
				// 这里entity只能读取一次,想要读取多次,百度一下.
				httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
			}
			HttpClient httpClient = getHttpClient();
			html = httpClient.execute(httpPost, rh);
		} catch (Exception e) {
			LOGGER.error("请求错误·url=" + urlString);
		} finally {
			httpPost.abort();
		}
		return html;
 
	}
 
	private static void setCommonHeaders(AbstractHttpMessage request) {
		request.addHeader("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		// request.addHeader("Connection", "keep-alive");
		request.addHeader("Accept-Language", "zh-CN,zh;q=0.8");
		request.addHeader("Accept-Encoding", "gzip, deflate, br");
		// User-Agent最好随机换
		request.addHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36");
	}
 
	/**
	 * 响应处理类,处理返回结果
	 * 
	 * @author 王
	 *
	 */
	public static class MyResponseHandler implements ResponseHandler<String> {
 
		@Override
		public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
			try {
				// 返回状态码
				int statusCode = response.getStatusLine().getStatusCode();
				switch (statusCode) {
				case 200:
					return EntityUtils.toString(response.getEntity(), "UTF-8");
				case 400:
					LOGGER.error("下载400错误代码，请求出现语法错误.");
					break;
				case 403:
					LOGGER.error("下载403错误代码，资源不可用.");
					break;
				case 404:
					LOGGER.error("下载404错误代码，无法找到指定资源地址.");
					break;
				case 503:
					LOGGER.error("下载503错误代码，服务不可用.");
					break;
				case 504:
					LOGGER.error("下载504错误代码，网关超时.");
					break;
				default:
					LOGGER.error("未处理的错误,code=" + statusCode);
				}
 
			} finally {
				if (response != null) {
					try {
						((CloseableHttpResponse) response).close();
					} catch (IOException e) {
						LOGGER.error("关闭响应错误.", e);
					}
				}
			}
			return "";
		}
 
	}
 
	/**
	 * 连接处理,释放连接池连接
	 * 
	 * @author 王
	 *
	 */
	public static class IdleConnectionMonitorThread extends Thread {
 
		private static volatile boolean shutdown = false;
		private PoolingHttpClientConnectionManager poolingHttpClientConnectionManager;
 
		public IdleConnectionMonitorThread(PoolingHttpClientConnectionManager poolingHttpClientConnectionManager) {
			this.poolingHttpClientConnectionManager = poolingHttpClientConnectionManager;
		}
 
		@Override
		public void run() {
			try {
				while (!shutdown) {
					synchronized (IdleConnectionMonitorThread.class) {
						wait(RELEASE_CONNECTION_WAIT_TIME);
						// Close expired connections
						poolingHttpClientConnectionManager.closeExpiredConnections();
						// that have been idle longer than 30 sec
						poolingHttpClientConnectionManager.closeIdleConnections(2, TimeUnit.MINUTES);
					}
				}
			} catch (InterruptedException ex) {
				LOGGER.error("释放连接池连接出错.");
			}
		}
 
		public void shutdown() {
			shutdown = true;
			synchronized (IdleConnectionMonitorThread.class) {
				notifyAll();
			}
		}
 
	}
 
	public static boolean checkIp(HttpHost ip) {
		HttpResponse response;
		int code = 0;
		try {
			String url = "https://www.baidu.com";
			HttpClient hc = HttpClients.custom()//
					.setProxy(ip)//
					.build();
			HttpGet httpGet = new HttpGet(url);
			response = hc.execute(httpGet);
			code = response.getStatusLine().getStatusCode();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return code == 200;
	}
 
	public static void main(String[] args) {
		System.out.println(getUrlContent("https://www.baidu.com"));
	}
 
}
