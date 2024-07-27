<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->
<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Mba-WX Exam Dashboard" name="title" />
</jsp:include>
<head>
<link rel="stylesheet"
	href="https://cdn.datatables.net/1.10.19/css/jquery.dataTables.min.css">
	<style>
	.mettlContainer {
  font-family: arial;
  font-size: 24px;
  margin: 25px;
  min-width: 400px;
  height: 200px;
  /* Center child horizontally*/
  display: flex;
  justify-content: center;
}

.mettlChild {
  width: 350px;
    height: 150px;
    margin: inherit;
  background-color: red;
  border-radius:10px;
}

.mettlChild > h4 {
	text-align:center;
	color: #FFFFFF;
}

.mettlChild > h2 {
	text-align:center;
	margin-top: 10%;
	color: #FFFFFF;
}
	
</style>
</head>
<body class="inside">
	<%@ include file="header.jsp"%>
	<section class="content-container login">
		<div class="container-fluid customTheme">
			<div class="row">
				<legend>Mba-WX Exam Dashboard</legend>
			</div>

			<form method="post">
				<div class="row">

					<div class="col-sm-6">
						<div class="form-group">
							<label for="sel1">Program Type</label> 
							<select name="programType" id="programType" class="form-control" required>
								<option value="">-- Program Type --</option>
								<option value="M.Sc. (AI & ML Ops)">M.Sc. (AI & ML Ops)</option>
								<option value="M.Sc. (AI)">M.Sc. (AI)</option>
								<option value="MBA - WX">MBA - WX</option>
								<option value="Modular PD-DM">Modular PD-DM</option>
							</select>
						</div>
					</div>
					<div class="col-sm-6">
						<div class="form-group">
							<label for="sel1">Exam Type</label> 
							<select name="examType" id="examType" class="form-control"  required>
								<option value="">-- Exam Type --</option>
								<option value="40">Regular Exam</option>
								<option value="100">Re-Exam(100 marks)</option>
							</select>
						</div>
					</div>
					<div class="col-sm-6">
						<div class="form-group">
							<label for="sel1">Select Exam Date:</label>
							 <input type="date" name="examDate" id="examDate" class="form-control" onChange='examTimeListOnExamDate(event)' onclick="examTimeListOnExamDate(event)" required>
						</div>
					</div>
					<div class="col-sm-6">
						<div class="form-group">
							<label for="sel1">Select Exam Time:</label>
							 <select name="examTime" id="timeId" class="form-control" required>
								<option disabled selected value="">-- select exam time --</option>
							</select>
						</div>
					</div>
				</div>
				<button id="submitBtn" class="btn btn-primary" type="submit" onclick="getExamData(event)">Get Status</button>
				<div id='theImg' style="display:none">
					<img  src='/exam/resources_2015/gifs/loading-29.gif' alt="Loading..." style="height:40px" />
				</div>
			</form>
			<div class="mettlContainer">
			<div class="mettlChild" style="background-color: #FF5733 !important;">
				<h4>Redirected To Mettl</h4>
				<h2 id="portal_started" class=''>0</h2>
			</div>

			<div class="mettlChild"
				style="background-color: rgb(0, 132, 205) !important;">
				<h4>Exam Started</h4>
				<h2 id="mettl_started" class=''>0</h2>
			</div>

			<div class="mettlChild" style="background-color: #0CBF83 !important;">
				<h4>Exam Completed</h4>
				<h2 id="mettl_completed" class=''>0</h2>
			</div>

			<div class="mettlChild" style="background-color: #FFBD00 !important;">
				<h4>No Action</h4>
				<h2 id="no_action" class=''>0</h2>
			</div>
			</div>
		</div>
	</section>	
	 <jsp:include page="footer.jsp" />
	 <script>
		function getExamData(event){
			event.preventDefault();
			loadDashboardData();
		}	

		function loadDashboardData(){

			var programType = document.getElementById("programType").value;
			var examType = document.getElementById("examType").value;
			var examDate = document.getElementById("examDate").value;
			var examTime = document.getElementById("timeId").value;
			if(programType == "" || examType == "" || examDate == "" || examTime=="")
			{
				alert("Please Provide All Input")
				return false;
			}
			hideSubmitBtn();
			$.ajax({
				url:"/exam/m/mbaWxExamDashboardMettl?programType=" + encodeURIComponent(programType) + "&examType=" + examType + "&examDate=" + examDate + "&examTime=" + examTime,
				method:'POST',
				contentType:'application/json',
				success:function(response)
				{
					showSubmitBtn();
					document.getElementById("portal_started").innerHTML=response.portal_started;
					document.getElementById("mettl_started").innerHTML=response.mettl_started;
					document.getElementById("mettl_completed").innerHTML=response.mettl_completed;
					document.getElementById("no_action").innerHTML=response.no_action;
					document.getElementById("programType").value=response.programType;
					document.getElementById("examType").value=response.examType;
					document.getElementById("examDate").value=response.examDate;
					document.getElementById("timeId").value=response.examTime;
				},
				error:function(error)
				{
					showSubmitBtn();
					console.log(error)
					alert("Unable to refresh Status for program:!"+programType);
				}
			})
		}

		function examTimeListOnExamDate(e){

			var examDate = e.target.value;
			console.log(examDate)
			var examType = document.getElementById("examType").value;
			let optionList = '<option disabled selected value="">-- select exam time --</option>';
			
			if(examType=="40"){
				optionList = optionList + '<option value="'+examDate+' 11:00:00">'+examDate+' 11:00:00</option>'
				document.getElementById("timeId").innerHTML=optionList;
			}
			else{
				optionList = '<option disabled selected value="">Loading...</option>';	
				document.getElementById("timeId").innerHTML=optionList;
				$.ajax({
					url:"/exam/m/getExamTimeByExamDate?examDate=" + examDate,
					method:"GET",
					success:function(response){
						console.log(response)
						optionList = '<option disabled selected value="">-- select exam time --</option>';
						for(let i=0;i<=response.length-1;i++)
						{
							optionList= optionList + '<option value="'+response[i]+'">'+response[i]+'</option>';
						}
						document.getElementById("timeId").innerHTML=optionList;
					},
					error:function(error){
						alert("Error while getting exam date");
						}
				})
			}
				
			
		}
	
			function showSubmitBtn() {
				document.getElementById("theImg").style.display="none";
				document.getElementById("submitBtn").style.display="block";
			}
			function hideSubmitBtn() {
				document.getElementById("submitBtn").style.display="none";
				document.getElementById("theImg").style.display="block";
			}
		</script>
</body>
</html>