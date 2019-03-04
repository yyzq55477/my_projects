//package com.zjtzsw.embed.util.httpclient;
//import java.io.File;
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.Set;
//
//import org.apache.http.HttpEntity;
//import org.apache.http.NameValuePair;
//import org.apache.http.ParseException;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.client.methods.HttpRequestBase;
//import org.apache.http.client.protocol.HttpClientContext;
//import org.apache.http.entity.ContentType;
//import org.apache.http.entity.mime.HttpMultipartMode;
//import org.apache.http.entity.mime.MultipartEntityBuilder;
//import org.apache.http.entity.mime.content.FileBody;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.util.EntityUtils;
//
//import com.zjtzsw.embed.util.httpcp.body.HttpResultEntity;
//import com.zjtzsw.embed.util.httpcp.service.HttpClientService;
//
///**
// * HttpClient连接池工具类
// *
// * @author franocris
// *
// */
//public class HttpClientPoolUtil {
//	
//	/** 
//	 * @Description: HttpClient连接池请求处理工厂(通用请求处理方法)
//	 * @MethodName: httpClientRequest
//	 * @Param: [httpMethod,url, params, files, cookie] 
//	 * @return: com.tzsw.bj.apps.httpservice.util.HttpResultEntity 
//	 * @Author: Franocris
//	 * @Date: 2018/7/24 
//	 */ 
//	public static HttpResultEntity httpClientRequest(HttpRequestBase httpRequest, String headCookie){
////			config(httpPost,headCookie);
//		return doHttpReq(httpRequest);
//	}
//	
//	/** 
//	 * @Description: HttpClient连接池请求处理工厂
//	 * @MethodName: httpClientRequest
//	 * @Param: [httpMethod,url, params, files, cookie] 
//	 * @return: com.tzsw.bj.apps.httpservice.util.HttpResultEntity 
//	 * @Author: Franocris
//	 * @Date: 2018/7/24 
//	 */ 
//	public static HttpResultEntity httpClientRequestPost(HttpPost httpRequest,Map<String, String> params, String headCookie){
//		setPostParams(httpRequest, params);
////			config(httpPost,headCookie);
//		return doHttpReq(httpRequest);
//	}
//
//	/** 
//	* @Description: HttpClient连接池请求处理工厂
//	* @MethodName: httpClientRequest
//	* @Param: [url, params, files, cookie] 
//	* @return: com.tzsw.bj.apps.httpservice.util.HttpResultEntity 
//	* @Author: Franocris
//	* @Date: 2018/7/24 
//	*/ 
//	public static HttpResultEntity httpClientRequest(String url, Map<String, String> params, String headCookie){
//		if(params == null){
//			HttpGet httpGet = new HttpGet(url);
//			config(httpGet,headCookie);
//			return doHttpReq(httpGet);
//		}else {
//			HttpPost httpPost = new HttpPost(url);
//			setPostParams(httpPost, params);
//			config(httpPost,headCookie);
//			return doHttpReq(httpPost);
//		}
//	}
//	
//	public static HttpResultEntity httpRequestWithFile(String url, Map<String, String> params, String headCookie,Map<String, File> files) {
//		HttpPost httpPost = new HttpPost(url);
//		setParamsWithFile(httpPost, params,files);
//		config(httpPost,headCookie);
//		return doHttpReq(httpPost);
//	}
//	
//	/** 
//	* @Description: 执行httpRequest请求并获得请求结果 
//	* @MethodName: doHttpReq
//	* @Param: [httpReq] 
//	* @return: com.tzsw.bj.apps.httpservice.util.HttpResultEntity 
//	* @Author: Franocris
//	* @Date: 2018/7/24 
//	*/ 
//	private static HttpResultEntity doHttpReq(HttpRequestBase httpReq) {
//		HttpResultEntity httpResult = null;
//		CloseableHttpResponse response = null;
//		try {
//			CloseableHttpClient httpClient = HttpClientService.getHttpClient();
//			response = httpClient.execute(httpReq,HttpClientContext.create());
//			httpResult = getHttpResult(response);
//		} catch (ClientProtocolException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally{
//			try {
//				if (response != null)
//					response.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		return httpResult;
//	}
//
//	/**
//	 * <p>方法：getHttpResult</p>
//	 * <p>描述：解析请求结果</p>
//	 * @param response
//	 * @return
//	 * @author franocris
//	 * 2018年7月17日
//	 */
//	private static HttpResultEntity getHttpResult(CloseableHttpResponse response) {
//		HttpResultEntity httpResult = null;
//		try {
//			if (response.getEntity() != null) {
//				httpResult = new HttpResultEntity(response.getStatusLine().getStatusCode(),
//						EntityUtils.toString(response.getEntity(), "UTF-8"));
//			} else {
//				httpResult = new HttpResultEntity(response.getStatusLine().getStatusCode(), "");
//			}
//
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return httpResult;
//
//	}
//
//	/**
//	 * <p>方法：config</p>
//	 * <p>描述：设置请求的相关配置信息</p>
//	 * @param httpRequestBase
//	 * @param headCookie
//	 * @author franocris
//	 * 2018年7月17日
//	 */
//	private static void config(HttpRequestBase httpRequestBase,String headCookie) {
//		// 设置Header等
//		httpRequestBase.setHeader("User-Agent", "Mozilla/5.0");
//		httpRequestBase.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//		httpRequestBase.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");// "en-US,en;q=0.5");
//		httpRequestBase.setHeader("Accept-Charset","ISO-8859-1,utf-8,gbk,gb2312;q=0.7,*;q=0.7");
//		if(headCookie!=null) {
//			httpRequestBase.setHeader("Cookie",headCookie);
//		}
//		// 配置请求的超时设置
//	}
//
//
//	/**
//	 * <p>方法：setPostParams</p>
//	 * <p>描述：设置提交的参数</p>
//	 * @param httpost
//	 * @param params
//	 * @author franocris
//	 * 2018年7月12日
//	 */
//	private static void setPostParams(HttpPost httpost, Map<String, String> params) {
//		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
//		Set<String> keySet = params.keySet();
//		for (String key : keySet) {
//			nvps.add(new BasicNameValuePair(key, params.get(key).toString()));
//		}
//		try {
//			httpost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
//			System.out.println("Entity::::"+httpost.getEntity().toString());
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//
//	}
//	
//	private static void setParamsWithFile(HttpPost httpost, Map<String, String> params,Map<String, File> files) {
//		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
//		Set<String> keySet = params.keySet();
//		for (String key : keySet) {
//			nvps.add(new BasicNameValuePair(key, params.get(key).toString()));
//		}
//		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//	    builder.setMode(HttpMultipartMode.RFC6532); //如果有SocketTimeoutException等情况，可修改这个枚举BROWSER_COMPATIBLE
//	    //builder.setCharset(Charset.forName("UTF-8")); //不要用这个，会导致服务端接收不到参数
//	    if (params != null && params.size() > 0) {
//	        for (NameValuePair p : nvps) {
//	            builder.addTextBody(p.getName(), p.getValue(), ContentType.TEXT_PLAIN.withCharset("UTF-8"));
//	        }
//	    }
//	    if (files != null && files.size() > 0) {
//	        Set<Entry<String, File>> entries = files.entrySet();
//	        for (Entry<String, File> entry : entries) {
//	            builder.addPart(entry.getKey(), new FileBody(entry.getValue()));
//	        }
//	    }
//	    HttpEntity reqEntity = builder.build();
//	    httpost.setEntity(reqEntity);
//	}
//
//	@SuppressWarnings("unused")
//	private static void setPostParamsWithFile(HttpPost httpost, Map<String, Object> params, List<File> files) {
//		MultipartEntityBuilder builder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
//		if(files != null){
//			for(File file : files){
//				FileBody fileBody = new FileBody(file);
//				builder.addPart("uploadFile",fileBody);
//			}
//		}
//		if(params != null){
//			Set<String> keySet = params.keySet();
//			for (String key : keySet) {
//				builder.addTextBody(key,params.get(key).toString());
//			}
//		}
//		httpost.setEntity(builder.build());
//	}
//
//}