<!DOCTYPE html>
<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.AssignmentStatusBean"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<html lang="en">
    

	
    
    <jsp:include page="../common/jscssNew.jsp">
	<jsp:param value="All Assignment Submissions Till Date" name="title"/>
    </jsp:include>
    
    <body>
    
    	<%@ include file="../common/header.jsp" %>
    	
    	
        
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="../common/breadcrum.jsp">
		<jsp:param value="Student Zone;Exam;All Assignment Submissions" name="breadcrumItems"/>
		</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
                    	<div id="sticky-sidebar"> 
						<jsp:include page="../common/left-sidebar.jsp">
							<jsp:param value="Assignment" name="activeMenu" />
						</jsp:include>
						</div>              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="../common/studentInfoBar.jsp" %>
              						
              						
              						<div class="sz-content">
								
										<h5 class="text-danger fw-bolder text-capitalize mt-3 mb-3" >${rowCount} Assignment Submissions Till Date</h5>
										<div class="clearfix"></div>
		              					<div class=" container-fluid bg-light mt-2 mb-2 rounded ">
											<%@ include file="../common/messages.jsp" %>
											
											<c:if test="${rowCount > 0 }">
											<!-- <div class="table-responsive"> -->
											<table id="datatab" class="table table-striped table-responsive  mt-2 mb-2  " style="width:100%">
											<!-- <table class="table table-striped table-hover" style="font-size:12px"> -->
																<thead>
																	<tr> 
																		<th>Sr. No.</th>
																		<th>Exam Year</th>
																		<th>Exam Month</th>
																		<th>Subject</th>
																		<th>Student ID</th>
																		<th>File</th>
																	</tr>
																</thead>
																<tbody>
																
																<c:forEach var="assignmentFile" items="${assignmentFilesList}" varStatus="status">
															        <tr>
															            <td><c:out value="${status.count}"/></td>
																		<td><c:out value="${assignmentFile.year}"/></td>
																		<td><c:out value="${assignmentFile.month}"/></td>
																		<td nowrap="nowrap"><c:out value="${assignmentFile.subject}"/></td>
																		<td><c:out value="${assignmentFile.sapId}"/></td>
																		
																		<td><a href="#" onClick="window.open('<spring:eval expression="@propertyConfigurer.getProperty('ASSIGNMENT_PREVIEW_PATH')" />${assignmentFile.previewPath}')" /><i class="fa-solid fa-download fa-lg"></i></a></td>
																		
															        </tr>   
															    </c:forEach>
																	
																	
																</tbody>
															</table>
											<!-- </div> -->
											</c:if>
										</div>
              								
              						</div>
              				</div>
              		
                            
					</div>
            </div>
        </div>
            
  	
        <jsp:include page="../common/footerNew.jsp"/>
            
		
		
    </body>
</html>



