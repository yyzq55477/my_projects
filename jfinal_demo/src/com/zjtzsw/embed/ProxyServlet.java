package com.zjtzsw.embed;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zjtzsw.embed.SpUtils.HttpMethod;

/**
 * 简单的一个代理功能，将此处发送的所有请求转发至指定的服务器，保证所有请求头、请求方法和请求内容不变。 请求规范如下：
 * filext-api?node=xxx&method=xxx&param1=&param2=...
 * 其中node为转发的服务器名称，servlet需要根据服务器地址映射表查询。
 * 如果没有传递node，表示发送到中心服务器。服务器地址映射表在init的时候从中心服务器获取
 * （registerBiz），中心服务器地址根据web.xml配置 调用registerBiz的时候需传递一个本地生成的token
 * 
 * 
 * @author wangs
 *
 */
public class ProxyServlet extends HttpServlet {
	private static final long serialVersionUID = 9128158101886223528L;

	private static final String SERVER_URL_NAME = "SERVER_URL";
	private static final String BOOK_NAME = "BOOK_NAME";
	private static final String IS_FILEXT_SERVER_NAME = "IS_FILEXT_SERVER";
	private static final String DEFAULT_SERVER = "DEFAULT_SERVER";
	private static final String IMAGE_PROXY_NAME = "IMAGE_PROXY_NAME";
	private static final String DEFAULT_NODE_SERVER = "default";

	private static final String SERVER_CONTEXT = "SERVER_CONTEXT";// 服务器的上下文路径
	private static final String APP_KEY = "APP_KEY";// 帐套的唯一校验码
	private static final String IMAGE_ACCESS_AUTHORIZE = "imageAccessAuthorize";

	private static Map<String, String> HOST_MAP = new ConcurrentHashMap<String, String>();

	private static String bname = null;
	private static String appkey = null;
	private static String imageProxy = null;
	private static Boolean isEnable = true;
	@SuppressWarnings("unused")
	private boolean isFilextServer = false;
	//private String token;

	private static final String SP_NODE_SERVER = "/service/filext-api"; // /SpNodeServer
	private static final String SP_SERVER = "/service/filext-api"; // /lemis/filext-api

	private static String CONTEXT_PATH = ""; // /lemis

	@SuppressWarnings("deprecation")//消除对弃用方法的警告提示
	void proxy(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map<String, String> params = SpUtils.getParamsMap(req);
		String date = new Date().toLocaleString();
		System.out.println("请求发生时间:" + date);
		// 外网会重复发请求两次，这个bug太奇怪，一直是从QQBrowser发送的请求，找不到根本原因，先这类请求，忽略
		String MQQBrowser = "MQQBrowser";
		String userAgent = req.getHeader("user-agent");
		System.out.println("请求发生浏览器:" + userAgent);

		if (StringUtils.contains(userAgent, MQQBrowser)) {
			System.out.println("来自QQBrowser浏览浏览器的重复请求，请求发生时间:" + date);
			outDetailRequest(req, params);
			return;
		}
		String node = "";// params.get("node"); 暂时先没有节点的概念。。。 by yuanzp
		String method = params.get("method");

		{
			params.remove("node");
			params.remove("method");
		}

		params.put("hostIP", req.getServerName()+":"+req.getServerPort());
		String result;
		if (IMAGE_ACCESS_AUTHORIZE.equals(method)) {
			result = "{'appkey':'" + appkey + "'}";
		} else {
			result = execRemoteMethod(node, method, HttpMethod.valueOf(req.getMethod().toUpperCase()), params, req, resp);
		}

		resp.setContentType("application/json");
		resp.setCharacterEncoding(SpUtils.getDefaultCharset());
		resp.getWriter().write(result==null?"":result);
	}

	private void outDetailRequest(HttpServletRequest req, Map<String, String> params) {
		System.out.println("#####################################################");
		System.out.printf("在%s的proxy()里：", this.getClass()).append("\n").printf("接受请求【%s】", req.getRequestURL())
				.append("\n").printf("详细请求【%s】", req).append("\n").printf("发送ip【%s】", req.getRemoteAddr()).append("\n")
				.printf("接收ip【%s】", req.getLocalAddr()).append("\n").printf("参数【%s】", params).println();

		System.out.println("request的头文件：");
		@SuppressWarnings("rawtypes")
		Enumeration ns = req.getHeaderNames();
		while (ns.hasMoreElements()) {
			Object name = ns.nextElement();
			if (name instanceof String)
				System.out.println(name + "【" + req.getHeader((String) name) + "】");
			else
				System.out.println(name);
		}
		System.out.println("#####################################################\n拦截该请求..");
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		proxy(req, resp);
	}

	private CloseableHttpClient httpClient;

	public Map<String, String> registerSp() {
		Map<String, String> map = new LinkedHashMap<String, String>();

		map.put("bname", bname);
		JSONObject result = getResultJson(execRemoteMethod(DEFAULT_SERVER, "registerBiz", HttpMethod.GET, map));

		// 返回结果，生成node映射
		SpUtils.log("获取节点服务器地址成功");
		map.clear(); // map用作结果，清除作为参数时候的值
		try {
			JSONObject json = result.getJSONObject("data");
			//token = json.getString("token"); // 获取服务器返回Token
			JSONArray nodes = json.getJSONArray("nodes");
			if (nodes == null)
				return map;
			for (int i = 0; i < nodes.length(); i++) {
				JSONObject node = (JSONObject) nodes.get(i);
				map.put(node.getString("uuid"), node.getString("host"));

				SpUtils.log((i + 1) + ":" + node.getString("uuid") + " --> " + node.getString("host")); // 打印节点

				if (node.getBoolean("isDefault")) {
					HOST_MAP.put(DEFAULT_NODE_SERVER, node.getString("host"));
					SpUtils.log("设置默认节点服务器地址：" + node.getString("host"));
				}
			}

			return map;
		} catch (Exception e) {
			e.printStackTrace();
			return map;
		}
	}

	JSONObject getResultJson(String s) {
		try {
			return new JSONObject(s);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	String getServerEndpoint() {
		return getNodeEndpoint(null);
	}

	/**
	 * 如果不传递nodeName，使用中心服务器，如果传递到默认节点服务器，使用DEFAULT_NODE_SERVER的值，默认为default
	 * 
	 * @param nodeName
	 * @return
	 */
	String getNodeEndpoint(String nodeName) {
		if (StringUtils.isEmpty(nodeName))
			nodeName = DEFAULT_SERVER;

		if (HOST_MAP.size() == 1 && !DEFAULT_SERVER.equalsIgnoreCase(nodeName)) { // 表示未初始化，否则至少会有两个元素，分别为默认中心服务器和默认节点服务器
			// 向中心服务器注册，并获取Node地址映射
			Map<String, String> map = registerSp(); // "registerBiz"
			if (map != null)
				HOST_MAP.putAll(map);

			// 如果不存在默认的节点服务器，增加一个默认地址，和中心服务器相同地址
			if (HOST_MAP.get(DEFAULT_NODE_SERVER) == null)
				HOST_MAP.put(DEFAULT_NODE_SERVER, HOST_MAP.get(DEFAULT_SERVER));
		}

		String url = HOST_MAP.get(nodeName);

		if (url == null) {
			SpUtils.log("服务器地址未定义:" + nodeName);
			return null;
		}

		if (DEFAULT_SERVER.equals(nodeName))
			return url + CONTEXT_PATH + SP_SERVER;
		else
			return url + CONTEXT_PATH + SP_NODE_SERVER;
	}

	public String execRemoteMethod(String nodeName, String method, final HttpServletRequest req,
			final HttpServletResponse resp) {
		return execRemoteMethod(nodeName, method, HttpMethod.GET, null, req, resp);
	}

	public String execRemoteMethod(String nodeName, String method, Map<String, String> params,
			final HttpServletRequest req, final HttpServletResponse resp) {
		return execRemoteMethod(nodeName, method, HttpMethod.GET, params, req, resp);
	}

	public String execRemoteMethod(String nodeName, String method, HttpMethod httpMethod, final HttpServletRequest req,
			final HttpServletResponse resp) {
		return execRemoteMethod(nodeName, method, httpMethod, null, req, resp);
	}

	public String execRemoteMethod(String nodeName, String method, HttpMethod httpMethod, Map<String, String> params) {
		return execRemoteMethod(nodeName, method, httpMethod, params, null, null);
	}

	public String execRemoteMethod(String nodeName, String method, HttpMethod httpMethod, Map<String, String> params,
			final HttpServletRequest request, final HttpServletResponse resp) {
		if (!isEnable) {
			System.err.println("+++++++++请求影像系统失败，非有效帐套！+++++++");
			return "";
		}
		if (params == null) {
			params = new LinkedHashMap<String, String>() {
				private static final long serialVersionUID = 1L;
				{
					//this.put("token", token);
					if (bname != null)
						this.put("appkey", appkey);
				}
			};
		} else {
			//params.put("token", token);
			if (bname != null)
				params.put("bname", bname);
			params.put("appkey", appkey);
		}
		
		boolean isScanOnWeb = request==null ? false : "scanOnWeb".equals(request.getParameter("widget"));
		if (StringUtils.isNotBlank(imageProxy))
			params.put("imageProxy", imageProxy);
		if (isScanOnWeb)
			params.remove("scanImage");
		params.put("_t", String.valueOf(System.currentTimeMillis()));

		String urlString = SpUtils.combine(getNodeEndpoint(nodeName) + "?method=" + method, params);

		SpUtils.log("...Send-> " + urlString);

		HttpRequestBase httpRequest = SpUtils.createHttpRequest(httpMethod, urlString);
		if (request != null) {
			if (isNotMe()) SpUtils.copyRequest(request, httpRequest, SpUtils.getDefaultCharset(), null);
			else SpUtils.copyFullRequest(request, httpRequest, SpUtils.getDefaultCharset(), null);
			
			if (isScanOnWeb) SpUtils.copyRequest4Scan(request, httpRequest, SpUtils.getDefaultCharset(), null);
		}

		HttpResponse httpResponse = null;
		try {
			// http请求
//			CloseableHttpClient httpClient = SpUtils.createDefaultHttpClient(SpUtils.createDefaultHttpClientConfig());
			CloseableHttpClient httpClient = HttpClientUtil.createDefaultSslHttpClient(HttpClientUtil.createDefaultHttpClientConfig());
			RequestConfig requestConfig = RequestConfig.custom()
					.setSocketTimeout(1000*120)
					.setConnectTimeout(1000*500)
					.setConnectionRequestTimeout(1000*300)
					.build();// 设置请求和传输超时时间
			httpRequest.setConfig(requestConfig);
			System.out.println("+++++++++++++"+httpClient+"&&&&&"+Thread.currentThread().getId());
			long start = System.currentTimeMillis();
			httpResponse = httpClient.execute(httpRequest, HttpClientContext.create());
			long usedTime = (System.currentTimeMillis() - start) / 1000;
			System.out.println("Generate complete in " + usedTime + " seconds.");  
			System.out.println("-------------");
			return EntityUtils.toString(httpResponse.getEntity(), SpUtils.getDefaultCharset());
		} catch (ClientProtocolException e) {
			SpUtils.log("ClientProtocolException", e);
		} catch (IOException e) {
			SpUtils.log("IOException", e);
		} finally {
			HttpClientUtils.closeQuietly(httpResponse);
		}
		return null;
	}

	private void createDefaultHttpClient() {
//		this.httpClient = SpUtils.createDefaultHttpClient(SpUtils.createDefaultHttpClientConfig());
		this.httpClient = HttpClientUtil.createDefaultSslHttpClient(HttpClientUtil.createDefaultHttpClientConfig());
	}
	
	protected static String getServerUrl() {
		return HOST_MAP.get(DEFAULT_SERVER) + CONTEXT_PATH + SP_SERVER;
	}

	@Override
	public void init() throws ServletException {
		super.init();
		bname = getInitParameter(BOOK_NAME);
		appkey = getInitParameter(APP_KEY);
		imageProxy = getInitParameter(IMAGE_PROXY_NAME);
		CONTEXT_PATH = getInitParameter(SERVER_CONTEXT);

		if (StringUtils.isEmpty(bname))
			throw new ServletException("缺少" + BOOK_NAME + "参数，必须定义账套名");

		// 如果是嵌入到filext服务器中，值为true
		if (StringUtils.isNotEmpty(getInitParameter(IS_FILEXT_SERVER_NAME)))
			isFilextServer = true;

		// 获取配置的SpSeverURL
		String url = getInitParameter(SERVER_URL_NAME);
		if (StringUtils.isEmpty(url))
			throw new ServletException("缺少" + SERVER_URL_NAME + "参数，必须定义中心服务器地址");
		
		BridgeServlet.init(bname, appkey, url, CONTEXT_PATH, imageProxy);
		// 把中心服务器地址初始放入
		HOST_MAP.put(DEFAULT_SERVER, url);

		// 创建HttpClient
		createDefaultHttpClient();
		// 校验帐套有效性
		try {
			if (isNotMe()) checkAppKey();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 校验appkey
	 * 
	 * @throws JSONException
	 * 
	 */
	void checkAppKey() throws JSONException {
		// TODO
		Map<String, String> params = new HashMap<String, String>();
		String ReqData = execRemoteMethod(DEFAULT_SERVER, "validateAppKey", HttpMethod.GET, params);
		JSONObject jsonObject = new JSONObject(ReqData);
		String code = String.valueOf(jsonObject.get("code"));
		if (!code.equals("1")) {
			isEnable = false;
		}
		System.err.println("++++++++影像初始化反馈意见:" + String.valueOf(jsonObject.get("message"))+"++++++++");
	}
	
	/**
	 * 判断是否为本系统
	 * @return
	 */
	public static boolean isNotMe() {
		return !"J1DZYX".equals(bname);
	}

	@Override
	public void destroy() {
		super.destroy();

		try {
			httpClient.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}