<!DOCTYPE html>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<%@page import="com.nmims.beans.StudentExamBean"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>


<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%-- 
<spring:eval expression="@propertyConfigurer.getProperty('SHOW_RESULTS_FROM_REDIS')"
	var="SHOW_RESULTS_FROM_REDIS" />
 --%>	
<%
StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
	String programStructure = student.getPrgmStructApplicable();
	BaseController ssrmCon = new BaseController();
%>


<html lang="en">
    

	
    
    <jsp:include page="common/jscss.jsp">
	<jsp:param value="Data Not Available" name="title"/>
    </jsp:include>
    
    <style>
		    	
		#parentSpinnerDiv {
			background-color: rgba(255,255,255,0.5) !important;
			z-index: 999;
			width: 100%;
			height: 100vh;
			position: fixed;
		}
		
		#childSpinnerDiv {
			position: absolute;
			top: 50%;
			left: 50%;
			transform: translate(-50%, -50%);
		}
		


.loading{
  box-sizing: border-box;
  display: inline-block;
  padding: 0.5em;
  vertical-align: middle;
  text-align: center;
  background-color: transparent;
  border: 5px solid transparent;
  border-top-color: grey;
  border-bottom-color: grey;
  border-radius: 50%;
}

.outer{
  animation: spin 1s infinite;
}

.inner{
  animation: spin 1s infinite;
}

@keyframes spin{
  0% {
    transform: rotateZ(0deg);
  }
  100% {
    transform: rotateZ(360deg);
  }
}

#wrap {
	box-sizing: border-box;
}
		
    </style>
    
    
    <body>
    

    	<%@ include file="common/header.jsp" %>
    	
    	
        
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="common/breadcrum.jsp">
			<jsp:param value="Student Zone;Exams" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="common/left-sidebar.jsp">
								<jsp:param value="Exam Results" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="common/studentInfoBar.jsp" %>
              						
              						
              						<div class="sz-content">
										<!-- Code for page starts -->
              						
	              						<h2 class="red text-capitalize">Data Will Be Available Shortly.</h2>

										<!-- Code for page ends -->
              					</div>
              				</div>
              		
                            
					</div>
            </div>
        </div>
            
  	
        <jsp:include page="common/footer.jsp"/>
            
		
    </body>
    
</html> 	