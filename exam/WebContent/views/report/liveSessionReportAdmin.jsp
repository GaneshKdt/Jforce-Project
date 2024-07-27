<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@page import="java.util.*"%>
<%@page import="com.nmims.beans.LiveSessionReportAdminDTO"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!DOCTYPE html>
<html lang="en">
<head>
<style>
</style>
<script>
</script>
<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="LiveSession Report" name="title" />
</jsp:include>
<body>
	<%@ include file="../adminCommon/header.jsp"%>

	<div class="sz-main-content-wrapper">
		<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;LiveSession Report" name="breadcrumItems" />
		</jsp:include>

		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>
				<div class="sz-content-wrapper examsPage">
					<%@ include file="../adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">
						<h2 class="red text-capitalize">LiveSession Report</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%--@ include file="../adminCommon/messages.jsp"--%><%@ include
								file="../adminCommon/newmessages.jsp"%>
							<div class="container-fluid customTheme">
								<div class="row">
									<form:form modelAttribute="liveSessionReportAdminDTO"
										enctype="multipart/form-data">
										<fieldset>
											<div class="row">
												<div class="col-md-3 column">

													<div class="form-group">
														<form:select id="year" path="acadYear" type="text"
															required="required" placeholder="Acad Year"
															class="form-control" itemValue="${liveSessionReportAdminDTO.acadYear}">
															<form:option value="">(*) Select Acad Year</form:option>
															<form:options items="${yearList}" />
														</form:select>
													</div>
													<div class="form-group">
														<form:select id="month" path="acadMonth" type="text"
															required="required" placeholder="Acad Month"
															class="form-control" itemValue="${liveSessionReportAdminDTO.acadMonth}">
															<form:option value="">(*) Select Acad Month</form:option>
															<form:options items="${monthList}" />
														</form:select>
													</div>

													<div class="form-group">
														<button id="submit" name="submit" formmethod="post"
															class="btn btn-large btn-primary" formaction="fetchLiveSessionReport">View</button>
														<button id="clear" name="clear" formmethod="post" formnovalidate="formnovalidate"
															class="btn btn-large btn-primary" formaction="clearScreen">Clear</button>
														<c:if test="${rowCount > 0}">
														<button id="download" name="download" formmethod="post"
															formnovalidate="formnovalidate"
															class="btn btn-large btn-primary" formaction="downloadLiveSessionReport">Download</button>
														</c:if>
													</div>
												</div>
												<div class="col-md-3 column">
													<div class="form-group">
														<form:select id="consumerTypeId" path="studentType"
															type="text" placeholder="Students Type"
															class="form-control" disabled="true"
															itemValue="${liveSessionReportAdminDTO.studentType}">
															<form:options items="${consumerTypeMap}"/>
														</form:select>
													</div>
												</div>
												<div class="clearfix"></div>
												<c:if test="${rowCount > 0}">
													<div class="column">
														<legend>
															&nbsp;Recorded/Live Session (Subject Type) Report 
															<c:if test="${rowCount > 500}">
															<span style="color:red;font-size:18px">Data is too large, Please download the excel file
															</span></c:if>
														</legend>
														<div class="table-responsive">
															<table class="table table-striped table-hover tables"
																style="font-size: 12px">
																<thead>
																	<tr>
																		<th>Sr. No.</th>
																		<th>Center Name</th>
																		<th>Consumer</th>
																		<th>Program Structure</th>
																		<th>Program</th>
																		<th>Acad Year</th>
																		<th>Acad Month</th>
																		<th>SapId</th>
																		<th>Name</th>
																		<th>Sem</th>
																		<th>Subject</th>
																		<th>Recorded/Live</th>
																		<th>Email</th>
																		<th>Phone</th>
																	</tr>
																</thead>
																<tbody>
																	<c:forEach var="d" items="${dataList}" varStatus="statusOld" begin="0" end="499">
																		<tr>
																			<td><c:out value="${statusOld.count}" /></td><!-- 1  -->
																			<td><c:out value="${d.centerName}" /></td>
																			<td><c:out value="${d.consumerType}" /></td>
																			<td><c:out value="${d.programStructure}" /></td><!-- 4 -->
																			<td><c:out value="${d.program}" /></td>
																			<td><c:out value="${d.acadYear}" /></td><!-- 6  -->
																			<td><c:out value="${d.acadMonth}" /></td>
																			<td><c:out value="${d.sapId}" /></td>
																			<td><c:out value="${d.studentName}" /></td><!-- 9  -->
																			<td><c:out value="${d.sem}" /></td>
																			<td><c:out value="${d.subjectName}" /></td>
																			<td><c:out value="${d.sessionType}" /></td><!-- 12 -->
																			<td><c:out value="${d.emailId}" /></td>
																			<td><c:out value="${d.phone}" /></td><!-- 14 -->
																		</tr>
																	</c:forEach>
																</tbody>
															</table>
														</div>
													</div>
													<%--<div class="column">
													<legend>
														&nbsp;Recorded/Live Subject Type Report<font size="2px"> </font>
													</legend>
													<div class="table-responsive">
														<table class="table table-striped table-hover tables"
															style="font-size: 12px">
															<thead>
																<tr>
																	<th>Sr.No.</th>
																	<th>Center Name</th>
																	<th>Consumer</th>
																	<th>Program Structure</th>
																	<th>Program</th>
																	<th>Acad Year</th>
																	<th>Acad Month</th>
																	<th>SapId</th>
																	<th>Name</th>
																	<th>Sem</th>
																	<th>Subject</th>
																	<th>Recorded/Live</th>
																	<th>Email</th>
																	<th>Phone</th>
																</tr>
															</thead>
															<tbody>
															<c:forEach var="d" items="${dataList}" varStatus="status">
															<tr>
																<td><c:out value="${status.count}" /></td>
																<td><c:out value="${d.centerName}" /></td>
																<td><c:out value="${d.consumerType}" /></td>
																<td><c:out value="${d.programStructure}" /></td>
																<td><c:out value="${d.program}" /></td>
																<td><c:out value="${d.acadYear}" /></td>
																<td><c:out value="${d.acadMonth}" /></td>
																<td><c:out value="${d.sapId}" /></td>
																<td><c:out value="${d.studentName}" /></td>
																<td><c:out value="${d.sem}" /></td>
																<td><c:out value="${d.subjectName}" /></td>
																<td><c:out value="${d.sessionType}" /></td>
																<td><c:out value="${d.emailId}" /></td>
																<td><c:out value="${d.phone}" /></td>
															</tr>
															</c:forEach>
															</tbody>
														</table>
													</div>
												</div> --%>
												</c:if>
											</div>
										</fieldset>
									</form:form>
								</div>
							</div>

							<div class="clearfix"></div>
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
$('.tables').Tabledit({
	columns: {
		identifier: [13, 'phone'],                 
		editable: []
	},
	
// link to server script
// e.g. 'ajax.php'
url: "",//NOTE: URL must not be empty, URL of save or update must be here.
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
rowIdentifier: 'phone',
// activate focus on first input of a row when click in save button
autoFocus: true,
// hide the column that has the identifier
hideIdentifier: false,
// activate edit button instead of spreadsheet style
editButton: false,
// activate delete button
deleteButton: false,
// activate save button when click on edit button
saveButton: false,
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
               
               if(headerText == "Center Name")
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

   			if(headerText == "Consumer") {
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
   			if(headerText == "Program") {
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
   			if(headerText == "Name") {
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
			if(headerText == "Sem") {
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
			if(headerText == "Subject") {
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

			if(headerText == "Recorded/Live") {
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
onSuccess: function(data, textStatus, jqXHR) { return; },
// executed when occurred an error on ajax request
// onFail()
onFail: function(jqXHR, textStatus, errorThrown) { return; },
// executed whenever there is an ajax request
onAlways: function() { return; },

// executed before the ajax request
onAjax: function(action, serialize) { }

});
</script>
</html>