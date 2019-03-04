var appcontext = parent.icv.config.appcontext;
/**
 * 提供一体机包类
 */
(function() {
	var pdfJS = {
		id:"pdfFrame",//容器Id
		title:null,
		showToolBar:"hidden",
		viewUrl: appcontext + "/filext-embed/lib/pdf/web/viewer.html",
		config:{}
	};
	
	pdfJS.show = function(pdfUrl,id){
		if(!isEmpty(id)){
			this.id=id;
		}
		var pdfFrame = window.parent.document.getElementById(this.id);
		pdfFrame.src = this.viewUrl+"?fileUrl="+pdfUrl;
		$(pdfFrame).load(function(){//等iframe加载完毕 调用方法
			if(typeof pdfFrame.contentWindow.showToolBarFn=='function'){
				pdfFrame.contentWindow.showToolBarFn(pdfJS.showToolBar);
			}
		});
	}
	
	//判断字符是否为空的方法
	function isEmpty(obj) {
		if (typeof obj == "undefined" || obj == null || obj == "") {
			return true;
		} else {
			return false;
		}
	}
	
	window.pdfJS == null ? window.pdfJS = pdfJS : window._pdfJS = pdfJS; // 
})();
