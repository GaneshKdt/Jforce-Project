<!DOCTYPE html>


<%@page import="com.nmims.helpers.*"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.nmims.beans.StudentBean"%>
<%@page import="com.nmims.beans.LTIConsumerRequestBean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%try{ %>


<%
	ArrayList<LTIConsumerRequestBean> resources_list = (ArrayList<LTIConsumerRequestBean>)session.getAttribute("resources_list");

	int no_resources_list= resources_list != null ? resources_list.size() : 0;
	StudentBean sbean = (StudentBean)request.getSession().getAttribute("student");

%>

<html lang="en">
    
    <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
	<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')" var="server_path" />
	
     
    <jsp:include page="common/jscss.jsp">
	<jsp:param value="E-Learn Resources" name="title"/>
	</jsp:include> 
    <body> 
    	<jsp:include page ="common/header.jsp" />
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="common/breadcrum.jsp">
			<jsp:param value="Student Zone;E-Learn Resources" name="breadcrumItems"/>
			</jsp:include>
            
            <div class="sz-main-content menu-closed">
            	<div class="sz-main-content-inner">
                    
		          	<c:choose>
						<c:when test="${isStudent == 'Y'}">
							<jsp:include page="common/left-sidebar.jsp">
								<jsp:param value="E-Learn" name="activeMenu"/>
							</jsp:include>
						</c:when>
						
						<c:otherwise>
							<jsp:include page="adminCommon/left-sidebar.jsp">
								<jsp:param value="E-Learn" name="activeMenu"/>
							</jsp:include>
						</c:otherwise>
			     	</c:choose>
                    	
 				<div class="sz-content-wrapper dashBoard myCoursesPage">
      				<jsp:include page="common/studentInfoBar.jsp" />
              						
					<div class="sz-content">			
						<h2>E-Learn Resources</h2>
						<div class="clearfix"></div>
																						
						<div class="panel panel-default panel-courses-page">
							<div class="clearfix"></div>
		
						<div class="panel-body" > 
							<div class="data-content">
								<div class="table-responsive">
				          	
				      				<table class="table table-striped ">
										<thead>
											<tr>
												<th>Sr No.</th>
												<th>Resource Name</th>
												<th>Resource Description</th>
												<th>Resource Provider</th>
												<th>Action</th>
												
											</tr>
										</thead>
										
										<tbody>
										
										<c:choose>
											<c:when test="${fn:length(resources_list) gt 0}">
												<c:forEach var="resource" items="${resources_list}" varStatus="status">
											        <tr style="display:table-row;">
											            <td ><c:out value="${status.count}"/></td>
														<td ><c:out value="${resource.context_title}"/></td>
														<td ><c:out value="${resource.resource_link_description}"/></td>
														<%-- <td ><c:out value="Stukent"/></td> --%>
														<td ><c:out value="${resource.provider_name }"/></td>
														<td > 
															<a href="#" onClick="window.open('/ltidemo/viewLTIResource?rid=${resource.resource_id}')" >View</a>
														</td> 					
											        </tr>   
											    </c:forEach>
											</c:when>
											
											<c:otherwise>
												<tr style="display:table-row;">
													<td colspan="5" style="text-align: center;">
														<h4><i class="fas fa-exclamation-circle"></i> No Books Available For You.</h4>
													</td>
												</tr>
											</c:otherwise>
										</c:choose>
											
										</tbody>
									</table>
								</div>
							</div>
					
					<!-- <hr>
					<div style="font-size: 14px"><br>
						<b>Note : </b>
						Currently we have identified issues with Stukent tool access via Student portal, if you are not able to access the tool you can try an alternate method mentioned below.
							<ul>
								<li><b>STEP 1:</b> Visit <a href="https://home.stukent.com" target="blank" style="font-size: 14px"> https://home.stukent.com </a></li>
								<li><b>STEP 2:</b> Enter the registered email ID and click on "Don't remember your password?" </li>
								<li><b>STEP 3:</b> Use the password sent to your email address </li>
							</ul>
							We regret this inconvenience and looking at resolving this at the earliest.
					</div> -->
					
						</div>
					</div>
				</div>
	
				<div class="clearfix"></div>									
   			</div>
		</div>   
	</div>
</div>      
		<c:choose>
			<c:when test="${isStudent == 'Y'}">
				<jsp:include page="common/footer.jsp"/>
			</c:when>
			
			<c:otherwise>
				<jsp:include page="adminCommon/footer.jsp"/>
			</c:otherwise>
     	</c:choose>
            
		   <%}catch(Exception e){
	      	e.printStackTrace();
	      	}%> 
    </body>
</html>