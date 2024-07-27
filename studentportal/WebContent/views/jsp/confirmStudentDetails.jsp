<!DOCTYPE html>
<%@page import="com.nmims.helpers.*"%>
<%@page import="java.net.URLEncoder"%>
<html lang="en">
<style>
	.complete-profile-warpper .sz-content-wrapper.withBgImage .student-info-bar .student-image {
		border: 2px solid #000;
	}
	.complete-profile-warpper .sz-content-wrapper.withBgImage .student-info-bar ul.student-info-list li {
		color: #333;
	}
	.contactDetailsUpdateLink {
		color: #d2232a;
	}
</style>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" var="BASE_URL_STUDENTPORTAL_STATIC_RESOURCES"/>

<jsp:include page="common/jscss.jsp">
	<jsp:param value="Update Profile" name="title" />
</jsp:include>
<body>
 
	<%@ include file="common/header.jsp"%>

	<c:if test="${fromProfileIcon}">
		<div class="sz-main-content-wrapper">
			<%@ include file="common/breadcrum.jsp"%>
			<div class="sz-main-content menu-closed">
				<div class="sz-main-content-inner">
				   <div id="sticky-sidebar">
						<%@ include file="common/left-sidebar.jsp"%>
					</div>
					<div class="sz-content-wrapper examsPage">
						<%@ include file="common/studentInfoBar.jsp"%>
						<div class="sz-content">
	</c:if>
	<c:if test="${fromProfileIcon == false}">
		<div class="sz-main-content-wrapper complete-profile-warpper">

			<jsp:include page="common/breadcrum.jsp">
				<jsp:param value="Student Zone;Update Information"
					name="breadcrumItems" />
			</jsp:include>

			<div class="sz-main-content menu-closed">
				<div class="container">
 
					<div class="sz-content-wrapper dashBoard withBgImage">
						<%@ include file="common/studentInfoBar.jsp"%>
	</c:if>
	<div id="errorPopup"></div>
	<h2 class="red text-capitalize">Update Profile</h2>
	<div class="clearfix"></div>
	<div class="panel-content-wrapper">
		<%@ include file="common/messages.jsp"%>
		<form:form id="update-profile" method="post"
			onSubmit="return validateForm();" modelAttribute="student">
			<div class="row">
				<div class="col-md-8">
					<%--   <form:input type="hidden" name="studentAddress" value="<%=address%>"> --%>
					<!--Accordion wrapper-->
					<div class="panel-group" id="accordion" role="tablist"
						aria-multiselectable="true">
						<!-- Accordion card -->
						<div class="panel panel-default">
							<!-- Card header -->

							<div class="panel-heading" style="border-left: 5px solid red;">
								<a class="collapsed" data-toggle="collapse"
									data-parent="#accordion" aria-expanded="false" href="#101">
									<h4 class="panel-title">

										Personal Information <i class="fa-solid fa-arrow-down"
											aria-hidden="true" style="float: right"></i>

									</h4>
								</a>
							</div>

							<!-- Card body -->
							<div id="101" class="panel-collapse collapse out">

								<div class="form-group">
									<form:label for="firstName" path="firstName">First Name</form:label>
									<form:input id="firstName" path="firstName"
										class="form-control doNotAllowedSpace" readonly="true" />
								</div>
								<div class="form-group">
									<form:label for="middleName" path="middleName">Middle Name</form:label>
									<form:input id="middleName" path="middleName"
										class="form-control " placeholder="Enter Middle Name"
										readonly="true" />
								</div>

								<div class="form-group">
									<form:label for="lastName" path="lastName">Last Name</form:label>
									<form:input id="lastName" path="lastName"
										class="form-control doNotAllowedSpace"
										placeholder="Enter Last Name" readonly="true" />
								</div>
								<div class="form-group">
									<form:label for="mother" path="motherName"> Mother Name
										<c:if test="${fromProfileIcon}">
											&nbsp;&nbsp;&nbsp;<a class="contactDetailsUpdateLink" href="changeFatherMotherSpouseNameSRForm">
												<em>Click here to update Mother Name</em></a>
										</c:if>
									</form:label>
									<form:input type="text" class="form-control"
										id="motherId" name="motherName" readonly="true" 
										placeholder="Enter Mothers Name." path="motherName" />
									*<span style="color: red">(First Name)</span>
								</div>
								<div class="form-group">
									<form:label for="father" path="fatherName">Father Name
										<c:if test="${fromProfileIcon}">
											&nbsp;&nbsp;&nbsp;<a class="contactDetailsUpdateLink" href="changeFatherMotherSpouseNameSRForm">
												<em>Click here to update Father Name</em></a>
										</c:if>
									</form:label>
									<form:input type="text" path="fatherName" readonly="true" 
										class="form-control" id="fatherId"
										name="fatherName" placeholder="Enter Fathers Name." />
									*<span style="color: red">(First Name)</span>
								</div>
								<div class="form-group">
									<form:label for="husbandName" path="husbandName">Spouse Name
										<c:if test="${fromProfileIcon}">
											&nbsp;&nbsp;&nbsp;<a class="contactDetailsUpdateLink" href="changeFatherMotherSpouseNameSRForm">
												<em>Click here to update Spouse Name</em></a>
										</c:if>
									</form:label>
									<form:input type="text" path="husbandName"
										class="form-control" id="husbandId"
										name="husbandName" readonly="true" />
								</div>
								<div class="form-group">
									<form:label for="dob" path="dob">Date of Birth</form:label>
									<form:input type="date" class="form-control" path="dob"
										readonly="true" />
								</div>
								<div class="form-group">
									<form:label for="age" path="age">Age</form:label>
									<form:input id="age" path="age"
										class="form-control numonly doNotAllowedSpace" readonly="true"
										placeholder="Enter Age" />
								</div>
								<div class="form-group">
									<form:label for="gender" path="gender">Gender(*)</form:label>
									<form:select id="gender" name="gender" path="gender"
										type="text" placeholder="gender" class="form-control"
										required="required">
										<option value="${student.gender }">${student.gender }
										</option>

										<c:if test="${student.gender == 'Male'  }">
											<option value="Female">Female</option>
										</c:if>
										<c:if test="${student.gender == 'Female' }">
											<option value="Male">Male</option>
										</c:if>
										<c:if test="${empty student.gender}">
											<option value="Male">Male</option>
											<option value="Female">Female</option>
										</c:if>


									</form:select>
								</div>
								<div class="row">
									<div class="form-group col-md-6">
										<form:label for="abcId" path="abcId">Academic Bank of Credits Id</form:label>
										<form:input type="text" id="abcId" class="form-control" name="abcId" placeholder="Enter Academic Bank of Credits Id (Optional)" path="abcId" minlength="12" maxlength="12" pattern="^$|^[0-9]{1,12}$" title="Invalid Abc Id. It should be numeric and have a maximum of 12 digits." />
									</div>
									<div class="form-group col-md-6 card">
								  		<div class="card-body">
										    <h5 class="card-title">Update Your ABC ID</h5>
										    <p class="card-text" style="color: #6c757d;">(As per the UGC guidelines, it is recommended to create your unique ID for Academic Bank of Credits purpose. 
									    	<a href="${BASE_URL_STUDENTPORTAL_STATIC_RESOURCES}resources_2015/How_to_Create_ABC_ID.pdf" target="/blank" class="card-link">Click Here</a> to know how to generate your ABC ID)</p>
							  			</div>
									</div>
								</div>
							</div>
						</div>
						<!-- Accordion card -->
						<br>

						<div class="panel panel-default">
							<!-- Card header -->

							<div class="panel-heading" style="border-left: 5px solid red;">
								<a class="collapsed" data-toggle="collapse"
									data-parent="#accordion" aria-expanded="false" href="#102">
									<h4 class="panel-title">

										Contact Information <i class="fa-solid fa-arrow-down"
											aria-hidden="true" style="float: right"></i>

									</h4>
								</a>
							</div>

							<!-- Card body -->
							<div id="102" class="panel-collapse collapse out">

								<div class="form-group">
									<form:label for="emailId" path="emailId">Enter Email
										<c:if test="${fromProfileIcon}">
											&nbsp;&nbsp;&nbsp;<a class="contactDetailsUpdateLink" href="changeInContactDetailsSRForm">
												<em>Click here to update Email Address</em></a>
										</c:if>
									</form:label>
									<form:input path="emailId" class="form-control" id="emailId"
										name="email" placeholder="Enter Email" readonly="true" />
								</div>

								<div class="form-group">
									<form:label for="mobile" path="mobile">Mobile No.
										<c:if test="${fromProfileIcon}">
											&nbsp;&nbsp;&nbsp;<a class="contactDetailsUpdateLink" href="changeInContactDetailsSRForm">
												<em>Click here to update Mobile Number</em></a>
										</c:if>
									</form:label>
									<form:input type="text" path="mobile"
										class="form-control" id="mobile"
										name="mobile" placeholder="Enter Mobile No."
										readonly="true" maxlength="12" />
								</div>

								<div class="form-group">
									<form:label for="altPhone" path="altPhone">Alternate Contact No.</form:label>
									<form:input type="text" path="altPhone"
										class="form-control numonly" id="altMobile" name="altMobile"
										placeholder="Enter Alternate Contact No." />
								</div>

								<%-- <c:choose>
																				<c:when test="${showShippingAddress eq 'Yes'}"> --%>
								<h2 class="red text-capitalize">SHIPPING ADDRESS (*)</h2>
								<div class="clearfix"></div>
								<div class="form-group">
									<form:label for="houseNoName" path="houseNoName">Address Line 1 : House Details</form:label>
									<form:input type="text" path="houseNoName"
										class="form-control shippingFields" id="houseNameId"
										name="shippingHouseName" value="${student.houseNoName}"
										required="required" onkeyup="charcountupdate(this.value,'houseNoName')" 
										maxlength="30" onfocusout="clearSpan()"/>
										<span id=houseSpan></span><br>
								</div>
								<div class="form-group">
									<form:label for="street" path="street">Address Line 2 : Street Name</form:label>
									<form:input type="text" path="street"
										class="form-control shippingFields" id="shippingStreetId"
										name="shippingStreet" value="${student.street}"
										required="required" onkeyup="charcountupdate(this.value,'street')" 
										maxlength="35" onfocusout="clearSpan()"/>
										<span id=streetSpan></span><br>
								</div>
								<div class="form-group">
									<form:label for="locality" path="locality">Address Line 3 : Locality Name</form:label>
									<form:input type="text" path="locality"
										class="form-control shippingFields" id="localityNameId"
										name="shippingLocalityName" value="${student.locality}"
										required="required" onkeyup="charcountupdate(this.value,'locality')"
										maxlength="35" onfocusout="clearSpan()"/>
										<span id=localitySpan></span><br>
								</div>
								<div class="form-group" style="display: none;">
									<form:label for="landMark" path="landMark">Address Line 4 : Nearest LandMark</form:label>
									<form:input type="hidden" path="landMark"
										class="form-control shippingFields" id="nearestLandMarkId"
										name="shippingNearestLandmark" value="${student.landMark}"
										/>
								</div>
								<div class="form-group">
									<form:label for="pin" path="pin">Postal Code</form:label>
									<form:input type="text"
										class="form-control shippingFields numonly" id="postalCodeId"
										name="shippingPostalCode" value="${student.pin}" maxlength="6"
										path="pin" required="required" />
								</div>
								<br> <span class="well-sm" id="pinCodeMessage"></span>
								<div class="form-group">
									<form:label for="shippingCityId" path="city">Shipping City</form:label>
									<form:input type="text" class="form-control shippingFields"
										id="shippingCityId" name="shippingCity" path="city"
										value="${student.city}" readonly="true"
										onkeypress="return onlyAlphabets(event,this);" />
								</div>
								<div class="form-group">
									<form:label for="stateId" path="state">Shipping State</form:label>
									<form:input type="text" path="state"
										class="form-control shippingFields" id="stateId"
										name="shippingState" value="${student.state}" readonly="true"
										onkeypress="return onlyAlphabets(event,this);" />
								</div>
								<div class="form-group">
									<form:label for="countryId" path="country">Country For Shipping</form:label>
									<form:input type="text" path="country"
										class="form-control shippingFields" id="countryId"
										name="shippingCountry" value="${student.country}"
										readonly="true" onkeypress="return onlyAlphabets(event,this);" />
								</div>
								<%-- </c:when>
																				<c:otherwise>
																					<h2 class="red text-capitalize">SHIPPING ADDRESS (*)</h2>
									                    	
																						<div class="form-group">
																						<textarea name="studentAddress" path = "address" id="studentAddress" cols="50" rows="7" class="form-control" required = "required">${student.address}</textarea>
																						</div>
																				</c:otherwise>
																			</c:choose> --%>
							</div>
						</div>
					</div>
					<!-- Accordion card -->

					<div class="panel panel-default">
						<!-- Card header -->
						<div class="panel-heading" style="border-left: 5px solid red;">
							<a class="collapsed" data-toggle="collapse"
								data-parent="#accordion" aria-expanded="false" href="#103">
								<h4 class="panel-title">

									Education / Work Information <i class="fa-solid fa-arrow-down"
										aria-hidden="true" style="float: right"></i>

								</h4>
							</a>
						</div>
						<!-- Card body -->
						<div id="103" class="panel-collapse collapse out">
							<div class="form-group">
								<form:label for="highestQualification"
									path="highestQualification">Highest Qualification</form:label>
								<form:input id="highestQualification"
									path="highestQualification"
									class="form-control doNotAllowedSpace"
									placeholder="Enter Highest Qualification" readonly="true" />
							</div>
							<div class="form-group">
								<label for="designation">Designation</label> <select
									id="designation" name="designation" type="text"
									placeholder="Designation" class="form-control">
									<option value="">Please Select Designation</option>
									<c:forEach items="${designationList}" var="designationParam">
										<option value="${designationParam}"
											${student.designation == designationParam ? 'selected' : ''}>${designationParam}</option>
									</c:forEach>
								</select>
							</div>
							<div class="form-group">
								<label for="industry">Industry</label> <select id="industry"
									name="industry" type="text" placeholder="Industry"
									class="form-control">
									<option value="">Please Select Industry</option>
									<c:forEach items="${industryList}" var="industryParam">
										<option value="${industryParam}"
											${student.industry == industryParam ? 'selected' : ''}>${industryParam}</option>
									</c:forEach>
								</select>
							</div>
						</div>
					</div>

					<!-- Accordion card -->
					<!-- LOU form -->
					<c:if test="${!student.louConfirmed||empty student.louConfirmedTimestamp}">
						<h2 class="red text-capitalize">Student Undertaking with respect to the Student Guidelines</h2>
						<br><br>
						<p style="float: left">I will always uphold the values and honour of the NGASCE, NMIMS. I promise to fulfil my responsibilities as a student and treat my colleagues, Staff and Faculty with dignity and respect. I hereby declare that I will follow the Student Guidelines, Policies, Procedures, University and Exam Code of Conduct etc. and in case of any violation on my part, I consent to the action in accordance with the Management's decision.</p>
						<p>I hereby agree to abide by the rules and regulations of SVKM's NMIMS in my role as a participant of this program. I agree that NMIMS has the right to make any changes as it may deem fit in terms of the program, the decision of the vice-Chancellor of SVKM's NMIMS will be final and binding on all the participants.</p>
						<input type="checkbox" id="LOU" name="louConfirmed" />
						
						<label for="LOU" style="margin-left: -30px; font-weight: normal; line-height: normal;">
							I have read and understood the above guidelines/instructions.
						</label>
					</c:if>
					<div class="form-group">
						
						<%--<c:choose>
															 	<c:when test="${showShippingAddress eq 'Yes'}"> --%>
				 		<c:choose>
						<c:when test="${!student.louConfirmed||empty student.louConfirmedTimestamp}">
						<button type="submit" class="btn btn-primary"
							formaction="${pageContext.request.contextPath}/student/saveConfirmedProfileForSFDCAndPortal"
							onclick="return validateForm()" id="update" disabled="disabled">Update Information</button>
						</c:when>
						<c:otherwise>
						<button type="submit" class="btn btn-primary"
							formaction="${pageContext.request.contextPath}/student/saveConfirmedProfileForSFDCAndPortal"
							onclick="return validateForm()">Update Information</button>
						</c:otherwise>
						</c:choose>
						<%-- 	</c:when> 
																<c:otherwise>
																  <button type="submit" class="btn btn-primary" formaction="saveProfileForPortal">Update Information</button>
																</c:otherwise>
															</c:choose>--%>
						<button type="button" class="btn btn-danger" id="cancelBtn">Cancel</button>
					</div>
				</div>
				<!-- Accordion wrapper -->

			</div>
		</form:form>
	</div>

	<c:if test="${fromProfileIcon}">
		</div>
		</div>
		</div>
		</div>
		</div>
	</c:if>
	<c:if test="${fromProfileIcon == false}">
		</div>
		</div>
		</div>
		</div>
	</c:if>

	<jsp:include page="common/footer.jsp" />


</body>

<script>
	$(function () {
	    $("#LOU").click(function () {
	        if ($(this).is(":checked")) {
	            $("#update").removeAttr("disabled");
	        } else {
	            $("#update").attr("disabled", "disabled");
	        }
	    });
	});

function charcountupdate(str,id) {   
	var lng = str.length;
	if(id==="houseNoName"){
		if(lng===30){
			document.getElementById("houseSpan").innerHTML = "Max capacity reached "+lng+ ' out of 30 characters';
			document.getElementById("houseSpan").style.color ="#ff0000";
			return;	
		}
		document.getElementById("houseSpan").innerHTML = lng+ ' out of 30 characters';
		return;
	}
	if(id==="street"){
		if(lng===35){
			document.getElementById("streetSpan").innerHTML = "Max capacity reached "+lng+ ' out of 35 characters';
			document.getElementById("streetSpan").style.color ="#ff0000";
			return;	
		}
		document.getElementById("streetSpan").innerHTML = lng+ ' out of 35 characters';
		return;
	}
	if(id==="locality"){
		if(lng===35){
			document.getElementById("localitySpan").innerHTML = "Max capacity reached "+lng+ ' out of 35 characters';
			document.getElementById("localitySpan").style.color ="#ff0000";
			return;	
		}
		document.getElementById("localitySpan").innerHTML = lng+ ' out of 35 characters';
		return;
	}
}

function clearSpan() {
	  document.getElementById("houseSpan").innerHTML="";
	  document.getElementById("streetSpan").innerHTML="";
	  document.getElementById("localitySpan").innerHTML="";
	  
	  document.getElementById("houseSpan").style.color ="#000000";
	  document.getElementById("streetSpan").style.color ="#000000";
	  document.getElementById("localitySpan").style.color ="#000000";
	}
</script>


<script>
	const cancelButton = document.getElementById("cancelBtn");
	let errorPopup = document.getElementById("errorPopup");
	
	cancelButton.onclick = function() {
		if(${fromProfileIcon}) {
			window.location.href = '${pageContext.request.contextPath}/home';
		}
		else {
			let error = document.getElementById("errorTxt");
			if(error == null) {
				error = document.createElement("div");
				error.setAttribute("id", "errorTxt");
				error.setAttribute("class", "alert alert-danger alert-dismissible");
				error.setAttribute("style", "margin-bottom: 0");
				error.textContent = "Please update your profile to continue further.";
				errorPopup.appendChild(error);
				
				let buttonInner = document.createElement("button");
				buttonInner.innerHTML = " &times; ";
				buttonInner.setAttribute("type", "button");
				buttonInner.setAttribute("class", "close");
				buttonInner.setAttribute("data-dismiss", "alert");
				buttonInner.setAttribute("aria-hidden","true");
										
			    error.appendChild(buttonInner);
			}

			Element.prototype.documentOffsetTop = function () {
			    return this.offsetTop + ( this.offsetParent ? this.offsetParent.documentOffsetTop() : 0 );
			};
			
			const top = errorPopup.documentOffsetTop() - (window.innerHeight / 2);
			window.scrollTo(0, top);
		}
	}

    function validateForm(){
// 		let fatherName = document.getElementById("fatherId");
// 		let motherName = document.getElementById("motherId");
		
		var gender = document.getElementById("gender").value;
// 		var email = document.getElementById("emailId").value;
// 		var mobile = document.getElementById("mobile").value;
		var houseNoName = document.getElementById("houseNameId").value;
		var street = document.getElementById("shippingStreetId").value;
		var locality = document.getElementById("localityNameId").value;
		//var landMark = document.getElementById("nearestLandMarkId").value;
		var pin = document.getElementById("postalCodeId").value;

// 		fatherNameValue = fatherName.value.trim();
// 		motherNameValue = motherName.value.trim();
// 		if(fatherNameValue.match(/\s/g) || motherNameValue.match(/\s/g)){
//             alert("Please enter only First Name for Parents, Full name not allowed.");
//             return false;
//         }
// 		else {
// 			fatherName.value = fatherNameValue;
// 			motherName.value = motherNameValue;
// 		}
		
// 		var mobileNumberValue = $(".tenDigit").val();
//         if(mobileNumberValue.length >10 || mobileNumberValue.length<10)
//         {
//             alert("Please enter 10 digit Mobile Number");
//             return false;
//         }
       
        if(
//              fatherNameValue === "" ||
// 				motherNameValue === ""||
        		gender === ""|| 
//         		email === ''|| 
//         		mobile ===''|| 
        		houseNoName === ''|| 
        		street === ''|| 
        		locality === ''|| 
        		//landMark === ''||
        		pin === '')
        {
            alert("Please enter all mandatory fields");
            return false;
        }
  
		if (pin.length != 6) {
		    alert("Pincode must be exactly 6 characters");
		    return false;
		  }

//         if(fatherNameValue === "null" || motherNameValue === "null") 
//         {
//         	alert("Name cannot be 'null'");
//             return false;
//         }
        
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
				  $("#pinCodeMessage").text("Unable to get City, State and Country. Kindly Contact Support.");   
				  $("#pinCodeMessage").css("color","red");	
			  });
    		
    	});
    	</c:if> 
    	//Code for auto fill address on change of pincode end
    	try{
	    	var hashId = window.location.href.split('#')[1];
	    	if(hashId === 'abcId')
	    		openPersonalInformationAndNavigateToAbcIdDiv();
       	}catch(error){
           	console.log("Error: in navigating to #abcId div due to ",error);
       	}
    	
    });//End of doc.ready

    function openPersonalInformationAndNavigateToAbcIdDiv(){
        $('#accordion #101').collapse('show');
        $('html, body').animate({
            scrollTop: $('#abcId').offset().top
        }, 100);
    }


    </script>
<%--    <%
		Person p = (Person)session.getAttribute("user_studentportal");
	    String validityEndTime = (String)session.getAttribute("validityEndDate");
	 //   String userId = (String)session.getAttribute("userId");
	    String firstName = "";
		String lastName = "";
		String displayName = "";
		String program = "";
		String email = "";
		String lastLogon = "";
		 
	    
	    if(p != null){
	    	displayName = p.getDisplayName();
	    	program = p.getProgram();
	    	lastLogon = p.getLastLogon();
	    }
    
		String encryptedSapId = URLEncoder.encode(AESencrp.encrypt(p.getUserId())); 
		String examAppSSOUrl = (String)pageContext.getAttribute("server_path") + "exam/loginforSSO?uid="+encryptedSapId;
    	String acadsAppSSOUrl = (String)pageContext.getAttribute("server_path") + "acads/loginforSSO?uid="+encryptedSapId;
	
%>


			 <script>
			$( "#examApp" ).load( "<%=examAppSSOUrl%>" );
			$( "#acadsApp" ).load( "<%=acadsAppSSOUrl%>" );
			
			
			
			</script>  --%>


</html>
