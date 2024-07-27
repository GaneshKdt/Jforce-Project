<!DOCTYPE html>


<%@page import="java.util.List"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<html lang="en">
    

	
    
    <jsp:include page="common/jscss.jsp">
	<jsp:param value="Welcome to Student Zone" name="title"/>
    </jsp:include>
    
   <%
   List<AnnouncementStudentPortalBean> announcementList = (List<AnnouncementStudentPortalBean>)request.getAttribute("announcementsPortal");
 
   		int noOfAnnouncemnts = announcementList.size();
   		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
   		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy");
   %> 
    
    <body>
    
    	<%@ include file="common/header.jsp" %>
    	
    	
        
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="common/breadcrum.jsp">
		<jsp:param value="Student Zone;Announcements" name="breadcrumItems"/>
		</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<%@ include file="common/left-sidebar.jsp" %>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="common/studentInfoBar.jsp" %>
              						
              						
              						<div class="sz-content">
								
										<h2 class="red text-capitalize">All Announcements</h2>
										<div class="clearfix"></div>
		              					<div class="panel-content-wrapper">
											<%@ include file="common/messages.jsp" %>
											<div class="table-responsive">
												<table class="table table-striped" style="font-size:12px" id="datafill">
													<thead>
														<tr>
															<th>Sr. No.</th>
															<th>Subject</th>
															<th>Start Date</th>
															<th>End Date</th>
															<th>Category</th>
															
														</tr>
													</thead>
												 <tbody>
												 	<c:forEach var="announcement" items="${announcementsPortal}" varStatus="status">
					       						<tr>
									            <td><c:out value="${status.count}"/></td>
									            <td><a href="#" data-toggle="modal" data-dismiss="modal" data-target="#announcementModalForStudent${status.count}"><c:out value="${announcement.subject}"/></a></td>
									            <td><c:out value="${announcement.startDate}"/></td>
									            <td><c:out value="${announcement.endDate}"/></td>
									            <td><c:out value="${announcement.category}"/></td>
									         </tr>   
					   						 </c:forEach>
												 
												 </tbody>
												
												</table>
											
											</div>
											<%-- <c:url var="firstUrl" value="getAllStudentAnnouncements?pageNo=1" />
			<c:url var="lastUrl"
			value="getAllStudentAnnouncements?pageNo=${page.totalPages}" />
			<c:url var="prevUrl"
			value="getAllStudentAnnouncements?pageNo=${page.currentIndex - 1}" />
			<c:url var="nextUrl"
			value="getAllStudentAnnouncements?pageNo=${page.currentIndex + 1}" /> --%>


	<%-- 	<c:choose>
			<c:when test="${page.totalPages > 1}">
				<div align="center">
					<ul class="pagination">
						<c:choose>
							<c:when test="${page.currentIndex == 1}">
								<li class="disabled"><a href="#">&lt;&lt;</a></li>
								<li class="disabled"><a href="#">&lt;</a></li>
							</c:when>
							<c:otherwise>
								<li><a href="${firstUrl}">&lt;&lt;</a></li>
								<li><a href="${prevUrl}">&lt;</a></li>
							</c:otherwise>
						</c:choose>
						<c:forEach var="i" begin="${page.beginIndex}"
							end="${page.endIndex}">
							<c:url var="pageUrl" value="getAllStudentAnnouncements?pageNo=${i}" />
							<c:choose>
								<c:when test="${i == page.currentIndex}">
									<li class="active"><a href="${pageUrl}"><c:out
												value="${i}" /></a></li>
								</c:when>
								<c:otherwise>
									<li><a href="${pageUrl}"><c:out value="${i}" /></a></li>
								</c:otherwise>
							</c:choose>
						</c:forEach>
						<c:choose>
							<c:when test="${page.currentIndex == page.totalPages}">
								<li class="disabled"><a href="#">&gt;</a></li>
								<li class="disabled"><a href="#">&gt;&gt;</a></li>
							</c:when>
							<c:otherwise>
								<li><a href="${nextUrl}">&gt;</a></li>
								<li><a href="${lastUrl}">&gt;&gt;</a></li>
							</c:otherwise>
						</c:choose>
					</ul>
				</div>
			</c:when>
		</c:choose> --%>
										</div>
              								
              						</div>
              						
              				</div>
              		
                            
					</div>
					<%if(noOfAnnouncemnts > 0){ %>
<!-- MODAL FOR INDIVIDUAL ANNOUNCEMENTS-->
<%
int countInModal = 0;
for(AnnouncementStudentPortalBean announcement : announcementList){
countInModal++;
Date formattedDate = formatter.parse(announcement.getStartDate());
String formattedDateString = dateFormatter.format(formattedDate);
%>

<div class="modal fade announcement" id="announcementModalForStudent<%=countInModal%>" tabindex="-1" role="dialog">
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
<a target="_blank" href="<spring:eval expression="@propertyConfigurer.getProperty('ANNOUNCEMENT_PREVIEW_PATH')" /><%=announcement.getAttachment1()%>"><%=announcement.getAttachmentFile1Name() %></a><br/>
<%} %>
<%if(announcement.getAttachment2() != null){ %>
<a target="_blank" href="<spring:eval expression="@propertyConfigurer.getProperty('ANNOUNCEMENT_PREVIEW_PATH')" /><%=announcement.getAttachment2()%>"><%=announcement.getAttachmentFile2Name() %></a><br/>
<%} %>
<%if(announcement.getAttachment3() != null){ %>
<a target="_blank" href="<spring:eval expression="@propertyConfigurer.getProperty('ANNOUNCEMENT_PREVIEW_PATH')" /><%=announcement.getAttachment3()%>"><%=announcement.getAttachmentFile3Name() %></a><br/>
<%} %>


<h4 class="small"><%=formattedDateString%> <span>by</span><a href="#"> <%=announcement.getCategory() %></a></h4>

</div>
<div class="modal-footer">
<button type="button" class="btn btn-default" data-dismiss="modal">DONE</button>
</div>
</div>
</div>
</div>
<%}//End of For loop %>
<%} %>
            </div>
        </div>
        
            <script>
			$(document).ready(function() {
			    $('#datafill').DataTable( {
			        "pagingType": "full_numbers"
			    } );
			} );
			
			</script>
  	
        <jsp:include page="common/footer.jsp"/>
            
		
    </body>
</html>