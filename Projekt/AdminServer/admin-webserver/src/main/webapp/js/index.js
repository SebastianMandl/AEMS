$(document).ready(function() {
   $(".user-entry").on("click", function() {
      let text = $(this).find("h4").text();
      $("#selectedUser").text(text);
      $("#selectedEnquiry").text(text);
   });
});