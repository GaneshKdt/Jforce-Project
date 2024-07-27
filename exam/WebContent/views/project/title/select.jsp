<!DOCTYPE html>
<%@page import="java.util.Arrays"%>
<%@page import="com.nmims.beans.StudentExamBean"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%try{ %>
<%
	String sapId = (String)session.getAttribute("userId");
	StudentExamBean student = (StudentExamBean)session.getAttribute("studentExam");
	
	ArrayList<String> studentlistForBlocking =new ArrayList<String>();
	String studentName = "";
	if(student != null){
		studentName = student.getFirstName() + " " + student.getLastName();
	}
	ArrayList<String> timeExtendedStudentIdSubjectList = (ArrayList<String>)session.getAttribute("timeExtendedStudentIdSubjectList");
	String subject = (String)session.getAttribute("subject");
	
%>

<html lang="en">
    <jsp:include page="../../common/jscss.jsp">
	<jsp:param value="Submit Project" name="title"/>
    </jsp:include>
    
    	<%@ include file="../../common/header.jsp" %>
        
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="../../common/breadcrum.jsp">
			<jsp:param value="Student Zone;Exams;Project Title Selection" name="breadcrumItems"/>
			</jsp:include>
        	
            <div class="sz-main-content menu-closed">
				<div class="sz-main-content-inner">
					<jsp:include page="../../common/left-sidebar.jsp">
						<jsp:param value="Project Submission" name="activeMenu"/>
					</jsp:include>
	            				
					<div class="sz-content-wrapper examsPage">
						<%@ include file="../../common/studentInfoBar.jsp" %>
						<div class="sz-content">
            				<%@ include file="../../common/messages.jsp" %>
							<div class="clearfix"></div>
							<h2 class="red text-capitalize">Select Project Title</h2>
							<div class="clearfix"></div>
							<div class="panel-content-wrapper">
								<form:form  action="selectProjectTitle" method="post" modelAttribute="projectTitle" >
									<form:hidden path="examYear"/>
									<form:hidden path="examMonth"/>
									<form:hidden path="consumerProgramStructureId"/>
									
									<form:select path="titleId" class="form-control" required="required">
										<form:option value="">Select Project Title</form:option>
										<form:options items="${titleList}" itemLabel="title" itemValue="id"/>
									</form:select>
									<button id="btn" name="btn" class="btn btn-primary" formaction="selectProjectTitle">Confirm Selection</button>
								</form:form>
							</div>
						</div>	
					</div>
				</div>
            </div>
        </div>
            
  	
        <jsp:include page="../../common/footer.jsp"/>
            
		
    </body>
</html>

<%}catch(Exception e){

}
%> 