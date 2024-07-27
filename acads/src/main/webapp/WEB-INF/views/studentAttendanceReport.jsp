<%--added by stef on 6-Nov


 <!DOCTYPE html>

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>

<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Student Attendance" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">
			<div class="row">
				<legend>Attendance</legend>
			</div>
			<%@ include file="messages.jsp"%>
			<form:form action="studentAttendanceReport" method="post"
								modelAttribute="searchBean">
								<fieldset>
									<div class="col-md-6 column">

										<div class="form-group">
											<form:select id="writtenYear" path="year" required="required"
												class="form-control" itemValue="${searchBean.year}">
												<form:option value="">Select Academic Year</form:option>
												<form:options items="${yearList}" />
											</form:select>
										</div>

										<div class="form-group">
											<form:select id="writtenMonth" path="month"
												required="required" class="form-control">
												<form:option value="">Select Academic Month</form:option>
												<form:option value="Jan">Jan</form:option>
												<form:option value="Jul">Jul</form:option>
											</form:select>
										</div>
                                        
                                        
                                        <div class="form-group" style="overflow: visible;">
											<form:select id="subject" path="subject"
												class="combobox form-control">
												<form:option value="">Type OR Select Subject</form:option>
												<form:options items="${subjectList}" />
											</form:select>
										</div>
										
										 <div class="form-group" style="overflow: visible;">
											<form:select id="sessionName" path="sessionName"
												class="combobox form-control">
												<form:option value="">Type OR Select Session</form:option>
												<form:options items="${sessionList}" />
											</form:select>
										</div>
										

										<div class="form-group">
											<button id="submit" name="submit"
												class="btn btn-large btn-primary"
												formaction="downloadStudentAttendance">Generate</button>
											<button id="cancel" name="cancel" class="btn btn-danger"
												formaction="home" formnovalidate="formnovalidate">Cancel</button>

										</div>



									</div>

					

								</fieldset>

					
							</form:form>



</div>


	</section>

	<jsp:include page="footer.jsp" />


</body>
</html> --%>