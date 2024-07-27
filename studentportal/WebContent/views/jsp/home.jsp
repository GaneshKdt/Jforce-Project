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

<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')" var="server_path" />


 <jsp:include page="common/jscssNew.jsp">
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
    .sz-content-wrapper{
           margin-bottom: 0;
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
	<%@ include file="common/headerDemo.jsp"%>

 
	<div class="sz-main-content-wrapper">

		<jsp:include page="common/breadcrum.jsp">
			<jsp:param value="Student Zone;Dashboard" name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
	      <div id="sticky-sidebar">  
				<jsp:include page="common/left-sidebar.jsp">
					<jsp:param value="Dashboard" name="activeMenu" />
				</jsp:include>
               </div> 
				<div class="sz-content-wrapper dashBoard withBgImage">
					<%@ include file="common/studentInfoBar.jsp"%>

					<%try{ %>
					<div class="sz-content large-padding-top" style="margin-bottom: 100px;">
						<div class="clearfix mt-md-4 mt-lg-0 mt-sm-0" style="padding-bottom: 20px;"></div>
						<%@ include file="common/messageDemo.jsp"%>
						<%if("true".equals( (String)request.getSession().getAttribute("markedForUFM"))) { %>
							<div class="alert alert-danger alert-dismissible fade show mt-4" role="alert">
							<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
								Please click <a href = '/exam/student/ufmStatus'>here</a> to view the status of UFM notices from University.
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
											Your Demo Exam is pending. <a href="/exam/student/viewModelQuestionForm">Click here</a> to complete Your Demo Exam.</p>								        </div>
								      </div>
								    </div>
								  </div>
								</div>
							</c:if>
							
							<%try{ %>
							<%@ include file="studentHome/teeExamsDemo.jsp"%>
							<%}catch(Exception e){} %>

							<%try{ %>
							<%@ include file="studentHome/academicCalenderDemo.jsp"%>
							<%}catch(Exception e){} %>
							
							<div class="row mb-2">
								<% try{ %>
									<%@ include file="studentHome/homeSessionDemo.jsp"%>
								<%}catch(Exception e){} %>
							</div>
										
							<div class="row mb-2">
								<div class="col-lg-6 mb-2">
									<%@ include file="studentHome/courseDemo.jsp"%>
								</div>

								<div class="col-lg-6 mb-2 ">
									<%@ include file="studentHome/assignmentDemo.jsp"%>
									<!-- Internal Assessment Tests -->
									<%-- <%@ include file="studentHome/iATests.jsp" %> --%>
								</div>
							</div>
							
							<div class="row mb-2">
							
								<!-- <div class=" col-md-6 mb-2"> -->
											<%@ include file="studentHome/resultDemo.jsp"%>
<!-- 								</div> -->
								
 								<div class=" col-md-6 mb-2"> 
									<%@ include file="studentHome/homeRankDemo.jsp"%>
 								</div> 
								
<!-- 							</div> -->
														
							<% 
								String display="", width="";
								if(bcon.checkLead(request, response)){
									display="display: none;";
									width="width: 100%;";
								}
							%>
							
							<% if(!bcon.checkLead(request, response)){ %>
<!-- 							<div class="row mb-2"> -->
<!-- 								<div class="col-md-6 mb-2"> -->
									<%@ include file="studentHome/serviceRequestDemo.jsp"%>
<!-- 								</div> -->
<!-- 								<div class=" col-md-6 mb-2"> -->
									<%@ include file="studentHome/annoucementsDemo.jsp"%>
<!-- 								</div> -->
							
								<div class="clearfix"></div>
<!-- 							</div> -->
							<% } %>
						</div>
							<div class="row">								
							  <%@ include file="studentHome/badgesDemo.jsp"%>
							</div>
					 </div>
				 </div>				
			</div>
		</div>
	</div>
	 <jsp:include page="common/footerDemo.jsp" />
 
	<div id="examApp"></div>
	<div id="acadsApp"></div>
	<div id="csApp"></div>
	<div id="ltiApp"></div>
	<!-- Card 10453 disable Portal Experience for Leads and redirect directly to freeCourses page -->
<%-- 	<% if(bcon.checkLead(request, response)){ %> 
	<c:if test="${empty perspective}">
	<div class="modal-backdrop fade in"></div>
	 <div class="popup " > 
		<div class="popup-inner">
		 
		<span class="title">Choose A Perspective</span>
		<p class="para">Switch to a view to find how experienced and free course student can view.</p>
			<div class="row" > 
				 <a href="/studentportal/student/setPerspectiveForLeads?perspective=free"><div class="col-md-6"><div style="width:100%" class="btn btn-primary">Free-Course</div></div></a>
				 
				 <a href="/studentportal/student/setPerspectiveForLeads?perspective=experienced"><div class="col-md-6"><div style="width:100%"class="btn btn-primary">Experience Student Portal</div></div></a> 
			${perspective }
			</div>
		</div>	 
	</div>
	</c:if>
 	<%} %>   --%>

  
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
	<script type="text/javascript" src="${pageContext.request.contextPath }/assets/js/home.js"></script>

<!-- <script>
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
</script>  -->
</body>
<%}catch(Exception e){}%>
</html>	