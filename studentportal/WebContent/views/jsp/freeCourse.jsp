<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html lang="en">


<jsp:include page="./common/jscss.jsp">
	<jsp:param value="Free Certification Program" name="title" />
</jsp:include>

<style>
	.border {
		border-left: 1px solid #eee;
    }
    .text-center {
	    text-align: center;
    }
    .card-button {
    	background:none;color:#d2232a;float:right;
    }
    .program-name {
    	font-weight:bold;
    	font-size:22px;
    }
	@media only screen and (min-width: 992px) {
	    .align-items-bottom {
	    	display: flex;
	    	align-items: flex-end;
	    }
		.d-md {
			display: none !important;
		}
		
		.certified-stamp {
		    margin-left: auto;
		    margin-right: 25px;
    	}
	}
	@media only screen and (max-width: 993px) {
		.d-sm-none {
			display: none !important;
		}
		.certified-stamp {
		    float: right;
		    margin-right: 25px;
    	}
	}
	.course-list {
    	position: relative;
	}
	.certified-stamp {
	    transform: rotate(-15deg);
	    max-width:75px;
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

		<!-- Breadcrum added on free courses -->
		<%-- <%@ include file="./common/breadcrum.jsp"%> --%>
		
		<jsp:include page="common/breadcrum.jsp">
			<jsp:param value="Student Zone;Free Courses" name="breadcrumItems" />
		</jsp:include>

		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
			    <c:if test = "${isLoginAsLead ne true}">
				    <jsp:include page="common/left-sidebar.jsp">
						<jsp:param value="Get Certified" name="activeMenu" />
					</jsp:include>
			    </c:if>

				<div class="sz-content-wrapper examsPage">
					<%@ include file="./common/studentInfoBar.jsp"%>


					<div class="sz-content">
						<%@ include file="./common/messages.jsp"%>
						
						<c:choose>
							<c:when test = "${ hasOnGoingPrograms || hasNotEnrolledPrograms || hasCompletedPrograms}">
								<h2 class="red text-capitalize">Current Programs</h2>
								<div class="clearfix"></div>
								<c:choose>
									<c:when test = "${ hasOnGoingPrograms }">
										<c:forEach items="${onGoingPrograms}" var="program">
											<div class="panel-content-wrapper course-list">
												<div class="row">
													<div class="col-md-9 col-lg-10">
														<p><span class="program-name">${program.programname }</span>
														<p>${program.description }</p>
														<p> Program Duration : <span class="text-capitalize">${program.programDuration} ${program.programDurationUnit}</span> </p>
													</div>
													<div class="col-md-3 col-lg-2">
														<a class="btn btn-link card-button" href="getProgramSubjectList?id=${program.consumerProgramStructureId }">
															Continue
														</a>
														<c:if test="${ program.certificate != null }">
															<button type="submit" class="btn btn-link downloadCertificate card-button" 
																data-lead-id="${ program.certificate.leads_id }" data-certificate-type="${ program.certificate.certificateType }"
																data-consumer-program-structure-id = "${ program.certificate.consumerProgramStructureId }">
																Download Certificate
															</button>
															<button type="button"
																class="btn btn-link shareCertificate card-button"
																data-lead-id="${ program.certificate.leads_id }"
																data-certificate-type="${ program.certificate.certificateType }"
																data-consumer-program-structure-id="${ program.certificate.consumerProgramStructureId }">
																Share Certificate</button>
														</c:if>
													</div>
												</div>
											</div>
										</c:forEach>
									</c:when>
									<c:otherwise>
										<div class="panel-content-wrapper">
											<h3 style="float:left !important;">No active certification courses found.</h3>
											<div class="clearfix"></div>
										</div>
									</c:otherwise>
								</c:choose>
								<hr/>
								
								<h2 class="red text-capitalize">Available Programs</h2>
								<div class="clearfix"></div>
								
								<c:choose>
									<c:when test = "${ hasNotEnrolledPrograms }">
										<c:forEach items="${ programsBeanNotEnrolledList }" var="program">
											<div class="panel-content-wrapper course-list">
												<div class="row">
													<div class="col-md-9 col-lg-10">
														<p><span class="program-name">${ program.programname }</span> </p>
														<p>${ program.description }</p>
														<p> Program Duration : <span class="text-capitalize">${program.programDuration} ${program.programDurationUnit}</span> </p>
													</div>
													<div class="col-md-3 col-lg-2">
														<form action="registerFreeCourse" method="POST">
															<input type="hidden" name="consumerProgramStructureId" value="${program.consumerProgramStructureId}" />
															<input type="hidden" name="programName" value="${ program.programname }">
															<button type="submit" class="btn btn-link card-button">Enroll Now</button>
														</form>
													</div>
												</div>
											</div>
										</c:forEach>
									</c:when>
									<c:otherwise>
										<div class="panel-content-wrapper">
											<h3 style="float:left !important;">No new certification courses found.</h3>
											<div class="clearfix"></div>
										</div>
									</c:otherwise>
								</c:choose>
								
								<hr/>
								<h2 class="red text-capitalize">Completed Programs</h2>
								<div class="clearfix"></div>
								<div class="clearfix"></div>
								<c:choose>
									<c:when test = "${ hasCompletedPrograms }">
										<c:forEach items="${completedPrograms}" var="program">
											<div class="panel-content-wrapper course-list">
												<div class="row">
												
													<span class="d-md">
														<c:choose>
															<c:when test="${ program.certificate.certificateType == 'participation' }">
																<img class="certified-stamp" src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/images/participated.png" />
															</c:when>
															<c:otherwise>
																<img class="certified-stamp" src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/images/certified.png"/>
															</c:otherwise>
														</c:choose>
													</span>
													<div class="col-md-9 col-lg-10">
														<p><span class="program-name">${program.programname }</span>
														<p>${program.description }</p>
														<p> Program Duration : <span class="text-capitalize">${program.programDuration} ${program.programDurationUnit}</span> </p>
													</div>
													<div class="col-md-3 col-lg-2">
														<span class="d-sm-none" style="display: flex; width: 100%">
															<c:choose>
																<c:when test="${ program.certificate.certificateType == 'participation' }">
																	<img class="certified-stamp" src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/images/participated.png" />
																</c:when>
																<c:otherwise>
																	<img class="certified-stamp" src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/images/certified.png"/>
																</c:otherwise>
															</c:choose>
														</span>
														<a class="btn btn-link card-button" href="getProgramSubjectList?id=${program.consumerProgramStructureId }">
															View Program
														</a>
														<button type="submit" class="btn btn-link card-button downloadCertificate" 
															data-lead-id="${ program.certificate.leads_id }" data-certificate-type="${ program.certificate.certificateType }"
															data-consumer-program-structure-id = "${ program.certificate.consumerProgramStructureId }">
															Download Certificate
														</button>
														<button type="button"
															class="btn btn-link shareCertificate card-button"
															data-lead-id="${ program.certificate.leads_id }"
															data-certificate-type="${ program.certificate.certificateType }"
															data-consumer-program-structure-id="${ program.certificate.consumerProgramStructureId }">
															Share Certificate</button>
													</div>
												</div>
											</div>
										</c:forEach>
									</c:when>
									<c:otherwise>
										<div class="panel-content-wrapper">
											<h3 style="float:left !important;">You have not completed any courses.</h3>
											<div class="clearfix"></div>
										</div>
									</c:otherwise>
								</c:choose>
							</c:when>
							<c:otherwise>
								<div class="panel-content-wrapper" style="margin-top:20px">
									<h3 style="float:left !important;">No certification courses found.</h3>
									<div class="clearfix"></div>
								</div>
							</c:otherwise>
						</c:choose>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<jsp:include page="./common/footer.jsp" />
	<jsp:include page="./common/SSOLoader.jsp" />

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
		});
		
		$('.shareCertificate').on('click', function() {
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
	    			</div>`);
	    			
			$.ajax({
	            url: '/exam/m/generateCertificateForLead',
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
	
	
	            	var url = "https://www.linkedin.com/profile/add?startTask=CERTIFICATION_NAME&name="
						+ data.programName
						+ "&organizationId=13365633"						
						+ "&issueYear="
						+ new Date().getFullYear()
						+ "&issueMonth="
						+ new Date().getMonth()
						+ "&expirationYear="
						/* + $("#expirationYear").val() */
						+ "&expirationMonth="
						/* + $("#expirationMonth").val() */
						+ "&certUrl="
						+ data.download_url
						+ "&certId=" + <%=Integer.toHexString(Integer.parseInt(lead.getConsumerProgramStructureId()))%>;
	
					window.open(url,'_blank');
	            },
	            error : function(error) {
	            	$('#fullPageLoading').fadeOut(200);
	            	alert()	
	            }
        	});
			setTimeout(function(){ $('#fullPageLoading').fadeOut(200); }, 15000);
		});
	</script>
	


</body>
</html>