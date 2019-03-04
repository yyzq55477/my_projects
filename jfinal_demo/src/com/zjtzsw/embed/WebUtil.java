package com.zjtzsw.embed;

import java.net.URI;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;

/**
 * Web工具集，CPI和宿主系统公用
 * 
 * @author wangs
 * 
 */
public class WebUtil {
	public final static String VERSION = "1.2";
	/**
	 * 设置是否调试状态，正式部署时，应通过配置web.xml更改为false
	 */
	public static boolean DEBUG = true;

	public static final String INIT_PARAMETER_DEBUG = "DEBUG"; // web.xml中的InitParameter的name
	public static final String CPI_VERSION = "1.7";

	public static final String HTTP_HEADER_COOKIE = "Cookie";

	public static final String PARAMETER_CPI_ACCESS_DETAIL = "CPI_ACCESS_DETAIL";
	public static final String PARAMETER_CPI_TARGET_URL = "CPI_TARGET_URL";
	public static final String PARAMETER_CPI_SOURCE_LOCATION = "CPI_SOURCE_LOCATION";

	public static final String HEADER_DOMAIN = "CPI_DOMAIN";
	public static final String HEADER_USERNAME = "CPI_USERNAME";
	public static final String HEADER_PASSWORD = "CPI_PASSWORD";
	public static final String HEADER_TOKEN = "CPI_TOKEN";
	public static final String HEADER_CONTEXT_PATH = "CPI_CONTEXT_PATH";
	public static final String HEADER_NEED_VALIDATE = "CPI_NEED_VALIDATE";
	public static final String HEADER_DOMAIN_TITLE = "CPI_DOMAIN_TITLE";
	public static final String HEADER_SESSION_ID = "CPI_SESSIONID";
	public static final String HEADER_DOMAIN_REGISTER_URL = "CPI_DOMAIN_REGISTER_URL";

	public static final String PREFIX_CPI_URL = "/cpi"; // 宿主的前缀，标识需CPI转发处理的请求

	public static final int MINS = 60 * 1000;

	public static final int MAX_CONN_PER_ROUTE = 150;
	public static final int MAX_CONN_TOTAL = 2000;

	public static final int SOCKET_TIMEOUT = 5 * MINS;
	public static final int CONNECT_TIMEOUT = 10 * MINS;
	public static final int CONNECTION_REQUEST_TIMEOUT = 5 * MINS;

	static {
		System.out
				.println("\n-------------------------------------------------------------------------------------------------------------------");
		System.out.println("CPII 通用表现层智能整合平台");
		System.out.println("©2013-2014 浙江天正思维信息技术有限公司，保留所有权利");
		System.out.println("主程序：v" + CPI_VERSION + "\n组件WebUtil：v"
				+ WebUtil.VERSION + "\n组件HttpClientUtil：v"
				+ HttpClientUtil.VERSION);
		System.out.println("性能参数： " + "MAX_CONN_PER_ROUTE="
				+ MAX_CONN_PER_ROUTE + ",MAX_CONN_TOTAL=" + MAX_CONN_TOTAL
				+ ",SOCKET_TIMEOUT=" + SOCKET_TIMEOUT + ",CONNECT_TIMEOUT="
				+ CONNECT_TIMEOUT + ",CONNECTION_REQUEST_TIMEOUT="
				+ CONNECTION_REQUEST_TIMEOUT);
		System.out
				.println("-------------------------------------------------------------------------------------------------------------------");
	}

	/**
	 * 获取默认的CPI的默认Charset
	 * 
	 * @return 返回utf-8
	 */
	public static String getDefaultCharset() {
		return getDefaultCharset(null);
	}

	/**
	 * 获取宿主的默认编码
	 * 
	 * @return 返回CPI默认编码
	 */
	public static String getHostDefaultCharset() {
		return getDefaultCharset();
	}

	/**
	 * 获取指定域的默认Charset，当响应的ContentType不存在时，使用默认Charset
	 * 
	 * @param domain
	 * @return
	 */
	public static String getDefaultCharset(String domain) {
		try {
			if (domain == null)
				return DEFAULT_CHARSET;
			String charset = defaultCharsetMap.get(domain);
			return charset == null ? DEFAULT_CHARSET : charset;
		} catch (Exception e) {
			return DEFAULT_CHARSET;
		}
	}

	/**
	 * 根据用户标识名、CPIkey和url创建Token，供CPI验证使用
	 * 
	 * @param username
	 * @param cpiKey
	 * @param url
	 * @return
	 */
	public static String createToken(String username, String cpiKey, String url) {
		if (StringUtils.isEmpty(cpiKey)) {
			log("CPI-KEY不存在，生成的Token可能不正确"); // 因为初始注册也会生成Token，所以不强值cpikey存在
		}

		String key = username + cpiKey + url;

		try {
			key = DigestUtils.md5Hex(key);
		} catch (Exception e) {
			WebUtil.log("生成Token失败");
			return null;
		}
		return key;
	}

	/**
	 * 去除上下文部分，主要去除url的路径中的第一级内容
	 * 
	 * @param url
	 * @param context
	 * @return
	 */
	public static String trimContextPath(String url, String context) {
		try {
			url = url.replaceAll("/+", "/");// 修复url 多/错误情况 例//lemis/cpi 导致截取问题
		} catch (Exception e) {
			System.out.println(e);
		}
		if (StringUtils.isNotEmpty(context)) {
			if (!context.startsWith("/"))
				context = "/" + context;
			url = url.replaceFirst("^" + context, "");
		}
		return url;
	}

	/**
	 * url添加上下文，用于给url加上前缀。函数会自动修正url，总是以/开头
	 * 
	 * @param url
	 * @param context
	 * @return
	 */
	public static String addContextPath(String url, String context) {
		if (StringUtils.isEmpty(url) && StringUtils.isEmpty(context))
			return ""; // 防止增加了/

		if (StringUtils.isEmpty(url)) {
			url = "";
		} else {
			if (!url.startsWith("/")) {
				url = "/" + url;
			}
		}
		if (StringUtils.isEmpty(context)) {
			context = "";
		} else {
			if (!context.startsWith("/")) {
				context = "/" + context;
			}
		}

		return context + url;
	}

	/**
	 * 将域名转换成前缀方式
	 * 
	 * @param domainName
	 * @return
	 */
	public static String domain2prefix(String domainName) {
		return ("/" + domainName).toLowerCase();
	}

	public static String resolveDomainName(HttpServletRequest request,
			String contextPath, String prefix) {
		String domain = getDomainNameByForce(request.getQueryString()); // url中指定，优先级最高
		if (domain != null)
			return domain;

		return getDomainNameByUrl(request, contextPath, prefix); // 根据url判断
	}

	private static String getDomainNameByForce(String queryString) {
		// 强制参数指定访问域
		try {
			Matcher m = REFERER_PARAMETER_NAME_PATTERN.matcher(queryString);
			if (m.find())
				return StringUtils.upperCase(m.group(2));
		} catch (Exception e) {
			// 不处理
		}
		return null;
	}

	public static String getDomainNameByUrl(HttpServletRequest request,
			String contextPath, String prefix) {
		String path = request.getRequestURI();

		if (!StringUtils.isEmpty(contextPath))
			path = trimContextPath(path, contextPath);

		if (!StringUtils.isEmpty(prefix) && !path.startsWith(prefix))
			return null;

		return WebUtil.getFirstWordInUrl(trimContextPath(path, prefix));
	}

	/**
	 * 以斜杠开头，返回第二个斜杠之间的所有字母和数字，所有不满足的都是不正确的域名
	 * 
	 * @param questUri
	 *            以斜杠开头的字符串
	 * @return 没有正确获取域名称的都返回null
	 */
	private static String getFirstWordInUrl(String questUri) {
		if (questUri == null)
			return null;
		Matcher m = DOMAIN_URL_PATTERN.matcher(questUri);
		if (m.find()) {
			String domain = m.group(1);
			if (!StringUtils.isEmpty(domain))
				return domain.toUpperCase();
		}
		return null;
	}

	public static String getRequestURIWithQuery(HttpServletRequest request) {
		String targetUrl = request.getRequestURI();
		String query = request.getQueryString();
		String temp = targetUrl;
		if (StringUtils.isNotEmpty(query))
			temp = targetUrl + "?" + query;

		try {
			URI.create("http://127.0.0.1/" + temp);
		} catch (IllegalArgumentException e) {// 如果构造出错，将queryString替换特殊字符
			log("URL含有特殊符号，已编码：" + e.toString());
			query = encodeIllegalCharacter(query);
			temp = targetUrl + "?" + query;
		}

		return temp;
	}

	public static Map<String, String> illegalChar = new HashMap<String, String>();

	static {
		illegalChar.put("|", "%7C");
		illegalChar.put(" ", "%20");
	}

	public static String encodeIllegalCharacter(String query) {
		for (Map.Entry<String, String> entry : illegalChar.entrySet()) {
			query = query.replace(entry.getKey(), entry.getValue());
		}
		return query;
	}

	/**
	 * 是否满足指定的模式集 是否需要验证的url，所有忽略的或者暴露的url不需要验证
	 * 如果定义的字符串以斜杠结束，代表所有以此开头的都符合排除规则，否则全字匹配 匹配不包括服务器地址、上下文部分
	 * 
	 * @param url
	 * @param patterns
	 * @return
	 */
	public static boolean matchUrlPattern(String url, String[] patterns) {
		if (url == null)
			return false;

		for (String pattern : patterns) {
			if (isMatch(url, pattern))
				return true;
		}

		return false;
	}

	private static boolean isMatch(String str, String pattern) {
		if (pattern.endsWith("/")) {
			return str.startsWith(pattern);
		} else {
			return str.equals(pattern);
		}
	}

	public static void log(String log) {
		if (DEBUG) {
			System.out.println(log);
		}
	}

	public static void log(String log, Throwable t) {
		System.out.println(log);
		t.printStackTrace();
	}

	public static void setDomainCharset(String domainName, String encoding) {
		log("域设置编码：" + domainName + "-->" + encoding);

		// TODO 应增加编码有效性检测

		defaultCharsetMap.put(domainName, encoding);
	}

	private static final Pattern DOMAIN_URL_PATTERN = Pattern
			.compile("^/(\\w*)[-/]");

	// 原地址的路径的参数名
	private static final String REFERER_PARAMETER_NAME = "_referer_";

	// 原地址的路径的参数名Pattern
	private static final Pattern REFERER_PARAMETER_NAME_PATTERN = Pattern
			.compile("(^|[?&;])" + REFERER_PARAMETER_NAME + "=([^&;]*)");

	static private final String DEFAULT_CHARSET = "utf-8";

	static private Map<String, String> defaultCharsetMap = new HashMap<String, String>();
	static {
		defaultCharsetMap.put("HOST", DEFAULT_CHARSET);
		defaultCharsetMap.put("CPI", DEFAULT_CHARSET);
	}

	@SuppressWarnings("unchecked")
	public static void printHeaders(HttpServletRequest request) {
		WebUtil.log("请求头列表：");
		Enumeration<?> e = request.getHeaderNames();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			WebUtil.log(key + "="
					+ Collections.list(request.getHeaders(key)).toArray());
		}
	}

	public static void main(String[] argc) {

	}

	public static void printHeaders(HttpResponse httpResponse) {
		Header[] headers = httpResponse.getAllHeaders();
		for (Header header : headers) {
			WebUtil.log(header.getName() + "=" + header.getValue());
		}
	}
}
