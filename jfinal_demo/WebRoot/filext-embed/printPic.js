var ContextPath=getContextPath();

function getContextPath() {
    var pathName = document.location.pathname;
    var index = pathName.substr(1).indexOf("/");
    var result = pathName.substr(0,index+1);
    return result;
}

function allLog(id){
	$.ajax({  
		type : "POST",
		url : ContextPath+"/business/yx59/addOrEdit",
		data : {yx2100 : id},
		success : function(result) { }  
	});
};

