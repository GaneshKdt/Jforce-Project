
 <!DOCTYPE html>


<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>     
<jsp:include page="../jscss.jsp">
	<jsp:param value="Review Assignment Questions" name="title" />
</jsp:include> 

<style>
.btn-disabled{
cursor: not-allowed;
    pointer-events: none;
    opacity: 0.65;
    filter: alpha(opacity=65);
    -webkit-box-shadow: none;
    box-shadow: none;   
}
</style>
<link rel="stylesheet" href="resources_2015/css/dataTables.bootstrap.css">  		

<body class="inside">

	<%-- <%@ include file="header.jsp"%> --%>

	<section class="content-container login">
		<div class="container-fluid customTheme"">
			
			<div class="row"><legend>Review Assignment Questions</legend></div>
			<%@ include file="../common/messages.jsp"%>
			<div class="pull-left">
			<a href="/studentportal/home" ><u><i class="fa-solid fa-arrow-left" aria-hidden="true"></i> back</u></a> 
			</div> </br></br>    
			<div class="panel-body clearfix" >   
			<table class="table table-striped table-hover"  id="dataTable"  style="font-size:12px">
						<thead>
							<tr> 
								<th>Exam Year</th>
								<th>Exam Month</th>
								<th>Subject</th>
								<th>Due Date</th>
								<th>Preview</th> 
								<th>Feedback</th>
								<th>Overall Remark</th>
								<th>Action</th> 
							</tr>
						</thead>
						<tbody>
						 <c:if test="${beanList.size() > 0}">  
	    					<c:forEach var="bean" items="${beanList}">
	    					<tr>
								<form:form modelAttribute="filesSet" class="review_form" method="post" action="qpApproveOrReject">
								   <form:hidden class="examYear" path="examYear" value="${bean.examYear }"   />
							       <form:hidden class="examMonth" path="examMonth" value="${bean.examMonth }"  />
							       <form:hidden path="startDate" value="${bean.startDate }"/>
							       <form:hidden path="endDate" value="${bean.endDate }"/> 
							       <form:hidden class="pss_id" path="pss_id" value="${bean.pss_id }"/> 
							       <form:hidden class="approve" path="approve" value="${bean.approve }"/>  
							       <input type="hidden"  name="assignmentFiles[0].subject" value="${bean.subject }"  />
							      	
							            <td>${bean.examYear }</td>
							            <td>${bean.examMonth }</td>
										<td>${bean.subject }</td> 
										<td style="color:#c72127;min-width:95px!important;">     
										<i class="fa-regular fa-clock " aria-hidden="true"></i> 
										<fmt:parseDate value = "${bean.dueDate }" var = "parsedDate" pattern = "yyyy-MM-dd HH:mm:ss" />
											<fmt:formatDate pattern="yyyy-MM-dd" value="${parsedDate}" var="formatedDueDate" />
											<c:out value = "${formatedDueDate}" />  
										</td> 
										<td>
										   <c:choose>
											  <c:when test="${bean.uploadStatus eq 'Y'}">
											      <a href="showAsgQns?qpId=${bean.qpId}&role=reviewer">Preview</a> 
											  </c:when>
											  <c:otherwise>
											  <button class="btn btn-sm btn-light" disabled >upload await</button>
										      </c:otherwise> 
										   </c:choose>
										</td>
										<td>
										 
											<c:choose>
											  <c:when test="${bean.uploadStatus eq 'Y'}"> 
											  <input type="hidden" value="${bean.feedback}" class="feedbackMsg"/>
											  <a    data-toggle="modal" class="btn btn-sm btn-primary openModal"  data-target="#myModal" >
											   <c:choose>
													<c:when test="${bean.feedback ne null}">
													Edit feedback
													</c:when> 
													<c:otherwise>
													Add feedback
													</c:otherwise> 
												</c:choose>
											  </a></c:when>
											  <c:otherwise>     
											  <button class="btn btn-sm btn-light" disabled >upload await</button>
										      </c:otherwise> 
										   </c:choose>
										</td>
										    
										<td>${bean.remark }</td>
										<td>   
										<c:choose>  
										<c:when test="${bean.uploadStatus eq 'Y'}">   
										<c:choose> 
											  <c:when test="${bean.reviewStatus !='Y'}">
											  <button data="Y" class="btn btn-sm btn-success actionbtn" >Approve</button>
       									
											  </c:when>
											  <c:otherwise>
											  <button data="N" class="btn btn-sm btn-danger actionbtn" >Reject</button>
											   </c:otherwise> 
										</c:choose>  
										 </c:when>
										 <c:otherwise><button class="btn btn-sm btn-success actionbtn btn-disabled">Approve</button></c:otherwise>
										 </c:choose> 
										</td>       
							        
							    </form:form>
							    </tr>   
						    </c:forEach>	 
						</c:if>     
					    <c:if test="${beanList.size() eq 0}">     
					         <tr><td colspan=12 style="text-align:center;"><p>No qp allocated </p></td></tr>   
					    </c:if>   

							
						</tbody>
					</table>
				</div>
			</div>
			
		</div>
	</section>
	<div style="min-height:25rem;">
	
	</div>
<jsp:include page="../footer.jsp" />

<div id="myModal" class="modal fade" role="dialog">
  <div class="modal-dialog">

    <!-- Modal content-->
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">&times;</button>
        <h4 class="modal-title">Feedback</h4>
      </div>
      <div class="modal-body"> 
       <form:form modelAttribute="feedbackbean" method="post"	 action="sendQpFeedback">
		   <form:hidden path="examMonth" id="exam_month"   />  
	       <form:hidden path="examYear" id="exam_year"  /> 
	       <form:hidden path="pss_id" id="pss" />
	       <form:textarea required="required" class="form-control " id="feedbackEditor" path="feedback"  />
	       </br>    
	       <button class="btn btn-sm btn-primary" >Save</button>
	   </form:form>	   				       
      </div> 
    </div>

  </div>
</div> 
</body>
</html>
   <script
              src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
       <script src="resources_2015/js/vendor/dataTables.bootstrap.js"></script>
       <script
              src="resources_2015/js/vendor/dataTables.buttons.min.js"></script> 
 <script>
 $(document).ready (function(){
	    $('#dataTable').DataTable();  
	   
	});
 $('.openModal').click(function() {      
	 var tr =$(this).closest("tr"); 
	 var feedbackMsg = tr.find(".feedbackMsg").val();
	 $('#feedbackEditor').val(feedbackMsg); 
	 $('#exam_year').val(tr.find(".examYear").val());  
	 $('#exam_month').val(tr.find(".examMonth").val());
	 $('#pss').val(tr.find(".pss_id").val());
 });  
 function modalFill(examyear,exammonth,pssid){ 
	 alert("test");        
	 $('#exam_year').val(examyear);
	 $('#exam_month').val(exammonth);
	 $('#pss').val(pssid);
 }
 $('.actionbtn').click(function(e) {
	 e.preventDefault(); 	
	 var val = $(this).attr("data");   
	 var tr = $(this).closest('tr');
	 var approve = tr.find('.approve');  
	 approve.val(val);   
	 tr.find('.review_form').submit();  
 }); 
 
 </script>