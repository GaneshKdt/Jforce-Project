<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<%@page import="com.nmims.beans.StudentStudentPortalBean"%>
<%
String resultSubject = (String)request.getAttribute("subject");
List<StudentMarksBean> studentMarksBeanList = (List<StudentMarksBean>)request.getSession().getAttribute("studentMarksBeanList");
StudentStudentPortalBean rbean = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
%>
<style>
.nodata {
    vertical-align: middle;
    color: #a6a8ab;
    font: 1.00em "Open Sans";
    text-align: center;
    margin: 0;
}
</style>
<div class="course-results-m-wrapper">
	<div class="panel-courses-page">
		
		<div class="clearfix"></div>

		<%-- <%if(marksBean == null){ %>
			<div id="collapseFour" class="panel-collapse collapse in academic-schedule courses-panel-collapse panel-content-wrapper accordion-has-content" role="tabpanel">
		<%}else{ %> --%>
			<div id="collapseFour" class=" collapse in academic-schedule courses-panel-collapse panel-content-wrapper accordion-has-content" role="tabpanel">
		<%-- <%} %> --%>
		
			<div class="panel-body" style="padding: 20px;"> 
				<%if(studentMarksBeanList == null || studentMarksBeanList.size() == 0){ %>
					<div class="no-data-wrapper">

						<h6 class="no-data nodata"><span class="icon-exams"></span>No Results Available</h6>
					</div>

				<%}else{ %>
				
					<div class="data-content" >

						<div class="col-lg-12">
							<div class="row">
								<div class="statusWrapper">
									<div>
										<div >
											<table class="table">
												<thead>
													<td>Year</td>
													<td>Month</td>
													<td>Sem</td>
													<td>WrittenScore</td>
													<td>Assignmentscore</td>
													<td>Gracemarks</td>
												</thead>
												
												<% try{ %>
													<% for(int i=0;i < studentMarksBeanList.size();i++){ %>
														<tr>
															<td><%=studentMarksBeanList.get(i).getYear() %></td>
															<td><%=studentMarksBeanList.get(i).getMonth() %></td>
															<td><%=studentMarksBeanList.get(i).getSem() %></td>
															<td><%=studentMarksBeanList.get(i).getWritenscore() %></td>
															<td><%=studentMarksBeanList.get(i).getAssignmentscore() %></td>
															<td><%=studentMarksBeanList.get(i).getGracemarks() == null ? 0 : studentMarksBeanList.get(i).getGracemarks()  %></td>
														</tr>
													<% } %>
												<% }catch(Exception e) { } %>
											</table>
										</div>
									</div>
								</div>
							</div>
						</div>
						
						<div class="col-lg-6">
							<div class="row">
								<div class="statusBox">

									<%if(!"EPBM".equalsIgnoreCase(rbean.getProgram())){ %>
									<div class="media">
										<div class="media-left media-top">ANS</div>
										<div class="media-body">Assignment Not Submitted</div>
									</div>
									<%} %>
									<div class="media">
										<div class="media-left media-top">AB</div>
										<div class="media-body">Absent</div>
									</div>
									<div class="media">
										<div class="media-left media-top">NV</div>
										<div class="media-body">Null & Void</div>
									</div>
									<div class="media">
										<div class="media-left media-top">CC</div>
										<div class="media-body">Copy Case</div>
									</div>
									<div class="media">
										<div class="media-left media-top">RIA</div>
										<div class="media-body">Result Kept in Abeyance</div>
									</div>
									<%if(!"EPBM".equalsIgnoreCase(rbean.getProgram())){ %>
									<div class="media">
										<div class="media-left media-top">NA</div>
										<div class="media-body">Not Eligible due to non
											submission of assignment</div>
									</div>
									<%} %>
								</div>
							</div>
						</div>
					

					</div>
					<%} %>

				</div>
			</div>
		</div>
	</div>