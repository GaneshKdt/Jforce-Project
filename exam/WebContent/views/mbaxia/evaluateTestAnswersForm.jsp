
<!DOCTYPE html>
<html lang="en">
	
<%@page import="com.nmims.beans.Person"%>
  
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
  
    <jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Evaluate Test Answers" name="title"/>
    </jsp:include>
    
 
<link
	href="https://gitcdn.github.io/bootstrap-toggle/2.2.2/css/bootstrap-toggle.min.css"
	rel="stylesheet">
<script
  src="https://code.jquery.com/jquery-3.3.1.js"
  integrity="sha256-2Kok7MbOyxpgUVvAk/HJ2jigOSYS2auK4Pfzbm7uH60="
  crossorigin="anonymous"></script>
<script
	src="https://gitcdn.github.io/bootstrap-toggle/2.2.2/js/bootstrap-toggle.min.js"></script>

    
    
    <style>
    	.evaluateQuestionsButton {
		   position:relative;
		}
		.evaluateQuestionsButton[data-badge]:after {
		   content:attr(data-badge);
		   position:absolute;
		   top:-10px;
		   right:-10px;
		   font-size:.7em;
		   background: #D2232A ;
		   color:white;
		   width:18px;height:18px;
		   text-align:center;
		   line-height:18px;
		   border-radius:50%;
		   box-shadow:0 0 1px #333;
		   border : 2px solid white;
		}
		
		.badge-button {
		   position:relative;
		}
		.badge-button[data-badge]:after {
		   content:attr(data-badge);
		   position:absolute;
		   z-index: 1;		   
		   top:-10px;
		   right:-10px;
		   font-size:.7em;
		   background: #D2232A ;
		   color:white;
		   width:25px;
		   height:25px;
		   text-align:center;
		   line-height:18px;
		   border-radius:50%;
		   box-shadow:0 0 1px #333;
		   border : 2px solid white;
		}
		
		.container{
		width:95%;
		}
    	
		.action-modal {
			display: none;
			position: fixed;
			z-index: 3;
			padding-top: 150px;
			left: 0;
			top: 0;
			width: 100%;
			height: 100%;
			overflow: auto;
			background-color: rgb(0, 0, 0);
			background-color: rgba(0, 0, 0, 0.4);
		}
		
		.action-modal-content {
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
		
		.action-modal-content::-webkit-scrollbar {
			width: 10px;
		}
		
		/* Track */
		.action-modal-content::-webkit-scrollbar-track {
			background: #f1f1f1;
			border-radius: 4px;
		}
		
		/* Handle */
		.action-modal-content::-webkit-scrollbar-thumb {
			background: #888;
			border-radius: 4px;
		}
		
		/* Handle on hover */
		.action-modal-content::-webkit-scrollbar-thumb:hover {
			background: #555;
		}
		
		.action-modal-content b {
			font-weight: 700;
		}
    </style>
    
    <body>
    
    	<div id="notification-modal" class="action-modal">
			<div class="action-modal-content" id="action-modal-body" style="max-width: 500px; text-align: center;">
				<i class='fa-solid fa-triangle-exclamation'></i> <b>Confirm Action</b> <br>
				<br> <div id="action-modal-body-content"></div>
				<div id="action-modal-body-footer" style='width: 100%; margin: auto;'></div>
			</div>
		</div>
		
    	<%@ include file="../adminCommon/header.jsp" %>
        <div class="sz-main-content-wrapper">
        
		
        <!-- Custom breadcrumbs as requirement is diff. Start -->
			<div class="sz-breadcrumb-wrapper">
			    <div class="container-fluid">
			        <ul class="sz-breadcrumbs">
			        		<li><a href="/exam/">Exam</a></li>
			        		<li><a href="/exam/viewAllTests">Tests</a></li>
			        		<li><a href="#">Evaluate Test Answers</a></li>
			        	
			        </ul>
			        <ul class="sz-social-icons">
			            <li><a href="https://www.facebook.com/NMIMSSCE" class="icon-facebook" target="_blank"></a></li>
			            <li><a href="https://twitter.com/NMIMS_SCE" class="icon-twitter" target="_blank"></a></li>
			            <!-- <li><a href="https://plus.google.com/u/0/116325782206816676798/posts" class="icon-google-plus" target="_blank"></a></li> -->
						
			        </ul>
			    </div>
			</div>
			<!-- Custom breadcrumbs as requirement is diff. End -->
        	
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="../adminCommon/left-sidebar.jsp">
								<jsp:param value="" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="../adminCommon/adminInfoBar.jsp" %>
              						<div class="sz-content">
								
											<h2 class="red text-capitalize">View All Online Tests </h2>
											<div class="clearfix"></div>
							<div class="panel-content-wrapper" style="min-height:450px;">
								<%@ include file="../adminCommon/messages.jsp" %>
							
							<!-- Code For Page Goes in Here Start -->
							<% try{ %>
								
<div class="container" >

  <ul class="nav nav-tabs">
    <li class="active" ><a style="color : red;" data-toggle="tab" href="#home" data-badge="${countOfanswerToBeEvaluated }" class="badge-button" >Non-Evaluated </a></li>
    <li  ><a  style="color : green;" data-toggle="tab" href="#menu1" data-badge="${countOfEvaluatedAnswersByFacultyIdNTestId}"  class="badge-button">Evaluated </a></li>
    <li  ><a  style="color : orange;" data-toggle="tab" href="#menu2" data-badge="${countOfCopyCasesAnswers}"  class="badge-button">Copy Cases </a></li>
  </ul>

  <div class="tab-content">
    <div id="home" class="tab-pane fade in active">
   		
		<c:forEach var="answer" items="${answersToBeEvaluated}"
			varStatus="status">

						
			<div class="container" onClick="loadIframeURL(${answer.id} , ${answer.type} , '${answer.uploadType}' , '${fn:replace(fn:replace(answer.answer, '\"', ' '), '\'', ' ')}' )" >			
			  <div class="panel-group" >
			    <div class="panel panel-default">
			      <div class="panel-heading">
			        <h4 class="panel-title">
			          <a data-toggle="collapse" href="#collapse${answer.id}">${status.count}. ${answer.sapid} - ${answer.testName} </a>
			        </h4>
			      </div>			      
			      <div id="collapse${answer.id}"  class="panel-collapse collapse">
			        <div class="panel-body">
			        	<c:if test="${answer.type != 8 }">
			        	<div class="well" >
			        		Question. 
			        		
			        		<div class="container" >
			        			${answer.question}
			        		</div>	
			        	</div>
			        	
			        	<div class="well" >
			        		Answer. 
			        		
			        		<div class="container" >
			        		 	<b style="white-space: pre-wrap;">
			        			${fn:replace(fn:replace(answer.answer, '<', '&lt;'), '>', '&gt;')} 
			        		 	 </b>
			        		</div>	
			        	</div>
			        	</c:if>
			        	<c:if test="${answer.type == 8 }">			        	
			        <!--    <div class="row">
			         <span title="Toggle Fullscreen" id="go-button"
											style="float: right; margin: 0px 15px; padding: 2px 5px 0px 5px; border: 0px solid black; border-radius: 5px; background-color: white; cursor: pointer;">
											<i class="fa-solid fa-maximize" style="font-size: 20px;"></i>
										</span
			        </div> !-->
					        	<div  style="width: 100%;" id="element" data-id="${answer.id}">
					        	
					        	<div class="well" >					        	
						        		
						        		Question. <b>${answer.question}</b><br>
						        		<c:if test="${answer.url ne ''}">
						        		
						        		 	Question Attachment : <a href="/${answer.url}" style="white-space: pre-wrap;"> ${answer.url} </a>
						        		
						        		<!-- div class="container" >
						        			<!-- iframe id="questionLink"
												style="width: 100%; min-height: 500px;" src="https://studentzone-ngasce.nmims.edu/${answer.url}"
												frameborder='0' webkitAllowFullScreen
												mozallowfullscreen allowFullScreen></iframe>
						        		</div!-->
						        		</c:if>	
						        	</div>
					        		<div class="well"  >
					        		
					        		Answer. 
					        		<div class="container" >
					        		<c:if test="${answer.uploadType == 'pdf' }">
					        			<div class="row">
												<span title="Toggle Fullscreen" onClick="openAnswerPdf('${answer.answer}')"
											style="float: right; margin: 0px 15px; padding: 2px 5px 0px 5px; border: 0px solid black; border-radius: 5px; background-color: white; cursor: pointer;">
											<i class="fa-solid fa-maximize" style="font-size: 20px;"></i>
										</span></div>
					        		 	<iframe id="answerLink"
					        		 			class="iFrameClass${answer.id}"
												style="width: 100%; min-height: 500px;" 
												frameborder='0' webkitAllowFullScreen
												mozallowfullscreen allowFullScreen></iframe>
												</c:if>
												<c:if test="${answer.uploadType == 'mp4' }">
												 <embed id="answerLink" src="${answer.answer}" showcontrols="true" style="width: 100%; min-height: 500px;" type="video/webm" ></embed>
												</c:if>
												</div>
											
					        		</div>
				        		</div>
			        		</c:if>
			        	
			        </div>
			        <div class="panel-footer">
			        
			        	<form:form id="${answer.sapid}-${answer.testId}-${answer.attempt}-${answer.questionId}-form" 
			        		action="saveTestAnswerEvaluation" method="post" modelAttribute="answerBean">
							<form:hidden path="id" value="${answer.id }"/> 
							<form:hidden path="testId" value="${answer.testId }"/> 
								
								
							<div class="row">	
										
									<div class="col-md-4 col-sm-6 col-xs-12 column">
										<div class="form-group">	
											<form:label path="marks" for="applicableType">Select Marks To Be Assigned To Above Answer</form:label>
											<form:select id="${answer.sapid}-${answer.testId}-${answer.attempt}-${answer.questionId}-marks" path="marks" 
												type="text" placeholder="select marks" 
												class="form-control" required="required" > 
													 
													 <c:forEach var = "i" begin = "0" end = "${answer.questionMarks}">
												         <form:option  value="${i}" >${i}</form:option>
													  </c:forEach>
													 
											</form:select>
											<small style="color: #708090;">
											  Weightage :  ${answer.questionMarks}
											</small>
										</div>
									</div>	
									
							</div>
							
							
									<div class="row">

										<form:label path="remark" for="remark">Add Remark To Above Answer</form:label>
										<br>
										 

										<form:textarea id="${answer.sapid}-${answer.testId}-${answer.attempt}-${answer.questionId}-remarks"
											class="form-group" path="remark" name="remark" rows="10"
											cols="80" type="textarea" placeholder="Add Remark" value="${answer.remark}"  required="required" />

										

									</div>	
									
									<input type='button' class="btn btn-large btn-primary" value='Save'
										onClick="validateRemarksAndMarks(`${answer.sapid}-${answer.testId}-${answer.attempt}-${answer.questionId}`)" >
									         
						</form:form>
			        
			        </div>
			      </div>
			    </div>
			  </div>
			</div>
						
			
		</c:forEach>
   		
   		
    </div>
    <div id="menu1" class="tab-pane fade">
   		
		<c:forEach var="answer" items="${answersEvaluated}"
			varStatus="status">
			
						
			<div class="container"  onClick="loadEvaluatedIframeURL(${answer.id} , ${answer.type} , '${answer.uploadType}' , '${fn:replace(fn:replace(answer.answer, '\"', ' '), '\'', ' ')}' )">
			  <div class="panel-group">
			    <div class="panel panel-default">
			      <div class="panel-heading">
			        <h4 class="panel-title">
			          <a data-toggle="collapse" href="#collapse${answer.id}">${status.count}. ${answer.sapid} - ${answer.testName} </a>
			        </h4>
			      </div>
			      <div id="collapse${answer.id}" class="panel-collapse collapse">
			        <div class="panel-body">
			        	<c:if test="${answer.type != 8 }">
			        	<div class="well" >
			        		Question. 
			        		
			        		<div class="container" >
			        			${answer.question}
			        		</div>	
			        	</div>
			        	
			        	<div class="well" >
			        		Answer. 
			        		
			        		<div class="container" >
			        		 	<b style="white-space: pre-wrap;"> ${fn:replace(fn:replace(answer.answer, '<', '&lt;'), '>', '&gt;')}  </b>
			        		</div>	
			        	</div>
			        	</c:if>
			        	<c:if test="${answer.type == 8 }">
			      <!--   <div class="row">
			        <span title="Toggle Fullscreen" id="go-button"
											style="float: right; margin: 0px 15px; padding: 2px 5px 0px 5px; border: 0px solid black; border-radius: 5px; background-color: white; cursor: pointer;">
											<i class="fa-solid fa-maximize" style="font-size: 20px;"></i>
										</span>
			        </div> -->
					        	<div  style="width: 100%;" id="element"  data-id="${answer.id}">
					        	
						        	<div class=" well" >
						        		
						        		Question. <b>${answer.question}</b><br>
						        		<c:if test="${answer.url ne ''}">
						        	Question Attachment : <a href="/${answer.url}" style="white-space: pre-wrap;"> ${answer.url} </a>
						        		
						        		<!-- div class="container" >
						        			<iframe id="questionLink"
												style="min-width: 100%; min-height: auto;" src="https://studentzone-ngasce.nmims.edu/${answer.url}"
												frameborder='0' webkitAllowFullScreen
												mozallowfullscreen allowFullScreen></iframe>
						        		</div!-->	
						        		</c:if>
						        	</div>
					        		<div class=" well"  >
					        		
					        		Answer. 
					        			<div class="container" >
						        		 	<c:if test="${answer.uploadType == 'pdf' }">
						        		 	<!-- src="${answer.answer}"!-->
						        		 <div class="row">
												<span title="Toggle Fullscreen" onClick="openAnswerPdf('${answer.answer}')"
											style="float: right; margin: 0px 15px; padding: 2px 5px 0px 5px; border: 0px solid black; border-radius: 5px; background-color: white; cursor: pointer;">
											<i class="fa-solid fa-maximize" style="font-size: 20px;"></i>
										</span></div>
							        		 	<iframe id="answerLink"
														 style="width:100%;min-height:500px	" 
														 class="evaluatedIFrameClass${answer.id}"														 
														frameborder='0' webkitAllowFullScreen
														mozallowfullscreen allowFullScreen></iframe>
											</c:if>
											<c:if test="${answer.uploadType == 'mp4' }">
													 <embed id="answerLink" src="${answer.answer}" showcontrols="true" style="width: 100%; min-height: 500px;" type="video/webm" ></embed>
											</c:if>
										</div>
					        		</div>
				        		</div>
			        		</c:if>
			        	
			        </div>
			        <div class="panel-footer">
			        
			        	<form:form  action="saveTestAnswerEvaluation" method="post" modelAttribute="answerBean">
							<form:hidden path="id" value="${answer.id }"/> 
							<form:hidden path="testId" value="${answer.testId }"/> 
							
														
							<div class="row">	
										
									<div class="col-md-4 col-sm-6 col-xs-12 column">
										<div class="form-group">	
											<form:label path="marks" for="applicableType">Select Marks To Be Assigned To Above Answer</form:label>
											<form:select  path="marks" type="text"	
												placeholder="select marks" 
												 itemValue="${answer.marks}" 
												class="form-control" required="required" > 
													 
													 <c:forEach var = "i" begin = "0" end = "${answer.questionMarks}">
												         
												          <c:if test="${answer.marks == i }">
												         	
												      	   <form:option  value="${i}" selected="selected" >${i}</form:option> 	 
												          </c:if>
												          
												          <c:if test="${answer.marks != i }">
												         	
												      	   <form:option  value="${i}" >${i}</form:option> 	 
												          </c:if>
												         
													  </c:forEach>
													 
											</form:select>
											<small style="color: #708090;">
											  Weightage :  ${answer.questionMarks}
											</small>
										</div>
									</div>	
									
							</div>
							
							
									<div class="row">

										<form:label path="remark" for="remark">Add Remark To Above Answer</form:label>
										<br>
										 
										 <textarea id="remark" name="remark" placeholder="Add Remark" type="textarea" class="form-group" rows="10" cols="80">${answer.remark}</textarea>

										<%-- <form:textarea class="form-group"
											path="remark" name="remark" rows="10"
											cols="80" type="textarea" placeholder="Add Remark" value="" /> --%>

										

									</div>
									
									<button class="btn btn-large btn-primary"
											onClick="return confirm('Are you sure? ')"
											formaction="saveTestAnswerEvaluation">Save</button>
									               							
						
						</form:form>
			        
			        </div>
			      </div>
			    </div>
			  </div>
			</div>
						
			
		</c:forEach>
   		
   		
    </div>

	
    <div id="menu2" class="tab-pane fade">
   		
		<c:forEach var="answer" items="${copyCasesAnswers}"
			varStatus="status">
			
						
			<div class="container">
			  <div class="panel-group">
			    <div class="panel panel-default">
			      <div class="panel-heading">
			        <h4 class="panel-title">
			          <a data-toggle="collapse" href="#collapse${answer.id}">${status.count}. ${answer.sapid} - ${answer.testName} </a>
			        </h4>
			      </div>
			      <div id="collapse${answer.id}"  class="panel-collapse collapse">
			        <div class="panel-body">
			        	<div class="well" >
			        		Question. 
			        		
			        		<div class="container" >
			        			${answer.question}
			        		</div>	
			        	</div>
			        	
			        	<div class="well" >
			        		Answer. 
			        		
			        		<div class="container" >
			        		 	<b style="white-space: pre-wrap;" > ${fn:replace(fn:replace(answer.answer, '<', '&lt;'), '>', '&gt;')}  </b>
			        		</div>	
			        	</div>
			        	
			        	
			        </div>
			        <div class="panel-footer">
			        
			        	<form:form  action="copyCasesIAPlaceholder" method="post" modelAttribute="answerBean">
							<form:hidden path="id" value="${answer.id }"/> 
							<form:hidden path="testId" value="${answer.testId }"/> 
							
														
							<div class="row">	
										
									<div class="col-md-4 col-sm-6 col-xs-12 column">
										<div class="form-group">	
											<form:label path="marks" for="applicableType">Marks Assigned To Above Answer</form:label>
											<form:select  path="marks" type="text"	
												placeholder="select marks" 
												 itemValue="${answer.marks}" 
												class="form-control" required="required" > 
													 
												    <form:option  value="0" selected="selected" >0</form:option> 
													 
											</form:select>
										</div>
									</div>	
									
							</div>
							
							
									<div class="row">

										<form:label path="remark" for="remark"> Remark To Above Answer</form:label>
										<br>
										 
										 <textarea id="remark" name="remark" placeholder="Add Remark" type="textarea" class="form-group" rows="10" cols="80">${answer.remark}</textarea>

										<%-- <form:textarea class="form-group"
											path="remark" name="remark" rows="10"
											cols="80" type="textarea" placeholder="Add Remark" value="" /> --%>

										

									</div>
									
									               							
						
						</form:form>
			        
			        </div>
			      </div>
			    </div>
			  </div>
			</div>
						
			
		</c:forEach>
   		
   		
    </div>
	

  </div>
</div>
							<%
							}catch(Exception e ){
								
							}
							%>	
							
							<!-- Code For Page Goes in Here End -->
							</div>
							
							</div>
              			</div>
    				</div>
			   </div>
		    </div>
        <jsp:include page="../adminCommon/footer.jsp"/>
        
		<script>	
		
		
	$('.tables').DataTable( {
        initComplete: function () {
            this.api().columns().every( function () {
                var column = this;
                var headerText = $(column.header()).text();
                console.log("header :"+headerText);
                if(headerText == "Subject")
                {
                   var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
                    .appendTo( $(column.header()) )
                    .on( 'change', function () {
                        var val = $.fn.dataTable.util.escapeRegex(
                            $(this).val()
                        );
 
                        column
                            .search( val ? '^'+val+'$' : '', true, false )
                            .draw();
                    } );
 
                column.data().unique().sort().each( function ( d, j ) {
                    select.append( '<option value="'+d+'">'+d+'</option>' )
                } );
              }
            } );
        }
    } );

	/* Get into full screen */
	function GoInFullscreen(element) {
		if(element.requestFullscreen)
			element.requestFullscreen();
		else if(element.mozRequestFullScreen)
			element.mozRequestFullScreen();
		else if(element.webkitRequestFullscreen)
			element.webkitRequestFullscreen();
		else if(element.msRequestFullscreen)
			element.msRequestFullscreen();
	}

	/* Get out of full screen */
	function GoOutFullscreen() {
		if(document.exitFullscreen)
			document.exitFullscreen();
		else if(document.mozCancelFullScreen)
			document.mozCancelFullScreen();
		else if(document.webkitExitFullscreen)
			document.webkitExitFullscreen();
		else if(document.msExitFullscreen)
			document.msExitFullscreen();
	}

	/* Is currently in full screen or not */
	function IsFullScreenCurrently() {
		var full_screen_element = document.fullscreenElement || document.webkitFullscreenElement || document.mozFullScreenElement || document.msFullscreenElement || null;
		
		// If no element is in full-screen
		if(full_screen_element === null)
			return false;
		else
			return true;
	}

	$("#go-button").on('click', function() {
		if(IsFullScreenCurrently())
			GoOutFullscreen();
		else
			GoInFullscreen($("#element").get(0));
	});

	$(document).on('fullscreenchange webkitfullscreenchange mozfullscreenchange MSFullscreenChange', function() {
		if(IsFullScreenCurrently()) {
			$("#element #spanDiv").text('Full Screen Mode Enabled');
			
			$("#go-button i").removeClass("fa-maximize");
			
			$("#go-button i").addClass("fa-down-left-and-up-right-to-center");	
			
		}
		else {
			$("#element #spanDiv").text('Full Screen Mode Disabled');

			$("#go-button i").removeClass("fa-down-left-and-up-right-to-center");
			
			$("#go-button i").addClass("fa-maximize");
		}
	});


	function loadIframeURL(answerId , answerType , answerUploadType , answerAnswer){	
		var iFrameClass = "iFrameClass"+answerId
		if(answerType == 8 && answerUploadType == 'pdf'){

			  var x = document.getElementsByClassName(iFrameClass);
			  x[0].src = answerAnswer+"#zoom=100";
			  
		}		
		
	}
	
	function loadEvaluatedIframeURL(answerId , answerType , answerUploadType , answerAnswer){
	
	var iFrameClass = "evaluatedIFrameClass"+answerId
	if(answerType == 8 && answerUploadType == 'pdf'){

		  var x = document.getElementsByClassName(iFrameClass);
		  x[0].src = answerAnswer+"#zoom=100";
		  
	}	
	
	}
	function openAnswerPdf(answerURL){

		  window.open(answerURL);
	}

	function validateRemarksAndMarks(id){
		let marks  = $('#'+id+"-marks").val();
		let remarks = $('#'+id+"-remarks").val();
		
		if(typeof(remarks) == "undefined" || remarks == null || remarks == ""){
			console.debug("triggring modal with only close option")
			$('#action-modal-body-content').html("<b>Please enter reamrks.</b>");
			$('#action-modal-body-footer').html("<button type='button' class='btn btn-primary' onclick='document.getElementById(`notification-modal`).style.display = `none`' "+
					"style='margin: 10px;'>Close</button>")
			$('#notification-modal').toggle()
			return
		}
		if( marks == 0 ){
			$('#action-modal-body-content').html("<b>Are you sure you want to award zero marks to the student?</b>")
			$('#action-modal-body-footer').html("<button type='button' class='btn btn-primary ' style='margin: 10px;' onclick=saveAnswer(`"+id+
					"`)>Proceed</button><button type='button' class='btn btn-primary' onclick='document.getElementById(`notification-modal`).style.display = `none`' "+
					"style='margin: 10px;'>Close</button>")
			$('#notification-modal').toggle()
			return
		}
		if(typeof(remarks) !== "undefined" && remarks !== null && remarks !== "" &&  marks != 0){
			saveAnswer(id)
			return
		}
	}
	function saveAnswer(id){
		console.debug('id: '+id)
		document.getElementById(id+'-form').submit();
		return
	}
	
	</script>
		
    </body>    
</html>