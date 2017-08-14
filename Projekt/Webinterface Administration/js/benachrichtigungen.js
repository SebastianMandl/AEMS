		  var newEnquiries = 10;

function functionNotifications(){
	 
	$.notify("Sie haben " + newEnquiries + " neue Anfragen", "info");
	newEnquiries = 0;
		if(newEnquiries<=0){
			$('#notificationCounter').text('0');
		}
		else{
			$('#notificationCounter').text(''+newEnquiries);
	    	}
  
	  }  



window.onload = function(){

	$('#notificationCounter').text('' + newEnquiries);
		
}
