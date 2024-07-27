<!DOCTYPE html>


<%@page import="com.nmims.controllers.BaseController"%>
<%@page import="com.nmims.helpers.*"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.nmims.beans.AssignmentStudentPortalFileBean"%>
<%@page import="java.util.Calendar"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<c:set var = "perspective" scope="page" value = '<%=((StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal")).getPerspective()%>'/>


<html lang="en">

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')" var="server_path" />


<jsp:include page="common/jscss.jsp">
	<jsp:param value="Welcome to Student Zone" name="title" />
</jsp:include>
<style>

		.fullPageLoading {
			position : fixed;
			height : 100%;
			width : 100%;
			z-index: 9999;
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
	.popup{
		padding:2rem;
		position: fixed;
	    top: 25%;
	    right: 35%;
	    width: 550px;
	    z-index: 11111;
	    text-align: left;
	    transition: 1s ease; 
	    transform: translate(1em, 50px); 
	}
	.popup-inner{
	color: black;
	    padding: 1em 1.5em 1.5em;
	    transition: box-shadow 0.3s ease;
	    background: transparent;
	    border-radius: 1em / 1em;
	    overflow: hidden;
	    position: relative;
	    background-color: white; 
	    content: "";
	    border-width: 0;
	    position: absolute;
	    box-sizing: border-box;
	    width: 100%; 
	    z-index: -1; 
	}
	
	.popup .title{
		display: block;
	    font-size: 1.2em;
	    margin: 0 0 1em 0 
	}
	.popup .para{
		color:#333;margin: 0 0 1em 0 }
	.close-btn:hover {
	    text-decoration: underline;
	}
	@media ( max-width : 768px) { 
		.popup{
			width: 300px;   
		}
	}@media ( max-width : 425px) { 
		.popup{
			width: 220px;   
		}
	}
	</style>
    <body>
    <div class="fullPageLoading">
		<div class="modal-backdrop fade in"></div>
		<div id="loader-container">
	 		<div id="loader"></div>
			<div> Please wait... </div>
		</div>
	</div>
	<%@ include file="common/header.jsp"%>


	<div class="sz-main-content-wrapper">

		<jsp:include page="common/breadcrum.jsp">
			<jsp:param value="Student Zone;Dashboard" name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="common/left-sidebar.jsp">
					<jsp:param value="Dashboard" name="activeMenu" />
				</jsp:include>


				<div class="sz-content-wrapper dashBoard withBgImage">
					<%@ include file="common/studentInfoBar.jsp"%>

					<%try{ %>
					<div class="sz-content large-padding-top">
						<div class="clearfix" style="padding-bottom: 20px;"></div>
						<%@ include file="common/messages.jsp"%>
						<%if("true".equals( (String)request.getSession().getAttribute("markedForUFM"))) { %>
							<div class="alert alert-danger alert-dismissible">
								<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times;  </button>
								Please click <a href = '/exam/ufmStatus'>here</a> to view the status of UFM notices from University. 
							</div>
						<%} %>
						<div class="panel-group" id="accordion" role="tablist"
							aria-multiselectable="true">
							
							<!-- Modal If Demo Exam Pending -->
							<c:if test="${isDemoExamPending}">
								<div>
								  <div class="modal fade" id="myModal" role="dialog">
								    <div class="modal-dialog">
								      <div class="modal-content">
								        <div class="modal-header">
								          <button type="button" class="close" data-dismiss="modal">&times;</button>
								          <h4 class="modal-title">Demo Exam Pending</h4>
								        </div>
								        <div class="modal-body">
								        <p>It is Mandatory to Attend the Demo Exam before giving your Final Exam. 
											Your Demo Exam is pending. <a href="/exam/viewModelQuestionForm">Click here</a> to complete Your Demo Exam.</p>								        </div>
								      </div>
								    </div>
								  </div>
								</div>
							</c:if>
							
							<%
							System.out.println("studentHome/teeExams.jsp");
							try{ %>
							<%@ include file="studentHome/teeExams.jsp"%>
							<%}catch(Exception e){e.printStackTrace();} %>

							<%try{ %>
							<%@ include file="studentHome/academicCalendar.jsp"%>
							<%}catch(Exception e){e.printStackTrace();} %>
							
							<div class="row">
								<% try{ %>
									<%@ include file="studentHome/homeSessions.jsp"%>
								<%}catch(Exception e){e.printStackTrace();} %>
							</div>
										
							<div class="row">
								<div class="col-md-4">
									<%@ include file="studentHome/courses.jsp"%>
								</div>

								<div class="col-md-8">
									<%@ include file="studentHome/assignments.jsp"%>
									<!-- Internal Assessment Tests -->
									<%-- 
              														<%@ include file="studentHome/iATests.jsp" %> --%>
								</div>
							</div>


							<!--  <%@ include file="studentHome/results.jsp"%>-->
							
							<div class="row">
								<div class="col-md-8">
									<% System.out.println("tudentHome/announcements.jsp"); %>
									<%@ include file="studentHome/announcements.jsp"%>
								</div>
								
								<div class="col-md-4">
									<div class="ranks">
										<div class="panel panel-default">
											<div class="panel-heading" role="tab" id="">
												<h4 class="panel-title">RANK</h4>
												<ul class="topRightLinks list-inline">
													<li><a class="panel-toggler collapsed" role="button"
														data-toggle="collapse" data-parent="#accordion"
														href="#collapseRank" aria-expanded="false"></a></li>
												</ul>
												<div class="clearfix"></div>
											</div>
											<div id="collapseRank"
												class="panel-collapse collapse in courses-panel-collapse"
												role="tabpanel" aria-labelledby="headingRank">
												<div class="panel-body">
									
													<div class="p-closed">
														<div class="no-data-wrapper">
															<p class="no-data">
																<span class="icon-exams"></span>Rank List
															</p>
														</div>
													</div>
													<div class="rankDiv rankStudents">
														<a href="/studentportal/student/ranks">Click here to see the ranks</a>									
													</div>
													
												</div>
											</div>
										</div>
									</div>									
								</div>
							</div>
							<% 
								String display="", width="";
								if(bcon.checkLead(request, response)){
									display="display: none;";
									width="width: 100%;";
								}
							%>
							
							<% if(!bcon.checkLead(request, response)){ %>
							<div class="row">
								<div class="col-md-8" style="<%= width %>">
									<% System.out.println("tudentHome/serviceRequest.jsp"); %>
									<%@ include file="studentHome/serviceRequest.jsp"%>
								</div>
							
								<div class="col-md-4" style="<%= display %>">
									<% System.out.println("tudentHome/quickLinks.jsp"); %>
									<%@ include file="studentHome/quickLinks.jsp"%>
								</div>
								<div class="clearfix"></div>
							</div>
							<% } %>

						</div>
					</div>
				</div>


			</div>
		</div>
	</div>

	<div id="examApp"></div>
	<div id="acadsApp"></div>
	<div id="csApp"></div>
	<div id="ltiApp"></div>
	<% if(bcon.checkLead(request, response)){ %>
	<c:if test="${empty perspective}">
	<div class="modal-backdrop fade in"></div>
	 <div class="popup " > 
		<div class="popup-inner">
		 
		<span class="title">Choose A Perspective</span>
		<p class="para">Switch to a view to find how experienced and free course student can view.</p>
			<div class="row" > 
				 <a href="setPerspectiveForLeads?perspective=free"><div class="col-md-6"><div style="width:100%" class="btn btn-primary">Free-Course</div></div></a>
				 
				 <a href="setPerspectiveForLeads?perspective=experienced"><div class="col-md-6"><div style="width:100%"class="btn btn-primary">Experience Student Portal</div></div></a> 
			${perspective }
			</div>
		</div>	 
	</div>
	</c:if>
	<%} %>  
	<jsp:include page="common/footer.jsp" />

	<jsp:include page="common/SSOLoader.jsp" />


<%--			
// 		$(document).ready(function(){
// 			var status = document.getElementById("access").value;
// 			console.log(status);
// 			if(status === "false"){
// 				alert("Please enrole for the complete course...");
 				<% request.getSession().setAttribute("access", true); %>
// 			}
// 	    }); --%>
			</script>

	<!-- Conversion Pixel - NMIMS_ExistingStudent_8378369 - DO NOT MODIFY -->
	<script src="https://secure.adnxs.com/px?id=839244&seg=8378369&t=1"
		type="text/javascript"></script>
	<!-- End of Conversion Pixel -->	

<script>
	$(document).ready(function(){
		$("#myModal").modal('show');
	});
	
	
	$.ajax({
		   type : "POST",
		   contentType : "application/json",
		   url : "m/reRegForMobile",   
		   data : JSON.stringify(data),
		   success : function(data) {
			   if(data.success==true){
				   $(".re_reg_li").css("display","block");
			   }
		   } 
	});
</script>
</body>

<%}catch(Exception e){
    	e.printStackTrace();
    	}%>

</html>