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

function getVars() {
        document.getElementById("newWarningForm:_warningType").value = "BENACHRICHTIGUNG";
        document.getElementById("newWarningForm:_warningMeters").value = getWarningMeters();
        document.getElementById("newWarningForm:_warningDays").value = getWarningDays();
        document.getElementById("newWarningForm:_warningVariance").value = $("#variance").val();
        document.getElementById("newWarningForm:_warningDates").value = getWarningDates();
}

function getWarningMeters() {
    var str = "";
    $("#meterList input:checked").each(function(index, element) {
        str += $(element).val();
        str += ";";
    });
    return str;
}

function getWarningDays() {
    var str = "";
    $("#meterDays input:checked").each(function(index, element) {
        str += $(element).val();
        str += ";";
    });
    return str;
}

function getWarningDates() {
    var str = "";
    $("#dateList li").each(function(index, element) {
        str += $(element).attr("id");
        str += ";";
    });
    return str;
}



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
