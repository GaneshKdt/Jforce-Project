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
	<jsp:param value="Regular Exam Registration" name="title"/>
    </jsp:include>
    <body>
    	<%@ include file="adminCommon/header.jsp" %>
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Regular Exam Registration Checklist" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
          				<jsp:include page="adminCommon/left-sidebar.jsp">
				<jsp:param value="" name="activeMenu"/>
			</jsp:include>
            	<div class="sz-content-wrapper examsPage">
   						
   						<div class="sz-content">
						<h2 class="red text-capitalize">Regular/Corporate Exam Registration Checklist</h2>
						<div class="clearfix"></div>
							<div class="panel-content-wrapper" style="min-height:450px;">
								<%@ include file="adminCommon/messages.jsp" %>
								<div class="col-md-16 column">
				<legend>&nbsp;Check List<font size="2px">  </font></legend>
				<div class="table-responsive">
				<table class="table table-striped" style="font-size:12px">
						<thead>
						<tr>
							<th><a href="/exam/uploadTimetableForm" target="_blank">Upload Timetable</a> </th>
							<td>Click Link To Upload Timetable for Regular/Corporate Students.</td>
						</tr>
						<tr>
							<th><a href="/exam/admin/makeResultsLiveForm" target="_blank">Make Timetable Live</a>     
							</th>
							<td>Click Link to Make Timetable Live for Regular/Corporate Students.</td>
						</tr>
						<tr>
							<th ><a href="/exam/adminTimeTableForm" target="_blank">View TimeTable</a></th>
							<td>Click Link to View Uploaded Time Table Regular/Corporate Students.</td>
						</tr>
					
						<tr>
							<th><a href="/exam/addExamCenterForm" target="_blank">Add Exam Center</a> 
							/ <a href="/exam/excelUploadForExamCentersForm" target="_blank">Mass Exam Center Regular Students</a> 
							/ <a href="/exam/searchExamCenterForm" target="_blank">Search Exam Center</a> 
							</th>
							<td>Click Link to Add Exam Centers Regular/Corporate Students.</td>
						</tr>
						<tr>
							<th ><a href="/exam/centerUserMappingForm" target="_blank">Corporate Center User Mapping</a></th>
							<td>Click Link to Upload Corporate Students Center User Mapping</td>
						</tr>
						
						<tr>
							<th><a href="/exam/uploadExamFeeExemptForm" target="_blank">Fee Exempt students</a></th>
							<td>Click Link to Upload SemWise Fee exempt students</td>
						</tr>
						<tr>
							<th><a href="/exam/uploadExamFeeExemptSubjectsForm" target="_blank">Subject Fee Exempt students</a></th>
							<td>Click Link to Upload Subject Wise Fee exempt students</td>
						</tr>
						
						<tr>
							<th><a href="/exam/admin/changeConfigurationForm" target="_blank">Make Registration Live</a></th>
							<td>Click Link to make Registration Live for regular students</td>
						</tr>	
						
						<tr>
							<th><a href="/exam/admin/changeConfigurationForm" target="_blank">Make HallTicket Live</a></th>
							<td>Click Link to make HallTicket Live for students</td>
						</tr>	
						<tr>
							<th><a href="/exam/admin/onlineExamPasswordReportForm" target="_blank">Generate Exam HallTicket Password</a></th>
							<td>Click Link to generate Exam HallTicket password for students</td>
						</tr>
						</thead>
						<tbody>
						</tbody>
					</table>
				</div>
				</div></div></div></div></div></div></div>
		    
        <jsp:include page="adminCommon/footer.jsp"/>
        

</body>
</html>
 
