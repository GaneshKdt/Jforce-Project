<!DOCTYPE html>
<html lang="en">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>  
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="IA Verify Parameters Report" name="title" />
</jsp:include>

<style>
	.panel-default {
		margin-top: 1em;
	}
	.panel-heading {
		padding: 1em 1.6em 2em;
	}
	.panel-body {
		padding-left: 2em;
	}
	.formLabel {
		font-size: 1em;
	}
	input {
		max-width: 300px;		/*As max width is set to 300px for Select tags in bootstrap, replicating the same for input tags*/
	}
	h2 {
		margin: 0.5em;
	}
	#excelDownload {
		padding: 0.4em 0.8em 0.3em;
		margin-left: 0.8em;
	}
</style>
<body>
	<%@ include file="../adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;Internal Assessment;IA Verify Parameters Report" name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>


				<div class="sz-content-wrapper examsPage">
					<%@ include file="../adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">

						<div class="panel panel-default">
							<div class="panel-heading">
								<h2 class="panel-title red text-capitalize">IA Verify Parameters Report</h2>
							</div>
							<div class="panel-body">
								<%@ include file="../adminCommon/messages.jsp"%>
								<form:form id="verifyParametersReportForm" action="iaVerifyParametersReport" 
									method="post" modelAttribute="iaReportDto">
									
									<div class="row">	
										<div class="col-md-3 form-group">
											<label class="formLabel" for="examYear">Exam Year</label>
											<form:select id="examYear" path="examYear" 
		   										class="form-control" itemValue="${iaReportDto.examYear}">   
		   										<form:option value="">Select Exam Year</form:option>
		   										<form:options items="${currentYearList}" />
		   									</form:select>
										</div>
		
										<div class="col-md-3 form-group">
											<label class="formLabel" for="examMonth">Exam Month</label>
											<form:select id="examMonth" path="examMonth" 
												class="form-control"  itemValue="${iaReportDto.examMonth}">
												<form:option value="" label="Select Exam Month"></form:option>
												<form:options items="${acadMonthList}"/>
											</form:select>
										</div>
		
										<div class="col-md-3 form-group">
											<label class="formLabel" for="acadYear">Academic Year</label>
											<form:select id="acadYear" path="acadYear" 
												class="form-control" itemValue="${iaReportDto.acadYear}">
												<form:option value="">Select Academic Year</form:option>
												<form:options items="${acadYearList}"/>
											</form:select>
										</div>
		
										<div class="col-md-3 form-group">
											<label class="formLabel" for="acadMonth">Academic Month</label>
											<form:select id="acadMonth" path="acadMonth" class="form-control"
												itemValue="${iaReportDto.acadMonth}">
												<form:option value="" label="Select Academic Month"></form:option>
												<form:options items="${acadMonthList}"/>
											</form:select>
										</div>
									</div>
									
									<div class="row">
										<div class="col-md-3 form-group">
											<label class="formLabel" for="startDate">Start Date</label>
											<input type="date" id="startDate" name="startDate"
												class="form-control" value="${iaReportDto.startDate}"/>
										</div>
										
										<div class="col-md-3 form-group">
											<label class="formLabel" for="facultyNameId">Faculty Name & Id</label>
											<form:select id="facultyNameId" path="facultyNameId" 
												class="form-control" itemValue="${iaReportDto.facultyNameId}">
												<form:option value="">Select Faculty Name & Id</form:option>
												<form:options items="${facultyList}"/>
											</form:select>
										</div>
									</div>
									
									<input type="hidden" id="programStructureByProgramMap" name="programStructureByProgramMap" 
											value='<c:out value="${programStructureByProgramMap}"></c:out>'>
											
									<div class="row">
										<div class="col-md-3 form-group">
											<label class="formLabel" for="consumerTypeId">Consumer Type</label>
											<form:select id="consumerTypeId" path="consumerTypeId" 
												class="form-control" itemValue="${iaReportDto.consumerTypeId}">
												<form:option value="">Select Consumer Type</form:option>
												<c:forEach var="entry" items="${consumerTypeMap}">
													<form:option value="${entry.key}">
														<c:out value="${entry.value}"/>
													</form:option>
												</c:forEach>
											</form:select>
										</div>
										
										<div class="col-md-3 form-group">
											<label class="formLabel" for="programId">Program</label>
											<form:select id="programId" path="programId" class="form-control" 
												onchange="programChange()" itemValue="${iaReportDto.programId}">
												<form:option value="">Select Program</form:option>
												<c:forEach var="entry" items="${programMap}">
													<form:option value="${entry.key}">
														<c:out value="${entry.value}"/>
													</form:option>
												</c:forEach>
											</form:select>
										</div>
										
										<div class="col-md-3 form-group">
											<label class="formLabel" for="programStructureId">Program Structure</label>
											<select id="programStructureId" name="programStructureId" disabled="disabled" 
												class="form-control" title="Select Program field value to unlock">
												<option value="">Select Program Structure</option>
												<c:if test="${(iaReportDto.programStructureId != null) && (iaReportDto.programStructureId != 0)}">
													<%System.out.println("in programStructure if"); %>
													<option value="${iaReportDto.programStructureId}" selected>
														<c:out value="${selectedProgramStructureName}"/>
													</option>
												</c:if>
											</select>
										</div>
									</div>
									
									<div class="row">
										<div class="col-md-3 form-group">
											<label class="formLabel" for="testName">IA Name</label>
											<form:select id="testName" path="testName" 
												class="form-control" itemValue="${iaReportDto.testName}">
												<form:option value="">Select IA Name</form:option>
												<form:options items="${testNameList}"/>
											</form:select>
										</div>
										
										<div class="col-md-3 form-group">
											<label class="formLabel" for="subject">Subject</label>
											<form:select id="subject" path="subject" 
												class="form-control" itemValue="${iaReportDto.subject}">
												<form:option value="">Select Subject</form:option>
												<form:options items="${subjectList}"/>
											</form:select>
										</div>
										
										<div class="col-md-3 form-group">
											<label class="formLabel" for="testType">IA Type</label>
											<form:select id="testType" path="testType" 
												class="form-control">
												<form:option value="">Select IA Type</form:option>
												<form:option value="test">Test</form:option>
												<form:option value="assignment">Assignment</form:option>
											</form:select>
										</div>
									</div>
									
									<div class="row col-md-6">
										<div class="form-group">
											<button id="submit" name="submit"
												class="btn btn-large btn-primary">Search</button>
											<button id="reset" type="reset"
												class="btn btn-danger">Reset</button>
											<button id="cancel" name="cancel" class="btn btn-danger" formnovalidate
												formaction="${pageContext.request.contextPath}/home">Cancel</button>
										</div>
									</div>
								</form:form>
							</div>
						</div>
						
						<hr style="background-color: rgb(228, 209, 209); height: 1px; border: 0;">
						<c:if test="${dataCount > 0}">
							<h2>
								&nbsp;Internal Assessment Records: 
								<font id="recordsCountFont" size="4em">${dataCount}
									<c:set var="isAdmin" value="false" />
									<c:forTokens items="${userRoles}" delims="," var="role">
										<c:if test="${(fn:containsIgnoreCase(role, 'Assignment Admin')) || (fn:containsIgnoreCase(role, 'Exam Admin'))}">
											<c:set var="isAdmin" value="true" />
										</c:if>
									</c:forTokens>
									<c:if test="${isAdmin}">
										<a id="excelDownload" class="btn btn-sm btn-danger"
											href="downloadIaVerifyParametersReport">
											<i class="fa-solid fa-download" aria-hidden="true"></i>
											&nbsp;Download to Excel
										</a>
									</c:if>
								</font>
							</h2>
							<div class="clearfix"></div>
							<div class="panel-content-wrapper">
								<div class="table-responsive">
									<table class="table table-striped table-hover"
										style="font-size: 12px" id="iaReportTable">
										<thead>
											<tr>
												<th>Sr. No.</th>
												<th>Test Name</th>
												<th>Test Initials</th>
												<th>Test Description</th>
												<th>Exam Year</th>
												<th>Exam Month</th>
												<th>Acad Year</th>
												<th>Acad Month</th>
												<th>Faculty Id</th>
												<th>Faculty Name</th>
												<th>Start Date Time</th>
												<th>End Date Time</th>
												<th>Window time (in Minutes)</th>
												<th>Duration (in Minutes)</th>
												<th>Consumer Type</th>
												<th>Program</th>
												<th>Program Structure</th>
												<th>Subject</th>
												<th>Applicable Type</th>
												<th>Batch Name</th>
												<th>Module Name</th>
												<th>Type</th>
												<th>Max Questions To Show to Students</th>
												<th>Questions Configured</th>
												<th>Questions Uploaded</th>
												<th>Score out of</th>
<!-- 												<th>Allow After End Date (Y / N)<th> -->
												<th>Send Email Alert (Y / N)</th>
												<th>Send SMS Alert (Y / N)</th>
												<th>Proctoring Enabled (Y / N)</th>
												<th>Show Calculator (Y / N)</th>
												<th>Test Live (Y / N)</th>
												<th>Results Live (Y / N)</th>
											</tr>
										</thead>
										<tbody>
											<c:forEach var="iaReportData" items="${iaReportDataList}" varStatus="status">
												<tr>
													<td><c:out value="${status.count}" /></td>
													<td><c:out value="${iaReportData.testName}" /></td>
													<td><c:out value="${iaReportData.testInitials}"/></td>
													<td><c:out value="${iaReportData.testDescription}" /></td>
													<td><c:out value="${iaReportData.year}" /></td>
													<td><c:out value="${iaReportData.month}" /></td>
													<td><c:out value="${iaReportData.acadYear}" /></td>
													<td><c:out value="${iaReportData.acadMonth}" /></td>
													<td><c:out value="${iaReportData.facultyId}" /></td>
													<td><c:out value="${iaReportData.facultyName}" /></td>
													<td><c:out value="${iaReportData.startDate}" /></td>
													<td><c:out value="${iaReportData.endDate}" /></td>
													<td><c:out value="${iaReportData.windowTime}" /></td>
													<td><c:out value="${iaReportData.duration}" /></td>
													<td><c:out value="${iaReportData.consumerType}" /></td>
													<td><c:out value="${iaReportData.program}" /></td>
													<td><c:out value="${iaReportData.programStructure}" /></td>
													<td><c:out value="${iaReportData.subject}" /></td>
													<td><c:out value="${iaReportData.applicableType}" /></td>
													<td><c:out value="${iaReportData.batchName}" /></td>
													<td><c:out value="${iaReportData.moduleName}" /></td>
													<td><c:out value="${iaReportData.testType}" /></td>
													<td><c:out value="${iaReportData.maxQuestnToShow}" /></td>
													<td><c:out value="${iaReportData.questionsConfigured}" /></td>
													<td><c:out value="${iaReportData.questionsUploaded}" /></td>
													<td><c:out value="${iaReportData.maxScore}" /></td>
<%-- 													<td><c:out value="${iaReportData.allowAfterEndDate}" /></td> --%>
													<td><c:out value="${iaReportData.sendEmailAlert}" /></td>
													<td><c:out value="${iaReportData.sendSmsAlert}" /></td>
													<td><c:out value="${iaReportData.proctoringEnabled}" /></td>
													<td><c:out value="${iaReportData.showCalculator}" /></td>
													<td><c:out value="${iaReportData.testLive}" /></td>
													<td><c:out value="${iaReportData.showResultsToStudents}" /></td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
						</c:if>
					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="../adminCommon/footer.jsp" />

<script type="text/javascript">
	const programStructureByProgramMap = document.getElementById("programStructureByProgramMap").value;
	const programStructureByProgramObject = JSON.parse(programStructureByProgramMap);
	console.log("programStructureByProgramObject: ", programStructureByProgramObject);

	$(document).ready(function() {
	    $("#iaReportTable").DataTable();

		limitTextLength(document.getElementById("testName"));
	    let programSelect = document.getElementById("programId");
		let programSelectValue = programSelect.options[programSelect.selectedIndex].value;
		console.log("programSelectValue: ", programSelectValue);

		if(programSelectValue !== "") {
			let programStructureSelect = document.getElementById("programStructureId");
			let programStructureSelectValue = programStructureSelect.options[programStructureSelect.selectedIndex].value;
			console.log("programStructureSelectValue: ", programStructureSelectValue);

			let programStructureObject = programStructureByProgramObject[programSelectValue];
			console.log("programStructureObject: ", programStructureObject);

			for(const programStructureId in programStructureObject) {
				console.log("programStructureId: ", programStructureId);
				console.log("programStructureValue: ", programStructureObject[programStructureId]);
				if(programStructureSelectValue !== programStructureId) 
					programStructureSelect.options[programStructureSelect.options.length] = new Option(programStructureObject[programStructureId], programStructureId);
			}
			
			programStructureSelect.removeAttribute("disabled");
			programStructureSelect.removeAttribute("title");
		}
	} );

	function programChange() {
		let programSelect = document.getElementById("programId");
		let programSelectValue = programSelect.options[programSelect.selectedIndex].value;
		console.log("programSelectValue: ", programSelectValue);

		let programStructureSelect = document.getElementById("programStructureId");
		let programStructureSelectOptions = programStructureSelect.querySelectorAll("option");		//Creates a nodeList of options present in #programStructureId dropdown
		[...programStructureSelectOptions].forEach(function(selectOption) {			//Using spread operator (ES6) to iterate over the nodeList
			selectOption.remove();			//deleting the option
		});
		programStructureSelect.options[0] = new Option("Select Program Structure", "", true);		//Re-creating the first Option and selecting the Option as default

		if(programSelectValue !== "") {
			const programStructureObject = programStructureByProgramObject[programSelectValue];
			console.log("programStructureObject: ", programStructureObject);

			for(const programStructureId in programStructureObject) {
				console.log("programStructureId: ", programStructureId);
				console.log("programStructureValue: ", programStructureObject[programStructureId]);
				programStructureSelect.options[programStructureSelect.options.length] = new Option(programStructureObject[programStructureId], programStructureId);
			}
			
			programStructureSelect.removeAttribute("disabled");
			programStructureSelect.removeAttribute("title");
		}
		else {
			programStructureSelect.setAttribute("disabled", true);
			programStructureSelect.setAttribute("title", "Select Program field value to unlock");
		}
	}

	/**
	* Method which checks if the text length of the options match the threshold, 
	* if the length of the text exceeds the threshold, the text is cut to match the threshold length
	* @param dropdownElement - An Element which contains Option(s)
	*/
	function limitTextLength(dropdownElement) {
		const lengthLimit = 160;		//Text length threshold
		let dropdownOptions = dropdownElement.options;		//Creates an Array Like Object of the options from the #testName dropdown
		console.log("dropdownOptions length: ", dropdownOptions.length);
		for(option of dropdownOptions) {			//Iterating the Array Like Object using for of
			if(option.textContent.length > lengthLimit) 		//If text length is greater than the threshold, the text is cut short to match the threshold
				option.textContent = option.textContent.trim().substring(0, lengthLimit - 3) + "...";
		}
	}
</script>
</body>
</html>