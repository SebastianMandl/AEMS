var pdf_xaxis_indent = 10;

$(".download-pdf").click(function () {
    var statistic = $(this).parent().find("canvas").get(0);
    var title = $(this).parent().find(".stat-title").eq(0).text();

    // Scroll to top so statistic is rendered
    $('html, body').animate({
        scrollTop: $("#h_" + title).offset().top
    }, 0);

    var currentDate = formatDate(new Date());
    var dateStr = "Statistik wurde erstellt am: " + currentDate;

    html2canvas($(statistic), {
        onrendered: function (canvas) {
            var imageData = canvas.toDataURL('image/png');
            var pdf = new jsPDF('l', 'mm');

            pdf.setFontSize(25);
            pdf.text(title, pdf_xaxis_indent, 35);
            pdf.setFontSize(15);
            pdf.text(dateStr, pdf_xaxis_indent, 40);

            // Add graph image
            pdf.addImage(imageData, 'PNG', pdf_xaxis_indent, 50);

            // Add Logo @ Top Left
            imageToBase64("images/logo_schrift.png", function (base64) {
                var idx = base64.indexOf(',');
                var imgData = "data:image/png;base64" + base64.substring(idx);
                pdf.addImage(imgData, 'PNG', pdf_xaxis_indent - 5, 0);
                pdf.save(title + ".pdf");
            });

        }
    });
});

function formatDate(date) {
    var day = date.getDate();
    var month = date.getMonth() + 1; // Month is zero based
    var fullYear = date.getFullYear();

    if (day < 10) {
        day = "0" + day;
    }
    if (month < 10) {
        month = "0" + month;
    }
    var dateStr = day + "." + month + "." + fullYear;
    return dateStr;
}

function imageToBase64(url, callback) {
    var xhr = new XMLHttpRequest();
    xhr.onload = function () {
        var reader = new FileReader();
        reader.onloadend = function () {
            callback(reader.result);
        }
        reader.readAsDataURL(xhr.response);
    };
    xhr.open('GET', url);
    xhr.responseType = 'blob';
    xhr.send();
}