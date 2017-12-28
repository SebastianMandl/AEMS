function populateStatisticLists(data) {
    var homepageDiv = $("#homepageList");
    var androidDiv = $("#androidList");
    var allDiv = $("#allList");
    
    $.get("http://localhost:8080/webserver/templates/statistic-entry.html", function(html) {
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