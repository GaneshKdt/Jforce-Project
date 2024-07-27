<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.itextpdf.text.log.SysoCounter"%>
<%@page import="com.nmims.helpers.PersonStudentPortalBean"%>
<html class="no-js">
<!--<![endif]-->

<jsp:include page="jscss.jsp">
	<jsp:param value="Change Password" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<%
	PersonStudentPortalBean p = (PersonStudentPortalBean)session.getAttribute("user_studentportal");
    System.out.println("name in jsp :::::::::::::: "+p.getUserId());
     if(p != null){
    	 //roles = p.getRoles();
    	 name = p.getUserId();
    }  
    System.out.println("Roles in jsp :::::::::::::: "+roles);
    %>

	<section class="content-container login">
		<div class="container-fluid customTheme">

			<div class="row">
				<legend>Change User's Password</legend>
			</div>

			<%@ include file="messages.jsp"%>

			<div class="row">
				<div class="col-xs-18">
					<div class="panel-body">
						<h4>Password Policy</h4>
						<p>Please adhere to the below password policy while you set
							your new password</p>
						<ul class="policy">
							<li><i class="fa fa-arrow-circle-right"></i> Passwords
								cannot contain the user's account name or parts of the user's
								full name that exceed two consecutive characters.</li>
							<li><i class="fa fa-arrow-circle-right"></i> Passwords must
								be at least eight characters in length.</li>
							<li><i class="fa fa-arrow-circle-right"></i> Passwords must
								contain characters from at least three of the following four
								categories:</li>
							<li><i class="fa fa-arrow-circle-right"></i> English
								uppercase alphabet characters (A-Z)</li>
							<li><i class="fa fa-arrow-circle-right"></i> English
								lowercase alphabet characters (a-z)</li>
							<li><i class="fa fa-arrow-circle-right"></i> Numbers (0-9)</li>
							<li><i class="fa fa-arrow-circle-right"></i>
								Non-alphanumeric characters (for example, !$#%)</li>
						</ul>
					</div>
					<div class="panel-body content">

						<form role="form" id="change-password" action="saveUserPassword"
							method="post">

							<div class="row">
								<%if(roles.indexOf("Portal Admin") != -1 ){ %>
								<div class="col-sm-9">
									<div class="form-group">
										<label for="userId">Enter User ID</label> <input type="text"
											class="form-control" id="userId" name="userId"
											placeholder="Enter User ID">
									</div>

									<div class="form-group">
										<label for="password">Enter New Password</label> <input
											type="password" class="form-control" id="password"
											name="password" placeholder="Enter Password">
									</div>
									<div class="form-group">
										<label for="confirm_password">Confirm Password</label> <input
											type="password" class="form-control" id="confirm_password"
											name="confirm_password" placeholder="Confirm Password">
									</div>
								</div>
								<div class="col-sm-9 bottom-pass-button">
									<button type="submit" class="btn btn-danger">Change
										Password</button>
								</div>
								<%} %>
								<%if(roles.indexOf("Information Center") != -1 ){ %>
								<input type="hidden" id="userId" name="userId" value="<%=name%>" />

								<div class="col-sm-9">
									<div class="form-group">
										<label for="password">Enter New Password</label> <input
											type="password" class="form-control" id="password"
											name="password" placeholder="Enter Password">
									</div>
									<div class="form-group">
										<label for="confirm_password">Confirm Password</label> <input
											type="password" class="form-control" id="confirm_password"
											name="confirm_password" placeholder="Confirm Password">
									</div>
								</div>
								<div class="col-sm-9 bottom-pass-button">
									<button type="submit" class="btn btn-danger">Change
										Password</button>
								</div>
								<%} %>

							</div>
						</form>

					</div>
					<!--/module-box-->
				</div>
				<!-- /col-xs-6 -->
			</div>
			<!-- /row -->
			<%-- 
          
          <div class="row">          
            <div class="col-xs-18">

              <div class="panel-body content">
              
                <form role="form" id="change-roles" action="saveUserRoles" method="post">
                <div class="row">          
	              <div class="col-sm-9">
	              
	               <div class="form-group">
                      <label for="userId">Enter User ID</label>
                      <input type="text" class="form-control" id="userId" name="userId" placeholder="Enter User ID">
                    </div>
                    <div class="form-group">
                      <label for="password">Enter New Roles</label>
                      <input type="text" class="form-control" id="roles" name="roles" placeholder="Enter Roles" value="<%=roles%>">
                    </div>
					Please do not delete old roles, but append new ones with comma separated. 
					(Available Roles: Portal Admin, Exam Admin, TEE Admin, Marksheet Admin, Assignment Admin, Read Admin)
                  </div>
	              <div class="col-sm-9 bottom-pass-button">
                    <button type="submit" class="btn btn-danger">Change Roles</button>
                  </div>
                </div>  
                </form>
                
              </div> <!--/module-box-->
            </div> <!-- /col-xs-6 -->
          </div> <!-- /row --> --%>

		</div>
		<!-- /container -->
	</section>
	<br>
	<jsp:include page="footer.jsp" />



	<script>
		$(function() {
		  $( "#change-password" ).validate({
			rules: {
			  password: {
					required: true,
					minlength: 8},
			  confirm_password: {
					equalTo: "#password"
			},
			userId:{
				required: true
			}
		  }
			  
		  });
		});
	</script>


</body>
</html>
