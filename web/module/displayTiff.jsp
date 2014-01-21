<%@ include file="/WEB-INF/template/include.jsp"%>

<page height="100%">
<link
	href="${pageContext.request.contextPath}/moduleResources/chica/chica.css"
	type="text/css" rel="stylesheet" />
<script type="text/javascript">

function printSelection(node){

  var content=node.innerHTML
  var pwin=window.open('','print_content','width=300,height=300');

  pwin.document.open();
  pwin.document.write('<html><body onload="window.print()">'+content+'</body></html>');
  pwin.document.close();
 
  setTimeout(function(){pwin.close();},1000);

}
</script>
<form height="100%" name="input" action="displayTiff.form" method="get">

	<table height="5%" width="100%"
		class="displayTiffHeader chicaBackground">
		<tr width="100%">
			<td width="25%"><INPUT TYPE="button" class="exitButton"
				VALUE="Exit" onClick="history.go(-1);return true;"></td>
			<td width="25%" class="displayLeftTiffHeaderSegment"><c:if
					test="${!empty leftImageFormname}">
					<b>${leftImageFormname}:&nbsp;${leftImageForminstance}</b>
				</c:if> <c:if test="${empty leftImageFormname}">
N/A
</c:if></td>
			<td width="50%" class="displayRighttiffHeaderSegment"><b> <c:if
						test="${!empty rightImageFormname}">
${rightImageFormname}:&nbsp;${rightImageForminstance}
</c:if> <c:if test="${empty rightImageFormname}">
N/A
</c:if> </b></td>
		</tr>
	</table>

	<table width="100%" height="95%" class="tiffs">
		<tr height="90%">
			<td width="50%">
			    <c:choose>
			        <c:when test="${empty leftHtmlOutput}">
					    <object width="100%" height="100%"
							classid="CLSID:106E49CF-797A-11D2-81A2-00E02C015623">
							<param name="src" value="${leftImagefilename}"/>
		
							<embed width="100%" height="100%" src="${leftImagefilename}"
								type="image/tiff"/>
						</object>
					</c:when>
					<c:otherwise>
					   <div style="width:100%">
					       <button type="button" onclick="printSelection(document.getElementById('divLeft'));return false">Print</button>
					   </div>
					   <hr/>
					   <div id="divLeft" style="height:100%; width:100%; position:relative; overflow:scroll">
                            ${leftHtmlOutput}
                       </div>
					</c:otherwise>
				</c:choose>
		    </td>
			<td width="50%">
			    <c:choose>
			        <c:when test="${empty rightHtmlOutput}">
					    <object width="100%" height="100%"
							classid="CLSID:106E49CF-797A-11D2-81A2-00E02C015623">
							<param name="src" value="${rightImagefilename}"/>
		
							<embed width="100%" height="100%" src="${rightImagefilename}"
								type="image/tiff"/>
						</object>
					</c:when>
					<c:otherwise>
					   <div style="width:100%">
                           <button type="button" onclick="printSelection(document.getElementById('divRight'));return false">Print</button>
                       </div>
					   <div id="divRight" style="height:100%; width:100%; position:relative; overflow:scroll">
                            ${rightHtmlOutput}
                       </div>
					</c:otherwise>
				</c:choose>
		    </td>
		</tr>
	</table>
</form>
</page>







