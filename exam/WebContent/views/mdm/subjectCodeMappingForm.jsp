<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@page import="java.util.*"%>
<%@page import="com.nmims.beans.MDMSubjectCodeMappingBean"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!DOCTYPE html>
<html lang="en">
<head>
<style>
.panel-title .glyphicon {
	font-size: 14px;
}

.column {
	margin-bottom: 20px;
}
</style>
<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="MDMSubjectCode Mapping" name="title" />
</jsp:include>
<body>
	<%@ include file="../adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;MDMSubjectCode Mapping" name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>
				<div class="sz-content-wrapper examsPage">
					<%@ include file="../adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">
						<h2 class="red text-capitalize">SubjectCode Mapping</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%@ include file="../adminCommon/newmessages.jsp"%>
							<%
							Map<String,String> yesNoNAMap = (LinkedHashMap<String,String>) request.getAttribute("yesNoNAMap");
							Map<String,String> bestLatestMap = (LinkedHashMap<String,String>) request.getAttribute("bestLatestMap");
							Map<String,String> bestLatestNAMap = (LinkedHashMap<String,String>) request.getAttribute("bestLatestNAMap");
							ArrayList<String> semesterList = (ArrayList<String>) request.getAttribute("semesterList");
							ArrayList<MDMSubjectCodeMappingBean> subjectCodeMappingList = (ArrayList<MDMSubjectCodeMappingBean>) request
									.getAttribute("subjectCodeMappingList");
							%>
							<form:form modelAttribute="mdmSubjectCodeMappingBean">
								<fieldset>

									<div class="panel-body">
										<div class="column">
											<div class="col-sm-3 column">
												<label for="consumerType">Consumer Type</label>
												<form:select data-id="consumerTypeDataId" id="consumerType"
													path="consumerType" name="consumerType"
													class="form-control" required="required">
													<option disabled selected value="">Select Consumer Type</option>
													<form:options items="${consumerTypeMap}" />
												</form:select>
											</div>

											<div class="col-sm-3 column">
												<label>Program Structure</label>
												<form:select id="prgmStructApplicable"
													path="prgmStructApplicable" type="text" required="required"
													placeholder="Select Program Structure"
													data-id="programStructureDataId" class="form-control"
													itemValue="${mdmSubjectCodeMappingBean.prgmStructApplicable}">
													<option disabled selected value="">Select Program
														Structure</option>
												</form:select>
											</div>

											<div class="col-sm-3 column">
												<label>Program Name</label>
												<form:select data-id="programDataId" id="program"
													path="program" type="text" required="required"
													placeholder="Select Program Name" class="form-control"
													itemValue="${mdmSubjectCodeMappingBean.program}">
													<option disabled selected value="">Select Program
														Name</option>
												</form:select>
											</div>

											<div class="col-sm-3 column">
												<label>Semester</label>
												<form:select id="sem" path="sem" type="text"
													required="required" placeholder="Select Semester"
													class="form-control"
													itemValue="${mdmSubjectCodeMappingBean.sem}">
													<form:option value="">Select Semester</form:option>
													<form:options items="${semesterList}" />
												</form:select>
											</div>

											<div class="col-sm-3 column">
												<label>SubjectCode</label>
												<form:select id="subjectCodeId" path="subjectCodeId" type="text"
													required="required" placeholder="Select SubjectCode"
													class="form-control"
													itemValue="${mdmSubjectCodeMappingBean.subjectCodeId}">
													<form:option value="">Select SubjectCode</form:option>
													<form:options items="${subjCodeMap}" />
												</form:select>
											</div>

											<div class="col-sm-3 column">
												<label>Active Status</label>
												<form:select id="active" path="active" type="text"
													required="required" placeholder="Active"
													class="form-control"
													itemValue="${mdmSubjectCodeMappingBean.active}">
													<form:option value="">Select Active</form:option>
													<form:options items="${yesNoMap}" />
												</form:select>
											</div>
											
											
											<div class="col-sm-3 column">
												<label>Sify Subject Code</label>
												<form:input id="sifySubjectCode" path="sifySubjectCode"
													type="number" placeholder="Sify Subject Code"
													class="form-control" value="${mdmSubjectCodeMappingBean.sifySubjectCode}" />
											</div>
											<div class="col-sm-3 column">
												<label>Program Pass Score</label>
												<form:input id="passScore" path="passScore" type="number"
													required="required" placeholder="Pass Score" class="form-control"
													value="${mdmSubjectCodeMappingBean.passScore}" />
											</div>
											<div class="col-sm-3 column">
												<label>Has TEE?</label>
												<form:select id="hasTEE" path="hasTEE" type="text" 
													placeholder="Select TEE Applicable"
													class="form-control"
													itemValue="${mdmSubjectCodeMappingBean.hasTEE}">
													<form:option value="">Select TEE Applicable</form:option>
													<form:options items="${yesNoMap}" />
												</form:select>
											</div>
											<div class="col-sm-3 column">
												<label>Has IA?</label>
												<form:select id="hasIA" path="hasIA" type="text" 
													placeholder="Select IA Applicable"
													class="form-control"
													itemValue="${mdmSubjectCodeMappingBean.hasIA}">
													<form:option value="">Select IA Applicable</form:option>
													<form:options items="${yesNoMap}" />
												</form:select>
											</div>
											<div class="col-sm-3 column">
												<label>Has Test?</label>
												<form:select id="hasTest"
													path="hasTest" type="text" 
													placeholder="Test Applicable" class="form-control"
													itemValue="${mdmSubjectCodeMappingBean.hasTest}">
													<form:option value="">Select Test Applicable</form:option>
													<form:options items="${yesNoMap}" />
												</form:select>
											</div>
											<div class="col-sm-3 column">
											<label>Has Assignment?</label>
												<form:select id="hasAssignment" path="hasAssignment" type="text" 
													placeholder="Select Assignment Applicable"
													class="form-control"
													itemValue="${mdmSubjectCodeMappingBean.hasAssignment}">
													<form:option value="">Select Assignment Applicable</form:option>
													<form:options items="${yesNoMap}" />
												</form:select>
											</div>
											<div class="col-sm-3 column">
												<label>Is Assignment Needed Before Written?</label>
												<form:select id="assignmentNeededBeforeWritten"
													path="assignmentNeededBeforeWritten" type="text"
													 placeholder="Active"	class="form-control"
													itemValue="${mdmSubjectCodeMappingBean.assignmentNeededBeforeWritten}">
													<form:option value="">Select Status</form:option>
													<form:options items="${yesNoNAMap}" />
												</form:select>
											</div>
											<div class="col-sm-3 column">
												<label>Assignment Score Model</label>
												<form:select id="assignmentScoreModel"
													path="assignmentScoreModel" type="text" 
													placeholder="Active" class="form-control"
													itemValue="${mdmSubjectCodeMappingBean.assignmentScoreModel}">
													<form:option value="">Select Assignment Score Model</form:option>
													<form:options items="${bestLatestNAMap}" />
												</form:select>
											</div>
											<div class="col-sm-3 column">
												<label>Written Score Model</label>
												<form:select id="writtenScoreModel" path="writtenScoreModel"
													type="text"	placeholder="Active"	class="form-control"
													itemValue="${mdmSubjectCodeMappingBean.writtenScoreModel}">
													<form:option value="">Select Written Score Model</form:option>
													<form:options items="${bestLatestMap}" />
												</form:select>
											</div>
											<div class="col-sm-3 column">
												<label>Create Case For Query?</label>
												<form:select id="createCaseForQuery"
													path="createCaseForQuery" type="text" 
													placeholder="Grace Applicable" class="form-control"
													itemValue="${mdmSubjectCodeMappingBean.createCaseForQuery}">
													<form:option value="">Case For Query</form:option>
													<form:options items="${yesNoMap}" />
												</form:select>
											</div>
											<div class="col-sm-3 column">
												<label>Assign Query To Faculty?</label>
												<form:select id="assignQueryToFaculty"
													path="assignQueryToFaculty" type="text" 
													placeholder="Grace Applicable" class="form-control"
													itemValue="${mdmSubjectCodeMappingBean.assignQueryToFaculty}">
													<form:option value="">Assign Query To Faculty</form:option>
													<form:options items="${yesNoMap}" />
												</form:select>
											</div>
											<div class="col-sm-3 column">
												<label>Has Grace?</label>
												<form:select id="isGraceApplicable" path="isGraceApplicable"
													type="text" placeholder="Grace Applicable" class="form-control"
													itemValue="${mdmSubjectCodeMappingBean.isGraceApplicable}">
													<form:option value="">Select Grace Applicable</form:option>
													<form:options items="${yesNoMap}" />
												</form:select>
											</div>
											<div class="col-sm-3 column">
												<label>Maximum Grace Marks</label>
												<form:input id="maxGraceMarks" path="maxGraceMarks"
													required="required" type="number" placeholder="Max Grace Marks"
													class="form-control" value="${mdmSubjectCodeMappingBean.maxGraceMarks}" />
											</div>
											
											<div class="col-sm-3 column">
												<label>Subject Credits</label>
												<form:select id="credit" path="subjectCredits"
													required="required" placeholder="Select Subject Credits"
													class="form-control"
													itemValue="${mdmSubjectCodeMappingBean.subjectCredits}">
													<form:option value="">Select Subject Credits</form:option>
													<form:options items="${subject_credits}" />
												</form:select>
											</div>
											
											<div class="clearfix"></div>

											<div class="col-md-6 column">
												<button id="submit" name="submit" class="btn btn-large btn-primary"
													formaction="saveMDMSubjectCodeMappingForm">Add Entry</button>
												<button id="cancel" name="cancel" class="btn btn-danger"
													formaction="${pageContext.request.contextPath}/home" formnovalidate="formnovalidate">Cancel</button>
											</div>
										</div>
									</div>
								</fieldset>
							</form:form>
<%--<form:form id="uploadForm" modelAttribute="mdmSubjectCodeMappingBean2" method="post" enctype="multipart/form-data" action="uploadMDMSubjectCodeMapping">
<fieldset>
<div class="panel-body">
<div class="col-sm-3 column">
<div class="form-group">
	<form:label for="fileData" path="fileData">Select file</form:label>
	<form:input path="fileData" type="file" />
</div>
</div>
<div class="col-sm-3 column">
	<button id="submit" name="submit" class="btn btn-large btn-primary">Upload</button>
</div>
<div class="col-md-6 column">
<b>&nbsp;</b><a href="${pageContext.request.contextPath}/resources_2015/templates/UPLOAD_MDMSUBJECTCODE_MAPPING_TEMPLATE.xlsx" target="_blank">Download Sample Template</a>
</div>
</div>
</fieldset>
</form:form> --%><!-- Commented since row also created in program_subject and program_sem_subject. Would be used later. -->
<!--
<fieldset>
<div class="panel-body">
<div class="col-md-6 column">
<a href="${pageContext.request.contextPath}/resources_2015/templates/UPLOAD_MDMSUBJECTCODE_MAPPING_TEMPLATE.xlsx" target="_blank">Download Sample Template</a>
</div>
</div>
</fieldset>
--><!-- Commented since upload is not available. -->
<div class="clearfix"></div>
<div class="column">
	<legend>
		&nbsp;Existing MDMSubjectCode Mapping(s) <font size="2px">
			<a href="downloadMDMSubjectCodeMapping" style="color: #aa1f24"><b>Download
					MDMSubjectCode Mapping(s) to Verify</b></a>
		</font>
	</legend>
	<div class="table-responsive">
		<table class="table table-striped table-hover tables"
			style="font-size: 12px">
			<thead>
				<tr>
					<!-- <th>Program Id</th> -->
					<th>Sr. No.</th>
					<th>Consumer Type</th>
					<th>Program Structure</th>
					<th>Program Code</th>
					<th>Program Name</th>
					<th>SubjectName</th>
					<th>SubjectCode</th>
					<th>Subject Credits</th>
					<th>Semester</th>
					<th>Active</th>
					<th>Sify Subject Code</th>
					<th>Program Pass Score</th>
					<th>Has IA</th>
					<th>Has Test</th>
					<th>Has Assignment</th>
					<th>Has TEE</th>
					<th>Assignment Needed Before Written</th>
					<th>Assignment Score Model</th>
					<th>Written Score Model</th>
					<th>Create Case For Query?</th>
					<th>Assign Query To Faculty?</th>
					<th>Has Grace?</th>
					<th>Max Grace Marks</th>
					<th>Actions</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="subjCodeMapp"
					items="${subjectCodeMappingList}">
					<tr>
						<!-- <td><c:out value="${subjCodeMapp.sem}~${subjCodeMapp.subjectCodeId}~${subjCodeMapp.consumerProgramStructureId}" /></td> -->
						<td><c:out value="${subjCodeMapp.id}" /></td><!-- 1  -->
						<td><c:out value="${subjCodeMapp.consumerType}" /></td>
						<td><c:out
								value="${subjCodeMapp.prgmStructApplicable}" /></td>
						<td><c:out value="${subjCodeMapp.program}" /></td><!-- 4 -->
						<td><c:out value="${subjCodeMapp.programFullName}" /></td><!-- 5 -->
						<td><c:out value="${subjCodeMapp.subjectName}" /></td>
						<td><c:out value="${subjCodeMapp.subjectCode}" /></td>
						<td><c:out value="${subjCodeMapp.subjectCredits}" /></td>
						<td><c:out
								value="${subjCodeMapp.sem}" /></td><!-- 7  -->
						<td><c:out value="${subjCodeMapp.active}" /></td>
						<td><c:out value="${subjCodeMapp.sifySubjectCode}" /></td>
						<td><c:out value="${subjCodeMapp.passScore}" /></td>
						<td><c:out value="${subjCodeMapp.hasIA}" /></td><!-- 11  -->
						<td><c:out value="${subjCodeMapp.hasTest}" /></td>
						<td><c:out value="${subjCodeMapp.hasAssignment}" /></td>
						<td><c:out value="${subjCodeMapp.hasTEE}" /></td>
						<td><c:out
								value="${subjCodeMapp.assignmentNeededBeforeWritten}" /></td><!-- 15 -->
						<td><c:out
								value="${subjCodeMapp.assignmentScoreModel}" /></td>
						<td><c:out
								value="${subjCodeMapp.writtenScoreModel}" /></td><!-- 17 -->
						<td><c:out
								value="${subjCodeMapp.createCaseForQuery}" /></td>
						<td><c:out
								value="${subjCodeMapp.assignQueryToFaculty}" /></td>
						<td><c:out
								value="${subjCodeMapp.isGraceApplicable}" /></td><!-- 20 -->
						<td><c:out value="${subjCodeMapp.maxGraceMarks}" /></td>
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
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery-1.11.3.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/bootstrap.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery.tabledit.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/jquery-ui.min.js"></script>
<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js"></script>
<link rel="stylesheet" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/css/jquery-ui.min.css"><!--  added for the modal dialog. -->
<script>
/*var id;
$(".tables").on('click','tr',function(e){
	e.preventDefault();	
	var str = $(this).attr('value');
	id = str; //str.split('~');
	console.log("---------->>>>>>>>>> id");  
});*/

//////////////
$('.tables').Tabledit({
	columns: {
		identifier: [0, 'id'],                 
		editable: [	
			[7, 'subjectCredits', '{"":"Select Subject Credits","0.0":"0.0","0.5":"0.5","1.0":"1.0","1.5":"1.5","2.0":"2.0","2.5":"2.5","3.0":"3.0","3.5":"3.5","4.0":"4.0","4.5":"4.5","5.0":"5.0","5.5":"5.5","6.0":"6.0","6.5":"6.5","7.0":"7.0","7.5":"7.5","8.0":"8.0","8.5":"8.5","9.0":"9.0","9.5":"9.5","10.0":"10.0","10.5":"10.5","11.0":"11.0","11.5":"11.5","12.0":"12.0","12.5":"12.5","13.0":"13.0","13.5":"13.5","14.0":"14.0","14.5":"14.5","15.0":"15.0","15.5":"15.5","16.0":"16.0","16.5":"16.5","17.0":"17.0","17.5":"17.5","18.0":"18.0","18.5":"18.5","19.0":"19.0","19.5":"19.5","20.0":"20.0","20.5":"20.5","21.0":"21.0","21.5":"21.5","22.0":"22.0","22.5":"22.5","23.0":"23.0","23.5":"23.5","24.0":"24.0","24.5":"24.5","25.0":"25.0"}'],				
			[9, 'active', '{"":"Select Status","Y": "Y", "N": "N"}'],
			[10, 'sifySubjectCode'],
			[11, 'passScore'],
			[12, 'hasIA', '{"":"Select Status","Y": "Y", "N": "N"}'],
			[13, 'hasTest', '{"":"Select Status","Y": "Y", "N": "N"}'],
			[14, 'hasAssignment', '{"":"Select Status","Y": "Y", "N": "N"}'],
			[15, 'hasTEE', '{"":"Select Status","Y": "Y", "N": "N"}'],
			[16, 'assignmentNeededBeforeWritten', '{"":"Select Status","Y": "Y", "N": "N","NA":"NA"}'],
			[17, 'assignmentScoreModel', '{"":"Select Status","Best": "Best", "Latest": "Latest","NA":"NA"}'],
			[18, 'writtenScoreModel', '{"":"Select Status","Best": "Best", "Latest": "Latest"}'],
			[19, 'createCaseForQuery', '{"":"Select Status","Y": "Y", "N": "N"}'] ,
			[20, 'assignQueryToFaculty', '{"":"Select Status","Y": "Y", "N": "N"}'] ,
			[21, 'isGraceApplicable','{"":"Select Status","Y": "Y", "N": "N"}'],  
			[22, 'maxGraceMarks']]
	},
	
// link to server script
// e.g. 'ajax.php'
url: "updateOrDeleteMDMSubjectCodeMapping",//NOTE: URL must not be empty, URL of save or update must be here.
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
deleteButton: true,
// activate save button when click on edit button
saveButton: true,
// activate restore button to undo delete action
restoreButton: false,
// custom action buttons
// executed after draw the structure
onDraw: function() { 
	$('.tables').DataTable( {
       initComplete: function () {
           this.api().columns().every( function () {
        	   var column = this;
               var headerText = $(column.header()).text();
               //console.log("header :"+headerText);
               
               if(headerText == "Consumer Type")
               {
                  var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
                   .appendTo( $(column.header()) )
                   .on( 'change', function () {
                       var val = $.fn.dataTable.util.escapeRegex(
                           $(this).val()
                       );
                       column
                           .search( val ? '^'+val+'$' : '', true, false )
                           .draw();
                   } );

               column.data().unique().sort().each( function ( d, j ) {
                   select.append( '<option value="'+d+'">'+d+'</option>' )
               } );
             }
			if(headerText == "Program Structure") {
			   var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
			    .appendTo( $(column.header()) )
			    .on( 'change', function () {
			        var val = $.fn.dataTable.util.escapeRegex(
			            $(this).val()
			        );
			        column
			            .search( val ? '^'+val+'$' : '', true, false )
			            .draw();
			    } );
			
			  column.data().unique().sort().each( function ( d, j ) {
			      select.append( '<option value="'+d+'">'+d+'</option>' )
			  } );
			}
			if(headerText == "Program Code") {
			     var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
			      .appendTo( $(column.header()) )
			      .on( 'change', function () {
			          var val = $.fn.dataTable.util.escapeRegex(
			              $(this).val()
			          );
			          column
			              .search( val ? '^'+val+'$' : '', true, false )
			              .draw();
			      } );
			
			  column.data().unique().sort().each( function ( d, j ) {
			      select.append( '<option value="'+d+'">'+d+'</option>' )
			  } );
			}
			if(headerText == "Program Name") {
			     var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
			      .appendTo( $(column.header()) )
			      .on( 'change', function () {
			          var val = $.fn.dataTable.util.escapeRegex(
			              $(this).val()
			          );
			          column
			              .search( val ? '^'+val+'$' : '', true, false )
			              .draw();
			      } );
			
			  column.data().unique().sort().each( function ( d, j ) {
			      select.append( '<option value="'+d+'">'+d+'</option>' )
			  } );
			}
			if(headerText == "SubjectName") {
			   var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
			    .appendTo( $(column.header()) )
			    .on( 'change', function () {
			        var val = $.fn.dataTable.util.escapeRegex(
			            $(this).val()
			        );
			        column
			            .search( val ? '^'+val+'$' : '', true, false )
			            .draw();
			    } );
			
			  column.data().unique().sort().each( function ( d, j ) {
			      select.append( '<option value="'+d+'">'+d+'</option>' )
			  } );
			}

			if(headerText == "SubjectCode") {
			   var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
			    .appendTo( $(column.header()) )
			    .on( 'change', function () {
			        var val = $.fn.dataTable.util.escapeRegex(
			            $(this).val()
			        );
			        column
			            .search( val ? '^'+val+'$' : '', true, false )
			            .draw();
			    } );
			
			  column.data().unique().sort().each( function ( d, j ) {
			      select.append( '<option value="'+d+'">'+d+'</option>' )
			  } );
			}
			if(headerText == "Subject Credits") {
			     var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
			      .appendTo( $(column.header()) )
			      .on( 'change', function () {
			          var val = $.fn.dataTable.util.escapeRegex(
			              $(this).val()
			          );
			          column
			              .search( val ? '^'+val+'$' : '', true, false )
			              .draw();
			      } );
			
			  column.data().unique().sort().each( function ( d, j ) {
			      select.append( '<option value="'+d+'">'+d+'</option>' )
			  } );
			}
             
           } );
       }
	
   } );
return; 
},
// executed when the ajax request is completed
// onSuccess()
onSuccess: function(data, textStatus, jqXHR) { 
	//console.log('In success ' + jqXHR.responseText);
	//console.log('In success ' + jqXHR.responseText + ' || ' + jqXHR.responseJSON.success + ' || ' + jqXHR.responseJSON.error);
	if(undefined !== jqXHR.responseJSON.success) {
		displayDialogMessageOnUI(jqXHR.responseJSON.success, 'Success!',jqXHR.responseJSON.action);
	
	} else {
		displayDialogMessageOnUI(jqXHR.responseJSON.error, 'Error',jqXHR.responseJSON.action);
	}

	return; 
},
// executed when occurred an error on ajax request
// onFail()
onFail: function(jqXHR, textStatus, errorThrown) {
	//console.log('In Fail ' + jqXHR.responseText);
	displayDialogMessageOnUI(errorThrown, 'Error',''); 
	return; 
},
// executed whenever there is an ajax request
onAlways: function() { return; },

// executed before the ajax request
onAjax: function(action, serialize) { 
	if(action == 'delete') {
		 var stat = confirm("Are you sure, you want to Delete ?"); 
		 if(stat == true) {
			return true;	
		 } else {
			return false;
		}
	} 
}

});
/////////

$(document).ready(function() {
    $('#tables').DataTable( {
        "order": [[ 1, "asc" ]]
    } );
} );

/////////On change of ProgramStructure, fetch Program with ProgramStructure & ConsumerType////
$('#prgmStructApplicable').on('change', function(){
	//let id = $(this).attr('data-id');
	let consTypeId = $('#consumerType').find('option:selected').val();
	//console.log('consumerTypeId, programStructureId >> ' + consTypeId + ',' + this.value);
	
	let options = "<option>Loading... </option>";
	$('#program').html(options);
	 
	var data = {
		consumerTypeId: consTypeId,
		programStructureId: this.value
	}
	
	$.ajax({
		type : "POST",
		contentType : "application/json",
		url : "getPrgmByPrgmStructConsumerType",   
		data : JSON.stringify(data),
		success : function(data) {
			//console.log("SUCCESS Program: ", data.programData);
			var programData = data.programData;
			
			var options1 = '';
			
			//Data Insert For Program List
			for(let i=0;i < programData.length;i++) {
				options1 = options1 + "<option value='" + programData[i].programId + "'> " + programData[i].code + " </option>";
			}
			//console.log("==========> options\n" + options1);
			
			$('#program').html(
				"<option disabled selected value=''>Select Program Name</option> " + options1
			);
		},
		error : function(e) {
			alert("Please Refresh The Page.");
			console.log("ERROR: ", e);
			display(e);
		}
	});
});
/////////

/////////On change of ConsumerType, fetch ProgramStructure////
$('#consumerType').on('change', function(){
	//let id = $(this).attr('data-id');
	//console.log('consumerTypeId >' + this.value);
	
	let options = "<option>Loading... </option>";
	$('#prgmStructApplicable').html(options);
	 
	var data = {
		consumerTypeId: this.value
	}
	
	$.ajax({
		type : "POST",
		contentType : "application/json",
		url : "getPrgmStructByConsumerType",   
		data : JSON.stringify(data),
		success : function(data) {
			//console.log("SUCCESS Program: ", data.programStructureData);
			var programStructureData = data.programStructureData;
			
			var options1 = '';
			
			//Data Insert For ProgramStructure List
			for(let i=0;i < programStructureData.length;i++) {
				options1 = options1 + "<option value='" + programStructureData[i].programStructureId + "'> " + programStructureData[i].program_structure + " </option>";
			}
			//console.log("==========> options\n" + options1);
			
			$('#prgmStructApplicable').html(
				"<option disabled selected value=''>Select Program Structure</option> " + options1
			);
			options1 = '';
		},
		error : function(e) {
			alert("Please Refresh The Page.");
			console.log("ERROR: ", e);
			display(e);
		}
	});
});
/////////

////Add Dialog to show result of Edit/Delete button click in table. i.e server response///
function displayDialogMessageOnUI(mymessage, mytitle,action) { 
	if($('#scModalDiv')) { $('#scModalDiv').remove(); }
	$('#parentmsgdiv').append("<div id='scModalDiv'></div>");
	$('#scModalDiv').html(mymessage);
	$('#scModalDiv').dialog({
		height: "auto",
		modal: true,
	    show: "slide",
		width: 400,
		title: mytitle,
		autoOpen: false,
		close: function(event,ui) { $('#scModalDiv').empty(); 
		if(action == 'delete')
			window.location =  '/exam/admin/mdmSubjectCodeMappingForm' ;
		} 
   });
	$("#scModalDiv" ).dialog("open");
}
</script>
</html>