<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 
<html class="no-js"> <!--<![endif]-->


	<jsp:include page="jscss.jsp">
	<jsp:param value="NMIMS Global Access School for Continuing Education | Sign in" name="title"/>
	</jsp:include>


    <body class="home">
	
   <%@ include file="header.jsp" %>
    
    <%
    String timeoutStr = request.getParameter("timeoutMins");
    int timeOut = Integer.parseInt(timeoutStr) * 60;
    session.setMaxInactiveInterval(timeOut);
    out.println("Session changed to "+timeoutStr+" mins");
    
    %>
    
    
    <section class="content-container login">
        <div class="container-fluid customTheme">
          <div class="row">
            <div class="col-xs-18">
             
            </div> <!-- /col-xs-18 -->
          
            <div class="col-sm-5">          
            
          </div> <!-- /col-sm-5 -->
            <div class="col-sm-8">
              
              <div class="login-box">
               
                <h2>Login</h2>
                
                <%@ include file="messages.jsp" %>
				  
                <form role="form" id="login-form" action="authenticate" method="post">
                  <div class="form-group">
                    <label for="userId">User ID (SAP ID)</label>
                    <input type="text" class="form-control" id="userId" name="userId" placeholder="Enter User Id" required />
                  </div>
                  <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" class="form-control" id="password" name="password" placeholder="Enter Password" required />
                  </div>
                  <button type="submit" class="btn btn-default">Sign In</button>
                </form>
                
              
          
              </div> <!-- /login-box -->
                
            </div> <!-- /col-sm-8 -->
            <div class="col-sm-5">&nbsp;</div> <!-- /col-sm-5 -->
          </div> <!-- /row -->
          

        </div> <!-- /container -->
    </section>
    
	<jsp:include page="footer.jsp"/>
	<script>
		$(function() {
		  $("#login-form").validate({
			rules: {
			userId: {
				required: true				
			},
			password: {
				required: true
			  }
			}
		  });
		});
	</script>
   
    
  </body>
</html>
