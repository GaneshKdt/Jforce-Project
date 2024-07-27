
<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>


<%
StudentStudentPortalBean student = (StudentStudentPortalBean)session.getAttribute("student_studentportal");
boolean isCertificate = (Boolean)session.getAttribute("isCertificate");
int numberOfCopiesIssued = (Integer)request.getAttribute("numberOfBonafiedCopiesIssued");
%>

<html lang="en">


<jsp:include page="../common/jscss.jsp">
	<jsp:param value="Enter Service Request Information" name="title" />
</jsp:include>



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

							<form:form id="form1" action="saveBonafide" method="post"
								modelAttribute="sr" enctype="multipart/form-data">
								<fieldset>

									<div class="row">
										<div class="col-md-4 column">

											<div class="form-group">
												<form:label path="serviceRequestType"
													for="serviceRequestType">Service Request Type:</form:label>
												<p>${sr.serviceRequestType }</p>
												<form:hidden path="serviceRequestType" />
											</div>
											<div class="form-group">
												<label>Charges: INR. <span id="chargesSpan">${charges}</span>/-
													<p>(for the first 5 copies the amount is 0. Additional
														copies will be charged 100/- per copy)</p></label>
												<form:hidden path="amount" id="amount" value="${charges}" />

											</div>
											<!-- payment gateway option  -->
											<input type="hidden" id="paymentOption" name="paymentOption"
												value="paytm" />
											<div class="form-group">
												<form:select id="noOfCopiesId" name="numberOfCopies"
													required="true" path="noOfCopies" class="form-control"
													onchange="assignCost(this);">
													<form:option value="0">Select Additional Copies</form:option>
													<form:option value="1">1</form:option>
													<form:option value="2">2</form:option>
													<form:option value="3">3</form:option>
													<form:option value="4">4</form:option>
													<form:option value="5">5</form:option>
													<form:option value="6">6</form:option>
													<form:option value="7">7</form:option>
													<form:option value="8">8</form:option>
												</form:select>
											</div>

											<div class="form-group" id="purposeDiv">
												<label for="purposeDiv">Reason for Bonafide</label>
												<textarea name="purpose" class="form-control"
													required="true" id="purposeForBonafideRequest" cols="50"
													rows="5"></textarea>
											</div>

											<div class="form-group">
												<label> <form:checkbox path="wantAtAddress"
														value="Yes" id="addressConfirmation"
														style="width:15px;height:15px;margin:5px;position: absolute; left: -2px;opacity: 10"
														onclick="confirmAddress(this);" /><span> &nbsp;I
														want Certificate at my address (Shipping Charges INR.
														100/-)</span>
												</label>
											</div>
											<div class="form-group">
												TOTAL COST :-
												<div id="totalCost" style="color: red; size: 20px;"></div>
											</div>

											<div class="form-group" id="shippingAddressDiv"
												style="display: none">
												<%-- <label for="shippingAddress">Confirm/Edit Address</label>
												<form:textarea path="postalAddress" class="form-control"
													id="shippingAddress" cols="50" rows="5"></form:textarea> --%>

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
													id="saveBonafideRequestAndPay" name="submit"
													class="btn btn-large btn-primary" style="display: none;">Save
													And Proceed to Payment</a> <a id="saveBonafideRequest"
													name="submit" class="btn btn-large btn-primary">Request
													For Certificate</a> <a id="backToSR"
													name="BacktoNewServiceRequest" class="btn btn-danger">Back
													to New Service Request</a>
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

<script type="text/javascript">
	var totalCost = 0;
	var isCertificate = <%=isCertificate%>
	var count = 0; //for address conformation;
	// avoid back and forth on Jsp 
	function isEmptyData(data){
				if(data.trim() == "" || data == null){
					return true;
				}
				return false;
			}
	function ChangeFormAction(action){
			$('#form1').attr('action',action);
			$('#form1').submit();
		}
	$(document).ready(function(){
		
		$("#backToSR").click(function(){
			ChangeFormAction('selectSRForm');
		});
		
		$("#saveBonafideRequest").click(function(){
			let reason = $("textarea[name='purpose']").val();
			if(!isEmptyData(reason)){
				ChangeFormAction('saveBonafideRequest');
				return false;
			}
			alert("please write reason");
		});
		
		$('#saveBonafideRequestAndPay').click(function(){
			let reason = $("textarea[name='purpose']").val();
			if(!isEmptyData(reason)){
				if (!confirm('Are you sure you want to submit this Service Request?')) return false;
				 $("#paymentGatewayOptions").modal();
				return false;
			}
			alert("please write reason");
		});
		
		
		$(document).on('click','.selectPaymentGateway',function(){
			$("#paymentOption").attr("value",$(this).attr('data-paymentGateway'));
			ChangeFormAction('saveBonafideRequestAndProceedToPay');
		});
		
		/* $(document).on('click','#selectPaytm',function(){
			$("#paymentOption").attr("value","paytm");
			ChangeFormAction('saveBonafideRequestAndProceedToPay');
		});
		
		$(document).on('click','#selectHdfc',function(){
			$("#paymentOption").attr("value","hdfc");
			ChangeFormAction('saveBonafideRequestAndProceedToPay');
		});
		
		$(document).on('click','#selectBilldesk',function(){
			$("#paymentOption").attr("value","billdesk");
			ChangeFormAction('saveBonafideRequestAndProceedToPay');
		});
		
		$(document).on('click','#selectPayu',function(){
			$("#paymentOption").attr("value","payu");
			ChangeFormAction('saveBonafideRequestAndProceedToPay');
		}); */
		
		
		$('#addressConfirmation').prop('checked', false);
		var numberOfCopiesAlreadyIssued = <%=numberOfCopiesIssued%> //Get number of copies already issued/
		var numberOfCopiesSelected = $('#noOfCopiesId').val();//Current selection of copies
		totalCost = setTotalCostBasedOnCopiesIssued(numberOfCopiesAlreadyIssued,numberOfCopiesSelected);
		if(totalCost!=0){
			
			document.getElementById("saveBonafideRequestAndPay").style.display = 'block';
		    document.getElementById("saveBonafideRequest").style.display = 'none';
		    
		}else{
			totalCost = 0;
			document.getElementById("saveBonafideRequestAndPay").style.display = 'none';
			document.getElementById("saveBonafideRequest").style.display = 'block';
		}
		if(isCertificate == true){
			totalCost = totalCost + parseFloat(0.18 * totalCost);
		}
		document.getElementById("amount").value = totalCost;
		document.getElementById("totalCost").innerHTML = totalCost;
	});
	
	function assignCost(e){
		document.getElementById("addressConfirmation").checked = false;
		
		var selectedOption = document.getElementById("noOfCopiesId");
		var numberOfCopiesAlreadyIssued = <%=numberOfCopiesIssued%> //Get number of copies already issued/
		var numberOfCopiesSelected = selectedOption.options[e.selectedIndex].value;//Current selection of copies
		
		
		totalCost = setTotalCostBasedOnCopiesIssued(numberOfCopiesAlreadyIssued,numberOfCopiesSelected);
		if(totalCost!=0){
			
			document.getElementById("saveBonafideRequestAndPay").style.display = 'block';
		    document.getElementById("saveBonafideRequest").style.display = 'none';
		}else{
			totalCost = 0;
			document.getElementById("saveBonafideRequestAndPay").style.display = 'none';
			document.getElementById("saveBonafideRequest").style.display = 'block';
		}
		if(isCertificate == true){
			totalCost = totalCost + parseFloat(0.18 * totalCost);
		}
		document.getElementById("amount").value = totalCost;
		document.getElementById("totalCost").innerHTML = totalCost;
	}
	
	function setTotalCostBasedOnCopiesIssued(numberOfCopiesAlreadyIssued,numberOfCopiesSelected){
		var totalCostToBeReturned = 0;
		var sumOfIssuedCopiesAndExistingSelection = parseInt(numberOfCopiesAlreadyIssued) + parseInt(numberOfCopiesSelected);
		//alert("numberOfCopiesAlreadyIssued-->"+numberOfCopiesAlreadyIssued+"<--numberOfCopiesSelected->"+numberOfCopiesSelected);
		if(numberOfCopiesAlreadyIssued==0 && parseInt(numberOfCopiesSelected) >5){
			totalCostToBeReturned = 100*(numberOfCopiesSelected-5);
		}else if(numberOfCopiesAlreadyIssued > 5){
			totalCostToBeReturned = 100 * numberOfCopiesSelected;
		}else if(numberOfCopiesAlreadyIssued!=0 && sumOfIssuedCopiesAndExistingSelection > 5){
			totalCostToBeReturned = 100 * (sumOfIssuedCopiesAndExistingSelection - 5);
		}
		//alert("totalCostToBeReturned-->"+totalCostToBeReturned);
		return totalCostToBeReturned;
	}
	function confirmAddress(e){
		document.getElementById("saveBonafideRequest").style.display = 'none';
		var numberOfCopiesAlreadyIssued = <%=numberOfCopiesIssued%> //Get number of copies already issued/
		var numberOfCopiesSelected = $('#noOfCopiesId').val();//Current selection of copies
		totalCost = setTotalCostBasedOnCopiesIssued(numberOfCopiesAlreadyIssued,numberOfCopiesSelected);
		if(e.checked){
			document.getElementById("shippingAddressDiv").style.display = 'block';
			document.getElementById("saveBonafideRequestAndPay").style.display = 'block';
			count = 1;
			
			if(numberOfCopiesAlreadyIssued !=0 || numberOfCopiesSelected != 0 )
			{
				totalCost = totalCost +100;
				if(isCertificate == true){
					totalCost = totalCost + parseFloat(0.18 * totalCost);
				}
			}else
			{
				totalCost = totalCost +100;
				if(isCertificate == true){
					totalCost = totalCost + parseFloat(0.18 * totalCost);
				}
			}
			document.getElementById("amount").value = totalCost;
			document.getElementById("totalCost").innerHTML = totalCost;
		}else{
			document.getElementById("shippingAddressDiv").style.display = 'none';
			console.log(numberOfCopiesSelected);
			if((numberOfCopiesAlreadyIssued !=0 || numberOfCopiesSelected != 0) && count != 1 )
			{
				totalCost = totalCost ;
			}else
			{
				if(count ==1 && numberOfCopiesSelected != 0)
				{
					totalCost = totalCost;
				}else
				{
					totalCost = 0;
				}
			}
			count = 0;
			console.log(totalCost);
			if(totalCost !=0)
			{
				document.getElementById("saveBonafideRequestAndPay").style.display = 'block';
				document.getElementById("saveBonafideRequest").style.display = 'none';
				if(isCertificate == true){
					totalCost = totalCost + parseFloat(0.18 * totalCost);
				}
			}else
			{
				document.getElementById("saveBonafideRequestAndPay").style.display = 'none';
				document.getElementById("saveBonafideRequest").style.display = 'block';
			}
			document.getElementById("amount").value = totalCost;
			document.getElementById("totalCost").innerHTML = totalCost;
		}
	}
	
	
	
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
