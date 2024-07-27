<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.StudentExamBean"%>
<html class="no-js"> <!--<![endif]-->
   
   
   
    <jsp:include page="jscss.jsp">
	<jsp:param value="Welcome to Exam Portal" name="title" />
	</jsp:include>
	
    <body class="inside">
	
    <%@ include file="header.jsp"%>
    <%
    Person p = (Person)session.getAttribute("user");
    StudentExamBean student = (StudentExamBean)session.getAttribute("studentExam");
    String programStructure = null;
    if(student != null){
    	programStructure = student.getPrgmStructApplicable();
    }else{
    	programStructure = "";
    }
    
    
    String firstName = "";
	String lastName = "";
	String displayName = "";
	String studentProgram = "";
	String email = "";
	String lastLogon = "";
    
    if(p != null){
    	displayName = p.getDisplayName();
    	studentProgram = p.getProgram();
    	lastLogon = p.getLastLogon();
    }
    %>
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>&nbsp;</legend></div>
          
          <div class="panel-body">
            <div class="col-xs-18">
             
           
             <%@ include file="messages.jsp" %>
			
            </div> <!-- /col-xs-18 -->
          </div> <!-- /row -->
           
		<%--   <div class="row">          
           <%if("Jul2014".equals(programStructure)){ %>
            <div class="col-sm-6">
              <div class="module-box ">
                <a href="takeDemoTest" target="_blank" class="" onclick="return confirm('Once you take Demo Test, you cannot attempt it for next 4 hours. Do you wish to continue to Demo Exam?');">Take a Demo Online Exam</a>
              </div> 
            </div> 
            <%} %>
            
               <!-- <div class="col-sm-6">
              <div class="module-box ">
                <a href="#" class="" onclick="alert('Exam registration closed for December 2014 exam. For any further query, please contact at ngasce.exams@nmims.edu');">Register for Dec-2014 Exam</a>
              </div> 
            </div>   -->
            
           <div class="col-sm-6">
              <div class="module-box ">
                <a href="downloadHallTicket" class="">Download Hall Ticket for Dec-2014 Exams</a>
              </div> 
            </div>    
             
             <div class="col-sm-6">
              <div class="module-box ">
                <a href="studentTimeTable" class="">View Exam Timetable</a>
              </div> 
            </div> 
           
          </div> <!-- /row -->
          
           <%if("Jul2014".equals(programStructure)){ %>
           
          <div class="row">   
          	 <div class="col-sm-6">
              <div class="module-box ">
                <a href="getMostRecentResults" class="">View Dec-2014 Exam Results</a>
              </div> <!--/module-box-->
            </div> <!-- /col-xs-6 -->
          <%}else{ %>  
           <div class="row">   
          	 <div class="col-sm-6">
              <div class="module-box ">
                <a href="/student/viewNotice" class="">View Dec-2014 Exam Results</a>
              </div> <!--/module-box-->
            </div> <!-- /col-xs-6 -->
            
            <%} %>
            <div class="col-sm-6">
              <div class="module-box ">
                <a href="getAStudentMarks" class="">View Marks History</a>
              </div> <!--/module-box-->
            </div> <!-- /col-xs-6 -->
            
            <div class="col-sm-6">
              <div class="module-box ">
                <a href="viewAssignmentsForm" class="">Assignments</a>
              </div> <!--/module-box-->
            </div> <!-- /col-xs-6 -->
            
            <!--  <div class="col-sm-6">
              <div class="module-box ">
                <a href="selectResitSubjectsForm" class="">Resit Exam Registration</a>
              </div> /module-box
            </div> /col-xs-6 -->
          </div> --
          
          
        </div> <!-- /container --%>
    </div>
    </section>
    
    
    <jsp:include page="footer.jsp" />
  </body>
</html>
