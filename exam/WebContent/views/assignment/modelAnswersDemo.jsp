<!DOCTYPE html>
<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.AssignmentStatusBean"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<html lang="en">
    

	
    
    <jsp:include page="../common/jscssNew.jsp">
	<jsp:param value="Assignment Model Answers" name="title"/>
    </jsp:include>
    
    
    
    <body>
    
    	<%@ include file="../common/header.jsp" %>
    	
    	
        
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="../common/breadcrum.jsp">
		<jsp:param value="Student Zone;Exam;Assignment Model Answers" name="breadcrumItems"/>
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
								
										<h5 class="text-danger fw-bolder text-capitalize mt-3 mb-3">Assignment Model Answers</h5>
										<div class="clearfix"></div>
		              					<div class="table-responsive container-fluid bg-light rounded">
											<%@ include file="../common/messages.jsp" %>
											
											<!-- <div class="table-responsive"> -->
											<table class="table  table-striped mt-3 mb-3 ml-2 mr-2 rounded">
												<thead>
													<tr> 
														<th>Sr. No.</th>
														<th>Subject</th>
														<th>Model Answers</th>
													</tr>
												</thead>
												<tbody>
												
											        <tr>
											            <td>1</td>
														<td>Corporate Finance</td>
														<td><a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/modelAnswers/Corporate_Finance_Model_Answer.pdf" target="_blank"><i class="fa-solid fa-download"></i>Download</a></td>
											        </tr> 
											        
											        <tr>
											            <td>1</td>
														<td>Information Systems for Managers</td>
														<td><a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/modelAnswers/Information_System_for_Managers_Model_Answer.pdf" target="_blank"><i class="fa-solid fa-download"></i>Download</a></td>
											        </tr> 
											        
											        <tr>
											            <td>1</td>
														<td>Marketing Management</td>
														<td><a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/modelAnswers/Marketing_Management_Model_Answer.pdf" target="_blank"><i class="fa-solid fa-download"></i>Download</a></td>
											        </tr> 
											        
											        <tr>
											            <td>1</td>
														<td>Operations Management</td>
														<td><a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/modelAnswers/Operations_Management_Model_Answer.pdf" target="_blank"><i class="fa-solid fa-download"></i>Download</a></td>
											        </tr> 
											        
											        <tr>
											            <td>1</td>
														<td>Organisational Behaviour</td>
														<td><a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/modelAnswers/Organisational_Behaviour_Model_Answer.pdf" target="_blank"><i class="fa-solid fa-download"></i>Download</a></td>
											        </tr>   
													
													
												</tbody>
											</table>
											<!-- </div> -->
										</div>
              								
              						</div>
              				</div>
              		
                            
					</div>
            </div>
        </div>
            
  	
        <jsp:include page="../common/footerNew.jsp"/>
            
		
    </body>
</html>