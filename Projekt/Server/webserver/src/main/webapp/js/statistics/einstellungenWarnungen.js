$(document).ready(function(){
    
//function addDateToList(){
//    var date = $('#newDate').val();
//    $('#dateList').add('li').val(date);
//}

$('#addDate').on('click', addListItem);

function addListItem(){
    var date = $('#newDate').val();
    $('#dateList').add('li').val(date);
}

});
