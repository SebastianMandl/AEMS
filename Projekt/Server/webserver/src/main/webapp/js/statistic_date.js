 $(document).ready(function(){
     var newInput = $("<input id='meters' type='date' class='form-control'>");
     $('#meters').remove();
        $('select#period').after(newInput);
 });
 
 $("#period").on("change", function()
 {
     if ($('#period').val() == '0') {
        var newInput = $("<input id='meters' type='date' class='form-control'>");
        $('#meters').remove();
        $('select#period').after(newInput);
    }
    if ($('#period').val() == '1') {
        var newInput = $("<input id='meters' type='week' class='form-control'>");
        $('#meters').remove();
        $('select#period').after(newInput);
    }
    if ($('#period').val() == '2') {
        var newInput = $("<input id='meters' type='month' class='form-control'>");
        $('#meters').remove();
        $('select#period').after(newInput);
    }
    if ($('#period').val() == '3') {
        var newInput = $("<input id='meters' type='number' min='2018' max='2099' class='form-control'>");
        $('#meters').remove();
        $('select#period').after(newInput);
    }
});