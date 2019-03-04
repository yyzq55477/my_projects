package com.zjtzsw.embed.paas.sdk;

import java.io.Serializable;

/**
 * 接口平台所涉及到的实体
 * 
 * @author yuanzp
 * @data 2017年3月6日 上午10:47:20
 */
public class PaasEntity {

	/**
	 * 初始化人员实体
	 * 
	 * @param aac002
	 *            社会保障号码
	 * @param aac003
	 *            姓名
	 * @return
	 */
	public Person getPerson(String aac002, String aac003) {
		return new Person(aac002, aac003);
	}

	/**
	 * 初始化单位实体
	 * 
	 * @param aab003
	 *            组织机构代码
	 * @param aab004
	 *            单位名称
	 * @param bab010
	 *            Bab010
	 * @return
	 */
	public Organization getOrganization(String aab003, String aab004, String bab010) {
		return new Organization(aab003, aab004, bab010);
	}

	/**
	 * 初始化办件实体
	 * 
	 * @param aax001
	 *            办件的外部系统唯一码
	 * @param aax003
	 *            事项编码
	 * @param aax006
	 *            办件名称
	 * @param xxx100
	 *            人员唯一码
	 * @param xxx200
	 *            单位唯一码
	 * @return
	 */
	public BizItem getBizItem(String aax001, String aax003, String aax006, String xxx100, String xxx200) {
		return new BizItem(aax001, aax003, aax006, xxx100, xxx200);
	}

	/**
	 * 人员信息指标集实体
	 * 
	 * @author yuanzp
	 * @data 2017年3月6日 上午10:08:50
	 */
	public class Person implements Serializable {

		private Person(String aac002, String aac003) {
			this.setAac002(aac002);
			this.setAac003(aac003);
		}

		private static final long serialVersionUID = 1L;

		private String Aac002;// 社会保障号码
		private String Aac003;// 姓名
		private String Xxx001;// 系统唯一码

		public String getAac002() {
			return Aac002;
		}

		public void setAac002(String aac002) {
			Aac002 = aac002;
		}

		public String getAac003() {
			return Aac003;
		}

		public void setAac003(String aac003) {
			Aac003 = aac003;
		}

		public String getXxx001() {
			return Xxx001;
		}

		public void setXxx001(String xxx001) {
			Xxx001 = xxx001;
		}
	}

	/**
	 * 办件信息指标集实体
	 * 
	 * @author yuanzp
	 * @data 2017年3月6日 上午10:08:41
	 */
	public class BizItem implements Serializable {

		private BizItem(String aax001, String aax003, String aax006, String xxx100, String xxx200) {
			this.setAax001(aax001);
			this.setAax003(aax003);
			this.setAax006(aax006);
			this.setXxx100(xxx100);
			this.setXxx200(xxx200);
		}

		private static final long serialVersionUID = 1L;

		private String Xxx001;// 系统唯一码
		private String Aax001;// 办件的外部系统唯一码
		private String Aax003;// 事项编码
		private String Aax004;// 事项版本号
		private String Aax005;// 事项名称
		private String Aax006;// 办件名称
		private String Aax010;// 办件类型
		private String Aax011;// 业务类型
		private String Aax012;// 处理单位名称
		private String Aax013;// 处理地区代码
		private String Aax014;// 处理部门名称
		private String Aax015;// 处理人员名称
		private String Xxx100;// 人员唯一码
		private String Xxx200;// 单位唯一码

		public String getXxx001() {
			return Xxx001;
		}

		public void setXxx001(String xxx001) {
			Xxx001 = xxx001;
		}

		public String getAax001() {
			return Aax001;
		}

		public void setAax001(String aax001) {
			Aax001 = aax001;
		}

		public String getAax003() {
			return Aax003;
		}

		public void setAax003(String aax003) {
			Aax003 = aax003;
		}

		public String getAax004() {
			return Aax004;
		}

		public void setAax004(String aax004) {
			Aax004 = aax004;
		}

		public String getAax005() {
			return Aax005;
		}

		public void setAax005(String aax005) {
			Aax005 = aax005;
		}

		public String getAax006() {
			return Aax006;
		}

		public void setAax006(String aax006) {
			Aax006 = aax006;
		}

		public String getAax010() {
			return Aax010;
		}

		public void setAax010(String aax010) {
			Aax010 = aax010;
		}

		public String getAax011() {
			return Aax011;
		}

		public void setAax011(String aax011) {
			Aax011 = aax011;
		}

		public String getAax012() {
			return Aax012;
		}

		public void setAax012(String aax012) {
			Aax012 = aax012;
		}

		public String getAax013() {
			return Aax013;
		}

		public void setAax013(String aax013) {
			Aax013 = aax013;
		}

		public String getAax014() {
			return Aax014;
		}

		public void setAax014(String aax014) {
			Aax014 = aax014;
		}

		public String getAax015() {
			return Aax015;
		}

		public void setAax015(String aax015) {
			Aax015 = aax015;
		}

		public String getXxx100() {
			return Xxx100;
		}

		public void setXxx100(String xxx100) {
			Xxx100 = xxx100;
		}

		public String getXxx200() {
			return Xxx200;
		}

		public void setXxx200(String xxx200) {
			Xxx200 = xxx200;
		}

	}

	/**
	 * 单位信息指标集实体
	 * 
	 * @author yuanzp
	 * @data 2017年3月6日 上午10:09:36
	 */
	public class Organization implements Serializable {

		private Organization(String aab003, String aab004, String bab010) {
			this.setAab003(aab003);
			this.setAab004(aab004);
			this.setBab010(bab010);
		}

		private static final long serialVersionUID = 1L;

		private String Aab003;// 组织机构代码
		private String Aab004;// 单位名称
		private String Bab010;// 统一社会信用代码
		private String Xxx001;// 系统唯一码

		public String getAab003() {
			return Aab003;
		}

		public void setAab003(String aab003) {
			Aab003 = aab003;
		}

		public String getAab004() {
			return Aab004;
		}

		public void setAab004(String aab004) {
			Aab004 = aab004;
		}

		public String getBab010() {
			return Bab010;
		}

		public void setBab010(String bab010) {
			Bab010 = bab010;
		}

		public String getXxx001() {
			return Xxx001;
		}

		public void setXxx001(String xxx001) {
			Xxx001 = xxx001;
		}
	}
}
