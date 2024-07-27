<%@page import="com.nmims.beans.AnnouncementStudentPortalBean"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>

<%

String collapseAnnouncementsSection = "";
ArrayList<AnnouncementStudentPortalBean> announcements = (ArrayList<AnnouncementStudentPortalBean>)session.getAttribute("announcementsPortal");
int noOfAnnouncemnts = announcements != null ? announcements.size() : 0;
SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy");

if(noOfAnnouncemnts > 0){
	collapseAnnouncementsSection = "in";//Adding "in" class will expand Announcements section. Expand only when Announcements are present
}
%>

<%if(noOfAnnouncemnts > 0){ %>
<div class=" col-md-6 mb-2"> 
<div class="d-flex align-items-center text-wrap ">
		<span class="fw-bold me-3"><small class="fs-5">ANNOUNCEMENTS</small></span>
		<div class="ms-auto">
			<a href="#" data-bs-toggle="modal" data-bs-target="#announcements" class="text-dark me-1"><small class="text-nowrap">SEE
				ALL</small></a> <a type="button" data-bs-toggle="collapse" href="#collapseEight"
				role="button" aria-expanded="true" aria-controls="collapseEight" class="text-muted"
				id="collapseCard"> <i class="fa-solid fa-square-minus"></i></a>
			</ul>
		</div>
		
	</div>
		

				<%if(noOfAnnouncemnts == 0){ %>
				<div class="card card-body text-center">
					<h6 ><i class="fa-solid fa-bullhorn"></i>
			<small> There are No Active Announcements</small></h6>	
				</div>
				<%}else{ %>

				<!--Announcement section content -->
				<div class="collapse " id="collapseEight">
					<div class="card card-body text-center">
					<h6 ><i class="fa-solid fa-bullhorn"></i><small class="text-dark"> View Announcements</small></h6>									
					</div>
				</div>
		<div class="collapse show " id="collapseEight">
 <div class="list-group">
				<%
          SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
  		  SimpleDateFormat dateFormatter1 = new SimpleDateFormat("dd-MMM-yyyy");
          int anouncemntCount = 0;
          for(AnnouncementStudentPortalBean announcement : announcements){
        	  anouncemntCount++;
        	  Date formattedDate = formatter1.parse(announcement.getStartDate());
  			  String formattedDateString = dateFormatter1.format(formattedDate);
        	  String announcemntBrief = announcement.getDescription().length() > 115 ? announcement.getDescription().substring(0, 200) : announcement.getDescription();
          %>
         		
				<a href="#"  data-dismiss="modal" data-bs-toggle="modal" data-bs-target="#announcementModal<%=anouncemntCount %>"
					 class="text-dark list-group-item list-group-item-action">
					
						<h6 ><%=announcement.getSubject() %></h6>
						<%-- <p><%=announcement.getDescription() %></p>     commented by discussion with pranit sir--%>
						<p ><%=formattedDateString%>
							<span>by <%=announcement.getCategory() %></span>
						</p>
				</a>
			

				<%
	          if(anouncemntCount == 3){
	        	  break;
	          }
          
          } %>
         </div> 
</div>
				<%} %>



<%if(noOfAnnouncemnts > 0){ %>
<!-- MODAL FOR INDIVIDUAL ANNOUNCEMENTS-->
<%
int countInModal = 0;
for(AnnouncementStudentPortalBean announcement : announcements){
countInModal++;
Date formattedDate = formatter.parse(announcement.getStartDate());
String formattedDateString = dateFormatter.format(formattedDate);
%>

			
<div class="modal fade" id="announcementModal<%=countInModal %>" tabindex="-1" aria-labelledby="modalLabel" aria-hidden="true">
  <div class="modal-dialog modal-dialog-scrollable">
    <div class="modal-content">
      <div class="modal-header">
        <h1 class="modal-title fs-5" id="modalLabel">ANNOUCEMENTS</h1>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
			<div class="modal-body">
				<h6><%=announcement.getSubject() %></h6>
				<p><%=announcement.getDescription() %></p>

				<%if(announcement.getAttachment1() != null){ %>
				<a target="_blank"
					href="<spring:eval expression="@propertyConfigurer.getProperty('ANNOUNCEMENT_PREVIEW_PATH')" /><%=announcement.getAttachment1()%>"><%=announcement.getAttachmentFile1Name() %></a><br />
				<%} %>
				<%if(announcement.getAttachment2() != null){ %>
				<a target="_blank"
					href="<spring:eval expression="@propertyConfigurer.getProperty('ANNOUNCEMENT_PREVIEW_PATH')" /><%=announcement.getAttachment2()%>"><%=announcement.getAttachmentFile2Name() %></a><br />
				<%} %>
				<%if(announcement.getAttachment3() != null){ %>
				<a target="_blank"
					href="<spring:eval expression="@propertyConfigurer.getProperty('ANNOUNCEMENT_PREVIEW_PATH')" /><%=announcement.getAttachment3()%>"><%=announcement.getAttachmentFile3Name() %></a><br />
				<%} %>


				<h4 class="small"><%=formattedDateString%>
					<span>by</span><a href="#"> <%=announcement.getCategory() %></a>
				</h4>

			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-bs-dismiss="modal">Done</button>
			</div>
		</div>
	</div>
</div>
<% }//End of For loop %>

<%}%>
</div>
<%} %>


