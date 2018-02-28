$(document).ready(function () {
    showStatistics();
});

function showStatistics() {
    var userId = 1;
    var q = ` {
        statistics(user: ${userId}, show_home: true) {
            name
            dateFrom
            dateTo
            period
            values
        }
    }`;
}

function showChart(canvasId, labels, actualValues, previousValues, anomalies) {
/*
    var highestTemperature = Math.max.apply(Math, temperature);
    var lowestTemperature = Math.min.apply(Math, temperature); */

    var highestValueA = Math.max.apply(Math, actualValues);
    var highestValueP = Math.max.apply(Math, previousValues);

    var highestValueToDisplay = Math.max(highestValueA, highestValueP);
    var valueOffset = highestValueToDisplay % 10 === 0 ? highestValueToDisplay : 10 - highestValueToDisplay % 10;

    var ctx = document.getElementById(canvasId).getContext('2d');

    var dataSets = getAnomalieDataset(anomalies);

    var av = {
        label: 'Verbrauch',
        data: actualValues,
        backgroundColor: "rgba(255, 202, 68, 1)",
        yAxisID: "primary"
    };
    var pv = {
        label: "Verbrauch (Vorperiode)",
        data: previousValues,
        backgroundColor: "rgba(255, 202, 68, 0.5)",
        yAxisID: "primary"
    };
    dataSets.push(av);
    if(previousValues !== null) {
	dataSets.push(pv);
    }
	
    var highestAnomaly = getPeekValue(anomalies, Math.max);
    var lowestAnomaly = getPeekValue(anomalies, Math.min);
    var averageAnomaly = getAvg(anomalies);

    var mixedChart = new Chart(ctx, {
        type: 'bar',
        data: {
            datasets: dataSets,
            labels: labels
        },
        options: {
            scales: {
                yAxes: [{
                        id: 'primary',
                        type: 'linear',
                        position: 'left',
                        ticks: {
                            max: highestValueToDisplay + valueOffset,
                            min: 0,
                            callback: function (value, index, values) {
                                return value + " kWh";
                            }
                        }
                    }, { 
                        id: 'secondary',
                        type: 'linear',
                        position: 'right',
                        ticks: {
                            max: highestAnomaly + averageAnomaly, //highestTemperature + 10,
                            min: lowestAnomaly - averageAnomaly,
                            callback: function (value, index, values) {
                                return value;
                            }
                        }
                    }

                ]
            }
        }
    });
}

function getPeekValue(anomalies, compareFunc) {
    var keys = Object.keys(anomalies);
    var array = [];
    for(var key of keys) {
        array.push(...anomalies[key]);
    }
    return compareFunc.apply(Math, array);
}

function getAvg(anomalies) {
    var keys = Object.keys(anomalies);
    var array = [];
    for(var key of keys) {
        array.push(...anomalies[key]);
    }
    var sum = 0;
    array.forEach(x => sum += x);
    return sum / array.length;
}

function getAnomalieDataset(anomalies) {
    // {"anomalie1": [1,2,3],

    var array = [];
    var keys = Object.keys(anomalies);
    var colours = ["green", "red", "purple"];
    var idx = 0;
    for (var key of keys) {
        var set = {
            label: key,
            data: anomalies[key],
            yAxisID: "secondary",
            fill: false,
            borderColor: colours[idx],
            type: 'line'
        };
        idx++;
        if(idx > 2)
            idx = 0;
        array.push(set);
    }
    return array;
}
