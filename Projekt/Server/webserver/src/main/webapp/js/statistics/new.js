var _meterData = [];
$(document).ready(function () {

    const STATISTIC_NAME_LIMIT = 64;
    const STATISTIC_ANNOTATION_LIMIT = 255;

    $("#meterType input").on("click", function() {
        let dis = $(this);
        if(!dis.prop("checked")) {
            dis.prop("checked", true);
        } 
        $("#meterType input").each(function(index, element) {
            if($(this).val() !== dis.val()) {
                $(this).prop("checked", false);
            }
        });
        
        let type = dis.val();
        $("#meters input").each(function(index, element) {
           if($(this).attr("data-type") === type) {
               $(this).parent().show();
           } else {
               $(this).parent().hide();
               $(this).prop("checked", false);
           }
        });
    });
    $("#meterType input").eq(0).click(); 

    $("#submitNewStatistic").on("click", function () {

        var name = $("#statisticName").val();
        var annotation = $("#statisticAnnotation").val();
        var userId = $("#uId").val();
        var boxes = $("#meters label input:checked");
        var meterIds = [];
        boxes.each(function (index, obj) {
            meterIds.push(this.value);
        });

        var data = {
            name: name,
            annotation: annotation,
            meters: meterIds,
            user_id: userId
        };

        if (isDataValid(data)) {
            console.log(data);
        } else {
            $.notify("Bitte überprüfen Sie Ihre Eingaben!", "warn");
        }


    });

    function isDataValid(data) {
        if (data.name.length > STATISTIC_NAME_LIMIT || data.name.length < 1)
            return false;
        if (data.annotation.length > STATISTIC_ANNOTATION_LIMIT)
            return false;
        if (data.meters.length === 0)
            return false;

        return true;
    }

    function addMeterTypes(data) {
        /*
        _meterData = data;
        let types = [];
        let typeList = $("#meterType");
        for (let meter of data) {
            if (types.indexOf(meter.type) === -1) {
                types.push(meter.type);
                appendElement(typeList, meter.type);
            }
        } */
    }


    function appendElement(list, element) {
        let li = $("<li role='presentation'></li>");
        let div = $("<div class='checkbox'>");
        let labl = $("<label></label>");
        let box = $("<input type='checkbox'></input>");
        box.val(element);

        box.on("click", function () {
            $("#meterType input[type=checkbox]").prop("checked", false);
            box.prop("checked", true);
            $("#meters").empty();
            addMeters(box.val());
        });

        labl.append(box);
        labl.append(element);
        div.append(labl);
        li.append(div);
        list.append(li);
        // <label><input type="checkbox" value="" />Strom</label>
    }

    function addMeters(type) {
        /*
        for (let meter of _meterData) {
            if (meter.type === type) {
                let li = $("<li role='presentation'></li>");
                let div = $("<div class='checkbox'>");
                let labl = $("<label></label>");
                let box = $("<input type='checkbox'></input>");
                box.val(meter.id);
                labl.append(box);
                labl.append(meter.id);
                div.append(labl);
                li.append(div);
                $("#meters").append(li);
            }
        } */
    }
});

    function setVars() {
        document.getElementById("newStatistic:_meters").value = getMeters();
        document.getElementById("newStatistic:_period").value = $("#period").find(":selected").val();
    }
    
    function getMeters() {
        var str = "";
        $("#meters input:checked").each(function(index, element) {
            str += $(element).val();
            str += ";";
        });
        return str;
    }