package com.tzsw.bj.common.controller;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import com.jfinal.upload.UploadFile;
import com.tzsw.jfinal.core.BaseController;
import com.tzsw.jfinal.ext.annotation.ControllerBind;
import com.zjtzsw.embed.BridgeServlet;

@ControllerBind(controllerKey = "/upload")
public class UploadController extends BaseController{
	
	public void index() {
		renderNull();
	}
	
	public void upload() throws JSONException, ClientProtocolException, IOException {
		UploadFile file = getFile("file");
		
		//影像业务进行初始化
		Map<String,String> params = new HashMap<String,String>();
		params.put("bizCode", "166dfe29-ebcb-4329-9e72-8544d61cf68d");					//事项编码
		params.put("bizId", "A2");						//业务系统主键
		params.put("subjectKey", "140424199405203215");					//主体编码
		params.put("subjectName", "主体李");					//主体名称
		params.put("subjectType", "01");					//主体类型 01个人 02单位
		org.json.JSONObject res =null;
		res = new BridgeServlet().relBuild("initFileUnit", params);
		System.out.println(res.toString());
		
//		//电子档案办件与业务办件进行绑定
//		JSONObject jsonData = res.getJSONObject("data");
//		String fuId = jsonData.getString("uuid");
//		params.put("bizId", "ks04Pk");		//业务系统主键
//		params.put("fuId", fuId);			//影像主键(办件信息表主键),在影像初始化返回的参数中的data里的uuid获得
//		params.put("lockId","Tzsw"+fuId);	//秘钥
//		res = new BridgeServlet().relBuild("saveFileUnit", params);
//		System.out.println(res.toString());
 
		//图片上传
//		params.put("uuid", "54141");							// 业务编码
		params.put("fileCode","0ad33b85219d4869bf709026cb16da7f");		// 材料要挂到的材料分类的材料编码yx1919
		res = new BridgeServlet().relBuildFile("putStorageContent", params, file.getUploadPath()+"\\" + file.getFileName());	//方法名  参数  图片路径
		System.out.println(res.toString());

		renderJson(res);
	}
	
	public void initFileUnit() {
		Map<String,String> params = new HashMap<String,String>();
		
		//影像业务进行初始化
		params.put("bizCode", "166dfe29-ebcb-4329-9e72-8544d61cf68d");		//事项编码
		params.put("bizId", "A2");						//业务系统主键
		params.put("subjectKey", "140424199405203215");					//主体编码
		params.put("subjectName", "主体李");					//主体名称
		params.put("subjectType", "01");					//主体类型 01个人 02单位
		org.json.JSONObject res =null;
		res = new BridgeServlet().relBuild("initFileUnit", params);
		System.out.println(res.toString());
		renderJson(res);
	}
	public void test() {
		@SuppressWarnings("unused")
		String uuid = UUID.randomUUID().toString();
		Map<String,String> params = new HashMap<String,String>();
		
		//基础材料
//		params.put("YX3101", "测试李1");					//材料名称
//		params.put("YX3102", uuid);					//材料编码
//		params.put("YX099", "01");					//是否基础材料
//		params.put("YX026", "01");					//材料密级
//		params.put("YX3104", "1");					//顺序

		//事项
//		params.put("YX0601", "166dfe29-ebcb-4329-9e72-8544d61cf68d");		//事项编码
//		params.put("YX0603", "测试事项李");										//事项名称
//		params.put("YX085", "01");											//主体类型

		//事项材料
//		params.put("YX0601", "166dfe29-ebcb-4329-9e72-8544d61cf68d");		//事项编码
//		params.put("YX3102", "0ad33b85219d4869bf709026cb16da7f");		//材料编码						
//		params.put("YX021", "01");											//是否有效 
//		params.put("YX096", "02");											//是否归档材料

		//办件删除（注销）
//		params.put("bizId", "ks04Pk");		//业务主键
//		params.put("hostId", "ks04Pk");											//						
		
		//供业务系统调用保存提交的数据
//		params.put("bname", "SXRSKS");											//帐套名
//		params.put("fuId", "d763864f4319462c8a99240e9d0f3513");					//影像办件ID yx1700
//		params.put("bizId", "ks04Pk");											//业务主键
		
		//供业务系统更新提交的数据
//		params.put("bizId", "ks04Pk");											//业务主键
//		params.put("fuId", "d763864f4319462c8a99240e9d0f3513");					//影像办件ID yx1700
//		params.put("subjectKey", "sxrsks");											//主体编码
//		params.put("subjectName", "测试");										//主体名称
//		params.put("subjectType", "09");											//主体类型
		
		//影像提交绑定前的校验（！！注意参数是"fuid" 不是 "fuId" ）
//		params.put("fuid", "d763864f4319462c8a99240e9d0f3513");					//影像办件ID yx1700
		
		//根据材料编码和业务键id获取指定图片
//		params.put("subjectKey", "sxrsks");											//主体编码
//		params.put("subjectType", "09");											//主体类型
//		params.put("materialCode", "f9faa69a-01-LDJKGZFKB0");						//办件材料编码 yx1919
//		params.put("bizId", "ks04Pk");											//主体类型
		
		//删除图片
//		params.put("uuid", "17835");											//办件材料存储主键yx2100

		//装订成册下载
//		params.put("all", "");											//
//		params.put("filter", "");										//
//		params.put("uuids", "");											//办件材料主键（用“，”隔开）yx1900
//		params.put("bizId", "");											//业务主键yx1719
//		params.put("bizCode", "");											//办件材料存储主键yx2100
//		params.put("materialCode", "");									//办件材料编码 yx1919
//		params.put("imageProxy", "");											//办件材料存储主键yx2100
//		params.put("hostIP", "");											//办件材料存储主键yx2100
//		params.put("appContext", "");											//办件材料存储主键yx2100
//		params.put("clientIP", "");											//办件材料存储主键yx2100

		//更新服务目录
//		params.put("YX0607", "10-01002-000");											//办件材料存储主键yx2100
//		params.put("YX0601", "166dfe29-ebcb-4329-9e72-8544d61cf68d");											//办件材料存储主键yx2100
//		params.put("YX0603", "");											//办件材料存储主键yx2100
		
//		org.json.JSONObject res =null;
//		res = new BridgeServlet().relBuild("addYx07", params);
//		System.out.println(res.toString());

		//更新服务目录
//		params.put("YX0607", "10-01002-000");											//办件材料存储主键yx2100
//		params.put("YX0601", "166dfe29-ebcb-4329-9e72-8544d61cf68d");											//办件材料存储主键yx2100
//		params.put("YX0603", "");											//办件材料存储主键yx2100
		
		//装订成册下载
		params.put("all", "");    						// 
		params.put("filter", "");    						// 
		params.put("uuids", "3a5031b9-7583-485f-bd2c-53b6448b63e5");   // 办件材料主键
		params.put("bizId", "");    						// 业务主键
		params.put("bizCode", "");    						// 事项编码
		params.put("materialCode", "");    						// 材料编码
		params.put("appContext", "/lemis");    						// 上下文路径
		params.put("hostIP", "localhost:8080");    						// IP地址
		params.put("appkey", "36864ffe-c82b-4394-8d72-b6eb313a7ecc");    						// 接入密匙
		params.put("bname", "HZYTH");    						// 帐套编码
		params.put("method", "buildPdf");
		
		org.json.JSONObject res =null;
		res = new BridgeServlet().relBuild("buildPdf", params);
		System.out.println(res.toString());
		renderJson(res);
	}
	
//	public static void main(String[] args) {
//		System.out.println(UUID.randomUUID().toString());
//	}
}
