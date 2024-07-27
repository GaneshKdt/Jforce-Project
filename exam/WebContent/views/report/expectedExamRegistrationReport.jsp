<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->


<%@page import="java.util.TreeMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="../jscss.jsp">
	<jsp:param value="Report for Expected Exam Registrations" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="../header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">

			<div class="row"><legend>Report for Expected Exam Registrations</legend></div>
			
			<%@ include file="../messages.jsp"%>

			<div class="panel-body clearfix">
			<form:form  action="expectedExamRegistrationReport" method="post" modelAttribute="studentMarks">
			<fieldset>
			<div class="col-md-8 column">

					<div class="form-group">
						<form:select id="writtenYear" path="year" type="text" required="required"	placeholder="Written Year" class="form-control"   itemValue="${studentMarks.year}">
							<form:option value="">Select Acad Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
					
					<div class="form-group">
						<form:select id="writtenMonth" path="month" type="text" required="required" placeholder="Written Month" class="form-control"  itemValue="${studentMarks.month}">
							<form:option value="">Select Acad Month</form:option>
							<form:option value="Jan">Jan</form:option>
							<form:option value="Jul">Jul</form:option>
						</form:select>
					</div>
					
					<div class="form-group">
					<button id="submit" name="submit" class="btn btn-sm btn-primary"
						formaction="expectedExamRegistrationReport">Generate Count Before Submission</button>
					<button id="submit" name="submit" class="btn btn-sm btn-primary"
					formaction="/exam/admin/expectedExamRegistrationReportAfterAssignmentSubmission">Generate Count After Submission</button>
						
						<button id="cancel" name="cancel" class="btn btn-sm btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
					</div>

				</div>
				
				</fieldset>
				</form:form>
				
		</div>

		<c:if test="${rowCount > 0}">
			
			
			<%
				
				TreeMap<String, Integer> sortedSubjectCityStudentCountMap = (TreeMap<String, Integer>)request.getAttribute("sortedSubjectCityStudentCountMap");
				
			%>
			<h2>Subject-City Wise Student Count <a href="/exam/admin/downloadExpectedExamRegistrationReport"> (Download to Excel)</a></h2>
			<div class="panel-body table-responsive">
				<table class="table table-striped table-hover" style="font-size:12px">
					<thead>
						<tr> 
							<th>Sr. No.</th>
							<th>Subject</th>
							<th>City</th>
							<th>Student Count</th>
						</tr>
					</thead>
					<tbody>
			<%
				int count = 1;
				for (Map.Entry<String, Integer> entry : sortedSubjectCityStudentCountMap.entrySet()) {
				    String key = entry.getKey();
				    int studentCount = entry.getValue().intValue();
				    
				    String subject = key.substring(0, key.indexOf("~"));
				    String city = key.substring(key.indexOf("~")+1, key.length());
				    
			%>
			<tr>
				<td><%=count++ %></td>
				<td><%=subject %></td>
				<td><%=city %></td>
				<td><%=studentCount %></td>
			</tr>
			
			
			<%	}//End of for	%>
			</tbody>
			</table>
			</div>
			
			
			
			
			
		</c:if>
		</div>
	</section>

	<jsp:include page="../footer.jsp" />


</body>
</html> --%>
 
 <!DOCTYPE html>
<html lang="en">
	
	
	
   <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%> 
<%@page import="java.util.TreeMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>

    <jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Report for Expected Exam Registrations" name="title"/>
    </jsp:include>
    
    
    
    <body>
    
    	<%@ include file="../adminCommon/header.jsp" %>
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;Report for Expected Exam Registrations" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="../adminCommon/left-sidebar.jsp">
								<jsp:param value="" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="../adminCommon/adminInfoBar.jsp" %>
              						<div class="sz-content">
								
											<h2 class="red text-capitalize">Report for Expected Exam Registrations</h2>
											<div class="clearfix"></div>
								<div class="panel-content-wrapper" style="min-height:450px;">
											<form:form  action="/exam/admin/expectedExamRegistrationReport" method="post" modelAttribute="studentMarks">
													<fieldset>
													<div class="col-md-8">
														<div class="form-group">
																<form:select id="writtenYear" path="year" type="text" required="required"	placeholder="Written Year" class="form-control"   itemValue="${studentMarks.year}">
																	<form:option value="">Select Acad Year</form:option>
																	<form:options items="${yearList}" />
																</form:select>
															</div>
															<div class="form-group">
																<form:select id="writtenMonth" path="month" type="text" required="required" placeholder="Written Month" class="form-control"  itemValue="${studentMarks.month}">
																	<form:option value="">Select Acad Month</form:option>
																	<form:option value="Jan">Jan</form:option>
																	<form:option value="Jul">Jul</form:option>
																</form:select>
															</div>
															<div class="form-group">
															<button id="submit" name="submit" class="btn btn-sm btn-primary"
																formaction="/exam/admin/expectedExamRegistrationReport">Generate Count Before Submission</button>
															<button id="submit" name="submit" class="btn btn-sm btn-primary"
															formaction="/exam/admin/expectedExamRegistrationReportAfterAssignmentSubmission">Generate Count After Submission</button>
																<button id="cancel" name="cancel" class="btn btn-sm btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
															</div>
														</div>
														</fieldset>
											</form:form>
												
								</div>
								<c:if test="${rowCount > 0}">
			
			
								<%
									
									TreeMap<String, Integer> sortedSubjectCityStudentCountMap = (TreeMap<String, Integer>)request.getAttribute("sortedSubjectCityStudentCountMap");
									
								%>
								<h2>Subject-City Wise Student Count <a href="/exam/admin/downloadExpectedExamRegistrationReport" style="color:blue;"> (Download to Excel)</a></h2>
								<div class="clearfix"></div>
									<div class="panel-content-wrapper">
										<div class="table-responsive">
									<table class="table table-striped table-hover" style="font-size:12px">
										<thead>
											<tr> 
												<th>Sr. No.</th>
												<th>Subject</th>
												<th>City</th>
												<th>Student Count</th>
											</tr>
										</thead>
										<tbody>
								<%
									int count = 1;
									for (Map.Entry<String, Integer> entry : sortedSubjectCityStudentCountMap.entrySet()) {
										String key = entry.getKey();
										int studentCount = entry.getValue().intValue();
										
										String subject = key.substring(0, key.indexOf("~"));
										String city = key.substring(key.indexOf("~")+1, key.length());
										
								%>
								<tr>
									<td><%=count++ %></td>
									<td><%=subject %></td>
									<td><%=city %></td>
									<td><%=studentCount %></td>
								</tr>
								
								
								<%	}//End of for	%>
								</tbody>
								</table>
								</div>
								</div>
							</c:if>
	          				</div>
              			</div>
    				   </div>
			           </div>
		           </div>
        <jsp:include page="../adminCommon/footer.jsp"/>
    </body>
</html>