<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.PersonAcads"%>
<%@page import="com.nmims.beans.StudentAcadsBean"%>
<html class="no-js"> <!--<![endif]-->
   
   
   
    <jsp:include page="jscss.jsp">
	<jsp:param value="Welcome to Academics Portal" name="title" />
	</jsp:include>
	
    <body class="inside">
	
    <%@ include file="header.jsp"%>
    <%
    PersonAcads p = (PersonAcads)session.getAttribute("user_acads");
    
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
          <div class="row">
            <div class="col-xs-18">
             <h2>Welcome <%=displayName %></h2>
           
             <%@ include file="messages.jsp" %>
			
             <div class="student-details">
               <div class="row">          
                 <div class="col-xs-9 col-sm-6 col-md-6"><span>Student ID:</span> <%=userId %></div>
                 <div class="col-xs-9 col-sm-6 col-md-6"><span>Last Login:</span> <%=lastLogon %></div>
               </div>  
             </div>
            </div> <!-- /col-xs-18 -->
          </div> <!-- /row -->
         
          
         
           
          <div class="row">   
          	 <div class="col-sm-6">
              <div class="module-box ">
                <a href="viewTimeTable" class="">Academic Calendar</a>
              </div> <!--/module-box-->
            </div> <!-- /col-xs-6 -->
            
            <div class="col-sm-6">
              <div class="module-box ">
                <a href="/acads/student/viewApplicableSubjectsForm" class="">Learning Resources</a>
              </div> 
            </div> 
         

           </div>
        </div> <!-- /container -->
    </section>
    
    
    <jsp:include page="footer.jsp" />
  </body>
</html>
