 function functionNotifications(){
		  var measuredElectricity = 10;
		  var maxElectricity = 5;
		  var measuredGas=15;
		  var maxGas=4;
		  var measuredWater;
		  var maxWater;
		  var report =1;
		  

		  if(measuredElectricity>maxElectricity){
			  alert("Ihr Stromverbrauch ist höher als sonst!", "warn");
		  }
		  if(measuredGas>maxGas){
			  alert("Ihr Gasverbrauch ist höher als sonst!", "warn");
		  }
		  if(measuredWater>maxWater){
			 alert("Ihr Wasserverbrauch ist höher als sonst!", "warn");
			 }
		  if(report>0){
			  alert("Ein neuer Bericht steht zum Download bereit!", "info")
		  }
	  }  