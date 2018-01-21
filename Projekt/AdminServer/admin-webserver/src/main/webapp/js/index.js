$(document).ready(function () {
    $("#search").hide();
    $(".user-entry").on("click", function (event) {
        let text = $(this).find("h4").text();
        let email = $(this).attr("data-mail");
        $("#selectedUser").text(text);
        $("#selectedEnquiry").text(text);
        $("#selectedEnquiryEmail").text(email);
        $("#enquiry-form\\:_email").val(email);
        
        $("#deny-div").hide(); 
    });
    
    $("#deny-enquiry").on("click", function() {
       $("#deny-div").show(); 
    });
    
    $("#search-btn").on("click", function() {
       let search = $("#search");
       search.toggle();
       if(search.is(":visible")) {
           search.focus();
       }
    });
    
    $("#search").on("keyup", function() {
        let search = $(this).val().trim().toLowerCase();
        console.log("search: " + search);
        $("#user-list").children().each(function(index, element) {
           
           let username = $(element).find("h4").text().toLowerCase();
           let email = $(element).find("p").text().toLowerCase();
           
           if(username.indexOf(search) !== -1 || email.indexOf(search) !== -1) {
               $(element).show();
           } else {
               $(element).hide();
           }
            
        });
    });
    
    $("#enquiry-form\\:deny-confirm").on("click", function() {
        $("#enquiry-form\\:_denyMessage").val($("#deny-reason").val());
    });
});

