//BEGIN SETUP
var photographData = "";
var baseUrl = "ws://127.0.0.1:12345";
var socket;

function disconnect()
{
	dialog.get_actionType("closeSignal");
	socket.close();
	socket = null;
}

function connect()
{
	//socket.close();
	//socket = null;
	var ip = "127.0.0.1";
	baseUrl = "ws://" + ip + ":12345";
	var re=/^(\d+)\.(\d+)\.(\d+)\.(\d+)$/;//正则表达式     
	if(re.test(ip))     
	{     
		if( RegExp.$1<256 && RegExp.$2<256 && RegExp.$3<256 && RegExp.$4<256)
		{
			//alert("IP有效" + baseUrl);
			openSocket();					
		} 					
	} 
	else{
		alert("请输入有效的IP地址" + baseUrl);
	}						
}
function openSocket() {				
	socket = new WebSocket(baseUrl);
    socket.onclose = function()
    {
        console.error("web channel closed");
    };
    socket.onerror = function(error)
    {
        console.error("web channel error: " + error);
    };
	socket.onopen = function()
    {
        new QWebChannel(socket, function(channel) {
            //获取c++注册对象
            window.dialog = channel.objects.dialog;
			
			//网页关闭函数
			window.onunload = function() {
				dialog.get_actionType("closeSignal");
			}
			//断开按钮点击
			/* document.getElementById("btnDisConnect").onclick = function() {
				window.dialog = channel.objects.dialog;		
				dialog.get_actionType("closeSignal");
				var img = document.getElementById('highshootPhoto');				
				img.src = "";
				socket.close();
				socket = null;	
			}; */
			
			
			
			/*//拍照按钮点击
			document.getElementById("shot").onclick = function() {
				var url = UpdataFile();
				dialog.set_configValue('set_httpUrl:http://192.3.3.214:8082/index?a=1');
				dialog.photoBtnClicked("single");
				dialog.get_actionType("savePhoto");
			};*/
			
			/* //纠偏裁边
			document.getElementById("setdeskew").onclick = function() {
				dialog.get_actionType("setdeskew");
			}; */
			
			//接收设备名(在设备列表中添加或删除item)
			/*dialog.send_devName.connect(function(message) {							
				//判断是否存在，否 加入，是 删除
				if(message.indexOf('delete') >= 0)
				{
					var msg = message.substr(6);
					var select = document.getElementById("devList");
					select.remove(msg);															
				} else{
					var select = document.getElementById("devList");
					//副头放在列表末
					if(message.indexOf("USB") >= 0)
					{
						select.add(new Option(message));									
					} else{
						select.add(new Option(message), 0);
						select.selectedIndex=0;
					}
				}
            });*/
									
			//接收设备分辨率，若为空，则清空现有列表
			dialog.send_resolutionList.connect(function(message) {
				if(message)
				{
					var select = document.getElementById("resolutionList");
						select.add(new Option(message));
				} else{
					var select = document.getElementById("resolutionList");
					select.options.length = 0;
				}
            });
			
			//接收设备出图模式，若为空，则清空现有列表
			dialog.send_modelList.connect(function(message) {
				/* if(message)
				{
					var select = document.getElementById("subtypeList");
					if(message.indexOf("MJPG") >= 0)//默认mjpg
					{
						select.add(new Option(message),0);
						select.selectedIndex=0;
					} else{
						select.add(new Option(message));
					}
				} else{
					var select = document.getElementById("subtypeList");
					select.options.length = 0;
				} */
            });
			//服务器返回消息
            dialog.sendPrintInfo.connect(function(message) {
			
            });
			//接收图片流用来展示，多个，较小的base64
			dialog.send_imgData2.connect(function(message) {
				changeImgdata(message);	
            });
			//接收图片流用来展示，单个，较大的base64
			dialog.send_imaData3.connect(function(message) {
				photographData = message;						
            });
			//网页加载完成信号
			dialog.html_loaded("one");
			dialog.devChanged('S920A3');
			dialog.get_actionType("openVideo");
			alert("111");
			//dialog.set_configValue('set_autoUpload:true');
        });
        
    }
}

//根据文本换图片
function changeImgdata(message)
{
	var element = document.getElementById('highshootPhoto');
	element.src = "data:image/jpg;base64," + message;;
}

//网页初始化函数
window.onload = function() {
	baseUrl = "ws://127.0.0.1:12345";			  
	openSocket();              
}
//END SETUP