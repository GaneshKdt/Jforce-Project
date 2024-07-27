<!DOCTYPE html>
<%@page import="java.util.Arrays"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<%@page import="com.nmims.beans.StudentExamBean"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>



<%-- 
<spring:eval expression="@propertyConfigurer.getProperty('SHOW_RESULTS_FROM_REDIS')"
	var="SHOW_RESULTS_FROM_REDIS" />
 --%>	
<%
	StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
	//String programStructure = student.getPrgmStructApplicable();// Vilpesh  commented on 2021-11-19 - unused
	//BaseController ssrmCon = new BaseController();// Vilpesh  commented on 2021-10-13
%>

<html lang="en">   
    <jsp:include page="../common/jscssNew.jsp">
	<jsp:param value="View Recent Results" name="title"/>
    </jsp:include>
    
    <style>
		    	
		#parentSpinnerDiv {
			background-color: rgba(255,255,255,0.5) !important;
			z-index: 999;
			width: 100%;
			height: 100vh;
			position: fixed;
		}
		
		#childSpinnerDiv {
			position: absolute;
			top: 50%;
			left: 50%;
			transform: translate(-50%, -50%);
		}

		.loading{
		  box-sizing: border-box;
		  display: inline-block;
		  padding: 0.5em;
		  vertical-align: middle;
		  text-align: center;
		  background-color: transparent;
		  border: 5px solid transparent;
		  border-top-color: grey;
		  border-bottom-color: grey;
		  border-radius: 50%;
		}
		
		.outer{
		  animation: spin 1s infinite;
		}
		
		.inner{
		  animation: spin 1s infinite;
		}
		
		@keyframes spin{
		  0% {
		    transform: rotateZ(0deg);
		  }
		  100% {
		    transform: rotateZ(360deg);
		  }
		}
		
		#wrap {
			box-sizing: border-box;
		}	
		 .student-info-bar,.sz-breadcrumb-wrapper{
			z-index:4 !important;
		}
	
    </style>
    
    <body>     
     <c:if test="${'SHOW_RESULTS_FROM_REDIS' eq 'Y' }">
		<div id="parentSpinnerDiv">
	
			<div id="childSpinnerDiv">
			
				<div id="wrap">
				  <div class="loading outer">
				    <div class="loading inner"></div>
				  </div>
				</div>
			
			</div>
	
		</div>
	</c:if> 
   	<%@ include file="../common/headerDemo.jsp" %>
    	
     <div class="sz-main-content-wrapper">
     <div>
		     	<jsp:include page="../common/breadcrum.jsp">
				<jsp:param value="Student Zone;Exams;Exam Results" name="breadcrumItems"/>
				</jsp:include>      
         <div class="sz-main-content menu-closed">
                <div class="sz-main-content-inner">  
                   		<div id="sticky-sidebar">
			           		<jsp:include page="../common/left-sidebar.jsp">
								<jsp:param value="Exam Results" name="activeMenu"/>
							</jsp:include>
				  		</div>
           				<div class="sz-content-wrapper examsPage">
           						<div id="studentInfoBar">
           						<%@ include file="../common/studentInfoBar.jsp" %>
           						</div>
           						
           						<div class="sz-content">
								<!-- Code for page starts -->
           						
           						<c:choose>
           							<c:when test="${'SHOW_RESULTS_FROM_REDIS' eq 'Y' }">
           							
           							 <div id="resultsFromCacheMainDiv" >
           							 	
           							 	<div id="resultsFromCacheMostRecentDiv" >
           							 
           							 	</div>
										<div class="clearfix"></div>
           							 	<div id="resultsFromCachePassFailDiv" >
           							 
           							 	</div>
										<div class="clearfix"></div>
           							 	<div id="resultsFromCacheMarksDiv" >
           							 
           							 	</div>
										<div class="clearfix"></div>
           							 	<div id="resultsFromCacheFooterDiv" >
           							 
           							 	</div>
							
           							 </div>
           							
           							</c:when>
           							<c:otherwise>
           						
           						<h2 class="text-danger text-capitalize mt-3 mb-3 fs-4 fw-bold ">${size} Marks Entries for ${mostRecentResultPeriod}
           						<c:choose>
           							<c:when test="${requestScope.resultSource eq 'REDIS' }">
           								<c:out value=":"></c:out>
           							</c:when>
           							<c:when test="${requestScope.resultSource eq 'DB' }">
           								<!-- <c:out value="${requestScope.resultSource}"></c:out> -->
           							</c:when>
	           						<c:otherwise>
	           							<c:out value="."></c:out>
	           						</c:otherwise>
           						</c:choose>
           						</h2>
						
								<!-- <ul class="pull-right list-inline topRightLinks">
										<li class="borderRight"><a href="#0" onclick="window.print();">Print</a></li>
								</ul> -->
							
								<div class="clearfix"></div>
								<%-- <%
									if("Diageo".equalsIgnoreCase(student.getConsumerType())){
									
								%>
									<h5>Results Data Will Be Displayed Shortly.</h5>	
									
								<%
									}else{
								%> --%>
							
								<div class="panel-content-wrapper rounded">
									<%@ include file="../common/messagesNew.jsp" %>
								
									<c:if test="${size > 0}">
									<div class="table-responsive ">
										<table class="table courses-sessions" id="marksEntry">
											<thead>
												<tr>
													<th class="text-center">Sr. No.</th>
													<th class="text-left">Subject</th>
													<th class="text-center">Sem</th>
													
													<%if("Online".equals(student.getExamMode())){ %>
														<th class="text-center">Term End Exam MCQ</th>
														<%if(!"JUL2017".equalsIgnoreCase(student.getPrgmStructApplicable())){ %>
														<th class="text-center">Term End Exam Descriptive</th>
														<%} %>
														<th class="text-center">Term End Exam Rounded Total</th>
													<%}else{%>
														<th class="text-center">Term End Exam Marks</th>
													<%} %>
													
													<th class="text-center">Assignment</th>
													<th class="text-center">Remarks</th>
												</tr>
											</thead>
											<tbody>
												<c:forEach var="studentMarks" items="${studentMarksList}" varStatus="status">
													<tr>
														<td class="text-center"><c:out value="${status.count}" /></td>
														<td class="text-left d-flex flex-nowrap"><c:out value="${studentMarks.subject}" /></td>
													
														<td class="text-center"><c:out value="${studentMarks.sem}" /></td>
														
														
														<%if("Online".equals(student.getExamMode())){ %>
															<c:choose>
																<c:when test="${studentMarks.writenscore eq 'NV'}">
																	<td class="text-center">NV</td>
																	<%if(!"JUL2017".equalsIgnoreCase(student.getPrgmStructApplicable())){ %>
																	<td class="text-center">NV</td>
																	<%} %>
																</c:when>
																<c:when test="${studentMarks.writenscore eq 'RIA'}">
																	<td class="text-center">RIA</td>
																	<%if(!"JUL2017".equalsIgnoreCase(student.getPrgmStructApplicable())){ %>
																	<td class="text-center">RIA</td>
																	<%} %>
																</c:when>
																<c:otherwise>
																	<td class="text-center"><c:out value="${studentMarks.mcq}" /></td>
																	<%if(!"JUL2017".equalsIgnoreCase(student.getPrgmStructApplicable())){ %>
																	<td class="text-center"><c:out value="${studentMarks.part4marks}" /></td>
																	<%} %>
																</c:otherwise>
															</c:choose>
															<td class="text-center">
																<c:out value="${studentMarks.writenscore}" />
															</td>
														<% }else{%>
															<td class="text-center">
															<c:out value="${studentMarks.writenscore}" />
															</td>
														<% } %>
													
															<td class="text-center">
															<c:out value="${studentMarks.assignmentscore}" />
															</td>
														<td>
														
														<!--<c:if test="${studentMarks.markedForRevaluation == 'Y'}">
															<c:out value="${studentMarks.remarks}" />
															</c:if>-->
															<c:out value="${studentMarks.remarks}" />
														</td>
													</tr>
												</c:forEach>
												
											</tbody>
										</table>
									
									</div>
									
									</c:if>
							</div>
						<div class="clearfix"></div>
						
						<%@include file="singleStudentPassFailMarks.jsp" %>
						
						<div class="mt-3">					
							<%@include file="studentMarksHistory.jsp" %>
						</div>	
						<c:if test="${size > 0}">
								<hr class="exam-separator"></hr>
								<div class="row">
									<div class="col-md-4 mt-2">
										<div class="card " >
										  <div class="card-body text-center ">
										   
										   <div class="signatureLeft">
													<div >
														<img src='https://staticfilesexam.s3.ap-south-1.amazonaws.com/resources_2015/images/signature.jpg' height="80px" width="100px"></img>
													</div>
													<h5>Controller of Examinations</h5>
													<p>Result Declaration Date:  <b>${declareDate}</b></p>
	      
											</div>
										  </div>
										</div>
									</div>
										
									<div class="col-md-4 mt-2">
										<div class="card">
										  <div class="card-body">
											<%-- <%if("ACBM".equals(student.getProgram())){ %> --%>
											<%
											boolean isBajaj = "Bajaj".equalsIgnoreCase(student.getConsumerType());
											boolean isDBMJul2014 = "Jul2014".equalsIgnoreCase(student.getPrgmStructApplicable()) && "DBM".equalsIgnoreCase(student.getProgram());
											if(isBajaj && !isDBMJul2014){
											%>
												<p>
												<ol>
												<li>Individual CutOff or Individual Passing Criteria will be 40%(i.e. Aggregate Passing:
												 Internal Continuous Assessments + Term-End-Examination) for each Subject</li>
												</ol>
												</p>
												<%}else{ %>
												
												Pass Marks:
												 <%if( "Certificate".equalsIgnoreCase(student.getProgramType()) || (Arrays.asList("ACBM", "ADSCM", "CBM", "CCC", "CDM", "CPBM").contains(student.getProgram())) || student.getPrgmStructApplicable().equalsIgnoreCase("Jul2017")) {%>
												  <p> 40 % on the aggregate of marks obtained in the internal assignment and Term End Examination taken together.
												  <% }else{%>
												  <p> 	50  out of 100 (i.e. Aggregate Passing: Internal Continuous Assessments + Term-End-Examination)</p>
												  <%} %>
												  
													 <%if ( "Certificate".equalsIgnoreCase(student.getProgramType()) || (Arrays.asList("ACBM", "ADSCM", "CBM", "CCC", "CDM", "CPBM").contains(student.getProgram())) || student.getPrgmStructApplicable().equalsIgnoreCase("Jul2017")) {%> 
														 <p>Individual cut-off of individual passing criteria will be 40% for each  subject.
														 </p>
													 <%} %>
												 <%} %>
												<p>Discrepancy if any in the above information should be mailed with student name, Student No.,
													Program enrolled, Semester details, Subject: at <a href="mailto:ngasce.exams@nmims.edu" target="_top">ngasce.exams@nmims.edu</a></p>
											
												<%if("Online".equals(student.getExamMode()) && !("JUL2017".equalsIgnoreCase(student.getPrgmStructApplicable()) || Arrays.asList("ACBM", "ADSCM", "CBM", "CCC", "CDM", "CPBM").contains(student.getProgram()))){ %>
												<p>Students who wish to apply for revaluation of the descriptive answers may apply for the same on or before <b>27th July 2023 before 23.59 p.m. (IST)</b> using Service Request link available on Student Zone Home Page.</p>
												<%} %>
										  </div>
										</div>
									</div>
									
									
									
									<div class="col-md-4 mt-2">
									
									
									
									<div class="card">
									  <div class="card-body">   
											<div class="row">
												<div class="statusBox">
													<div class="media">
														<div class="media-left media-top"> ANS </div>
														<div class="media-body"> Assignment Not Submitted </div>
													</div>
													<div class="media">
														<div class="media-left media-top"> AB </div>
														<div class="media-body"> Absent </div>
													</div>
													<div class="media">
														<div class="media-left media-top"> NV </div>
														<div class="media-body"> Null & Void </div>
													</div>
													<div class="media">
														<div class="media-left media-top"> CC </div>
														<div class="media-body"> Copy Case </div>
													</div>
													<div class="media">
														<div class="media-left media-top"> RIA </div>
														<div class="media-body"> Result Kept in Abeyance </div>
													</div>
													<div class="media">
														<div class="media-left media-top"> NA </div>
														<div class="media-body"> Not Eligible due to non submission of assignment </div>
													</div>
												</div>
											</div>
									  </div>
									</div>
							
									</div>
									<div class="inline-block ml-1 mt-2">
									<p>The above result is provisional in nature and is just for student's information. The gradesheet/marksheet/transcript issued by the University will be the authentic document.</p>
									</div>
									
								</div>
									
								<div class="clearfix"></div>
							</c:if>
           							<%-- <%
									}
							%> --%>	
							
							
           							</c:otherwise>
           						</c:choose>
							
							<!-- Code for page ends -->
           					</div>
           				</div>
           		
                         
		</div>
        </div>
     </div>
  	
        <jsp:include page="../common/footerNew.jsp"/>
		
    </body>
    
<c:if test="${'SHOW_RESULTS_FROM_REDIS' eq 'Y' }">
              							
<script>
$(document).ready(function(){


	var clearFixHtml = '<div class="clearfix"></div>';

	var studentDetails = null;
	///////////////////////////////////////////////////////
	getReultsFromCacheBySapid();
	
	function getReultsFromCacheBySapid(){
	try{
	var data = {
			sapid:"${userId}",
	}

	console.log("getReultsFromCacheBySapid() data : ",JSON.stringify(data));
	
	$.ajax({
		type : "POST",
		contentType : "application/json",
		url : "/timeline/api/results/getResultsDataFromRedisBySapid",   
		data : JSON.stringify(data),
		success : function(data) {
			
			console.log("SUCCESS: ", data);
			let resultsData = data.resultsData;
			//resultsData.studentMarksList
			console.log("resultsData: ", resultsData);
				
			if(resultsData && resultsData.studentDetails[0] && resultsData.studentMarksList && resultsData.passFailStatus && resultsData.studentMarksHistory){
				
				
				async function asyncCallToSetResultsData(){
					let saved = await  setResultsData(resultsData)
						.then(success, failure)
				 		function success(data){
							return true;
					 		}
				 		function failure(data){

				 			getReultsFromDBBySapid()
				 			return false;
						 	} 
				}
				asyncCallToSetResultsData();
				
				
				
			} // end of if 
			else{
				getReultsFromDBBySapid()
			}

		},
		error : function(e) {
			
			console.log("getReultsFromCache ERROR: ", e);
			getReultsFromDBBySapid()
		}
	});

	}catch(err){

		console.log("IN getReultsFromCache catch got ERROR: ", err);
		getReultsFromDBBySapid()
	}

	} //getReultsFromCache ends

/////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////
function getReultsFromDBBySapid(){
try{
var data = {
		sapid:"${userId}",
		fromAdmin:"Y"
}

console.log("getReultsFromDBBySapid() data : ",JSON.stringify(data));

$.ajax({
	type : "POST",
	contentType : "application/json",
	url : "/exam/m/getMostRecentResults",   
	data : JSON.stringify(data),
	success : function(data) {
		
		console.log("SUCCESS: ", data);
		let resultsData = data;
		//resultsData.studentMarksList
		console.log("resultsData: ", resultsData);
			
		if(resultsData && resultsData.studentDetails[0] && resultsData.studentMarksList && resultsData.passFailStatus && resultsData.studentMarksHistory){
			
			
			async function asyncCallToSetResultsData(){
				let saved = await  setResultsData(resultsData)
					.then(success, failure)
			 		function success(data){
						return true;
				 		}
			 		function failure(data){

						showErrorMessage();
						return false;
					 	} 
			}
			asyncCallToSetResultsData();
			
			
			
		} // end of if 
		

	},
	error : function(e) {
		
		console.log("ERROR: ", e);
		showErrorMessage();
	}
});

}catch(err){

	console.log("ERROR: ", err);
	showErrorMessage();
}

} //getReultsFromCache ends

/////////////////////////////////////////////////////////////



//setResultsData start
function setResultsData(resultsData){
	console.log("resultsData: ", resultsData);

	 var promiseObj = new Promise(function(resolve, reject){
		try{


			studentDetails = resultsData.studentDetails[0] ? resultsData.studentDetails[0] : null;
			console.log("studentDetails: ", studentDetails);
			let mostRecentMarksList = resultsData.studentMarksList;
			let mostRecentMarksSize =mostRecentMarksList ? mostRecentMarksList.length : 0;
			let mostRecentResultPeriod = resultsData.mostRecentResultPeriod;
			let declareDate = resultsData.declareDate[0] ? resultsData.declareDate[0] : '';
			let passFailStatusDataArray= resultsData.passFailStatus;

			let marksHistoryRecordsArray= resultsData.studentMarksHistory;
			
			async function asyncCallToSetMostRecentData(){
				let saved = await  setMostRecentData(mostRecentMarksList,mostRecentResultPeriod)
					.then(success, failure)
			 		function success(data){
						return true;
				 		}
			 		function failure(data){

						showErrorMessage();
						return false;
					 	} 
			}
			asyncCallToSetMostRecentData();


			async function asyncCallToSetPassFailStatusData(){
				let savedPFS = await  setPassFailStatusData(passFailStatusDataArray)
					.then(success, failure)
			 		function success(data){
						return true;
				 		}
			 		function failure(data){

						showErrorMessage();
						return false;
					 	} 
			}
			asyncCallToSetPassFailStatusData();


			async function asyncCallToSetMarksData(){
				let savedMH = await  setMarksData(marksHistoryRecordsArray)
					.then(success, failure)
			 		function success(data){
						return true;
				 		}
			 		function failure(data){

						showErrorMessage();
						return false;
					 	} 
			}
			asyncCallToSetMarksData();

			//mostRecentMarksSize

			async function asyncCallToSetFooterData(){
				let savedMH = await  setFooterData(mostRecentMarksSize,declareDate)
					.then(success, failure)
			 		function success(data){
			 			$('#parentSpinnerDiv').hide();
						return true;
				 		}
			 		function failure(data){

						showErrorMessage();
			 			$('#parentSpinnerDiv').hide();
						return false;
					 	} 
			}
			asyncCallToSetFooterData();
		
			
			resolve(true);
		}catch(err){
			console.log('IN setResultsData() catch() err : ',err);
			reject(true);
		}
	 })
	 
	return promiseObj;
	

} 
//setResultsData end

//resultsFromCacheMostRecentDiv
	function setMostRecentData(mostRecentMarksList,mostRecentResultPeriod){
		console.log('iN setMostRecentData got mostRecentMarksList : ',mostRecentMarksList);
		console.log('iN setMostRecentData got mostRecentResultPeriod : ',mostRecentResultPeriod);

		 var promiseObj = new Promise(function(resolve, reject){
			 try{
				let size = mostRecentMarksList ? mostRecentMarksList.length : 0;

				let mostRecentMarksHeaderHtml = '<h2 class="red text-capitalize"> '+size+' Marks Entries for '+mostRecentResultPeriod+'</h2>';	

				let mostRecentMarksRecordsHtml ='';
				
				if(mostRecentMarksList && mostRecentMarksList.length > 0){

					mostRecentMarksRecordsHtml = mostRecentMarksRecordsHtml +'<div class="panel-content-wrapper">'
					mostRecentMarksRecordsHtml = mostRecentMarksRecordsHtml +'<div class="table-responsive">'
					mostRecentMarksRecordsHtml = mostRecentMarksRecordsHtml +'<table class="table courses-sessions">'
					mostRecentMarksRecordsHtml = mostRecentMarksRecordsHtml +'	<thead>'
					mostRecentMarksRecordsHtml = mostRecentMarksRecordsHtml +'		<tr>'
					mostRecentMarksRecordsHtml = mostRecentMarksRecordsHtml +'			<th>Sr. No.</th>'
					mostRecentMarksRecordsHtml = mostRecentMarksRecordsHtml +'			<th style="text-align:left;">Subject</th>'
					mostRecentMarksRecordsHtml = mostRecentMarksRecordsHtml +'			<th>Sem</th>'
					mostRecentMarksRecordsHtml = mostRecentMarksRecordsHtml +'			<th>Term End Exam MCQ</th>'
					mostRecentMarksRecordsHtml = mostRecentMarksRecordsHtml +'			<th>Term End Exam Descriptive</th>'
					mostRecentMarksRecordsHtml = mostRecentMarksRecordsHtml +'			<th>Term End Exam Rounded Total</th>'
					mostRecentMarksRecordsHtml = mostRecentMarksRecordsHtml +'			<th>Assignment</th>'
					mostRecentMarksRecordsHtml = mostRecentMarksRecordsHtml +'			<th>Remarks</th>'
					mostRecentMarksRecordsHtml = mostRecentMarksRecordsHtml +'		</tr>'
					mostRecentMarksRecordsHtml = mostRecentMarksRecordsHtml +'	</thead>'
					mostRecentMarksRecordsHtml = mostRecentMarksRecordsHtml +'	<tbody>'

					for(var i=0;i<mostRecentMarksList.length;i++){
						console.log("i",i);
						mostRecentMarksRecordsHtml = mostRecentMarksRecordsHtml +'		<tr>'
						mostRecentMarksRecordsHtml = mostRecentMarksRecordsHtml +'<td>'+(i+1)+'</td>'
						mostRecentMarksRecordsHtml = mostRecentMarksRecordsHtml +'<td nowrap="nowrap" style="text-align:left;">'+mostRecentMarksList[i].subject.replace(/'/g,' ')+'</td>'
						mostRecentMarksRecordsHtml = mostRecentMarksRecordsHtml +'<td>'+mostRecentMarksList[i].sem+'</td>'

						if(mostRecentMarksList[i].writenscore == 'NV'){
							mostRecentMarksRecordsHtml = mostRecentMarksRecordsHtml +'<td>NV</td>'
							mostRecentMarksRecordsHtml = mostRecentMarksRecordsHtml +'<td>NV</td>'

						}else if(mostRecentMarksList[i].writenscore == 'RIA'){
							mostRecentMarksRecordsHtml = mostRecentMarksRecordsHtml +'<td>RIA</td>'
							mostRecentMarksRecordsHtml = mostRecentMarksRecordsHtml +'<td>RIA</td>'

						}else{
							mostRecentMarksRecordsHtml = mostRecentMarksRecordsHtml +'<td>'+(mostRecentMarksList[i].mcq?mostRecentMarksList[i].mcq:'')+'</td>'
							mostRecentMarksRecordsHtml = mostRecentMarksRecordsHtml +'<td>'+(mostRecentMarksList[i].part4marks?mostRecentMarksList[i].part4marks:'')+'</td>'

						}
						mostRecentMarksRecordsHtml = mostRecentMarksRecordsHtml +'<td>'+(mostRecentMarksList[i].writenscore?mostRecentMarksList[i].writenscore:'')+'</td>'
						mostRecentMarksRecordsHtml = mostRecentMarksRecordsHtml +'<td>'+(mostRecentMarksList[i].assignmentscore?mostRecentMarksList[i].assignmentscore:'')+'</td>'
						mostRecentMarksRecordsHtml = mostRecentMarksRecordsHtml +'<td>'+(mostRecentMarksList[i].remarks?mostRecentMarksList[i].remarks:'')+'</td>'
						mostRecentMarksRecordsHtml = mostRecentMarksRecordsHtml +'		<tr>'
						
					}
					mostRecentMarksRecordsHtml = mostRecentMarksRecordsHtml +'	</tbody>'
					mostRecentMarksRecordsHtml = mostRecentMarksRecordsHtml +'	</table>'
					mostRecentMarksRecordsHtml = mostRecentMarksRecordsHtml +'</div>';
				
				}else{
					mostRecentMarksRecordsHtml = '<div class="panel-content-wrapper"></div>';
				}

				$('#resultsFromCacheMostRecentDiv').html(mostRecentMarksHeaderHtml+clearFixHtml+mostRecentMarksRecordsHtml+clearFixHtml);	

				resolve(true);
			}catch(err){
				console.log('IN setMostRecentData() catch() err : ',err);
				reject(true);
			}
		 })
		 
		return promiseObj;
		
	
	}//end of setMostRecentData()

	//resultsFromCachePassFailDiv
	function setPassFailStatusData(passFailStatusDataArray){
		console.log('iN setPassFailStatusData got passFailStatusDataArray : ',passFailStatusDataArray);

		 var promiseObj = new Promise(function(resolve, reject){
				try{
				let passFailRecords =  passFailStatusDataArray ? (passFailStatusDataArray[1] ? passFailStatusDataArray[1] : []) : [];
				
				let passFailRecordsSize = passFailRecords.length;
				
				
				let passFailStatusRecordsHtml ='';


				passFailStatusRecordsHtml = passFailStatusRecordsHtml +'<div class="panel panel-default panel-courses-page">'
				passFailStatusRecordsHtml = passFailStatusRecordsHtml +'	<div class="panel-heading" role="tab" id="">'
				passFailStatusRecordsHtml = passFailStatusRecordsHtml +'		<h2>Pass Fail Status</h2>'
				passFailStatusRecordsHtml = passFailStatusRecordsHtml +'		<div class="custom-clearfix clearfix"></div>'
				passFailStatusRecordsHtml = passFailStatusRecordsHtml +'		<ul class="topRightLinks list-inline">'
				passFailStatusRecordsHtml = passFailStatusRecordsHtml +'			<li>'
				passFailStatusRecordsHtml = passFailStatusRecordsHtml +'				<h3 class=" green"><span>'+passFailRecordsSize+'</span> Records Available</h3>'
				passFailStatusRecordsHtml = passFailStatusRecordsHtml +'			</li>'
				passFailStatusRecordsHtml = passFailStatusRecordsHtml +'			<li><a class="panel-toggler collapsed"  role="button" data-toggle="collapse" href="#collapseOne" aria-expanded="true"><span class="glyphicon glyphicon-arrow-down"></span></a></li>'
						
				passFailStatusRecordsHtml = passFailStatusRecordsHtml +'		</ul>'
				passFailStatusRecordsHtml = passFailStatusRecordsHtml +'		<div class="clearfix"></div>'
				passFailStatusRecordsHtml = passFailStatusRecordsHtml +'	</div>'


				passFailStatusRecordsHtml = passFailStatusRecordsHtml +'<div id="collapseOne" class="panel-collapse collapse panel-content-wrapper" role="tabpanel">'
				passFailStatusRecordsHtml = passFailStatusRecordsHtml +'	<div class="panel-body" > '

				if(passFailRecordsSize ==0){
					passFailStatusRecordsHtml = passFailStatusRecordsHtml +'			<div class="no-data-wrapper">'
					passFailStatusRecordsHtml = passFailStatusRecordsHtml +'				<p class="no-data"><span class="icon-exams"></span>No Pass Fail Records </p>'
					passFailStatusRecordsHtml = passFailStatusRecordsHtml +'			</div>'
				}
				
				if(passFailRecordsSize > 0){
							
					passFailStatusRecordsHtml = passFailStatusRecordsHtml +'		<div class="data-content">'
					passFailStatusRecordsHtml = passFailStatusRecordsHtml +'<div class="panel-content-wrapper">'
					passFailStatusRecordsHtml = passFailStatusRecordsHtml +'<div class="table-responsive">'
					passFailStatusRecordsHtml = passFailStatusRecordsHtml +'<table class="table courses-sessions">'
					passFailStatusRecordsHtml = passFailStatusRecordsHtml +'	<thead>'
					passFailStatusRecordsHtml = passFailStatusRecordsHtml +'		<tr>'
					passFailStatusRecordsHtml = passFailStatusRecordsHtml +'			<th>Sr. No.</th>'
					passFailStatusRecordsHtml = passFailStatusRecordsHtml +'			<th style="text-align:left;">Subject</th>'
					passFailStatusRecordsHtml = passFailStatusRecordsHtml +'			<th>Sem</th>'
					passFailStatusRecordsHtml = passFailStatusRecordsHtml +'			<th style="text-align:center">TEE Marks</th>'
					passFailStatusRecordsHtml = passFailStatusRecordsHtml +'			<th style="text-align:center">Assignment Marks</th>'
					passFailStatusRecordsHtml = passFailStatusRecordsHtml +'			<th style="text-align:center">Grace Marks</th>'
					passFailStatusRecordsHtml = passFailStatusRecordsHtml +'			<th style="text-align:center">Total Marks</th>'
					passFailStatusRecordsHtml = passFailStatusRecordsHtml +'		</tr>'
					passFailStatusRecordsHtml = passFailStatusRecordsHtml +'	</thead>'
					passFailStatusRecordsHtml = passFailStatusRecordsHtml +'	<tbody>'

					for(var i=0;i<passFailRecords.length;i++){
						passFailStatusRecordsHtml = passFailStatusRecordsHtml +'		<tr>'
						passFailStatusRecordsHtml = passFailStatusRecordsHtml +'<td>'+(i+1)+'</td>'
						passFailStatusRecordsHtml = passFailStatusRecordsHtml +'<td nowrap="nowrap" style="text-align:left;">'+passFailRecords[i].subject.replace(/'/g,' ')+'</td>'
						passFailStatusRecordsHtml = passFailStatusRecordsHtml +'<td>'+passFailRecords[i].sem+'</td>'

						passFailStatusRecordsHtml = passFailStatusRecordsHtml +'<td style="text-align:center">'+passFailRecords[i].writtenscore+'<sub>('+passFailRecords[i].writtenMonth+'-'+passFailRecords[i].writtenYear+')</sub></td>'
						passFailStatusRecordsHtml = passFailStatusRecordsHtml +'<td style="text-align:center">'+passFailRecords[i].assignmentscore+'<sub>('+passFailRecords[i].assignmentMonth+'-'+passFailRecords[i].assignmentYear+')</sub></td>'
						passFailStatusRecordsHtml = passFailStatusRecordsHtml +'<td style="text-align:center">'+(passFailRecords[i].gracemarks ? passFailRecords[i].gracemarks:'')+' </td>'

						if('Y' === passFailRecords[i].isPass){ 
							passFailStatusRecordsHtml = passFailStatusRecordsHtml +'<td style="text-align:center;color: green"><b>'+(passFailRecords[i].total)+'</b></td>'
						}
						else if(('ANS' === passFailRecords[i].assignmentscore) && (!isNaN(passFailRecords[i].writtenscore)) ) {
							passFailStatusRecordsHtml = passFailStatusRecordsHtml +'<td style="text-align:center"><b>On Hold (Assignment Not Submitted)</b></td>'
						
						}
						else if('N' === passFailRecords[i].isPass){ 
							passFailStatusRecordsHtml = passFailStatusRecordsHtml +'<td style="text-align:center;color: red"><b>'+(passFailRecords[i].total)+'</b></td>'
						}else{
							passFailStatusRecordsHtml = passFailStatusRecordsHtml +'<td style="text-align:center;"><b>'+(passFailRecords[i].total)+'</b></td>'
						}
						passFailStatusRecordsHtml = passFailStatusRecordsHtml +'		<tr>'
						
					}
					passFailStatusRecordsHtml = passFailStatusRecordsHtml +'	</tbody>'
					passFailStatusRecordsHtml = passFailStatusRecordsHtml +'	</table>'
					passFailStatusRecordsHtml = passFailStatusRecordsHtml +'</div>';
					passFailStatusRecordsHtml = passFailStatusRecordsHtml +'</div>';
					
				}

				passFailStatusRecordsHtml = passFailStatusRecordsHtml +'		</div>';
				passFailStatusRecordsHtml = passFailStatusRecordsHtml +'	</div>';
				passFailStatusRecordsHtml = passFailStatusRecordsHtml +'</div>';
				
				$('#resultsFromCachePassFailDiv').html(passFailStatusRecordsHtml);	

					resolve(true);
				}catch(err){
					console.log('IN setPassFailStatusData() catch() err : ',err);
					reject(true);
				}

		 })
		 
		return promiseObj;
		
	
	}//end of setPassFailStatusData()

	//resultsFromCacheMarksDiv
	//setMarksData(marksHistoryRecordsArray)
		function setMarksData(marksHistoryRecordsArray){
		console.log('iN setMarksData got marksHistoryRecordsArray : ',marksHistoryRecordsArray);

		 var promiseObj = new Promise(function(resolve, reject){
				try{
				let marksHistoryRecords =  marksHistoryRecordsArray ? marksHistoryRecordsArray : [];
				
				let marksHistoryRecordsSize = marksHistoryRecords.length;
				
				
				let marksHistoryRecordsHtml ='';


				marksHistoryRecordsHtml = marksHistoryRecordsHtml +'<div class="panel panel-default panel-courses-page">'
				marksHistoryRecordsHtml = marksHistoryRecordsHtml +'	<div class="panel-heading" role="tab" id="">'
				marksHistoryRecordsHtml = marksHistoryRecordsHtml +'		<h2>Marks History</h2>'
				marksHistoryRecordsHtml = marksHistoryRecordsHtml +'		<div class="custom-clearfix clearfix"></div>'
				marksHistoryRecordsHtml = marksHistoryRecordsHtml +'		<ul class="topRightLinks list-inline">'
				marksHistoryRecordsHtml = marksHistoryRecordsHtml +'			<li>'
				marksHistoryRecordsHtml = marksHistoryRecordsHtml +'				<h3 class=" green"><span>'+marksHistoryRecordsSize+'</span> Records Available</h3>'
				marksHistoryRecordsHtml = marksHistoryRecordsHtml +'			</li>'
				marksHistoryRecordsHtml = marksHistoryRecordsHtml +'			<li><a class="panel-toggler collapsed"  role="button" data-toggle="collapse" href="#collapseMarksHistory" aria-expanded="true"><span class="glyphicon glyphicon-arrow-down"></span></a></li>'
						
				marksHistoryRecordsHtml = marksHistoryRecordsHtml +'		</ul>'
				marksHistoryRecordsHtml = marksHistoryRecordsHtml +'		<div class="clearfix"></div>'
				marksHistoryRecordsHtml = marksHistoryRecordsHtml +'	</div>'


				marksHistoryRecordsHtml = marksHistoryRecordsHtml +'<div id="collapseMarksHistory" class="panel-collapse collapse panel-content-wrapper" role="tabpanel">'
				marksHistoryRecordsHtml = marksHistoryRecordsHtml +'	<div class="panel-body" > '

				if(marksHistoryRecordsSize ==0){
					marksHistoryRecordsHtml = marksHistoryRecordsHtml +'			<div class="no-data-wrapper">'
					marksHistoryRecordsHtml = marksHistoryRecordsHtml +'				<p class="no-data"><span class="icon-exams"></span>No Mark History </p>'
					marksHistoryRecordsHtml = marksHistoryRecordsHtml +'			</div>'
				}
				
				if(marksHistoryRecordsSize > 0){
							
					marksHistoryRecordsHtml = marksHistoryRecordsHtml +'		<div class="data-content">'
					marksHistoryRecordsHtml = marksHistoryRecordsHtml +'<div class="panel-content-wrapper">'
					marksHistoryRecordsHtml = marksHistoryRecordsHtml +'<div class="table-responsive">'
					marksHistoryRecordsHtml = marksHistoryRecordsHtml +'<table class="table table-striped" style="font-size: 12px" id="studentMarksHistory">'
					marksHistoryRecordsHtml = marksHistoryRecordsHtml +'	<thead>'
					marksHistoryRecordsHtml = marksHistoryRecordsHtml +'		<tr>'
					marksHistoryRecordsHtml = marksHistoryRecordsHtml +'			<th>Sr. No.</th>'
					marksHistoryRecordsHtml = marksHistoryRecordsHtml +'			<th>Exam Year</th>'
					marksHistoryRecordsHtml = marksHistoryRecordsHtml +'			<th>Exam Month</th>'
					marksHistoryRecordsHtml = marksHistoryRecordsHtml +'			<th>Sem</th>'
					marksHistoryRecordsHtml = marksHistoryRecordsHtml +'			<th style="text-align:left;">Subject</th>'
					marksHistoryRecordsHtml = marksHistoryRecordsHtml +'			<th style="text-align:center">Written</th>'
					marksHistoryRecordsHtml = marksHistoryRecordsHtml +'			<th style="text-align:center">Assign.</th>'
					marksHistoryRecordsHtml = marksHistoryRecordsHtml +'			<th style="text-align:center">Grace</th>'
					marksHistoryRecordsHtml = marksHistoryRecordsHtml +'		</tr>'
					marksHistoryRecordsHtml = marksHistoryRecordsHtml +'	</thead>'
					marksHistoryRecordsHtml = marksHistoryRecordsHtml +'	<tbody>'

					for(var i=0;i<marksHistoryRecords.length;i++){
						marksHistoryRecordsHtml = marksHistoryRecordsHtml +'		<tr>'
						marksHistoryRecordsHtml = marksHistoryRecordsHtml +'<td>'+(i+1)+'</td>'
						marksHistoryRecordsHtml = marksHistoryRecordsHtml +'<td>'+marksHistoryRecords[i].year+'</td>'
						marksHistoryRecordsHtml = marksHistoryRecordsHtml +'<td>'+marksHistoryRecords[i].month+'</td>'
						marksHistoryRecordsHtml = marksHistoryRecordsHtml +'<td>'+marksHistoryRecords[i].sem+'</td>'
						marksHistoryRecordsHtml = marksHistoryRecordsHtml +'<td nowrap="nowrap" style="text-align:left;">'+marksHistoryRecords[i].subject.replace(/'/g,' ')+'</td>'

						marksHistoryRecordsHtml = marksHistoryRecordsHtml +'<td style="text-align:center">'+marksHistoryRecords[i].writenscore+'</td>'
						marksHistoryRecordsHtml = marksHistoryRecordsHtml +'<td style="text-align:center">'+marksHistoryRecords[i].assignmentscore+'</td>'
						marksHistoryRecordsHtml = marksHistoryRecordsHtml +'<td style="text-align:center">'+(marksHistoryRecords[i].gracemarks ? marksHistoryRecords[i].gracemarks:'')+' </td>'

						marksHistoryRecordsHtml = marksHistoryRecordsHtml +'		<tr>'
						
					}
					marksHistoryRecordsHtml = marksHistoryRecordsHtml +'	</tbody>'
					marksHistoryRecordsHtml = marksHistoryRecordsHtml +'	</table>'
					marksHistoryRecordsHtml = marksHistoryRecordsHtml +'</div>';
					marksHistoryRecordsHtml = marksHistoryRecordsHtml +'</div>';
					
				}

				marksHistoryRecordsHtml = marksHistoryRecordsHtml +'		</div>';
				marksHistoryRecordsHtml = marksHistoryRecordsHtml +'	</div>';
				marksHistoryRecordsHtml = marksHistoryRecordsHtml +'</div>';
				
				$('#resultsFromCacheMarksDiv').html(marksHistoryRecordsHtml);	

					resolve(true);
				}catch(err){
					console.log('IN setMarksData() catch() err : ',err);
					reject(true);
				}

		 })
		 
		return promiseObj;
		
	
	}//end of setMarksData()
	
	//setFooterData(mostRecentMarksSize)
	function setFooterData(mostRecentMarksSize,declareDate){
		console.log('iN setFooterData got mostRecentMarksSize : ',mostRecentMarksSize);
		console.log('iN setFooterData got declareDate : ',declareDate);

		 var promiseObj = new Promise(function(resolve, reject){
				try{
					
					let footerHtml ='';
					if(mostRecentMarksSize > 0){

						
						footerHtml =footerHtml +'		<hr class="exam-separator"></hr>'
						footerHtml =footerHtml +'		<div class="row">'
						footerHtml =footerHtml +'			<div class="col-md-4">'
						footerHtml =footerHtml +'						<div class="signatureLeft">'
						footerHtml =footerHtml +'						<div >'
						footerHtml =footerHtml +'							<img src="https://staticfilesexam.s3.ap-south-1.amazonaws.com/resources_2015/images/signature.jpg" height="80px" width="100px"></img>'
						footerHtml =footerHtml +'						</div>'
						footerHtml =footerHtml +'							<h5>Controller of Examinations</h5>'
						footerHtml =footerHtml +'							<p>Result Declaration Date:  <b>'+declareDate+'</b></p>'


						footerHtml =footerHtml +'						</div>'
						footerHtml =footerHtml +'					</div>'
										
						footerHtml =footerHtml +'				<div class="col-md-4">'
											//below to be implemented
											let isBajaj = ('Bajaj' === studentDetails.consumerType) ? true : false;
											let isDBMJul2014 = ( ("Jul2014" === studentDetails.prgmStructApplicable)  ? true : false) &&  ( ('DBM' === studentDetails.program)  ? true : false) ;
														
											if(isBajaj && !isDBMJul2014){
											
						footerHtml =footerHtml +'						<p>'
						footerHtml =footerHtml +'						<ol>'
						footerHtml =footerHtml +'						<li>Individual CutOff or Individual Passing Criteria will be 40%(i.e. Aggregate Passing:'
						footerHtml =footerHtml +'						 Internal Continuous Assessments + Term-End-Examination) for each Subject</li>'
						footerHtml =footerHtml +'						</ol>'
						footerHtml =footerHtml +'						</p>'
												}else{
						footerHtml =footerHtml +'						<p> '
						footerHtml =footerHtml +'						Pass Marks: '
																	  if( ('Certificate' === studentDetails.programType) || (["ACBM", "ADSCM", "CBM", "CCC", "CDM", "CPBM"].includes(student.program)) || (studentDetails.prgmStructApplicable === "Jul2017") ) {
																		  footerHtml =footerHtml +'40 % on the aggregate of marks obtained in the internal assignment and Term End Examination taken together.</p>'
																	  }else{
																		  footerHtml =footerHtml +'50'
																		  footerHtml =footerHtml +' out of 100(i.e. Aggregate Passing:'
																		 footerHtml =footerHtml +' Internal Continuous Assessments + Term-End-Examination)</p>'
																	  }
						
																	  if ( ('Certificate' === studentDetails.programType) || (["ACBM", "ADSCM", "CBM", "CCC", "CDM", "CPBM"].includes(student.program)) || (studentDetails.prgmStructApplicable === "Jul2017") ) { 
																		  footerHtml =footerHtml +'<p>Individual cut-off of individual passing criteria will be 40% for each  subject.'
																		  footerHtml =footerHtml +'</p>'
																	 }
																 }
						footerHtml =footerHtml +'										<p>Discrepancy if any in the above information should be mailed with student name, Student No.,'
						footerHtml =footerHtml +'											Program enrolled, Semester details, Subject: at <a href="mailto:ngasce.exams@nmims.edu" target="_top">ngasce.exams@nmims.edu</a></p>'
															
																if( ! (studentDetails.prgmStructApplicable === "Jul2017") ){ 
																	footerHtml =footerHtml +'<p>'
																	footerHtml =footerHtml +'Students who wish to apply for revaluation of the descriptive answers may apply for the same on or before'
																	footerHtml =footerHtml +'<b>'
																	footerHtml =footerHtml +'8th May 2023 23:59 p.m. '
																	footerHtml =footerHtml +'</b> using Service Request link available on Student Zone Home Page.'
																	footerHtml =footerHtml +'</p>'
																}
											
												
						footerHtml =footerHtml +'			</div>'
						footerHtml =footerHtml +'				<div class="col-md-4">'
						footerHtml =footerHtml +'				<div class="row">'
						footerHtml =footerHtml +'					<div class="statusBox">'
						footerHtml =footerHtml +'						<div class="media">'
						footerHtml =footerHtml +'							<div class="media-left media-top"> ANS </div>'
						footerHtml =footerHtml +'							<div class="media-body"> Assignment Not Submitted </div>'
						footerHtml =footerHtml +'						</div>'
						footerHtml =footerHtml +'						<div class="media">'
						footerHtml =footerHtml +'	<div class="media-left media-top"> AB </div>'
						footerHtml =footerHtml +'							<div class="media-body"> Absent </div>'
						footerHtml =footerHtml +'						</div>'
						footerHtml =footerHtml +'						<div class="media">'
						footerHtml =footerHtml +'							<div class="media-left media-top"> NV </div>'
						footerHtml =footerHtml +'							<div class="media-body"> Null & Void </div>'
						footerHtml =footerHtml +'						</div>'
						footerHtml =footerHtml +'						<div class="media">'
						footerHtml =footerHtml +'							<div class="media-left media-top"> CC </div>'
						footerHtml =footerHtml +'							<div class="media-body"> Copy Case </div>'
						footerHtml =footerHtml +'						</div>'
						footerHtml =footerHtml +'						<div class="media">'
						footerHtml =footerHtml +'							<div class="media-left media-top"> RIA </div>'
						footerHtml =footerHtml +'							<div class="media-body"> Result Kept in Abeyance </div>'
						footerHtml =footerHtml +'						</div>'
						footerHtml =footerHtml +'						<div class="media">'
						footerHtml =footerHtml +'							<div class="media-left media-top"> NA </div>'
						footerHtml =footerHtml +'							<div class="media-body"> Not Eligible due to non submission of assignment </div>'
						footerHtml =footerHtml +'						</div>'
						footerHtml =footerHtml +'					</div>'
						footerHtml =footerHtml +'				</div>'
										
						footerHtml =footerHtml +'			</div>'
						footerHtml =footerHtml +'			<div style="display:inline-block;margin-left:10px">'
						footerHtml =footerHtml +'			<p>The results published on this website are only for immediate information to the examinees and cannot be considered as final. '
						footerHtml =footerHtml +'			Information as regards marks/ grades published by NMIMS University in mark sheet/ grade sheet should only be treated as authentic.</p>'
						footerHtml =footerHtml +'			</div>'
									
						footerHtml =footerHtml +'		</div>'
									
						footerHtml =footerHtml +'		<div class="clearfix"></div>'
							

						$('#resultsFromCacheFooterDiv').html(footerHtml);
					}	

					resolve(true);

					}catch(err){
					console.log('IN setMarksData() catch() err : ',err);
					reject(true);
				}

		 })
		 
		return promiseObj;
		
	
	}//end of setFooterData()


	function showErrorMessage(){

		let errorMessageHtml ='';
		
		errorMessageHtml =errorMessageHtml +''
		errorMessageHtml =errorMessageHtml +'<div class="alert alert-danger" style="margin-top:10px; font-size:16px;" >'
		errorMessageHtml =errorMessageHtml +'  <strong><span class="glyphicon glyphicon-alert"></span></strong> Something Went Wrong, Please try again later.'
		errorMessageHtml =errorMessageHtml +'</div>'		

		$('#resultsFromCacheMainDiv').html(errorMessageHtml);
	}



		
		
	}); // end of doc ready()
</script>
</c:if>


	<script>
	<!-- datatable js -->
	$(document).ready(function () {
		//datatable
		  $('#marksEntry').DataTable();
	});

	</script>	

</html> 	