<!DOCTYPE html>
<html lang="en">


<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>    
    <jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Welcome to Student Zone" name="title"/>
    </jsp:include>
     <style>
   .jumbotron{
    background-color:#fff;
    padding:0.5px;
    }
    label{
    font-size:15px;
    }
   </style>
 
      
    <body>
    
    	<%@ include file="adminCommon/header.jsp" %>
    	
        
   
   
  
   
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Student Zone;Home" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="adminCommon/left-sidebar.jsp">
								<jsp:param value="" name="activeMenu"/>	
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="adminCommon/adminInfoBar.jsp" %>
              						
              						
              						<div class="sz-content">
              						<h2 class="red text-capitalize">Allocate Faculty</h2>
              						<div class="clearfix"></div>
              						<div class="panel-content-wrapper" style="min-height:450px;">
              						<%@ include file="adminCommon/messages.jsp" %>
              						<%try{ %>
									<form:form id="allocateFormId" method="POST" action="saveAllocation" modelAttribute="facultyBean">
										<fieldset>
											<div class="form-group">
											<label for="roleForAllocationId">Roles</label>
											<form:select id="roleForAllocationId" path="roleForAllocation" required="true" placeholder="ROLE" class="form-control">
												<form:option value="">Select Allocation</form:option>
												<form:options items="${rolesForAllocationList}" />
											</form:select>
											</div>
											
											<div class="form-group">
											<label for="roleForAllocationId">Assign By</label>
											
															<select class="form-control" required="true" placeholder="MONTH" id="assignByID">
															     <option value="">Select Month</option>
															    <option value="EM">Exam Month</option>
															    <option value="AM">Academic Month</option>
															</select>
											</div>
											
											<hr>
											
											<div class="form-group" id="acadYearAndMonthDiv" style="display:none;">
											<label for="acadYearId">ACADEMIC YEAR</label>
												<form:select id="acadYearId" path="acadYear" placeholder="ACAD YEAR" class="form-control">
												<form:option value="">Select Acad Year</form:option>
												<form:options items="${yearList}" />
											</form:select>
											<label for="acadMonthId">ACADEMIC MONTH</label>
												<form:select id="acadMonthId" path="acadMonth" placeholder="ACAD MONTH" class="form-control">
												<form:option value="">Select Acad Month</form:option>
												<form:options items="${acadMonthList}" />
											</form:select>
											
											</div>
											
											<div class="form-group" id="examYearAndMonthDiv" style="display:none;">
											<label for="examYearId">EXAM YEAR</label>
												<form:select id="examYearId" path="examYear" placeholder="EXAM YEAR" class="form-control">
												<form:option value="">Select Exam Year</form:option>
												<form:options items="${yearList}" />
											</form:select>
											
											<label for="examMonthId">EXAM MONTH</label>
												<form:select id="examMonthId" path="examMonth" placeholder="EXAM MONTH" class="form-control">
												<form:option value="">Select Exam Month</form:option>
												<form:options items="${examMonthList}" />
											</form:select>
											</div>
											
											
											<div id="allocateDiv" class="form-group row" style="overflow:visible;display:none;">	
												<div class="col-md-8 column">
												<label for="facultySelected">Faculty List</label>
													<form:select id="facultySelected" path="facultySelected"  class="form-control" multiple="true">
														<form:options items="${activeFacultyNameAndIdList}" />
													</form:select>
												</div>
												<div class="col-md-2 column" style="text-align:center">
													<br/><br/>
													<a href="JavaScript:void(0);" id="btn-add">Add &raquo;</a><br/>
		    										<a href="JavaScript:void(0);" id="btn-remove">&laquo; Remove</a>
    											</div>
    											<div class="col-md-8 column">	
		    										<label for="facultyAllocated">Faculty Allocated</label>
		    										<form:select id="facultyAllocated" path="facultyAllocated"  required="true" class="form-control" multiple="true" size="5">
														<form:options items="${getFacultyAllocatedList}" />
													</form:select>
    									       </div>	
											</div>
											
											<div class="controls" id="controlsId" style="display:none;">
											<button id="submit" name="submit" class="btn btn-primary" formaction="allocateFacultyToRole">Allocate</button>
											<button id="canecl" name="cancel" class="btn btn-default" formnovalidate="formnovalidate" formaction="home">Cancel</button>
											</div>
											
											
										</fieldset>
									</form:form>
									</div>
									</div>
										
							                
							        <%}catch(Exception e){
							        	  }	%>    
							        
									</div>
              								
              						</div>
              				</div>
              		
                            
					</div>
            </div>
            
      
  	
        <jsp:include page="adminCommon/footer.jsp"/>
      
    </body>
    <script>
    	$("#assignByID").change(function(){
    		
    		var assignByValue = $('#assignByID :selected').text();
    		$("#allocateDiv").hide();
    		$("#controlsId").hide();
    		
    		if(assignByValue == 'Exam Month'){
    			$("#examYearAndMonthDiv").show();
    			$("#acadYearAndMonthDiv").hide();
    			
    			$("#examYearId").attr('required',true);
    			$("#examMonthId").attr('required',true);
    			
    			$("#acadYearId").attr('required',false);
    			$("#acadMonthId").attr('required',false);
    			
    			$("#allocateDiv").show();
    			$("#controlsId").show();
    		}
    		if(assignByValue == 'Academic Month'){
    			$("#examYearAndMonthDiv").hide();
    			$("#acadYearAndMonthDiv").show();
    			
    			$("#acadYearId").attr('required',true);
    			$("#acadMonthId").attr('required',true);
    			
    			$("#examYearId").attr('required',false);
    			$("#examMonthId").attr('required',false);
    			
    			$("#allocateDiv").show();
    			$("#controlsId").show();
    		}
    	});
    	
    	 $('#btn-add').click(function(){
 	        $('#facultySelected option:selected').each( function() {
 	                $('#facultyAllocated').append("<option value='"+$(this).val()+"' selected='true'>"+$(this).text()+"</option>");
 	            $(this).remove();
 	        });
 	    });
 	    $('#btn-remove').click(function(){
 	        $('#facultyAllocated option:selected').each( function() {
 	            $('#facultySelected').append("<option value='"+$(this).val()+"'>"+$(this).text()+"</option>");
 	            $(this).remove();
 	        });
 	    });
    </script>
</html>