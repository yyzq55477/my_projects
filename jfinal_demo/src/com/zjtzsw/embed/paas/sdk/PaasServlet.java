package com.zjtzsw.embed.paas.sdk;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
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

import com.zjtzsw.embed.HttpClientUtil;
import com.zjtzsw.embed.SpUtils;
import com.zjtzsw.embed.SpUtils.HttpMethod;

/**
 * 接口平台
 * 
 * @author yuanzp
 * @data 2017年3月3日 下午3:36:20
 */
public class PaasServlet extends PaasBase {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = -5729081544673261868L;
	private CloseableHttpClient httpClient;

	private static String bname = null;
	private static String appkey = null;
	private static String context = null;
	private static Boolean isEnable = false;

	private static String serverhost = "a little monkey";// url地址
	private static final String SERVER_URL = "/paas"; // /请求分发类地址

	public static void init(String book_name, String app_key, String server_host, String context_path,
			Boolean is_enable) {
		bname = book_name;
		appkey = app_key;
		serverhost = server_host;
		context = context_path;
		isEnable = is_enable;
	}

	/**
	 * @param action
	 *            方法调用的方法名
	 * @param params
	 *            参数集合
	 * @return
	 */

	protected JSONObject relBuild(Enum<?> action, Map<String, String> params) {

		// ...
		if (!isEnable) {
			System.err.println("+++++++++请求平台系统失败，非有效帐套！+++++++");
			return null;
		}
		double g = System.currentTimeMillis();
		params.put("method", action.name());
		JSONObject json = null;
		try {
			String nodeName = "";
			if (DataServiceEnumContains(action.name()))
				nodeName = "/dataService";
			else if (MgrServiceEnumContains(action.name()))
				nodeName = "/mgrService";
			else
				nodeName = "/bizService";

			String result = execRemoteMethod(nodeName, HttpMethod.POST, params);
			json = new JSONObject(result);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		double i = System.currentTimeMillis();
		System.out.println("发请求与影像系统建立关联花费时间：" + (i - g) / 1000 + "秒");
		return json;
	}

	/**
	 * 文件绑定到某个办件材料下面
	 * 
	 * @param action
	 * @param params
	 * @param filePath
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	JSONObject relBuildFile(Enum<?> action, Map<String, String> params, String filePath)
			throws ClientProtocolException, IOException {

		File file = new File(filePath);

		if (!file.exists()) {
			System.err.println("---------------------<<<<<" + filePath + "文件不存在>>>>>---------------");
			return null;
		}
		String fileName = file.getName();
		String postfix = (fileName.substring(fileName.lastIndexOf(".") + 1)).toLowerCase();
		params.put("uuidExt", postfix);
		params.put("filename", fileName);
		params.put("filesize", String.valueOf(file.length()));
		double g = System.currentTimeMillis();
		params.put("method", action.name());

		JSONObject json = null;
		try {
			String result = sendHttpClientUpload(params, file);
			json = new JSONObject(result);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		double i = System.currentTimeMillis();
		System.out.println("发请求与影像系统建立关联花费时间：" + (i - g) / 1000 + "秒");
		return json;
	}

	public List<JSONObject> relBuildFiles(Enum<?> action, Map<String, String> params, String[] filePaths)
			throws ClientProtocolException, IOException {
		List<JSONObject> jsonList = new ArrayList<JSONObject>();
		for (String filePath : filePaths) {
			jsonList.add(relBuildFile(action, params, filePath));
		}
		return jsonList;
	}

	String execRemoteMethod(String nodeName, HttpMethod httpMethod, Map<String, String> params) throws JSONException {
		httpClient = SpUtils.createDefaultHttpClient(SpUtils.createDefaultHttpClientConfig());
		if (bname != null && params.get("bname") == null) {
			params.put("bname", bname);
			params.put("appkey", appkey);
		}
		params.put("_t", String.valueOf(System.currentTimeMillis()));

		String urlString = serverhost + context + SERVER_URL + nodeName;

		HttpRequestBase httpRequest = SpUtils.createHttpRequest(httpMethod, urlString);

		HttpClientUtil.addParameters(httpRequest, params);
		SpUtils.log("...Send-> " + httpRequest.getURI());
		HttpResponse httpResponse = null;
		try {
			// http请求
			/*RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(50000)
					.build();// 设置请求和传输超时时间
					
			*/ RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(1000 * 120)
					.setConnectTimeout(1000 * 500).setConnectionRequestTimeout(1000 * 300).build();// 设置请求和传输超时时间
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

	String sendHttpClientUpload(Map<String, String> params, File file) throws ClientProtocolException, IOException {
		String message = null;
		httpClient = SpUtils.createDefaultHttpClient(SpUtils.createDefaultHttpClientConfig());
		if (bname != null) {
			params.put("bname", bname);
			params.put("appkey", appkey);
		}

		params.put("_t", String.valueOf(System.currentTimeMillis()));

		String urlString = serverhost + context + SERVER_URL + "/dataService";

		urlString = SpUtils.combine(urlString, params);

		PostMethod filePost = new PostMethod(urlString);
		HttpClient client = new HttpClient();

		Part[] parts = { new FilePart(file.getName(), file) };
		filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));

		client.getHttpConnectionManager().getParams().setConnectionTimeout(1000 * 50);
		int status = client.executeMethod(filePost);
		String httpResponse = filePost.getResponseBodyAsString();
		if (status == HttpStatus.SC_OK) {
			message = "上传成功";
		} else {
			message = "上传失败";
		}
		SpUtils.log(message);
		return httpResponse;
	}

	/**
	 * 对象转map
	 * 
	 * @param obj
	 * @return
	 */
	public static Map ConvertObjToMap(Object obj) {
		Map<String, Object> reMap = new HashMap<String, Object>();
		if (obj == null)
			return null;
		Field[] fields = obj.getClass().getDeclaredFields();
		try {
			for (int i = 0; i < fields.length; i++) {
				try {
					Field f = obj.getClass().getDeclaredField(fields[i].getName());
					f.setAccessible(true);
					Object o = f.get(obj);
					reMap.put(fields[i].getName(), o);
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return reMap;
	}

	public String StrJoin(String str0, String str1, String separator) {
		if ("".equals(str0))
			str0 = str1;
		else
			str0 += separator + str1;

		return str0;
	}
}
