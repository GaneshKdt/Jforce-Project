<!DOCTYPE html>

<html> 

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="../jscss.jsp">
<jsp:param value="Add MBA - X Exam Schedule" name="title" />
</jsp:include>
<head>
   		 <link rel="stylesheet" href="https://cdn.datatables.net/1.10.19/css/jquery.dataTables.min.css">
</head>
<body class="inside">
<%@ include file="../header.jsp"%>
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row">
        <legend>Exam Schedule Panel</legend>
        </div>
        <%@ include file="../messages.jsp"%>
        
 		<c:if test="${not empty count}">  
 		<div id="successMsg">
 		<div class="alert alert-success alert-dismissible" id="successMsgDescription">
 		Successfully ${count} students registered in portal
 		<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times; </button>
 		</div>
 		</div>
		</c:if> 
		<form method="post" action="mbaxScheduleCreation">
				<div class="row">
								
					<div class="col-sm-6">
						<div class="form-group">
							<label for="sel1">Select Program Type:</label>
							 <select name="programType" id="programType" class="form-control" onChange='programTypeFunc(event)' disabled="disabled">
								<option value="MBA - X">MBA - X</option>
							
							</select>
						</div>
					</div>
					
					<div class="col-sm-6">
						<div class="form-group">
							<label for="sel1">Select Assessment:</label>
							 <select name="assessments_id" id="assessmentList" class="form-control" onChange='assessmentListFunc(event)' required>
								<option disabled selected value="">-- select Assessment Type --</option>
								<c:forEach items="${assesmentsResponseList}" var = "assessment">
								<option data-duration="<c:out value="${assessment.duration}"/>"  data-customAssessmentName="<c:out value="${assessment.name}"/>" data-name="<c:out value="${assessment.name}"/>" value="<c:out value="${assessment.assessments_id}"/>"><c:out value="${assessment.name}"/></option>
								</option>
								</c:forEach>
							</select>
						</div>
					</div>
					
				      <div class="col-sm-6">
                        <div class="form-group">
                            <label for="studentName">Search By Batch Name:</label>
                            <select name="batch_id" id="batchList" class="form-control" onChange="batchListFunc(event)"
                                required>
                                <option disabled selected value="">-- select Batch --</option>
                                <c:forEach items="${batchList}" var="batch">
                                    <option value="<c:out value="${batch.id}" />"
                                        data-name="<c:out value="${batch.name}" />">
                                        <c:out value="${batch.name}" /></option>
                                </c:forEach>
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
								<option value="60">60</option>
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
	
	 <jsp:include page="../footer.jsp" />
	 <script src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js"></script>
	 <script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.24.0/moment.min.js"></script>
	 <script>
        // Your other functions here

        function batchListFunc(e) {

            var batchId = e.target.value;
            if (batchId == "") {
                return false;
            }
            console.log(batchId);
            let optionList = '<option value="" disabled selected>Loading...</option>';
            document.getElementById("subjectList").innerHTML = optionList;
            $.ajax({
                url: '/exam/admin/getSubjectListByBatchId?id=' + batchId,
                method: 'GET',
                success: function (response) {
                    optionList = '<option disabled selected value="">-- select Subject --</option>';
                    for (let i = 0; i < response.length; i++) {
                        var startDateFormat = new Date(response[i].startDate);
                        optionList += '<option value="' + response[i].subject + '" data-timeboundid="'
                            + response[i].id + '" data-startdate="' + response[i].startDate + '" >'
                            + response[i].subject + '  (Start Date : ' + startDateFormat.toDateString() + ')</option>';
                    }
                    document.getElementById("subjectList").innerHTML = optionList;
                },
                error: function (error) {
                    alert("Unable to fetch Subject List !");
                    optionsList = '<option value="" disabled selected>Unable to fetch Subject List !</option>';

                    document.getElementById("subjectList").innerHTML = optionList;
                }
            })
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
    	

     
    	

    	function assessmentListFunc(e)
    	{
        	
    		let assessment=document.getElementById("assessmentList")
    		console.log("assessment",assessment);
    		let assessmentOption= assessment.options[assessment.selectedIndex]
    		let assessmentName=assessmentOption.getAttribute('data-name')
    			console.log("assessmentName",assessmentName);
    		let assessmentCustomName=assessmentOption.getAttribute('data-name')
    		let assessmentDuration=assessmentOption.getAttribute('data-duration')
    		console.log("ass",assessmentDuration);
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
        // Rest of the functions
    </script>
	
</body>
</html>