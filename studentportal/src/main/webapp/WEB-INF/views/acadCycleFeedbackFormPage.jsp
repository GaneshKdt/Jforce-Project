<!DOCTYPE html>


<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html lang="en">
    
    
    <jsp:include page="common/jscss.jsp">
	<jsp:param value="Share Feedback" name="title"/>
    </jsp:include>
    
    <style>
    
    .complete-profile-warpper .sz-content-wrapper.withBgImage .student-info-bar .student-image {
    	border: 2px solid #000;
	}
	
	.complete-profile-warpper .sz-content-wrapper.withBgImage .student-info-bar ul.student-info-list li {
	    color: #333;
	}
    </style>
    
    <body>
    
    	<%@ include file="common/header.jsp" %>
    	
    	
        
        <div class="sz-main-content-wrapper complete-profile-warpper">
        
        	<jsp:include page="common/breadcrum.jsp">
		<jsp:param value="Student Zone;Feedback" name="breadcrumItems"/>
		</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="container">
              			
              				
              				
              				<div class="sz-content-wrapper dashBoard withBgImage">
              						<%@ include file="common/studentInfoBar.jsp" %>
              						
              						
										<h2 class="red text-capitalize" style="margin-top:-20px;">Feedback for last Academic cycle</h2>
										<div class="clearfix"></div>
		              					<div class="panel-content-wrapper">
											<%@ include file="common/messages.jsp" %>
					
												
										<div class="clearfix"></div>
										
										<form:form method="post" modelAttribute="feedback">
												<fieldset>
							<form:hidden path="sem" value = "${feedback.sem}"/>
							<form:hidden path="year" value = "${feedback.year}"/>
							<form:hidden path="month" value = "${feedback.month}"/>
													<div class="clearfix"></div>
												
														<div class="row">
														
															<p style ="padding-left: 15px">Dear Student,<br>
																	We at NMIMS would like to hear about your feedback/suggestions during the last Academic cycle to help us fix issues/concerns if any.<br><br>
																	Thanks and Regards,<br>
																	Team NGASCE<br><br>
																	You would like to submit your feedback.
															</p>
															
														</div>
													
														
													
													<div class="col-sm-6">
														<button id="submit" name="submit" class="primary red-btn"	onclick="return validateForm();" formaction="acadCycleFeedbackForm" style="background-color: green">Yes</button>
														<button id="submit" name="submit" class="customBtn red-btn"	onclick="return validateForm();" formaction="saveAcadCycleFeedback?val=No">No</button>
														
													</div>
												</fieldset>
											</form:form>
												
													<div class="row">
													<div class="col-sm-6">
														<a href="/studentportal/skipFeedback"
															class="btn btn-warning"	
															title = "Skip Feedback"
															>Skip Feedback</a>
													</div>
													</div>
										</div>
              								
              				</div>
                            
					</div>
            </div>
        </div>
            
        <jsp:include page="common/footer.jsp"/>
    </body>
</html>