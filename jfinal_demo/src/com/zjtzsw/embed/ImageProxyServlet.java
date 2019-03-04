package com.zjtzsw.embed;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 图片请求中转
 * @author Lintel
 * @date 2016年11月28日 下午7:23:03
 *
 */
public class ImageProxyServlet extends HttpServlet {
	private static final long serialVersionUID = 1812458074026197358L;
	
	private static final String CONTENT_DISPOSITION = "Content-disposition";
	private static final String DOWNLOAD_METHOD_NAME = "/d/";
	private static final String DEFAULT_ENCODING = "UTF-8";
	private static final String URL = "url";
	
	private static final int TIME_OUT = 10 * 1000;
	private static String imageUrl;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) {
		String url = request.getParameter(URL);
		if (url!=null && !"".equals(url)) {
			long startTime = System.currentTimeMillis();
			loadImage(url, request, response);
			System.out.println("代理下载图片 耗时=======>" + (System.currentTimeMillis()-startTime) + "ms");
		}
	}

	/**
	 * 请求图片
	 * @param urlStr
	 * @param request
	 * @param response
	 */
	private void loadImage(String urlStr, HttpServletRequest request, HttpServletResponse response) {
		if (imageUrl == null) imageUrl = ProxyServlet.getServerUrl() + DOWNLOAD_METHOD_NAME;
				
		try {
			URL url = new URL(imageUrl + combineUrl(urlStr, request));
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(TIME_OUT);
			
			InputStream input = conn.getInputStream();
			byte data[] = readInputStream(input);
			input.read(data);
			
			response.setContentType(conn.getContentType());
			response.setHeader(CONTENT_DISPOSITION, conn.getHeaderField(CONTENT_DISPOSITION));
			OutputStream output = response.getOutputStream();
			output.write(data);
			
			input.close();
			output.flush();
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	private String combineUrl(String urlStr, HttpServletRequest request) {
		StringBuffer url = new StringBuffer(urlStr);
		Map<String, String[]> map = request.getParameterMap();
		boolean first = true;
		
		Set<String> keys = map.keySet();
		for (String key : keys) {
			if (!URL.equals(key)) {
				url.append(first ? "?" : "&");
				if (first) first = false;
				
				String value = map.get(key)[0];
				if ("f".equals(key)) value = encode(value);
				url.append(key).append("=").append(value);
			}
		}
		
		return url.toString();
	}

	/**
	 * 读取输入流以字节数组返回
	 * @param input
	 * @return
	 * @throws Exception
	 */
	private static byte[] readInputStream(InputStream input) throws Exception {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte[] buffer = new byte[2048];
		int len = 0;
		while ((len=input.read(buffer)) != -1) {
			output.write(buffer, 0, len);
		}
		output.close();
		
		return output.toByteArray();
	}
	
	/**
	 * URL编码
	 * @param encodedStr
	 * @return
	 */
	public static String encode(String encodedStr) {
		if (encodedStr == null) return null;
		try {
			return URLEncoder.encode(encodedStr, DEFAULT_ENCODING);
		} catch (UnsupportedEncodingException e) {
			return encodedStr;
		}
	}
}