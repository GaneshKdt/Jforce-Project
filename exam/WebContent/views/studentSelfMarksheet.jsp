<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Generate Marksheet" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Generate Marksheet</legend></div>
        <%@ include file="messages.jsp"%>
		<div class="panel-body clearfix">
		<form:form  action="studentSelfMarksheet" method="post" modelAttribute="studentMarks">
			<fieldset>
			<div class="col-md-6 column">
				
					<div class="form-group">
						<form:select id="writtenYear" path="writtenYear" type="text" required="required"	placeholder="Written Year" class="form-control"   itemValue="${studentMarks.writtenYear}">
							<form:option value="">Select Exam Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="writtenMonth" path="writtenMonth" type="text" required="required" placeholder="Written Month" class="form-control"  itemValue="${studentMarks.writtenMonth}">
							<form:option value="">Select Exam Month</form:option>
							<form:option value="Apr">Apr</form:option>
							<form:option value="Jun">Jun</form:option>
							<form:option value="Sep">Sep</form:option>
							<form:option value="Dec">Dec</form:option>
						</form:select>
					</div>
					
					<div class="form-group">
						<form:select id="sem" path="sem" required="required" placeholder="Semester" class="form-control"  value="${studentMarks.sem}">
							<form:option value="">Select Semester</form:option>
							<form:option value="1">1</form:option>
							<form:option value="2">2</form:option>
							<form:option value="3">3</form:option>
							<form:option value="4">4</form:option>
						</form:select>
					</div>
					
					<div class="form-group">
						<label class="control-label" for="submit"></label>
							<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="studentSelfMarksheet">Generate</button>
							<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
					</div>
					
			</div>

			</fieldset>
		</form:form>
		
		<%if("true".equals((String)request.getAttribute("success"))){ %>
			<a href="download">Download Marksheet</a>
		<%} %>
		</div>
	</div>

	</section>

	<jsp:include page="footer.jsp" />


</body>
</html>
 --%>


<!DOCTYPE html>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<%@page import="com.nmims.beans.StudentExamBean"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html lang="en">

<style>
 .student-info-bar,.sz-breadcrumb-wrapper{
		z-index:4 !important;
	}
</style>


<%
	StudentExamBean studentData = (StudentExamBean) request.getSession().getAttribute("studentExam");
%>
<jsp:include page="common/jscssNew.jsp">
	<jsp:param value="Generate Marksheet" name="title" />
</jsp:include>

<body>

	<%@ include file="common/headerDemo.jsp"%>

	<div class="sz-main-content-wrapper">

		<jsp:include page="common/breadcrum.jsp">
			<jsp:param value="Student Zone;Exams;Marksheet" name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">

				<div id="sticky-sidebar">
					<jsp:include page="common/left-sidebar.jsp">
						<jsp:param value="Marksheet" name="activeMenu" />
					</jsp:include>
				</div>

				<div class="sz-content-wrapper examsPage">
					<%@ include file="common/studentInfoBar.jsp"%>


					<div class="sz-content">

						<h2 class="red text-capitalize fs-4 mt-2 text-capitalize p-2 fw-bold">Generate
							Marksheet</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper rounded">
							<%@ include file="common/messagesNew.jsp"%>
							<form:form action="studentSelfMarksheet" method="post"
								modelAttribute="studentMarks">
								<fieldset>
									<div class="col-md-4 column mt-2">

										<div class="form-group">
											<form:select id="writtenYear" path="writtenYear" type="text"
												required="required" placeholder="Written Year"
												class="form-select" itemValue="${studentMarks.writtenYear}">
												<form:option value="">Select Exam Year</form:option>
												<form:options items="${yearList}" />
											</form:select>
										</div>



										<div class="form-group">
											<form:select id="writtenMonth" path="writtenMonth"
												type="text" required="required" placeholder="Written Month"
												class="form-select" itemValue="${studentMarks.writtenMonth}">
												<form:option value="">Select Exam Month</form:option>
												<form:option value="Apr">Apr</form:option>
												<form:option value="Jun">Jun</form:option>
												<form:option value="Sep">Sep</form:option>
												<form:option value="Dec">Dec</form:option>
											</form:select>
										</div>

										<div class="form-group">
											<form:select id="sem" path="sem" required="required"
												placeholder="Semester" class="form-select"
												value="${studentMarks.sem}">
												<form:option value="">Select Semester</form:option>
												<form:option value="1">1</form:option>
												<form:option value="2">2</form:option>
												<form:option value="3">3</form:option>
												<form:option value="4">4</form:option>
												<%
													if ("127".equalsIgnoreCase(studentData.getConsumerProgramStructureId())
														|| "128".equalsIgnoreCase(studentData.getConsumerProgramStructureId())) {
												%>
												<form:option value="5">5</form:option>
												<form:option value="6">6</form:option>
												<%
													}
												%>
											</form:select>
										</div>

										<div class="form-group">
											<label class="control-label" for="submit"></label>
											<button id="submit" name="submit" class="btn btn-danger"
												formaction="studentSelfMarksheet">Generate</button>
											<button id="cancel" name="cancel" class="btn btn-dark"
												formaction="/studentportal/home"
												formnovalidate="formnovalidate">Cancel</button>
										</div>

									</div>

								</fieldset>
							</form:form>

							<%
								if ("true".equals((String) request.getAttribute("success"))) {
							%>
							<a class="fw-bold text-danger"
								href="${pageContext.request.contextPath}/student/download">Download
								Marksheet</a>
							<%
								}
							%>
						</div>
						<div class="clearfix"></div>
						<%@include file="../views/examHome/studentMarksHistory.jsp"%>
						<div class="inline-block ml-1 mt-2">
						<p> The above result is provisional in nature and is just for student's information. The gradesheet/marksheet/transcript issued by the University will be the authentic document.</p>
						</div>
					</div>
				</div>


			</div>
		</div>
	</div>

	
	<jsp:include page="common/footerNew.jsp" />
	


</body>
</html>