 <!DOCTYPE html>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 

<html lang="en">
    <style>
    #confirmation{
    position: unset;left: 999px;opacity: 100;
    }
    </style>

	
    
    <jsp:include page="../common/jscss.jsp">
	<jsp:param value="Enter Service Request Information" name="title"/>
    </jsp:include>
    
    
    
    <body>
    <c:set var="reason" value="${fn:split('Shifting Abroad,Not Interested,No time to study,Other', ',')}" scope="application" />

    	<%@ include file="../common/header.jsp" %>
    	
    	
        
        <div class="sz-main-content-wrapper">
        
        	<%@ include file="../common/breadcrum.jsp" %>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="../common/left-sidebar.jsp">
								<jsp:param value="Service Request" name="activeMenu"/>
							</jsp:include>
							
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="../common/studentInfoBar.jsp" %>
              						
              						
              						<div class="sz-content">
								
										<h2 class="red text-capitalize">${sr.serviceRequestType }</h2>
										<div class="clearfix"></div>
		              					<div class="panel-content-wrapper">
											<%@ include file="../common/messages.jsp" %>
											
											<p>
											Dear Student, You have chosen below Service Request. Please fill in required information below before proceeding for Payment. 
											</p>
											<!-- <p>You won't be able to submit service Request for next 48hrs . For details refer to "My Communications" Tab</p> -->
											
											<form:form   method="post" modelAttribute="sr" enctype="multipart/form-data" class="">
											<fieldset>
												
												<div class="row">
												<div class="col-md-8">
												
												<div class="form-group">
													<form:label path="serviceRequestType" for="serviceRequestType">Service Request Type:</form:label>
													<p>${sr.serviceRequestType }</p>
													<form:hidden path="serviceRequestType"/>
												</div>
												
												<div class="form-group">
													<form:label path="">Charges:</form:label>
													<p>No Charges</p>
												</div>
												
												<div class="row">
													<div class="col-md-6">
														<div class="form-group">
															<form:label path="">Student No:</form:label>
															<p>${student.sapid }</p> 
														</div>
													</div>
													<div class="col-md-6">
														<div class="form-group">
															<label>Student Name:</label>
															<p>${student.firstName } ${student.lastName }</p>  
														</div> 
													</div>
												</div> 
												<div class="row">
													<div class="col-md-6">
														<div class="form-group">
														<label>Enrolment Year:</label>
														<p><p>${student.enrollmentYear }</p> </p>
													</div>
													</div>
													<div class="col-md-6">
														<div class="form-group">
														<label>Program Name:</label>
														<p><p>${student.program }</p> </p>  
													</div> 
													</div>
												</div> 
												<div class="row">
													<div class="col-md-6">
														<div class="form-group">
															<label>Validity Month:</label>
															<p><p>${student.validityEndMonth }</p> </p>
														</div>
													</div>
													<div class="col-md-6">
														<div class="form-group">
															<label>Validity Year:</label>
															<p><p>${student.validityEndYear }</p> </p>
														</div> 
													</div>
												</div> 
												 
												<div class="form-group">
													<label>Reason for withdrawal:</label>
													<form:select id="serviceRequestType" path="description" class="form-control reason" required="required">
														<form:option value="">Select Service Request</form:option>
														<form:options items="${reason}" />  
													</form:select>
												</div> 
												<div class="row otherReasonDiv" style="display:none;">   
													<div class="form-group col-md-6"> 
														<label>Reason :</label>
														<form:input type="textarea" path="additionalInfo1" class="form-control otherReason"   />   
														   
													</div>  
													<div class="form-group col-md-6"> 
													</div>   
												</div>
												<div class="form-group">
													<input type="checkbox"  id="confirmation" /> 
													I hereby agree and accept to completely withdraw from my program of study offered by SVKM's NMIMS - NMIMS Global Access - School for Continuing Education. I also agree that in case of any dispute or differences about this withdrawal, the decision of the SVKM's NMIMS - NMIMS Global Access - School for Continuing Education will be final and binding on me. I am aware that No fees pending or otherwise will be refunded.
												</div>  
	
												<div class="form-group">
													<label class="control-label" for="submit"></label>
													<div class="controls">
														 <button id="submit" name="submit"
															class="btn btn-large btn-primary submitform" formaction="saveProgramWithdrawal" onClick="return confirm('Are you sure you want to save this information?');">Save Service Request</button>
															<button id="backToSR" name="BacktoNewServiceRequest" class="btn btn-danger" 
															formaction="selectSRForm" formnovalidate="formnovalidate">Back to New Service Request</button>
													</div>
												</div>
												
												</div>
												</div>
												
												</fieldset>
												</form:form>
													
											
										</div>
              								
              						</div>
              				</div>
              		
                            
					</div>
            </div>
        </div>
            
  	
        <jsp:include page="../common/footer.jsp"/>
            <script>
		$("document").ready(function() {
			$('.reason').change(function () {
		
				var selectedReason = $(this).children("option:selected").val();
				
				if(selectedReason=="Other"){    
					$('.otherReasonDiv').css('display', 'block');
					$(".otherReason").val("");   
				}else{  
					$('.otherReasonDiv').css('display', 'none');  
					$(".otherReason").val(selectedReason);      
				}
			});
			$('.submitform').click(
					function () {
						var reason= $(".otherReason").val();
						if(reason.length<5){
							alert("please provide a reason for withdrawal");
							return false;      
						}
						
						if(!$('#confirmation').is(":checked")){
							alert("please check confirmation "); 
							return false;
						}
					}
			);
			
		});
		</script>
    </body>
</html>