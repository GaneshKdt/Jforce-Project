<%--@page import="jdk.internal.org.objectweb.asm.util.CheckAnnotationAdapter"--%> <!--Commented by Vilpesh on 2021-10-29-->
<%--@page import="com.nmims.controllers.BaseController"--%><!--Commented by Vilpesh on 2021-10-29-->
<%@page import="java.util.Arrays, java.util.ArrayList"%>
<%@page import="com.nmims.beans.StudentMarksBean, com.nmims.controllers.HomeController"%><!--Added by Vilpesh on 2021-11-24-->
<%@page import="java.util.ArrayList"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%
	String collapseResultsSection = "";
	ArrayList<StudentMarksBean> studentMarksList = (ArrayList<StudentMarksBean>)session.getAttribute(HomeController.S_MARKS_LIST);//("studentMarksList_studentportal");
	int noOfMarksResults = 0;
	if(null != studentMarksList) {
		Integer size = (Integer) session.getAttribute(HomeController.S_MARKS_LIST_SIZE);//("studentMarksListSize_studentportal");
		noOfMarksResults = size;
	}
	String mostRecentResultPeriod = (String)session.getAttribute(HomeController.S_MOST_RECENT_RESULT_PERIOD);//("mostRecentResultPeriod_studentportal");
	//BaseController resCon = new BaseController(); //Commented by Vilpesh on 2021-10-29
	if(noOfMarksResults > 0){
		collapseResultsSection = "in";//Adding "in" class will expand Results section. Expand only when results are present
	}
%>
<%if(noOfMarksResults > 0){ %>
  <div class=" col-md-6 mb-2"> 
		<div class="d-flex align-items-center text-wrap py-1">
		<span class="fw-bold me-3"><small class="fs-5">RESULTS</small></span>
		<div class="ms-auto">
			<a href="#"  data-bs-toggle="modal" data-bs-target="#resultsModal" class="text-dark me-1"><small class="text-nowrap">SEE ALL</small></a> 
			<a type="button" data-bs-toggle="collapse" data-bs-target="#collapseSix" class="text-muted"
				role="button" aria-expanded="true" aria-controls="collapseSix"
				id="collapseCard"> <i class="fa-solid fa-square-minus"></i></a>
			</ul>
		</div>
		
	</div>
		<%if(noOfMarksResults == 0){ %>
			
				<div class="card card-body text-center ">
						
					<h6><i class="fa-solid fa-clipboard-list"></i>
						<a href="/exam/student/getMostRecentResults" ><small class="text-dark" >View Result</small></a></h6>
					
				</div>
			
					<%}else{%>	
					<div id="collapseSix" class="collapse mb-2">
					<div class="card card-body text-center ">
						<h6 ><i class="fa-solid fa-clipboard-list"></i>
						<a href="/exam/student/getMostRecentResults"><small class="text-dark" >View Result</small></a></h6>
					
				</div>
				</div>
					 <div id="collapseSix" class="collapse show">
					 <div class="table-responsive">
						<table class=" table ">
  						<thead>
    					<tr>
     						
							<th scope="col" class="text-center">SR ID</th>
							<th scope="col" >SUBJECT</th>
							<th scope="col" class="text-center">TEE</th>
 						<%-- <%if(!"Project".equalsIgnoreCase(marksBean.getSubject()) && !"Module 4 - Project".equalsIgnoreCase(marksBean.getSubject())){ %>  --%>
							<th scope="col" class="text-center">ASSIGNMENT</th>
							<%-- <%} %> --%>
							<th scope="col" class="text-center">TOTAL</th>
					
					</tr>
					</thead>
					<tbody class="table-group-divider">

						<%
		          ArrayList<String> sectionColors = new ArrayList(Arrays.asList("bg-success ", "bg-danger", "bg-warning","bg-primary", "#6f42c1", "bg-secondary"));
		          int resultsCount = 0;
		          int index = 0;
		          String assignmentSectionClass = "";
		          for(StudentMarksBean marksBean : studentMarksList){ 
		        	  index = resultsCount % 6;
		        	  assignmentSectionClass = sectionColors.get(index);
		        	  resultsCount++;
		        	  
		        	  String writtenScore = "--";
		        	  if(marksBean.getWritenscore() != null && !"".equals(marksBean.getWritenscore() )){
		        		  writtenScore = marksBean.getWritenscore();
		        	  }
		        	  
		        	  String assignmentScore = "--";
		        	  if(marksBean.getAssignmentscore() != null && !"".equals(marksBean.getAssignmentscore() )){
		        		  assignmentScore = marksBean.getAssignmentscore();
		        	  }
		        	  
		          %>

					<tr>

						<th scope="row" class="text-center"><%=resultsCount %></td>
						<td><%=marksBean.getSubject() %></td>
						<td class="text-center"><%=writtenScore %></td>
						
						<!--  need to discuss on this condition -->
						<td class="text-center">
						 <%if(!"Project".equalsIgnoreCase(marksBean.getSubject()) && !"Module 4 - Project".equalsIgnoreCase(marksBean.getSubject())){ %> 
									<%=assignmentScore %>
							 <%} %> 
							</td>
								<td class="text-center"><%=marksBean.getTotal()%></td>
								</tr>
						
		
							<%
			        	if(resultsCount == 6){
			        		break;
			        	}
			          }//End of for loop 
		          
		          %> 
</tbody>
</table>
</div>
						</div> 

						<%
				}%>
				<!--ALL results Content ENDS-->
	
		






<!--MODAL FOR RESULTS -->
<% if(null != studentMarksList) { %>
<div class="modal fade results" id="resultsModal" tabindex="-1"
	role="dialog">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<h1 class="modal-title fs-5" id="modalLabel">RESULTS</h1>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
			</div>
			<div class="modal-body">
				<div class="table-responsive">
					<table class="table">
						<thead>
							<tr>
								<th>Subject</th>
								<th class="text-center">TEE</th>
								<th class="text-center">Assignment</th>
								<th class="text-center">Total</th>
							</tr>
						</thead>

						<tbody>
							<%
			          int resultsCount = 0;
			          for(StudentMarksBean marksBean : studentMarksList){ 
			        	  resultsCount++;
			        	  
			        	  String writtenScore = "--";
			        	  if(marksBean.getWritenscore() != null && !"".equals(marksBean.getWritenscore() )){
			        		  writtenScore = marksBean.getWritenscore();
			        	  }
			        	  
			        	  String assignmentScore = "--";
			        	  if(marksBean.getAssignmentscore() != null && !"".equals(marksBean.getAssignmentscore() )){
			        		  assignmentScore = marksBean.getAssignmentscore();
			        	  }

			          %>

							<tr>
								<td><%=marksBean.getSubject() %></td>
								<td class="text-center"><%= writtenScore %></td>
								<td class="text-center">
									<%if(!"Project".equalsIgnoreCase(marksBean.getSubject()) && !"Module 4 - Project".equalsIgnoreCase(marksBean.getSubject())){ %>
										<%= assignmentScore %>
									<%} %>	
								</td>
								<td class="text-center"><%=marksBean.getTotal() %></td>
							</tr>

							<%
				          }//End of for loop 
			          
			          %>

						</tbody>
					</table>
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-bs-dismiss="modal">Done</button>
			</div>
		</div>
	</div>
</div>
<% } %> <!--added by Vilpesh on 2021-10-29-->
</div>
<% } %> 