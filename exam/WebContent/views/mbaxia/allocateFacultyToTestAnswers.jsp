
<!DOCTYPE html>
<html lang="en">

<%@page import="com.nmims.beans.Person"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Allocate Faulty To MBAX Internal Assessment Answers" name="title" />
</jsp:include>


<link
	href="https://gitcdn.github.io/bootstrap-toggle/2.2.2/css/bootstrap-toggle.min.css"
	rel="stylesheet">
<script src="https://code.jquery.com/jquery-3.3.1.js"
	integrity="sha256-2Kok7MbOyxpgUVvAk/HJ2jigOSYS2auK4Pfzbm7uH60="
	crossorigin="anonymous"></script>
<script
	src="https://gitcdn.github.io/bootstrap-toggle/2.2.2/js/bootstrap-toggle.min.js"></script>

<style>
/* The snackbar - position it at the bottom and in the middle of the screen */
#snackbar {
	visibility: hidden;
	/* Hidden by default. Visible on click */
	min-width: 250px;
	/* Set a default minimum width */
	margin-left: -125px;
	/* Divide value of min-width by 2 */
	background-color: #333;
	/* Black background color */
	color: #fff;
	/* White text color */
	text-align: center;
	/* Centered text */
	border-radius: 2px;
	/* Rounded borders */
	padding: 16px;
	/* Padding */
	position: fixed;
	/* Sit on top of the screen */
	z-index: 1;
	/* Add a z-index if needed */
	left: 50%;
	/* Center the snackbar */
	bottom: 30px;
	/* 30px from the bottom */
}

/* Show the snackbar when clicking on a button (class added with JavaScript) */
#snackbar.show {
	visibility: visible;
	/* Show the snackbar */
	/* Add animation: Take 0.5 seconds to fade in and out the snackbar. 
However, delay the fade out process for 2.5 seconds */
	-webkit-animation: fadein 0.5s, fadeout 0.5s 2.5s;
	animation: fadein 0.5s, fadeout 0.5s 2.5s;
}

/* Animations to fade the snackbar in and out */
@
-webkit-keyframes fadein {from { bottom:0;
	opacity: 0;
}

to {
	bottom: 30px;
	opacity: 1;
}

}
@
keyframes fadein {from { bottom:0;
	opacity: 0;
}

to {
	bottom: 30px;
	opacity: 1;
}

}
@
-webkit-keyframes fadeout {from { bottom:30px;
	opacity: 1;
}

to {
	bottom: 0;
	opacity: 0;
}

}
@
keyframes fadeout {from { bottom:30px;
	opacity: 1;
}

to {
	bottom: 0;
	opacity: 0;
}

}
.redLeftBorder {
	padding-left: 10px;
	border-left: 5px solid #C72033;
}

.greenLeftBorder {
	padding-left: 10px;
	border-left: 5px solid #49a54e;
}

input[type=number] {
	color: black;
	width: 100%;
	padding: 12px 20px;
	margin: 8px 0;
	box-sizing: border-box;
	line-height: 20px;
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
					<li><a href="/exam/mbax/ia/a/viewTestDetails?id=${test.id}">Internal Assessment
							Details</a></li>
					<li><a href="#">Allocate Faulty To MBAX Internal Assessment Answers</a></li>

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
				<jsp:include page="../adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>


				<div class="sz-content-wrapper examsPage">
					<%@ include file="../adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">

						<h2 class="red text-capitalize">Allocate Faulty To MBAX Internal Assessment
							Answers</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper">
							<%@ include file="../adminCommon/messages.jsp"%>

							<!-- Code For Page Goes in Here Start -->
							<div class="row">
								<div class="well col-sm-12">
									<h5>Add Faculty For Allocation</h5>
									<div class="form-group">
										<div class="col-sm-6">
											<div class="col-sm-4">
												<input type="hidden" id="facultyid"
													value="${test.facultyId}" /> <select class="form-control"
													id="selectFaculty">
													<option>--Select Faculty--</option>
													<c:forEach var="i" items="${facultyList}">
														<option value="${i.facultyId}">${i.firstName}
															${i.lastName}</option>
													</c:forEach>
												</select>
											</div>
											<div class="col-sm-8">

												<button id="addFaultyButton" class="btn btn-primary "
													style="margin-top: 0px !important;">Add &amp;
													Faculty</button>

											</div>
										</div>
									</div>
								</div>
							</div>

							<div class="row">

								<div class="well col-sm-12">
									<div class="jumbotron">
										<h2>
											Answers Left To Allocate : <b id="noOfAllocationsLeft">${noOfAllocationsLeft}</b>
										</h2>
									</div>
									<div class="table-responsiv">
										<table id="fTable" class="table table-hover table-striped"
											style="font-size: 12px">
											<thead>
												<tr>
													<th>FacultyId</th>
													<th>Faculty Name</th>
													<th>Allocated Answers</th>
													<th>Allocate Answers</th>
													<th>Action</th>
													<th>Delete</th>
												</tr>
											</thead>
											<tbody>

												<c:forEach var="faculty" items="${facultyNAllocatedAnswers}"
													varStatus="status">
													<tr id="row-${typeId}">
														<td>${faculty.facultyId}</td>
														<c:set var="currentfacultyId" value="${faculty.facultyId}" />
														<c:set var="teachingfacultyId" value="${test.facultyId}" />
														<c:choose>
															<c:when test="${currentfacultyId == teachingfacultyId }">
																<td>${faculty.facutlyName}(Teaching Faculty)</td>
															</c:when>
															<c:otherwise>
																<td>${faculty.facutlyName}</td>
															</c:otherwise>
														</c:choose>
														<td id="id-${faculty.facultyId}">${faculty.allocatedAnswers}</td>
														<td><input type="number" min="1"
															id="i-${faculty.facultyId}" value="0" /></td>
														<td><a href="#"
															onclick="saveTestAnswerAllocation('${faculty.facultyId}') "
															class="btn btn-primary">Allocate</a></td>
														<td><a href="#"
															onclick="deleteTestAnswerAllocation('${faculty.facultyId}')"
															class="btn btn-warning">Delete</a></td>
													</tr>
												</c:forEach>
											</tbody>
										</table>
									</div>
								</div>
							</div>
							<!-- Code For Page Goes in Here End -->
						</div>

					</div>
				</div>
			</div>
		</div>
	</div>


	<div id="snackbar">Some text some message..</div>

	<jsp:include page="../adminCommon/footer.jsp" />
	<script type="text/javascript">
	 console.log("testId:2");

	 $(document).ready(function(){

		 var testId = ${test.id};
		 console.log("testId:"+testId);
		
		 var facultyList= new Array();
		 var rowIndex = 0;
		 var facultyIdRowIndex = new Array();


		 
	      <c:forEach var = "i" items="${facultyList}">
	      facultyList.push({
	      	 "facultyId" : "${i.facultyId}", 
	       	 "facultyName" : "${i.firstName} ${i.lastName}",
	       	 "allocatedCount" : "0"
	      });
	      </c:forEach>
	      
	      <c:forEach var="f" items="${facultyNAllocatedAnswers}"	varStatus="status">
	      facultyIdRowIndex.push("${f.facultyId}");	
	      rowIndex=rowIndex+1;
	      </c:forEach>


	      let teachingFacultyId =  $('#facultyid').val();
			 console.log('teachingFaculty Id'+teachingFacultyId);	
			  let checkFaculty =  getRowId(teachingFacultyId);
			  if(checkFaculty != null){
					console.log("Faculty Already Present!");
				}else{
				var selectedFaculty = getFacultyDetailsById(teachingFacultyId);
				addRow(selectedFaculty,1);
				facultyIdRowIndex.push(teachingFacultyId);
				console.log("Allocate No of Answers to be checked by faculty.");
				}     


				
		  
	      function getFacultyDetailsById(facultyId){
	    	  for(var i=0;i<facultyList.length;i++){
	    		  var f = facultyList[i];
	    		  if(f.facultyId == facultyId){
	    			  return f;
	    		  }
	    	  }
	    	  return null;
	      }
		 
		$("#addFaultyButton").click(function(){
			var facultyId = $("#selectFaculty").val();
			console.log("In addFaultyButton click facultyId:"+facultyId);
			var check = getRowId(facultyId);
			if(check != null){
				showSnackBar("Faculty Already Present!");
			}else{
			var selectedFaculty = getFacultyDetailsById(facultyId);
			addRow(selectedFaculty,0);
			facultyIdRowIndex.push(facultyId);
			showSnackBar("Allocate No of Answers to be checked by faculty.");
			}
		});
		
		function addRow(selectedFaculty,isTeaching) {
		    var table = document.getElementById("fTable");
		    var row = table.insertRow(facultyIdRowIndex.length+1);
		    
		    var cell0 = row.insertCell(0);
		    var cell1 = row.insertCell(1);
		    var cell2 = row.insertCell(2);
		    var cell3 = row.insertCell(3);
		    var cell4 = row.insertCell(4);
		    var cell5 = row.insertCell(5);

		    cell2.id = "id-"+selectedFaculty.facultyId;
		      
		    cell0.innerHTML = selectedFaculty.facultyId;

		    if(isTeaching == 1){
		    	cell1.innerHTML = selectedFaculty.facultyName+' (Teaching Faculty)';
		    }else{
		    	cell1.innerHTML = selectedFaculty.facultyName;	
			}	
		    cell2.innerHTML = selectedFaculty.allocatedCount;
		    cell3.innerHTML = getInputTag(selectedFaculty.facultyId);
		    cell4.innerHTML = getSaveButton(selectedFaculty.facultyId);
		    cell5.innerHTML = getDeleteButton(selectedFaculty.facultyId);
		    
		}

		function deleteRow(id) {
		    document.getElementById("fTable").deleteRow(id);
		}
		
		function getInputTag(facultyId){
			var tag ="<input type='number' min='1' id='i-"+facultyId+"' value='0'/>";
			console.log("In getInputTag got tag:"+tag);
			return tag;
		}
		function getSaveButton(facultyId){
			var tag ='<a href="#" onclick="saveTestAnswerAllocation(&quot;'+facultyId+'&quot;)" class= "btn btn-primary" >Allocate</a>';
			console.log("In getSaveButton got tag:"+tag);
			return tag;
		}
		function getDeleteButton(facultyId){
			var tag ='<a href="#" onclick="deleteTestAnswerAllocation(&quot;'+facultyId+'&quot;)" class= "btn btn-warning" >Delete</a>';
			console.log("In getDeleteButton got tag:"+tag);
			
			return tag;
		}

		/* $('input[type="number"]').on('focusin', function(){
		      console.log("Saving value " + $(this).val());  
		    $(this).data('val', $(this).val());
		}); */

		$(document).on('focusin', 'input[type="number"]', function(){
		    console.log("Saving value " + $(this).val());
		    $(this).data('val', $(this).val());
		});
		
		window.saveTestAnswerAllocation = function saveTestAnswerAllocation(facultyId){
			console.log("IN saveTestAnswerAllocation got facultyId:"+facultyId);
			var inputId = "#i-"+facultyId;
			var noOfAllocatedAnswers = $(inputId).val();
			var noOfAllocationsLeft = $("#noOfAllocationsLeft").html();
			var prev = $(inputId).data('val');
			var checkAllocationsVal = parseInt( noOfAllocationsLeft) + parseInt(prev); 
			console.log("IN saveTestAnswerAllocation got noOfAllocatedAnswers:"+noOfAllocatedAnswers);
			 if(noOfAllocatedAnswers > checkAllocationsVal || noOfAllocatedAnswers == 0 ){
				 alert("Please Enter Valid Allocation Input!!!");
				 $(inputId).val(prev);
			}else{
			saveTestFacultyAnswersAllocationAjax(facultyId, noOfAllocatedAnswers);
			showSnackBar("Saved Allocation!");
			}
		}
		
		window.deleteTestAnswerAllocation = function deleteTestAnswerAllocation(facultyId){
			console.log("IN deleteTestAnswerAllocation got facultyId:"+facultyId);
			
			var inputId = "#i-"+facultyId;
			var noOfAllocatedAnswers = $(inputId).val();
			console.log("IN saveTestAnswerAllocation got noOfAllocatedAnswers:"+noOfAllocatedAnswers);
			deleteTestFacultyAnswersAllocationAjax(facultyId, noOfAllocatedAnswers);
			
			console.log(facultyIdRowIndex);
			
			showSnackBar("Deleted Allocation!");
		}
		function getRowId(facultyId){
			for(var ri=0; ri<facultyIdRowIndex.length; ri++){
				var fac = facultyIdRowIndex[ri];
				if(fac == facultyId){
					return ri+1;
				}
			}
		}
		 //saveTestFacultyAnswersAllocationAjax start
		 function saveTestFacultyAnswersAllocationAjax(facultyId, allocatedAnswers){
			 var promiseObj = new Promise(function(resolve, reject){
			console.log("In saveTestFacultyAnswersAllocationAjax() ENTERED...");
			var methodReturns = false;
			//ajax to save question reponse start
   		var body = {
   			'testId' : testId,
   			'facultyId' : facultyId,
   			'allocatedAnswers' : allocatedAnswers
   		};
   		console.log(body);
   		$.ajax({
   			type : 'POST',
   			url : '/exam/mbax/ia/am/m/saveTestFacultyAnswersAllocation',
   			data: JSON.stringify(body),
               contentType: "application/json",
               dataType : "json",
               
   		}).done(function(data) {
				  console.log("iN AJAX SUCCESS");
             	console.log(data);
				$("#noOfAllocationsLeft").text(""+data.noOfAllocationsLeft);
				$("#id-"+facultyId).text(data.noOfAllocated);
				$("#i-"+facultyId).val('0');
             	showSnackBar("Allocation Saved.");
   			 
             	methodReturns= true;
     			console.log("In saveTestFacultyAnswersAllocationAjax() EXIT... got methodReturns: "+methodReturns);
     			resolve(methodReturns);
   		}).fail(function(xhr) {
   			console.log("iN AJAX eRROR");
				console.log(result);
				showSnackBar("Failed to save allocation.");
				console.log("In saveTestFacultyAnswersAllocationAjax() EXIT... got methodReturns: "+methodReturns);
			    console.log('error', xhr);
			    reject(methodReturns);
			  });
			//ajax to save question reponse end
			 })
			 return promiseObj;

   	}
		//saveTestFacultyAnswersAllocationAjax end 
	
		//deleteTestFacultyAnswersAllocationAjax start
		 function deleteTestFacultyAnswersAllocationAjax(facultyId, allocatedAnswers){
			 var promiseObj = new Promise(function(resolve, reject){
			console.log("In deleteTestFacultyAnswersAllocationAjax() ENTERED...");
			var methodReturns = false;
   		var body = {
   			'testId' : testId,
   			'facultyId' : facultyId,
   			'allocatedAnswers' : allocatedAnswers
   		};
   		console.log(body);
   		$.ajax({
   			type : 'POST',
   			url : '/exam/mbax/ia/am/m/deleteTestFacultyAnswersAllocation',
   			data: JSON.stringify(body),
               contentType: "application/json",
               dataType : "json",
               
   		}).done(function(data) {
				  console.log("iN AJAX SUCCESS");
             	console.log(data);
				$("#noOfAllocationsLeft").text(""+data.noOfAllocationsLeft);
				var rowId = getRowId(facultyId);
				console.log("IN deleteTestAnswerAllocation got rowId:"+rowId);
				deleteRow(rowId);
				facultyIdRowIndex.splice(rowId-1, 1);
				
				showSnackBar(" Allocation Deleted!");
   			 
             	methodReturns= true;
     			console.log("In deleteTestFacultyAnswersAllocationAjax() EXIT... got methodReturns: "+methodReturns);
     			resolve(methodReturns);
   		}).fail(function(xhr) {
   			console.log("iN AJAX eRROR");
				console.log(result);
				showSnackBar("Failed to delete allocation.");
				console.log("In deleteTestFacultyAnswersAllocationAjax() EXIT... got methodReturns: "+methodReturns);
			    console.log('error', xhr);
			    reject(methodReturns);
			  });
			 })
			 return promiseObj;

   	}
		//deleteTestFacultyAnswersAllocationAjax end 

		 
			function showSnackBar(message) {
			    // Get the snackbar DIV
			    var x = document.getElementById("snackbar");
				console.log("In showSnackBar() got message "+message);
			    x.innerHTML = message;
			    
			    // Add the "show" class to DIV
			    x.className = "show";

			    // After 3 seconds, remove the show class from DIV
			    setTimeout(function(){ x.className = x.className.replace("show", ""); }, 3000);
				console.log("Exiting showSnackBar()");
			}
	 }); //ready ends
	</script>

</body>
</html>