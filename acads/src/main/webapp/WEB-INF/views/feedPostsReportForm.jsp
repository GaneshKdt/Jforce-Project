
 <!DOCTYPE html>
<html lang="en">
	
<%@page import="com.nmims.beans.PersonAcads"%>
<%@page import="com.nmims.beans.*"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.text.SimpleDateFormat"%>



<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

    <jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Feed Posts Report" name="title"/>
    </jsp:include>
    
    <link rel="stylesheet" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/css/dataTables.bootstrap.css"> 
    <%
    List<FeedPostsBean> feedPostsList =(List<FeedPostsBean>)request.getAttribute("feedPostsList");
    %>
    <body>
    <%try{ %>
    	<include file="adminCommon/header.jsp" >
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Feed Posts Report" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="adminCommon/left-sidebar.jsp">
								<jsp:param value="" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="adminCommon/adminInfoBar.jsp" %>
              						<div class="sz-content">
								
											<h2 class="red text-capitalize">Feed Posts Report</h2>
											<div class="clearfix"></div>
											<div class="panel-content-wrapper" >
											<!-- style="min-height:450px;" -->
											<%@ include file="adminCommon/messages.jsp" %>
									<form:form  action="feedPostsReport" method="post" modelAttribute="feedPostsBean">									
											<fieldset>
											<div class="col-md-4 column">

													<div class="form-group">
														<form:select id="writtenYear" path="acadYear"  required="required"	class="form-control"   itemValue="${feedPostsBean.acadYear}">
															<form:option value="">Select Academic Year</form:option>
															<form:options items="${acadYearList}" />
														</form:select>
													</div>
													
													<div class="form-group">
														<form:select id="writtenMonth" path="acadMonth"  required="required"  class="form-control"  itemValue="${feedPostsBean.acadMonth}">
															<form:option value="">Select Academic Month</form:option>
															<form:options items="${acadMonthList}" />
														</form:select>
													</div>
													
												</div>
												
												
												
												<div class="col-md-4 column">
																							
										<div class="form-group"> 
												<button id="submit" name="submit" class="btn  btn-primary" formaction="feedPostsReport">Generate</button>												
												<button id="cancel" name="cancel" class="btn btn-danger" formaction="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />home" formnovalidate="formnovalidate">Cancel</button>
										</div>

												</div>
										</fieldset>
								</form:form>
							</div> 
						
		<div class="">
			<c:if test="${not empty feedPostsList}">
				
			<div class="row">
			<div class="col-md-8">
			
				<h2>
				Feed Posts (${feedPostsListSize }) </i>    
				</h2>
			
			</div>
			<div>
				<a href="/acads/admin/downloadFeedPostsReport" class="btn btn-large btn-primary" title="Download Feed Posts Report">Download Excel</a>
			</div>
			</div>
			<div>
						<table id="dataTable" class="table table-striped table-hover" style="font-size:12px">
						<thead>
							<tr> 							
								<th>Sr. No.</th>
								<th>Acad Year</th>
								<th>Acad Month</th>
								<th>Batch Name</th>
								<th>Subject</th>
								<th>Term</th>
								<th>Post URL </th>
								<th>Post Description</th>									
								<th>Comments</th>	
								<th>Faculty Name</th>						
								<th>Posted On </th>	
								<th>Post Type</th>
							</tr>
						</thead>		
						<tbody>
							<%
							int srCount = 0;
							for(FeedPostsBean feedPost : feedPostsList){ %>
							<tr>
									<td> <%= ++srCount %> </td>
									<td><%=feedPost.getAcadYear() %></td>
									<td><%=feedPost.getAcadMonth() %></td>
									<td><%=feedPost.getBatch() %>
									<td><%=feedPost.getSubject() %></td>
									<td><%=feedPost.getTerm() %></td>				
									<td><%=feedPost.getPostURL() %></td>									
									<td><%=feedPost.getPostDescription() %></td>		
								<td ><a href="javascript:void(0)" data-id="<%=feedPost.getPost_id() %>" class="commonLinkbtn"><%=feedPost.getNoOfComments() %> comments</a></td>																													
									<td><%=feedPost.getFacultyName()  %> </td>																		
									<td><%=feedPost.getPostedOn() %></td>											
									<td><%=feedPost.getPostType() %></td>		
												
							</tr>
							<%} %>
	
						</tbody>		
						
					
					</table>
				
			</div>		
			</c:if>
		</div>
				</div>
              			</div>
    				</div>
			   </div>
		    </div>
        <jsp:include page="adminCommon/footer.jsp"/>
        <%}catch(Exception e){e.printStackTrace();}%>
		
		
		 <div id="myModal" class="modal fade" role="dialog">
  <div class="modal-dialog">

    <!-- Modal content-->
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">&times;</button>
        <h4 class="modal-title">Post Comments</h4>
      </div>
      <div class="modal-body modalBody">
      	
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
      </div>
    </div>

  </div>
</div>
    </body>
    
       <script
              src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
       <script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
       <script
              src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.buttons.min.js"></script>

<script type="text/javascript">

$(document).ready (function(){
       $('#dataTable').DataTable();

});
</script>
  <script type="text/javascript">

$(document).ready(function() {
	
	$('.commonLinkbtn').on('click',function(){

		let id = $(this).attr('data-id');
		
		
		let modalBody = "<center><h4>Loading...</h4></center>";
		let data = {
			'post_id':id
		};
		$.ajax({
			   type : "POST",
			   contentType : "application/json",
			   url : "/acads/admin/getCommentsByPostId",   
			   data : JSON.stringify(data),
			   success : function(data) {
				   
			
				   modalBody = '<div class="table-responsive"> <table class="table"> <th>Sapid</th> <th>Comment</th> <th>Posted On</th> </thead><tbody>';
				   for(let i=0;i < data.length;i++){
					   modalBody = modalBody + '<tr><td>'+ data[i].sapid +'</td><td>'+ data[i].comment +'</td><td>'+ data[i].postedOn +'</td></tr>';
				   }
				  				   
				   modalBody = modalBody + '<tbody></table></div>';
				   $('.modalBody').html(modalBody);
			   },
			   error : function(e) {
				   alert("Please Refresh The Page.")
			   }
		});
		$('.modalBody').html(modalBody);
		//modal-body
		$('#myModal').modal('show');
	});
	
	
 
});

</script>
 
    
</html>