/**
 * filext SDK js版 基于 jQuery
 * 
 * @author: lsh, huenb
 */
var _timestamp = -1;
function FilextApi(conf) {
	this.config = conf || {};
	this.uuid = null;
	// 舟山电子档案外网nodeX=self 舟山电子档案内网 nodeX=self1
	this.nodeX = 'self1';
	/*this.WEB_APP = this.config.config.appcontext == null ? ""
			: this.config.config.appcontext;*/
	this.WEB_APP = this.config.config.serviceUrl == null ?  this.config.config.appcontext : this.config.config.serviceUrl;

	this.fileSizeLimit = '500MB';
    this.operaLog = {};
    
    // Base64加密
    this.encode64 = function(input) {
    	var keyStr = "ABCDEFGHIJKLMNOP" + "QRSTUVWXYZabcdef" + "ghijklmnopqrstuv" + "wxyz0123456789+/" + "=";
    	var output = "";
        var chr1, chr2, chr3 = "";
        var enc1, enc2, enc3, enc4 = "";
        var i = 0;
        do {
        	chr1 = input.charCodeAt(i++);
            chr2 = input.charCodeAt(i++);
            chr3 = input.charCodeAt(i++);
            enc1 = chr1 >> 2;
            enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
            enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
            enc4 = chr3 & 63;
            if (isNaN(chr2)) {
                    enc3 = enc4 = 64;
            } else if (isNaN(chr3)) {
                    enc4 = 64;
            }
            output = output + keyStr.charAt(enc1) + keyStr.charAt(enc2)
                            + keyStr.charAt(enc3) + keyStr.charAt(enc4);
            chr1 = chr2 = chr3 = "";
            enc1 = enc2 = enc3 = enc4 = "";
        } while (i < input.length);
        return output;
    }
    
    // 提交数据绑定timestamp后加密返回
    this.bindTime = function(ajaxData) {
    	ajaxData += "&timestamp="+_timestamp;
    	return this.encode64(ajaxData);
    }
    
	this.commonCall = function(setting) {
		var setting = setting || {};
		var rData;

		var data = setting.data;
		data.mode = this.config.config.mode;
		data = this.encode(data);// 前端统一转码
		var privilege = this.config.config.privilege || {};
		data._dealerCode = privilege.dealerCode || '';
		data._dealerName = privilege.dealerName || '';
		data._dealerTitle = privilege.dealerTitle || '';

		// if(data.node) data.node = this.getNode(data.node);

		var ajaxData = $.param(data || {});
		
		if(ajaxData.indexOf("initFileUnit") != -1) {
			var params = "method=initTimestamp";
			var initData = this.config.config.params.initData;
			if(initData != null) {
				$.each(initData, function (k, v) {
					params += ("&"+k+"="+v);
				});
			}
			var initData = this.encode64(params);
			$.ajax({
				url : this.WEB_APP + '/filext-api',
				data : {data: initData}, 
				cache : false,
				type : "post",
				async : false,
				success : function(r) {
					rData = r;
					_timestamp = rData.data.timestamp;
					setInterval(function(){_timestamp++}, 1000); 
				},
				error : function(jqHXR, error) {
					throw new Error(error);
				}
			});
		}
		
		ajaxData = this.bindTime(ajaxData);
		
		$.ajax({
			url : this.WEB_APP + '/filext-api',
			data : {data: ajaxData}, 
			cache : false,
			type : "post",
			async : false,
			success : function(r) {
				rData = r;
			},
			error : function(jqHXR, error) {
				throw new Error(error);
			}
		});
		return rData;
	};

	this.encode = function(data) {
		for ( var o in data) {
			var so = String(data[o]);
			if (so == 'undefined' || so == 'null')
				data[o] = '';
			try {
				var s = JSON.parse(JSON.stringify(data[o]));
				s = eval('(' + s + ')');
				data[o] = encode(s);
			} catch (e) {
				data[o] = encodeURIComponent(data[o])
			}
		}
		return data;
	};

	this.getNode = function(node) {
		var nodeBranch = this.config.config.nodeBranch;
		if (nodeBranch) {
			node = node + "-" + nodeBranch;
		}
		return node;
	};

	this.getNodeX = function(node) {
		var nodeBranch = this.config.config.nodeBranch;
		if (this.nodeX == 'self') {
			node = node + "-x";
		} else {
			if (nodeBranch) {
				node = node + "-" + nodeBranch;
			}
		}
		/*
		 * if(node.indexOf('-x')==-1){ node = node + '-x'; }
		 */
		return node;
	};

	this.getWatermark = function() {
		return this.watermark;
	};

	this.getFilename = function(path) {
		if (path == null)
			return null; // 返回null，表示获取错误

		var pp = path.split(/(\\|\/)/);
		if (pp.length > 0)
			return this.splitFilename(pp[pp.length - 1]);

		return null;
	};

	this.splitFilename = function(filename) {
		if (filename == null)
			return null;

		var ff = filename.split('.');
		if (ff.length == 1)
			return [ filename, ff[0], '' ];
		if (ff.length > 1)
			return [ filename, ff[0], ff[ff.length - 1] ];

		return null;
	};
	
	this.watermark = this.initWatermark(this.config.config.watermarkCode);
	
	
}

/* 获取初始化数据 */
FilextApi.prototype.getInitData = function(falg) {

	var me = this;
	var res = this.commonCall({
		data : {
			method : 'initFileUnit',
			appContext : me.WEB_APP,
			bizCode : me.config.bizCode,
			bizId : me.config.bizId,
			uuid : me.config.fuId || this.uuid,
			lockId : me.config.lockId,
			hostId : me.config.hostId,
			// nodeBranch : me.config.config.nodeBranch,
			// accordance : me.config.config.accordance,
			temporary : me.config.config.privilege.temporary,
			disableOperate : me.config.config.privilege.disableOperate,
			disableHistory : me.config.config.privilege.disableHistory,
			
			subjectType : me.config.config.params.subjectType,
			subjectKey : me.config.config.params.subjectKey,
			subjectName : me.config.config.params.subjectName,
			
			referSubjectType : me.config.config.params.referSubjectType,
			referSubjectKey : me.config.config.params.referSubjectKey,
			referSubjectName : me.config.config.params.referSubjectName,
			
			dealerId : me.config.config.dealerId,
			dealerCode : me.config.config.params.dealerCode,
			dealerName : me.config.config.params.dealerName,
			dealerOrgCode : me.config.config.params.dealerOrgCode,
			dealerOrgName : me.config.config.params.dealerOrgName,
			
			borrowStatus : me.config.config.params.borrowStatus,
			borrowId : me.config.config.params.borrowId,
			batch : me.config.config.batch,
			
			falg : falg,
			stage : me.config.config.params.stage
		}
	});
	if (res.success) {
		this.uuid = res.data.uuid;
		parent.icv.fuId = this.uuid;
		if (res.data != null && res.data.oper != null && res.data.oper == '1') {
			parent.icv.config.privilege.oper = false;
		} else {
			parent.icv.config.privilege.oper = true;
		}
		this.config.bizTitle = res.data.bizTitle;
		return res.data;
	}
	// alert(res.message);
	return null;
}


/* 提交图片 */
FilextApi.prototype.submitPictures = function(config) {
	return this.commonCall({
		data : {
			method : 'submitPictures',
			uuid : config.uuid,
			uuids : config.uuids,
			lockId : config.lockId
		}
	});
}
/* 获取下层数据 */
FilextApi.prototype.getSubData = function(uuid,lockId) {
	var me = this;
	return this.commonCall({
		data : {
			appContext : this.WEB_APP,
			method : 'queryStorageObjects',
			uuid : uuid,
			lockId : lockId,
			fuid : this.uuid,
			batch : me.config.config.batch,
			nodeBranch : me.config.config.nodeBranch,
			temporary : me.config.config.privilege.temporary
		}
	});
}
/* 获取水印文字 */
FilextApi.prototype.initWatermark = function(watermarkCode) {
	if (watermarkCode == null)
		return null;
	var me = this;
	var config = this.config.config || {};

	var res = this.commonCall({
		data : {
			method : 'wrapWatermark',
			bname : config.bname,
			watermarkCode : watermarkCode
		}
	});
	if (res.data && res.data.watermark)
		return res.data.watermark;
	else
		return watermarkCode;
}
/* 否是初始化 */
FilextApi.prototype.getInitialState = function(jgCode) {
	if (jgCode == null || jgCode == '')
		return true;
	var res = this.commonCall({
		data : {
			method : 'getInitialState',
			jgCode : jgCode
		}
	});
	if (res.data && res.data.initialState)
		return res.data.initialState;
	return true;
}
/* 图片处理请求 */
FilextApi.prototype.doImageProcess = function(type, config) {
	config.method = 'process';
	config.methodExt = type;
	config.node = this.getNodeX(config.node);
	if (this.getWatermark() != null) {
		config.watermark = this.getWatermark();
	}
	var res = this.commonCall({
		data : config
	});
	return res;
}

/* 裁剪 */
FilextApi.prototype.doCrop = function(config) {
	return this.doImageProcess('CROP', config);
};

/* 旋转 */
FilextApi.prototype.doRotate = function(config) {
	return this.doImageProcess('ROTATE', config);
};

/* 缩放 */
FilextApi.prototype.doResize = function(config) {
	return this.doImageProcess('RESIZE', config);
};

/* 纠偏 */
FilextApi.prototype.doCorrect = function(config) {
	return this.doImageProcess('CORRECT', config);
};

/* 保存操作 */
FilextApi.prototype.doSave = function(config) {
	config.method = 'apply';
	if (this.getWatermark() != null) {
		config.watermark = this.getWatermark();
	}
	config.node = this.getNodeX(config.node);
	var res = this.commonCall({
		data : config
	});
	if (res.success) {
		res = this.updateFile({
			uuid : res.data.uuid, // （config.uuid）TODO modified by Lintel
			title : config.title,
			filesize : res.data.fileSize
		});
	}
	return res;
};

/* 上传前请求 */
FilextApi.prototype.requestBeforeUpload = function(config) {
	var file = this.getFilename(config.filename);
	var filename = file[0];
	var title = file[1];
	var uuidExt = file[2];
	var highShootType = config.highShootType;
	if (highShootType = "undefined") {
		highShootType = 1;//本地上传
	}
	var me = this;
	var res = this.commonCall({
		data : {
			method : 'createStorageObject',
			uuid : config.uuid,
			lockId : config.lockId,
			uuidExt : uuidExt,
			filename : filename,
			filesize : config.filesize,
			title : title,
			dealer : me.config.config.dealer,
			batch : me.config.config.batch,
			temporary : me.config.config.privilege.temporary,
			highShootType : highShootType
		}
	});

	if (!res.success) {
		showTip(res.message);
	}
	var rs = {
		node : this.getNodeX(res.data.node),
		uuid : res.data.uuid,
		uuidExt : uuidExt,
		method : 'putStorageContent',
		dealer : this.config.config.dealer
	};

	return rs;
};
/* 本地上传 */
FilextApi.prototype.doUpload = function(config, callback) {
	var me = this;
	var errorMsg = [];
	
	$('#native-file')
			.uploadify(
					{
						'swf' : 'lib/uploadify/uploadify.swf',
						'uploader' : me.WEB_APP + '/filext-api',
						'buttonText' : '浏览...',
						'auto' : true,
						'removeTimeout' : 0,
						'fileSizeLimit' : me.fileSizeLimit,
						'fileTypeExts' : config.materialFormat
								|| '*.gif; *.jpg; *.png; *.jpeg; *.tif; *.bmp; *.doc; *.docx; *.ppt; *.pptx; *.xls; *.xlsx; *.txt; *.pdf; *.mp3; *.wav; *.swf;*.avi; *.mp4; *.rar; *.zip',
						'onSelectError' : function(file, errorCode, errorMsg) {
							$('.fileinput-upload').removeAttr('disabled');
							$('.back-to-default').removeAttr('disabled');
							if (errorCode == -130) {
								this.queueData.errorMsg = '文件类型不支持';
							}
							if (errorCode == -110) {
								this.queueData.errorMsg = '文件大小不能超过'
										+ me.fileSizeLimit;
							}
						},
						'onUploadStart' : function(file) {
							$('#' + file.id)
									.append(
											'<span class="tip" style="color:#f00">处理中...</span>');
							$('.fileinput-upload').attr('disabled', 'disabled');
							$('.back-to-default').attr('disabled', 'disabled');

							var data = me.requestBeforeUpload({
								uuid : config.uuid,
								filename : file.name,
								filesize : file.size
								
							});
							if (me.getWatermark() != null) {
								data.watermark = me.getWatermark();
								me.commonCall({
									data : {
										method : 'hasWatermark',
										uuid : data.uuid
									}
								});
							}
							data.local = true;
							/*var url = me.WEB_APP + '/filext-api?'
									+ $.param(data || {});*/
							var url = me.WEB_APP + '/filext-api?data=' + me.bindTime($.param(data || {}));
							$('#native-file').uploadify('settings', 'uploader',
									url);
							$('#native-file').uploadify('settings', 'formData',
									data);
						},
						'onUploadComplete' : function(file) {
							$('#' + file.id).children('.tip').remove();
						},
						'onQueueComplete' : function(queueData) {
							$('.uploadify-queue-item').children('.tip')
									.remove();
							$('.fileinput-upload').removeAttr('disabled');
							$('.back-to-default').removeAttr('disabled');
							var length = errorMsg.length;
							showTip('您成功上传了'
									+ (queueData.uploadsSuccessful - length)
									+ '个文件');
							if (length > 0) {
								alert(errorMsg.join('\n'));
							}

							callback(true);
						},
						'onUploadSuccess' : function(file, data, response) {
							data = $.parseJSON(data);
							if (!data.success) {
								errorMsg.push(file.name + data.message);
							}
						}
					});
};
/* icv4的新上传方法 */
FilextApi.prototype.doUploadNew = function(config, callback) {
	var me = this;
	var errorMsg = [];
	var xx = $('#native-file');
	$('#native-file')
			.uploadify(
					{
						'swf' : 'lib/uploadify/uploadify.swf',
						'uploader' : me.WEB_APP + '/filext-api',
						'buttonText' : '本地上传',
						'buttonClass' : 'uploadify-newstyle',
						'auto' : true,
						'removeTimeout' : 0,
						'fileSizeLimit' : me.fileSizeLimit,
						'fileTypeExts' : config.materialFormat
								|| '*.gif; *.jpg; *.png; *.jpeg; *.tif; *.bmp; *.doc; *.docx; *.ppt; *.pptx; *.xls; *.xlsx; *.txt; *.pdf; *.mp3; *.wav; *.swf;*.avi; *.mp4; *.rar; *.zip',
						'onSelectError' : function(file, errorCode, errorMsg) {
							$('.fileinput-upload').removeAttr('disabled');
							$('.back-to-default').removeAttr('disabled');
							if (errorCode == -130) {
								this.queueData.errorMsg = '文件类型不支持';
							}
							if (errorCode == -110) {
								this.queueData.errorMsg = '文件大小不能超过'
										+ me.fileSizeLimit;
							}
						},
						'onUploadStart' : function(file) {
							$('#' + file.id)
									.append(
											'<span class="tip" style="color:#f00">处理中...</span>');
							$('.fileinput-upload').attr('disabled', 'disabled');
							$('.back-to-default').attr('disabled', 'disabled');

							var data = me.requestBeforeUpload({
								uuid : config.uuid,
								filename : file.name,
								filesize : file.size,
								lockId : config.currentLockId
							});
							if (me.getWatermark() != null) {
								data.watermark = me.getWatermark();
								me.commonCall({
									data : {
										method : 'hasWatermark',
										uuid : data.uuid
									}
								});
							}
							data.local = true;
							/*var url = me.WEB_APP + '/filext-api?'
									+ $.param(data || {});*/
							var url = me.WEB_APP + '/filext-api?data='
									+ me.bindTime($.param(data || {}));

							$('#native-file').uploadify('settings', 'uploader',
									url);
							$('#native-file').uploadify('settings', 'formData',
									data);
						},
						'onUploadComplete' : function(file) {
							$('#' + file.id).children('.tip').remove();
						},
						'onQueueComplete' : function(queueData) {
							$('.uploadify-queue-item').children('.tip')
									.remove();
							$('.fileinput-upload').removeAttr('disabled');
							$('.back-to-default').removeAttr('disabled');
							var length = errorMsg.length;
							showTip('您成功上传了'
									+ (queueData.uploadsSuccessful - length)
									+ '个文件');
							if (length > 0) {
								alert(errorMsg.join('\n'));
							}

							callback(true);
						},
						'onUploadSuccess' : function(file, data, response) {
							data = $.parseJSON(data);
							if (!data.success) {
								errorMsg.push(file.name + data.message);
							}
						}
					});
};

FilextApi.prototype.doUploadhighShoot = function(config, callback) {
	var me = this;
	var errorMsg = [];
	var xx = $('#upload-file');
	debugger;
	$('#upload-file')
			.uploadify(
					{
						'swf' : 'lib/uploadify/uploadify.swf',
						'uploader' : me.WEB_APP + '/filext-api',
						'buttonText' : '本地上传',
						/*'buttonClass' : 'uploadify-newstyle',*/
						'auto' : true,
						'removeTimeout' : 0,
						'fileSizeLimit' : me.fileSizeLimit,
						'fileTypeExts' : config.materialFormat
								|| '*.gif; *.jpg; *.png; *.jpeg; *.tif; *.bmp; *.doc; *.docx; *.ppt; *.pptx; *.xls; *.xlsx; *.txt; *.pdf; *.mp3; *.wav; *.swf;*.avi; *.mp4; *.rar; *.zip',
						'onSelectError' : function(file, errorCode, errorMsg) {
							$('.fileinput-upload').removeAttr('disabled');
							$('.back-to-default').removeAttr('disabled');
							if (errorCode == -130) {
								this.queueData.errorMsg = '文件类型不支持';
							}
							if (errorCode == -110) {
								this.queueData.errorMsg = '文件大小不能超过'
										+ me.fileSizeLimit;
							}
						},
						'onUploadStart' : function(file) {
							$('#' + file.id)
									.append(
											'<span class="tip" style="color:#f00">处理中...</span>');
							$('.fileinput-upload').attr('disabled', 'disabled');
							$('.back-to-default').attr('disabled', 'disabled');

							var data = me.requestBeforeUpload({
								uuid : config.uuid,
								filename : file.name,
								filesize : file.size,
								lockId : config.currentLockId
							});
							if (me.getWatermark() != null) {
								data.watermark = me.getWatermark();
								me.commonCall({
									data : {
										method : 'hasWatermark',
										uuid : data.uuid
									}
								});
							}
							data.local = true;
							/*var url = me.WEB_APP + '/filext-api?'
									+ $.param(data || {});*/
							
							var url = me.WEB_APP + '/filext-api?data='
									+ me.bindTime($.param(data || {}));

							$('#upload-file').uploadify('settings', 'uploader',
									url);
							$('#upload-file').uploadify('settings', 'formData',
									data);
						},
						'onUploadComplete' : function(file) {
							$('#' + file.id).children('.tip').remove();
						},
						'onQueueComplete' : function(queueData) {
							$('.uploadify-queue-item').children('.tip')
									.remove();
							$('.fileinput-upload').removeAttr('disabled');
							$('.back-to-default').removeAttr('disabled');
							var length = errorMsg.length;
							showTip('您成功上传了'
									+ (queueData.uploadsSuccessful - length)
									+ '个文件');
							if (length > 0) {
								alert(errorMsg.join('\n'));
							}

							callback(true);
						},
						'onUploadSuccess' : function(file, data, response) {
							data = $.parseJSON(data);
							if (!data.success) {
								errorMsg.push(file.name + data.message);
							}
						}
					});
};

/* icv4新的本地上传方法 */
FilextApi.prototype.doUploadNewMethod = function(config, callback) {
	var fileType = config.materialFormat;
	if (fileType == undefined || fileType == '') {
		fileType == '';
	}else {
		var fType = fileType.split('*.');
		var fileTypes = '';
		for(var i in fType){
			if (fType[i] != '') {
				fileTypes += fType[i].replace(';',',').replace(' ','');
			}
		}
	}
	var me = this;
	var errorMsg = [];
	var thumbnailWidth = 30; // 缩略图高度和宽度
	var thumbnailHeight = 30;
	var uploader = WebUploader
			.create({
				// 选完文件后，是否自动上传。
				auto : true,
				// swf文件路径
				swf : '/lemis/filext-embed/lib/webuploader/Uploader.swf',
				// 文件接收服务端。
				server : '',
				// 选择文件的按钮。可选。
				// 内部根据当前运行是创建，可能是input元素，也可能是flash.
				pick : '#native-file',
				runtimeOrder: 'html5,flash', 
				fileNumLimit : 4,
				// 只允许选择图片文件。
				accept : {
					extensions : fileTypes||'gif,jpg,jpeg,bmp,png,tif,doc,docx,xls,xlsx,ppt,pptx,txt,pdf,mp3,wav,swf,avi,mp4,rar,zip'
				},
				fileSizeLimit : 500*1024*1024,
				method : 'POST',
				duplicate : true
			});
	/**
	 * 上传文件全部进入等待队列
	 */
	uploader.on('filesQueued',function(file){
		if (file.length > 4) {
			uploader.reset();
		}
	});
	/**
	 * 开始上传
	 */
	uploader.on('uploadStart',function(file){
		var data = me.requestBeforeUpload({
			uuid : config.uuid,
			filename : file.name,
			filesize : file.size,
			lockId : config.lockId
		});
		if (me.getWatermark() != null) {
			data.watermark = me.getWatermark();
			me.commonCall({
				data : {
					method : 'hasWatermark',
					uuid : data.uuid
				}
			});
		}
		data.local = true;
		/*var url = me.WEB_APP + '/filext-api?' + $.param(data || {});*/
		var url = me.WEB_APP + '/filext-api?data=' + me.bindTime($.param(data || {}));
		uploader.options.server = url;
	})
	
	/**
	 * 上传中
	 */
	uploader.on('uploadProgress', function(file, percentage) {
		$(".progress").show();
		$(".text").html(percentage.toFixed(2)*100+"%");
		$(".percentage").css('width',percentage.toFixed(2)*100+"%");
	});
	
	/**
	 * 上传失败
	 */
	uploader.on("error",function(type){
		uploader.reset();
		if (type == "Q_TYPE_DENIED") {
            alert("上传文件格式错误");
        } else if (type == "Q_EXCEED_SIZE_LIMIT") {
        	alert("文件大小不能超过"+me.fileSizeLimit);
        }else if (type == "Q_EXCEED_NUM_LIMIT") {
        	alert("上传出错！每一次最多可以上传4个文件");
        }else {
        	alert("上传出错！请联系管理员！");
		}
		
	});
	// 文件上传成功，给item添加成功class, 用样式标记上传成功。
	uploader.on('uploadSuccess', function(file) {
		showTip("上传成功！");
	});

	// 文件上传失败，显示上传出错。
	uploader.on('uploadError', function(file) {
		showTip("上传失败！");
		uploader.reset();
	});

	// 完成上传完了，成功或者失败，先删除进度条。
	uploader.on('uploadComplete', function(file) {
		$('.progress').hide();
//		window.setTimeout(function(){
//			initPage(1);
//		},2000);
		callback(true);
	});
}


/* icv4新的本地上传方法 */
FilextApi.prototype.doUploadForHighshoot = function(config, callback) {
	var fileType = config.materialFormat;
	if (fileType == undefined || fileType == '') {
		fileType == '';
	}else {
		var fType = fileType.split('*.');
		var fileTypes = '';
		for(var i in fType){
			if (fType[i] != '') {
				fileTypes += fType[i].replace(';',',').replace(' ','');
			}
		}
	}
	var me = this;
	var errorMsg = [];
	var thumbnailWidth = 30; // 缩略图高度和宽度
	var thumbnailHeight = 30;
	var uploader = WebUploader
			.create({
				// 选完文件后，是否自动上传。
				auto : true,
				// swf文件路径
				swf : '/lemis/filext-embed/lib/webuploader/Uploader.swf',
				// 文件接收服务端。
				server : '',
				// 选择文件的按钮。可选。
				// 内部根据当前运行是创建，可能是input元素，也可能是flash.
				pick : '#upload-file',
				runtimeOrder: 'html5,flash', 
				fileNumLimit : 4,
				// 只允许选择图片文件。
				accept : {
					extensions : fileTypes||'gif,jpg,jpeg,bmp,png,tif,doc,docx,xls,xlsx,ppt,pptx,txt,pdf,mp3,wav,swf,avi,mp4,rar,zip'
				},
				fileSizeLimit : 500*1024*1024,
				method : 'POST',
				duplicate : true
			});
	/**
	 * 上传文件全部进入等待队列
	 */
	uploader.on('filesQueued',function(file){
		if (file.length > 4) {
			uploader.reset();
		}
	});
	/**
	 * 开始上传
	 */
	uploader.on('uploadStart',function(file){
		var data = me.requestBeforeUpload({
			uuid : config.uuid,
			filename : file.name,
			filesize : file.size,
			lockId : config.lockId
		});
		if (me.getWatermark() != null) {
			data.watermark = me.getWatermark();
			me.commonCall({
				data : {
					method : 'hasWatermark',
					uuid : data.uuid
				}
			});
		}
		data.local = true;
		/*var url = me.WEB_APP + '/filext-api?' + $.param(data || {});*/
		var url = me.WEB_APP + '/filext-api?data=' + me.bindTime($.param(data || {}));
		uploader.options.server = url;
	})
	
	/**
	 * 上传中
	 */
	uploader.on('uploadProgress', function(file, percentage) {
		$(".progress").show();
		$(".text").html(percentage.toFixed(2)*100+"%");
		$(".percentage").css('width',percentage.toFixed(2)*100+"%");
	});
	
	/**
	 * 上传失败
	 */
	uploader.on("error",function(type){
		uploader.reset();
		if (type == "Q_TYPE_DENIED") {
            alert("上传文件格式错误");
        } else if (type == "Q_EXCEED_SIZE_LIMIT") {
        	alert("文件大小不能超过"+me.fileSizeLimit);
        }else if (type == "Q_EXCEED_NUM_LIMIT") {
        	alert("上传出错！每一次最多可以上传4个文件");
        }else {
        	alert("上传出错！请联系管理员！");
		}
		
	});
	// 文件上传成功，给item添加成功class, 用样式标记上传成功。
	uploader.on('uploadSuccess', function(file) {
		showTip("上传成功！");
	});

	// 文件上传失败，显示上传出错。
	uploader.on('uploadError', function(file) {
		showTip("上传失败！");
		uploader.reset();
	});

	// 完成上传完了，成功或者失败，先删除进度条。
	uploader.on('uploadComplete', function(file) {
		$('.progress').hide();
//		window.setTimeout(function(){
//			initPage(1);
//		},2000);
		callback(true);
	});
}

FilextApi.prototype.replaceNewMethod = function(config, callback) {
	var me = this;
	var data = {
		node : this.getNodeX(config.node),
		uuid : config.uuid,
		method : 'putStorageContent',
		local : true,
		replace : true,
		dealer : this.config.config.dealer || ''
	};
	var thumbnailWidth = 30; // 缩略图高度和宽度
								// （单位是像素），当宽高度是0~1的时候，是按照百分比计算，具体可以看api文档
	var thumbnailHeight = 30;
		var uploader = WebUploader
			.create({
				// 选完文件后，是否自动上传。
				auto : true,
				// swf文件路径
				swf : 'lib/webuploader/Uploader.swf',
				// 文件接收服务端。
				server : '',
				runtimeOrder: 'html5,flash', 
				// 选择文件的按钮。可选。
				// 内部根据当前运行是创建，可能是input元素，也可能是flash.
				pick : '#native-file',
				// 只允许选择图片文件。
				accept : {
					extensions : 'gif,jpg,jpeg,bmp,png,tif,doc,docx,xls,xlsx,ppt,pptx,txt,pdf,mp3,wav,swf,avi,mp4,rar,zip'
				},
				fileNumLimit : 4,
				fileSizeLimit : 500*1024*1024,
				method : 'POST',
				duplicate : true
			});
		
		/**
		 * 上传文件全部进入等待队列
		 */
		uploader.on('filesQueued',function(file){
			if (file.length > 4) {
				uploader.reset();
			}
		});
	uploader.on('uploadStart',function(file){
		$('#' + file.id).append(
			'<span class="tip" style="color:#f00">处理中...</span>');
			$('.fileinput-upload').attr('disabled', 'disabled');
			$('.back-to-default').attr('disabled', 'disabled');
			
			var file = me.getFilename(file.name);
			data.uuidExt = file[2];
			if (me.getWatermark() != null) {
				data.watermark = me.getWatermark();
			}
			/*var url = me.WEB_APP + '/filext-api?'
					+ $.param(data || {});*/
			var url = me.WEB_APP + '/filext-api?data='
					+ me.bindTime($.param(data || {}));
			
			uploader.options.server = url;
	});
	uploader.on("error",function(type){
		if (type == "Q_TYPE_DENIED") {
            alert("替换文件格式错误");
        } else if (type == "Q_EXCEED_SIZE_LIMIT") {
        	alert("替换大小不能超过"+me.fileSizeLimit);
        }else if (type == "Q_EXCEED_NUM_LIMIT") {
        	alert("替换出错！每一次最多可以上传4个文件");
        }else {
        	alert("替换出错！请联系管理员");
        }
	});
	
	// 文件上传成功，给item添加成功class, 用样式标记上传成功。
	uploader.on('uploadSuccess', function(file, data, response) {
		var dataJson = data;
		if (dataJson.success) {
			var file = me.getFilename(file.name);
			var filesize_ = dataJson.data.fileSize;
			var res = me.commonCall({
				data : {
					method : 'updateStorageObject',
					uuid : dataJson.data.uuid, // （config.uuid）TODO
												// modified
												// by Lintel
					uuidExt : file[2],
					filename : file[0],
					title : file[1],
					filesize : filesize_,
					lockId : config.lockId
				}
			});
			showTip(res.success ? '替换成功' : '替换失败');
		} else {
			showTip('替换失败');
			alert(file.name + dataJson.message);
		}

		$('.uploadify-queue-item').children('.tip')
				.remove();
		$('.fileinput-upload').removeAttr('disabled');
		$('.back-to-default').removeAttr('disabled');
		callback(true);
	});

	// 文件上传失败，显示上传出错。
	uploader.on('uploadError', function(file) {
		showTip("替换失败！");
	});

	// 完成上传完了，成功或者失败，先删除进度条。
	uploader.on('uploadComplete', function(file) {
		$('.progress').hide();
		callback(true);
	});
}
/* 替换 */
FilextApi.prototype.replace = function(config, callback) {
	var me = this;
	var data = {
		node : this.getNodeX(config.node),
		uuid : config.uuid,
		method : 'putStorageContent',
		local : true,
		replace : true,
		dealer : this.config.config.dealer || ''
	};
	$('#native-file')
			.uploadify(
					{
						'swf' : 'lib/uploadify/uploadify.swf',
						'uploader' : me.WEB_APP + '/filext-api',
						'buttonText' : '浏览...',
						'auto' : true,
						'multi' : false,
						'formData' : data,
						'fileSizeLimit' : me.fileSizeLimit,
						'fileTypeExts' : config.materialFormat
								|| '*.gif; *.jpg; *.png; *.jpeg; *.tif; *.bmp; *.doc; *.docx; *.ppt; *.pptx; *.xls; *.xlsx; *.txt; *.pdf; *.mp3; *.wav; *.swf;*.avi; *.mp4; *.rar; *.zip',
						'onSelectError' : function(file, errorCode, errorMsg) {
							$('.fileinput-upload').removeAttr('disabled');
							$('.back-to-default').removeAttr('disabled');
							if (errorCode == -130) {
								this.queueData.errorMsg = '文件类型不支持';
							}
							if (errorCode == -110) {
								this.queueData.errorMsg = '文件大小不能超过'
										+ me.fileSizeLimit;
							}
						},
						'onUploadStart' : function(file) {
							$('#' + file.id)
									.append(
											'<span class="tip" style="color:#f00">处理中...</span>');
							$('.fileinput-upload').attr('disabled', 'disabled');
							$('.back-to-default').attr('disabled', 'disabled');

							var file = me.getFilename(file.name);
							data.uuidExt = file[2];
							if (me.getWatermark() != null) {
								data.watermark = me.getWatermark();
							}
							/*var url = me.WEB_APP + '/filext-api?'
									+ $.param(data || {});*/
							var url = me.WEB_APP = '/filext-api?data=' + me.bindTime($.param(data || {}));

							$('#native-file').uploadify('settings', 'uploader',
									url);
							$('#native-file').uploadify('settings', 'formData',
									data);
						},
						'onUploadSuccess' : function(file, data, response) {
							var dataJson = $.parseJSON(data);
							if (dataJson.success) {
								var file = me.getFilename(file.name);
								var filesize_ = dataJson.data.fileSize;
								var res = me.commonCall({
									data : {
										method : 'updateStorageObject',
										uuid : dataJson.data.uuid, // （config.uuid）TODO
																	// modified
																	// by Lintel
										uuidExt : file[2],
										filename : file[0],
										title : file[1],
										filesize : filesize_,
										lockId : config.lockId
									}
								});
								showTip(res.success ? '替换成功' : '替换失败');
							} else {
								showTip('替换失败');
								alert(file.name + dataJson.message);
							}

							$('.uploadify-queue-item').children('.tip')
									.remove();
							$('.fileinput-upload').removeAttr('disabled');
							$('.back-to-default').removeAttr('disabled');
							callback(true);
						}
					});
}
FilextApi.prototype.submit = function(config) {
	var me = this;
	var data = {
		method : 'saveFileUnit',
		uuid : me.uuid,
		bizId : config.bizId,
		hostId : config.hostId,
		bizCode : config.bizCode,

		userCode : config.userCode,
		userName : config.userName,
		departmentCode : config.departmentCode,
		departmentName : config.departmentName,

		isOther : config.isOther,

		dealerCode : config.dealerCode,
		dealerName : config.dealerName,
		dealerTitle : config.dealerTitle,

		dealerOrgCode : config.dealerOrgCode,
		dealerOrgName : config.dealerOrgName,
		dealerOrgTitle : config.dealerOrgTitle,

		subjectType : config.subjectType,
		subjectKey : config.subjectKey,
		subjectName : config.subjectName,

		borrowStatus : config.borrowStatus,
		lockId : config.lockId
	};

	this.commonCall({
		data : data
	});

	if (this.getWatermark() != null)
		return false;
	if (config.watermarkCode == null || config.watermarkCode == '')
		return false;

	this.watermark = this.initWatermark(config.watermarkCode);
	if (this.watermark == null)
		return false;

	var data = pidMap[this.uuid];
	var uuidSet = [];
	for (var i = 0, len = data.length; i < len; i++) {
		var subData = data[i].children;
		if (subData == null)
			return false;
		for (var j = 0, l = subData.length; j < l; j++) {
			var image = subData[j];
			if (image.hasWatermark == true)
				return false;
			var res = me.commonCall({
				data : {
					method : 'watermarkFile',
					uuid : image.uuid,
					node : image.node,
					watermark : me.watermark
				}
			});
			if (res.success)
				uuidSet.push(image.uuid);
		}
	}
	this.commonCall({
		data : {
			method : 'hasWatermark',
			uuid : uuidSet.join(',')
		}
	});
};
/* 获取材料详情 */
FilextApi.prototype.getMaterialDetail = function(config) {
	var me = this;
	var res = this.commonCall({
		data : {
			method : 'getMaterialDetail',
			bizId : config.bizId,
			bizCode : config.bizCode,
			lockId : config.lockId
		}
	});
	return res.data;
};
/* 设置交接单数据 */
FilextApi.prototype.setHandOverList = function(config) {
	var me = this;
	var res = this.commonCall({
		data : {
			method : 'setHandOverList',
			handOverListNo : config.params1,
			refIds : config.params2,
			lockId : config.lockId
		}
	});
	return res.data;
};
/* 删除图片 */
FilextApi.prototype.remove = function(config) {
	var me = this;
	var res = this.commonCall({
		data : {
			method : 'removeStorageObject',
			uuid : config.uuid,
			foid : config.foid,
			lockId : config.lockId,
			dealer : this.config.config.dealer
		}
	});

	res = this.commonCall({
		data : {
			method : 'delete',
			uuid : config.uuid,
			node : this.getNodeX(config.node),
			dealer : this.config.config.dealer
		}
	});

	return res;
};
/* 排序 */
FilextApi.prototype.doOrder = function(config) {
	var me = this;
	return this.commonCall({
		data : {
			method : 'orderStorageObjects',
			uuid : config.uuid,
			uuids : config.uuids,
			lockId : config.lockId
		}
	});
};
/* 必输项调整 */
FilextApi.prototype.sentMaterial = function(uuid, value, code) {
	var me = this;
	var res = this.commonCall({
		data : {
			method : 'sentMaterial',
			uuid : uuid,
			value : value,
			code : code,
			lockId : config.lockId
		}
	});
};
/* 必输项校验 */
FilextApi.prototype.checkMaterial = function(uuids) {
	var me = this;
	var res = this.commonCall({
		data : {
			method : 'checkMaterial',
			uuids : uuids,
			lockId : config.lockId
		}
	});
	return res.data;
};
/* 文件变动后更新 */
FilextApi.prototype.updateFile = function(config) {
	var me = this;
	return this.commonCall({
		data : {
			method : 'updateStorageObject',
			uuid : config.uuid,
			title : config.title,
			filesize : config.filesize,
			lockId : config.lockId
		}
	});
};
/* 移动文件 */
FilextApi.prototype.moveToFile = function(config) {
	var me = this;
	return this.commonCall({
		data : {
			method : 'moveToFileObject',
			foid : config.foid,
			uuid : config.uuid,
			lockId : config.lockId
		}
	});
};
/* 添加分类 */
FilextApi.prototype.addCategory = function(config) {
	var me = this;
	return this.commonCall({
		data : {
			method : 'addFileObject',
			uuid : config.uuid,
			title : config.title,
			code : config.code,
			remark : config.remark,
			materialNumber : config.materialNumber,
			materialTime : config.materialTime,
			temporary : me.config.config.privilege.temporary,
			isAdd : config.isAdd,
			lockId : config.lockId
		}
	});
};
/* 编辑分类 */
FilextApi.prototype.editCategory = function(config) {
	return this.commonCall({
		data : {
			method : 'editFileObject',
			uuid : config.uuid,
			title : config.title,
			remark : config.remark,
			lockId : config.lockId
		}
	});
};
/* 删除分类 */
FilextApi.prototype.removeCategory = function(config) {
	return this.commonCall({
		data : {
			method : 'removeFileObject',
			uuid : config.uuid,
			lockId : config.lockId
		}
	});
};

/* 保存临时转正式 */
FilextApi.prototype.updateTemporary = function(uuid) {
	return this.commonCall({
		data : {
			method : 'updateTemporary',
			uuid : uuid
		}
	});
};

/* 某一个事项引用另一个事项中相同的材料 */
FilextApi.prototype.updatePicture = function(config) {
	var me = this;
	return this.commonCall({
		data : {
			method : 'updatePicture',
			selfCode : config.selfCode,
			goalCodes : config.goalCodes,
			fileUnieId : config.fileUnieId,
			title : config.title,
			lockId : config.lockId
		}
	});
};
/* 被引用的事项数据 数据状态变更 */
FilextApi.prototype.updateFileUnitDataTpye = function(fileUnieId) {
	var me = this;
	return this.commonCall({
		data : {
			method : 'updateFileUnitDataTpye',
			fileUnitId : fileUnieId
		}
	});
};
/* 验证比传项 */
FilextApi.prototype.validate = function() {
	var jsonObj = {
			
	};
	for ( var e in idMap) {
		var material = idMap[e];
		if (typeof (material.bizTitle) == 'undefined')
			continue;

		var required = material.required;
		var leastNumber = material.leastNumber || 0;
		var materialNumber = material.children.length;

		if (required && materialNumber == 0) {
			//alert(material.bizTitle + '为必需，请上传材料文件！');
			jsonObj.success = false;
			jsonObj.message = material.bizTitle + '为必需，请上传材料文件！';
			return jsonObj;
		}
		if (required && leastNumber != 0 && materialNumber < leastNumber) {
			//alert(material.bizTitle + '需上传至少' + leastNumber + "份材料文件！");
			jsonObj.success = false;
			jsonObj.message = material.bizTitle + '需上传至少' + leastNumber + "份材料文件！";
			return jsonObj;
		}
	}
	jsonObj.success = true;
	return jsonObj;
};

/* 被引用的事项数据 数据状态变更 */
FilextApi.prototype.createAndSaveFileUnit = function(bizId, conut) {
	var me = this;
	return this.commonCall({
		data : {
			method : 'createAndSaveFileUnit',
			bizId : bizId,
			conut : conut,
			lockId : config.lockId
		}
	});
};
FilextApi.prototype.returnApplication = function(config) {

	var res = this.commonCall({
		data : {
			method : 'returnApplication',
			type : config.type,
			bookName : config.bookName,
			jgcode : config.jgcode,
			dealerCode : config.dealerCode,
			dealerCodeTitle : config.dealerCodeTitle,
			fileNumber : config.fileNumber,
			subjectCode : config.subjectCode,
			subjectTitle : config.subjectTitle,
			entityUnitCode : config.entityUnitCode,
			isExist : config.isExist,
			biaoti : config.biaoti,
			code : config.code,
			title : config.title,
			windowStaff : config.windowStaff
		}
	});
	return res.data.resultFalg;
};

/* added by Lintel */
// 查看材料历史版本
FilextApi.prototype.viewMaterialHistory = function(config) {
	var res = this.commonCall({
		data : {
			method : 'viewMaterialHistory',
			uuid : config.uuid,
			isBase : config.isBase,
			lockId : config.lockId
		}
	});

	return res;
}

// 查看材料文件历史版本
FilextApi.prototype.viewMaterialFileHistory = function(config) {
	var me = this;
	var res = this.commonCall({
		data : {
			method : 'viewMaterialFileHistory',
			uuid : config.uuid,
			appContext : me.WEB_APP,
			lockId : config.lockId
		}
	});

	return res;
}

// 更新当前使用的材料版本
FilextApi.prototype.updateCurrentMaterial = function(config) {
	var res = this.commonCall({
		data : {
			method : 'updateCurrentMaterial',
			src : config.src,
			dest : config.dest,
			lockId : config.lockId
		}
	});

	return res;
}

// 删除办件
FilextApi.prototype.removeArchive = function(config) {
	var res = this.commonCall({
		data : {
			method : 'removeArchive',
			bizId : config.bizId,
			bizCode : config.bizCode,
			lockId : config.lockId
		}
	});

	return res;
}

// 获取材料版本
FilextApi.prototype.getMaterialVersion = function(config) {
	var res = this.commonCall({
		data : {
			method : 'getMaterialVersion',
			uuid : config.uuid,
			lockId : config.lockId
		}
	});

	return res;
}

// 生成PDF
FilextApi.prototype.buildPdf = function(config) {
	var res = this.commonCall({
		data : {
			appContext : this.WEB_APP,
			method : 'buildPdf',
			uuids : config.uuids,
			filter : config.filter,
			all : config.all,
			bizId : config.bizId,
			bizCode : config.bizCode,
			materialCode : config.materialCode
		}
	});

	return res;
}

// 拷贝办件
FilextApi.prototype.copyArchive = function(config) {
	var res = this.commonCall({
		data : {
			method : 'copyArchive',
			uuid : config.uuid,
			bizId : config.bizId,
			bizCodeForSearch : config.bizCodeForSearch,
			bizCode : config.bizCode,
			lockId : config.lockId
		}
	});

	return res;
}

// 修改材料是否有效
FilextApi.prototype.modifyEffective = function(config) {
	var res = this.commonCall({
		data : {
			method : 'modifyEffective',
			uuid : this.uuid,
			data : JSON.stringify(config),
			lockId : config.lockId
		}
	});

	return res;
}

// 修改材料是否必输
FilextApi.prototype.modifyRequired = function(config) {
	var res = this.commonCall({
		data : {
			method : 'modifyRequired',
			uuid : this.uuid,
			data : JSON.stringify(config),
			lockId : config.lockId
		}
	});

	return res;
}

FilextApi.prototype.getCategory = function() {
	var res = this.commonCall({
		data : {
			method : 'getCategory',
			uuid : this.uuid,
			lockId : this.lockId
		}
	});

	return res;
}

FilextApi.prototype.printLog = function(uuid,lockId) {
	var res = this.commonCall({
		data : {
			method : 'printLog',
			uuid : uuid,
			lockId : lockId
		}
	});

	return res;
}

FilextApi.prototype.moveToMe = function(config) {
	var res = this.commonCall({
		data : {
			method : 'moveToMe',
			uuid : config.uuid,
			foid : config.foid,
			batch : config.batch,
			lockId : config.lockId
		}
	});

	return res;
}

FilextApi.prototype.viewCount = function(config) {
	var res = this.commonCall({
		data : {
			method : 'viewCount',
			bizId : config.bizId,
			bizCode : config.bizCode,
			batch : config.batch
		}		
	});

	return res;
}
//查看全部图片
FilextApi.prototype.queryAllPic = function(config) {
	var res = this.commonCall({
		data : {
			appContext : this.WEB_APP,
			method : 'queryAllPic',
			uuid : config.uuid,
			bizId : config.bizId
		}
	});
	return res;
}

//绑定批次号param : uuid图片id  batch批次号
FilextApi.prototype.submitBatch = function(config) {
	var res = this.commonCall({
		data : {
			method : 'submitBatch',
			uuid : config.uuid,
			batch : config.batch,
			lockId : config.lockId
		}
	});
	return res;
}


/*
 * 防调试代码 
 */
function checkDebugger() {
	var d = new Date();
	debugger;
	var dur = Date.now()-d;
	if(dur < 5) {
		return false;
	} else {
		return true;
	}
}

function breakDebugger() {
	if(checkDebugger()) {
		breakDebugger();
	}
}

//$(document).ready(function(e) {
//	breakDebugger();
//});

/*$("body").click(function(){
	breakDebugger();
});*/

//$(".content-wrap").click(function(){
//	alert("防调试");
//    breakDebugger();
//});
