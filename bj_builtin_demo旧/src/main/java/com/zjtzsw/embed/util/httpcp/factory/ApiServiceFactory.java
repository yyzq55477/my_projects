package com.zjtzsw.embed.util.httpcp.factory;

import java.io.File;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.apache.http.client.methods.HttpRequestBase;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.zjtzsw.embed.ProxyServlet;
import com.zjtzsw.embed.SpUtils.HttpMethod;
import com.zjtzsw.embed.util.httpclient.HttpClientPoolUtil;
import com.zjtzsw.embed.util.httpcp.body.HttpResultEntity;
import com.zjtzsw.embed.util.httpcp.config.HttpClientProperties;

/**
 * 查询调用API
 * 
 * @author franocris
 *
 */
public class ApiServiceFactory {

//	final private static Prop prop = PropKit.use("api.properties");

	private String methodRoute = "";

	private Map<String,String> params;
	
	private HttpRequestBase httpRequest;
	
	private  Map<String, Cookie> cookieMap;

	public ApiServiceFactory(String methodRoute, Map<String,String> params, Map<String, Cookie> cookieMap) {
		this.methodRoute = methodRoute;
		this.params = params;
		this.cookieMap = cookieMap;
	}
	
	/**
	 * 
	 */
	public ApiServiceFactory(HttpRequestBase httpRequest, Map<String,String> params, Map<String, Cookie> cookieMap) {
		this.httpRequest = httpRequest;
		this.params = params;
		this.cookieMap = cookieMap;
	}
	
	/**
	 * proxyServlet(GET请求)专用调用，无需对路径再次拼接 
	 */
	public  HttpResultEntity doHttpRequestSimple() {
		HttpResultEntity httpResult = null;
		long start = System.currentTimeMillis();
		httpResult = HttpClientPoolUtil.httpClientRequestGet(httpRequest, getHttpHeadCookie()); 
		System.out.println("本次请求耗时："+(System.currentTimeMillis()-start)+"ms");
		return httpResult;
	}
	
	public  HttpResultEntity doHttpRequest() {
		HttpResultEntity httpResult = null;
		long start = System.currentTimeMillis();
		httpResult = HttpClientPoolUtil.httpClientRequest(getUrl(methodRoute), params, getHttpHeadCookie());
		System.out.println("本次请求耗时："+(System.currentTimeMillis()-start)+"ms");
		return httpResult;
	}
	
	public  HttpResultEntity doHttpRequestWithFile(Map<String, File> files) {
		HttpResultEntity httpResult = null;
		long start = System.currentTimeMillis();
		httpResult = HttpClientPoolUtil.httpRequestWithFile(getUrl(methodRoute), params, getHttpHeadCookie(),files);
		System.out.println("本次请求耗时："+(System.currentTimeMillis()-start)+"ms");
		return httpResult;
	}

	/**
	 * @Description: 获得请求头中应该带的Cookie信息
	 * @MethodName: getHttpHeadCookie
	 * @param cookie
	 * @return: String
	 * @Author: franocris
	 * @Date: 2018年7月24日
	 */
	private String getHttpHeadCookie() {
		String headCookie = "";
		String name = HttpClientProperties.LOCALSESSION;
		if(cookieMap != null) {
			if (cookieMap.containsKey(name)) {
				Cookie cookie = (Cookie) cookieMap.get(name);
				headCookie = HttpClientProperties.APISESSION+"="+cookie.getValue();
			} 
		}
		return headCookie;
	}

	/**
	 * <p>方法：getUrl</p>
	 * <p>描述：获取完整API的方法URL</p>
	 * @param methodRoute
	 * @return
	 * @author franocris
	 * 2018年7月12日
	 */
	private String getUrl(String methodRoute) {
		StringBuffer url = new StringBuffer();
		url.append(ProxyServlet.apiroute);
		url.append(methodRoute);
		return url.toString();
	}
}
