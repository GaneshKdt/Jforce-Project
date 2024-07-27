<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>

<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="jscss.jsp">
<jsp:param value="Content" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
   	<div class="container-fluid customTheme">
    <div class="row"> <legend><%=request.getParameter("name") %></legend></div>
	<embed align="middle" src="<spring:eval expression="@propertyConfigurer.getProperty('CONTENT_PREVIEW_PATH')" /><%=request.getParameter("previewPath") %>?id=<%=Math.random() %>" width = "100%" height="500px">	
	
	
	</div>
	<br>
	</section>

	  <jsp:include page="footer.jsp" />


</body>
</html>
 --%>
 
 
 <!DOCTYPE html>
<%@page import="org.apache.catalina.util.URLEncoder"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html lang="en">
    

	
    
    <jsp:include page="common/jscss.jsp">
	<jsp:param value="View Content" name="title"/>
    </jsp:include>
    
    
    
    <body onload="disableContextMenu();" oncontextmenu="return false">
    
    	<%@ include file="common/header.jsp" %>
    	
    	
        
        <div class="sz-main-content-wrapper">
        
        	<%@ include file="common/breadcrum.jsp" %>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="common/left-sidebar.jsp">
								<jsp:param value="My Courses" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="common/studentInfoBar.jsp" %>
              						
              					
              						<div class="sz-content">
								
										<h2 class="red text-capitalize"><%=request.getParameter("name") %></h2>
										<div class="clearfix"></div>
			              					<div class="panel-content-wrapper">
			              					<%@ include file="common/messages.jsp" %>
			              					<%
			              					if("PDF".equals(request.getParameter("type"))){ %>
												<div class="embed-responsive embed-responsive-16by9">	
													<iframe id="frameWindow" class="embed-responsive-item" src="<spring:eval expression="@propertyConfigurer.getProperty('CONTENT_PREVIEW_PATH')" /><%=request.getParameter("previewPath") %>?id=<%=Math.random() %>#toolbar=0"></iframe>
												</div>
	           								<%} %>
	           									
											</div>
              						</div>
              				</div>
              		
                            
					</div>
            </div>
        </div>
            <script>
				function previewContent() {
					var search = window.location.search;
					window.location.href = "/acads/student/previewContentAlt" + search;
				}
				
				function disableContextMenu() {
					document.getElementById("frameWindow").contentWindow.document.oncontextmenu = function(){return false;};;    
				}
            </script>
  	
        <jsp:include page="common/footer.jsp"/>
            
		
    </body>
</html>