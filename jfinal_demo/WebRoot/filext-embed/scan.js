var filextApi = parent.filextApi;
$(function() {
	$('#btnScan').click(function() {
		document.getElementById('scaner1').scan();
	});
	$('#btnUpload1').click(function() {
		ajaxSubmitBath();

	});
	document.getElementById('scaner1').setDpi(100, 200);
});

function getServerUrl() {
	return "/upload";
}

function getDateTimeStr() {
	var dateStr = "";
	var todayDate = new Date();
	var date = todayDate.getDate();
	var month = todayDate.getMonth() + 1;
	var year = todayDate.getYear();
	var hours = todayDate.getHours();
	var minutes = todayDate.getMinutes();
	var seconds = todayDate.getSeconds()
	if (navigator.appName == "Netscape") {
		dateStr += (1900 + year);
	}
	if (navigator.appVersion.indexOf("MSIE") != -1) {
		dateStr += year;
	}
	dateStr += "-";
	dateStr += month;
	dateStr += "-";
	dateStr += date;
	dateStr += " ";
	dateStr += hours;
	dateStr += "时";
	dateStr += minutes;
	dateStr += "分钟";
	dateStr += seconds;
	dateStr += "秒";
	return dateStr;
}

function ajaxSubmitBath(){
	var scanner = document.getElementById('scaner1');
	var imagesCount = scanner.imagesCount;
	for(var i=1;i<=imagesCount;i++){
		var base64_data = scanner.getJpegBase64DataById(i);
		ajax_post_base64(base64_data);
	}
	callbackfun1('1');
}

function ajax_post_base64(base64_data) {
	strFileName = "Img" +getDateTimeStr() + ".jpg";
	var config = {
			uuid: parent.currentId,
			filename: strFileName,
			filesize: base64_data.length
		};
	var res = filextApi.requestBeforeUpload(config);
	if(filextApi.getWatermark() != null){
		res.watermark = filextApi.getWatermark();
	}
	//注意 这个多了一个参数widget 以区分是扫描控件上传
	var path = filextApi.WEB_APP+'/filext-api?node=' + res.node + '&widget=scanOnWeb&watermark='+encodeURIComponent(res.watermark)+'&uuidExt=' + res.uuidExt + '&uuid=' + res.uuid + '&method=' + res.method;

	$.ajax({
		url : path,
		type : 'POST',
		dataType : 'json',
		async: false,
		data : {
			scanImage :  "'"+ base64_data + "'"
		},
		timeout : 1000,
		success : callbackfun2
	});
}


function ajax_post_1() {
	var base64_data = document.getElementById('scaner1').jpegBase64Data();
	
	strFileName = "Img" +getDateTimeStr() + ".jpg";
	var config = {
			uuid: parent.currentId,
			filename: strFileName,
			filesize: base64_data.length
		};
	var res = filextApi.requestBeforeUpload(config);
	if(filextApi.getWatermark() != null){
		res.watermark = filextApi.getWatermark();
	}
	//注意 这个多了一个参数widget 以区分是扫描控件上传
	var path = filextApi.WEB_APP+'/filext-api?node=' + res.node + '&widget=scanOnWeb&watermark='+encodeURIComponent(res.watermark)+'&uuidExt=' + res.uuidExt + '&uuid=' + res.uuid + '&method=' + res.method;

	$.ajax({
		url : path,
		type : 'POST',
		dataType : 'json',
		data : {
			scanImage :  "'"+ base64_data + "'"
		},
		timeout : 1000,
		success : callbackfun1
	});
}

function callbackfun1(data) {
	parent.updateAfterUpload(true);
}

function callbackfun2(data) {
	
}