package com.zjtzsw.embed.paas.sdk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import com.zjtzsw.embed.paas.sdk.PaasEntity.BizItem;
import com.zjtzsw.embed.paas.sdk.PaasEntity.Organization;
import com.zjtzsw.embed.paas.sdk.PaasEntity.Person;

/**
 * 数据服务类接口
 * 
 * @author yuanzp
 * @data 2017年3月3日 下午5:12:06
 */
public class DataService extends PaasServlet {

	/**
	 * 档案件提取
	 * 
	 * @param params
	 * @return
	 */
	public JSONObject fetchFileUnit(Map<String, String> params) {
		return relBuild(DataServiceEnum.fetchFileUnit, params);
	}

	/**
	 * 档案件报传
	 * 
	 * @param params
	 * @return
	 * @throws JSONException
	 */
	public JSONObject putFileUnitBatch(Map<String, String> params, String filepath) throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("success", true);
		jsonObject.put("code", 0);
		
		params.put("uuidExt", "zip");
		try {
			File all = new File(filepath);
			if (!all.isDirectory()) {
			}
			File[] files = all.listFiles();
			String ids = "";
			String errorIndexs = "";
			for (File file : files) {
				JSONObject ResultData = relBuildFile(DataServiceEnum.putFileUnit, params, file.getPath());
				String id = new JSONObject(ResultData.getString("data")).getString("id");
				if (ResultData.getInt("code") != 0) {// 失败
					errorIndexs = StrJoin(errorIndexs, id, ";");
				} else {// 成功
					ids = StrJoin(ids, id, ";");
				}
			}
			jsonObject.put("ids", ids);
			jsonObject.put("errorIndexs", errorIndexs);
			String message = "".equals(errorIndexs)?"全部成功！":"部分成功！";
			jsonObject.put("message", message);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return jsonObject;
	}

	/**
	 * 档案件报传
	 * 
	 * @param params
	 * @return
	 * @throws IOException
	 */
	public JSONObject putFileUnit(Map<String, String> params, String filepath) throws IOException {
		params.put("uuidExt", "zip");
//		File file = new File("");
//		OutputStream os = null;
//		try {
//			os = new FileOutputStream(file);
//			int bytesRead = 0;
//			byte[] buffer = new byte[8192];
//			while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
//				os.write(buffer, 0, bytesRead);
//			}
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} finally {
//			os.close();
//			ins.close();
//		}
		return relBuildFile(DataServiceEnum.putFileUnit, params, filepath);
	}

	/**
	 * 人员信息查询
	 * 
	 * @param params
	 * @return
	 */
	@Deprecated
	public JSONObject queryPerson(Person person) {
		return relBuild(DataServiceEnum.queryPerson, ConvertObjToMap(person));
	}

	/**
	 * 单位信息查询
	 * 
	 * @param params
	 * @return
	 */
	@Deprecated
	public JSONObject queryOrganization(Organization organization) {
		return relBuild(DataServiceEnum.queryOrganization, ConvertObjToMap(organization));
	}

	/**
	 * 办件信息查询
	 * 
	 * @param params
	 * @return
	 */
	public JSONObject queryBizItem(BizItem bizItem) {
		return relBuild(DataServiceEnum.queryBizItem, ConvertObjToMap(bizItem));
	}

	/**
	 * 办件信息变更
	 * 
	 * @param params
	 * @return
	 */
	public JSONObject updateBizItem(String id, BizItem bizItem) {
		Map params = ConvertObjToMap(bizItem);
		params.put("id", id);
		return relBuild(DataServiceEnum.updateBizItem, params);
	}

	/**
	 * 档案件/卷检索
	 * 
	 * @param params
	 * @return
	 */
	public JSONObject searchFileUnit(Map<String, String> params) {
		return relBuild(DataServiceEnum.searchFileUnit, params);
	}

	/**
	 * 档案件查询
	 * 
	 * @param params
	 * @return
	 */
	public JSONObject queryFileUnit(Map<String, String> params) {
		return relBuild(DataServiceEnum.queryFileUnit, params);
	}

	/**
	 * 档案卷查询
	 * 
	 * @param params
	 * @return
	 */
	@Deprecated
	public JSONObject queryFileUnitSet(Map<String, String> params) {
		return relBuild(DataServiceEnum.queryFileUnitSet, params);
	}

	/**
	 * 指标项内容查询
	 * 
	 * @param params
	 * @return
	 */
	public JSONObject lookupDirectory(Map<String, String> params) {
		return relBuild(DataServiceEnum.lookupDirectory, params);
	}

	/**
	 * 档案组卷/拆卷
	 * 
	 * @param params
	 * @return
	 */
	public JSONObject spliceFileUnitSet(Map<String, String> params) {
		return relBuild(DataServiceEnum.spliceFileUnitSet, params);
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
