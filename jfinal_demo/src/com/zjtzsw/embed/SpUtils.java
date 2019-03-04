/**
 * 
 */
package com.zjtzsw.embed;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

/**
 * @author huenbin 2014年11月6日
 */
public class SpUtils {

	public static final int MAX_CONN_PER_ROUTE = 150;
	public static final int MAX_CONN_TOTAL = 2000;
	public static final int MINS = 60 * 1000;
	public static final int SOCKET_TIMEOUT = 5 * MINS;
	public static final int CONNECT_TIMEOUT = 10 * MINS;
	public static final int CONNECTION_REQUEST_TIMEOUT = 5 * MINS;

	public static final String HTTP_HEADER_COOKIE = "Cookie";
	static private final String DEFAULT_CHARSET = "utf-8";

	public enum HttpMethod {
		GET, POST, PUT, DELETE, HEAD, OPTIONS
	}

	public static String combine(String url, Map<String, String> params) {
		if (params == null)
			return combine(url, (String) null);

		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			try {
				String value = entry.getValue();
				if(value!=null&&!"".equals(value)){
				sb.append(entry.getKey())
						.append('=')
						.append(URLEncoder.encode(value,
								SpUtils.getDefaultCharset())).append('&');
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
			}
		}
		return combine(url, sb.toString());
	}

	public static String combine(String url, String queryString) {
		StringBuilder builder = new StringBuilder();

		queryString = queryString == null ? "" : queryString;

		if (url.contains("?")) {
			builder.append(url).append("&").append(queryString);
		} else {
			builder.append(url).append("?").append(queryString);
		}

		return builder.toString();
	}
	
	public static void copyRequest4Scan(HttpServletRequest request,
			HttpRequestBase httpRequest, String defaultCharset,
			String[] excludeHeaderNames) {
		String scanImage = request.getParameter("scanImage");
		HttpEntity entity = null;
		try {
			List<NameValuePair> formParams = new ArrayList<NameValuePair>();
			formParams.add(new BasicNameValuePair("scanImage", scanImage));
			entity = new UrlEncodedFormEntity(formParams, "UTF-8");
			((HttpEntityEnclosingRequestBase) httpRequest).setEntity(entity);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}


	public static Map<String, String> getParamsMap(HttpServletRequest request) {
		Map<String, String> params = new LinkedHashMap<String, String>();
		@SuppressWarnings("rawtypes")
		Enumeration headerNames = request.getParameterNames();
		while (headerNames.hasMoreElements()) {
			String key = (String) headerNames.nextElement();
			String value = request.getParameter(key);
			params.put(key, value);
		}

		return params;
	}

	public static Map<String, String> queryString2Map(String queryString) {
		if (StringUtils.isBlank(queryString))
			return null;

		Map<String, String> params = new LinkedHashMap<String, String>();

		for (String param : queryString.split("&")) {
			if (StringUtils.isBlank(param))
				continue;

			String[] entry = param.split("=");
			params.put(entry[0], entry.length > 1 ? entry[1] : null);
		}

		return params;
	}

	public static void printHead(HttpServletRequest request) {
		@SuppressWarnings("rawtypes")
		Enumeration headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String stringHeaderName = (String) headerNames.nextElement();
			try {
				System.out.println("Head="
						+ stringHeaderName
						+ " value="
						+ new String(request.getHeader(stringHeaderName)
								.getBytes(getDefaultCharset())));
			} catch (UnsupportedEncodingException e) {

			}
		}
	}

	public static CloseableHttpClient createDefaultHttpClient(
			RequestConfig config) {
		CloseableHttpClient httpclient = HttpClients.custom()
				.setDefaultRequestConfig(config)
				.setMaxConnPerRoute(MAX_CONN_PER_ROUTE)
				.setMaxConnTotal(MAX_CONN_TOTAL).build();
		return httpclient;
	}

	public static RequestConfig createDefaultHttpClientConfig() {
		RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(SOCKET_TIMEOUT)
				.setConnectTimeout(CONNECT_TIMEOUT)
				.setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
				.setRedirectsEnabled(false).build();
		return requestConfig;
	}

	public static String getDefaultCharset() {
		return DEFAULT_CHARSET;
	}

	/**
	 * @param url
	 * @param request
	 * @param defaultCharset
	 * @return
	 */
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

	public static HttpRequestBase createHttpRequest(String method, String url) {
		return createHttpRequest(HttpMethod.valueOf(method), url);
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

	public static void copyRequest(HttpServletRequest request,
			HttpRequestBase httpRequest, String defaultCharset) {
		copyRequest(request, httpRequest, defaultCharset, null);
	}

	public static void copyRequest(HttpServletRequest request,
			HttpRequestBase httpRequest, String defaultCharset,
			String[] excludeHeaderNames) {
		addRequestHeaders(request, httpRequest, excludeHeaderNames, false);

		if (httpRequest instanceof HttpEntityEnclosingRequestBase) {
			try {
				HttpEntity entity = new InputStreamEntity(
						request.getInputStream(), request.getContentLength());

				((HttpEntityEnclosingRequestBase) httpRequest)
						.setEntity(entity);
			} catch (Exception e) {
				log("复制Entity失败", e);
			}
		}
	}
	
	public static void copyFullRequest(HttpServletRequest request,
			HttpRequestBase httpRequest, String defaultCharset,
			String[] excludeHeaderNames) {
		addRequestHeaders(request, httpRequest, excludeHeaderNames, true);

		if (httpRequest instanceof HttpEntityEnclosingRequestBase) {
			try {
				HttpEntity entity = new InputStreamEntity(
						request.getInputStream(), request.getContentLength());

				((HttpEntityEnclosingRequestBase) httpRequest)
						.setEntity(entity);
			} catch (Exception e) {
				log("复制Entity失败", e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static void addRequestHeaders(
			HttpServletRequest httpServletRequest, HttpRequestBase httpRequest,
			String[] excludeHeaderNames, boolean isFull) {
		Enumeration<String> enumerationOfHeaderNames = httpServletRequest
				.getHeaderNames();
		while (enumerationOfHeaderNames.hasMoreElements()) {
			String stringHeaderName = enumerationOfHeaderNames.nextElement();

			if (HTTP.TRANSFER_ENCODING.equalsIgnoreCase(stringHeaderName)
					|| HTTP.CONTENT_LEN.equalsIgnoreCase(stringHeaderName)
					|| HTTP.TARGET_HOST.equalsIgnoreCase(stringHeaderName)
					|| (!isFull && SpUtils.HTTP_HEADER_COOKIE
							.equalsIgnoreCase(stringHeaderName))) {
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

	/**
	 * 稳妥但效率低的方式，不同的服务器，请求头的大小写可能有区别
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "unused" })
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
			log("判断multipart失败", t);
			return false;
		}
		return false;
	}

	@SuppressWarnings("unused")
	private static String resolveCharset(HttpServletRequest request,
			String defaultCharset) {
		String charset = request.getCharacterEncoding();
		charset = (charset == null ? defaultCharset : charset);

		if (StringUtils.isEmpty(charset))
			return getDefaultCharset();
		else
			return charset;
	}

	public static void log(String log, Throwable e) {
		log(log + ":" + e.getMessage());
		e.printStackTrace();
	}

	public static void log(String log) {
		System.out.println(log);
	}
	public static void addParameters(HttpRequestBase httpRequest,
			Map<String, String> params) {
		addParameters(httpRequest, params, null);

	}

	public static void addParameters(HttpRequestBase httpRequest,
			Map<String, String> params, String charset) {
		charset = charset == null ? getDefaultCharset() : charset;

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
					log("参数编码错误：" + entry.getValue());
				}
			}

			String paramStr = param.substring(0, param.length());
			if (StringUtils.isEmpty(paramStr))
				return;

			String url = httpRequest.getURI().toString();
			httpRequest.setURI(URI.create(url
					+ (url.indexOf("?") == -1 ? "?" : "&") + paramStr));
		}

	}
}
