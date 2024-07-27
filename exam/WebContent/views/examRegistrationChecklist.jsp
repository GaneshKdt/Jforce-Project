 <!DOCTYPE html>
<%@page import="java.util.*"%>
<%@page import="java.text.DateFormat"%>
<html lang="en">
<% 
String roles = "";
UserAuthorizationExamBean userAuthorization = (UserAuthorizationExamBean)session.getAttribute("userAuthorization");
if(userAuthorization != null){
	roles = (userAuthorization.getRoles() != null && !"".equals(userAuthorization.getRoles())) ? userAuthorization.getRoles() : roles;
}
 %>

<%@page import="com.nmims.beans.Page"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

    <jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Exam Registration CheckList" name="title"/>
    </jsp:include>
    <body>
    	<%@ include file="adminCommon/header.jsp" %>
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Executive Registration;Checklist" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
          				<jsp:include page="adminCommon/left-sidebar.jsp">
				<jsp:param value="" name="activeMenu"/>
			</jsp:include>
            	<div class="sz-content-wrapper examsPage">
   						
   						<div class="sz-content">
						<h2 class="red text-capitalize">Executive Registration Checklist</h2>
						<div class="clearfix"></div>
							<div class="panel-content-wrapper" style="min-height:450px;">
								<%@ include file="adminCommon/messages.jsp" %>
								<div class="col-md-16 column">
				<legend>&nbsp;Check List<font size="2px">  </font></legend>
				<div class="table-responsive">
				<table class="table table-striped" style="font-size:12px">
						<thead>
						<%-- <%if(roles.indexOf("TEE Admin") != -1  || roles.indexOf(" Admin") != -1){ %> --%>
						<tr>
							<th><a href="/exam/uploadSASTimetableForm" target="_blank">Upload Timetable</a> </th>
							<td>Click Link To Upload Timetable for Executive Students.</td>
						</tr>
						<tr>
							<th ><a href="/exam/makeLiveSubjectsEntryForm" target="_blank">Setup Subjects for Exam</a></th>
							<td>Click Link to Add Subjects for Executive Students</td>
						</tr>
						<tr>
							<th><a href="/exam/makeExecutiveTimetableLiveForm" target="_blank">Make Timetable Live</a>
							/ <a href="/exam/adminExecutiveTimeTableForm" target="_blank">View Time Table</a>       
							</th>
							<td>Click Link to Make Executive Timetable Live</td>
						</tr>
						
						<%-- <% } %> --%>
						<%-- <%if(roles.indexOf("Exam Admin") != -1  || roles.indexOf("Admin") != -1){ %> --%>
						<tr>
							<th><a href="/exam/addExecutiveExamCenterForm" target="_blank">Add Exam Center</a> 
							/ <a href="/exam/addExecutiveExamCenterFormMassUpload" target="_blank">Mass Exam Center</a> 
							/ <a href="/exam/searchExecutiveExamCenter" target="_blank">Search Exam Center</a> 
							</th>
							<td>Click Link to Add Executive Exam Centers</td>
						</tr>
						<tr>
							<th><a href="/exam/makeExecutiveRegistrationLiveForm" target="_blank">Make Registration Live</a></th>
							<td>Click Link to make Registration Live for Executive students</td>
						</tr>	
						
						<tr>
							<th><a href="/exam/makeExecutiveRegistrationLiveForm" target="_blank">Make HallTicket Live</a></th>
							<td>Click Link to make HallTicket Live for Executive students</td>
						</tr>	
						<%-- <% } %> --%>
						<%-- <%if(roles.indexOf("Acads Admin") != -1  || roles.indexOf("Admin") != -1){ %> --%>
						<tr>
							<th><a href="/exam/admin/onlineExamPasswordReportForm" target="_blank">Generate Exam HallTicket Password</a></th>
							<td>Click Link to generate Exam HallTicket password for Executive students</td>
						</tr>
						<%-- <% } %> --%>
						</thead>
						<tbody>
						</tbody>
					</table>
				</div>
				</div></div></div></div></div></div></div>
		    
        <jsp:include page="adminCommon/footer.jsp"/>
        

</body>
</html>
 
