<!DOCTYPE html>
<%@page import="com.nmims.beans.KnowYourPolicyBean"%>
<%@page import="java.util.*"%>
<%@page import="java.text.DateFormat"%>
<html lang="en">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<script src="https://cdn.ckeditor.com/4.16.2/standard/ckeditor.js"></script>
<link rel="stylesheet" type="text/css"
	href="https://cdn.datatables.net/1.11.3/css/jquery.dataTables.css">

<script type="text/javascript" charset="utf8"
	src="https://cdn.datatables.net/1.11.3/js/jquery.dataTables.js"></script>
<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Policy Entry" name="title" />
</jsp:include>
<body>
	<%@ include file="../adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">
		<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="Student Portal;Policy Entry" name="breadcrumItems" />
		</jsp:include>
		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>
				<div class="sz-content-wrapper examsPage">
					<%@ include file="../adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">
						<h2 class="red text-capitalize">Add Policy</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%@ include file="../adminCommon/messages.jsp"%>
							<form:form action="savepolicy" method="POST"
								modelAttribute="policybean">
								<fieldset>

									<div class="form-group">
										<label for="groupId">Program Group:</label>
										<form:select id="groupId" name="groupId" path="groupId"
											class="form-control" required="required">
											<form:option value="0">-- Select Program Group --</form:option>
											<c:forEach items="${groupmap}" var="group" varStatus="Status">
												<form:option value="${group.key}">
													<c:out value="${group.value}"></c:out>
												</form:option>
											</c:forEach>
										</form:select>
									</div>

									<div class="form-group">
										<label for="categoryId">Category:</label>
										<form:select id="categoryId" name="categoryId"
											path="categoryId" class="form-control">
											<form:option value="0">-- Select Category --</form:option>
											<c:forEach items="${categorymap}" var="category"
												varStatus="Status">
												<form:option value="${category.key}">
													<c:out value="${category.value}"></c:out>
												</form:option>
											</c:forEach>

										</form:select>
									</div>

									<div class="form-group">
										<label for="subcategoryId">Sub Category:</label>
										<form:select id="subcategoryId" name="subcategoryId"
											path="subcategoryId" class="form-control">
											<form:option value="0">-- Select SubCategory --</form:option>
										</form:select>
									</div>

									<div class="form-group">
										<label for="title">Policy Title:</label>
										<textarea id="title" path="title" name="title"
											class="form-control" placeholder="Enter Policy Title Here!"
											required="required"></textarea>
									</div>

									<div class="form-group">
										<label for="description">Policy Description:</label>
										<textarea id="description" name="description"
											path="description" class="form-control ckeditor"></textarea>
									</div>
								</fieldset>
								<input type="reset" id="reset" class="btn btn-danger" />
								<input type="submit" value="save" id="savepolicy"
									class="btn btn-primary" />
							</form:form>

							<div class="table-responsive">
								<table id="table_id"
									class="table datatable table-striped table-hover tables"
									style="font-size: 12px">
									<thead>
										<tr style="text-align: center">
											<td>Sr. No</td>
											<td>title</td>
											<td>description</td>
											<td>Group Name</td>
											<td>Category Name</td>
											<td>SubCategory Name</td>
											<td>Action</td>
										</tr>
									</thead>
									<tbody>
										<c:forEach var="bean" items="${policylist}" varStatus="status">
											<tr style="text-align: center">
												<td><c:out value="${status.count}"></c:out></td>
												<td><c:out value="${bean.title }"></c:out></td>
												<td>${bean.description}</td>
												<td><c:out value="${bean.groupName}"></c:out></td>
												<td><c:out
														value="${empty bean.categoryName ? 'No Category' : bean.categoryName}"></c:out></td>
												<td><c:out
														value="${empty bean.subcategoryName ? 'No Sub Category' : bean.subcategoryName}"></c:out></td>
												<td><a style="color: Green"
													href="updatepolicyform?policyId=<c:out value="${bean.policyId}"></c:out>">Update
												</a> <a style="color: Red" id="deletepolicy"
													href="deletepolicy?policyId=<c:out value="${bean.policyId}"></c:out>">Delete</a>
													</button></td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
							</div>
						</div>
						<div class="clearfix"></div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="../adminCommon/footer.jsp" />
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/assets/js/knowYourPolicy/knowYourPolicy.js"></script>
	<script
		src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js"></script>

	<script
		src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/js/bootstrap.js"></script>
	<script
		src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/js/jquery.tabledit.js"></script>

	<script
		src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />resources_2015/js/vendor/jquery-ui.min.js"></script>
	<script
		src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
	<script
		src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
	<script
		src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js"></script>
	<script>
		$(document).ready(function() {
			$('#table_id').DataTable();
		});
	</script>

</body>
</html>