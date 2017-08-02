$(function() {
	$( "#RadioButtonsVorperiode" ).buttonset(); 
});

$(function() {
	$( "#RadioButtonsAutomaticGenerating" ).buttonset(); 
});

function setConfiguredModalText(name) {
	$("#configuredReportName").text(name);
}

function openEditReportModal() {
	editReport($("#configuredReportName").text());
}
function editReport(reportName) {
	var modal = $("#newReport-modal");
	modal.modal('show');
	$('#newReportName').attr("value", reportName);
}
