<%@page import="java.net.URLDecoder"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%
	String code = request.getParameter("fuid");
	String title = URLDecoder.decode(request.getParameter("title"), "UTF-8");
	System.out.println("code="+code);
	System.out.println("title="+title);
%>

<object id="LODOP" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA" width="0px" height="0px">
	<embed id="LODOP_EM" type="application/x-print-lodop" width="0px" height="0px"></embed>
</object>
<script type="text/javascript">
	var code = '<%=code%>';
	function print() {
		var LODOP = getLodop(document.getElementById('LODOP'), document .getElementById('LODOP_EM'));
		if ((LODOP == null) || (typeof (LODOP.VERSION) == "undefined")) {
			return false;
		}

		//LODOP.SET_PRINT_STYLE("FontSize", 20);
		//LODOP.SET_PRINT_STYLE("FontName", "黑体");
		var table2 = document.getElementById("printContext").innerHTML;
		if (typeof (LODOP.SET_PRINT_STYLE) == "undefined") {
			window.location.reload();
		}
		setBQ(LODOP);
		LODOP.SET_PRINT_PAGESIZE(1,"6cm","3cm","");
		LODOP.SET_PRINT_STYLE("FontSize", 24);
		LODOP.SET_PRINT_STYLE("FontName", "黑体");
		LODOP.ADD_PRINT_TABLE(-11, -4, 600, 300, table2);
		LODOP.ADD_PRINT_BARCODE(30, 10, 210, 70, '128A', code);
		LODOP.SET_PRINT_STYLEA(0, "ShowBarText", 0);
		LODOP.SET_PRINT_STYLEA(0, "LinkedItem", 0);
		LODOP.PREVIEW();
	}
	window.onload = print;
</script>
<div class="v1-container" id="v1-page-printFileDigit">
	<script type="text/javascript" src="lodop/LodopFuncs.js?v1.11"></script>
	<div class="container-fluid" id="printContext">
		<style>
			html{
				height: 160px;;
				width: 200px;
			}
		</style>
		<table class="table2">
			<tr style="width:500px">  
				<td align="center" style="width:200px"><div style="margin-top:10px;"><%=title%></div></td>
			</tr>
		</table>
	</div>
</div>

