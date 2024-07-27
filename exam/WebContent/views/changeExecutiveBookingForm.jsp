
<!DOCTYPE html>
<%@page import="java.util.*"%>
<%@page import="com.nmims.beans.StudentExamBean"%>
<%@page import="com.nmims.beans.ExecutiveBean"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%
	ArrayList<String> currentSemApplicableLiveSubject = (ArrayList<String>)request.getAttribute("currentSemApplicableLiveSubject");
	int applicableSubjectsListCount = 0;
	if(currentSemApplicableLiveSubject !=null && currentSemApplicableLiveSubject.size() > 0){
		applicableSubjectsListCount = currentSemApplicableLiveSubject.size();
	}
	boolean isExamRegistrationLive = (boolean)request.getSession().getAttribute("isExamRegistrationLive");
	HashMap<String,ExecutiveBean> mapOfBookedSubjects  = (HashMap<String,ExecutiveBean>)request.getAttribute("mapOfBookedSubjects");
	int noOfBookedSubjects= mapOfBookedSubjects!=null ? mapOfBookedSubjects.size() : 0;
	StudentExamBean student = (StudentExamBean)session.getAttribute("studentExam");
%>
<html lang="en">
<jsp:include page="common/jscss.jsp">
	<jsp:param value="Select subjects for Exam" name="title" />
</jsp:include>
<body>
	<%try { %>
	<%@ include file="common/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<jsp:include page="common/breadcrum.jsp">
			<jsp:param value="Student Zone;Exams;Exam Registration"
				name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="common/left-sidebar.jsp">
					<jsp:param value="Exam Registration" name="activeMenu" />
				</jsp:include>
				<div class="sz-content-wrapper examsPage">
					<%@ include file="common/studentInfoBar.jsp"%>
					<div class="sz-content">
						<%@ include file="common/messages.jsp"%>
						<%if(isExamRegistrationLive){ %>
						<%-- <c:if test="${isExamRegistrationLive}"> --%>
						<h2 class="red text-capitalize" style="width: 100%">
							Select subjects for Exam to change their Exam Center / Slot

						</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper">

							<div>
								<div class="table-responsive">
									<form:form id="bookExamCenter"
										action="changeExecutiveExamBooking" method="post"
										modelAttribute="executiveBean">
										<fieldset>
											<%-- <form:hidden path="applicableSubjects"
												name="applicableSubjects"
												value="${executiveBean.applicableSubjects }" /> --%>


											<table class="table table-striped" style="font-size: 12px">
												<thead>
													<tr>
														<th>Sr. No.</th>
														<!-- <th>Program</th>
														<th>Sem</th> -->
														<th>Subject</th>

														<th >Select</th>
													</tr>
												</thead>
												<tbody>
												   <%int count = 1; %>
													<%
													if(noOfBookedSubjects>0){
														 Iterator it = mapOfBookedSubjects.entrySet().iterator();
														    while (it.hasNext()) {
														        Map.Entry pair = (Map.Entry)it.next();
														    
														ExecutiveBean bean = new ExecutiveBean();
														String sapId = student.getSapid();
														bean = mapOfBookedSubjects.get(pair.getKey());
														
														/* if(mapOfBookedSubjects.containsKey(sapId+"-"+subject.trim())){
															bean = mapOfBookedSubjects.get(sapId+"-"+subject.trim());
														} */
												    %>
														<tr>
															<td><%=count++ %></td>
															<%-- <td>${executiveBean.program}</td>
															<td>${executiveBean.sem }</td> --%>
															<td><%=bean.getSubject()%></td>
															<td><form:checkbox path="applicableSubjects" value="<%=bean.getSubject() %>"  /></td>
														     
														</tr>
													<%	
												    it.remove(); // avoids a ConcurrentModificationException
												    
														}
													}
													%>
												</tbody>

											</table>
											<form:hidden path="changeOfCenter" />
											<button id="submit" name="submit"
												class="btn btn-large btn-primary" formaction="executiveSelectCenterForm"> Select to Change Exam Center / Slot  </button>
											
											<div class="form-group">
												<label class="control-label" for="submit"></label>
												<div class="controls"></div>
											</div>
										</fieldset>
									</form:form>
								</div>
							</div>

						</div>
						<%-- </c:if> --%>
						<%} %>
						
						<%-- 	</c:if> --%>
					</div>
					<%-- 
              						<b>Note:</b>
											<ol>
											<li>A student enrolling in each semester, has to necessarily complete a study period of at least six months in the enrolled semester, to be eligible for the Term End Examination of the subjects of that semester. Students can register and appear for the examination without submitting the internal assignment.</li>
											<li>For Result Declaration: Aggregate passing is the criteria i.e. Internal Assignment plus Term End Examination marks together must be <c:if test="${student.prgmStructApplicable eq 'Jul2017'}">40</c:if><c:if test="${student.prgmStructApplicable ne 'Jul2017'}">50</c:if> marks or more out of 100. For being declared as 'Pass' in each subject, appearance in both the components (Internal Assignment and Term End Examination) is mandatory. Without submitting the assignment and only appearing for term end examination cannot be declared as pass. In such cases, the result will be kept on hold.</li>
											<li>Internal Assignment/s submitted on or before the last date of assignment submission only will reflect in the respective exam cycle result declaration. No assignment submission request will be considered for reason whatsoever after the closure of assignment submission window for that respective exam cycle. Please verify your assignment submitted status in the exam registration table.</li>
											<c:if test="${student.centerName ne 'Verizon'}">
											<li>Exam fee is not a part of program fee and is charged separately. Exam fees once paid is neither refunded nor carry forwarded to next exam cycle in case the student cannot appear for the examination for reasons whatsoever.</li>
											</c:if>
											</ol>  --%>
				</div>
			</div>





		</div>
	</div> 
	
            <jsp:include page="common/footer.jsp"/>
	<% } catch(Exception e){ } %>

</body>
</html>
