$(function() {
	addClickListenersToReports();
	$( "#RadioButtons1" ).buttonset(); 
});

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