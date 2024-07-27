<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.Person"%>
<html class="no-js"> <!--<![endif]-->
   
    <jsp:include page="jscss.jsp">
	<jsp:param value="Manage Exam Centers" name="title" />
	</jsp:include>
	
    <body class="inside">
	
    <%@ include file="header.jsp"%>
    <%
    Person p = (Person)session.getAttribute("user");
    
    String firstName = "";
	String lastName = "";
	String displayName = "";
	String email = "";
	String lastLogon = "";
    
    if(p != null){
    	displayName = p.getDisplayName();
    	lastLogon = p.getLastLogon();
    }
    %>
    <section class="content-container login">
        <div class="container-fluid customTheme">
          <%@ include file="messages.jsp" %>
          <div class="row">  <legend>Manage Exam Centers</legend> </div>
           
		  <div class="row">          
           
            <div class="col-sm-6">
              <div class="module-box ">
                <a href="addExamCenterForm" class="">Add Exam Center</a>
              </div> <!--/module-box-->
            </div> <!-- /col-xs-6 -->
            
             <div class="col-sm-6">
              <div class="module-box ">
                <a href="searchExamCenterForm" class="">Search Exam Centers </a>
              </div> 
            </div> 
             
             
            
            
          </div> <!-- /row -->
          
          
        </div> <!-- /container -->
    </section>
    
    
    <jsp:include page="footer.jsp" />
  </body>
</html>
