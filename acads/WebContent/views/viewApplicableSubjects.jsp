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
<jsp:param value="Learning Resources" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Learning Resources</legend></div>
        <%@ include file="messages.jsp"%>
		
		
		
	<c:choose>
	<c:when test="${rowCount > 0}">

	<div class="table-responsive panel-body">
	<table class="table table-striped" style="font-size:12px">
						<thead>
						<tr>
							<th>Sr. No.</th>
							<%if(roles.indexOf("Faculty") == -1){ %> 
							<th>Program</th>
							<%} %>
							<th>Subject</th>
							<%if(roles.indexOf("Faculty") == -1){ %> 
							<th>Sem</th>
							<%} %>
							<th>Content</th>
							
						</tr>
					</thead>
						<tbody>
						<form:form  action="" method="post" >
						<c:forEach var="bean" items="${subjects}" varStatus="status">
					        <tr>
					            <td><c:out value="${status.count}" /></td>
					            
					            <%if(roles.indexOf("Faculty") == -1){ %> 
					            <td><c:out value="${bean.program}" /></td>
					            <%} %>
					            
					            <td><c:out value="${bean.subject}" /></td>
					            
					            <%if(roles.indexOf("Faculty") == -1){ %> 
								<td><c:out value="${bean.sem}" /></td>
								<%} %>

								<td> 
						            <c:url value="viewContentForSubject" var="contentUrl">
									  <c:param name="subject" value="${bean.subject}" />
									</c:url>

									<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="${contentUrl}">View Learning Resources</button>
									
					            </td>
					            
					            
					        </tr>   
					    </c:forEach>
						</form:form>
							
						</tbody>
					</table>
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

<html lang="en">
    

	
    
    <jsp:include page="common/jscss.jsp">
	<jsp:param value="Learning Resources" name="title"/>
    </jsp:include>
    
    
    
    <body>
    
    	<%@ include file="common/header.jsp" %>
    	
    	
        
        <div class="sz-main-content-wrapper">
        
        	<%@ include file="common/breadcrum.jsp" %>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="common/left-sidebar.jsp">
								<jsp:param value="My Courses" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="common/studentInfoBar.jsp" %>
              						
              						
              						<div class="sz-content">
								
										<h2 class="red text-capitalize">Learning Resources</h2>
										<div class="clearfix"></div>
		              					<div class="panel-content-wrapper">
											<%@ include file="common/messages.jsp" %>
											
											<c:if test="${rowCount > 0}">
										
											<div class="table-responsive panel-body">
											<table class="table table-striped" style="font-size:12px">
																<thead>
																<tr>
																	<th>Sr. No.</th>
																	<%if(roles.indexOf("Faculty") == -1){ %> 
																	<th>Program</th>
																	<%} %>
																	<th>Subject</th>
																	<%if(roles.indexOf("Faculty") == -1){ %> 
																	<th>Sem</th>
																	<%} %>
																	<th>Content</th>
																	
																</tr>
															</thead>
																<tbody>
																<form:form  action="" method="post" >
																<c:forEach var="bean" items="${subjects}" varStatus="status">
															        <tr>
															            <td><c:out value="${status.count}" /></td>
															            
															            <%if(roles.indexOf("Faculty") == -1){ %> 
															            <td><c:out value="${bean.program}" /></td>
															            <%} %>
															            
															            <td><c:out value="${bean.subject}" /></td>
															            
															            <%if(roles.indexOf("Faculty") == -1){ %> 
																		<td><c:out value="${bean.sem}" /></td>
																		<%} %>
										
																		<td> 
																            <c:url value="viewContentForSubject" var="contentUrl">
																			  <c:param name="subject" value="${bean.subject}" />
																			</c:url>
										
																			<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="${contentUrl}">View Learning Resources</button>
																			
															            </td>
															            
															            
															        </tr>   
															    </c:forEach>
																</form:form>
																	
																</tbody>
															</table>
											</div>
										
											<br>
										
											</c:if>
											
										</div>
              								
              						</div>
              				</div>
              		
                            
					</div>
            </div>
        </div>
            
  	
        <jsp:include page="common/footer.jsp"/>
            
		
    </body>
</html>