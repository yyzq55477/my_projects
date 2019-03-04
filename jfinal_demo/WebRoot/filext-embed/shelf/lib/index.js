var inputCodeUrl = '';
var submitUrl = '';
var itemScope = '';
var hdstar = '';
var hdEnd = '';
var gridMap = {};  // grid的速查表
var itemMap = {};  // item的速查表
var itemTitleMap = {};  // itemTitle的速查表
var gridTitleMap = {};  // gridTitle的速查表
var entities = []; // 关系结构，[{grid:gridId,items:[itemId, itemId ...]},{grid:gridId,items:[itemId, itemId ...]}]

var currentGrid = null;  // 当前选中的grid


function init(config) {
	inputCodeUrl = config.inputCodeUrl;
	submitUrl = config.submitUrl;
	itemScope = config.itemScope;
	hdstar= config.hdstar;
	hdEnd= config.hdEnd;
	if(itemScope == '') 
		itemScope = null;
	else
		itemScope = ','+itemScope+',';
	if(hdstar == '') 
		hdstar = null;
	if(hdEnd == '') 
		hdEnd = null;
	
}

/**
 * 发送编码
 * @param code
 */
function sendCode(code) {
	$.get(inputCodeUrl, {code : code}, function(result){
		if (result && result.data) {
			// 验证数据是否满足当前的录入条件
			result.id=code;
			if (validate(result) == false) return;
			
			// 根据当前模型
		
			updateModel(result);
		} else {
			throw Error('数据格式错误');
		}
	});
}

/**
 * 验证数据
 * @param data
 * @returns {Boolean}
 */
function validate(data) {
	if (data.type == 'Item') {
		if (currentGrid == null) {
			v1.alert('请扫柜架号');
			return false;
		}
		
		if (itemMap[data.id] != null) {
			v1.alert('该档案已经扫描，编号：' + data.id);
			return false;
		}
		
		if(itemScope != null && itemScope.indexOf(','+data.fileNumber+',') == -1){
			v1.alert(data.id+'档案不在选择档案范围,请核对' );
			return false;
		}
//		if(hdstar!=null&&hdEnd!=null){
//			if(data.id>hdEnd||data.id<hdstar){
//				v1.alert(data.id+'不在盘库号段内,请核对' );
//				return false;
//			}
//			
//		}
	}
	
	return true;
}

/**
 * 更新模型
 * 根据编码数据请求后得到的数据更新模型
 * @param data
 */
function updateModel(data) {
	if (data.type == 'Grid') {
		// 如果是新的Grid
		if (gridMap[data.id] == null) {
			gridMap[data.id] = data.id;  // 新增速查表
			entities.push({
				grid : data.id,
				items : []
			}); // 添加Grid
		}
		  $("#inputCode").val("");
		currentGrid = data.id; // 设置当前Grid
	} else if(data.type == 'Item') {
		if (currentGrid == null) throw Error('异常操作');
		
		//if(itemMap[data.id]!=null&&itemMap[data.id]!=''){
		//	alert("已扫描,请核对");
			//  $("#inputCode").val("");
		//}else if(data.id==null||data.id==''){
		//	alert("无内容,请核对");
		//}else{
			getGridEntity(currentGrid).items.push(data.id);
			  $("#inputCode").val("");
			  $("#inputCode").focus();
	//	}
		itemMap[data.id] = data.id;
	} else {
		throw Error('数据错误,不支持的类型:' + data.type);
	}
	
	// 更新数据后，渲染当前grid
	renderModel(currentGrid);
}

/**
 * 渲染模型
 * 对指定的grid渲染
 * @param scope
 */
function renderModel(scope) {
	// 找到范围内的柜架数据
	var entity = getGridEntity(scope);
	var grid = entity.grid;
	var items = entity.items;
	
	var findGridRow = null;
	$('#canvas tr').each(function() {
		if ($(this).data('gridid') == grid) {
			findGridRow = $(this); // 记录当前行
			return false;
		}
	});
	if (findGridRow == null) { //表示grid还没有创建过
		var html = ['<tr data-gridid=' + grid + ' id=' + grid + '>', renderGridRow(entity), '</tr>'];
		$('#canvas').append(html.join(''));
	} else {
		findGridRow.html(renderGridRow(entity));
	}
	
	highlight(currentGrid);
}

/**
 * 高亮显示最后选中的grid和添加的item
 * 为了结构清晰，效率相对较低，但因为数据量小可以忽略
 * @param gridId
 */
function highlight(gridId) {
	$('#canvas tr').each(function(gridUI) {
		if ($(this).data('gridid') == gridId) {
			var tr = $(this);
			tr.find('td.grid').addClass('grid-highlight');
			tr.find('td.items li:last').addClass('item-highlight');
		} else {
			var tr = $(this);
			tr.find('td.grid').removeClass('grid-highlight');
			tr.find('td.items li').removeClass('item-highlight');
		}
	});
}

/**
 * 渲染Grid正行数据，包括grid和items
 * @param entity
 * @returns
 */
function renderGridRow(entity) {
	var html = ['<td class="grid">', renderGridDetail(entity.grid), '</td>', '<td class="items">', renderItems(entity.items), '</td>'];
	return html.join('');
}

/**
 * 渲染Item列表
 * @param items
 */
function renderItems(items) {
	if (items.length == 0) return '';
	var html = ['<ul>'];
	for (var index in items) {
		html.push('<li id=\''+items[index]+'\'>');
		html.push(renderItemDetail(items[index]));
		html.push('</li>');
	}
	html.push('</ul>');
	return html.join('');
}

/**
 * 渲染grid的明细信息
 * @param gridId
 * @returns
 */
function renderGridDetail(gridId) {
	if(gridTitleMap[gridId]!=null&&gridTitleMap[gridId]!=''){
		return gridTitleMap[gridId];
	}
	var title=gridId;
	var delet='<span style="float:right;"><a  onclick=removeGrid(\''+gridId +'\') title="删除"><i class="glyphicon glyphicon-remove"></i></a></span>';
	//delet='';
	$.ajax({
		url: '/v1/FileCatalog/queryFileCatalog',
		type : 'post',
		dataType: 'json',
		data: {uuid:gridId,operateType:1},
		cache: false,
	    async: false,
		error: function(rs){
		},
		success: function(rs){
			if(rs.status == 'success'){
				if(rs.errorType=='1101'){
					title='<font color="red">'+title+'('+rs.title+')'+'</font>';
				}else{
					
					var location=rs.title.split('-');
					var res = "";
					var a=location[0];
					var b=location[1];
					var c=location[2];
					var d=location[3];
					
					
					if(a!=null){
						res += a+"号库房";
					}
					if(b!=null){
						res += parseInt((parseInt(b)))+"柜";

					}
					if(c!=null){
						res += c+'列'
					}
					if(d!=null){
						res += d+'排'
					}
					
					
					title=title+'('+res+')';
				}
				
			}
		}
	});	
	gridTitleMap[gridId]=title+delet;
	return title+delet;
}

/**
 * 渲染Item的明细信息
 * @param itemId
 * @returns
 */
function renderItemDetail(itemId) {
	if(itemTitleMap[itemId]!=null&&itemTitleMap[itemId]!=''){
		return itemTitleMap[itemId];
	}
	var title=itemId;
	var delet='<span style="float:right;"><a  onclick=removeItem('+itemId +') title="删除"><i class="glyphicon glyphicon-remove"></i></a></span>';
	//delet='';
	$.ajax({
		url: '/v1/FileCatalog/queryFileCatalog',
		type : 'post',
		dataType: 'json',
		data: {uuid:itemId,operateType:2,currentGrid:currentGrid},
		cache: false,
	    async: false,
		error: function(rs){
		},
		success: function(rs){
			if(rs.status == 'success'){
				if(rs.errorType=='1101'){
					title='<font color="red">'+title+'('+rs.title+')'+delet+"</font>";
				}else{
					title=title+'('+rs.title+')'+delet;
				}
			}
		}
	});	
	itemTitleMap[itemId]=title;
	return title;
}

/**
 * 从关系结构中获得指定的grid
 * @param id
 * @returns 返回一条关系
 */
function getGridEntity(id) {
	var grid = null;
	// 遍历关系，获取Grid
	$.each(entities, function(index, item) {
		if (item.grid != id) return;
		
		grid = item;
		//item.items.push(item.id);
		return false;
	});
	return grid;
}


var DONT_ENUM =  "propertyIsEnumerable,isPrototypeOf,hasOwnProperty,toLocaleString,toString,valueOf,constructor".split(","),
hasOwn = ({}).hasOwnProperty;
for (var i in {
    toString: 1
}){
    DONT_ENUM = false;
}


//jia
Object.keys = Object.keys || function(obj){//ecma262v5 15.2.3.14
        var result = [];
        for(var key in obj ) if(hasOwn.call(obj,key)){
            result.push(key) ;
        }
        if(DONT_ENUM && obj){
            for(var i = 0 ;key = DONT_ENUM[i++]; ){
                if(hasOwn.call(obj,key)){
                    result.push(key);
                }
            }
        }
        return result;
    };

	function  removeGrid(id){
		delete gridMap[id];
		var gridMaplenght=Object.keys(gridMap).length;

		for(var i in entities){		
			if(entities[i].grid==id){
				for(var j in entities[i].items){
					delete itemMap[entities[i].items[j]];
				}
				entities.splice(i,1);
			    $('#'+id+'').remove();   
			}
		}
		if(gridMaplenght==0){
			currentGrid = null;
		}else{
			currentGrid=entities[entities.length-1].grid;
		}
	}

	function  removeItem(id){
		for(var i in entities){
				for(var j in entities[i].items){
				   if(entities[i].items[j]==id){
						delete itemMap[entities[i].items[j]];
					    entities[i].items.splice(j,1);
				   }
				}	
		}
		   $('#'+id+'').remove();     
	}