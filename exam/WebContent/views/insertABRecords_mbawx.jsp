<!DOCTYPE html>
<!--[if lt IE 7]>	<html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>		<html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>		<html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.beans.EMBABatchSubjectBean"%>
<<<<<<< HEAD
=======
<%@page import="org.apache.commons.lang3.StringUtils"%>
>>>>>>> branch 'feature/sec_audit2' of https://ngasce@bitbucket.org/ngasceteam/exam.git
<%@page import="com.google.gson.Gson"%>
<%@page import="java.util.List"%>
<%@page import="com.nmims.beans.MettlResponseBean"%>
<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<link rel="stylesheet"
	href="https://cdn.datatables.net/1.10.19/css/jquery.dataTables.min.css">
	
<jsp:include page="jscss.jsp">
	<jsp:param value="Insert AB Records for Online Exam" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>
	<section class="content-container">
		<div class="container-fluid customTheme">

			<div class="row">
				<legend> Insert AB Records for EMBA Online Exam </legend>
			</div>

			<%@ include file="messages.jsp"%>

			<form:form action="searchABRecordsToInsertMBAWX" method="post"
				modelAttribute="searchBean">

				<fieldset>
					<div class="panel-body">
						<div class="col-md-6 column">
							<div class="form-group">
								<label for="sel1">Acad Month</label>
									<select name="acadMonth" id="acadMonth" class="form-control" onclick="clearProgram()" >
										<option value="">-- Acad Month --</option>
										<c:forEach items="${acadMonth}" var="month">
											<option value="${month}">${month}</option>
										</c:forEach>
									</select>
							</div>
							
							<div class="form-group">
								<label for="sel1">Acad Year</label>
								<select name="acadYear" id="acadYear" class="form-control" onclick="clearProgram()" >
									<option value="">-- Acad Year --</option>
									<c:forEach items="${acadYear}" var="year">
									<option value="${year}">${year}</option>
									</c:forEach>
								</select>
							</div>
							
							<div class="form-group">
								<label for="sel1">Select Program Type:</label>
								<select name="programType" id="programType" class="form-control" onChange='programTypeFunc(event)'>
								<option value="">-- select Program Type --</option>
								<option value="M.Sc. (AI & ML Ops)">M.Sc. (AI & ML Ops)</option>
								<option value="M.Sc. (AI)">M.Sc. (AI)</option>
								<option value="MBA - WX">MBA - WX</option>
								<option value="Modular PD-DM">Modular PD-DM</option>
								</select>
							</div>
							<div class="form-group">
								<label for="sel1">Select Batch:</label>
								<select name="batchId" id="batchId" class="form-control" itemValue="${searchBean.batchId}" required>
									<option disabled selected value="">-- select Batch --</option>
								</select>
							</div>
							<div class="form-group">
								
								<label>Select Subject</label>
								<select class="form-control p-2" id="timebound_id"
									name="timebound_id" required disabled>
									<option>Select Subject</option>
								</select>
							</div>
							<div class="form-group">
								<label for="sel1">Select Exam Type:</label>
								<select name="max_marks" class="form-control" id="subject" itemValue="${searchBean.max_marks}">
									<option selected value="">-- select exam type --</option>
									<option selected value="100">Re Exam</option>
									<option selected value="30">Normal</option>
								</select>
							</div>
							<div class="form-group">
								<label class="control-label" for="submit"></label>
								<button id="submit" name="submit"
									class="btn btn-large btn-primary"
									formaction="searchABRecordsToInsertMBAWX">Search</button>
								<button id="cancel" name="cancel" class="btn btn-danger"
									formaction="examCenterHome" formnovalidate="formnovalidate">Cancel</button>
							</div>
						</div>

					</div>
				</fieldset>
			</form:form>
			<c:choose>
				<c:when test="${rowCount > 0}">
					<div class="panel-body">
						<legend>
							&nbsp;Absent Records<font size="2px"> (${rowCount} Records
								Found) &nbsp; <a href="downloadABReportMBAWX">Download AB
									report to verify before insertion</a>
							</font>
						</legend>
						<a class="btn btn-large btn-primary" href="insertABReportMBAWX">Insert
							AB Records</a>
					</div>
				</c:when>
			</c:choose>
		</div>
	</section>

	<jsp:include page="footer.jsp" />
	<script type="text/javascript">

		var batchesAndSubjects = <%=(String) request.getSession().getAttribute("batchAndSubjectListMBAWX")%>;

		$(document).on('change','#batchId',function(){

			$('#timebound_id').empty().append('<option value="">Select Subject</option>');
			
			if( $("#batchId").val() ){

				$("#timebound_id").attr("disabled", false);

				let selectedIndex = $("#batchId").val();
				batchesAndSubjects[selectedIndex].forEach((subject) => {
					console.log(subject);
					$('#timebound_id').append('<option value="' + subject.timeboundId + '">' + subject.subjectName + '</option>');
				})
				
			} else {
				$("#timebound_id").attr("disabled", true);
			}
		})
		
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
			document.getElementById("batchId").innerHTML=optionList;
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
						optionList = optionList + '<option value="' + response[i].id +'" >'+ response[i].name + '</option>';
					}
					document.getElementById("batchId").innerHTML=optionList;
				},
				error:function(error)
				{
					alert("Unable to fetch Batch List !");
			        optionsList = '<option value="" disabled selected>Unable to fetch Batch List !</option>';
			        document.getElementById("batchId").innerHTML=optionList;
				}
			})
		}

		function clearProgram()
		{
			if(document.getElementById("programType").value!=""){
				document.getElementById("programType").value="";
			}
		}
	</script>
</body>
</html>
