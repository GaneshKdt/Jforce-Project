<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 


<%@page import="org.apache.jasper.tagplugins.jstl.core.ForEach"%>
<%@page import="java.util.ArrayList"%>
<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="../jscss.jsp">
<jsp:param value="Marksheet Confirmation"  name="title" />
</jsp:include>


<body class="inside">

<%@ include file="../header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
         <div class="row"><legend>Marksheet Confirmation</legend></div>
        <form:form  action="saveSingleBook" method="post" modelAttribute="sr" enctype="multipart/form-data">
			<fieldset>
			
       
        <%@ include file="../messages.jsp"%>
		<div class="panel-body">
			<div>
			Dear Student, You have chosen below Service Request. Please confirm before proceeding. 
			</div>
			<br>
			
			<div class="col-md-12 col-xs-18 column">
			
				
				<div class="form-group">
					<form:label path="serviceRequestType" for="serviceRequestType">Service Request Type:</form:label>
					${sr.serviceRequestType }
					<form:hidden path="serviceRequestType"/>
				</div>
				
				<div class="form-group">
					<label>Charges: INR. <span id="chargesSpan">${charges}</span>/-</label>
					<form:hidden path="amount" id="amount" value="${charges}"/>
				</div>
				
				
					<div>Exam Year: ${sr.year}</div>	
					<div>Exam Month: ${sr.month}</div>	
					<div>Semester: ${sr.sem}</div>	
					
					<br/>
					<div>
					Note: First marksheet request for given Exam Year-Month-Semester is free. Any subsequent requests for same marksheet will incur Service Request fees of INR. 500/-
					</div>
					
					<br/>
					
					<form:hidden path="year" value="${sr.year}"/>
					<form:hidden path="month" value="${sr.month}"/>
					<form:hidden path="sem" value="${sr.sem}"/>
					
					<c:if test="${duplicateMarksheet == 'true'}">
						<div class="form-group">
							<label>Please Upload Copy of FIR:</label>
							<input type="file" name="firCopy" required="required">
						</div>
						
						<div class="form-group">
							<label>Please Upload Copy of Indemnity Bond as per format shown here:</label>
							<input type="file" name="indemnityBond" required="required">
						</div>
						
						<a href="resources_2015/notices/Proforma_of_Indemnity_Bond.docx" target="_blank">Download Proforma of Indemnity Bond</a>
					</c:if>
					
					<div class="form-group" id="marksheetAddressNotice">
						Marksheet will be sent to NMIMS Learning Center (Not to Information Center) (No Shipping Charges)
					</div>
					
					<div class="form-group">
						<label>
						<form:checkbox path="wantAtAddress" value="Yes" id="addressConfirmation"
						style="width:15px;height:15px;margin:5px;"/><span> &nbsp;I want Marksheet at my address (Shipping Charges INR. 100/-)</span>
						</label>
					</div>
					
					<div class="form-group" id="addressDiv" style="display:none">
						<label for="postalAddress">Confirm/Edit Address</label>
						<textarea name="postalAddress"  class="form-control" id="postalAddress" style="display:none;"  cols="50" rows = "5">${student.address}, Contact No:${student.mobile}</textarea>
					</div>
				
					<div class="form-group">
						<label class="control-label" for="submit"></label>
							<button id="saveWithPaymentButton" name="submit"
								class="btn btn-large btn-primary" formaction="saveMarksheetAndPayment" 
								<c:if test="${charges != 0}">
									style="display:block;"
								</c:if>
								<c:if test="${charges == 0}">
									style="display:none;"
								</c:if>
								
								>Save & Proceed to Payment</button>
							
							<button id="saveForFreeButton" name="submit"
								class="btn btn-large btn-primary" formaction="saveMarksheetRequest" 
								onClick="return confirm('Are you sure you want to request for this Marksheet? You can not make any changes after this step.');"
								<c:if test="${charges == 0}">
									style="display:block;"
								</c:if>
								<c:if test="${charges != 0}">
									style="display:none;"
								</c:if>
								>Request Marksheet</button>
							
							<button id="cancel" name="cancel" class="btn btn-danger"
								formaction="home" formnovalidate="formnovalidate">Cancel</button>
					</div>
				
					
			</div>
				
				
							
				
		</div>
		</fieldset>
		</form:form>
	</div>
	
</section>
	
<jsp:include page="../footer.jsp" />
<script type="text/javascript">
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
</script>
<script type="text/javascript">
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
</script>
</body>
</html>
 --%>
<!DOCTYPE html>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html lang="en">


<jsp:include page="../common/jscss.jsp">
	<jsp:param value="Marksheet Confirmation" name="title" />
</jsp:include>
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



<body>
	<%try{ %>
	<%@ include file="../common/header.jsp"%>
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
						<h2 class="red text-capitalize">${sr.serviceRequestType }</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper">
							<%@ include file="../common/messages.jsp"%>

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
												<form:label path="serviceRequestType"
													for="serviceRequestType">Service Request Type:</form:label>
												${sr.serviceRequestType }
												<form:hidden path="serviceRequestType" />
											</div>

											<div class="form-group">
												<label>Charges: INR. <span id="chargesSpan">${charges}</span>/-
												</label>
												<form:hidden path="amount" id="amount" value="${charges}" />
											</div>
											<!-- </p> -->
											<%-- <p>Exam Year: ${sr.year}</p>	
												<p>Exam Month: ${sr.month}</p>	
												<p>Semester: ${sr.sem}</p>	
												<br></br> --%>

											<p>
												<b>Note: First marksheet request for given Exam
													Year-Month-Semester is free. Any subsequent requests for
													same marksheet will be considered as a duplicate request
													and Service Request fees of INR. 500/- will be charged for
													the same</b>
											</p>
											<p>
												<b>Note: If you are issuing duplicate marhsheet due to
													any other reason, please submit a copy of Indeminity bond
													and FIR for the same</b>
											</p>
											<form:hidden path="year" value="${sr.year}" />
											<form:hidden path="month" value="${sr.month}" />
											<form:hidden path="sem" value="${sr.sem}" />
											<c:if test="${duplicateMarksheet == 'true'}">
												<div class="form-group">
													<label>Please Upload Copy of FIR/Upload
														Application:</label> <input type="file" name="firCopy"
														required="required" class="form-control">
												</div>

												<div class="form-group">
													<label>Please Upload Copy of Indemnity Bond as per
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

											<%-- <div class="form-group">
												<label>
												<form:checkbox path="wantAtAddress" value="Yes" id="addressConfirmation"
												style="width:15px;height:15px;margin:5px;"/><span> &nbsp;I want Marksheet at my address (Shipping Charges INR. 100/-)</span>
												</label>
											</div>
											
											<div class="form-group" id="addressDiv" style="display:none">
												<label for="postalAddress">Confirm/Edit Address</label>
												<textarea name="postalAddress"  class="form-control" id="postalAddress" style="display:none;"  cols="50" rows = "5">${student.address}</textarea>
											</div> --%>

											<div class="form-group">
												<label class="control-label" for="submit"></label> <a
													href="javascript:void(0)" id="saveWithPaymentButton"
													name="submit" class="btn btn-primary"
													formaction="saveMarksheetAndPayment"
													<c:if test="${charges != 0.0}">
															style="display:inline;"
														</c:if>
													<c:if test="${charges == 0.0}">
															style="display:none;"
														</c:if>>Save
													& Proceed to Payment</a> <a href="javascript:void(0)"
													id="saveForFreeButton" name="submit"
													class="btn btn-primary" formaction="saveMarksheetRequest"
													<c:if test="${charges == 0.0}">
															style="display:inline;"
														</c:if>
													<c:if test="${charges != 0.0}">
															style="display:none;"
														</c:if>>Request
													Marksheet</a> <a href="javascript:void(0)" id="cancel"
													name="cancel" class="btn btn-danger" formaction="${pageContext.request.contextPath}/home"
													formnovalidate="formnovalidate">Cancel</a>

											</div>
								</fieldset>
							</form:form>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<jsp:include page="../common/footer.jsp" />
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