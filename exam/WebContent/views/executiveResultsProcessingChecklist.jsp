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
	<jsp:param value="Exam Results Processing Checklist" name="title"/>
    </jsp:include>
    <body>
    	<%@ include file="adminCommon/header.jsp" %>
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Exam Results Processing ; Checklist" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                <div class="sz-main-content-inner">
          				<jsp:include page="adminCommon/left-sidebar.jsp">
						<jsp:param value="" name="activeMenu"/>
						</jsp:include>
					<div class="sz-content-wrapper examsPage">
							
						<div class="sz-content">
							<h2 class="red text-capitalize">Executive Results Processing Checklist</h2>
							<div class="clearfix"></div>
							<div class="panel-content-wrapper" style="min-height:450px;">
								<%@ include file="adminCommon/messages.jsp" %>
								<div class="col-md-16 column">
									<legend>&nbsp;Check List<font size="2px">  </font></legend>
									<div class="table-responsive">
										<table class="table table-striped" style="font-size:12px">
											<thead>
											
												<tr>
													<th ><a href="/exam/admin/uploadWrittenMarksForm" target="_blank">Upload Executive Marks</a></th>
													<td>Click Link to Upload Executive Marks</td>
												</tr>
												
												<tr>
													<th ><a href="/exam/insertExecutiveABRecordsForm" target="_blank">Download Executive Exam Absent Students List</a></th>
													<td>Click Link to Download Executive Exam Absent Students List</td>
												</tr>
												
												<tr>
													<th ><a href="/exam/admin/uploadWrittenMarksForm" target="_blank">Upload Executive Exam Absent Students List</a></th>
													<td>Click Link to Upload Executive Exam Absent Students List</td>
												</tr>
												
												<tr>
													<th ><a href="/exam/admin/passFailTriggerSearchForm" target="_blank">Pass Fail Trigger</a></th>
													<td>Click Link to Pass Fail Trigger </td>
												</tr>
												
												<tr>
													<th ><a href="/exam/makeExecutiveRegistrationLiveForm" target="_blank">Make Results Live</a></th>
													<td>Click Link to Make Results Live</td>
												</tr>
												<tr>
													<th ><a href="/studentportal/loginAsForm" target="_blank">Login As Student</a></th>
													<td>Click Link to Login As Student to Verify Results</td>
												</tr>
											</thead>							
										</table>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>    
        <jsp:include page="adminCommon/footer.jsp"/>
	</body>
</html> 