<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<%@page import="com.nmims.beans.StudentBean"%>
<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Pass Fail Status" name="title" />
</jsp:include>

<body class="inside">

<%
	StudentBean student = (StudentBean)request.getSession().getAttribute("student");
	String programStructure = student.getPrgmStructApplicable();

%>

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">

			<div class="row">
			<legend>Pass Fail Status&nbsp;<font size="2px">(${size} Records Found)</font></legend> </div>
			<c:choose>
				<c:when test="${size > 0}">
					<div class="row">


						<div class="col-sm-18 ">
							<div class="panel-body">
								
								<!-- <div class="col-md-18 ">
									<button class="btn btn-download btn-print" onclick="window.print();">Print</button>
									
							</div> -->
								
								<div class="col-sm-18">


								<c:choose>
								<c:when test="${size > 0}">
									<div class="table-responsive">
									<table class="table table-striped" style="font-size: 12px">
										<thead>
											<tr>
												<th>Sr. No.</th>
												<th style="text-align:left;">Subject</th>
												<th>Sem</th>
												<th style="text-align:center">TEE Marks</th>
												<th style="text-align:center">Assignment Marks</th>
												<th style="text-align:center">Grace Marks</th>
												<th style="text-align:center">Total Marks</th>
												
											</tr>
										</thead>
										<tbody>

											<c:forEach var="studentMarks" items="${studentMarksList}"
												varStatus="status">
												<tr>
													<td><c:out value="${status.count}" /></td>
													<td nowrap="nowrap" style="text-align:left;"><c:out value="${studentMarks.subject}" /></td>
													<td><c:out value="${studentMarks.sem}" /></td>
													<td style="text-align:center"><c:out value="${studentMarks.writtenscore} " /><sub>(${studentMarks.writtenMonth}-${studentMarks.writtenYear})</sub></td>
													<td style="text-align:center"><c:out value="${studentMarks.assignmentscore} " /><sub>(${studentMarks.assignmentMonth}-${studentMarks.assignmentYear})</sub></td>
													<td style="text-align:center"><c:out value="${studentMarks.gracemarks}" /></td>
													<c:if test="${studentMarks.isPass == 'Y' }">
														<td style="text-align:center;color: green"><b><c:out value="${studentMarks.total}" /></b></td>
													</c:if>
													<c:if test="${studentMarks.isPass == 'N' }">
														<td style="text-align:center;color: red"><b><c:out value="${studentMarks.total}" /></b></td>
													</c:if>
													
												</tr>
											</c:forEach>


										</tbody>
									</table>
									</div>
									
									</div>
								
								<div class="clearfix"></div>
								
								<div class="row">
									<div class="col-sm-18">
										<div class="col-md-6">
											<ul class="resultList">
												<li><span>ANS</span><a>Assignment Not Submitted</a></li>
												<li><span>AB</span><a>Absent</a></li>
												<li><span>NV</span><a>Null & Void</a></li>
												<li><span>CC</span><a>Copy Case</a></li>
												<li><span>RIA</span><a>Result Kept in Abeyance</a></li>
												<li><span>NA</span><a>Not Eligible due to non submission of assignment</a></li>
											</ul>
										</div>
										
										<div class="col-md-12">
											<div class="resultBottomPara">
												<h3>Pass Marks: 50 out of 100.</h3>
												<h3>(i.e. Aggregate Passing: Assignments + Term-End-Examination)</h3>
												  <br/> <br/> <br/> 
											</div>
											
											<div class="col-lg-7 col-lg-offset-10 col-sm-10 col-sm-offset-7 resultSig">
												<h3><b>Controller of Examinations</b></h3>
											</div>
										</div>
										
										<div class="col-md-18 resultPara">
											<p>Discrepancy if any in the above information should be mailed with student name, Student No., Program enrolled, Semester details, Subject: at <a href="mailto:ngasce.exams@nmims.edu" target="_top">ngasce.exams@nmims.edu</a><br>
											</b></p>
										</div>
									</div>
								</div>
									
									
								</c:when>
							</c:choose>


								
								
								
							</div>
						</div>
						

			
						
					</div>
				</c:when>
			</c:choose>
			
			

		</div>
	</section>




	<jsp:include page="footer.jsp" />

</body>
</html>
 --%>
 
 <!DOCTYPE html>
<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<%@page import="com.nmims.beans.StudentExamBean"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%
StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
	String programStructure = student.getPrgmStructApplicable();
%>

<html lang="en">
    

	
    
    <jsp:include page="common/jscss.jsp">
	<jsp:param value="Pass Fail Status" name="title"/>
    </jsp:include>
    
    
    
    <body>
    
    	<%@ include file="common/header.jsp" %>
    	
    	
        
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="common/breadcrum.jsp">
		<jsp:param value="Student Zone;Home" name="breadcrumItems"/>
		</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<%@ include file="common/left-sidebar.jsp" %>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="common/studentInfoBar.jsp" %>
              						
              						
              						<div class="sz-content">
								
										<h2 class="red text-capitalize">Pass Fail Status (${size} Records Found)</h2>
										<div class="clearfix"></div>
		              					<div class="panel-content-wrapper">
											<%@ include file="common/messages.jsp" %>
											
											<c:if test="${size > 0}">
											
												<div class="table-responsive">
												<table class="table table-striped" style="font-size: 12px">
													<thead>
														<tr>
															<th>Sr. No.</th>
															<th style="text-align:left;">Subject</th>
															<th>Sem</th>
															<th style="text-align:center">TEE Marks</th>
															<th style="text-align:center">Assignment Marks</th>
															<th style="text-align:center">Grace Marks</th>
															<th style="text-align:center">Total Marks</th>
															
														</tr>
													</thead>
													<tbody>
			
														<c:forEach var="studentMarks" items="${studentMarksList}"
															varStatus="status">
															<tr>
																<td><c:out value="${status.count}" /></td>
																<td nowrap="nowrap" style="text-align:left;"><c:out value="${studentMarks.subject}" /></td>
																<td><c:out value="${studentMarks.sem}" /></td>
																<td style="text-align:center"><c:out value="${studentMarks.writtenscore} " /><sub>(${studentMarks.writtenMonth}-${studentMarks.writtenYear})</sub></td>
																<td style="text-align:center"><c:out value="${studentMarks.assignmentscore} " /><sub>(${studentMarks.assignmentMonth}-${studentMarks.assignmentYear})</sub></td>
																<td style="text-align:center"><c:out value="${studentMarks.gracemarks}" /></td>
																<c:if test="${studentMarks.isPass == 'Y' }">
																	<td style="text-align:center;color: green"><b><c:out value="${studentMarks.total}" /></b></td>
																</c:if>
																<c:if test="${studentMarks.isPass == 'N' }">
																	<td style="text-align:center;color: red"><b><c:out value="${studentMarks.total}" /></b></td>
																</c:if>
																
															</tr>
														</c:forEach>
			
			
													</tbody>
												</table>
												</div>
												
												
												<hr class="exam-separator"></hr>
												<div class="row">
													<div class="col-md-4">
																<div class="signatureLeft">
																	<h5>Controller of Examinations</h5>
																	<p>Result Declaration Date: ${declareDate}</p>
																</div>
															</div>
														
														<div class="col-md-4">
																<p> Pass Marks: 50 out of 100(i.e. Aggregate Passing:
																 Internal Continuous Assessments + Semester-End-Examination)</p>
																<p>Discrepancy if any in the above information should be mailed with student name, Student No.,
																	Program enrolled, Semester details, Subject: at <a href="mailto:ngasce.exams@nmims.edu" target="_top">ngasce.exams@nmims.edu</a></p>
																
														</div>
														<div class="col-md-4">
														<div class="row">
															<div class="statusBox">
																<div class="media">
																	<div class="media-left media-top"> ANS </div>
																	<div class="media-body"> Assignment Not Submitted </div>
																</div>
																<div class="media">
																	<div class="media-left media-top"> AB </div>
																	<div class="media-body"> Absent </div>
																</div>
																<div class="media">
																	<div class="media-left media-top"> NB </div>
																	<div class="media-body"> Null & Void </div>
																</div>
																<div class="media">
																	<div class="media-left media-top"> CC </div>
																	<div class="media-body"> Copy Case </div>
																</div>
																<div class="media">
																	<div class="media-left media-top"> RIA </div>
																	<div class="media-body"> Result Kept in Abeyance </div>
																</div>
																<div class="media">
																	<div class="media-left media-top"> NA </div>
																	<div class="media-body"> Not Eligible due to non submission of assignment </div>
																</div>
															</div>
														</div>
													</div>
												</div>
													
												<div class="clearfix"></div>
											</c:if>
											
										</div>
              								
              						</div>
              				</div>
              		
                            
					</div>
            </div>
        </div>
            
  	
        <jsp:include page="common/footer.jsp"/>
            
		
    </body>
</html>