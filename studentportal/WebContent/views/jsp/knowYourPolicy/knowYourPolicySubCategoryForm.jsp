<!DOCTYPE html>
<%@page import="com.nmims.beans.KnowYourPolicyBean"%>
<%@page import="java.util.*"%>
<%@page import="java.text.DateFormat"%>
<html lang="en">
<%@page import="com.nmims.beans.PageStudentPortal"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Policy SubCategory" name="title" />
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
			<jsp:param value="Student Portal;Policy SubCategory"
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
						<h2 class="red text-capitalize">Add Policy SubCateogory</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%@ include file="../adminCommon/messages.jsp"%>
							<form:form action="addsubcategory" method="POST"
								modelAttribute="bean">
								<fieldset>
									<div class="col-md-4">
										<div class="form-group">
											<label for="categoryId">Category:</label>
											<form:select id="categoryId" path="categoryId"
												class="form-control" required="required">
												<form:option value="0">-- Select Category --</form:option>
												<c:forEach items="${categorymap}" var="category"
													varStatus="status">
													<form:option value="${category.key }">
														<c:out value="${category.value }"></c:out>
													</form:option>
												</c:forEach>

											</form:select>
										</div>
									</div>
									<div class="col-md-4">
										<div class="form-group">
											<label for="subcategoryName">Sub Category:</label> <input
												type="text" class="form-control" name="subcategoryName"
												id="subcategoryName" placeholder="Enter Sub Cateogory Here!"
												required="required" />
										</div>
									</div>

								</fieldset>
								<div class="form-group" style="margin-left: 1vw">
									<input type="reset" class="btn btn-danger" /> <input
										type="submit" value="save" id="savesubcategory"
										class="btn btn-primary" />
								</div>
							</form:form>
						</div>
						<div class="table-responsive">
							<table id="table_id"
								class="table datatable table-striped table-hover tables"
								>
								<thead>
									<tr style="text-align: center">
										<td>Sr No</td>
										<td>Id</td>
										<td>Sub Category Name</td>
										<td>Action</td>
									</tr>
								</thead>
								<tbody>
									<c:forEach items="${subcateogorymap}" var="subcategory"
										varStatus="status">
										<tr style="text-align: center"
											value="<c:out value="${subcategory.subcategoryId}~${subcategory.subcategoryName}~${subcategory.categoryId }" ></c:out>">
											<td><c:out value="${status.count }"></c:out></td>
											<td><c:out value="${subcategory.subcategoryId }"></c:out></td>
											<td><c:out value="${subcategory.subcategoryName}"></c:out></td>
											<td></td>
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

	<script type="text/javascript">
		var id = "";
		var str="";
		$(".tables").on('click', 'tr', function(e) {
			e.preventDefault();
			 str = $(this).attr('value');
			 id = str.split('~');
		});

		
		$('.tables')
				.Tabledit(
						{
							columns : {
								identifier : [ 1, 'subcategoryId' ],
								editable : [
										 [ 2, 'subcategoryName' ]
										]
							},

							// link to server script
							// e.g. 'ajax.php'
							url : "",
							// class for form inputs
							inputClass : 'form-control input-sm',
							// // class for toolbar
							toolbarClass : 'btn-toolbar',
							// class for buttons group
							groupClass : 'btn-group btn-group-sm',
							// class for row when ajax request fails
							dangerClass : 'warning',
							// class for row when save changes
							warningClass : 'warning',
							// class for row when is removed
							mutedClass : 'text-muted',
							// trigger to change for edit mode.
							// e.g. 'dblclick'
							eventType : 'click',
							// change the name of attribute in td element for the row identifier
							rowIdentifier : 'id',
							// activate focus on first input of a row when click in save button
							autoFocus : true,
							// hide the column that has the identifier
							hideIdentifier : false,
							// activate edit button instead of spreadsheet style
							editButton : true,
							// activate delete button
							deleteButton : true,
							// activate save button when click on edit button
							saveButton : true,
							// activate restore button to undo delete action
							restoreButton : true,
							// custom action buttons
							// executed after draw the structure
							onDraw : function() {
								$('.tables').DataTable();
							},

							// executed when the ajax request is completed
							// onSuccess(data, textStatus, jqXHR)
							onSuccess : function() {

								return;
							},

							// executed when occurred an error on ajax request
							// onFail(jqXHR, textStatus, errorThrown)
							onFail : function() {
								return;
							},

							// executed whenever there is an ajax request
							onAlways : function() {
								return;
							},

							// executed before the ajax request
							// onAjax(action, serialize)

							onAjax : function(action, serialize) {
								
								if(action=='edit')
									{
								
								serialize['categoryId']=id[2];
								setTimeout(()=>{
								body = JSON.stringify(serialize)
								$.ajax({
									type : "POST",
									url : 'updatepolicysubcategory',
									contentType : "application/json",
									data : body,
									dataType : "json",
									success : function(response) {
										console.log(response)
										if (response.status == "success") {
											alert('Entry Saved Successfully');
											window.location.href = "knowYourPolicySubCategory";
										}else if(response.status == "duplicate"){
											alert('Sub Category Already Exists !');
											window.location.href = "knowYourPolicySubCategory";
										}  else {
											alert('Entries Failed to update. Reload page and retry');
											window.location.href = "knowYourPolicySubCategory";
										}

									}
								});
								}, 100);
									}
								if(action=='delete')
									{
									
									
									let prompt = confirm("Are you sure you want to delete? Once you deleted this Category then all The Policies related to this will get delete Automatically");
									if(!prompt){
										return false;
									}
									
									setTimeout(()=>{
										$.ajax({
											type : "GET",
											url : 'deletepolicysubcategory?subcategoryId='+serialize.subcategoryId,
											contentType : "application/json",
											dataType : "json",
											success : function(response) {
												console.log(response)

												if (response.status == "success") {
													alert('Entry deleted Successfully')
													window.location.href = "knowYourPolicySubCategory";
													
												}
												else {
													alert('Entries Failed to delete. Reload page and retry')
													window.location.href = "knowYourPolicySubCategory";
												}

											}
										});
										}, 100);
									}
							}

						});
		</script>
</body>
</html>