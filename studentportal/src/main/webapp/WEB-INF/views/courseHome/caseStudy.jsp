<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.nmims.beans.CaseStudyStudentPortalBean"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%
	ArrayList<CaseStudyStudentPortalBean> caseStudyList = (ArrayList<CaseStudyStudentPortalBean>) session
			.getAttribute("caseStudyList");
	int noOfCaseStudy = caseStudyList != null ? caseStudyList.size()
			: 0;
	System.out.println("caseStudyList---" + caseStudyList);
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


<div class="course-learning-resources-m-wrapper" id="learningResources">
	<div class="panel-courses-page">
		<div class="panel-heading" role="tab" id="">
			<h2>Case Study</h2>
			<!---TOP TABS-->
			<div class="custom-clearfix clearfix"></div>
			<ul class="topRightLinks list-inline">
				<li>
					<h3 class=" green">
						<span><%=noOfCaseStudy%></span>Case Study Available
					</h3>
				</li>


				<li><a class="panel-toggler collapsed" role="button"
					data-toggle="collapse" href="#collapseCaseStudy"
					aria-expanded="true"></a></li>
				<div class="clearfix"></div>
			</ul>
			<div class="clearfix"></div>
		</div>
		<div class="clearfix"></div>

		<%-- <%if(noOfCaseStudy == 0){ %>
			<div id="collapseThree" class="panel-collapse collapse academic-schedule courses-panel-collapse panel-content-wrapper" role="tabpanel">
		<%}else{ %> --%>
			<div id="collapseFive" class=" collapse in academic-schedule courses-panel-collapse panel-content-wrapper accordion-has-content" role="tabpanel">
		<%-- <%} %> --%>
		


				<%
			if (noOfCaseStudy == 0) {
		%>


			<div class="panel-body" style="padding: 20px;"> 
				<div class="no-data-wrapper">
					<h6 class="no-data nodata"> 
						<span class="icon-icon-pdf"></span>No New Case Study
					</h6>
				</div>

			</div>
	
		<%
			} else {
		%>


			<div class="panel-body">
				<div class="data-content">
					<div class="col-md-12 p-closed">
						<i class="icon-icon-view-submissions"></i>
						<h4>
							<span> <%=noOfCaseStudy%></span> Case Study Available<span
								class="expand">Expand to view all Case Studies</span>
						</h4>
					</div>
					<div class="table-responsive">
						<table class="table table-striped "
							id="courseHomeLearningResources">
							<thead>
								<tr>
									<th>SI</th>
									<th>Name</th>
									<!-- <th>Description</th> -->
									<th>Action</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="contentFile" items="${caseStudyList}"
									varStatus="status">
									<tr>
										<td><c:out value="${status.count}" /></td>
										<td><c:out value="${contentFile.topic}" /></td>
										<td ><c:out value="${contentFile.description}"/></td>

												<td><c:if test="${contentFile.filePath ne null}">
														<c:if test="${not empty contentFile.filePath}">
															<a href="#"
																onClick="window.open('<spring:eval expression="@propertyConfigurer.getProperty('CONTENT_PREVIEW_PATH')" />${contentFile.filePath}')" /> Click to submit</a>
														</c:if>

														<c:if
															test="${fn:endsWith(contentFile.filePath, '.pdf') || fn:endsWith(contentFile.filePath, '.PDF')
											|| fn:endsWith(contentFile.filePath, '.Pdf')}">
															<a
																href="/exam/viewSingleCaseStudy?year=${contentFile.year}&month=${contentFile.month}&topic=${contentFile.topic}&status=${contentFile.status}">Click
																to Submit</a>
														</c:if>
													</c:if></td>

											</tr>
										</c:forEach>
									</tbody>
								</table>
							</div>
							<%
						if (noOfCaseStudy > 5) {
					%>
							<div class="load-more-table">
								<a>+<%=(noOfCaseStudy - 5)%> More Resources <span
									class="icon-accordion-closed"></span></a>
							</div>
							<%
						}
					%>
						</div>
					</div>
					<%
				}
			%>



		
	</div>
</div>


		<%if(noOfCaseStudy > 0){ %>
		<div class="modal fade assignments" id="caseStudyPanel" tabindex="-1"
			role="dialog">
			<div class="modal-dialog" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
						<h4 class="modal-title">Case Study</h4>
					</div>
					<div class="modal-body">


						<div class="table-responsive">
							<table class="table table-striped "
								id="courseHomeLearningResources">
								<thead>
									<tr>
										<th>SI</th>
										<th>Name</th>
										<!-- <th>Description</th> -->
										<th>Action</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="contentFile" items="${lastCycleContentList}"
										varStatus="status">
										<tr>
											<td><c:out value="${status.count}" /></td>
											<td><c:out value="${contentFile.topic}" /></td>
											<td><c:out value="${contentFile.description}" /></td>

											<td><c:if test="${not empty contentFile.filePath}">
													<a href="#"
														onClick="window.open('<spring:eval expression="@propertyConfigurer.getProperty('CONTENT_PREVIEW_PATH')" />${contentFile.filePath}')" /> Download</a>
												</c:if> <c:if
													test="${fn:endsWith(contentFile.filePath, '.pdf') || fn:endsWith(contentFile.filePath, '.PDF')
										|| fn:endsWith(contentFile.filePath, '.Pdf')}">
													<a
														href="<c:url value = "../acads/previewContent?filePath=${contentFile.filePath}&name=${contentFile.name}" />  target="_blank">View</a>
												</c:if> <c:if test="${not empty contentFile.webFileurl}">
													<c:if
														test="${contentFile.urlType == 'View' || contentFile.urlType == '' || empty contentFile.urlType	}">
														<a href="${contentFile.webFileurl}" target="_blank">View</a>
													</c:if>
													<c:if test="${contentFile.urlType == 'Download'}">
														<a href="${contentFile.webFileurl}" target="_blank">
															Download</a>
													</c:if>
												</c:if></td>

										</tr>
									</c:forEach>
								</tbody>
							</table>
						</div>
					</div>
				</div>
			</div>
		</div>


	</div>

	<%}%>