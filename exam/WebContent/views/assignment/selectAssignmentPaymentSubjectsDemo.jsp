
 
 <!DOCTYPE html>
<%@page import="com.nmims.beans.ProgramSubjectMappingExamBean"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.nmims.beans.StudentExamBean"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%
	ArrayList<ProgramSubjectMappingExamBean> applicableSubjectsList = (ArrayList<ProgramSubjectMappingExamBean>)session.getAttribute("applicableSubjectsList");
	
StudentExamBean student = (StudentExamBean)session.getAttribute("studentExam");
	String firstName = student.getFirstName();
	String lastName = student.getLastName();
	String sapId = student.getSapid();
%>
	
	
<html lang="en">
	
    
    <jsp:include page="../common/jscssNew.jsp">
	<jsp:param value="Select subjets for Assignment Submission" name="title"/>
    </jsp:include>
    
    <script language="JavaScript">
		function validateForm() {
			
			var assignmentSubmittedList = document.querySelectorAll("input[name^='applicableSubjects[']");
			var atleastOneSelected = false;
			for(var i = 0; i < assignmentSubmittedList.length; ++i)
			{
			    if(assignmentSubmittedList[i].checked && assignmentSubmittedList[i].value){
			    	atleastOneSelected = true;
			    	break;
			    }
			}
			if(!atleastOneSelected){
				alert("Please select at least one subject to proceed.");
				return false;
			}
			
			for(var i = 0; i < assignmentSubmittedList.length; ++i)
			{
			    if(!assignmentSubmittedList[i].checked){
			    	return confirm('You have not selected some of the subjects for Assignment Payment. Are you sure you want to proceed to next step?');
			    }
			}
			
			return confirm("Fees once paid will not be refunded nor carry forwarded. Are you sure you want to proceed towards payment gateway");
			
			return true;
		}
		
		
		
		
	</script>
    
    <body>
    
    	<%@ include file="../common/header.jsp" %>
    	
        
        <div class="sz-main-content-wrapper">
        
<%--         	<%@ include file="../common/breadcrum.jsp" %> --%>
        	<jsp:include page="../common/breadcrum.jsp">
			<jsp:param value="Student Zone;Exams;Assignment;Proceed to Payment" name="breadcrumItems"/>
			</jsp:include>
			
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
          				<div id="sticky-sidebar"> 
							<jsp:include page="../common/left-sidebar.jsp">
								<jsp:param value="Assignment" name="activeMenu" />
							</jsp:include>
						</div>
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="../common/studentInfoBar.jsp" %>
              						
              						
              						<div class="sz-content">
								
										<h5 class="text-danger fw-bolder text-capitalize mt-4 mb-3">Select Assignments for ${mostRecentTimetablePeriod} Exam : Payable Subjects  (${applicableSubjectsListCount})</h5>
										<div class="clearfix"></div>
		              					<div class="container-fluid pt-4 pb-4 h-100 bg-light rounded">
											<%@ include file="../common/messagesDemo.jsp" %>
											
												<c:choose>
												<c:when test="${applicableSubjectsListCount > 0}">
												<div>
													<div class="table-responsive container-fluid bg-light rounded">
													<form:form id="subjectForm" action="assignmentPaymentGotoGateway" method="post" modelAttribute="examBooking" >
														<fieldset>
														<input type="hidden"  name = "month" value = "${examBooking.month }"/>
														<input type="hidden"  name = "year" value = "${examBooking.year }"/>
													
														<table class="table  table-striped mt-3 mb-3 ml-2 mr-2 rounded" style="font-size:12px">
														<thead>
															<tr> 
																<th>Sr. No.</th>
																<th>Program</th>
																<th>Sem</th>
																<th>Subject</th>
																<th>Booking Status</th>
																<th>Select</th>
													
															</tr>
														</thead>
														<tbody>
														
														<% int count = 0; 
														final String DD_APPROVED = "DD Approved";
														final String NOT_BOOKED = "Not Booked";
														final String ONLINE_PAYMENT_MANUALLY_APPROVED = "Online Payment Manually Approved"; 
														final String FEE_IN_ADMISSION = "Exam Fees part of Registration Fees/Exam Fees Exempted";
														final String SEAT_RELEASED = "Seat Released";
														final String BOOKED = "Booked";
														
														for(int i = 0; i < applicableSubjectsList.size() ; i++){
															ProgramSubjectMappingExamBean bean = applicableSubjectsList.get(i);
															count++;
															String studentProgram = bean.getProgram();
															String sem = bean.getSem();
															String subject = bean.getSubject();
															String bookingStatus = bean.getBookingStatus();
															String canBook = bean.getCanBook();
															String bookingStatusForDisplay = bookingStatus;
															if(ONLINE_PAYMENT_MANUALLY_APPROVED.equals(bookingStatus)){
																bookingStatusForDisplay = "Approved";
															}
														 %>
														
														 	<tr>
													            <td><c:out value="<%=count %>"/></td>
													            <td><c:out value="<%=studentProgram %>"/></td>
													            <td><c:out value="<%=sem %>"/></td>
																<td nowrap="nowrap"><c:out value="<%=subject %>"/></td>
																<td><c:out value="<%=bookingStatusForDisplay %>"/></td>
																<td>
																<%if("Yes".equals(canBook)){ %>
																
																	<input type="checkbox" name="applicableSubjects[<%= i %>]" id="applicableSubjects1" value="<%=subject %>" checked="checked"/>
																<%}%>
													            </td>
													            
													        </tr>   
														        
														<%} %>
															
														</tbody>
													</table>
													
												
													<div class="form-group">
														<label class="control-label" for="submit"></label>
														<div class="controls">
														
														<c:if test="${subjectsToPay > 0}">
															<div>Assignment Submission Fees per Subject: INR. 500/-</div>
															<button id="submit" name="submit" class="btn btn-large btn-danger raunded" onclick="return validateForm();">Proceed to Pay</button>
														</c:if>
																
														<button id="cancel" name="cancel" type="button" class="btn btn-dark raunded" onclick="window.location.href='/studentportal/home'" formnovalidate="formnovalidate">Back</button>
														</div>
													</div>
												</fieldset>
												</form:form>
												</div>
												</div>
											</c:when>
												<c:otherwise>
													<button id="cancel" name="cancel" type="button" class="btn btn-dark raunded" onclick="window.location.href='/studentportal/home'" formnovalidate="formnovalidate">Back</button>
												</c:otherwise>
											</c:choose>
											
										</div>
              								
              						</div>
              				</div>
              		
                            
					</div>
            </div>
        </div>
            
  	
        <jsp:include page="../common/footerNew.jsp"/>
            
		
    </body>
</html>