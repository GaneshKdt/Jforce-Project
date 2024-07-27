<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>

<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Classroom Full" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Full Capacity Reached</legend></div>
        <%@ include file="messages.jsp"%>
		
	
	
	<div class="panel-body">
	<p>
	Dear Student,<br>
	There has been an overwhelming response for today's live online lecture session, and we have reached full capacity. <br>
	We request you to login and join the repeat session which is scheduled as follows:
	</p>
	</div>
	<c:choose>
	<c:when test="${rowCount > 0}">

	<div class="panel-body">
	<h2>&nbsp;Scheduled Sessions</h2>
	<div class="table-responsive">
	<table class="table table-striped" style="font-size:12px">
			<thead>
			<tr>
				<th>Sr. No.</th>
				<th>Subject</th>
				<th>Session</th>
				<th>Date</th>
				<th>Day</th>
				<th>Start Time</th>
				<th>Faculty Name</th>
			</tr>
			</thead>
			<tbody>
			
			<c:forEach var="bean" items="${scheduledSessionList}" varStatus="status">
		        <tr>
		            <td><c:out value="${status.count}" /></td>
		            <td><c:out value="${bean.subject}" /></td>
		            <td><c:out value="${bean.sessionName}" /></td>
		            <td><c:out value="${bean.date}" /></td>
					<td><c:out value="${bean.day}" /></td>
					<td><c:out value="${bean.startTime}" /></td>
					<td><c:out value="${bean.firstName} ${bean.lastName}" /></td>
		            
		        </tr>   
		    </c:forEach>
				
			</tbody>
		</table>
	</div>
	</div>
	<br>

	</c:when>
	</c:choose>


	</div>
	</section>

	  <jsp:include page="footer.jsp" />


</body>
</html>
 --%>
 
 <!DOCTYPE html>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html lang="en">
    

	
    
    <jsp:include page="common/jscss.jsp">
	<jsp:param value="Full Capacity Reached" name="title"/>
    </jsp:include>
    
    
    
    <body>
    
    	<%@ include file="common/header.jsp" %>
    	
    	
        
        <div class="sz-main-content-wrapper">
        
        	<%@ include file="common/breadcrum.jsp" %>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="common/left-sidebar.jsp">
								<jsp:param value="Academic Calendar" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="common/studentInfoBar.jsp" %>
              						
              						
              						<div class="sz-content">
								
										<h2 class="red text-capitalize">Full Capacity Reached</h2>
										<div class="clearfix"></div>
		              					<div class="panel-content-wrapper">
											<%@ include file="common/messages.jsp" %>
											
											<p>
											Dear Student,<br>
											There has been an overwhelming response for today's live online lecture session, and we have reached full capacity.<br>
											We request you to refer recordings of this session which will be available on Student Zone in 48 hours: 
											</p>
											<p>
											You can however try by clicking below Meetings to check for availability.
											</p>
											
											<!--
											<div class="col-md-3 col-sm-9 col-xs-18">
												<form:form modelAttribute="session">
													<input type="hidden" name="meetingKey" value ="${session.meetingKey}"/>
													<input type="hidden" name="meetingPwd" value ="${session.meetingPwd}"/>
													<button class="btn btn-large btn-primary" formaction="joinFullMeeting">Try Meeting 1</button>
												</form:form>
											</div>
											
											<c:if test="${not empty session.altMeetingKey}">
												<div class="col-md-3 col-sm-9 col-xs-18">
													<form:form modelAttribute="session">
														<input type="hidden" name="meetingKey" value ="${session.altMeetingKey}"/>
														<input type="hidden" name="meetingKey" value ="${session.altMeetingPwd}"/>
														<button class="btn btn-large btn-primary" formaction="joinFullMeeting">Try Meeting 2</button>
													</form:form>
												</div>
											</c:if>
											
											<c:if test="${not empty session.altMeetingKey2}">
												<div class="col-md-3 col-sm-9 col-xs-18">
													<form:form modelAttribute="session">
														<input type="hidden" name="meetingKey" value ="${session.altMeetingKey2}"/>
														<input type="hidden" name="meetingKey" value ="${session.altMeetingPwd2}"/>
														<button class="btn btn-large btn-primary" formaction="joinFullMeeting">Try Meeting 3</button>
													</form:form>
												</div>
											</c:if>
											
											<c:if test="${not empty session.altMeetingKey3}">
												<div class="col-md-3 col-sm-9 col-xs-18">
													<form:form modelAttribute="session">
														<input type="hidden" name="meetingKey" value ="${session.altMeetingKey3}"/>
														<input type="hidden" name="meetingKey" value ="${session.altMeetingPwd3}"/>
														<button class="btn btn-large btn-primary" formaction="joinFullMeeting">Try Meeting 4</button>
													</form:form>
												</div>
											</c:if>
											-->
											
										</div>
										
										<h2>&nbsp;Scheduled Sessions</h2>
										<div class="clearfix"></div>
										<div class="panel-content-wrapper">	
											
											<div class="table-responsive">
											<table id="dataTable" class="table table-striped" style="font-size:12px">
													<thead>
													<tr>
														<th>Sr. No.</th>
														<th>Subject</th>
														<th>Session</th>
														<th>Date</th>
														<th>Day</th>
														<th>Start Time</th>
														<th>Faculty Name</th>
													</tr>
													</thead>
													<tbody>
													
													<c:forEach var="bean" items="${scheduledSessionList}" varStatus="status">
												        <tr>
												            <td><c:out value="${status.count}" /></td>
												            <td><c:out value="${bean.subject}" /></td>
												            <td><c:out value="${bean.sessionName}" /></td>
												            <td><c:out value="${bean.date}" /></td>
															<td><c:out value="${bean.day}" /></td>
															<td><c:out value="${bean.startTime}" /></td>
															<td><c:out value="${bean.firstName} ${bean.lastName}" /></td>
												            
												        </tr>   
												    </c:forEach>
														
													</tbody>
												</table>
											</div>
											
										</div>
              								
              						</div>
              				</div>
              		
                            
					</div>
            </div>
        </div>
  	
    <jsp:include page="common/footer.jsp"/>
    
    <script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />assets/js/bootstrap.js"></script> 
    <script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/jquery-ui.min.js"></script>  
	<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js" ></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
	<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js" ></script>

    <script>
		$(document).ready(function() {
		    $('#dataTable').DataTable();
		} );
	</script>
		
    </body>
</html>