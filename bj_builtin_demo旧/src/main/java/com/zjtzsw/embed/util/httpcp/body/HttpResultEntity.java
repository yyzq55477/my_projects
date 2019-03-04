/**
 * 
 */
package com.zjtzsw.embed.util.httpcp.body;

/**
 * HttpClient结果实体
 * 
 * @author franocris
 *
 */
public class HttpResultEntity {
	// 响应的状态码
	private int code;

	// 响应的响应体
	private String body;

	/**
	 * @param code
	 * @param body
	 */
	public HttpResultEntity(int code, String body) {
		super();
		this.code = code;
		this.body = body;
	}

	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(int code) {
		this.code = code;
	}

	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * @param body the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}
	
	
}
