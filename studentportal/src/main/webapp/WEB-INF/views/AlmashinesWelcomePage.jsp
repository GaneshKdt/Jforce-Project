<!DOCTYPE html>

<%@page import="java.net.URLEncoder"%>
<%@page import="com.nmims.helpers.AESencrp"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html lang="en">
    
    <jsp:include page="common/jscss.jsp">
	<jsp:param value="Welcome to Almashines" name="title"/>
    </jsp:include>
    
<style>
.button {
  color: rgb(255,255,255);
  background-color: #EA750C;
  font-size: 14px; 
  width: 270px;
  transition: all 0.5s;
}
.button span {
  cursor: pointer;
  display: inline-block;
  position: relative;
  transition: 0.5s;
}
.button span:after {
  content: '\00bb';
  position: absolute;
  opacity: 0;
  top: 0;
  right: -20px;
  transition: 0.5s;
}
.button:hover span {
  padding-right: 25px;
}
.button:hover span:after {
  opacity: 1;
  right: 0;
}
.sz-content p{ 
font-size: 14px;!important} 
</style>
    
    <body>
    
	<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')" var="server_path" />
	
    	<%@ include file="common/header.jsp" %>
    	
    	
        
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="common/breadcrum.jsp">
			<jsp:param value="Student Zone;Almashines" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="common/left-sidebar.jsp">
								<jsp:param value="almashines" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper ">
              						<%@ include file="common/studentInfoBar.jsp" %>
              						
              						<div class="row">
              						<div class="col-md-1">
              						</div> 
              						<div class="col-md-8">
              						<div class="sz-content"> 
										
										<div class="clearfix"></div>
		              					<div class="panel-content-wrapper" style="padding:3rem;margin-top:2rem;">
										<%@ include file="messages.jsp"%> 
		              					<h2 style="font-size:30px;">Welcome To NGASCE Alumni Portal </h2>  
		              						<h2 class="">Drive Connections And Create Opportunities With Your Alumni's Professional Network</h2>
											<div class="clearfix"></div>
											<p>Enrich your career, stay engaged with NMIMS Alumni, 
											receive career opportunities and more... Only with NGASCE Alumni Portal. A dedicated platform for the final year students of Post Graduate programs and alumni of Post Graduate and Diploma programs at NMIMS Global Access. </p>
											 
											<h2 class="">Key Benefits:</h2>
											<div class="clearfix"></div>
											
												<p><i class="fa fa-angle-double-right"></i> Unique 'Search Feature' allows you to find members based on their current city, company, designation, professional skills and many more</p>
												<p><i class="fa fa-angle-double-right"></i> Create a niche for your expertise by posting on the "Notice Boar" </p></li>
												<p><i class="fa fa-angle-double-right"></i> Collaborate and share new thoughts & ideas with fellow alumnus </p></li>
											
											<br><br> 
											<a  href="almashinesLogin" target="_blank"><button class="button"><span>Connect to NGASCE Alumni Portal</span></button></a>
											</button>
										</div> 
              								
              						</div>
              						</div>
              						</div>
              						
              				</div>
              		
                            
					</div>
            </div>
        </div>
            
		<div id="csApp"></div>
		<div id="examApp"></div>
        <jsp:include page="/WEB-INF/views/common/footer.jsp"/>


	
    </body>
</html>