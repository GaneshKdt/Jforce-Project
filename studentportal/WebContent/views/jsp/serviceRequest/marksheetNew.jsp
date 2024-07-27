 <!DOCTYPE html>

<html lang="en">
<%@page import="org.apache.jasper.tagplugins.jstl.core.ForEach"%>
<%@page import="java.util.ArrayList"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" var="BASE_URL_STUDENTPORTAL_STATIC_RESOURCES"/>
    
    <jsp:include page="../common/jscssNew.jsp">
	<jsp:param value="Enter Service Request Information" name="title"/>
    </jsp:include>
    
    <%
    StudentStudentPortalBean student = (StudentStudentPortalBean)session.getAttribute("student_studentportal");
    %>
<!-- <style>
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
</style> -->

    <body>
    
    	<%@ include file="../common/headerDemo.jsp" %>
    	
    	
        
        <div class="sz-main-content-wrapper">
        <div class="sz-breadcrumb-wrapper">
			<div class="container-fluid">
				<ul class="sz-breadcrumbs">
					<li><a href="/studentportal/home">Student Zone</a></li>
					<li><a href="selectSRForm">Select Service Request</a></li>
					<li>Issuance Of Marksheet</a></li>
				</ul>
			</div>
		</div>
        	<%-- <%@ include file="../common/breadcrum.jsp" %> --%>
        	
            
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
								
										<h2 class="text-danger text-capitalize">${sr.serviceRequestType }</h2>
										<div class="clearfix"></div>
		              					<div class="card card-body">
		              					<%@ include file="../common/messageDemo.jsp" %>
		              						<p>
											Dear Student, You have chosen below Service Request. Please fill in required information below before proceeding for Payment. 
											</p>
											
											<div class="clearfix"></div>
											<form:form  action="saveSingleBook" method="post" modelAttribute="sr" enctype="multipart/form-data">
											<fieldset>
											<div class="col-md-10 column">
			
				
											<div class="form-group">
												<form:label class="fw-bold" path="serviceRequestType" for="serviceRequestType">Service Request Type:</form:label>
												<p>${sr.serviceRequestType }</p>
												<form:hidden path="serviceRequestType"/>
											</div>
											
											   <form:hidden id="marksheetDetailRecord1" path="marksheetDetailRecord1"/>
										       <form:hidden id="marksheetDetailRecord2" path="marksheetDetailRecord2"/>
										       <form:hidden id="marksheetDetailRecord3" path="marksheetDetailRecord3"/>
										       <form:hidden id="marksheetDetailRecord4" path="marksheetDetailRecord4"/>
										       <c:if test="${programName eq 'BBA' || programName eq 'B.Com' || programName eq 'BBA-BA'}">
													<form:hidden id="marksheetDetailRecord5" path="marksheetDetailRecord5"/>
													<form:hidden id="marksheetDetailRecord6" path="marksheetDetailRecord6"/>
												</c:if>
											
											
												<div class="form-group">
													<label class= "fw-bold">Charges:</label>
													<p>INR. ${courierAmount}/-</p>
													<form:hidden path="courierAmount" id="courierAmount" value="${courierAmount}"/>
												</div>
											
											<div class="controls">
											<div class="form-group">
											
												<button type="button" id="addMarksheetRecord" class="btn btn-danger">
												 <i class="fa-solid fa-plus fa-fade"></i> Add Marksheet Request</button>
												<!-- <button type="button" id="removeMarksheetRecord" class="btn btn-danger btn-sm"><span class="glyphicon glyphicon-minus"></span> Remove Marksheet Request</button>&nbsp;&nbsp;<span class="red">To delete any marksheet kindly Un-select and click on <b>Remove Marksheet</b></span> -->
        										 
											</div>
											</div>
											<table  id="markSheetTable">
											<tr><td><label class="fw-bold">Semester Cleared Marksheets: </label> </td> </tr>    
											
												<tr>
												<td>Exam Year</td>
												<td>Exam Month</td>
												<td>Semester</td>
												<td>Select</td>
												
												</tr>
												<c:forEach var="list" items="${yearMonthList}">
												     
												    <tr>
														<td>
														<div class="form-group me-2">
															<form:select id="year1" path="year"  disabled="true" class="form-select makeDisable year"   itemValue="${sr.year}">
																<form:option value="${list.writtenYear}"  items="${list.writtenYear}" />
															</form:select>
														</div> 
														</td>
														
														<td>
														<div class="form-group me-2">
															<form:select id="month1" path="month"  disabled="true"	 class="form-select makeDisable month"   itemValue="${sr.month}">
																<form:option value="${list.writtenMonth}"  items="${list.writtenYear}" />
															</form:select>
														</div> 
														</td> 
														<td>
														<div class="form-group me-2">
															<form:select id="sem1" path="sem" disabled="true"  class="form-select makeDisable sem"   itemValue="${sr.sem}">
																<form:option value="${list.sem}"  items="${list.sem}" />
															</form:select>
														</div> 
														</td>
														<td>
															<div class="form-group ms-3">
															<input type="checkbox" class="selectCheckBox"  name="checkBox1" id="checkBox1"   >
															</div> 
														</td> 
														<td>
															<div class="form-group">
																<button type="button" id="previewMarksheet" class="btn btn-danger previewMarksheet"   onclick="showMarks(this.id)" 
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
												
												<tr><td><label class="fw-bold">Additional Marksheets: </label> </td> </tr>    
												<tr>
												<td><div class="form-group me-2">
													<form:select id="year1" path="year"  class="form-select makeDisable year"  itemValue="${sr.year}">
														<form:option value="">Select Exam Year</form:option>
														<form:options items="${yearList}" />
													</form:select>
												</div></td>
												
												<td><div class="form-group me-2">
													<form:select id="month1" path="month" class="form-select makeDisable month"   itemValue="${sr.month}">
														<form:option value="">Select Exam Month</form:option>
														<form:option value="Apr">Apr</form:option>
														<form:option value="Jun">Jun</form:option>
														<form:option value="Sep">Sep</form:option>
														<form:option value="Dec">Dec</form:option>
													</form:select>
												</div></td>
												
												<td class="semester"><div class="form-group me-2">
													<form:select id="sem1" path="sem"  class="form-select makeDisable sem"   itemValue="${sr.sem}">
														<form:option value="">Select Semester</form:option>
														<form:option value="1">1</form:option>
														<form:option value="2">2</form:option>
														<form:option value="3">3</form:option>
														<form:option value="4">4</form:option>
														<c:if test="${programName eq 'BBA' || programName eq 'B.Com' || programName eq 'BBA-BA'}">
														<form:option value="5">5</form:option>
														<form:option value="6">6</form:option>
														</c:if>
													</form:select>
												</div></td>
										
												<td>
												<div class="form-group ms-3">
												<input type="checkbox" class="selectCheckBox"  name="checkBox1" id="checkBox1" >
												</div>
												</td>
												
												
												 <td>
													<div class="form-group">
														<button type="button" id="previewMarksheet" class="btn btn-danger previewMarksheet" style="display:none"  onclick="showMarks(this.id)" 
														 data-program="<%=student.getProgram()%>" data-programStructure="<%=student.getPrgmStructApplicable()%>" data-sapid="<%=student.getSapid()%>" data-mode="<%=student.getExamMode()%>" >Marks Preview</button>
													</div>
												</td> 
												
												<td>
												 	<!-- Image loader -->
													<div class="form-group " id='loader' style='display: none;'>
													  <img src='/studentportal/resources_2015/gifs/loading.gif' width='90px' height='90px'>
													</div>
													<!-- Image loader -->
												</td>
												</tr>
											  </table>
											  
											  <div id="dialog" class = "modal hide" style="display:none;"></div>
											  <!-- style="width:15px;height:15px;margin:5px;display:none" -->
												<div class="form-group mt-2">
											  <form:checkbox class="d-none" path="wantAtAddress" value="Yes" id="addressConfirmation"
													 checked="checked"/>
													<label for="addressConfirmation" class="fw-bold">
													<span>I want my Marksheet at my address (Shipping Charges INR. 100/-)</span>
													</label>
												</div>
												<div class="form-check">
													<label class="fw-bold">
													<input class="form-check-input" type="checkbox" value="" id="infoConfirmation">
													<!-- <input type="checkbox"  id="infoConfirmation" style="width:15px;height:15px;margin:5px;"/> -->
													I declare that I have read, understood and accepted the information on the processing of my marksheet
													</label>
												</div>
												<div class="form-group" id="addressDiv" style="display:none">
													<%-- <label for="postalAddress">Confirm/Edit Address</label>
													<textarea name="postalAddress" class="form-control" id="postalAddress"  cols="50" rows = "5"><%=student.getAddress() %></textarea> --%>
													<h5 class="text-danger text-capitalize fs-6 fw-bold mt-3">SHIPPING ADDRESS</h5>
																					<div class="clearfix"></div>
																							<div class="form-group fw-bold">
																								<form:label for="houseNoName" path="houseNoName"> (*) Address Line 1 : House Details</form:label>
																								<form:input type="text" path="houseNoName"  class="form-control shippingFields" id="houseNameId" 
																									   name="shippingHouseName"  value="${student.houseNoName}" required = "required"/>
																							</div>
																							<div class="form-group fw-bold">
																								<form:label for="street" path="street"> (*) Address Line 2 : Street Name</form:label>
																								<form:input type="text" path="street" class="form-control shippingFields" id="shippingStreetId"
																								   name="shippingStreet"  value="${student.street}" required = "required"/>
																							</div>
																							<div class="form-group fw-bold">
																								<form:label for="locality" path="locality"> (*) Address Line 3 : Locality Name</form:label>
																								<form:input type="text" path="locality" class="form-control shippingFields" id="localityNameId" 
																								   name="shippingLocalityName"  value="${student.locality}" required = "required"/>
																							</div>
																							<div class="form-group fw-bold">
																								<form:label for="landMark" path="landMark"> (*) Address Line 4 : Nearest LandMark</form:label>
																								<form:input type="text"  path="landMark" class="form-control shippingFields" id="nearestLandMarkId" 
																								   name="shippingNearestLandmark"  value="${student.landMark}" required = "required"/>
																							</div>
																							<div class="form-group fw-bold">
																								<form:label for="pin" path="pin"> (*) Postal Code</form:label>
																								<form:input type="text" class="form-control shippingFields numonly" id="postalCodeId" 
																								   name="shippingPostalCode"  value="${student.pin}" maxlength="6" path="pin"
																								   required="required"/>
																							</div>
																							<br>
																							<span class="well-sm" id="pinCodeMessage"></span>	   
																							<div class="form-group fw-bold">
																								<form:label for="shippingCityId" path="city"> (*) Shipping City</form:label>
																								<form:input type="text" class="form-control shippingFields" id="shippingCityId" 
																									   name="shippingCity"  path="city"  value="${student.city}" readonly = "true"  
																									   onkeypress= "return onlyAlphabets(event,this);" />
																							</div>
																							<div class="form-group fw-bold">
																								<form:label for="stateId" path="state"> (*) Shipping State</form:label>
																								<form:input type="text" path="state" class="form-control shippingFields bg-light" id="stateId"
																								   name="shippingState"  value="${student.state}" readonly = "true"
																								   onkeypress="return onlyAlphabets(event,this);"/>
																							</div>
																							<div class="form-group fw-bold">
																								<form:label for="countryId" path="country"> (*) Country For Shipping</form:label>
																								<form:input type="text" path="country" class="form-control shippingFields bg-light" id="countryId" 
																								   name="shippingCountry"  value="${student.country}"  readonly = "true"
																								   onkeypress="return onlyAlphabets(event,this);"/>
																							</div> 
																							<div class="row">
																								<div class="form-group col-md-6 fw-bold">
																									<form:label for="abcId" path="abcId">Academic Bank of Credits Id</form:label>
																									<form:input type="text" id="abcId" class="form-control bg-light" name="abcId" placeholder="Academic Bank of Credits Id (Optional)" path="abcId" readonly="true" />
																								</div>
																								<div class="form-group col-md-6 card mt-3">
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
															class="btn btn-danger" formaction="checkMarksheetHistory" class="form-control">Proceed</button>
															<button id="backToSR" name="BacktoNewServiceRequest" class="btn btn-dark"
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
		        <button type="button" class="close" data-bs-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
		        <h4 class="modal-title" id="myModalLabel"><i class="fa-solid fa-triangle-exclamation fa-lg"></i> Alert: You are issuing a marksheet for the same semester,year and month</h4>
		      </div>
		      <div class="modal-body">
		        <p> We observed that you have selected a marksheet request for the same Semester</p>
		      <div class="modal-footer">
		        <button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
		      </div>
		      </div>
		    </div>
		  </div>
		</div>

		<div class="modal fade" id="myModal2" tabindex="-1" role="dialog"
			aria-labelledby="myModalLabe2">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<h5 class="modal-title" id="myModalLabel2">Marksheet Marks
							Preview</h5>
						<button type="button" class="btn-close" data-bs-dismiss="modal"
							aria-label="Close"></button>
					</div>
					<div class="modal-body">
						<div class="row marksheet-header"
							style="text-transform: uppercase;">
							<div class="col-md-7">
								<p class="text-dark">
									NAME:
									<%=student.getFirstName() + " " + student.getLastName()%></p>
								<p class="text-dark">
									Father's Name:
									<%=student.getFatherName()%></p>
								<p class="text-dark">
									Mother's Name:
									<%=student.getMotherName()%></p>
								<p class="text-dark">
									Program:
									<%=student.getProgram()%></p>
							</div>
							<div class="col-md-5">
								<p class="text-dark">
									Semester: <span class="marksheetSem"></span>
								</p>
								<p class="text-dark">
									Student No:
									<%=userId%></p>
							</div>
						</div>

						<table class="table table-bordered">
						</table>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-danger"
							data-bs-dismiss="modal">Close</button>
					</div>
				</div>


			</div>
					</div>
				</div>
			<%--  <div class="modal fade" id="myModal2" tabindex="-1" role="dialog" aria-labelledby="myModalLabe2">
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
		</div>  --%>


		<jsp:include page="../common/footerDemo.jsp"/>
            
		
		
	
			<script type="text/javascript"
		src="${pageContext.request.contextPath}/assets/js/serviceRequest/marksheet.js"></script>
    </body>
</html>