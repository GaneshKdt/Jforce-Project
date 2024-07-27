<!DOCTYPE html>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html lang="en">
        <link href="advanced-progress-tracker.css" rel="stylesheet">
        <script src="//code.jquery.com/jquery-3.1.0.min.js"></script>
        <script src="advanced-progress-tracker.js"></script>
    <!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">

<!-- jQuery library -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>

<!-- Latest compiled JavaScript -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
        <script src="https://ajax.aspnetcdn.com/ajax/jQuery/jquery-3.3.1.min.js"></script>
    <script src="http://code.jquery.com/jquery-1.11.1.min.js"></script>
    <jsp:include page="../views/common/jscss.jsp">
	<jsp:param value="Learning Resources" name="title"/>
    </jsp:include>
    
    <style type="text/css">
    .bs-example{
    	margin: 20px;
    }
    .panel-title .glyphicon{ 
        font-size: 14px;
    }
	.panel-heading {
    	padding : 20px 20px 20px 20px;
    }

    .glyphicon {
        float:right;
    }

    .panel-group .panel {
        border-left-width: 4px;
        margin-bottom:20px;
    }

    .sz-content-wrapper .sz-content {
    padding: 80px 0px 0 2px;
}

@media (min-width: 768px) {
.nav-tabs.nav-justified>.active>a, .nav-tabs.nav-justified>.active>a:focus, .nav-tabs.nav-justified>.active>a:hover {
    border-bottom-color: #f70c0c;
}}

.tab-pane {
  display:none;
}

#login-block {
  display:block;
}

#register-block {
    width:380px;
    margin:25px;
    font-family: "p22-underground",sans-serif;
    font-style: normal;
    font-weight: 500;
    font-size:13pt;
    line-height:1.1;
    display:none;

}

.nav-tabs.nav-justified {
    width: 100%;
    border-bottom: 0;
    border-radius: 5px;
}

.panel-heading .accordion-toggle:after {
    /* symbol for "opening" panels */
    font-family: 'Glyphicons Halflings';  /* essential for enabling glyphicon */
    content: "\e114";    /* adjust as needed, taken from bootstrap.css */
    float: right;        /* adjust as needed */
    color: grey;         /* adjust as needed */
}
.panel-heading .accordion-toggle.collapsed:after {
    /* symbol for "collapsed" panels */
    content: "\e080";    /* adjust as needed, taken from bootstrap.css */
}

.one:hover { 
    background-color: gainsboro;
}

.two:hover { 
    background-color:gainsboro;
}
.bs-example{
		margin: 20px;
	}
</style>
    
    <body>
    
    	<%@ include file="../views/common/header.jsp" %>
        <div class="sz-main-content-wrapper">
        	<jsp:include page="../views/common/breadcrum.jsp">
			<jsp:param value="Student Zone;Learning Resources;Business Communication and Etiquette" name="breadcrumItems"/>
			</jsp:include>
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="../views/common/left-sidebar.jsp">
								<jsp:param value="Service Request" name="activeMenu"/>
							</jsp:include>
							
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="../views/common/studentInfoBar.jsp" %>
              						
              						
              						<div class="sz-content">
								
								
										<div class="clearfix"></div>
		              					
                                         
											<%@ include file="../views/common/messages.jsp" %>
											
                                            <div class="customBackground" style="background-image: linear-gradient(rgb(152, 139, 213), rgb(104, 91, 175));padding:30px;border-radius:5px;">	
                                                <div class="info">
                                                        
                                                    <div style="padding:10px 10px 10px 80px;">
                                                           
                                                    <p style="padding-left:5.2%;color:white;font-size:14px;margin-bottom:0px;">Business Communication and Etiquette >> Chapter 1</p>
                                                    <p style="color:white;font-size:18px;padding-left:3%;"><a href="/acads/demoOne">
                                                        <i class="fa-solid fa-arrow-left" style="font-size:20px;color:#fff;padding-right:10px;"></i>
                                                        </a>Defining Professionalism</p></div>
                                                    </div>
                                                </div>
                <div class="panel-body" style="width:80%;margin-left:11.5%;margin-top:-30px;background-color:#fff;border-radius:5px;padding:30px;">
                    <div class="row">
                    <div class="col-xs-4">
                        <div class="module-info" >
                          <p style="font-weight:400;">Module Progess</p>
                    <p style="font-size:16px;">0% Completed</p>
                    <div class="progress" style="background-color:#c5c5ca;">
                            <div class="progress-bar" role="progressbar" style="width: 0%;" aria-valuenow="25" aria-valuemin="0" aria-valuemax="100">0%</div>
                          </div>
                    </div>
                    </div>
                    <div class="col-xs-4 pull-right text-left">
                      <div class="due-date-info">
                            <i class="fa-regular fa-calendar" style="font-size:18px;">
                        Due Date</i>
                        <p style="font-size:18px;font-weight:800;">9th Feb 2018</p>
                      </div>  
                    </div>
                    </div>
                   </div>
                   
                 
            <div class="bs-example">
                <ul class="nav nav-tabs" id="myTab" style="padding-left:10.5%;">
                    <li class="active" style="width:30%"><a href="#sectionA">Module Content</a></li>
                    <li style="width:30%"><a href="#sectionB" >Video Content</a></li>
                   
                </ul>

                <div class="row" style="padding-left:20%;padding-top:2%;">
                    <div class="col-xs-4">
                            <p>SESSIONS </p>
                                </div>
                            <div class="col-xs-4 pull-right text-left">
                            <p>STATUS</p>
                            </div>
        </div>
                <div class="tab-content">
                    <div id="sectionA" class="tab-pane fade in active">
                       <div class="row" style="margin-left:2%;">
                
                        <div class = "container" style="width:85%;border-radius:20px;">
                        
                          
                                <div class="panel-group" id="accordion">
                                <div class="panel panel-default">
                                  <div class="panel-heading">
                                    <h4 class="panel-title">
                                        <p>Session 1</p>
                                      <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion" href="#collapseOne">
                                        Introduction To The Platform
                                      </a>
                                    
                                    </h4>
                                  </div>
                                  <div id="collapseOne" class="panel-collapse collapse in">
                                    <div class="panel-body" style="margin-left:8%;margin-right:8%;">
                                    <div class="one">
                                        <div class="link1" style="padding:20px;">
                                           <a href="/acads/pdfView">Welcome to the UpGrad Product Management Program</a></div></div>
                                           <hr>
                                           <div class="two">
                                               <div class="link2"  style="padding:20px;">
                                           <a href="/acads/pdfView">Welcome to the UpGrad Product Management Program</a></div></div>
                                           </div>
                                    </div>
                                  </div>
                                </div>
                                <div class="panel panel-default">
                                  <div class="panel-heading">
                                    <h4 class="panel-title">
                                        <p>Session 2</p>
                                      <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion" href="#collapseTwo">
                                            What to Expect from Management Roles
                                      </a>
                                    </h4>
                                  </div>
                                  <div id="collapseTwo" class="panel-collapse collapse">
                                    <div class="panel-body" style="margin-left:8%;margin-right:8%;">
                                     
                                    </div>
                                  </div>
                                </div>
                                
                              </div>
                                
                                
                              </div> 
                    </div>
                    <div id="sectionB" class="tab-pane fade">
                        <div class="row" style="margin-left:2%;">
                
                        <div class = "container" style="width:85%;border-radius:20px;">
                        
                          
                                <div class="panel-group" id="accordion">
                                <div class="panel panel-default">
                                  <div class="panel-heading">
                                    <h4 class="panel-title">
                                        <p>Session 1</p>
                                      <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion" href="#collapseOne">
                                        Introduction To The Platform
                                      </a>
                                    
                                    </h4>
                                  </div>
                                  <div id="collapseOne" class="panel-collapse collapse in">
                                    <div class="panel-body" style="margin-left:8%;margin-right:8%;">
                                    <div class="one">
                                        <div class="link1" style="padding:20px;">
                                           <a href="/acads/videoJsp">Welcome to the UpGrad Product Management Program</a></div></div>
                                           <hr>
                                           <div class="two">
                                               <div class="link2"  style="padding:20px;">
                                           <a href="/acads/videoJsp">Welcome to the UpGrad Product Management Program</a></div></div>
                                           </div>
                                    </div>
                                  </div>
                                </div>
                                <div class="panel panel-default">
                                  <div class="panel-heading">
                                    <h4 class="panel-title">
                                        <p>Session 2</p>
                                      <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion" href="#collapseTwo">
                                            What to Expect from Management Roles
                                      </a>
                                    </h4>
                                  </div>
                                  <div id="collapseTwo" class="panel-collapse collapse">
                                    <div class="panel-body" style="margin-left:8%;margin-right:8%;">
                                     
                                    </div>
                                  </div>
                                </div>
                                
                              </div>
                                
                                
                              </div>
                    </div>
                    
                </div>
            </div>
            
            
            </div>
                    
        <jsp:include page="../views/common/footer.jsp"/>
            
		
    </body>
   
    <script>
    $(document).ready(function(){
        $("#myTab a").click(function(e){
    	e.preventDefault();
    	$(this).tab('show');
    });
        $('body').progressTracker();
      
        jQuery(document).ready(function() {
        jQuery('.ui-105-content .nav a').on('click', function(e)  {
            console.log('called jquery');
        var currentAttrValue = jQuery(this).attr('href');
        console.log(currentAttrValue);
        // Show/Hide Tabs
        jQuery('.ui-105-content' + currentAttrValue).show().siblings().hide();
        // Change/remove current tab to active
        jQuery(this).parent('li').addClass('link-one').siblings().removeClass('link-one');
        e.preventDefault();
        });
        });
        
        var myColors = [
                        'red', 'blue', 'yellow' , 'green' , 'purple' , 'orange'
                    ];
                    var i = 0;
                    
                    
                    $('div#myDiv').each(function() {
                        $(this).css('border-left-color', myColors[i]);
                        i = (i + 1) % myColors.length;
                    });
                    
   
                  
    });
</script>
</html>