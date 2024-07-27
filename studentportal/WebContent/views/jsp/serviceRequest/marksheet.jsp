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
<jsp:param value="Enter Service Request Information"  name="title" />
</jsp:include>


<body class="inside">

<%@ include file="../header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
         <div class="row"><legend>Enter Service Request Information</legend></div>
        <form:form  action="saveSingleBook" method="post" modelAttribute="sr" >
			<fieldset>
			
       
        <%@ include file="../messages.jsp"%>
		<div class="panel-body">
			<div>
			Dear Student, You have chosen below Service Request. Please fill in required information. 
			</div>
			<br>
			
			<div class="col-md-6 column">
			
				
				<div class="form-group">
					<form:label path="serviceRequestType" for="serviceRequestType">Service Request Type:</form:label>
					${sr.serviceRequestType }
					<form:hidden path="serviceRequestType"/>
				</div>
				
				<c:if test="${charges != 0 && not empty charges}">
					<div class="form-group">
						<label>Charges:</label>
						INR. ${charges}/-
						<form:hidden path="amount" value="${charges}"/>
					</div>
				</c:if>
				
				
					<div class="form-group">
						<form:select id="year" path="year"  required="required"	 class="form-control"   itemValue="${sr.year}">
							<form:option value="">Select Exam Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
					
					<div class="form-group">
						<form:select id="month" path="month"  required="required"	 class="form-control"   itemValue="${sr.month}">
							<form:option value="">Select Exam Month</form:option>
							<form:option value="Apr">Apr</form:option>
							<form:option value="Jun">Jun</form:option>
							<form:option value="Sep">Sep</form:option>
							<form:option value="Dec">Dec</form:option>
						</form:select>
					</div>
					
					<div class="form-group">
						<form:select id="sem" path="sem"  required="required"	 class="form-control"   itemValue="${sr.sem}">
							<form:option value="">Select Semester</form:option>
							<form:option value="1">1</form:option>
							<form:option value="2">2</form:option>
							<form:option value="3">3</form:option>
							<form:option value="4">4</form:option>
						</form:select>
					</div>
				
				
					<div class="form-group">
						<label class="control-label" for="submit"></label>
						<div class="controls">
							<button id="submit" name="submit"
								class="btn btn-large btn-primary" formaction="checkMarksheetHistory" class="form-control">Proceed</button>
							
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

 <%-- <!DOCTYPE html>

<html lang="en">
  <%@page import="org.apache.jasper.tagplugins.jstl.core.ForEach"%>
<%@page import="java.util.ArrayList"%>
  
    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
	
    
    <jsp:include page="../common/jscss.jsp">
	<jsp:param value="Enter Service Request Information" name="title"/>
    </jsp:include>
    

    
    <body>
    
    	<%@ include file="../common/header.jsp" %>
    	
    	
        
        <div class="sz-main-content-wrapper">
        
        	<%@ include file="../common/breadcrum.jsp" %>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="../common/left-sidebar.jsp">
								<jsp:param value="Service Request" name="activeMenu"/>
							</jsp:include>
							
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="../common/studentInfoBar.jsp" %>
              						
              						
              						<div class="sz-content">
								
										<h2 class="red text-capitalize">${sr.serviceRequestType }</h2>
										<div class="clearfix"></div>
		              					<div class="panel-content-wrapper">
		              					<%@ include file="../common/messages.jsp" %>
		              						<p>
											Dear Student, You have chosen below Service Request. Please fill in required information below before proceeding for Payment. 
											</p>
											
											<div class="clearfix"></div>
											<form:form  action="saveSingleBook" method="post" modelAttribute="sr" enctype="multipart/form-data">
											<fieldset>
											<div class="col-md-6 column">
			
				
											<div class="form-group">
												<form:label path="serviceRequestType" for="serviceRequestType">Service Request Type:</form:label>
												<p>${sr.serviceRequestType }</p>
												<form:hidden path="serviceRequestType"/>
											</div>
											
											<c:if test="${charges != 0 && not empty charges}">
												<div class="form-group">
													<label>Charges:</label>
													<p>INR. ${charges}/-</p>
													<form:hidden path="amount" value="${charges}"/>
												</div>
											</c:if>
											
											
												<div class="form-group">
													<form:select id="year" path="year"  required="required"	 class="form-control"   itemValue="${sr.year}">
														<form:option value="">Select Exam Year</form:option>
														<form:options items="${yearList}" />
													</form:select>
												</div>
												
												<div class="form-group">
													<form:select id="month" path="month"  required="required"	 class="form-control"   itemValue="${sr.month}">
														<form:option value="">Select Exam Month</form:option>
														<form:option value="Apr">Apr</form:option>
														<form:option value="Jun">Jun</form:option>
														<form:option value="Sep">Sep</form:option>
														<form:option value="Dec">Dec</form:option>
													</form:select>
												</div>
												
												<div class="form-group">
													<form:select id="sem" path="sem"  required="required"	 class="form-control"   itemValue="${sr.sem}">
														<form:option value="">Select Semester</form:option>
														<form:option value="1">1</form:option>
														<form:option value="2">2</form:option>
														<form:option value="3">3</form:option>
														<form:option value="4">4</form:option>
													</form:select>
												</div>
											
											
												<div class="form-group">
													<label class="control-label" for="submit"></label>
													<div class="controls">
														<button id="submit" name="submit"
															class="btn btn-large btn-primary" formaction="checkMarksheetHistory" class="form-control">Proceed</button>
															<button id="backToSR" name="BacktoNewServiceRequest" class="btn btn-danger"
															formaction="selectSRForm" formnovalidate="formnovalidate">Back to New Service Request</button>
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
        <jsp:include page="../common/footer.jsp"/>
            
		
    </body>
</html> --%>
 <!DOCTYPE html>

<html lang="en">
<%@page import="org.apache.jasper.tagplugins.jstl.core.ForEach"%>
<%@page import="java.util.ArrayList"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" var="BASE_URL_STUDENTPORTAL_STATIC_RESOURCES"/>
    
    <jsp:include page="../common/jscss.jsp">
	<jsp:param value="Enter Service Request Information" name="title"/>
    </jsp:include>
    
    <%
    StudentStudentPortalBean student = (StudentStudentPortalBean)session.getAttribute("student_studentportal");
    %>
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
.marksheet-header p{
color:black!important;
}
</style>

    <body>
    
    	<%@ include file="../common/header.jsp" %>
    	
    	
        
        <div class="sz-main-content-wrapper">
        
        	<%@ include file="../common/breadcrum.jsp" %>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
                    		<div id="sticky-sidebar"> 
	              				<jsp:include page="../common/left-sidebar.jsp">
									<jsp:param value="Service Request" name="activeMenu"/>
								</jsp:include>
							</div>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="../common/studentInfoBar.jsp" %>
              						
              						
              						<div class="sz-content">
								
										<h2 class="red text-capitalize">${sr.serviceRequestType }</h2>
										<div class="clearfix"></div>
		              					<div class="panel-content-wrapper">
		              					<%@ include file="../common/messages.jsp" %>
		              						<p>
											Dear Student, You have chosen below Service Request. Please fill in required information below before proceeding for Payment. 
											</p>
											
											<div class="clearfix"></div>
											<form:form  action="saveSingleBook" method="post" modelAttribute="sr" enctype="multipart/form-data">
											<fieldset>
											<div class="col-md-10 column">
			
				
											<div class="form-group">
												<form:label path="serviceRequestType" for="serviceRequestType">Service Request Type:</form:label>
												<p>${sr.serviceRequestType }</p>
												<form:hidden path="serviceRequestType"/>
											</div>
											
											   <form:hidden id="marksheetDetailRecord1" path="marksheetDetailRecord1"/>
										       <form:hidden id="marksheetDetailRecord2" path="marksheetDetailRecord2"/>
										       <form:hidden id="marksheetDetailRecord3" path="marksheetDetailRecord3"/>
										       <form:hidden id="marksheetDetailRecord4" path="marksheetDetailRecord4"/>
												<form:hidden id="marksheetDetailRecord5" path="marksheetDetailRecord5"/>
												<form:hidden id="marksheetDetailRecord6" path="marksheetDetailRecord6"/>
											
											
												<div class="form-group">
													<label>Charges:</label>
													<p>INR. ${courierAmount}/-</p>
													<form:hidden path="courierAmount" id="courierAmount" value="${courierAmount}"/>
												</div>
											
											<div class="controls">
											<div class="form-group">
											
												<button type="button" id="addMarksheetRecord" class="btn btn-default btn-sm"><span class="glyphicon glyphicon-plus"></span> Add Marksheet Request</button>
												<!-- <button type="button" id="removeMarksheetRecord" class="btn btn-danger btn-sm"><span class="glyphicon glyphicon-minus"></span> Remove Marksheet Request</button>&nbsp;&nbsp;<span class="red">To delete any marksheet kindly Un-select and click on <b>Remove Marksheet</b></span> -->
        										 
											</div>
											</div>
											<table id="markSheetTable">
											<tr><td><label>Semester Cleared Marksheets: </label> </td> </tr>    
											
												<tr>
												<td>Exam Year</td>
												<td>Exam Month</td>
												<td>Semester</td>
												<td>Select</td>
												
												</tr>
												<c:forEach var="list" items="${yearMonthList}">
												     
												    <tr>
														<td>
														<div class="form-group">
															<form:select id="year1" path="year"  disabled="true" class="form-control makeDisable year"   itemValue="${sr.year}">
																<form:option value="${list.writtenYear}"  items="${list.writtenYear}" />
															</form:select>
														</div> 
														</td>
														
														<td>
														<div class="form-group">
															<form:select id="month1" path="month"  disabled="true"	 class="form-control makeDisable month"   itemValue="${sr.month}">
																<form:option value="${list.writtenMonth}"  items="${list.writtenYear}" />
															</form:select>
														</div> 
														</td> 
														<td>
														<div class="form-group">
															<form:select id="sem1" path="sem" disabled="true"  class="form-control makeDisable sem"   itemValue="${sr.sem}">
																<form:option value="${list.sem}"  items="${list.sem}" />
															</form:select>
														</div> 
														</td>
														<td>
															<div class="form-group">
															<input type="checkbox" class="selectCheckBox"  name="checkBox1" id="checkBox1"   >
															</div> 
														</td> 
														<td>
															<div class="form-group">
																<button type="button" id="previewMarksheet" class="btn btn-default btn-sm previewMarksheet"   onclick="showMarks(this.id)" 
																 data-program="<%=student.getProgram()%>" data-programStructure="<%=student.getPrgmStructApplicable()%>" data-sapid="<%=student.getSapid()%>" data-mode="<%=student.getExamMode()%>" >Marks Preview</button>
															</div>
														</td> 
														
														<td>
														 	<!-- Image loader -->
															<div class="form-group" id='loader' style='display: none;'>
															  <img src='/studentportal/resources_2015/gifs/loading.gif' width='90px' height='90px'>
															</div>
															<!-- Image loader -->
														</td>
													</tr>
												</c:forEach>
												
												<tr><td><label>Additional Marksheets: </label> </td> </tr>    
												<tr>
												<td><div class="form-group">
													<form:select id="year1" path="year"  class="form-control makeDisable year"  itemValue="${sr.year}">
														<form:option value="">Select Exam Year</form:option>
														<form:options items="${yearList}" />
													</form:select>
												</div></td>
												
												<td><div class="form-group">
													<form:select id="month1" path="month" class="form-control makeDisable month"   itemValue="${sr.month}">
														<form:option value="">Select Exam Month</form:option>
														<form:option value="Apr">Apr</form:option>
														<form:option value="Jun">Jun</form:option>
														<form:option value="Sep">Sep</form:option>
														<form:option value="Dec">Dec</form:option>
													</form:select>
												</div></td>
												
												<td class="semester"><div class="form-group">
													<form:select id="sem1" path="sem"  class="form-control makeDisable sem"   itemValue="${sr.sem}">
														<form:option value="">Select Semester</form:option>
														<form:option value="1">1</form:option>
														<form:option value="2">2</form:option>
														<form:option value="3">3</form:option>
														<form:option value="4">4</form:option>
														<c:if test="${programName eq 'BBA'}">
														<form:option value="5">5</form:option>
														<form:option value="6">6</form:option>
														</c:if>
													</form:select>
												</div></td>
										
												<td>
												<div class="form-group">
												<input type="checkbox" class="selectCheckBox"  name="checkBox1" id="checkBox1" >
												</div>
												</td>
												
												
												 <td>
													<div class="form-group">
														<button type="button" id="previewMarksheet" class="btn btn-default btn-sm previewMarksheet" style="display:none"  onclick="showMarks(this.id)" 
														 data-program="<%=student.getProgram()%>" data-programStructure="<%=student.getPrgmStructApplicable()%>" data-sapid="<%=student.getSapid()%>" data-mode="<%=student.getExamMode()%>" >Marks Preview</button>
													</div>
												</td> 
												
												<td>
												 	<!-- Image loader -->
													<div class="form-group" id='loader' style='display: none;'>
													  <img src='/studentportal/resources_2015/gifs/loading.gif' width='90px' height='90px'>
													</div>
													<!-- Image loader -->
												</td>
												</tr>
											  </table>
											  
											  <div id="dialog" class = "modal hide" style="display:none;"></div>
											  
											  <form:checkbox path="wantAtAddress" value="Yes" id="addressConfirmation"
													style="width:15px;height:15px;margin:5px;display:none" checked="checked"/>
												<div class="form-group">
													<label>
													<span> &nbsp;I want my Marksheet at my address (Shipping Charges INR. 100/-)</span>
													</label>
												</div>
												<div class="form-group">
													<label>
													<input type="checkbox"  id="infoConfirmation" style="width:15px;height:15px;margin:5px;"/>
													I declare that I have read, understood and accepted the information on the processing of my marksheet
													</label>
												</div>
												<div class="form-group" id="addressDiv" style="display:none">
													<%-- <label for="postalAddress">Confirm/Edit Address</label>
													<textarea name="postalAddress" class="form-control" id="postalAddress"  cols="50" rows = "5"><%=student.getAddress() %></textarea> --%>
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
													<div class="form-group">
													<label class="control-label" for="submit"></label>
													<div class="controls">
														<button id="submit" name="submit"
															class="btn btn-large btn-primary" formaction="checkMarksheetHistory" class="form-control">Proceed</button>
															<button id="backToSR" name="BacktoNewServiceRequest" class="btn btn-danger"
															formaction="selectSRForm" formnovalidate="formnovalidate">Back to New Service Request</button>
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
      		<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
		  <div class="modal-dialog" role="document">
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
		        <h4 class="modal-title" id="myModalLabel"><i class="fa-solid fa-triangle-exclamation fa-lg"></i> Alert: You are issuing a marksheet for the same semester,year and month</h4>
		      </div>
		      <div class="modal-body">
		        <p> We observed that you have selected a marksheet request for the same Semester</p>
		      </div>
		      <div class="modal-footer">
		        <button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
		      </div>
		    </div>
		  </div>
		</div>
		
		
		 <div class="modal fade" id="myModal2" tabindex="-1" role="dialog" aria-labelledby="myModalLabe2">
		  <div class="modal-dialog" role="document">
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
		        <h4 class="modal-title" id="myModalLabel2"><i class="fa-solid fa-triangle-exclamation fa-lg">Marksheet Marks Preview</i> </h4> 
		      </div>
		      <div class="modal-body">
		      <div class="row marksheet-header" style="text-transform: uppercase;">
		      <div class="col-md-7"> 
			      <p>NAME: <%=student.getFirstName()+" "+student.getLastName()%></p>
				  <p>Father's Name: <%=student.getFatherName() %></p>
				  <p>Mother's Name: <%=student.getMotherName() %></p>
				  <p>Program: <%=student.getProgram() %></p>
		      </div>
		      <div class="col-md-5"> 
		      <p >Semester: <span class="marksheetSem"></span></p>
		      <p>Student No: <%=userId %></p> 
		      </div>
		      </div>  
		      
		        <table class="table-container" cellpadding="2" cellspacing="0" border="1">
                  </table>
		      </div>
		      <div class="modal-footer">
		        <button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
		      </div>
		    </div>
		  </div>
		</div> 
		
  		</div>
  		
        <jsp:include page="../common/footer.jsp"/>
            
		<script>
		var previewed=false;
		$("document").ready(function() {
			
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
	                        $('#myModal2').modal('show');
	                	}else{
	                		var table = $(".modal-body").find('table');
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
	                       
	                        $(".table-container").html(table);
	                        $('#myModal2').modal('show');
	                     
	                	}
	      			
	                	//alert("1");
	               },
	               error:function(){
	            	   var table = $(".modal-body").find('table');
	                   
	                   var table="<tr><th>Error</th></tr>";
	                 
	                       table += "<tr><td>Server Error</td><td>" ;
	                   
	                   $(".modal-body").html(table);
	                   $('#myModal2').modal('show');
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
    </body>
</html>