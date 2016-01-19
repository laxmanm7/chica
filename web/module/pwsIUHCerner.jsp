<%@ include file="/WEB-INF/template/include.jsp"%>
<!DOCTYPE html>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/login.htm" redirect="/module/chica/pws.form" />
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/pwsIUHCerner.css" type="text/css" />
        <link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/chica/chica.css" type="text/css" />
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/chica/forcePrintJITs.css"/>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.min.css"/>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.structure.min.css"/>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.theme.min.css"/>
        <script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-1.9.1.min.js"></script>
        <script src="${pageContext.request.contextPath}/moduleResources/chica/jquery-ui-1.11.2/jquery-ui.min.js"></script>
        <script src="${pageContext.request.contextPath}/moduleResources/chica/pws.js"></script>
        <script src="${pageContext.request.contextPath}/moduleResources/chica/forcePrintJITs.js"></script>
        <title>CHICA Physician Encounter Form</title>
    </head>

    <body>
        <div id="formContainer">
            <form id="pwsForm" name="pwsForm" action="pwsIUHCerner.form" method="post">
                <div id="titleContainer">
                    <div id="submitFormTop">
                        <a href="#" id="submitButtonTop" class="icon-button ui-state-default ui-corner-all"><span class="ui-icon ui-icon-newwin"></span>Submit</a>
                    </div>
                    <div id="title">
                        <h3>CHICA Physician Encounter Form</h3>
                    </div>
                    <div id="mrn">
                        <h3><c:out value="${MRN}"/></h3>
                    </div>
                </div>
                <div id="infoLeft">
                    <b>Patient:</b> <c:out value="${PatientName}"/><br/>
                    <b>DOB:</b> <c:out value="${DOB}"/> <b>Age:</b> <c:out value="${Age}"/><br/>
                    <b>Doctor:</b> <c:out value="${Doctor}"/>
                </div>
                <div id="infoRight">
                    <b>MRN:</b> <c:out value="${MRN}"/><br/>
                    <b>Date:</b> <c:out value="${VisitDate}"/><br/>
                <b>Time:</b> <c:out value="${VisitTime}"/></div>
                                
                <%@ include file="pwsVitals.jsp" %>
                      
                <div id="buttons">
                    <div class="buttonsData">
                        <a href="#" id="formPrintButton" class="icon-button largeButton ui-state-default ui-corner-all"><span class="ui-icon ui-icon-newwin"></span>Recommended Handouts</a>
                    </div>
                    <div class="buttonsData">
                        <a href="#" id="forcePrintButton" class="icon-button largeButton ui-state-default ui-corner-all"><span class="ui-icon ui-icon-newwin"></span>Other Handouts</a>
                    </div>
                    <!-- <c:if test="${not empty diag1}">
                        <div class="buttonsData">
                            <a href="#" id="problemButton" class="icon-button ui-state-default ui-corner-all"><span class="ui-icon ui-icon-newwin"></span>Problem List</a>
                        </div>
                    </c:if>
                    <c:if test="${not empty Med1_A || not empty Med1_B || not empty Med2_A || not empty Med2_B || 
                                  not empty Med3_A || not empty Med3_B || not empty Med4_A || not empty Med4_B || 
                                  not empty Med5_A || not empty Med5_B || not empty Med6_A || not empty Med6_B}">
                        <div class="buttonsData">
                            <input id="medButton" type="button" value="Medications"/>
                        </div>
                    </c:if> -->
                </div>
                
                <%@ include file="pwsQuestions.jsp" %>
                
                 
                <div id="submitContainer">
                    <a href="#" id="submitButtonBottom" class="icon-button ui-state-default ui-corner-all"><span class="ui-icon ui-icon-newwin"></span>Submit</a>
                </div>
                
                <%@ include file="pwsDialogs.jsp" %>
                
                
                <input type=hidden id= "Choice1" name="Choice1"/>
                <input type=hidden id= "Choice2" name="Choice2"/>
                <input type=hidden id= "Choice3" name="Choice3"/>
                <input type=hidden id= "Choice4" name="Choice4"/>
                <input type=hidden id= "Choice5" name="Choice5"/>
                <input type=hidden id= "Choice6" name="Choice6"/>
                <input id="patientId" name="patientId" type="hidden" value="${patient.patientId}"/>
                <input id="encounterId" name="encounterId" type="hidden" value="${encounterId}"/>
                <input id="sessionId" name="sessionId" type="hidden" value="${sessionId}"/>
                <input id="formId" name="formId" type="hidden" value="${formId}"/>
                <input id="formInstanceId" name="formInstanceId" type="hidden" value="${formInstanceId}"/>
                <input id="locationId" name="locationId" type="hidden" value="${locationId}"/>
                <input id="locationTagId" name="locationTagId" type="hidden" value="${locationTagId}"/>
                <input id="maxElements" name="maxElements" type="hidden" value="5"/>
                <input id="language" name="language" type="hidden" value="${language}"/>
                <input id="formInstance" name="formInstance" type="hidden" value="${formInstance}"/>
                <input id="providerId" name="providerId" type="hidden" value="${providerId}"/>
                <input id="patientNameForcePrint" name="patientNameForcePrint" type="hidden" value="${PatientName}"/>
            </form>
        </div>
    </body>
</html> 