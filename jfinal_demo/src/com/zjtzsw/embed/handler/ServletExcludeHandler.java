package com.zjtzsw.embed.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.handler.Handler;

public class ServletExcludeHandler extends Handler {

	@Override
	public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
		// TODO Auto-generated method stub
		if(!target.equals("/filext-api")){
			next.handle(target, request, response, isHandled);
		}
	}

}
