$(document).ready(function() {
   getNotifications(); 
});

function getNotifications() {
    var userId = 1;
    var graphQL = `{
        notifications(user: ${userId}) {
            name
            type
            meters {
                id
            }
        }
    }`;
    
    $.post(API_URL, {query: graphQL}, displayNotifications);
}

function displayNotifications(data) {
    for(var noti of data) {
        // Display
    }
}

var notifications = 0;
var measuredElectricity = 10;
var maxElectricity = 5;
var measuredGas = 15;
var maxGas = 4;
var measuredWater;
var maxWater;
var report = 1;

function functionNotifications() {
    if (measuredElectricity > maxElectricity) {
        $.notify("Ihr Stromverbrauch ist höher als sonst!", "warn");
        notifications--;
    }
    if (measuredGas > maxGas) {
        $.notify("Ihr Gasverbrauch ist höher als sonst!", "warn");
        notifications--;
    }
    if (measuredWater > maxWater) {
        $.notify("Ihr Wasserverbrauch ist höher als sonst!", "warn");
        notifications--;
    }
    if (report > 0) {
        $.notify("Ein neuer Bericht steht zum Download bereit!", "info");
        notifications--;
    }

    if (notifications < 0) {
        $('#notificationCounter').text('0');
    } else {
        $('#notificationCounter').text('' + notifications);
    }

}



window.onload = function () {

    if (measuredElectricity > maxElectricity) {
        notifications++;
    }
    if (measuredGas > maxGas) {
        notifications++;
    }
    if (measuredWater > maxWater) {
        notifications++;
    }
    if (report > 0) {
        notifications++;
    }

    $('#notificationCounter').text('' + notifications);

};
