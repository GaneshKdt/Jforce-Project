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
	<jsp:param value="Program Structure" name="title" />
</jsp:include>
<body>
	<%@ include file="../adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;Program Structure Entries"
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
						<h2 class="red text-capitalize">Add Program Structure Entries</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%@ include file="../adminCommon/messages.jsp"%>

							<form:form 
								modelAttribute="programStructureBean">
								<fieldset>

									<div class="panel-body">
										<div class="column">

											<div class="col-sm-3 column">
												<label>ProgramStructure Name</label>
												<form:input id="subject" path="program_structure"
													type="text" placeholder="ProgramStructure Name"
													class="form-control" required="required"
													value="${programStructureBean.program_structure}" />
											</div>
											<div class="clearfix"></div>
											<div class="col-md-6 column">

											<button id="submit" name="submit"
													class="btn btn-large btn-primary"
													formaction="programStructureFormData">Add Entry</button> 
											<button id="cancel" name="cancel" class="btn btn-danger"
													formaction="${pageContext.request.contextPath}/home" formnovalidate="formnovalidate">Cancel</button>
											</div>

										</div>

									</div>

								</fieldset>
							</form:form>

							<div class="clearfix"></div>
							<div class="column">
								<legend>&nbsp;Existing ProgramStructure Entries</legend>
								<div class="table-responsive">
									<table class="table table-striped table-hover tables"
										style="font-size: 12px; margin-left: 25px;">
										<thead>
											<tr>
												<th>id</th>
												<th>Program Structure</th>
											</tr>
										</thead>
										<tbody>

											<c:forEach var="programStructureBean"
												items="${programStructureListBean}">
												<tr
													value="${programStructureBean.id}~${programStructureBean.program_structure}">
													<td><c:out value="${programStructureBean.id}" /></td>
													<td><c:out
															value="${programStructureBean.program_structure}" /></td>

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

	<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery-1.11.3.min.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/bootstrap.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery.tabledit.js"></script>

	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/jquery-ui.min.js"></script>
	<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
	<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js"></script>

	<script>
	$(document).ready( function () {
		let id = "";
	   
	    $(".tables").on('click','tr',function(e){
		    //e.preventDefault();	
		    var str = $(this).attr('value');
		     id = str.split('~');
		    console.log(id);
		});
	    
	    $('.tables').Tabledit({
	    	columns: {
				  identifier: [0, 'id'],                 
				  editable: [[1, 'program_structure']]
				},
				// link to server script
				// e.g. 'ajax.php'
				url: "",
				// class for form inputs
				inputClass: 'form-control input-sm',
				// // class for toolbar
				toolbarClass: 'btn-toolbar',
				// class for buttons group
				groupClass: 'btn-group btn-group-sm',
				// class for row when ajax request fails
				 dangerClass: 'warning',
				// class for row when save changes
				warningClass: 'warning',
				// class for row when is removed
				mutedClass: 'text-muted',
				// trigger to change for edit mode.
				// e.g. 'dblclick'
				eventType: 'click',
				// change the name of attribute in td element for the row identifier
				rowIdentifier: 'id',
				// activate focus on first input of a row when click in save button
				autoFocus: true,
				// hide the column that has the identifier
				hideIdentifier: false,
				// activate edit button instead of spreadsheet style
				editButton: true,
				// activate delete button
				deleteButton: false,
				// activate save button when click on edit button
				saveButton: true,
				// activate restore button to undo delete action
				restoreButton: true,
				onDraw: function() { 
					$('.tables').DataTable(); 
				},
				onAjax: function(action, serialize) {
					setTimeout(() => {
						serialize['id'] = id[0];
						//serialize['program_structure'] = id[1];
						let body = JSON.stringify(serialize);
						$.ajax({
							type : "POST",
							url : 'updateProgramStructure',
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
					

				}
			});
		});
	</script>
</body>
</html>