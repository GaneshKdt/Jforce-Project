<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@page import="java.util.*"%>
<%@page import="com.nmims.beans.MDMSubjectCodeBean"%>
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
<script>
</script>
<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="MDMSubjectCode Creation" name="title" />
</jsp:include>

<body>
<%@ include file="../adminCommon/header.jsp"%>

<div class="sz-main-content-wrapper">
<jsp:include page="../adminCommon/breadcrum.jsp">
	<jsp:param value="Exam;MDMSubjectCode Creation" name="breadcrumItems" />
</jsp:include>

<div class="sz-main-content menu-closed">
	<div class="sz-main-content-inner">
		<jsp:include page="../adminCommon/left-sidebar.jsp">
			<jsp:param value="" name="activeMenu" />
		</jsp:include>
		<div class="sz-content-wrapper examsPage">
			<%@ include file="../adminCommon/adminInfoBar.jsp"%>
			<div class="sz-content">
				<h2 class="red text-capitalize">Add SubjectCode</h2>
				<div class="clearfix"></div>
				<div class="panel-content-wrapper" style="min-height: 450px;">
					<%--@ include file="../adminCommon/messages.jsp"--%>
					<%@ include file="../adminCommon/newmessages.jsp"%>

					<% 	Map<String,String> yesNoMap = (LinkedHashMap<String,String>) request.getAttribute("yesNoMap");
						Map<String,String> numberYesNoMap = (LinkedHashMap<String,String>) request.getAttribute("numberYesNoMap");
						Map<String,String> regTimeMap = (LinkedHashMap<String,String>) request.getAttribute("regTimeMap");
						ArrayList<MDMSubjectCodeBean> subjectCodeList = (ArrayList<MDMSubjectCodeBean>) request
								.getAttribute("subjectCodeList");
					%>

<form:form modelAttribute="mdmSubjectCodeBean" id="mainForm">
<fieldset>

<div class="panel-body">
<div class="column">
<div class="col-sm-3 column">
	<label>SubjectCode <span style="color: red">*</span></label>
	<form:input id="subjectcode" path="subjectcode"
		type="text" placeholder="SubjectCode"
		class="form-control" value="${mdmSubjectCodeBean.subjectcode}" />
</div>
<div class="col-sm-3 column">
	<label>Subject Name <span style="color: red">*</span></label>
	<form:input id="subjectname" path="subjectname" type="text"
		placeholder="Subject Name" class="form-control"
		value="${mdmSubjectCodeBean.subjectname}" />
</div>
<div class="col-sm-3 column">
	<label>Common Subject</label>
	<form:input id="commonSubject" path="commonSubject"
		type="text" placeholder="Common Subject"
		class="form-control" value="${mdmSubjectCodeBean.commonSubject}" />
</div>
<div class="col-sm-3 column">
	<label>Active Status</label>
	<form:select id="active" path="active" type="text"
		required="required" placeholder="Active"
		class="form-control"
		itemValue="${mdmSubjectCodeBean.active}">
		<form:option value="">Select Active</form:option>
		<form:options items="${yesNoMap}" />
	</form:select>
</div>
<div class="col-sm-3 column">
	<label>Is Project</label>
	<form:select id="isProject" path="isProject" type="text"
		required="required" placeholder="isProject"
		class="form-control"
		itemValue="${mdmSubjectCodeBean.isProject}">
		<form:option value="">Select </form:option>
		<form:options items="${numberYesNoMap}" />
	</form:select>
</div>
<div class="col-sm-3 column">
	<label>Specialization Type</label>
	<form:select id="specializationType" path="specializationType" type="text"
		placeholder="Select Specialization Name"  required="required"
		class="form-control" value="${mdmSubjectCodeBean.specializationType}">
		<form:option value="">Select Specialization Name</form:option>
		<form:options items="${specializationTypeNameList}"/>
	</form:select>
</div>
<div class="col-sm-3 column">
<label>Student Type</label>
	<form:select id="studentType" path="studentType" type="text" required="required" placeholder="Student Type"
	 class="form-control" itemValue="${mdmSubjectCodeBean.studentType}" >
		<form:option value="">Select Status</form:option>
		<form:options items="${regTimeMap}" />
	</form:select>
</div>
<div class="col-sm-3 column">
	<label>Description</label>
	<form:textarea id="description" path="description"
		type="text" placeholder="Description"
		class="form-control" value="${mdmSubjectCodeBean.description}" />
</div>
<div class="col-sm-3 column">
	<label>Session Time (Minute) <span style="color: red">*</span></label>
	<form:input id="sessionTime" path="sessionTime"
		required="required" type="number" placeholder="Session Time"
		class="form-control" value="${mdmSubjectCodeMappingBean.sessionTime}" />
</div>								
<div class="clearfix"></div>

<div class="col-md-6 column">

	<button id="submit" name="submit"
		class="btn btn-large btn-primary" formaction="saveMDMSubjectCodeForm">Add Entry</button>

	<button id="cancel" name="cancel" class="btn btn-danger"
		formaction="${pageContext.request.contextPath}/home" formnovalidate="formnovalidate">Cancel</button>
</div>

</div>

</div>

</fieldset>
</form:form>
<%--
<form:form id="uploadForm" modelAttribute="mdmSubjectCodeBean2" method="post" enctype="multipart/form-data" action="uploadMDMSubjectCode">
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
<b>&nbsp;</b><a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/templates/UPLOAD_MDMSUBJECTCODE_TEMPLATE.xlsx" target="_blank">Download Sample Template</a>
</div>
</div>
</fieldset>
</form:form>
 --%><!-- Commented since upload is not available. -->
<div class="clearfix"></div>
<div class="column">
	<legend>
		&nbsp;Existing SubjectCode(s) <font size="2px">
			<a href="downloadMDMSubjectCode" style="color: #aa1f24"><b>Download
				SubjectCode(s) to Verify</b></a>
	</font>
</legend>
<div class="table-responsive">
	<table class="table table-striped table-hover tables"
		style="font-size: 12px">
<thead>
	<tr>
		<th>Sr. No.</th>
		<th>SubjectCode</th>
		<th>Subject Name</th>
		<th>Common Subject</th>
		<th>Active Status</th>
		<th>isProject</th>
		<th>Specialization</th>
		<th>Student Type</th>
		<th>Description</th>
		<th>Session Time</th>
		<th>Actions</th>
	</tr>
</thead>
<tbody>
	<c:forEach var="subjectCodeList"
		items="${subjectCodeList}" varStatus="status">
		<tr
			value="${subjectCodeList.id}">
			<td><c:out value="${subjectCodeList.id}" /></td>
			<td><c:out value="${subjectCodeList.subjectcode}" /></td><!-- 2  -->
			<td><c:out value="${subjectCodeList.subjectname}" /></td>
			<td><c:out value="${subjectCodeList.commonSubject}" /></td>
			<td><c:out value="${subjectCodeList.active}" /></td><!-- 5  -->
			<td><c:out value="${subjectCodeList.isProject}" /></td>
			
			<td><c:out
					value="${subjectCodeList.specializationType}" /></td><!-- 8 -->
			<td><c:out value="${subjectCodeList.studentType}"/></td>
			<td><c:out value="${subjectCodeList.description}"/></td>
			<td><c:out value="${subjectCodeList.sessionTime}"/></td>
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

///Very Important.TR i.e row contains id which is primarykey.
/*var id;
$(".tables").on('click','tr',function(e) {
    e.preventDefault();	
    var str = $(this).attr('value');
    id = str; //.split('~');
    console.log("---------->>>>>>>>>> id " + id);
});*/
////

////Table displayed configured here.//
$('.tables').Tabledit({
	columns: {
		identifier: [0, 'id'],                 
		editable: [
			[1, 'subjectcode'],
			[2, 'subjectname'],
			[3, 'commonSubject'],
			[4, 'active', '{"":"Select Status","Y": "Y", "N": "N"}'],
			[5, 'isProject', '{"":"Select Status","1": "Y", "0": "N"}'],
			[6, 'specializationType','{"":"Select Specialization","Applied Finance":"Applied Finance","Leadership and Strategy":"Leadership and Strategy","Marketing":"Marketing","No Specialization":"No Specialization","Operations and Supply Chain":"Operations and Supply Chain", "Deep Learning":"Deep Learning", "Dev Ops":"Dev Ops"}'],
			[7, 'studentType', '{"":"Select Status","Regular": "Regular","TimeBound": "TimeBound"}' ],
			[8, 'description'],
			[9, 'sessionTime']
		]
	},

	// link to server script
	// e.g. 'ajax.php'
	url: "updateOrDeleteMDMSubjectCode",//NOTE: URL must not be empty, URL of save or update must be here.
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

	//custom action buttons
	//executed after draw the structure
	onDraw: function() { 
		$('.tables').DataTable( {
			initComplete: function () {
				this.api().columns().every( function () {
					/*var column = this;
					var headerText = $(column.header()).text();
					console.log("header :"+headerText);
					if(headerText == "SubjectCode") {
						var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
						.appendTo( $(column.header()) )
						.on('change', function () {
							var val = $.fn.dataTable.util.escapeRegex(
								$(this).val()
							);
							<!-- -->
							column
								.search( val ? '^'+val+'$' : '', true, false )
								.draw();
						} );

						column.data().unique().sort().each( function ( d, j ) {
							select.append( '<option value="'+d+'">'+d+'</option>' )
					} );
				  }
					if(headerText == "Subject Name") {
						var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
						.appendTo( $(column.header()) )
						.on('change', function () {
							var val = $.fn.dataTable.util.escapeRegex(
								$(this).val()
							);
							<!-- -->
							column
								.search( val ? '^'+val+'$' : '', true, false )
								.draw();
						} );

						column.data().unique().sort().each( function ( d, j ) {
							select.append( '<option value="'+d+'">'+d+'</option>' )
					} );
				  }*/
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
			displayDialogMessageOnUI(jqXHR.responseJSON.success, 'Success!');
		} else {
			displayDialogMessageOnUI(jqXHR.responseJSON.error, 'Error');
		}
		return; 
	},

	// executed when occurred an error on ajax request
	// onFail()
	onFail: function(jqXHR, textStatus, errorThrown) {
		//console.log('In Fail ' + jqXHR.responseText);
		displayDialogMessageOnUI(errorThrown, 'Error'); 
		return; 
	},
	
	// executed before the ajax request
	// onAjax(action, serialize)
	onAjax: function(action, serialize) {  
		if(action == 'delete') {
			 var stat = confirm("Are you sure, you want to Delete ?"); 
			 if(stat == true) {
				return true;	
			 } else {
				return false;
			}
		} 
	},

	// executed whenever there is an ajax request
	onAlways: function() { return; }
});
//////////////

function isSpecialCharacter(val) {
	const regex = RegExp('[^A-Za-z0-9]', 'g');
	var isSpecChar = regex.test(val);
	//console.log('RegEx match -> '+val + ' -> ' + regex.test(val) + ' -> ' + isSpecChar);
	return isSpecChar;
}

////Checking if SubjectCode is already existing in Table.//
$('#subjectcode').on('change', function() {
	//let id = $(this).attr('subjectcode');
	
	var isSpecChar = isSpecialCharacter(this.value);
	if(isSpecChar) {
		displayMessageOnUI('SubjectCode must be alphanumeric only. No Special character allowed.');
	} else {
		let options = '<option>Loading... </option>';
		var data = {
			subjectcode:this.value
		}
		//console.log("onchange : subjectcode : " + this.value);
		$.ajax({
			type : 'POST',
			contentType : 'application/json',
			url : 'checkIfDuplicateMDMSubjectCode',   
			data : JSON.stringify(data),
			success : function(data) {
				//console.log("SUCCESS Program: ", data);
				//alert(JSON.parse(data));
				if(data.status === 'error') { //'fail'
					displayMessageOnUI(data.message);
				}
				
			},
			error : function(e) {
				alert("Please Refresh The Page.");
				//console.log("ERROR: ", e);
				display(e);
			}
		});
	}
});

////Function displays message from Server on UI/////
function displayMessageOnUI(m) {
	/*if($('#msgdiv').length) {
		$('#msgdiv').attr('class', 'alert alert-danger alert-dismissible');
		$('#msgdiv').append(m);
	} else {
		console.log('create the element!');
		$('#parentmsgdiv').append("<div id='msgdiv' class='alert alert-danger alert-dismissible'><button id='msgbtn' type='button' class='close' data-dismiss='alert'  aria-hidden='true'>  &times;  </button>");
		$('#msgdiv').append(m);
	}*/
	$('#parentmsgdiv').empty();
	//console.log('create the element!');
	$('#parentmsgdiv').append("<div id='msgdiv' class='alert alert-danger alert-dismissible'><button id='msgbtn' type='button' class='close' data-dismiss='alert' aria-hidden='true'>  &times;  </button>");
	$('#msgdiv').append(m);
	
}

////Clear SubjectCode contents, when clicked in SubjectCode textbox//
$('#subjectcode').on('focus', function() { $('#parentmsgdiv').empty(); });

////Add Dialog to show result of Edit button click in table. i.e server response///
function displayDialogMessageOnUI(mymessage, mytitle) { 
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
		close: function(event,ui) { $('#scModalDiv').empty(); }
   });
	$("#scModalDiv" ).dialog("open");
}
</script>
</html>