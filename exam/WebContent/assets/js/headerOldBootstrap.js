		


$(document).ready(function() {
var value = $("#checkTextToSpeech").val();
if(value==='1'){
window.speechSynthesis.cancel();
var utterance = new SpeechSynthesisUtterance();	
			//selected area text will be spoken 
	 	$(document).mouseup(function() {
		  if (window.getSelection) {
		    var selectedText = window.getSelection().toString();
		    utterance.text = selectedText;
		    window.speechSynthesis.speak(utterance);
		  } 
		});

		$(document).mousedown(function() {
		  window.speechSynthesis.cancel();
		});

		//On FocusIn words will be Spoken
	  	   document.addEventListener('focusin', function(event) {			  	    
	  		 if (!event.target.classList.contains('modal') && !event.target.classList.contains('studentAnnouncementCounter') && !event.target.classList.contains('globalSearch')) {
	                utterance.text = document.activeElement.textContent;
	                window.speechSynthesis.speak(utterance);
	              }    
	  	  }); 

		//On FocusOut previous speaking words cancel
	  	  document.addEventListener('focusout', function(event) {
	  	    window.speechSynthesis.cancel();
	  	  });
		}	 

		  	$('#globalSearch').keyup(function(){
		  		var sbjectCodeIds=$("#subjectCodeIds").val();
		  		var subjectIdsArray = JSON.parse(sbjectCodeIds);
				var searchBoxVal=$(this).val() 
			    var data = {
			    	'keyword': $(this).val(),
			    	subjectCodeIdList : subjectIdsArray
			    };
	    		if(($(this).val() || !$(this).val()=="") && searchBoxVal.length>=3){

	    			   $.ajax({
	    				   	url: "/searchapp/getSuggestions",
	    			    	type: "POST",
	    			    	contentType: "application/json",
	    			    	data: JSON.stringify(data),
	    			    	success: function(keywordSeparatedLists) {
	    							 var optionalList = "";
	    							 optionalList+='<ul class="list-group">';
	    					    	 // Iterate over the keywordSeparatedLists map
	    					    	 var count=0;
	    					    	    for (var key in keywordSeparatedLists) {
	    					    	      if (keywordSeparatedLists.hasOwnProperty(key)) {
	    					    	        var responseList = keywordSeparatedLists[key];

	    					    	        //resources
	    					    	        if ((key==="pdf") && keywordSeparatedLists[key].length>0){
	    					    	        	count++;
	    					    	        	var staticValue = 'resource';
	    					    	        	if(responseList.length>3){
	    					    	        		optionalList+='<div class="row mt-1">'
	    					    	        						+'<div class="col-lg-8 col-md-8 col-8">'
	    					    	        							+'<span><i class="fa-solid fa-clone fa-xl"></i></span><span class="fs-6">Resources</span>'
	    					    	        						+'</div>'
	    					    	        						+'<div class="col-lg-4 col-md-4 col-4" style="text-align: end;">'
	    					    	        							+'<a class="seeAll" href="#" onclick="seeAll(\'' + staticValue + '\')"><small >See All</small></a>'
	    					    	        						+'</div>'
	    					    	        						+'</div>';
	   					    	        						
					    	        						 for (var i = 0; i <3; i++) {
					    						    	          var responseBean = responseList[i];
					    						    	          // Perform further actions with the response bean
					    						    	        
					    						    	          optionalList += '<li class="list-group-item text-wrap" style="width:100%;" id="' + responseBean.id + '"  onClick="openPdfPage(\'' + responseBean.previewPath + '\', \'' + responseBean.name + '\');" value="'+responseBean.id+'" onclick=""  >'+ responseBean.pdfContent +'</li>';
					    						    	          
					    						    	        }
	    					    	        		}else{
			    					    	            optionalList +='<div class="mt-1"><i class="fa-solid fa-clone fa-xl"></i>&nbsp;&nbsp;'
			    			    	        				+'<span class="fs-6">Resources</span></div>';
			    			    	        				  // Iterate over the responseList
			    			    	        				for (var i = 0; i <responseList.length; i++) {
					    						    	          var responseBean = responseList[i];
					    						    	          // Perform further actions with the response bean
					    						    	          
																	
					    						    	          optionalList += '<li class="list-group-item text-wrap" style="width:100%;" id="' + responseBean.id + '" onClick="openPdfPage(\'' + responseBean.previewPath + '\', \'' + responseBean.name + '\');"   value="'+responseBean.pdfContent+'"  >'+ responseBean.pdfContent +'</li>';
					    						    	        }
	    					    	        		}	
			    					    	      
			    						    	        
	    					    	       }
	     					    	       //ppt
	    					    	        if ((key==="ppt") && keywordSeparatedLists[key].length>0){
	    					    	        	count++;
	    					    	        	var staticValue = 'resource';
	    					    	        	if(responseList.length>3){
	    					    	        	optionalList+='<div class="row mt-3">'
	    					    	        					+'<div class="col-8">'
	    					    	        						+'<span><i class="fa-solid fa-clone fa-xl"></i></span><span class="fs-6">PPT</span>'
	    					    	        					+'</div>'
	    					    	        					+'<div class="col-4 text-end">'
	    					    	        						+'<a class="seeAll" href="#" onclick="seeAll(\'' + staticValue + '\')"><small>See All</small></a>'
	    					    	        					+'</div>'
	    					    	        					+'</div>';

	    					    	        					// Iterate over the responseList
	    			    						    	        for (var i = 0; i <3; i++) {
	    			    						    	          var responseBean = responseList[i];
	    			    						    	          // Perform further actions with the response bean
	    			    						    	          optionalList += '<li class="list-group-item text-wrap"  style="width:100%;" id="' + responseBean.id + '" value="'+responseBean.pdfContent+'"  >' + responseBean.pdfContent+'</li>';
	    			    						    	        }
	    					    	        	}else{
	    					    	            optionalList +='<div class="mt-3"><i class="fa-solid fa-clone fa-xl"></i>&nbsp;&nbsp;'
	    			    	        				+'<span class="fs-6">PPT</span></div>';

	    			    	        				// Iterate over the responseList
		    						    	        for (var i = 0; i <responseList.length; i++) {
		    						    	          var responseBean = responseList[i];
		    						    	          // Perform further actions with the response bean
		    						    	          optionalList += '<li class="list-group-item text-wrap"  style="width:100%;" id="' + responseBean.id + '" value="'+responseBean.pdfContent+'" >' + responseBean.pdfContent+'</li>';
		    						    	        }
	    					    	        	}		
			    					    	       	 
	     					    	       }
	    					    	        if (key==="video"  && keywordSeparatedLists[key].length>0){
	    					    	        	count++;
	    					    	        	var staticValue = 'video';
	    					    	        	if(responseList.length>3){
	    					    	        	optionalList+='<div class="row mt-3">'
	    					    	        					+'<div class="col-lg-8 col-md-8 col-8">'
	    					    	        						+'<span><i class="fa-sharp fa-regular fa-circle-play fs-4"></i></span><span class="fs-6">Session Recording</span>'
	    					    	        					+'</div>'
	    					    	        					+'<div class="col-lg-4 col-md-4 col-4" style="text-align: end;">'
	    					    	        						+'<a class="seeAll" href="#" onclick="seeAll(\'' + staticValue + '\')"><small >See All</small></a>'
	    					    	        					+'</div>'
	    					    	        					+'</div>';
					    	        					 // Iterate over the responseList
		    						    	        for (var i = 0; i <3; i++) {
		    						    	          var responseBean = responseList[i];
		    						    	          // Perform further actions with the response bean
		    						    	          optionalList += '<li class="list-group-item text-wrap"  style="width:100%;" id="' + responseBean.id + '" value="'+responseBean.transcriptContent+'" onClick="openVideoPage(\'' + responseBean.videoContentId + '\', \'' + responseBean.programSemSubjectId + '\');">' + responseBean.transcriptContent+'</li>';
		    						    	        }
	    					    	        	}else{
		    					    	        optionalList +='<div class="mt-2"><i class="fa-sharp fa-regular fa-circle-play fs-4"></i>&nbsp;&nbsp;'
		    					    	        				+'<span class="fs-6">Session Recording</span></div>';

			    	        						 for (var i = 0; i <responseList.length; i++) {
			    						    	          var responseBean = responseList[i];
			    						    	          // Perform further actions with the response bean
			    						    	          optionalList += '<li class="list-group-item text-wrap"  style="width:100%;" id="' + responseBean.id + '"  onClick="openVideoPage(\'' + responseBean.videoContentId + '\', \'' + responseBean.programSemSubjectId + '\');"   value="'+responseBean.transcriptContent+'"  >' + responseBean.transcriptContent+'</li>';
			    						    	        }
	    					    	        	} 
		    					    	       
	    					    	       }
	    					    	        if (key==="qna"  && keywordSeparatedLists[key].length>0){
	    					    	        	count++;
	    					    	        	var staticValue = 'qna';
	    					    	        	if(responseList.length>3){
	    					    	        	optionalList+='<div class="row " style="margin-top: 82px;">'
	    					    	        					+'<div class="col-lg-8 col-md-8 col-8">'
	    					    	        						+'<span><i class="far fa-comments"></i></span><span class="fs-6">QnA</span>'
	    					    	        					+'</div>'
	    					    	        					+'<div class="col-lg-4 col-md-4 col-4" style="text-align: end;">'
	    					    	        						+'<a class="seeAll" href="#" onclick="seeAll(\'' + staticValue + '\')"><small >See All</small></a>'
	    					    	        					+'</div>'
	    					    	        					+'</div>';
					    	        					 // Iterate over the responseList
			    						    	        for (var i = 0; i <3; i++) {
			    						    	          var responseBean = responseList[i];
			    						    	          // Perform further actions with the response bean
			    						    	          optionalList += '<li class="list-group-item" id="' + responseBean.id + '" value="'+responseBean.query+'"  >' + responseBean.query+'</li>';
			    						    	        }
	    					    	        	}else{
		    					    	        optionalList +='<div class="mt-3" style="margin-top: 82px;"> <i class="far fa-comments"></i>&nbsp; &nbsp;'
		    					    	        			+'<span class="fs-6">QnA</span></div>';

	   					    	        			 // Iterate over the responseList
		    						    	        for (var i = 0; i <responseList.length; i++) {
		    						    	          var responseBean = responseList[i];
		    						    	          // Perform further actions with the response bean
		    						    	          optionalList += '<li class="list-group-item text-wrap"  style="width:100%;" id="' + responseBean.id + '" value="'+responseBean.query+'"   >' + responseBean.query+'</li>';
		    						    	        }
	    					    	        	}
		    					    	       
	    					    	        	 }
	    					    	      }//keywordSeparatedLists.hasOwnProperty(key) end
	    					    	    }//responce iteration end
	    							 optionalList += '</ul><br>';		    	    
	    								$('#searchResult').html(optionalList);
	    							//if all list dont have matching data	
	   								if(count==0){
	   					    	    	$("#searchResult").html("<p>No Data Found</p>");
	       					    	  }
	    			    	},
	    			    	error: function(xhr, status, error) {
	    			    	  console.log("Error:", error);
	    			    	}
	    			   	});

	        		}else{
	        			$("#searchResult").html("<p>No Data Found</p>");

	            	}
			 
			});


}); 




//script to get value from globsal search and put to search result page
function seeAll(type) {
	var searchValue = document.getElementById('globalSearch').value;
	if (searchValue) {
	    window.location.href = "/studentportal/student/searchResultPage?search=" + searchValue+"&type="+type;
	}
	}	
//script to get value from globsal search and put to search result page
$("#allSearch").click(function(){
	var searchValue=document.getElementById('globalSearch').value;
	//redirecting to another url using javascript
	if(searchValue){
		window.location.href = "/studentportal/student/searchResultPage?search="+searchValue;
	}
});	

//appending value into search input
function getListName(title){
	//onclick="getListName(\'' + responseBean.pdfContent + '\');"
 //  $('#globalSearch').val(title)
}

function openPdfPage(priviewPath,name){
	if(priviewPath!=null && name!=null){
		var stringWithoutMarkTags = name.replace(/<\/?mark>/g, '');		
		var replacedName = stringWithoutMarkTags.replace(/ /g, '+');
		window.location.href = "/acads/student/previewContent?previewPath="+priviewPath+"&name="+replacedName+"&type=PDF";
		}
	}



function openVideoPage(videoContentId,pssId){
	if(videoContentId!=null && pssId!=null){
		window.location.href = "/acads/student/watchVideos?id="+videoContentId+"&pssId="+pssId;
	}
	
}	
//fuction to focus on directly to input field
function focusSearchBar() {
  setTimeout(function() {
  	var inputField = document.getElementById('globalSearch');
    inputField.focus();
  }, 1000); 
}	
   
	$(document).ready(function() {
	
	      var value = $("#checkHighContrast").val();
	      if (value === '1' ) {
	      $("button").addClass("buttonContrast");
	      $("input , select").addClass("inputBorder")
	      $("a").addClass("linkContrast");
	      $("a > .card").addClass("inputBorder");
  	      $("i").addClass("linkContrast");
	      
	    }
	    
	});
