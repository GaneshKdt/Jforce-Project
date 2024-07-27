<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<%@page import="com.nmims.beans.StudentExamBean"%>
<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Verify Registration Information" name="title" />
</jsp:include>



<body class="inside">

	<%@ include file="header.jsp"%>

	<%
	StudentExamBean student = (StudentExamBean)session.getAttribute("studentExam");
	String firstName = student.getFirstName();
	String lastName = student.getLastName();
	String sapId = student.getSapid();
	String studentProgram = student.getProgram();
	String email = student.getEmailId();
	String mobile = student.getMobile();
	String altPhone = student.getAltPhone();
	String enrollmentMonth = student.getEnrollmentMonth();
	String enrollmentYear = student.getEnrollmentYear();
	String validityEndMonth = student.getValidityEndMonth();
	String validityEndYear = student.getValidityEndYear();
	boolean allInfoPresent = true;
	
	if(email == null || "".equals(email.trim()) || mobile == null || "".equals(mobile.trim())){
		allInfoPresent = false;
	}
	 if(email == null || "".equals(email.trim())){
		email = "";
	}
	if(mobile == null || "".equals(mobile.trim())){
		mobile = "";
	}
	if(altPhone == null || "".equals(altPhone.trim())){
		altPhone = "";
	} 
	%>
	<section class="content-container">
		<div class="container-fluid customTheme">
		<%@ include file="messages.jsp"%>
			<div class="row"><legend>Verify Registration Information</legend></div>
			
			<div class="panel-body">
			
			<div class="col-md-10">
			
			<form:form  action="selectSubjects" method="post" modelAttribute="examBooking" >
			<fieldset>
				<!-- <div class="titleContainer titleContainerResultIns">
					<a href="resources_2015/ExamRegistrationProcess.pdf" target="_blank" ><b><i class="fa fa-download fa-lg"></i> Download Exam Registration Process Guide</b></a>
				</div> -->
				 
				<div class="titleContainer titleContainerResultIns">
					<p>Email</p>
					<h3><%=email %></h3>
				</div>

				<div class="titleContainer titleContainerResultIns">
					<p>Mobile</p>
					<h3><%=mobile %></h3>
				</div>

				<div class="titleContainer titleContainerResultIns">
					<p>Alternate Phone</p>
					<h3><%=altPhone %></h3>
				</div>

				<div class="titleContainer titleContainerResultIns">
					<p>Program</p>
					<h3><%=studentProgram %></h3>
				</div>

				<div class="titleContainer titleContainerResultIns">
					<p>Enrolled in</p>
					<h3><%=enrollmentMonth %>-<%=enrollmentYear %></h3>
				</div>

				<div class="titleContainer titleContainerResultIns">
					<p>Registration History</p>
					<h3><c:choose>
							<c:when test="${not empty registrationList}">
						<c:forEach var="registration" items="${registrationList}" varStatus="status">
							Sem ${registration.sem}: ${registration.month}-${registration.year}<br>
						</c:forEach>
							</c:when>
							<c:otherwise>
							Not Available
							</c:otherwise>
						</c:choose>
					</h3>
				</div>



				<div class="controls">
					<c:if test="${isExamRegistraionLive == 'true' }">
						<button id="submit" name="submit" type="button" class="btn btn-primary" onClick="document.getElementById('contactInfo').style.display = '';">Edit Contact Information</button>
						<button id="proceed" name="submit" class="btn btn-primary" formaction="selectSubjectsForm">My Info. is Correct, Proceed</button>
					</c:if>
					<button id="cancel" name="cancel" class="btn btn-danger" onclick="window.location.href='/studentportal/home'" formnovalidate="formnovalidate">Cancel</button>
				</div>


			</fieldset>
			</form:form>
			</div>
			
			<div id="contactInfo" class="col-md-6 panel-body">
			Note: You cannot proceed with Exam Registration till your contact information is updated.
				<form id="updateContactInfo" action="updateContactInfo" method="post">
                <fieldset>
                <div class="row">          
	              <div class="col-sm-18">
	              
	              
	               <div class="form-group">
                      <label for="emailId">Email</label>
                      <input type="email" required="required" class="form-control" id="emailId" name="emailId" placeholder="Enter Email" value="<%=email %>">
                    </div>
                    
                    <div class="form-group">
                      <label for="mobile">Mobile</label>
                      <input type="tel" required="required" class="form-control" id="mobile" name="mobile" placeholder="Enter Mobile" value="<%=mobile %>">
                    </div>

					<div class="form-group">
                      <label for="mobile">Alternate Phone</label>
                      <input type="tel" class="form-control" id="altPhone" name="altPhone" placeholder="Enter Alternate Phone" value="<%=altPhone %>">
                    </div>
                    
                     <div class="form-group">
                      <button type="submit" class="customBtn red-btn" formaction="updateContactInfo">Update Information</button>
                      <button type="button" class="customBtn cancleBtn" onClick="document.getElementById('contactInfo').style.display = 'none';">Cancel</button>
                    </div>
                    
                    
	              </div>
	              </div>
	              </fieldset>
	              </form>
				</div>
			</div>
			<div class="row">
			
			<div class="col-sm-18">
				<p align="justify">
					<b>Note:</b> 
					Please verify information displayed here before proceeding with Exam booking. 
					If you find any discrepancy, please report it to 
					<a href="mailto:ngasce@nmims.edu" target="_top">ngasce.exams@nmims.edu</a>
					 immediately. <br>We recommend you to complete the Exam Registration using Google Chrome or Mozilla Firefox.
				</p>
			</div>
						
			</div>

					
		</div>
	</section>

	<script type="text/javascript">
	document.getElementById('contactInfo').style.display = 'none';
	
	<%
	if(!allInfoPresent){
	%>
		document.getElementById('contactInfo').style.display = '';
		document.getElementById('proceed').disabled = true;
	<%}%>
	</script>


	<jsp:include page="footer.jsp" />

</body>
</html>
