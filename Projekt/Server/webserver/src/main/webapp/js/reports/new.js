$(document).ready(function() {
   addListener();
});

function addListener() {
    $("#newReport").on("click", function() {
        var q = `{
            statistic_meters {
                statistic {
                    id
                    name
                }
                meter {
                    id
                    metertype {
                        id
                        display_name
                    }
                }
            }
        }`;
        
        $.post(API_URL, {}, addElements);
    });
}

function addElements(data) {
    
}