<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@page import="org.jsoup.Jsoup"%>
<%@page import="com.nmims.beans.ForumStudentPortalBean"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>  
<%try{ %>
<%
List<ForumStudentPortalBean> listOfForumsRelatedToSubject = (List<ForumStudentPortalBean>)session.getAttribute("listOfForumsRelatedToSubjectInSession");
List<ForumStudentPortalBean> listOfForumsRelatedToSubjectBacklog = (List<ForumStudentPortalBean>)session.getAttribute("listOfForumsRelatedToSubjectInSessionBacklog");

HashMap<Long,String> mapOfForumThreadAndReplyCount = (HashMap<Long,String>)session.getAttribute("mapOfForumThreadAndReplyCount");
HashMap<Long,String> mapOfForumThreadAndReplyCountBacklog = (HashMap<Long,String>)session.getAttribute("mapOfForumThreadAndReplyCountBacklog");

ForumStudentPortalBean mostRecentPost = new ForumStudentPortalBean();
ForumStudentPortalBean mostRecentPostBacklog = new ForumStudentPortalBean();

int numberOfForumThreads = 0;
int numberOfForumThreadsBacklog = 0;

if(listOfForumsRelatedToSubject!=null && listOfForumsRelatedToSubject.size()>0){
	mostRecentPost = listOfForumsRelatedToSubject.get(0);
	
}
if(listOfForumsRelatedToSubjectBacklog!=null && listOfForumsRelatedToSubjectBacklog.size()>0){
	mostRecentPostBacklog = listOfForumsRelatedToSubjectBacklog.get(0);
	
}

numberOfForumThreads = listOfForumsRelatedToSubject.size();
numberOfForumThreadsBacklog = listOfForumsRelatedToSubjectBacklog.size();

%>
<style>
.nodata { 
    vertical-align: middle;
    color: #a6a8ab;
    font: 1.00em "Open Sans";
    text-align: center;
    margin: 0;
}
.table-hover tr:hover { 
    background-color: #ffffff!important; /* Assuming you want the hover color to be white */
}
table.dataTable thead .sorting:after, table.dataTable thead .sorting_asc:after, table.dataTable thead .sorting_desc:after {
    position: sticky;
}

.dataTables_wrapper.form-inline .col-sm-9{
width: 100%!important;
}
table.dataTable thead>tr>th { 
padding-right: 0px!important; 
 }
 
 @media only screen and (max-width: 425px) {
  .dataTables_length select {
        margin-bottom : 10%;
        display: flex;
        justify-content: flex-start;
     } 
 }
</style>
<link rel="stylesheet" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/css/tablesaw.css"> 


<c:set var="programSemSubjectIdWithSubjectForBacklog" value="${sessionScope.programSemSubjectIdWithSubjectForBacklog}" />
<c:set var="programSemSubjectIdFromUrl" value="${param.programSemSubjectId}" />

 <div class="container-fluid float-start bg-white rounded"  id="forumContainer">
 <div>
 
 	<h4 class="text-danger text-uppercase mt-3">Forum</h4>
		<c:if
			test="${fn:length(programSemSubjectIdWithSubjectForBacklog)>0 and fn:contains(programSemSubjectIdWithSubjectForBacklog.keySet(),programSemSubjectIdFromUrl)}">
			<br>
			<select
				class=" form-select form-select-sm col-auto float-end switchContent" id="cycle" >
				<option selected value="current Cycle Content">Current Cycle Content</option>
				<option value="Last Cycle Content">Last Cycle Content</option>
			</select>
			<br>
			<br>
		</c:if>
	</div>

		<!-- 	<ul class="topRightLinks list-inline" >
				<li><a class="panel-toggler collapsed" role="button"
					data-toggle="collapse" href="#collapseSix" aria-expanded="true"></a></li>
				<div class="clearfix"></div>
			</ul> -->
	           
	           
	
				<%if(numberOfForumThreads == 0 && numberOfForumThreadsBacklog==0){ %>
					<div class="no-data-wrapper">
						<h6 class="no-data nodata text-center fw-bold">
						<span class="fa-solid fa-message"></span>
						 No Discussion Threads</h6> 
					</div>
                   <br>



				<%}else{ %>
				
				<div class="current-cycle">
				<div class="forum fw-bold">
							<i class="icon-icon-view-submissions"></i>

							<span><%=numberOfForumThreads %></span> 
							 Discussion Threads 
						 </div>
						 
						<div class="forum table-responsive">
							<table class="table table-hover tables">
							  <thead>
							    <tr>
							      <th scope="col"><a>User</a></th>
							      <th scope="col"><a >Title</a></th>
							      <th scope="col"><a >Replies</a></th>
							      <!-- <th scope="col">Views</th> -->
							      <th scope="col"><a>Started</a></th>
							    </tr>
							  </thead>
							  <tbody>
						<%if(listOfForumsRelatedToSubject!=null && listOfForumsRelatedToSubject.size()>0){ 
							 int countToShow = 0;
							 for(ForumStudentPortalBean bean:listOfForumsRelatedToSubject){
								 countToShow++;
								 Date dNow = new Date();
								 Date temp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(bean.getCreatedDate());
								 long diff = Math.abs(dNow.getTime() - temp.getTime());
								 long hours = diff / (60 * 60 * 1000);
								 String descriptionWithoutHtmlCharacters = Jsoup.parse(bean.getDescription()).text();
						 %>	  
							  
							    <tr>
							     <td >
							     <span class="post-info"><h5><img src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/images/admin.png" width="35px" style="border-radius: 50%;">&nbsp&nbsp&nbsp&nbsp<%=bean.getFacultyFullName() %></h5></span></td>
							      <td scope="row" >
							      	<span class="description" > 
								      	<a href="/forum/student/viewForumThreadChain?id=<%=bean.getId()%>&programSemSubjectId=${programSemSubjectId}">
												<b><%=bean.getTitle()%></b>
										</a>
									</span>
								  </td>
							      <td><span class=""><%=mapOfForumThreadAndReplyCount.get(bean.getId())%> </span></td>
							      <td><span class="post-info"> <%=(hours > 24) ? hours/24+" Days ago": hours+"Hours ago"%><span></td>
							    </tr>
							  
						<%} %>
						<%} %>	 </tbody> 
							</table>
							<div class="row">
							
								<div class="col-sm-10">
									
								</div>
								<div class="col-sm-2">

								</div>
								<div class="col-sm-7">
									
								</div>
								<div class="col-sm-5">
									
								</div>
								<div class="clearfix"></div>


							</div>
						</div>
						
						<%if(numberOfForumThreads > 6){ %>
						<div class="load-more-table">
							<a>+<%=(numberOfForumThreads -6) %> More Discussions <span
								class="icon-accordion-closed"></span></a>
						</div>
						<%} %>
				<!-- 	</div> -->
				</div>
				<div class="last-cycle">
				<div class="forum fw-bold">
							<i class="icon-icon-view-submissions"></i>

							<span><%=numberOfForumThreadsBacklog %></span> 
							 Discussion Threads
						 </div>
						 
						<div class="forum table-responsive">
							<table class="table table-hover tables">
							  <thead>
							    <tr>
							      <th scope="col"><a>User</a></th>
							      <th scope="col"><a >Title</a></th>
							      <th scope="col"><a >Replies</a></th>
							      <!-- <th scope="col">Views</th> -->
							      <th scope="col"><a>Started</a></th>
							    </tr>
							  </thead>
							  <tbody>
						<%if(listOfForumsRelatedToSubjectBacklog!=null && listOfForumsRelatedToSubjectBacklog.size()>0){ 
							 int countToShow = 0;
							 for(ForumStudentPortalBean bean:listOfForumsRelatedToSubjectBacklog){
								 countToShow++;
								 Date dNow = new Date();
								 Date temp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(bean.getCreatedDate());
								 long diff = Math.abs(dNow.getTime() - temp.getTime());
								 long hours = diff / (60 * 60 * 1000);
								 String descriptionWithoutHtmlCharacters = Jsoup.parse(bean.getDescription()).text();
						 %>	  
							  
							    <tr>
							     <td >
							     <span class="post-info"><h5><img src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/images/admin.png" width="35px" style="border-radius: 50%;">&nbsp&nbsp&nbsp&nbsp<%=bean.getFacultyFullName() %></h5></span></td>
							      <td scope="row" >
							      	<span class="description" > 
								      	<a href="/forum/student/viewForumThreadChain?id=<%=bean.getId()%>&programSemSubjectId=${programSemSubjectId}">
												<b><%=bean.getTitle()%></b>
										</a>
									</span>
								  </td>
							      <td><span class=""><%=mapOfForumThreadAndReplyCountBacklog.get(bean.getId())%> </span></td>
							      <td><span class="post-info"> <%=(hours > 24) ? hours/24+" Days ago": hours+"Hours ago"%><span></td>
							    </tr>
							  
						<%} %>
						<%} %>	 </tbody> 
							</table>
							<div class="row">
							
								<div class="col-sm-10">
									
								</div>
								<div class="col-sm-2">

								</div>
								<div class="col-sm-7">
									
								</div>
								<div class="col-sm-5">
									
								</div>
								<div class="clearfix"></div>


							</div>
						</div>
						
						<%if(numberOfForumThreadsBacklog > 6){ %>
						<div class="load-more-table">
							<a>+<%=(numberOfForumThreadsBacklog -6) %> More Discussions <span
								class="icon-accordion-closed"></span></a>
						</div>
						<%} %>
				<!-- 	</div> -->
				</div>
				
					<%} %>
			
		

	   <br>
</div>
	
	
	
	<%}catch(Exception e){}%>
		<script type="text/javascript"
		src="<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')" />studentportal/assets/js/forum.js"></script>
