package com.demo.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Arrays;
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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jfinal.core.Controller;
import com.jfinal.upload.UploadFile;
import com.zjtzsw.embed.SpUtils;
import com.zjtzsw.embed.SpUtils.HttpMethod;

/**
 * IndexController
 */
public class IndexController extends Controller {
	public void index() {
		render("index.html");
	}
	
	public void threadTest() {
		for(int i =0;i<20;i++){
			TestThread t1 = new TestThread();
			t1.start();
			
		}
//		TestThread t2 = new TestThread();
//		TestThread t3 = new TestThread();
//		TestThread t4 = new TestThread();
//		TestThread t5 = new TestThread();
		
//		t2.start();
//		t3.start();
//		t4.start();
//		t5.start();
		renderNull();
	}
	
	public void initFile() {
		Map<String,String> params = new HashMap<String,String>();
//		params.put("bizCode", "166dfe29-ebcb-4329-9e72-8544d61cf68d");	//事项编码
//		params.put("bizId", "A2"); 										//业务ID
//		params.put("uuid", "");						//办件ID
//		params.put("subjectKey", "140424199405203215");
//		params.put("subjectName", "主体李");//姓名
//		params.put("subjectType", "01");//主体类型 01个人 02单位
//		params.put("idcards", "");		//
//		params.put("borrowStatus", "");		//
//		params.put("borrowId", "");		//
//		params.put("stage", "");		//
//		params.put("referSubjectType", "");		//
//		params.put("referSubjectKey", "");		//
//		params.put("referSubjectName", "");		//
		
		//1.
//		params.put("bizCode", "6767195b73f0402e908e6002b3226469");//事项编码
//		params.put("bizId", "A2");//业务ID
//		params.put("uuid", "c80ca27d-23f8-4048-811d-10cd424de635");	//办件ID
//		params.put("subjectKey", "140424199405203215");
//		params.put("subjectName", "主体李");//姓名
//		params.put("subjectType", "01");//主体类型 01个人 02单位
//		params.put("idcards", "");//
//		params.put("borrowStatus", "");//
//		params.put("borrowId", "");//
//		params.put("stage", "");//
//		params.put("referSubjectType", "");		//
//		params.put("referSubjectKey", "");		//
//		params.put("referSubjectName", "");		//
		
		//2.
//		params.put("bizCode", "");//事项编码
//		params.put("bizId", "");//业务ID
//		params.put("uuid", "");	//办件ID
//		params.put("subjectKey", "140424199405203215");
//		params.put("subjectName", "主体李");//姓名
//		params.put("subjectType", "01");//主体类型 01个人 02单位
//		params.put("idcards", "");//
//		params.put("borrowStatus", "");//
//		params.put("borrowId", "");//
//		params.put("stage", "");//
//		params.put("referSubjectType", "");		//
//		params.put("referSubjectKey", "");		//
//		params.put("referSubjectName", "");		//
//		
//		//3.
		params.put("bizCode", "");//事项编码
		params.put("bizId", "");//业务ID
		params.put("uuid", "cc8c667b-193c-41de-bd52-c4fd545f05f7");	//办件ID
		params.put("subjectKey", "");
		params.put("subjectName", "");//姓名
		params.put("subjectType", "");//主体类型 01个人 02单位
		params.put("idcards", "");//
		params.put("borrowStatus", "");//
		params.put("borrowId", "");//
		params.put("stage", "");//
		params.put("referSubjectType", "");		//
		params.put("referSubjectKey", "");		//
		params.put("referSubjectName", "");		//
		
		params.put("bname", "ZTCS");//
		params.put("method","initFileUnit");//主体类型 01个人 02单位
		String urlString = "http://192.3.3.239:8888/lemis/caller/remote";//请求地址，需要向当询问电子档案地址
		StringBuffer buffer = new StringBuffer();
		buffer.append(urlString+"?");
		for(Map.Entry<String, String> entry : params.entrySet()){
			buffer.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
		}
		String url = buffer.toString();
		String result = client(url);
		renderJson(result);
	}
//	public void index() {
//		Map<String,String> params = new HashMap<String,String>();
//		params.put("bizCode", "166dfe29-ebcb-4329-9e72-8544d61cf68d");//事项编码
//		params.put("subjectKey", "140424199405203215");
//		params.put("subjectName", "主体李");//姓名
//		params.put("subjectType", "01");//主体类型 01个人 02单位
//		params.put("bizId", "A2"); //
//		params.put("bname", "HZYTH");//
////		params.put("appkey", "36864ffe-c82b-4394-8d72-b6eb313a7ecc");//
//		params.put("method","initFileUnit");//主体类型 01个人 02单位
//		String urlString = "http://172.16.76.164:8888/lemis/caller/remote";//请求地址，需要向当询问电子档案地址
//		StringBuffer buffer = new StringBuffer();
//		buffer.append(urlString+"?");
//		for(Map.Entry<String, String> entry : params.entrySet()){
//			buffer.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
//		}
//		String url = buffer.toString();
//		client(url);
//		render("index.html");
//	}
	public void a() {
		Map<String,String> params = new HashMap<String,String>();//组织参数
		params.put("fuId", "e035d862-2cab-467d-a2ea-9903efa81720");											//影像办件id
		params.put("bizId","A3"); 										//业务id
		params.put("bname","HZYTH"); 									//帐套名
		params.put("method","saveFileUnit"); 							//方法名
		String urlString = "http://172.16.76.164:8888/lemis/caller/remote";//请求地址，需要向当询问电子档案地址
		StringBuffer buffer = new StringBuffer();
		buffer.append(urlString+"?");
		for(Map.Entry<String, String> entry : params.entrySet()){
			buffer.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
		}
		String url = buffer.toString();
		client(url);
		renderNull();
	}
	public void upload() throws JSONException, ClientProtocolException, IOException {
		UploadFile file1 = getFile("file");
		String filePath = file1.getUploadPath()+"\\" + file1.getFileName();
		File file = new File(filePath);
		
		if(!file.exists()){
			System.err.println("---------------------<<<<<" + filePath + "文件不存在>>>>>---------------");
			return;
		}
		
		String fileName = file.getName();
		String postfix = (fileName.substring(fileName.lastIndexOf(".") + 1)).toLowerCase();

		//TODO 后台接口调用
		Map<String,String> params = new HashMap<String,String>();
		//1.
//		params.put("uuid", "");  									// 材料主键
//		params.put("MaterialName", "");  								// 办件材料名称
//		params.put("bizId", "A2");  										// 业务主键
//		params.put("fileCode","0ad33b85219d4869bf709026cb16da7f"); 			// 材料要挂到的材料分类的材料编码
		//2.
		params.put("uuid", "c8dc8d55-d2d2-4cef-8995-e6a77d888891");  	// 材料主键
		params.put("MaterialName", "");  								// 办件材料名称
		params.put("bizId", "");  										// 业务主键
		params.put("fileCode",""); 			// 材料要挂到的材料分类的材料编码
		//3
//		params.put("uuid", "");  									// 材料主键
//		params.put("MaterialName", "测试材料新");  								// 办件材料名称
//		params.put("bizId", "A2");  										// 业务主键
//		params.put("fileCode","0ad33b85219d4869bf709026cb16da7f"); 			// 材料要挂到的材料分类的材料编码
//		params.put("YX1916","备注"); 			
		
		//固定不变的
		params.put("bname", "HZYTH");  										// 帐套编码
		params.put("uuidExt", postfix); 									// 文件扩展名
		params.put("filename",fileName); 										// 材料文件名
		params.put("filesize",String.valueOf(file.length())); 										// 文件大小
		params.put("clly",""); 											// 备注
		params.put("method","putStorageContent");   					// 方法名

		
		//上传结果材料文件upload
//		params.put("bizId", "");    						// 业务主键
//		params.put("fuId", "227ccb89-a398-4879-8d9a-2cd5c410fc2c");    // 影像办件ID
//		params.put("effective_days", "");    				// 
//		params.put("oss_flag", "");    						// 是否上传oss
//		params.put("fileName", fileName);    						// 材料文件名
//		params.put("bname", "HZYTH");    					// 帐套编码
//		params.put("method", "upload");
		
		//confirmOffice
//		params.put("bname", "HZYTH");    						// 帐套编码
//		params.put("uuid", "A2");    						// 业务主键
//		params.put("filename", fileName);    						// 材料文件名
//		params.put("method", "confirmOffice");
		
		/*JSONObject json = null;
		try {
			String result = sendHttpClientUpload(params,file);
			json = new JSONObject(result);
//			System.out.println();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		renderJson(json);*/

		String urlString = "http://172.16.76.164:8888/lemis/caller/remote";//请求地址，需要向当询问电子档案地址
		StringBuffer buffer = new StringBuffer();
		buffer.append(urlString+"/putStorageContent?");
//		buffer.append(urlString+"?");
//		buffer.append(urlString+"/upload?");
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			try {
				String value = entry.getValue();
				if(value!=null&&!"".equals(value)){
				sb.append(entry.getKey())
						.append('=')
						.append(URLEncoder.encode(value,
								SpUtils.getDefaultCharset())).append('&');
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
			}
		}
		String url = buffer.toString()+sb.toString();

		PostMethod filePost = new PostMethod(url);
		HttpClient client = new HttpClient();		
		Part[] parts = {new FilePart(file.getName(), file)};
		filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));
		client.getHttpConnectionManager().getParams().setConnectionTimeout(1000*50);
		int status = client.executeMethod(filePost);
		BufferedReader reader = new BufferedReader(new InputStreamReader(filePost.getResponseBodyAsStream()));
		StringBuffer stringBuffer = new StringBuffer();
		String str = "";
		while((str = reader.readLine()) != null){
		    stringBuffer.append(str);
		}
		String httpResponse = stringBuffer.toString();
		System.out.println("上传结果："+httpResponse);
		renderJson(httpResponse);
	}
	
	public void test() {
		Map<String,String> params = new HashMap<String,String>();
		
		//图片下载
//		Map<String,String> map = new HashMap<String,String>();
//		map.put("bizId", "A2"); 						// 业务系统主键
//		map.put("materialCode", "0ad33b85219d4869bf709026cb16da7f"); // 材料编码
//		map.put("subjectType", "01"); 				// 主体类型
//		map.put("subjectKey", "140424199405203215"); // 主体编码
//		List<Map<String, String>> list = Arrays.asList(map);
//		JSONArray ja = new JSONArray(list);
//		params.put("bname", "HZYTH"); // 帐套编码
//		params.put("params",ja.toString());
//		params.put("method", "fetchImages"); 			// 方法名

		//事项新增
//		params.put("YX0601", "6767195b73f0402e908e6002b3226469");      //事项编码
//		params.put("YX0603", "测试事项");    	//事项名称
//		params.put("YX085", "01");          	//主体类型
//		params.put("bname", "HZYTH");           //帐套名（电子档案用作校验）
//		params.put("method", "addYx06");		//方法名
//		params.put("YX3200", ""); 				// 案卷表主键
//		params.put("BSC001", "");				// 机构编码
//		params.put("YX0608", "");				// 机构编码
		
		//编辑事项
//		params.put("YX0601", "6767195b73f0402e908e6002b3226469");    // 事项编码
//		params.put("YX0603", "测试事项编辑");    // 事项名称
//		params.put("YX085", "01");    // 主体类型
//		params.put("bname", "HZYTH");        // 帐套名（电子档案用作校验）
//		params.put("method", "editYx06");
		
		//删除办件
//		params.put("hostId", "");    		 // 业务主键
//		params.put("bizId", "A3");    		 // 业务主键
//		params.put("bname", "HZYTH");        // 帐套名（电子档案用作校验）
//		params.put("method", "cancelYx17");

		//办件删除前校验
//		params.put("hostId", "A2");    						// 业务ID
//		params.put("method", "checkCancelYx17");
		
		//查询办件
//		params.put("uuid", "48a2cd90-a70b-4771-aae8-9dd60d73850f");   // 办件材料主键
//		params.put("idcards", "");    						// 业务ID
//		params.put("batch", "");    						// 批次
//		params.put("bname", "HZYTH");    					// 帐套编码
//		params.put("method", "queryStorageObjects");
		
		//绑定批次号
//		params.put("uuid", "");    						// 办件材料主键
//		params.put("batch", "");    						// 批次（数据库备注和代码有冲突）
//		params.put("method", "submitBatch");
		
		//供业务系统更新提交的数据
//		params.put("bizId", "A3");    								// 业务主键
//		params.put("fuId", "e035d862-2cab-467d-a2ea-9903efa81720"); // 影像办件主键
//		params.put("subjectKey", "140424199405203215");    			// 主体编码
//		params.put("subjectName", "主体李");    						// 主体名称
//		params.put("subjectType", "01");    						// 主体类型
//		params.put("AAZ998", "");    						// 宁波数据中心id
//		params.put("AAZ999", "");    						// 宁波数据中心id
//		params.put("bname", "HZYTH");    						// 帐套名称
//		params.put("method", "updateFileUnit");
		
		//影像提交绑定前的校验
//		params.put("fuid", "e035d862-2cab-467d-a2ea-9903efa81720");    						// 影像办件主键
//		params.put("method", "requiredValidate");
		
		//删除图片
//		params.put("uuid", "12cfacbd6c2042b6bffc6544f429a8fd");    	// 办件电子材料存储主键
//		params.put("bname", "HZYTH");								//帐套编码
//		params.put("method", "removeStorageObject");
		
		//装订成册下载
//		params.put("all", "");    						// 
//		params.put("filter", "");    						// 
////		params.put("uuids", "48a2cd90-a70b-4771-aae8-9dd60d73850f");    	// 办件材料主键(多个用"，"隔开)
//		params.put("bizId", "A2");    						// 业务主键
//		params.put("bizCode", "166dfe29-ebcb-4329-9e72-8544d61cf68d");    						// 事项编码
//		params.put("materialCode", "0ad33b85219d4869bf709026cb16da7f");    						// 材料编码
//		params.put("appContext", "/lemis");    						// 上下文路径
//		params.put("hostIP", "localhost:8888");    						// IP地址
//		params.put("appkey", "36864ffe-c82b-4394-8d72-b6eb313a7ecc");    						// 接入密匙
//		params.put("bname", "HZYTH");    						// 帐套编码
//		params.put("method", "buildPdf");
		
		//删除办件材料
//		params.put("uuid", "48a2cd90-a70b-4771-aae8-9dd60d73850f");    						// 办件材料ID
//		params.put("bname", "HZYTH");    						// 办件材料ID
//		params.put("method", "removeCategory");
		
		//根据批次删除办件存储材料
//		params.put("bizId", "A1");    						// 业务主键
//		params.put("bizCode", "166dfe29-ebcb-4329-9e72-8544d61cf68d");    	// 事项编码
//		params.put("batch", "123");    						// 批次
//		params.put("bname", "HZYTH");    						// 帐套编码
//		params.put("method", "removePicsByBatch");
		
		//创建新办件
//		params.put("bizId", "");    						// 业务主键
//		params.put("bizCode", "166dfe29-ebcb-4329-9e72-8544d61cf68d");  // 事项编码
//		params.put("subjectKey", "140424199405203215");    				// 主体编码
//		params.put("subjectType", "01");    							// 主体类型
//		params.put("subjectName", "主体李");    							// 主体名称
//		params.put("bname", "HZYTH");    								// 帐套编码
//		params.put("method", "initFileUp");
		
		//更新办件绑定的业务主键和事项
//		params.put("bizId", "A2");    											// 业务主键
//		params.put("bizCode", "166dfe29-ebcb-4329-9e72-8544d61cf68d");    		// 事项编码
//		params.put("fuId", "227ccb89-a398-4879-8d9a-2cd5c410fc2c");    			// 影像办件ID
//		params.put("method", "updateHzBizId");
		
		//查询办件材料
//		params.put("fuid", "3e09d384-ffec-45e8-a9ba-f72e606cba28");    						// 影像办件ID
//		params.put("method", "queryMaterials");
		
		//根据业务编码和办件材料名称查找办件材料
//		params.put("bizId", "A2");    							// 业务主键
//		params.put("clname", "在读证明");    					// 办件材料名称
//		params.put("bname", "HZYTH");    						// 帐套编码
//		params.put("method", "queryMaterialsByName");
		
		//申请业务采用
//		params.put("bizId", "A2");    						// 业务主键
//		params.put("fuID", "3e09d384-ffec-45e8-a9ba-f72e606cba28");    						// 影像办件id
//		params.put("method", "application");
		
		//根据业务主键创建新的人脸办件材料 
//		params.put("bizId", "A2");    						// 业务主键
//		params.put("YX1916", "备注");    						// 业务主键
//		params.put("bname", "HZYTH");    						// 帐套编码
//		params.put("method", "photoNewMaterial");
		
		//测试一下
//		params.put("bname", "HZYTH");    						// 帐套编码
//		params.put("method", "testYx21");
		
		//根据事项解释查询办件材料
//		params.put("flh", "测试事项李");    						// 事项解释
//		params.put("method", "importMaterial");
		
		//获取文件提取密码和下载路径
		//1.
//		params.put("type", "01");    						// 
//		params.put("code", "f23bf9b88ec44b959447d824b6d9be2b");	// 文件ID
		//2.
//		params.put("type", "02");    						// 
//		params.put("code", "A2"); 							// 业务ID 
		//3.
//		params.put("type", "03");    						// 
//		params.put("code", "227ccb89-a398-4879-8d9a-2cd5c410fc2c");// 办件ID
		
//		params.put("method", "getOssUrl");
		
		//checkIDcardExists
//		params.put("subjectType", "01");    						// 主体类型
//		params.put("subjectKey", "140424199405203215");    			// 主体编码
//		params.put("method", "checkIDcardExists");
		
		//根据业务ID查找办件文件信息并生成xml
//		params.put("bizId", "A2");    						// 业务主键
//		params.put("method", "attrXml");

		//生成参数列表（自己写的生成<table>的方法）
//		params.put("yx1700", "227ccb89-a398-4879-8d9a-2cd5c410fc2c");
//		params.put("method", "test2");
		
		//根据事项编码和业务主键查询办件
//		params.put("bizId", "A2");    									 // 业务主键
//		params.put("bizCode", "166dfe29-ebcb-4329-9e72-8544d61cf68d");   // 事项编码
//		params.put("method", "getFuIdByBizIdBizCode");
		
		//下载指定的文件
//		params.put("uuid", "f23bf9b88ec44b959447d824b6d9be2b");    						// 文件主键
//		params.put("bname", "HZYTH");    				// 事项编码
//		params.put("method", "download");
		
		//拷贝办件数据信息
//		params.put("fuId", "227ccb89-a398-4879-8d9a-2cd5c410fc2c");    						// 办件主键
//		params.put("bizId", "A2");    				// 事项编码
//		params.put("bizCode", "166dfe29-ebcb-4329-9e72-8544d61cf68d");    				// 事项编码
//		params.put("method", "copyFileUnit");
		
		//什么也没干，有什么用，服了
//		params.put("bizId", "A2");    						// 业务主键
//		params.put("method", "receiveBizId");
		
		//事后办件绑定
//		params.put("hostId", "A3");    										// 业务主键
//		params.put("bizCode", "166dfe29-ebcb-4329-9e72-8544d61cf68d");    	// 事项编码
//		params.put("subjectKey", "140424199405203215");    					// 主体编码
//		params.put("subjectName", "主体李");    								// 主题名称
//		params.put("subjectType", "01");    								// 主体类型
//		params.put("bname", "HZYTH");	    								// 帐套编码
//		params.put("method", "saveAfterYx17");
		
		//办件初始化（fileUnitInit）
////		params.put("bizCode", "166dfe29-ebcb-4329-9e72-8544d61cf68d");    						// 事项编码
//		params.put("bizId", "B1");    						// 业务ID
////		params.put("subjectType", "01");    						// 主题类型
////		params.put("subjectKey", "140424199405203215");    						// 主体编码
////		params.put("subjectName", "主体李");    						// 主体名称
//		Map<String,String> fileMap = new HashMap<String,String>();
//		fileMap.put("materialName", "人脸材料  ");
//		fileMap.put("filePath", "测试提取路径");
//		fileMap.put("takeCode", "测试提取码");
//		fileMap.put("fileName", "测试文件名");
//		List<Map<String, String>> files = Arrays.asList(fileMap);
////		params.put("files", "[{materialName:\"人脸材料\",filePath:\"测试提取路径\",takeCode:\"测试提取码\",fileName:\"测试文件名\"}]");    						// 
//		JSONArray fileJson = new JSONArray(files);
//		params.put("files", fileJson.toString());    						// 
//		params.put("replace", "");    						// 
//		params.put("bname", "HZYTH");    						// 
//		params.put("method", "fileUnitInit");
		
		//
//		params.put("bizId", "A2");    									// 事项解释
//		params.put("bizCode", "166dfe29-ebcb-4329-9e72-8544d61cf68d");	// 事项编码
//		params.put("method", "getJingTuDzdaID");
		
		//添加基础材料
//		params.put("YX3101", "结果材料");        //材料名称
//		params.put("YX3102", "888888888888");        //材料编码
//		params.put("YX099", "02");           //是否基础材料
//		params.put("YX026", "99");           //材料密级
//		params.put("YX3104", "1");           //顺序
//		params.put("bname", "HZYTH");        //帐套名（电子档案用作校验）
//		params.put("method", "addYx31");
		
		//添加事项材料
//		params.put("YX0601", "166dfe29-ebcb-4329-9e72-8544d61cf68d");    // 事项编码
//		params.put("YX0601", "6767195b73f0402e908e6002b3226469");    // 事项编码
//		params.put("YX3102", "888888888888");    // 材料编码
//		params.put("YX021", "01");     // 是否有效
//		params.put("YX096", "01");     // 是否归档材料
//		params.put("bname", "HZYTH");        // 帐套名（电子档案用作校验）
//		params.put("method", "addYx07");

		
//		params.put("bname", "HZYTH");        // 帐套名（电子档案用作校验）
//		params.put("method", "test1");
		
		//是否启用一体化事项
//		params.put("use", "true");    						// 是否启用一体化事项
//		params.put("method", "useBizCode");
		
		//推送事项
//		Map<String,Object> map = new HashMap<String,Object>();
//
//		map.put("bizCode", "ceshibianma");    		//事项编码 
//		map.put("bizTitle", "推送事项测试");    		// 
//		map.put("YX085", "01");    					//主体类型 
//		map.put("YX0607", "f4f470d6f32e4e23b38e24c11d692ee7");  //服务目录 
//		map.put("YX0608", "216280528cf8409787faa0d9d1c6a57c"); //政务网事项内码 
//		map.put("YX0609", "f2d728cccf1e4dc3bbb3b828268f84d2");//行政区划
//		
//		Map<Object,Object> baseMaterialMap = new HashMap<Object,Object>();//基础材料
//		baseMaterialMap.put("baseMaterialName", "推送事项测试基础材料2");//材料名称
//		baseMaterialMap.put("baseMaterialCode", "2546983cc33e4e00b28363041223b1cf");//材料编码
//		baseMaterialMap.put("YX099", "02");		// 是否基础材料
//		baseMaterialMap.put("YX026", "01");		// 材料密级
//		baseMaterialMap.put("YX3104", "");		// 顺序
//		
//		Map<Object,Object> materialMap = new HashMap<Object,Object>();
//		materialMap.put("materialName", "推送事项测试事项材料2");// 材料名称
//		materialMap.put("materialCode", "8760189794ae4f0cae2401f7aa03cef9"); // 材料编码
//		materialMap.put("YX096", "01");// 是否归档材料
//		materialMap.put("YX021", "01");// 是否有效
//		materialMap.put("YX0711", "0");// 网报不维护
//		JSONObject jsonObj = new JSONObject(materialMap);
//		
//		baseMaterialMap.put("material", jsonObj);
//		
//		List<Map<Object,Object>> list1 = Arrays.asList(baseMaterialMap);
//		map.put("baseMaterials", new JSONArray(list1));    						// 
//
//		List<Map<String, Object>> list = Arrays.asList(map);
//		params.put("item", (new JSONArray(list)).toString());    						// 
//		params.put("bname", "HZYTH");
//		params.put("method", "initItem");
		
		
		//自己测试的方法
//		params.put("yx1700", "dec58f5b-073a-40ad-9a0b-fbf84cf64e95");    						
//		params.put("yx1918", "结果材料");    						
//		params.put("yx1919", "888888888888");    						
//		params.put("bname", "HZYTH");    						
//		params.put("bname", "bname");    						
//		params.put("method", "test1");

		//办件初始化（fileUnitInit）
//		Map<String,String> fileMap1 = new HashMap<String,String>();
//		fileMap1.put("materialName", "《人力资源服务许可申请表》");
//		fileMap1.put("filePath", "http://59.202.58.229/oss-nanwei/330122161901248000004_wR3sqXToksJ.jpeg");
//		fileMap1.put("takeCode", "");
//		fileMap1.put("fileName", "python.jpeg");
//		Map<String,String> fileMap2 = new HashMap<String,String>();
//		fileMap2.put("materialName", "中介组织章程及有关工作制度");
//		fileMap2.put("filePath", "http://59.202.58.229/oss-nanwei/330122161901248000004_maJ4N3Afn0h.jpeg");
//		fileMap2.put("takeCode", "");
//		fileMap2.put("fileName", "python.jpeg");
//		Map<String,String> fileMap3 = new HashMap<String,String>();
//		fileMap3.put("materialName", "办公及服务场所产权或使用权证明");
//		fileMap3.put("filePath", "http://59.202.58.229/oss-nanwei/330122161901248000004_xAM0qvc7qsd.jpeg");
//		fileMap3.put("takeCode", "");
//		fileMap3.put("fileName", "python.jpeg");
//		Map<String,String> fileMap4 = new HashMap<String,String>();
//		fileMap4.put("materialName", "从业人员的劳动合同、身份证明、学历证明");
//		fileMap4.put("filePath", "http://59.202.58.229/oss-nanwei/330122161901248000004_EeiQFW06fhN.jpeg");
//		fileMap4.put("takeCode", "");
//		fileMap4.put("fileName", "python.jpeg");
//		Map<String,String> fileMap5 = new HashMap<String,String>();
//		fileMap5.put("materialName", "营业执照及法定代表（负责）人身份证或受委托人身份证、委托书");
//		fileMap5.put("filePath", "http://59.202.58.229/oss-nanwei/330122161901248000004_15RstB3Sj3b.jpeg");
//		fileMap5.put("takeCode", "");
//		fileMap5.put("fileName", "python.jpeg");
//		List<Map<String, String>> files = Arrays.asList(fileMap1,fileMap2,fileMap3,fileMap4,fileMap5);
//	//	params.put("files", "[{materialName:\"人脸材料\",filePath:\"测试提取路径\",takeCode:\"测试提取码\",fileName:\"测试文件名\"}]");    						// 
//		JSONArray fileJson = new JSONArray(files);
//		params.put("files", fileJson.toString());    						// 
//		params.put("title", "经营性人力资源服务机构从事职业中介活动许可");    						// 
//		params.put("subjectName", "王金龙人力资源服务机构测试");    						// 主体名称
//		params.put("subjectType", "01");    						// 主题类型
//		params.put("subjectKey", "92330105MA2CGXLQ54");    						// 主体编码
//		params.put("bizCode", "64c4c124-1f61-4dd5-a07d-d61d675a94be");    						// 事项编码
//		params.put("bizId", "330122161901248000004");    						// 业务ID
//		params.put("replace", "");    						// 
//		params.put("bname", "bname");    						// 
//		params.put("method", "fileUnitInit");
		
		//全部引用办件(办件删除前校验)
		params.put("bizId", "D33333");    						
		params.put("bizCode", "81053e49");    						
		params.put("newBizId", "E1");    						
		params.put("bname", "ZTCS");    						
		params.put("method", "quoteFileUnit");
		
//		String urlString = "http://172.16.76.164:8888/lemis/caller/remote";//请求地址，需要向当询问电子档案地址
//		String urlString = "http://172.16.83.29:7005/lemis/caller/remote";//请求地址，需要向当询问电子档案地址
		String urlString = "http://192.3.3.239:8888/lemis/caller/remote";//请求地址，需要向当询问电子档案地址
		StringBuffer buffer = new StringBuffer();
		buffer.append(urlString+"?");
//		for(Map.Entry<String, String> entry : params.entrySet()){
//		    buffer.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
//		}
//		String url = buffer.substring(0,buffer.length()-1).toString();
		
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			try {
				String value = entry.getValue();
				if(value!=null&&!"".equals(value)){
				sb.append(entry.getKey())
						.append('=')
						.append(URLEncoder.encode(value,
								SpUtils.getDefaultCharset())).append('&');
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
			}
		}
		String url = buffer.toString()+sb.toString();
		
		//使用httpClient发送url请求即可
		String result = client(url);

		renderJson(result);
	}
	
	public String client(String url) {
		HttpRequestBase httpRequest = SpUtils.createHttpRequest(HttpMethod.GET, url);
		httpRequest.setHeader("User-Agent", "Mozilla/5.0");
		httpRequest.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		httpRequest.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");// "en-US,en;q=0.5");
		httpRequest.setHeader("Accept-Charset","ISO-8859-1,utf-8,gbk,gb2312;q=0.7,*;q=0.7");	
		httpRequest.setHeader("Referer","1");	

		HttpResponse httpResponse = null;
		try {
			// http请求
			CloseableHttpClient httpClient = SpUtils.createDefaultHttpClient(SpUtils.createDefaultHttpClientConfig());
			RequestConfig requestConfig = RequestConfig.custom()
					.setSocketTimeout(1000*120)
					.setConnectTimeout(1000*500)
					.setConnectionRequestTimeout(1000*300)
					.build();// 设置请求和传输超时时间
			httpRequest.setConfig(requestConfig);
//			System.out.println("+++++++++++++"+httpClient+"&&&&&"+Thread.currentThread().getId());
			long start = System.currentTimeMillis();
			httpResponse = httpClient.execute(httpRequest, HttpClientContext.create());
			long usedTime = (System.currentTimeMillis() - start) / 1000;
//			System.out.println("Generate complete in " + usedTime + " seconds.");  
			String result = EntityUtils.toString(httpResponse.getEntity(), SpUtils.getDefaultCharset());
			System.out.println("-------------");
			System.out.println("得到的结果：："+result);
			return result;
		} catch (ClientProtocolException e) {
			SpUtils.log("ClientProtocolException", e);
		} catch (IOException e) {
			SpUtils.log("IOException", e);
		} finally {
			HttpClientUtils.closeQuietly(httpResponse);
		}
		return null;
	}
	
	public String client(URI url) {
		HttpRequestBase httpRequest = new HttpGet(url);
		httpRequest.setHeader("User-Agent", "Mozilla/5.0");
		httpRequest.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		httpRequest.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");// "en-US,en;q=0.5");
		httpRequest.setHeader("Accept-Charset","ISO-8859-1,utf-8,gbk,gb2312;q=0.7,*;q=0.7");	
		httpRequest.setHeader("Referer","1");	
		
		HttpResponse httpResponse = null;
		try {
			// http请求
			CloseableHttpClient httpClient = SpUtils.createDefaultHttpClient(SpUtils.createDefaultHttpClientConfig());
			RequestConfig requestConfig = RequestConfig.custom()
					.setSocketTimeout(1000*120)
					.setConnectTimeout(1000*500)
					.setConnectionRequestTimeout(1000*300)
					.build();// 设置请求和传输超时时间
			httpRequest.setConfig(requestConfig);
//			System.out.println("+++++++++++++"+httpClient+"&&&&&"+Thread.currentThread().getId());
			long start = System.currentTimeMillis();
			httpResponse = httpClient.execute(httpRequest, HttpClientContext.create());
			long usedTime = (System.currentTimeMillis() - start) / 1000;
//			System.out.println("Generate complete in " + usedTime + " seconds.");  
			String result = EntityUtils.toString(httpResponse.getEntity(), SpUtils.getDefaultCharset());
			System.out.println("-------------");
			System.out.println("得到的结果：："+result);
			return result;
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
		
		params.put("_t", String.valueOf(System.currentTimeMillis()));

		String urlString = "http://172.16.76.164:8888/lemis/caller/remote/putStorageContent";
		
		urlString = SpUtils.combine(urlString, params);
		
		PostMethod filePost = new PostMethod(urlString);
		HttpClient client = new HttpClient();
		
		Part[] parts = {new FilePart(file.getName(), file)};
		filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));
		
		client.getHttpConnectionManager().getParams().setConnectionTimeout(1000*50);
		int status = client.executeMethod(filePost);
//		当返回值过大时会报错:Going to buffer response body of large or unknown size.
//		String httpResponse = filePost.getResponseBodyAsString();
		BufferedReader reader = new BufferedReader(new InputStreamReader(filePost.getResponseBodyAsStream()));
		StringBuffer buffer = new StringBuffer();
		String str = "";
		while((str = reader.readLine()) != null){
			buffer.append(str);
		}
		String httpResponse = buffer.toString();
		
		if (status == HttpStatus.SC_OK) {
			message = "上传成功";
		 } else {
			message = "上传失败";
		}

		return httpResponse;
	}
}





