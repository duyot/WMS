jQuery(document).ready(function($){if($('.iso-box-wrapper').length>0){var $container=$('.iso-box-wrapper'),$imgs=$('.iso-box img');$container.imagesLoaded(function(){$container.isotope({layoutMode:'fitRows',itemSelector:'.iso-box'});$imgs.on('load',function(){$container.isotope('layout');})});$('.filter-wrapper li a').click(function(){var $this=$(this),filterValue=$this.attr('data-filter');$container.isotope({filter:filterValue,animationOptions:{duration:750,easing:'linear',queue:false,}});if($this.hasClass('selected')){return false;}var filter_wrapper=$this.closest('.filter-wrapper');filter_wrapper.find('.selected').removeClass('selected');$this.addClass('selected');return false;});}});$(window).on('load',function(){$('.preloader').fadeOut(1000);});$(window).scroll(function(){if($(".navbar").offset().top>50){$(".navbar-fixed-top").addClass("top-nav-collapse");}else{$(".navbar-fixed-top").removeClass("top-nav-collapse");}});$(function(){wow=new WOW({mobile:false});wow.init();$('.navbar-collapse a').click(function(){$(".navbar-collapse").collapse('hide');});$('.iso-box-section a').nivoLightbox({effect:'fadeScale',});$(function(){jQuery(document).ready(function(){$('#home').backstretch(["home_page/images/home-bg-slideshow1.jpg","home_page/images/home-bg-slideshow2.jpg","home_page/images/contact-bg.jpg",],{duration:3000,fade:750});});})});