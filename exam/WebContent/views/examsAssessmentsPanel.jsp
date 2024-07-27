<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 


<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Add Exam Center" name="title" />
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
display:none;
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

<%@ include file="header.jsp"%>
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Exam Assessment Panel</legend></div>
        		<%@ include file="messages.jsp"%>
        		
        		
				<form method="post" action="examAssessmentPanel">
				<div class="row">
								
								<div class="col-sm-6">
								<div class="form-group">
									<label for="sel1">Acad Month</label>
									<select name="acadMonth" id="acadMonth" class="form-control" onclick="clearProgram()">
										<option value="">-- Acad Month --</option>
										<c:forEach items="${acadMonth}" var="month">
											<option value="${month}">${month}</option>
										</c:forEach>
									</select>
								</div>
								</div>
					
								<div class="col-sm-6 ">
									<div class="form-group">
										<label for="sel1">Acad Year</label>
											<select name="acadYear" id="acadYear" class="form-control" onclick="clearProgram()">
												<option value="">-- Acad Year --</option>
												<c:forEach items="${acadYear}" var="year">
												<option value="${year}">${year}</option>
												</c:forEach>
											</select>
									</div>
								</div>
					
								<div class="col-sm-6">
									<div class="form-group">
									  <label for="sel1">Select Program Type:</label>
									  <select name="programType" class="form-control programType" >
									  	<option value="">-- select Program Type --</option>
									  	<option value="MBA - WX">MBA - WX</option>
									  	<option value="M.Sc. (AI & ML Ops)">M.Sc. (AI & ML Ops)</option>
									  	<option value="M.Sc. (AI)">M.Sc. (AI)</option>
									  	<option value="Modular PD-DM">Modular PD-DM</option>
									  </select>
									</div>
								</div>
								
								<div class="col-sm-6">
									<div class="form-group">
									  <label for="sel1">Select Assessment:</label>
									  <select name="assessments_id" class="form-control assessmentList" required>
									  	<option disabled selected value="">-- select assessment --</option>
									  	<c:forEach var="assessment" items="${assessmentList}">
									  		<option data-customAssessmentName="<c:out value="${assessment.customAssessmentName}"/>" data-name="<c:out value="${assessment.name}"/>" value="<c:out value="${assessment.assessments_id}"/>"><c:out value="${assessment.name}"/></option>
									  	</c:forEach>
									  </select>
									</div>
								</div>
								
								<div class="col-sm-6">
									<div class="form-group">
									  <label for="sel1">Select Schedule:</label>
									  <select name="schedule_id" class="form-control scheduleList" required>
									  	<option disabled selected value="">-- select Schedule --</option>
									  	
									  </select>
									</div>
								</div>
								
								<div class="col-sm-6">
									<div class="form-group">
									  	<label for="sel1">Select Batch:</label>
									  	
									  <select name="batch_id" class="form-control batchList" required>
									  	<option disabled selected value="">-- select batch --</option>

									  </select>
									</div>
								</div>
								<div class="col-sm-6 subjectDiv">
									<div class="form-group">
										<label for="sel1">Select Subject:</label>
										<select name="subject" class="form-control subjectList" required>
									  		<option disabled selected value="">-- select subject --</option>
										</select>
									</div>
								</div>
								<div class="col-sm-6 subjectDiv">
									<div class="form-group">
										<label for="max_score">Select Max Score:</label>
										<select name="max_score" class="form-control" required>
-											<option  value="">-- select max_score --</option>
-									  		<option  value="30">30</option>
-									  		<option  value="40">40</option>
											<option  value="70">70</option>
-									  		<option  value="100">100</option>
										</select>
									</div>
								</div>
								<div id="hiddenFormData">
								</div>
								<div id="hiddenAssessmentFormData">
								</div>
								<input type="hidden" id="startdate" name="startDate" value=""/> 
								
							</div>
        			<button id="submitBtn" class="btn btn-primary" type="submit">Add Assessment</button>
        		
        		</form>
        		
        		
        		<br/><br/>
				<div class="table-responsive">
		 		<table id="dataTable" class="table table-striped ">
		 			<thead>
		 				<td><b>Assessments_id</b></td>
		 				<td><b>Name</b></td>
		 				<td><b>CustomAssessmentName</b></td>
		 				<td><b>Subject</b></td>
		 				<td><b>Schedule_id</b></td>
		 				<td><b>Schedule Name</b></td>
		 				<td><b>Exam Start Date</b></td>
		 				<td><b>Exam End Date</b></td>
		 				<td><b>Batch Name</b></td>
		 			</thead>
		 			<tbody>
		 				<c:forEach var="exam_assessment" items="${exam_assessments}">
		 					<tr>
		 						<td><c:out value="${exam_assessment.assessments_id}"/></td>
		 						<td><c:out value="${exam_assessment.name}"/></td>
		 						<td><c:out value="${exam_assessment.customAssessmentName}"/></td>
		 						<td><c:out value="${exam_assessment.subject}"/></td>
		 						<td><c:out value="${exam_assessment.schedule_id}"/></td>
		 						<td><c:out value="${exam_assessment.schedule_name}"/></td>
		 						<td><c:out value="${exam_assessment.exam_start_date_time}"/></td>
		 						<td><c:out value="${exam_assessment.exam_end_date_time}"/></td>
		 						<td><c:out value="${exam_assessment.batchName}"/></td>
		 					</tr>
		 				</c:forEach>
		 			</tbody>
		 		</table>
		 		</div>

		</div>
		
	
	</section>

	  <jsp:include page="footer.jsp" />
	 <script src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js"></script>
	 <script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.24.0/moment.min.js"></script>
	    <script>
	    $(document).ready( function () {
	        $('#dataTable').DataTable();
	        
	        $(document).on('click','.toggleListWell',function(){
	        	$(this).parent().children(".toggleWell").slideToggle();
	        });

	        $(document).on('click','#acadMonth,#acadYear',function(){
				var programName= $('.programType').val(); 
				if(programName != ""){
					$('.programType').val("");
				}
			});
	        
	        $(document).on('change','.programType',function(){
	        	var programType = $(this).val();
	        	var acadMonth=$('#acadMonth').val();
				var acadYear=$('#acadYear').val();
				if(programType == "" || acadMonth == "" || acadYear == ""){
					return false;
				}
				let optionsList = '<option value="" disabled selected>loading</option>';
				let self = $(this);
				$.ajax({
					url:"getAssessmentListByProgramType?programType=" + encodeURIComponent(programType),
					method:"GET",
					
					success:function(response){
						if(response.status != "success"){
							alert("Error while getting assessment data");
							return false;
						}
						response = response.mettlResponseBeans;
						optionsList = '<option disabled selected value="">-- select assessment --</option>';
						for(let i=0;i < response.length;i++){
							optionsList = optionsList + '<option value="' + response[i].assessments_id +'" data-examduration="' + response[i].duration + '"  data-name="' + response[i].name + '" data-customAssessmentName="' + response[i].name + '">'+ response[i].name + '</option>';							
						}
						$('.assessmentList').html(optionsList);
					},
					error:function(error){
						console.log(error)
						alert("Error while getting assessment data");
					}
				});
				$('.assessmentList').html(optionsList);
				getBatchList(programType,acadMonth,acadYear);
	        });
	        
	        $(document).on('change','.assessmentList',function(){
	        	var name = $(this).find(':selected').attr("data-name");
	        	var customAssessmentName = $(this).find(':selected').attr("data-customAssessmentName");
	        	var duration = $(this).find(':selected').attr("data-examduration");
	        	var HtmlFormData = '<input type="hidden" name="customAssessmentName" value="' + customAssessmentName +'"/><input name="name" type="hidden" value="' + name + '"/> <input type="hidden" name="duration" value="' + duration +'"/>';
	        	$('#hiddenAssessmentFormData').html(HtmlFormData);
	        });
	        
	        $(document).on('change','.scheduleList',function(){
	        	var schedulebuttonid = $(this).find(':selected').attr("data-schedulebuttonid");
	        	var schedule_status = $(this).find(':selected').attr("data-schedule_status");
	        	var schedule_accessurl = $(this).find(':selected').attr("data-schedule_accessurl");
	        	var schedule_accesskey = $(this).find(':selected').attr("data-schedule_accesskey");
	        	var schedule_name = $(this).find(':selected').attr("data-schedule_name");
	        	var examendsontime = $(this).find(':selected').attr("data-examendsontime");
	        	var examendsondate = $(this).find(':selected').attr("data-examendsondate");
	        	var examstartsontime = $(this).find(':selected').attr("data-examstartsontime");
	        	var examstartondate = $(this).find(':selected').attr("data-examstartondate");
	        	var exam_start_date_time = moment(examstartondate + " " + examstartsontime,"ddd, DD MMM YYYY HH:mm:ss ").format("YYYY-MM-DD HH:mm:ss");
	        	var exam_end_date_time = moment(examendsondate + " " + examendsontime).format("YYYY-MM-DD HH:mm:ss");
	        	var HtmlFormData = '<input type="hidden" name="schedule_status" value="' + schedule_status +'"/> <input type="hidden" name="schedule_accessUrl" value="' + schedule_accessurl +'"/> <input type="hidden" name="schedule_accessKey" value="' + schedule_accesskey +'"/> <input type="hidden" name="schedule_name" value="' + schedule_name +'"/> <input type="hidden" name="exam_start_date_time" value="' + exam_start_date_time +'"/> <input type="hidden" name="exam_end_date_time" value="' + exam_end_date_time +'"/>';
	        	$('#hiddenFormData').html(HtmlFormData);
	        	if(exam_start_date_time == "Invalid date" || exam_end_date_time == "Invalid date"){
	        		$('#submitBtn').attr("disabled",true);
	        		alert("Invalid Start Date or End Date Found");
	        	}else{
	        		$('#submitBtn').attr("disabled",false);
	        	}
	        	
	        	//ddd, DD MMM YYYY HH:mm:ss  
	        	//YYYY-MM-DD HH:mm:ss
	        });
	        
	        $(document).on('change','.assessmentList',function(){
	        	var assessmentId = $(this).val();
	        	var programType = $('.programType').val();
				if(assessmentId == ""){
					return false;
				}
				let optionsList = '<option value="" disabled selected>loading</option>';
				let self = $(this);
				$.ajax({
					url:"getScheduleFromAssessmentId?id=" + assessmentId + '&programType='+encodeURIComponent(programType),
					method:"GET",
					
					success:function(response){
						if(response.status != "success"){
							alert("Error while getting schedule data");
							return false;
						}
						response = response.mettlResponseBeans;
						optionsList = '<option disabled selected value="">-- select schedule --</option>';
						for(let i=0;i < response.length;i++){
							optionsList = optionsList + '<option data-examstartondate="' + response[i].startOnDate + '" data-examstartsontime="' + response[i].startsOnTime + '" data-examendsondate="' + response[i].endsOnDate + '"  data-examendsontime="' + response[i].endsOnTime + '" data-schedule_name="' + response[i].schedule_name + '"  data-schedule_accesskey="' + response[i].schedule_accessKey + '" data-schedule_accessurl="' + response[i].schedule_accessUrl + '" data-schedule_status="' + response[i].schedule_status + '" value="'+ response[i].schedule_id +'">'+ response[i].schedule_name +'</option>';
						}
						$('.scheduleList').html(optionsList);
					},
					error:function(error){
						alert("Error while getting schedule data");
					}
				});
				$('.scheduleList').html(optionsList);
	        })
	        
	        $(document).on('change','.batchList',function(){
	        	var batch = $(this).val();
				if(batch == ""){
					return false;
				}
				let optionsList = '<option value="" disabled selected>loading</option>';
				let self = $(this);
				$.ajax({
					url:"getSubjectListByBatchId?id=" + batch,
					method:"GET",
					success:function(response){
						optionsList = '<option disabled selected value="">-- select subject --</option>';
						for(let i=0;i < response.length;i++){
							var startDate  = new Date(response[i].startDate );
							optionsList = optionsList + '<option value="'+ response[i].subject +'" data-startdate="'+response[i].startDate+'" >'+ response[i].subject+'  (Start Date : '+startDate.toDateString() +') </option>';
						}
						self.parent().parent().parent().children(".subjectDiv").children(".form-group").children(".subjectList").html(optionsList);
					},
					error:function(error){
						alert("Error while getting schedule data");
					}
				});
				self.parent().parent().parent().children(".subjectDiv").children(".form-group").children(".subjectList").html(optionsList);
	        })

	        $(document).on('change','.subjectList',function(){
	        	 let startdate = $(this).find(':selected').data('startdate')
	        	 $("#startdate").val(startdate);
	        	});
	    } );

	    function getBatchList(programType,acadMonth,acadYear){
	    	  let optionsList = '<option value="" disabled selected>loading</option>';
	    	  $('.batchList').html(optionsList);
			$.ajax({
	    	 	url : '/exam/m/getActiveBatchList',
			    type : 'POST',
				contentType : "application/json",
				data : JSON.stringify({ 
					programType : programType,
					acadMonth : acadMonth,
					acadYear : acadYear
					}),
				dataType : "json",
		      success : function(response){
					let batchList = '<option disabled selected value="">-- select batch --</option>';
					for(let i=0;i < response.length;i++){
						batchList = batchList + '<option value="' + response[i].id +'" >'+ response[i].name + '</option>';
					}
					$('.batchList').html(batchList);
						
		      },error: function (result, status, err) {
			          alert("Unable to fetach Batch List !");
			          let optionsList = '<option value="" disabled selected>Unable to fetach Batch List !</option>';
			    	  $('.batchList').html(optionsList);        
			  }

		    });
			
		  }
	    </script>
</body>
</html>
