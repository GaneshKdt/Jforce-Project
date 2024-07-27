<!DOCTYPE html>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<style>
 .student-info-bar,.sz-breadcrumb-wrapper{
		z-index:4 !important;
	}
</style>
<html lang="en">
    
    <jsp:include page="common/jscssNew.jsp">
	<jsp:param value="Generate Transcript" name="title"/>
    </jsp:include>
    
    <body>
    
    	<%@ include file="common/headerDemo.jsp" %>
    	
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="common/breadcrum.jsp">
				<jsp:param value="Student Zone;Exams;Transcript" name="breadcrumItems"/>
			</jsp:include>
        	
            
           	<div class="sz-main-content menu-closed rounded">
				<div class="sz-main-content-inner">
				        <div id="sticky-sidebar">  
		       				<jsp:include page="common/left-sidebar.jsp">
								<jsp:param value="Transcript" name="activeMenu"/>
							</jsp:include>	
						</div>
       				<div class="sz-content-wrapper examsPage">
   						<%@ include file="common/studentInfoBar.jsp" %>
						<div class="sz-content">
							<h2 class="text-danger text-capitalize fs-5 fw-bold mt-3 mb-3">Generate Transcript</h2>
							<div class="clearfix"></div>
							<div class="panel-content-wrapper">
								<%@ include file="common/messagesNew.jsp" %>
								<%if("true".equals((String)request.getAttribute("showGenerateButton"))){ %>
									<a class="btn btn-large btn-danger fw-bold fs-6" href="generateStudentSelfTranscript">Generate Transcript</a>
								<%} %>
								<%if("true".equals((String)request.getAttribute("success"))){ %>
									<a class="btn btn-large btn-danger fw-bold fs-6" href="${pageContext.request.contextPath}/downloadTrascriptSheet">Download Transcript</a>
								<%} %>	
							</div>
								<%@include file="../views/examHome/studentMarksHistory.jsp" %>
								
								<div class="inline-block ml-1 mt-2">
									<p> The above result is provisional in nature and is just for student's information. The gradesheet/marksheet/transcript issued by the University will be the authentic document.</p>
									</div>
						</div>
					</div>
				</div> 
			</div>
		</div>
          		
		  		
		  		
        <jsp:include page="common/footerNew.jsp"/>
            
		
    </body>
</html>