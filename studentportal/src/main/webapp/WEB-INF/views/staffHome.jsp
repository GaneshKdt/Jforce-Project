
<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.helpers.Person"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="com.nmims.helpers.AESencrp"%>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html class="no-js"> <!--<![endif]-->
   
    <jsp:include page="jscss.jsp">
	<jsp:param value="Welcome to Student Zone" name="title" />
	</jsp:include>
	
	<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')" var="server_path" />
	
    <body class="inside">
	
    <%@ include file="header.jsp"%>
    <%
    
    Person p = (Person)session.getAttribute("user_studentportal");
    
    String firstName = "";
	String lastName = "";
	String displayName = "";
	String email = "";
	String lastLogon = "";
    
    if(p != null){
    	displayName = p.getDisplayName();
    	lastLogon = p.getLastLogon();
    }
    
    String encryptedSapId = URLEncoder.encode(AESencrp.encrypt(userId)); 
    
    String examAppSSOUrl = (String)pageContext.getAttribute("server_path") + "exam/loginforSSO?uid="+encryptedSapId;
    String acadsAppSSOUrl = (String)pageContext.getAttribute("server_path") + "acads/loginforSSO?uid="+encryptedSapId;
    
    %>
    <section class="content-container login">
        <div class="container-fluid customTheme">
          <div class="row">
          <legend>Welcome <%=displayName %></legend>
            <div class="col-xs-18">
             
           
             <%@ include file="messages.jsp" %>
			
             <div class="student-details">
               <div class="row">          
                 <div class="col-xs-9 col-sm-6 col-md-6"><span>User ID:</span> <%=userId %></div>
                 <div class="col-xs-9 col-sm-6 col-md-6"><span>Last Login:</span> <%=lastLogon %></div>
               </div>  
             </div>
            </div> <!-- /col-xs-18 -->
          </div> <!-- /row -->
           
		 <div class="jumbotron">
		  <h1  style="text-align:left">Hello, <%=displayName %></h1>
		  <p> Welcome to NGASCE Portal</p>
		 </div>
        </div> <!-- /container -->
        
        <div id="examApp"></div>
  		<div id="acadsApp"></div>
    </section>
    
    
    <jsp:include page="footer.jsp" />
    
    <script>
		$( "#examApp" ).load( "<%=examAppSSOUrl%>" );
		$( "#acadsApp" ).load( "<%=acadsAppSSOUrl%>" );
	</script>
	
  </body>
</html> --%>

<!DOCTYPE html>
<html lang="en">
<%@page import="com.nmims.helpers.PersonStudentPortalBean"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="com.nmims.helpers.AESencrp"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.HashMap"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Welcome to Student Zone" name="title" />
</jsp:include>
<style>
.jumbotron {
	background-color: #fff;
	padding: 0.5px;
}
</style>
<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')"
	var="server_path" />

<body>

	<%@ include file="adminCommon/header.jsp"%>


	<%
    
	PersonStudentPortalBean p = (PersonStudentPortalBean)session.getAttribute("user_studentportal");
    
    String firstName = "";
	String lastName = "";
	String displayName = "";
	String email = "";
	String lastLogon = "";
    
    if(p != null){
    	displayName = p.getFirstName()+" "+p.getLastName();
    	lastLogon = p.getLastLogon();
    }
    
    String encryptedSapId = URLEncoder.encode(AESencrp.encrypt((String)session.getAttribute("userId"))); 
    
    String examAppSSOUrl = (String)pageContext.getAttribute("server_path") + "exam/loginforSSO?uid="+encryptedSapId;
    String acadsAppSSOUrl = (String)pageContext.getAttribute("server_path") + "acads/loginforSSO?uid="+encryptedSapId;
    String ltiAppSSOUrl = (String)pageContext.getAttribute("server_path") + "ltidemo/loginforSSO?uid="+encryptedSapId;
    String csAppSSOUrl = (String)pageContext.getAttribute("server_path") + "careerservices/loginforSSO?uid="+encryptedSapId;
    
    
    %>



	<div class="sz-main-content-wrapper">

		<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Student Zone;Home" name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>


				<div class="sz-content-wrapper examsPage">
					<%@ include file="adminCommon/adminInfoBar.jsp"%>


					<div class="sz-content">

						<h2 class="red text-capitalize">
							Welcome
							<%=displayName %></h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%@ include file="adminCommon/messages.jsp"%>



							<div class="jumbotron">

								<c:if test="${isfacultyForCS == true}">
									<div class="row">
										<a href="/careerservices/assignedCourseQueries"
											class="col-sm-3 WidgeBlock B-orange z-depth-3">
											<center>
												<h4 style="font-size: 18px !important;">Count of not
													answered query for CS</h4>
												<hr />
												<div style="font-size: 35px; font-weight: bold">${ countOfNotAnsCS }</div>
											</center>
										</a>
									</div>
								</c:if>
								<c:if test="${isfacultyRole == true && sessions.size() > 0 }">
									<div style="color: gray; font-size: 28px;">upcoming
										sessions</div>
									<div class="row">
										<c:forEach items="${sessions}" var="session">
											<a href="/acads/viewFacultyTimeTable?id=${ session.id }">
												<div class="col-sm-3 dashboard-border-left">
													<h4>${ session.subject }</h4>
													<div class="gray_border"></div>
													<div>Date : ${ session.date }</div>
													<div>StartTime : ${ session.startTime }</div>
													<div>EndTime : ${ session.endTime }</div>
													<div>Day : ${ session.day }</div>
												</div>
											</a>
										</c:forEach>
									</div>
									<br />
								</c:if>

								<c:if test="${UsersRole != null}">

									<div class="row">
										<a href="DashBoardWidge"
											class="col-sm-3 WidgeBlock B-orange z-depth-3">
											<center>
												<h4 style="font-size: 18px !important;">List of Student
													with missing data</h4>
												<hr />
												<div style="font-size: 35px; font-weight: bold">${ StudentDataMissingCount }</div>
											</center>
										</a> <a href="MissingSubjectMap"
											class="col-sm-3 WidgeBlock B-green">
											<center>
												<h4 style="font-size: 18px !important;">Missing Subject
													Mapping Count</h4>
												<hr />
												<div style="font-size: 35px; font-weight: bold">${ MissingSubjectMapping }</div>
											</center>
										</a>

									</div>

									<br />
									<hr />

								</c:if>


								<c:if test="${isfacultyRole == true}">

									<div class="row">
										<a href="/acads/assignedCourseQueries"
											class="col-sm-3 WidgeBlock B-orange z-depth-3">
											<center>
												<h4 style="font-size: 18px !important;">Count of not
													answered query</h4>
												<hr />
												<div style="font-size: 35px; font-weight: bold">${ countOfNotAns }</div>
											</center>
										</a> <a href="/exam/searchAssignmentToEvaluateForm"
											class="col-sm-3 WidgeBlock B-green z-depth-3">
											<center>
												<h4 style="font-size: 18px !important;">Count of
													assignment not revaluated</h4>
												<hr />
												<div style="font-size: 35px; font-weight: bold">${ countOfAssignmentNotRevalued }</div>
											</center>
										</a> <a href="/exam/searchAssignmentToEvaluateForm"
											class="col-sm-3 WidgeBlock B-blue z-depth-3">
											<center>
												<h4 style="font-size: 18px !important;">Count of
													assignment not evaluated</h4>
												<hr />
												<div style="font-size: 35px; font-weight: bold">${ countOfAssignmentNotEvaluated }</div>
											</center>
										</a> <a href="/exam/searchProjectToEvaluateForm"
											class="col-sm-3 WidgeBlock B-yellow z-depth-3">
											<center>
												<h4 style="font-size: 18px !important;">Count of
													project not revaluated</h4>
												<hr />
												<div style="font-size: 35px; font-weight: bold">${ ProjectNotRevalutedCount }</div>
											</center>
										</a> <a href="/exam/searchProjectToEvaluateForm"
											class="col-sm-3 WidgeBlock B-red z-depth-3">
											<center>
												<h4 style="font-size: 18px !important;">Count of
													project not evaluated</h4>
												<hr />
												<div style="font-size: 35px; font-weight: bold">${ ProjectNotEvaluatedCount }</div>
											</center>
										</a> <a href="/exam/searchAssignedCaseStudyFilesForm"
											class="col-sm-3 WidgeBlock B-orange z-depth-3">
											<center>
												<h4 style="font-size: 18px !important;">Count of case
													study not evaluated</h4>
												<hr />
												<div style="font-size: 35px; font-weight: bold">${ CaseStudyNotEvaluatedCount }</div>
											</center>
										</a> <a href="/acads/gotoFacultySessionList"
											class="col-sm-3 WidgeBlock B-orange z-depth-3">
											<center>
												<h4 style="font-size: 18px !important;">Count of Live
													Session Queries not answered</h4>
												<hr />
												<div style="font-size: 35px; font-weight: bold">${ SessionQueriesNotAnsweredCount }</div>
											</center>
										</a>
									</div>

									<br />
									<hr />

								</c:if>

							</div>
						</div>


					</div>

				</div>
			</div>


		</div>
	</div>

	<jsp:include page="adminCommon/footer.jsp" />
	
	<jsp:include page="common/SSOLoader.jsp" />
    <script>
		$(document).ready(function(){
			$('#btn_refresh_widges').click(function(){
					$('.WidgeImageBlock').show();
					$('#synDataAlert').hide();
					$.ajax({
						type:'GET',
						url:'getCurrentWidgeData',
						dataType:'json',
						success:function(response){
							console.log(response);
							$('#ExamOfflineBookedCount').html(response[0].count);
							$('#ExamOnlineBookedCount').html(response[1].count);
							$('#ExamOnlineReleaseSeatCount').html(response[2].count);
							$('#ExamOfflineReleaseSeatCount').html(response[3].count);
							$('#TwiceExamBooking').html(response[4].count);
							$('.WidgeImageBlock').hide();
							$('#synDataAlert').show().removeClass("alert-danger").addClass("alert-info").html("Dashboard Data Sync successfully.");
						},
						error(){
							$('.WidgeImageBlock').hide();
							$('#synDataAlert').show().removeClass("alert-info").addClass("alert-danger").html("Dashboard Data failed to Sync,refresh these page");
						}
					});
			});	
			
		});
	</script>
</body>
</html>