<!DOCTYPE html>
<%@page import="java.util.*"%>
<%@page import="java.text.DateFormat"%>
<html lang="en">
<%@page import="java.util.ArrayList"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@page import="com.nmims.beans.ProgramExamBean"%>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<style>
.panel-title .glyphicon {
	font-size: 14px;
}

.column {
	margin-bottom: 20px;
}
</style>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery-1.11.3.min.js"></script>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.MDMSubjectCodeBean"%>

<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Program Entry" name="title" />
</jsp:include>
<body>
	<%@ include file="../adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;Program Entries" name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>
				<div class="sz-content-wrapper examsPage">
					<%@ include file="../adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">
						<h2 class="red text-capitalize">Add Program Entries</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%@ include file="../adminCommon/messages.jsp"%>

							<form:form method="post" action="insertProgramFormMasterEntry"
								modelAttribute="programsForm">
								<fieldset>

									<div class="panel-body">
										<div class="column">

											<div class="col-sm-3 column">
												<label>Program Abbrevation</label>
												<form:input id="program" path="code" type="text"
													placeholder="Program" class="form-control"
													  required="required" />
											</div>

											<div class="col-sm-3 column">
												<label>Program Name</label>
												<form:input id="programName" path="name" type="text"
													placeholder="Program Name" class="form-control"
													   required="required"/>
											</div>
											
											
											
											<div class="col-sm-3 column">
												<label for="specializationName">Specialization Type</label>
												<form:select id="specializationName"
													path="Specialization" type="text"
													placeholder="Select Specialization Name"
													required="required" class="form-control"
													value="${mdmSubjectCodeBean.id}">
													<form:option value="">Select Specialization Name</form:option>
													<form:options items="${specializationNameList}" />
												</form:select>
											</div>
											<div class="col-sm-3 column">
												<label for="modeOfLearning">Mode Of Learning</label>
												<form:select id="modeOfLearning"
													path="modeOfLearning" type="text"
													placeholder="Select Mode Of Learning "
													required="required" class="form-control">
													<form:option value="">Select Mode Of Learning</form:option>
													<form:option value="Online" />
													<form:option value="Online Distance Learning(ODL)" />
												</form:select>
											</div>
											


											<div class="clearfix"></div>


											<div class="col-md-6 column">

												<button id="submitEntry" name="submit"
													class="btn btn-large btn-primary"
													formaction="insertProgramFormMasterEntry">Add Program Entry</button>

												<button id="cancel" name="cancel" class="btn btn-danger"
													formaction="${pageContext.request.contextPath}/home" formnovalidate="formnovalidate">Cancel</button>
											</div>

										</div>

									</div>


								</fieldset>
							</form:form>


							<div class="panel-body">
							
								<legend>
									&nbsp;Existing Program Entries 
								</legend>
								<div class="column">
								<div class="table-responsive" >
									<table class="table table-striped table-hover tables"
										style="font-size: 12px">
										<thead>
											<tr>
												<th>Sr. No.</th>
												<th>Program</th>
												<th>Program Name</th>
												<th>Specialization Type</th>
												<th>Mode Of Learning</th>
												<th>Action</th>

											</tr>
										</thead>
										<tbody>

											<c:forEach var="programsList" items="${programsList}"
												varStatus="status">
												<tr
													value="${programsList.id}~${programsList.code}~${programsList.name}~${programsList.specialization}~${programsList.modeOfLearning}">

													<td><c:out value="${status.count}" /></td>
													<td><c:out value="${programsList.code}" /></td>
													<td><c:out value="${programsList.name}" /></td>
													<td><c:out value="${programsList.specializationType}"/></td>
													<td><c:out value="${programsList.modeOfLearning}"/></td>
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
			</div>
		</div>
	</div>
	<jsp:include page="../adminCommon/footer.jsp" />


</body>
<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->

<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/bootstrap.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery.tabledit.js"></script>

<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/jquery-ui.min.js"></script>
<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js"></script>



<script>
var ddlArray= new Array();
 var ddl = document.getElementById('specializationName');
 var ddlJSON = "{";
for (i = 0; i < ddl.options.length; i++) {
   /* ddlArray[i] = ddl.options[i].value; */
   if(ddl.options[i].value) {
   if(i == ddl.options.length -1){
	   ddlJSON = ddlJSON + "\"" + ddl.options[i].value + "\":\"" +ddl.options[i].value + "\"";
}else {
	ddlJSON = ddlJSON + "\"" + ddl.options[i].value + "\":\"" +ddl.options[i].value + "\",";
} 
   }
}
var ddlJSON = ddlJSON + " } ";
console.log(ddlJSON);

 
/*  <c:forEach var="val" items="${}">
 ddlArray.push("${specializationNameList}");
 </c:forEach> */
 	var id = "";
	$(".tables").on('click', 'tr', function(e) {
		e.preventDefault();
		var str = $(this).attr('value');
		id = str.split('~');
		console.log(id)

	});

	/* */
	$('.tables')
			.Tabledit(
					{
						columns : {
							identifier : [ 0, 'id' ],
							editable : [
									 [ 1, 'code' ],
									 [ 2, 'name' ],
									 [ 3,'specializationType',ddlJSON],
									 [ 4,'modeOfLearning','{"Online": "Online", "Online Distance Learning(ODL)": "Online Distance Learning(ODL)"}']
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
							setTimeout(()=>{
							serialize['id'] = id[0];
							body = JSON.stringify(serialize)
						
							var url = ''
								if(action === 'delete'){
									url = 'deleteProgramFormMasterEntry'
									alert("Are you sure you want to delete the entry?");
								}else if (action === 'edit'){
									url = 'updateProgramFormMasterEntry'
								}
										

							$.ajax({
								type : "POST",
								url : url,
								contentType : "application/json",
								data : body,
								dataType : "json",
								success : function(response) {
									alert(response.message);
									if(action == 'delete' && response.Status == 'Success'){
										location.reload();
									}
								}
							});
							}, 100);
						}

					});
</script>
</html>