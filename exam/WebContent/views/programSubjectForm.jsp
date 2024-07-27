<!DOCTYPE html>
<%@page import="java.util.*"%>
<%@page import="java.text.DateFormat"%>
<html lang="en">
<%@page import="java.util.ArrayList"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
        <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%@page import="com.nmims.beans.ProgramSubjectMappingExamBean"%>


<style>
.panel-title .glyphicon {
	font-size: 14px;
}

.column {
	margin-bottom: 20px;
}
</style>

<%@page import="com.nmims.beans.Page"%>



<jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Program Subject" name="title" />
</jsp:include>
<body>
	<%@ include file="adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;Program Subject Entries" name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>
				<div class="sz-content-wrapper examsPage">
					<%@ include file="adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">
						<h2 class="red text-capitalize">Add Program Subject Entries</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%@ include file="adminCommon/messages.jsp"%>

							<%
								boolean isEdit = "true".equals((String) request.getAttribute("edit"));
								String active = (String) request.getAttribute("active");
								String hasAssignment = (String) request.getAttribute("hasAssignment");
								String assignmentNeededBeforeWritten = (String) request.getAttribute("assignmentNeededBeforeWritten");
								String writtenScoreModel = (String) request.getAttribute("writtenScoreModel");
								String assignmentScoreModel = (String) request.getAttribute("assignmentScoreModel");
								ArrayList<ProgramSubjectMappingExamBean> programSubjectList = (ArrayList<ProgramSubjectMappingExamBean>) request
										.getAttribute("programSubjectList");
								ArrayList<String> programListFromProgramMaster = (ArrayList<String>) request
										.getAttribute("programListFromProgramMaster");
								ArrayList<String> progStructListFromProgramMaster = (ArrayList<String>) request
										.getAttribute("progStructListFromProgramMaster");
								ArrayList<String> semesterList = (ArrayList<String>) request.getAttribute("semesterList");

								ArrayList<String> subjectList = (ArrayList<String>) request.getAttribute("subjectList");
							%>
					
					<div class="col-sm-3 column">
<label>Program Pass Score</label>
						<form:input id="passScore" path="passScore" type="number"
							placeholder="Pass Score" class="form-control" required="required" 
							value="${programSubjectMappingBean.passScore}"/>
					</div>
					
<div class="col-sm-3 column">
<label>Active Status</label>
						<form:select id="active" path="active" type="text" required="required"	placeholder="Active"
						 class="form-control"   itemValue="${programSubjectMappingBean.active}">
							 
							<form:option value="">Select Active</form:option>
							<form:option value="Y">Yes</form:option>
							<form:option value="N">No</form:option>
						</form:select>
						 
					</div> 
    			
    			<div class="col-sm-3 column">
<label>Is Internal Assessment like PDF upload/MCQ test  Applicable?</label>
						<form:select id="hasIA" path="hasIA" type="text" required="required"	placeholder="Select Internal Assessment Applicable"
						 class="form-control"   itemValue="${programSubjectMappingBean.hasIA}">
							 
							<form:option value="">Select Internal Assessment Applicable</form:option>
							<form:option value="Y">Yes</form:option>
							<form:option value="N">No</form:option>
						</form:select>
						 
					</div> 
    			<div class="col-sm-3 column">
<label>Is Assignment PDF upload Applicable?</label>
						<form:select id="hasAssignment" path="hasAssignment" type="text" required="required"	placeholder="Select Assignment Applicable"
						 class="form-control"   itemValue="${programSubjectMappingBean.hasAssignment}">
							 
							<form:option value="">Select Assignment Applicable</form:option>
							<form:option value="Y">Yes</form:option>
							<form:option value="N">No</form:option>
						</form:select>
						 
					</div> 
    			<div class="col-sm-3 column">
<label>Is Assignment MCQ test Applicable?</label>
						<form:select id="hasTest" path="hasTest" type="text" required="required"	placeholder="Select Assignment MCQ test Applicable"
						 class="form-control"   itemValue="${programSubjectMappingBean.hasAssignment}">
							 
							<form:option value="">Select Assignment MCQ test Applicable</form:option>
							<form:option value="Y">Yes</form:option>
							<form:option value="N">No</form:option>
						</form:select>
						 
					</div> 
					<div class="col-sm-3 column">
<label>Is Assignment Needed Before Written?</label>
						<form:select id="assignmentNeededBeforeWritten" path="assignmentNeededBeforeWritten" type="text" required="required"	placeholder="Active"
						 class="form-control"   itemValue="${programSubjectMappingBean.assignmentNeededBeforeWritten}">
							 
							<form:option value="">Select Status</form:option>
							<form:option value="Y">Yes</form:option>
							<form:option value="N">No</form:option>
							<form:option value="NA">Not Applicable</form:option>
						</form:select>
						 
					</div> 
					<div class="col-sm-3 column">
<label>Written Score Model</label>
						<form:select id="writtenScoreModel" path="writtenScoreModel" type="text" required="required"	placeholder="Active"
						 class="form-control"   itemValue="${programSubjectMappingBean.writtenScoreModel}">
							 
							<form:option value="">Select Written Score Model</form:option>
							<form:option value="Best">Best</form:option>
							<form:option value="Latest">Latest</form:option>
						</form:select>
						 
					</div> 
					<div class="col-sm-3 column">
<label>Assignment Score Model</label>
						<form:select id="assignmentScoreModel" path="assignmentScoreModel" type="text" required="required"	placeholder="Active"
						 class="form-control"   itemValue="${programSubjectMappingBean.assignmentScoreModel}">
							 
							<form:option value="">Select Assignment Score Model</form:option>
							<form:option value="Best">Best</form:option>
							<form:option value="Latest">Latest</form:option>
							<form:option value="NA">Not Applicable</form:option>
						</form:select>
						 
					</div> 
					
					
					<div class="col-sm-3 column">
<label>Create Case For Query?</label>
						<form:select id="createCaseForQuery" path="createCaseForQuery" type="text" required="required"	placeholder="Grace Applicable"
						 class="form-control"   itemValue="${programSubjectMappingBean.createCaseForQuery}">
							 
							<form:option value="">Case For Query</form:option>
							<form:option value="Y">Yes</form:option>
							<form:option value="N">No</form:option>
							
						</form:select>
						 
					</div> 
					
					<div class="col-sm-3 column">
<label>Assign Query To Faculty?</label>
						<form:select id="assignQueryToFaculty" path="assignQueryToFaculty" type="text" required="required"	placeholder="Grace Applicable"
						 class="form-control"   itemValue="${programSubjectMappingBean.assignQueryToFaculty}">
							 
							<form:option value="">Assign Query To Faculty</form:option>
							<form:option value="Y">Yes</form:option>
							<form:option value="N">No</form:option>
							
						</form:select>
						 
					</div> 
					
					
					<div class="col-sm-3 column">
<label>Is Grace Applicable?</label>
						<form:select id="isGraceApplicable" path="isGraceApplicable" type="text" required="required"	placeholder="Grace Applicable"
						 class="form-control"   itemValue="${programSubjectMappingBean.isGraceApplicable}">
							 
							<form:option value=""> Grace Applicable</form:option>
							<form:option value="Y">Yes</form:option>
							<form:option value="N">No</form:option>
							
						</form:select>
						 
					</div> 
					
					<div class="col-sm-3 column">
						<label>Maximum Grace Marks</label>
						<form:input id="maxGraceMarks" path="maxGraceMarks" type="number"
							placeholder="Max Grace Marks" class="form-control" required="required" 
							value="${programSubjectMappingBean.maxGraceMarks}"/> 
					</div> 
					
					<div class="col-sm-3 column">
							<label>Sify Subject Code</label>
							<form:input id="sifySubjectCode" path="sifySubjectCode" type="number"
							placeholder="Sify Subject Code" class="form-control" required="required" 
							value="${programSubjectMappingBean.sifySubjectCode}"/>
					</div>
					
					<div class="col-sm-3 column">
					<label>Student Type</label>
						<form:select id="studentType" path="studentType" type="text" required="required" placeholder="Student Type"
						 class="form-control"   itemValue="${programSubjectMappingBean.studentType}" >
							 
							<form:option value=""> Student Type</form:option>
							<form:option value="Regular">Regular</form:option>
							<form:option value="Timebond">Timebond</form:option>
							
						</form:select>
					</div>
					
    			<div class="clearfix"></div>
    			
    									
				<div class="col-md-6 column">
					<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="programSubjectFormData">Add Entry</button>
					<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
				</div>

							<form:form modelAttribute="programSubjectMappingBean">
								<fieldset>

									<div class="panel-body">
										<div class="column">

											<div class="col-sm-3 column">

												<label for="consumerType">Consumer Type</label>
												<form:select data-id="consumerTypeDataId" id="consumerType"
													path="consumerType" name="consumerType"
													class="form-control" required="required">
													<option disabled selected value="">Select Consumer
														Type</option>
													<c:forEach var="consumerType" items="${consumerType}">
														<option value="<c:out value="${consumerType.name}"/>">
															<c:out value="${consumerType.name}" />
														</option>
													</c:forEach>
												</form:select>
											</div>


											<div class="col-sm-3 column">

												<label>Program Structure</label>

												<form:select id="prgmStructApplicable"
													path="prgmStructApplicable" type="text" required="required"
													placeholder="Select Program Structure"
													data-id="programStructureDataId" class="form-control"
													itemValue="${programSubjectMappingBean.prgmStructApplicable}">
													<option disabled selected value="">Select Program
														Structure</option>
												</form:select>
											</div>



											<div class="col-sm-3 column">

												<label>Program Name</label>

												<form:select data-id="programDataId" id="program"
													path="program" type="text" required="required"
													placeholder="Select Program Name" class="form-control"
													itemValue="${programSubjectMappingBean.program}">
													<option disabled selected value="">Select Program
														Name</option>
												</form:select>
											</div>





											<div class="col-sm-3 column">
												<label>Semester</label>

												<form:select id="sem" path="sem" type="text"
													required="required" placeholder="Select Semester"
													class="form-control"
													itemValue="${programSubjectMappingBean.sem}">

													<form:option value="">Select Semester</form:option>
													<form:options items="${semesterList}" />
												</form:select>

											</div>

											<div class="col-sm-3 column">
												<label>Subject Name</label>
												<form:select id="subject" path="subject" type="text"
													required="required" placeholder="Select Subject"
													class=" form-control"
													itemValue="${programSubjectMappingBean.subject}">
													<form:option value=""> Select Subject</form:option>
													<form:options items="${subjectList}" />

												</form:select>
											</div>

											<div class="col-sm-3 column">
												<label>Program Pass Score</label>
												<form:input id="passScore" path="passScore" type="number"
													placeholder="Pass Score" class="form-control"
													value="${programSubjectMappingBean.passScore}" />
											</div>

											<div class="col-sm-3 column">
												<label>Active Status</label>
												<form:select id="active" path="active" type="text"
													required="required" placeholder="Active"
													class="form-control"
													itemValue="${programSubjectMappingBean.active}">

													<form:option value="">Select Active</form:option>
													<form:option value="Y">Yes</form:option>
													<form:option value="N">No</form:option>
												</form:select>

											</div>

											<div class="col-sm-3 column">
												<label>Is Assignment Applicable?</label>
												<form:select id="hasAssignment" path="hasAssignment"
													type="text" 
													placeholder="Select Assignment Applicable"
													class="form-control"
													itemValue="${programSubjectMappingBean.hasAssignment}">

													<form:option value="">Select Assignment Applicable</form:option>
													<form:option value="Y">Yes</form:option>
													<form:option value="N">No</form:option>
												</form:select>

											</div>
											<div class="col-sm-3 column">
												<label>Is Assignment Needed Before Written?</label>
												<form:select id="assignmentNeededBeforeWritten"
													path="assignmentNeededBeforeWritten" type="text"
													 placeholder="Active"
													class="form-control"
													itemValue="${programSubjectMappingBean.assignmentNeededBeforeWritten}">

													<form:option value="">Select Status</form:option>
													<form:option value="Y">Yes</form:option>
													<form:option value="N">No</form:option>
													<form:option value="NA">Not Applicable</form:option>
												</form:select>

											</div>
											<div class="col-sm-3 column">
												<label>Written Score Model</label>
												<form:select id="writtenScoreModel" path="writtenScoreModel"
													type="text"  placeholder="Active"
													class="form-control"
													itemValue="${programSubjectMappingBean.writtenScoreModel}">

													<form:option value="">Select Written Score Model</form:option>
													<form:option value="Best">Best</form:option>
													<form:option value="Latest">Latest</form:option>
												</form:select>

											</div>
											<div class="col-sm-3 column">
												<label>Assignment Score Model</label>
												<form:select id="assignmentScoreModel"
													path="assignmentScoreModel" type="text" 
													placeholder="Active" class="form-control"
													itemValue="${programSubjectMappingBean.assignmentScoreModel}">

													<form:option value="">Select Assignment Score Model</form:option>
													<form:option value="Best">Best</form:option>
													<form:option value="Latest">Latest</form:option>
													<form:option value="NA">Not Applicable</form:option>
												</form:select>

											</div>


											<div class="col-sm-3 column">
												<label>Create Case For Query?</label>
												<form:select id="createCaseForQuery"
													path="createCaseForQuery" type="text" 
													placeholder="Grace Applicable" class="form-control"
													itemValue="${programSubjectMappingBean.createCaseForQuery}">

													<form:option value="">Case For Query</form:option>
													<form:option value="Y">Yes</form:option>
													<form:option value="N">No</form:option>

												</form:select>

											</div>

											<div class="col-sm-3 column">
												<label>Assign Query To Faculty?</label>
												<form:select id="assignQueryToFaculty"
													path="assignQueryToFaculty" type="text" 
													placeholder="Grace Applicable" class="form-control"
													itemValue="${programSubjectMappingBean.assignQueryToFaculty}">

													<form:option value="">Assign Query To Faculty</form:option>
													<form:option value="Y">Yes</form:option>
													<form:option value="N">No</form:option>

												</form:select>

											</div>


											<div class="col-sm-3 column">
												<label>Is Grace Applicable?</label>
												<form:select id="isGraceApplicable" path="isGraceApplicable"
													type="text" 
													placeholder="Grace Applicable" class="form-control"
													itemValue="${programSubjectMappingBean.isGraceApplicable}">

													<form:option value=""> Grace Applicable</form:option>
													<form:option value="Y">Yes</form:option>
													<form:option value="N">No</form:option>

												</form:select>

											</div>

											<div class="col-sm-3 column">
												<label>Maximum Grace Marks</label>


												<form:input id="maxGraceMarks" path="maxGraceMarks"
													type="number" placeholder="Max Grace Marks"
													class="form-control" 
													value="${programSubjectMappingBean.maxGraceMarks}" />

											</div>

											<div class="col-sm-3 column">
												<label>Sify Subject Code</label>
												<form:input id="sifySubjectCode" path="sifySubjectCode"
													type="number" placeholder="Sify Subject Code"
													class="form-control" 
													value="${programSubjectMappingBean.sifySubjectCode}" />
											</div>

											<div class="col-sm-3 column">
												<label>Test Applicable</label>
												<form:select id="hasTest"
													path="hasTest" type="text" 
													placeholder="Test Applicable" class="form-control"
													itemValue="${programSubjectMappingBean.hasTest}">

													<form:option value="">Test Applicable</form:option>
													<form:option value="Y">Yes</form:option>
													<form:option value="N">No</form:option>

												</form:select>

											</div>



											<div class="clearfix"></div>


											<div class="col-md-6 column">

												<button id="submit" name="submit"
													class="btn btn-large btn-primary"
													formaction="programSubjectFormData">Add Entry</button>

												<button id="cancel" name="cancel" class="btn btn-danger"
													formaction="home" formnovalidate="formnovalidate">Cancel</button>
											</div>

										</div>
				
				<div class="clearfix"></div>
			 <div class="column">
				<legend>&nbsp;Existing Program Subjects Entries  <font size="2px"> <a href = "downloadMDM" style="color: #aa1f24" ><b>Download Program Subject Entries to Verify</b></a>  </font></legend>
				<div class="table-responsive">
				<table class="table table-striped table-hover tables" style="font-size: 12px">
						<thead>
						<tr>
							<th>Sr. No.</th>
							<th>Program</th>
							<th>Subject</th>
							<th>Sify Subject Code</th>
							<th>Sem</th>
							<th>Program Structure</th>
							<th>Active Status</th>
							<th>Pass Score</th>
							<th>Internal Assessment Applicable</th>
							<th>Assignment PDF uplaod Applicable</th>
							<th>Assignemnt MCQ test Applicable</th>
							<th>Assignment Needed Before Written</th>
							<th>Written Score Model</th>
							<th>Assignment Score Model</th>
							<th>Create Case For Query?</th>
							<th>Assign Query To Faculty?</th>
							<th>Is Grace Applicable?</th>
							<th>Max Grace Marks</th>
							<th>Actions</th>
							
						</tr>
						</thead>
						<tbody>
						
						 <c:forEach var="programSubjectList" items="${programSubjectList}"   varStatus="status">
					        <tr value="${programSubjectList.program}~${programSubjectList.subject}~${programSubjectList.prgmStructApplicable}">
					            <td ><c:out value="${status.count}" /></td>
					            <td ><c:out value="${programSubjectList.program}" /></td>
					            <td ><c:out value="${programSubjectList.subject}" /></td>
					            <td ><c:out value="${programSubjectList.sifySubjectCode}" /></td>
					            <td ><c:out value="${programSubjectList.sem}" /></td>
					            <td ><c:out value="${programSubjectList.prgmStructApplicable}" /></td>
					            <td ><c:out value="${programSubjectList.active}"/></td>
								<td ><c:out value="${programSubjectList.passScore}" /></td>
								<td ><c:out value="${programSubjectList.hasIA}" /></td>
								<td ><c:out value="${programSubjectList.hasAssignment}" /></td>
								<td ><c:out value="${programSubjectList.hasTest}" /></td>
								<td ><c:out value="${programSubjectList.assignmentNeededBeforeWritten}" /></td>
								<td ><c:out value="${programSubjectList.writtenScoreModel}" /></td>
								<td ><c:out value="${programSubjectList.assignmentScoreModel}" /></td>
								<td ><c:out value="${programSubjectList.createCaseForQuery}" /></td>
								<td ><c:out value="${programSubjectList.assignQueryToFaculty}" /></td>
								<td ><c:out value="${programSubjectList.isGraceApplicable}" /></td>
								<td ><c:out value="${programSubjectList.maxGraceMarks}" /></td>
							
					          	 <td ></td>
														
																 
					        </tr>   
					    </c:forEach>
							
						</tbody>
					</table>
				</div>
				
				
				</div> 
				
			
				
				</div>
				
			</fieldset>
		</form:form>



									</div>


							<div class="clearfix"></div>
							<div class="column">
								<legend>
									&nbsp;Existing Program Subjects Entries <font size="2px">
										<a href="downloadMDM" style="color: #aa1f24"><b>Download
												Program Subject Entries to Verify</b></a>
									</font>
								</legend>
								<div class="table-responsive">
									<table class="table table-striped table-hover tables"
										style="font-size: 12px">
										<thead>
											<tr>
												<th>Sr. No.</th>
												<th>Consumer Type</th>
												<th>Program</th>
												<th>Subject</th>
												<th>Sify Subject Code</th>
												<th>Sem</th>
												<th>Program Structure</th>
												<th>Active Status</th>
												<th>Pass Score</th>
												<th>Assignment Applicable</th>
												<th>Test Applicable</th>
												<th>Assignment Needed Before Written</th>
												<th>Written Score Model</th>
												<th>Assignment Score Model</th>
												<th>Create Case For Query?</th>
												<th>Assign Query To Faculty?</th>
												<th>Is Grace Applicable?</th>
												<th>Max Grace Marks</th>
												<th>Actions</th>

											</tr>
										</thead>
										<tbody>

											<c:forEach var="programSubjectList"
												items="${programSubjectList}" varStatus="status">
												<tr
													value="${programSubjectList.consumerType}~${programSubjectList.program}~${programSubjectList.subject}~${programSubjectList.prgmStructApplicable}~${programSubjectList.sem}">
													<td><c:out value="${status.count}" /></td>
													<td><c:out value="${programSubjectList.consumerType}" /></td>
													<td><c:out value="${programSubjectList.program}" /></td>
													<td><c:out value="${programSubjectList.subject}" /></td>
													<td><c:out
															value="${programSubjectList.sifySubjectCode}" /></td>
													<td><c:out value="${programSubjectList.sem}" /></td>
													<td><c:out
															value="${programSubjectList.prgmStructApplicable}" /></td>
													<td><c:out value="${programSubjectList.active}" /></td>
													<td><c:out value="${programSubjectList.passScore}" /></td>
													<td><c:out value="${programSubjectList.hasAssignment}" /></td>
													<td><c:out value="${programSubjectList.hasTest}" /></td>
													<td><c:out
															value="${programSubjectList.assignmentNeededBeforeWritten}" /></td>
													<td><c:out
															value="${programSubjectList.writtenScoreModel}" /></td>
													<td><c:out
															value="${programSubjectList.assignmentScoreModel}" /></td>
													<td><c:out
															value="${programSubjectList.createCaseForQuery}" /></td>
													<td><c:out
															value="${programSubjectList.assignQueryToFaculty}" /></td>
													<td><c:out
															value="${programSubjectList.isGraceApplicable}" /></td>
													<td><c:out value="${programSubjectList.maxGraceMarks}" /></td>

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
	<jsp:include page="adminCommon/footer.jsp" />


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
		 var id
		 $(".tables").on('click','tr',function(e){
			    e.preventDefault();	
			    var str = $(this).attr('value');
			     id = str.split('~');
			     console.log("---------->>>>>>>>>> id")
			    console.log(id)
			  
			    
			}); 
		$('.tables').Tabledit({

			columns: {
			  identifier: [0, 'id'],                 
			  editable: [[3, 'sifySubjectCode'],
			          [4, 'sem'],
					  [6, 'active', '{"Y": "Y", "N": "N"}'],
					  [7, 'passScore'],
					  [8, 'hasIA', '{"Y": "Y", "N": "N"}'],
					  [9, 'hasAssignment', '{"Y": "Y", "N": "N"}'],
					  [10, 'hasTest', '{"Y": "Y", "N": "N"}'],
					  [11, 'assignmentNeededBeforeWritten', '{"Y": "Y", "N": "N","NA":"NA"}'],
					  [12, 'writtenScoreModel', '{"Best": "Best", "Latest": "Latest"}'],
					  [13, 'assignmentScoreModel', '{"Best": "Best", "Latest": "Latest","NA":"NA"}'] ,
					  [14, 'createCaseForQuery', '{"Y": "Y", "N": "N"}'] ,
					  [15, 'assignQueryToFaculty', '{"Y": "Y", "N": "N"}'] ,
					  [16, 'isGraceApplicable','{"Y": "Y", "N": "N"}'],  

					  editable: [[4, 'sifySubjectCode'],
			         
					  [7, 'active', '{"":"Select Status","Y": "Y", "N": "N"}'],
					  [8, 'passScore'],
					  [9, 'hasAssignment', '{"":"Select Status","Y": "Y", "N": "N"}'],
					  [10, 'hasTest', '{"":"Select Status","Y": "Y", "N": "N"}'],
					  [11, 'assignmentNeededBeforeWritten', '{"":"Select Status","Y": "Y", "N": "N","NA":"NA"}'],
					  [12, 'writtenScoreModel', '{"":"Select Status","Best": "Best", "Latest": "Latest"}'],
					  [13, 'assignmentScoreModel', '{"":"Select Status","Best": "Best", "Latest": "Latest","NA":"NA"}'] ,
					  [14, 'createCaseForQuery', '{"":"Select Status","Y": "Y", "N": "N"}'] ,
					  [15, 'assignQueryToFaculty', '{"":"Select Status","Y": "Y", "N": "N"}'] ,
					  [16, 'isGraceApplicable','{"":"Select Status","Y": "Y", "N": "N"}'],  

					  [17, 'maxGraceMarks'],]
			},
			
		// link to server script
		// e.g. 'ajax.php'
		url: "",
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
		deleteButton: false,
		// activate save button when click on edit button
		saveButton: true,
		// activate restore button to undo delete action
		restoreButton: true,
		// custom action buttons
		// executed after draw the structure
		onDraw: function() { 

			$('.tables').DataTable( {
	        initComplete: function () {
	            this.api().columns().every( function () {
	                var column = this;
	                var headerText = $(column.header()).text();
	                console.log("header :"+headerText);
	                if(headerText == "Subject")
	                {
	                   var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
	                    .appendTo( $(column.header()) )
	                    .on( 'change', function () {
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
	              
	                if(headerText == "Program Structure")
	                {
	                   var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
	                    .appendTo( $(column.header()) )
	                    .on( 'change', function () {
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
	                
	                if(headerText == "Consumer Type")
	                {
	                   var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
	                    .appendTo( $(column.header()) )
	                    .on( 'change', function () {
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
	                  
	                            
	                               
	                if(headerText == "Program")
	                {
	                   var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
	                    .appendTo( $(column.header()) )
	                    .on( 'change', function () {
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
	              
	            } );
	        }
			
			
	    } );
			return; },

		// executed when the ajax request is completed
		// onSuccess(data, textStatus, jqXHR)
		onSuccess: function() { 

			return; },

		// executed when occurred an error on ajax request
		// onFail(jqXHR, textStatus, errorThrown)
		onFail: function() { 
return; },

		// executed whenever there is an ajax request
		onAlways: function() { return; },

		// executed before the ajax request
		// onAjax(action, serialize)
		
		onAjax: function(action, serialize) {
			serialize['consumerType'] = id[0];
			serialize['program'] = id[1];
			serialize['subject'] = id[2];
			serialize['prgmStructApplicable'] = id[3]; 
			serialize['sem'] = id[4]; 
			serialize['lastModifiedBy'] = '${userId}';
			body = JSON.stringify(serialize)

  		 $.ajax({
            type:"POST",
            url: 'updateProgramSubjectEntry',
            contentType: "application/json",
            data:body,
            dataType : "json",
            success: function(response){
            	console.log(response)
    
if(response.Status == "Success"){
	alert('Entries Saved Successfully')
}else{
 	alert('Entries Failed to update')
}
           

            }
        });
			
		}
		
		});
		/////////////////////////////////////////////////////
		/// data loading based on selection
		
		 $('#consumerType').on('change', function(){
	
	
	let id = $(this).attr('data-id');
	
	
	let options = "<option>Loading... </option>";
	$('#prgmStructApplicable').html(options);
	$('#program').html(options);
	
	
	 
	var data = {
			id:this.value
	}
console.log(this.value)
	
	console.log("===================> data id : " + id);
	$.ajax({
		type : "POST",
		contentType : "application/json",
		url : "getValueByConsumerType",   
		data : JSON.stringify(data),
		success : function(data) {
			console.log("SUCCESS Program Structure: ", data.programStructureData);
			console.log("SUCCESS Program: ", data.programData);
			
			var programData = data.programData;
			var programStructureData = data.programStructureData;
			
			
			options = "";
			
			
			//Data Insert For Program List
			//Start
			for(let i=0;i < programData.length;i++){
				
				options = options + "<option value='" + programData[i].name + "'> " + programData[i].name + " </option>";
			}
			
			
			console.log("==========> options\n" + options);
			$('#program').html(
					" <option disabled selected value=''> Select Program Name</option> " + options
			);
			//End
			options = ""; 
			
			//Data Insert For Program Structure List
			//Start
			for(let i=0;i < programStructureData.length;i++){
				
				options = options + "<option value='" + programStructureData[i].name + "'> " + programStructureData[i].name + " </option>";
			}
			
			
			console.log("==========> options\n" + options);
			$('#prgmStructApplicable').html(
					" <option disabled selected value=''> Select Program Structure </option> " + options
			);
			//End
			
			
			
			
			
		},
		error : function(e) {
			
			alert("Please Refresh The Page.")
			
			console.log("ERROR: ", e);
			display(e);
		}
	});
	
	
});
	
	///////////////////////////////////////////////////////
	
	
		$('#prgmStructApplicable').on('change', function(){
	
	
	let id = $(this).attr('data-id');
	
	
	let options = "<option>Loading... </option>";
	$('#program').html(options);
	
	
	 
	var data = {
			programStructureId:this.value,
			consumerTypeId:$('#consumerType').val()
	}
	console.log(this.value)
	
	console.log("===================> data id : " + $('#consumerType').val());
	$.ajax({
		type : "POST",
		contentType : "application/json",
		url : "getValueByProgramStructure",   
		data : JSON.stringify(data),
		success : function(data) {
			
			console.log("SUCCESS: ", data.programData);
			var programData = data.programData;
			
			
			options = "";
			
			
			//Data Insert For Program List
			//Start
			for(let i=0;i < programData.length;i++){
			
				options = options + "<option value='" + programData[i].name + "'> " + programData[i].name + " </option>";
			}
			
			
			console.log("==========> options\n" + options);
			$('#program').html(
					" <option disabled selected value=''> Select Program Structure </option> " + options
			);
			//End
			
			
			
			
			
			
		},
		error : function(e) {
			
			alert("Please Refresh The Page.")
			
			console.log("ERROR: ", e);
			display(e);
		}
	});
	
	
});
 

/////////////////////////////////////////////////////////////
		
			
		</script>
</html>