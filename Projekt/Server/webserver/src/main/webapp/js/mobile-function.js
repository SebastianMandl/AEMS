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