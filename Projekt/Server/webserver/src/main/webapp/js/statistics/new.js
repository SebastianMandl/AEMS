var _meterData = [];
$(document).ready(function() {
   $("#createStatistic").on("click", function() {
       $("#meterType").empty();
       let userId = $("#uId").val();
       let userData = {"userId": userId};
       $.post("http://localhost:8080/webserver/dummy/meters.json", userData, function(data) {
           _meterData = data;
           let types = [];
           let typeList = $("#meterType");
           for(let meter of data) {
               if(types.indexOf(meter.type) === -1) {
                   types.push(meter.type);
                   appendElement(typeList, meter.type);
               }
           }
       }, "json");
       
       
   });
   
   function appendElement(list, element) {
       let li = $("<li role='presentation'></li>");
       let div = $("<div class='checkbox'>");
       let labl = $("<label></label>");
       let box = $("<input type='checkbox' value=''></input>");
       box.val(element);
       
       box.on("click", function() {
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
       for(let meter of _meterData) {
           if(meter.type === type) {
               let li = $("<li role='presentation'></li>");
               let div = $("<div class='checkbox'>");
               let labl = $("<label></label>");
               let box = $("<input type='checkbox' value=''></input>");
               
               labl.append(box);
               labl.append(meter.id);
               div.append(labl);
               li.append(div);
               $("#meters").append(li);
           }
       }
   }
});