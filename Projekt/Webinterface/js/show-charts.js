
var l = ["Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"];
var av = [];
var pv = [];
var t = [-12, 12, 15, 13, 17, 23, 28, 31, 24, 22, 14, 10];
randomValues();
showChart("statistic_1", l, av, pv, t);
showChart("statistic_2", l, av, pv, t);
showChart("statistic_3", l, av, pv, t);

function showChart(canvasId, labels, actualValues, previousValues, temperature) {

var highestTemperature = Math.max.apply(Math, temperature);
var lowestTemperature = Math.min.apply(Math, temperature);

var highestValueA = Math.max.apply(Math, actualValues);
var highestValueP = Math.max.apply(Math, previousValues);

var highestValueToDisplay = Math.max(highestValueA, highestValueP);

var ctx = document.getElementById(canvasId).getContext('2d');
var mixedChart = new Chart(ctx, {
  type: 'bar',
  data: {
    datasets: [
		{
			label: 'Temperatur',
			data: temperature,
			yAxisID: "secondary",
			fill: false,
			borderColor: "green",
			type: 'line'
        },
		{
          label: 'Aktuell',
          data: actualValues,
		  backgroundColor: "orange",
		  yAxisID: "primary"
        }, 
		{
			label: "Vorperiode",
			data: previousValues,
			backgroundColor: "red",
			yAxisID: "primary"
		}
		],
    labels: labels
  },
	options: {
    scales: {
      yAxes: [{
        id: 'primary',
        type: 'linear',
        position: 'left',
		ticks: {
          max: highestValueToDisplay,
          min: 0,
		  callback: function(value, index, values) {
                        return value + " kW";
                    }
        }
      }, {
        id: 'secondary',
        type: 'linear',
        position: 'right',
        ticks: {
          max: highestTemperature + 10,
          min: lowestTemperature  - 10,
		  callback: function(value, index, values) {
                        return value + " Celsius";
                    }
        }
      }]
    }
  }
});
}

function randomValues() {
for(var i = 0; i < l.length; i++) {
		av.push(Math.floor(Math.random() * 200) + 50);
		pv.push(Math.floor(Math.random() * 200) + 50);
}	
}