<%@ page language="java" contentType="text/html; charset=ISO-8859-1"    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html lang="en">
    
    <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
	<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')" var="server_path" />
	
    
    <jsp:include page="../common/jscss.jsp">
	<jsp:param value="Welcome to Student Zone" name="title"/>
	</jsp:include>
    
    
    
    <body>
    
    	<%@ include file="../common/header.jsp" %>
    	
    	
        
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="../common/breadcrum.jsp">
			<jsp:param value="Student Zone;Exam" name="breadcrumItems"/>
			</jsp:include>
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				
              				<jsp:include page="../common/left-sidebar.jsp">
								<jsp:param value="Exam" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper dashBoard myCoursesPage">
              						<%@ include file="../common/studentInfoBar.jsp" %>
              						
              						
              						<div class="sz-content">
											
											<div class="panel-group has-no-result" id="accordion" role="tablist" aria-multiselectable="true">
															<%try{ %>
															
															<%@ include file="singleStudentPassFailMarks.jsp" %>
															
															<%@ include file="studentTimetable.jsp" %>
															<%@include file="studentMarksHistory.jsp" %>
															
															<%}catch(Exception e){
																}%>
													
													
													
													 
											</div>
											<div class="clearfix"></div>
              						</div>
              				</div>
              		
                            
					</div>
            </div>
        </div>
            
  	
            <jsp:include page="../common/footer.jsp"/>
            
			<script>
			
			$(document).on('ready', function() {
			
				$('.load-more-table a').on('click', function() {
					$("#examHomePassFailTable").addClass('showAllEntries');
					$(this).hide();
				});
				
				$('.load-more-table a').on('click', function() {
					$("#examHomePageTimetable").addClass('showAllEntries');
					$(this).hide();
				});
				
				
				
			
			});
			</script>
			

		
    </body>
</html>