<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<html class="no-js"> <!--<![endif]-->
	
	<jsp:include page="jscss.jsp">
		<jsp:param value="Exam Timetable Notice for Dec-2014 Exam" name="title" />
	</jsp:include>
	
	<body class="inside">
	
   <%@ include file="header.jsp"%>
    
    <section class="content-container login">
        <div class="container-fluid customTheme">
          <div class="row">
             <legend>Exam Timetable Notice for Dec-2014 Exam</legend>
          </div> <!-- /row -->
           

           
           <div class="">          
            <div class="col-xs-18 panel-body">
	          <div class="bullets">
                <p>Dear Student,</p>
                <ol class="policy">
                <li><p align="justify">Displayed Timetable is applicable for <b>students enrolled in July 2014 in Diploma / Post Graduate Program in Sem I.</b>
				<br/> <font size="4.5">*</font> Mode of Examination: Online<br>
				<font size="4.5">*</font> Applicable Program Structure: Jul2014</p>
				</li>
				
                <li><p align="justify">Displayed Timetable is applicable for <b> students who have enrolled from July 2009 to January, 2014 in Certificate / Diploma / Post Graduate Program</b>
					<br/> <font size="4.5">*</font> Mode of Examination: Written (Offline)<br>
				<font size="4.5">*</font> Applicable Program Structure: Jul2009</p></li>
				
				<li><p align="justify">Displayed Timetable is applicable for<b> students who have enrolled in Certificate Program in July, 2014.</b>
				<br/> <font size="4.5">*</font> Mode of Examination: Written (Offline)<br>
				<font size="4.5">*</font> Applicable Program Structure: Jul2013</p></li>
				
                <li><p align="justify">Displayed Timetable is applicable for <b>Max Life Insurance students </b>who have enrolled in Post Graduate Diploma in Marketing Management
					<br/> <font size="4.5">*</font> Mode of Examination: Written (Offline)<br>
				<font size="4.5">*</font> Applicable Program Structure: Jul2009</p></li>
                
               

				<hr>
				<p align="justify"><b><u>Examination Registration For December, 2014:</u> 
				<br/>
				<font size="4.5">*</font> EXAMINATION REGISTRATION DATES & REGISTRATION PROCESS WILL BE ONLINE. 
				Detailed process will be announced by End of October, 2014.  
 </b>
				</p>
				
                <br/>
                <a href="#" class="customBtn cancleBtn" onClick="window.open('resources_2015/conduct.pdf')">STUDENT CODE OF CONDUCT IN EXAMINATION HALL (Offline Exam)</a>
                <a href="timeTable" class="customBtn red-btn">View Exam Timetable</a>
                
                </ol>
	           </div>
	           </div>
           </div>
                
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
