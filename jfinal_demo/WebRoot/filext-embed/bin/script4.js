var filextApi = null,
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
    totalLength = 0,
    x1 = 0,
    y1 = 0,
    x2 = 0,
    y2 = 0,
    isDown = false,
    picasa = null,
    template = "<div class='cont-box'>"
                +"<ul  id = 'dragWrap' class='clearfix'>"
                    +"{{#data}}"
                        +"<li  class='item {{type}}Cls' id='item_{{uuid}}' data-id='{{uuid}}'>"
                            +"<div class='hover-operate clear' style='background:'#108cee'> "
                                +"<a class='gobig fl' data-id='{{uuid}}'>查看大图</a>"
                                +"<a class='remove' data-id='{{uuid}}' >删除</a>"
                            +"</div>"
                            +"{{#compare type 'Image'}}"
                            +"<img src='{{thumb-2}}' data-error='0'/>"
                            +"<input type='checkbox' class='checkpic' data-id='{{uuid}}' data-original='{{file}}'/>"
                            +"{{else}}<img src='images/icons/{{uuidExt}}.png' /><p class='operate'>{{/compare}}"
                            +"<p>{{title}}</p>"
                        +"</li>"
                    +"{{/data}}"
                +"</ul>"
            +"</div>",
    thumbTemplate = "<ul>"
                       +"{{#data}}"
                            +"{{#compare @index '0'}}"
                            +"<li  tab='{{uuid}}' data-id='{{uuid}}' "
                                +"class='thistab'"
                                +"data-title='{{bizTitle}}' data-base-file='{{hasBaseFile}}'"
                                +" data-cv='{{current_version}}' data-format='{{materialFormat}}' data-number='{{leastNumber}}'>{{bizTitle}}"
                                +"<img class='category-remove'}' src='images/icon-delete.png'/>"
                                +"<img class='category-edit' src='images/icon-edit.png'>"
                                +"<span>{{total}}</span>"
                                +"</li>"
                            +"{{else}}"
                            +"<li  tab='{{uuid}}' data-id='{{uuid}}'"
                                +"data-title='{{bizTitle}}' data-base-file='{{hasBaseFile}}'"
                                +" data-cv='{{current_version}}' data-format='{{materialFormat}}' data-number='{{leastNumber}}'>{{bizTitle}}"
                                +"<img class='category-remove'}' src='images/icon-delete.png'/>"
                                +"<img class='category-edit' src='images/icon-edit.png'>"
                                +"<span>{{total}}</span>"
                                +"</li>"
                            +"{{/compare}}"
                            +"{{/data}}"
                        +"</ul>"
                    +"</div>";




/*入口*/
function init(config) {
    var config = config || {};
    filextApi = new FilextApi(config);
    privilege = config.config.privilege || {};
    initPage(1);
    if (config.config.openHighShoot && isIE()) {
        $('.highShoot').click();
    }
    renderUpload();
    
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

        template = null;
        thumbTemplate = null;
        tempdown = null;
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
        //recycle(window);
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
    //$('#refreshing').fadeIn();
    // 获取数据
    fetchData(falg);

    if (initData.length>0 || rootJson.group) {
        // 渲染列表页
        initListView();
        setTimeout(function() {
            //$('#refreshing').fadeOut('slow');
        }, 1000);
    } else {
        $('#refreshing').hide();

        showTip("当前办件没有任何影像材料！");
    }
    $('#loading').hide();
    if(privilege.enableGroup) $('.collection').hide();
}
function fetchData(falg) {
    idMap = {};
    pidMap = {};
    rootJson = filextApi.getInitData(falg) || {};
    initData = rootJson.children || [];
    rootJson.watermark = rootJson.watermark || '';
    totalLength = initData.length;
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
    //获取分类数据
    for(var i in initData){
        var uuid = initData[i].uuid;
        /*var res = filextApi.getSubData(uuid);
        pidMap[uuid] = filterCache(res.data) || [];*/
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
function initListView(){
    /*viewFileCatelog查看按钮权限*/
    if(privilege.temporary){
        $('#listView .thumb').addClass('with-save');
        $('.btn-pic-wrap').removeClass('hidden');
    }
    if(!rootJson.allowCite) $('.finance').remove();

    initListThumb();

    if($('#listView .thumb .thistab').length > 0)
        currentId = $('#listView .thumb .thistab').data('id');
    updateContent();
    $('#listView').show();
    $('#editView').hide();
    $('#detailView').hide();

    initListViewEvent();
    //initOffsetMap();
}



function initListViewEvent(){
     //二级子菜单 滑出
    $('.page-nav li').hover(function () {
        $(this).children('ul').show();//$(this).children('ul').stop(true,true).show('slow');
    }, function () {
        $(this).children('ul').hide();//$(this).children('ul').stop(true,true).hide('slow');
    });

    $('.category-remove').hover(function(){
    	$(this).attr('src','images/icon-deleteclick.png');
    },function(){
    	$(this).attr('src','images/icon-delete.png');
    });
    $('.category-edit').hover(function(){
    	$(this).attr('src','images/icon-editclick.png');
    },function(){
    	$(this).attr('src','images/icon-edit.png');
    })
    function toggleShow(togTab,togCont) {
        $(togTab).click(function () {
            $(togCont).toggle();
        });
    }
    /*调用方法如下：*/
    toggleShow(".area-choose",".area-cont");
    /* 双击图片 */
    $('.gobig').off('click').click(function() {
    	removeBigContainer();
    	var href = $(this).data('href');
    	var icv = null, doc = null;
    	if (parent.icv) {
    		icv = parent.icv;
    		doc = $(parent.document);
    	} else {
    		icv = window.icv;
    		doc = $(window.document);
    	}
    	
    	if (href) {
    		if (href.slice(-3) == 'pdf') {
    			icv.viewBigPdf(href);
    			var pdfHeight = doc.height() - 6;//iframe border=2
    			doc.find('a.media').media({width : '100%', height : pdfHeight});
    			doc.find('.view-big-container .media').css('margin-top', '50px');
    		} else {
    			showTip('此文件暂不支持预览！');
    		}
    	} else {
    		icv._win = window;
    		icv._targetId = $(this).data('id');
    		var obj = getObject(icv._targetId);
    		
    		icv.viewBigImage(obj.file);
    	}
    });
    //删除
    $('.remove').off('click').click(function(){
        var id = $(this).attr('data-id');
        var uuids = new Array();
        uuids[0] = id;
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
                initPage(1);
                $("#btn-load-more-div").remove();
                updateCateView();
                $("#btn-load-more-div").remove();
                updateMaterialVersion();
            }
        }else{
            alert("请选择要删除的图片！");
        }
    });
    //滑动门
    jQuery.jqtab = function(tabtit,tabcon) {
        $(tabcon).hide();
        var tab = $("#tabs ul").children();
        var isShowTab = tab.hasClass("thistab");
        if(!isShowTab){
        	$(tabtit+" li:first").addClass("thistab").show();
        }
        $(tabcon+":first").show();
        $(tabtit+" li").off('click').click(function() {
            $(tabtit+" li").removeClass("thistab");
            $(this).addClass("thistab");
            $(tabcon).hide();
            var activeTab = $(this).attr("tab");
            $("#tab").fadeIn();
            currentId = activeTab;
            renderUpload();
            $("#btn-load-more-div").remove();
            updateContent();
            $(tabtit+" li").removeClass("thistab");
            $(this).addClass("thistab");
            return false;
        });
    };
    /*调用方法如下：*/
    $.jqtab("#tabs",".tab-cont");

    //弹出隐藏层
    function ShowDiv(show_div,bg_div){
        document.getElementById(show_div).style.display='block';
        document.getElementById(bg_div).style.display='block' ;
        var bgdiv = document.getElementById(bg_div);
        bgdiv.style.width = document.body.scrollWidth;
// bgdiv.style.height = $(document).height();
        $("#"+bg_div).height($(document).height());
    };
    //关闭弹出层
    function CloseDiv(show_div,bg_div)
    {
        document.getElementById(show_div).style.display='none';
        document.getElementById(bg_div).style.display='none';
    };
    //模拟下拉弹出关闭
    $(".fm").hover(function(){
        $(".more-operate").show();
    },function(){
    	 $(".more-operate").hide();
    })
    //模拟下拉弹出关闭
    $(".fh").hover(function(){
        $(".more-list-fh").show();
    },function(){
    	 $(".more-list-fh").hide();
    })
   
    $(".category-remove").off('click').click(function(e){
    	preventBubble(e);
		var id = $(this).parent().data('id');
		var obj = getObject(id);
		if(totalLength<=1){
			alert("只有一项材料，禁止删除！");
		}else{
			if (confirm('您确定要删除 “' + obj.bizTitle + '”吗？该操作将不可恢复！')) {
				 $("#btn-load-more-div").remove();
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
		}
    });
    
    function preventBubble(e) {
    	if (e.stopPropagation) {
    		e.stopPropagation();
    	} else {
    		e.cancelBubble = true;
    	}
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
    
    //移上出现图片操作
    $(".cont-box li").mouseover(function(){
        $(this).children(".hover-operate").show();
    });
    $(".cont-box li").mouseleave(function(){
        $(this).children(".hover-operate").hide();
    });
    /*viewFileCatelog查看*/
    $('.btn-viewFile').click(function(){
        $('#view-file-catelog').modal({backdrop: 'static'});
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
        var uuids = [];
        $('.checkpic').each(function() {
            if($(this)[0].checked){
                uuids.push($(this).data('id'));
            }
        });
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
                alert();
                showTip("删除成功"+s+"条，失败"+f+"条!");
                initPage(1);
                updateCateView();
                updateMaterialVersion();
            }
        }else{
            alert("请选择要删除的图片！");
        }
    });
    $('.btn-pic-viewall').off('click').click(function() {
        removeBigContainer();

        var checkedItems = $('.thumbTable tr:gt(0) input:checked');
        var all = checkedItems.length == 0;
        var allDetail = [];
        currentId = '_allDetail';

        if (all) {
            $('.thumbTable tr:gt(0)').each(function() {
                var id = $(this).data('id');
                allDetail = allDetail.concat(pidMap[id]);
            });
        } else {
            checkedItems.parent().parent().each(function() {
                var id = $(this).data('id');
                allDetail = allDetail.concat(pidMap[id]);
            });
        }

        if (allDetail.length > 0) {
            detailPageMap[currentId] = { 'pageIndex':1,'pageSize':5 };

            editId = allDetail[0].uuid;
            pidMap[currentId] = allDetail;
            initDetailView();
        } else {
            showTip('当前没有任何材料可供查看！');
        }
    });

    /*返回*/
    $('.shutdown').click(function(){
        shutdown();
    });

    /* 将多张图片转换成pdf */
    $('.buildpdf').click(function() {
        buildPdf();
    });
    
 // 判断是否为IE浏览器
    function isIE() {
    	return navigator.userAgent.indexOf('MSIE')>=0 || navigator.userAgent.indexOf('Trident')>=0;
    }
    /*高拍*/
    $('.highShoot').off('click').click(function(){
       
        if (isIE()) {
            
            openHighShoot();
        } else {
            alert('高拍功能仅支持IE浏览器！');
        }
        /*$('#upload-default').hide();
        $('#high-shoot_f').attr('src','high-shoot2.html').show();
        $('#modal-upload').addClass('large-modal');*/
    });

    /*得意拍 高拍 by yuanzp 20170314*/
    $('.highShootDyp').off('click').click(function(){
        if ($('#uploadType').is(':checked')) {
            uploadType = 'high-shoot-dyp';
        }
        if (isIE()) {
            $('#modal-upload').modal('hide');
            openHighShootDyp();
        } else {
            alert('高拍功能仅支持IE浏览器！');
        }
    });

    /*良田 高拍 by yuanzp 20170330*/
    $('.highShootLt').off('click').click(function(){
        if (isIE()) {
            $('#modal-upload').modal('hide');
            openHighShootLt();
        } else {
        	alert('高拍功能仅支持IE浏览器！');
        }
    });

    /*多易拍 高拍 by gaofx 20171031*/
    $('.highShootXinhan').off('click').click(function(){
        if ($('#uploadType').is(':checked')) {
            uploadType = 'high-shoot-xinhan';
        }
        if (isIE()) {
            $('#modal-upload').modal('hide');
            openHighShootXinhan();
        } else {
        	alert('高拍功能仅支持IE浏览器！');
        }
    });
    /*方正 高拍 by gaofx 20171101*/
    $('.highShootFangzheng').off('click').click(function(){
        if ($('#uploadType').is(':checked')) {
            uploadType = 'high-shoot-fangzheng';
        }
        if (isIE()) {
            $('#modal-upload').modal('hide');
            openHighShootFangzheng();
        } else {
        	alert('高拍功能仅支持IE浏览器！');
        }
    });
    
    /*国光 高拍 by gaofx 20171108*/
	$('.highShootGuoguang').off('click').click(function(){
		if ($('#uploadType').is(':checked')) {
			uploadType = 'high-shoot-guoguang';
		}
		if (isIE()) {
			$('#modal-upload').modal('hide');
			openHighShootGuoguang();
		} else {
			alert('高拍功能仅支持IE浏览器！');
		}
	});
	
    $('.scanning').off('click').click(function(){
        if (isIE()) {
            if($('#uploadType').is(':checked')){
                uploadType = 'scanning';
            }
            window.showModalDialog('scan.html', window, "dialogHeight:600px;dialogWidth:1000px;");
            //$('#upload-default').hide();
            //$('#scanning_f').attr('src','scan.html').show();
            //$('#modal-upload').addClass('large-modal');
        } else {
        	alert('扫描功能仅支持IE浏览器！');
        }
    });

    /*本地上传*/
    $('.nativeUpload').click(function(){
        if($('#uploadType').is(':checked')){
            uploadType = 'native-upload';
        }
        $('#upload-default').hide();
        $('#native-upload').show();
    });

    $('.finance-upload').click(function(){
        $('#loading').hide();
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
    $('.category-add').off('click').click(function(){
    	$('.category-save-edit').data('type', 'add');
    	$("#groupTitle").text("新增材料");
    	$('#categoryName').val('');
    	$('#categoryName5').val('');
    	$("#modal-group").show();
    });
    
    $('.category-edit').off('click').click(function(){
    	$('.category-save-edit').data('type', 'edit');
    	$("#groupTitle").text("编辑材料");
    	$('.category-save-edit').data('uuid', $(this).parent().data('id'));
    	$('#categoryName').val($(this).parent().data('title'));
    	$("#modal-group").show();
    });
    $('.close').off('click').click(function(){
    	$("#modal-group").hide();
    })
    $('.btn-default').off('click').click(function(){
    	$("#modal-group").hide();
    });
    $('.category-save-edit').off('click').click(function(){
    	 $("#btn-load-more-div").remove();
    	var title = $('#categoryName').val();
		if (title == '') {
			$('.input-wrap').addClass('has-error');
			$('.input-wrap input[type=text]').focus();
			return false;
		}
		
		var type = $('.category-save-edit').data('type');
		var uuid;
		var isAdd;
		if(type=='add'){
			uuid = filextApi.uuid;
			isAdd = true;
		}else{
			uuid = $('.category-save-edit').data('uuid');
			isAdd = false;
		}
		var remark = $('#categoryName5').val();
		var data = {
				uuid : uuid,
				title : title,
				code : '',
				//materialNumber : groupForm.materialNumber.value,
				//materialTime : groupForm.materialTime.value,
				remark:remark,
				isAdd : isAdd
			};
		var res = filextApi.addCategory(data);
		
		if (res.success) {
			$("#modal-group").hide();
			$('.prompt-modal').modal('hide');
			initPage();
		} else {
			alert(res.message);
		}
    })
    /* 返回默认上传界面 */
    $('.back-to-default').click(function() {
        backToDefault();
    });

    $('.category-cancel').off('click').click(function(){
    	$("#modal-group").hide();
	});
    /* 打开高拍界面 */
    function openHighShoot() {
    	window.showModalDialog('high-shoot.html', window, "dialogHeight:600px;dialogWidth:1000px;");
    }

    /* 打开得意拍高拍界面   by yuanzp 20170314*/
    function openHighShootDyp() {
    	if(window.navigator.platform=="Win32"){
    		window.showModalDialog('high-shoot-dyp.html', window, "dialogHeight:600px;dialogWidth:1000px;");
    	}else{
    		window.showModalDialog('high-shoot-dyp64.html', window, "dialogHeight:600px;dialogWidth:1000px;");
    	}
    	
    }

    /* 打开良田高拍界面   by yuanzp 20170330*/
    function openHighShootLt() {
    	window.showModalDialog('high-shoot-lt.html', window, "dialogHeight:600px;dialogWidth:1000px;");
    }

    /* 打开多易拍高拍界面   by gaofx 20171031*/
    function openHighShootXinhan() {
    	window.showModalDialog('high-shoot-xinhan.html', window, "dialogHeight:600px;dialogWidth:1000px;");
    }
    /* 打开方正高拍界面   by gaofx 20171101*/
    function openHighShootFangzheng() {
    	window.showModalDialog('high-shoot-fangzheng.html', window, "dialogHeight:600px;dialogWidth:1000px;");
    }

    /* 打开国光高拍界面   by gaofx 20171108*/
    function openHighShootGuoguang() {
    	window.showModalDialog('high-shoot-guoguang.html', window, "dialogHeight:600px;dialogWidth:1000px;");
    }
    
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
            if(!isFound) setTimeout(function(){alert('没有找到匹配的内容')}, 1000);
        }
    });

    /* 全选按钮 */
    $('#checkAll').off('click').click(function() {
        $('.thumbTable input[type="checkbox"]').attr('checked', $(this).attr('checked')||false);
    });
    /* 复选框选择事件 */
    $('.thumbTable input[type="checkbox"]').off('change').change(function() {
        var all = $('.thumbTable tr:gt(0) input:checked').length == 0;
        $('.btn-pic-viewall span').text(all?'查看全部':'查看选中');
    });
    $('.search-pic').off('click').click(function() {
    	search_image();
	});
    $('#search-input').off('keyup').keyup(function(e){
    	if (e.which == 13) {
    		search_image();
    	}
    });
}


function search_image(){
	var needUpdate = false;
	var key = $('#search-input').val();
	var searchId = '_searchId';
	if (key==''&& pidMap[searchId]) {
		var items = pidMap[getCurrentId()];
		var searchItems = new Array();
		for (var e in items) {
			searchItems.push(items[e]);
		}
		needUpdate = true;
		currentId = searchId;
		pidMap[currentId] = searchItems;
		thumbPageMap[currentId] = {
			pageIndex : 1,
			pageSize : searchItems.length
		}
	} else if (key != '') {
		var items = pidMap[getCurrentId()];
		var searchItems = new Array();
		for (var e in items) {
			if (items[e].filename.indexOf(key)>=0) {
				searchItems.push(items[e]);
			}
		}
			needUpdate = true;
			currentId = searchId;
			pidMap[currentId] = searchItems;
			thumbPageMap[currentId] = {
				pageIndex : 1,
				pageSize : searchItems.length
			}
	}
	
	if (needUpdate) {
		updateContent();
		//$('.search-pic').val(key);
		$('.search-pic').blur();
	}
}

/*过滤处理图片缓存问题*/
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

function initListThumb(){
    if(privilege.enableGroup){
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
}

function updateContent() {
    showPages(false);
    imageErrorHandler('#dragWrap img');
    dragToCategory();
}


function dragToCategory(){
	$('.cont-box ul').dragsort({ 
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
				/*排序*/
				var ids = [];
				$('.item').each(function(){
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
		}, 
		dragBetween: true,
		placeHolderTemplate: "<li class='item'></li>" 
	});
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
    if (hasMore) {
		var selector = '#dragWrap';
		$('.tab-cont').append('<div id="btn-load-more-div" style="width:100%;text-align:center">'
				+ '<button class="btn btn-info btn-load-more" type="button" style="background:##eaf6fe">'
				+ '<i class="icon-white icon-picture"></i>(' + pageNumber + '/' + dataNumber + ')点此加载更多...</button>'
				+ '</div>');
		$('.btn-load-more').off('click').click(function() {
			$("#btn-load-more-div").remove();
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
    
    compileTemplate(template, {'data' : items}, '#listView .cont-box');
    initListViewEvent();
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
//上传控件渲染方法
function renderUpload(){
    filextApi.doUploadNewMethod({
        uuid : currentId,
        materialFormat : generateMaterialFormat(currentId)
    },function(res){
        updateAfterUpload(res);
    });
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
function getObject(id) {
    return idMap[id] == null ? {} : idMap[id];
}
/*提示*/
function showTip(msg){
    $('#showtip').html(msg).fadeIn();
    setTimeout(function() {
        $('#showtip').fadeOut(2000);
    }, 1000);
}
/* 上传后更新 */
function updateAfterUpload(res) {
    if (res) {
    	$("#btn-load-more-div").remove();
        $('#modal-upload').modal('hide');
        $('#native-file').val('');
        updateCateView();
        updateMaterialVersion();
        $(".thistab span").html(pidMap[currentId].length);
    } else {
        var msg = isReplace?'替换失败':'上传失败';
        alert(msg);
    }
}
function updateCateView(){
    updateCurrentData();
    updateContent();
    $('#badge_'+ currentId).html(pidMap[currentId].length);
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

function generateIdMap() {
    for ( var i in pidMap) {
        var data = pidMap[i];
        for (var j = 0, len = data.length; j < len; j++) {
            idMap[data[j].uuid] = data[j];
        }
    }
}
function removeBigContainer() {
	var container = $('.view-big-container', parent.document);
	if (container.length > 0) container.remove();
}
/* 双击图片 */
$('li.item').off('dblclick').dblclick(function() {
	removeBigContainer();
	var href = $(this).data('href');
	var icv = null, doc = null;
	if (parent.icv) {
		icv = parent.icv;
		doc = $(parent.document);
	} else {
		icv = window.icv;
		doc = $(window.document);
	}
	
	if (href) {
		if (href.slice(-3) == 'pdf') {
			icv.viewBigPdf(href);
			var pdfHeight = doc.height() - 6;//iframe border=2
			doc.find('a.media').media({width : '100%', height : pdfHeight});
			doc.find('.view-big-container .media').css('margin-top', '50px');
		} else {
			showTip('此文件暂不支持预览！');
		}
	} else {
		icv._win = window;
		icv._targetId = $(this).data('id');
		var obj = getObject(icv._targetId);
		icv.viewBigImage(obj.file);
	}
});
/* 更新材料版本 */
function updateMaterialVersion() {
    return ;    //屏蔽

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

//返回选中的选择框
function getCheckedPics() {
    var uuids = [];
    $('.checkpic').each(function() {
        if($(this)[0].checked){
            uuids.push($(this).data('id'));
        }
    });

    return uuids;
}
function deteleFile(e){
    console.log(e);
}

function getCurrentId() {
	return $('.thistab').attr("tab");
}