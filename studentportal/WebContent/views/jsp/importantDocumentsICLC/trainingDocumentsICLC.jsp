 <!DOCTYPE html>


<html lang="en">
	
  <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
    <jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Important Documents" name="title"/>
    </jsp:include>
    
    
    
    <body>
    
    	<%@ include file="../adminCommon/header.jsp" %>
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;Important Documents" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="../adminCommon/left-sidebar.jsp">
								<jsp:param value="" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="../adminCommon/adminInfoBar.jsp" %>
              					<div class="sz-content">
								
									<h2 class="red text-capitalize">Important Documents</h2>
									<div class="clearfix"></div>
									<div class="panel-content-wrapper" style="min-height:450px;">
										<%@ include file="../adminCommon/messages.jsp" %>
									<div id="All" class="table-responsive" style="display:none;">
											<table class="table table-striped table-hover" style="font-size:12px">
												<thead>
													<tr> 
													
														<th>Subject</th>
														<th>Model Answers</th>
													</tr>
												</thead>
												<tbody>
												
											      
											        <tr>
											            
														<td>Program Information- NGASCE</td>
														<td><a href="${pageContext.request.contextPath}/resources_2015/importantDocsICLC/Program Information- NGASCE.pptx" target="_blank"><i class="fa-solid fa-download"></i>Download</a></td>
											        </tr> 
											        
											        <tr>
											            
														<td>Training Session July-12-2018</td>
														<td><a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/ImportantDocICLC/Training/Training Session July 12, 2018-20180712 0541-1.mp4" target="_blank"><i class="fa-solid fa-download"></i>Download/View</a></td>
											        </tr> 
											        
											        <tr>
											            
														<td>Training Session July-17-2018</td>
														<td><a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/ImportantDocICLC/Training/Training Session July 17, 2018-20180717 0537-1.mp4" target="_blank"><i class="fa-solid fa-download"></i>Download/View</a></td>
											        </tr> 
											        
											        <tr>
											            
														<td>Admissions</td>
														<td><a href="${pageContext.request.contextPath}/resources_2015/importantDocsICLC/Admissions.pdf" target="_blank"><i class="fa-solid fa-download"></i>Download</a></td>
											        </tr> 
											        
											        <tr>
											            
														<td>Examination</td>
														<td><a href="${pageContext.request.contextPath}/resources_2015/importantDocsICLC/Examination.pdf" target="_blank"><i class="fa-solid fa-download"></i>Download</a></td>
											        </tr> 
											        
											          <tr>
											            
														<td>Student Support</td>
														<td><a href="${pageContext.request.contextPath}/resources_2015/importantDocsICLC/Student Support.pdf" target="_blank"><i class="fa-solid fa-download"></i>Download</a></td>
											        </tr>  
													
											        
											          <tr>
											            
														<td>Academics</td>
														<td><a href="${pageContext.request.contextPath}/resources_2015/importantDocsICLC/Academics .pdf" target="_blank"><i class="fa-solid fa-download"></i>Download</a></td>
											        </tr> 
											        
											        
											        <tr>
											            
														<td>ISM Session Plan</td>
														<td><a href="${pageContext.request.contextPath}/resources_2015/importantDocsICLC/ISM Session Plan.pdf" target="_blank"><i class="fa-solid fa-download"></i>Download</a></td>
											        </tr> 
											        
											        <tr>
											            
														<td>Live Session Guide</td>
														<td><a href="${pageContext.request.contextPath}/resources_2015/importantDocsICLC/Live Session Guide.pdf" target="_blank"><i class="fa-solid fa-download"></i>Download</a></td>
											        </tr>  
											        
											          <tr>
											            
														<td>PCP Guidelines July 2017</td>
														<td><a href="${pageContext.request.contextPath}/resources_2015/importantDocsICLC/PCP Guidelines July 2017.pdf" target="_blank"><i class="fa-solid fa-download"></i>Download</a></td>
											        </tr> 
											        
											          <tr>
											            
														<td>PCP Registration Guide July 2017</td>
														<td><a href="${pageContext.request.contextPath}/resources_2015/importantDocsICLC/PCP Registration Guide July 2017.pdf" target="_blank"><i class="fa-solid fa-download"></i>Download</a></td>
											        </tr> 
											        
											          <tr>
											            
														<td>Steps to Post Query NGA - SCE</td>
														<td><a href="${pageContext.request.contextPath}/resources_2015/importantDocsICLC/Steps to Post Query NGA - SCE.pdf" target="_blank"><i class="fa-solid fa-download"></i>Download</a></td>
											        </tr> 
											        
											         <tr>
											            
														<td>Training Session MBA(WX) 10-Sep-2019</td>
														<td><a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/ImportantDocICLC/Training/Training Session MBA(WX) 10-Sep-2019.mp4" target="_blank"><i class="fa-solid fa-download"></i>Download/View</a></td>
											        </tr> 
													
												</tbody> 
											</table>
									 </div>
									 <div>
									 <div id="SAS" class="table-responsive" style="display:none;">
											<table class="table table-striped table-hover" style="font-size:12px">
												<thead>
													<tr> 
													
														<th>Subject</th>
														<th>Document</th>
													</tr>
												</thead>
												<tbody>
											        <tr>
														<td>Program Information</td>
														<td><a href="${pageContext.request.contextPath}/resources_2015/importantDocsICLC/Program Information- SAS.pdf" target="_blank">View</a></td>
											        </tr> 
											        
												</tbody>
											</table>
									
									 
									 </div>
              								
              					</div>
              				</div>
    				</div>
			</div>
		</div>
        <jsp:include page="../adminCommon/footer.jsp"/>
       
       

<script type="text/javascript">
	// Parse the URL parameter
	function getParameterByName(name, url) {
	    if (!url) url = window.location.href;
	    name = name.replace(/[\[\]]/g, "\\$&");
	    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
	        results = regex.exec(url);
	    if (!results) return null;
	    if (!results[2]) return '';
	    return decodeURIComponent(results[2].replace(/\+/g, " "));
	}
	// Give the parameter a variable name
	var dynamicContent = getParameterByName('viewer');

	 $(document).ready(function() {

		// Check if the URL parameter is All
		if (dynamicContent == 'All') {
			$('#All').show();
		} 
		// Check if the URL parameter is SAS
		else if (dynamicContent == 'SAS') {
			$('#SAS').show();
		} 
	});
</script> 
		
    </body>
</html>