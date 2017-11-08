const BASE_URL = "http://localhost:8080/webserver/";
$(document).ready(function() {
     $.post("http://localhost:8080/webserver/dummy/statistics.json", populateStatisticLists, "json");
});

function populateStatisticLists(data) {
    var homepageDiv = $("#homepageList");
    var androidDiv = $("#androidList");
    var allDiv = $("#allList");
    
    $.get(BASE_URL + "/templates/statistic-entry.html", function(html) {
        for(var item of data) {
            var template = $(html);
            template.find(".list-group-item-heading").text(item.statistic_name);
            template.find(".list-group-item-text").text(item.annotation);
            
            allDiv.append(template);
            
            if(item.display_home) {
                homepageDiv.append(template.clone());
            }
            if(item.display_android) {
                androidDiv.append(template.clone());
            } 
        }
    });
}

function createEntry(data) {
    
}