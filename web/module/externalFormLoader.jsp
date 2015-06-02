<%@ include file="/WEB-INF/template/include.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/externalFormLoader.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.min.css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.structure.min.css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.theme.min.css"/>
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-1.9.1.min.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.min.js"></script>
<script src="${pageContext.request.contextPath}/moduleResources/chica/externalFormLoader.js"></script>
</head>
<body>

<div id="content">
    <div>
        <c:choose>
	        <c:when test="${hasErrors eq 'true'}">
	           <c:choose>
	               <c:when test="${mrn ne null}">
	                   <div class="ui-state-error"><h2><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>Error loading form ${formName} for ${mrn}</h2></div>
	               </c:when>
	               <c:otherwise>
	                   <div class="ui-state-error"><h2><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span>Error loading form ${formName}</h2></div>
	               </c:otherwise>
	           </c:choose>
               <c:choose>
                   <c:when test="${missingUser eq 'true'}">
                       <div><p>No user was provided for authentication.</p></div>
                   </c:when>
                   <c:when test="${missingPassword eq 'true'}">
                       <div><p>No password was provided for authentication.</p></div>
                   </c:when>
                   <c:when test="${failedAuthentication eq 'true'}">
                       <div><p>Invalid username/password provided.</p></div>
                   </c:when>
                   <c:when test="${missingForm eq 'true'}">
                       <div><p>A valid formName parameter was not provided.</p></div>
                   </c:when>
                   <c:when test="${invalidForm eq 'true'}">
                       <div><p>A form with the name ${formName} cannot be found in the system.</p></div> 
                   </c:when>
                   <c:when test="${missingFormPage eq 'true'}">
                       <div><p>A valid formPage parameter was not provided.</p></div>
                   </c:when>
                   <c:when test="${missingMRN eq 'true'}">
                       <div><p>A valid mrn parameter was not provided.</p></div>
                   </c:when>
                   <c:when test="${invalidPatient eq 'true'}">
                       <div><p>A patient with MRN ${mrn} cannot be found in the system.</p></div>
                   </c:when>
                   <c:when test="${missingEncounter eq 'true'}">
                       <div><p>A valid encounter within the past 48 hours cannot be found for patient ${mrn}.</p></div>
                   </c:when>
                   <c:when test="${missingFormInstance eq 'true'}">
                       <div><p>A valid instance of the form ${formname} cannot be found for patient ${mrn}.</p></div>
                   </c:when>
                   <c:when test="${missingStartState eq 'true'}">
                       <div><p>A valid startState parameter was not provided.</p></div>
                   </c:when>
                   <c:when test="${missingEndState eq 'true'}">
                       <div><p>A valid endState parameter was not provided.</p></div>
                   </c:when>
                   <c:when test="${invalidStartState eq 'true'}">
                       <div><p>A start state with name ${startState} cannot be found in the system.</p></div>
                   </c:when>
                   <c:when test="${invalidEndState eq 'true'}">
                       <div><p>An end state with name ${endState} cannot be found in the system.</p></div>
                   </c:when>
                   <c:when test="${missingProviderId eq 'true'}">
                       <div><p>A valid providerId parameter was not provided.</p></div>
                   </c:when>
                   <c:when test="${invalidProviderId eq 'true'}">
                       <div><p>A provider with providerId ${providerId} cannot be found in the system.</p></div>
                   </c:when>
                   <c:when test="${missingVendor eq 'true'}">
                       <div><p>A valid vendor parameter was not provided.</p></div>
                   </c:when>
                   <c:when test="${invalidVendor eq 'true'}">
                       <div><p>A vendor with name ${vendor} cannot be found in the system.</p></div>
                   </c:when>
               </c:choose>
	        </c:when>
	        <c:otherwise>
	           <div><p>Loading form ${formName} for ${mrn}.  Please wait...</p></div>
	        </c:otherwise>
        </c:choose>
        <br/>
        <c:if test="${hasErrors eq 'true'}">
	        <div>Please close this window at your convenience.</div>
        </c:if>
    </div>
    <form id="loadForm" method="POST">
        <input id="formName" name="formName" type="hidden" value="${formName}"/>
        <input id="formPage" name="formPage" type="hidden" value="${formPage}"/>
        <input id="mrn" name="mrn" type="hidden" value="${mrn}"/>
        <input id="hasErrors" name="hasErrors" type="hidden" value="${hasErrors}"/>
        <input id="startState" name="startState" type="hidden" value="${startState}"/>
        <input id="endState" name="endState" type="hidden" value="${endState}"/>
        <input id="sessionId" name="endState" type="hidden" value="${sessionId}"/>
        <input id="providerId" name="providerId" type="hidden" value="${providerId}"/>
        <input id="vendor" name="vendor" type="hidden" value="${providerId}"/>
    </form>
</div>

</body>
</html>
