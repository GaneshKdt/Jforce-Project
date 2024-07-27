<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.Person"%>
<html class="no-js"> <!--<![endif]-->
   
    <jsp:include page="jscss.jsp">
	<jsp:param value="Welcome to Exam Data Management" name="title" />
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
          <div class="row">
          <legend>Welcome <%=displayName %></legend>
            <div class="col-xs-18">
             
           
             <%@ include file="messages.jsp" %>
			
             <div class="student-details">
               <div class="row">          
                 <div class="col-xs-9 col-sm-6 col-md-6"><span>User ID:</span> <%=userId %></div>
                 <div class="col-xs-9 col-sm-6 col-md-6"><span>Last Login:</span> <%=lastLogon %></div>
               </div>  
             </div>
            </div> <!-- /col-xs-18 -->
          </div> <!-- /row -->
           
		 <div class="jumbotron">
		  <h1  style="text-align:left">Hello, <%=displayName %></h1>
		  <p> Welcome to Exam Portal</p>
		 </div>
        </div> <!-- /container -->
    </section>
    
    
    <jsp:include page="footer.jsp" />
  </body>
</html>
