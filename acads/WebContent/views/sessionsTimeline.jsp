
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
 <!DOCTYPE html>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>



<html lang="en">
    

	
    
    <jsp:include page="common/jscss.jsp">
	<jsp:param value="Sessions Timeline" name="title"/>
    </jsp:include>
    
     
    
    <body>
    
    	<%@ include file="common/header.jsp" %>
    	
    	
        
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="common/breadcrum.jsp">
		<jsp:param value="Student Zone;Exam;Sessions Timeline" name="breadcrumItems"/>
		</jsp:include>
        	
          <%try{ %>  
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="common/left-sidebar.jsp">
								<jsp:param value="Academic Calendar" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="common/studentInfoBar.jsp" %>
              						
              						
              						<div class="sz-content">
              							<%@include file="common/messages.jsp" %>
              						
										<div class="calendar-operations row">
											<div class="col-md-4 col-sm-12  col-xs-12">
													<div class="calendar-navigation">
														<h2 id="month">Sessions Timeline</h2>
														
													</div>
											</div>
											
											
											<div class="clearfix"></div>
										</div>
										
										<div class="row">
											<div class="col-lg-12 col-md-12 col-sm-12  col-xs-12">
												<!-- code for page goes here start -->
															<c:if test="${fn:length(scheduledSessionList) > 0}">				
																<div class="">
																	<table class="table-responsive table table-striped table-hover tables"
																		style="">
																		<thead>
																			<tr>
																				<th>Subject</th>
																				<th>Session Name</th>
																				<th>Track</th>
																				<th>Date</th>

																				<th>Start Time</th>
																				<th>Attended</th>
																				<th>Videos</th>
																			</tr>
																		</thead>
																		<tbody>

																			<c:forEach var="session" items="${scheduledSessionList}"
																				varStatus="status">
																				<tr>
																				
																					<td><c:out value="${session.subject}" /></td>
																					<td><c:out value="${session.sessionName}" /></td>
																					<td><c:out value="${session.track}" /></td>
																					<td><c:out value="${session.date}" /></td>
																					<td><c:out value="${session.startTime}" /></td>
																					<td>
																						<c:choose>
																						
																							<c:when test="${session.attended=='Yes'}">
																								Yes
																							</c:when>
																							<c:otherwise>
																								No
																							</c:otherwise>
																						
																						</c:choose>	
																					</td>
																					<td>
																						<ul>
																						<c:forEach var="video" items="${session.videosOfSession}" varStatus="vStatus">
																							<li style="margin-bottom:5px;">
																							
																								<%--  <c:if test="${(not empty video.firstName) && (not empty video.lastName)}">  --%>
																								  <c:if test="${(not empty video.facultyName)}"> 
 																									Video By <a href="/studentportal/facultyProfile?facultyId=${video.facultyId}" target="_blank">${video.facultyName}.</a> 
																									<%-- Video By Prof. ${video.firstName}&nbsp;${video.lastName}  --%>
																									<br>
																								 </c:if> 
																								<div class="row">
																								<div class="col-xs-1">

																								<a href="/acads/student/watchVideos?id=${video.id}&pssId=${session.programSemSubjectId}" title="Watch Video" class="">

																									<b>
																										<i style=" color:red;" class="fa-regular fa-play-circle" aria-hidden="true"></i>
																									</b>
																								</a> 
																								</div>
																								
																								<div class="col-xs-11">
																							 	<style>
																							 		.videoDownloadLink {
																									  pointer-events: none;
																									  cursor: default;
																									}
																							 	</style>
																							 	
																							 	<!-- 
																							 	<a  href="${video.mobileUrlHd}" class="videoDownloadLink">
																							 		<b>
																										<i style="font-size:20px; " class="fa fa-download" aria-hidden="true"></i>
																									</b>
																							 	</a>
																							 	<a  href="#" class="videoDownloadLink">
																							 		<b>
																										<i style="font-size:20px; " class="fa fa-download" aria-hidden="true"></i>
																									</b>
																							 	</a>
																							 	(Download Disable, Will be available in due time.)
																							 	(Right Click on button and Click "Save Link As").
																							 	-->
																							 	</div>
																							 	</div>
																							</li>
																						</c:forEach>
																						</ul>
																					</td>
																				</tr>
																			</c:forEach>


																		</tbody>
																	</table>
																</div>	
																</c:if>
												<!-- code for page goes here end -->
											</div>
											
											
											<div class="clearfix"></div>
										</div>
              								
              						</div>
              				</div>
              		
                            
					</div>
            </div>
        </div>
            
  	
        <jsp:include page="common/footer.jsp"/>
        		<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
	<script src="/exam/assets/js/jquery-1.11.3.min.js"></script>
	<script src="/exam/assets/js/bootstrap.js"></script>
<script src="/exam/assets/js/jquery.tabledit.js"></script>

	<script src="/exam/resources_2015/js/vendor/jquery-ui.min.js"></script>
	<script
		src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
	<script src="/exam/resources_2015/js/vendor/dataTables.bootstrap.js"></script>
	<script
		src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js"></script>
        		
        		<script>
		
	$('.tables').DataTable( {
        initComplete: function () {
            this.api().columns().every( function () {
                var column = this;
                var headerText = $(column.header()).text();
                console.log("header :"+headerText);
                if(headerText == "Subject")
                {
                   var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
                    .appendTo( $(column.header()) )
                    .on( 'change', function () {
                        var val = $.fn.dataTable.util.escapeRegex(
                            $(this).val()
                        );
 
                        column
                            .search( val ? '^'+val+'$' : '', true, false )
                            .draw();
                    } );
 
                column.data().unique().sort().each( function ( d, j ) {
                    select.append( '<option value="'+d+'">'+d+'</option>' )
                } );
              }
 	
                
                
                if(headerText == "Track")
                {
                   var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
                    .appendTo( $(column.header()) )
                    .on( 'change', function () {
                        var val = $.fn.dataTable.util.escapeRegex(
                            $(this).val()
                        );
 
                        column
                            .search( val ? '^'+val+'$' : '', true, false )
                            .draw();
                    } );
 
                column.data().unique().sort().each( function ( d, j ) {
                    select.append( '<option value="'+d+'">'+d+'</option>' )
                } );
              }
            } );
        }
    } );
	
	</script>
	<script>
	$('.videoDownloadLink').click(function(event) {
	    event.preventDefault();
	});
</script> 
            
		
		<% } catch(Exception e){;
		  
		}%>
    </body>
</html>