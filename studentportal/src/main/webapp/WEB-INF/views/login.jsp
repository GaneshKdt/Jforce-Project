<%-- 
<!DOCTYPE html>
<html lang="en">
    
    <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
	<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')" var="server_path" />
	<%
		
		//String SERVER_PATH = (String)request.getAttribute("SERVER_PATH");
		String examAppLogoutUrl = (String)pageContext.getAttribute("server_path") + "exam/logoutforSSO";
		String acadsAppLogoutUrl = (String)pageContext.getAttribute("server_path") + "acads/logoutforSSO";
	
	%>
    
    <jsp:include page="common/jscss.jsp">
	<jsp:param value="NMIMS Global Access School for Continuing Education | Sign in" name="title"/>
	</jsp:include>
    
    
    
    
    <body>
    
    	<%@ include file="common/header.jsp" %>
    	
        
        
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="common/breadcrum.jsp">
			<jsp:param value="Student Zone;Login" name="breadcrumItems"/>
			</jsp:include>
        	
        	
            
            <div class="sz-main-content menu-closed">
                <div class="container-fluid">
                    <div class="sz-main-content-inner">
              
                            <div class="editContent sz-login">
                            	<div class="loginTitle">
                                    <img src="assets/images/hat.png"  alt=""/>
                                    <h3>LOGIN</h3>
                                </div>
                                <%@ include file="common/messages.jsp" %>
                                
                                <form class="profileForm" action="authenticate" method="post">
                                    <div class="row">
                                        <div class="form-group">
                                                    <label for="name">USER NAME<span class="star">*</span></label>
                                                    <input type="text" id="userId" name="userId" class="form-control" placeholder="Enter Student Number" required="required">
                                          </div>
                                          <div class="clearfix"></div>
                                          
                                           <div class="form-group">
                                                    <label for="password">PASSWORD<span class="star">*</span></label>
                                                    <input type="password" id="password" name="password" class="form-control" required="required">
                                                    <a href="resetPasswordForm">Forgot Password?</a>    
                                          </div>
                                          
                                          <div class="clearfix"></div>
                                          
                                                <div class="formBtn rightBtn">
                                                        <button type="submit" class="btn  btn-default" id="loginBtn">Login</button>
                                                        <div class="clearfix"></div>
                                                </div>
                                              </div>  
                                  </form>
								<div class="clearfix"></div>
                                 </div>
 						</div>
                    </div>
                </div>
            </div>
            
            <div id="examApp"></div>
  			<div id="acadsApp"></div>
  	
            <jsp:include page="common/footer.jsp"/>
            
            <script>
		    $(document).ready(function(e) {
		        //////Enter Method for submitting form//////
		        $(document).keypress(function (e) {
		        	
		            var key = e.which;
		            if(key == 13){
		                $('#loginBtn').click();
		                return false;
		            }
		        });
		    });
		    </script>
		    
		    
		    <script>
		    	//Logout users from all apps when they visit login page
				$( "#examApp" ).load( "<%=examAppLogoutUrl%>" );
				$( "#acadsApp" ).load( "<%=acadsAppLogoutUrl%>" );
			</script>
		
    </body>
</html> --%>


<!DOCTYPE html>


<%@page import="org.jsoup.Jsoup"%>
<%@page
	import="org.springframework.web.servlet.support.RequestContextUtils"%>
<%@page import="org.springframework.context.ApplicationContext"%>
<%@page import="java.util.List"%>
<%@page import="com.nmims.daos.PortalDao"%>
<%@page import="java.util.ArrayList"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')"
	var="server_path" />
<%
		
		//String SERVER_PATH = (String)request.getAttribute("SERVER_PATH");
		String examAppLogoutUrl = (String)pageContext.getAttribute("server_path") + "exam/logoutforSSO";
		String acadsAppLogoutUrl = (String)pageContext.getAttribute("server_path") + "acads/logoutforSSO";
		String ltiAppLogoutUrl = (String)pageContext.getAttribute("server_path") + "ltidemo/logoutforSSO";
		String csAppLogoutUrl = (String)pageContext.getAttribute("server_path") + "careerservices/logoutforSSO";
		
	%>



<html lang="en">

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')" var="server_path" />


<jsp:include page="common/jscss.jsp">
	<jsp:param value="Welcome to Student Zone" name="title" />
</jsp:include>

<%
/* ApplicationContext context = RequestContextUtils.getWebApplicationContext(request);
PortalDao pDao = (PortalDao)context.getBean("portalDAO");
System.out.println("PortalDao = "+pDao);
List<AnnouncementBean> announcements =  pDao.getAllActiveAnnouncements();
int noOfAnnouncemntsInLogin = announcements != null ? announcements.size() : 0;
 */
%>

<body>

	<%@ include file="common/header.jsp"%>



	<div class="sz-main-content-wrapper complete-profile-warpper">

		<jsp:include page="common/breadcrum.jsp">
			<jsp:param value="Student Zone;Login" name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">


			<div class="sz-content-wrapper dashBoard withBgImage loginPage">

				<div class="container">
					<div class="contentMainWraper">
						<%@ include file="common/messages.jsp"%>
						<div class="login-cont">
							<h4>LOGIN</h4>
							<form class="profileForm" action="authenticate" method="post">
								<div class="form-group">
									<label for="name">USER NAME</label> <input type="text"
										class="form-control" placeholder="Student Number" id="userId"
										name="userId">
								</div>
								<div class="form-group">
									<label for="password">PASSWORD</label> <input type="password"
										id="password" name="password" class="form-control"> 
									<a href="resetPasswordForm">Forgot Password?</a>
									<a href="loginForLeadForm">Not a student? Get started here.</a>
								</div>
								
								<button type="submit" id="loginBtn" class="btn  btn-default">Login</button>
							</form>
						</div>

						<div class="row">
							<div class="col-md-12">
								<div class="panel-content-wrapper latestNews">
									<!-- <h2 class="text-capitalize">Welcome to NGASCE Student Zone</h2> -->
									<div class="clearfix"></div>
									<img src="assets/images/overview.jpg" class="img-responsive" />
									<p align="justify">Welcome to NMIMS Global Access - School
										for Continuing Education (NGASCE)! You are about to log in to
										the world of Online Learning at NGASCE, a world made possible
										due to a combination of 30 years of legacy of best in class
										education and state of the art learning technology!</p>
									<p align="justify">As you log in using the credentials
										given to you by the University, please take time to go through
										your profile and update your contact information. The details
										mentioned there are your details as per the current University
										Student Database. In case there is any change or any error in
										these details, it will hamper the University to stay in touch
										with you.</p>
									<p align="justify">With this Portal, we hope to provide you
										all the support you need during your enrolment with the
										Program offered by the University. It will be our endeavour to
										keep improving your experience with this Portal as we go
										along.</p>
									<p>Happy Learning!</p>
									<p>
										<b>Team NGASCE</b>
									</p>
								</div>
							</div>
							<%-- <div class="col-md-4">
								            <div class="panel-content-wrapper upcomingNews">
								              <h2 class="text-capitalize">Announcements</h2>
								              <%
								              if(announcements != null){
								            	  int count = 0;
										          for(AnnouncementBean announcement : announcements){
										        	  count++;
										        	  String announcemntBrief = announcement.getDescription().length() > 115 ? announcement.getDescription().substring(0, 114)+"..."  : announcement.getDescription();
										        	  Date formattedDate = formatterHeader.parse(announcement.getStartDate());
										  			  String formattedDateString = dateFormatterHeader.format(formattedDate);
									          %>
									          <a href="#" data-toggle="modal" data-dismiss="modal" data-target="#announcementModal<%=count%>">
									          <!-- <a href="#" onclick="window.open('/studentportal/getAllAnnouncementDetailsForLogin','Announcements', 300, 400);"> -->
								              <div class="szEvents">
								                <h4><%=announcement.getCategory()%></h4>
								                <p class="date"><%=formattedDateString%></p>
								                <p><%=Jsoup.parse(announcemntBrief).text() %></p>
								              </div>
								              </a>
								              <%}
									          }%>
								              
								            </div>
								          </div> --%>
						</div>
					</div>
				</div>
			</div>


		</div>
	</div>


	<%-- 
<%if(noOfAnnouncemntsInLogin > 0){ %>
<!-- MODAL FOR INDIVIDUAL ANNOUNCEMENTS-->
<%
        	 int count = 0;
	          for(AnnouncementBean announcement : announcements){
	        	  count++;
	        	  Date formattedDate = formatterHeader.parse(announcement.getStartDate());
	  			  String formattedDateString = dateFormatterHeader.format(formattedDate);
	          %>
	          
			<div class="modal fade announcement" id="announcementModal<%=count %>" tabindex="-1" role="dialog">
			  <div class="modal-dialog" role="document">
			    <div class="modal-content modal-md">
			      <div class="modal-header">
			        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
			        <h4 class="modal-title">ANNOUNCEMENTS</h4>
			      </div>
			      <div class="modal-body">
			      
				          			
				          			 <h6><%=announcement.getSubject() %></h6>
							        <p><%=announcement.getDescription() %></p>
							         <%if(announcement.getAttachment1() != null){ %>
							        <a target="_blank" href="<spring:eval expression="@propertyConfigurer.getProperty('CONTENT_PREVIEW_PATH')" /><%=announcement.getAttachment1()%>"><%=announcement.getAttachmentFile1Name() %></a><br/>
							        <%} %>
							         <%if(announcement.getAttachment2() != null){ %>
							        <a target="_blank" href="<spring:eval expression="@propertyConfigurer.getProperty('CONTENT_PREVIEW_PATH')" /><%=announcement.getAttachment2()%>"><%=announcement.getAttachmentFile2Name() %></a><br/>
							        <%} %>
							         <%if(announcement.getAttachment3() != null){ %>
							        <a target="_blank" href="<spring:eval expression="@propertyConfigurer.getProperty('CONTENT_PREVIEW_PATH')" /><%=announcement.getAttachment3()%>"><%=announcement.getAttachmentFile3Name() %></a><br/>
							        <%} %>
							        <h4 class="small"><%=formattedDateString%> <span>by</span><a href="#"> <%=announcement.getCategory() %></a></h4>
				         
				          
			       
			      </div>
			      <div class="modal-footer">
			        <button type="button" class="btn btn-default" data-dismiss="modal">DONE</button>
			      </div>
			    </div>
			  </div>
			</div>
 		<% }//End of For loop %>
	          
<%}%> --%>


	<div id="examApp"></div>
	<div id="acadsApp"></div>
	<div id="ltiApp"></div>
	<div id="csApp"></div>
	<jsp:include page="common/footer.jsp" />

	<script>
		    $(document).ready(function(e) {
		        //////Enter Method for submitting form//////
		        $(document).keypress(function (e) {
		        	
		            var key = e.which;
		            if(key == 13){
		                $('#loginBtn').click();
		                return false;
		            }
		        });
		        
		        document.getElementById("userId").focus();
		    });
		    </script>


	<script>
		    	//Logout users from all apps when they visit login page
				$( "#examApp" ).load( "<%=examAppLogoutUrl%>" );
				$( "#acadsApp" ).load( "<%=acadsAppLogoutUrl%>" );
				$( "#ltiApp" ).load( "<%=ltiAppLogoutUrl%>" );
				$( "#csApp" ).load( "<%=csAppLogoutUrl%>" );
			</script>




</body>
</html> 