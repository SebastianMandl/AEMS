$(document).ready(function() {
    $("#notifIcon").one("click", getNotifications);
});

function getNotifications() {
    var userId = 1;
    var graphQL = `{
        archived_meter_notifications(user_id: ${userId}, viewed: false) {
            id
            notification {
                name
                type
            }
        }
    }`;
    
    $.post(API_URL + "warnings.json", {query: graphQL}, displayNotifications, "json");
}

function displayNotifications(data) {
    for(var noti of data) {
        var type = noti.notification.type;
        
        var n = new Noty({
            text: noti.notification.name,
            type: type,
            callbacks: {
                onClose: function() {
                    alert(noti.id);
                }
            }
        });
        n.show();
        console.log(n);
    }
    $("#notificationCounter").text("0");
}
