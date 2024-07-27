<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.PersonAcads"%>
<html class="no-js"> <!--<![endif]-->
   
    <jsp:include page="jscss.jsp">
	<jsp:param value="Reports" name="title" />
	</jsp:include>
	
    <body class="inside">
	
    <%@ include file="header.jsp"%>
    <%
    PersonAcads p = (PersonAcads)session.getAttribute("user");
    
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
           
            <div class="col-sm-6">
              <div class="module-box ">
                <a href="/acads/admin/pcpRegistrationReportForm" class="">PCP Registration Report</a>
              </div> 
            </div> 
            
            <!--  <div class="col-sm-6">
              <div class="module-box ">
                <a href="graceToCompleteProgramReportForm" class="">Student needing < 10 Grace </a>
              </div> 
            </div> 
             
             <div class="col-sm-6">
              <div class="module-box ">
                <a href="/exam/admin/bookingSummaryReport" class="">Bookings Number</a>
              </div> 
            </div>  -->
            
            
          </div> 
          
          
          
          
          
        </div> <!-- /container -->
    </section>
    
    
    <jsp:include page="footer.jsp" />
  </body>
</html>
