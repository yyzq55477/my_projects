package com.demo.util;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.junit.Test;

import java.util.Map;

public class CxfUtil {

	public static final String HTTP = "http://192.168.100.224";
	private static ClassLoader cl = Thread.currentThread().getContextClassLoader();
	/**
	 * 调用远程webservice
	 *
	 * @param wsdl
	 * @param methodName
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	public static String cxfWebservice(String wsdl, String methodName, String xml) throws Exception {
		Thread.currentThread().setContextClassLoader(cl);
		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		Client client = dcf.createClient(wsdl);
		Object[] objects = client.invoke(methodName, xml);
		String result = objects[0].toString() == null ? "0" : objects[0].toString();
		return result;
	}
/*
	@Test
	public void test001() {
		try {
			String result = cxfWebservice("http://172.16.69.155:9002/hzrssjjh/services/DataExchangeServer?wsdl","execute","<?xml version=\"1.0\" encoding=\"UTF-8\"?><PACKET><VERSION>2016</VERSION><SNDCODE>SYTH</SNDCODE><RCVCODE>MHXT</RCVCODE><MSGCODE>211021</MSGCODE><SNDMSGID>SMID1470709705302</SNDMSGID><USERCODE>101</USERCODE><USERNAME>ADMIN</USERNAME><SNDTIME>20160809 10:28:25</SNDTIME><RCVTIME></RCVTIME><DATASIZE>2</DATASIZE><DATALIST><DATA>	</DATA><DATA><USERID>100012</USERID><DUSERID>S0002</DUSERID><NAME>李四</NAME><IDNO>431008198307201042</IDNO></DATA></DATALIST><PAGENO>0</PAGENO><EOPSIGN></EOPSIGN><RCVMSGID></RCVMSGID><STATUS></STATUS><ERRMSG></ERRMSG><RESERVED></RESERVED></PACKET>");
			
			System.out.println(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	
	
	
//	/**
//	 * 材料信息同步接口 <?xml version="1.0" encoding="UTF-8"?> <Array>
//	 * <accessKey></accessKey><!--必填 接口认证密钥，由电子档案系统规定的密钥，访问该接口时，返回该密钥，密钥不正确，不能访问 -->
//	 * <userId></userId><!--!-- 必填 用户Id， 即用户在业务系统的唯一编号 -- --> <loginName></
//	 * loginName ><!-- 必填 用户登录名 --> <userName></userName><!--必填 用户中文名称 -->
//	 * <sysCode></sysCode ><!-- 必填 系统编号如“123456” --> <materialList> <material>
//	 * <materialId></materialId><!--! 必填 材料ID--> <materialCode></materialCode><!--!
//	 * 必填 材料编码--> <materialName></materialName><!--! 必填 材料名称-->
//	 * <reuseFlag></reuseFlag><!--! 必填 是否复用--> </material> </materialList> </Array>
//	 *
//	 * @param xml
//	 * @return
//	 * @throws Exception
//	 */
//	public static Map<String, String> callUpdateServiceCatalogWs(String xml) throws Exception {
//		String obj = CxfUtil.cxfWebservice(HTTP + "/dzda/ws/materialMsg?wsdl", "materialMsg", xml);
//		return Dom4jUtil.buildMap(obj);
//	}

//	/**
//	 * 原文信息上传接口 <?xml version="1.0" encoding="UTF-8"?> <Array>
//	 * <accessKey></accessKey><!--必填 接口认证密钥，由电子档案系统规定的密钥，访问该接口时，返回该密钥，密钥不正确，不能访问 -->
//	 * <bizId></ bizId><!--必填 业务唯一号 --> <loginName></loginName><!—必填，用户登录名>
//	 * <userName></userName><!—必填，用户名> <sysCode></sysCode><!-- 必填 系统编号如（“123456”）
//	 * --> <Fieldset><!--可多个> <ftpPath></fileType><!—文件ftp保存路径,材料保存名称必须不能是中文 -->
//	 * <fileType></fileType><!--必填 文件后缀名 --> <fileSize></fileSize><!--必填 文件大小，单位KB
//	 * --> <fileName></fileName><!—文件名称 --> <pageSize></pageSize><!--必填 文件页数-->
//	 * <materialId></materialId><!--必填 材料类型id --> </Fieldset> </Array>
//	 *
//	 * @param xml
//	 * @return
//	 * @throws Exception
//	 */
//	public static Map<String, String> callAttachTransferWs(String xml) throws Exception {
//		String obj = CxfUtil.cxfWebservice(HTTP + "/dzda/ws/attachTransfer?wsdl", "attachTransfer", xml);
//		return Dom4jUtil.buildMap(obj);
//	}
//
//	/**
//	 * 数据传输接口 <?xml version="1.0" encoding="UTF-8"?> <Array>
//	 * <accessKey></accessKey><!--必填 接口认证密钥，由电子档案系统规定的密钥，访问该接口时，返回该密钥，密钥不正确，不能访问 -->
//	 * <userId></userId><!--!-- 必填 用户Id， 即用户在业务系统的唯一编号 -- --> <loginName></
//	 * loginName ><!-- 必填 用户登录名 --> <userName></userName><!--必填 用户中文名称 -->
//	 * <sysCode></sysCode><!—必填 系统编号如（“123456”）> <Filedset> <bizId></bizId><!--必填
//	 * 该笔业务在业务系统中的唯一编号 --> <mergeId></mergeId><!--非必填 已扫描后档案唯一ID -->
//	 * <dzdaId></dzdaId><!--非必填 已扫描后档案唯一ID --> <isOther></isOther><!--必填
//	 * 数据状态：0--新增/修改；1—撤销；2—退回到未扫描 --> <STATE>已办结</STATE><!--业务经办状态 -->
//	 * <D_OPERATED></D_OPERATED><!--必填 操作时间 --> <YWMC></YWMC><!--必填 该笔业务的名称 -->
//	 * <FLH></FLH><!--必填 档案分类编号 --> <SFZH></SFZH><!--非必填 身份证号码 --> <RYBH></RYBH><!--
//	 * 人员编号 --> <XM></XM><!--非必填 姓名 --> <DWBH></DWBH><!--非必填 单位编号 -->
//	 * <DWMC></DWMC><!--非必填 单位名称 --> <PZBH></PZBH><!--非必填 凭证编号 -->
//	 * <GSNY></GSNY><!--非必填 归属年月 --> <GSD></GSD><!--非必填 归属地 --> <RQ></RQ><!--非必填 日期
//	 * --> <FL></FL><!--非必填 分类 --> <BZ></BZ><!--非必填 备注 --> <private><!--私有字段-->
//	 * <人员类别></人员类别><!-- 人员类别 --> <性别></性别><!-- 性别 --> <参保险种></参保险种><!-- 参保险种 -->
//	 * <业务类型></业务类型><!-- 业务类型 --> </private> <processes><!--流转信息--> <process>
//	 * <YWXW>审核</YWXW><!--业务行为,如“受理”、“审核”、“审批”--> <CLRY>张三</CLRY><!--处理人员-->
//	 * <CLJG>办公室</CLJG><!--处理机构--> <CLSJ>2014-04-14
//	 * 17:21:09</CLSJ><!--处理时间，格式如：yyyy-MM-dd HH:mm:ss--> </process> </processes>
//	 * </Filedset> </Array>
//	 *
//	 * @param xml
//	 * @return
//	 */
//	public static Map<String, String> callDataTransmissionWs(String xml) throws Exception {
//		String obj = CxfUtil.cxfWebservice(HTTP + "/dzda/ws/outDataTransmission?wsdl", "dataTransmission", xml);
//		return Dom4jUtil.buildMap(obj);
//	}
//	
//	/***
//	 * 一窗的办件同步
//	 * @param xml
//	 * @return
//	 * @throws Exception
//	 */
//	public static Map<String, String> callImportData(String xml) throws Exception {
//		String obj = CxfUtil.cxfWebservice(HTTP + "/dzda/ws/importData?wsdl", "importData", xml);
//		return Dom4jUtil.buildMap(obj);
//	}
//	
//	/**
//	 * 一窗受理的原文信息上传（同步yx19）
//	 */
//	public static Map<String, String> callImportPage(String xml) throws Exception {
//		String addr = HTTP + "/dzda/ws/importPage?wsdl";
//		String obj = CxfUtil.cxfWebservice(addr.trim().replace(" ", ""), "importPage", xml);
//		return Dom4jUtil.buildMap(obj);
//	}
//	/**
//	 * 批量审核接口 <?xml version="1.0" encoding="UTF-8"?> <Array>
//	 * <accessKey></accessKey><!--必填 接口认证密钥，由电子档案系统规定的密钥，访问该接口时，返回该密钥，密钥不正确，不能访问 -->
//	 * <userId></userId><!--必填 用户Id， 即用户在业务系统的唯一编号--> <sysCode></sysCode><!-- 必填
//	 * 系统编号如“123456” --> <loginName></ loginName><!-- 必填 用户登录名 -->
//	 * <userName></userName><!--必填 用户中文名称 --> <dzdaId></dzdaId><!--必填 业务唯一码 -->
//	 * <isAgree></isAgree><!—必填 审核是否通过--> <reason></reason><!—非必填 不通过的原因--> </Array>
//	 *
//	 * @param xml
//	 * @return
//	 * @throws Exception
//	 */
//	public static Map<String, String> callArrDataCheckWs(String xml) throws Exception {
//		String obj = CxfUtil.cxfWebservice(HTTP + "/dzda/ws/arrDataCheck?wsdl", "arrDataCheck", xml);
//		return Dom4jUtil.buildMap(obj);
//	}
//
//	public static Map<String, String> callUserCertificationWs(String xml) throws Exception {
//		String obj = CxfUtil.cxfWebservice(HTTP + "/dzda/ws/userCertification?wsdl", "userCertification", xml);
//		return Dom4jUtil.buildMap(obj);
//	}
}
