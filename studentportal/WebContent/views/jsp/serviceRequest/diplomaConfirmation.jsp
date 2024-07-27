<%--  <!DOCTYPE html>
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
<jsp:param value="Final Certificate Confirmation"  name="title" />
</jsp:include>


<body class="inside">

<%@ include file="../header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
         <div class="row"><legend>Final Certificate Confirmation</legend></div>
        <form:form  action="saveSingleBook" method="post" modelAttribute="sr" enctype="multipart/form-data">
			<fieldset>
			
       
        <%@ include file="../messages.jsp"%>
		<div class="panel-body">
			<div>
			Dear Student, You have chosen below Service Request. Please confirm before proceeding. 
			</div>
			<br>
			
			<div class="col-md-12 col-xs-18  column">
			
				
				<div class="form-group">
					<form:label path="serviceRequestType" for="serviceRequestType">Service Request Type:</form:label>
					${sr.serviceRequestType }
					<form:hidden path="serviceRequestType"/>
				</div>
				
				<div class="form-group">
					<label>Charges: INR. <span id="chargesSpan">${charges}</span>/-</label>
					<form:hidden path="amount" id="amount" value="${charges}"/>
				</div>
				
					<br/>
					<div>
					Note: First Final Certificate request is free. Any subsequent requests for the same will incur Service Request fees of INR. 1000/-
					</div>
					
					<br/>
					
					
					<c:if test="${duplicateDiploma == 'true'}">
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
						Final Certificate will be sent to NMIMS Learning Center (Not to Information Center) (No Shipping Charges)
					</div>
					
					<div class="form-group">
						<label>
						<form:checkbox path="wantAtAddress" value="Yes" id="addressConfirmation"
						style="width:15px;height:15px;margin:5px;"/><span> &nbsp;I want Final Certificate at my address (Shipping Charges INR. 100/-)</span>
						</label>
					</div>
					
					<div class="form-group" id="addressDiv" style="display:none">
						<label for="postalAddress">Confirm/Edit Address</label>
						<textarea name="postalAddress" class="form-control" id="postalAddress" style="display:none;"  cols="50" rows = "5">${student.address}, Contact No:${student.mobile}</textarea>
					</div>
				
					<div class="form-group">
						<label class="control-label" for="submit"></label>
							<button id="saveWithPaymentButton" name="submit"
								class="btn btn-large btn-primary" formaction="saveFinalCertificateAndPayment" 
								<c:if test="${charges != 0}">
									style="display:block;"
								</c:if>
								<c:if test="${charges == 0}">
									style="display:none;"
								</c:if>
								
								>Save & Proceed to Payment</button>
							
							<button id="saveForFreeButton" name="submit"
								class="btn btn-large btn-primary" formaction="saveFinalCertificateRequest" 
								onClick="return confirm('Are you sure you want to request for Final Certificate? You can not make any changes after this step.');"
								<c:if test="${charges == 0}">
									style="display:block;"
								</c:if>
								<c:if test="${charges != 0}">
									style="display:none;"
								</c:if>
								>Request Certificate</button>
							
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
</body>
</html>
--%>
<!DOCTYPE html>
<%@page import="com.nmims.beans.StudentStudentPortalBean"%>
<%@page import="org.apache.commons.lang3.text.WordUtils"%> 

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" var="BASE_URL_STUDENTPORTAL_STATIC_RESOURCES"/>

<%StudentStudentPortalBean student = (StudentStudentPortalBean)session.getAttribute("student_studentportal"); 
String firstNameOfStudent = student.getFirstName();
String lastNameOfStudent = student.getLastName();
String fullName = firstNameOfStudent + " " +lastNameOfStudent;
String finalNameOnCertificate = WordUtils.capitalizeFully(fullName);
boolean isCertificate = (Boolean)session.getAttribute("isCertificate");
%>

<html lang="en">



    <jsp:include page="../common/jscss.jsp">
	<jsp:param value="Enter Service Request Information" name="title"/>
    </jsp:include>

    <style>
td{
padding:10px;
}
.selectCheckBox{
width: 30px; /*Desired width*/
  height: 30px; /*Desired height*/
}
.red{
color:red;
font-size:14px;
}

[type="checkbox"]:not(:checked), [type="checkbox"]:checked {
    position: relative;
    left: 0px;
    opacity: 1;
}

</style>
    
    <body onLoad="showDisclaimer();">
    
    	<%@ include file="../common/header.jsp" %>
    	
    	
        
        <div class="sz-main-content-wrapper">
        
        	<%@ include file="../common/breadcrum.jsp" %>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
				<div id="sticky-sidebar">
					<jsp:include page="../common/left-sidebar.jsp">
						<jsp:param value="Service Request" name="activeMenu" />
					</jsp:include>
				</div>
				<div class="sz-content-wrapper examsPage">
          						<%@ include file="../common/studentInfoBar.jsp" %>
          						<div class="sz-content">
						<h2 class="red text-capitalize">Final Certificate Confirmation</h2>
						<div class="clearfix"></div>
            					<div class="panel-content-wrapper">
							<%@ include file="../common/messages.jsp" %>
							
							
							<div style="width: 60%">
								Add Final Certificate to your LinkedIn Profile
								<img src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/images/linkedInAddToProfile.png" height=35px
									alt="Add To Profile" style="cursor: pointer; float :right;"
									onClick="callShareCertificate()" />
								
								<br /> <br /> 
									
								Share Certificate to your LinkedIn Feed
								<img src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/images/linkedin-share-button.png" height=35px
									alt="Share Certificate" style="cursor: pointer; float :right;"
									onClick="shareCertificate()"  />
									
							</div>
							
							<br /> <br /> 
							
							<div>
								Dear Student, You have chosen below Service Request. Please fill in required information below before proceeding for Payment. 
							</div>
							
							<br/>
							
							<form:form id="form1" action="saveSingleBook" method="post" modelAttribute="sr" enctype="multipart/form-data">
							<!-- payment gateway option  -->
							<!-- <input type="hidden" id="paymentOption" name="paymentOption" value="paytm"/> -->
							<fieldset>
								<div class="row">
									<div class="col-md-6 column">
										<div class="form-group">
										<form:label path="serviceRequestType" for="serviceRequestType">Service Request Type:</form:label>
										${sr.serviceRequestType }
										<form:hidden path="serviceRequestType"/>
									</div>
									<div class="form-group">
									<label>Charges: INR. <span id="chargesSpan">${charges}</span>/-</label>
									<form:hidden path="amount" id="amount" value="${charges}"/>
								</div>
							<br/>
							<div>
							Note: First Final Certificate request is free. Any subsequent requests for same certificate will be considered as a duplicate request and  Service Request fees of INR. 1000/- will be charged for the same
							</div>
							
							<br/>
							
							
							<c:if test="${duplicateDiploma == 'true'}">
								<div class="form-group">
									<label for="firCopy">Please Upload Copy of FIR:</label>
									<input type="file" name="firCopy" required="required" class="form-control" id="firCopy">
								</div>
								
								<div class="form-group">
									<label for="indemnityBond">Please Upload Copy of Indemnity Bond as per format shown here:</label>
									<input type="file" name="indemnityBond" required="required" class="form-control" id="indemnityBond">
								</div>
								
								<a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />resources_2015/notices/Proforma_of_Indemnity_Bond.docx" target="_blank">Download Proforma of Indemnity Bond</a>
							</c:if>
							
							<div class="form-group" id="marksheetAddressNotice">
								Final Certificate will be sent to NMIMS Learning Center (Not to Information Center) (No Shipping Charges)
							</div>
							<!-- Newly Added by Vikas on 09/08/2016 -->
							
							
								<div class="form-group">
								<label>
								<form:checkbox path="additionalInfo1" value="Parent" id="nameOnCertifcateIdYes" class="nameOnCertificateClass" 
								style="width:15px;height:15px;margin:5px;"/><span> &nbsp;Do you wish the certificate to be in Husbands name ? </span>
								</label>
								
								</div>
								<div class="form-group" id="nameOnCertificateDocId" style="display:none">
								<label for="nameOnCertificateDoc">Please Upload Marriage Certificate OR Affidavit</label>
								<input type="file" id="nameOnCertificateDoc" name="nameOnCertificateDoc"  class="form-control">
							</div>
						
							<!-- Ends here -->
							<form:checkbox path="wantAtAddress" value="Yes" id="addressConfirmation"
								style="width:15px;height:15px;margin:5px;display:none" checked="checked"/>
							<div class="form-group">
								<label>
								<span> &nbsp;I want Final Certificate at my address (Shipping Charges INR. 100/-)</span>
								</label>
							</div>

							 
							<div class="form-group" id="addressDiv" style="display:none">
								<%-- <label for="postalAddress">Confirm/Edit Address</label>
								<textarea name="postalAddress" class="form-control" id="postalAddress" style="display:none;"  cols="50" rows = "5"><%=student.getAddress() %></textarea> --%>
								
								<h5 class="red text-capitalize">SHIPPING ADDRESS</h5>
																					<div class="clearfix"></div>
																							<div class="form-group">
																								<form:label for="houseNameId" path="houseNoName"> (*) Address Line 1 : House Details</form:label>
																								<form:input type="text" path="houseNoName"  class="form-control shippingFields" id="houseNameId" 
																									   name="shippingHouseName"  value="${student.houseNoName}" required = "required"/>
																							</div>
																							<div class="form-group">
																								<form:label for="shippingStreetId" path="street"> (*) Address Line 2 : Street Name</form:label>
																								<form:input type="text" path="street" class="form-control shippingFields" id="shippingStreetId"
																								   name="shippingStreet"  value="${student.street}" required = "required"/>
																							</div>
																							<div class="form-group">
																								<form:label for="localityNameId" path="locality"> (*) Address Line 3 : Locality Name</form:label>
																								<form:input type="text" path="locality" class="form-control shippingFields" id="localityNameId" 
																								   name="shippingLocalityName"  value="${student.locality}" required = "required"/>
																							</div>
																							<div class="form-group">
																								<form:label for="nearestLandMarkId" path="landMark"> (*) Address Line 4 : Nearest LandMark</form:label>
																								<form:input type="text"  path="landMark" class="form-control shippingFields" id="nearestLandMarkId" 
																								   name="shippingNearestLandmark"  value="${student.landMark}" required = "required"/>
																							</div>
																							<div class="form-group">
																								<form:label for="postalCodeId" path="pin"> (*) Postal Code</form:label>
																								<form:input type="text" class="form-control shippingFields numonly" id="postalCodeId" 
																								   name="shippingPostalCode"  value="${student.pin}" maxlength="6" path="pin"
																								   required="required"/>
																							</div>
																							<br>
																							<span class="well-sm" id="pinCodeMessage"></span>	   
																							<div class="form-group">
																								<form:label for="shippingCityId" path="city"> (*) Shipping City</form:label>
																								<form:input type="text" class="form-control shippingFields" id="shippingCityId" 
																									   name="shippingCity"  path="city"  value="${student.city}" readonly = "true"  
																									   onkeypress= "return onlyAlphabets(event,this);" />
																							</div>
																							<div class="form-group">
																								<form:label for="stateId" path="state"> (*) Shipping State</form:label>
																								<form:input type="text" path="state" class="form-control shippingFields" id="stateId"
																								   name="shippingState"  value="${student.state}" readonly = "true"
																						   onkeypress="return onlyAlphabets(event,this);"/>
																							</div>
																							<div class="form-group">
																								<form:label for="countryId" path="country"> (*) Country For Shipping</form:label>
																								<form:input type="text" path="country" class="form-control shippingFields" id="countryId" 
																								   name="shippingCountry"  value="${student.country}"  readonly = "true"
																								   onkeypress="return onlyAlphabets(event,this);"/>
																							</div> 
																							<div class="row">
																								<div class="form-group col-md-6">
																									<form:label for="abcId" path="abcId">Academic Bank of Credits Id</form:label>
																									<form:input type="text" id="abcId" class="form-control" name="abcId" placeholder="Academic Bank of Credits Id (Optional)" path="abcId" readonly="true" />
																								</div>
																								<div class="form-group col-md-6 card">
																							  		<div class="card-body">
																									    <h5 class="card-title">Update Your ABC ID</h5>
																									    <p class="card-text" style="color: #6c757d;">(As per the UGC guidelines, it is recommended to create your unique ID for Academic Bank of Credits purpose. 
																								    	<a href="${BASE_URL_STUDENTPORTAL_STATIC_RESOURCES}resources_2015/How_to_Create_ABC_ID.pdf" target="/blank" class="card-link">Click Here</a> to know how to generate your ABC ID)</p>
																								    	<p class="card-text" style="color: #6c757d;">To update your Academic Bank of Credits Id <a class="card-link" href="${pageContext.request.contextPath}/student/updateProfile#abcId">Click Here</a></p>
																						  			</div>
																								</div>
																							</div>
																							
																							
							</div>
						
						    <div class="preview" >
								<button id="preveiwCert" class="btn btn-large btn-danger"  data-toggle="modal" data-target="#previewCertificate"  >Preview Final Certificate</button>
							</div>
								
											
							<div class="form-group col-md-6">
							
									
								
									<button id="saveWithPaymentButton"
										class="btn btn-large btn-primary submitButton"  
										<c:if test="${charges != 0}">
											style="display:inline;margin-top:20px;"
										</c:if>
										<c:if test="${charges == 0}">
											style="display:none;margin-top:20px;"
										</c:if>
										
										>Save & Proceed to Payment</button>
										
									
									
									<button id="saveForFreeButton"
										class="btn btn-large btn-primary submitButton" 
										<c:if test="${charges == 0}">
											style="display:inline;"
										</c:if>
										<c:if test="${charges != 0}">
											style="display:none;"
										</c:if>
										>Request Certificate</button>
										
										
									<button id="backToSR" name="BacktoNewServiceRequest" class="btn btn-danger" 
									 formnovalidate="formnovalidate">Back to New Service Request</button>
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
       <div class="modal fade" id="previewCertificate" role="dialog">
    <div class="modal-dialog">
    
      <!-- Modal content-->
      <div class="modal-content">
     
        <div class="modal-body">
        <h5 align="center" style="color:red;">This is how your name would reflect on the certificate, in case of changes kindly email us at ngasce@nmims.edu</h5>
          <h4 align="center">Certified that</h4>
          <br></br>
          <h1 align="center"><b><%=finalNameOnCertificate %></b></h1><br>
         <%--  <%if("Male".equals(student.getGender())){%>
          <h4 align="center" id="description">(Son of Shri.<%=student.getFatherName() %> and Smt.<%=student.getMotherName()%>)</h4>
          <%}else{ %>
          <h4 align="center" id="description">(Daughter of Shri.<%=student.getFatherName() %> and Smt.<%=student.getMotherName()%>)</h4>
          <%} %> --%>
          <h4 align="center" id="description"></h4>
          <br></br>
          
          		<div class="form-group" id="verifyInformation" >    
					<label>
						<input type="checkbox" path="wantAtAddress" value="Yes" id="previewConfirmation"
						style="width:15px;height:15px;margin:5px;"/><span> &nbsp; I accept the above information is true and complete to the best of my knowledge</span>
					</label> 
				</div>   
        </div> 
       
      </div>  
      
    </div>
  </div>
  	  
  	  
  	  
  	  
        <jsp:include page="../common/footer.jsp"/>
        <%-- <%@ include file="./paymentOption.jsp"%> --%>
        
<script type="text/javascript">
      var duplicateDiploma = ${duplicateDiploma};
        function showDisclaimer(){
        	alert("Please view the preview of your Final year Certificate before issuing Service Request. Kindly fill mandatory fields in order to preview.");
        }

	

var salutationArray = ["(Son of Shri."+'<%=student.getFatherName() %>'+" and Smt."+'<%=student.getMotherName()%>'+")","(Daughter of Shri."+'<%=student.getFatherName() %>'+" and Smt."+'<%=student.getMotherName()%>'+")","(Wife of Shri."+"<%=student.getHusbandName() %>"+" and Daughter of Smt. "+'<%=student.getMotherName()%>'+")"];


var gender =  '<%=student.getGender()%>'; 
var previewDescription = (gender =="Male") ? salutationArray[0]: salutationArray[1];

$("#description").text(previewDescription);
var previewDescription;
var charges = ${charges};
var totalCost = 0;
var isCertificate = <%=isCertificate%>

var isOkToSubject = false;
//saveFinalCertificateRequest
function ChangeFormAction(action){
	$('#form1').prop('action',action);
	$('#form1').submit();
}

function sendWithPaymentRequest(){
	
}

$('#saveWithPaymentButton').click(function(){
	
	 var nameOnCertifcateIdYes = $('#nameOnCertifcateIdYes').prop('checked');
	 var previewConfirmation = $('#previewConfirmation').prop('checked');
	 var spouseName='<%=student.getHusbandName() %>';
	 if(nameOnCertifcateIdYes == true && (spouseName == null || spouseName == "") ){
		 alert("Spouse Name is not Saved Kindly update Profile");  
		 return false;
	 }
	 if(nameOnCertifcateIdYes == true && previewConfirmation == false){
		 alert("Kindly accept the information is true");   
		 return false;
	 }  
   


		var nameOnCertificateDoc = $('#nameOnCertificateDoc').val();
		/* if(nameOnCertifcateIdYes == true && nameOnCertificateDoc.trim() == ""){
			alert("Kindly submit required files");
			 return false;
		} */

		if(duplicateDiploma == true){
			 var firCopy = $('input[type="file"][name="firCopy"]').val();
			 var indemnityBond = $('input[type="file"][name="indemnityBond"]').val();
			 if(firCopy.trim() == "" || indemnityBond.trim() == ""){
				 alert("Kindly submit required files");
				 return false;
			 }
		}
		 if (!confirm('Are you sure you want to submit this Service Request?')) return false;
		 /* $("#paymentGatewayOptions").modal(); */
		 $.ajax({
				url : '/studentportal/CheckServiceRequestCount?sapId=${ usersapId }&requestType=Issuance of Final Certificate&isFreeRequest=false',
				type:'POST',
				success:function(response){
					console.log('success response : ' + response.status);
					if(response.status == "500"){
						alert(response.result);
						return false;
					}else if(response.status == "200"){
						console.log("inside status 200");
						ChangeFormAction('saveFinalCertificateAndPayment');
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
/* $(document).on('click','.selectPaymentGateway',function(){
	$("#paymentOption").attr("value",$(this).attr('data-paymentGateway'));
	
}); */
/* $(document).on('click','#selectPaytm',function(){
	$("#paymentOption").attr("value","paytm");
	ChangeFormAction("saveFinalCertificateAndPayment");
});
$(document).on('click','#selectHdfc',function(){
	$("#paymentOption").attr("value","hdfc");
	ChangeFormAction("saveFinalCertificateAndPayment");
});
$(document).on('click','#selectBilldesk',function(){
	$("#paymentOption").attr("value","billdesk");
	ChangeFormAction('saveFinalCertificateAndPayment');
});
$(document).on('click','#selectPayu',function(){
	$("#paymentOption").attr("value","payu");
	ChangeFormAction('saveFinalCertificateAndPayment');
}); */


$('#backToSR').click(function(){
	ChangeFormAction('selectSRForm');
});
$('#saveForFreeButton').click(function(){

	 var nameOnCertifcateIdYes = $('#nameOnCertifcateIdYes').prop('checked');
	 var previewConfirmation = $('#previewConfirmation').prop('checked');
	 var spouseName='<%=student.getHusbandName() %>';
	 if(nameOnCertifcateIdYes == true && (spouseName == null || spouseName == "") ){
		 alert("Spouse Name is not Saved Kindly update Profile");  
		 return false;
	 }
	 if(nameOnCertifcateIdYes == true && previewConfirmation == false){
		 alert("Kindly accept the information is true");   
		 return false;
	 }    

	$.ajax({
		url : '/studentportal/student/CheckFinalCertificateCount?sapId=${ usersapId }&requestType=Issuance of Final Certificate&isFreeRequest=true',
		type:'POST',
		success:function(response){
			console.log('success response : ' + response.status);
			if(response.status == "500"){
				alert(response.result);
				return false;
			}else if(response.status == "200"){
				console.log("inside status 200");
				ChangeFormAction('saveFinalCertificateRequest');
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

console.log("Gender of student "+gender);
$('.nameOnCertificateClass').change(function() {
	if (this.checked) {
		$('#previewCertificate').modal('show');  
		$(this).val("Spouse");
		$("#nameOnCertificateDocId").css('display','block');
        $("#nameOnCertificateDoc").attr('required',true);
        previewDescription = salutationArray[2];
        $("#description").text(previewDescription);
        //$("#verifyInformation").css('display','block');
        $('#saveWithPaymentButton').attr('disabled', 'true'); 
        $('#saveForFreeButton').attr('disabled', 'true'); 
        console.log('Want cert in husbands name'+salutationArray[2]);
    } 
    if (!this.checked){ 
    	$(this).val("Parent");
    	$("#nameOnCertificateDocId").css('display','none');
   	 $("#nameOnCertificateDoc").removeAttr('required');
   	 if(gender == 'Male'){
   		 previewDescription = salutationArray[0];
   		 $("#description").text(previewDescription);
   	 }else{
   		 previewDescription = salutationArray[1];
   		 $("#description").text(previewDescription);
   	 }
   	//$("#verifyInformation").css('display','none'); 
    $('#saveWithPaymentButton').attr('disabled', false);
    $('#saveForFreeButton').attr('disabled', false); 
   	console.log('No');
     }
    
});  
$('#previewConfirmation').change(function() {
	if (this.checked) { 
		$('#saveWithPaymentButton').attr('disabled', false);
		$('#saveForFreeButton').attr('disabled', false);
	}  else{  
		$('#saveWithPaymentButton').attr('disabled', true);  
		$('#saveForFreeButton').attr('disabled', true);    
	}  
});
//husband or spouse not mandatory
/* $(".submitButton").click(function(){
	var selected = $(".nameOnCertificateClass:checked");
	if(!selected.val()){
        alert('Please mention if the Certicate is to be provided in Husbands name!');
        return false;
    }
	
}); */


/*$('#addressConfirmation').click(function(){
    if (this.checked) {
        $('#postalAddress').css('display', 'block');
        
        $('#saveWithPaymentButton').css('display', 'block');
        
        $('#saveForFreeButton').css('display', 'none');
        $('#marksheetAddressNotice').css('display', 'none');
        
        $('#addressDiv').css('display', 'block');
        $('#postalAddress').removeAttr('disabled');
        totalCost = charges + 100;
        if(isCertificate == true){
			totalCost = totalCost + parseFloat(0.18 * totalCost);
		}
        $("#amount").val(totalCost);
        $("#chargesSpan").text(totalCost);
        
        
        alert('Total Amount Payable = ' + $("#amount").val());
    }else{
    	$('#addressDiv').css('display', 'none');
    	$('#postalAddress').css('display', 'none');
    	$('#postalAddress').attr('disabled', 'true');
    	
    	$('#marksheetAddressNotice').css('display', 'block');
    	totalCost = charges ;
        if(isCertificate == true){
			totalCost = totalCost + parseFloat(0.18 * totalCost);
		}
    	$("#amount").val(totalCost);
    	$("#chargesSpan").text(totalCost);
    	
    	if(charges == 0){
    		$('#saveWithPaymentButton').css('display', 'none');
    		
    		$('#saveForFreeButton').css('display', 'block');
    		alert('Total Amount Payable = ' + $("#amount").val());
    	}else{
    		$('#saveForFreeButton').css('display', 'none');
    		alert('Total Amount Payable = ' + $("#amount").val());
    	}
    }
});*/


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

<script>
function callShareCertificate(){		
	var body = {
		'sapId' : '${usersapId}',
		'productType' : 'PG'
	};
	$.ajax({
		url : '/social-media/m/addCredentialLinkedIn',
		type : 'POST',
		data : JSON.stringify(body),
		contentType: "application/json",
        dataType : "json",
	}).done(
		function(data) {
			//window.open(data.return_url);
			window.location.replace( data.return_url );
	}).fail(
		function(xhr) {
			alert('Error while loading the page, Please re-try!');
	});
}


function shareCertificate(){		
	var body = {
		'sapId' : '${usersapId}',
		'productType' : 'PG'
	};
	$.ajax({
		url : '/social-media/m/shareCertificateLinkedIn',
		type : 'POST',
		data : JSON.stringify(body),
		contentType: "application/json",
        dataType : "json",
	}).done(
			function(data) {
				if(data.return_url){
					window.open(data.return_url,'_self');

				}

		}).fail(
			function(xhr) {
				alert('Error while loading the page, Please re-try!');
		});
}
	
	// default select send to address
	$('#postalAddress').css('display', 'block');
    
    $('#saveWithPaymentButton').css('display', 'block');
    
    $('#saveForFreeButton').css('display', 'none');
    $('#marksheetAddressNotice').css('display', 'none');
    
    $('#addressDiv').css('display', 'block');
    $('#postalAddress').removeAttr('disabled');
    totalCost = charges + 100;
    if(isCertificate == true){
		totalCost = totalCost + parseFloat(0.18 * totalCost);
	}
    $("#amount").val(totalCost);
    $("#chargesSpan").text(totalCost);
    
    
    alert('Total Amount Payable = ' + $("#amount").val());
</script>
		
    </body>
</html>