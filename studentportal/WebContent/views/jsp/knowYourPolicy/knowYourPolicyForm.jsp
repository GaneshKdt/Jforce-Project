<!DOCTYPE html>
<%@page import="com.nmims.beans.KnowYourPolicyBean"%>
<%@page import="java.util.*"%>
<%@page import="java.text.DateFormat"%>
<html lang="en">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Policy's" name="title" />
</jsp:include>
<style>
.dataTables_filter {
   width: 50%;
   float: right;
   text-align: right;
}
</style>
<body>
	<%@ include file="../adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">
		<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="Student Portal;Policy's" name="breadcrumItems" />
		</jsp:include>
		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>
				<div class="sz-content-wrapper examsPage">
					<%@ include file="../adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">
						<h2 class="red text-capitalize">Policy's</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%@ include file="../adminCommon/messages.jsp"%>
							<div class="table-responsive">
								<table id="table_id"
									class="table datatable table-striped table-hover tables"
									style="font-size: 12px">
									<thead>
										<tr style="text-align: center">
											<td>Sr. No.</td>
											<td>Id</td>
											<td>title</td>
											<td>description</td>
											<td>Group Name</td>
											<td>Category Name</td>
											<td>SubCategory Name</td>
										</tr>
									</thead>
									<tbody>
										<c:forEach var="bean" items="${policylist}" varStatus="status">
											<tr style="text-align: center">
												<td><c:out value="${status.count}"></c:out></td>
												<td><c:out value="${bean.policyId }"></c:out></td>
												<td><c:out value="${bean.title }"></c:out></td>
												<td>${bean.description}</td>
												<td><c:out value="${bean.groupName}"></c:out></td>
												<td><c:out
														value="${empty bean.categoryName ? 'No Category' : bean.categoryName}"></c:out></td>
												<td><c:out
														value="${empty bean.subcategoryName ? 'No Sub Category' : bean.subcategoryName}"></c:out></td>
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
	</div>
	<jsp:include page="../adminCommon/footer.jsp" />
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