$(document).ready(function() {
   $(".admin").on("click", function() {
       let name = $(this).find("h4").text();
      $("#selectedAdmin").text(name); 
   });
});

