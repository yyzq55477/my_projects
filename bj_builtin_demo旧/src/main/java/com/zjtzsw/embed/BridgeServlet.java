package com.zjtzsw.embed;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
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

import com.zjtzsw.embed.SpUtils.HttpMethod;
import com.zjtzsw.embed.util.httpcp.factory.PoolManagerFactory;
import com.zjtzsw.embed.util.httpcp.service.HttpClientService;

/**
 * 影像系统后台接口
 * @author yuanzp
 * @data 2016年10月19日 下午9:28:21 
 */
public class BridgeServlet {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = -5729081544673261868L;
	private static CloseableHttpClient httpClient;

	private static String bname = null;
	private static String appkey = null;
	private static String context = null;
	private static String imageproxy = null;

	private static String serverhost = "a little monkey";// url地址
	private static final String SERVER_URL = "/caller/remote"; // /请求分发类地址

	private static final String[] methods = { "addYx31", "editYx31", "deleteYx31", "addYx06", "editYx06", "deleteYx06", "buildPdf", "removeCategory",
			"addYx07", "editYx07", "cancelYx17", "saveFileUnit","updateFileUnit","requiredValidate",
			"fetchImages","putStorageContent","initFileUnit","removeStorageObject", "removePicsByBatch","saveFileUnitForMulti" };// 有效的方法
	
	static void init(String book_name, String app_key, String server_host, String context_path,String image_proxy) {
		bname = book_name;
		appkey = app_key;
		serverhost = server_host;
		context = context_path;
		imageproxy = image_proxy;
	}

	static {
		PoolManagerFactory.start();
		System.err.println("++++++++影像系统后台接口初始化成功！++++++++");
	}

	/**
	 * @param action
	 *            方法调用的方法名
	 * @param params
	 *            参数集合
	 * @return
	 */
	public JSONObject relBuild(String action, Map<String, String> params) {
		if (!checkAction(action)) {
			System.err.println("---------------------<<<<<" + action + "该请求方法无效>>>>>---------------");
			return null;
		}
		//...
		if("fetchImages".equals(action) || "initFileUnit".equals(action)){
			params.put("imageProxy", imageproxy);
		}
		double g = System.currentTimeMillis();
		params.put("method", action);
		// 插入时间戳
		params.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
		double i = System.currentTimeMillis();
		JSONObject json = null;
		try {
			String result = execRemoteMethod("DEFAULT_SERVER", HttpMethod.POST, params);
			json = new JSONObject(result);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		System.out.println("发请求与影像系统建立关联花费时间：" + (i - g) / 1000 + "秒");
		return json;
	}
	
	/**
	 * 文件绑定到某个办件材料下面
	 * @param action
	 * @param params
	 * @param filePath
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public JSONObject relBuildFile(String action, Map<String, String> params,String filePath) throws ClientProtocolException, IOException {
		if (!checkAction(action)) {
			System.err.println("---------------------<<<<<" + action + "该请求方法无效>>>>>---------------");
			return null;
		}
		File file = new File(filePath);
		
		if(!file.exists()){
			System.err.println("---------------------<<<<<" + filePath + "文件不存在>>>>>---------------");
			return null;
		}
		String fileName = file.getName();
		String postfix = (fileName.substring(fileName.lastIndexOf(".") + 1)).toLowerCase();
		params.put("uuidExt",postfix);
		params.put("filename",fileName);
		params.put("filesize",String.valueOf(file.length()));
		double g = System.currentTimeMillis();
		params.put("method", action);
		
		JSONObject json = null;
		try {
			String result = sendHttpClientUpload(params,file);
			json = new JSONObject(result);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// 插入时间戳
		params.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
		double i = System.currentTimeMillis();
		System.out.println("发请求与影像系统建立关联花费时间：" + (i - g) / 1000 + "秒");
		return json;
	}
	
	public List<JSONObject> relBuildFiles(String action, Map<String, String> params, String[] filePaths) throws ClientProtocolException, IOException {
		List<JSONObject> jsonList = new ArrayList<JSONObject>();
		for (String filePath : filePaths) {
			jsonList.add(relBuildFile(action,params,filePath));
		}
		return jsonList;
	}

	boolean checkAction(String action) {
		boolean flag = false;
		for (String method : methods) {
			if (action.equals(method)) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	public String execRemoteMethod(String nodeName, HttpMethod httpMethod, Map<String, String> params)
			throws JSONException {
		httpClient = HttpClientService.getHttpClient();
		if (bname != null&&params.get("bname")==null) {
			params.put("bname", bname);
			params.put("appkey", appkey);
		}
		params.put("_t", String.valueOf(System.currentTimeMillis()));

		String urlString = serverhost + context + SERVER_URL;

		HttpRequestBase httpRequest = SpUtils.createHttpRequest(httpMethod, urlString);
		httpRequest.setHeader("Referer","1");
		HttpClientUtil.addParameters(httpRequest, params);
		SpUtils.log("...Send-> " + httpRequest.getURI());
		HttpResponse httpResponse = null;
		try {
			// http请求
			/*RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(50000)
					.build();// 设置请求和传输超时时间
*/			RequestConfig requestConfig = RequestConfig.custom()
					.setSocketTimeout(1000*120)
					.setConnectTimeout(1000*500)
					.setConnectionRequestTimeout(1000*300)
					.build();// 设置请求和传输超时时间
			httpRequest.setConfig(requestConfig);
			httpResponse = httpClient.execute(httpRequest, HttpClientContext.create());
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
	
	public String sendHttpClientUpload(Map<String, String> params,File file)
			throws ClientProtocolException, IOException {
		String message = null;
		//httpClient = SpUtils.createDefaultHttpClient(SpUtils.createDefaultHttpClientConfig());
		httpClient = HttpClientService.getHttpClient();
		if (bname != null) {
			params.put("bname", bname);
			params.put("appkey", appkey);
		}
		
		params.put("_t", String.valueOf(System.currentTimeMillis()));

		String urlString = serverhost + context + SERVER_URL+"/putStorageContent";
		
		urlString = SpUtils.combine(urlString, params);
		
		PostMethod filePost = new PostMethod(urlString);
		HttpClient client = new HttpClient();
		
		Part[] parts = {new FilePart(file.getName(), file)};
		filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));
		
		client.getHttpConnectionManager().getParams().setConnectionTimeout(1000*50);
		int status = client.executeMethod(filePost);
		String httpResponse = filePost.getResponseBodyAsString();
		if (status == HttpStatus.SC_OK) {
			message = "上传成功";
		 } else {
			message = "上传失败";
		}

		return httpResponse;
	}
}
