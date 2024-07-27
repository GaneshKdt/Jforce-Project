<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html class="no-js">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Manual Recording Upload Report" name="title" />
</jsp:include>

<style>
.modal-backdrop {
	z-index: -1;
}
</style>

<body class="inside">
	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">
			<div class="row">
				<legend>Manual Recording Video Upload Report</legend>
			</div>
			<%@ include file="messages.jsp"%>

			<form:form action="manualRecordingUploadReport" method="post"
				modelAttribute="videoContentBean">
				<fieldset>
					<div class="panel-body">
						<div class="col-md-6 column">
							<label for="fromDate">Acad Year</label><br>
							<div class="form-group">
								<form:select id="year" path="year" type="text"
									placeholder="Acad Year" class="form-control" required="true"
									itemValue="${videoContentBean.year}">
									<form:option value="">Select Academic Year</form:option>
									<form:options items="${yearList}" />
								</form:select>
							</div>

							<label for="fromDate">Acad Month</label><br>
							<div class="form-group">
								<form:select id="month" path="month" type="text"
									placeholder="Acad Month" class="form-control" required="true"
									itemValue="${videoContentBean.month}">
									<form:option value="">Select Academic Month</form:option>
									<form:options items="${monthList}" />
								</form:select>
							</div>

							<label for="fromDate">Subject Code</label><br>
							<div class="form-group" style="overflow: visible;">
								<form:select id="subjectCodeId" path="subjectCodeId"
									class="combobox form-control"
									itemValue="${videoContentBean.subjectCodeId}">
									<form:option value="">Select Subject Code</form:option>
									<c:forEach items="${subjectCodeMapWithId}" var="subjectMap">
										<form:option value="${subjectMap.key}">${subjectMap.value}</form:option>
									</c:forEach>
								</form:select>
							</div>
						</div>


						<div class="col-md-6 column">

							<label for="fromDate">From Date</label><br>
							<div class="form-group">
								<form:input path="fromSessionDate" type="date"
									placeholder="Session Date" class="form-control"
									value="${videoContentBean.fromSessionDate}" />
							</div>

							<label for="fromDate">Upto Date</label><br>
							<div class="form-group">
								<form:input path="toSessionDate" type="date"
									placeholder="Session Date" class="form-control"
									value="${videoContentBean.toSessionDate}" />
							</div>

							<label for="fromDate">Faculty Id</label><br>
							<div class="form-group">
								<form:input id="facultyId" path="facultyId" type="text"
									placeholder="Faculty Id" class="form-control"
									value="${videoContentBean.facultyId}" />
							</div>
						</div>
					</div>
					<div class="form-group">
						<label class="control-label" for="submit"></label>
						<button id="submit" name="submit"
							class="btn btn-large btn-primary"
							formaction="manualRecordingUploadReport">Search</button>

						<button id="cancel" name="cancel" class="btn btn-danger"
							formaction="home" formnovalidate="formnovalidate">Cancel</button>
					</div>
				</fieldset>
			</form:form>

			<c:choose>
				<c:when test="${manualRecordingUploadReportListSize > 0}">
					<legend>
						&nbsp;Manual Video Recording Report<font size="2px">
							(${manualRecordingUploadReportListSize} Records Found) &nbsp; <a
							href="downloadManualRecordingExcel">Download to Excel</a>
						</font>
					</legend>
					
					 
					<div class="table-responsive">
						<table class="table table-striped table-hover tables" style="font-size: 12px">
							<thead>
								<tr>
									<th>Sr. No.</th>
									<th>Year</th>
									<th>Month</th>
									<th>Subject</th>
									<th>File Name</th>
									<th>Faculty Id</th>
									<th>Session Id</th>
									<th>Keywords</th>
									<th>Description</th>
									<th>Added On</th>
								</tr>
							</thead>

							<tbody>
								<c:forEach var="bean" items="${manualRecordingUploadReportList}"
									varStatus="status">
									<tr>
										<td><c:out value="${status.count}" /></td>
										<td><c:out value="${bean.year}" /></td>
										<td><c:out value="${bean.month}" /></td>
										<td nowrap="nowrap"><c:out value="${bean.subject}" /></td>
										<td><c:out value="${bean.fileName}" /></td>
										<td><c:out value="${bean.facultyId}" /></td>
										<td><c:out value="${bean.sessionId}" /></td>
										<td><c:out value="${bean.keywords}" /></td>
										<td><c:out value="${bean.description}" /></td>
										<td><c:out value="${bean.addedOn}" /></td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
					<br>
				</c:when>
			</c:choose>
		</div>
	</section>
	<jsp:include page="footer.jsp" />
	
<!-- jQuery (necessary for Bootstrap's JavaScript plugins) --> 
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />assets/js/bootstrap.js"></script> 
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/jquery-ui.min.js"></script>  
<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js" ></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js" ></script>

<script>var editor = new MediumEditor('.editable');</script>

	<script>
	$('.tables').DataTable({
        initComplete: function () {
            this.api().columns().every( function () {
                var column = this;
                var headerText = $(column.header()).text();
                console.log("header :"+headerText);
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
                
                if(headerText == "Year") {
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

                if(headerText == "Month") {
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
                
                if(headerText == "Batch") {
                   var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
                    .appendTo( $(column.header()) )
                    .on( 'change', function () {
                        var val = $.fn.dataTable.util.escapeRegex(
                            $(this).val()
                        ); 
                        column
                            .search( val ? '^'+val+'$' : '', true, false )
                            .draw();
                    });
 
                column.data().unique().sort().each( function ( d, j ) {
                    select.append( '<option value="'+d+'">'+d+'</option>' )
                } );
              }                
            });
        }
    });
	</script>
	
	</body>
</html>