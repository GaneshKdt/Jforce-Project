<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>


<html lang="en">


<jsp:include page="./common/jscss.jsp">
	<jsp:param value="Free Certification Program" name="title" />
</jsp:include>
<link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">

<style>
	.card-locked, .card-locked a, .card-locked p, .card-locked li {
		color: #7f7f7f;
	}
	
    .card-button {
    	background:none;
    	color:#d2232a;
    }
    @media(max-width: 992px){
    	.card-button {
    		float:right;
    	}
    }
    @media(min-width: 992px){
    	.card-button {
	    	margin-top:0px;
	    	margin-bottom:0px;
	    	margin-left:10px;
	    	margin-right:10px;
	    	padding:0px;
	    	white-space: normal;
    		float:right;
    	}
    }
</style>

<style>
	#fullPageLoading {
		position : fixed;
		height : 100%;
		width : 100%;
		z-index : 10;
		display : flex;
	}
	#loader-container {
		margin-top : auto;
		margin-bottom : auto;
		margin-left : auto;
		margin-right : auto;
		background-color : white;
		padding : 20px;
		border-radius : 5px;
		z-index: 11111;
		text-align: center;
	}
	
	#loader {
		border: 16px solid #f3f3f3; /* Light grey */
		border-top: 16px solid #d2232a; /* Blue */
		border-radius: 50%;
		width: 120px;
		height: 120px;
		animation: spin 2s linear infinite;
	}
	
	@keyframes spin {
		0% { transform: rotate(0deg); }
		100% { transform: rotate(360deg); }
	}
</style>
<body>

	<%@ include file="./common/header.jsp"%>



	<div class="sz-main-content-wrapper">
		<div class="sz-breadcrumb-wrapper">
   			<div class="container-fluid">
       			<ul class="sz-breadcrumbs">
	        		<li><a href="/studentportal/home">Student Zone</a></li> 
	        		<li><a href="student/getFreeCoursesList">My Courses</a></li>
	        		<li><a href="student/getProgramSubjectList?id=${cpsId}">Subjects</a></li>
		        </ul>
          	</div>
        </div>

<%-- 		<%@ include file="./common/breadcrum.jsp"%> --%>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="common/left-sidebar.jsp">
					<jsp:param value="Get Certified" name="activeMenu" />
				</jsp:include>



				<div class="sz-content-wrapper examsPage">
					<%@ include file="./common/studentInfoBar.jsp"%>


					<div class="sz-content">

						<h2 class="red text-capitalize">Subject List</h2>
						<div class="clearfix"></div>
						<%@ include file="./common/messages.jsp"%>
						<div class="row"> 
							<c:forEach items="${subjectList}" var="subjectBean" varStatus="loop">
							
								<c:choose>
								    <c:when test="${subjectBean.unlocked}">
								        <c:set var="hrefClass" value="encodedHref"/>
								        <c:set var="cardClass" value=""/>
								    </c:when>
								    <c:otherwise>
								        <c:set var="hrefClass" value=""/>
								        <c:set var="cardClass" value="card-locked"/>
								    </c:otherwise>
								</c:choose>
								<c:choose>
									<c:when test="${ !loop.last }">
										<div class="col-md-4  ${ cardClass }">
											<div class="panel-content-wrapper subject-info">
												<c:if test="${ !subjectBean.unlocked }">
													<div style="position:absolute;background-color:rgba(255,255,255,0.5);width:80%;height:80%;">
													</div>
												</c:if>
												<div class="row">
													<div class="col-xs-9 subject-name" style="display: flex; flex-direction: column;">
														<a data-pssid="${subjectBean.program_sem_subject_id }" class="${ hrefClass }">
															<span style="font-weight:bold;font-size:16px;">${subjectBean.subjectName }</span> 
														</a>
														<br>
														<p style="margin-top: auto; margin-bottom: 0px">Module - ${ subjectBean.sem }</p>
													</div>
													<div class="col-xs-3">
														<div style="font-weight:bold;font-size:20px;float:right;color:rgba(0,0,0,0.5)">
															<c:choose>
																<c:when test="${ subjectBean.completed }">
																	<img src="assets/images/icons/certificate.PNG" style="max-width:25px;"/>
																</c:when>
																<c:when test="${ subjectBean.unlocked }">
																	<i class="fa fa-unlock" aria-hidden="true"></i>
																</c:when>
																<c:otherwise>
																	<i class="fa fa-lock" aria-hidden="true"></i>
																</c:otherwise>
															</c:choose>
														</div>
													</div>
												</div>
												<hr />
												<div class="row subject-details" style="color:rgba(0,0,0,0.5) !important">
													<div class="col-md-12 subject-description">
														<p data-description = "${ subjectBean.subjectDescription }">${ subjectBean.subjectDescription }</p>
													</div>
													<div class="col-md-12">
														<c:if test="${ subjectBean.quizAttemptsTaken > 0 }">
															<p style="float: left; padding-top: 5px;"><b>Score Obtained</b>: ${ subjectBean.quizScore } / ${ subjectBean.quizMaxScore }</p>
														</c:if>
													</div>
												</div>
												
												<hr />
												<div class="row" style="color:rgba(0,0,0,0.5) !important">
												
													<div class="col-xs-4 text-center">
														<a data-pssid="${subjectBean.program_sem_subject_id }" data-param="session" class="${ hrefClass }">
															<i class="material-icons sessionplan-icon">play_circle_outline</i>
															<p>Sessions</p>
														</a>
													</div>
													
													<div class="col-xs-4 text-center">
														<a data-pssid="${subjectBean.program_sem_subject_id }" data-param="resource" class="${ hrefClass }">
															<i class="material-icons sessionplan-icon">library_books</i>
															<p>Resources</p>
														</a>
													</div>
			
													<div class="col-xs-4 text-center">
														<a data-pssid="${subjectBean.program_sem_subject_id }" data-param="quiz" class="${ hrefClass }">
															<i class="material-icons sessionplan-icon">assessment</i>
															<p>Quiz</p>
														</a>
													</div>
												</div>
											</div>
										</div>
									</c:when>
									<c:otherwise>
										<div class="col-md-4 ${ cardClass }">
											<div class="panel-content-wrapper">
												<div class="row">
													<div class="col-xs-9">
														<span class="${ hrefClass }">
															<span style="font-weight:bold;font-size:16px;">${  subjectBean.subjectName }</span> 
														</span>
													</div>
													<div class="col-xs-3">
														<div style="font-weight:bold;font-size:20px;float:right;color:rgba(0,0,0,0.5)">
															<c:choose>
																<c:when test="${ subjectBean.unlocked }">
																	<i class="fa fa-unlock" aria-hidden="true"></i>
																</c:when>
																<c:otherwise>
																	<i class="fa fa-lock" aria-hidden="true"></i>
																</c:otherwise>
															</c:choose>
														</div>
													</div>
												</div>
												<hr />
												<div class="text-center">
													<div style="margin: auto;">
														<c:choose>
															<c:when test="${ subjectBean.completed }">
																<img src="assets/images/icons/certificate.PNG" style="max-width:30px;"/>
															</c:when>
															<c:otherwise>
																<img src="assets/images/certificate_black.PNG" style="max-width:30px;"/>
															</c:otherwise>
														</c:choose>
														<p>Program Certification Final Assessment</p>
														<c:if test="${ !subjectBean.unlocked }">
															<p>
																<img src="assets/images/lock_round.PNG" style="max-width:20px;" /> Complete all modules to give the final certification exam.
															</p>
														</c:if>
														<div class="col-md-12">
															<c:if test="${ subjectBean.quizAttemptsTaken > 0 }">
																<p><b>Score Obtained</b>: ${ subjectBean.quizScore } / ${ subjectBean.quizMaxScore }</p>
															</c:if>
															<c:if test="${ subjectBean.quizAttemptsTaken > 0 && subjectBean.quizAttemptsLeft > 0 }">
																<p><b>Attempts</b> :${ subjectBean.quizAttemptsTaken }/${ subjectBean.quizAttemptsTaken + subjectBean.quizAttemptsLeft }</p>
															</c:if>
														</div>
													</div>
												</div>
												<div class="clearfix"></div>
												<hr/>
												<div>
													<div>
														<div class="col-md-12">
															<div class="row">
																<c:choose>
																	<c:when test="${ subjectBean.completed }">
																		<button type="submit" class="btn btn-link card-button downloadCertificate" 
																			data-lead-id="${ subjectBean.leads_id }" data-certificate-type="completion"
																			data-consumer-program-structure-id = "${ subjectBean.consumerProgramStructureId }">
																			Download Certificate
																		</button>
																	</c:when>
																	<c:when test="${ subjectBean.unlocked && subjectBean.quizAttemptsTaken > 0 && subjectBean.quizAttemptsLeft <= 0 }">
																		<button type="submit" class="btn btn-link card-button downloadCertificate" 
																			data-lead-id="${ subjectBean.leads_id }" data-certificate-type="participation"
																			data-consumer-program-structure-id = "${ subjectBean.consumerProgramStructureId }">
																			Download Certificate
																		</button>
																	</c:when>
																</c:choose>
																<c:choose>
																	<c:when test="${ subjectBean.unlocked && subjectBean.quizAttemptsLeft > 0 && subjectBean.quizAttemptsTaken > 0 }">
																		<a class="btn btn-link card-button float-right ${ hrefClass }" data-pssid="${subjectBean.program_sem_subject_id }" data-param="finalAssessQuiz">
																			View Quiz
																		</a>
																	</c:when>
																	<c:when test="${ subjectBean.unlocked }">
																		<a class="btn btn-link card-button float-right ${ hrefClass }" data-pssid="${subjectBean.program_sem_subject_id }" data-param="finalAssessQuiz">
																			Start Quiz
																		</a>
																	</c:when>
																	<c:otherwise>
																		<div class="col-md-12">
																			<button type="submit" class="btn btn-link float-right" style="background: none;color: #d2232a;float: right;" onclick="alert('Complete all modules to unlock')" disabled>
																				Start Quiz
																			</button>
																		</div>
																	</c:otherwise>
																</c:choose>
															</div>
														</div>
														<div class="clearfix"></div>
														
													</div>
												</div>
											</div>
										</div>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<jsp:include page="./common/footer.jsp" />

	<script>
		$('.encodedHref').each(function() {
   			var pssId = $(this).attr("data-pssid");
   			var param = $(this).attr("data-param");
   			$(this).attr("href","viewModuleDetails?pssId="+pssId+"&activeMenu="+param); 
   		});
		
		$(window).on('resize', function(){
			manageHeights('.subject-name')
			
			autoManageEllipsesText()
			manageHeights('.subject-description')
			manageHeights('.subject-details')
			manageHeights('.subject-info')
		});
		$(window).on('load', function(){
			manageHeights('.subject-name')
			autoManageEllipsesText()
			manageHeights('.subject-description')
			manageHeights('.subject-details')
			manageHeights('.subject-info')
		});
		
		function autoManageEllipsesText() {
			var textContainerHeight = '70';
			$('.subject-description p').each(function () {
				$(this).html($(this).attr('data-description'))
				var $ellipsisText = $(this);
				while ($ellipsisText.outerHeight(true) > textContainerHeight) {
					$ellipsisText.text(function(index, text) {
						return text.replace(/\W*\s(\S)*$/, '...');
					});
				}
			});
		}
		
		function manageHeights(className) {
			$(className).css('height', '')
			var maxHeight = 0;
			$(className).each(function() {
				var height = $(this).height();
				if(height > maxHeight) {
					maxHeight = height
				}
			})
			
			$(className).each(function() {
				$(this).height(maxHeight);
			})
			
			
		}
	</script>

	<script>
		$('.downloadCertificate').on('click', function() {
			var leads_id = $(this).attr("data-lead-id");
			var consumerProgramStructureId = $(this).attr("data-consumer-program-structure-id");
			var certificateType = $(this).attr("data-certificate-type");

    		$('body').prepend(`
    			<div id="fullPageLoading">
    				<div class="modal-backdrop fade in"></div>
    				<div id="loader-container">
    			 		<div id="loader"></div>
    					<div> Please wait... </div>
    				</div>
    			</div>`)
			$.ajax({
	            url: '/exam/m/genreateCertificateForLead',
	            type: 'post',
	            dataType: 'json',
	            contentType: 'application/json',
	            data: JSON.stringify({
	                "leadId": leads_id,
	                "consumerProgramStructureId": consumerProgramStructureId,
	                "certificateType": certificateType
	            }),
	            success: function (data) {
	            	$('#fullPageLoading').fadeOut(200);
	            	if(data.error == 'true') {
	            		alert(data.errorMessage)
	            		return;
	            	}
	            	
	            	if(!data.download_url) {
	            		alert('Error generating certificate!')
	            		return;
	            	}
	            	window.open(data.download_url, "_blank")
	            },
	            error : function(error) {
	            	$('#fullPageLoading').fadeOut(200);
	            	alert()	
	            }
	        });

			setTimeout(function(){ $('#fullPageLoading').fadeOut(200); }, 15000);
		})
	</script>
</body>
</html>