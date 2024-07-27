<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 
<html class="no-js"> <!--<![endif]-->


	<jsp:include page="jscss.jsp">
	<jsp:param value="NMIMS Global Access School for Continuing Education | Sign in" name="title"/>
	</jsp:include>
        <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


    <body class="home">
	
   <%@ include file="header.jsp" %>
    
    
    <div class="container-fluid customMainWrapper">
        <!-- Example row of columns -->
        <div class="row">
        	<div class="col-lg-9 col-lg-offset-5 col-md-12 col-md-offset-3 col-sm-15 col-sm-offset-2 loginWrapper">
            	<h3>Login</h3>
            	<div class="row">
                	<div class="col-sm-12 padding-small">
                    	<div class="col formWrapper">
                            
                            <form id="login-form" name="login-form" action="authenticate" method="post">
			                  <div class="form-group">
			                    <label for="userId">User ID (Student Number)</label> <a href="resetPasswordForm" class="forgetLink">Forgot Password?</a>
			                    <input type="text" class="form-control" id="userId" name="userId" placeholder="Enter User Id" required />
			                  </div>
			                  <a class="errorTxt error1"></a>
			                  <div class="form-group">
			                    <label for="password">Password</label>&nbsp; 
			                    <input type="password" class="form-control" id="password" name="password" placeholder="Enter Password" required />
			                  </div>
		                  	<a class="errorTxt error2"></a>
			                </form>
                
                
                
                        </div>
                    </div>
                    
                    <div class="col-md-6 col-sm-6 padding-small">
                    	<ul>
                            <li class="tile tile-big tile-7 rotate3d rotate3dX loginBtn">
                              <div class="faces">
                                <div class="front" onclick="document.forms['login-form'].submit();"><p>SIGN IN</p></div>
                                <div class="back" onclick="document.forms['login-form'].submit();"><p>SIGN IN</p></div>
                              </div>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    
        
    </div>

	<jsp:include page="footer.jsp"/>
	<script>
    $(document).ready(function(e) {
        /////validation method////
        $('.loginBtn').click(function(){
            validate=true;
            if($('#userId').val() == ""){
                $('.loginWrapper a.error1').html('Please enter User ID.');
                validate=false;                
            }else{
                $('.loginWrapper a.error1').html('');
            }
            
            if($('#password').val() == ""){
                $('.loginWrapper a.error2').html('Please enter Password.');
                validate=false;
            }else{
                $('.loginWrapper a.error2').html('');
            }
            
            if(validate==true){
                //////Submit Method here
            	document.forms['login-form'].submit();
            }else{
                return false;
            }
        });
        
        //////Enter Method for submitting form//////
        $(document).keypress(function (e) {
        	
            var key = e.which;
            if(key == 13){
                $('.loginBtn').click();
                return false;
            }
        });
    });
    </script>	
   
    
  </body>
</html>
