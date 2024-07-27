
<%@page import="org.jsoup.Jsoup"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.nmims.beans.UserAuthorizationStudentPortalBean"%>
<%@page import="com.nmims.helpers.PersonStudentPortalBean"%>
<%@page import="com.nmims.beans.StudentStudentPortalBean"%>
<%@page import="com.nmims.beans.AnnouncementStudentPortalBean"%>
<%@page import="java.util.ArrayList"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%-- 		<nav class="navbar navbar-inverse navbar-fixed-top customNavbar" role="navigation" style="min-height:40px;">
        <div class="container-fluid">
        
            <!-- <div class="social-col">
                <ul class="headerSocialLinks">
                    <li><a href="https://www.facebook.com/NMIMSSCE" target="_blank" class="facebook"><i class="fa fa-facebook"></i></a></li>
                    <li><a href="https://twitter.com/NMIMS_SCE" target="_blank" class="twitter"><i class="fa fa-twitter"></i></a></li>
                    <li><a href="https://plus.google.com/u/0/116325782206816676798/posts" target="_blank" class="google-plus"><i class="fa fa-google-plus"></i></a></li>
                    <li><a href="#" target="_blank" class="youtube"><i class="fa fa-youtube"></i></a></li>
                </ul>
            </div> -->
                
            <div class="navbar-header">
                <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                </button>                
            </div>
            
            <div id="navbar" class="navbar-collapse collapse">
            	<div class="col-md-18 no-padding">
            	
            	
            	<%
		            String userId = (String)session.getAttribute("userId");
            		Person user = (Person)session.getAttribute("user_studentportal");
            		StudentBean studentBean = (StudentBean)session.getAttribute("student_studentportal");
            		String name = "";
            		String roles = "";
            		String program = "";
            		String studentPhotoUrl = "";
            		String userEmail = "";
            		String userMobile = "";
            		String pStructure = "";
            		
            		if(user != null){
            			roles = user.getRoles();
            			name = user.getFirstName() + " " + user.getLastName();
            			program = user.getProgram();
            			userEmail = user.getEmail();
            			userMobile = user.getContactNo();
            		}
            		if(studentBean != null && studentBean.getImageUrl() != null){
            			studentPhotoUrl = studentBean.getImageUrl().trim();
            			name = studentBean.getFirstName() + " " + studentBean.getLastName();
            			pStructure = studentBean.getPrgmStructApplicable();
            			userEmail = studentBean.getEmailId();
            			program = studentBean.getProgram();
            		}
            		
            		UserAuthorizationBean userAuthorization = (UserAuthorizationBean)session.getAttribute("userAuthorization_studentportal");
            		if(userAuthorization != null){
            			roles = (userAuthorization.getRoles() != null && !"".equals(userAuthorization.getRoles())) ? userAuthorization.getRoles() : roles;
            		}
            		
		            if(userId != null) { 
		            	if(userId.startsWith("77") || userId.startsWith("79")){
		            %>
		            		<ul class="headerLinks" >
	                    
	                    
	                    	<li><a href="/studentportal/home">Home</a></li>
							
							<li>
	                        	<a href="#">Exam</a>
	                            <ul class="subMenu">
	                            	<li><a href="/exam/student/viewNotice">Results</a></li>
	                            	<li><a href="/exam/student/getMostRecentAssignmentResults">Assignment Marks</a></li>
									<li><a href="/exam/getAStudentMarks">Marks History</a></li>
									<li><a href="/exam/studentSelfMarksheetForm">Marksheet</a></li>
									<li><a href="/exam/studentTimeTable">Exam Timetable</a></li>
									<!-- <li><a href="#" onclick="window.alert('Hall Ticket will be available for download after Exam Registration')">Hall Ticket</a></li>  -->
									<li><a href="/exam/downloadHallTicket" >Hall Ticket</a></li>
									<li><a href="/exam/viewAssignmentsForm">Assignments</a></li>
									<li><a href="/exam/verifyInformation">Exam Registration</a></li>
									<!-- <li><a href="#" onclick="window.alert('Exam Registration is NOT active.')">Exam Registration</a></li> -->
									<%if("Jul2014".equalsIgnoreCase(pStructure)){ %>
									<li><a href="/exam/selectResitSubjectsForm">Re-sit Exam Registration</a>
									<%} %>
									<li><a href="/exam/printBookingStatus">Exam Registration Receipt</a></li>
									<%if(program.startsWith("PG")){ %>
									<li><a href="/exam/viewProject?subject=Project">Project Submission</a>
									<%} %>
	                            </ul>
							</li>
							
							
							<li>
	                        	<a href="#">Academics</a>
	                            <ul class="subMenu">
	                            	<li><a href="/acads/viewTimeTable">Sessions Calendar</a></li>
									<li><a href="/acads/viewApplicableSubjectsForm">Learning Resources</a></li>
									<li><a href="/studentportal/gotoEZProxy" target="_blank">Digital Library</a></li>
									<li><a href="/acads/selectPCPSubjectsForm">PCP/VC Registration</a></li>
									<li><a href="/acads/downloadPCPRegistrationReceipt">PCP/VC Registration Receipt</a></li>
	                            </ul>
							</li>
							
							<li>
	                        	<a href="#">General</a>
	                            <ul class="subMenu">
	                            	<li><a href="/studentportal/getAllAnnouncementDetails">Announcements</a></li>
									<li><a href="http://ngasce.desk.com">Student Support System</a></li>
	                            </ul>
							</li>
							
			                <li><a href="/studentportal/changePassword">Change Password</a></li>
			                <li><a href="/studentportal/updateProfile">Update Profile</a></li>
			                <li><a href="/studentportal/logout">Logout</a></li>
			                
	                    </ul>
		            
		            <%}else{ %>
		            
		            	<ul class="headerLinks">
	                    
	                    
	                    	<li><a href="/studentportal/home" title="Home"><i class="fa fa-home fa-lg" ></i></a></li>

							<jsp:include page="/views/studentPortalHeader.jsp">
								<jsp:param value="<%=roles %>" name="roles" />
							</jsp:include>
							
							<jsp:include page="/views/examHeader.jsp">
								<jsp:param value="<%=roles %>" name="roles" />
							</jsp:include>
							
							<jsp:include page="/views/acadsHeader.jsp">
								<jsp:param value="<%=roles %>" name="roles" />
							</jsp:include>
			                <li><a href="/studentportal/changePassword"  title="Change Password"><i class="fa fa-key fa-lg" ></i></a></li>
			                <li><a href="/studentportal/updateProfile" title="Update Profile"><i class="fa fa-user fa-lg"></i></a></li>
			                <li><a href="/studentportal/logout" title="Logout"><i class="fa fa-power-off fa-lg"></i></a></li>  
			                
	                    </ul>
		            
		           	 <%} %>
		            
		            
	                    
	                 <%} %>
				</div>
                
                                
            </div>
        </div>
    </nav>
    
    <header class="customHeader">
    	<div class="logoWrapper">
        	<img src="resources_2015/images/logo.jpg" width="100%" alt="Logo"/>
   	    </div>
        
        <div class="rightHeadWrapper">
        	<h1>Welcome to NGASCE Student Zone</h1>
            
            <%if(userId != null) { %>
            <div class="userContainer">
                <div class="userImg">
                    <%if(!"".equals(studentPhotoUrl)) {%>
                    	<img src="<%=studentPhotoUrl%>" alt="Student Photo" class="img-responsive" style="height:100%;"/>
                    <%}else{ %>
                    	<img src="resources_2015/images/userImg.jpg" alt="Student Photo"  class="img-responsive"/>
                    <%} %>
                </div>
                <div class="detailWrapper">
                    <h2><%=name.toUpperCase() %></h2>
                    <p>User ID: <%=userId %>, Program: <%=program %></p>
                    <p><a href="/studentportal/updateProfile" style="color: white"><%=userEmail%>/<%=userMobile %></a></p>
                </div>        
            </div>
            
            <%} %>
        </div>
        
    </header> 
 
 	<div class="clearfix">
 	 <c:import url="https://studentzone-ngasce.nmims.edu/maintenance.html" /> 
 	</div>
--%>

<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')"
	var="server_path" />

<%


ArrayList<AnnouncementStudentPortalBean> announcementsInHeader = (ArrayList<AnnouncementStudentPortalBean>)session.getAttribute("announcements");
int noOfAnnouncemntsInHeader = announcementsInHeader != null ? announcementsInHeader.size() : 0;
System.out.println("noOfAnnouncemntsInHeader = "+noOfAnnouncemntsInHeader);
SimpleDateFormat formatterHeader = new SimpleDateFormat("yyyy-MM-dd");
SimpleDateFormat dateFormatterHeader = new SimpleDateFormat("dd-MMM-yyyy");
String hideProfileLink = (String)request.getAttribute("hideProfileLink");
String mobileVisibleHeaderClass = "";//Use this class to show header on login page only

if(session.getAttribute("userId") == null){
	//Login page
	mobileVisibleHeaderClass = "visibleHeader";
}

%>

<div class="sz-header <%=mobileVisibleHeaderClass%>">
	<div class="container-fluid">
		<div class="sz-logo">
			<img src="assets/images/logo.png" class="img-responsive" alt="" />
		</div>


		<%if(session.getAttribute("userId") != null){ %>
		<ul class="sz-header-menu">
			<%if(!"true".equals(hideProfileLink)){ %>
			<!--  Below link needs to be replaced by a separate page for admin to update profile: Pending work -->
			<!-- <li><a href="/studentportal/updateProfile"><span class="icon-my-profile"></span>My Profile</a></li> -->
			<%} %>
			<li><a href="/studentportal/logout"><span
					class="icon-logout"></span>Logout</a></li>
			<li class="notification-link"><a href="" data-toggle="modal"
				data-keyboard="true" data-target="#new-announcements"> <span
					class="icon-notification"><div class="notification-count"><%=noOfAnnouncemntsInHeader %></div></span></a>
			</li>
		</ul>


		<%if(noOfAnnouncemntsInHeader > 0){ %>
		<!-- Announcements Modal -->
		<div id="new-announcements" class="modal fade" role="dialog"
			tabindex="-1">
			<div class="modal-dialog modal-md">
				<!-- Modal content-->
				<div class="modal-content">
					<div class="modal-header">
						<h4 class="modal-title">Announcements</h4>
					</div>
					<div class="modal-body">
						<ul class="announcement-list">

							<%
					                		  int count = 0;
									          for(AnnouncementStudentPortalBean announcement : announcementsInHeader){
									        	  count++;
									        	  String announcementWithoutHtmlCharacgters = Jsoup.parse(announcement.getDescription()).text();
									        	  String announcemntBrief = announcementWithoutHtmlCharacgters.length() > 150 ? announcementWithoutHtmlCharacgters.substring(0, 149)+"..."  : announcementWithoutHtmlCharacgters;
									        	  Date formattedDate = formatterHeader.parse(announcement.getStartDate());
									  			  String formattedDateString = dateFormatterHeader.format(formattedDate);
									          %>

							<li><a href="#" data-toggle="modal" data-dismiss="modal"
								data-target="#announcementModal<%=count%>"> <!-- <li> <a href="#" onclick="return false;" style="cursor:default;"> -->

									<h3><%=announcement.getSubject() %></h3>
									<p><%=announcemntBrief %></p>
									<h4><%=formattedDateString %>
										by <span><%=announcement.getCategory() %></span>
									</h4>
							</a></li>
							<%
										          if(count == 4){
										        	  break;
										          }
									          } %>
						</ul>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal"
							data-toggle="modal" data-target="#announcementModalAll">See
							All</button>

						<!-- <button type="button" class="btn btn-default" onclick="window.location.href='/studentportal/getAllAnnouncementDetails'">See All</button> -->
					</div>
				</div>
			</div>
		</div>

		<%} %>
		<%}else{ %>
		<div class="pull-right hidden-xs">
			<h2>Welcome to NGASCE Student Zone</h2>
		</div>
		<%} %>

		<div class="clearfix"></div>
	</div>
</div>
<!--header ends-->


<%if(noOfAnnouncemntsInHeader > 0){ %>
<!--MODAL FOR ALL ANNOUNCEMENT-->

<div class="modal fade announcementModalTop" id="announcementModalAll"
	tabindex="-1" role="dialog">
	<div class="modal-dialog modal-md" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title">ALL ANNOUNCEMENTS</h4>
			</div>
			<div class="modal-body">
				<ul>

					<%
        	 int count = 0;
	          for(AnnouncementStudentPortalBean announcement : announcementsInHeader){
	        	  count++;
	        	  String announcementWithoutHtmlCharacgters = Jsoup.parse(announcement.getDescription()).text();
	        	  String announcemntBrief = announcementWithoutHtmlCharacgters.length() > 150 ? announcementWithoutHtmlCharacgters.substring(0, 149)+"..."  : announcementWithoutHtmlCharacgters;
	        	  Date formattedDate = formatterHeader.parse(announcement.getStartDate());
  				  String formattedDateString = dateFormatterHeader.format(formattedDate);
	          %>

					<li><a href="#" data-toggle="modal" data-dismiss="modal"
						data-target="#announcementModal<%=count%>"> <!-- <li> <a href="#" onclick="return false;" style="cursor:default;"> -->
							<h3><%=announcement.getSubject() %></h3>
							<p><%=announcemntBrief %></p>

							<h4><%=formattedDateString%>
								by <span><%=announcement.getCategory()%></span>
							</h4>
					</a></li>

					<%
	          
	          } %>


				</ul>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">DONE</button>
			</div>
		</div>
	</div>
</div>

<%}%>


<%if(noOfAnnouncemntsInHeader > 0){ %>
<!-- MODAL FOR INDIVIDUAL ANNOUNCEMENTS-->
<%
        	 int count = 0;
	          for(AnnouncementStudentPortalBean announcement : announcementsInHeader){
	        	  count++;
	        	  Date formattedDate = formatterHeader.parse(announcement.getStartDate());
	  			  String formattedDateString = dateFormatterHeader.format(formattedDate);
	          %>

<div class="modal fade announcement" id="announcementModal<%=count %>"
	tabindex="-1" role="dialog">
	<div class="modal-dialog" role="document">
		<div class="modal-content modal-md">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title">ANNOUCEMENTS</h4>
			</div>
			<div class="modal-body">
				<h6><%=announcement.getSubject() %></h6>
				<p><%=announcement.getDescription() %></p>

				<%if(announcement.getAttachment1() != null){ %>
				<a target="_blank"
					href="<spring:eval expression="@propertyConfigurer.getProperty('CONTENT_PREVIEW_PATH')" /><%=announcement.getAttachment1()%>"><%=announcement.getAttachmentFile1Name() %></a><br />
				<%} %>
				<%if(announcement.getAttachment2() != null){ %>
				<a target="_blank"
					href="<spring:eval expression="@propertyConfigurer.getProperty('CONTENT_PREVIEW_PATH')" /><%=announcement.getAttachment2()%>"><%=announcement.getAttachmentFile2Name() %></a><br />
				<%} %>
				<%if(announcement.getAttachment3() != null){ %>
				<a target="_blank"
					href="<spring:eval expression="@propertyConfigurer.getProperty('CONTENT_PREVIEW_PATH')" /><%=announcement.getAttachment3()%>"><%=announcement.getAttachmentFile3Name() %></a><br />
				<%} %>


				<h4 class="small"><%=formattedDateString%>
					<span>by</span><a href="#"> <%=announcement.getCategory() %></a>
				</h4>

			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">DONE</button>
			</div>
		</div>
	</div>
</div>
<% }//End of For loop %>

<%}%>