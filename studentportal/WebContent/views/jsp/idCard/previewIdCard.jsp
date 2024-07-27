<!DOCTYPE html>
<%@page import="org.apache.catalina.util.URLEncoder"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html lang="en">
    	<style>
*{
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
.outer{
  position: relative;
  padding: 10px;
  animation: reduce 2s ease-in-out infinite;
  height: 50px;
}

button.inner {
  position: absolute;
  left: 50%;
}

@keyframes reduce {
  0%,
  100% {
    height: 100vh;
  }
  50% {
    height: 100vh;
  }
}
</style>

	
    
    <jsp:include page="../common/jscss.jsp">
	<jsp:param value="My Id Card" name="title"/>
    </jsp:include>
    
    	<%-- <%@ include file="../common/header.jsp" %> --%>
    	
    	
        
     <!--    <div class="sz-main-content-wrapper"> -->
        
        <%-- 	<%@ include file="../common/breadcrum.jsp" %> --%>
        	
            
          <!--   <div class="sz-main-content menu-closed"> -->
                    <div class="sz-main-content-inner">
              				<%-- <jsp:include page="../common/left-sidebar.jsp">
								<jsp:param value="My Documents" name="activeMenu"/>
							</jsp:include>
              				 --%>
              				
              				<!-- <div class="sz-content-wrapper examsPage"> -->
              						<%-- <%@ include file="../common/studentInfoBar.jsp" %> --%>
              						
              					
              						<div class="sz-content">
								
										<h2 class="red text-capitalize">ID Card</h2>
										<div class="clearfix"></div>
			              					<div class="panel-content-wrapper">
			              					<%-- <%@ include file="../common/messages.jsp" %> --%>
												<div class="embed-responsive embed-responsive-16by9 outer">	
												 <iframe id="frameWindow" class="embed-responsive-item inner" src='<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />pdfjsViewerX/webX/viewer.html?file=<spring:eval expression="@propertyConfigurer.getProperty('ID_CARD_S3_PATH')" />${fileName }#zoom=100'></iframe>
												 
												 </div>	           									
											</div>
              						</div>
              			<!-- 	</div> -->
              		
                            
					</div>
<!--             </div> -->
 <!--        </div> -->
  	
       <%--  <jsp:include page="../common/footer.jsp"/> --%>
            
		
    </body>
</html>