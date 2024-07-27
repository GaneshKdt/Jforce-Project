<%@page import="com.nmims.beans.AnnouncementCareerservicesBean"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>

<%
String collapseAnnouncementsSection = "";
ArrayList<AnnouncementCareerservicesBean> announcements = (ArrayList<AnnouncementCareerservicesBean>)session.getAttribute("announcements");
int noOfAnnouncemnts = announcements != null ? announcements.size() : 0;
SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy");

if(noOfAnnouncemnts > 0){
	collapseAnnouncementsSection = "in";//Adding "in" class will expand Announcements section. Expand only when Announcements are present
}
%>

<div class="announcements">
    <div class="panel panel-default">
      <div class="panel-heading" role="tab" id="">
        <h4 class="panel-title"> ANNOUNCEMENTS</h4>
        <ul class="topRightLinks list-inline">
          <li><a href="#" data-toggle="modal" data-target="#announcementModalAll">SEE ALL</a></li>
          <!-- <li><a href="/studentportal/getAllAnnouncementDetails" >SEE ALL</a></li> -->
          
          <li><a class="panel-toggler collapsed"  role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseFive" aria-expanded="true"></a></li>
        </ul>
        <div class="clearfix"></div>
      </div>
      <div id="collapseFive" class="panel-collapse collapse <%=collapseAnnouncementsSection%> courses-panel-collapse" role="tabpanel" aria-labelledby="headingFive">
        <div class="panel-body"> 
        
        	<%
                 	if(noOfAnnouncemnts == 0){
                 	%>
        			<div class="no-data-wrapper no-border">
			         	<p class="no-data">There are No Active Announcements</p>  
			          </div>
        	<%
        	}else{
        	%>
        
          <!--Announcement section content -->
          <section class="col-md-12 p-closed announcement-collapse">
             <div class="no-data-wrapper no-border">
	         	<p class="no-data">Expand section to see Announcements</p>  
	          </div>
          </section>
          
          <%
                    SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
                      		  SimpleDateFormat dateFormatter1 = new SimpleDateFormat("dd-MMM-yyyy");
                              int anouncemntCount = 0;
                              for(AnnouncementCareerservicesBean announcement : announcements){
                            	  anouncemntCount++;
                            	  Date formattedDate = formatter1.parse(announcement.getStartDate());
                      			  String formattedDateString = dateFormatter1.format(formattedDate);
                            	  String announcemntBrief = announcement.getDescription().length() > 115 ? announcement.getDescription().substring(0, 114) +"..." : announcement.getDescription();
                    %>
          			<a href="#" data-toggle="modal" data-dismiss="modal" data-target="#announcementModal<%=anouncemntCount%>"><section class="col-md-4">
			            <h6><%=announcement.getSubject()%></h6>
			            <p><%=announcemntBrief%></p>
			            <h4 class="small"><%=formattedDateString%> <span>by</span> <a href="#" onclick="return false;" style="cursor:default;"><%=announcement.getCategory()%></a></h4>
			          </section></a>
          
          <%
                    if(anouncemntCount == 3){
                    	        	  break;
                    	          }
                              
                              }
                    %>
          
          <%
                    }
                    %>
          
        </div>
      </div>
     

      
      
      
    </div>
  </div>
  <%
  if(noOfAnnouncemnts > 0){
  %>
<!-- MODAL FOR INDIVIDUAL ANNOUNCEMENTS-->
<%
int countInModal = 0;
for(AnnouncementCareerservicesBean announcement : announcements){
countInModal++;
Date formattedDate = formatter.parse(announcement.getStartDate());
String formattedDateString = dateFormatter.format(formattedDate);
%>

<div class="modal fade announcement" id="announcementModal<%=countInModal%>" tabindex="-1" role="dialog">
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
<a target="_blank" href="${pageContext.request.contextPath}<%=announcement.getAttachment1()%>"><%=announcement.getAttachmentFile1Name() %></a><br/>
<%} %>
<%if(announcement.getAttachment2() != null){ %>
<a target="_blank" href="${pageContext.request.contextPath}<%=announcement.getAttachment2()%>"><%=announcement.getAttachmentFile2Name() %></a><br/>
<%} %>
<%if(announcement.getAttachment3() != null){ %>
<a target="_blank" href="${pageContext.request.contextPath}<%=announcement.getAttachment3()%>"><%=announcement.getAttachmentFile3Name() %></a><br/>
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
  
  
  
  