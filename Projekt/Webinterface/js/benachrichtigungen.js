 function functionNotifications(){
		  var measuredElectricity = 10;
		  var maxElectricity = 5;
		  var measuredGas=15;
		  var maxGas=4;
		  var measuredWater;
		  var maxWater;
		  var report =1;
		  

		  if(measuredElectricity>maxElectricity){
			  $.notify("Ihr Stromverbrauch ist höher als sonst!", "warn");
		  }
		  if(measuredGas>maxGas){
			  $.notify("Ihr Gasverbrauch ist höher als sonst!", "warn");
		  }
		  if(measuredWater>maxWater){
			 $.notify("Ihr Wasserverbrauch ist höher als sonst!", "warn");
			 }
		  if(report>0){
			  $.notify("Ein neuer Bericht steht zum Download bereit!", "info");
		  }
	  }  