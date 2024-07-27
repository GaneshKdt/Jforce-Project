<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<%@page import="com.nmims.beans.PassFailBean"%>
<%@page import="com.nmims.beans.StudentStudentPortalBean"%>
<%@page import="java.util.List"%>
<%@page import="com.nmims.controllers.HomeController"%>

<%
String resultSubject = (String)request.getAttribute("subject");
List<StudentMarksBean> studentMarksBeanList = (List<StudentMarksBean>)request.getSession().getAttribute("studentMarksBeanList_studentportal");
HashMap<String, StudentMarksBean> courseResultsMap = (HashMap<String, StudentMarksBean>)session.getAttribute("courseResultsMap_studentportal");
StudentMarksBean marksBean = null;
/*try{
	 marksBean = courseResultsMap.get(resultSubject);
}catch(Exception e){
}*/
//if added by Vilpesh on 2021-11-23
if(null != courseResultsMap) {
	marksBean = courseResultsMap.get(resultSubject);	
}
PassFailBean passFailBean = (PassFailBean)session.getAttribute("passFailBean_studentportal");
String mostRecentResultPeriod = (String)session.getAttribute(HomeController.S_MOST_RECENT_RESULT_PERIOD);//("mostRecentResultPeriod_studentportal");
//String declareDate =(String)session.getAttribute("declareDate_studentportal");
String declareDate =(String)session.getAttribute(HomeController.S_DECLAREDATE_RR);//"declareDate_RR_studentportal");//renamed by Vilpesh on 2021-11-23
String passfailStatus = "NA";
StudentStudentPortalBean rbean = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
if(passFailBean != null && marksBean !=null ){
	if("Y".equals(passFailBean.getIsPass())){
		passfailStatus = "Pass";		
	}else{
		passfailStatus = "Fail";
	}
}
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

<div class="container-fluid bg-white rounded">
	
		<div class="row">
		<div class="col-12">
			<% if(null != mostRecentResultPeriod) { %>
				<h4 class="text-danger text-uppercase mt-3">Results for <%=mostRecentResultPeriod %>  <span class="text-success text-capitalize">Status:
						<%=passfailStatus %></span></h4>
			<% } else { %>
				<h4 class="text-danger text-uppercase mt-3">No Results</h4>
			<% } %>
			
			<ul class="topRightLinks list-inline">
				<li>
					
						
				</li>
				<li><a class="panel-toggler collapsed" role="button"
					data-toggle="collapse" href="#collapseFour" aria-expanded="true"></a></li>
			
			   </ul>
		  </div>
		</div>
	
	


				<%if(marksBean == null){ %>
					<div class="no-data-wrapper">
			
						<% if(null != mostRecentResultPeriod) { %>
							<h6 class="no-data nodata fw-bold mt-3 mb-3"><span class="fa-solid fa-clipboard-list"></span> No Results Available for <%=mostRecentResultPeriod %></h6>
						<% } else { %>
							<h6 class="no-data nodata fw-bold mb-5"><span class="fa-solid fa-clipboard-list"></span> No Results Available.</h6>
						<% } %>
					</div>
					<br>
            
                   
                   
				<%}else{ %>
				
					<div class="data-content" >

						<div class="col-lg-12">
							<div class="row">
								<div class="statusWrapper">
									<div class="row">
										<div class="col-md-12 col-lg-6 col-xl-5">
											<ul class="colored-box list-inline ">
												<%if(!"Project".equalsIgnoreCase(resultSubject) && !"EPBM".equalsIgnoreCase(rbean.getProgram()) && !"Module 4 - Project".equalsIgnoreCase(resultSubject)){ %>
												<li class="bgBlue"><span
													class="icon icon-icon-review-submission"></span>
													<h4>
													<% if(null == marksBean.getWritenscore()) { %>
													<span>/&nbsp;70</span><span
															class="second-line">WRITTEN EXAM MARKS</span>
													<% } else { out.print(marksBean.getWritenscore()); %>		
													<span>&nbsp;/&nbsp;70 </span><span
															class="second-line">WRITTEN EXAM MARKS</span>
													<% } %></h4></li>
												<li class="bgMaroon"><span
													class="icon icon-icon-view-submissions"></span>
													<h4>
													<% if(null == marksBean.getAssignmentscore()) { %>
														<span>/&nbsp;30</span><span 
														class="second-line">ASSIGNMENT MARKS</span>
													<% } else { out.print(marksBean.getAssignmentscore()); %>
													<span>&nbsp;/&nbsp;30</span><span class="second-line">ASSIGNMENT MARKS</span>
													<% } %></h4></li>
												<%}else{ %>
												<li class="bgBlue"><span
													class="icon icon-icon-review-submission"></span>
													<h4><%=marksBean.getWritenscore() %><span>/100
														</span><span class="second-line">WRITTEN EXAM MARKS</span>
													</h4></li>
												<%} %>
												<div class="clearfix"></div>
											</ul>
										</div>
										<div class="col-md-12 col-lg-6 col-xl-4">
											<div class="contentIns">
												<p align="justify">
													Pass Marks:
													<%if( "Certificate".equalsIgnoreCase(rbean.getProgram()) || rbean.getPrgmStructApplicable().equalsIgnoreCase("Jul2017")) {%>
													  	40
													  <% }else{%>
													  	50
													  <%} %>
													 out of 100
													<%if(!"EPBM".equalsIgnoreCase(rbean.getProgram())){ %>
													(i.e. Aggregate Passing: Internal Continuous Assessments +
													Semester-End-Examination)
													<%} %>
												</p>
												<p align="justify">
													Discrepancy if any in the above information should be
													mailed with student name, Student No., Program enrolled,
													Semester details, Subject: at <a
														href="mailto:ngasce.exams@nmims.edu" target="_top">ngasce.exams@nmims.edu</a>
												</p>
												<p align="justify">Students who wish to apply for
													revaluation of the descriptive answers may apply for the
													same on or before 9th October 2022 23:59 p.m. using
													Service Request link available on Student Zone Home Page.</p>
												<p align="justify">
													<h5>Controller of Examinations</h5>
													Result Declaration Date : <%=declareDate %>
												</p>	
											</div>
										</div>
													
						<div class="col-lg-12 col-md-12 col-xl-3">
							
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
					</div>
			  </div>
		</div>
	</div>
				
		<div class="container-fluid mt-3"  style="background-color: white;">
		 <br>
						<div class="col-lg-12 mt-3">
							<div  class="row">
								<div class="table-responsive">
										<table class="table table-result" style="width:100%">
											<thead>
												<td>Year</td>
												<td>Month</td>
												<td>Sem</td>
												<td>WrittenScore</td>
												<td>AssignmentScore</td>
												<td>Gracemarks</td>
											</thead>
											<% try{ %>
											<% for(int i=0;i < studentMarksBeanList.size();i++){ %>
												<tr>
													<td><%=studentMarksBeanList.get(i).getYear() %></td>
													<td><%=studentMarksBeanList.get(i).getMonth() %></td>
													<td><%=studentMarksBeanList.get(i).getSem() %></td>
													<td><%=studentMarksBeanList.get(i).getWritenscore() %></td>
													<td><%=studentMarksBeanList.get(i).getAssignmentscore() == null ? 0 : studentMarksBeanList.get(i).getAssignmentscore()%></td>
													<td><%=studentMarksBeanList.get(i).getGracemarks() == null ? 0 : studentMarksBeanList.get(i).getGracemarks()  %></td>
												</tr>
											<% } %>
											<% }catch(Exception e) { } %>
										</table>
									</div>
								</div>
						</div>
						<br>
						<div class="clearfix"></div>
						
                    
				
					<%} %>

				</div>
		
	