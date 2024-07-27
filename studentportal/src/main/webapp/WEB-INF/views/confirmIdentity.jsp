<%-- <%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<html class="no-js"> <!--<![endif]-->
	
	<jsp:include page="jscss.jsp">
	<jsp:param	value="Confirm Identity"	name="title" />
	</jsp:include>
   
    <body class="inside">
	
     <%@ include file="limitedAccessHeader.jsp"%>
    
    <section class="content-container login">
        <div class="container-fluid customTheme">
          <div class="row">
             <h2>Confirm Identity and Change Default Password</h2>
          </div> <!-- /row -->
           
		  <div class="">          
            <div class="col-xs-18 panel-body">
              <div class="bullets">
                <p>Welcome ${displayName}</p>
                <p>If you are not ${displayName}, please login again with correct credentials else proceed to change your default password as one time mandatory activity</p>
                
                <div class="row">          
	              <div class="col-md-3"><a href="logout" class="">Login Again</a></div>
	              <div class="col-md-3"><a href="updateFirstTimeProfile" class="">Change Password</a></div>
                </div>  
                
              </div> <!--/module-box-->
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
	<jsp:param value="Confirm Identity" name="title" />
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
			<jsp:param value="Student Zone;Confirm Identity"
				name="breadcrumItems" />
		</jsp:include>

		<div class="sz-content-wrapper dashBoard withBgImage">
			<div class="sz-main-content ">
				<div class="container" style="">

					<div class="sz-content-wrapper dashBoard withBgImage">
						<%@ include file="common/studentInfoBar.jsp"%>

						<h2 class="text-capitalize red" style="margin-top: -20px;">Confirm
							Identity</h2>
						<div class="clearfix"></div>


						<div class="panel-content-wrapper">
							<%@ include file="common/messages.jsp"%>

							<p>Welcome ${displayName}</p>
							<p>If you are not ${displayName}, please login again with
								correct credentials else proceed to change your default password
								as one time mandatory activity</p>

							<a href="logout" class="">Login Again</a> | <a
								href="updateFirstTimeProfile" class="">Change Password</a>


						</div>
					</div>

				</div>
			</div>
		</div>
	</div>

	<jsp:include page="common/footer.jsp" />

</body>
</html>