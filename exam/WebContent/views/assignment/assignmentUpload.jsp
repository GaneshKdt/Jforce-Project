
 <!DOCTYPE html>


<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %> 
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>   
<jsp:include page="../jscss.jsp">
	<jsp:param value="Upload Assignment Files" name="title" />
</jsp:include>
<link rel="stylesheet" href="resources_2015/css/dataTables.bootstrap.css"> 
<style>
.tag-danger{
	background: #c72127; 
    border-radius: 3px 0 0 3px;
    color: #fff;   
    display: inline-block;
    height: 26px;
    line-height: 26px;
    padding: 0 9px 0 12px;            
    position: relative;        
    text-decoration: none;
    -webkit-transition: color 0.2s;
}
.tag {
	background: #5cb85c;
    border-radius: 3px 0 0 3px;
    color: #fff;   
    display: inline-block;
    height: 26px;
    line-height: 26px;
    padding: 0 9px 0 12px;            
    position: relative;        
    text-decoration: none;
    -webkit-transition: color 0.2s; 
} 
.tag-danger:after{
  border-bottom: 13px solid transparent;
  border-left: 10px solid #c72127;                
  border-top: 13px solid transparent;
  content: ''; 
  position: absolute;
  right: 0; 
  top: 0;     
  border-color: transparent transparent transparent #c72127 ;
  position: absolute;
  right: -10px;      
  display: inline-block;
}
.tag:after {
  border-bottom: 13px solid transparent;
  border-left: 10px solid #5cb85c;               
  border-top: 13px solid transparent;
  content: ''; 
  position: absolute;
  right: 0; 
  top: 0;     
  border-color: transparent transparent transparent #5cb85c ;
  position: absolute;
  right: -10px;      
  display: inline-block;
} 
.pending{   
	background: #c72127;     
}  
.pending:after{
	border-color: transparent transparent transparent #c72127 ;  
}    
input[type="file"] {
    display: none;
}   
</style>
<body class="inside">

	<%-- <%@ include file="header.jsp"%> --%>

	<section class="content-container login">
		<div class="container-fluid customTheme"">
			
			<div class="row"><legend>Upload Assignment Question</legend></div>
			<%@ include file="../common/messages.jsp"%>
			<div class="pull-left">
			<a href="/studentportal/home" ><u><i class="fa-solid fa-arrow-left " aria-hidden="true"></i> back</u></a> 
			</div> </br></br>   
			<div class="panel-body clearfix " >  
			<table class="table table-striped table-hover" id="dataTable" style="font-size:12px">
						<thead>
							<tr> 
								<th>Exam Year</th>
								<th>Exam Month</th>
								<th>Subject</th>
								<th>Due Date</th> 
								<th>Status</th>  
								<th>Feedback</th> 
								<th>Overall Remark</th>
								<th>Action</th>
								<th>Preview</th>
							</tr>
						</thead>
						<tbody>
						<c:if test="${beanList.size() > 0}">
							<c:forEach var="bean" items="${beanList}">
								
							       <%-- <form:hidden path="subject" value="${bean.subject }"/> --%> 
							        <tr>     
							            <td>${bean.examYear }</td>
							            <td>${bean.examMonth }</td>
										<td > ${bean.subject } </td>             
										<td style="color:#c72127;min-width:95px!important;">     
										<i class="fa-regular fa-clock " aria-hidden="true"></i> 
										<fmt:parseDate value = "${bean.dueDate }" var = "parsedDate" pattern = "yyyy-MM-dd HH:mm:ss" />
											<fmt:formatDate pattern="yyyy-MM-dd" value="${parsedDate}" var="formatedDueDate" />
											<c:out value = "${formatedDueDate}" />       
										</td> 
										<td style="width: 120px!important;">  
											<c:choose>   
												  
												<c:when test="${bean.reviewStatus == 'N' and (!empty(bean.feedback))}">
												<span class="tag-danger">Resolve</span>  
												</c:when> 
												<c:when test="${bean.uploadStatus == 'Y'}">
												<span class="tag">uploaded</span> 
												</c:when>     
												<c:otherwise> 
												<span class="tag pending">pending</span>          
												</c:otherwise>
											</c:choose>   
											<%-- <c:if test=" test="${bean.reviewStatus == 'N' and (!empty(bean.feedback)) }">
												<span class="tag-danger">Resolve</span>    
											</c:if>  --%>  
										</td>
										<!-- <td>
										<label for="fileData" class="custom-file-upload">
   								 		<a href="createQpForm" class="btn btn-sm btn-danger">Upload</a> 
										</label>
										<input id="fileData" type="file" name="assignmentFiles[0].fileData"/> 
										</td> --> 
										<td >${bean.feedback } </br> </td>   
										<td >${bean.remark } </br> </td> 
										<td >
										<form:form modelAttribute="filesSet" method="post"	enctype="multipart/form-data" action="createQpForm">
										   <form:hidden path="year" value="${bean.examYear }"   />
									       <form:hidden path="month" value="${bean.examMonth }"  />
									       <form:hidden path="pss_id" value="${bean.pss_id }"/> 
									       <form:hidden path="facultyId" value="${bean.facultyId }"/>
									       <form:hidden path="qpId" value="${bean.qpId }"/>    
									       <input type="hidden"  name="subject" value="${bean.subject }"  /> 
										<c:choose > 
										<c:when test="${bean.uploadStatus eq 'Y'}">
										<button id="submit" name="submit" class="btn btn-sm btn-primary float-left"
											     formaction="createQpForm">Edit</button> 
										</c:when>  
										
										<c:otherwise>
										<button id="submit" name="submit" class="btn btn-sm btn-primary float-left"
											     formaction="createQpForm">Create</button> 
										</c:otherwise>
										</c:choose >
										</form:form>	  
										</td>
										<td >
										<c:choose >
											<c:when test="${bean.uploadStatus eq 'Y'}">
											<a href="showAsgQns?qpId=${bean.qpId}&role=creator">Preview</a>
											</c:when>
											<c:otherwise>
											<button class="btn btn-sm btn-light" disabled >upload await</button>
											</c:otherwise> 
										</c:choose>
										</td> 
										<%-- <td>
										   <c:choose>
											  <c:when test="${bean.uploadStatus eq 'Y'}">
											  <form:form modelAttribute="filesSet" method="post" action="downloadAssignmentQpFile">
									   			   
												  <button  class="btn btn-sm btn-primary"
												     formaction="createQpForm">Edit</button>  
											  </form:form>
										      </c:when>
											  <c:otherwise>
											  <button class="btn btn-sm btn-light" disabled >upload await</button>
										      </c:otherwise> 
										   </c:choose>
										</td> --%>
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
   <script
              src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
       <script src="resources_2015/js/vendor/dataTables.bootstrap.js"></script>
       <script
              src="resources_2015/js/vendor/dataTables.buttons.min.js"></script>
<script> 
$(document).ready (function(){
    $('#dataTable').DataTable();  
   
}); 
</script> 
</body>
</html>

 