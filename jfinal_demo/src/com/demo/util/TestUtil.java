package com.demo.util;

import java.util.UUID;

public class TestUtil {

	/**
	 * 生成组标识
	 * @return
	 */
	protected static String getUUID() {
		return UUID.randomUUID().toString();
	}
	
	/**
	 * 生成组标识
	 * @return
	 */
	protected static String getUUID16() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	public static void main(String[] args) {
//		for (int i = 0;i<5;i++) {
//			System.out.println(getUUID16());
//		}
		System.out.println(3960 % 360);
	}
}
