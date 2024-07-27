<!DOCTYPE html>
<%@page import="java.util.*"%>
<%@page import="java.text.DateFormat"%>
<html lang="en">
<%@page import="java.util.ArrayList"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@page import="com.nmims.beans.ProgramSubjectMappingExamBean"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<style>
.panel-title .glyphicon {
	font-size: 14px;
}

.column {
	margin-bottom: 20px;
}
</style>

<%@page import="com.nmims.beans.Page"%>


<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Subject" name="title" />
</jsp:include>
<body>
	<%@ include file="../adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;Subject Entries" name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>
				<div class="sz-content-wrapper examsPage">
					<%@ include file="../adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">
						<h2 class="red text-capitalize">Add Subject Entries</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%@ include file="../adminCommon/messages.jsp"%>


							<form:form modelAttribute="subject">
								<fieldset>

									<div class="panel-body">

										<c:choose>
											<c:when test="${responseType == 'success'}">
												<div class="alert alert-success">${ message }</div>
											</c:when>
											<c:when test="${responseType == 'error'}">
												<div class="alert alert-danger">${ message }</div>
											</c:when>
										</c:choose>



										<div class="column">
											<div class="col-sm-3 column">
												<label>Subject Name <span style="color: red">*</span></label>
												<form:input id="subjectname" path="subjectname" type="text"
													placeholder="Subject Name" class="form-control"
													value="${subject.subjectname}" />
											</div>
											<div class="col-sm-3 column">
												<label>subjectbbcode</label>
												<form:input id="subjectbbcode" path="subjectbbcode"
													type="text" placeholder="subjectbbcode"
													class="form-control" value="${subject.subjectbbcode}" />
											</div>
											<div class="col-sm-3 column">
												<label>commonSubject</label>
												<form:input id="commonSubject" path="commonSubject"
													type="text" placeholder="commonSubject"
													class="form-control" value="${subject.commonSubject}" />
											</div>
											<div class="clearfix"></div>
											<div class="col-md-6 column">

												<button id="submit" name="submit"
													class="btn btn-large btn-primary"
													formaction="subjectFormData">Add Entry</button>

												<button id="cancel" name="cancel" class="btn btn-danger"
													formaction="home" formnovalidate="formnovalidate">Cancel</button>
											</div>
								</fieldset>
							</form:form>









							<div class="clearfix"></div>
							<div class="column">
								<legend>
									&nbsp;Existing Subjects Entries <font size="2px"> </font>
								</legend>
								<div class="table-responsive">
									<table class="table table-striped table-hover tables"
										style="font-size: 12px">
										<thead>
											<tr>
												<th>Id</th>
												<th>Subject Name</th>
												<th>Subjectbbcode</th>
												<th>Common Subject</th>

											</tr>
										</thead>
										<tbody>

											<c:forEach var="subjectList" items="${subjectsList}"
												varStatus="status">
												<tr
													value="${subjectList.id}~${subjectList.subjectname}~${subjectList.subjectbbcode}~${subjectList.commonSubject}">
													<td><c:out value="${subjectList.id}" /></td>
													<td><c:out value="${subjectList.subjectname}" /></td>
													<td><c:out value="${subjectList.subjectbbcode}" /></td>
													<td><c:out value="${subjectList.commonSubject}" /></td>
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
	</div>
	<jsp:include page="../adminCommon/footer.jsp" />


</body>
<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery-1.11.3.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/bootstrap.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery.tabledit.js"></script>

<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/jquery-ui.min.js"></script>
<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js"></script>

<script>
	var id
	$(".tables").on('click', 'tr', function(e) {
		e.preventDefault();
		var str = $(this).attr('value');
		id = str.split('~');
		console.log(id)

	});

	$(".tables").on(
			'change',
			'input[name="subject name"]',
			function(e) {
				id = id[0] + "~" + $(this).val() + "~" + id[2] + "~" + id[3];
				$(this).parent().parent().attr("value", id);
				console.log("========> input value changes : " + $(this).val()
						+ " | " + id);
			});

	$(".tables").on(
			'change',
			'input[name="subject bb code"]',
			function(e) {
				id = id[0] + "~" + id[1] + "~" + $(this).val() + "~" + id[3];
				$(this).parent().parent().attr("value", id);
				console.log("========> input value changes : " + $(this).val()
						+ " | " + id);
			});

	$(".tables").on(
			'change',
			'input[name="common Subject"]',
			function(e) {
				id = id[0] + "~" + id[1] + "~" + id[2] + "~" + $(this).val();
				$(this).parent().parent().attr("value", id);
				console.log("========> input value changes : " + $(this).val()
						+ " | " + id);
			});

	$('.tables').Tabledit(
			{

				columns : {
					identifier : [ 0, 'id' ],
					editable : [ [ 1, "subject name" ],
							[ 2, "subject bb code" ], [ 3, "common Subject" ] ]
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
				deleteButton : false,
				// activate save button when click on edit button
				saveButton : true,
				// activate restore button to undo delete action
				restoreButton : true,
				// custom action buttons
				// executed after draw the structure
				onDraw : function() {

					$('.tables').DataTable({
						initComplete : function() {

						}

					});
					return;
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
					setTimeout(() => {
					serialize['id'] = id[0];
					serialize['subjectname'] = id[1];
					serialize['subjectbbcode'] = id[2];
					serialize['commonSubject'] = id[3];
					let body = JSON.stringify(serialize);
					

					$.ajax({
						type : "POST",
						url : 'updateSubjectEntry',
						contentType : "application/json",
						data : body,
						dataType : "json",
						success : function(response) {
							console.log(response)

							if (response.Status == "Success") {
								alert('Entries Saved Successfully')
							} else {
								alert('Entries Failed to update')
							}

						}
					});
					}, 100);
				}
				
				
		/* 		onAjax: function(action, serialize) {
					setTimeout(() => {
						serialize['id'] = id[0];
						serialize['consumerTypeId'] = id[1];
						serialize['programStructureId'] = id[2];
						serialize['programId'] = id[3];
						let body = JSON.stringify(serialize);

			  		 
						
						$.ajax({
							type : "POST",
							url : 'updateConsumerProgramStructureMapping',
							contentType : "application/json",
							data : body,
							dataType : "json",
							success : function(response) {
								console.log(response)

								if (response.status == "Success") {
									alert('Entries Saved Successfully')
								} else {
									alert('Entries Failed to update : ' + response.message)
								}

							}
						});
					}, 100);
					

				} */
				
	

			});
</script>
</html>