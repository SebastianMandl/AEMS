$(document).ready(function() {
   $("#categories input").on("click", function() {

        let dis = $(this);
        if(!dis.prop("checked")) {
            dis.prop("checked", true);
        } 
        $("#categories input").each(function(index, element) {
            if($(this).val() !== dis.val()) {
                $(this).prop("checked", false);
            }
        });
        
        let type = dis.val();
        $("#statistics input").each(function(index, element) {
           if($(this).attr("data-type") === type) {
               $(this).parent().show();
           } else {
               $(this).parent().hide();
               $(this).prop("checked", false);
           }
        });
    });
    $("#categories input").eq(0).click();  
});

$(function() {
	$( "#RadioButtonsVorperiode" ).buttonset(); 
});

$(function() {
	$( "#RadioButtonsAutomaticGenerating" ).buttonset(); 
});

function setVars() {
    document.getElementById("newReport:_period").value = $("#period").find(":selected").val();
    document.getElementById("newReport:_reportStatistics").value = getStatistics();
    document.getElementById("newReport:_autoGenerate").value = $("#radioAutomaticGenerateYes").is(":checked");
}

function getStatistics() {
    var str = "";
    $("#statistics input").each(function(index, ele) {
        if($(this).prop("checked") === true) {
            str += $(this).val();
            str += ";";
        }
    });
    return str;
}


function editReport(reportName) {
	var modal = $("#newReport-modal");
	modal.modal('show');
	$('#newReportName').attr("value", reportName);
}

function addClickListenersToReports() {
	var reportList = $("#reportList");
	for(var i = 0; i < reportList.children().length; i++) {
		var report = reportList.children().eq(i);
		report.click(function() {
			editReport($(this).attr("rname"));
		});
	}
}
