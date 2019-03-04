package com.zjtzsw.embed.paas.sdk;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

/**
 * 业务服务类接口
 * 
 * @author yuanzp
 * @data 2017年3月3日 下午5:12:06
 */
public class MgrService extends PaasServlet {

	/**
	 * 单子档案借阅申请
	 * 
	 * @param params
	 * @return
	 */
	public JSONObject borrowApply(Map<String, String> params) {
		return relBuild(MgrServiceEnum.borrowApply, params);
	}

	/**
	 * 档案借阅归还
	 * 
	 * @param params
	 * @return
	 */
	@Deprecated
	public JSONObject borrowReturnApply(Map<String, String> params) {
		return relBuild(MgrServiceEnum.borrowReturnApply, params);
	}

	/**
	 * 档案查阅申请
	 * 
	 * @param params
	 * @return
	 */
	@Deprecated
	public JSONObject consultApply(Map<String, String> params) {
		return relBuild(MgrServiceEnum.consultApply, params);
	}

	/**
	 * 档案转出申请
	 * 
	 * @param params
	 * @return
	 */
	public JSONObject outApply(Map<String, String> params) {
		return relBuild(MgrServiceEnum.outApply, params);
	}

	/**
	 * 档案转入申请
	 * 
	 * @param params
	 * @return
	 */
	public JSONObject inApply(Map<String, String> params) {
		return relBuild(MgrServiceEnum.inApply, params);
	}

	/**
	 * 归档申请
	 * 
	 * @param params
	 * @return
	 */
	@Deprecated
	public JSONObject bulkApply(Map<String, String> params) {
		return relBuild(MgrServiceEnum.bulkApply, params);
	}

	/**
	 * 档案状态变更
	 * 
	 * @param params
	 * @return
	 */
	public JSONObject statusChangeApply(Map<String, String> params) {
		return relBuild(MgrServiceEnum.statusChangeApply, params);
	}

	/**
	 * 申请审核申请
	 * 
	 * @param params
	 * @return
	 */
	public JSONObject checkApply(Map<String, String> params) {
		return relBuild(MgrServiceEnum.checkApply, params);
	}

	/**
	 * 到档通知申请
	 * 
	 * @param params
	 * @return
	 */
	public JSONObject acceptApply(Map<String, String> params) {
		return relBuild(MgrServiceEnum.acceptApply, params);
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
