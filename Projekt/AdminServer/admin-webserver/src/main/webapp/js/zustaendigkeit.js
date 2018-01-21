$(document).ready(function() { 
   $(".responsibility").on("click", function() {
       let responsibility = $(this).find("h4").text();
       let code = responsibility.substring(0, responsibility.indexOf(" "));
       
      $("#selectedResponsibility").text(responsibility); 
      $("#eod-resp\\:_plz").val(code);
   });
   
   $("#edit-responsibility").on("click", function() {
       let text = $("#selectedResponsibility").text();
       
       let code = text.substring(0, text.indexOf(" "));
       let name = text.substring(text.indexOf(" ") + 1, text.length);
       
       $("#edit-resp\\:edit-postalcode").val(code);
       $("#edit-resp\\:edit-placename").val(name);
   });
});