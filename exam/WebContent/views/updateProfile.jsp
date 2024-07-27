<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.Person"%>
<html class="no-js"> <!--<![endif]-->
	
	<jsp:include page="jscss.jsp">
		<jsp:param value="Update Profile" name="title" />
	</jsp:include>
	
	<body class="inside">
	
   <%@ include file="header.jsp"%>
    
    <%
    Person p = (Person)session.getAttribute("user");

	String email = "";
	String mobile = "";
	String altMobile = "";
	String postalAddress = "";
    
    if(p != null){
    	email = p.getEmail();
    	mobile = p.getContactNo();
    	postalAddress = p.getPostalAddress();
    	altMobile = p.getAltContactNo();
    }
    %>
    
    <section class="content-container login">
        <div class="container-fluid customTheme">
          <div class="row">
             <legend>Update Profile</legend>
          </div> <!-- /row -->
           
          <%@ include file="messages.jsp" %>
		  
		  <div class="row">          
            <div class="col-xs-18 panel-body">
	          
              <div class="">
              
                <form role="form" id="update-profile" action="saveProfile" method="post">
                <div class="row">          
	              <div class="col-sm-9">
                    
                     <div class="form-group">
                      <label for="email">Enter Email(*)</label>
                      <input type="text" class="form-control" id="email" name="email" placeholder="Enter Email" value="<%=email%>">
                    </div>
                    
                     <div class="form-group">
                      <label for="mobile">Mobile No.(*)</label>
                      <input type="text" class="form-control" id="mobile" name="mobile" placeholder="Enter Mobile No." value="<%=mobile%>">
                    </div>

					<div class="form-group">
                      <label for="altMobile">Alternate Contact No.</label>
                      <input type="text" class="form-control" id="altMobile" name="altMobile" placeholder="Enter Alternate Contact No." value="<%=altMobile%>">
                    </div>
                     <div class="form-group">
                      <label for="address">Postal Address(*)</label>
                      <textarea rows="4" cols="5" class="form-control" id="address" name="address" placeholder="Enter Address" ><%=postalAddress%></textarea>
                      
                    </div>
                    
                     <div class="form-group">
                      <button type="submit" class="btn btn-danger">Update Information</button>
                    </div>
                    
                  </div>
	              <div class="col-sm-9 ">
                    
                  </div>
                </div>  
                </form>
                
              </div> <!--/module-box-->
            </div> <!-- /col-xs-6 -->
          </div> <!-- /row -->
          
        </div> <!-- /container -->
    </section>
    
    <jsp:include page="footer.jsp" />
    
	
    
	<script>
		$(function() {
		  $( "#update-profile" ).validate({
			rules: {
			 
			email: {
				required: true,
				email: true},
			mobile: {
				required: true,
				digits: true},
			altMobile: {
				digits: true},
			address: {
				required: true}
		  }
			  
		  });
		});
	</script>
    
    
  </body>
</html>
