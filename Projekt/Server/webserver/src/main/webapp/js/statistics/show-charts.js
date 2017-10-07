
var l = ["Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"];
var av = [];
var pv = [];
var t = [1,2,3,4,5,6,7,8,9,10,11,12];
randomValues();
showChart("statistic_1", l, av, pv, t);
showChart("statistic_2", ["KW 08", "KW 09", "KW 10", "KW 11"], [150,120,120, 100], [150,130,130,90], t);
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
		  backgroundColor: "rgba(255, 202, 68, 1)",
		  yAxisID: "primary"
        }, 
		{
			label: "Vorperiode",
			data: previousValues,
			backgroundColor: "rgba(255, 202, 68, 0.5)",
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
/* Checks if the array parameter is null or empty. If not, the values will be added to the cart */
function addValuesIfNeeded(chart, array, label, color, axisId, chartType) {
	if(chartType == null) {
		chartType = "bar";
	}
	if(array != null && array != undefined && array.length > 0) {
	var vals = {
		  label: label,
          data: array,
		  backgroundColor: color,
		  yAxisID: axisId,
		  type: chartType
		};
	chart.data.datasets.push(vals);
	}
	return chart;
}

function randomValues() {
for(var i = 0; i < l.length; i++) {
		av.push(Math.floor(Math.random() * 200) + 50);
		pv.push(Math.floor(Math.random() * 200) + 50);
}	
}