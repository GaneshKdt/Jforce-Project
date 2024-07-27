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
						<h2 class="red text-capitalize">Policy Entry</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%@ include file="../adminCommon/messages.jsp"%>
							<form:form action="updatepolicy" method="POST"
								modelAttribute="policybean">
								<fieldset>
									<c:forEach items="${policy}" var="policy">
										<div class="form-group">
											<label for="groupId">Program Group:</label>
											<form:select id="groupId" name="groupId" path="groupId"
												class="form-control" required="required">
												<form:option value="${policy.groupId}">
													<c:out value="${policy.groupName}"></c:out>
												</form:option>
												<c:forEach items="${groupmap }" var="group"
													varStatus="status">
													<form:option value="${group.key }">
														<c:out value="${group.value }"></c:out>
													</form:option>
												</c:forEach>
											</form:select>
										</div>

										<div class="form-group">
											<label for="categoryId">Category:</label>
											<form:select id="categoryId" name="categoryId"
												path="categoryId" class="form-control">
												<form:option value="${policy.categoryId}">
													<c:out
														value="${empty policy.categoryName ? 'Select Category' : policy.categoryName}"></c:out>
												</form:option>
												<c:forEach items="${categorymap }" var="category"
													varStatus="status">
													<form:option value="${category.key }">
														<c:out value="${category.value }"></c:out>
													</form:option>
												</c:forEach>
											</form:select>
										</div>

										<div class="form-group">
											<label for="subcategoryId">Sub Category:</label>
											<form:select id="subcategoryId" name="subcategoryId"
												path="subcategoryId" class="form-control">
												<form:option value="${policy.subcategoryId}">
													<c:out
														value="${empty policy.subcategoryName ? 'Select Sub Category' : policy.subcategoryName}"></c:out>
												</form:option>
											</form:select>
										</div>

										<div class="form-group">
											<label for="title">Policy Title:</label>
											<textarea id="title" path="title" name="title"
												class="form-control" placeholder="Enter Policy Title Here!"
												required="required"><c:out
													value="${policy.title}"></c:out></textarea>
										</div>

										<div class="form-group">
											<label for="description">Policy Description:</label>
											<textarea id="description" name="description"
												path="description" class="form-control ckeditor"><c:out
													value="${policy.description}"></c:out></textarea>
										</div>
									</c:forEach>

								</fieldset>
								<input type="submit" id="update" value="update"
									class="btn btn-primary" />
								<button class="btn btn-danger">
									<a style="color: white" href="knowYourPolicyEntry">cancel</a>
								</button>
							</form:form>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="../adminCommon/footer.jsp" />
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/assets/js/knowYourPolicy/knowYourPolicy.js"></script>

</body>
</html>