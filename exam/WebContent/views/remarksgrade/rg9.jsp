<!DOCTYPE html>

<%@page import="com.nmims.beans.RemarksGradeBean"%>
<%-- searchPassFail.jsp --%>
<html class="no-js">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="RemarksGrade 9" name="title" />
</jsp:include>

<body class="inside">
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
						<h2 class="red text-capitalize">RemarksGrade : PassFail Report</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%--@ include file="../adminCommon/messages.jsp"--%><%@ include
								file="../adminCommon/newmessages.jsp"%>
							<div class="container-fluid customTheme">
								<legend>&nbsp;PassFail Report</legend>
								<div class="row">
									<form:form method="post" modelAttribute="remarksGradeBean">
										<fieldset>
											<div class="col-md-4">
												<div class="form-group">
													<% if(roles.indexOf("Information Center") == -1 && roles.indexOf("Corporate Center") == -1) { %>
													<form:select id="centerCode" path="centerCode" type="text"
														class="form-control" placeholder="Centre Code"
														itemValue="${remarksGradeBean.centerCode}">
														<form:option value="">Select Information Centre</form:option>
														<form:options items="${centerCodeNameMap}" />
													</form:select>
													<% } %>
												</div>
												<div class="form-group">
													<form:select id="year" path="year" type="text"
														placeholder="Year" class="form-control"
														itemValue="${remarksGradeBean.year}" required="required">
														<form:option value="">(*) Select Exam Year</form:option>
														<form:options items="${yearList}" />
													</form:select>
												</div>

												<div class="form-group">
													<form:input id="sapid" path="sapid" type="text"
														placeholder="SAP ID" class="form-control"
														value="${remarksGradeBean.sapid}"/>
												</div>
												
												<div class="form-group">
													<form:select id="studentsResultType"
														path="studentsResultType" type="text"
														placeholder="Search Students Result Type" class="form-control"
														itemValue="${remarksGradeBean.studentsResultType}">
														<form:option value="">Select Pass/Fail</form:option>
														<form:option value="${RemarksGradeBean.ASSIGNMENT_RESULT_LIVE}">Pass Students</form:option>
														<form:option value="${RemarksGradeBean.ASSIGNMENT_RESULT_NOTLIVE}">Fail Students</form:option>
													</form:select>
												</div>
												<div class="form-group">
													<label class="control-label" for="submit"></label>
													<div class="controls" style="margin-top: -40px">
														<button id="submit" name="submit"
															class="btn btn-large btn-primary" formaction="searchRG9">Search</button>
														<button id="submit" name="submit"
															class="btn btn-large btn-primary" formaction="downloadRG9">Download</button>
													</div>
												</div>
											</div>
											<div class="col-md-4">
												<div class="form-group">&nbsp;</div>
												<div class="form-group">
													<form:select id="month" path="month" type="text"
														placeholder="Month" class="form-control"
														itemValue="${remarksGradeBean.month}" required="required">
														<form:option value="">(*) Select Exam Month</form:option>
														<form:options items="${monthList}" />
													</form:select>
												</div>
												<div class="form-group">
													<form:select id="sem" path="sem" placeholder="Semester"
														class="form-control" value="${remarksGradeBean.sem}">
														<form:option value="">Select Semester</form:option>
														<form:options items="${semList}" />
													</form:select>
												</div>
											</div>

											<div class="col-md-4">
												<div class="form-group">&nbsp;</div>
												<div class="form-group">
													<form:select id="studentTypeId" path="studentTypeId"
														type="text" placeholder="Students Type" class="form-control"
														itemValue="${remarksGradeBean.studentTypeId}">
														<form:option value="">Select Student Type</form:option>
														<form:options items="${consumerTypeMap}"/>
													</form:select>
												</div>
												<div class="form-group">
													<form:select id="programStructureId"
														path="programStructureId" type="text"
														placeholder="Program Structure" class="form-control"
														itemValue="${remarksGradeBean.programStructureId}">
														<form:option value="">Select Program Structure</form:option>
														<form:options items="${programStructureMap}"/>
													</form:select>
												</div>
												<div class="form-group">
													<form:select id="programId" path="programId" type="text"
														placeholder="Program" class="form-control"
														itemValue="${remarksGradeBean.programId}">
														<form:option value="">Select Program</form:option>
														<form:options items="${programMap}"/>
													</form:select>
												</div>
											</div>
										</fieldset>
									</form:form>
									<br>
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