<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@page import="java.util.*"%>
<%@page import="com.nmims.beans.RemarksGradeBean"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html lang="en">
<head>
<style>
</style>
<script>
	
</script>
<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="RemarksGrade 66" name="title" />
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
						<h2 class="red text-capitalize">RemarksGrade : Absolute
							Grading</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%--@ include file="../adminCommon/messages.jsp"--%><%@ include
								file="../adminCommon/newmessages.jsp"%>

							<div class="container-fluid customTheme">
								<div class="row">
									<form:form modelAttribute="remarksGradeBean" method="post"
										enctype="multipart/form-data">
										<fieldset>
											<div class="col-md-4">
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
														value="${remarksGradeBean.sapid}" />
												</div>

												<div class="form-group">
													<label class="control-label" for="submit"></label>
													<div class="controls" style="margin-top: -40px">
														<button id="submit" name="submit"
															class="btn btn-large btn-primary" formaction="searchProcessRG66">Process</button>
													</div>
												</div>
											</div>
											<div class="col-md-4">
												<div class="form-group">
													<form:select id="month" path="month" type="text"
														placeholder="Month" class="form-control"
														itemValue="${remarksGradeBean.month}" required="required">
														<form:option value="">(*) Select Exam Month</form:option>
														<form:options items="${monthList}" />
													</form:select>
												</div>
												<div class="form-group">
													<form:select id="subject" path="subject"
														placeholder="Subject" class="form-control"
														value="${remarksGradeBean.subject}">
														<form:option value="">Select Subject</form:option>
														<form:options items="${subjectCodeIdMap}" />
													</form:select>
												</div>
											</div>

											<div class="col-md-4">
												<div class="form-group">
													<form:select id="studentTypeId" path="studentTypeId"
														type="text" placeholder="Students Type" class="form-control"
														itemValue="${remarksGradeBean.studentTypeId}">
														<option disabled selected value="">Select Students Type</option>
														<form:options items="${consumerTypeMap}"/>
													</form:select>
												</div>
												<div class="form-group">
													<form:select id="programStructureId"
														path="programStructureId" type="text"
														placeholder="Program Structure" class="form-control"
														itemValue="${remarksGradeBean.programStructureId}">
														<form:option value="">Select ProgramStructure</form:option>
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
	console.log('studentTypeId >' + this.value);
	
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