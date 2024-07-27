var previewed=false;
		$("document").ready(function() {
			console.log("Hello there!!")
			var pipedValueArray = new Array();
			var semesters = new Array("1", "2", "3","4","5","6");//Assign array of semester//
			var courierAmount = 0;
			
			$('#addressDiv').css('display', 'block');
			$("#courierAmount").val(courierAmount + 100);
			$('#addressConfirmation').val("Yes");
			//alert('Total Amount Payable = '+ $("#courierAmount").val());
			
			$("#removeMarksheetRecord").prop("disabled",true);
			$('#addMarksheetRecord').click(function () {
				var sizeOfTable = $("#markSheetTable").find("tr").length;
				if(sizeOfTable == 1){
					
					$("#removeMarksheetRecord").prop("disabled",true);//Minimum one row should be there on the table
				}
				
				if(sizeOfTable >5){
					alert("You cannot issue more than 4 marksheets")
				}else{
					
					$("#removeMarksheetRecord").prop("disabled",false); //Give access to remove record//
					var count = 0; //This will assign unique numbers to ids below//
                    var lastRow = $("#markSheetTable").find("tr:last-child"); // query the row of the table
                   
														var cloned = lastRow.clone(); // Clone the row  
														count++;  
														cloned.find('input, select, button').each(
																function(){ //In the cloned row query the select attributes//
																	var id  = $(this).attr('id');	
																	if (semesters.indexOf(id[id.length - 1]) == -1) { //if last element not equal to any number
																		var newId = id ; 
																		$(this).attr('id',newId);
																		$(this).attr('name',newId);	
																		
																	
																			} else {//else append the id with a unique number for future purpose

																				var newId = id.substr(0,id.length -1)+(parseInt(id[id.length - 1]) + count);
																			 //	console.log('append the id with a unique number for future purpose :: ' + newId);
																				$(this).attr('id',newId);
																				$(this).attr('name',newId);	
																				$(this).attr('data-count', count+1);
																			}
																		});
														cloned.insertAfter(lastRow);
													}
												});
			
								$("#submit").click(function(){
									/* console.log('pipedValueArray in submit function'+pipedValueArray); */
									if(!previewed){
										
										alert("please verify marksheet generated "); 
										return false;
									} 
									if(!$('#infoConfirmation').is(":checked")){
										alert("please check confirmation "); 
										return false;
									}
									var pipedValueArray = [];
									var count=1;
									var pipedDetails="";
									$('.selectCheckBox').each(function() {
										var tr = $(this).closest("tr");
										var year= tr.find(".year").val();
										var month=tr.find(".month").val();
										var sem=tr.find(".sem").val();
										if ($(this).is(":checked")) {
											if(year!="" && month!="" && sem!=""){  
												var pipedDetails = year+ '|' +month+ '|' + sem;
												$("#marksheetDetailRecord"+sem).val(pipedDetails);
												pipedValueArray.push(pipedDetails);   
											}
									 	}else{
									 		$("#marksheetDetailRecord"+sem).val(""); 
									 	}
										 
									});    
									console.log('Sem 1-->'+document.getElementById("marksheetDetailRecord1").value);
									console.log('Sem 2-->'+document.getElementById("marksheetDetailRecord2").value);
									console.log('Sem 3-->'+document.getElementById("marksheetDetailRecord3").value);
									console.log('Sem 4-->'+document.getElementById("marksheetDetailRecord4").value); 
									console.log("pipedValueArray");
									console.log(pipedValueArray);
									 
									var sorted_arr = pipedValueArray.slice().sort(); // You can define the comparing function here. 
                                    // JS by default uses a crappy string compare.
                                    // (we use slice to clone the array so the
                                    // original array won't be modified)
									var results = [];
									for (var i = 0; i < pipedValueArray.length - 1; i++) {
									   if (sorted_arr[i + 1] == sorted_arr[i]) {
									       results.push(sorted_arr[i]);
									   }
									} 
									if(results.length > 0){
										alert("Kindly verify the current selection for Marksheet Request.Possible duplicate values");
										return false;
									}
									
								}); 

								$('#removeMarksheetRecord').click(function() {
									$("table").find("input:checkbox:not(:checked)").each(function() {
										$(this).parents("tr").remove();							
									});									
								});													
								$('#markSheetTable').on('click',':checkbox',function() {	
									 
									var selectBoxId = $(this).attr('id');
									var count = selectBoxId.substring(8);
									console.log(" selectBoxId");
									console.log(selectBoxId);
									var rowValuesGenerated = $("#markSheetTable").find("tr:not(:first)"); //Exclude the first row while checking if same semester is selected//			
									console.log(rowValuesGenerated.length);
									if ($(this).is(":checked")) {
										var previewButton = $(this).closest("tr").find(".previewMarksheet");  //get nearest preview element id
										generateMarksheetPreview(previewButton);   //call common function to generate preview
										var semesterParameter = selectBoxId[selectBoxId.length - 1];						
										if (rowValuesGenerated.length != 1) {			 
											rowValuesGenerated.find('input, select, button').each(function() {				
												var id = $(this).attr('id');	
												var selectOptionValue = $("#"+ id).val();
												console.log(" selectOptionValue");
												console.log(selectOptionValue);
											});		
										}		
										
										/* assignHiddenValueWithMarksheetParameters(semesterParameter,pipedValueArray); */	
										       
										$(this).closest("tr").find('#previewMarksheet').css('display', 'block');   
									} else {					
										var semesterParameter = selectBoxId[selectBoxId.length - 1];									
										/* clearValuesInHiddenParameterForParticularSemester(semesterParameter,pipedValueArray); */
										$(this).closest("tr").find('#previewMarksheet').css('display', 'none');  
									} 

								});
							});
	
		$(document).on('click', ".previewMarksheet", function(e) {
			
			generateMarksheetPreview($(this));
			   
		});
		
		function generateMarksheetPreview(thisElement){
			previewed = true;
			var sem = $(thisElement).closest("tr").find(".sem").val();
			  
			var examYear = $(thisElement).closest("tr").find(".year").val();
			  
			var examMonth = $(thisElement).closest("tr").find(".month").val();
			
			 
				var program = $(thisElement).attr('data-program');  
				 
				var sapid =$(thisElement).attr('data-sapid');
				
				var examMode = $(thisElement).attr('data-mode');
				  
				var programStructure = $(thisElement).attr('data-programStructure');
			
				//alert(sem + program + examYear + examMonth +sapid+examMode);
				var body =  {
		            	'sem' : sem,
		            	'program' : program,
		            	'examYear' : examYear,
		            	'examMonth' : examMonth,
		            	'sapid' : sapid,
		            	'examMode' : examMode,
		            	'prgmStructApplicable':programStructure
		            };

        		$('.marksheetSem').html(sem)
				$.ajax({
	                type: "POST",
	                url: '/exam/student/generateMarksheetPreviewFromSR',
	                data: JSON.stringify(body),
	                contentType: "application/json",
	                dataType : "json",
	               
	                success:function(data){
	                	///console.log("success :: ");
	                	//console.log(data);
	                	if(data.error == "true"){
	                		//console.log("Error found");
	                		var table = $(".modal-body").find('table');
	                        var resp = data;
	                        $("#myModalLabel2")[0].innerText = '';
                        	$("#myModalLabel2")[0].innerText = 'Marksheet Marks Preview';
	                        var error = [];
	                        error = resp.errorMessage;
	                        var i = 0;
	                        var table="<tr><th>Error</th></tr>";
	                    
	                        for (i = 0; i <error.length; i++) { 
	                            table += "<tr><td>" +
	                            error[i] +
	                            "</td><td>" ;
	                          }
	                        $(".modal-body").html(table); 
	                        var myModal = document.getElementById('myModal2');
							var modal = new bootstrap.Modal(myModal) ;
							modal.show();
	                	}else{
	                		var table = $(".modal-body").find('table table-bordered');
	                        var resp = data;
	                        var resultSourcee = resp.resultSourcee;
	                        $("#myModalLabel2")[0].innerText = '';
                        	$("#myModalLabel2")[0].innerText = 'Marksheet Marks Preview';
	                        if(undefined != resultSourcee) {
	                        	if(resultSourcee == ':') {
	                        		$("#myModalLabel2")[0].innerText += ' ' + resultSourcee;
	                        	}
	                        }
	                        var mark = []; 
	                        mark = resp.marks;
	                        var i = 0;
	                       // console.log(mark.length);
	                        var table="<tr><th>Subject</th><th>Written Marks</th><th>Assignment Marks</th><th>Total Marks</th></tr>";
	                        
	                        for (i = 0; i <mark.length; i++) { 
	                            table += "<tr><td>" +
	                            mark[i].subject +
	                            "</td><td>" +
	                            mark[i].writtenscore +
	                            "</td><td>" +
	                            mark[i].assignmentscore +
	                            "</td><td>"+
	                            mark[i].total +
	                            "</td></tr>";
	                          }
	                       
	                        $(".table").html(table);
	                        var myModal = document.getElementById('myModal2');
							var modal = new bootstrap.Modal(myModal) ;
							modal.show();
	                     
	                	}
	      			
	                	//alert("1");
	               },
	               error:function(){
	            	   var table = $(".modal-body").find('table');
	                   
	                   var table="<tr><th>Error</th></tr>";
	                 
	                       table += "<tr><td>Server Error</td><td>" ;
	                   
	                   $(".modal-body").html(table);
	                   var myModal = document.getElementById('myModal2');
						var modal = new bootstrap.Modal(myModal) ;
						modal.show();
	                   /* $('#myModal2').modal('show'); */
	               }
	            });        
		
		}
		
/*  			function assignHiddenValueWithMarksheetParameters(semester,pipedValueArray) {
				if (semester == '1') {
					/* $("#year1").attr('disabled', 'disabled');
					$("#month1").attr('disabled', 'disabled');
					$("#sem1").attr('disabled', 'disabled'); 
					
					var pipedDetails = document.getElementById("year1").value+ '|' + document.getElementById("month1").value+ '|' + document.getElementById("sem1").value;
					document.getElementById("marksheetDetailRecord1").value = pipedDetails;
					pipedValueArray.push(pipedDetails);
					
				} else if (semester == '2') {
					/* $("#year2").attr('disabled', 'disabled');
					$("#month2").attr('disabled', 'disabled');
					$("#sem2").attr('disabled', 'disabled'); 
					var pipedDetails = document.getElementById("year2").value+ '|' + document.getElementById("month2").value+ '|' + document.getElementById("sem2").value;
					document.getElementById("marksheetDetailRecord2").value = pipedDetails;
					pipedValueArray.push(pipedDetails);
				} else if (semester == '3') {
					/* $("#year3").attr('disabled', 'disabled');
					$("#month3").attr('disabled', 'disabled');
					$("#sem3").attr('disabled', 'disabled'); 
					var pipedDetails = document.getElementById("year3").value+ '|' + document.getElementById("month3").value+ '|' + document.getElementById("sem3").value;
							
					document.getElementById("marksheetDetailRecord3").value = pipedDetails;
					pipedValueArray.push(pipedDetails);
				} else {
					 $("#year4").attr('disabled', 'disabled');
					$("#month4").attr('disabled', 'disabled');
					$("#sem4").attr('disabled', 'disabled'); 
					var pipedDetails = document.getElementById("year4").value
							+ '|' + document.getElementById("month4").value
							+ '|' + document.getElementById("sem4").value;
					document.getElementById("marksheetDetailRecord4").value = pipedDetails;
					pipedValueArray.push(pipedDetails);
				}
			}  */
			/*$('#addressConfirmation').click(
					function() {
						var courierAmount = 0;
						if (this.checked) {
							$('#addressDiv').css('display', 'block');
							$("#courierAmount").val(courierAmount + 100);
							$('#addressConfirmation').val("Yes");
							alert('Total Amount Payable = '+ $("#courierAmount").val());
							} else {
								$('#addressConfirmation').val("No");
							$('#addressDiv').css('display', 'none');
							$("#courierAmount").val(courierAmount);
						}
					});*/
			function clearValuesInHiddenParameterForParticularSemester(semester,pipedValueArray) {//Releasing hidden fields if checkbox is unselected//
				if (semester == '1') {
					$("#year1").removeAttr('disabled');
					$("#month1").removeAttr('disabled');
					$("#sem1").removeAttr('disabled');
					for(var x=0;x<pipedValueArray.length;x++){
						if(pipedValueArray[x] == $("#marksheetDetailRecord1").val()){//check if the attribute is in the array
							//Get the index//
							pipedValueArray.splice(x,1);//Splice it from the array//
						}
					}
					$("#marksheetDetailRecord1").val("");
				} else if (semester == '2') {
					$("#year2").removeAttr('disabled');
					$("#month2").removeAttr('disabled');
					$("#sem2").removeAttr('disabled');
					for(var x=0;x<pipedValueArray.length;x++){
						if(pipedValueArray[x] == $("#marksheetDetailRecord2").val()){//check if the attribute is in the array
							//Get the index//
							pipedValueArray.splice(x,1);//Splice it from the array//
						}
					}
					$("#marksheetDetailRecord2").val("");
				} else if (semester == '3') {
					$("#year3").removeAttr('disabled');
					$("#month3").removeAttr('disabled');
					$("#sem3").removeAttr('disabled');
					for(var x=0;x<pipedValueArray.length;x++){
						if(pipedValueArray[x] == $("#marksheetDetailRecord3").val()){//check if the attribute is in the array
							//Get the index//
							pipedValueArray.splice(x,1);//Splice it from the array//
						}
					}
					$("#marksheetDetailRecord3").val("");
				} else if (semester == '4') {
					$("#year4").removeAttr('disabled');
					$("#month4").removeAttr('disabled');
					$("#sem4").removeAttr('disabled');
					for(var x=0;x<pipedValueArray.length;x++){
						if(pipedValueArray[x] == $("#marksheetDetailRecord4").val()){//check if the attribute is in the array
							//Get the index//
							pipedValueArray.splice(x,1);//Splice it from the array//
						}
					}
					$("#marksheetDetailRecord4").val("");
				} else if (semester == '5') {
					$("#year5").removeAttr('disabled');
					$("#month5").removeAttr('disabled');
					$("#sem5").removeAttr('disabled');
					for(var x=0;x<pipedValueArray.length;x++){
						if(pipedValueArray[x] == $("#marksheetDetailRecord5").val()){//check if the attribute is in the array
							//Get the index//
							pipedValueArray.splice(x,1);//Splice it from the array//
						}
					}
					$("#marksheetDetailRecord5").val("");
				} else {
					$("#year6").removeAttr('disabled');
					$("#month6").removeAttr('disabled');
					$("#sem6").removeAttr('disabled');
					for(var x=0;x<pipedValueArray.length;x++){
						if(pipedValueArray[x] == $("#marksheetDetailRecord6").val()){//check if the attribute is in the array
							//Get the index//
							pipedValueArray.splice(x,1);//Splice it from the array//
						}
					}
					$("#marksheetDetailRecord6").val(""); 
				}
			}
			
			
			$(document).ajaxStart(function(){
				  // Show image container
				  $("#loader").show();
				});
				$(document).ajaxComplete(function(){
				  // Hide image container
				  $("#loader").hide();
				});
				
				//Code for auto fill address on change of pincode start
				
				$("#postalCodeId").blur(function(){
					    //alert("This input field has lost its focus.");
						console.log("AJAX Start....");
						$("#pinCodeMessage").text("Getting City, State and Country. Please wait...");   
						var pinUrl = '/studentportal/getAddressDetailsFromPinCode';
				    	console.log("PIN : "+$("#postalCodeId").val());
				       var body =   {'pin' : $("#postalCodeId").val()};
				    	console.log(body);
				       $.ajax({
						url : pinUrl,
						type : 'POST',
						data: JSON.stringify(body),
			           contentType: "application/json",
			           dataType : "json",
			         
					}).done(function(data) {
						  console.log("iN AJAX SUCCESS");
						  console.log(data);
						  var status = data.success;
						  if("true" == status){
							  $("#shippingCityId").val(data.city);
							  $("#stateId").val(data.state);
							  $("#countryId").val(data.country);   
							  $("#pinCodeMessage").text("");   
							  console.log("iN SUCCESS true");
							  }else{
							  $('#shippingCityId').prop('readonly', false);
							  $('#stateId').prop('readonly', false);
							  $('#countryId').prop('readonly', false);
							  $("#pinCodeMessage").text("Unavaible to get City,State and Country. Kindly enter manually.");   
							  $("#pinCodeMessage").css("color","red");	
							  console.log("iN SUCCESS false");
						  }
					}).fail(function(xhr) {
						console.log("iN AJAX eRROR");
						console.log( xhr);
						  $("#pinCodeMessage").text("Unavaible to get City,State and Country. Kindly enter manually.");   
						  $("#pinCodeMessage").css("color","red");	
					  });
					
				});
			
				
					//Code for auto fill address on change of pincode end
					
					
					   function onlyAlphabets(e, t) {
				        try {
				            if (window.event) {
				                var charCode = window.event.keyCode;
				            }
				            else if (e) {
				                var charCode = e.which;
				            }
				            else { return true; }
				            if ((charCode > 64 && charCode < 91) || (charCode > 96 && charCode < 123))
				                return true;
				            else
				                return false;
				        }
				        catch (err) {
				            alert(err.Description);
				        }
				    }