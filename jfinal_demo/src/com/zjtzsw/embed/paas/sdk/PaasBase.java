package com.zjtzsw.embed.paas.sdk;

/**
 * paas基类
 * 
 * @author yuanzp
 * @data 2017年3月6日 下午4:59:26
 */
public class PaasBase {

	public static final DataService DataService = new DataService();
	public static final MgrService MgrService = new MgrService();
	public static final BizService BizService = new BizService();

	protected enum DataServiceEnum {
		fetchFileUnit, 
		putFileUnit, 
		queryPerson, 
		queryOrganization, 
		queryBizItem, 
		updateBizItem, 
		searchFileUnit, 
		queryFileUnit, 
		queryFileUnitSet, 
		lookupDirectory, 
		spliceFileUnitSet, 
		tryIt
	}

	protected enum MgrServiceEnum {
		borrowApply, 
		borrowReturnApply, 
		consultApply, 
		outApply, 
		inApply, 
		bulkApply, 
		statusChangeApply,
		checkApply,
		acceptApply,
		tryIt
	}

	protected enum BizServiceEnum {
		polling, 
		queryBacklog,
		queryWorkTrail,
		tryIt
	}

	static boolean DataServiceEnumContains(String type) {
		for (DataServiceEnum typeEnum : DataServiceEnum.values()) {
			if (typeEnum.name().equals(type)) {
				return true;
			}
		}
		return false;
	}

	static boolean MgrServiceEnumContains(String type) {
		for (MgrServiceEnum typeEnum : MgrServiceEnum.values()) {
			if (typeEnum.name().equals(type)) {
				return true;
			}
		}
		return false;
	}
}
