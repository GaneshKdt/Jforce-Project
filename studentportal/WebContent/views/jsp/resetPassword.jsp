<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<html class="no-js"> <!--<![endif]-->
	
	<jsp:include page="jscss.jsp">
		<jsp:param value="Reset Password" name="title" />
	</jsp:include>
	
	<body >
	
   <%@ include file="header.jsp"%>
    
    <section class="content-container login">
        <div class="container-fluid customTheme">
        
          <%@ include file="messages.jsp" %>
		  <div class="row"><legend>Get Password Back</legend></div>
		  <div class="">          
            <div class="col-xs-18 panel-body">
              
                
                <div class="row">      
                <form  id="change-password" action="resetPassword" method="post">    
	              <div class="col-sm-6">
                    <div class="form-group">
                      <input type="text" class="form-control" id="userId" name="userId" placeholder="Enter Student Number" required="required">
                    </div>

                  </div>
	              <div class="col-sm-6">
                    <button type="submit" class="btn btn-primary">Get My Password</button>
                    <a href="login" class="btn btn-large btn-danger">Login Again</a>
                  </div>
                </div>  
                </form>

            </div> <!-- /col-xs-6 -->
          </div> <!-- /row -->
          
        </div> <!-- /container -->
    </section>
    
    <jsp:include page="footer.jsp" />
    
	
    
	    
  </body>
</html>
 --%>

<!DOCTYPE html>
<html lang="en">
<jsp:include page="common/jscss.jsp">
	<jsp:param value="Welcome to Student Zone" name="title" />
</jsp:include>

<style>
.complete-profile-warpper .sz-content-wrapper.withBgImage .student-info-bar .student-image
	{
	border: 2px solid #000;
}

.complete-profile-warpper .sz-content-wrapper.withBgImage .student-info-bar ul.student-info-list li
	{
	color: #333;
}
</style>

<body>

	<%@ include file="common/header.jsp"%>

	<div class="sz-main-content-wrapper complete-profile-warpper">
		<jsp:include page="common/breadcrum.jsp">
			<jsp:param value="Student Zone;Forgot Password" name="breadcrumItems" />
		</jsp:include>

		<div class="sz-content-wrapper dashBoard withBgImage">
			<div class="sz-main-content ">
				<div class="container">


					<h2 class="text-capitalize red" style="margin-top: 50px;">Forgot
						Password</h2>
					<div class="clearfix"></div>
					<div class="panel-content-wrapper">


						<%@ include file="common/messages.jsp"%>

						<form id="change-password" action="resetPassword" method="post">
							<div class="row">
								<div class="col-sm-6">
									<div class="form-group">
										<input type="text" class="form-control" id="userId"
											name="userId" placeholder="Enter Student Number"
											required="required">
									</div>

								</div>
								<div class="col-sm-6">
									<button type="submit" class="btn btn-primary">Get My
										Password</button>
									<a href="login" class="btn btn-large btn-danger">Login
										Again</a>
								</div>
							</div>
						</form>


					</div>
				</div>
			</div>
		</div>
	</div>

	<jsp:include page="common/footer.jsp" />

</body>
</html>