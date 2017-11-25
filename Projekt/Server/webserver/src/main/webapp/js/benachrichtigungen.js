var notifSelected = false;
$(document).ready(function () {
    $("#notifIcon").on("click", function() {
        if(notifSelected) {
            notifSelected = false;
            Noty.closeAll();
            return;
        }
        notifSelected = true;
       if(notifications === undefined) {
           getNotifications();
       } else {
           displayNotifications();
       }
    });
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

    $.post(API_URL + "warnings.json", {query: graphQL}, (result) => {notifications = result; displayNotifications(result)}, "json");
}

function displayNotifications(data) {
    if(notifications === undefined)
        notifications = data;
    for (var noti of notifications) {
        var type = noti.type;
        
        var n = new Noty({
            text: noti.name,
            type: type.toLowerCase(),
            theme: "relax",
            callbacks: {
                onClose: function () {
                    if(notifSelected === true)
                        alert(noti.notificationId);
                }
            }
        }); 
        n.show();

    }
    $("#notificationCounter").text("0");
}
