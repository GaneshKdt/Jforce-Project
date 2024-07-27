<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 
<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Add Exam Schedule" name="title" />
</jsp:include>
<head>
   		 <link rel="stylesheet" href="https://cdn.datatables.net/1.10.19/css/jquery.dataTables.min.css">
</head>
<body class="inside">
<%@ include file="header.jsp"%>
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row">
        <legend>Exam Schedule Panel</legend>
        </div>
        <%@ include file="messages.jsp"%>
        
 		<c:if test="${not empty count}">  
 		<div id="successMsg">
 		<div class="alert alert-success alert-dismissible" id="successMsgDescription">
 		Successfully ${count} students registered in portal
 		<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times; </button>
 		</div>
 		</div>
		</c:if> 
		<form method="post" action="scheduleCreation">
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
							 <select name="programType" id="programType" class="form-control" onChange="programTypeFunc(event)" required>
								<option value="">-- select Program Type --</option>
								<option value="M.Sc. (AI & ML Ops)">M.Sc. (AI & ML Ops)</option>
								<option value="M.Sc. (AI)">M.Sc. (AI)</option>
								<option value="MBA - WX">MBA - WX</option>
								<option value="Modular PD-DM">Modular PD-DM</option>
							</select>
						</div>
					</div>
	
					
					<div class="col-sm-6">
						<div class="form-group">
							<label for="sel1">Select Assessment:</label>
							 <select name="assessments_id" id="assessmentList" class="form-control" onChange='assessmentListFunc(event)' required>
								<option disabled selected value="">-- select Assessment Type --</option>
							</select>
						</div>
					</div>
					
					<div class="col-sm-6">
						<div class="form-group">
							<label for="sel1">Select Batch:</label>
							 <select name="batch_id" id="batchList" class="form-control" onChange='batchListFunc(event)' required>
								<option disabled selected value="">-- select Batch --</option>
							</select>
						</div>
					</div>
					
					<div class="col-sm-6">
						<div class="form-group">
							<label for="sel1">Select Subject:</label>
							 <select name="subject_id" id="subjectList" class="form-control" onChange='subjectListFunc(event)' required>
								<option disabled selected value="">-- select Subject --</option>
							</select>
						</div>
					</div>
					
					<div class="col-sm-6">
						<div class="form-group">
							<label for="sel1">Mettl Window Access Duration:</label>
							<select name="accessDuration" id="accessDuration" class="form-control" onChange='checkAccess(event)'>
								<option value="">Select Mettl Window Access Duration</option>
								<option value="15">15 minutes</option>
								<option value="30">30 minutes</option>
								<option value="45">45 minutes</option>
								<option value="60">60 minutes (1 hour)</option>
								<option value="90">90 minutes (1.5hour)</option>
								<option value="120">120 minutes (2 hour)</option>
								<option value="150">150 minutes (2.5 hour)</option>
								<option value="180">180 minutes (3 hour)</option>
							</select>
						</div>
					</div>
					
					<div class="col-sm-6">
						<div class="form-group">
							<label for="sel1">Select Web Proctoring:</label>
							 <select name="webProctoring" id="webProctoring" class="form-control" required>
							 	<option value="">Select Web Proctoring:</option>
								<option value="enabled">Enabled</option>
								<option value="disabled">Disabled</option>
							</select>
						</div>
					</div>
					
					<!-- <div class="col-sm-6">
						<div class="form-group">
							<label for="sel1">Select Link Type:</label>
							 <select name="link_id" id="linkList" class="form-control" required>
								<option disabled selected value="">-- select Link Type --</option>
								<option value="Final">Final</option>
								<option value="Test">Test</option>
							</select>
						</div>
					</div> -->
					
					<div class="col-sm-6">
						<div class="form-group">
							<label for="sel1">Select Max Marks:</label>
							 <select name="max_score" id="markList" class="form-control" required>
								<option disabled selected value="">-- select Max Marks --</option>
								<option value="30">30</option>
								<option value="40">40</option>
								<option value="70">70</option>
								<option value="100">100</option>
							</select>
						</div>
					</div>
					
					<div class="col-sm-6">
						<div class="form-group">
							<label for="sel1">Select Waiting Room:</label>
							 <select name="waitingRoom" id="waitingRoom" class="form-control" onChange='checkWaitingRoom(event)' required>
								<option disabled selected value="">-- Select Waiting Room --</option>
								<option value="enabled">Enabled</option>
								<option value="disabled">Disabled</option>
							</select>
						</div>
					</div>
					
					<div class="col-sm-6">
						<div class="form-group">
							<label for="sel1">Select Slot Date:</label>
							 <input type="date" name="slotDate_id" id="slotDateList" class="form-control" required>
						</div>
					</div>
					
					<div class="col-sm-6">
						<div class="form-group">
							<label for="sel1">Select Slot Time:</label>
							 <input type="time" name="slotTime_id" id="slotTimeList" class="form-control" required>
						</div>
					</div>
					
					<div id="hiddenAssessmentFormData">
					</div>
					<div id="hiddenSubjectFormData">
					</div>
					<div id="hiddenBatchFormData">
					</div>
				</div>
				<button id="submitBtn" class="btn btn-primary" type="submit">Add Schedule</button>
		</form>
		
		<br/><br/>
		<c:if test="${downloadExcel eq true}">  
   			<a  href="downloadFailedRegistrationsList"  class="btn btn-large btn-primary"  id="btnDownload">Download Error Records Excel </a>  
		</c:if>  
		<br/><br/>
		<c:if test="${successList eq true}">  
   			<a  href="downloadSuccessRegistrationsList"  class="btn btn-large btn-primary"  id="btnDownload">Download Success Records Excel </a>  
		</c:if> 
		<br/><br/>
		</div>
	</section>
	 <jsp:include page="footer.jsp" />
	 <script src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js"></script>
	 <script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.24.0/moment.min.js"></script>
	<script>
	function clearProgram()
	{
		if(document.getElementById("programType").value!=""){
			document.getElementById("programType").value="";
		}
	}
	function checkWaitingRoom(e)
	{
		var waitingRoom = e.target.value;
		console.log(waitingRoom)
		if(waitingRoom == ""){
			return false;
		}
		else if(waitingRoom=="enabled"){
			document.getElementById("accessDuration").disabled=true;
			document.getElementById("accessDuration").required=false;
		}
		else if(waitingRoom=="disabled"){
			document.getElementById("accessDuration").disabled=false;
			document.getElementById("accessDuration").required=true;
		}
	}

	function checkAccess(e)
	{
		var access = e.target.value;
		if(access!=""){
			optionsList = '<option value="disabled" selected>Disabled</option>';
	        document.getElementById("waitingRoom").innerHTML=optionsList;
			//document.getElementById("waitingRoom").readOnly=true;
			document.getElementById("waitingRoom").required=false;
		}
		if(access==""){
			optionsList = '<option value="enabled" selected>Enabled</option>';
	        document.getElementById("waitingRoom").innerHTML=optionsList;
			//document.getElementById("waitingRoom").disabled=false;
			document.getElementById("waitingRoom").required=true;
		}
	}
	
	function programTypeFunc(e)
	{
		var programType=e.target.value;
		var acadMonth=document.getElementById("acadMonth").value;
		var acadYear=document.getElementById("acadYear").value;
		if(programType == "" || acadMonth == "" || acadYear == "")
		{
			return false;
		}

 		let optionList='<option value="" disabled selected>Loading...</option>';
		document.getElementById("assessmentList").innerHTML=optionList;
		$.ajax({
			url:"getAssessmentListByProgramType?programType=" + encodeURIComponent(programType),
			method:"GET",
			success:function(response){
				if(response.status != "success")
				{
					alert("Error while getting assessment data");
					return false;
				}

				 let responseBean = response.mettlResponseBeans;
				optionList = '<option value="" disabled selected>-- select Assessment Type --</option>';
				for(let i=0;i<=responseBean.length-1;i++)
				{
					optionList= optionList + '<option value="'+responseBean[i].assessments_id+'" data-name="'+responseBean[i].name+'" data-duration="'+responseBean[i].duration+'" data-customname="'+responseBean[i].name+'">'+responseBean[i].name+'</option>';
				}
				document.getElementById("assessmentList").innerHTML=optionList;
			},
			error:function(error){
				alert("Error while getting assessment data");
				}
		})
		getBatchList(programType,acadMonth,acadYear);
	}

	function getBatchList(programType,acadMonth,acadYear)
	{
		let optionList='<option value="" disabled selected>Loading...</option>';
		document.getElementById("batchList").innerHTML=optionList;
		$.ajax({
			url:'/exam/m/getActiveBatchList',
			method:'POST',
			data:JSON.stringify({ 
				programType : programType,
				acadMonth : acadMonth,
				acadYear : acadYear
			}),
			contentType:'application/json',
			success:function(response)
			{
				optionList = '<option disabled selected value="">-- select Batch --</option>';
				for(let i=0;i < response.length;i++){
					optionList = optionList + '<option value="' + response[i].id +'"data-batchname="'+response[i].name+'" >'+ response[i].name + '</option>';
				}
				document.getElementById("batchList").innerHTML=optionList;
			},
			error:function(error)
			{
				alert("Unable to fetch Batch List !");
		        optionsList = '<option value="" disabled selected>Unable to fetch Batch List !</option>';
		        document.getElementById("batchList").innerHTML=optionList;
			}
		})
	}

	function batchListFunc(e)
	{
		var batchId=e.target.value;
		if(batchId == "")
		{
			return false;
		}
		let optionList='<option value="" disabled selected>Loading...</option>';
		document.getElementById("subjectList").innerHTML=optionList;
		$.ajax({
			url:'/exam/admin/getSubjectListByBatchId?id=' + batchId,
			method:'GET',
			success:function(response)
			{
				optionList = '<option disabled selected value="">-- select Subject --</option>';
				for(let i=0;i < response.length;i++){
					var startDateFormat = new Date(response[i].startDate)
					optionList = optionList + '<option value="' + response[i].subject +'"data-timeboundid="'+ response[i].id +'"data-startdate="'+response[i].startDate+'" >'+ response[i].subject+'  (Start Date : '+startDateFormat.toDateString() + ')</option>';
				}
				document.getElementById("subjectList").innerHTML=optionList;
			},
			error:function(error)
			{
				alert("Unable to fetch Subject List !");
		        optionsList = '<option value="" disabled selected>Unable to fetch Subject List !</option>';
		        
		        document.getElementById("subjectList").innerHTML=optionList;
			}
		})
		
		getBatchName();
	}

	function assessmentListFunc(e)
	{
		let assessment=document.getElementById("assessmentList")
		let assessmentOption= assessment.options[assessment.selectedIndex]
		let assessmentName=assessmentOption.getAttribute('data-name')
		let assessmentCustomName=assessmentOption.getAttribute('data-customname')
		let assessmentDuration=assessmentOption.getAttribute('data-duration')
		var HtmlFormData = '<input type="hidden" name="assessmentCustomName" value="' + assessmentCustomName +'"/><input name="assessmentName" type="hidden" value="' + assessmentName + '"/> <input type="hidden" name="assessmentDuration" value="' + assessmentDuration +'"/>';
		document.getElementById("hiddenAssessmentFormData").innerHTML=HtmlFormData;
	}
	
	function subjectListFunc(e)
	{
		let subject=document.getElementById("subjectList")
		let subjectOption= subject.options[subject.selectedIndex]
		let subjectTimeboundId=subjectOption.getAttribute('data-timeboundid')
		let subjectStartDate=subjectOption.getAttribute('data-startdate')
		var HtmlSubjectFormData = '<input type="hidden" name="timeboundId" value="' + subjectTimeboundId +'"/><input name="startDate" type="hidden" value="' + subjectStartDate + '"/>';
		document.getElementById("hiddenSubjectFormData").innerHTML=HtmlSubjectFormData;
	}

	function getBatchName()
	{
		let batch=document.getElementById("batchList")
		let batchOption= batch.options[batch.selectedIndex]
		let batchName=batchOption.getAttribute('data-batchname')
		var HtmlBatchData = '<input type="hidden" name="batchName" value="' + batchName +'"/>';
		document.getElementById("hiddenBatchFormData").innerHTML=HtmlBatchData;
	}
	
	function slotDateListFunc(e)
	{
		let a=e.target.value;
		console.log(a)
	}

	function slotTimeListFunc(e)
	{
		let a=e.target.value;
		console.log(a)
	}

	</script>
</body>
</html>