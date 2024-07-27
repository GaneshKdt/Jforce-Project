<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@page import="org.jsoup.Jsoup"%>
<%@page import="com.nmims.beans.ForumStudentPortalBean"%>
<%try{ %>
<%
List<ForumStudentPortalBean> listOfForumsRelatedToSubject = (List<ForumStudentPortalBean>)session.getAttribute("listOfForumsRelatedToSubjectInSession");
String sortForumBy = (String)session.getAttribute("sortForumBy");
HashMap<Long,String> mapOfForumThreadAndReplyCount = (HashMap<Long,String>)session.getAttribute("mapOfForumThreadAndReplyCount");
ForumStudentPortalBean mostRecentPost = new ForumStudentPortalBean();
int numberOfForumThreads = 0;
if(listOfForumsRelatedToSubject!=null && listOfForumsRelatedToSubject.size()>0){
	mostRecentPost = listOfForumsRelatedToSubject.get(0);
	
}
numberOfForumThreads =listOfForumsRelatedToSubject.size();
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
</style>
<link rel="stylesheet" href="assets/css/tablesaw.css"> 

<div class="course-forums-m-wrapper">

			<div class=" panel-courses-page">
				<div class="panel-heading" role="tab" id="">
					<h2>Forum</h2>
					<!---TOP TABS-->
			<div class="custom-clearfix clearfix"></div>
			<ul class="topRightLinks list-inline">

				<!-- <li> <a data-toggle="modal" data-target="#newPost" class="new-post">NEW POST</a> </li> -->
				<li><a class="panel-toggler collapsed" role="button"
					data-toggle="collapse" href="#collapseSix" aria-expanded="true"></a></li>
				<div class="clearfix"></div>
			</ul>
			<div class="clearfix"></div>
		</div>

		<div class="clearfix"></div>

		<%-- <%if(numberOfForumThreads == 0){ %>
		<div id="collapseSix" class="panel-collapse collapse academic-schedule courses-panel-collapse panel-content-wrapper" role="tabpanel">
		<%}else{ %> --%>
		<div id="collapseSix" class="collapse in academic-schedule courses-panel-collapse panel-content-wrapper accordion-has-content">
		<%-- <%} %>	 --%>
			<div class="panel-body" style="padding: 20px;"> 
			
			
				<%if(numberOfForumThreads == 0){ %>
					<div class="no-data-wrapper">

						<h6 class="no-data nodata"><span class="icon-icon-view-submissions"></span>No Discussion Threads</h6> 
					</div>

				<%}else{ %>
				<div class="row data-content panel-body">
						<div class="forum ">
							<i class="icon-icon-view-submissions"></i>

							<span><%=numberOfForumThreads %></span> Discussion Threads 
						 </div>
						 
						<div class="forum">
							<table class="table table-hover tables">
							  <thead>
							    <tr>
							      <th scope="col"><a >User</a></th>
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
							     <span class="post-info"><h5><img src="assets/images/admin.png" width="35px" style="border-radius: 50%;">&nbsp&nbsp&nbsp&nbsp<%=bean.getFacultyFullName() %></h5></span></td>
							      <td scope="row" >
							      	<span class="description" > 
								      	<a href="/studentportal/viewForumThreadChain?id=<%=bean.getId()%>&subject=<%=URLEncoder.encode(bean.getSubject(),"UTF-8")%>">
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
					</div>

					<%} %>
				</div>
			</div>

		</div>
	</div>
	<%}catch(Exception e){
 	}%>
<!--  	<script src="https://code.jquery.com/jquery-3.3.1.js"></script> -->
<!--  	<script src="https://cdn.datatables.net/1.10.20/js/jquery.dataTables.min.js"></script> -->


 	<script>
 	$(document).ready(function() {
 	    $('.tables').DataTable( {
			destroy: true,
 	        "order": [[ 3, "desc" ]]
 	    });
 	} );
 	</script>