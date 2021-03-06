var patientListFail = 0;
var refreshPeriod = 30000;
$(function() {
	$("#badScansArea").hide();
	$("#mrnError").hide();
	$("#manualCheckinError").hide();
	$("#manualCheckinComplete").hide();
	$("#manualCheckinSaving").hide();
	$("#manualCheckin").hide();
    
    $("#manualCheckinDob").datepicker({
    	showOn: "button",
    	buttonImage: ctx + "/moduleResources/chica/images/calendar.gif",
    	buttonImageOnly: true,
    	buttonText: "Select date of birth",
    	changeMonth:true,
    	changeYear:true,
    	appendText: "(mm/dd/yyyy)",
    	yearRange: "-21:+0"
    });
    
    $("#checkinButton, #viewEncountersButton, #printHandoutsButton, #selectPagerButton, #viewBadScans").button({
        icons: {
            primary: "ui-icon-newwin"
        }
    });
    
    $("#selectPagerButton").click(function() {
    	$("#pagerDialog").dialog("open");
    });
    
    $("#checkMRNButton").click(function() {
    	$("#listErrorDialog").dialog("close");
    });
    
    $("#checkinButton").click(function() {
    	$("#checkinMRNDialog").dialog("open");
    });
    
    $("#viewEncountersButton").click(function() {
    	$("#viewEncountersMRNDialog").dialog("open");
    });
    
    $("#printHandoutsButton").click(function() {
    	$("#printHandoutsMRNDialog").dialog("open");
    });
    
    $("#viewBadScans").click(function() {
    	displayBadScans(ctx,$("#badScans").val())
    });
    
    $("#listErrorDialog").dialog({
        open: function() { 
            $(".ui-dialog").addClass("ui-dialog-shadow"); 
        },
        close: function() { 
        	document.location.href = "greaseBoard.form";
        },
        autoOpen: false,
        modal: true,
        resizable: false,
        show: {
          effect: "fade",
          duration: 500
        },
        hide: {
          effect: "fade",
          duration: 500
        },
        buttons: [
          {
	          text:"OK",
	          click: function() {
	          	  document.location.href = "greaseBoard.form";
	          }
          }
        ]
    });
    
    $("#checkinMRNDialog").dialog({
        open: function() { 
        	$("#mrnLookup").val("");
            $(".ui-dialog").addClass("ui-dialog-shadow"); 
        },
        close: function() { 
            $("#mrnError").hide();
        },
        autoOpen: false,
        modal: true,
        resizable: false,
        show: {
          effect: "fade",
          duration: 500
        },
        hide: {
          effect: "fade",
          duration: 500
        },
        buttons: [
          {
        	  id: "checkinMRNOKButton",
	          text:"OK",
	          icons: {
	        	  primary: "ui-icon-newwin"
	          },
	          click: function() {
	            checkMRN();
	          }
          },
          {
        	  id: "checkinMRNCancelButton",
	          text:"Cancel",
	          click: function() {
	            $("#checkinMRNDialog").dialog("close");
	          }
          }
        ]
    });
    
    $("#manualCheckinDialog").dialog({
        open: function() { 
            $(".ui-dialog").addClass("ui-dialog-shadow"); 
        },
        close: function() { 
            $("#manualCheckinError").hide();
            $("#manualCheckinSaving").hide();
            $("#manualCheckinComplete").hide();
            $("#manualCheckinForm")[0].reset();
            $("#manualCheckinDialog").dialog("option", "buttons", []);
        },
        autoOpen: false,
        modal: true,
        resizable: false,
        show: {
          effect: "fade",
          duration: 500
        },
        hide: {
          effect: "fade",
          duration: 500
        },
        width:775
    });
    
    $("#viewEncountersMRNDialog").dialog({
        open: function() { 
        	$("#encounterMrnLookup").val("");
            $(".ui-dialog").addClass("ui-dialog-shadow"); 
        },
        close: function() { 
            $("#encounterMrnError").hide();
        },
        autoOpen: false,
        modal: true,
        resizable: false,
        show: {
          effect: "fade",
          duration: 500
        },
        hide: {
          effect: "fade",
          duration: 500
        },
        buttons: [
          {
        	  id: "viewEncountersMRNOKButton",
	          text:"OK",
	          icons: {
	        	  primary: "ui-icon-newwin"
	          },
	          click: function() {
	            checkEncounterMRN();
	          }
          },
          {
        	  id: "viewEncountersMRNCancelButton",
	          text:"Cancel",
	          click: function() {
	            $("#viewEncountersMRNDialog").dialog("close");
	          }
          }
        ]
    });
    
    $("#printHandoutsMRNDialog").dialog({
        open: function() { 
        	$("#printHandoutsMrnLookup").val("");
            $(".ui-dialog").addClass("ui-dialog-shadow"); 
        },
        close: function() { 
            $("#printHandoutsMrnError").hide();
        },
        autoOpen: false,
        modal: true,
        resizable: false,
        show: {
          effect: "fade",
          duration: 500
        },
        hide: {
          effect: "fade",
          duration: 500
        },
        buttons: [
          {
        	  id: "printHandoutsMRNOKButton",
	          text:"OK",
	          icons: {
	        	  primary: "ui-icon-newwin"
	          },
	          click: function() {
	            checkPrintHandoutsMRN();
	          }
          },
          {
        	  id: "printHandoutsMRNCancelButton",
	          text:"Cancel",
	          click: function() {
	            $("#printHandoutsMRNDialog").dialog("close");
	          }
          }
        ]
    });
    
    $("#otherDoctorDialog").dialog({
        resizable: false,
        modal: true,
        autoOpen: false,
        open: function() { 
            $(".ui-dialog").addClass("ui-dialog-shadow"); 
          },
        show: {
            effect: "fade",
            duration: 500
          },
          hide: {
            effect: "fade",
            duration: 500
          },
        buttons: {
          "Yes": function() {
            $(this).dialog("close");
            checkNewPatient();
          },
          "No": function() {
            $(this).dialog("close");
          }
        }
    });

    $("#otherStationDialog").dialog({
        resizable: false,
        modal: true,
        autoOpen: false,
        open: function() { 
            $(".ui-dialog").addClass("ui-dialog-shadow"); 
          },
        show: {
            effect: "fade",
            duration: 500
          },
          hide: {
            effect: "fade",
            duration: 500
          },
        buttons: {
          "Yes": function() {
            $(this).dialog("close");
            checkDoctor();
          },
          "No": function() {
            $(this).dialog("close");
          }
        }
    });
    
    $("#newPatientDialog").dialog({
        resizable: false,
        modal: true,
        autoOpen: false,
        open: function() { 
            $(".ui-dialog").addClass("ui-dialog-shadow"); 
          },
        show: {
            effect: "fade",
            duration: 500
          },
          hide: {
            effect: "fade",
            duration: 500
          },
        buttons: {
          "Yes": function() {
            $(this).dialog("close");
            submitManualCheckin();
          },
          "No": function() {
            $(this).dialog("close");
          }
        }
    });
    $("#pagerDialog").dialog({
        resizable: false,
        modal: true,
        autoOpen: false,
        open: function() { 
            $("#pagerSaving").hide();
            $("#pagerComplete").hide();
            $("#pagerDialog").dialog("option", "buttons", 
  			  [
  			    {
  			      id: "pagerSendButton",
  			      text: "Send",
  			      click: function() {
  			    	  sendPage();
  			      }
  			    },
  	            {
  	        	  id: "pagerCancelButton",
  		          text:"Cancel",
  		          click: function() {
  		            $("#pagerDialog").dialog("close");
  		          }
  	            }
  			  ]
  			);
            $(".ui-dialog").addClass("ui-dialog-shadow");
        },
        close: function() { 
            $("#pagerDescription").val("");
            $("#pagerName").val("");
            $("#pagerTextCount").html("0 of 160 character max");
            $("#pagerError").hide();
            $("#pagerBody").show();
        },
        show: {
            effect: "fade",
            duration: 500
          },
          hide: {
            effect: "fade",
            duration: 500
          }
    });
    $("#adhdWorkupDialog").dialog({
        resizable: false,
        modal: true,
        autoOpen: false,
        open: function() { 
            $(".ui-dialog").addClass("ui-dialog-shadow"); 
          },
        show: {
            effect: "fade",
            duration: 500
          },
          hide: {
            effect: "fade",
            duration: 500
          },
        buttons: {
          "Yes": function() {
        	var form = $(this).data("form");
            $(this).dialog("close");
            form.submit();
          },
          "No": function() {
            $(this).dialog("close");
          }
        }
    });
    
    var cell = document.getElementById("badScansCell");
    setInterval("Timer()", 500);
    x=1;
    
    $("#mrnLookup").keypress(function(e){
	    if (e.which == 13) {
	     e.preventDefault();
	     checkMRN();    
	    }
	});
    
    $("#pagerName").keypress(function(e){
	    if (e.which == 13) {
	     e.preventDefault();
	     sendPage();    
	    }
	});
    
    $("#encounterMrnLookup").keypress(function(e){
	    if (e.which == 13) {
	     e.preventDefault();
	     checkEncounterMRN();    
	    }
	});
    
    $("#printHandoutsMrnLookup").keypress(function(e){
	    if (e.which == 13) {
	     e.preventDefault();
	     checkPrintHandoutsMRN();    
	    }
	});
    
    $(".tableSelect").selectmenu();
    
    $("#manualCheckinState").selectmenu().selectmenu("menuWidget").addClass("stateOverflow");
    
    $("#pagerDescription").keyup(function() {
    	var max = 160;
    	var length = $(this).val().length;
    	$("#pagerTextCount").html(length + " of " + max + " character max");
    });
    
    $("#patientTable").floatThead({
        scrollContainer: function(){
            return $(".encounterarea");
        }       
    });
    $("#patientTable").show();
    
    $(window).bind('resize', resizeContent);
    resizeContent();
    
    setOverlayCSS();
});

function checkMRN() {
	$("#mrnError").hide();
	var url = ctx + "/moduleServlet/chica/chica";
	  $.ajax({
		  beforeSend: function(){
			  $("#mrnLoading").show();
			  $("#checkinMRNOKButton").button("disable");
	      },
	      complete: function(){
	    	  $("#mrnLoading").hide();
	    	  $("#checkinMRNOKButton").button("enable");
	      },
	      "cache": false,
	      "dataType": "xml",
	      "data": "action=verifyMRN&mrn=" + encodeURIComponent($("#mrnLookup").val()),
	      "type": "POST",
	      "url": url,
	      "timeout": 60000, // optional if you want to handle timeouts (which you should)
	      "error": handleVerifyMRNAjaxError, // this sets up jQuery to give me errors
	      "success": function (xml) {
	          verifyMRN(xml);
	      }
	  });
}

function checkEncounterMRN() {
	$("#encounterMrnError").hide();
	var url = ctx + "/moduleServlet/chica/chica";
	var newTab = window.open ('', '_blank');
	  $.ajax({
		  beforeSend: function(){
			  $("#viewEncountersMRNOKButton").button("disable");
			  $("#encounterMrnLoading").show();
	      },
	      complete: function(){
	    	  $("#encounterMrnLoading").hide();
	    	  $("#viewEncountersMRNOKButton").button("enable");
	      },
	      "cache": false,
	      "dataType": "xml",
	      "data": "action=verifyMRN&mrn=" + encodeURIComponent($("#encounterMrnLookup").val()),
	      "type": "POST",
	      "url": url,
	      "timeout": 60000, // optional if you want to handle timeouts (which you should)
	      "error": handleVerifyEncounterMRNAjaxError, // this sets up jQuery to give me errors
	      "success": function (xml) {
	          verifyEncounterMRN(xml, newTab);
	      }
	  });
}

function checkPrintHandoutsMRN() {
	$("#printHandoutsMrnError").hide();
	var url = ctx + "/moduleServlet/chica/chica";
	  $.ajax({
		  beforeSend: function(){
			  $("#printHandoutsMRNOKButton").button("disable");
			  $("#printHandoutsMrnLoading").show();
	      },
	      complete: function(){
	    	  $("#printHandoutsMrnLoading").hide();
	    	  $("#printHandoutsMRNOKButton").button("enable");
	      },
	      "cache": false,
	      "dataType": "xml",
	      "data": "action=verifyMRN&mrn=" + encodeURIComponent($("#printHandoutsMrnLookup").val()),
	      "type": "POST",
	      "url": url,
	      "timeout": 60000, // optional if you want to handle timeouts (which you should)
	      "error": handleVerifyPrintHandoutsMRNAjaxError, // this sets up jQuery to give me errors
	      "success": function (xml) {
	          verifyPrintHandoutsMRN(xml);
	      }
	  });
}

function getManualCheckinInfo() {
	$("#mrnError").hide();
	var url = ctx + "/moduleServlet/chica/chica";
	  $.ajax({
		  beforeSend: function(){
			  $("#manualCheckinLoading").show();
			  $("#manualCheckinError").hide();
			  $("#manualCheckin").hide();
	      },
	      complete: function(){
	    	  $("#manualCheckinLoading").hide();
	      },
	      "cache": false,
	      "dataType": "xml",
	      "data": "action=getManualCheckin&mrn=" + encodeURIComponent($("#mrnLookup").val()),
	      "type": "POST",
	      "url": url,
	      "timeout": 60000, // optional if you want to handle timeouts (which you should)
	      "error": handleGetManualCheckinAjaxError, // this sets up jQuery to give me errors
	      "success": function (xml) {
	          parseManualCheckinInfo(xml);
	      }
	  });
}

function setOverlayCSS() {
	 var encounterArea = $("#middle");
	$("#overlay").css({
      opacity : 0.3,
      width   : encounterArea.outerWidth(),
      height  : encounterArea.outerHeight()
    });

    $("#img-load").css({
      top  : (encounterArea.height() / 2) -15,
      left : ((encounterArea.width() / 2) -30)
    });
}

function Timer() {
    set=1;
    var cell = $('#badScansCell');
    if(x==0 && set==1) {
        $("#badScansCell").attr("class", "ui-state-default");
        x=1;
        set=0;
    }
    if(x==1 && set==1) {
 	   $("#badScansCell").attr("class", "ui-state-error");
        x=0;
        set=0;
    }
}

function resizeContent() {
    var windowHeight = $(window).height();
    $("#middle").css("height", windowHeight - 175);
    setOverlayCSS();
}

function startTimer(period) {
	if (period === null) {
		refreshPeriod = 30000;
	} else {
		refreshPeriod = period * 1000;
	}
    
    // This delay allows the wait cursor to display when loading the patient
	// list.
    setTimeout("populateList()", 1);
}

function populateList() {
  var url = ctx + "/moduleServlet/chica/chica";
  $.ajax({
	  beforeSend: function(){
		  $("#overlay").fadeIn();
      },
      complete: function(){
    	  $("#overlay").fadeOut();
      },
      "cache": false,
      "dataType": "xml",
      "data": "action=getGreaseboardPatients",
      "type": "POST",
      "url": url,
      "timeout": 60000, // optional if you want to handle timeouts (which you should)
      "error": handlePatientListAjaxError, // this sets up jQuery to give me errors
      "success": function (xml) {
    	  patientListFail = 0;
          parsePatientList(xml);
          setTimeout("populateList()", refreshPeriod);
      }
  });
}

function parsePatientList(responseXML) {
    // no matches returned
    if (responseXML === null) {
        return false;
    } else {
    	var needVitals = $(responseXML).find("needVitals").text();
    	$("#needVitals").html(needVitals);
    	var waitingForMD = $(responseXML).find("waitingForMD").text();
    	$("#waitingForMD").html(waitingForMD);
    	var new_tbody = document.createElement("tbody");
    	var content = "";
        var count = 1;
        $(responseXML).find("patientRow").each(function () {
        	var rowColor = "tableRowOdd";
        	count++;
            if ((count%2) === 1) {
            	rowColor = "tableRowEven";
            }
            
            var manualCheckin = $(this).find("isManualCheckin").text();
            if (manualCheckin === 'true') {
            	rowColor = "tableRowManualCheckin";
            }
            
            var firstName = $(this).find("firstName").text();
            var lastName = $(this).find("lastName").text();
        	content += '<tr style="text-align: left">';
        	content += '<td class="ln ' + rowColor + '">' + lastName + '</td>';
        	content += '<td class="fn ' + rowColor + '">' + firstName + '</td>';
        	content += '<td class="mrn ' + rowColor + '">' + $(this).find("mrn").text() + '</td>';
        	content += '<td class="dob ' + rowColor + '">' + $(this).find("dob").text() + '</td>';
        	content += '<td class="sex ' + rowColor + '">' + $(this).find("sex").text() + '</td>';
        	content += '<td class="MD ' + rowColor + '">' + $(this).find("mdName").text() + '</td>';
        	content += '<td class="aptTime ' + rowColor + '">' + $(this).find("appointment").text() + '</td>';
        	content += '<td class="chkTime ' + rowColor + '">' + $(this).find("checkin").text() + '</td>';
        	var reprintStatus = $(this).find("reprintStatus").text();
        	if (reprintStatus === "true") {
        		content += '<td class="reprint ' + rowColor + '"><span style="color: red;text-shadow: 1px 1px #000000;"><b>*</b></span></td>';
        	} else {
        		content += '<td class="reprint ' + rowColor + '">&nbsp;</td>';
        	}
        	
        	var statusText = $(this).find("status").text();
        	if (statusText == null || statusText == "null" || statusText.trim().length == 0) {
        		content += '<td class="status ' + rowColor + '" style="vertical-align:middle">Please wait...</td>';
        	} else {
        		content += '<td class="status ' + $(this).find("statusColor").text() + '" style="vertical-align:middle">' + statusText + '</td>';
        	}
            content +='<td class="action ' + rowColor + '">' +
							'<form method="post" style="margin: 0px; padding: 0px;">' +
							    '<fieldset class="tableFieldset">' +
									'<select name="options" class="view-forms tableSelect">' +
										'<option value="unselected">Options</option>' +
										'<option>Encounters</option>' +
										'<option>Print JITS</option>' +
										'<option value="Print Pre-Screener">Continue Tablet PSF</option>' +
										'<option>Print Physician Worksheet</option>' +
										'<option>ADHD WU</option>' +
									'</select>' +
								'</fieldset>' +
								'<input type="hidden" value="' + $(this).find("patientId").text() + '" name="greaseBoardPatientId"/>' +
								'<input type="hidden" value="' + $(this).find("sessionId").text() + '" name="greaseBoardSessionId"/>' +
								'<input type="hidden" value="' + $(this).find("locationId").text() + '" name="greaseBoardLocationId"/>' +
								'<input type="hidden" value="' + $(this).find("locationTagId").text() + '" name="greaseBoardLocationTagId"/>' +
								'<input type="hidden" value="' + firstName + " " + lastName + '" name="greaseBoardPatientName"/>' +
							'</form>' +
						'</td>';
            content = content + '</tr>';
        });
        
        // Get the bad scans
        var badScans = "";
        var badScanCount = 0;
        $(responseXML).find("url").each(function () {
        	badScanCount++;
        	if (badScanCount != 1) {
        		badScans += ",";
        	}
        	
        	badScans += $(this).text();
        });
        
        if (badScanCount > 0) {
        	$("#badScansArea").show();
        	$("#badScans").val(badScans);
        } else {
        	$("#badScansArea").hide();
        }
        
        $("#patientTable tbody").html(content);
        $("#patientTable").trigger("reflow");
        $(".view-forms").selectmenu({
            select: function( event, ui ) {
                var formInstance = ui.item.value;
                if (formInstance == "unselected") {
                    // A valid form was not selected
                } else {
                	var form = $(this).closest("form");
                	confirmation(this, form);
                }
            }
        });
    }
}

function handlePatientListAjaxError(xhr, textStatus, error) {
	patientListFail++;
	if (patientListFail < 4) {
		// try populating again before informing user.
		setTimeout("populateList()", 1);
	}
	else {
	    var error = "An error occurred on the server.";
	    if (textStatus === "timeout") {
	        error = "The server took too long to retrieve the patient list.";
	    }
	    
	    $("#listErrorResultDiv").html("<p>" + error + "  Click OK to try again.</p>");
	    $("#listErrorDialog").dialog("open");
	}
}

function verifyMRN(responseXML) {
    // no matches returned
    if (responseXML === null) {
    	$("#mrnMessage").html("<p><b>Error retrieving MRN information.  Please try again.</b></p>");
        $("#mrnError").show("highlight", 750);
    } else {
    	var result = $(responseXML).find("result").text();
        if (result == "true") {
        	getManualCheckinInfo();
        	$("#checkinMRNDialog").dialog("option", "hide", {effect: "none" } );
        	$("#checkinMRNDialog").dialog("close");
        	$("#checkinMRNDialog").dialog("option", "hide", { effect: "fade", duration: 500 } );
        	$("#manualCheckinDialog").dialog("open");
        } else {
        	$("#mrnMessage").html("<p><b>MRN is not valid.<br>Retype the MRN #. Press OK to display the record.</b></p>");
            $("#mrnError").show("highlight", 750);
        }
    }
}

function verifyEncounterMRN(responseXML, newTab) {
    // no matches returned
    if (responseXML === null) {
    	$("#encounterMrnMessage").html("<p><b>Error retrieving MRN information.  Please try again.</b></p>");
        $("#encounterMrnError").show("highlight", 750);
    } else {
    	var result = $(responseXML).find("result").text();
		var validEncounter = $(responseXML).find("validEncounter").text();
        if (result == "true" && validEncounter == "true") {
        	$("#viewEncountersMRNDialog").dialog("close");
        	popupfull("viewEncounter.form?mrn=" + $("#encounterMrnLookup").val(), newTab);
        } else if (result == "false"){
			newTab.close();
        	$("#encounterMrnMessage").html("<p><b>MRN is not valid.<br>Retype the MRN #. Press OK to display the encounters.</b></p>");
            $("#encounterMrnError").show("highlight", 750);
        } else { 
			newTab.close();
			var mrn = $("#encounterMrnLookup").val();
        	$("#encounterMrnMessage").html("<p><b>Patient "+ mrn +" does not exist in the CHICA system with a valid encounter.<br>Please re-enter the MRN #. </b></p>");
            $("#encounterMrnError").show("highlight", 750);
        }
    }
}

function verifyPrintHandoutsMRN(responseXML) {
    // no matches returned
    if (responseXML === null) {
    	$("#printHandoutsMrnMessage").html("<p><b>Error retrieving MRN information.  Please try again.</b></p>");
        $("#printHandoutsMrnError").show("highlight", 750);
    } else {
    	var result = $(responseXML).find("result").text();
		var validEncounter = $(responseXML).find("validEncounter").text();
		if (result == "true" && validEncounter == "true") {
        	$("#printHandoutsMrnError").hide();
        	$("#patientId").val("");
        	$("#sessionId").val("");
        	$("#locationId").val("");
        	$("#locationTagId").val("");
        	$("#patientName").val("");
        	var mrn = $("#printHandoutsMrnLookup").val();
        	$("#mrn").val(mrn);
        	$(".force-print-patient-name").html("<p>Please choose form(s) for #" + mrn + ".</p>");
        	$("#printHandoutsMRNDialog").dialog("option", "hide", {effect: "none" } );
        	$("#printHandoutsMRNDialog").dialog("close");
        	$("#printHandoutsMRNDialog").dialog("option", "hide", { effect: "fade", duration: 500 } );
        	$("#force-print-dialog").dialog("open");
        } else if (result == "false"){
        	$("#printHandoutsMrnMessage").html("<p><b>MRN is not valid.<br>Retype the MRN #. Press OK to display the patient handouts.</b></p>");
            $("#printHandoutsMrnError").show("highlight", 750);
        } else if (validEncounter == "false"){
			var mrn = $("#printHandoutsMrnLookup").val();
        	$("#printHandoutsMrnMessage").html("<p><b>Patient "+ mrn +" does not exist in the CHICA system with a valid encounter.<br>Please re-enter the MRN #. </b></p>");
            $("#printHandoutsMrnError").show("highlight", 750);
        }
    }
}

function parseManualCheckinInfo(responseXML) {
    // no matches returned
    if (responseXML === null) {
        return false;
    } else {
    	var patient = $(responseXML).find("patient");
    	var mrn = patient.find("mrn").text();
    	if (mrn != null && mrn.trim() != "") {
    		$("#manualCheckinMrnDisplay").val(mrn);
    		$("#manualCheckinMrn").val(mrn);
    	} else {
    		$("#manualCheckinMrnDisplay").val($("#mrnLookup").val());
    		$("#manualCheckinMrn").val($("#mrnLookup").val());
    	}
    	
    	$("#manualCheckinStation").find("option").remove();
    	var stations = $(responseXML).find("stations");
    	var options = [];
    	stations.find("station").each(function () {
    		var station = $(this).text();
			options.push("<option value='" + station + "'>" + station + "</option>");
    	});
    	
    	$("#manualCheckinStation").selectmenu("destroy");
    	$("#manualCheckinStation").append(options.join(""));
    	$("#manualCheckinStation").prop('selectedIndex', 0);
    	$("#manualCheckinStation").selectmenu().selectmenu("menuWidget").addClass("stationOverflow");
    	
    	$("#manualCheckinDoctor").find("option").remove();
    	var doctors = $(responseXML).find("doctors");
    	options = [];
    	doctors.find("doctor").each(function () {
    		var firstName = $(this).find("firstName").text();
    		var lastName = $(this).find("lastName").text();
    		var providerId = $(this).find("providerId").text();
    		if ("Other" === lastName) {
    			var text = "<option value='" + providerId + "' selected>" + lastName;
    			if (firstName != null && firstName.trim() != "") {
    				text += ", " + firstName;
    			}
    			
    			text += "</option>";
    			options.push(text);
    		} else {
    			var text = "<option value='" + providerId + "'>" + lastName;
    			if (firstName != null && firstName.trim() != "") {
    				text += ", " + firstName;
    			}
    			
    			text += "</option>";
    			options.push(text);
    		}
    	});
    	
    	$("#manualCheckinDoctor").selectmenu("destroy");
    	$("#manualCheckinDoctor").append(options.join(""));
    	$("#manualCheckinDoctor").selectmenu().selectmenu("menuWidget").addClass("doctorOverflow");
    	$("#manualCheckinSSNOne").val(patient.find("ssn1").text());
        $("#manualCheckinSSNTwo").val(patient.find("ssn2").text());
        $("#manualCheckinSSNThree").val(patient.find("ssn3").text());
        $("#manualCheckinLastName").val(patient.find("lastName").text());
        $("#manualCheckinFirstName").val(patient.find("firstName").text());
        $("#manualCheckinMiddleName").val(patient.find("middleName").text());
        $("#manualCheckinDob").datepicker("setDate", patient.find("dob").text());
        var sex = patient.find("sex").text();
        if (sex != null && sex.trim() != "") {
        	$("#manualCheckinSex").val(sex);
        } else {
        	$("#manualCheckinSex").val("U");
        }
        $("#manualCheckinSex").selectmenu().selectmenu("refresh");
        
        $("#manualCheckinNOKLastName").val(patient.find("nextOfKinLastName").text());
        $("#manualCheckinNOKFirstName").val(patient.find("nextOfKinFirstName").text());
        $("#manualCheckinPhone").val(patient.find("dayPhone").text());
        $("#manualCheckinStreetAddress").val(patient.find("address1").text());
        $("#manualCheckinStreetAddress2").val(patient.find("address2").text());
        $("#manualCheckinCity").val(patient.find("city").text());
        var state = patient.find("state").text();
        if (state != null && state.trim() != "") {
        	$("#manualCheckinState").val(state);
        } else {
        	$("#manualCheckinState").prop('selectedIndex', 0);
        }
        $("#manualCheckinState").selectmenu().selectmenu("refresh");
        
        $("#manualCheckinRace").find("option").remove();
        var races = $(responseXML).find("raceCodes");
    	options = [];
    	options.push("<option></option>");
    	var race = patient.find("race").text();
    	races.find("raceCode").each(function () {
    		var category = $(this).attr("category");
    		if (category === race) {
    			options.push("<option value='" + $(this).text() + "' selected>" + category + "</option>");
    		} else {
    			options.push("<option value='" + $(this).text() + "'>" + category + "</option>");
    		}
			
    	});
    	
    	$("#manualCheckinRace").selectmenu("destroy");
    	$("#manualCheckinRace").append(options.join(""));
    	$("#manualCheckinRace").selectmenu().selectmenu("menuWidget").addClass("raceOverflow");
    	$("#manualCheckinInsuranceCategory").find("option").remove();
    	var insurances = $(responseXML).find("insuranceCategories");
    	options = [];
    	options.push("<option></option>");
    	var insuranceCode = patient.find("insuranceCode").text();
    	insurances.find("insuranceCategory").each(function () {
    		var category = $(this).text();
    		if (category === insuranceCode) {
    			options.push("<option value='" + category + "' selected>" + category + "</option>");
    		} else {
    			options.push("<option value='" + category + "'>" + category + "</option>");
    		}
			
    	});
    	
    	$("#manualCheckinInsuranceCategory").selectmenu("destroy");
    	$("#manualCheckinInsuranceCategory").append(options.join(""));
    	$("#manualCheckinInsuranceCategory").selectmenu().selectmenu("menuWidget").addClass("insuranceOverflow");
        $("#manualCheckinZip").val(patient.find("zip").text());
        var newPatient = $(responseXML).find("newPatient").text();
        $("#manualCheckinNewPatient").val(newPatient);
        if ("true" === newPatient) {
        	$("#manualCheckinDialog").dialog("option", "buttons", 
			  [
			    {
			      id: "manualCheckinAddCheckinButton",
			      text: "Add + Checkin",
			      click: function() {
			    	  $("#manualCheckinError").hide();
		          	  checkForm();
			      }
			    },
  	            {
  	        	  id: "manualCheckinAddCancelButton",
  		          text:"Cancel",
  		          click: function() {
  		            $("#manualCheckinDialog").dialog("close");
  		          }
  	            }
			  ]
			);
        } else {
        	$("#manualCheckinDialog").dialog("option", "buttons", 
  			  [
  			    {
  			      id: "manualCheckinCheckinButton",
  			      text: "Checkin",
  			      click: function() {
  			    	  $("#manualCheckinError").hide();
  		          	  checkForm();
  			      }
  			    },
  	            {
	        	  id: "manualCheckinAddCancelButton",
		          text:"Cancel",
		          click: function() {
		            $("#manualCheckinDialog").dialog("close");
		          }
	            }
  			  ]
  			);
        }
        $("#manualCheckin").show();
    }
}

function handleVerifyMRNAjaxError(xhr, textStatus, error) {
    var error = "An error occurred on the server.";
    if (textStatus === "timeout") {
        error = "The server took too long to verify the MRN.";
    }
    
    $("#mrnMessage").html("<p>" + error + "  Click OK to try again.</p>");
    $("#mrnError").show("highlight", 750);
}

function handleVerifyEncounterMRNAjaxError(xhr, textStatus, error) {
    var error = "An error occurred on the server.";
    if (textStatus === "timeout") {
        error = "The server took too long to verify the MRN.";
    }
    
    $("#encounterMrnMessage").html("<p>" + error + "  Click OK to try again.</p>");
    $("#encounterMrnError").show("highlight", 750);
}

function handleVerifyPrintHandoutsMRNAjaxError(xhr, textStatus, error) {
    var error = "An error occurred on the server.";
    if (textStatus === "timeout") {
        error = "The server took too long to verify the MRN.";
    }
    
    $("#printHandoutsMrnMessage").html("<p>" + error + "  Click OK to try again.</p>");
    $("#printHandoutsMrnError").show("highlight", 750);
}

function handleGetManualCheckinAjaxError(xhr, textStatus, error) {
    var error = "An error occurred on the server.";
    if (textStatus === "timeout") {
        error = "The server took too long to verify the MRN.";
    }
    
    $("#manualCheckinMessage").html("<p>" + error + "  Please close this window and try again.</p>");
    $("#manualCheckinError").show("highlight", 750);
    $("#manualCheckinDialog").dialog("option", "buttons", []);
}

function setRefreshPeriod(period) {
	refreshPeriod = period;
}

function test(period) {
	
}

/* Idea by:  Nic Wolfe */
/* This script and many more are available free online at */
/* The JavaScript Source!! http:// javascript.internet.com */
function popUp(URL) {
    day = new Date();
    id = day.getTime();
    eval("page" + id + " = window.open(URL, '" + id + "', 'toolbar=0,scrollbars=0,location=0,statusbar=0,menubar=0,resizable=0,width=250,height=250,left = 312,top = 284');");
}

function pagerPopUp(URL) {
    window.open(URL, '', 'toolbar=0, scrollbars=0,location=0,locationbar=0,statusbar=0,menubar=0,resizable=0,width=400,height=100,left = 312,top = 284');
}

function lookupPatient(){
    document.location.href = "viewPatient.form";
    return false;
}

function popupfull(url, newTab) 
{
	newTab.location = url;
	
	return false;
}

function confirmation(optionsSelect, formName) {
    var answer = true;
    var selectedIndex = optionsSelect.selectedIndex;
    if(optionsSelect[selectedIndex].text == 'Encounters'){
        answer = false;
        var patientId = formName.find("input[name=greaseBoardPatientId]").val();
        var str = 'viewEncounter.form?patientId='+patientId;
        var newTab = window.open ('', '_blank');
        popupfull(str, newTab);
    } else if(optionsSelect[selectedIndex].text == 'Print JITS'){
	    answer = false;
	    var patientId = formName.find("input[name=greaseBoardPatientId]").val();
		var sessionId = formName.find("input[name=greaseBoardSessionId]").val();
		var locationId = formName.find("input[name=greaseBoardLocationId]").val();
		var locationTagId = formName.find("input[name=greaseBoardLocationTagId]").val();
		var patientName = formName.find("input[name=greaseBoardPatientName]").val();
		$("#patientId").val(patientId);
		$("#sessionId").val(sessionId);
		$("#locationId").val(locationId);
		$("#locationTagId").val(locationTagId);
		$("#patientName").val(patientName);
		$("#force-print-dialog").dialog("open");
		$(".force-print-patient-name").html("<p>Please choose form(s) for " + patientName + ".</p>");
	} else if(optionsSelect[selectedIndex].text == 'ADHD WU'){
        $("#adhdWorkupDialog").data("form", formName).dialog("open");
    } else {
    	formName.submit();
    }
}

function displayBadScans(context, badScans) {
	var newTab = window.open ('', '_blank');
	var str = context + '/module/chica/displayBadScans.form?badScans='+badScans;
	popupfull(str, newTab);
}

function checkForm() {       
	var ssn1 = $("#manualCheckinSSNOne").val();
	var ssn2 = $("#manualCheckinSSNTwo").val();
	var ssn3 = $("#manualCheckinSSNThree").val();
    if (ssn1 != ''|| ssn2 != ''|| ssn3 != '') {
       var stringSSN = ssn1 + ssn2 + ssn3;
	   var RE_SSN = /^[0-9]{3}[\- ]?[0-9]{2}[\- ]?[0-9]{4}$/;
	   if (RE_SSN.test(stringSSN)) {
       } else {
    	   $("#manualCheckinMessage").html("<p>The patient must have a valid SSN number.</p>");
    	   $("#manualCheckinError").show("highlight", 750);
		   return false;
       }
	}
	  
	if ($("#manualCheckinMrnDisplay").val() === "") {
		$("#manualCheckinMessage").html("<p>The patient must have a medical record number.</p>");
 	    $("#manualCheckinError").show("highlight", 750);
		return false;
	}
	else if ($("#manualCheckinLastName").val() === "") {
		$("#manualCheckinMessage").html("<p>The patient must have a last name.</p>");
 	    $("#manualCheckinError").show("highlight", 750);
		return false;
	}
	else if ($("#manualCheckinFirstName").val() === "") {
		$("#manualCheckinMessage").html("<p>The patient must have a first name.</p>");
 	    $("#manualCheckinError").show("highlight", 750);
		return false;
	}
	else if ($("#manualCheckinSex option:selected").val() === "U") 
	{
		$("#manualCheckinMessage").html("<p>Please select a valid sex.</p>");
 	    $("#manualCheckinError").show("highlight", 750);
        return false;
	}
	else if ($("#manualCheckinDob").val() === "") {
		$("#manualCheckinMessage").html("<p>The patient must have a date of birth.</p>");
 	    $("#manualCheckinError").show("highlight", 750);
		return false;
	} 
	else if (isNaN(Date.parse($("#manualCheckinDob").val()))) {
		$("#manualCheckinMessage").html("<p>Please enter a valid date of birth.</p>");
 	    $("#manualCheckinError").show("highlight", 750);
 	    return false;
	}
	else if ($("#manualCheckinNOKFirstName").val() != "" &&
			   $("#manualCheckinNOKLastName").val() === "") {
		$("#manualCheckinMessage").html("<p>A next of kin first name has been entered.  Please enter a next of kin last name.</p>");
 	    $("#manualCheckinError").show("highlight", 750);
        return false;
	}
	else if ($("#manualCheckinNOKLastName").val() != "" &&
			$("#manualCheckinNOKFirstName").val() === "") {
		$("#manualCheckinMessage").html("<p>A next of kin last name has been entered.  Please enter a next of kin first name.</p>");
 	    $("#manualCheckinError").show("highlight", 750);
		return false;
    }
	
	var curText = $("#manualCheckinStation option:selected").text();
	if (curText.indexOf('Other') > -1) {
		$("#otherStationDialog").dialog("open");
    } else {
    	checkDoctor();
    }
}

function checkDoctor() {
	var curText = $("#manualCheckinDoctor option:selected").text();
	if (curText.indexOf("Other") > -1) {
	  	$("#otherDoctorDialog").dialog("open");
    } else {
    	checkNewPatient();
    }
}

function checkNewPatient() {
	var new_p = $("#manualCheckinNewPatient").val();
    if(new_p === "true")
    {
    	$("#newPatientText").html("Are you sure you want to add " + $("#manualCheckinFirstName").val() + " " + $("#manualCheckinLastName").val() + " as a new patient?");
    	$("#newPatientDialog").dialog("open");
    } else {
    	submitManualCheckin();
    }
}

function submitManualCheckin() {
	var submitForm = $("#manualCheckinForm"); 
	var manualCheckinData = submitForm.serialize();
    $.ajax({
    	beforeSend: function(){
    		$("#manualCheckinAddCheckinButton").button("disable");
    		$("#manualCheckinCheckinButton").button("disable");
    		$("#manualCheckin").hide();
    		$("#savingContainer").html('<img src="' + ctx + '/moduleResources/chica/images/ajax-loader.gif"/>Checking in patient ' + $("#manualCheckinFirstName").val() + ' ' + $("#manualCheckinLastName").val() + '...');
    		$("#manualCheckinSaving").show();
        },
        complete: function(){
    	    $("#manualCheckinSaving").hide();
    	    $("#manualCheckinAddCheckinButton").button("enable");
    	    $("#manualCheckinCheckinButton").button("enable");
        },
        "cache": false,
        "data": "action=saveManualCheckin&" + manualCheckinData,
        "type": "POST",
        "url": submitForm.attr("action"),
        "timeout": 60000, // optional if you want to handle timeouts (which you should)
        "error": handleManualCheckinError, // this sets up jQuery to give me errors
        "success": function (xml) {
        	parseManualCheckinResult(xml);
        }
    });
}

function handleManualCheckinError(xhr, textStatus, error) {
	var error = "An error occurred on the server.";
    if (textStatus === "timeout") {
        error = "The server took too long to check in the patient.";
    }
    
    $("#manualCheckinMessage").html("<p>" + error + "  Please try again.</p>");
    $("#manualCheckinError").show("highlight", 750);
    $("#manualCheckin").show();
}

function parseManualCheckinResult(responseXML) {
	var result = $(responseXML).find("result").text();
	if ("success" === result) {
		$("#manualCheckinCompleteMessage").html($("#manualCheckinFirstName").val() + ' ' + $("#manualCheckinLastName").val() + ' successfully checked in.');
    	$("#manualCheckinComplete").show();
    	$("#manualCheckinDialog").dialog("option", "buttons", []);
    	setTimeout("closeManualCheckinDialog()", 5000);
	} else {
		$("#manualCheckinMessage").html('Failed to check in ' + $("#manualCheckinFirstName").val() + ' ' + $("#manualCheckinLastName").val() + '.  Please close this window and try again.');
    	$("#manualCheckinError").show();
    	$("#manualCheckinDialog").dialog("option", "buttons", []);
	}
}

function closeManualCheckinDialog() {
	$("#manualCheckinDialog").dialog("close")
}

function sendPage() {
	$("#pagerError").hide();
	var name = $("#pagerName").val();
	if (name.trim().length == 0) {
		$("#pagerErrorMessage").html("Please specify your name.");
		$("#pagerError").show("highlight", 750);
		return;
	}
	
	var url = ctx + "/moduleServlet/chica/chica";
	var reporter = $("#pagerName").val();
	var message = $("#pagerDescription").val();
	$.ajax({
		beforeSend: function(){
			//$(".ui-dialog-buttonpane button:contains('Send')").button("disable");
			$("#pagerSendButton").button("disable");
			$("#pagerError").hide();
			$("#pagerSaving").show();
	    },
	    complete: function(){
	     $("#pagerSaving").hide();
	     //$(".ui-dialog-buttonpane button:contains('Send')").button("enable");
	     $("#pagerSendButton").button("enable");
	    },
	    "cache": false,
	    "dataType": "xml",
	    "data": "action=sendPageRequest&reporter=" + encodeURIComponent(reporter) + "&message=" + encodeURIComponent(message),
	    "type": "POST",
	    "url": url,
	    "timeout": 60000, // optional if you want to handle timeouts (which you should)
	    "error": handleSendPageAjaxError, // this sets up jQuery to give me errors
	    "success": function (xml) {
	        parsePagerResult(xml);
	    }
	});
}

function handleSendPageAjaxError(xhr, textStatus, error) {
    var error = "An error occurred on the server.";
    if (textStatus === "timeout") {
        error = "The server took too long to send the page request.";
    }
    
    $("#pagerErrorMessage").html("<p>" + error + "  Click Send to try again.</p>");
    $("#pagerError").show("highlight", 750);
}

function parsePagerResult(responseXML) {
    // no matches returned
    if (responseXML === null) {
    	$("#pagerErrorMessage").html("<p><b>Error sending page request.  Click Send to try again.</b></p>");
        $("#pagerError").show("highlight", 750);
    } else {
    	var result = $(responseXML).find("result").text();
        if (result === "success") {
        	$("#pagerBody").hide();
        	$("#pagerComplete").show();
        	$("#pagerDialog").dialog("option", "buttons", []);
        	setTimeout("closePagerDialog()", 5000);
        } else {
        	$("#pagerErrorMessage").html("<b>" + $(responseXML).find("response").text() + "</b>");
            $("#pagerError").show("highlight", 750);
        }
    }
}

function closePagerDialog() {
	$("#pagerDialog").dialog("close");
}
