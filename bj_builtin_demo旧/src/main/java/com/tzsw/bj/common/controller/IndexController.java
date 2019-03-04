package com.tzsw.bj.common.controller;

import java.util.Map;

import com.tzsw.jfinal.core.BaseController;
import com.tzsw.jfinal.ext.annotation.ControllerBind;
import com.zjtzsw.embed.ProxyImpl;

/**
 * index controller
 *
 * @author yuanzp
 * @date 2018年4月23日 下午4:58:20
 */
@ControllerBind(controllerKey = "/")
public class IndexController extends BaseController implements ProxyImpl{


	public void index() {
		render("index.html");
	}

	@Override
	public boolean initValidator(Map<String, String> params) {
		System.out.println("登录校验");
		return true;
	}
}
