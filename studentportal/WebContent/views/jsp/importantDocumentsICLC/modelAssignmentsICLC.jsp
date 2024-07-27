
<!DOCTYPE html>


<html lang="en">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Model Assignments" name="title" />
</jsp:include>



<body>

	<%@ include file="../adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="Important Documents;Model Assignments"
				name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>


				<div class="sz-content-wrapper examsPage">
					<%@ include file="../adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">

						<h2 class="red text-capitalize">Model Assignments</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper">
							<%@ include file="../common/messages.jsp"%>

							<div class="table-responsive">
								<table class="table table-striped table-hover"
									style="font-size: 12px">
									<thead>
										<tr>
											<th>Sr. No.</th>
											<th>Subject</th>
											<th>Model Answers</th>
										</tr>
									</thead>
									<tbody>

										<tr>
											<td>1</td>
											<td>Corporate Finance</td>
											<td><a
												href="${pageContext.request.contextPath}/resources_2015/modelAnswers/Corporate_Finance_Model_Answer.pdf"
												target="_blank"><i class="fa-solid fa-download"></i>Download</a></td>
										</tr>

										<tr>
											<td>1</td>
											<td>Information Systems for Managers</td>
											<td><a
												href="${pageContext.request.contextPath}/resources_2015/modelAnswers/Information_System_for_Managers_Model_Answer.pdf"
												target="_blank"><i class="fa-solid fa-download"></i>Download</a></td>
										</tr>

										<tr>
											<td>1</td>
											<td>Marketing Management</td>
											<td><a
												href="${pageContext.request.contextPath}/resources_2015/modelAnswers/Marketing_Management_Model_Answer.pdf"
												target="_blank"><i class="fa-solid fa-download"></i>Download</a></td>
										</tr>

										<tr>
											<td>1</td>
											<td>Operations Management</td>
											<td><a
												href="${pageContext.request.contextPath}/resources_2015/modelAnswers/Operations_Management_Model_Answer.pdf"
												target="_blank"><i class="fa-solid fa-download"></i>Download</a></td>
										</tr>

										<tr>
											<td>1</td>
											<td>Organisational Behaviour</td>
											<td><a
												href="${pageContext.request.contextPath}/resources_2015/modelAnswers/Organisational_Behaviour_Model_Answer.pdf"
												target="_blank"><i class="fa-solid fa-download"></i>Download</a></td>
										</tr>


									</tbody>
								</table>
							</div>
						</div>

					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="../adminCommon/footer.jsp" />


</body>
</html>