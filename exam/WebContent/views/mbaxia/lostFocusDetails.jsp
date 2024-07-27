
<!DOCTYPE html>
<html lang="en">

<%@page import="com.nmims.beans.Person"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="MBAX IA Lost focus details" name="title" />
</jsp:include>


<link
	href="https://gitcdn.github.io/bootstrap-toggle/2.2.2/css/bootstrap-toggle.min.css"
	rel="stylesheet">

<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/chosen/1.5.1/chosen.min.css">
<script src="https://code.jquery.com/jquery-3.3.1.js"
	integrity="sha256-2Kok7MbOyxpgUVvAk/HJ2jigOSYS2auK4Pfzbm7uH60="
	crossorigin="anonymous"></script>
<script
	src="https://gitcdn.github.io/bootstrap-toggle/2.2.2/js/bootstrap-toggle.min.js"></script>


<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/selectize.js/0.12.6/css/selectize.bootstrap3.min.css">


<style>
	input[type=datetime-local]::-webkit-inner-spin-button {
		-webkit-appearance: none;
		display: none;
	}
	
	.actionModal {
	  display: none; 
	  position: fixed; 
	  z-index: 3; 
	  padding-top: 150px; 
	  left: 0;
	  top: 0;
	  width: 100%; 
	  height: 100%; 
	  overflow: auto; 
	  background-color: rgb(0,0,0); 
	  background-color: rgba(0,0,0,0.4); 
	}
	
	.actionModal-content {
		font-family: "Open Sans";
		font-weight: 400;
		background-color: #fefefe;
		margin: auto;
		padding: 20px;
		border: 1px solid #888;
		max-height: calc(100vh - 250px);
		overflow: auto; 
		border-radius: 4px;
		font-size: 1.2em;
	}
	
	.actionModal-content b{
		font-weight: 700;
	}

</style>


<body>

	<%@ include file="../adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">


		<!-- Custom breadcrumbs as requirement is diff. Start -->
		<div class="sz-breadcrumb-wrapper">
			<div class="container-fluid">
				<ul class="sz-breadcrumbs">
					<li><a href="/exam/">Exam</a></li>
					<li><a href="/exam/mbax/ia/a/viewAllTests">Internal Assessments</a></li>
					<li><a href="/exam/mbax/ia/a/viewTestDetails?id=${testId}">Internal Assessment Details</a></li>
					<li><a href="#">MBAX IA Lost Focus Details</a></li>
				</ul>
				<ul class="sz-social-icons">
					<li><a href="https://www.facebook.com/NMIMSSCE"
						class="icon-facebook" target="_blank"></a></li>
					<li><a href="https://twitter.com/NMIMS_SCE"
						class="icon-twitter" target="_blank"></a></li>
					<!-- <li><a href="https://plus.google.com/u/0/116325782206816676798/posts" class="icon-google-plus" target="_blank"></a></li> -->

				</ul>
			</div>
		</div>
		<!-- Custom breadcrumbs as requirement is diff. End -->



		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">

				<%try{ %>

				<jsp:include page="../adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>

				<%}catch(Exception e){} %>

				<div class="sz-content-wrapper examsPage">
					<%@ include file="../adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">

						<h2 class="red text-capitalize">Lost Focus Details For : ${testName}</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">

							<%@ include file="../adminCommon/messages.jsp"%>
							<!-- Code For Page Goes in Here Start -->

							<%try{ %>
							
							<div>
								<div class="form-group" style="margin: 20px;">
									<label style="font-size: 1em">Duration: </label>
									<select id='filterDuration' class="form-control">
										<option value='0'>Select Duration</option>
									</select>
								</div>
								<div id='lostFocusDetails'> 
								</div>
							</div>

							<%}catch(Exception e){} %>

							<!-- Code For Page Goes in Here End -->
						</div>

					</div>
				</div>
			</div>
		</div>
	</div>

	<jsp:include page="../adminCommon/footer.jsp" />


	<script src="https://cdnjs.cloudflare.com/ajax/libs/chosen/1.5.1/chosen.jquery.min.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/jquery-ui.min.js"></script>
	<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
	<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/selectize.js/0.12.6/js/standalone/selectize.min.js"></script>

	
	<script type="text/javascript">

	let studentDetails = [];
	let lostFocusDetails; 
	let selectSize = 0;
	
	$(document).ready (function(){
	
		let body = {
				'testId':'${testId}'
				};
		
		$.ajax({
			type : "POST",
			contentType : "application/json",
			url : "https://ngasce-content.nmims.edu/ltidemo/api/getTestLostFocusDetails",
			data : JSON.stringify(body),
			success : function(data) {
				lostFocusDetails = data;
				try{
					
					if(data && data.length != 0){
						let	lostFocusDiv = "<div class='table-responsive'> <table class='table table-striped table-hover' id='lostFocusTabel'>   <thead> <tr> <th>Instances</th> <th>SAPID</th> <th>Duration</th> <th>IP Address</th>  </tr></thead>";
						lostFocusDiv = lostFocusDiv + " <tbody>";
						
							for(let i=0;i < data.length;i++){
								
								if( !( (new RegExp('777777')).test(data[i].sapid) ) ){
									
									studentDetails.push({
										'timeAway':moment({}).seconds(data[i].totalTimeAwayInSecs).format("mm:ss") +" min",
										'count':data[i].count,
										'sapid':data[i].sapid,
										'ipAddress':data[i].ipAddress
									});
								
									lostFocusDiv = lostFocusDiv + "<tr>";
									lostFocusDiv = lostFocusDiv + "<td>"+data[i].count+"</td>";
									lostFocusDiv = lostFocusDiv + "<td>"+data[i].sapid+"</td>";
									lostFocusDiv = lostFocusDiv + "<td><p>"+moment({}).seconds(data[i].totalTimeAwayInSecs).format("mm:ss") +" min</p></td>";
									lostFocusDiv = lostFocusDiv + "<td>"+data[i].ipAddress+"</td>";
									lostFocusDiv = lostFocusDiv + "</tr>";
	
									selectSize = data[i].totalTimeAwayInMins;
								}
							}
							
							lostFocusDiv = lostFocusDiv + "</tbody></table> <button type='button' id='sendEmail' class='btn btn-primary' style='margin:20px 10px 10px 0px;' onclick='sendEmail("+JSON.stringify(studentDetails)+")'>Send Email</button>";
							lostFocusDiv = lostFocusDiv + "<a id='downalodExcel' class='btn btn-primary' style='margin:20px 10px 10px 0px;' onclick='downloadExcel("+JSON.stringify(studentDetails)+")'>Download Excel</button> </div>" ;
							lostFocusDivContainer = "<div style='margin:20px'>"+lostFocusDiv+"</div>";

						}else{
							lostFocusDivContainer = "<div style='margin-top:20px'>There are no students with an over all duration of greater than 3 min for focus lost.</div>";
							
						}

					for(let i = 1; i<=selectSize; i++){
						$('#filterDuration').append($('<option>', {
						    value: i,
						    text: i+' minutes'
						}));
					}
	
					$('#lostFocusDetails').html(lostFocusDivContainer);
					
				}catch(err){
					console.log("Got error, Refresh Page! ");
					console.log(err);
				}
				
			},
			error : function(e) {
				console.log("ERROR: ", e);
				alert("Please Refresh The Page.")
				
			}
		});

		$('#filterDuration').on('change',function(){

			let duration = $('#filterDuration').val();
			let lostFocusDivContainer;
			let flag = false;
			let studentList = [];
			
			if(lostFocusDetails && lostFocusDetails.length != 0){
				
				let	lostFocusDiv = "<div class='table-responsive'> <table class='table table-striped table-hover' id='lostFocusTabel'>   <thead> <tr> <th>Instances</th> <th>SAPID</th> <th>Duration</th> <th>IP Address</th>  </tr></thead>";
				lostFocusDiv = lostFocusDiv + " <tbody>";
				
					for(let i=0;i < lostFocusDetails.length;i++){
						
						if(duration < lostFocusDetails[i].totalTimeAwayInMins){

							if( !( (new RegExp('777777')).test(data[i].sapid) ) ){
								
								studentList.push({
									'timeAway':lostFocusDetails[i].totalTimeAwayInMins + ":" + lostFocusDetails[i].totalTimeAwayInSecs +" min",
									'count':lostFocusDetails[i].count,
									'sapid':lostFocusDetails[i].sapid,
									'ipAddress':lostFocusDetails[i].ipAddress
								});
								
								lostFocusDiv = lostFocusDiv + "<tr>";
								lostFocusDiv = lostFocusDiv + "<td>"+lostFocusDetails[i].count+"</td>";
								lostFocusDiv = lostFocusDiv + "<td>"+lostFocusDetails[i].sapid+"</td>";
								lostFocusDiv = lostFocusDiv + "<td><p>"+ lostFocusDetails[i].totalTimeAwayInMins + ":" + lostFocusDetails[i].totalTimeAwayInSecs +" min</p></td>";
								lostFocusDiv = lostFocusDiv + "<td>"+lostFocusDetails[i].ipAddress+"</td>";
								lostFocusDiv = lostFocusDiv + "</tr>";
								flag = true;
							}
						}else if(duration == 0){

							if( !( (new RegExp('777777')).test(data[i].sapid) ) ){
								
								studentList.push({
									'timeAway':lostFocusDetails[i].totalTimeAwayInMins + ":" + lostFocusDetails[i].totalTimeAwayInSecs +" min",
									'count':lostFocusDetails[i].count,
									'sapid':lostFocusDetails[i].sapid,
									'ipAddress':lostFocusDetails[i].ipAddress
								});
								
								lostFocusDiv = lostFocusDiv + "<tr>";
								lostFocusDiv = lostFocusDiv + "<td>"+lostFocusDetails[i].count+"</td>";
								lostFocusDiv = lostFocusDiv + "<td>"+lostFocusDetails[i].sapid+"</td>";
								lostFocusDiv = lostFocusDiv + "<td><p>"+ lostFocusDetails[i].totalTimeAwayInMins + ":" + lostFocusDetails[i].totalTimeAwayInSecs +" min</p></td>";
								lostFocusDiv = lostFocusDiv + "<td>"+lostFocusDetails[i].ipAddress+"</td>";
								lostFocusDiv = lostFocusDiv + "</tr>"
								flag = true;
							}
						}
					}

					if(flag){
						lostFocusDiv = lostFocusDiv + "</tbody></table> <button type='button' id='sendEmail' class='btn btn-primary' style='margin:20px 10px 10px 0px;' onclick='sendEmail("+JSON.stringify(studentList)+")'>Send Email</button>";
						lostFocusDiv = lostFocusDiv + "<a id='downalodExcel' class='btn btn-primary' style='margin:20px 10px 10px 0px;' onclick='downloadExcel("+JSON.stringify(studentList)+")'>Download Excel</button> </div>" ;
						lostFocusDivContainer = "<div style='margin:20px'>"+lostFocusDiv+"</div>";

					}else{
						lostFocusDivContainer = "<div style='margin-top:20px'>There are no students with an over all duration of " + duration+ " minutes for focus lost.</div>";
					}
				}else{
					lostFocusDivContainer = "<div style='margin-top:20px'>There are no students with an over all duration of greater than 3 min for focus lost.</div>";
				}

			$('#lostFocusDetails').html(lostFocusDivContainer);
			
		});
	});

	function sendEmail(list){
		let action = confirm("Are you sure you want to send unfair means mail to the following students?");
		if(action){
			let body = list;
			$.ajax({
				type : "POST",
				contentType : "application/json",
				url : "/exam/m/sendEmailForUnfairMeans",   
				data : JSON.stringify(body),
				success : function(data) {
					if(data.success){
						alert(data.successMessage)
					}else{
						alert("An error occured while sending mails.")
					}
					
				},
				error : function(e) {
					console.log("ERROR: ", e);
					alert("Please Refresh The Page.")
					
				}
			});
		}else{
			console.log("in else")
		}
		
	}
	
	function downloadExcel(data){
		console.log('infunction')
		console.log(data)
		let body = data;
		$.ajax({
			type : "POST",
			contentType : "application/json",
			url : "/exam/admin/getDataForLostFocusReport",   
			data : JSON.stringify(body),
			success : function(data) {
				console.log("success");
				window.location.href = "/exam/Exam_Lost_Focus_Report";
			},
			error : function(e) {
				console.log("ERROR: ", e);
				alert("Please Refresh The Page.")
				
			}
		});
	}
	
	</script>
	
</body>
</html>