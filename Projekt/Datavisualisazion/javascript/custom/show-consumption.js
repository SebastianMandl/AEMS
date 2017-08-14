/*
	https://github.com/leighquince/Chart.js
*/

function showConsumption(intervals, valuesConsumption, valuesTemperature) {
	// Options
	var o = {
		high: 60,
		low: -30
	};
	var data = {
    labels: intervals,
    datasets: [
	 {
        label: "My First dataset",
		yAxesGroup: "1",
        //new option, type will default to bar as that what is used to create the scale
        type: "bar",
        fillColor: "rgba(220,20,220,0.2)",
        strokeColor: "rgba(220,20,220,1)",
        pointColor: "rgba(220,20,220,1)",
        pointStrokeColor: "#fff",
        pointHighlightFill: "#fff",
        pointHighlightStroke: "rgba(220,220,220,1)",
        data: valuesConsumption
    },
	{
        label: "Temperatur",
		yAxesGroup: "2",
        //new option, type will default to bar as that what is used to create the scale
        type: "line",
        fillColor: "rgba(255, 177, 53, 0.5)",
        strokeColor: "rgba(255, 177, 53, 1)",
        pointColor: "orange",
        pointStrokeColor: "#fff",
        pointHighlightFill: "red",
        pointHighlightStroke: "rgba(220,220,220,1)",
        data: valuesTemperature
    }],
	yAxes: [{
		 name: "1",
		 scalePositionLeft: false, //setting to false will dispaly this on the right side of the graph
		 scaleFontColor: "rgba(151,137,200,0.8)"
	 }, {
		 name: "2",
		 scalePositionLeft: true,
		 scaleFontColor: "rgba(151,187,205,0.8)"
	 }]
};

var canvas = document.getElementById("canvas").getContext("2d");
	
	window.myChart = new Chart(canvas).Overlay(data, {
		populateSparseData: true,
		overlayBars: false,
		datasetFill: true,
	}); 

}
