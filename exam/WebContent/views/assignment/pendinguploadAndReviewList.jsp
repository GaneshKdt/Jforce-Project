
 <!DOCTYPE html>


<html class="no-js">
<!--<![endif]--> 
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>    
<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Pending Upload/Review List" name="title" />
</jsp:include>
<style>
.bgsuccess{color: #fff; 
    background-color: #5cb85c;
    border-color: #4cae4c;
    color: #fff!important;
    }
    .cardstyle{
    padding:1rem;
    }
    .card-body .rotate i {
    color: rgba(20, 20, 20, 0.15);
    position: absolute;
    left: 0;
    left: auto;
    right: 24px; 
    bottom: 3px;
    display: block;
    -webkit-transform: rotate(-44deg);
    -moz-transform: rotate(-44deg);
    -o-transform: rotate(-44deg);
    -ms-transform: rotate(-44deg);
    transform: rotate(-44deg);
}
.dash-count{
	color:white!important;} 
	.dataTables_filter{
	width:135%;}
	.dataTables_paginate.paging_simple_numbers{width: 111%;} 
	
</style>

<link href="assets/css/navtab.css" rel="stylesheet">
<link rel="stylesheet" href="resources_2015/css/dataTables.bootstrap.css"> 
 <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
<body class="inside">
<%@ include file="../adminCommon/header.jsp" %>
	<div class="sz-main-content-wrapper">
	<div class="sz-breadcrumb-wrapper">
			    <div class="container-fluid">
			        <ul class="sz-breadcrumbs">
			        		<li><a href="/exam/">Exam</a></li>
			        		<li><a href="#">Assignment</a></li>
			        		<li><a href="#">Pending Upload/Review List</a></li> 
			        	
			        </ul>
			        <ul class="sz-social-icons">
			            <li><a href="https://www.facebook.com/NMIMSSCE" class="fa-brands fa-facebook-f" target="_blank"></a></li>
			            <li><a href="https://twitter.com/NMIMS_SCE" class="fa-brands fa-twitter" target="_blank"></a></li>
			            <!-- <li><a href="https://plus.google.com/u/0/116325782206816676798/posts" class="icon-google-plus" target="_blank"></a></li> -->
						
			        </ul>
			    </div>
			</div>
		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
	       <div id="sticky-sidebar"> 
				<jsp:include page="../common/left-sidebar.jsp">
					<jsp:param value="Assignment" name="activeMenu" />
				</jsp:include>
			</div>			       				
			<div class="sz-content-wrapper examsPage">
					<%@ include file="../adminCommon/adminInfoBar.jsp" %>
					<div class="sz-content">



				<c:choose> 
				  <c:when test="${fileSet.tabindex == 1}">
				  <c:set var="tab1" value="active"/>
				  	<c:set var="tab2" value=""/>
				  	<c:set var="tab3" value=""/>
				  	<c:set var="tab4" value=""/>
				  </c:when>
				  <c:when test="${fileSet.tabindex == 2}">
				  <c:set var="tab1" value=""/>
				  	<c:set var="tab2" value="active"/>
				  	<c:set var="tab3" value=""/>
				  	<c:set var="tab4" value=""/>
				  </c:when>
				  <c:when test="${fileSet.tabindex == 3}">
				    <c:set var="tab1" value=""/>
				    <c:set var="tab2" value=""/>
				  	<c:set var="tab3" value="active"/>
				  	<c:set var="tab4" value=""/>
				  </c:when>
				  <c:otherwise> 
					  <c:set var="tab1" value=""/>
					  <c:set var="tab2" value=""/>
					  <c:set var="tab3" value=""/>
					  <c:set var="tab4" value="active"/>
				  </c:otherwise>
			   </c:choose>
			   <br>
			   <div class="tab-grp1 tabbable-panel">
					<div class="tabbable-line"> 
									<section class="login">  
					<div class=" customTheme">      
					<div class="container">     
					<%@ include file="../common/messages.jsp"%> 	     
						<div class="row mb-3">
				                <div class="col-xl-2 col-lg-3  col-sm-4 py-1"  >
					                <a href="pendingUploadAndReviewList?tabindex=1">  
					                   <div class="card bg-primary text-white cardstyle  " > 
					                       <div class="card-body bg-primary">
					                        
					                            <div class="rotate">
					                                <i class="fa-solid fa-upload fa-4x" ></i>
					                            </div>
					                            <h5 >Upload Pending</h5> 
					                            <h1 class="dash-count">${fileSet.totalQpNotUploadedCount }</h1> 
					                        </div>
				                    	</div> 
					                </a>
				                </div>
				                <div class="col-xl-2 col-lg-3  col-sm-4 py-1"> 
				                 	<a href="pendingUploadAndReviewList?tabindex=2"> 
					                    <div class="card text-white bgsuccess cardstyle h-100">  
					                        <div class="card-body bgsuccess">   
					                            <div class="rotate"> 
					                                <i class="fa-solid fa-list fa-4x"></i>
					                            </div>
					                            <h5 class="text-uppercase">Review Pending</h5>
					                            <h1 class="dash-count">${fileSet.totalQpNotReviewedCount }</h1>
					                        </div>
					                    </div>
				                    </a>
				                </div>
				                
				            </div>  
						</div>
						<br> <br>    
						<div class="container"> 
						<div class="tab-grp1 tabbable-panel"> 
						<div class="tabbable-line">
					  	<ul class="nav nav-tabs ">
								<li class="${tab1 }">
								<a data-toggle="tab" href="#home">
								<div class="text-center"><i class="material-icons sessionplan-icon">assignment_returned</i><p>Pending Upload List </p></div> 
								</a>
								</li>
								<li class="${tab2 }">
								<a data-toggle="tab" href="#menu1">
								<div class="text-center"><i class="material-icons sessionplan-icon">assignment_late</i><p>Pending Review List</p></div> 
								</a>
								</li>
								<li class="${tab3 }">
								<a data-toggle="tab" href="#menu2">
								<div class="text-center"><i class="material-icons sessionplan-icon">assignment_turned_in</i><p>Pending to Resolve List</p></div> 
								</a>
								</li>
								<li class="${tab4 }">
								<a data-toggle="tab" href="#menu3">
								<div class="text-center"><i class="material-icons sessionplan-icon">assignment_turned_in</i><p>Reviewed List</p></div> 
								</a>
								</li> 	
						</ul>    
						</div> 
						</div> 	  
							<div class="tab-content">
							  <div id="home" class="tab-pane fade in ${tab1 }">
							   
							    	<table class="table table-striped table-hover tables" id="dataTable" style="font-size: 12px">
										<thead>
											<tr>
												<th>Exam Year</th>
												<th>Exam Month</th> 
												<th> Start Date</th>
												<th> End Date</th>
												<th>Subject</th>
												<th>Faculty</th>
											</tr>
										</thead>
										<tbody>
			
											<c:forEach var="assig" items="${fileSet.uploadList}"	varStatus="status">
												<tr>        
													<td><c:out value="${assig.examYear}" /></td>
													<%-- <td><c:out value="${test.testName}" /></td> --%>
													<td><c:out value="${assig.examMonth}" /></td>
													<td><c:out value="${assig.startDate}" /></td>
													<td><c:out value="${assig.endDate}" /></td>
													<td><c:out value="${assig.subject}"></c:out></td>
													<td><c:out value="${assig.faculty}"></c:out>
													(<c:out value="${assig.facultyId}"></c:out>)</td>
												</tr>
											</c:forEach>
			
										</tbody>
									</table>
							  </div>
							  <div id="menu1" class="tab-pane fade in ${tab2 }">
							    <table class="table table-striped table-hover tables" id="dataTable2" style="font-size: 12px">
										<thead>
											<tr>
												<th>Exam Year</th>
												<th>Exam Month</th>  
												<th> Start Date</th>
												<th> End Date</th> 
												<th>Subject</th>
												<th>Reviewer</th>
											</tr>
										</thead>
										<tbody>
			
											<c:forEach var="review" items="${fileSet.reviewList}"	varStatus="status">
												<tr>        
													<td><c:out value="${review.examYear}" /></td>
													<%-- <td><c:out value="${test.testName}" /></td> --%>
													<td><c:out value="${review.examMonth}" /></td>
													<td><c:out value="${review.startDate}" /></td>
													<td><c:out value="${review.endDate}" /></td> 
													<td><c:out value="${review.subject}"></c:out></td>
													<td><c:out value="${review.faculty}"></c:out>
													(<c:out value="${review.facultyId}"></c:out>)</td>
												</tr> 
											</c:forEach> 
											<c:if test="${fileSet.reviewList.size() eq 0}">
												<tr><td colspan="12">No data to show</td></tr>  
											</c:if>
										</tbody>
									</table>
							  </div>
							  <div id="menu2" class="tab-pane fade in ${tab3 }">
							    <table class="table table-bordered table-hover tables" id="dataTable2" style="font-size: 12px">
										<thead>
											<tr> 
												<th>Exam Year</th>
												<th>Exam Month</th>
												<th> Start Date</th>
												<th> End Date</th>
												<th>Subject</th>
												<th>Faculty</th>  
												<th>Feedback</th>
												<th>Overall Remark</th>  
											</tr>
										</thead>
										<tbody> 
											<c:forEach var="resolution" items="${fileSet.resolutionList}"	varStatus="status">
												<tr class="resolution_tr">        
													<td class="examYear"><c:out value="${resolution.examYear}" /></td>   
													<td class="examMonth"><c:out value="${resolution.examMonth}" /></td> 
													<td><c:out value="${resolution.startDate}" /></td> 
													<td><c:out value="${resolution.endDate}" /></td>
													<td><c:out value="${resolution.subject}"></c:out></td>
													<td><c:out value="${resolution.faculty}"></c:out> (<c:out value="${resolution.facultyId}"></c:out>)
													<td><c:out value="${resolution.feedback}"></c:out></td>   
													<td> 
													<div class=" resolution_remark" style=" padding: 18px 0px;"><c:out value="${resolution.remark}"></c:out></div>
													<c:if test="${empty resolution.remark}"><p style="color:grey">no remarks added  </p></c:if>
													<input type="hidden" class="examYear1"   value="${resolution.examYear }"/>
													<input type="hidden" class="examMonth1"   value="${resolution.examMonth }"/> 
													<input type="hidden" class="pss_id1"   value="${resolution.pss_id }"/>  
													<input type="hidden" class="tabindex" value="3"/>
													<a data-toggle="modal" style="padding:2px 4px" class="btn btn-sm btn-danger openModal pull-right"  data-target="#myModal" ><i class="fa-solid fa-pen-to-square" aria-hidden="true"></i>
													</a> 
													</td> 
												</tr>  
											</c:forEach> 
											<c:if test="${fileSet.resolutionList.size() eq 0}">
												<tr><td colspan="12">No data to show</td></tr>  
											</c:if>
										</tbody>
									</table>
							  </div>
							  <div id="menu3" class="tab-pane fade in ${tab4 }">
							  <div class="response_msg "></div>  
							    <table class="table table-striped table-hover tables"  id="dataTable3" style="font-size: 12px">
										<thead>
											<tr> 
												<th>Exam Year</th>
												<th>Exam Month</th>
												<th> Start Date</th>
												<th> End Date</th>
												<th>Subject</th>
												<th>Reviewer</th>  
												<th>Feedback</th>
												<th>Overall Remark</th>
												<th>Action</th>
												<th>Status</th> 
												<th>Select</th>
											</tr>
										</thead>
										<tbody>
			
											<c:forEach var="completed" items="${fileSet.completedList}"	varStatus="status">
												<tr>        
													<td ><c:out value="${completed.examYear}" /></td>
													<%-- <td><c:out value="${test.testName}" /></td> --%>
													<td><c:out value="${completed.examMonth}" /></td>
													<td><c:out value="${completed.startDate}" /></td>
													<td><c:out value="${completed.endDate}" /></td>
													<td><c:out value="${completed.subject}"></c:out></td>
													<td><c:out value="${completed.faculty}"></c:out>(<c:out value="${completed.facultyId}"></c:out>)
													<td><c:out value="${completed.feedback}"></c:out></td>  
													<td style="min-width: 100px!important;"> 
													<div class="resolution_remark" style=" padding: 18px 0px;"><c:out value="${completed.remark}"></c:out></div> 
													<c:if test="${empty completed.remark}"><p style="color:grey">no remarks added  </p></c:if>
													<%-- <input type="hidden" class="pss_id" path="pss_id" value="${resolution.pss_id }"/> --%>
													<a data-toggle="modal" style="padding:2px 4px" class="btn btn-sm btn-danger openModal pull-right"  data-target="#myModal" ><i class="fa-solid fa-pen-to-square" aria-hidden="true"></i>
													</a> 
													</td>   
													<td><b class="editBtn"><button class="btn btn-sm btn-primary " style="max-width: 90px!important;font-size: 11px!important;">Set Date</button> </b> 
														<form modelAttribute="formBean" method="post" class="editForm" style="display:none;" >
																<div class="row ">           
																	<div class="col-md-12 form-group">   
																	    <label>StartDate:</label>      
																		<input type="datetime-local" required="required" class="form-control startDate" value="${fn:replace(completed.studentStartDate, ' ', 'T')}"/>   
																	</div>    
																</div>
																<div class="row ">            
																	<div class="col-md-12 form-group">   
																	    <label>EndDate:</label>           
																		<input type="datetime-local" required="required" class="form-control endDate" value="${fn:replace(completed.studentEndDate, ' ', 'T')}"/>   
																	</div>    
																</div> 
														  		<div class="row ">            
																	<div class="col-md-12 form-group">
																	<button class="btn btn-sm btn-primary submitBtn">Save</button> 
																	<button class="btn btn-sm btn-danger cancelBtn">Cancel</button>  
																	</div>      
																</div> 
														</form>				
													</td>  
													<td style="width:10rem;" class="approve_status">       
													<c:choose>
														  <c:when test="${completed.approve == 'Y'}">
														  <span style="color: #5cb85c;" ><i class=" fa-solid fa-circle-check fa-1x" ></i> Approved</span>
														  </c:when>            
														  <c:otherwise>  
														   <span style="color: #c72127;" ><i class=" fa-solid fa-circle-xmark fa-1x" ></i> Not Approved</span>
														 
														  </c:otherwise> 
												   	</c:choose>
													</td> 
													<td>     
													<input type="hidden" class="examYear1" value="${completed.examYear}"/>
													 <input type="hidden" class="examMonth1" value="${completed.examMonth}"/>
													 <input type="hidden" class="pss_id1"  value="${completed.pss_id}"/>
													 <input type="hidden" class="subject" value="${completed.subject}"/>
													 <input type="hidden" class="studentStartDate" value="${completed.studentStartDate}"/>
													 <input type="hidden" class="studentEndDate" value="${completed.studentEndDate}"/>
													 <input type="hidden" class="filePath" value="${completed.filePath}"/>
													 <input type="hidden" class="questionFilePreviewPath" value="${completed.questionFilePreviewPath}"/>
													 <input type="hidden" class="consumerProgramStructureId" value="${completed.consumerProgramStructureId}"/>
													 <input type="hidden" class="dueDate" value="${completed.dueDate}"/>
													 <input type="hidden" class="qpId" value="${completed.qpId}"/> 
													 <input type="hidden" class="tabindex" value="4"/>
													<input type="checkbox"  class="form-control chkbx"  /></td>
												      
												</tr> 
											</c:forEach>
											<c:if test="${fileSet.completedList.size() eq 0}">
												<tr><td colspan="12">No data to show</td></tr>  
											</c:if>
										</tbody>
									</table>
									       
			                        <c:if test="${fileSet.completedList.size() gt 0}">  
			                        <button class="btn btn-primary pull-right adm_approve">approve</button>
									</c:if> 
			                         <div class="preloader"></div>          
							  </div> 
							</div>  
						</div>
			
						<br>		
					</div>
				</section>	
					</div>
				</div>

					</div>
			   
			</div>
		</div>
	</div>
	
	<div id="myModal" class="modal fade" role="dialog">
	  <div class="modal-dialog">
	
	    <!-- Modal content-->
	    <div class="modal-content">
	      <div class="modal-header">
	        <button type="button" class="close" data-dismiss="modal">&times;</button>
	        <h4 class="modal-title">Overall Remark</h4>
	      </div>
	      <div class="modal-body"> 
	       <form:form modelAttribute="feedbackbean" method="post"  action="sendOverallRemark">
			   <form:hidden path="examMonth" id="exam_month"   />  
		       <form:hidden path="examYear" id="exam_year"  /> 
		       <form:hidden path="pss_id" id="pss" /> 
		       <form:hidden path="tabindex" id="tabindex" />
		       <form:textarea id="remark" required="required" class="form-control" path="remark"  />
		        <br>
		       <button style="width:60px;padding: 10px 0!important;" class="btn btn-primary" >Send</button>
		       
		   </form:form>	   				       
	      </div> 
	    </div>
	
	  </div>
    </div> 

	<%-- <%@ include file="header.jsp"%> --%>


	<jsp:include page="../adminCommon/footer.jsp" /> 
   <script
              src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
       <script src="resources_2015/js/vendor/dataTables.bootstrap.js"></script>
       <script
              src="resources_2015/js/vendor/dataTables.buttons.min.js"></script>

<script type="text/javascript"> 
$(document).ready (function(){
    $('#dataTable').DataTable();

});
$(document).ready (function(){
    $('#dataTable2').DataTable();  

});
$(document).ready (function(){
    $('#dataTable3').DataTable();  
   
});
$('.editBtn').click(function(e) {
	$(this).closest("td").find('.editForm').show();  
	$(this).hide();
});
$('.cancelBtn').click(function(e) {
	e.preventDefault();   
	$(this).closest("td").find('.editForm').hide();  
	$(this).closest("td").find('.editBtn').show();  
});
$('.submitBtn').click(function(e) {
	e.preventDefault(); 
	var parent =$(this).closest("tr"); 
	var startDate=$(parent).find(".startDate").val().replace("T", " ");
	var endDate=$(parent).find(".endDate").val().replace("T", " ");
	var dueDate=$(parent).find(".dueDate").val().replace("T", " "); 
   	var item = {
   			examYear: $(parent).find(".examYear1").val(),
   			examMonth:$(parent).find(".examMonth1").val(), 
   			pss_id:$(parent).find(".pss_id1").val(),
   			studentStartDate:startDate,
   			studentEndDate:endDate,
   			dueDate:dueDate
			}    
   	console.log(item);   
	 $.ajax({  
         url:"updateQpDateForStudent",
         type: 'POST',
         data:  JSON.stringify(item),                 
         dataType: "html",  
         contentType: 'application/json',
         mimeType: 'application/json',
         success:function(response){  
        	 var data = $.parseJSON(response);   
        	 if(data.status=="error"){ 
      			$(".response_msg").html('<div class="alert alert-danger"><span class="success_msg" style="color:#c72127;">'+data.message+'</span></div> ');  
			    return false; 
        	 }  
        	 $(parent).find(".studentStartDate").val(startDate);
        	 $(parent).find(".studentEndDate").val(endDate);
        	 $(".response_msg").html('<div class="alert alert-success"><span class="success_msg" style="color:#5cb85c;">Date Updated Successfully</span></div> '); 
 		},    
 		error:function(){  
 			$(".response_msg").html('<div class="alert alert-danger"><span class="success_msg" style="color:#c72127;">Failed to Update</span></div> ');  
 		}  

     });
});
   
$('.adm_approve').click(function(e) { 
	var qpIds = [];  
	var fileset = [];  
	var parent_tr_array = []; 
	var button = $(this); 
	$('.chkbx').each(function () {
		var parent_tr =$(this).closest("tr"); 
		var parent =$(this).closest("td"); 
		if (this.checked) { 
			if(($(parent).find(".studentStartDate").val()=="") ||  
			($(parent).find(".studentEndDate").val()=="")){
				$(".response_msg").html('<div class="alert alert-danger"><span class="success_msg" style="color:#c72127;">Please add dates</span></div> ');  
				return false;
				} 
			jQuery(button).parent().find(".preloader").html('<div id="preloader" class="pull-right" ><img id="theImg" style="width: 40px;"  src="assets/images/Widgeloading.gif" /></div>');  
			   
			var item = {};  
			parent_tr_array.push(parent_tr);
			item ["pss_id"] = $(parent).find(".pss_id1").val(); 
			item ["year"] = $(parent).find(".examYear1").val();
			item ["month"] = $(parent).find(".examMonth1").val();
			item ["subject"] = $(parent).find(".subject").val();
			fileset.push(item);
	    }  
	});    
	
	if(fileset.length>0){ 
	 $.ajax({

         url:"adminApproveQp",

         type: 'POST',

         data:  JSON.stringify({

        	 'fileset' : fileset  
 
         }),             

         dataType: "html",          

         contentType: 'application/json',

         mimeType: 'application/json',

         success:function(response){ 
        	 jQuery("#preloader").remove();
        	 var data = JSON.parse(response);                                           
        	 if(data.status=="success"){    
        		 for (i = 0; i < data.listOfStringData.length; i++) {  
        			 window.open('<spring:eval expression="@propertyConfigurer.getProperty('ASSIGNMENT_QUESTION_PREVIEW_PATH')" />'+data.listOfStringData[i], '_blank');
                 }        
        		 for (i = 0; i < parent_tr_array.length; i++) {  
        			 $(parent_tr_array[i]).find(".approve_status").html('<span style="color: #5cb85c;" ><i class="fa-solid fa-circle-check fa-1x" ></i><span style="color: #5cb85c;" > Approved</span>'); 
        		 }                                  
        		 $(".response_msg").html('<div class="alert alert-success"><span class="success_msg" style="color:#5cb85c;">Approved Successfully</span></div> '); 
        	 }else{
        		 $(".response_msg").html('<div class="alert alert-danger"><span class="success_msg" style="color:#c72127;">Failed to Approve</span></div> ');  
        	 } 
        	   
 		},  
 		error:function(){
 			$(".response_msg").html('<div class="alert alert-danger"><span class="success_msg" style="color:#c72127;">Error in Approve</span></div> ');  
 		}  

     });
	} 
});
$('.openModal').click(function() {      
	 var tr =$(this).closest("tr");    
	 $('#exam_year').val(tr.find(".examYear1").val());  
	 $('#exam_month').val(tr.find(".examMonth1").val());  
	 $('#pss').val(tr.find(".pss_id1").val());        
	 $('#remark').val(tr.find(".resolution_remark").html()); 
	 $('#tabindex').val(tr.find(".tabindex").val());  
});  
</script>

</body>
</html>
 