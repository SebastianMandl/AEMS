$(document).ready(() => {
	
  $('#addDate').on("click", function(){
          var date = $("#newDate").val();
          $("#dateList").append('<li id="'+date+'">'+date+'</li>')
  }); 
})