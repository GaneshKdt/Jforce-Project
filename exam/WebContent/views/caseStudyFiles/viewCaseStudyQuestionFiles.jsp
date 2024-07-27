<!DOCTYPE html>
<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.AssignmentStatusBean"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<html lang="en">
    

	
    
    <jsp:include page="../common/jscss.jsp">
	<jsp:param value="All Case Study Question Files" name="title"/>
    </jsp:include>
    
    
    
    <body>
    
    	<%@ include file="../common/header.jsp" %>
    	
    	
        
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="../common/breadcrum.jsp">
		<jsp:param value="Student Zone;Exam;All Case Study Question Files" name="breadcrumItems"/>
		</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<%@ include file="../common/left-sidebar.jsp" %>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="../common/studentInfoBar.jsp" %>
              						
              						
              						<div class="sz-content">
								
										<h2 class="red text-capitalize">${rowCount} Case Study Question Files</h2>
										<div class="clearfix"></div>
		              					<div class="panel-content-wrapper">
											<%@ include file="../common/messages.jsp" %>
											
											<c:if test="${rowCount > 0 }">
											<div class="table-responsive">
											<table class="table table-striped table-hover" style="font-size:12px">
																<thead>
																	<tr> 
																		<th>Sr. No.</th>
																		<th>Batch Year</th>
																		<th>Batch Month</th>
																		<th>Subject</th>
																		<th>File</th>
																	</tr>
																</thead>
																<tbody>
																
																<c:forEach var="caseStudyList" items="${caseStudyList}" varStatus="status">
															        <tr>
															            <td><c:out value="${status.count}"/></td>
																		<td><c:out value="${caseStudyList.batchYear}"/></td>
																		<td><c:out value="${caseStudyList.batchMonth}"/></td>
																		<td nowrap="nowrap"><c:out value="${caseStudyList.topic}"/></td>
																		<td><a href="#" onClick="window.open('<spring:eval expression="@propertyConfigurer.getProperty('CASESTUDY_QUESTION_PATH')" />${caseStudyList.questionFilePreviewPath}')" /><i class="fa-solid fa-download fa-lg"></i></a></td>
																		
															        </tr>   
															    </c:forEach>
																	
																	
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
            
  	
        <jsp:include page="../common/footer.jsp"/>
            
		
    </body>
</html>