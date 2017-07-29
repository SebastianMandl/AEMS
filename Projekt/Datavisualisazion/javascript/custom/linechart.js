var data = {
	labels: ['Montag', 'Dienstag', 'Mittwoch', 'Donnerstag', 'Freitag', 'Samstag', 'Sonntag'],
	series: [
		[54, 21, 95, 91, 0, 0, 0, 0]
	]
};

var options = {
  high: 100,
  low: 0,
  axisX: {
	    position: 'start'
  },
  axisY: {
    offset: 100,
    labelInterpolationFnc: function(value) {
      return value + ' KW/h';
    }
  },

};

var responsiveOptions = [
  ['screen and (max-width: 800px)', {
  
	axisX: {
      labelInterpolationFnc: function(value) {
        return value.slice(0, 3);
      }
    }
  }
 ],
   ['screen and (max-width: 400px)', {
  
	axisX: {
      labelInterpolationFnc: function(value) {
        return value.slice(0, 1);
      }
    },
  }
 ]
];


var chart = new Chartist.Line('.chart-here', data, options, responsiveOptions);
chart.on("created", function () {
    $('.ct-chart-line .ct-point').click(function () {
        var val = $(this).attr("ct:value");
		var day = getWeekdayForLine($(this));
        alert("Wert am " + day + ": " + val + "\nHier kommt dann noch viel genauerer Shit hin und so!\nNatürlich nicht in einem Alert ;)");
    });
	$('.ct-chart-line .ct-point').hover(function () {
		$(this).css({"stroke":"red", "cursor":"pointer"});
    });
	$('.ct-chart-line .ct-point').mouseout(function () {
		$(this).css({"stroke":"#d70206"});
    });
	
});

function getWeekdayForLine(line) {
	var p = $(line).parent();
	var labels = $(".ct-labels");
	var index = 0;
	var lines = p.children();
	for(var i = 0; i < lines.length; i++) {
		if(line.attr("x1") == lines.eq(i).attr("x1")) {
			return labels.children().eq(i - 1).children().eq(0).text();
		}
	}
	return -1;
}
