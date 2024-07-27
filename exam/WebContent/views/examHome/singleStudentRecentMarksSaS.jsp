<%-- Added for SAS--%>
<!DOCTYPE html>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<%@page import="com.nmims.beans.StudentExamBean"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>

<%
StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
	String programStructure = student.getPrgmStructApplicable();

%>S
<html lang="en">
    
    <jsp:include page="../common/jscss.jsp">
	<jsp:param value="View Recent Results" name="title"/>
    </jsp:include>     
    <body>
    
    	<%@ include file="../common/header.jsp" %>
    	
    	
        
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="../common/breadcrum.jsp">
			<jsp:param value="Student Zone;Exams;Exam Results" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
                     <div id="sticky-sidebar"> 
              				<jsp:include page="../common/left-sidebar.jsp">
								<jsp:param value="Exam Results" name="activeMenu"/>
							</jsp:include>
              		   </div>             				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="../common/studentInfoBar.jsp" %>
              						
              						
              						<div class="sz-content">
              						
              						<h2 class="red text-capitalize">${size} Marks Entries for ${mostRecentResultPeriod}</h2>
									
								
									
										<ul class="pull-right list-inline topRightLinks">
												<li class="borderRight"><a href="#0" onclick="window.print();">Print</a></li>
										</ul>
										<div class="clearfix"></div>
										<div class="panel-content-wrapper">
											<%@ include file="../common/messages.jsp" %>
											<%if(!student.getProgram().equalsIgnoreCase("EPBM") && !student.getProgram().equalsIgnoreCase("MPDV") ){ %>
											<c:if test="${size > 0}">
											<div class="table-responsive">
												<table class="table courses-sessions">
													<thead>
														<tr>
															<th>Sr. No.</th>
															<th style="text-align:left;">Subject</th>
															<th>Sem</th>
															<th>Marks</th>
															
														</tr>
													</thead>
													<tbody>
														<c:forEach var="studentMarks" items="${studentMarksList}" varStatus="status">
															<tr>
																<td><c:out value="${status.count}" /></td>
																<td nowrap="nowrap" style="text-align:left;"><c:out value="${studentMarks.subject}" /></td>
																<td><c:out value="${studentMarks.sem}" /></td>
																<td><c:out value="${studentMarks.writenscore}" /></td>
																
															</tr>
														</c:forEach>
													</tbody>
												</table>
											
											</div>
											
											</c:if>
											<%}else{ %>
											
											<%-- <c:if test="${size > 0}">  --%>
											<div class="table-responsive">
													<table class="table courses-sessions">
													<thead>
														<tr>
															<th>Sr. No.</th>
															<th style="text-align:left;">Subject</th>
															<th>Sem</th>
															<th>Marks</th>
															
														</tr>
													</thead>
													<tbody>
														<c:forEach var="studentMarks" items="${studentMarksList}" varStatus="status">
															<tr>
																<td><c:out value="${status.count}" /></td>
																<td nowrap="nowrap" style="text-align:left;"><c:out value="${studentMarks.subject}" /></td>
																<td><c:out value="${studentMarks.sem}" /></td>
																<td><c:out value="${studentMarks.writenscore}" /></td>
																
															</tr>
														</c:forEach>
													</tbody>
												</table>
											</div>
							<%-- </c:if>  --%>
											
											
											<%} %>
										</div>
									<div class="clearfix"></div>
									<%@include file="singleStudentPassFailMarks.jsp" %>
									
								<%@include file="studentMarksHistory.jsp" %>
									
									<c:if test="${size > 0}">
											<hr class="exam-separator"></hr>
											<div class="row">
												<div class="col-md-4">
															<div class="signatureLeft">
																<h5>Controller of Examinations</h5>
																<p>Result Declaration Date:  <b>${declareDate}</b></p>
         
         
															</div>
														</div>
													
													<div class="col-md-4">
															<p> 
																Pass Marks: 50 out of 100 marks obtained in the Term end Examination.
															</p>
															
															<p>Discrepancy if any in the above information should be mailed with student name, Student No.,
																Program enrolled, Semester details, Subject: at <a href="mailto:ngasce.exams@nmims.edu" target="_top">ngasce.exams@nmims.edu</a></p>
														
															
													</div>
													<div class="col-md-4">
													<div class="row">
														<div class="statusBox">
														
														
															<div class="media">
																<div class="media-left media-top"> AB </div>
																<div class="media-body"> Absent </div>
															</div>
															<div class="media">
																<div class="media-left media-top"> NV </div>
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
														</div>
													</div>
													
												</div>
												<div style="display:inline-block;margin-left:10px">
												<p>
												The results published on this website are only for immediate information to the examinees and cannot be considered as final. 
												Information as regards marks/ grades published by NMIMS University in mark sheet/ grade sheet should only be treated as authentic.
												</p>
												</div>
												
											</div>
												
											<div class="clearfix"></div>
										</c:if>
              								
              					</div>
              				</div>
              		
                            
					</div>
            </div>
        </div>
            
  	
        <jsp:include page="../common/footer.jsp"/>
            
		
    </body>
</html> 
