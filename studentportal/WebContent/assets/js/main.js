// JavaScript Document

$(document).on('ready', function() {
	console.log('Doc Ready');	
	
	$('#toggle-nav').on('click', function() {
		$('.sz-main-content').toggleClass('menu-closed');	
	});
	
	/* Mobile Toggle Nav */
	$('#mobileToggleNav').on('click', function() {
		$('.sz-main-content').toggleClass('menu-opened');
	});
	
	
    //$('.sz-main-content').toggleClass('menu-closed');	

	var newYear = new Date(); 
	newYear = new Date(newYear.getFullYear() + 1, 1 - 1, 1); 
	$('.exam-assg-timer').countdown({until: new Date(2016, 7-1, 10), format: 'dHM'});
	$('.exam-assg-timer').countdown('toggle');

	
	/* Mobile Toggle Notification */
	$('#mobileNotification,.hide-link').on('click', function() {
		$('.sz-main-content').toggleClass('notification-opened');
	});
	
	$('#show-more-resources').on('click', function() {
		$('.show-extra-resources').toggle();
	});
	
	
		
		//var moment = $('#calendar').fullCalendar('getDate');
		//var month = moment.format('MMM YYYY');
		//$('#month').html(month);
		
		
		//$('#calendar').fullCalendar( 'changeView', 'agendaDay' );

		

    $('#new-announcements').on('show.bs.modal', function () {
	    $(".notification-link").addClass("active-modal");
	});
	$('#new-announcements').on('hide.bs.modal', function () {
	    $(".notification-link").removeClass("active-modal");
	}); 
	
	
	var lastScrollTop = 0;
	$(window).scroll(function(event){
	   var st = $(window).scrollTop();
	   		
	   
	   if (st > lastScrollTop){
		   if ($(window).scrollTop() > 80){ 
		   	$(".courses-menu-wrapper").addClass('fix-course');
		   }
	   } else {
		   if ($(window).scrollTop() < 80){ 
		   	 $(".courses-menu-wrapper").removeClass('fix-course');
		   }
		 
	   }
	   lastScrollTop = st;
	});
	
	$('.load-more-table a').on('click', function() {
		$(this).parent().prev('table').addClass('showAllEntries');
		$(this).hide();
	});
	
	
	$('.calendar-container').hide();
	$('#switch-view-list').on('click', function() {
		$('.list-type-switcher li').removeClass('active');
		$(this).parent().addClass('active');
		$('.list-container').show();
		$('.calendar-container').hide();
	});
	$('#switch-view-calendar').on('click', function() {
		$('.list-type-switcher li').removeClass('active');
		$(this).parent().addClass('active');
		$('.list-container').hide();
		$('.calendar-container').show();
	});
	
	$('.has-sub-menu>a').click(function(e) {
        if($(window).width()<1200){
            e.preventDefault();
            if($(this).closest('.has-sub-menu').hasClass('active')){
                $(this).closest('.has-sub-menu').removeClass('active');
            }else{
                $('.has-sub-menu').removeClass('active');
                $(this).closest('.has-sub-menu').addClass('active');
            }
        }
    });

	
});

function tableToggle(className,supportName) {
		var city = $('select#'+supportName).val();
		$('table.'+className+' tr').each(function() {
			var tdVal = $(this).find('td').html();
			if(tdVal == city) {
				$(this).show();
			} else {
				$(this).hide();
			}
		})
		
	}
	
	$('select#supportCity').on('change',function() {
    	var city = $(this).val();
		$('select#supportCity').find('table tr').each(function(index) {
			var tdVal = $(this).find('td').html();
			console.log(tdVal);
			if(tdVal == city) {
				alert(city);
			}
		});
    });



$(function() {
    // Clear event
    $('.image-preview-clear').click(function(){
        $('.image-preview-filename').val("");
        $('.image-preview-clear').hide();
        $('.image-preview-input input:file').val("");
        $(".image-preview-input-title").text("Browse"); 
    }); 
    // Create the preview image
    $(".image-preview-input input:file").change(function (){     
        var img = $('<img/>', {
            id: 'dynamic',
            width:250,
            height:200
        });      
        var file = this.files[0];
        var reader = new FileReader();
        // Set preview image into the popover data-content
        reader.onload = function (e) {
            $(".image-preview-input-title").text("Change");
            $(".image-preview-clear").show();
            $(".image-preview-filename").val(file.name);            
            img.attr('src', e.target.result);
        };        
        reader.readAsDataURL(file);
    });  
});