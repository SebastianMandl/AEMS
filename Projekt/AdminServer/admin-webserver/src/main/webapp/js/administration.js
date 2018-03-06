$(document).ready(function () {
    $(".admin").on("click", function () {
        let name = $(this).find("h4").text();
        $("#selectedAdmin").text(name);
        $("#deleteAdmin\\:_user").val(name);
    });

    $("#new-admin-form\\:username").on("change", function () {
        let username = $(this).val();
	
	// Replace with user query
	let qry = `{
	    users: meters(id: "${username}") {
		id
	    }
	}`;
        
	$.post("/admin-webserver/query", {q: qry}, function(data) {
	    if(data["users"][0] !== undefined) {
		$("#new-admin-form\\:add-admin").removeAttr("disabled");
		$("#new-admin-form\\:add-admin").removeClass("loginmodal-disabled");
	    }
	});
	
        if (username === "some") {
            
        }
    });

    $("#new-admin-form\\:username").on("keyup", function () {
        $("#new-admin-form\\:add-admin").attr("disabled", "yes");
        $("#new-admin-form\\:add-admin").addClass("loginmodal-disabled");
    });
});

