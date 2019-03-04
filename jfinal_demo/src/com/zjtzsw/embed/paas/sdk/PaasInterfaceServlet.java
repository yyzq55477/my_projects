package com.zjtzsw.embed.paas.sdk;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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
import org.json.JSONException;
import org.json.JSONObject;

import com.zjtzsw.embed.SpUtils;
import com.zjtzsw.embed.SpUtils.HttpMethod;

public class PaasInterfaceServlet extends HttpServlet {
	private static final long serialVersionUID = 9128158101886223528L;

	private static final String SERVER_URL_NAME = "SERVER_URL";
	private static final String BOOK_NAME = "BOOK_NAME";

	private static final String SERVER_CONTEXT = "SERVER_CONTEXT";// 服务器的上下文路径
	private static final String APP_KEY = "APP_KEY";// 帐套的唯一校验码

	private static String bname = null;
	private static String appkey = null;

	private static Boolean isEnable = true;

	private static final String SP_SERVER = "/service/filext-api"; // /lemis/filext-api

	private static String urlPath = ""; // /lemis

	public String execRemoteMethod(String method, HttpMethod httpMethod, Map<String, String> params,
			final HttpServletRequest request, final HttpServletResponse resp) {
		if (!isEnable) {
			System.err.println("+++++++++请求影像系统失败，非有效帐套！+++++++");
			return "";
		}
		if (params == null) {
			params = new LinkedHashMap<String, String>() {
				private static final long serialVersionUID = 1L;
				{
					if (bname != null)
						this.put("appkey", appkey);
				}
			};
		} else {
			if (bname != null)
				params.put("bname", bname);
			params.put("appkey", appkey);
		}

		params.put("_t", String.valueOf(System.currentTimeMillis()));

		// TODO
		String urlString = SpUtils.combine(urlPath + SP_SERVER + "?method=" + method, params);

		SpUtils.log("...Send-> " + urlString);

		HttpRequestBase httpRequest = SpUtils.createHttpRequest(httpMethod, urlString);
		if (request != null) {
			if (isNotMe())
				SpUtils.copyRequest(request, httpRequest, SpUtils.getDefaultCharset(), null);
			else
				SpUtils.copyFullRequest(request, httpRequest, SpUtils.getDefaultCharset(), null);
		}

		HttpResponse httpResponse = null;
		try {
			// http请求
			CloseableHttpClient httpClient = SpUtils.createDefaultHttpClient(SpUtils.createDefaultHttpClientConfig());
			// HttpClientUtil.createDefaultHttpClientConfig();
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(1000 * 120)
					.setConnectTimeout(1000 * 500).setConnectionRequestTimeout(1000 * 300).build();// 设置请求和传输超时时间
			httpRequest.setConfig(requestConfig);
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

	@Override
	public void init() throws ServletException {
		super.init();
		bname = getInitParameter(BOOK_NAME);
		appkey = getInitParameter(APP_KEY);
		String contextPath = getInitParameter(SERVER_CONTEXT);

		if (StringUtils.isEmpty(bname))
			throw new ServletException("缺少" + BOOK_NAME + "参数，必须定义账套名");

		// 获取配置的SpSeverURL
		String url = getInitParameter(SERVER_URL_NAME);
		urlPath = url + contextPath;
		if (StringUtils.isEmpty(url))
			throw new ServletException("缺少" + SERVER_URL_NAME + "参数，必须定义中心服务器地址");

		// 校验帐套有效性
		try {
			if (isNotMe())
				checkAppKey();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		PaasServlet.init(bname, appkey, url, contextPath, isEnable);
		System.err.println("paas平台接口初始化成功...");
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
		String ReqData = execRemoteMethod("validateAppKey", HttpMethod.GET, params, null, null);
		JSONObject jsonObject = new JSONObject(ReqData);
		String code = String.valueOf(jsonObject.get("code"));
		if (!code.equals("1")) {
			isEnable = false;
		}
		System.err.println("++++++++影像平台初始化反馈意见:" + String.valueOf(jsonObject.get("message")) + "++++++++");
	}

	/**
	 * 判断是否为本系统
	 * 
	 * @return
	 */
	public static boolean isNotMe() {
		return !"J1DZYX".equals(bname);
	}

	@Override
	public void destroy() {
		super.destroy();
	}
}