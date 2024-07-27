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



<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Program Details Entry" name="title" />
</jsp:include>
<body>
	<%@ include file="../adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;Program Details Entries" name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>
				<div class="sz-content-wrapper examsPage">
					<%@ include file="../adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">
						<h2 class="red text-capitalize">Add Program Details Entries</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%@ include file="../adminCommon/messages.jsp"%>

							<form:form method="post" action="programDetails"
								modelAttribute="programsForm">
								<fieldset>

									<div class="panel-body">
										<div class="column">
											
											<div class="col-sm-3 column">
										<label>Consumer Type </label>
												<form:select data-id="consumerTypeDataId" required="required" value="${ programsForm.consumerType }" id="consumerTypeId" name="consumerTypeId"   path = "consumerType" class="selectConsumerType form-control" >
													<option disabled selected value="">Select Consumer Type</option>
													<c:forEach var="consumerType" items="${consumerType}">
										                
										                <c:choose>
															<c:when test="${consumerType.id == programsForm.consumerType}">
																<option selected value="<c:out value="${consumerType.id}"/>">
												                  <c:out value="${consumerType.name}"/>
												                </option>
															</c:when>
															<c:otherwise>
																<option value="<c:out value="${consumerType.id}"/>">
												                  <c:out value="${consumerType.name}"/>
												                </option>
															</c:otherwise>
														</c:choose>
														
										            </c:forEach>
												</form:select>
										</div>
										<div class="col-sm-3 column">
										<label>Program Structure </label>
												<form:select id="programStructureId" required="required" name="programStructureId"  path = "programStructure" class="selectProgramStructure form-control" >
													<option disabled selected value="">Select Program Structure</option>
												</form:select>
										</div>
										<div class="col-sm-3 column">
												<label>Program</label>
												<form:select id="programId" required="required" name="programId" path = "program" class="selectProgram form-control" >
													<option disabled selected value="">Select Program</option>
												</form:select>
										</div>


											<div class="col-sm-3 column">
												<label>Program Code</label>
												<form:input id="programCode" path="programcode"
													type="number" placeholder="0" class="form-control"
													value="${programsForm.programcode}" />
											</div>

											<div class="col-sm-3 column">

												<label>Program Duration</label>

												<form:input id="programDuration" list="programDurationList"
													path="programDuration" type="text"
													placeholder="Program Duration" class="form-control"
													 value="${programsForm.programDuration}" />
												<datalist id="programDurationList">
													<option value="one">
													<option value="two">
													<option value="three">
													<option value="six">
													<option value="nine">
													<option value="eleven">
													
												</datalist>

											</div>



											<div class="col-sm-3 column">

												<label>Program Duration Unit</label>

													<form:select id="programDurationUnit"
													path="programDurationUnit" type="text" 
													placeholder="Program Duration" class="form-control"
													itemValue="${programsForm.programDurationUnit}">

													<form:option value="">Program Duration</form:option>
													<form:option value="months">months</form:option>
													<form:option value="years">years</form:option>
													<form:option value="year">year</form:option>
													<form:option value="hours">hours</form:option>
													<form:option value="minutes">minutes</form:option>

												</form:select>

											</div>

											<div class="col-sm-3 column">
												<label>Program Type</label>
												<form:select id="programType" path="programType" type="text"
													 placeholder="Select Program Type"
													class="form-control" value="${programsForm.programType}">
													<form:option value="">Select Program Type</form:option>
													<form:options items="${programTypeList}"/>
													
												</form:select>

											</div>


											<div class="col-sm-3 column">
												<label>No. Of Subjects To Clear</label>
												<form:input id="noOfSubjectsToClear"
													path="noOfSubjectsToClear" type="number" placeholder="0"
													class="form-control"
													value="${programsForm.noOfSubjectsToClear}" />
											</div>


											<div class="col-sm-3 column">
												<label>No. Of Subjects To Clear Lateral</label>
												<form:input id="noOfSubjectsToClearLateral"
													path="noOfSubjectsToClearLateral" type="number"
													placeholder="0" class="form-control"
													value="${programsForm.noOfSubjectsToClearLateral}" />
											</div>

											
											<div class="col-sm-3 column">
												<label>Exam Duration In Minutes</label>
												<form:input id="examDurationInMinutes"
													path="examDurationInMinutes" type="number" placeholder="0"
													class="form-control"
													value="${programsForm.examDurationInMinutes}" />
											</div>

											<div class="col-sm-3 column">
												<label>No. Of Semesters</label>
												<form:input id="noOfSemesters" path="noOfSemesters"
													type="number" placeholder="0" class="form-control"
													 value="${programsForm.noOfSemesters}" />
											</div>

											<div class="col-sm-3 column">
												<label>No. Of Subjects To Clear Semester</label>
												<form:input id="noOfSubjectsToClearSem" path="noOfSubjectsToClearSem"
													type="number" placeholder="0" class="form-control"
													
													value="${programsForm.noOfSubjectsToClearSem}" />
											</div>

											<div class="col-sm-3 column">
												<label>Active Program</label>
												<form:select id="active"
													path="active" type="text" 
													placeholder="Active Program" class="form-control"
													itemValue="${programsForm.active}" required="required">

													<form:option value="">Active Program</form:option>
													<form:option value="Y">Yes</form:option>
													<form:option value="N">No</form:option>

												</form:select>
												
											</div>
											
											<div class="col-sm-3 column">
												<label>Description</label>
												<form:input id="description" path="description"
													type="text" placeholder="Enter your description" class="form-control"
													
													value="${programsForm.description}" />
												
											</div>



											<div class="clearfix"></div>


											<div class="col-md-6 column">

												<button id="submitEntry" name="submit"
													class="btn btn-large btn-primary"
													formaction="programDetails">Add Program Details Entry</button>

												<button id="cancel" name="cancel" class="btn btn-danger"
													formaction="${pageContext.request.contextPath}/home" formnovalidate="formnovalidate">Cancel</button>
											</div>

										</div>

									</div>


								</fieldset>
							</form:form>


							<div class="clearfix"></div>
							<div class="column">
								<div class="table-responsive">
									<table class="table table-striped table-hover tables"
										style="font-size: 12px">
										<thead>
											<tr>
												<th>Sr. No.</th>
												<th>Program</th>
												<th>Program Name</th>
												<th>Program Code</th>
												<th>Program Duration</th>
												<th>Program Duration Unit</th>
												<th>Program Type</th>
												<th>No. Of Subjects To Clear Program</th>
												<th>No. Of Subjects To Clear Program Lateral</th>
												<th>Program Structure</th>
												<th>Exam Duration In Minutes</th>
												<th>No. Of Semesters</th>
												<th>No. Of Subjects To Clear Sem</th>
												<th>Master Key</th>
												<th>Active</th>
												<th>Description</th>
												<th>Action</th>

											</tr>
										</thead>
										<tbody>

											<c:forEach var="programsList" items="${programsList}"
												varStatus="status">
												<tr
													value="${programsList.id}~${programsList.program}~${programsList.programname}~${programsList.programStructure}~${programsList.consumerProgramStructureId}">


													<td><c:out value="${status.count}" /></td>
													<td><c:out value="${programsList.program}" /></td>
													<td><c:out value="${programsList.programname}" /></td>
													<td><c:out value="${programsList.programcode}" /></td>
													<td><c:out value="${programsList.programDuration}" /></td>
													<td><c:out value="${programsList.programDurationUnit}" /></td>
													<td><c:out value="${programsList.programType}" /></td>
													<td><c:out value="${programsList.noOfSubjectsToClear}" /></td>
													<td><c:out value="${programsList.noOfSubjectsToClearLateral}" /></td>
													<td><c:out value="${programsList.programStructure}" /></td>
													<td><c:out value="${programsList.examDurationInMinutes}" /></td>
													<td><c:out value="${programsList.noOfSemesters}" /></td>
													<td><c:out value="${programsList.noOfSubjectsToClearSem}" /></td>
													<td><c:out value="${programsList.consumerProgramStructureId}" /></td>
													<td><c:out value="${programsList.active}" /></td>
													<td><c:out value="${programsList.description}" /></td>
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
	var id = "";
	$(".tables").on('click', 'tr', function(e) {
		 e.preventDefault();	
		var str = $(this).attr('value');
		id = str.split('~');
		console.log(id)

	});
	
	$('.tables')
			.Tabledit(
					{
						columns : {
							identifier : [ 0, 'id' ],
							editable : [
									[ 3, 'programcode' ],
									[ 4, 
										'programDuration', 
										'{"":"Select Duration","one":"one","two":"two","three":"three","four":"four","six":"six","nine":"nine","eleven":"eleven","twelve":"twelve","thirteen":"thirteen","fourteen":"fourteen","fifteen":"fifteen"}' ],
									[ 5, 
										'programDurationUnit', 
										'{"":"Select DurationUnit","months":"months","year":"year","years":"years","hours":"hours","minutes":"minutes"}' ],
									[ 6,
										'programType',
										'{"":"Select Type","Certificate":"Certificate", "Executive Programs":"Executive Programs", "Diploma":"Diploma", "Post Graduate Diploma":"Post Graduate Diploma", "Professional Program":"Professional Program", "Master":"Master", "Modular Program":"Modular Program","Bachelor Programs":"Bachelor Programs","Professional Diploma":"Professional Diploma"}' ],
									[ 7,
										'noOfSubjectsToClear',
										'{"0":"0","1":"1","2":"2","3":"3","4":"4","5":"5","6":"6","7":"7","8":"8","9":"9","10":"10","11":"11","12":"12","13":"13","14":"14","15":"15","16":"16","17":"17","18":"18","19":"19","20":"20","21":"21","22":"22","23":"23","24":"24","25":"25","26":"26","27":"27","28":"28","29":"29","30":"30","31":"31","32":"32","33":"33","34":"34","35":"35","36":"36","37":"37","38":"38","39":"39","40":"40"}' 
										],
									[ 8, 
										'noOfSubjectsToClearLateral',
										'{"0":"0","1":"1","2":"2","3":"3","4":"4","5":"5","6":"6","7":"7","8":"8","9":"9","10":"10","11":"11","12":"12","13":"13","14":"14","15":"15","16":"16","17":"17","18":"18","19":"19","20":"20","21":"21","22":"22","23":"23","24":"24","25":"25","26":"26","27":"27","28":"28","29":"29","30":"30","31":"31","32":"32","33":"33","34":"34","35":"35","36":"36","37":"37","38":"38","39":"39","40":"40"}' 
										],
									[ 10, 'examDurationInMinutes' ],
									[ 11, 
										'noOfSemesters',
										'{"0":"0","1":"1","2":"2","3":"3","4":"4","5":"5","6":"6","7":"7","8":"8","9":"9","10":"10","11":"11","12":"12","13":"13","14":"14","15":"15","16":"16","17":"17","18":"18","19":"19","20":"20","21":"21","22":"22","23":"23","24":"24","25":"25","26":"26","27":"27","28":"28","29":"29","30":"30","31":"31","32":"32","33":"33","34":"34","35":"35","36":"36","37":"37","38":"38","39":"39","40":"40"}' 
										],
									[ 12, 
										'noOfSubjectsToClearSem',
										'{"0":"0","1":"1","2":"2","3":"3","4":"4","5":"5","6":"6","7":"7","8":"8","9":"9","10":"10","11":"11","12":"12","13":"13","14":"14","15":"15","16":"16","17":"17","18":"18","19":"19","20":"20","21":"21","22":"22","23":"23","24":"24","25":"25","26":"26","27":"27","28":"28","29":"29","30":"30","31":"31","32":"32","33":"33","34":"34","35":"35","36":"36","37":"37","38":"38","39":"39","40":"40"}' 
										],
									[       
										   14, 
										   'active',
										   '{"":"Select program status","Y":"Y", "N":"N"}' ],
										   [
												15,
												'description'
											]
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
							serialize['program'] = id[1];
							serialize['programname'] = id[2];
							serialize['programStructure'] = id[3];
							serialize['consumerProgramStructureId'] = id[4];
							serialize['id'] = id[0];
							body = JSON.stringify(serialize);
							
							var url = ''
								if(action === 'delete'){
									url = 'deleteProgramsEntry'
									alert("Are you sure you want to delete the entry?");
								}else if (action === 'edit'){
									url = 'updateProgramEntry'
								}
									

							$.ajax({
								type : "POST",
								url : url,
								contentType : "application/json",
								data : body,
								dataType : "json",
								success : function(response) {
									//console.log(response)
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

<script>
		 var consumerTypeId = '${ programsForm.consumerType }';
		 var programStructureId = '${ programsForm.programStructure }';
		 var programId = '${ programsForm.program }';
		// var g_subject = '${ programsForm.subject }';
		</script>
	<script>
		console.log("==========> consumerTypeId 1: " + consumerTypeId);	
	</script>
	<%@ include file="../../views/common/consumerProgramStructure.jsp" %>
	<script>
		console.log("==========> consumerTypeId 2: " + consumerTypeId);	
	</script>
</html>