$(document).ready(function() { 
   $(".responsibility").on("click", function() {
       let responsibility = $(this).find("h4").text();
      $("#selectedResponsibility").text(responsibility); 
   });
   
   $("#edit-responsibility").on("click", function() {
       let text = $("#selectedResponsibility").text();
       
       let code = text.substring(0, text.indexOf(" "));
       let name = text.substring(text.indexOf(" ") + 1, text.length);
       
       $("#edit-postalcode").val(code);
       $("#edit-placename").val(name);
   });
});