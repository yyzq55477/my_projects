/**
 * 供业务系统嵌入页面调用，可以生成ICV控件
 * 此脚本不依赖任何第三方脚本库
 *
 * 当前版本只支持一个页面一个icv实例，基本够用了
 *
 * @author wangs, lish, jiangcy
 */

(function() {
	var icv = {
			bizCode : null,
			bizId : null,
			fuId  : null,
			falg  : true,
			status : 'Init',
			config : {appcontext : '', nodeBranch : '', accordance : ''},
			complete : null
	};
	// 网上申报 accordance=1  嵌入端 内网nodeBranch=x
	// 配置icv的属性，详细的属性在此文件中不需要解析
	icv.setupConfig = function(config) {
		if (config == null) return;
		for (var key in config) {
			this.config[key] = config[key];
		}
		return icv;
	};

	icv.setDealer = function(code) {
		icv.config.dealer = code;
	};

	icv.setHandOverList2 = function(config){
		  try {
				var data = {
						handOverListNo: config.params1,
						refIds: config.params2
				};
				var xhr = new XMLHttpRequest();
				var url= this.config.appcontext + '/filext-api?method=setHandOverList&_t=' + (new Date().getTime());
				url = url + '&' + encodeURI(decodeLM(data));
				var result = null;
				xhr.open('POST', url,false);
				//设置回调函数
				xhr.onreadystatechange = function () {
				    if (xhr.readyState == 4 && xhr.status == 200) {
				    	 var s=xhr.responseText;
				    	 result=eval('(' + s + ')');
				    	 return;
				    } else  if (xhr.readyState == 4) {
				    	alert('调用setHandOverList2出错 ' + xhr.status);
				    }
				}
				xhr.send(null);
				return result;
				} catch(ee) {
					alert('调用setHandOverList2出错 ');
					alert('详细错误:'+ee.message);
					alert(ee.stack);
					return null;
				}
	};


	// 将icv-ct.html作为iframe载入
	icv.render = function(id, bizCode, bizId, fuId, jgCode, params) {
		var ctDiv = document.getElementById(id);
		if (ctDiv == null) throw Error("容器对象不存在,id=" + id);
		if (params==null || params.autoAdjustHeight || typeof(params.autoAdjustHeight)=='undefined')
			ctDiv.style.height = (document.body.clientHeight-30)+'px';

		icv.divId = id;
		icv.bizCode = bizCode;
		icv.bizId = bizId;
		icv.fuId = fuId;
		icv.config.params = params || {};
		if(jgCode!=null && jgCode!='' && typeof(jgCode)!="undefined"){
			alert("jgCode");
			var xhr = new XMLHttpRequest();
			var url= this.config.appcontext +'/filext-api?method=getInitialState&jgCode='+jgCode+'&bizCode='+bizCode;
			xhr.open('POST', url, false);
			//设置回调函数
			xhr.onreadystatechange = function () {
			    if (xhr.readyState == 4 && xhr.status == 200) {
			    	var s=xhr.responseText;
			    	var ss=eval('(' + s + ')');
			    	icv.falg= ss.data.initialState;
			    	return;
			    }
			}
			xhr.send(null);
		}

		if(!icv.falg){
			document.getElementById(id).style.display="none";
			icv.requiredValidate = function() {return true;};
			icv.submit = function() {};
			icv.getMaterialDetail = function() {};
			icv.setHandOverList = function() {};
			icv.sentMaterial = function() {};
			icv.checkMaterial = function() {};
			icv.returnApplication = function() {};
			icv.updatePicture = function() {};
			icv.updateFileUnitDataTpye = function() {};
			return;
		}
		var cturl = this.config.appcontext + '/filext-embed/icv-ct.html?v1.11';
		
		ctDiv.innerHTML = '<iframe name="iframe" src="' + cturl + '" width="100%" height="100%" frameborder="0" scrolling="no"></iframe>';
	};

	// @Deprecated
	// Use render or renderMode instead
	icv.renderBind = function(id, bizCode, params, hostId) {
		icv.hostId = hostId;

		icv.render(id, bizCode, null, null, null, params);
	}

	// 带模式的render方法
	icv.renderMode = function(id, mode, params) {
		var ctDiv = document.getElementById(id);
		if (ctDiv == null) throw Error("容器对象不存在,id=" + id);
		if (params==null || params.autoAdjustHeight || typeof(params.autoAdjustHeight)=='undefined')
			ctDiv.style.height = (document.body.clientHeight-30)+'px';

		icv.divId = id;
		icv.bizCode = params.bizCode;
		icv.bizId = params.bizId;
		icv.fuId = params.fuId;

		icv.config.mode = mode;
		icv.config.params = params || {};

		if(!icv.falg) {
			document.getElementById(id).style.display="none";
			icv.requiredValidate = function() {return true;};
			icv.submit = function() {};
			icv.getMaterialDetail = function() {};
			icv.setHandOverList = function() {};
			icv.sentMaterial = function() {};
			icv.checkMaterial = function() {};
			icv.returnApplication = function() {};
			icv.updatePicture = function() {};
			icv.updateFileUnitDataTpye = function() {};
			return;
		}
		var cturl = this.config.appcontext + '/filext-embed/icv-ct.html?v1.11';

		ctDiv.innerHTML = '<iframe name="iframe" src="' + cturl + '" width="100%" height="100%" frameborder="0" scrolling="no"></iframe>';
	}

	icv.viewBigImage = function(url){
		var div = document.createElement("div");
		//div.style = 'position:absolute;top:0;left:0;right:0;bottom:0;z-index:99999;background:#333;text-align:center;';
		/*为了兼容google*/
		div.style.position = 'absolute';
		div.style.top = '0';
		div.style.left = '0';
		div.style.right = '0';
		div.style.bottom = '0';
		div.style.zIndex = '99999';
		div.style.background = '#333';
		div.style.textAlign = 'center';
		div.className = 'view-big-container';

		var table = document.createElement("table");
		//table.style = 'width:100%;height: 100%;';
		table.style.width = '100%';
		table.style.height = '100%';

		var tr = document.createElement("tr");
		var td = document.createElement("td");
		td.style.verticalAlign = 'middle';
		tr.appendChild(td);
		table.appendChild(tr);

		var img = document.createElement('img');
		img.src = url;

		var close = document.createElement('div');
		/*close.style = 'width:20px;height:20px;background:#f00;position:absolute;top:10px;right:10px;cursor:default;
		border-radius:20px;text-align:center;line-height:20px;color:#fff;';*/
		close.style.width = '30px';
		close.style.height = '30px';
		close.style.background = '#f00';
		close.style.position = 'fixed';
		close.style.top = '10px';
		close.style.right = '10px';
		close.style.cursor = 'pointer';
		close.style.borderRadius = '30px';
		close.style.textAlign = 'center';
		close.style.lineHeight = '30px';
		close.style.color = '#fff';
		close.style.fontSize = '20px';
		close.style.zIndex = '9999999';

		close.innerHTML = '×';

		td.appendChild(img);
		div.appendChild(table);
		div.appendChild(close);
		document.body.appendChild(div);

		if(close.addEventListener){
			close.addEventListener('click',function(){
				document.body.removeChild(div);
			},false);
		}else{
			close.attachEvent("onclick",function(){
				document.body.removeChild(div);
			});
		}
	}
	/*jiangcy pdf全屏显示 需在调用处启用media插件*/
	icv.viewBigPdf = function(url){
		var div = window.parent.document.createElement("div");
		//div.style = 'position:absolute;top:0;left:0;right:0;bottom:0;z-index:99999;background:#333;text-align:center;';
		/*为了兼容google*/
		div.style.position = 'fixed';
		div.style.top = '0';
		div.style.left = '0';
		div.style.right = '0';
		div.style.bottom = '0';
		div.style.zIndex = '99999';
		div.style.background = '#333';
		div.style.textAlign = 'center';
		div.className = 'view-big-container';

		var a = document.createElement("a");
			a.className="media";
			a.href=url;

		var close = document.createElement('div');
		/*close.style = 'width:20px;height:20px;background:#f00;position:absolute;top:10px;right:10px;cursor:default;
		border-radius:20px;text-align:center;line-height:20px;color:#fff;';*/
		close.style.width = '30px';
		close.style.height = '30px';
		close.style.background = '#f00';
		close.style.position = 'absolute';
		close.style.top = '10px';
		close.style.right = '10px';
		close.style.cursor = 'pointer';
		close.style.borderRadius = '30px';
		close.style.textAlign = 'center';
		close.style.lineHeight = '30px';
		close.style.color = '#fff';
		close.style.fontSize = '20px';
		close.style.zIndex = '9999999';

		close.innerHTML = '×';

		div.appendChild(a);
		div.appendChild(close);
		document.body.appendChild(div);

		if(close.addEventListener){
			close.addEventListener('click',function(){
				document.body.removeChild(div);
			},false);
		}else{
			close.attachEvent("onclick",function(){
				document.body.removeChild(div);
			});
		}
	}
	// iframe载入完成后，调用
	icv.ctloaded = function(proxyCt) {
		if (proxyCt == null) throw Error("未知错误");

		this._proxy = proxyCt;
		this.status = 'Complete';

		//  生成代理方法
		delegateFactory(this, "setHandOverList","getMaterialDetail","getIcvConfig", "getIcv", "submit", "requiredValidate",
				"sentMaterial", "checkMaterial", "returnApplication", "getInitialState", "updatePicture", "updateFileUnitDataTpye",
				"createAndSaveFileUnit", "removeArchive", "copyArchive", "modifyEffective", "modifyRequired", "getCheckedPics",
				"addCategory", "updateCateView", "moveToMe", "viewCount", "buildPdf","submitBatch");
	}

	// 调用icv-impl中的方法前，需要检测状态
	function checkState(icv) {
		if (icv.status != 'Complete') return false;
		if (icv._proxy == null) return;
		return true;
	}

	// 产生delegate方法
	function delegateFactory(icv) {
		if (icv == null) return;
		for (var i = 0; i < arguments.length; i++) {
			if (i == 0) continue;
			var fn = icv._proxy[arguments[i]];
			if (fn == null || typeof(fn) != 'function') continue;

			addDelegateMethod(icv, arguments[i], fn);
		}
	}

	function addDelegateMethod(icv, name, fn) {
		icv[name] = function() {
			if (checkState(icv)) return fn.apply(icv, arguments);
		};
	}

	function decodeLM(data) {
		var _data = [];
		for (var key in data) {
			_data.push(key + '=' + data[key]);
		}
		return _data.join('&');
	}

	window.icv == null ? window.icv = icv : window._icv = icv; // 如果_icv也被占用，那业务系统自己解决一下吧

	window.onunload = function() {
		for (var e in icv) {
			delete icv[e];
			icv[e] = null;
		}
		icv = null;
	}
})();
