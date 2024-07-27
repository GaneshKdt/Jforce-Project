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
	<jsp:param value="TCS Checklist" name="title"/>
    </jsp:include>
    <body>
    	<%@ include file="adminCommon/header.jsp" %>
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="TCS ; Checklist" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                <div class="sz-main-content-inner">
          				<jsp:include page="adminCommon/left-sidebar.jsp">
						<jsp:param value="" name="activeMenu"/>
						</jsp:include>
					<div class="sz-content-wrapper examsPage">
							
						<div class="sz-content">
							<h2 class="red text-capitalize">TCS</h2>
							<div class="clearfix"></div>
							<div class="panel-content-wrapper" style="min-height:450px;">
								<%@ include file="adminCommon/messages.jsp" %>
								<div class="col-md-16 column">
									<legend>&nbsp;Check List<font size="2px">  </font></legend>
									<div class="table-responsive">
										<table class="table table-striped" style="font-size:12px">
											<thead>
												<tr>
													<th><a href="/exam/pullMarksDataFromTCSForm" target="_blank">Pull TCS Results</a> / <a href="viewTCSMarks" target="_blank">View TCS Results</a> </th>
													<td>Click Link To Retrive TCS Results./ View TCS Results.</td>
												</tr>
												<tr>
													<th><a href="/exam/transferTCSResultsToOnlineMarksForm" target="_blank">Transfer TCS Results To Marks Table</a> </th>
													<td>Click Link To Transfer TCS Results To Marks Table.</td>
												</tr>
												<tr>
													<th ><a href="/exam/insertExamBookingDataForm" target="_blank">Insert Exam Booking Details</a></th>
													<td>Click Link to Insert Exam Booking Details TCS</td>
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