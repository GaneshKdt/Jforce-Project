<!DOCTYPE html>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html lang="en">


<jsp:include page="../common/jscssNew.jsp">
	<jsp:param value="Marksheet Confirmation" name="title" />
</jsp:include><!-- 
<style>
td {
	padding: 10px;
}

.selectCheckBox {
	width: 30px; /*Desired width*/
	height: 30px; /*Desired height*/
}

.red {
	color: red;
	font-size: 14px;
}

[type="checkbox"]:not (:checked ), [type="checkbox"]:checked {
	position: relative;
	left: 0px;
	opacity: 1;
}
</style>
 -->


<body>
	<%try{ %>
	<%@ include file="../common/headerDemo.jsp"%>
	<div class="sz-main-content-wrapper">
		<%@ include file="../common/breadcrum.jsp"%>
		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
			<div id="sticky-sidebar"> 
				<jsp:include page="../common/left-sidebar.jsp">
					<jsp:param value="Service Request" name="activeMenu" />
				</jsp:include>
				</div>
				<div class="sz-content-wrapper examsPage">
					<%@ include file="../common/studentInfoBar.jsp"%>
					<div class="sz-content">
						<h2 class="text-danger text-capitalize">${sr.serviceRequestType }</h2>
						<div class="clearfix"></div>
						<div class="card card-body">
							<%@ include file="../common/messageDemo.jsp"%>

							<p>Dear Student, You have chosen below Service Request.
								Please fill in required information below before proceeding for
								Payment.</p>
							<form:form id="form1" action="saveSingleBook" method="post"
								modelAttribute="sr" enctype="multipart/form-data">
								<fieldset>

									<!-- payment gateway option  -->
									<!-- <input type="hidden" id="paymentOption" name="paymentOption"
										value="paytm" /> -->
									<div class="row">
										<div class="col-md-8">

											<div class="form-group">
												<form:label class="fw-bold mb-3" path="serviceRequestType"
													for="serviceRequestType">Service Request Type:</form:label>
												${sr.serviceRequestType }
												<form:hidden path="serviceRequestType" />
											</div>

											<div class="form-group mb-3">
												<label class="fw-bold"> Charges: INR. <span id="chargesSpan">${charges}</span>/-
												</label>
												<form:hidden path="amount" id="amount" value="${charges}" />
											</div>
									
											<p class="fw-bold mb-3">
												Note: First marksheet request for given Exam
													Year-Month-Semester is free. Any subsequent requests for
													same marksheet will be considered as a duplicate request
													and Service Request fees of INR. 500/- will be charged for
													the same
											</p>
											<p class="fw-bold mb-3">
												Note: If you are issuing duplicate marhsheet due to
													any other reason, please submit a copy of Indeminity bond
													and FIR for the same
											</p>
											<form:hidden path="year" value="${sr.year}" />
											<form:hidden path="month" value="${sr.month}" />
											<form:hidden path="sem" value="${sr.sem}" />
											<c:if test="${duplicateMarksheet == 'true'}">
												<div class="form-group mb-3">
													<label class="fw-bold">Please Upload Copy of FIR/Upload
														Application:</label> <input type="file" name="firCopy"
														required="required" class="form-control">
												</div>

												<div class="form-group">
													<label class="fw-bold">Please Upload Copy of Indemnity Bond as per
														format shown here:</label> <input type="file" name="indemnityBond"
														required="required" class="form-control">
												</div>

												<a
													href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />resources_2015/notices/Proforma_of_Indemnity_Bond.docx"
													target="_blank">Download Proforma of Indemnity Bond</a>
											</c:if>
											<div class="form-group" id="marksheetAddressNotice">
												Marksheet will be sent to NMIMS Learning Center (Not to
												Information Center) (No Shipping Charges)</div>


											<div class="form-group">
												<label class="control-label" for="submit"></label> <a
													href="javascript:void(0)" id="saveWithPaymentButton"
													name="submit" class="btn btn-danger"
													formaction="saveMarksheetAndPayment"
													<c:if test="${charges != 0.0}">
															style="display:inline;"
														</c:if>
													<c:if test="${charges == 0.0}">
															style="display:none;"
														</c:if>>Save
													& Proceed to Payment</a> <a href="javascript:void(0)"
													id="saveForFreeButton" name="submit"
													class="btn btn-danger" formaction="saveMarksheetRequest"
													<c:if test="${charges == 0.0}">
															style="display:inline;"
														</c:if>
													<c:if test="${charges != 0.0}">
															style="display:none;"
														</c:if>>Request
													Marksheet</a> <a href="javascript:void(0)" id="cancel"
													name="cancel" class="btn btn-dark" formaction="${pageContext.request.contextPath}/home"
													formnovalidate="formnovalidate">Cancel</a>

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

	<jsp:include page="../common/footerDemo.jsp" />
	<%-- <%@ include file="./paymentOption.jsp"%> --%>
	<% }catch(Exception e) {}%>
</body>
<!-- 	<script type="text/javascript">
						var charges = ${charges};
						
						$('#addressConfirmation').click(function(){
						    if (this.checked) {
						        $('#postalAddress').css('display', 'block');
						        
						        $('#saveWithPaymentButton').css('display', 'block');
						        $('#saveForFreeButton').css('display', 'none');
						        $('#marksheetAddressNotice').css('display', 'none');
						        
						        $('#addressDiv').css('display', 'block');
						        $('#postalAddress').removeAttr('disabled');
						        $("#amount").val(charges + 100);
						        $("#chargesSpan").text(charges + 100);
						        
						        
						        alert('Total Amount Payable = ' + $("#amount").val());
						    }else{
						    	$('#addressDiv').css('display', 'none');
						    	$('#postalAddress').css('display', 'none');
						    	$('#postalAddress').attr('disabled', 'true');
						    	
						    	$('#marksheetAddressNotice').css('display', 'block');
						    	
						    	$("#amount").val(charges);
						    	$("#chargesSpan").text(charges);
						    	
						    	if(charges == 0){
						    		$('#saveWithPaymentButton').css('display', 'none');
						    		$('#saveForFreeButton').css('display', 'block');
						    		alert('Total Amount Payable = ' + $("#amount").val());
						    	}else{
						    		$('#saveForFreeButton').css('display', 'none');
						    		alert('Total Amount Payable = ' + $("#amount").val());
						    	}
						    }
						})
						</script> -->

<script type="text/javascript">
							
						function ChangeFormAction(action){
							console.log("===========>>>>>> inside changeFormAction : " + action);
							$('#form1').prop('action',action);
							$('#form1').submit();
						}
						
						
						/* $(document).on('click','.selectPaymentGateway',function(){
		    				 $("#paymentOption").attr("value",$(this).attr('data-paymentGateway')); 
		    				var isDuplicate = ${duplicateMarksheet};
							 if (!confirm('Are you sure you want to request for this Marksheet? You can not make any changes after this step.')) return false;
							 if(isDuplicate == true){
								 var firCopy = $('input[type="file"][name="firCopy"]').val();
								 var indemnityBond = $('input[type="file"][name="indemnityBond"]').val();
								 if(firCopy.trim() == "" || indemnityBond.trim() == ""){
									 alert("Kindly submit required files");
									 return false;
								 }
							 }
							 $.ajax({
									url : '/studentportal/CheckServiceRequestCount?sapId=${ sapid }&requestType=${ reqest_type }&isFreeRequest=false',
									type:'POST',
									success:function(response){
										console.log('success response : ' + response.status);
										if(response.status == "500"){
											alert(response.result);
											return false;
										}else if(response.status == "200"){
											console.log("inside status 200");
											ChangeFormAction('saveMarksheetAndPayment');
										}else{
											alert("Alert! Application error found,Refresh your page");
											return false;
										}
									},
									error:function(error){
										alert("alert system error found, refresh page");	
									}
								});
		    				
		    			}); */
						
						/* $(document).on('click','#selectPaytm',function(){
							$("#paymentOption").attr("value","paytm");
							ChangeFormAction('saveMarksheetAndPayment');
						});
						
						$(document).on('click','#selectHdfc',function(){
							$("#paymentOption").attr("value","hdfc");
							ChangeFormAction('saveMarksheetAndPayment');
						});
						
						$(document).on('click','#selectBilldesk',function(){
							$("#paymentOption").attr("value","billdesk");
							ChangeFormAction('saveMarksheetAndPayment');
						});
						
						$(document).on('click','#selectPayu',function(){
							$("#paymentOption").attr("value","payu");
							ChangeFormAction('saveMarksheetAndPayment');
						}); */
						
						$('#saveWithPaymentButton').click(function(){
		    				var isDuplicate = ${duplicateMarksheet};
							 if (!confirm('Are you sure you want to request for this Marksheet? You can not make any changes after this step.')) return false;
							 if(isDuplicate == true){
								 var firCopy = $('input[type="file"][name="firCopy"]').val();
								 var indemnityBond = $('input[type="file"][name="indemnityBond"]').val();
								 if(firCopy.trim() == "" || indemnityBond.trim() == ""){
									 alert("Kindly submit required files");
									 return false;
								 }
							 }
							 $.ajax({
									url : '/studentportal/CheckServiceRequestCount?sapId=${ sapid }&requestType=${ reqest_type }&isFreeRequest=false',
									type:'POST',
									success:function(response){
										console.log('success response : ' + response.status);
										if(response.status == "500"){
											alert(response.result);
											return false;
										}else if(response.status == "200"){
											console.log("inside status 200");
											ChangeFormAction('saveMarksheetAndPayment');
										}else{
											alert("Alert! Application error found,Refresh your page");
											return false;
										}
									},
									error:function(error){
										alert("alert system error found, refresh page");	
									}
								});
						});
						
						$('#cancel').click(function(){
							ChangeFormAction('home');
						});
						
						$('#saveForFreeButton').click(function(){
							if (!confirm('Are you sure you want to request for this Marksheet? You can not make any changes after this step.')) return false;
							$.ajax({
								url : '/studentportal/CheckServiceRequestCount?sapId=${ sapid }&requestType=${ reqest_type }&isFreeRequest=true',
								type:'POST',
								success:function(response){
									console.log('success response : ' + response.status);
									if(response.status == "500"){
										alert(response.result);
										return false;
									}else if(response.status == "200"){
										console.log("inside status 200");
										ChangeFormAction('saveMarksheetRequest');
									}else{
										alert("Alert! Application error found,Refresh your page");
										return false;
									}
								},
								error:function(error){
									alert("alert system error found, refresh page");	
								}
							});
						});
							
						</script>
</html>