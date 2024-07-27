<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@page import="java.util.*"%>
<%@page import="com.nmims.beans.RemarksGradeBean"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!-- views/RIANVCases.jsp -->
<!DOCTYPE html>
<html lang="en">
<head>
<style>
</style>
<script>
	
</script>
<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="RemarksGrade 4" name="title" />
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
						<h2 class="red text-capitalize">RemarksGrade : RIA/NV Cases</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
						<div class = "js_result"></div>
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
														value="${remarksGradeBean.sapid}"/>
												</div>

												<div class="form-group">
													<label class="control-label" for="submit"></label>
													<div class="controls" style="margin-top: -40px">
														<button id="submit" name="submit"
															class="btn btn-large btn-primary" formaction="searchRG4">Search</button>
														<c:if test="${rowCount > 0}">
														<button data-programId="${remarksGradeBean.programId}"
															data-sapid="${remarksGradeBean.sapid}"
															data-year="${remarksGradeBean.year}"
															data-month="${remarksGradeBean.month}"
															data-sem="${remarksGradeBean.sem}" data-status="RIA"
															data-programStructureId="${remarksGradeBean.programStructureId}"
															data-studentTypeId="${remarksGradeBean.studentTypeId }"
															id="ria" type="button" style="color: white"
															class="markAllRIANV">RIA ALL</button>
														<button data-programId="${remarksGradeBean.programId}"
															data-sapid="${remarksGradeBean.sapid}"
															data-year="${remarksGradeBean.year}"
															data-month="${remarksGradeBean.month}"
															data-sem="${remarksGradeBean.sem}" data-status="NV"
															data-programStructureId="${remarksGradeBean.programStructureId}"
															data-studentTypeId="${remarksGradeBean.studentTypeId }"
															class="markAllRIANV" id="nv" type="button"
															style="color: white">NV ALL</button>
														<button data-programId="${remarksGradeBean.programId}"
															data-sapid="${remarksGradeBean.sapid}"
															data-year="${remarksGradeBean.year}"
															data-month="${remarksGradeBean.month}"
															data-sem="${remarksGradeBean.sem}" data-status="ATTEMPTED"
															data-programStructureId="${remarksGradeBean.programStructureId}"
															data-studentTypeId="${remarksGradeBean.studentTypeId }"
															class="markAllRIANV" id="scoreall" type="button"
															style="color: white">SCORE ALL</button>
														</c:if>
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
													<form:select id="sem" path="sem" placeholder="Semester"
														class="form-control" value="${remarksGradeBean.sem}">
														<form:option value="">Select Semester</form:option>
														<form:options items="${semList}" />
													</form:select>
												</div>
											</div>

											<div class="col-md-4">
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
<c:if test="${rowCount > 0}">
									<h2>
										&nbsp;Search Results<!-- <font size="2px"> (0 Records
											Found)&nbsp;<a href="downloadStudentMarksResults">Download
												to Excel</a>
										</font> -->
									</h2>
									<div class="clearfix"></div>
									<div class="panel-content-wrapper">
										<div class="table-responsive">
											<table class="table table-striped table-hover"
												style="font-size: 12px">
												<thead>
													<tr>
														<th>Sr. No.</th>
														<th>Exam Year</th>
														<th>Exam Month</th>
														<th>SAP ID</th>
														<th>Student Name</th>
														<th>Subject</th>
														<th>Sem</th>
														<th>Program</th>
														<th>Program Structure</th>
														<th>Student Type</th>
														<th>Written Score</th>
														<th>Assign Score</th>
														<th>Total Score</th>
														<th>Actions</th>
													</tr>
												</thead>
												<tbody>
												<c:forEach var="d" items="${dataList}" varStatus="statusOld">
												<tr>
													<td><c:out value="${statusOld.count}" /></td>
													<td><c:out value="${d.year}" /></td>
													<td><c:out value="${d.month}" /></td>
													<td><c:out value="${d.sapid}" /></td>
													<td><c:out value="${d.name}" /></td>
													<td><c:out value="${d.subject}" /></td>
													<td><c:out value="${d.sem}" /></td>
													<td><c:out value="${d.program}" /></td>
													<td><c:out value="${d.programStructure}" /></td>
													<td><c:out value="${d.studentType}" /></td>
													<td><c:out value="${d.scoreWritten}" /></td>
													<td class = "score" data-count = ${statusOld.count}><c:out value="${d.scoreIA}" /></td>
													<td><c:out value="${d.scoreTotal}" /></td>
													<td>
													<c:choose>
														<c:when test="${d.status eq 'RIA'}">
														   RIA <input checked   data-program="${d.program}" data-sapid="${d.sapid}" 
														   data-year="${d.year}" data-month="${d.month}" 
														   data-subject="${d.subject}"  data-sem="${d.sem}" 
														   data-studentType="${d.studentType}" data-programstructure="${d.programStructure}" 
														   type="radio"  value="RIA" name = "markstatus${statusOld.count}" class = "markSingleSubjectRIANV" /> <br>
														</c:when>
														<c:otherwise>
														    RIA <input data-program="${d.program}" data-sapid="${d.sapid}" 
														    data-year="${d.year}" data-month="${d.month}" 
														    data-subject="${d.subject}"  data-sem="${d.sem}" 
														    data-studentType="${d.studentType}" data-programstructure="${d.programStructure}"
														    type="radio"  value="RIA" name = "markstatus${statusOld.count}" class = "markSingleSubjectRIANV" /> <br> 
														</c:otherwise>
													</c:choose>
													<c:choose>
														<c:when test="${d.status eq 'NV'}">
														   NV <input checked   data-program="${d.program}" data-sapid="${d.sapid}" 
														   data-year="${d.year}" data-month="${d.month}" 
														   data-subject="${d.subject}"  data-sem="${d.sem}" 
														   data-studentType="${d.studentType}" data-programstructure="${d.programStructure}"
														   type="radio"  value="NV" name = "markstatus${statusOld.count}" class = "markSingleSubjectRIANV" /> <br>
														</c:when>
														<c:otherwise>
														    NV <input data-program="${d.program}" data-sapid="${d.sapid}" 
														    data-year="${d.year}" data-month="${d.month}" 
														    data-subject="${d.subject}"  data-sem="${d.sem}" 
														    data-studentType="${d.studentType}" data-programstructure="${d.programStructure}"
														    type="radio"  value="NV" name = "markstatus${statusOld.count}" class = "markSingleSubjectRIANV" /> <br> 
														</c:otherwise>
													</c:choose>
													<c:choose>
														<c:when test="${d.status eq 'ATTEMPTED'}">
														   SCORE <input checked   data-program="${d.program}" data-sapid="${d.sapid}" 
														   data-year="${d.year}" data-month="${d.month}" 
														   data-subject="${d.subject}"  data-sem="${d.sem}" 
														   data-studentType="${d.studentType}" data-programstructure="${d.programStructure}"
														   type="radio"  value="ATTEMPTED" name = "markstatus${statusOld.count}" class = "markSingleSubjectRIANV" /> <br>
														</c:when>
														<c:otherwise>
														    SCORE <input data-program="${d.program}" data-sapid="${d.sapid}" 
														    data-year="${d.year}" data-month="${d.month}" 
														    data-subject="${d.subject}"  data-sem="${d.sem}" 
														    data-studentType="${d.studentType}" data-programstructure="${d.programStructure}"
														    type="radio"  value="ATTEMPTED" name = "markstatus${statusOld.count}" class = "markSingleSubjectRIANV" /> <br> 
														</c:otherwise>
													</c:choose>
													</td>
												</tr>
												</c:forEach>
												</tbody>
											</table>
										</div>
									</div>
</c:if>
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
$('.markSingleSubjectRIANV').click(function(){
	
	var conf = confirm('Are you sure you want to edit this?');
	if(conf == true) {
		$('.js_result').html('<h3>loading...</h3>');
		if(this.checked) {
			var self = $(this);
	        $.ajax({
	            type: "POST",
	            url: 'singleUpdateRIANV',
	            data: {
	            	'status' : $(this).attr('value'),
	            	'year' : $(this).attr('data-year'),
	            	'month' : $(this).attr('data-month'),
	            	'sapid' : $(this).attr('data-sapid'),
	            	'sem' : $(this).attr('data-sem'),
	            	'studentType' : $(this).attr('data-studentType'),
	            	'programStructure' : $(this).attr('data-programstructure'),
	            	'program' : $(this).attr('data-program'),
	            	'subject' : $(this).attr('data-subject'),
	            },
	            success:function(response){
	            	if(response.Status == "Success"){
	            		if(response.marksStatus == "RIA"){
	            			$('.js_result').html('<div class="alert alert-success alert-dismissible"> <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a> <strong>Success!</strong> updated result status to RIA. </div>');
	            		 self.parents('tr').children('.score').html('RIA');
	            		}
						if(response.marksStatus == "NV"){
							$('.js_result').html('<div class="alert alert-success alert-dismissible"> <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a> <strong>Success!</strong> updated result status to NV. </div>');
							 self.parents('tr').children('.score').html('NV');
	            		}
						if(response.marksStatus == "ATTEMPTED"){
							$('.js_result').html('<div class="alert alert-success alert-dismissible"> <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a> <strong>Success!</strong> updated result status to SCORE. </div>');
							 self.parents('tr').children('.score').html('ATTEMPTED');
	            		}
						if(response.marksStatus != "NV" && response.marksStatus != "RIA" && response.marksStatus != "ATTEMPTED"){
							$('.js_result').html('<div class="alert alert-success alert-dismissible"> <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a> <strong>Success!</strong> updated all result status to marks. </div>');
							 self.parents('tr').children('.score').html(response.marksStatus);
	            		}
	            		
	            	}else{
	            		$('.js_result').html('<div class="alert alert-danger alert-dismissible"> <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a> <strong>Failed!</strong> update of result status. </div>');
	            	}
	           },
	           error:function(){
	        	   $('.js_result').html('<div class="alert alert-danger alert-dismissible"> <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a> <strong>Failed!</strong> Server Error Found. </div>');
	           }
	        });

		}
	} else {
		this.checked = false;
		var return_score = $(this).parent().parent().find('.score');
		var status_count = return_score.attr('data-count');
		$('input[type="radio"][name = "markstatus"+status_count][value = "'+ return_score.html() +'"]').prop('checked', true);
   		console.log($(this).parent().parent().find('.score').html());
	}
});

$('.markAllRIANV').click(function(){
	var conf = confirm('Are you sure you want to edit these?');
	if(conf == true){
		$('.js_result').html('<h3>loading...</h3>');
		var self = $(this);
       	$.ajax({
          type: "POST",
          url: 'multipleUpdateRIANV',
          data: {
          	'status' : $(this).attr('data-status'),
          	'year' : $(this).attr('data-year'),
          	'month' : $(this).attr('data-month'),
          	'sapid' : $(this).attr('data-sapid'),
          	'sem' : $(this).attr('data-sem'),
          	'studentType' : $(this).attr('data-studentTypeId'),
        	'programStructure' : $(this).attr('data-programStructureId'),
          	'program' : $(this).attr('data-programId'),
          	'subject' : '',
          }, 
          success:function(response){
	          	if(response.Status == "Success"){
	          		if(response.marksStatus == "RIA"){
	          			$('.js_result').html('<div class="alert alert-success alert-dismissible"> <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a> <strong>Success!</strong> updated all result status to RIA. </div>');
	           		$('.score').html('RIA');
	           		$("input[type='radio'][value = 'NV']").removeAttr('checked');
	           		$("input[type='radio'][value = 'RIA']").prop('checked',true);
	           		$("input[type='radio'][value = 'ATTEMPTED']").removeAttr('checked');
	          	}
				if(response.marksStatus == "NV"){
					$('.js_result').html('<div class="alert alert-success alert-dismissible"> <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a> <strong>Success!</strong> updated all result status to NV. </div>');
					$('.score').html('NV');
					$("input[type='radio'][value = 'RIA']").removeAttr('checked');
					$("input[type='radio'][value = 'NV']").prop('checked',true);
	           		$("input[type='radio'][value = 'ATTEMPTED']").removeAttr('checked');
	          	}
				if(response.marksStatus == "ATTEMPTED"){
					$('.js_result').html('<div class="alert alert-success alert-dismissible"> <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a> <strong>Success!</strong> updated all result status to SCORE. </div>');
					$('.score').html('SCORE');
					$("input[type='radio'][value = 'RIA']").removeAttr('checked');
					$("input[type='radio'][value = 'NV']").removeAttr('checked');
					$("input[type='radio'][value = 'ATTEMPTED']").prop('checked',true);
	          	}
          	}else{
          		$('.js_result').html('<div class="alert alert-danger alert-dismissible"> <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a> <strong>Failed!</strong> update of all result status. </div>');
          	}
         },
         error:function(){
      	   $('.js_result').html('<div class="alert alert-danger alert-dismissible"> <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a> <strong>Failed!</strong> Server Error Found. </div>');
         }
      });
	}else{
		$(this).parent().children('.score').html();
		console.log($(this).parent().children('.score').html());
		this.checked = false;
		
	}
});

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

$('#year').on('change', function(){ hideButtons(); });
$('#month').on('change', function(){ hideButtons(); });
$('#sapid').on('change', function(){ hideButtons(); });
$('#sem').on('change', function(){ hideButtons(); });
$('#programId').on('change', function(){ hideButtons(); });

function hideButtons() {
	if($('#ria').length) {
		$('#ria').hide();
		console.log('Hiding/Disabling RIA ALL buttons');
	} else {
		console.log('not Hiding/Disabling RIA ALL buttons');
	}
	if($('#nv').length) {
		$('#nv').hide();
		console.log('Hiding/Disabling NV ALL buttons');
	} else {
		console.log('not Hiding/Disabling NV ALL buttons');
	}
	if($('#scoreall').length) {
		$('#scoreall').hide();
		console.log('Hiding/Disabling SCORE ALL buttons');
	} else {
		console.log('not Hiding/Disabling SCORE ALL buttons');
	}
}
</script>
</html>