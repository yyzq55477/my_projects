/*捷宇websocket调用js*/ 
var webSocket = new WebSocket('ws://localhost:1818');

     webSocket.onerror = function(event) {
       onError(event)
    };

     webSocket.onopen = function(event) {
      //onOpen(event);

     };

    webSocket.onclose = function(event) {
       onClose(event);
	   alert(event.data);
    };

     webSocket.onmessage = function(event) {
      onMessage(event)
    }

    function onMessage(event)
	{

//	  $("#photo").attr('src','data:image/jpeg;base64,'+event);
	var begin_data="data:image/jpeg;base64,";
	document.getElementById('highshootPhoto').src =begin_data+event.data;
//	document.getElementById('result').value = event.data;
//	document.getElementById('photo').src =event.data;
<!-- += '<br />' + event.data;-->
	}
	function onError(event)
	{
 //      alert(event.data);
	}

	function start(el)
	{
       webSocket.send(el);
       return false;
	}
	function vout_OnClick()
	{

	}