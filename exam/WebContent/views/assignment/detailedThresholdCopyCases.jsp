<!DOCTYPE html>

<html class="no-js">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="../jscss.jsp">
<jsp:param value="Assignment Detailed Threshold CC" name="title" />
</jsp:include>
<head>
	<link rel="stylesheet" href="https://cdn.datatables.net/1.10.19/css/jquery.dataTables.min.css">
	<style>
		.dataTables_filter > label > input{
			float:right !important;
		}
		.toggleListWell{
		cursor: pointer !important;
			margin-bottom:0px !important;
		}
		.toggleWell{
			background-color:white !important;
		}
		input[type="radio"]{
			width:auto !important;
			height:auto !important;
			
		}
		.optionsWell{
			padding:0px 10px;
		}
	</style>
</head>
<body class="inside">

<%@ include file="../header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Assignment Detailed Threshold Copy Cases</legend></div>
        <%@ include file="../messages.jsp"%>
		<div class="panel-body clearfix">
			<form:form id='assignmentDetailedThresholdCCForm' action="searchAssignmentDetailedThresholdCC" method="post" modelAttribute="searchBean">
				<fieldset>
				<div class="panel-body">
					<div class="col-md-6 column">
						<div class="form-group">
							<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control" itemValue="${searchBean.year}" required="required">
								<form:option value="">Select Year</form:option>
								<form:options items="${yearList}" />
							</form:select>
						</div>
					</div>
					<div class="col-md-6 column">
						<div class="form-group">
							<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" itemValue="${searchBean.month}" required="required">
								<form:option value="">Select Month</form:option>
								<form:options items="${examMonthList}" />
							</form:select>
						</div>
					</div>
					<div class="col-md-6 column">	
						<%-- <div class="form-group">
								<form:select id="subject" path="subject" type="text" placeholder="Subject" class="form-control" itemValue="${searchBean.subject}" required="required">
									<form:option value="">Select Subject</form:option>
									<form:options items="${subjectList}" />
								</form:select>
						</div> --%>
						<div class="form-group" style="overflow:visible;">
							<form:select id="subject" path="subject"  class="combobox form-control"   itemValue="${searchBean.subject}" required="required" >
								<form:option value="" selected="selected">Type OR Select Subject</form:option>
								<form:options items="${subjectList}" />
							</form:select>
						</div>
					</div>
					<div class="col-md-6 column">
						<div class="form-group">
							<form:input id="sapId1" path="sapId1" type="text" placeholder="Sapid" class="form-control" value="${searchBean.sapId1}"/>
						</div>
					</div>
				</div>	
				<div class="col-md-6 column">
					<div class="form-group">
						<div class="controls">
							<button id="submit" name="submit" class="btn btn-sm btn-primary" formaction="searchAssignmentDetailedThresholdCC">Search</button>
							<button id="cancel" name="cancel" class="btn btn-danger btn-sm" formaction="home" formnovalidate="formnovalidate">Cancel</button>
						</div>
					</div>
				</div>
				</fieldset>
			</form:form>
				
			<div class="panel-group" style="margin-top : 30px" id="accordion" role="tablist" aria-multiselectable="true">
				<c:if test="${ detailedThreshold1List != null && detailedThreshold1List.size() > 0 }">
					<div class="panel panel-default">
						<div class="panel-heading" role="tab" id="DT1List">
							<h4 class="panel-configuration">
								<a role="button" data-toggle="collapse" data-parent="#accordion" href="#DT1-list" aria-controls="DT1-list">
									Detailed Threshold 1 Count: ${DT1ListCount}
								</a>
							</h4>
						</div>
						<div id="DT1-list" class="panel-collapse collapse" role="tabpanel" aria-labelledby="DT1List">
							<div class="panel-body">
								<h5></h5>
								<div class="table-responsive">
									<table id="DT1-list-table" class="table table-striped" style="width: 100% !important;">
										<thead>
											<tr>
												<!-- <th>Month</th>
												<th>Year</th> -->
												<th>Subject</th>
												<th>Student ID 1</th>
												<th>First Name 1</th>
												<th>Last Name 1</th>
												<th>Program 1</th>
												<th>IC 1</th>
												<th>Student ID 2</th>
												<th>First Name 2</th>
												<th>Last Name 2</th>
												<th>Program 2</th>
												<th>IC 2</th>
												<th>Matching %</th>
												<th>Matching % in 80 to 80.99</th>
												<th>Max Consecutive Lines Matched</th>
												<th># of Lines in First File</th>
												<th># of Lines in Second File</th>
												<th># of Lines matched</th>
											</tr>
										</thead>
										<tbody>
											<c:forEach var="DT1Bean" items="${detailedThreshold1List}">
												<fmt:parseNumber value="${DT1Bean.matching}" var="matching" integerOnly="true" type="number"/>
												<tr>
													<%-- <td><c:out value="${DT1Bean.month}"/></td>
													<td><c:out value="${DT1Bean.year}"/></td> --%>
													<td><c:out value="${DT1Bean.subject}"/></td>
													<td><c:out value="${DT1Bean.sapId1}"/></td>
													<td><c:out value="${DT1Bean.firstName1}"/></td>
													<td><c:out value="${DT1Bean.lastName1}"/></td>
													<td><c:out value="${DT1Bean.program1}"/></td>
													<td><c:out value="${DT1Bean.centerName1}"/></td>
													<td><c:out value="${DT1Bean.sapId2}"/></td>
													<td><c:out value="${DT1Bean.firstName2}"/></td>
													<td><c:out value="${DT1Bean.lastName2}"/></td>
													<td><c:out value="${DT1Bean.program2}"/></td>
													<td><c:out value="${DT1Bean.centerName2}"/></td>
													<td><c:out value="${matching}"/></td>
													<td><c:out value="${DT1Bean.matchingFor80to90}"/></td>
													<td><c:out value="${DT1Bean.maxConseutiveLinesMatched}"/></td>
													<td><c:out value="${DT1Bean.numberOfLinesInFirstFile}"/></td>
													<td><c:out value="${DT1Bean.numberOfLinesInSecondFile}"/></td>
													<td><c:out value="${DT1Bean.noOfMatches}"/></td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
						</div>
					</div>
				</c:if>
				<c:if test="${ detailedThreshold2List != null && detailedThreshold2List.size() > 0 }">
					<div class="panel panel-default">
						<div class="panel-heading" role="tab" id="DT2List">
							<h4 class="panel-configuration">
								<a role="button" data-toggle="collapse" data-parent="#accordion" href="#DT2-list" aria-controls="DT2-list">
									Detailed Threshold 2 Count: ${DT2ListCount}
								</a>
							</h4>
						</div>
						<div id="DT2-list" class="panel-collapse collapse" role="tabpanel" aria-labelledby="DT2List">
							<div class="panel-body">
								<h5></h5>
								<div class="table-responsive">
									<table id="DT2-list-table" class="table table-striped" style="width: 100% !important;">
										<thead>
											<tr>
												<!-- <th>Month</th>
												<th>Year</th> -->
												<th>Subject</th>
												<th>Student ID 1</th>
												<th>First Name 1</th>
												<th>Last Name 1</th>
												<th>Program 1</th>
												<th>IC 1</th>
												<th>Student ID 2</th>
												<th>First Name 2</th>
												<th>Last Name 2</th>
												<th>Program 2</th>
												<th>IC 2</th>
												<th>Matching %</th>
												<th>Matching % in 80 to 80.99</th>
												<th>Max Consecutive Lines Matched</th>
												<th># of Lines in First File</th>
												<th># of Lines in Second File</th>
												<th># of Lines matched</th>
											</tr>
										</thead>
										<tbody>
											<c:forEach var="DT2Bean" items="${detailedThreshold2List}">
											<fmt:parseNumber value="${DT2Bean.matching}" var="matching" integerOnly="true" type="number"/>
												<tr>
													<%-- <td><c:out value="${DT2Bean.month}"/></td>
													<td><c:out value="${DT2Bean.year}"/></td> --%>
													<td><c:out value="${DT2Bean.subject}"/></td>
													<td><c:out value="${DT2Bean.sapId1}"/></td>
													<td><c:out value="${DT2Bean.firstName1}"/></td>
													<td><c:out value="${DT2Bean.lastName1}"/></td>
													<td><c:out value="${DT2Bean.program1}"/></td>
													<td><c:out value="${DT2Bean.centerName1}"/></td>
													<td><c:out value="${DT2Bean.sapId2}"/></td>
													<td><c:out value="${DT2Bean.firstName2}"/></td>
													<td><c:out value="${DT2Bean.lastName2}"/></td>
													<td><c:out value="${DT2Bean.program2}"/></td>
													<td><c:out value="${DT2Bean.centerName2}"/></td>
													<td><c:out value="${matching}"/></td>
													<td><c:out value="${DT2Bean.matchingFor80to90}"/></td>
													<td><c:out value="${DT2Bean.maxConseutiveLinesMatched}"/></td>
													<td><c:out value="${DT2Bean.numberOfLinesInFirstFile}"/></td>
													<td><c:out value="${DT2Bean.numberOfLinesInSecondFile}"/></td>
													<td><c:out value="${DT2Bean.noOfMatches}"/></td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
						</div>
					</div>
				</c:if>
			</div>
		</div>
	</section>
	
<jsp:include page="../footer.jsp" />

<script src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js"></script>
<script>
	$(document).ready( function () {
		var DT1Table = $('#DT1-list-table').DataTable({
			"autoWidth": true,
			/* "scrollY": '500px',
	        "scrollCollapse": false,
	        "paging": true */
		});
		var DT2Table = $('#DT2-list-table').DataTable({
			"autoWidth": true,
			/* "scrollY": '500px',
	        "scrollCollapse": true,
	        "paging": true */
		});
	} );	

</script>
</body>
</html>