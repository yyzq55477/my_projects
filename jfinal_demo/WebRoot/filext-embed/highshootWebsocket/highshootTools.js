/*websocket高拍的通用方法
 * author bw*/

var localparent = window.parent;	//获取他爹的dom对象
var filextApi = localparent.filextApi;	//获取爹dom里的icv对象

function getPicName(){
	var date=new Date();
	var yy=date.getFullYear().toString();
	var mm=(date.getMonth()+1).toString();
	var dd=date.getDate().toString();
	var hh=date.getHours().toString();
	var nn=date.getMinutes().toString();
	var ss=date.getSeconds().toString();
	var mi=date.getMilliseconds().toString();
	var picName=yy+mm+dd+hh+nn+ss+mi;
	return picName;
}

function getPath(filename){
	var config = {
			uuid : localparent.currentId,
			filename : filename+'.jpg',
			//filesize : filesize
		};
		var res = filextApi.requestBeforeUpload(config);
		if (filextApi.getWatermark() != null) {
			res.watermark = filextApi.getWatermark();
		}
		var path = filextApi.WEB_APP + '/filext-api?local=true&watermark='
				+ encodeURIComponent(res.watermark) + '&node=' + res.node
				+ '&uuidExt=' + res.uuidExt + '&uuid=' + res.uuid + '&method='
				+ res.method;
		var host = window.location.host;
		var port = host.split(":")[1] || '80';
		host = host.split(":")[0];
		var url = "http://" +host+":"+port+path;
		var result = {
			host : host,
			port : port,
			path : path,
			url : url
		}
		return result;
}

function doMask(){
    $("#mask").fadeTo(350,0.5);  
}

function unMask(){
	$("#mask").hide();
}