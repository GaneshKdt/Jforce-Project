<%@page import="org.jsoup.Jsoup"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.nmims.beans.UserAuthorizationBean"%>
<%@page import="com.nmims.beans.PersonCareerservicesBean"%>
<%@page import="com.nmims.beans.StudentCareerservicesBean"%>
<%@page import="com.nmims.beans.AnnouncementCareerservicesBean"%>
<%@page import="java.util.ArrayList"%>
 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
  
<style>
	
	.sz-main-content-inner{
		min-height: calc(100vh);
	}


	.icon{
		font-size: 2.2rem;
	}
	
	.material-icon-containter{
	    display: inline-flex;
	    vertical-align: middle;
    }
</style>
<%
ArrayList<AnnouncementCareerservicesBean> announcementsInHeader =  new ArrayList<AnnouncementCareerservicesBean>();
if(session.getAttribute("announcements") != null){
	announcementsInHeader = (ArrayList<AnnouncementCareerservicesBean>)session.getAttribute("announcements");
}

	int noOfAnnouncemntsInHeader = announcementsInHeader != null ? announcementsInHeader.size() : 0;
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
	<div class="container-fluid" style="background-color: inherit">
			<div class="sz-logo">
				<img src="assets/images/logo.png" class="img-responsive" alt="" />
			</div>
		
			
			<%if(session.getAttribute("userId") != null){ %>
						<ul class="sz-header-menu">
							<%
								boolean consumerProgramStructureHasCSAccess = false;
								if(session.getAttribute("consumerProgramStructureHasCSAccess") != null){
									consumerProgramStructureHasCSAccess = (boolean) session.getAttribute("consumerProgramStructureHasCSAccess");
								}
								boolean csActiveInHeader = false;
								if(session.getAttribute("student_careerservices") != null){
									csActiveInHeader = ((StudentCareerservicesBean) session.getAttribute("student_careerservices")).isPurchasedOtherPackages();
								}
								if(!"true".equals(hideProfileLink) && !csActiveInHeader && consumerProgramStructureHasCSAccess){ %>
								<li><a href="/careerservices/showAllProducts"><span class="fa fa-briefcase"></span>Career Services</a></li>
							<%} %>
							
							<%if(!"true".equals(hideProfileLink)){ %>
								<li><a href="/studentportal/myEmailCommunicationsForm"><i class="fa fa-envelope icon pb-1"></i><br>My Communications</a></li>
							<%} %>
							<%if(!"true".equals(hideProfileLink)){ %>
								<li><a href="/studentportal/updateProfile"><span class="icon-my-profile"></span>My Profile</a></li>
							<%} %>
						    <li><a href="/logout"><span class="icon-logout"></span>Logout</a></li>
						    <li class="notification-link"><a href="" data-toggle="modal" data-keyboard="true" data-target="#new-announcements">
						    	<span class="icon-notification"><div class="notification-count"><%=noOfAnnouncemntsInHeader %></div></span></a> 
						    </li>
						</ul>
						
						<!-- Announcements Modal -->
					    	<div id="new-announcements" class="modal fade" role="dialog" tabindex="-1">
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
									          for(AnnouncementCareerservicesBean announcement : announcementsInHeader){
									        	  count++;
									        	  String announcementWithoutHtmlCharacgters = Jsoup.parse(announcement.getDescription()).text();
									        	  String announcemntBrief = announcementWithoutHtmlCharacgters.length() > 150 ? announcementWithoutHtmlCharacgters.substring(0, 149)+"..."  : announcementWithoutHtmlCharacgters;
									        	  Date formattedDate = formatterHeader.parse(announcement.getStartDate());
									  			  String formattedDateString = dateFormatterHeader.format(formattedDate);
									          %>
									          			
									          			<li> <a href="#" data-toggle="modal" data-dismiss="modal" data-target="#announcementModal<%=count%>">
									          			<!-- <li> <a href="#" onclick="return false;" style="cursor:default;"> -->
									          			
									                    <h3><%=announcement.getSubject() %></h3>
									                    <p><%=announcemntBrief %></p>
									                    <h4><%=formattedDateString %> by <span><%=announcement.getCategory() %></span> </h4>
									                    </a> </li>
									          <%
										          if(count == 4){
										        	  break;
										          }
									          } %>
					                </ul>
					              </div>
					              <div class="modal-footer">
					                <button type="button" class="btn btn-default" data-dismiss="modal" data-toggle="modal" 
					                                                            data-target="#announcementModalAll">See All</button>
					                                                            
					                <!-- <button type="button" class="btn btn-default" onclick="window.location.href='/studentportal/getAllAnnouncementDetails'">See All</button> -->
					              </div>
					            </div>
					          </div>
					        </div>
			<%}else{ %>
			<%} %>
		
		<div class="clearfix"></div>
	</div>
</div>
<!--header ends-->

		
<%if(noOfAnnouncemntsInHeader > 0){ %>
		<!--MODAL FOR ALL ANNOUNCEMENT-->

<div class="modal fade announcementModalTop" id="announcementModalAll" tabindex="-1" role="dialog">
  <div class="modal-dialog modal-md" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title">ALL ANNOUNCEMENTS</h4>
      </div>
      <div class="modal-body">
        <ul>
        
        	<%
        	 int count = 0;
	          for(AnnouncementCareerservicesBean announcement : announcementsInHeader){
	        	  count++;
	        	  String announcementWithoutHtmlCharacgters = Jsoup.parse(announcement.getDescription()).text();
	        	  String announcemntBrief = announcementWithoutHtmlCharacgters.length() > 150 ? announcementWithoutHtmlCharacgters.substring(0, 149)+"..."  : announcementWithoutHtmlCharacgters;
	        	  Date formattedDate = formatterHeader.parse(announcement.getStartDate());
	  			  String formattedDateString = dateFormatterHeader.format(formattedDate);
	          %>
				          
				        <li> <a href="#" data-toggle="modal" data-dismiss="modal" data-target="#announcementModal<%=count%>">
				        <!-- <li> <a href="#" onclick="return false;" style="cursor:default;"> -->
			            <h3><%=announcement.getSubject() %></h3>
			            <p><%=announcemntBrief %></p>
			     
			            <h4><%=formattedDateString%> by <span><%=announcement.getCategory() %></span> </h4>
			            </a> </li>
	          
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
	          for(AnnouncementCareerservicesBean announcement : announcementsInHeader){
	        	  count++;
	        	  Date formattedDate = formatterHeader.parse(announcement.getStartDate());
	  			  String formattedDateString = dateFormatterHeader.format(formattedDate);
	          %>
	          
			<div class="modal fade announcement" id="announcementModal<%=count %>" tabindex="-1" role="dialog">
			  <div class="modal-dialog" role="document">
			    <div class="modal-content modal-md">
			      <div class="modal-header">
			        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
			        <h4 class="modal-title">ANNOUCEMENTS</h4>
			      </div>
			      <div class="modal-body">
			      
				          			
				          			 <h6><%=announcement.getSubject() %></h6>
							        <p><%=announcement.getDescription() %></p>
							         <%if(announcement.getAttachment1() != null){ %>
								        <a target="_blank" href="${pageContext.request.contextPath}/<%=announcement.getAttachment1()%>"><%=announcement.getAttachmentFile1Name() %></a><br/>
								        <%} %>
								         <%if(announcement.getAttachment2() != null){ %>
								        <a target="_blank" href="${pageContext.request.contextPath}/<%=announcement.getAttachment2()%>"><%=announcement.getAttachmentFile2Name() %></a><br/>
								        <%} %>
								         <%if(announcement.getAttachment3() != null){ %>
								        <a target="_blank" href="${pageContext.request.contextPath}/<%=announcement.getAttachment3()%>"><%=announcement.getAttachmentFile3Name() %></a><br/>
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
	          
<%}%>