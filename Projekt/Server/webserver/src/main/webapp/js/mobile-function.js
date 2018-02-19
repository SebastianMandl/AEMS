$(document).load($(window).bind("resize", startFormat));

function startFormat(){
	footerFormat();
	navbarFormat();
}

function footerFormat(){
if (/Mobi/.test(navigator.userAgent)) {
    $('footer').removeClass("footer");
	$('footer').removeClass("navbar-fixed-bottom");
}
}

function navbarFormat(){	
if(/Mobi/.test(navigator.userAgent)){
	$('.image-size').css('width', '100%');
}
}