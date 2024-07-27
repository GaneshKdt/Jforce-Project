<%@page import="java.util.Arrays"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<%@page import="java.util.ArrayList"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%
String collapseResultsSection = "";
ArrayList<StudentMarksBean> studentMarksList = (ArrayList<StudentMarksBean>)session.getAttribute("studentMarksList_studentportal");
int noOfMarksResults = studentMarksList.size();
String mostRecentResultPeriod = (String)session.getAttribute("mostRecentResultPeriod_studentportal");

if(noOfMarksResults > 0){
	collapseResultsSection = "in";//Adding "in" class will expand Results section. Expand only when results are present
}

%>


<div class="result open">
	<div class="panel panel-default">
		<div class="panel-heading" role="tab" id="">
			<h4 class="panel-title">RESULTS</h4>
			<ul class="topRightLinks list-inline">
				<!-- <li class="borderRight"><a href="#">SEE PAST RESULTS </a></li> -->
				<li><a href="#" data-toggle="modal" data-target="#resultsModal">SEE
						ALL</a></li>
				<li><a class="panel-toggler collapsed" role="button"
					data-toggle="collapse" data-parent="#accordion"
					href="#collapseFour" aria-expanded="true"></a></li>
			</ul>
			<div class="clearfix"></div>
		</div>
		<div id="collapseFour"
			class="panel-collapse collapse <%=collapseResultsSection %> courses-panel-collapse"
			role="tabpanel" aria-labelledby="headingFour">
			<div class="panel-body">
				<%if(noOfMarksResults == 0){ %>
				<div class="no-data-wrapper">
					<p class="no-data">
						<span class="icon-exams"></span>Most recent results declared :
						<%=mostRecentResultPeriod %>. You have 0 records for
						<%=mostRecentResultPeriod %></p>
				</div>
				<%}else{ %>


				<!--ALL results Content -->
				<div class="row">
					<div class="p-closed">
						<div class="no-data-wrapper">
							<p class="no-data">
								<span class="icon-exams"></span>
								<%=noOfMarksResults %>
								<a href="#">Results</a>
							</p>
						</div>
					</div>

					<%
          ArrayList<String> sectionColors = new ArrayList(Arrays.asList("green", "red", "yellow","blue", "purple", "gray"));
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

					<div class="col-lg-4 col-md-6">
						<section class="<%=assignmentSectionClass%>">
							<div class="ins">
								<h4><%=marksBean.getSubject() %></h4>
								<h5>
									<span><%=writtenScore %></span> TEE
								</h5>
								<%if(!"Project".equalsIgnoreCase(marksBean.getSubject()) && !"Module 4 - Project".equalsIgnoreCase(marksBean.getSubject())){ %>
								<h5>
									<span><%=assignmentScore %></span> Assignment
								</h5>
								<%} %>
							</div>
							<span class="total"><%=marksBean.getTotal() %></span>
						</section>
					</div>
					<%if(resultsCount % 2 == 0) {%>
					<div class="clearfix visible-md"></div>
					<%} %>
					<%if(resultsCount % 3 == 0) {%>
					<div class="clearfix visible-lg"></div>
					<%} %>

					<%
	        	if(resultsCount == 6){
	        		break;
	        	}
	          }//End of for loop 
          
          %>


				</div>

				<%} %>
				<!--ALL results Content ENDS-->
			</div>
		</div>
	</div>
</div>




<!--MODAL FOR RESULTS -->
<div class="modal fade results" id="resultsModal" tabindex="-1"
	role="dialog">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title">RESULTS</h4>
			</div>
			<div class="modal-body">
				<div class="table-responsive">
					<table class="table courses-sessions">
						<thead>
							<tr>
								<th>Subject</th>
								<th>TEE</th>
								<th>Assignment</th>
								<th>Total</th>
							</tr>
						</thead>

						<tbody>
							<%
			          int resultsCount = 0;
			          for(StudentMarksBean marksBean : studentMarksList){ 
			        	  resultsCount++;
			          %>

							<tr>
								<td><%=marksBean.getSubject() %></td>
								<td><%=marksBean.getWritenscore() %></td>
								<td><%=marksBean.getAssignmentscore() %></td>
								<td><%=marksBean.getTotal() %></td>
							</tr>

							<%
				          }//End of for loop 
			          
			          %>

						</tbody>
					</table>
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Done</button>
			</div>
		</div>
	</div>
</div>