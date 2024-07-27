        <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<header>
		
      <section class="top-bar">	
        <div class="container">
          <div class="row">
            <div class="col-sm-18">
            <%
		            String userId = (String)session.getAttribute("userId");
		            if(userId != null) { %>
			              <ul>
			              	<li><a href="gotoStudentHome">Home</a></li>
			                
			              </ul>
              
              <%} %>
		      <div class="clearfix"></div>
            </div> <!-- /col-sm-18 -->
          </div> <!-- /row -->
        </div> <!-- /container -->
      </section> <!-- /top-bar -->  

	  <section class="main-header">
        <div class="container">
          <div class="row">
            <div class="col-xs-18 col-sm-6">
              <a href="http://distance.nmims.edu/" target="_blank"><img src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources/images/logo.png" alt="Narsee Monjee Distance Learning University" class="logo img-responsive" /></a>
            </div> <!-- /col-xs-10 -->
            <div class="col-xs-18 col-sm-12">
              <h1 class="text-right">Exam Portal</h1>
            </div> <!-- /col-xs-8 -->
          </div> <!-- /row -->
        </div> <!-- /container -->
      </section> <!-- /main-header -->  
      
    </header> <!-- /header -->