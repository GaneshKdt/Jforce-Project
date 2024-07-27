<!DOCTYPE html>
<html lang="en">

<%@page import="com.nmims.beans.RemarksGradeBean"%>
<%--  views/passFailTriggerSearchForm.jsp --%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="RemarksGrade 5" name="title" />
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

						<h2 class="red text-capitalize">RemarksGrade : Search
							Records For PassFail Processing</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%--@ include file="../adminCommon/messages.jsp"--%><%@ include
								file="../adminCommon/newmessages.jsp"%>
							<form:form method="post" modelAttribute="remarksGradeBean">
								<fieldset>
									<div class="row">
										<div class="col-md-4">
											<div class="form-group">
												<form:select id="enrollmentYear" path="year" type="text"
													placeholder="Year" class="form-control"
													itemValue="${remarksGradeBean.year}" required="required">
													<form:option value="">(*) Select Exam Year</form:option>
													<form:options items="${yearList}" />
												</form:select>
											</div>

											<div class="form-group">
												<form:select id="enrollmentMonth" path="month"
													class="form-control" itemValue="${remarksGradeBean.month}"
													required="required">
													<form:option value="">(*) Select Exam Month</form:option>
													<form:options items="${monthList}" />
												</form:select>
											</div>
										</div>

										<div class="col-md-4">
											<div class="form-group">
												<form:select id="studentTypeId" path="studentTypeId"
													placeholder="Students Type" class="form-control"
													itemValue="${remarksGradeBean.studentTypeId}">
													<option disabled selected value="">Select Consumer Type</option>
													<form:options items="${consumerTypeMap}"/>
												</form:select>
											</div>

											<div class="form-group">
												<form:select id="programStructureId"
													path="programStructureId" placeholder="Program Structure"
													class="form-control"
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

									</div>

									<div class="row">
										<div class="col-md-3">

											<div class="form-group">
												<label class="control-label" for="submit"></label>
												<button id="submit" name="submit"
													class="btn btn-large btn-primary"
													formaction="searchRG5">Search</button>
												<button id="reset" type="reset" class="btn btn-danger"
													type="reset">Reset</button>
												<button id="cancel" name="cancel" class="btn btn-danger"
													formaction="${pageContext.request.contextPath}/home" formnovalidate="formnovalidate">Cancel</button>
											</div>
										</div>
									</div>
								</fieldset>
							</form:form>
							<div class="clearfix"></div>
							<c:if test="${rowCount > 0}">
								<form:form method="post" modelAttribute="remarksGradeBean">
								<form:hidden path="year" />
								<form:hidden path="month" />
								<form:hidden path="studentTypeId" />
								<form:hidden path="programStructureId" />
								<form:hidden path="programId" />
								<div class="row">
									<div class="col-md-4">
										
										<div class="panel-body">

											<div class="row">
												<h2>&nbsp;Records Count Distribution</h2>
											</div>

											<div class="table-responsive">
												<table class="table table-striped" style="font-size: 12px"
													border="1px">
													<thead>
														<tr>
															<td>Category</td>
															<td>Count</td>
														</tr>
													</thead>
													<tbody>
														<c:forEach items="${summaryMap}" var="d">
															<tr>
																<td>${d.key}</td>
																<td>${d.value}</td>
															</tr>
														</c:forEach>
													</tbody>
												</table>
											</div>
										</div>

										<button id="submitforPF" name="submit"
											class="btn btn-large btn-primary" formaction="processRG5">Process remaining
											records for Pass/Fail</button>
										<button id="cancel" name="cancel" class="btn btn-danger"
											formaction="${pageContext.request.contextPath}/home" formnovalidate="formnovalidate">Cancel</button>
									</div>
								</div>
							</form:form>
							</c:if>
						</div>
						<div class="clearfix"></div>
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