<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.helpers.*"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.nmims.beans.AssignmentStudentPortalFileBean"%>
<%@page import="java.util.Calendar"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Welcome to Student Zone" name="title" />
</jsp:include>

<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')"
	var="server_path" />

<body class="inside">

	<%@ include file="header.jsp"%>
	<%
	PersonStudentPortalBean p = (PersonStudentPortalBean)session.getAttribute("user_studentportal");
    
    String firstName = "";
	String lastName = "";
	String displayName = "";
//	String program = "";
	String email = "";
	String lastLogon = "";
	 
    
    if(p != null){
    	displayName = p.getDisplayName();
    	program = p.getProgram();
    	lastLogon = p.getLastLogon();
    }
    
    /* userId = "77114000512";
    session.setAttribute("userId", userId); */
    
    String encryptedSapId = URLEncoder.encode(AESencrp.encrypt(userId)); 
    //String encryptedSapId = "";
    
   // String SERVER_PATH = (String)request.getAttribute("SERVER_PATH");
    
    String examAppSSOUrl = (String)pageContext.getAttribute("server_path") + "exam/loginforSSO?uid="+encryptedSapId;
    String acadsAppSSOUrl = (String)pageContext.getAttribute("server_path") + "acads/loginforSSO?uid="+encryptedSapId;
    
    
    Calendar now = Calendar.getInstance();
    int currentExamYear = now.get(Calendar.YEAR);
    int month = (now.get(Calendar.MONTH) + 1);
    String currentExamMonth = "";
    
    if(month >= 1 && month <= 6){
    	currentExamMonth = "Jun";
    }else if(month >= 6 && month <= 12){
    	currentExamMonth = "Dec";
    }
    
    String programStructure = "";
    if(studentBean != null){
    	programStructure = studentBean.getPrgmStructApplicable();
    }
    
    
    %>

	<%
    
    ArrayList<AssignmentStudentPortalFileBean> assignments = (ArrayList<AssignmentStudentPortalFileBean>)session.getAttribute("studentAssignments");
    
    if(assignments == null){
    	//Bring assignments from Exam App, only if it is not already fetched. Otherwises take from session
	    ExamAppHelper examAppHelper = new ExamAppHelper();
	    assignments = examAppHelper.getStudentAssignments((String)pageContext.getAttribute("server_path")	, encryptedSapId);
	    session.setAttribute("studentAssignments", assignments);
    }
    
    %>


	<div class="container-fluid customMainWrapper">

		<%@ include file="messages.jsp"%>
		<div class="row">
			<div class="demo-wrapper">

				<div class="dashboard clearfix">
					<h2 class="boldTitle">Academics</h2>
					<ul class="tiles">

						<div class="clearfix col-md-6 col-sm-9 padding-small">
							<!--col3-->
							<a href="/acads/admin/viewTimeTable">
								<li class="tile doubleHeight fig-tile"
								style="background-image: url(resources_2015/images/sessionImg.jpg);">
									<figure>
										<figcaption class="tile-caption caption-bottom">SESSIONS
											CALENDAR</figcaption>
									</figure>
							</li>
							</a>
						</div>

						<div class="clearfix col-md-6 col-sm-9 padding-small">
							<!--col1-->
							<a href="/acads/student/viewApplicableSubjectsForm">
								<li class="tile theme-9 slideTextUp doubleHeight">
									<div>
										<p>
											<i class="fa fa-book"></i>LEARNING RESOURCES
										</p>
									</div>
									<div>
										<p>Go to Learning Resources</p>
									</div>
							</li>
							</a>

						</div>

						<div class="clearfix col-md-6 col-sm-18 padding-small">
							<!--col2-->


							<!--Tiles with a 3D effect should have the following structure:
                            1) a container inside the tile with class of .faces
                            2) 2 figure elements, one with class .front and the other with class .back-->
							<a href="gotoEZProxy" class="tile-small col-sm-18  col-xs-18"
								target="_blank">
								<li class="tile theme-11 rotate3d rotate3dX doubleHeight">
									<div class="faces">
										<div class="back">
											<span><i class="fa-custom fa-university"></i></span>
										</div>
										<div class="front">
											<p>DIGITAL LIBRARY</p>
										</div>
									</div>
							</li>
							</a>


						</div>

					</ul>
				</div>
				<!--end Academic-->


				<br>

				<div class="dashboard clearfix">
					<!-- <div class="col-md-18 col-centered"> -->
					<h2 class="boldTitle">General</h2>
					<ul class="tiles">
						<div class="clearfix col-md-6 col-sm-9 padding-small ">
							<!--col2-->
							<a href="getAllAnnouncementDetails">
								<li class="tile tile-1 tile-1-slider">
									<h2 style="color: #fff; padding-top: 10px;">ANNOUNCEMENTS</h2>
									<div id="carousel-example-generic" class="carousel slide"
										data-ride="carousel">
										<!-- Wrapper for slides -->
										<div class="carousel-inner" role="listbox">

											<c:forEach var="announcement" items="${announcements}"
												varStatus="status">

												<div
													class="item <c:if test="${status.count == 1 }">active</c:if>">
													<div class="carousel-caption">
														<p>${announcement.category}</p>
														<h3>${announcement.subject}</h3>
													</div>
												</div>

											</c:forEach>

										</div>

										<!-- Controls -->
										<a class="left carousel-control"
											href="#carousel-example-generic" role="button"
											data-slide="prev"> <span class="fa fa-angle-left"
											aria-hidden="true"></span> <span class="sr-only">Previous</span>
										</a> <a class="right carousel-control"
											href="#carousel-example-generic" role="button"
											data-slide="next"> <span class="fa fa-angle-right"
											aria-hidden="true"></span> <span class="sr-only">Next</span>
										</a>
									</div>
							</li>
							</a>
						</div>


						<div class="clearfix col-md-6 col-sm-9 padding-small">
							<!--col2-->
							<a href="selectSRForm">
								<li class="tile theme-1 rotate3d rotate3dX">
									<div class="faces">
										<div class="front">
											<p>Service Request</p>
										</div>
										<div class="back">
											<span><i class="fa-custom fa-info-circle"></i></span>
										</div>
									</div>
							</li>
							</a>
						</div>


						<div class="clearfix col-md-6 col-sm-9 padding-small ">
							<!--col2-->
							<a href="http://ngasce.desk.com" target="_blank">
								<li class="tile theme-8 rotate3d rotate3dX">
									<div class="faces">
										<div class="back">
											<span><i class="fa-custom fa-life-ring"></i></span>
										</div>
										<div class="front">
											<p>STUDENT SUPPORT</p>
										</div>
									</div>
							</li>
							</a>
						</div>

					</ul>
					<!-- </div> -->
				</div>
				<!--end General-->
				<br>
				<div class="dashboard clearfix">

					<h2 class="boldTitle">Examination</h2>
					<ul class="tiles">
						<div class="clearfix col-md-6 col-sm-9 padding-small">
							<!--col3-->

							<a href="#">
								<li class="tile theme-1 doubleHeight slideTextUp">
									<div>
										<p style="color: #fff;">
											<i class="fa fa-file-text-o"></i> ASSIGNMENTS
										</p>
									</div>
									<div class="assignmentsList">
										<table class="table">

											<%
                                    int counter = 1;
                                    for(AssignmentStudentPortalFileBean assignment : assignments){ 
                                    	String subject = assignment.getSubject();
                                    	if("Project".equals(subject) || "Module 4 - Project".equals(subject)){
                                    		//No Assignments for Project
                                    		continue;
                                    	}
                                    %>
											<tr>
												<td><p style="line-height: 20px;"><%=counter++ %></p></td>
												<td><p style="line-height: 20px;"><%=assignment.getSubject() %></p></td>
												<td><p style="line-height: 20px;">
														<a
															href="/exam/student/viewSingleAssignment?year=<%=currentExamYear%>&month=<%=currentExamMonth%>&subject=<%=URLEncoder.encode(assignment.getSubject(), "UTF-8")%>">Submit</a>
													</p></td>
											</tr>
											<%} %>


										</table>
										<p class="viewAllBtn">
											<a href="/exam/student/viewAssignmentsForm">VIEW ALL</a>
										</p>
									</div>
							</li>
							</a>
						</div>


						<div class="clearfix col-md-6 col-sm-9 padding-small">
							<!--col1-->

							<a href="/exam/student/viewNotice">
								<li class="tile theme-2 slideTextRight">
									<!--tile-small-->
									<div>
										<p>
											<i class="fa fa-arrow-right"></i>
										</p>
									</div>
									<div>
										<p>RESULTS</p>
									</div>
							</li>
							</a> <a href="/exam/getAStudentMarks">
								<li class="tile theme-4 slideTextUp">
									<!--tile-small-->
									<div>
										<p>MARKS HISTORY</p>
									</div>
									<div>
										<p>
											<i class="fa fa-arrow-right"></i>
										</p>
									</div>
							</li>
							</a>


						</div>



						<div class="clearfix col-md-6 col-sm-9 padding-small">

							<a class="tile-small col-sm-9 col-xs-18"
								href="/exam/studentTimeTable">
								<li class="tile theme-5 rotate3d rotate3dX">
									<div class="faces">
										<div class="back">
											<span><i class="fa-custom fa-calendar"></i></span>
										</div>
										<div class="front">
											<p>TIMETABLE</p>
										</div>
									</div>
							</li>
							</a>

							<!-- <a href="/exam/verifyInformation" class="tile-small col-sm-9 col-xs-18" >
                                <li class="tile theme-6 rotate3d rotate3dY">
                                  <div class="faces">
                                    <div class="back"><span class="icon-instagram"><i class="fa-custom fa-pencil-square-o"></i></span></div>
                                    <div class="front"><p>Exam Registration</p></div>
                                  </div>
                                </li>
                            </a> -->

							<%if("Jul2014".equalsIgnoreCase(programStructure)){ %>
							<!-- 
                            Uncomment this during Re-sit and comment one below it
                            <a href="/exam/selectResitSubjectsForm"
                            	class="tile-small col-sm-9 col-xs-18" >
                                <li class="tile theme-6 rotate3d rotate3dY">
                                  <div class="faces">
                                    <div class="back"><span class="icon-instagram"><i class="fa-custom fa-pencil-square-o"></i></span></div>
                                    <div class="front"><p>Re-Sit Exam Registration</p></div>
                                  </div>
                                </li>
                            </a> -->

							<a href="/exam/verifyInformation"
								class="tile-small col-sm-9 col-xs-18">
								<li class="tile theme-6 rotate3d rotate3dY">
									<div class="faces">
										<div class="back">
											<span class="icon-instagram"><i
												class="fa-custom fa-pencil-square-o"></i></span>
										</div>
										<div class="front">
											<p>Exam Registration</p>
										</div>
									</div>
							</li>
							</a>

							<%}else{ %>
							<a href="/exam/verifyInformation"
								class="tile-small col-sm-9 col-xs-18">
								<li class="tile theme-6 rotate3d rotate3dY">
									<div class="faces">
										<div class="back">
											<span class="icon-instagram"><i
												class="fa-custom fa-pencil-square-o"></i></span>
										</div>
										<div class="front">
											<p>Exam Registration</p>
										</div>
									</div>
							</li>
							</a>
							<%} %>

						</div>

						<div class="clearfix col-md-6 col-sm-9 padding-small">

							<%if("Jul2014".equalsIgnoreCase(programStructure)){ %>

							<a class="tile-small col-sm-9 col-xs-18"
								href="/exam/student/downloadHallTicket">
								<li class="tile theme-1 rotate3d rotate3dY">
									<div class="faces">
										<div class="front">
											<p>HALL TICKET</p>
										</div>
										<div class="back">
											<span><i class="fa-custom fa-ticket"></i></span>
										</div>

									</div>
							</li>
							</a> <a class="tile-small col-sm-9 col-xs-18"
								href="http://www.thepracticetest.in/NMIMS/" target="_blank">
								<li class="tile theme-11 rotate3d rotate3dX">
									<div class="faces">
										<div class="front">
											<p>Demo Exam</p>
										</div>
										<div class="back">
											<span><i class="fa-custom fa-external-link-square"></i></span>
										</div>
									</div>
							</li>
							</a>

							<%}else{ %>

							<a href="/exam/student/downloadHallTicket">
								<li class="tile theme-1 rotate3d rotate3dY">
									<div class="faces">
										<div class="front">
											<p>HALL TICKET</p>
										</div>
										<div class="back">
											<span><i class="fa-custom fa-ticket"></i></span>
										</div>

									</div>
							</li>
							</a>

							<%} %>
						</div>

					</ul>
				</div>
				<!--end Exam-->


			</div>
		</div>


	</div>

	<div id="examApp"></div>
	<div id="acadsApp"></div>


	<jsp:include page="footer.jsp" />

	<script>
	$( "#examApp" ).load( "<%=examAppSSOUrl%>" );
	$( "#acadsApp" ).load( "<%=acadsAppSSOUrl%>" );
	</script>


</body>
</html>
