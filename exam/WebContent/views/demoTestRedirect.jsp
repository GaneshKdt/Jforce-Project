<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->
        <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html class="no-js"> <!--<![endif]-->
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
        <title>Redirecting to Demo Exam Application</title>
        
    </head>
    <body >
	
    
 
             <h2>Redirecting to Demo Exam Application. Please wait...</h2> 
			 <img alt="Redirecting" src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources/images/redirecting4.gif" align="middle" class="img-responsive displayed">
			 
			<form  id='loginfrm' name='loginfrm' method="post" autocomplete="off" action="http://thepracticetest.in/NMIMS/logincandidate.php">

				<input type="hidden" name="from_page" value="" />
				<input type="hidden" name="hid_lefttime" value="">
				<input type='hidden' name='ta_override' value="">

				<input type="hidden" name="memno" class="textbox" value="${userId}"  maxlength="20" />
				<input type="hidden" name="candpassword"  maxlength="50" value="password"  />


			</form>

    
    <script>
		document.getElementById('loginfrm').submit();
	</script>
    

  </body>
</html>
