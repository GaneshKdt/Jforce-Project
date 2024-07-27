$(document).ready(function(e) {
    $('ul.headerLinks li').mouseenter(function(){
		if($(this).children('a').next('ul').hasClass('subMenu') && $('.navbar-toggle').css('display')=='none'){			
			$(this).children('a').next('ul').clearQueue();
			$(this).children('a').next('ul').stop();
			$(this).children('a').next('ul').slideDown();
		}
	});
	
	$('ul.headerLinks li').mouseleave(function(){
		if($(this).children('a').next('ul').hasClass('subMenu') && $('.navbar-toggle').css('display')=='none'){
			$(this).children('a').next('ul').clearQueue();
			$(this).children('a').next('ul').stop();
			$(this).children('a').next('ul').slideUp();
		}
	});
	
	$('ul.headerLinks li').click(function(){
		if($('.navbar-toggle').css('display')=='block'){
			if($(this).children('a').next('ul').hasClass('subMenu')){
				$(this).children('a').next('ul').clearQueue();
				$(this).children('a').next('ul').stop();
				$(this).children('a').next('ul').slideToggle();
				//return false; //After commenting mobile links are working
			}
		}
	});	
	
	/*Date Icker*/
	$('.input-daterange').datepicker();
	
	/*add upload*/
	$('.uploadWrapper').find('.addBtn').off('click');
	$('.uploadWrapper').find('.addBtn').on('click',function(){
		addUpload($(this));
	});
	
	var currentElem=1;
	
	function addUpload(trgtElem){
		if(trgtElem.find('i.fa').hasClass('fa-plus')){
			trgtElem.closest('.uploadWrapper').after(trgtElem.closest('.uploadWrapper').get(0).outerHTML);
			
			trgtElem.find('i.fa').removeClass('fa-plus');
			trgtElem.find('i.fa').addClass('fa-minus');
			
			trgtElem.closest('.uploadWrapper').next('.uploadWrapper').find('.customFileWrapper .file-input ').remove();
			trgtElem.closest('.uploadWrapper').next('.uploadWrapper').find('.customFileWrapper').append('<input id="file1" type="file" class="file" data-show-preview="false"  data-show-upload="false" data-show-remove="false">');
			
			$('.uploadWrapper').find('.addBtn').off('click');
			$('.uploadWrapper').find('.addBtn').on('click',function(){
				addUpload($(this));
			});
			
			$('.input-daterange').datepicker();
			trgtElem.closest('.uploadWrapper').next('.uploadWrapper').find(".file").fileinput({showUpload:true});
		}else{
			trgtElem.closest('.uploadWrapper').remove();
		}
	};
});
