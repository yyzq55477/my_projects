var path="http://"+window.location.host;
var pathName=window.document.location.pathname;
var WEB_APP=pathName.substring(0,pathName.substr(1).indexOf('/')+1);
var messagePopTime = 5000;

__CreateJSPath = function (js) {
    var scripts = document.getElementsByTagName("script");
    var path = "";
    for (var i = 0, l = scripts.length; i < l; i++) {
        var src = scripts[i].src;
        if (src.indexOf(js) != -1) {
            var ss = src.split(js);
            path = ss[0];
            break;
        }
    }
    var href = location.href;
    href = href.split("#")[0];
    href = href.split("?")[0];
    var ss = href.split("/");
    ss.length = ss.length - 1;
    href = ss.join("/");
    if (path.indexOf("https:") == -1 && path.indexOf("http:") == -1 && path.indexOf("file:") == -1 && path.indexOf("\/") != 0) {
        path = href + "/" + path;
    }
    return path;
}

var bootPATH = __CreateJSPath("LodopFuncs.js");

var CreatedOKLodop7766=null;

function getLodop(oOBJECT,oEMBED){
/**************************
  本函数根据浏览器类型决定采用哪个页面元素作为Lodop对象：
  IE系列、IE内核系列的浏览器采用oOBJECT，
  其它浏览器(Firefox系列、Chrome系列、Opera系列、Safari系列等)采用oEMBED,
  如果页面没有相关对象元素，则新建一个或使用上次那个,避免重复生成。
  64位浏览器指向64位的安装程序install_lodop64.exe。
**************************/
    var strHtmInstall="<br><font color='#FF00FF'>打印控件未安装!点击这里<a href='"+bootPATH+"install_lodop32.exe' target='_self'>执行安装</a>,安装后请刷新页面或重新进入。</font>";
    var strHtmUpdate="<br><font color='#FF00FF'>打印控件需要升级!点击这里<a href='"+bootPATH+"install_lodop32.exe' target='_self'>执行升级</a>,升级后请重新进入。</font>";
    var strHtm64_Install="<br><font color='#FF00FF'>打印控件未安装!点击这里<a href='"+bootPATH+"install_lodop64.exe' target='_self'>执行安装</a>,安装后请刷新页面或重新进入。</font>";
    var strHtm64_Update="<br><font color='#FF00FF'>打印控件需要升级!点击这里<a href='"+bootPATH+"install_lodop64.exe' target='_self'>执行升级</a>,升级后请重新进入。</font>";
    var strHtmFireFox="<br><br><font color='#FF00FF'>（注意：如曾安装过Lodop旧版附件npActiveXPLugin,请在【工具】->【附加组件】->【扩展】中先卸它）</font>";
    var strHtmChrome="<br><br><font color='#FF00FF'>(如果此前正常，仅因浏览器升级或重安装而出问题，需重新执行以上安装）</font>";
         var LODOP;		
	try{	
	     //=====判断浏览器类型:===============
	     var isIE	 = (navigator.userAgent.indexOf('MSIE')>=0) || (navigator.userAgent.indexOf('Trident')>=0);
	     var is64IE  = isIE && (navigator.userAgent.indexOf('x64')>=0);
	     //=====如果页面有Lodop就直接使用，没有则新建:==========
	     if (oOBJECT!=undefined || oEMBED!=undefined) { 
               	 if (isIE) 
	             LODOP=oOBJECT; 
	         else 
	             LODOP=oEMBED;
	     } else { 
		 if (CreatedOKLodop7766==null){
          	     LODOP=document.createElement("object"); 
		     LODOP.setAttribute("width",0); 
                     LODOP.setAttribute("height",0); 
		     LODOP.setAttribute("style","position:absolute;left:0px;top:-100px;width:0px;height:0px;");  		     
                     if (isIE) LODOP.setAttribute("classid","clsid:2105C259-1E0C-4534-8141-A753534CB4CA"); 
		     else LODOP.setAttribute("type","application/x-print-lodop");
		     document.documentElement.appendChild(LODOP); 
	             CreatedOKLodop7766=LODOP;		     
 	         } else 
                     LODOP=CreatedOKLodop7766;
	     };
	     //=====判断Lodop插件是否安装过，没有安装或版本过低就提示下载安装:==========
	     if ((LODOP==null)||(typeof(LODOP.VERSION)=="undefined")) {
	    	 var newWindow = window.open("../fileCatalog/blank","打印错误");
	 		//var strHtmInstall="<br><font color='#FF00FF'>打印控件未安装!点击这里<a href='/lodop/install_lodop32.exe' >执行安装</a>,安装后请刷新页面或重新进入。</font>";
	             if (navigator.userAgent.indexOf('Chrome')>=0)
	            	 newWindow.document.write(strHtmChrome);
	                 //document.documentElement.innerHTML=strHtmChrome+document.documentElement.innerHTML;
	             if (navigator.userAgent.indexOf('Firefox')>=0)
	            	 newWindow.document.write(strHtmFireFox);
	                // document.documentElement.innerHTML=strHtmFireFox+document.documentElement.innerHTML;
	             if (is64IE)
	            	 newWindow.document.write(strHtm64_Install);
	            	// document.write(strHtm64_Install);
	             else
	             if (isIE)  
	            	 newWindow.document.write(strHtmInstall);
	            	 //document.write(strHtmInstall);   
	             else
	            	 newWindow.document.write(strHtmInstall);
	                // document.documentElement.innerHTML=strHtmInstall+document.documentElement.innerHTML;
	             return LODOP;
	     } else 
	     if (LODOP.VERSION<"6.1.7.5") {
	    	 	var newWindow = window.open("../fileCatalog/blank","打印错误");
	             if (is64IE) document.write(strHtm64_Update); else
	             if (isIE) document.write(strHtmUpdate); else
	            	 newWindow.document.write(strHtmUpdate);
	             //document.documentElement.innerHTML=strHtmUpdate+document.documentElement.innerHTML;
	    	     return LODOP;
	     };
	     //=====如下空白位置适合调用统一功能(如注册码、语言选择等):====	     
	     LODOP.SET_LICENSES("浙江天正思维信息技术有限公司","D79B50E6418ED370BA2E10D05D108ADC051","浙江天正思維信息技術有限公司","2C3431426A46B5C62074EE8897CA7273EA9");
	     LODOP.SET_LICENSES("THIRD LICENSE","","Zhejiang Tianzheng Thinking Information Technology Co., Ltd.","7916C0A1ADAFE826ACA3740392FFF869487");
	     //============================================================	     
	     return LODOP; 
	} catch(err) {
		var newWindow = window.open("../fileCatalog/blank","打印错误");
	     if (is64IE){	
	    	 newWindow.document.write("Error:"+strHtm64_Install);
            //document.documentElement.innerHTML="Error:"+strHtm64_Install+document.documentElement.innerHTML;
	     }else{
	    	 newWindow.document.write("Error:"+strHtmInstall);
            //document.documentElement.innerHTML="Error:"+strHtmInstall+document.documentElement.innerHTML;
	     }
	     return LODOP; 
	};
}


function setPrintName(LODOP,printName){
	try{
		var count = LODOP.GET_PRINTER_COUNT();
		LODOP.SET_PRINT_COPIES(1);//设置打印份数
		/*var strHtm64_Install = "<br><font color='#FF00FF'>打印控件运行不正常，请检查浏览器设置！</font>";
		var newWindow = window.open("../fileCatalog/blank","打印错误");
		newWindow.document.write("Error:"+strHtm64_Install);*/
	}catch(e){
		alert('打印控件运行不正常，请检查浏览器设置！');
		return false;
	}
	var print_name;
	var print_num;
	var printNames = printName.split(",");
	for(var i=0;i<count;i++){
		var name = LODOP.GET_PRINTER_NAME(i);
		for(var j=0;j<printNames.length;j++){
			if(name.indexOf(printNames[j])>=0){
				print_name = name;
				break;
			}
		}
	}
	if(print_name!=null&&print_name!=''){
		LODOP.SET_PRINTER_INDEXA(print_name);
	}
}
function setEPSON(LODOP){
	setPrintName(LODOP,'票据,TM-U220');
}
function setBQ(LODOP){
	setPrintName(LODOP,'GK888t');
}
function setDefaultPrint(LODOP){
	try{
		setPrintName(LODOP,LODOP.GET_PRINTER_NAME(-1));
	}catch(e){
		alert('打印控件运行不正常，请检查浏览器设置！');
		return false;
	}
}
function setCKSQ(LODOP){
	setPrintName(LODOP,'出库申请');
}


