$(document).ready(function () {
    $(".user-entry").on("click", function (event) {
        let text = $(this).find("h4").text();
        $("#selectedUser").text(text);
        $("#selectedEnquiry").text(text);
        
        $("#deny-div").hide(); 
    });
    
    $("#deny-enquiry").on("click", function() {
       $("#deny-div").show(); 
    });
});