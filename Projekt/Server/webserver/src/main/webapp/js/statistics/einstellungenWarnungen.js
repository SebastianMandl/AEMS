$(document).ready(function () {

//function addDateToList(){
//    var date = $('#newDate').val();
//    $('#dateList').add('li').val(date);
//}

    $('#addDate').on('click', addListItem);
    
    $('#meterList input').on('click', function() {
	if($(this).prop("checked")) {
	    $("#meterList input").each(function(index, element){
		$(element).prop("checked", false); 
	    });
	    $(this).prop("checked", true); 
	}
    }); 
    
    $("#warningType").change(function() {
	let typeText = $("#warningType option:selected").text();
	$("#newWarningForm\\:submitWarning").val(typeText + " erstellen");
    });

    function addListItem() {
	var date = $('#newDate').val();
	$('#dateList').add('li').val(date);
    }
    
    function uncheckOtherCheckboxes() {
	
    }

});
