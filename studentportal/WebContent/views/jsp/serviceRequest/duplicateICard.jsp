<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 


<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="../jscss.jsp">
<jsp:param value="Enter Service Request Information"  name="title" />
</jsp:include>

<%

StudentBean student = (StudentBean)session.getAttribute("student_studentportal");
String pgrmStructure = student.getPrgmStructApplicable();

%>

<body class="inside">

<%@ include file="../header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
         <div class="row"><legend>Enter Service Request Information</legend></div>
        <form:form  action="saveDuplicateStudyKit" method="post" modelAttribute="sr" enctype="multipart/form-data">
			<fieldset>
			
       
        <%@ include file="../messages.jsp"%>
		<div class="panel-body">
			<div>
			Dear Student, You have chosen below Service Request. Please fill in required information below before proceeding for Payment. 
			</div>
			<br>
			
			<div class="col-md-6 column">
			
				
				<div class="form-group">
					<form:label path="serviceRequestType" for="serviceRequestType">Service Request Type:</form:label>
					${sr.serviceRequestType }
					<form:hidden path="serviceRequestType"/>
				</div>
				
				<div class="form-group">
					<label>Charges:</label>
					INR. 200/-
					<form:hidden path="amount" value="200"/>
				</div>
				
				
				<%
				System.out.println("Program Structure = "+pgrmStructure);
				if("Jul2009".equalsIgnoreCase(pgrmStructure)){
				%>
					<div class="form-group">
						<label>Please Upload Passport Size Photograph:</label>
						<input type="file" name="photoGraph" required="required" class="form-control">
					</div>
				<%} %>
						
				<div class="form-group">
					<label class="control-label" for="submit"></label>
					<div class="controls">
						<button id="submit" name="submit"
							class="btn btn-large btn-primary" formaction="saveDuplicateICard" onClick="return confirm('Are you sure you want to proceed to Payment?');">Save & Proceed to Payment</button>
						<button id="cancel" name="cancel" class="btn btn-danger"
							formaction="home" formnovalidate="formnovalidate">Cancel</button>
					</div>
				</div>
				
					
				</div>
				
				
							
				
		</div>
		</fieldset>
		</form:form>
	</div>
	
</section>
	
<jsp:include page="../footer.jsp" />

</body>
</html>
 --%>
<!DOCTYPE html>

<html lang="en">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="../common/jscss.jsp">
	<jsp:param value="Enter Service Request Information" name="title" />
</jsp:include>

<%
StudentStudentPortalBean student = (StudentStudentPortalBean)session.getAttribute("student_studentportal");
String pgrmStructure = student.getPrgmStructApplicable();
boolean isCertificate = (Boolean)session.getAttribute("isCertificate");
%>


<body>

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

							<form:form id="form1" action="saveDuplicateStudyKit"
								method="post" modelAttribute="sr" enctype="multipart/form-data">
								<form:hidden path="modeOfDispatch" id="modeOfDispatch" />
								<fieldset>

									<!-- payment gateway option  -->
									<input type="hidden" id="paymentOption" name="paymentOption"
										value="paytm" />
									<div class="col-md-6 column">


										<div class="form-group">
											<form:label path="serviceRequestType"
												for="serviceRequestType">Service Request Type:</form:label>
											<p>${sr.serviceRequestType}</p>
											<form:hidden path="serviceRequestType" />
										</div>

										<div class="form-group">
											<label>Charges:</label>
											<div id="showValue" style="color: red; size: 20px;"></div>
											<form:hidden id="amount" path="amount" value="200" />
										</div>
										<div class="form-group">
											<label> <form:checkbox path="wantAtAddress"
													value="Yes" id="addressConfirmation"
													style="width:15px;height:15px;margin:5px;position: absolute; left: -2px;opacity: 10" /><span>
													&nbsp;I want Duplicate ICard at my address (Shipping
													Charges INR. 100/-)</span>
											</label>
										</div>
										<div class="form-group" id="shippingAddressDiv"
											style="display: none">
											<%-- <label for="shippingAddress">Confirm/Edit Address</label>
											<textarea name="shippingAddress" class="form-control"
												id="shippingAddress" cols="50" rows="5">${student.address}</textarea> --%>


											<h5 class="red text-capitalize">SHIPPING ADDRESS</h5>
											<div class="clearfix"></div>
											<div class="form-group">
												<form:label for="houseNoName" path="houseNoName"> (*) Address Line 1 : House Details</form:label>
												<form:input type="text" path="houseNoName"
													class="form-control shippingFields" id="houseNameId"
													name="shippingHouseName" value="${student.houseNoName}"
													required="required" />
											</div>
											<div class="form-group">
												<form:label for="street" path="street"> (*) Address Line 2 : Street Name</form:label>
												<form:input type="text" path="street"
													class="form-control shippingFields" id="shippingStreetId"
													name="shippingStreet" value="${student.street}"
													required="required" />
											</div>
											<div class="form-group">
												<form:label for="locality" path="locality"> (*) Address Line 3 : Locality Name</form:label>
												<form:input type="text" path="locality"
													class="form-control shippingFields" id="localityNameId"
													name="shippingLocalityName" value="${student.locality}"
													required="required" />
											</div>
											<div class="form-group">
												<form:label for="landMark" path="landMark"> (*) Address Line 4 : Nearest LandMark</form:label>
												<form:input type="text" path="landMark"
													class="form-control shippingFields" id="nearestLandMarkId"
													name="shippingNearestLandmark" value="${student.landMark}"
													required="required" />
											</div>
											<div class="form-group">
												<form:label for="pin" path="pin"> (*) Postal Code</form:label>
												<form:input type="text"
													class="form-control shippingFields numonly"
													id="postalCodeId" name="shippingPostalCode"
													value="${student.pin}" maxlength="6" path="pin"
													required="required" />
											</div>
											<br> <span class="well-sm" id="pinCodeMessage"></span>
											<div class="form-group">
												<form:label for="shippingCityId" path="city"> (*) Shipping City</form:label>
												<form:input type="text" class="form-control shippingFields"
													id="shippingCityId" name="shippingCity" path="city"
													value="${student.city}" readonly="true"
													onkeypress="return onlyAlphabets(event,this);" />
											</div>
											<div class="form-group">
												<form:label for="stateId" path="state"> (*) Shipping State</form:label>
												<form:input type="text" path="state"
													class="form-control shippingFields" id="stateId"
													name="shippingState" value="${student.state}"
													readonly="true"
													onkeypress="return onlyAlphabets(event,this);" />
											</div>
											<div class="form-group">
												<form:label for="countryId" path="country"> (*) Country For Shipping</form:label>
												<form:input type="text" path="country"
													class="form-control shippingFields" id="countryId"
													name="shippingCountry" value="${student.country}"
													readonly="true"
													onkeypress="return onlyAlphabets(event,this);" />
											</div>


										</div>
										<div class="form-group">
											TOTAL COST :-
											<div id="showTotalValue" style="color: red; size: 20px;"></div>
										</div>

										<%
											System.out.println("Program Structure = "+pgrmStructure);
																				if("Jul2009".equalsIgnoreCase(pgrmStructure)){
										%>
										<div class="form-group">
											<label>Please Upload Passport Size Photograph:</label> <input
												type="file" name="photoGraph" required="required"
												class="form-control">
										</div>
										<%
											}
										%>



										<div class="form-group">
											<label class="control-label" for="submit"></label>
											<div class="controls">
												<a id="submit" name="submit"
													class="btn btn-large btn-primary"
													formaction="saveDuplicateICard">Save & Proceed to
													Payment</a> <a id="backToSR" name="BacktoNewServiceRequest"
													class="btn btn-danger" formaction="selectSRForm"
													formnovalidate="formnovalidate">Back to New Service
													Request</a>
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


	<jsp:include page="../common/footer.jsp" />
	<%@ include file="./paymentOption.jsp"%>
	<script type="text/javascript">
		document.getElementById("showValue").innerHTML = document.getElementById("amount").value ;
		document.getElementById("showTotalValue").innerHTML = document.getElementById("amount").value;
		
		var isCertificate = <%=isCertificate%>
		
		// avoid back and forth on Jsp 
		$(document).ready(function(){
			$('#addressConfirmation').prop('checked', false);
			document.getElementById("amount").value = 200;
			document.getElementById("showValue").innerHTML = document.getElementById("amount").value ;
			document.getElementById("showTotalValue").innerHTML = document.getElementById("amount").value;
			
			
			function ChangeFormAction(action){
				$('#form1').attr('action',action);
				$('#form1').submit();
			}
			$("#backToSR").click(function(){
				ChangeFormAction('selectSRForm');
			});
			
			$("#submit").click(function(){
				if (!confirm('Are you sure you want to submit this Service Request?')) return false;
				 $("#paymentGatewayOptions").modal();
			});
			
			
			$(document).on('click','.selectPaymentGateway',function(){
				$("#paymentOption").attr("value",$(this).attr('data-paymentGateway'));
				ChangeFormAction('saveDuplicateICard');
			});
			
			/* $(document).on('click','#selectPaytm',function(){
				$("#paymentOption").attr("value","paytm");
				ChangeFormAction('saveDuplicateICard');
			});
			
			$(document).on('click','#selectHdfc',function(){
				$("#paymentOption").attr("value","hdfc");
				ChangeFormAction('saveDuplicateICard');
			});
			
			$(document).on('click','#selectBilldesk',function(){
				$("#paymentOption").attr("value","billdesk");
				ChangeFormAction('saveDuplicateICard');
			});
			
			$(document).on('click','#selectPayu',function(){
				$("#paymentOption").attr("value","payu");
				ChangeFormAction('saveDuplicateICard');
			}); */
		});
		
		$("#addressConfirmation")
				.click(
						function() {
							var totalCost = 200;
							if (this.checked) {

								if ($("#addressConfirmation").val() == "Yes") {
									totalCost = totalCost + 100;
									if(isCertificate == true){
										totalCost = totalCost + parseFloat(0.18 * totalCost);
									}
									document.getElementById("amount").value = totalCost;
									$("#shippingAddressDiv").css("display","block");
									$("#modeOfDispatch").val("Courier");
								} else {
									$("#shippingAddressDiv").css("display","none");
									$("#modeOfDispatch").val("LC");
									document.getElementById("amount").value = totalCost;
								}
							}else {
								$("#shippingAddressDiv").css("display","none");
								$("#modeOfDispatch").val("LC");
								document.getElementById("amount").value = totalCost;
							}
							
							console.log(document
									.getElementById("amount").value);
							document.getElementById("showValue").innerHTML = document
									.getElementById("amount").value;
							document.getElementById("showTotalValue").innerHTML = document
							.getElementById("amount").value;
						});
		
		
	  	
    	//Code for auto fill address on change of pincode start
    	 <c:if test="true"> 
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
    	</c:if> 
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
 
    </script>
	</script>
</body>
</html>