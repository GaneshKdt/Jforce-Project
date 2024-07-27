		var step = 7;
		let questions={'q1Response':"The subject matter covered in this session helped you to understand and learn effectively?",
	        'q2Response':"The course material used was helpful towards today's session?",
	        'q3Response':"Audio quality was upto the mark?",
	        'q4Response':"Video quality was upto the mark?",
	        'q5Response':"The Faculty was organized and well prepared for the class?",
	        'q6Response':"The Faculty was effective in communicating the concept in the class (in terms of clarity and presenting the concepts in understandable manner)?",
	        'q7Response':"The Faculty was responsive to student's learning difficulties and dealt with questions appropriately.",
	        'q8Response':"The learning process adopted (e.g. case studies, relevant examples and presentation work etc.) were helpful towards learning from the session?"
	        }
	    $(document).ready(function(e){	    		   	    		    		    		    		    		        				        				        			        			        		    		   
	    	$("#studentConfirmationForAttendance").val("");
	    	document.getElementById("sessionAttendId").style.display = 'none';
	    	//$('.notnull').prop('required',true);
	    	
	    	$("#studentConfirmationForAttendance").change(function(){
	    		var sessionAttendanceResponse = $("#studentConfirmationForAttendance").val();
	    		if(sessionAttendanceResponse.trim() != ""){
	        		if(sessionAttendanceResponse =="Y"){
	        			document.getElementById("sessionAttendId").style.display = 'block';
	        			$('.reasonForAttend').prop('required',false);
	        			$('.otherReason').prop('required',false);
	        			document.getElementById("reasonForNotAttend").style.display ='none';
	        			document.getElementById("otherReason").style.display = 'none';
	        			//$('.notnull').prop('required',true);
	        			
	        			$(".reasonForAttend").val("");
	        			$("#otherReasonForNotAttending").val("");
	        			$("#otherReasonForNotAttending").html("");
	        		}else{
	        			document.getElementById("sessionAttendId").style.display = 'none';
	        			//$('.notnull').prop('required',false);
	        			$('.reasonForAttend').prop('required',true);
	        			$('.otherReason').prop('required',false);
	        			document.getElementById("reasonForNotAttend").style.display ='block';
	        			document.getElementById("otherReason").style.display = 'none';
	        			
	        			$(".slider").each(function () {
	        			      $(this).slider({
	        			        value: 1,
	        			      });
	        			      $(this).find(".text strong").html("-");
	        			 });
	        			
	        			 $(".rateDiv").attr("style","display:none;");

	        			    $(".worked").each(function () {
	        			      if ($(this).data("if") == "selected") {
	        			        $(this).click();
	        			        if ($(".othersWorked input").val() != "") {
	        			          $(".othersWorked input").val("");
	        			        }
	        			      }
	        			    });

	        			    $(".notWorked").each(function () {
	        			      if ($(this).data("if") == "selected") {
	        			        $(this).click();
	        			        if ($(".othersNotWorked input").val() != "") {
	        			          $(".othersNotWorked input").val("");
	        			        }
	        			      }
	        			    });

	        			    if ($("#rateExpYes").data("if") == "selected") {
	        			        $("#rateExpYes").data("if", "notSelected");
	        			        $("#rateExpYes").attr("style","background-color:white;color:black;border:1px solid black;font-weight:bold;");
	        			        $(".rateExpNotWorkedDiv").html("");									        			       
	        			    }

	        			    if ($("#rateExpNo").data("if") == "selected") {
	        			      $("#rateExpNo").data("if", "notSelected");
	        			      $("#rateExpNo").attr("style","background-color:white;color:black;border:1px solid black;font-weight:bold;");
	        			    }
	        			    
	        			    $(".q1Response").val("");									
							$(".q2Response").val("");									
							$(".q3Response").val("");									
							$(".q4Response").val("");									
							$(".q5Response").val("");									
							$(".q6Response").val("");								
							$(".q7Response").val("");									
							$(".q8Response").val("");
							$("#rateExpNotWorked").val("");
							$(".feedbackRemark").val("");
	        		}
	        	}else{
	        		document.getElementById("sessionAttendId").style.display = 'none';
        			document.getElementById("reasonForNotAttend").style.display ='none';
        			$('.reasonForAttend').prop('required',false);
        			$('.otherReason').prop('required',false);
        			document.getElementById("otherReason").style.display = 'none';
        			
        			$(".slider").each(function () {
      			      $(this).slider({
      			        value: 1,
      			      });
      			      $(this).find(".text strong").html("-");
      			 });
      			
      			 $(".rateDiv").attr("style","display:none;");

      			    $(".worked").each(function () {
      			      if ($(this).data("if") == "selected") {
      			        $(this).click();
      			        if ($(".othersWorked input").val() != "") {
      			          $(".othersWorked input").val("");
      			        }
      			      }
      			    });

      			    $(".notWorked").each(function () {
      			      if ($(this).data("if") == "selected") {
      			        $(this).click();
      			        if ($(".othersNotWorked input").val() != "") {
      			          $(".othersNotWorked input").val("");
      			        }
      			      }
      			    });

      			    if ($("#rateExpYes").data("if") == "selected") {
      			        $("#rateExpYes").data("if", "notSelected");
      			        $("#rateExpYes").attr("style","background-color:white;color:black;border:1px solid black;font-weight:bold;");
      			        $(".rateExpNotWorkedDiv").html("");									        			       
      			    }

      			    if ($("#rateExpNo").data("if") == "selected") {
      			      $("#rateExpNo").data("if", "notSelected");
      			      $("#rateExpNo").attr("style","background-color:white;color:black;border:1px solid black;font-weight:bold;");
      			    }
      			    
      			    $(".q1Response").val("");									
					$(".q2Response").val("");									
					$(".q3Response").val("");									
					$(".q4Response").val("");									
					$(".q5Response").val("");									
					$(".q6Response").val("");								
					$(".q7Response").val("");									
					$(".q8Response").val("");
					$("#rateExpNotWorked").val("");
					$(".feedbackRemark").val("");
	        	}
	    	});
	    	
	    	$("#reasonForNotAttending").change(function(){
	    		var reasonForNotAttending = $("#reasonForNotAttending").val();
		    	if(reasonForNotAttending.trim() != ""){
		    		if(reasonForNotAttending =="Others"){
		    			document.getElementById("otherReason").style.display = 'block';
		    			$('.otherReason').prop('required',true);
		    		}else{
		    			document.getElementById("otherReason").style.display = 'none';
		    			$('.otherReason').prop('required',false);
		    		}
		    	}else{
		    		document.getElementById("otherReason").style.display = 'none';
		    		$('.otherReason').prop('required',false);
		    	}
	    	});
	    	
	    });
	    					//code for slider								

							$(".slider").each(
									function() {
										var self = $(this);
										var slider = self.slider({
											create : function() {
												self.find(".text strong").text("-");
												setPathData(self
														.find(".smiley").find(
																"svg path"),
														self.slider("value"));
											},
											slide : function(event, ui) {
												self.find(".text strong").text(
														ui.value);
												setPathData(self
														.find(".smiley").find(
																"svg path"),
														ui.value);
												$(".rateDiv").attr("style","");												
												$(".q1Response").val(ui.value);
												$(".q2Response").val(ui.value);
												$(".q3Response").val(ui.value);
												$(".q4Response").val(ui.value);
												$(".q5Response").val(ui.value);
												$(".q6Response").val(ui.value);
												$(".q7Response").val(ui.value);
												$(".q8Response").val(ui.value);												
											},
											range : "min",
											min : 1,
											max : step,
											value : 1,
											step : 1
										});
									});

							function setPathData(path, value) {
								var firstStep = (6 / step) * value;
								var secondStep = (2 / step) * value;
								path.attr("d", "M1," + (7 - firstStep)
										+ " C6.33333333," + (2 + secondStep)
										+ " 11.6666667," + (1 + firstStep)
										+ " 17," + (1 + firstStep)
										+ " C22.3333333," + (1 + firstStep)
										+ " 27.6666667," + (2 + secondStep)
										+ " 33," + (7 - firstStep));
							}
							
							$(".worked").click(function(){
								if($(this).data("if")=="notSelected"){
									$(this).data("if","selected");
									$(this).attr("style","background-color:#d2232a;color:white;border:1px solid #d2232a;font-weight:bold;padding:0.5rem;");																	
									
									if($(this).attr("id")=="othersWorked"){
										$(".othersWorked").attr("style","");
										$(".othersWorked input").attr("required","required");
									}else{
										$(".notWorkedDiv #"+$(this).attr("id")).attr("disabled","disabled");										
										$(".notWorkedDiv #"+$(this).attr("id")).attr("style","background-color:white;color:black;border:1px solid black;font-weight:bold;padding:0.5rem;");
										$(".notWorkedDiv #"+$(this).attr("id")).data("if","notSelected");
										$(".notWorkedDiv #"+$(this).attr("id")).css("cursor","not-allowed");
									}
								} else{
									$(this).data("if","notSelected");
									$(this).attr("style","background-color:white;color:black;border:1px solid black;font-weight:bold;padding:0.5rem;");																	
									
									if($(this).attr("id")=="othersWorked"){
										$(".othersWorked").attr("style","display:none;");
										$(".othersWorked input").removeAttr("required");
										$(".othersWorked input").val("");
									}else{
										$(".notWorkedDiv #"+$(this).attr("id")).removeAttr("disabled");
										$(".notWorkedDiv #"+$(this).attr("id")).css("cursor","pointer");										
									}
								}
								
								
							});
							
							$(".notWorked").click(function(){
								if($(this).data("if")=="notSelected"){
									$(this).data("if","selected");
									$(this).attr("style","background-color:#d2232a;color:white;border:1px solid #d2232a;font-weight:bold;padding:0.5rem;");
																		
									
									if($(this).attr("id")=="othersNotWorked"){
										$(".othersNotWorked").attr("style","");
										$(".othersNotWorked input").attr("required","required");
									}
									
									/* if($(this).attr("id")=="notAttendedNotWorked"){
										$(".reasonForNotAttending").attr("style","");
										$(".attended").val("N");
										$(".reasonForNotAttending input").attr("required","required");
									} */
									
									if($("#rateExpYes").data("if")=="selected"){
										$(".rateExpNotWorkedDiv").html("");																			
										
										$("#rateExpYes").data("if","notSelected");
										$("#rateExpYes").attr("style","background-color:white;color:black;border:1px solid black;font-weight:bold;");
										
										$("#rateExpNo").data("if","notSelected");
										$("#rateExpNo").attr("style","background-color:white;color:black;border:1px solid black;font-weight:bold;");
									}
								} else{
									$(this).data("if","notSelected");
									$(this).attr("style","background-color:white;color:black;border:1px solid black;font-weight:bold;padding:0.5rem;");																				
									
									if($(this).attr("id")=="othersNotWorked"){
										$(".othersNotWorked").attr("style","display:none;");
										$(".othersNotWorked input").removeAttr("required");
										$(".othersNotWorked input").val("");
									}
									
									/* if($(this).attr("id")=="notAttendedNotWorked"){
										$(".reasonForNotAttending").attr("style","display:none;");
										$(".reasonForNotAttending input").removeAttr("required");
										$(".attended").val("Y");
										$(".reasonForNotAttending input").val("");
									} */
									
									if($("#rateExpYes").data("if")=="selected"){
										$(".rateExpNotWorkedDiv").html("");																			
										
										$("#rateExpYes").data("if","notSelected");
										$("#rateExpYes").attr("style","background-color:white;color:black;border:1px solid black;font-weight:bold;");
										
										$("#rateExpNo").data("if","notSelected");
										$("#rateExpNo").attr("style","background-color:white;color:black;border:1px solid black;font-weight:bold;");
									}
								}
																
							});
							
							$("#rateExpYes").click(function(){
								if($(this).data("if")=="notSelected"){
									var c=0;
									$(".notWorked").each(function(){
										if($(this).data("if")=="selected" && $(this).val()!="Others"){
											c++;
										}
									});
									if(c==0){
										alert("Please choose at least one option which didn't work for you!");
									} else{
										$(this).attr("style","background-color:#d2232a;color:white;border:1px solid #d2232a;font-weight:bold;");
										$(this).data("if","selected");
										
										$("#rateExpNo").attr("style","background-color:white;color:black;border:1px solid black;font-weight:bold;");
										$("#rateExpNo").data("if","notSelected");
										
										$("#rateExpNotWorked").val($(this).val());
										
										$(".notWorked").each(function(){
											if($(this).data("if")=="selected"){
												var valSlice=$(this).attr("id").slice(0,2)+"Remark";											
												var orgVal=$(this).val();
												var id=$(this).attr("id");
												if(orgVal!="Others"/*  && orgVal!="Not Attended" */){
													$(".rateExpNotWorkedDiv").html($(".rateExpNotWorkedDiv").html()+'<h2 class="black">'+questions[id]+'</h2><div class="clearfix"></div><div class="row"><div class="col-sm-9" style="padding: 1rem; margin: 6rem 0rem 2rem 3rem;"><div class="slider '+id+'"><div class="ui-slider-handle"><div class="smiley"><svg viewBox="0 0 34 10" version="1.1"><path d=""></path></svg></div></div><div class="text"><span><b>Rate</b></span> <strong data-val="'+orgVal+'">-</strong></div></div></div></div><div class="row"><div class="col-sm-8 notAttendedNotWorked" id="'+valSlice+'" style="display: none;"><div class="col-sm-12"><input type="text" class="form-control" name="'+valSlice+'" placeholder="Enter remark" /></div></div>');
												}
											}
										});
										
										$(".rateExpNotWorkedDiv .slider").each(function(){
											var self = $(this);
											var slider = self.slider({
												create : function() {
													self.find(".text strong").text("-");
													setPathData(self
															.find(".smiley").find(
																	"svg path"),
															self.slider("value"));
												},
												slide : function(event, ui) {												
													self.find(".text strong").text(
															ui.value);
													setPathData(self
															.find(".smiley").find(
																	"svg path"),
															ui.value);	
													
													$("."+self.attr("class").split(" ")[1]).val(ui.value);
													
													if(parseInt(ui.value)<5){
														$("#"+self.attr("class").split(" ")[1].slice(0,2)+"Remark").attr("style","display:block;");
														$("#"+self.attr("class").split(" ")[1].slice(0,2)+"Remark input").attr("required","true");
													} else if(parseInt(ui.value)>=5){
														$("#"+self.attr("class").split(" ")[1].slice(0,2)+"Remark").attr("style","display:none;");
														$("#"+self.attr("class").split(" ")[1].slice(0,2)+"Remark input").val("");
														$("#"+self.attr("class").split(" ")[1].slice(0,2)+"Remark input").removeAttr("required");
													}																									
												},
												range : "min",
												min : 1,
												max : step,
												value : 1,
												step : 1
											});
										});
									}
								}
							});	
																		
								$("#rateExpNo").click(function(){
									$(this).attr("style","background-color:#d2232a;color:white;border:1px solid #d2232a;font-weight:bold;");
									$(this).data("if","selected");
									
									$("#rateExpYes").attr("style","background-color:white;color:black;border:1px solid black;font-weight:bold;");
									$("#rateExpYes").data("if","notSelected");
									
									$("#rateExpNotWorked").val($(this).val());
									
									$(".rateExpNotWorkedDiv").html("");
									
									$(".q1Response").val($(".rateExp").find(".text strong").text());									
									$(".q2Response").val($(".rateExp").find(".text strong").text());									
									$(".q3Response").val($(".rateExp").find(".text strong").text());									
									$(".q4Response").val($(".rateExp").find(".text strong").text());									
									$(".q5Response").val($(".rateExp").find(".text strong").text());									
									$(".q6Response").val($(".rateExp").find(".text strong").text());								
									$(".q7Response").val($(".rateExp").find(".text strong").text());									
									$(".q8Response").val($(".rateExp").find(".text strong").text());																	
							});						

			
			
    
	 function askReson(responseId,remarkId,remarkValue){
		 var select_id = document.getElementById(responseId);

		 var response = parseInt( select_id.options[select_id.selectedIndex].value);
		 if(response <5){
			 document.getElementById(remarkId).style.display = 'block';
		 }else{
			 document.getElementById(remarkId).style.display = 'none';
			 document.getElementById(remarkValue).value = "";
		 }
	 }
	 
	 $("#submit").click(function(){
		if(validateForm()){
			$("#form").submit();
		}
	});
	 
    function validateForm(){    	
    	/* console.log("worked",workedArr);    	
    	console.log("notWorked",notWorkedArr);
    	
	    var q1Response = parseInt(document.getElementById("q1Response").value);
		var q2Response = parseInt(document.getElementById("q2Response").value);
		var q3Response = parseInt(document.getElementById("q3Response").value);
		var q4Response = parseInt(document.getElementById("q4Response").value);
		var q5Response = parseInt(document.getElementById("q5Response").value);
		var q6Response = parseInt(document.getElementById("q6Response").value);
		var q7Response = parseInt(document.getElementById("q7Response").value);
		var q8Response = parseInt(document.getElementById("q8Response").value);
		
		var q1Remark = document.getElementById("q1Remark").value;
		var q2Remark = document.getElementById("q2Remark").value;
		var q3Remark = document.getElementById("q3Remark").value;
		var q4Remark = document.getElementById("q4Remark").value;
		var q5Remark = document.getElementById("q5Remark").value;
		var q6Remark = document.getElementById("q6Remark").value;
		var q7Remark = document.getElementById("q7Remark").value;
		var q8Remark = document.getElementById("q8Remark").value; */
		
		var total = 0, cWorked = 0, cNotWorked = 0, cRateExpNotWrked = 0;
		var workedComment="", notWorkedComment="";
		var remarksAlertMessage = "";
		if($("#studentConfirmationForAttendance").val()=="Y"){		
			if ($(".rateExp .text strong").text() == "-") {
				remarksAlertMessage += "Please rate your experience for session \n";
			} else{

			$(".worked").each(function() {
				if ($(this).data("if") == "selected") {
					cWorked++;
					if($(this).val()=="Others"){
						workedComment=$(".othersWorked input").val();		
					}
				}
				
			});
			$(".notWorked").each(function() {
				if ($(this).data("if") == "selected") {
					cNotWorked++;
					if($(this).val()=="Others"){
						notWorkedComment=$(".othersNotWorked input").val();		
					}
				}
			});					

			/* if (cWorked == 0) {
				remarksAlertMessage += "Please select at least one option which worked for you \n";
			} */

			/* if (cNotWorked == 0) {
				remarksAlertMessage += "Please select at least one option which didn't work for you \n";
			} */
			
			$(".rateExpNotWorked").each(function(){
				if($(this).data("if")=="selected"){
					cRateExpNotWrked++;
				}
			});
			
			if(cRateExpNotWrked == 0){
				remarksAlertMessage += "Please choose yes or no option for which factors didn't work for you \n";
			}
			
			if($("#rateExpYes").data("if")=="selected"){
				$(".rateExpNotWorkedDiv .slider .text strong").each(function(){
					if ($(this).text() == "-") {
						remarksAlertMessage += "Please rate your experience for : "+$(this).data("val")+" \n";
					}
				});
			}
			/* 
			$("#worked").val(workedArr.join(","));			
			$("#notWorked").val(notWorkedArr.join(","));	 */				

		/* if (!isNaN(q1Response)) {
			total = parseInt(q1Response);
			if (total < 5) {
				if (q1Remark.trim() == "") {
					remarksAlertMessage = remarksAlertMessage
							+ "Please Enter remarks for low rating of Session Q.1 \n";
					document.getElementById("q1Remark").focus();
				}
			}
		}
		if (!isNaN(q2Response)) {
			total = parseInt(q2Response);
			if (total < 5) {
				if (q2Remark.trim() == "") {
					remarksAlertMessage = remarksAlertMessage
							+ "Please Enter remarks for low rating of Session Q.2 \n";
					document.getElementById("q2Remark").focus();
				}
			}
		}

		if (!isNaN(q3Response)) {
			total = parseInt(q3Response);
			if (total < 5) {
				if (q3Remark.trim() == "") {
					remarksAlertMessage = remarksAlertMessage
							+ "Please Enter remarks for low rating of TECHNICAL Q.1 \n";
					document.getElementById("q3Remark").focus();
				}
			}
		}
		if (!isNaN(q4Response)) {
			total = parseInt(q4Response);
			if (total < 5) {
				if (q4Remark.trim() == "") {
					remarksAlertMessage = remarksAlertMessage
							+ "Please Enter remarks for low rating of TECHNICAL Q.2 \n";
					document.getElementById("q4Remark").focus();
				}
			}
		}
		if (!isNaN(q5Response)) {
			total = parseInt(q5Response);
			if (total < 5) {
				if (q5Remark.trim() == "") {
					remarksAlertMessage = remarksAlertMessage
							+ "Please Enter remarks for low rating of Faculty Q.1 \n";
					document.getElementById("q5Remark").focus();
				}
			}
		}
		if (!isNaN(q6Response)) {
			total = parseInt(q6Response);
			if (total < 5) {
				if (q6Remark.trim() == "") {
					remarksAlertMessage = remarksAlertMessage
							+ "Please Enter remarks for low rating of Faculty Q.2 \n";
					document.getElementById("q6Remark").focus();
				}
			}
		}
		if (!isNaN(q7Response)) {
			total = parseInt(q7Response);
			if (total < 5) {
				if (q7Remark.trim() == "") {
					remarksAlertMessage = remarksAlertMessage
							+ "Please Enter remarks for low rating of Faculty Q.3 \n";
					document.getElementById("q7Remark").focus();
				}
			}
		}
		if (!isNaN(q8Response)) {
			total = parseInt(q8Response);
			if (total < 5) {
				if (q8Remark.trim() == "") {
					remarksAlertMessage = remarksAlertMessage
							+ "Please Enter remarks for low rating of Faculty Q.4 \n";
					document.getElementById("q8Remark").focus();
				}
			}
		} */
			}
		}
		if (remarksAlertMessage != "") {
			alert(remarksAlertMessage);
			return false;
		}
		
		
		if(workedComment!="" && notWorkedComment!=""){
			$(".feedbackRemark").val(workedComment+"~|~"+notWorkedComment);
		}
		return true;
	}
