<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.nmims.beans.ContentStudentPortalBean"%>
<%@page import="com.nmims.beans.StudentStudentPortalBean"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


<%
	/* ArrayList<String> subjectsfortool = (ArrayList<String>)session.getAttribute("studentCourses_studentportal");
	int noOfSubjectsfortool = subjectsfortool != null ? subjectsfortool.size() : 0; */
	String selectedSubjectforTool = (String)request.getAttribute("subject");
	
	StudentStudentPortalBean sbeanTool = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
%>


<div class="course-learning-resources-m-wrapper" id="toolAccess">
	<div class="panel panel-default panel-courses-page">
		<div class="panel-heading" role="tab" id="">
			<h2>Tool Access</h2>
			<!---TOP TABS-->
			<div class="custom-clearfix clearfix"></div>

			<%if(("EPBM".equalsIgnoreCase(sbeanTool.getProgram()) && "Enterprise Guide".equalsIgnoreCase(selectedSubjectforTool)) || ("EPBM".equalsIgnoreCase(sbeanTool.getProgram()) && "Enterprise Miner".equalsIgnoreCase(selectedSubjectforTool))){%>
			<ul class="topRightLinks list-inline">
				<li><a class="panel-toggler collapsed" role="button"
					data-toggle="collapse" href="#collapseThree" aria-expanded="true"></a></li>
			</ul>
			<%} %>

			<div class="clearfix"></div>



		</div>
		<div class="clearfix"></div>





		<%-- <%if("MPDV".equalsIgnoreCase(sbeanTool.getProgram()) && "Visual Analytics".equalsIgnoreCase(selectedSubjectforTool) ){ %>
						<p class="no-data"><span class="icon-icon-wrench"></span>Tool Access Will be provided before the sessions go live for Visual Analytics.</p> --%>
		<% if ("EPBM".equalsIgnoreCase(sbeanTool.getProgram()) && "Enterprise Miner".equalsIgnoreCase(selectedSubjectforTool)) {%>
		<div class="col-md-12 ">
			<div class="table-responsive">
				<table class="table table-striped " id="toollinks">
					<thead>
						<tr>
							<th>SI</th>
							<th>Name</th>
							<th>Description</th>
							<th>Action</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td>1</td>
							<td>Steps to access Enterprise Miner_VLE</td>
							<td>Step by step process to access Enterprise Miner virtual
								learning environment</td>
							<td><a
								href="resources_2015/sasPDF/Steps to access Eminer_VLE.PDF"
								target="_blank"> View/Download</a></td>
						</tr>
						<tr>
							<td>2</td>
							<td>Accessing_SAS_software_AWS</td>
							<td>Step by step process to understand how a student can
								book slots on Amazon web services for working on data sets</td>
							<td><a
								href="resources_2015/sasPDF/Accessing_SASEminer_software_AWS.pdf"
								target="_blank"> View/Download</a></td>
						</tr>
						<tr>
							<td>3</td>
							<td>Click here to access the EM Tool</td>
							<td>Access to Enterprise Miner tool after creating your
								profile, first time users should refer to the document
								&ldquo;Steps to access Visual analytics VLE&rdquo;</td>
							<td><a
								href="https://login.sas.com/opensso/UI/Login?realm=/extweb&goto=https%3A%2F%2Fwww.sas.com%2Fen_in%2Fhome.html&locale=en_in"
								target="_blank"> Access</a></td>
						</tr>
					</tbody>
				</table>
				<br>
				<p style="font: Open Sans; font-weight: 600; font-size: 0.8rem;">
					<b>Instructions below:</b>
				</p>
				<ol style="font: Open Sans; font-weight: 600; font-size: 0.8rem;">
					<li>If you have not created your profile refer to the document
						&ldquo;Steps to access Enterprise Miner_VLE&rdquo;</li>
					<li>To access data sets refer to &ldquo;Accessing SAS software
						document&rdquo;</li>
					<li>Refer to your registered email id for credentials sent by
						ngasce@nmims.edu</li>
					<li>For any queries call us at 1-800-1025-136</li>
				</ol>

			</div>

		</div>
		<%} %>

		<%if("MPDV".equalsIgnoreCase(sbeanTool.getProgram()) && "Visual Analytics".equalsIgnoreCase(selectedSubjectforTool) ){ %>

		<div class="col-md-12 ">
			<div class="table-responsive">
				<table class="table table-striped " id="toollinks">
					<thead>
						<tr>
							<th>SI</th>
							<th>Name</th>
							<th>Description</th>
							<th>Action</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td>1</td>
							<td>Steps to access Visual Analytics_VLE</td>
							<td>Step by step process to access visual analytics virtual
								learning environment</td>
							<td><a
								href="resources_2015/sasPDF/Steps to access Visual Analytics_VLE.PDF"
								target="_blank"> View/Download</a></td>
						</tr>
						<tr>
							<td>2</td>
							<td>Accessing_SAS_software_AWS</td>
							<td>Step by step process to understand how a student can
								book slots on Amazon web services for working on data sets</td>
							<td><a
								href="resources_2015/sasPDF/Accessing_SAS_software_AWS.pdf"
								target="_blank"> View/Download</a></td>
						</tr>
						<tr>
							<td>3</td>
							<td>Click here to access the VA Tool</td>
							<td>Access to Visual analytics tool after creating your
								profile, first time users should refer to the document "Steps to
								access Visual analytics VLE"</td>
							<td><a
								href="https://login.sas.com/opensso/UI/Login?realm=/extweb&goto=https%3A%2F%2Fwww.sas.com%2Fen_in%2Fhome.html&locale=en_in"
								target="_blank"> Access</a></td>
						</tr>

					</tbody>
				</table>
				<br>
				<p style="font: Open Sans; font-weight: 600; font-size: 0.8rem;">
					<b>Instructions below:</b>
				</p>
				<ol style="font: Open Sans; font-weight: 600; font-size: 0.8rem;">
					<li>If you have not created your profile refer to the document
						"Steps to access Visual Analytics_VLE"</li>
					<li>To access data sets refer to "Accessing SAS software
						document"</li>
					<li>For any queries call us at 1-800-1025-136</li>
				</ol>

			</div>

		</div>

		<%}%>

		<!-- Tool Access for EPBM Enterprise Guide Start -->
		<%if("EPBM".equalsIgnoreCase(sbeanTool.getProgram()) && "Enterprise Guide".equalsIgnoreCase(selectedSubjectforTool) ){ %>

		<div class="col-md-12 ">
			<div class="table-responsive">
				<table class="table table-striped " id="toollinks">
					<thead>
						<tr>
							<th>SI</th>
							<th>Name</th>
							<th>Description</th>
							<th>Action</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td>1</td>
							<td>Steps to access Eguide_Server</td>
							<td>Step by step process to access Enterprise Guide Server
								environment</td>
							<td><a
								href="resources_2015/sasPDF/Steps to access Eguide_Server.pdf"
								target="_blank"> View/Download</a></td>
						</tr>
						<tr>
							<td>2</td>
							<td>Demo and Pre-test to login to Enterprise Guide server
								before you start using your credentials</td>
							<td>Step by step Demo walk through before you start using
								your credentials</td>
							<td><a
								href="resources_2015/sasPDF/Pre-Test for EGBS session.pdf"
								target="_blank"> View/Download</a></td>
						</tr>

					</tbody>
				</table>
				<br>
				<p style="font: Open Sans; font-weight: 600; font-size: 0.8rem;">
					<b>Instructions below:</b>
				</p>
				<ol style="font: Open Sans; font-weight: 600; font-size: 0.8rem;">
					<li>To access E guide, go through &ldquo;Step to access
						Eguide_Server &rdquo; document</li>
					<li>To practice the login process, go through &ldquo;Pre-Test
						for EGBS session&rdquo; document</li>
					<li>Refer to your registered email id for credentials sent by
						ngasce@nmims.edu</li>
					<li>For any queries call us at 1-800-1025-136</li>
				</ol>

			</div>

		</div>

		<%}%>
		<!-- End -->

	</div>
</div>


