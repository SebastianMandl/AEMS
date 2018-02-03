$(document).ready(function() {
    console.log("ready!");
   $(".edit-statistic").on("click", function() {
      let id = $(this).attr("data-id");
      let pre = "#edit-statistic-form\\:";
      console.log("clicked: " + id); 
      $.post("/webserver/Edit", {'type': 's', 'id': id}, function(data) {
          $(pre + "_id").val(id);
          $(pre + "name").val(data.name);
          $("#edit-period").val(data.period);
          $(pre + "annotation").val(data.annotation);
      });
      
   });
   
   $("#edit-statistic-form\\:edit").on("click", function() {
       $("#edit-statistic-form\\:_editPeriod").val($("#edit-period").val());
   });
});