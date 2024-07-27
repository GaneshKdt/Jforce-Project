<!DOCTYPE html>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<%@page import="com.nmims.beans.ProgramSubjectMappingExamBean"%>
<%@page import="java.util.*"%>
<%@page import="com.nmims.beans.*"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>


<%
	ArrayList<ProgramSubjectMappingExamBean> applicableSubjectsList = (ArrayList<ProgramSubjectMappingExamBean>)session.getAttribute("applicableSubjectsList");
	String earlyAccess = (String)session.getAttribute("earlyAccess");
	StudentExamBean student = (StudentExamBean)session.getAttribute("studentExam");
	String firstName = student.getFirstName();
	String lastName = student.getLastName();
%>
	
<script type="text/javascript">

	
	function validateForm() {
		
		var assignmentSubmittedList = document.getElementsByName('releaseSubjects');
		var atleastOneSelected = false;
		for(var i = 0; i < assignmentSubmittedList.length; ++i)
		{
		    if(assignmentSubmittedList[i].checked){
		    	atleastOneSelected = true;
		    	break;
		    }
		}
		if(!atleastOneSelected){
			alert("Please select at least one subject to proceed.")
			return false;
		}
		
		return confirm('Are you sure you want to change exam bookings for those subjects?');
		    
	}

</script>
<%try{ %>

<html lang="en">
	
    
    <jsp:include page="common/jscss.jsp">
	<jsp:param value="Select subjects for Exam" name="title"/>
    </jsp:include>
    
    <body>
    
    	<%@ include file="common/header.jsp" %>
        
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="common/breadcrum.jsp">
			<jsp:param value="Student Zone;Exams;Exam Registration" name="breadcrumItems"/>
			</jsp:include>
        	
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
                    	<div id="sticky-sidebar"> 
	             			<jsp:include page="common/left-sidebar.jsp">
								<jsp:param value="Exam Registration" name="activeMenu"/>
							</jsp:include>
						</div>	
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="common/studentInfoBar.jsp" %>
              						
              						
              						<div class="sz-content">
              						<%-- <c:if test="${student.centerName eq 'Verizon'}">
              						   <font color="red" size="5"><b>Exam Registration is not Live currently</b></font>
              						</c:if> --%>
              						<%@ include file="common/messages.jsp" %>
              						
              						
									<c:if test="${isExamRegistraionLive}">
										<h2 class="red text-capitalize">Release Exam Bookings</h2>
										<div class="clearfix"></div>
	             						<div class="panel-content-wrapper">
										
										<c:if test="${rowCount > 0}">
										<div class="table-responsive">
										<form:form  action="searchBookingsToReleaseStudent" method="post" modelAttribute="booking" >
												<fieldset>
										<table class="table table-striped" style="font-size:12px">
															<thead>
															<tr>
																<th>Sr. No.</th>
																<th>Year</th>
																<th>Month</th>
																<th>SAP ID</th>
																<th>Subject</th>
																<th>Select</th>
																<th>Transaction Status</th>
																<th>Center Booked</th>
									
															</tr>
														</thead>
															<tbody>
															
															<%
															
																HashMap<String, String> examCenterIdNameMap = (HashMap<String, String>)session.getAttribute("examCenterIdNameMap");
															    ArrayList<ExamBookingTransactionBean> confirmedBookings = (ArrayList<ExamBookingTransactionBean>)request.getAttribute("confirmedBookings");
																String SEAT_RELEASED = (String)request.getAttribute("SEAT_RELEASED");
																boolean hasSeatToRelease =false;
															    for(int i = 0; i < confirmedBookings.size(); i++){
																	ExamBookingTransactionBean bean = confirmedBookings.get(i);
																	String examCenterName = examCenterIdNameMap.get(bean.getCenterId());
															%>
																
																<tr>
														            <td><%= (i+1)%></td>
														            
														            <td><%=bean.getYear()%></td>
														            <td><%=bean.getMonth()%></td>
														            <td><%=bean.getSapid()%></td>
														            <td><%=bean.getSubject()%></td>
														            
														            <%if(!SEAT_RELEASED.equalsIgnoreCase(bean.getTranStatus())){ 
														            	hasSeatToRelease = true;
														            %>
														            <td><form:checkbox path="releaseSubjects" value="<%=bean.getSubject() %>"  /></td>
														            <%}else{ 
														            %>
														            <td></td>
														            <%} %>
														            
														            <td><%=bean.getTranStatus()%></td>
														            <td><%=examCenterName%></td>
									          
																</tr>
															<% }%>
															</tbody>
														</table>
														
														<div class="form-group">
															<label class="control-label" for="submit"></label>
															<button id="submit" name="submit" class="btn btn-large btn-primary" onclick="return validateForm();" formaction="releaseBookingsStudent">Proceed to Selection of New Exam Centers</button>
															<button id="cancel" name="cancel" class="btn btn-danger" formaction="selectSubjectsForm" formnovalidate="formnovalidate">Back</button>
														</div>
														</fieldset>
											</form:form>
										</div>
										<br>
									
									</c:if>
											
										</div>
              								</c:if>
              							<div>
											
											<!-- <b>Note:</b>
											<ol>
											<li>A student enrolling in each semester, has to necessarily complete a study period of at least six months in the enrolled semester, to be eligible for the Term End Examination of the subjects of that semester. Students can register and appear for the examination without submitting the internal assignment.</li>
											<li>For Result Declaration: Aggregate passing is the criteria i.e. Internal Assignment plus Term End Examination marks together must be 50 marks or more out of 100. For being declared as 'Pass' in each subject, appearance in both the components (Internal Assignment and Term End Examination) is mandatory. Without submitting the assignment and only appearing for term end examination cannot be declared as pass. In such cases, the result will be kept on hold.</li>
											<li>Internal Assignment/s submitted on or before the last date of assignment submission only will reflect in the respective exam cycle result declaration. No assignment submission request will be considered for reason whatsoever after the closure of assignment submission window for that respective exam cycle. Please verify your assignment submitted status in the exam registration table.</li>
											</ol> -->
											
											
										</div>		
										
              						</div>
              				</div>
              		
                            
					</div>
            </div>
        </div>
            
  	
        <jsp:include page="common/footer.jsp"/>
            
		
    </body>
</html>

<%}catch(Exception e){}%>