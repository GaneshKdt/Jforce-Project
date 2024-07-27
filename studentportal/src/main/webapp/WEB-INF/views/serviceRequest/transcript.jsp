
<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%
boolean isCertificate = (Boolean)session.getAttribute("isCertificate");
%>


<html lang="en">


<jsp:include page="../common/jscss.jsp">
	<jsp:param value="Enter Service Request Information" name="title" />
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

	<%@ include file="../common/header.jsp"%>



	<div class="sz-main-content-wrapper">

		<%@ include file="../common/breadcrum.jsp"%>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../common/left-sidebar.jsp">
					<jsp:param value="Service Request" name="activeMenu" />
				</jsp:include>


				<%try{ %>
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

							<form:form id="form1" action="saveTranscript" method="post"
								modelAttribute="sr">
								<fieldset>
									<!-- payment gateway option  -->
									<input type="hidden" id="paymentOption" name="paymentOption"
										value="paytm" />
									<div class="row">
										<div class="col-md-12 column">

											<div class="form-group">
												<form:label path="serviceRequestType"
													for="serviceRequestType">Service Request Type:</form:label>
												<p>${sr.serviceRequestType }</p>
												<form:hidden path="serviceRequestType" />
											</div>
											<div class="form-group">
												<label>Charges: INR. <span id="chargesSpan">${charges}</span>/-
													(for 3 Transcript and 300 /- for any Additional Transcript)
												</label>
												<form:hidden path="amount" id="amount" value="${charges}" />
											</div>

											<div class="form-group">
												<form:select id="noOfTranscript" path="noOfCopies"
													class="form-control">
													<form:option value="">Select Additional Transcript</form:option>
													<form:option value="1">1</form:option>
													<form:option value="2">2</form:option>
													<form:option value="3">3</form:option>
													<form:option value="4">4</form:option>
													<form:option value="5">5</form:option>
													<form:option value="6">6</form:option>
													<form:option value="7">7</form:option>
													<form:option value="8">8</form:option>
													<form:option value="9">9</form:option>
													<form:option value="10">10</form:option>
													<form:option value="11">11</form:option>
													<form:option value="12">12</form:option>
												</form:select>
											</div>
											<div class="form-group">
												<label> <form:checkbox path="wantAtAddress"
														value="Yes" id="addressConfirmation"
														style="width:15px;height:15px;margin:5px;" /><span>
														&nbsp;I want Transcript at my address (Shipping Charges
														INR. 100/-)</span>
												</label>
											</div>
											<div class="form-group">
												TOTAL COST OF TRANSCRIPTS :-
												<div id="showValue" style="color: red; size: 20px;"></div>
											</div>

											<div class="form-group" id="shippingAddressDiv"
												style="display: none">
												<%-- <label for="shippingAddress">Confirm/Edit Address</label>
												<textarea name="shippingAddress" class="form-control"
													id="shippingAddress" cols="50" rows="5">${student.shippingAddress}</textarea> --%>

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
												<label class="control-label" for="submit"></label> <a
													id="transcriptFeeAndShipping" name="submit"
													class="btn btn-large btn-primary"
													formaction="saveTranscriptRequest" style="display: none;">Save
													& Proceed to Payment</a> <a id="transcriptFee" name="submit"
													class="btn btn-large btn-primary"
													formaction="saveTranscriptRequest">Request Transcript</a> <a
													id="backToSR" name="BacktoNewServiceRequest"
													class="btn btn-danger" formaction="selectSRForm"
													formnovalidate="formnovalidate">Back to New
													Service Request</a> 
													<br>
													<label>Or Apply here if you want NGASCE to send your transcripts directly to the Universities outside India, thereby guaranteeing the authenticity. More details and guidelines on the page that opens as you click on the apply button below.</label>
													<br>
												<a class="btn  btn-primary" target="_blank" href="https://nmims.edu/application-for-e-transcript-delivery">Apply for WES</a>	
													
	
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
	<%}catch(Exception e){
			e.printStackTrace();}%>

</body>

<%-- <script type="text/javascript">
	var charges = ${charges};
	var additionalCharge =${extraChargePerCopy};
	var isCertificate = <%=isCertificate%>
	var count = 0; // check for if srudent click on checkBox
	document.getElementById("showValue").innerHTML = charges
	var totalCostToPay = charges;
	// avoid back and forth on Jsp 
	$(document).ready(function(){
		$('#addressConfirmation').prop('checked', false);
		if($("#noOfTranscript").val()!=null && $("#noOfTranscript").val()!="")
		{
			totalCostToPay = charges + (additionalCharge*$("#noOfTranscript").val());
			if(isCertificate == true){
				totalCostToPay = totalCostToPay + parseFloat(0.18 * totalCostToPay);
			}
		}
		document.getElementById("amount").value = totalCostToPay;
		document.getElementById("showValue").innerHTML = totalCostToPay
	});
	
	
	$("#addressConfirmation").click(function(){
		var totalCost;
		if(this.checked){
		count = 1;
		
	    document.getElementById("addressConfirmation").value="Yes";
		$('#shippingAddressDiv').css('display', 'block');
		if($("#noOfTranscript").val()!=null && $("#noOfTranscript").val()!="")
		{
			totalCost = charges +100+(additionalCharge*$("#noOfTranscript").val());
			if(isCertificate == true){
				totalCost = totalCost + parseFloat(0.18 * totalCost);
			}
		}else{
			totalCost = charges +100;
		}
		document.getElementById("amount").value = totalCost;
		$("#transcriptFeeAndShipping").css('display','block');
		$("#transcriptFee").css('display','none');
        
		}else{
				$('#shippingAddressDiv').css('display', 'none');
				$("#transcriptFeeAndShipping").css('display','none');
				$("#transcriptFee").css('display','block');
				document.getElementById("addressConfirmation").value="";
				if($("#noOfTranscript").val()!=null && $("#noOfTranscript").val()!="")
				{	
					totalCost = charges +(additionalCharge*$("#noOfTranscript").val());
					if(isCertificate == true){
						totalCost = totalCost + parseFloat(0.18 * totalCost);
					}
				}else{
					totalCost = charges;
				}
		}
		document.getElementById("amount").value = totalCost;
		document.getElementById("showValue").innerHTML = totalCost;

	});

	$("#noOfTranscript").change(function(){
		var totalCost;
	if($("#noOfTranscript").val()!=null && $("#noOfTranscript").val()!="")
	{

		if($("#addressConfirmation").val() =="Yes"  && count == 1)
		{
			totalCost = charges +100 +(additionalCharge*$("#noOfTranscript").val()); // if Student ask for More than 3 Transcript than charge 300/- per copy
			if(isCertificate == true){
				totalCost = totalCost + parseFloat(0.18 * totalCost);
			}
		}
		else{
			totalCost = charges +(additionalCharge*$("#noOfTranscript").val()); // if Student ask for More than 3 Transcript than charge 300/- per copy
			if(isCertificate == true ){
				totalCost = totalCost + parseFloat(0.18 * totalCost);
			}
		}
		document.getElementById("amount").value = totalCost;
	}
	else
	{
		if($("#addressConfirmation").val() =="Yes")
		{
			totalCost = charges +100;
			
			if(isCertificate == true){
				totalCost = totalCost + parseFloat(0.18 * totalCost);
			}
			document.getElementById("amount").value = totalCost +100; // if Student ask for More than 3 Transcript than charge 300/- per copy
		}
		else{
			totalCost = charges; 	
			
			if(isCertificate == true){
				totalCost = totalCost + parseFloat(0.18 * totalCost);
			}
			document.getElementById("amount").value = totalCost;
		}
	}
	   document.getElementById("showValue").innerHTML = totalCost;
	});
	</script> --%>
<script type="text/javascript">
	var charges = ${charges};
	var additionalCharge =${extraChargePerCopy};
	var isCertificate = <%=isCertificate%>
	var count = 0; // check for if srudent click on checkBox
	document.getElementById("showValue").innerHTML = charges
	var totalCostToPay = charges;
	// avoid back and forth on Jsp 
	
	function ChangeFormAction(action){
		$('#form1').attr('action',action);
		$('#form1').submit();
	}
	
	$(document).ready(function(){
		//alert("called 1");
		
		$("#backToSR").click(function(){
			ChangeFormAction('selectSRForm');
		});
		
		$("#transcriptFee").click(function(){
			if (!confirm('Are you sure you want to submit this Service Request?')) return false;
			 $("#paymentGatewayOptions").modal();
		});
		
		$('#transcriptFeeAndShipping').click(function(){
			if (!confirm('Are you sure you want to submit this Service Request?')) return false;
			 $("#paymentGatewayOptions").modal();
		});
		
		$(document).on('click','.selectPaymentGateway',function(){
			$("#paymentOption").attr("value",$(this).attr('data-paymentGateway'));
			ChangeFormAction('saveTranscriptRequest');
		});
		
		/* $('#selectPaytm').click(function(){
			$("#paymentOption").attr("value","paytm");
			ChangeFormAction('saveTranscriptRequest');
		});
		
		$('#selectHdfc').click(function(){
			$("#paymentOption").attr("value","hdfc");
			ChangeFormAction('saveTranscriptRequest');
		});
		
		$('#selectBilldesk').click(function(){
			$("#paymentOption").attr("value","billdesk");
			ChangeFormAction('saveTranscriptRequest');
		});
		
		$('#selectPayu').click(function(){
			$("#paymentOption").attr("value","payu");
			ChangeFormAction('saveTranscriptRequest');
		}); */
		
		$('#addressConfirmation').prop('checked', false);
		if($("#noOfTranscript").val()!=null && $("#noOfTranscript").val()!="")
		{
			totalCostToPay = charges + (additionalCharge*$("#noOfTranscript").val());
			if(isCertificate == true){
				totalCostToPay = totalCostToPay + parseFloat(0.18 * totalCostToPay);
			}
		}
		document.getElementById("amount").value = totalCostToPay;
		document.getElementById("showValue").innerHTML = totalCostToPay
	});
	
	
	$("#addressConfirmation").click(function(){
		var totalCost;
		if(this.checked){
		count = 1;
		//alert("called on click");
	    document.getElementById("addressConfirmation").value="Yes";
		$('#shippingAddressDiv').css('display', 'block');
		if($("#noOfTranscript").val()!=null && $("#noOfTranscript").val()!="")
		{
			totalCost = charges +100+(additionalCharge*$("#noOfTranscript").val());
			if(isCertificate == true){
				totalCost = totalCost + parseFloat(0.18 * totalCost);
			}
		}else{
			totalCost = charges +100;
			if(isCertificate == true){
				totalCost = totalCost + parseFloat(0.18 * totalCost);
			}
		}
		document.getElementById("amount").value = totalCost;
		$("#transcriptFeeAndShipping").css('display','block');
		$("#transcriptFee").css('display','none');
        
		}else{
				$('#shippingAddressDiv').css('display', 'none');
				$("#transcriptFeeAndShipping").css('display','none');
				$("#transcriptFee").css('display','block');
				document.getElementById("addressConfirmation").value="";
				if($("#noOfTranscript").val()!=null && $("#noOfTranscript").val()!="")
				{	
					totalCost = charges +(additionalCharge*$("#noOfTranscript").val());
					if(isCertificate == true){
						totalCost = totalCost + parseFloat(0.18 * totalCost);
					}
				}else{
					totalCost = charges;
				}
		}
		document.getElementById("amount").value = totalCost;
		document.getElementById("showValue").innerHTML = totalCost;

	});

	$("#noOfTranscript").change(function(){
		var totalCost;
		//alert("called on change value of tr");
	if($("#noOfTranscript").val()!=null && $("#noOfTranscript").val()!="")
	{

		if($("#addressConfirmation").val() =="Yes"  && count == 1)
		{
			totalCost = charges +100 +(additionalCharge*$("#noOfTranscript").val()); // if Student ask for More than 3 Transcript than charge 300/- per copy
			if(isCertificate == true){
				totalCost = totalCost + parseFloat(0.18 * totalCost);
			}
		}
		else{
			totalCost = charges +(additionalCharge*$("#noOfTranscript").val()); // if Student ask for More than 3 Transcript than charge 300/- per copy
			if(isCertificate == true ){
				totalCost = totalCost + parseFloat(0.18 * totalCost);
			}
		}
		document.getElementById("amount").value = totalCost;
	}
	else
	{
		if($("#addressConfirmation").val() =="Yes" && ($("#noOfTranscript").val()== null || $("#noOfTranscript").val()==""))
		{
			totalCost = charges +100;
			
			if(isCertificate == true){
				totalCost = totalCost + parseFloat(0.18 * totalCost);
			}
			document.getElementById("amount").value = totalCost; // if Student ask for More than 3 Transcript than charge 300/- per copy
		}
		else{
			if($("#addressConfirmation").val() ==""  && ($("#noOfTranscript").val()== null || $("#noOfTranscript").val()=="")){
			totalCost = charges; 	
		
			document.getElementById("amount").value = totalCost;
			}
		}
	}
	   document.getElementById("showValue").innerHTML = totalCost;
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
</html>
