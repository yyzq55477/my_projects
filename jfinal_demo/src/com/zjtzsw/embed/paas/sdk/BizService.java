package com.zjtzsw.embed.paas.sdk;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

/**
 * 业务服务类接口(Biz)
 * 
 * @author yuanzp
 * @data 2017年3月6日 上午9:46:13
 */
public class BizService extends PaasServlet {

	/**
	 * 流程状态轮询
	 * 
	 * @param params
	 * @return
	 */
	public JSONObject polling(Map<String, String> params) {
		return relBuild(BizServiceEnum.polling, params);
	}
	
	/**
	 * 待办列表
	 * @param params
	 * @return
	 */
	@Deprecated
	public JSONObject queryBacklog(Map<String, String> params) {
		return relBuild(BizServiceEnum.queryBacklog, params);
	}
	
	/**
	 * 办事记录列表
	 * @param params
	 * @return
	 */
	@Deprecated
	public JSONObject queryWorkTrail(Map<String, String> params) {
		return relBuild(BizServiceEnum.queryWorkTrail, params);
	}

	/**
	 * 测试请求
	 * 
	 * @return
	 */
	public JSONObject tryIt() {
		return relBuild(BizServiceEnum.tryIt, new HashMap<String, String>());
	}
}
