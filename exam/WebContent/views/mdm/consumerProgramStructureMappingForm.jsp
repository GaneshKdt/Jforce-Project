<!DOCTYPE html>
<%@page import="java.util.*"%>
<%@page import="java.text.DateFormat"%>
<html lang="en">
<%@page import="java.util.ArrayList"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@page import="com.nmims.beans.ConsumerProgramStructureExam"%>
<%@page import="java.util.ArrayList"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<style>
.panel-title .glyphicon {
	font-size: 14px;
}

.column {
	margin-bottom: 20px;
}
</style>





<%@page import="com.nmims.beans.Page"%>


<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Consumer Program Structure Mapping" name="title" />
</jsp:include>
<body>
	<%@ include file="../adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;Consumer Program Structure Mapping Entries" name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>
				<div class="sz-content-wrapper examsPage">
					<%@ include file="../adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">
						<h2 class="red text-capitalize">Add Consumer Program Structure Mapping Entries</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%@ include file="../adminCommon/messages.jsp"%>

							<%
								boolean isEdit = "true".equals((String) request.getAttribute("edit"));
								String active = (String) request.getAttribute("active");
								String hasAssignment = (String) request.getAttribute("hasAssignment");
								String assignmentNeededBeforeWritten = (String) request.getAttribute("assignmentNeededBeforeWritten");
								String writtenScoreModel = (String) request.getAttribute("writtenScoreModel");
								String assignmentScoreModel = (String) request.getAttribute("assignmentScoreModel");
								/* 	ArrayList<ConsumerProgramStructure> programSubjectList = (ArrayList<ConsumerProgramStructure>) request
											.getAttribute("ConsumerProgramStructure"); */
								/* 	ArrayList<String> programListFromProgramMaster = (ArrayList<String>) request
											.getAttribute("programListFromProgramMaster");
									ArrayList<String> progStructListFromProgramMaster = (ArrayList<String>) request
											.getAttribute("progStructListFromProgramMaster");
									ArrayList<String> semesterList = (ArrayList<String>) request.getAttribute("semesterList"); */
							%>





							<form:form  modelAttribute="ConsumerProgramStructure">
								<fieldset>

									<div class="panel-body">
										<div class="column">


											<div class="col-sm-3 column">
												<label for="consumerTypeList">Consumer Type</label>
												<form:select path="consumerTypeId" name="consumerTypeId"
													class="form-control" required="required">
													<option disabled selected value="">Select Consumer
														Type</option>
													<c:forEach var="consumerTypeList"
														items="${consumerTypeList}">
														<option value="<c:out value="${consumerTypeList.id}"/>">
															<c:out value="${consumerTypeList.name}" />
														</option>
													</c:forEach>
												</form:select>
											</div>

											<div class="col-sm-3 column">
												<label for="programStructureList">Program Structure</label>
												<form:select path="programStructureId"
													name="programStructureId" class="form-control"
													required="required">
													<option disabled selected value="">Select Program
														Structure</option>
													<c:forEach var="programStructureList"
														items="${programStructureList}">
														<option
															value="<c:out value="${programStructureList.id}"/>">
															<c:out value="${programStructureList.program_structure}" />
														</option>
													</c:forEach>
												</form:select>
											</div>

											<div class="col-sm-3 column">
												<label for="programList">Program</label>
												<form:select path="programId" name="programId"
													class="form-control" required="required">
													<option disabled selected value="">Select Program</option>
													<c:forEach var="programList" items="${programList}">
														<option value="<c:out value="${programList.id}"/>">
															<c:out value="${programList.code}" />
														</option>
													</c:forEach>
												</form:select>
											</div>

											<div class="col-sm-3 column">
												<label for="hasPaidSessionApplicable">Select Paid Sessions Applicable Or Not</label>
												<form:select path="hasPaidSessionApplicable"
													name="hasPaidSessionApplicable" class="form-control"
													required="required">
													<option disabled selected value="">Select Program
														Structure</option>
														<option value="1">Y</option>
														<option value="0">N</option>
												</form:select>
											</div>



											<div class="clearfix"></div>


											<div class="col-md-6 column">

												<button id="submit" name="submit"
													class="btn btn-large btn-primary"
													formaction="consumerProgramStructureMappingData">Add
													Entry</button>

												<button id="cancel" name="cancel" class="btn btn-danger"
													formaction="${pageContext.request.contextPath}/home" formnovalidate="formnovalidate">Cancel</button>
											</div>

										</div>

									</div>

								</fieldset>
							</form:form>
							
							<div class="clearfix"></div>
							<div class="column">
								<legend>
									&nbsp;Existing Consumer Program Structure Mapping Entries <font
										size="2px"> 
									</font>
								</legend>
								<div class="table-responsive">
									<table class="table table-striped table-hover tables"
									style="font-size: 12px; margin-left: 25px;">
										<thead>
											<tr>
												<th>Sr. No.</th>
												<th>Consumer Type</th>
												<th>Program Structure</th>
												<th>Program</th>
												<th>hasPaidSessionApplicable</th>


											</tr>
										</thead>
										<tbody>

											<c:forEach var="consumerProgramStructureMappingList"
												items="${consumerProgramStructureMappingList}"
												varStatus="status">
												<tr
													value="${consumerProgramStructureMappingList.id}~${consumerProgramStructureMappingList.consumerTypeId}~${consumerProgramStructureMappingList.programStructureId}~${consumerProgramStructureMappingList.programId}~${consumerProgramStructureMappingList.hasPaidSessionApplicable}">
													<td><c:out value="${status.count}" /></td>
													<td><c:out
															value="${consumerProgramStructureMappingList.name}" /></td>
													<td><c:out
															value="${consumerProgramStructureMappingList.program_structure}" /></td>
													<td><c:out
															value="${consumerProgramStructureMappingList.code}" /></td>
													<td>
													 <c:choose>
													<c:when test="${consumerProgramStructureMappingList.hasPaidSessionApplicable == 1}">
													<c:out
															value="Y" />
														</c:when>
													<c:otherwise>
													<c:out
															value="N" />
													</c:otherwise>
													</c:choose>
													</td>

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


<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/jquery-ui.min.js"></script>
<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery.tabledit.js"></script>

<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js"></script>



<script>
$(document).ready(function(){




	 var id="";
	 $(".tables").on('click','tr',function(e){
		    e.preventDefault();	
		    var str = $(this).attr('value');
		     id = str.split('~');
		  
		    
		}); 


	 
	 $(".tables").on('change','select[name="Consumer Type"]',function(e){
			id =id[0] + "~" + $(this).val() + "~" +  id[2] + "~" + id[3]+ "~" + id[4];
			$(this).parent().parent().attr("value",id);
		}); 
		
$(".tables").on('change','select[name="Program Structure"]',function(e){
	id = id[0] + "~" + id[1] + "~" + $(this).val() + "~" + id[3]+ "~" + id[4];
	$(this).parent().parent().attr("value",id);
}); 

$(".tables").on('change','select[name="Program"]',function(e){
	id =id[0] + "~" + id[1] + "~" +id[2]+ "~" + $(this).val()+ "~" + id[4];
	$(this).parent().parent().attr("value",id);
}); 


 $(".tables").on('change','select[name="hasPaidSessionApplicable"]',function(e){
	id =id[0] + "~" + id[1] + "~" +id[2]+ "~" +id[3]+ "~" + $(this).val();
	$(this).parent().parent().attr("value",id);
});

 		
	$(".tables").Tabledit({

		columns: {
		  identifier: [0, 'id'],                 
		  editable: [[1, 'Consumer Type','${consumerTypeList_tmp}'],
		          [2, 'Program Structure','${programStructureList_tmp}'],
		          [3, 'Program','${programDataList_tmp}'],
		          [4, 'hasPaidSessionApplicable','{"":"","Y":"Y","N":"N"}']
		          ]
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
					$('.tables').DataTable(); 
				},

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
					setTimeout(()=>{
						serialize['id'] = id[0];
						serialize['consumerTypeId'] = id[1];
						serialize['programStructureId'] = id[2];
						serialize['programId'] = id[3];
						serialize['hasPaidSessionApplicable'] = id[4];
						let body = JSON.stringify(serialize);

			  		 
						
						$.ajax({
							type : "POST",
							url : 'updateConsumerProgramStructureMapping',
							contentType : "application/json",
							data : body,
							dataType : "json",
							success : function(response) {
								console.log(response)

								if (response.status == "Success") {
									alert(response.message)
								} else {
									alert('Entries Failed to update. please try again!')
								}

							}
						});
					}, 100);
					

				}
	
	});
	
	

});		
		</script>

</html>