 <!DOCTYPE html>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@page import="org.apache.commons.lang3.text.WordUtils"%> 
<%-- <%@page import="com.nmims.beans.StudentStudentPortalBean"%>
<%StudentStudentPortalBean student = (StudentStudentPortalBean)session.getAttribute("student_studentportal"); 
String firstNameOfStudent = student.getFirstName();
String lastNameOfStudent = student.getLastName();
String fullName = firstNameOfStudent + " " +lastNameOfStudent;
String finalNameOnCertificate = WordUtils.capitalizeFully(fullName);
boolean isCertificate = (Boolean)session.getAttribute("isCertificate");
%> --%>
<html lang="en">
   <!--  <style>
    #confirmation{
    position: unset;left: 999px;opacity: 100;
    }
    </style> -->

	
    
    <jsp:include page="../common/jscssNew.jsp">
	<jsp:param value="Enter Service Request Information" name="title"/>
    </jsp:include>
    
   
    <body>
    <c:set var="reason" value="${fn:split('Shifting Abroad,Not Interested,No time to study,Other', ',')}" scope="application" />

    	<jsp:include page="../common/headerDemo.jsp"/> 
    	
    	
        
        <div class="sz-main-content-wrapper">
        	<div class="sz-breadcrumb-wrapper">
			<div class="container-fluid">
				<ul class="sz-breadcrumbs">
					<li><a href="/studentportal/home">Student Zone</a></li>
					<li><a href="selectSRForm">Select Service Request</a></li>
					<li>Exit Program</li>
				</ul>
			</div>
		</div>
<%--         	<%@ include file="../common/breadcrum.jsp" %> --%>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
                     <div id="sticky-sidebar"> 
              				<jsp:include page="../common/left-sidebar.jsp">
								<jsp:param value="Service Request" name="activeMenu"/>
							</jsp:include>
							</div>
							
              				
              				
              				<div class="sz-content-wrapper examsPage">
              				
									<jsp:include page="../common/studentInfoBar.jsp"/>              						
              						
              						<div class="sz-content">
								
										<h2 class="text-danger text-capitalize">${sr.serviceRequestType }</h2>
										<div class="clearfix"></div>
		              					<div class="card card-body">
											<jsp:include page="../common/messageDemo.jsp"/>
											
											<p>
											Dear Student, You have chosen below Service Request. Please fill in required information below before proceeding for Payment. 
											</p>
											<!-- <p>You won't be able to submit service Request for next 48hrs . For details refer to "My Communications" Tab</p> -->
											
											<form:form  method="post" modelAttribute="sr" enctype="multipart/form-data" id="form1">
											<fieldset>
												
												<div class="row">
												<div class="col-md-8">
												
												<div class="form-group">
													<form:label class="fw-bold" path="serviceRequestType" for="serviceRequestType">Service Request Type:</form:label>
													<p>${sr.serviceRequestType }</p>
													<form:hidden path="serviceRequestType"/>
												</div>
												<!-- <input type="hidden" id="paymentOption" name="paymentOption" value="paytm"/> -->
												<div class="form-group">
													<form:label class="fw-bold" path="">Charges:</form:label>
													<p><span id="chargesSpan">No Charges</span></p>
												</div>
												<%-- <form:hidden path="amount" id="amount" value="0"/> --%>
												
												<div class="row">
													<div class="col-md-6">
														<div class="form-group">
															<form:label class="fw-bold" path="">Student No:</form:label>
															<p>${student.sapid }</p> 
														</div>
													</div>
													<div class="col-md-6">
														<div class="form-group">
															<label class="fw-bold">Student Name:</label>
															<p>${student.firstName } ${student.lastName }</p>  
														</div> 
													</div>
												</div> 
												<div class="row">
													<div class="col-md-6">
														<div class="form-group">
														<label class="fw-bold">Enrolment Year:</label>
														<p>${student.enrollmentYear }</p>
													</div>
													</div>
													<div class="col-md-6">
														<div class="form-group">
														<label class="fw-bold">Program Name:</label>
														<p>${student.program }</p>
													</div> 
													</div>
												</div> 
												<div class="row">
													<div class="col-md-6">
														<div class="form-group">
															<label class="fw-bold">Validity Month:</label>
															<p>${student.validityEndMonth }</p> 
														</div>
													</div>
													<div class="col-md-6">
														<div class="form-group">
															<label class="fw-bold">Validity Year:</label>
															<p>${student.validityEndYear }</p>
														</div> 
													</div>
												</div> 
												<div class="form-group">
													<label class="fw-bold">Reason for withdrawal:</label>
													<form:select id="serviceRequestType" path="description" class="form-select" required="required">
														<form:option value="">Select Service Request</form:option>
														<form:options items="${reason}" />  
													</form:select>
												</div> 
												<div class="row otherReasonDiv" style="display:none;">   
													<div class="form-group col-md-6"> 
														<label class="fw-bold" for="otherReason">Reason :</label>
														<form:input type="textarea" path="additionalInfo1" class="form-control otherReason"  id="otherReason" />   
														   
													</div>  
													<div class="form-group col-md-6"> 
													</div>   
												</div>
											<div class="form-group">
											 <!-- style="width:15px;height:15px;margin:5px; -->
												<label class="fw-bold"> <form:checkbox  path="hasDocuments" value="Parent" id="nameOnCertifcateIdYes"
														class="nameOnCertificateClass me-3" />
												<span>Do you wish the certificate to be in Husbands name ?</span>
												</label>
											</div>
											<!-- <div class="form-group" id="nameOnCertificateDocId" style="display: none">
												<label>Please Upload Marriage Certificate OR Affidavit</label> 
												<input type="file" id="nameOnCertificateDoc"
													name="nameOnCertificateDoc" class="form-control">
											</div> --> 

											<div class="preview">
												<button id="preveiwCert" class="btn btn-dark"
													data-bs-toggle="modal" data-bs-target="#previewCertificate">Preview
													Final Certificate</button>
											</div>
											 
											<%-- <div class="form-group">
												<label>
												<form:checkbox path="wantAtAddress" value="Yes" id="addressConfirmation"
												style="width:15px;height:15px;margin:5px;"/><span> &nbsp;I want Final Certificate at my address (Shipping Charges INR. 100/-)</span>
												</label>
											</div> --%>

							 
											<div class="form-group" id="addressDiv" style="display:none">
												<%-- <label for="postalAddress">Confirm/Edit Address</label>
												<textarea name="postalAddress" class="form-control" id="postalAddress" style="display:none;"  cols="50" rows = "5"><%=student.getAddress() %></textarea> --%>
												
												<h5 class="text-danger text-capitalize">SHIPPING ADDRESS</h5>
												<div class="clearfix"></div>
														<div class="form-group">
															<form:label class="fw-bold" for="houseNameId" path="houseNoName"> (*) Address Line 1 : House Details</form:label>
															<form:input type="text" path="houseNoName"  class="form-control shippingFields" id="houseNameId" 
																   name="shippingHouseName"  value="${student.houseNoName}" required = "required"/>
														</div>
														<div class="form-group">
															<form:label class="fw-bold" for="shippingStreetId" path="street"> (*) Address Line 2 : Street Name</form:label>
															<form:input type="text" path="street" class="form-control shippingFields" id="shippingStreetId"
															   name="shippingStreet"  value="${student.street}" required = "required"/>
														</div>
														<div class="form-group">
															<form:label class="fw-bold" for="localityNameId" path="locality"> (*) Address Line 3 : Locality Name</form:label>
															<form:input type="text" path="locality" class="form-control shippingFields" id="localityNameId" 
															   name="shippingLocalityName"  value="${student.locality}" required = "required"/>
														</div>
														<div class="form-group">
															<form:label class="fw-bold" for="nearestLandMarkId" path="landMark"> (*) Address Line 4 : Nearest LandMark</form:label>
															<form:input type="text"  path="landMark" class="form-control shippingFields" id="nearestLandMarkId" 
															   name="shippingNearestLandmark"  value="${student.landMark}" required = "required"/>
														</div>
														<div class="form-group mb-3">
															<form:label class="fw-bold" for="postalCodeId" path="pin"> (*) Postal Code</form:label>
															<form:input type="text" class="form-control shippingFields numonly" id="postalCodeId" 
															   name="shippingPostalCode"  value="${student.pin}" maxlength="6" path="pin"
															   required="required"/>
														</div>
														
														<span class="well-sm" id="pinCodeMessage"></span>	   
														<div class="form-group">
															<form:label class="fw-bold" for="shippingCityId" path="city"> (*) Shipping City</form:label>
															<form:input type="text" class="form-control shippingFields bg-dark-subtle" id="shippingCityId" 
																   name="shippingCity"  path="city"  value="${student.city}" readonly = "true"  
																   onkeypress= "return onlyAlphabets(event,this);" />
														</div>
														<div class="form-group">
															<form:label class="fw-bold" for="stateId" path="state"> (*) Shipping State</form:label>
															<form:input type="text" path="state" class="form-control shippingFields bg-dark-subtle" id="stateId"
															   name="shippingState"  value="${student.state}" readonly = "true"
													   onkeypress="return onlyAlphabets(event,this);"/>
														</div>
														<div class="form-group">
															<form:label for="countryId" path="country"> (*) Country For Shipping</form:label>
															<form:input type="text" path="country" class="form-control shippingFields bg-dark-subtle" id="countryId" 
															   name="shippingCountry"  value="${student.country}"  readonly = "true"
															   onkeypress="return onlyAlphabets(event,this);"/>
														</div> 
																											
																											
													</div>
													<div class="form-group">
														<input type="checkbox"  id="confirmation" /> 
														I hereby agree and accept to permanently exit from my program of study offered by SVKM's NMIMS - NMIMS Global Access - School for Continuing Education. I also agree that in case of any dispute or differences about this program exit, the decision of the SVKM's NMIMS - NMIMS Global Access - School for Continuing Education will be final and binding on me. I am aware that No fees pending or otherwise will be refunded.
													</div>
													<div class="form-group">
														<label class="control-label" for="submit"></label>
														<div class="controls">
															 <button 
																class="btn btn-danger submitform" >Save Service Request</button>
																<button id="backToSR" 
																 name="BacktoNewServiceRequest" class="btn btn-dark" 
																formaction="selectSRForm" formnovalidate="formnovalidate">Back to New Service Request</button>
														
														<%-- <button id="saveWithPaymentButton" class="btn btn-danger" style="display:none;" 
															<c:if test="${charges != 0}">
																style="display:inline;margin-top:20px;"
															</c:if>
															<c:if test="${charges == 0}">
																style="display:none;margin-top:20px;"
																</c:if> >Save & Proceed to Payment
														</button>
														
														 --%>
														</div>
														
													</div>
												
													</div>
												</div>
												
												</fieldset>
												</form:form>
													
											
										</div>
              								
              						</div>
              				</div>
              		
                            
					</div>
            </div>
        </div>
   <%-- <div class="modal fade" id="previewCertificate" role="dialog">
    <div class="modal-dialog">
    
      <!-- Modal content-->
      <div class="modal-content">
         <div class="modal-header">
          <button type="button" class="close" data-bs-dismiss="modal">&times;</button>
          <h4 class="modal-title">Certificate Preview</h4>
        </div>
        <div class="modal-body">
        <h5 align="center" class="text-danger">This is how your name would reflect on the certificate, in case of changes kindly email us at ngasce@nmims.edu</h5>
          <h4 class="mb-3" align="center">Certified that</h4>
          
           
           
          <h1 class="fw-bold mb-3" align="center">${fn:toUpperCase(student.firstName)} ${fn:toUpperCase(student.lastName)} </h1>
          <h1 class="fw-bold mb-3" align="center">${fn:toUpperCase(student.firstName)} ${fn:toUpperCase(student.lastName)}</h1>
         
          <h4 align="center" id="description"></h4>
          <br>
          <div style="text-align: center;">
           <h2 style="float: inherit;" >${serviceRequest.certificationType} </h2>
          </div>
         
          		<div class="form-group" id="verifyInformation" >    
					<label for="previewConfirmation">
						<input type="checkbox" path="wantAtAddress" value="Yes" id="previewConfirmation"
						style="width:15px;height:15px;margin:5px;"/><span> &nbsp; I accept the above information is true and complete to the best of my knowledge</span>
					</label> 
					
					<div class="alert alert-warning">  
						    For Final Certificate issuance service request for Final Certificate need to be separately raised after approval of Exit Program.
					</div>  
				</div> 
				  
        </div> 
       
      </div>  
      
    </div>
  </div>    --%>
  <div class="modal fade" id="previewCertificate" role="dialog">
    <div class="modal-dialog">
    
      <!-- Modal content-->
      <div class="modal-content">
     <div class="modal-header">
     <button type="button" class="btn-close float-right" data-bs-dismiss="modal" aria-label="Close"></button></div>
        <div class="modal-body overflow-hidden">
        <h6 align="center" class="text-danger" >This is how your name would reflect on the certificate, in case of changes kindly email us at ngasce@nmims.edu</h6>
          <h4 align="center">Certified that</h4>
          <br></br>
          <h1 class="fw-bold" align="center">${fn:toUpperCase(student.firstName)} ${fn:toUpperCase(student.lastName)}</h1><br>
          <c:choose>
          <c:when test="${student.gender=='Male'}" >
          <h4 align="center" id="description">(Son of Shri. ${student.fatherName} and Smt. ${student.motherName})</h4>
          </c:when>
          <c:otherwise>
          <h4 align="center" id="description">(Daughter of Shri. ${student.fatherName} and Smt. ${student.motherName})</h4>
          </c:otherwise>
           </c:choose>
          <h4 align="center" id="description"></h4>
          <br></br>
          
          		<div class="form-group" id="verifyInformation" >    
					<label>
						<input type="checkbox" path="wantAtAddress" value="Yes" id="previewConfirmation"
						/><span> &nbsp; I accept the above information is true and complete to the best of my knowledge</span>
					</label> 
					<div class="alert alert-warning">  
						    For Final Certificate issuance service request for Final Certificate need to be separately raised after approval of Exit Program.
					</div>  
				</div>   
        </div> 
       
      </div>  
      
    </div>
  	</div> 
        <jsp:include page="../common/footerDemo.jsp"/>
       <%--  <%@ include file="./paymentOption.jsp"%>  --%>
        <script>
       
       
    	        var isCertificate = '${serviceRequest.isCertificate}';
                var fathername = '${student.fatherName}' ;
                var mothername = '${student.motherName}' ;
                
                var spouseName = '${student.husbandName}';
                if (spouseName.trim().length > 0) {
                	spouseName='${student.husbandName}';
                } 
                else{
                    spouseName=null
                    }
           var salutationArray = [ "(Son of Shri."  + '${student.fatherName}' + " and Smt."+ '${student.motherName}' +  ")", "(Daughter of Shri." + '${student.fatherName}' + " and Smt."+ '${student.motherName}'+ ")" , "(Wife of Shri." + '${student.husbandName}' + " and Daughter of Smt. " + '${student.motherName}' +")"];  
            var gender =  '${student.gender}'; 
            var previewDescription = (gender =="Male") ? salutationArray[0]: salutationArray[1];


            
            if(fathername=="null" ||  mothername=="null"  ){
            	$(".preview").css('display','none');
            }
            $("#description").text(previewDescription);
            
            $('#nameOnCertifcateIdYes').change(function() {
            	if (this.checked) {
            		var myModal = document.getElementById('previewCertificate');
            		var modal = new bootstrap.Modal(myModal) ;
            		modal.show();
            		/* $('#previewCertificate').modal('show');   */
            		$(this).val("Y");
            		//$("#nameOnCertificateDocId").css('display','block');
                    //$("#nameOnCertificateDoc").attr('required',true);
                    previewDescription = salutationArray[2];
                    $("#description").text(previewDescription);
                    console.log('Want cert in husbands name'+salutationArray[2]);
                } 
                if (!this.checked){ 
                	$(this).val("Parent");
                	//$("#nameOnCertificateDocId").css('display','none'); 
               	 //$("#nameOnCertificateDoc").removeAttr('required');
               	 if(gender == 'Male'){
               		 previewDescription = salutationArray[0];
               		 $("#description").text(previewDescription);
               	 }else{
               		 previewDescription = salutationArray[1];
               		 $("#description").text(previewDescription);
               	 }
               	
               	console.log('No');
                 }
                
            });  
            $('#addressConfirmation').click(function(){
                var totalCost=0;
                if (this.checked) {
                	$('.submitform').css('display', 'none');
                	$('#saveWithPaymentButton').css('display', 'block');
                    $('#postalAddress').css('display', 'block');
                    
                    $('#marksheetAddressNotice').css('display', 'none');
                    
                    $('#addressDiv').css('display', 'block');
                    $('#postalAddress').removeAttr('disabled');
                    totalCost =100;
                    if(isCertificate == true){
            			totalCost = totalCost + parseFloat(0.18 * totalCost);
            		} 
                    $("#amount").val(totalCost);
                    $("#chargesSpan").text(totalCost);
                    
                    
                    alert('Total Amount Payable = ' + $("#amount").val());
                }else{
                	$('.submitform').css('display', 'block');
                	$('#saveWithPaymentButton').css('display', 'none');
                	$('#addressDiv').css('display', 'none');
                	$('#postalAddress').css('display', 'none');
                	$('#postalAddress').attr('disabled', 'true');
                	
                	$('#marksheetAddressNotice').css('display', 'block');
                	totalCost = 0 ;
                    if(isCertificate == true){
            			totalCost = totalCost + parseFloat(0.18 * totalCost);
            		}
                	$("#amount").val(totalCost);
                	$("#chargesSpan").text(totalCost);
                }
            });
    		$(document).ready(function() {
    			$(".form-select").change(function ()  {
     			 	var selectedReason = $(this).val();   
     				console.log("Reason ---------->>>"+selectedReason); 
    				if(selectedReason=="Other"){     
    					 $('.otherReasonDiv').css('display', 'block'); 
    					 $(".otherReason").val("");    
    				 }else{  
    					$('.otherReasonDiv').css('display', 'none');  
    					$(".otherReason").val(selectedReason);   
    				} 
    			});
    			$('.submitform').click(
    					function () {
    						var reason= $("#otherReason").val();
    						if(reason.length<5){
    							alert("please provide a reason for withdrawal");
    							return false;      
    						}
    						if(fathername=="null" ||  mothername=="null"  ){
    							alert("Please update your Parent's Name");  
    							 return false;
    				        }
    						 var nameOnCertifcateIdYes = $('#nameOnCertifcateIdYes').prop('checked');
    						 var previewConfirmation = $('#previewConfirmation').prop('checked');
    						 try{
    						 
    						 if(nameOnCertifcateIdYes == true &&  spouseName){
    							 alert("Spouse Name is not Saved Kindly update Profile");  
    							 return false;
    							 }
    						 }
    						 catch(err){
    							 }
    						 if(previewConfirmation == false){
    							 alert("Kindly verify the certificate preview and accept the information is true");   
    							 return false;
    						 } 
    						 
    						if(!$('#confirmation').is(":checked")){
    							alert("please check confirmation "); 
    							return false;
    						}
    						ChangeFormAction("saveExitProgram");
    					}
    			);
    			$('#saveWithPaymentButton').click(function(){
    				var reason= $(".otherReason").val();
    				if(reason.length<5){
    					alert("please provide a reason for withdrawal");
    					return false;      
    				}
    				if(fathername=="null" ||  mothername=="null"  ){
    					alert("Please update your Parent's Name");  
    					 return false;
    		        }
    				var nameOnCertifcateIdYes = $('#nameOnCertifcateIdYes').prop('checked');
    				 var previewConfirmation = $('#previewConfirmation').prop('checked');
    				 
    				
    				 if(nameOnCertifcateIdYes == true && (spouseName === null || spouseName === "") ){
    					 alert("Spouse Name is not Saved Kindly update Profile");  
    					 return false;
    				 }
    				 if(previewConfirmation == false){
    					 alert("Kindly verify the certificate preview and accept the information is true");   
    					 return false;
    				 }
    				if(!$('#confirmation').is(":checked")){
    					alert("please check confirmation "); 
    					return false;
    				}
    				if (!confirm('Are you sure you want to submit this Service Request?')) return false;
    				$.ajax({
    					url : '/studentportal/CheckServiceRequestCount?sapId=${ usersapId }&requestType=Exit Program&isFreeRequest=false',
    					type:'POST',
    					success:function(response){
    						console.log('success response : ' + response.status);
    						if(response.status == "500"){
    							alert(response.result);
    							return false;
    						}else if(response.status == "200"){
    							console.log("inside status 200");
    							 $("#paymentGatewayOptions").modal();
    						}else{
    							alert("Alert! Application error found,Refresh your page");
    							return false;
    						}
    					},
    					error:function(error){
    						alert("alert system error found, refresh page");	
    					}
    				});
    				return false;
    			});
    			
    		});
    		$(document).on('click','.selectPaymentGateway',function(){
    			$("#paymentOption").attr("value",$(this).attr('data-paymentGateway'));
    			ChangeFormAction("saveExitProgramAndPayment");
    		});
    		
    		function ChangeFormAction(action){
    			$('#form1').prop('action',action); 
    			$('#form1').submit();
    		}</script>  
    </body>
</html>