/**
 * CHighshoot.js
 * 
 * 与高拍客户端配套使用的工具js
 * 浙江天正思维信息技术有限公司版权所有
 * 
 * @date 2017/01/22
 * @version 1.0
 */

;(function() {
	CHighshoot = {
		params : {
			'serverName' : window.location.host.split(":")[0],
			'serverPort' : window.location.host.split(":")[1] || '80',
			'reqUrl' : null,
			//'baseFileName' : null,
			'cookie' : document.cookie
		},
		afterClosed : function() {},
		
		/* 初始化高拍 */
		init : function(_config) {
			var config = _config || {};
			this.url = 'http://' + (config.host||'127.0.0.1') + ':' + (config.port||'20000') + '/';
			
			this.available = false;
			this.isAvailable();
			
			return this;
		},
		
		/* 设置提交的参数 */
		setParams : function(_params) {
			for (var key in _params) {
				if (typeof(this.params[key]) != 'undefined') {
					this.params[key] = _params[key];
				}
			}
			
			return this;
		},

		/* 设置高拍客户端关闭后回调方法 */
		setAfterClosed : function(_afterClosed) {
			this.afterClosed = _afterClosed;
			
			return this;
		},

		requestUrl : function(method) {
			return this.url + method;
		},

		/* 检测高拍服务是否可用 */
		isAvailable : function() {
			$.getJSON(this.requestUrl('index')+'?callback=?', function() {
				CHighshoot.available = true;
		    });
			
			return this;
		},

		/* 启动高拍客户端 */
		start : function() {
			if (this.available) {
				var url = this.requestUrl('highshoot') + '?_t=' + (new Date().getTime());
				for (var e in this.params) {
					url += '&' + e + '=' + encodeURIComponent(this.params[e]);
				}
				var iframe = document.createElement("iframe");
				var onload = function() {
					CHighshoot.afterClosed();
					document.body.removeChild(iframe);
				}
				
				iframe.style.display = 'none';
				iframe.src = url;
				if (iframe.attachEvent) {
					iframe.attachEvent("onload", onload);
				} else {
					iframe.onload = onload;
				}
				document.body.appendChild(iframe); 
			} else {
				alert('高拍服务不可用！');
			}
		}
	};
	
	$(function() {
		CHighshoot.init();
	});
})()
