"use strict";

const BASE_AEMS_URL = "http://localhost:8080/webserver/";

function getUserMeters(userId, callback) {
    let userData = {"userId": userId};
    $.post(BASE_AEMS_URL + "dummy/meters.json", userData, callback, "json");
}

