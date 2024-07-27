<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<html class="no-js"> <!--<![endif]-->
	
	<jsp:include page="jscss.jsp">
		<jsp:param value="Change Password" name="title" />
	</jsp:include>
	
	<body class="inside">
	
   <%@ include file="header.jsp"%>
    
    <section class="content-container login">
        <div class="container-fluid customTheme">
          <div class="row">
             <legend>Change Password</legend>
          </div> <!-- /row -->
           
          <%@ include file="messages.jsp" %>
		  
		  <div class="">          
            <div class="col-xs-18 panel-body">
	          <div class="bullets">
                <h4>Password Policy</h4>
                <p>Please adhere to the below password policy while you set your new password</p>
                <ul class="policy">
                  <li><i class="fa fa-arrow-circle-right"></i> Passwords cannot contain the user's account name or parts of the user's full name that exceed two consecutive characters.</li>
                  <li><i class="fa fa-arrow-circle-right"></i> Passwords must be at least eight characters in length.</li>
                  <li><i class="fa fa-arrow-circle-right"></i> Passwords must contain characters from at least three of the following four categories: </li>
                  <li><i class="fa fa-arrow-circle-right"></i> English uppercase alphabet characters (A-Z)</li>
				  <li><i class="fa fa-arrow-circle-right"></i> English lowercase alphabet characters (a-z)</li>
				  <li><i class="fa fa-arrow-circle-right"></i> Numbers (0-9)</li>
				  <li><i class="fa fa-arrow-circle-right"></i> Non-alphanumeric characters (for example, !$#%)</li>
                </ul>
              </div>
			  <br>
              <div class="panel-body">
              
                <form role="form" id="change-password" action="savePassword" method="post">
                <div class="row">          
	              <div class="col-sm-9">
                    <div class="form-group">
                      <label for="password">Enter New Password</label>
                      <input type="password" class="form-control" id="password" name="password" placeholder="Enter Password">
                    </div>
                    <div class="form-group">
                      <label for="confirm_password">Confirm Password</label>
                      <input type="password" class="form-control" id="confirm_password" name="confirm_password" placeholder="Confirm Password">
                    </div>
                  </div>
	              <div class="col-sm-9 bottom-pass-button">
                    <button type="submit" class="btn btn-primary">Change Password</button>
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
		  $( "#change-password" ).validate({
			rules: {
			  password: {
					required: true,
					minlength: 8},
			  confirm_password: {
					equalTo: "#password"
			}
		  }
			  
		  });
		});
	</script>
    
    
  </body>
</html>
 --%>

<!DOCTYPE html>

<html lang="en">




<jsp:include page="common/jscss.jsp">
	<jsp:param value="Change Password" name="title" />
</jsp:include>



<body>

	<%@ include file="common/header.jsp"%>



	<div class="sz-main-content-wrapper">

		<jsp:include page="common/breadcrum.jsp">
			<jsp:param value="Student Zone;Change Password" name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="common/left-sidebar.jsp">
					<jsp:param value="Change Password" name="activeMenu" />
				</jsp:include>


				<div class="sz-content-wrapper examsPage">
					<%@ include file="common/studentInfoBar.jsp"%>


					<div class="sz-content">

						<h2 class="red text-capitalize">Change Password</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper">
							<%@ include file="common/messages.jsp"%>

							<p>Please adhere to the below password policy while you set
								your new password</p>
							<ul>
								<li><p>Passwords cannot contain the user's account name
										or parts of the user's full name that exceed two consecutive
										characters.</p></li>
								<li><p>Passwords must be at least eight characters in
										length.</p></li>
								<li><p>Passwords must contain characters from at least
										three of the following four categories:</p></li>

								<ul>
									<li><p>English uppercase alphabet characters (A-Z)</p></li>
									<li><p>English lowercase alphabet characters (a-z)</p></li>
									<li><p>Numbers (0-9)</p></li>
									<li><p>Non-alphanumeric characters (for example, !$#%)</p></li>
								</ul>

							</ul>

							<form role="form" id="change-password" action="savePassword"
								method="post">
								<div class="row">
									<div class="col-md-6">
										<div class="form-group">
											<label for="password">Enter New Password</label> <input
												type="password" class="form-control" id="password"
												name="password" placeholder="Enter Password"
												required="required">
										</div>
										<div class="form-group">
											<label for="confirm_password">Confirm Password</label> <input
												type="password" class="form-control" id="confirm_password"
												name="confirm_password" placeholder="Confirm Password"
												required="required">
										</div>
									</div>
									<div class="col-sm-9 bottom-pass-button">
										<button type="submit" class="btn btn-primary">Change
											Password</button>
									</div>
								</div>
							</form>
						</div>

					</div>
				</div>


			</div>
		</div>
	</div>


	<jsp:include page="common/footer.jsp" />
	<!-- <script>
		$(function() {
		  $( "#change-password" ).validate({
			rules: {
			  password: {
					required: true,
					minlength: 8},
			  confirm_password: {
					equalTo: "#password"
			}
		  }
			  
		  });
		});
	</script>  -->

</body>
</html>