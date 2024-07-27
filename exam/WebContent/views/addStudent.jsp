<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<!-- Deprecated. This JSP file was used by the /admin/editStudent API, which now uses the editStudent JSP  -->
<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Add/Edit Student Details" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Add/Edit Student Details</legend></div>
		<div class="panel-body">
		<form:form  action="addStudentMarks" method="post" modelAttribute="student">
			<fieldset>
			<div class="col-md-6 column">
		

				<%if("true".equals((String)request.getAttribute("edit"))){ %>

				<%} %>
				<div class="form-group">
						<label class="control-label" for="enrollmentYear">Enrollment Year</label>
						<form:select id="enrollmentYear" path="enrollmentYear" type="text"	placeholder="Year" class="form-control"   itemValue="${student.enrollmentYear}">
							<form:option value="">Select Enrollment Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					
					<div class="form-group">
						<label class="control-label" for="enrollmentMonth">Enrollment Month</label>
						<form:select id="enrollmentMonth" path="enrollmentMonth" type="text" placeholder="Month" class="form-control"  itemValue="${student.enrollmentMonth}">
							<form:option value="">Select Enrollment Month</form:option>
							<form:option value="Jan">Jan</form:option>
							<form:option value="Feb">Feb</form:option>
							<form:option value="Mar">Mar</form:option>
							<form:option value="Apr">Apr</form:option>
							<form:option value="May">May</form:option>
							<form:option value="Jun">Jun</form:option>
							<form:option value="Jul">Jul</form:option>
							<form:option value="Aug">Aug</form:option>
							<form:option value="Sep">Sep</form:option>
							<form:option value="Oct">Oct</form:option>
							<form:option value="Nov">Nov</form:option>
							<form:option value="Dec">Dec</form:option>
						</form:select>
					</div>
					
						<%-- <div class="form-group">
									 <label>oldValidityEndYear: </label>
										<form:input id="oldValidityEndYear" path="oldValidityEndYear" type="text"	placeholder="Year" class="form-control"   itemValue="${student.oldValidityEndYear}"  readonly="true"/>
									</div> --%>
					
					<div class="form-group">
						<label class="control-label" for="validityEndYear">Validity End Year</label>
						<form:select id="validityEndYear" path="validityEndYear" type="text"	placeholder="Year" class="form-control"   itemValue="${student.validityEndYear}">
							<form:option value="">Select Validity End Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
					
				<%-- 	<div class="form-group">
									<label>oldValidityEndMonth </label>
										<form:input id="oldValidityEndMonth" path="oldValidityEndMonth" type="text"	placeholder="Month" class="form-control"   itemValue="${student.oldValidityEndMonth}" readonly="true" />
									</div> --%>
				
					
					<div class="form-group">
						<label class="control-label" for="validityEndMonth">Validity End Month</label>
						<form:select id="validityEndMonth" path="validityEndMonth"  class="form-control"  itemValue="${student.validityEndMonth}">
							<form:option value="">Select Validity End Month</form:option>
							<form:option value="Mar">Mar</form:option>
							<form:option value="Jun">Jun</form:option>
							<form:option value="Aug">Aug</form:option>
							<form:option value="Sep">Sep</form:option>
							<form:option value="Oct">Oct</form:option>
							<form:option value="Dec">Dec</form:option>
							<form:option value="Aug">Aug</form:option>
							<form:option value="Oct">Oct</form:option>
						</form:select>
					</div>
					
					<div class="form-group">
							
							<label class="control-label" for="sapid">SAP ID</label>
							<%if("true".equals((String)request.getAttribute("edit")) && roles.indexOf("Exam Admin") == -1){ %>
								<form:input id="sapid" path="sapid" type="text" placeholder="SAP ID" class="form-control" value="${student.sapid}"  readonly="true" />
							<%}else{ %>
								<form:input id="sapid" path="sapid" type="text" placeholder="SAP ID" class="form-control" value="${student.sapid}" />
							<%} %>
					</div>
					
					<div class="form-group">
						<label class="control-label" for="sem">Semester</label>
						<form:select id="sem" path="sem"  class="form-control" required="required" value="${student.sem}" readonly = "true">
							<form:option value="">Select Semester</form:option>
							<form:option value="1">1</form:option>
							<form:option value="2">2</form:option>
							<form:option value="3">3</form:option>
							<form:option value="4">4</form:option>
							<form:option value="5">5</form:option>
						</form:select>
					</div>
					
					<div class="form-group">
							<label class="control-label" for="emailId">Email Id</label>
							<form:input id="emailId" path="emailId" type="email" placeholder="Email" class="form-control" value="${student.emailId}" />
					</div>
					<div class="form-group">
							<label class="control-label" for="spouseName">Spouse Name</label>
							<form:input id="spouseName" path="husbandName" type="text" placeholder="Spouse Name" class="form-control" value="${student.husbandName}" />
					</div>
					
					<div class="form-group">
							<label class="control-label" for="dob">DOB</label>
							<form:input id="dob" path="dob" type="text" placeholder="Date Of Birth" class="form-control" value="${student.dob}" />
					</div>
					
					<div class="form-group">
							<label class="control-label" for="mobile">Mobile</label>
							<form:input id="mobile" path="mobile" type="text" placeholder="Mobile" class="form-control" value="${student.mobile}" />
					</div>
					
					<div class="form-group">
						<label class="control-label" for="gender">Gender</label>
						<form:select id="gender" path="gender"  class="form-control"  itemValue="${student.gender}">
							<form:option value="">Select Gender</form:option>
							<form:option value="Male">Male</form:option>
							<form:option value="Female">Female</form:option>
						</form:select>
					</div>
					
				</div>
				
				<div class="col-md-6 column">
					<div class="form-group">
							<label class="control-label" for="firstName">First Name</label>
							<form:input id="firstName" path="firstName" type="text" placeholder="First Name" class="form-control" value="${student.firstName}" />
					</div>
					
					<div class="form-group">
							<label class="control-label" for="lastName">Last Name</label>
							<form:input id="lastName" path="lastName" type="text" placeholder="Last Name" class="form-control" value="${student.lastName}" />
					</div>
					
					<div class="form-group">
							<label class="control-label" for="fatherName">Father Name</label>
							<form:input id="fatherName" path="fatherName" type="text" placeholder="Father Name" class="form-control" value="${student.fatherName}" />
					</div>
					
					<div class="form-group">
							<label class="control-label" for="middleName">Middle Name</label>
							<form:input id="middleName" path="middleName" type="text" placeholder="Middle Name" class="form-control" value="${student.middleName}" />
					</div>
					
					<div class="form-group">
							<label class="control-label" for="motherName">Mother Name</label>
							<form:input id="motherName" path="motherName" type="text" placeholder="Mother Name" class="form-control" value="${student.motherName}" />
					</div>
					
					
					
					<div class="form-group">
						<label class="control-label" for="program">Program</label>
						<form:select id="program" path="program"  class="form-control" required="required" itemValue="${student.program}" >
							<form:option value="">Select Program</form:option>
							<form:options items="${programList}" />
						</form:select>
					</div>
					
					<div class="form-group">
						<label class="control-label" for="centerCode">Information Center</label>
						<form:select id="centerCode" path="centerCode"  class="form-control" required="required" itemValue="${student.centerCode}" >
							<form:option value="">Select IC</form:option>
							<form:options items="${centerList}" />
						</form:select>
					</div>
					
					<div class="form-group">
						<label class="control-label" for="prgmStructApplicable">Program Structure</label>
						<form:select id="prgmStructApplicable" required="required" path="prgmStructApplicable" placeholder="Program Structure" class="form-control"  value="${student.prgmStructApplicable}">
							<form:options items="${progStructListFromProgramMaster}" />

						</form:select>
						
					</div>
					
					<div class="form-group">
						<label class="control-label" for="programStatus">Program Status</label>
						<form:select id="programStatus" path="programStatus" placeholder="Program Status" class="form-control"  value="${student.programStatus}">
							<form:option value="">Select Program Status</form:option>
							<form:option value="Program Terminated">Program Terminated</form:option>
						</form:select>
					</div>
					
					<div class="form-group">
							<label class="control-label" for="programRemarks">Program Remarks</label>
							<form:textarea path="programRemarks" cols="40" rows="5"/>
					</div>
					
					<div class="form-group">
							<form:hidden id="oldProgram" path="oldProgram"  class="form-control" value="${student.oldProgram}" />
					</div>
					


					
					
				</div>
				
				<div class="col-md-6 column">
					<div class="form-group">
							<label class="control-label" for="address">Address</label>
							<form:textarea path="address" cols="40" rows="7"/>
					</div>
					
					
					<!-- Button (Double) -->
					<div class="form-group">
						<label class="control-label" for="submit"></label>
						<div class="controls">
							<%if("true".equals((String)request.getAttribute("edit"))){ %>
								<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="updateStudent">Update</button>
							<%}else	{%>
								<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="addStudent">Submit</button>
							<%} %>
							<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
						</div>
					</div>
				</div>
				
			</fieldset>
		</form:form>

		</div>
		</div>
	
	</section>

	  <jsp:include page="footer.jsp" />


</body>
</html>
