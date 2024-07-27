
 <!DOCTYPE html>
<%@page import="java.util.*"%>
<%@page import="java.text.DateFormat"%>
<html lang="en">
	

<%@page import="com.nmims.beans.Page"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

    <jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Registration CheckList" name="title"/>
    </jsp:include>
    <body>
    	<%@ include file="adminCommon/header.jsp" %>
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Exam Registration;Checklist" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
          				<jsp:include page="adminCommon/left-sidebar.jsp">
				<jsp:param value="" name="activeMenu"/>
			</jsp:include>
            	<div class="sz-content-wrapper examsPage">
   						<%@ include file="adminCommon/adminInfoBar.jsp" %>
   						<div class="sz-content">
						<h2 class="red text-capitalize">Exam Registration Checklist</h2>
						<div class="clearfix"></div>
							<div class="panel-content-wrapper" style="min-height:450px;">
								<%@ include file="adminCommon/messages.jsp" %>
								<div class="col-md-16 column">
				<legend>&nbsp;Check List<font size="2px">  </font></legend>
				<div class="table-responsive">
				<table class="table table-striped" style="font-size:12px">
						<thead>
						<tr>
							<th><a href="/exam/uploadTimetableForm">Upload Timetable</a> </th>
							<td>Click Link To Upload Exam Timetable.</td>
						</tr>
						<tr>
							<th><a href="/exam/admin/makeTimetableLive">Make Timetable Live</a></th>
							<td>Click Link to Make Exam Timetable Live</td>
						</tr>
					
						<tr>
							<th><a href="/exam/addExamCenterForm">Add Exam Center</a></th>
							<td>Click Link to Add Exam Centers</td>
						</tr>
						
						<tr>
							<th><a href="/exam/admin/onlineExamPasswordReportForm">Generate Exam HallTicket Password</a></th>
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
 
