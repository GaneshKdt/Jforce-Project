
<!DOCTYPE html>


<%@page import="com.nmims.beans.FaqCategoryBean"%>
<%@page import="com.nmims.beans.FaqQuestionAnswerTableBean"%>
<%@page import="java.util.HashMap"%>
<html lang="en">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri ="http://www.springframework.org/tags" %>
<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="FAQ Category Entry" name="title" />
</jsp:include>

<link rel="stylesheet" type="text/css"
	href="https://cdn.datatables.net/1.11.3/css/jquery.dataTables.css">

<script type="text/javascript" charset="utf8"
	src="https://cdn.datatables.net/1.11.3/js/jquery.dataTables.js"></script>


<script src="https://cdn.ckeditor.com/4.16.2/standard/ckeditor.js"></script>
<body>
	<%
	ArrayList<FaqCategoryBean> categorylist = (ArrayList<FaqCategoryBean>) request.getAttribute("categorylist");
	%>
	<%@ include file="../adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="Important Documents;FAQ Category Entry"
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

						<div class="clearfix"></div>


						<div class="common-content supportWrap">

							<div class="row">
								<div class="col-lg-9 col-sm-6 col-xs-12">
									<h3 class="information-title red">Add  Category</h3>
								</div>

							</div>
							<%
							if (String.valueOf(request.getAttribute("succes")).equals("true")) {
							%>
							<div class="alert alert-success" id="succesalert">
								<strong>Success!</strong> Entered Successfully.
							</div>
							<%
							}
							%>

							<%
							if (String.valueOf(request.getAttribute("succes")).equals("false")) {
							%>
							<div class="alert alert-danger" id="failalert">
								<strong>Failed!</strong> Please add New Category.
							</div>
							<%
							}
							%>



							<div class="panel-content-wrapper" style="min-height: 200px;">

								<form action="addFaqCategoryEntry" method="post">

									<div class="form-group">
										<label for="categoryname">Enter Category</label> <input
											type="text" class="form-control is-invalid" id="categoryname"
											placeholder="Enter Category Name" name="category"
											style="width: 350px" required>
									</div>

									<button type="submit" class="btn btn-default"
										onclick="return validation()">Submit</button>
								</form>


							</div>
							<div class="panel-content-wrapper" style="min-height: 450px;">
								<div class="table-responsive">
									<table id="table_id"
										class="table  table-striped table-hover tables"
										style="font-size: 12px">
										<thead>
											<tr>
												<th style="text-align:center">Sr No</th>
												<th>Category Name</th>
												<th style="text-align:center">Action</th>
											</tr>
										</thead>
										<tbody>
											<%
											for (FaqCategoryBean category : categorylist) {
											%>
											<tr id="<%=category.getId()%>">
												<td style="text-align:center"><%=category.getId()%></td>
												<td><%=category.getCategoryname()%></td>
												<td style="text-align:center"></td>

											</tr>
											<%
											}
											%>

										</tbody>
									</table>
								</div>
							</div>


							<div class="clearfix"></div>



						</div>
					</div>
				</div>

				<div>></div>
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




		<script type="text/javascript">
		var id = "";
		$(".tables").on('click', 'tr', function(e) {
			e.preventDefault();
		});

		
		$('.tables')
				.Tabledit(
						{
							columns : {
								identifier : [ 0, 'id' ],
								editable : [
										 [ 1, 'categoryname' ]
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
								
									
								setTimeout(()=>{
								body = JSON.stringify(serialize)
	                           console.log("action is "+action+" serialize is "+body)
								$.ajax({
									type : "POST",
									url : 'updateFaqCategory',
									contentType : "application/json",
									data : body,
									dataType : "json",
									success : function(response) {
										console.log(response)

										if (response.status == "success") {
											alert('Entry Saved Successfully')
											window.location.href = "faqCategoryEntryPage";
										} else {
											alert('Entries Failed to update. Reload page and retry')
											window.location.href = "faqCategoryEntryPage";
										}

									}
								});
								}, 100);
									}
								if(action=='delete')
									{
									
									
									let prompt = confirm("Are you sure you want to delete? Once you deleted this Category then all The FAQs related to this will get delete Automatically");
									if(!prompt){
										return false;
									}
									
									setTimeout(()=>{
										body = JSON.stringify(serialize)
			                           console.log("action is "+action+" serialize is "+serialize.id)
										$.ajax({
											type : "POST",
											url : 'deletefaqcategory?id='+serialize.id,
											contentType : "application/json",
											dataType : "json",
											success : function(response) {
												console.log(response)

												if (response.status == "success") {
													alert('Entry deleted Successfully')
													window.location.href = "faqCategoryEntryPage";
													
												} else {
													alert('Entries Failed to delete. Reload page and retry')
													window.location.href = "faqCategoryEntryPage";
												}

											}
										});
										}, 100);
									}
							}

						});
		</script>

		<script>
		
		var duration = 5000; //2 seconds
	    setTimeout(function () { $('#succesalert').hide(); }, duration);
		
	    setTimeout(function () { $('#failalert').hide(); }, duration);
		
	

			function validation() {
		
				if (document.getElementById("categoryname").value == ""
						) {
					alert("Please select options properly");
					return false;
				}

				return true;
			}
			

		</script>
</body>
</html>