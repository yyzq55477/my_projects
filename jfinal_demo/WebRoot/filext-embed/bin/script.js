﻿﻿var filextApi = null,
	rootJson = null,
	initData = null,
	privilege = {},
	currentId = 0,
	editId = 0,
	editCanvas = {},
	editTarget = {},
	editRadio = 1,
	idMap = {},
	pidMap = {},
	thumbPageMap = {},
	detailPageMap = {},
	holder = null,
	support = {
		filereader: typeof FileReader != 'undefined',
		dnd: 'draggable' in document.createElement('span')
	},
	jcropApi = null,
	editType = '',
	rotateDegree = 0,
	isApplied = false,
	uploadType = '',
	isReplace = false,
	replaceData = {},
	offsetMap = {},
	
	x1 = 0,
	y1 = 0,
	x2 = 0,
	y2 = 0,
	isDown = false,
	picasa = null,
	
	template = "<input id='pic-search' type='text' class='form-control' placeholder='搜索图片名称' /><ul id='items' class='clearfix'>"+
			"<li id='holder' class='collection'><img src='style/images/upload.jpg' /><p class='title'>采集</p>"+
			"<p class='operate'><label><input id='uploadType' type='checkbox' checked='checked' /><span>记住选择</span></label></p></li>"+
			"{{#data}}<li class='item {{type}}Cls' id='item_{{uuid}}' data-id='{{uuid}}' data-href='{{var-0}}' title='{{title}}'>"+
			"<span class='badge pull-left badge-item-version'>{{item_version_total}}</span>" +
			"{{#compare type 'Image'}}<img src='{{thumb-2}}' data-original='{{file}}' data-error='0'/>"+
			"<label style='width:100%'><input class='checkpic' type='checkbox'data-id='{{uuid}}'/><span></span></label>"+
			"<p class='operate'><span class='edit' data-id='{{uuid}}'>编辑</span><i class='line'>|</i>"+
			"{{else}}<img src='images/icons/{{uuidExt}}.png' />"+
			"<label style='width:100%'><input class='checkpic' type='checkbox'data-id='{{uuid}}'/><span></span></label>"+
			"<p class='operate'>{{/compare}}"+
			"<span class='replace' data-id='{{uuid}}'>替换</span><i class='line'>|</i>"+
			"<span class='remove' data-id='{{uuid}}'>删除</span></p>"+
			"<p class='title'>{{#limit filename 12}}{{filename}}{{/limit}}</p></li>{{/data}}"+
			"</ul>",
	
	thumbTemplate = "<ul>{{#data}}{{#compare @index '0'}}"+
				"<li class='thumbItem clearfix active {{type}}' data-id='{{uuid}}' data-title='{{bizTitle}}' data-base-file='{{hasBaseFile}}'"+
				" data-cv='{{current_version}}' data-format='{{materialFormat}}' data-number='{{leastNumber}}'>"+
				"{{else}}<li class='thumbItem clearfix {{type}}' data-id='{{uuid}}' data-title='{{bizTitle}}' data-base-file='{{hasBaseFile}}'"+
				" data-cv='{{current_version}}' data-format='{{materialFormat}}' data-number='{{leastNumber}}'>{{/compare}}"+
				"<span class='badge pull-left badge-version {{#if hasBaseFile}}base-material{{/if}}'>" + 
				"{{#if version_total}}{{version_total}}{{else}}0{{/if}}</span>" +
				"<span class='caret'></span>{{bizTitle}}{{#if required}}<i class='star'>*</i>{{/if}}"+
				"<span id='badge_{{uuid}}' class='badge pull-right'>{{#if total}}{{total}}{{else}}0{{/if}}</span>"+
				"<span class='material_time pull-right'>{{create_time}}&nbsp;</span>" +
				"{{#compare type 'special-proof'}}"+
				"{{#compare @index '0'}}<i class='icon-white icon-remove category-remove'></i><i class='icon-white icon-edit category-edit'></i>"+
				"{{else}}<i class='icon-remove category-remove'></i><i class='icon-edit category-edit'></i>"+
				"{{/compare}}{{/compare}}"+
				"</li>{{/data}}</ul>"+
				"<div class='add-wrap'><button class='btn btn-default btn-add-category'>添加分类</button>"+
				"<div class='input-wrap'><input id='categoryName2' class='form-control' type='text' placeholder='材料名称' />"+
				"<div class='btn-group'><button class='btn btn-primary category-save'>保存</button>"+
				"<button class='btn btn-default category-cancel'>取消</button></div>"+
				"</div></div>",
				
	thumbGroupTemplate = "<input id='cate-search' type='text' class='form-control' placeholder='搜索条件' /><div id='accordion'>{{#groupList groupData}}{{/groupList}}</div>",
			
	viewTemplate = "<ul>{{#data}}{{#compare type 'Image'}}<li id='li_{{uuid}}' data-id='{{uuid}}'>"+
		"<p class='title'><input type='checkbox'/>{{title}}</p>"+
		"<div class='image-outline' data-id='{{uuid}}'><img src='{{file}}' id='img_{{uuid}}' data-error='0'/></div>"+
		"<div class='operate'><div class='btn-toolbar' data-id='{{uuid}}'>"+
		"<button class='btn btn-default bigImage' type='button'>查看大图</button>"+
		"<button class='btn btn-default viewRotate' type='button'>旋转</button>"+
		"<button class='btn btn-default download' type='button'>下载</button>"+
		"<button class='btn btn-default print' type='button'>打印</button>"+
		"</div></div></li>"+
		"{{else}}<li id='li_{{uuid}}' data-id='{{uuid}}' data-url='{{var-0}}'><p class='title'>{{title}}</p><span id='img_{{uuid}}'></span><a class='media' href='{{var-0}}'></a>"+
		"<div class='operate'><div class='btn-toolbar' data-id='{{uuid}}'>"+
		"<button class='btn btn-default bigPdf' type='button'>全屏显示</button>"+
		"<button class='btn btn-default download' type='button'>下载</button>"+
		"</div></div></li>"+
		"{{/compare}}{{/data}}</ul>",	
		
	viewThumbTemplate = "<ul>{{#data}}<li id='thumb-{{uuid}}' data-id='{{uuid}}'>"+
			"{{#compare @index '0'}}<a class='hover' href='javascript:;'>{{else}}<a href='javascript:;'>{{/compare}}"+
			"{{#if thumb-0}}<img src='{{thumb-0}}' data-error='0'/>{{else}}"+
			"<img src='images/icons/{{uuidExt}}.png' />{{/if}}</a></li>"+
			"{{/data}}</ul>",
			
	viewThumbTemplate2 = "<ul>{{#data}}<li id='thumb-{{uuid}}' data-id='{{uuid}}'>"+
			"{{#compare @index '0'}}<a class='hover' href='javascript:;'>{{else}}<a href='javascript:;'>{{/compare}}"+
			"<input type='checkbox' />{{filename}}</a></li>"+
			"{{/data}}</ul>";

/* 入口 */
function init(config) {
	var config = config || {};
	filextApi = new FilextApi(config);
	picasa = new Picasa();
	privilege = config.config.privilege || {};
	setDefaultPrivilege();
	initPage(1);
	if (config.config.openHighShoot) {
		$('.highShoot').click();
	}
	
	window.onunload = function() {
		recycle(filextApi);
		recycle(rootJson);
		recycle(initData);
		recycle(privilege);
		recycle(idMap);
		recycle(thumbPageMap);
		recycle(detailPageMap);
		recycle(support);
		recycle(replaceData);
		recycle(offsetMap);
		recycle(picasa);
		
		template = null;
		thumbTemplate = null;
		thumbGroupTemplate = null;
		viewTemplate = null;
		viewThumbTemplate = null;
		viewThumbTemplate2 = null;
		$('iframe').each(function() {
			try {
				var iframe = $(this)[0];
				iframe.attr('src', 'about:blank');
				iframe.contentWindow.document.write('');
				iframe.contentWindow.document.clear();
				iframe.contentWindow.document.close();
				iframe.remove();
			} catch (e) {}
		});
		recycle(window);
		if (typeof(CollectGarbage)!='undefined') CollectGarbage();
		
		return true;
	}
}

function recycle(obj) {
	for (var e in obj) {
		obj[e] = null;
		delete obj[e];
	}
	obj = null;
}

function initPage(falg){
	$('#refreshing').fadeIn();
	// 获取数据
	fetchData(falg);
	
	if (initData.length>0 || rootJson.group) {
		// 渲染列表页
		initListView();
		setTimeout(function() {
			$('#refreshing').fadeOut('slow');
		}, 1000);
	} else {
		$('#refreshing').hide();
		showTip("当前办件没有任何影像材料！");
	}
	
	if(privilege.enableGroup) $('.collection').hide();
}

//获取数据
function fetchData(falg) {
	idMap = {};
	pidMap = {};
	rootJson = filextApi.getInitData(falg) || {};
	initData = rootJson.children || [];
	
	rootJson.watermark = rootJson.watermark || '';

	if (rootJson.fileSizeLimit)
		filextApi.fileSizeLimit = rootJson.fileSizeLimit;
	if (privilege.disableOperate == null && rootJson.allowEdit == false && rootJson.collection == true) {
		privilege.disableOperate = true;
	}
	if(rootJson.enableGroup){
		if(initData.length == 0 || (initData[0].code != null && initData[0].code != '')) 
			privilege.enableGroup = true;
	}
	
	pidMap[filextApi.uuid] = initData;
	// 获取分类数据
	for(var i in initData){
		var uuid = initData[i].uuid;
		/*
		 * var res = filextApi.getSubData(uuid); pidMap[uuid] =
		 * filterCache(res.data) || [];
		 */
		var data = initData[i].children;
		pidMap[uuid] = filterCache(data) || [];
		thumbPageMap[uuid] = {
			'pageIndex' : 1,
			'pageSize' : 10																																																							
		};
		detailPageMap[uuid] = {
			'pageIndex' : 1,
			'pageSize' : 5
		};
	}
	generateIdMap();
}

function generateIdMap() {
	for ( var i in pidMap) {
		var data = pidMap[i];
		for (var j = 0, len = data.length; j < len; j++) {
			idMap[data[j].uuid] = data[j];
		}
	}
}
	
function updateCurrentData(id) {
	var updateId = id || currentId;
	var res = filextApi.getSubData(updateId);
	var data = res.data || [];
	if (res.success) {
		pidMap[updateId] = filterCache(data);
		generateIdMap();
	}
	
	var rootObj = pidMap[rootJson.uuid];
	for(var i = 0, len = rootObj.length; i < len; i++){
		if(rootObj[i].uuid == updateId){
			rootObj[i].children = pidMap[updateId];
			rootObj[i].total = pidMap[updateId].length;
			return false;
		}
	}
}
/* 过滤处理图片缓存问题 */
function filterCache(data) {
	var time = new Date().getTime();
	var tail = '&watermark='+ encodeURIComponent(filextApi.watermark) +'&_cache=' + time + '&appContext=' + filextApi.WEB_APP;
	for(var i = 0, len = data.length; i < len; i++) {
		if (data[i].type == 'Doc') continue;
		
		data[i]['editFile'] = data[i]['editFile'] + '&oper=true' + tail;
		data[i]['file'] = data[i]['file'] + '&oper=true' + tail;
		data[i]['thumb-2'] = data[i]['thumb-2'] + tail;
	}
	return data;
}
function updateCateView(){
	updateCurrentData();
	updateContent();
	$('#badge_'+ currentId).html(pidMap[currentId].length);
}
	
function initListView(){
	/* viewFileCatelog查看按钮权限 */
	if(rootJson.allowViewFileCatalog){
		var src = $('#file-catalog-iframe').attr('src');
		$('#file-catalog-iframe').attr('src', src+'/v1/FileCatalog/viewFileCatalog?id='+ rootJson.fileCatalogUuid);
		$('.btn-viewFile-wrap').removeClass('hidden');
		$('#listView .thumb').addClass('with-view');
		$('.btn-viewStaff-wrap').removeClass('hidden');
	}
	if(privilege.temporary){
		$('#listView .thumb').addClass('with-save');
		$('.btn-pic-wrap').removeClass('hidden');
	}
	if(!rootJson.allowCite) $('.finance').remove();
	
	if (!privilege.disablePrintBar) {
		$('.btn-printBar').parent().removeClass('hidden');
	}
	if (privilege.disableMultiDel) {
		$('.btn-pic-remove').parent().addClass('hidden');
	}
	if (privilege.disableViewAll) {
		$('.btn-pic-viewall').parent().addClass('hidden');
	}
	if (privilege.disableSidelist) {
		$('#listView .guide').hide();
	}
	
	initListThumb();
	
	if (privilege.disableQuote) {
		$('.badge-version').remove();
	}
	if($('#listView .thumb li.active').length > 0)
		currentId = getCurrentId();
	
	updateContent();
	$('#listView').show();
	$('#editView').hide();
	$('#detailView').hide();
	
	initListViewEvent();
	initOffsetMap();
}

function setDefaultPrivilege() {
	if (typeof(privilege.disablePrintBar) == 'undefined')
		privilege.disablePrintBar = true;
	privilege.disableMultiDel = privilege.disableMultiDel || false;
	privilege.disableViewAll = privilege.disableViewAll || false;
	privilege.disableQuote = privilege.disableQuote || false;
	privilege.disableSidelist = privilege.disableSidelist || false;
}

function initListViewEvent(){
	/* viewFileCatelog查看 */
	$('.btn-viewFile').click(function(){
		$('#view-file-catelog').modal({backdrop: 'static'});
	});
	$('.btn-viewStaff').click(function(){
		window.open('http://220.191.208.101:8080/lemis/v1/qc01/editBaseInfoDzyx?aqc002='+rootJson.fileCatalogFileNumber,'viewStaff');
	});
	$('.btn-pic-save').off('click').click(function(){
		var msg=filextApi.updateTemporary(filextApi.uuid);
		if(msg.success=true){
			alert("保存成功");
		}else{
			alert("保存失败");
		}
		parent.opener.selectList();
		parent.window.close();
	});
	/**
	 * 删除图片
	 */
	$('.btn-pic-remove').off('click').click(function(){
		var uuids = getCheckedPics();
		if(uuids.length>0){
			if(confirm('您确定要删除吗？')){
				var s=0;f=0;var uuid;
				for(var i=0;i<uuids.length;i++){
					uuid = uuids[i];
					var res = filextApi.remove({
						uuid: uuid,
						node:idMap[uuid].node,
						foid: currentId
					});
					if(res.success){
						s++;
					}else{
						f++;
					}
				}
				showTip("删除成功"+s+"条，失败"+f+"条!");
				updateCateView();
				updateMaterialVersion();
			}
		}else{
			alert("请选择要删除的图片！");
		}
	});
	/* 查看全部文件 */
	$('.btn-pic-viewall').off('click').click(function() {
		var disablePrintPics = false;
		var allDetail = [];
		currentId = '_allDetail';
		
		$('#listView .thumb ul li').each(function() {
			var id = $(this).data('id');
			allDetail = allDetail.concat(pidMap[id]);
		});
		
		if (allDetail.length > 0) {
			detailPageMap[currentId] = { 'pageIndex':1,'pageSize':5 };
			
			editId = allDetail[0].uuid;
			pidMap[currentId] = allDetail;
			initDetailView();
		} else {
			showTip('当前没有任何材料可供查看！');
		}
	});
	
	/* 打印材料目录 */
	$('.btn-printCategory').off('click').click(function() {
		var res = filextApi.getCategory();
		if (res.success) {
			var data = res.data;
			var table = '';
			if (data.length > 0) {
				for (var i in data) {
					var l = data[i];
					for (var j in l) {
						if (l[j] == null) l[j] = '';
					}
					table += '<tr><td>'+l.cllh+'</td><td>'+l.clname+'</td><td>'+l.clcount+'</td><td>'+l.clremark+'</td></tr>';
				}
			} else {
				table = '<tr><td>-</td><td>-</td><td>-</td><td>-</td></tr>';
			}
			var created = res.created;
			var birthday = res.birthday;
			var sex = res.sex;
			var cardid = res.cardid;
			var name = res.name;
			$.get(filextApi.WEB_APP+'/filext-embed/doPrintRsClml.html', {}, function(html) {
				html = html.replace('{$$$$}', table);
				html = html.replace('{created}',created);
				html = html.replace('{birthday}',birthday);
				html = html.replace('{sex}',sex);
				html = html.replace('{name}',name);
				html = html.replace('{cardid}',cardid);
				var LODOP = getLodop();
				LODOP.PRINT_INIT("材料目录");
				LODOP.SET_PRINT_PAGESIZE(1, 0, 0, "A4");
				LODOP.ADD_PRINT_TABLE("5%", "5%", "90%", "90%", html);
				LODOP.PREVIEW();
			});
		} else {
			showTip('打印失败！');
		}
	});
	
	/* 返回 */
	$('.shutdown').click(function(){
		shutdown();
	});
	
	/* 将多张图片转换成pdf */
	$('.buildpdf').click(function() {
		buildAndDownloadPdf();
	});
	
	$('.printPics').click(function() {
		var images = {};
		var count = 0;
		var selector = '#detailView .content input:checked';
		if (needHideThumb())
			selector = '#detailView .thumb input:checked';
		$(selector).each(function(index, element) {
			var id = $(this).parent().parent().data('id');
			var obj = getObject(id);
			if (obj.type == 'Image') {
				count++;
				images[id] = '<img src="' + obj.file + '"/>';
			}
		});
		
		if (count > 0) {
			printImages(images);
		} else {
			showTip('请选中至少一张图片！');
		}
	});
	
	/* 高拍 */
	$('.highShoot').off('click').click(function(){
		if ($('#uploadType').is(':checked')) {
			uploadType = 'high-shoot';
		}
		if (CHighshoot.available) {
			$('#modal-upload').modal('hide');
			openCHighShoot();
		} else {
			if (isIE()) {
				$('#modal-upload').modal('hide');
				openHighShoot();
			} else {
				$('#upload-default').hide();
				$('#high-shoot_f').attr('src','high-shoot.html').show();
				$('#modal-upload').addClass('large-modal');
			}
		}	
		/*
		 * $('#upload-default').hide();
		 * $('#high-shoot_f').attr('src','high-shoot2.html').show();
		 * $('#modal-upload').addClass('large-modal');
		 */
	});
	
	$('.scanning').off('click').click(function(){
		if (isIE()) {
			if($('#uploadType').is(':checked')){
				uploadType = 'scanning';
			}
			$('#upload-default').hide();
			$('#scanning_f').attr('src','scan.html').show();
			$('#modal-upload').addClass('large-modal');
		} else {
			showTip('扫描功能仅支持IE浏览器！');
		}
	});
	
	/* 本地上传 */
	$('.nativeUpload').click(function(){
		
		if($('#uploadType').is(':checked')){
			uploadType = 'native-upload';
		}
		
		if(isIE()){
			if (!CheckFlashPlayer()) {
				return;
			}
		}
		
		$('#upload-default').hide();
		$('#native-upload').show();
	});
	
	/* 财务浏览 */
	$('.finance').click(function(){
		$('#upload-default').hide();
		$('#modal-upload').addClass('large-modal');
		$('#finance').show();
		$('#finance input[name=finance]').focus();
	});
	
	var financeSearchFn = function(){
		var finance = $('#finance input[name=finance]').val();
		if(finance == ''){
			$('#finance input[name=finance]').focus();
			$('#finance ul').html('');
			alert('请输入查询关键词');
			return false;
		} 
		var result = filextApi.queryPictures({
			key : finance,
			departmentId : rootJson.departmentId
		});
		if(result == null || result.length == 0) {
			$('#finance ul').html('');
			return false;
		}
		
		var lis = '';
		for(var i = 0, len = result.length; i < len; i++){
			lis += '<li><label><img src="'+ result[i]['thumb-2'] +'" /><input type="checkbox" value="'+ result[i].uuid +'" /></label></li>';
		}
		$('#finance ul').html(lis);
	};
	
	$('#finance input[name=finance]').keyup(function(e){
		if(e.which == 13) financeSearchFn();
	});
	$('.finance-search').click(function(){
		financeSearchFn();
	});
	$('.finance-upload').click(function(){
		$('#loading').show();
		var uuids = [];
		$('#finance input[type=checkbox]:checked').each(function() {
			uuids.push($(this).val());
		});
		if(uuids.length == 0) {
			alert('请选择图片');
			$('#loading').hide();
			return false;
		}
		setTimeout(function(){
			var result = filextApi.submitPictures({
				uuid: currentId,
				uuids : uuids.join(',')
			});
			if(result.success) {
				$('#modal-upload').modal('hide');
				updateCateView();
				$('#loading').hide();
			}else{
				alert(result.message);
				$('#loading').hide();
			}
		}, 1000);
	});
	/* 上传 */
	$('.fileinput-upload').click(function() {
		$('#native-file').uploadify('upload', '*');
	});
	/* 使用基础材料 */
	$('.basefile').off('click').click(function() {
		if(confirm("用基础材料会覆盖原材料，是否继续!")){
				var res = filextApi.useBaseFile({
	    			uuid : currentId
	    		});
	    		if (res.success) {
	    			$('#modal-upload').modal('hide');
	    			updateCateView();
	    		} else {
	    			alert(res.message);
	    		}
		}	
	});
	/* 返回默认上传界面 */
	$('.back-to-default').click(function() {
		backToDefault();
	});
	$('#modal-upload').off('hidden.bs.modal').on('hidden.bs.modal',function() {
		try {
			$('#native-file').uploadify('destroy');
			$('#finance ul').html('');
			$('#finance input[name=finance]').val('');
			unloadHandler();
		} catch (e) {}
	});
	/* 裁剪 */
	$('.crop').off('click').click(function() {
		crop();
	});
	/* 旋转 */
	$('.rotate').off('click').click(function() {
		rotate();
	});
	/* 纠偏 */
	$('.correct').off('click').click(function() {
		correct();
	});
	$('.apply').click(function() {
		apply();
	});
	$('.save').click(function() {
		save();
	});
	/* 保存图片名称 */
	$('.saveFilename').click(function() {
		var title = $('#filename').val();
		var rs = filextApi.updateFile({
			uuid : editId,
			title : title
		});
		if (rs.success) {
			updateCurrentData();
			alert('保存成功！');
		}else{
			alert(rs.message);
		}
	});
	$('.enlarge').off('click').click(function() {
		var $parent = $(this).parent();
		var ratio = $parent.data('ratio');
		ratio += 0.2;
		if(ratio >= 1.8) return false;
		$parent.data('ratio', ratio);
		changeImage(ratio);
	});
	$('.narrow').off('click').click(function() {
		var $parent = $(this).parent();
		var ratio = $parent.data('ratio').toFixed(2);
		ratio -= 0.2;
		if(ratio <= 0.2) return false;
		$parent.data('ratio', ratio);
		changeImage(ratio);
	});
	$('.adaptive-wide').click(function() {
		adaptiveWide();
	});
	$('.adative-height').click(function() {
		adativeHeight();
	});
	$('.edit-top-bar select').change(function() {
		var ratio = $(this).val();
		if(ratio == '') return false;
		changeImage(ratio);
	});
	$('#cate-search').keyup(function(e){
		var key = $(this).val();
		if(key == '') return false;
		
		var isFound = false;
		if(e.which == 13){
			$('.groupTitle').removeClass('active');
			$('.collapse').slideUp();
			
			for(var i = 0, len = initData.length; i < len; i++){
				if(initData[i].bizTitle.indexOf(key) != -1){
					var group = getGroup('code', initData[i].code);
					var $curCollapse = $('#collapse_' + group.uuid);
					
					var upParent = $curCollapse.parent().parent();
					while(upParent.hasClass('collapse')){
						upParent.slideDown();
						upParent = upParent.parent().parent();
					}
					
					$curCollapse.prev('.groupTitle').addClass('active');
					$curCollapse.slideDown();
					isFound = true;
				}
			}
			if(!isFound) setTimeout(function(){showTip('没有找到匹配的内容')}, 1000);
		} 
	});
	// 材料版本 TODO
	$('.badge-version').off('click').click(function(e) {
		if ($(this).text() == 0) {
			showTip("当前材料没有历史版本！");
			return ;
		}
		
		var iframe = $('#history-iframe')[0];
		$('#modal-history').modal({backdrop: 'static'});
		
		var cw = iframe.contentWindow;
		var parentEl = $(this).parent();
		var id = parentEl.data('id'),
		cv = parentEl.data('cv'),
		isBase = parentEl.data('base-file');
		isBase ? $('#history-btns').show() : $('#history-btns').hide();
		$('#current-version').text(cv);
		
		cw.initEnv({
			'id' : id,
			'cv' : cv,
			'appcontext' : filextApi.WEB_APP,
			'isBase' : isBase
		});
		
		$('.modal-history-ok').off('click').click(function() {
			var selectedId = cw.getSelectedId();
			var res = filextApi.updateCurrentMaterial({ 'src':selectedId, 'dest':id });
			if (res.success) {
				showTip("引用成功！");
				updateCateView();
			}
			$('#modal-history').modal('hide');
		});
		$('.modal-history-cancel').off('click').click(function() {
			$('#modal-history').modal('hide');
		});
	});
}

function needHideThumb() {
	return filextApi.config.config.viewType=='classical'
		&& privilege.enableGroup && currentId!='_allDetail';
}

function printImages(images) {
	var LODOP = getLodop();
	LODOP.PRINT_INIT("打印图片");
	LODOP.SET_PRINT_STYLE("Stretch", 2);
	LODOP.SET_PRINT_PAGESIZE(3, 0, 0, "A4");

	//1in(英寸)=2.54cm(厘米)=25.4mm(毫米)=72pt(磅)=96(px不是像素，是绝对长度单位)    A4高度1122
	var pageHeight = 1100;
	
	//获得各图片的尺寸集合
	var imagesHeight = {};
	var imagesWidth = {};
	$('#detailView .content img').each(function() {
		var id = $(this).parent().parent().data('id');
		imagesHeight[id] = this.height;
		imagesWidth[id] = this.width;
	});
	
	var totalHeight = 0;//分页控制量
	var imageBorder = 10;//上下图片间距
	
//	for (var id in images) {
//		//是否分页
//		if(totalHeight + imagesHeight[id] > pageHeight){
//			totalHeight = 0;
//			LODOP.NewPage();
//		}
//		// TODO 临时方式去掉水印
//		LODOP.ADD_PRINT_IMAGE(totalHeight, 0, "100%", "100%", images[id].replace(/watermark=\w+/, 'watermark=%20'));
//		totalHeight = totalHeight + imagesHeight[id] + imageBorder;
//	}
	var count = LODOP.PREVIEW();
	if (count>0 && privilege.enableGroup) {
		for (var id in images) {
			filextApi.printLog(id);
		}
	}
}

// 判断是否为IE浏览器
function isIE() {
	return navigator.userAgent.indexOf('MSIE')>=0 || navigator.userAgent.indexOf('Trident')>=0;
}

function removeBigContainer() {
	var container = $('.view-big-container', parent.document);
	if (container.length > 0) container.remove();
}

function getCheckedPics() {
	var uuids = [];
	$('.checkpic').each(function() {
		if($(this)[0].checked){
			uuids.push($(this).data('id'));
		}
	});
	
	return uuids;
}

/* 加载图片失败处理 */
function imageErrorHandler(selector) {
	$(selector).off('error').error(function() {
		var img = $(this);
		var error = img.data('error');
		if (error < 3) {
			img.data('error', ++error);
			setTimeout(function() {
				img.attr('src', img.attr('src'));
			}, 50);
		} else {
			img.attr('src', 'style/images/broken.jpg');
		}
	});
}

function updateContent() {
	showPages(false);
	imageErrorHandler('#dragWrap img');
	$('#fileInfo').html('').hide();
	
	if(privilege.disableHighShoot) $('.highShoot').remove();
	if(privilege.disableNativeUpload) $('.nativeUpload').remove();
	if(privilege.disableScanning) $('.scanning').remove();
	if (privilege.disableMultiDel) $('.checkpic').remove();
	
	if (!privilege.enableTime) $('.material_time').remove();
	
	if (privilege.disableOperate) {
		$('#listView .content li .operate').remove();
		$('#listView .content .collection').remove();
		if (!privilege.remainCheckbox) $('.checkpic').remove();
		$('.btn-pic-remove').parent().addClass('hidden');
		
		/* $('.badge-item-version').remove(); */
		if (!privilege.enableVersion) $('.badge-version').remove();
		
		$('#listView .content li').hover(function(e) {
			$('#listView .content li .title').show();
		});
	} else {
		dragToCategory();
		/* 本地拖拽上传 */
		dragUpload();
	}
	/* 刷新 */
	$('.refresh').off('click').click(function() {
		initPage();
	});
	/* 图片详情 */
	$('#listView .content li').hover(function() {
		$('#listView .content .hover').removeClass('hover');
		$(this).addClass('hover');
		if (!$(this).hasClass('collection')) {
			showFileInfo($(this).data('id'));
		}
	}, function() {
		$('#fileInfo').hide();
	});
	/* 双击图片 */
	$('li.item').off('dblclick').dblclick(function() {
		scrollTop();
		var url = $(this).data('href');
		if (url == '') {
			if (filextApi.config.config.viewType == 'classical') {
				editId = $(this).data('id');
				var activeItem = $('li.thumbItem.clearfix.active').parent();
				initDetailView();
			} else {
				picasaGallery('#dragWrap #items .ImageCls');
			}
		} else {
			parent.icv.viewBigPdf(url);
			var pdfHeight=$(window.parent.document.body).height()-6;// iframe
																	// border=2
			$(window.parent.document).find('a.media').media({width : '100%', height : pdfHeight});
		}
	});
	$('li.collection label').off('click').click(function(e) {
		preventBubble(e);
	});
	/* 双击pdf */
	$('li.Doc').off('dblclick').dblclick(function() {
		var href = $(this).data('href');
		$('#pdf-container').html('<a class="media" href="' + href + '"></a> ').modal('show');
		$('a.media').media({width : 600, height : 595});
	});
	/* 编辑 */
	$('.edit').off('click').click(function() {
		editId = $(this).data('id');
		initEditView();
	});
	/* 删除 */
	$('.remove').off('click').click(function() {
		var id = $(this).data('id');
		if(confirm('您确定要删除吗？')){
			var res = filextApi.remove({
				uuid: id,
				node:idMap[id].node,
				foid: currentId
			});
			if(res.success){
				updateCateView();
				updateMaterialVersion();
				showTip('删除成功！');
			}else{
				showTip(res.msg);
			}
		}
	});
	/* 替换 */
	$('.replace').off('click').click(function() {
		isReplace = true;
		
		var id = $(this).data('id');
		
		replaceData = {
			node : idMap[id].node,
			uuid : id,
			materialFormat : generateMaterialFormat(currentId)
		};
		
		filextApi.replaceNewMethod(replaceData, function(res){
			updateAfterUpload(res);
		});
		uploadModalShow();
	});
	/* 采集 */
	$('.collection').off('click').click(function() {
				
		var activeItem = $('li.thumbItem.clearfix.active');
		if (activeItem.length == 0) {
			showTip('未选中任何材料类别，无法上传！');
			return ;
		}
		$("#native-file").empty();
		$("#native-file").html("本地上传");
		isReplace = false;
		if (isIE()) {
			filextApi.doUpload({
				uuid : currentId,
				materialFormat : generateMaterialFormat(currentId)
			},function(res){
				updateAfterUpload(res);
			});
		}else {
			filextApi.doUploadNewMethod({
				uuid : currentId,
				materialFormat : generateMaterialFormat(currentId)
			},function(res){
				updateAfterUpload(res);
			});
		}
		
		if (uploadType!='' && $('#uploadType').is(':checked')) {
			$('#upload-default').hide();
			/*
			 * if(uploadType == 'scanning' ){
			 * $('#scanning_f').attr('src','scan.html').show();
			 * $('#modal-upload').addClass('large-modal'); }
			 */
		} else {
			uploadType = '';
			$('.upload-view').hide();
			$('#upload-default').show();
			$('#modal-upload').removeClass('large-modal');
		}
		uploadModalShow();
		
	});
	// 材料文件版本 TODO
	$(".badge-item-version").off('click').click(function(e) {
		var id = $(this).parent().data('id');
		var res = filextApi.viewMaterialFileHistory({ 'uuid':id });
		var data = filterCache(res.data.children);
		pidMap['file_history'] = data || [];
		generateIdMap();
		
		var template = "<ul id='items' class='clearfix'>"+
			"{{#data}}<li class='item {{type}}Cls' id='item_{{uuid}}' data-id='{{uuid}}' data-href='{{var-0}}' title='{{title}}'>"+
			"<span class='badge pull-left view-item-version'>{{item_version_total}}</span>" +
			"{{#compare type 'Image'}}<img src='{{thumb-2}}' data-original='{{file}}'/>"+
			"<p class='operate'>{{else}}<img src='images/icons/{{uuidExt}}.png' /><p class='operate'>{{/compare}}</p>"+
			"<p class='title'>{{#limit filename 12}}{{filename}}{{/limit}}</p></li>{{/data}}"+
			"</ul>";
		
		compileTemplate(template, {'data':data}, '#modal-item-history-content');
		imageErrorHandler('#modal-item-history-content img');
		$('#modal-item-history-content .hover').removeClass('hover');
		
		$('#modal-item-history-content li.item').off('dblclick').dblclick(function() {
			scrollTop();
			var url = $(this).data('href');
			if (url == '') {
				picasaGallery('#modal-item-history-content #items .ImageCls');
			} else {
				parent.icv.viewBigPdf(url);
				var pdfHeight=$(window.parent.document.body).height()-6;// iframe
																		// border=2
				$(window.parent.document).find('a.media').media({width : '100%', height : pdfHeight});
			}
		});
		$('#modal-item-history-content ul li').off('hover').hover(function() {
			$('#modal-item-history-content .hover').removeClass('hover');
			$(this).addClass('hover');
		});
		
		$('#modal-item-history').modal({backdrop: 'static'});
	});
	// 查看历史材料文件双击查看大图 TODO
	$('.icv-history li.item').off('dblclick').dblclick(function() {
		parent.scrollTop();
		var url = $(this).data('href');
		if (url == '') {
			picasaGallery('#dragWrap #items .ImageCls');
		} else {
			icv.viewBigPdf(url);
			var pdfHeight=$(document.body).height()-6;// iframe border=2
			$(document).find('a.media').media({width : '100%', height : pdfHeight});
		}
	});
	$('#pic-search').off('keyup').keyup(function(e) {
		if (e.which == 13) {
			var needUpdate = false;
			var key = $(this).val();
			var searchId = '_searchId';
			
			if (key=='' && pidMap[searchId]) {
				pidMap[searchId] = null;
				thumbPageMap[searchId] = null;
				delete pidMap[searchId];
				delete thumbPageMap[searchId];
				currentId = getCurrentId();
				needUpdate = true;
			} else if (key != '') {
				var items = pidMap[getCurrentId()];
				var searchItems = new Array();
				for (var e in items) {
					if (items[e].filename.indexOf(key)>=0) {
						searchItems.push(items[e]);
					}
				}
				if (searchItems.length > 0) {
					needUpdate = true;
					currentId = searchId;
					pidMap[currentId] = searchItems;
					thumbPageMap[currentId] = {
						pageIndex : 1,
						pageSize : searchItems.length
					}
				} else {
					showTip('没有找到匹配的内容');
				}
			}
			
			if (needUpdate) {
				updateContent();
				$('#pic-search').val(key);
				$('#pic-search').focus();
			}
		}
	});
}

function getCurrentId() {
	return $('#listView .thumb li.active').data('id');
}

function picasaGallery(selector) {
	picasa.init(selector);
	if (privilege.picasaAll) {
		picasa.initData(pidMap[currentId]);
	} else {
		picasa.initData();
	}
	picasa.play(picasa.selector);
	picasa.changeImage($(selector).index($(selector+'.hover')));
	
	var id = $(selector+'.hover').data('id');
	var parentUuid = getObject(id).parentUuid;
	var group = $('#listView .thumb li[data-id="'+parentUuid+'"]').parent();
	if (group.data('disableprint')) {
		$('#PV_Btn_Print').remove();
	}
}

function generateMaterialFormat(currentId) {
	var formats = getObject(currentId).materialFormat;
	if (formats) formats = formats.split(',');
	else return null;
	
	var formatArray = new Array();
	for (var e in formats) {
		formatArray.push(' *.' + formats[e]);
	}
	return formatArray.join(';');
}

/* 更新材料版本 */
function updateMaterialVersion() {
	return ;	// 屏蔽
	
	var el = $('li.thumbItem.clearfix.active');
	var res = filextApi.getMaterialVersion({
		uuid : el.data('id')
	});
	if (res.success) {
		/* el.data('cv', res.data)不知为何不行 */
		el.attr('data-cv', res.data);
		el.children('.badge-version').text(res.data);
	}
}

function dragUpload(){
	holder = document.getElementById('holder');
	var dragWrap = document.getElementById('dragWrap');
	if (support.dnd) {
		dragWrap.ondragover = function() {
			$(holder).addClass('dragRegion');
			if (support.filereader === false) {
				alert('当前浏览器不支持filereader');
			}
			return false;
		};
		dragWrap.ondragend = function() {
			$(holder).removeClass('dragRegion');
			return false;
		};

		holder.ondragover = function() {
			return false;
		};
		holder.ondragend = function() {
			return false;
		};
		holder.ondrop = function(e) {
			e.preventDefault();
			$(holder).removeClass('dragRegion');
			readfiles(e.dataTransfer.files);
		}
	}
}
function dragToCategory(){
	$('#listView .content ul').dragsort({ 
		dragSelector: "li.item img", 
		dragOver: function(el){
			if(privilege.enableGroup){
				var dataId = $(el).attr('data-id');
				$('#collapse_' + dataId).slideDown(function(){
					initOffsetMap();
				});
			}
		},
		dragEnd: function(res) {
			var l = res.offset.left;
			var t = res.offset.top + 25;
			
			if(l > $('.guide').offset().left){
				/* 拖放分类 */
				for(var pid in offsetMap){
					var obj = offsetMap[pid];
					if(t >= obj.top && t <= obj.bottom && l >= obj.left && l <= obj.right){
						if(currentId == pid) return false;
						var info = '您确定放入'+ getObject(pid).bizTitle +'中吗？';
						if(confirm(info)){
							var res = filextApi.moveToFile({
								foid: pid,
								uuid: res.uuid
							});
							if(res.success){
								updateCurrentData(pid);
								$('#badge_'+ pid).html(getObject(pid).total);
							}else{
								alert(res.message);
							}
						}
						updateCateView();
						$('#listView .thumb .hover').removeClass('hover');
						return false;
					}
				}
				return false;
			}else{
				/* 排序 */
				var ids = [];
				$('#listView .content li.item').each(function(){
					var id = $(this).attr('data-id');
					ids.push(id);
				});
				var res = filextApi.doOrder({
					uuid: currentId,
					uuids: ids.join(',')
				});
				if(res.success){
					updateCateView();
				}else{
					alert(res.message);
				}
			}
			
		}, 
		dragBetween: true,
		placeHolderTemplate: "<li class='item'></li>" 
	});
}
	
function showFileInfo(id) {
	var obj = getObject(id);
	var size = obj.filesize || 0;
	var info = '<p>' + obj.filename + ' ，' + formateBitSize(size);
	if (obj.uploadTime) info += ' ，' + obj.uploadTime;
	if (obj.dealer) info += ' ，' + obj.dealer;
	info += '</p>';
	$('#fileInfo').html(info).show();
}

function getGroup(key, value){
	return getGroupValue(key, value, rootJson.group);
}

function getGroupValue(key, value, data){
	var data = data || [];
	for(var i = 0, len = data.length; i < len; i++){
		if(data[i][key] == value){
			return data[i];
		}else{
			var res = getGroupValue(key, value, data[i].children);
			if(res != null) return res;
		}
	}
}

function formatGroupData(data, items, code, pdata){
	for(var i = 0, len = data.length; i < len; i++){
		data[i].children = data[i].children || [];
		data[i].total = data[i].total || 0;
		
		if(data[i].code == code){
			data[i].children = items;
			data[i].total = items.length;
			if(pdata != null) pdata.total += data[i].total;
		}else{
			formatGroupData(data[i].children, items, code, data[i]);
		}
	}
}

function initListThumb() {
	if(privilege.enableGroup){
		if (!privilege.disablePrintCategory) $('.btn-print-category').removeClass('hidden');
		var groupData = rootJson.group;
		var groupMap = {};
		for(var i = 0, len = initData.length; i < len; i++){
			var item = initData[i];
			if(groupMap[item.code] == null)
				groupMap[item.code] = [item];
			else
				groupMap[item.code].push(item);
		}
		
		for(var key in groupMap){
			formatGroupData(groupData, groupMap[key], key);
		}
		
		compileTemplate(thumbGroupTemplate, {'groupData' : groupData}, '#listView .thumb');
	}else{
		compileTemplate(thumbTemplate, {'data' : initData}, '#listView .thumb');
	}
	
	$('#listView .thumb').scroll(function(){
		initOffsetMap();
	});
	if (privilege.disableUncategory) {
		$('.special-class').remove();
	}
	$('.'+privilege.disableUncategorys).remove();
	if (privilege.disableOperate) {
		$('.btn-group-add-category').remove();
		$('.category-edit').remove();
		$('.category-editForAll').remove();
		$('.category-edit-save').remove();
		// $('.category-save-edit').remove();
		$('.category-remove').remove();
		$('li.thumbItem').addClass('viewThumbItem');
		$('#listView .thumb .add-wrap').remove();
	} else {
		if (rootJson.allowAdd || rootJson.allowAdd == 'true')
			;
		else {
			$('.category-editForAll').remove();
			$('.category-edit-save').remove();
			// $('.category-save-edit').remove();
			$('.category-edit').remove();
			$('.category-remove').remove();
			$('li.thumbItem').addClass('viewThumbItem');
			$('#listView .thumb .add-wrap').remove();
		}
	}
	
	$('.prompt-modal').off('hidden.bs.modal').on('hidden.bs.modal',function() {
		$('.prompt-input').val('');
		$('.prompt-modal .has-error').removeClass('has-error')
	});
	
	$('.prompt-modal').off('shown.bs.modal').on('shown.bs.modal',function() {
		$('.prompt-input:first').focus();
		/* 初始化材料份数 */
		if($('#materialNumber').val()==""){
			groupForm.materialNumber.value = 1;
		}
	});
	
	$('.input-wrap input[type=text]').keyup(function(){
		var value = $(this).val();
		if(value == ''){
			$('.input-wrap').addClass('has-error');
		}else{
			$('.input-wrap').removeClass('has-error');
		}
	});
	
	$('.number-wrap input[type=text]').keyup(function(){
		var value = $(this).val();
		if(value != '' && !/^\d+$/.test(value)){
			$('.number-wrap').addClass('has-error');
			$('.number-wrap input[type=text]').focus();
		}else{
			$('.number-wrap').removeClass('has-error');
		}
	});
	
	$('.groupTitle').off('click').click(function(e) {
		currentId = 0;
		
		if($(this).hasClass('active')){
			$(this).removeClass('active');
			$(this).next('.collapse').slideUp();
		}else{
			var siblingTitle = $(this).parent('.panel').siblings('.panel').children('.groupTitle.active');
			siblingTitle.removeClass('active');
			siblingTitle.next('.collapse').slideUp();
			
			$(this).addClass('active');
			$(this).next('.collapse').slideDown(function(){
				initOffsetMap();
			});
		}
		
		$('#items').html('');
		$('#listView .thumbItem.active').removeClass('active');
	});
	
	$('.btn-group-add-category').off('click').click(function(e) {
		if(parent.icv.area!='zs'){
			$('#numberOfMaterial').hide();
		}
		preventBubble(e);
		var id = $(this).data('id');
		var group = getGroup('uuid', id);
		
		$('#groupTitle').html(group.description+" "+group.title);
		$('#modal-group .category-save-edit').data('code', group.code);
		$('#modal-group .category-save-edit').data('groupId', id);
		$('#modal-group .category-save-edit').data('type', 'add');
		$('#modal-group').modal({backdrop: 'static'});
	});
	
	$("#materialTime").keyup('input',function(e){  
		var materialTime = $('#materialTime').val();
		if(!checkDate(materialTime)){
			$('.date-group').addClass('has-error');
			$('.date-groupe input[type=datetime]').focus();
		}else{
			$('.date-group').removeClass('has-error');
		}
	});

	
	/* 点击分类 */
	$('li.thumbItem').off('click').click(function() {
		$('#listView .thumbItem.active').removeClass('active');
		$('li.thumbItem .icon-white').removeClass('icon-white');
		$(this).addClass('active');
		$(this).children('i').addClass('icon-white');
		currentId = $(this).data('id');
		updateContent();
	});
	/* 编辑分类 */
	$('.category-edit').off('click').click(function(e) {
		preventBubble(e);
		var id = $(this).parent().data('id');
		var obj = getObject(id);
		$('#modal-category .prompt-input').val(obj.bizTitle);
		$('#categoryNumber').val(obj.materialNumber);
		$('#categoryTime').val(obj.materialTime);	
		$('.category-edit-save').data('id', obj.uuid);
		$('#modal-category').modal({backdrop: 'static'});
	});
	/* 修改材料 */
	$('.category-editForAll').off('click').click(function(e) {
		preventBubble(e);
		if(parent.icv.area!='zs'){
			$('#numberOfMaterial').hide();
		}
		var id = $(this).parent().data('id');
		var obj = getObject(id);
		$('#groupTitle').html("修改材料名称");
		$('#categoryName').val(obj.bizTitle);
		$('#categoryName5').val(obj.remark);
		$('#materialNumber').val(obj.materialNumber);
		$('#materialTime').val(obj.materialTime.substring(0,10));	
		$('.category-save-edit').data('id', obj.uuid);
		$('#modal-group .category-save-edit').data('type', 'edit');
		
		if($(this).data('group')) {
			var groupId = $(this).parent().parent().data('id');
			$('#modal-group .category-save-edit').data('groupId', groupId);
		}
		
		$('#modal-group').modal({backdrop: 'static'});
	});
	$('.category-edit-save').off('click').click(function(e) {
		var title = $('#modal-category .prompt-input').val();
		if (title == '') {
			$('.input-wrap').addClass('has-error');
			$('.input-wrap input[type=text]').focus();
			return false;
		}
		var uuid = $(this).data('id');
		var res = filextApi.editCategory({
			uuid : uuid,
			title : title
		});
		if (res.success) {
			$('.prompt-modal').modal('hide');
			initPage();
		} else {
			alert(res.message);
		}
	});
	$('.category-edit-cancel').off('click').click(function(e) {
		$('#listView .thumb .add-wrap').show();
		$('#listView .thumb .edit-wrap').hide();
	});
	/* 删除分类 */
	$('.category-remove').off('click').click(function(e) {
		preventBubble(e);
		var id = $(this).parent().data('id');
		var obj = getObject(id);
		if (confirm('您确定要删除 “' + obj.bizTitle + '”吗？该操作将不可恢复！')) {
			var res = filextApi.removeCategory({
				uuid : obj.uuid
			});
			if (res.success) {
				initPage();
				if($(this).data('group')) {
					var groupId = $(this).parent().parent().data('id');
					locateCurrent(groupId);
				}
			} else {
				alert(res.message);
			}
		}
	});
	/* 添加分类 */
	$('.btn-add-category').off('click').click(function(){
		$(this).hide();
		$('#listView .thumb .input-wrap').show();
		$('#categoryName2').val('').focus();
	});
	$('.category-cancel').off('click').click(function(){
		$('#listView .thumb .input-wrap').removeClass('has-error').hide();
		$('.btn-add-category').show();
	});
	
	$('.category-save-edit').off('click').click(function(){
		var title = $('#categoryName').val();
		var remark = $('#categoryName5').val();
		if(title == ''){
			$('.input-wrap').addClass('has-error');
			$('.input-wrap input[type=text]').focus();
			return false;
		}
		
		if(parent.icv.area=='zs'){
			var materialTime = $('#materialTime').val();
			if(materialTime == ''){
				$('.date-group').addClass('has-error');
				$('.date-groupe input[type=datetime]').focus();
				return false;
			}
			
			if(!checkDate(materialTime)){
				alert('时间格式不正确，正确格式为2015 05 05 或者2015-05-05');
				return false;
			}else{
				modifyDate(materialTime);
			}
			
			
			var materialNumber = $('#materialNumber').val();
			if(isNaN(materialNumber)){
				$('#materialNumber').addClass('has-error');
				$('#materialNumber').focus();
				alert("材料份数只能是数字，请重新输入");
				return false;
			}
		}

/*
 * if($('#modal-group .has-error').length > 0) { $('#modal-group
 * .has-error:first input[type=text]').focus(); return false; }
 */
		var uuid, isAdd = false;
		if($(this).data('type')=='edit'){
			uuid = $(this).data('id');
		}else{
			uuid = filextApi.uuid;
			isAdd = true;
		}
		var data = {
			uuid : uuid,
			title : title,
			code : $(this).data('code'),
			materialNumber : groupForm.materialNumber.value,
			materialTime : groupForm.materialTime.value,
			remark:remark,
			isAdd : isAdd
		};
		
		var res = filextApi.addCategory(data);
		if(res.success){
			$('.prompt-modal').modal('hide');
			initPage();
			var groupId = $(this).data('groupId');
			locateCurrent(groupId, {
				type : isAdd ? 'add' : 'edit'
			});
		}else{
			alert(res.message);
		}
	});
	
	$('.category-save').off('click').click(function(){
		var title = $('#categoryName2').val();
		if(title == ''){
			$('.input-wrap').addClass('has-error');
			$('.input-wrap input[type=text]').focus();
			return false;
		}
		
		if($('#modal-group .has-error').length > 0) {
			$('#modal-group .has-error:first input[type=text]').focus();
			return false;
		}
		
		var data = {
			uuid : filextApi.uuid,
			title : title,
			code : $(this).data('code'),
			materialNumber : groupForm.materialNumber.value,
			materialTime : groupForm.materialTime.value,
			isAdd : true
		};
		
		var res = filextApi.addCategory(data);
		if(res.success){
			$('.prompt-modal').modal('hide');
			initPage();
		}else{
			alert(res.message);
		}
	});
	
}

function locateCurrent(groupId, config){
	var $curCollapse = $('#collapse_' + groupId);
	
	var upParent = $curCollapse.parent().parent();
	while(upParent.hasClass('collapse')){
		upParent.slideDown();
		upParent.prev('.groupTitle').addClass('active');
		upParent = upParent.parent().parent();
	}
	
	$curCollapse.prev('.groupTitle').addClass('active');
	$curCollapse.slideDown();
	
	if(config.type == 'add'){
		var $cur = $('#collapse_' + groupId + ' li').last();
		$cur.addClass('active');
		currentId = $cur.data('id');
		updateContent();
	}else if(currentId != 0 && currentId != null){
		$('#collapse_' + groupId + ' li').each(function(){
			if(currentId == $(this).data('id')){
				$(this).addClass('active');
				return;
			}
		});
		updateContent();
	}
}

/* 分类元素页面坐标 */
function initOffsetMap(){
	$('#listView .thumb li').each(function() {
		var id = $(this).attr('data-id');
		var obj = {};
		obj.top = $(this).offset().top;
		obj.left = $(this).offset().left;
		obj.right = obj.left + $(this).width();
		obj.bottom = obj.top + $(this).height();

		offsetMap[id] = obj;
	});
}

function initEditView() {
	$('#listView').hide();
	$('#detailView').hide();
	$('#editView').show();
	
	var obj = getObject(editId);
	$('#filename').val(obj.title);
	
	editRadio = 1;
	editType = '';
	rotateDegree = 0;
	isApplied = false;
	clearLine();
	destoryDraw();
	if(jcropApi != null) jcropApi.destroy();
	
	/* editCanvas */
	var $table = $('#editView table');
	
	editCanvas = {
		width : $table.width() - 20,
		height : $('#editView').height() - 55
	};
	
	loadTargetImage(obj.editFile);
	
	/* 快捷键监听 */
	document.onkeydown = function (event) {
		if(event.shiftKey){
			var keyCode = event.keyCode;
			if (keyCode == 81) {
				shutdown();  // 返回
			}else if(keyCode == 82){
				refreshEditView();  // 重新编辑
			}else if(keyCode == 67){
				crop();  // 裁剪
			}else if(keyCode == 68){
				rotate();  // 旋转
			}else if(keyCode == 69){
				correct(); // 纠偏
			}else if(keyCode == 65){
				apply();  // 预览
			}else if(keyCode == 83){
				save();  // 保存
			}
		} 
	};
	
	/* 刷新 */
	$('.refresh').off('click').click(function() {
		refreshEditView();
	});
	
	$('#editView select:first').focus();
}

/* 分页显示 */
function showPages(isDetail) {
	// 当前页码对象
	var pageMap = isDetail ? detailPageMap : thumbPageMap;
	var defaultPage = { 'pageIndex':0, 'pageSize':0 };
	var page = pageMap[currentId] || defaultPage;
	var items = pidMap[currentId] || [];
	var dataNumber = items.length;
	var pageNumber = page.pageIndex * page.pageSize;
	var hasMore = false;
	
	// 数据长度大于页数则需要分页
	if (dataNumber > pageNumber) {
		hasMore = true;
		var pageContent = [];
		for (var i=0; i<pageNumber; i++)
			pageContent.push(items[i]);
		
		if (isDetail) editId = items[pageNumber].uuid;
		items = pageContent;
	}
	
	if (isDetail) {
		// 大图页数大于缩略图页数时增加缩略图页数保持一致
		var thumbPage = thumbPageMap[currentId] || defaultPage;
		var pageSize = thumbPage.pageSize;
		var len = items.length;
		if (len > thumbPage.pageIndex*pageSize)
			thumbPage.pageIndex = Math.floor(len/pageSize) + (len%pageSize==0?0:1);
		
		compileTemplate(viewTemplate, {'data' : items}, '#detailView .content');
		if (needHideThumb()) {
			compileTemplate(viewThumbTemplate2, {'data' : pidMap[currentId]}, '#detailView .thumb');
			$('#detailView .thumb ul li').css('text-align', 'left');
		} else {
			compileTemplate(viewThumbTemplate, {'data' : items}, '#detailView .thumb');
			$('#detailView .thumb ul li').css('text-align', 'center');
		}
	} else {
		compileTemplate(template, {'data' : items}, '#listView .content');
	}
	
	if (hasMore) {
		var selector = isDetail ? '#detailView .content' : '#dragWrap';
		$(selector).append('<div style="width:100%;text-align:center">'
				+ '<button class="btn btn-info btn-load-more" type="button">'
				+ '<i class="icon-white icon-picture"></i>(' + pageNumber + '/' + dataNumber + ')点此加载更多...</button>'
				+ '</div>');
		
		$('.btn-load-more').off('click').click(function() {
			var selecteds = [];
			if (isDetail) {
				$('#detailView input:checked').each(function() {
					selecteds.push($(this).parent().parent().data('id'));
				});
			}
			
			pageMap[currentId].pageIndex = page.pageIndex + 1;
			if (isDetail) initDetailView();
			else updateContent();
			
			if (isDetail) {
				for (var e in selecteds) {
					$('#detailView li[data-id=' + selecteds[e] + '] input').attr('checked', 'checked');
				}
			}
		});
	} else {
		$('.btn-load-more').remove();
	}
}

function modifyButtonStatus() {
	var disablePrintPics = false;
	
	$('#detailView .content li').each(function() {
		var id = $(this).data('id');
		var parentUuid = getObject(id).parentUuid;
		var group = $('#listView .thumb li[data-id="'+parentUuid+'"]').parent();
		if (group.data('disableprint')) {
			$(this).find('.operate .print').remove();
			disablePrintPics = true;
		}
		if (group.data('disabledownload')) {
			$(this).find('.operate .download').remove();
		}
		
		$(this).find('input[type="checkbox"]').attr('checked', $('#detailView .thumb li[data-id="'+id+'"] input[type="checkbox"]').attr('checked'));
	});
	
	disablePrintPics ? $('.printPics').addClass('hidden') : $('.printPics').removeClass('hidden');
	$('#detailView .guide .toUp').css('top', $('#detailView .operate').height()+10);
}

function initDetailView() {
	// 由于加入了分页，双击的图片可能不在大图当前页内，需要计算大图需要的页码
	var items = pidMap[currentId];
	var page = detailPageMap[currentId];
	var pageSize = page.pageSize;
	var pageNumber = page.pageIndex * pageSize;
	for (var i=0,l=items.length; i<l; i++) {
		if (editId == items[i].uuid) {
			var len = i + 1;
			if (len > pageNumber) {
				page.pageIndex = Math.floor(len/pageSize) + (len%pageSize==0?0:1);
				break;
			}
		}
	}
	
	showPages(true);
	imageErrorHandler('#detailView img');
	
	$('#listView').hide();
	$('#detailView').show();
	$('#editView').hide();
	
	modifyButtonStatus();
	rotateDegree = 0;

	$('a.media').media({width : '100%', height : 600});
	
	var listHeight = 1;
	var thumbHeight = 1;
	var thumbUlHeight = 1;
	$('#detailView .guide img').load(function() {
		thumbHeight = $('#detailView .guide').height() - 90;
		thumbUlHeight = $('#detailView .thumb ul').height();
	});
	var lw = $('#detailView .content ul').width();
	$('#detailView .content img').load(function() {
		var w = $(this).width();
		if (w > lw) {
			$(this).css('width', '100%');
		}
		listHeight = $('#detailView .content ul').height();
	});
	if(getObject(editId).type != 'Image'){
		$('#detailView .content').scrollTop(document.getElementById('li_'+editId).offsetTop);
	}else{
		$('#detailView .content #li_'+ editId+' img').load(function() {
			// 默认滚动到双击的图片
			$('#detailView .content').scrollTop(document.getElementById('li_'+editId).offsetTop);
		});
	}
	/* 刷新 */
	$('.refresh').off('click').click(function() {
		$('#refreshing').fadeIn();
		initDetailView();
		$('#detailView .content').scrollTop(0);
		setTimeout(function() {
			$('#refreshing').fadeOut('slow');
		}, 1000);
	});
	$('#detailView .content ul li').hover(function() {
		$(this).children('.operate').slideToggle();
	}, function(){
		$(this).children('.operate').hide();
	});
	/* 查看大图 */
	$('.bigImage').off('click').click(function() {
		/* removeBigContainer(); */
		
		var id = $(this).parent().data('id');
		var obj = getObject(id);
		var selector = '#detailView #li_' + id + ' .image-outline';
		/* parent.icv.viewBigImage(obj.file); */
		$(selector).attr('title', obj.filename);
		$(selector + ' img').attr('data-original', obj.file);
		picasaGallery(selector);
	});
	/* pdf全屏显示 */
	$('.bigPdf').off('click').click(function() {
		var url = $(this).parents('li').data('url');
		parent.icv.viewBigPdf(url);
		var pdfHeight=$(window.parent.document.body).height()-6;// iframe
																// border=2
		$(window.parent.document).find('a.media').media({width : '100%', height : pdfHeight});
	});
	/* 旋转图片 */
	$('.viewRotate').off('click').click(function() {
		var id = $(this).parent().data('id');
		var obj = getObject(id);

		rotateDegree += 90;

		var res = filextApi.doRotate({
			uuid : id,
			node : obj.node,
			uuidExt : obj.uuidExt,
			degree : rotateDegree
		});
		if (res.success) {
			$('#img_' + id).attr('src', getPath(obj.file, res.data.fileName));
		}
	});
	/* 下载图片 */
	$('.download').off('click').click(function() {
		var id = $(this).parent().data('id');
		var obj = getObject(id);
		window.open(obj.file + '&d=true');
	});
	/* 打印图片 */
	$('.print').off('click').click(function() {
		var id = $(this).parent().data('id');
		var images = {};
		images[id] = '<img src="' + getObject(id).file + '" />';
		printImages(images);
	});
	$('#detailView .thumb a').off('click').click(function(e) {
		$('#detailView .thumb a').removeClass('hover');
		$(this).addClass('hover');
	});
	
	$('#detailView .thumb li').off('click').click(function() {
		var uuid = $(this).data('id');
		$('#detailView .content').scrollTop(document.getElementById('li_'+uuid).offsetTop);
	});
	
	var viewIndex = 0;
	var distance = 0;
	/* 向上浏览 */
	$('.toUp').off('click').click(function() {
		if (viewIndex > 0) {
			var h = $('#detailView .thumb li').eq(viewIndex - 1).height();
			distance += h;
			if(distance > 0) distance = 0;
			$('#detailView .thumb ul').css('top', distance + 'px');
			viewIndex--;
		}
	});
	/* 向下浏览 */
	$('.toDown').off('click').click(function() {
		if(thumbHeight > thumbUlHeight) return false;
		var len = $('#detailView .thumb li').length;
		if (viewIndex < len - 1) {
			var h = $('#detailView .thumb li').eq(viewIndex).height();
			distance -= h;
			if(Math.abs(distance) > (thumbUlHeight-thumbHeight)){
				distance = thumbHeight - thumbUlHeight;
			}
			$('#detailView .thumb ul').css('top', distance-70+'px');
			viewIndex++;
		}
	});
	/**/
	$('#detailView .content').scroll(function() {
		$('#detailView .content li').each(function(i, el) {
			var t = $(el).offset().top;
			var h = $(el).height() / 2;
			var id = $(el).data('id');
			
			if (t < 0 && Math.abs(t) > h) {
				$('#detailView .thumb a').removeClass('hover');
				$('#thumb-' + id).next().children('a').addClass('hover');
			}
			if (t < 0 && Math.abs(t) <= h) {
				$('#detailView .thumb a').removeClass('hover');
				$('#thumb-' + id).children('a').addClass('hover');
			}
		});

		$('#detailView .thumb a').each(function(i, el) {
			if ($(el).hasClass('hover')) {
				viewIndex = i;
				return;
			}
		});

		distance = -($(this).scrollTop() / listHeight * thumbUlHeight);
		if(Math.abs(distance) > (thumbUlHeight-thumbHeight)){
			distance = thumbHeight - thumbUlHeight;
		}
		if(distance > 0) distance = 0;
		$('#detailView .thumb ul').css('top', distance + 'px');
	});
	/* 屏蔽img右键菜单 */
	$("img").bind('contextmenu', function(e) {
		return false;
	});
	$('#detailView .thumb li input[type="checkbox"]').off('change').change(function(e) {
		var id = $(this).parent().parent().data('id');
		$('#detailView .content li[data-id="'+id+'"] input[type="checkbox"]').attr('checked', e.target.checked);
	});
	$('#detailView .content li input[type="checkbox"]').off('change').change(function(e) {
		var id = $(this).parent().parent().data('id');
		$('#detailView .thumb li[data-id="'+id+'"] input[type="checkbox"]').attr('checked', e.target.checked);
	});
}

function compileTemplate(source, data, container) {
	var template = Handlebars.compile(source);
	Handlebars.registerHelper("compare", function(v1, v2, options) {
		if (v1 == v2) {
			return options.fn(this);
		} else {
			return options.inverse(this);
		}
	});

	Handlebars.registerHelper("limit", function(v1, v2, options) {
		if (v1.length > v2) {
			return v1.substring(0, v2) + '...';
		}
		return v1;
	});
	
	Handlebars.registerHelper('groupList', function(items, options) {
		return templateGroupList(items);
	});
	
	$('' + container).html(template(data));
	if (container=='#listView .content' && ($('body').height()!=0))
		$('#listView .thumb').height($('body').height()-$('.operate').height());
}

function templateGroupList(items){
	items = items || [];
	var groupList = "";
	for(var i = 0, len = items.length; i < len; i++){
		groupList += "<div class='panel catepanel'>";
		
		var item = items[i];
		if (typeof(item.children) == 'undefined') { 
			item.children=0;
		}
		var children = item.children;
		var clen = children.length;
		if (typeof(clen) == 'undefined') { 
			clen=0;
		}
		var className = '';
		if(clen > 0 && children[0].classifyNumber != null)
			className = "groupsTitle";
		
		groupList += "<div class='groupTitle "+ className +"' data-id='"+ item.uuid +"'>";
			
		groupList += "<abbr title='"+ item.title +"'>"+ (item.description == null ? "" : item.description + "&nbsp;") + item.title +"</abbr>";
		
		if(clen == 0  ){
			groupList += "<button class='btn-group-add-category pull-right' type='button' data-id='"+ item.uuid +"'>+</button>";
		}else if(children[0].classifyNumber == null){
			groupList += "<button class='btn-group-add-category pull-right' type='button' data-id='"+ item.uuid +"'>+</button>";	
		}
		if (typeof(item.total) == 'undefined') { 
			item.total=0;
		}
		groupList += "<span class='badge pull-right'>"+ item.total +"</span>"+
			"</div>"+
			"<div id='collapse_"+ item.uuid +"' class='collapse'>";
		
		if(clen > 0){
			if(children[0].classifyNumber == null){
				groupList += "<ul data-id='"+ item.uuid +"' data-disableprint='" + item.disablePrint + "' data-disabledownload='" + item.disableDownload + "'>";
				for(var j = 0; j < clen; j++){
					var child = children[j];
					if (typeof(child.total) == 'undefined') { 
						child.total=0;
					}
					groupList += "<li class='thumbItem clearfix "+ child.type +"' data-id='"+ child.uuid +
						"' data-title='" + child.bizTitle + "' data-base-file='"+ child.hasBaseFile +"'>" +
						"<span class='caret'></span>"+ child.bizTitle + (child.required == true ? "<i class='star'>*</i>" : "") +
						"<span id='badge_"+ child.uuid +"' class='badge pull-right'>"+ child.total +"</span>";
					
					if(child.type == 'special-proof')
						groupList += "<i class='glyphicon glyphicon-remove category-remove' data-group='true'></i>"+
									"<i class='glyphicon glyphicon-edit category-editForAll' data-group='true'></i>";
					
					groupList += "</li>";
				}
				groupList += "</ul>";
			}else{
				groupList += templateGroupList(children);
			}
		}
		
		groupList += "</div>";
		
		groupList += "</div>";
	}
	return groupList;
}

function getObject(id) {
	return idMap[id] == null ? {} : idMap[id];
}

/* 返回 */
function shutdown(){
	if (currentId == '_allDetail') {
		delete pidMap[currentId];
		delete detailPageMap[currentId];
		currentId = getCurrentId();
	}
	
	updateContent();
	$('#listView').show();
	$('#editView').hide();
	$('#detailView').hide();
	document.onkeydown = null;
	clearLine();
	destoryDraw();
}

/* 生成PDF */
function buildAndDownloadPdf() {
	var canceled = false;
	$('#modal-build-pdf').modal({backdrop: 'static'});
	/*
	 * $('.build-cancel').off('click').click(function() { canceled = true;
	 * showTip('操作已取消！'); setTimeout(function() {
	 * $('#modal-build-pdf').modal('hide'); }, 1000); });
	 */
	
	setTimeout(function() {
		var res = buildPdf();
		
		if (res.success && !canceled) {
			var form = document.createElement("form");
			form.method = 'post';
			form.action = res.data;
			
			document.body.appendChild(form); 
			form.submit();
			document.body.removeChild(form);
			
			showTip('装订完成！');
			$('#modal-build-pdf').modal('hide');
		} else {
			showTip('生成失败！');
		}
	}, 500);
}

function buildPdf(_config) {
	var config = _config || {};
	
	if (!(config.bizId && config.materialCode)) {
		var all = currentId == '_allDetail';
		var uuid = $('li.thumbItem.clearfix.active').data('id');
		var filter = [];
		$('#detailView input:checked').each(function() {
			filter.push($(this).parent().parent().data('id'));
		});
		if (all) {
			var uuids = [];
			$('li.thumbItem.clearfix').each(function() {
				uuids.push($(this).data('id'));
			});
			uuid = uuids.join(',');
		}
		config.uuids = uuid;
		config.filter = filter.join(',');
		config.all = all;
	}
	
	return filextApi.buildPdf(config);
}

function refreshEditView(){
	$('#refreshing').fadeIn();
	initEditView();
	setTimeout(function() {
		$('#refreshing').fadeOut('slow');
	}, 1000);
}

function crop(){
	clearLine();
	destoryDraw();
	
	if(editType != ''){
		showTip('你有未保存的操作，请先保存');
		return false;
	}
	editType = 'crop';
		
	$('#target').Jcrop({
		bgOpacity: 0.4,
	    bgColor: 'black',
	    addClass: 'mycrop',
	    onChange: function(c){
	    	$('.apply').attr('data-x', c.x).attr('data-y', c.y).attr('data-w', c.w).attr('data-h', c.h);
	    }
	},function(){
		jcropApi = this;
		jcropApi.setSelect([0,0,300,200]);
		$('.apply').attr('data-x', '0').attr('data-y', '0').attr('data-w', '300').attr('data-h', '200');
		jcropApi.setOptions({ bgFade: true, allowSelect: false, minSize: [ 50, 50 ]});
		jcropApi.ui.selection.addClass('jcrop-selection');
	});
}

function rotate(){
	clearLine();
	destoryDraw();
	
	if(editType != '' && editType != 'rotate'){
		showTip('你有未保存的操作，请先保存');
		return false;
	}
	editType = 'rotate';
		
	var obj = getObject(editId);
	
	rotateDegree += 90;
	var res = filextApi.doRotate({
		uuid: editId,
		node: obj.node,
		uuidExt: obj.uuidExt,
		degree: rotateDegree
	});
	if(res.success){
		var src = getPath(obj.editFile, res.data.fileName);
		loadTargetImage(src);
	}
}

function correct(){
	if(editType != '' && editType != 'correct'){
		showTip('你有未保存的操作，请先保存');
		return false;
	}
	
	clearLine();
	initDraw();
}

/* 应用 */
function apply() {
	var $target = $('.apply');
	
	if($('.linepoint').length > 0) editType = 'correct';
	
	if (editType == '') {
		return false;
	}
	
	var src = $('#target').attr('src');
	var w = $target.attr('data-w');
	var h = $target.attr('data-h');
	
	var _uuid = editId;

	var obj = getObject(editId);
	var node = obj.node;
	var uuidExt = obj.uuidExt;
	
	var res = null;
	if (editType == 'crop') {
		/* 裁剪 */
		var x = $target.attr('data-x');
		var y = $target.attr('data-y');
		var data = {
			node : node,
			uuid : _uuid,
			uuidExt : uuidExt,
			x : parseInt(x / editRadio),
			y : parseInt(y / editRadio),
			width : Math.round(w / editRadio),
			height : Math.round(h / editRadio)
		};
		
		res = filextApi.doCrop(data);
		if (res.success) {
			src = getPath(src, res.data.fileName);
			loadTargetImage(src);
			isApplied = true;
			jcropApi.destroy();
		} else {
			alert(res.message);
		}
	} else if (editType == 'resize') {
		/* 缩放 */
		var data = {
			node : node,
			uuid : _uuid,
			uuidExt : uuidExt,
			width : w,
			height : h
		};
		res = filextApi.doResize(data);
		if (res.success) {
			isApplied = true;
		} else {
			alert(res.message);
		}
	}else if(editType == 'correct'){
		res = filextApi.doCorrect({
			node : node,
			uuid : _uuid,
			uuidExt : uuidExt,
			x1: x1,
			y1: y1,
			x2: x2,
			y2: y2
		});
		if(res.success){
			var src = getPath(obj.editFile, res.data.fileName);
			loadTargetImage(src);
			clearLine();
		}else{
			showTip('纠偏失败');
		}
	}
	if(res == null) return true;
	return res.success;
}
/* 保存 */
function save() {
	clearLine();
	destoryDraw();
	
	if (editType == '') {
		showTip('没有文件操作可保存！');
		return false;
	}
	
	$('#refreshing').fadeIn('fast');
	if (editType == 'crop' || editType == 'resize') {
		if(!isApplied) {
			var res = apply();
			if(res == false) {
				initEditView();
				$('#refreshing').fadeOut('slow');
				return false;
			}
		}
	}
	
	var obj = getObject(editId);
	var data = {
		node : obj.node,
		uuid : obj.uuid,
		uuidExt : obj.uuidExt,
		title : $('#filename').val()
	};
	var res = filextApi.doSave(data);
	editId = res.data.uuid;		// TODO modified by Lintel
	if (res.success) {
		updateCurrentData();
	} else {
		alert(res.message);
	}
	initEditView();
	updateMaterialVersion();
	setTimeout(function() {
		$('#refreshing').fadeOut('slow');
		if (res.success) showTip("保存成功！");
	}, 1000);
}
/* 提示 */
function showTip(msg){
	$('#showtip').html(msg).fadeIn();
	setTimeout(function() {
		$('#showtip').fadeOut(2000);
	}, 1000);
}
/**
 * 
 * type:1-采集 2-替换
 */
function uploadModalShow(){
	scrollTop();
	$('#upload-default .basefile').hide();
	if (uploadType == 'high-shoot') {
		if (CHighshoot.available) {
			openCHighShoot(); return ;
		} else {
			if (isIE()) {
				openHighShoot(); return ;
			} else {
				$('#high-shoot_f').attr('src','high-shoot.html').show();
				$('#modal-upload').addClass('large-modal');
			}
		}
	} else if (uploadType == 'scanning'){
		$('#scanning_f').attr('src','scan.html').show();
		$('#modal-upload').addClass('large-modal');
	}
	$('#modal-upload').modal({backdrop: 'static'});
}

function scrollTop() {
	var parentDocument = $(window.parent.document.body);
	var icvContainer = parentDocument.find('div#'+parent.icv.divId);
	if (icvContainer.length > 0)
		parentDocument.scrollTop(icvContainer.offset().top);
}

/* 打开高拍客户端 */
function openCHighShoot() {
	var selectedItem = $('li.thumbItem.active');
	var url = filextApi.WEB_APP + '/filext-api?';
	var params = {
		method : 'createAndPutStorageObject',
		uuid : selectedItem.data('id'),
		dealer : filextApi.config.config.dealer,
		batch : filextApi.config.config.batch
	};
	if (isReplace) {
		params.method = 'putStorageContent';
		params.uuid = replaceData.uuid;
		params.replace = true;
	}
	
	$('#globalMask').show();
	CHighshoot.setParams({
		reqUrl : url+$.param(params),
		baseFileName : selectedItem.data('title')
	}).setAfterClosed(function() {
		updateAfterUpload(true);
		$('#globalMask').hide();
	}).start();
}

/* 打开高拍界面 */
function openHighShoot() {
	window.showModalDialog('high-shoot.html', window, "dialogHeight:600px;dialogWidth:1000px;");
}

function backToDefault(){
	if($('#uploadType').is(':checked')){
		uploadType = '';
	}
	$('.upload-view').hide();
	$('#modal-upload').removeClass('large-modal');
	$('#upload-default').show();
	$('#finance ul').html('');
	$('#finance input[name=finance]').val('');
}

/* 上传后更新 */
function updateAfterUpload(res) {
	if (res) {
		$('#modal-upload').modal('hide');
		$('#native-file').val('');
		updateCateView();
		updateMaterialVersion();
	} else {
		var msg = isReplace?'替换失败':'上传失败';
		alert(msg);
	}
}

/* 图片缩放 */
function changeImage(ratio){
	$('#target').css({
		width : editTarget.width * ratio + 'px',
		height : editTarget.height * ratio + 'px'
	});
}
/* 图片加载 */
function loadTargetImage(src){
	var image = new Image();
	image.src = src;
	image.onload = function(){
		$('#targetWrap').html('<img src="' + src + '" id="target" style="display:none;" draggable="false" data-error="0"/>');
		imageErrorHandler("#target");
		editTarget = {
			width : image.width,
			height : image.height
		};
		adaptiveImage();
		
		/* 图片缩放 */
		$('#slider').slider({
			range : "min",
			min : 1,
			max : 100,
			value : 50 * editRadio,
			slide : function(event, ui) {
				if(editType != '' && editType != 'resize'){
					showTip('你有未保存的操作，请先保存');
					return false;
				}
				editType = 'resize';
				var radio = ui.value / 50;
				$('#target').css({
					width : editTarget.width * radio + 'px',
					height : editTarget.height * radio + 'px'
				});
				$('.apply').attr('data-w', $('#target').width()).attr('data-h', $('#target').height());
			}
		});
		
		$('#target').show();
		image.onload = null;
	};
	image.onerror = function() {
		image.src = 'style/images/broken.jpg';
	}
}
/* 图片自适应 */
function adaptiveImage(){
	var iw = editTarget.width;
	var ih = editTarget.height;
	
	if(iw > editCanvas.width){
		var radio = editCanvas.width / iw;
		ih = ih * radio;
		iw = editCanvas.width;
	}
	if(ih > editCanvas.height){
		var radio = editCanvas.height / ih;
		iw = iw * radio;
		ih = editCanvas.height;
	}
	
	editRadio = iw / editTarget.width;
	
	$('#target').css({
		width : iw + 'px',
		height : ih + 'px'
	});
}
/* 图片自适应宽 */
function adaptiveWide(){
	var iw = editTarget.width;
	var ih = editTarget.height;
	
	if(editTarget.width > editCanvas.width){
		editRadio = editCanvas.width / editTarget.width;
		iw = editCanvas.width;
		ih = ih * editRadio;
	}
	$('#target').css({
		width : iw + 'px',
		height : ih + 'px'
	});
}
/* 图片自适应高 */
function adativeHeight(){
	var iw = editTarget.width;
	var ih = editTarget.height;
	
	if(editTarget.height > editCanvas.height){
		editRadio = editCanvas.height / editTarget.height;
		ih = editCanvas.height;
		iw = iw * editRadio;
	}
	$('#target').css({
		width : iw + 'px',
		height : ih + 'px'
	});
}

/* 替换图片路径 */
function getPath(src, fileName){
	return src.replace(/(\.|-|\w)+\-(jpg|jpeg|gif|png|tif|bmp)/, fileName);
}

function readfiles(files) {
	var fileLimitSize = transformToBit(filextApi.fileSizeLimit);
	for (var i = 0; i < files.length; i++) {
		var file = files[i];
		if (file.type == '' || file.type == 'application/x-msdownload'
				|| file.type == 'text/html') {
			alert('文件类型不支持');
			return false;
		}
		if (file.size > fileLimitSize) {
			alert('文件大小不能超过' + filextApi.fileSizeLimit);
			return false;
		}
		previewfile(file);
		var res = filextApi.requestBeforeUpload({
			uuid : currentId,
			filename : file.name,
			filesize : file.size
		});

		var url = '/filext-api?' + $.param(res || {});

		var xhr = new XMLHttpRequest();
		xhr.open('POST', url);
		var boundary = '----------ei4GI3gL6gL6ae0ei4cH2Ef1gL6GI3';
		xhr.setRequestHeader('Content-Type', 'multipart/form-data; boundary='+ boundary);

		xhr.onload = function() {
			$(holder).children('img').attr('src', 'style/images/upload.jpg');
		};
		xhr.send(file);
	}
	updateCateView();
}

function previewfile(file) {
	if (support.filereader === true) {
		var reader = new FileReader();
		reader.onload = function(event) {
			$(holder).children('img').attr('src', event.target.result);
		};
		reader.readAsDataURL(file);
	}
}

function transformToBit(filesize) {
	var bitsize = 0;
	var size = filesize.substring(0, filesize.length - 2);
	
	if (filesize.indexOf('GB') > 0) {
		bitsize = size * Math.pow(1024, 3);
	} else if (filesize.indexOf('MB') > 0) {
		bitsize = size * Math.pow(1024, 2);
	} else if (filesize.indexOf('KB') > 0) {
		bitsize = size * 1024;
	} else {
		bitsize = filesize.substring(0, filesize.length - 1);
	}
	
	return bitsize;
}
function formateBitSize(bitsize){
	var filesize = '0B';
	
	if (bitsize < 1024) {
		filesize = bitsize + 'B';
	} else if (bitsize < Math.pow(1024, 2)) {
		filesize = twoFixed(bitsize / 1024) + "KB";
	} else if (bitsize < Math.pow(1024, 3)) {
		filesize = twoFixed(bitsize / Math.pow(1024, 2)) + "MB";
	} else {
		filesize = twoFixed(bitsize / Math.pow(1024, 3)) + "GB";
	}
	
	return filesize;
}
function twoFixed(num) {
	return num.toFixed(2);
}
function preventBubble(e) {
	if (e.stopPropagation) {
		e.stopPropagation();
	} else {
		e.cancelBubble = true;
	}
}	

function initDraw(){
	var area = document.getElementById('lineArea');
	
	area.onmousedown = function(e){
		isDown = true;
		var e = e || window.event;
		x1 = e.clientX;
		y1 = e.clientY;
		drawLine(x1, y1, e.clientX, e.clientY);
	};
			
	area.onmousemove = function(e){
		if(!isDown) return false;
		var e = e || window.event;
		clearLine();
		drawLine(x1, y1, e.clientX, e.clientY);
	};
			
	area.onmouseup = function(e){
		isDown = false;
		var e = e || window.event;
		x2 = e.clientX;
		y2 = e.clientY;
		drawLine(x1, y1, x2, y2);
	};
}

function destoryDraw(){
	var area = document.getElementById('lineArea');
	if (area != null) {
		area.onmousedown = null;
		area.onmousemove = null;
		area.onmouseup = null;
	}
}

function drawPoint(x, y){
	var oDiv = document.createElement('div');
	oDiv.style.position = 'absolute';
	oDiv.style.height = '1px';
	oDiv.style.width = '1px';
	oDiv.style.backgroundColor = 'red';
	oDiv.className = 'linepoint';
	oDiv.style.left= x + 'px';
	oDiv.style.top = y + 'px';
	return oDiv;
}
		
function drawLine(x1,y1,x2,y2){
	// 画一条直线的方法
	var x = x2-x1;
	var y = y2-y1;
	var frag = document.createDocumentFragment();
	
	if(Math.abs(y) > Math.abs(x)){
		// 那个边更长，用那边来做画点的依据（就是下面那个循环），如果不这样，当是一条垂直线或水平线的时候，会画不出来
		if(y>0){
			// 正着画线是这样的
			for(var i=0; i<y; i++){
				var width = x/y*i;
				// x/y是直角两个边长的比，根据这个比例，求出新坐标的位置
				frag.appendChild(drawPoint(width+x1,i+y1));
			}
		}else if(y<0){
			// 有时候是倒着画线的
			for(var i=0; i>y; i--){
				var width=x/y*i;
				frag.appendChild(drawPoint(width+x1,i+y1));
			}
		}
	}else {
		if(x>0){
			// 正着画线是这样的
			for(var i=0;i<x;i++){
				var height=y/x*i;
				frag.appendChild(drawPoint(i+x1,height+y1));
			}
		}else if(x<0){
			// 有时候是倒着画线的
			for(var i=0;i>x;i--){
				var height=y/x*i;
				frag.appendChild(drawPoint(i+x1,height+y1));
			}
		}
	}
	document.body.appendChild(frag);
}

function clearLine(){
	$('.linepoint').remove();
}

function checkDate(dateStr){
	var date = dateStr;
    var result = date.match(/^(\d{4})([-, ,]|\/)(\d{1,2})\2(\d{1,2})$/);
    if(!result) return false;
    else{
    	var d = new Date(result[1], result[3] - 1, result[4]);
	   // $('#materialTime').val(d.getFullYear()+'-'+(d.getMonth() + 1) + '-' +
		// d.getDate());
	    return (d.getFullYear() == result[1] && (d.getMonth() + 1) == result[3] && d.getDate() == result[4]);
    }
}
function modifyDate(dateStr){
	var date = dateStr;
    var result = date.match(/^(\d{4})([-, ,]|\/)(\d{1,2})\2(\d{1,2})$/);
    if(!result) return false;
    else{
    	var d = new Date(result[1], result[3] - 1, result[4]);
	    $('#materialTime').val(d.getFullYear()+'-'+(d.getMonth() + 1) + '-' + d.getDate());
    }
}



//flash版本检测
function CheckFlashPlayer(){
    var version = swfobject.getFlashPlayerVersion();
    if (document.getElementById && version["major"] > 0) {
        if(version['major']<10) {
            alert("你的flash播放器版本过低!请安装flash Player 10版本!");
            return false;
        }
    }else{
        alert("您还未安装flash Player,请安装 flash Player 10版本!");
        return false;
    }
    return true;
}