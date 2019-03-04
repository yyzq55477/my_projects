/**
 * 
 */
package com.zjtzsw.embed.util.httpcp.handler;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

/**
 * * 响应处理类,处理返回结果
 * 
 * @author franocris
 *
 */
public class MyResponseHandler implements ResponseHandler<String> {
	public static Log log=LogFactory.getLog(MyResponseHandler.class); 

	public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
		try {
			// 返回状态码
			int statusCode = response.getStatusLine().getStatusCode();
			switch (statusCode) {
			case 200:
				return EntityUtils.toString(response.getEntity(), "UTF-8");
			case 400:
				log.error("下载400错误代码，请求出现语法错误.");
				break;
			case 403:
				log.error("下载403错误代码，资源不可用.");
				break;
			case 404:
				log.error("下载404错误代码，无法找到指定资源地址.");
				break;
			case 500:
				log.error("下载500错误代码，后台报错.");
				break;
			case 503:
				log.error("下载503错误代码，服务不可用.");
				break;
			case 504:
				log.error("下载504错误代码，网关超时.");
				break;
			default:
				log.error("未处理的错误,code=" + statusCode);
			}

		} finally {
			if (response != null) {
				try {
					((CloseableHttpResponse) response).close();
				} catch (IOException e) {
					log.error("关闭响应错误.", e);
				}
			}
		}
		return "";
	}
}
