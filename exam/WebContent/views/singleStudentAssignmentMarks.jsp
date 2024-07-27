<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="View Assignment Marks" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">

		<div class="row">
			<legend>Assignment Results declared for :
				${mostRecentResultPeriod}. You have ${size} records for
				${mostRecentResultPeriod}.</legend> 
		</div>
			<c:choose>
				<c:when test="${size > 0}">
					<div class="row">

						<div class="col-sm-18 ">
							<div class="panel-body">
								
								<div class="col-sm-18">
								
									<div class="table-responsive">
									<table class="table table-striped" style="font-size: 12px">
										<thead>
											<tr>
												<th>Sr. No.</th>
												<th style="text-align:left;">Subject</th>
												<th>Sem</th>
												<th>Assignment Marks</th>
												<th>Low Score Reason</th>
												<!-- <th>Revaluation Remarks (If applied for Revaluation)</th> -->
											</tr>
										</thead>
										<tbody>

											<c:forEach var="studentMarks" items="${studentMarksList}"
												varStatus="status">
												<tr>
													<td><c:out value="${status.count}" /></td>
													<td nowrap="nowrap" style="text-align:left;"><c:out value="${studentMarks.subject}" /></td>
													<td><c:out value="${studentMarks.sem}" /></td>
													<td><c:out value="${studentMarks.assignmentscore}" /></td>
													<td><c:out value="${studentMarks.reason}" /></td>
													<td><c:out value="${studentMarks.revaluationRemarks}" /></td>
												</tr>
											</c:forEach>


										</tbody>
									</table>
									</div>
									
									</div>
								
								<div class="clearfix"></div>
								
								<div class="col-xs-18 ">
									
									<div class="bullets">
									<h4>Note:</h4>
										<ul>
										
										<li>The marks displayed are out of 30 for Internal Assignment.</li>
										<li>In case student wants to apply for Revaluation of assignment he/she can apply for Revaluation of assignment marks only through the <b> Student Zone -> Service Request Tab -> Internal Assignment Revaluation</b> option by paying applicable fees.</li>
										<li>Fees for assignment revaluation is Rs.1000/- per subject.</li>
										<li>Payment Mode: Revaluation Fee payment is via Debit Card / Credit Card / Net Banking only. No Cash / Demand Draft facility is being offered.</li>
										
										<%if("Jul2014".equalsIgnoreCase(pStructure)){ %>
										<li>Last date to apply for June, 2016 Assignment Revaluation is <b> 17th June, 2016 before 23.59hrs.</b> No request for assignment revaluation will be accepted after closure of revaluation window for reasons whatsoever.</li>
										<%}else{ %>
										<li>Last date to apply for June, 2016 Assignment Revaluation is 17th June, 2016 before 23.59hrs. No request for assignment revaluation will be accepted after closure of revaluation window for reasons whatsoever.</li>
										<%} %>
										</ul>
									</div>
								</div>
									
								
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
<%@page import="com.nmims.beans.StudentMarksBean"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html lang="en">
    
<%
StudentExamBean student = (StudentExamBean)session.getAttribute("studentExam");
%>
	
    
    <jsp:include page="common/jscss.jsp">
	<jsp:param value="Assignment Marks" name="title"/>
    </jsp:include>    
    <body>
    
    	<%@ include file="common/header.jsp" %>
    	
    	
        
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="common/breadcrum.jsp">
			<jsp:param value="Student Zone;Exams;Assignment Marks" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
                         <div id="sticky-sidebar">  
              				<jsp:include page="common/left-sidebar.jsp">
								<jsp:param value="Assignment Marks" name="activeMenu"/>
							</jsp:include>
              				</div>
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="common/studentInfoBar.jsp" %>
              						
              						
              						<div class="sz-content">
										<h2 class="red text-capitalize">${size} <span>Assignment Marks records for ${mostRecentResultPeriod} Exam</h2>
										<div class="clearfix"></div>
              							<%-- <%
												if("Diageo".equalsIgnoreCase(student.getConsumerType())){
												
											%>
												<h5>Results Data Will Be Displayed Shortly.</h5>	
												
											<%
												}else{
											%> --%>
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
																	<th>Assignment Marks</th>
																	<th>Remarks</th>
																	<!-- <th>Assignment Reval Marks</th>
																	<th>Revaluation Remarks (If applied for Revaluation)</th> -->
																</tr>
															</thead>
															<tbody>
					
																<c:forEach var="studentMarks" items="${studentMarksList}"
																	varStatus="status">
																	<tr>
																		<td><c:out value="${status.count}" /></td>
																		<td nowrap="nowrap" style="text-align:left;"><c:out value="${studentMarks.subject}" /></td>
																		<td><c:out value="${studentMarks.sem}" /></td>
																		<td><c:out value="${studentMarks.assignmentscore}" /></td>
																		<td>
																			<c:if test="${studentMarks.markedForRevaluation eq 'N' }">
																				<c:out value="${studentMarks.reason}" />
																			</c:if>
																		</td>
																		<%-- <td><c:out value="${studentMarks.revaluationScore}" /></td>
																		<td><c:out value="${studentMarks.revaluationRemarks}" /></td> --%>
																	</tr>
																</c:forEach>
					
					
															</tbody>
														</table>
													</div>
													
													
													<div class="notesWrapper">
														<h5 class="text-uppercase">Notes:</h5>
														<ul>
															<%if("ACBM".equals(student.getProgram())){ %> 
															<li>The marks displayed are out of 40 for Internal Assignment.</li>
															<%}else{ %>
															<li>The marks displayed are out of 30 for Internal Assignment.</li>
															<%} %>
															<li>In case student wants to apply for Revaluation of assignment he/she can apply for Revaluation of assignment marks only through the <b> Student Zone -> Service Request Tab -> Internal Assignment Revaluation</b> option by paying applicable fees.</li>
															<li>Fees for assignment revaluation is Rs.1000/- per subject.</li>
															<li>Payment Mode: Revaluation Fee payment is via Debit Card / Credit Card / Net Banking only. No Cash / Demand Draft facility is being offered.</li>
															
															<%if("Online".equals(student.getExamMode())&& !"JUL2017".equalsIgnoreCase(student.getPrgmStructApplicable())){ %>
															<li>Last date to apply for ${mostRecentResultPeriod} Assignment Revaluation is <b> 27th July 2023 before 23.59 p.m. (IST)</b> No request for assignment revaluation will be accepted after closure of revaluation window for reasons whatsoever.</li>
															<%}else{ %>
															<li>Last date to apply for ${mostRecentResultPeriod} Assignment Revaluation is <b> 27th July 2023 before 23.59 p.m. (IST)</b> No request for assignment revaluation will be accepted after closure of revaluation window for reasons whatsoever.</li>
															<%} %>
														</ul>
													</div>
												</c:if>
										</div>
              								<%-- <%
												}
											%> --%>
              						</div>
              			      </div>  
					     </div>
                    </div>
               </div>
        <jsp:include page="common/footer.jsp"/>
    </body>
</html>