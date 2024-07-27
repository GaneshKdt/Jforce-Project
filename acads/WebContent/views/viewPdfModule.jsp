<!DOCTYPE html>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<?php $htmlString= 'testing'; ?>
<html lang="en">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/Swiper/4.1.6/css/swiper.min.css">
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
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
    }

    .sz-content-wrapper .sz-content {
    padding: 80px 0px 0 2px;
}

 .tile-progressbar {
height: 10px;
background: rgba(0,0,0,0.18);
margin: 0;

}
 .swiper-container {
    width: 600px;
    height: 300px;
}
 .tile-progressbar span {
    border-radius: 5px;
display: block;
background: #e25050;
width: 0;
height: 100%;
-webkit-transition: all 1.5s cubic-bezier(0.230,1.000,0.320,1.000);
-moz-transition: all 1.5s cubic-bezier(0.230,1.000,0.320,1.000);
-o-transition: all 1.5s cubic-bezier(0.230,1.000,0.320,1.000);
transition: all 1.5s cubic-bezier(0.230,1.000,0.320,1.000);
}
@media (min-width: 768px) {
.nav-tabs.nav-justified>.active>a, .nav-tabs.nav-justified>.active>a:focus, .nav-tabs.nav-justified>.active>a:hover {
    border-bottom-color: #f70c0c;
}}

*
{
    margin:0;
    padding:0;
}
 
/* ============================== This is for Overall Styling of the Content Gallery. */
.gallery-container
{
  
    margin:10px auto;
    padding:0px;
    background-color:#fff;
    min-height:1000px;
}
.gallery
{
    width:800px;
    height:600px;
    background:#fff;
    padding-left:20%;
    /*border:2px solid green;*/
}
 

.slider ul
{
    width:100000px;
    list-style:none;
    margin-left:0px;
}
.slider li
{
    float:left;
}
 
/* ============================== This is for Navigation Buttons Styling. */
.slider-nav
{
    display:none; /* This is important to hide the buttons if jQuery is Not supported. */
    margin-top: -10em;
    cursor: pointer;    
}
 
.backbtn
{
    position:relative;
}
 
.nextbtn
{
    position:relative;
}
 
/* ============================== This is for Pagination Styling. */

h3
{
    position:absolute;
    width:100%;
    top:50px;               
}
h3 span.slides_texts
{
   color: white;
   padding:10px;
   letter-spacing: 1px;
   background: rgba(0, 0, 0, 0.5);
   line-height:42px;   
}
 
h3 span.slides_heading
{
    color: white;
    display:table;

    margin:0px auto;
    background: rgba(0, 0, 0, 0.5);
    padding:10px;
}
 
h3 div.text_container
{
    margin:10px;
}
 
.bg_img
{
    width: 100%;
    height: 800px;
}
 .swiper-container {
      width: 100%;
      height: 100%;
    }
    .swiper-slide {
      text-align: center;
      font-size: 18px;
      background: #fff;
      /* Center slide text vertically */
      display: -webkit-box;
      display: -ms-flexbox;
      display: -webkit-flex;
      display: flex;
      -webkit-box-pack: center;
      -ms-flex-pack: center;
      -webkit-justify-content: center;
      justify-content: center;
      -webkit-box-align: center;
      -ms-flex-align: center;
      -webkit-align-items: center;
      align-items: center;
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
              				
        						
              						<div class="sz-content">
								            						                        
        
                                         
											<%@ include file="../views/common/messages.jsp" %>
											
                                            <div class="customBackground" style="background-color: #616161;color:#fff;padding:30px;border-radius:5px;">	
                                                    <div class="info">
                                                            
                                                        <div style="padding:10px 10px 10px 80px;">
                                                               
                                                        <p style="padding-left:8%;color:white;font-size:14px;margin-bottom:0px;">Business Communication and Etiquette >> Chapter 1 >> Session 1</p>
                                                        <p style="color:white;font-size:18px;padding-left:5.7%;"><a href="/acads/student/moduleLibraryList?moduleId=${moduleContentBean.moduleId}">
                                                            <i class="fa-solid fa-arrow-left" style="font-size:20px;color:#fff;padding-right:10px;"></i>
                                                            </a>  Introduction To The Platform</p></div>
                                                        </div>
                                                    </div>
                <div class="panel-body" style="width:90%;padding-left:15%;margin-top:-30px;">
                 <div class="ui-105-content">

            <div class="nav nav-tabs nav-justified" style="background-color:gainsboro;padding:40px;height:50px;border-radius:5px;">
               <p style="margin-left:5%;font-size:20px;color:#000;margin-bottom:0px;"> Welcome to the UpGrad Product Management Program</p>
                <div class="progress" style="margin-left:5%;width:80%">
                        <div class="progress-bar progress-bar-success" role="progressbar" style="width:40%">
                          Free Space
                        </div>
                        <div class="progress-bar progress-bar-warning" role="progressbar" style="width:10%">
                          Warning
                        </div>
                        <div class="progress-bar progress-bar-danger" role="progressbar" style="width:20%">
                          Danger
                        </div>
                      </div>
                </div>

                       <div class="tab-content"  >

                  <div class="tab-pane active fade in" id="pdf-tab" >

                        <!-- Login Block Form -->

                        <div class="login-block-form" id="tab-1">

                                        <div class="para" style="background-color:#fff;padding-left:8%;padding-bottom:30px;">
                                            <p style="font-size:25px;padding:30px 0px 20px 0px;">Punit Soni, Ex-Chief Product Officer, Flipkart</p>
                                            <p>Punit Soni is a seasoned product leader focused on the mobile sector, and specialises in all aspects of product development</p>
                                            <p> including crafting strategy, building teams and executing to go to market. Before Flipkart, he worked with Google and Motorola. </p>
                                        </div>
                                       
                                                   
                                                    <!-- The actual moving Slider -->
                                                    <!-- Slider main container -->
<div class="swiper-container">
    <!-- Additional required wrapper -->
   
        <!-- Slides -->
        
        
        <div class="swiper-wrapper">
                                                        
                                                        <c:forEach items="${images}" var="image" varStatus="status">
                                                            
                                                                <div class="swiper-slide">
                                                                    <div class="slides_content">
                                                                    		 <i class=" glyphicon glyphicon-zoom-in" id="imgSmile"></i> 
                                                                            <a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />assets/images/first_1.png" download>Download</a>
                                                                        <img class="bg_img"  src="data:image/jpeg;base64,${image}" alt="Image of Topic"/>
                                                                    </div>
                                                                </div>
                                                           
                                                            </c:forEach>
                                                     
 
    </div>
    <!-- If we need pagination -->
    <div class="swiper-pagination"></div>
 
    <!-- If we need navigation buttons -->
    <div class="swiper-button-prev"></div>
    <div class="swiper-button-next"></div>
 
    <!-- If we need scrollbar -->
    <div class="swiper-scrollbar"></div>
</div>
                                                     
                                                    <!-- Navigation Button Controls -->
                                                  
                                                         
                                               
                                                <!-- Pagination Controls -->
                                                
                     
                            
                                    </div>
                                </div>
                                   
                                   
                                   
                                   <div class="tab-pane fade in" id="video-tab" >

                        <!-- Login Block Form -->

                        <div class="login-block-form" id="tab-1">

                                        <div class="para" style="background-color:#fff;padding-left:8%;padding-bottom:30px;">
                                            <p style="font-size:25px;padding:30px 0px 20px 0px;">Punit Soni, Ex-Chief Product Officer, Flipkart</p>
                                            <p>Punit Soni is a seasoned product leader focused on the mobile sector, and specialises in all aspects of product development</p>
                                            <p> including crafting strategy, building teams and executing to go to market. Before Flipkart, he worked with Google and Motorola. </p>
                                        </div>
 												<div id="embed_container" class='embed-container'>
													<iframe height="770" src="${moduleContentBean.moduleVideoUrl}" id="video" frameborder='0'
													 webkitAllowFullScreen mozallowfullscreen allowFullScreen></iframe>
												</div>			
                            
                                    </div>
                                </div>
                                   
                                    </div>
                                </div>
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
    <script src="https://cdnjs.cloudflare.com/ajax/libs/Swiper/4.1.6/js/swiper.min.js"></script>

    <script>
   
    var mySwiper = new Swiper ('.swiper-container', {
        // Optional parameters
        direction: 'horizontal',
        loop: true,

        // If we need pagination
        pagination: {
          el: '.swiper-pagination',
        },

        // Navigation arrows
        navigation: {
          nextEl: '.swiper-button-next',
          prevEl: '.swiper-button-prev',
        },

        // And if we need scrollbar
        scrollbar: {
          el: '.swiper-scrollbar',
        },
      })
      </script>
 
    
    <script>
    $(document).ready(function(){
    	
    	var url = img.src.replace(/^data:image\/[^;]+/, 'data:application/octet-stream');
    	window.open(url);
       

   $('#imgSmile').width(300);

   $('#imgSmile').mouseover(function()

   {

      $(this).css("cursor","pointer");

   });

   $("#imgSmile").toggle(function()

     {$(this).animate({width: "500px"}, 'slow');},

     function()

     {$(this).animate({width: "200px"}, 'slow');

   });



        
        var path = 'assets/images/';
              
        // Add minus icon for collapse element which is open by default
        $(".collapse.in").each(function(){
        	$(this).siblings(".panel-heading").find(".glyphicon").addClass("glyphicon-minus").removeClass("glyphicon-plus");
        });
        
        // Toggle plus minus icon on show hide of collapse element
        $(".collapse").on('show.bs.collapse', function(){
        	$(this).parent().find(".glyphicon").removeClass("glyphicon-plus").addClass("glyphicon-minus");
        }).on('hide.bs.collapse', function(){
        	$(this).parent().find(".glyphicon").removeClass("glyphicon-minus").addClass("glyphicon-plus");
        });
        
        var myColors = [
                        'red', 'blue', 'yellow' , 'green' , 'purple' , 'orange'
                    ];
                    var i = 0;
                    
                    
                    $('div#myDiv').each(function() {
                        $(this).css('border-left-color', myColors[i]);
                        i = (i + 1) % myColors.length;
                    });
                    
                 
                    $('.tabs-menu a').click(function(event) {
    event.preventDefault();
    
    // Toggle active class on tab buttons
    $(this).parent().addClass("current");
    $(this).parent().siblings().removeClass("current");
    
    // display only active tab content
    var activeTab = $(this).attr("href");
    $('.tab-content').not(activeTab).css("display","none");
    $(activeTab).fadeIn();
    
  });
  
  
    });
  
</script>

</html>
