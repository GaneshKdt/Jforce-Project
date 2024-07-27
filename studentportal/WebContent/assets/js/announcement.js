window.addEventListener('load', function() {
		loadCount();
		$(".notification-link").click(function(event){
			event.preventDefault();
			loadAnnouncements();
			$('#announcements').modal('show');
		});
	});

	function loadCount(){
	  var myElement = document.getElementById("myElement");
	  var userId = myElement.getAttribute("userId");
	  var data = {
				sapid: userId
				};
	  
	  $.ajax({
	  	url: '/announcement/m/getUnreadAnnouncementCount',
	    type: 'POST',
	    contentType: 'application/json',
	   	data: JSON.stringify(data),
	    success: function(response) {
	    	var count = response;
	    	var announcementCount = '<div class="notification-count">'+count+'</div>';
	        	$('#announcementCounter').html(announcementCount);
	       	},
		error : function(xhr, status, error) {
			var announcementCount = '<div class="notification-count">0</div>';
	       	$('#announcementCounter').html(announcementCount);
		}
	  });
	}

	function isReadStatusInHeader(id, userId) {
		var modal = document.getElementById("announcementModal");
		var data = {
			announcementId : id,
			userId : userId
		};
		
		var requestDataForUpdate = {
			sapid : userId
		};
		
		$.ajax({
				url : '/announcement/m/changeAnnouncementStatus',
				method : 'POST',
				contentType : 'application/json',
				data : JSON.stringify(data),
				success : function(response1) {
					
					//On update Reload the Announcements and Announcements Counter
					$.ajax({
							url : '/announcement/m/getUnreadAnnouncementCount',
							method : 'POST',
							data : JSON.stringify(requestDataForUpdate),
							contentType : 'application/json',
							success : function(response2) {
								var count = response2;
								var updatedCounter = '<div class="notification-count">'+ count + '</div>';
								$('#announcementCounter').html(updatedCounter);
							},
							error : function(xhr, status, error) {
								console.error("Error while changing the status" ,error);
							}
					});
				},

				error : function(xhr, status, error) {
					console.error("Error while updating  the status" ,error);
				}
			});
	}
	
	function loadAnnouncements(){
		  var myElement = document.getElementById("myElement");
		  var userId = myElement.getAttribute("userId");
		  var data = {
					sapid: userId
				  	};
	  		$.ajax({
	      		url: '/announcement/m/getAllActiveAnnouncementsByStudentMapping',
	      		type: 'POST',
	      		contentType: 'application/json',
	      		data: JSON.stringify(data),
	      		beforeSend: function() {
	       			$('#loaderHome').removeClass('d-none');
					$('#loaderHome').addClass('d-block');
	 	  		},
	      
	 	  		success: function(response) {
	 	  			var activeStatus = response.isActiveAnnouncement;
if(activeStatus){
	      	  		$('#loaderHome').addClass('d-none');
			  		$('#loaderHome').removeClass('d-block');
			  	  var myElement = document.getElementById("myElement");
				  var userId = myElement.getAttribute("userId");
	          		var announcements = response.announcements;
	          		var announcementCount = typeof response.announcementCounter === "undefined" ? 0 : response.announcementCounter;
	          		var optionsList = '<div class="notification-count">'+announcementCount+'</div>';
	         		$('#announcementCounter').html(optionsList);
	          
	         		var count = 0;
					try {
	        	  		var announcement='';
	  		  	  		var individualAnnouncement ='';
	              		for (let i = 0; i < announcements.length; i++) {
	              			singleannouncement = response.announcements[i];
		           			var createdDate = new Date(singleannouncement.createdDate);
							var options = { day: '2-digit', month: 'short', year: 'numeric' };
							var formattedDate = createdDate.toLocaleDateString('en-US', options);
	                		count++;   
	               			var breifAnnouncement = announcements[i].description;
		                  	
	               			function stripHtmlTags(breifAnnouncement) {
								return breifAnnouncement.replace(/<[^>]*>/g, '');
		                	}
	               			
	              			var announcementWithTags = announcements[i].description;
	              			//to remove Rich Text HTML tags from the description
	              			var announcementWithoutTags = stripHtmlTags(announcementWithTags);
	              			var announcemntBrief = "";        	
			              	if (announcementWithoutTags.length > 150) {
			              	  announcemntBrief = announcementWithoutTags.substring(0, 149) + "...";
			              	}else {
	              	  			announcemntBrief = announcementWithoutTags;
	              			}

							if(announcements[i].readStatus){

	                announcement 		+='<a class="bg-light" href="#" data-bs-toggle="modal" data-bs-dismiss="modal"'
	    								+'onClick="isReadStatusInHeader('+announcements[i].id+', '+userId+')"'
	    								+'id="isRead" data-bs-target="#announcementModalInheader'+announcements[i].id+'">';           								
	    			announcement		+='<div id="announcementId-'+announcements[i].id+'">'
	    								+'<p class="card-text text-dark fw-bolder">'+announcements[i].subject+'</p>'
	    								+'</div>';
	    			announcement		+='<p class="card-text text-dark fw-bolder">'+announcemntBrief+'</p>';
	    		    announcement		+='<p class="card-text text-dark fw-bolder">'+announcements[i].startDate+''
	    								+'by <span class="card-text text-dark fw-bolder">'+announcements[i].category+'</span>'
	    								+'</p>'
	    								+'</a>';
	    			announcement   		+='<hr>';
	              						}

	              					    if(!announcements[i].readStatus){
	   				announcement		+='<span style="background-color: #f6f6f6;">'   
	      						 		+'<a class="bg-light" href="#" data-bs-toggle="modal" data-bs-dismiss="modal"'
	       								+'onClick="isReadStatusInHeader('+announcements[i].id+','+userId+')"'
	       								+'id="isRead" data-bs-target="#announcementModalInheader'+announcements[i].id+'">';   
	     		    announcement		+='<div id="announcementdetails">'
	      								+'<p class="card-text text-secondary">'+announcements[i].subject+'</p>'
	      								+'</div>';
	     		    announcement	    +='<p class="card-text  text-secondary">'+announcemntBrief+'</p>';
	   	    	    announcement		+='<p class="card-text  text-secondary">'+announcements[i].startDate+''
										+' by <span class="card-text text-secondary">'+announcements[i].category+'</span>'
										+'</p>'
										+'</a>'
										+'</span>';       				              		
	              	announcement		+='<hr>';
	              					    }
	              					    
	   			individualAnnouncement  +='<div class="modal fade" id="announcementModalInheader'+singleannouncement.id+'" tabindex="-1" aria-labelledby="seprateModel" aria-hidden="true">' 	
						   				+'<div class="modal-dialog">'
										+'<div class="modal-content">'
										+'<div class="modal-header">'
										+'<h1 class="modal-title fs-5" id="seprateModel">ANNOUNCEMENTS</h1>'
										+'<button type="button" class="btn-close" data-bs-dismiss="modal"aria-label="Close"></button>'
										+'</div>';
						
				individualAnnouncement  +='<div class="modal-body">'
										+'<h6>'+singleannouncement.subject+'</h6>'
										+'<p>'+singleannouncement.description+'</p>';	
					
										if(singleannouncement.attachment1 != null){
			    individualAnnouncement	+='<a target="_blank" href="https://announcementfiles.s3.ap-south-1.amazonaws.com/"/>'+singleannouncement.attachment1+singleannouncement.attachmentFile1Name+'</a><br/>';
										}
										if(singleannouncement.attachment2 != null){
				individualAnnouncement	+='<a target="_blank" href="https://announcementfiles.s3.ap-south-1.amazonaws.com/"/>'+singleannouncement.attachment2+singleannouncement.attachmentFile2Name+'</a><br/>';
										}
										if(singleannouncement.attachment3 != null){
			    individualAnnouncement	+='<a target="_blank" href="https://announcementfiles.s3.ap-south-1.amazonaws.com/"/>'+singleannouncement.attachment3+singleannouncement.attachmentFile3Name+'</a><br/>';
										}
																										
			    individualAnnouncement	+='<h4 class="small">' + formattedDate + '<span> by </span><a href="#">'+''+ singleannouncement.category + '</a></h4>';
		
				individualAnnouncement	+='</div>';
					
				individualAnnouncement	+='<div class="modal-footer">'
										+'<button data-bs-dismiss="modal" type="button" class="btn btn-default">DONE</button>'
										+'</div>'
										+'</div>'
										+'</div>'
										+'</div>';	

							}
					}catch (err) {
						console.error(err);
					}

					$('#announcementbody').html(announcement);
					$('#announcementInsideModal').html(individualAnnouncement);
					}
		else{
					$('#loaderHome').addClass('d-none');
			  		$('#loaderHome').removeClass('d-block');
					var msg = response.announcementMsg;
					var noActiveAnnouncement ='<div id="announcementbody"><p class="card-text text-dark fw-bolder text-center">'+msg+'</p></div>';
					$('#announcementbody').html(noActiveAnnouncement);
					}
				},
				error : function(xhr, status, error) {
					$('#loaderHome').addClass('d-none');
			  		$('#loaderHome').removeClass('d-block');
					var announcementError ='<div id="announcementbody"><p class="card-text text-dark fw-bolder text-center">Unable to load announcements!</p></div>';
					$('#announcementbody').html(announcementError);
					console.error('Unable to load announcements!', error);
				}
			});
		}