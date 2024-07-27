
<!DOCTYPE html>
<%@page import="com.nmims.beans.StudentStudentPortalBean"%>
<%@page import="com.nmims.helpers.PersonStudentPortalBean"%>
<%@page import="java.util.HashMap"%>
<%
	StudentStudentPortalBean student = (StudentStudentPortalBean)request.getAttribute("student_studentportal");
	PersonStudentPortalBean person = (PersonStudentPortalBean)session.getAttribute("user_studentportal");
	HashMap<String,String> mapOfShippingAddress = (HashMap<String,String>)request.getAttribute("mapOfShippingAddress");
	String email = "";
	String mobile = "";
	String altMobile = "";
	String postalAddress = "";
	String mothersName = "";
	String fathersName = "";
	String address = "";
	String spouseName ="";
	if(person !=null){
		email = person.getEmail();
    	mobile = person.getContactNo();
    	postalAddress = person.getPostalAddress();
    	altMobile = person.getAltContactNo();
    	
  // System.out.println("person postal------------"+ person.getPostalAddress());
	}
	
   if(student != null){
   	
   	mothersName = student.getMotherName();
   	fathersName = student.getFatherName();
   	spouseName = student.getHusbandName();
   	address = student.getAddress();
   	
   //	System.out.println("student postal------------"+ student.getAddress());
	   	if("".equalsIgnoreCase(spouseName))
	   	{
	   		spouseName="NA";
	   	}
    }
%>

<html lang="en">

<jsp:include page="common/jscss.jsp">
	<jsp:param value="Update Profile" name="title" />
</jsp:include>
<script type="text/javascript">
	
	function validateForm(){
		var fatherName = document.getElementById("fatherName").value;
		var motherName = document.getElementById("motherName").value;
		fatherName = fatherName.trim();
		motherName = motherName.trim();
		if(fatherName.match(/\s/g) || motherName.match(/\s/g)){
            alert("Please enter only First Name for Parents, Full name not allowed.");
            return false;
        }
		
		return true;
		
	}
	
	</script>


<body>

	<%@ include file="common/header.jsp"%>


	<div class="sz-main-content-wrapper">

		<%@ include file="common/breadcrum.jsp"%>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<%@ include file="common/left-sidebar.jsp"%>


				<div class="sz-content-wrapper examsPage">
					<%@ include file="common/studentInfoBar.jsp"%>
					<div class="sz-content">

						<h2 class="red text-capitalize">Update Profile</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper">
							<%@ include file="common/messages.jsp"%>

							<form role="form" id="update-profile" action="save" method="post"
								onSubmit="return validateForm();">
								<div class="row">
									<div class="col-md-8">
										<%--    <input type="hidden" name="studentAddress" value="<%=address%>"> --%>
										<div class="form-group">
											<label for="email">Enter Email(*)</label> <input type="email"
												class="form-control" id="email" name="email"
												placeholder="Enter Email" value="<%=email%>"
												required="required">
										</div>

										<div class="form-group">
											<label for="mobile">Mobile No.(*)</label> <input type="text"
												class="form-control tenDigit numonly" id="mobile"
												name="mobile" placeholder="Enter Mobile No."
												value="<%=mobile%>" required="required" maxlength="12">
										</div>

										<div class="form-group">
											<label for="altMobile">Alternate Contact No.</label> <input
												type="text" class="form-control numonly" id="altMobile"
												name="altMobile" placeholder="Enter Alternate Contact No."
												value="<%=altMobile%>">
										</div>

										<div class="form-group">
											<label for="mother">Mothers Name</label> <input type="text"
												class="form-control doNotAllowedSpace" id="motherId"
												name="motherName" placeholder="Enter Mothers Name."
												value="<%=mothersName%>">*<span style="color: red">(First
												Name)</span>
										</div>
										<div class="form-group">
											<label for="father">Father Name</label> <input type="text"
												class="form-control doNotAllowedSpace" id="fatherId"
												name="fatherName" placeholder="Enter Fathers Name."
												value="<%=fathersName%>">*<span style="color: red">(First
												Name)</span>
										</div>
										<div class="form-group">
											<label for="spouse">Spouse Name</label> <input type="text"
												class="form-control doNotAllowedSpace" id="husbandId"
												name="husbandName" disabled="disabled"
												value="<%=spouseName%>">
										</div>
										<div class="form-group">
											<label for="designationID">Designation</label> <select
												id="designationID" name="designation" type="text"
												placeholder="Designation" class="form-control">
												<option value="">Please Select Designation</option>
												<c:forEach items="${designationList}" var="designationParam">
													<option value="${designationParam}"
														${student.designation == designationParam ? 'selected' : ''}>${designationParam}</option>
												</c:forEach>


											</select>
										</div>

										<div class="form-group">
											<label for="industryID">Industry</label> <select
												id="industryID" name="industry" type="text"
												placeholder="Industry" class="form-control">
												<option value="">Please Select Industry</option>
												<c:forEach items="${industryList}" var="industryParam">
													<option value="${industryParam}"
														${student.industry == industryParam ? 'selected' : ''}>${industryParam}</option>
												</c:forEach>

											</select>
										</div>
										<c:choose>
											<c:when test="${showShippingAddress eq 'Yes'}">
												<h2 class="red text-capitalize">SHIPPING ADDRESS (*)</h2>
												<div class="clearfix"></div>
												<div class="form-group">
													<label for="houseNameId">Address Line 1 : House
														Details</label> <input type="text"
														class="form-control shippingFields" id="houseNameId"
														name="shippingHouseName" value="${student.houseNoName}">
												</div>
												<div class="form-group">
													<label for="shippingStreetId">Address Line 2 :
														Street Name</label> <input type="text"
														class="form-control shippingFields" id="shippingStreetId"
														name="shippingStreet" value="${student.street}">
												</div>
												<div class="form-group">
													<label for="localityNameId">Address Line 3 :
														Locality Name</label> <input type="text"
														class="form-control shippingFields" id="localityNameId"
														name="shippingLocalityName" value="${student.locality}">
												</div>
												<div class="form-group">
													<label for="nearestLandMarkId">Address Line 4 :
														Nearest LandMark</label> <input type="text"
														class="form-control shippingFields" id="nearestLandMarkId"
														name="shippingNearestLandmark" value="${student.landMark}">
												</div>
												<div class="form-group">
													<label for="postalCodeId">Postal Code</label> <input
														type="text" class="form-control shippingFields numonly"
														id="postalCodeId" name="shippingPostalCode"
														value="${student.pin}" maxlength="6" required="required">

												</div>
												<br>
												<span class="well-sm" id="pinCodeMessage"></span>
												<div class="form-group">
													<label for="shippingCityId">Shipping City</label> <input
														type="text" class="form-control shippingFields"
														id="shippingCityId" name="shippingCity"
														value="${student.city}" readonly
														onkeypress="return onlyAlphabets(event,this);">
												</div>
												<div class="form-group">
													<label for="stateId">Shipping State</label> <input
														type="text" class="form-control shippingFields"
														id="stateId" name="shippingState" value="${student.state}"
														readonly onkeypress="return onlyAlphabets(event,this);">
												</div>
												<div class="form-group">
													<label for="countryId">Country For Shipping</label> <input
														type="text" class="form-control shippingFields"
														id="countryId" name="shippingCountry"
														value="${student.country}" readonly
														onkeypress="return onlyAlphabets(event,this);">
												</div>
											</c:when>
											<c:otherwise>
												<h2 class="red text-capitalize">SHIPPING ADDRESS (*)</h2>

												<div class="form-group">
													<textarea name="studentAddress" id="studentAddress"
														cols="50" rows="7" class="form-control">${student.address}</textarea>
												</div>
											</c:otherwise>
										</c:choose>


										<div class="form-group">
											<c:choose>
												<c:when test="${showShippingAddress eq 'Yes'}">
													<button type="submit" class="btn btn-primary"
														formaction="saveProfileForSFDCAndPortal">Update
														Information</button>
												</c:when>
												<c:otherwise>
													<button type="submit" class="btn btn-primary"
														formaction="saveProfileForPortal">Update
														Information</button>
												</c:otherwise>
											</c:choose>
											<button type="button" class="btn btn-danger"
												onclick="window.history.back();">Cancel</button>
										</div>

									</div>

								</div>
							</form>

						</div>

					</div>
				</div>


			</div>
		</div>
	</div>


	<jsp:include page="common/footer.jsp" />



</body>

<script>
    function validateForm(){
		var fatherName = document.getElementById("fatherName").value;
		var motherName = document.getElementById("motherName").value;
		fatherName = fatherName.trim();
		motherName = motherName.trim();
		if(fatherName.match(/\s/g) || motherName.match(/\s/g)){
            alert("Please enter only First Name for Parents, Full name not allowed.");
            return false;
        }
		
		return true;
		
	}
    
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
    
    $(document).ready(function(){
    	$(".tenDigit").change(function(){
    		
    		var mobileNumberValue = $(".tenDigit").val();
            if(mobileNumberValue.length >10 || mobileNumberValue.length<10)
            {
                alert("Please enter 10 digit Mobile Number");
                $(".tenDigit").val("");
            }
    	});
    	$(".numonly").keypress(function(e){ 
            return (e.which != 8 && e.which != 0 && (e.which > 57 || e.which < 48) ) ? false : true;
                });
    	
    	$(".doNotAllowedSpace").keydown(function(event) {
    		if (event.keyCode == 32) {
    			event.preventDefault();
    		}
    	});
    	
    	$(".shippingFields").change(function(){
    		var shippingField = $(this).val();
    		if(shippingField==""){
    			$(this).val("N/A");
    		}
    	});
    	
    	//Code for auto fill address on change of pincode start
    	<c:if test="${showShippingAddress eq 'Yes'}"> 
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
    	
    	
    });//End of doc.ready
    </script>
</html>
