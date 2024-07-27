<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@page import="java.util.*"%>
<%@page import="com.nmims.beans.RemarksGradeBean"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%-- tcs/viewTCSMarks.jsp --%>
<!DOCTYPE html>
<html lang="en">
<head>
<style>
</style>
<script>
</script>
<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="RemarksGrade 2" name="title" />
</jsp:include>
<body>
	<%@ include file="../adminCommon/header.jsp"%>

	<div class="sz-main-content-wrapper">
		<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;RemarksGrade" name="breadcrumItems" />
		</jsp:include>

		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>
				<div class="sz-content-wrapper examsPage">
					<%@ include file="../adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">
						<h2 class="red text-capitalize">RemarksGrade : View Marks
							(Assignment)</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%--@ include file="../adminCommon/messages.jsp"--%><%@ include
								file="../adminCommon/newmessages.jsp"%>
							<div class="container-fluid customTheme">
								<div class="row">
									<form:form modelAttribute="remarksGradeBean"
										enctype="multipart/form-data">
										<fieldset>
											<div class="row">
												<div class="col-md-3 column">

													<div class="form-group">
														<form:select id="year" path="year" type="text"
															required="required" placeholder="Exam Year"
															class="form-control" itemValue="${remarksGradeBean.year}">
															<form:option value="">(*) Select Exam Year</form:option>
															<form:options items="${yearList}" />
														</form:select>
													</div>
													<div class="form-group">
														<form:select id="month" path="month" type="text"
															required="required" placeholder="Exam Month"
															class="form-control" itemValue="${remarksGradeBean.month}">
															<form:option value="">(*) Select Exam Month</form:option>
															<form:options items="${monthList}" />
														</form:select>
													</div>

													<div class="form-group">
														<button id="submit" name="submit" formmethod="post"
															class="btn btn-large btn-primary" formaction="displayDataRG2">View</button>
														<button id="clear" name="clear" formmethod="post" formnovalidate="formnovalidate"
															class="btn btn-large btn-primary" formaction="clearRG2">Clear</button>	
													</div>
												</div>
												<div class="col-md-3 column">
													<div class="form-group">
														<form:select id="studentTypeId" path="studentType"
															type="text" placeholder="Students Type"
															class="form-control"
															itemValue="${remarksGradeBean.studentType}">
															<option disabled selected value="">Select Students Type</option>
															<form:options items="${consumerTypeMap}"/>
														</form:select>
													</div>
													<div class="form-group">
														<form:select id="programStructureId"
															path="programStructure" type="text"
															placeholder="Program Structure" class="form-control"
															itemValue="${remarksGradeBean.programStructure}">
															<form:option value="">Select Program Structure</form:option>
															<form:options items="${programStructureMap}"/>
														</form:select>
													</div>
													<div class="form-group">
														<form:select id="programId" path="program" type="text"
															placeholder="Program" class="form-control"
															itemValue="${remarksGradeBean.program}">
															<form:option value="">Select Program</form:option>
															<form:options items="${programMap}"/>
														</form:select>
													</div>
												</div>
												<div class="clearfix"></div>
												<c:if test="${rowCount > 0}">
												<div class="column">
													<legend>
														&nbsp;Marks Entries<font size="2px"> </font>
													</legend>
													<div class="table-responsive">
														<table class="table table-striped table-hover tables"
															style="font-size: 12px">
															<thead>
																<tr>
																	<th>Sr.No.</th>
																	<th>Exam Year</th>
																	<th>Exam Month</th>
																	<th>SapId</th>
																	<th>Name</th>
																	<th>Subject</th>
																	<th>Status</th>
																	<th>Assignment Score</th>
																	<th>Total Marks</th>
																</tr>
															</thead>
															<tbody>
															<c:forEach var="d" items="${dataList}" varStatus="status">
															<tr>
																<td><c:out value="${status.count}" /></td>
																<td><c:out value="${d.year}" /></td>
																<td><c:out value="${d.month}" /></td>
																<td><c:out value="${d.sapid}" /></td>
																<td><c:out value="${d.name}" /></td>
																<td><c:out value="${d.subject}" /></td>
																<td><c:out value="${d.status}" /></td>
																<td><c:out value="${d.scoreIA}" /></td>
																<td><c:out value="${d.scoreTotal}" /></td>
															</tr>
															</c:forEach>
															</tbody>
														</table>
													</div>
												</div>
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
<script>
<%-- /views/common/consumerProgramStructure.jsp --%>
$('#programStructureId').on('change', function(){
	//let id = $(this).attr('data-id');
	var consumerTypeIdd = $('#studentTypeId').val();
	var programStructureIdd = this.value;
	
	//console.log('programStructureId >' + programStructureIdd);
	//console.log('consumerTypeId >' + consumerTypeIdd);
	
	let options = "<option>Loading... </option>";
	$('#programId').html(options);
	 
	var data = {
		programStructureId : programStructureIdd,
	    consumerTypeId : consumerTypeIdd
	}
	
	$.ajax({
		type : "POST",
		contentType : "application/json",
		url : "getDataByProgramStructure",   
		data : JSON.stringify(data),
		success : function(data) {
			console.log("SUCCESS Program: ", data.programData);
			var programData = data.programData;
			
			var options1 = '';
			
			//Data Insert For programData List
			for(let i=0;i < programData.length;i++) {
				options1 = options1 + "<option value='" + programData[i].id + "'> " + programData[i].name + " </option>";
			}
			//console.log("==========> options\n" + options1);
			
			$('#programId').html(
				"<option disabled selected value=''>Select Program</option> " + options1
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

$('#studentTypeId').on('change', function(){
	//let id = $(this).attr('data-id');
	//console.log('studentTypeId >' + this.value);
	
	let options = "<option>Loading... </option>";
	$('#programStructureId').html(options);
	$('#programId').html(options);
	
	var data = {
		id: this.value
	}
	
	$.ajax({
		type : "POST",
		contentType : "application/json",
		url : "getDataByConsumerType",   
		data : JSON.stringify(data),
		success : function(data) {
			console.log("SUCCESS Program: ", data.programStructureData);
			var programStructureData = data.programStructureData;
			
			var options1 = '';
			
			//Data Insert For ProgramStructure List
			for(let i=0;i < programStructureData.length;i++) {
				options1 = options1 + "<option value='" + programStructureData[i].id + "'> " + programStructureData[i].name + " </option>";
			}
			//console.log("==========> options\n" + options1);
			
			$('#programStructureId').html(
				"<option disabled selected value=''>Select Program Structure</option> " + options1
			);
			$('#programId').html(
				"<option disabled selected value=''>Select Program</option>"
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
</script>
</html>