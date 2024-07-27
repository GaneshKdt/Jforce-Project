<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.Person"%>
<html class="no-js"> <!--<![endif]-->
	
	<jsp:include page="jscss.jsp">
		<jsp:param value="Change Password & Update Profile" name="title" />
	</jsp:include>
	
	<body class="inside">
	
   <%@ include file="limitedAccessHeader.jsp"%>
    
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
            <div class="col-xs-18">
             <h2>Change Password & Update Profile</h2>
            </div> <!-- /col-xs-18 -->
          </div> <!-- /row -->
           
          <%@ include file="messages.jsp" %>
		  
		  <div class="row">          
            <div class="col-xs-18">
	          <div class="module-box">
                <h4>Password Policy</h4>
                <p>Please adhere to the below password policy while you set your new password</p>
                <ul class="policy">
                  <li><i class="fa-solid fa-circle-arrow-right"></i> Passwords cannot contain the user's account name or parts of the user's full name that exceed two consecutive characters.</li>
                  <li><i class="fa-solid fa-circle-arrow-right"></i> Passwords must be at least eight characters in length.</li>
                  <li><i class="fa-solid fa-circle-arrow-right"></i> Passwords must contain characters from at least three of the following four categories: </li>
                  <li><i class="fa-solid fa-circle-arrow-right"></i> English uppercase alphabet characters (A-Z)</li>
				  <li><i class="fa-solid fa-circle-arrow-right"></i> English lowercase alphabet characters (a-z)</li>
				  <li><i class="fa-solid fa-circle-arrow-right"></i> Numbers (0-9)</li>
				  <li><i class="fa-solid fa-circle-arrow-right"></i> Non-alphanumeric characters (for example, !$#%)</li>
                </ul>
              </div>
              <div class="module-box content">
              
                <form role="form" id="updateFirstTimeForm" action="saveFirstTimeProfile" method="post">
                <div class="row">          
	              <div class="col-sm-9">
                    <div class="form-group">
                      <label for="password">Enter New Password(*)</label>
                      <input type="password" class="form-control" id="password" name="password" placeholder="Enter Password">
                    </div>
                    <div class="form-group">
                      <label for="confirm_password">Confirm Password(*)</label>
                      <input type="password" class="form-control" id="confirm_password" name="confirm_password" placeholder="Confirm Password">
                    </div>
                    
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
		  $( "#updateFirstTimeForm" ).validate({
			rules: {
			  password: {
					required: true,
					minlength: 8},
			  confirm_password: {
					equalTo: "#password"
			},
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