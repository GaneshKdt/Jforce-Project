<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 


<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="jscss.jsp">
<jsp:param value="Session Recordings" name="title" />
</jsp:include>





<style>
.modal-backdrop {
  z-index: -1;
}
</style>
<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
       
       <div class="row"><legend>Session Recordings for ${subject}</legend></div>
		<div class="panel-body">
						
			<div class="col-md-18 column">
			
			<%@ include file="messages.jsp"%>
			
			<c:choose>
			<c:when test="${rowCount > 0}">
			<input type = "hidden" name="subjectCodeId" id="subjectCodeId" value="${subjectCodeId}" />
			<input type = "hidden" name="month" id="month" value="${month}" />
			<input type = "hidden" name="year" id="year" value="${year}" />
			<div class="table-responsive">
			<table class="table table-striped" style="font-size:12px">
								<thead>
									<tr> 
										<th>Sr. No.</th>
										<th>Name</th>
										<th>Description</th>
										<th>Details</th>
									<!-- 	<th>View/Download</th> -->
										
										<%if(roles.indexOf("Acads Admin") != -1){ %>
										<th>Actions</th>
										<%} %>
									</tr>
								</thead>
								<tbody>
								
								<c:forEach var="contentFile" items="${contentList}" varStatus="status">
							        <tr>
							            <td ><c:out value="${status.count}"/></td>
										<td width="20%"><c:out value="${contentFile.name}"/></td>
										<td width="50%"><c:out value="${contentFile.description}"/></td>
										<%if(roles.indexOf("Acads Admin") != -1){ %>
										 	<td>
										 	<c:if test="${ contentFile.countOfProgramsApplicableTo > 0}">
										 		<a href="javascript:void(0)"
									         	   data-contentId="${ contentFile.id }" 
									         	   class="commonLinkbtn">
									         	   Common file for 
									         	   <c:out value="${ contentFile.countOfProgramsApplicableTo }" /> 
									         	   programs 
									         	</a>
										 	</c:if>
								         	</td>
										<%} %>
										
										<td width="10%">
										<c:if test="${not empty contentFile.previewPath}">
										    <a href="downloadFile?filePath=${contentFile.filePath}">Download</a>
										   <a href="#" onClick="window.open('<spring:eval expression="@propertyConfigurer.getProperty('CONTENT_BASE_PATH')" />${contentFile.previewPath}')" />Download</a>
										</c:if>
										<!-- </td>
										
										<td> -->
										<c:if test="${fn:endsWith(contentFile.previewPath, '.pdf') || fn:endsWith(contentFile.previewPath, '.PDF')
										|| fn:endsWith(contentFile.previewPath, '.Pdf')}">
										    / <a href="previewContentForAdmin?previewPath=${contentFile.previewPath}&name=${contentFile.name}&type=PDF" target="_blank">View</a>
										</c:if>
										
										<c:if test="${not empty contentFile.webFileurl}">
											<c:if test="${contentFile.urlType == 'View' || contentFile.urlType == '' || empty contentFile.urlType	}">
										   		<a href="${contentFile.webFileurl}" target="_blank">View</a>
										   </c:if>
										   <c:if test="${contentFile.urlType == 'Download'}">
										   		<a href="${contentFile.webFileurl}" target="_blank">Download</a>
										   </c:if>
										</c:if>
										</td>
										
										<%if(roles.indexOf("Acads Admin") != -1){ %>
										
										<c:url value="deleteContent" var="deleteurl">
										  <c:param name="id" value="${contentFile.id}" />
										  <c:param name="subjectCodeId" value="${subjectCodeId}" />
										   <c:param name="month" value="${month}" />
										  <c:param name="year" value="${year}" />
										</c:url>
										
										<c:url value="editContent" var="editurl">
										  <c:param name="id" value="${contentFile.id}" />
										  <c:param name="subjectCodeId" value="${subjectCodeId}" />
										</c:url>
										
									
										<td>
										<a href="${editurl}" title="Edit"><i class="fa-solid fa-pen-to-square fa-lg"></i></a>&nbsp;
										<a href="${deleteurl}" title="Delete" onclick="return confirm('Are you sure you want to delete this record?')"><i class="fa-regular fa-trash-can fa-lg"></i></a>
										</td>
										<%} %>
							            
							        </tr>   
							    </c:forEach>
									
									
								</tbody>
							</table>
			</div>
			<br>
		
		</c:when>
		</c:choose>
		
		<form  action="editCompany" method="post">
		<div class="control-group">
		<%if(roles.indexOf("Faculty") != -1 ){ %>
			<button id="edit" name="edit" class="btn btn-primary" formaction="viewApplicableSubjectsForFacultyForm">Back To Subject List</button>
		<%} %>
		
		<%if(roles.indexOf("Acads Admin") != -1 || roles.indexOf("Acads Coordinator") != -1  || roles.indexOf("Learning Center") != -1 ){ %> 
			<button id="edit" name="edit" class="btn btn-primary" formaction="viewAllSubjectsForContent">Back To Subject List</button>
		<%} %>
		</div>
		</form>
		</div>
			
		</div><br/>
	</div>
</section>
<!-- Modal  -->
     <div id="myModal" class="modal fade" role="dialog">
  <div class="modal-dialog modal-lg ">

    <!-- Modal content-->
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">&times;</button>
        <h4 class="modal-title">Common Program List</h4>
      </div>
      <div class="modal-body modalBody">
      	
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
      </div>
    </div>

  </div>
</div>
<!-- End of modal -->
</body>

<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.buttons.min.js"></script>



<script type="text/javascript">

$(document).ready (function(){
	$('#dataTable').DataTable({
		destroy: true,
		initComplete: function () {
            this.api().columns().every(function () {
                var column = this;
                var headerText = $(column.header()).text();
                console.log("header :"+headerText);
                if(headerText == "Type")
                {
                   var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
                    .appendTo( $(column.header()))
                    .on('change', function () {
                        var val = $.fn.dataTable.util.escapeRegex(
                            $(this).val()
                        );
 
                        column
                            .search( val ? '^'+val+'$' : '', true, false )
                            .draw();
                    });
 
               		column.data().unique().sort().each( function ( d, j ) {
                   		select.append( '<option value="'+d+'">'+d+'</option>' )
               		});
              	}
	         });
		}
	});

});

	
	$('.commonLinkbtn').on('click',function(){
		let contentId = $(this).attr('data-contentId');
		let subjectCodeId = $("#subjectCodeId").val();
		let month = $("#month").val();
		let year = $("#year").val();
		let modalBody = "<center><h4>Loading...</h4></center>";
		let data = {
			'id':contentId
		};
		$.ajax({
			   type : "POST",
			   contentType : "application/json",
			   url : "/acads/admin/getProgramsListForCommonContent",   
			   data : JSON.stringify(data),
			   success : function(data) {
				   modalBody = '<div class="table-responsive"> <table class="table"> <thead><td>Exam year</td><td>Exam month</td> <td>Consumer Type</td> <td>Program Structure</td> <td>Program</td> <td>Subject</td> <td>Action</td> </thead><tbody>';
				   for(let i=0;i < data.length;i++){
					   modalBody = modalBody + '<tr><td>'
					   							+ data[i].year 
					   							+'</td><td>'
					   							+ data[i].month 
					   							+'</td><td>'
					   							+ data[i].consumerType 
					   							+'</td><td>'
					   							+ data[i].programStructure 
					   							+'</td><td>'
					   							+ data[i].program 
					   							+'</td><td>'
					   							+ data[i].subject 
					   							+'</td><td> '
					   							+' <a href="editSingleContentFromCommonSetup?contentId='+ data[i].id 
					   									+'&consumerTypeId='+ data[i].consumerTypeId
					   									+'&programStructureId='+data[i].programStructureId
					   									+'&programId='+data[i].programId
					   									+'&consumerProgramStructureId='+data[i].consumerProgramStructureId
					   									+'&subjectCodeId='+subjectCodeId
					   									+'&programSemSubjectId='+data[i].programSemSubjectId
					   									+'"  title="Edit"><i class="fa-solid fa-pen-to-square fa-lg"></i></a> | '
					   							+' <a href="deleteSingleContentFromCommonSetup?contentId='+ data[i].id
					   									+'&consumerProgramStructureId='+data[i].consumerProgramStructureId
					   									+'&subjectCodeId='+subjectCodeId
					   									+'&month='+month
					   									+'&year='+year
					   									+'" onclick="return confirm(\'Are you sure you want to delete this record?\')"  title="Delete"><i class="fa-regular fa-trash-can fa-lg"></i></a>  '
					   							
					   							+'</td></tr>';
				   }
				   
				   modalBody = modalBody + '<tbody></table></div>';
				   $('.modalBody').html(modalBody);
			   },
			   error : function(e) {
				   alert("Please Refresh The Page.")
			   }
		});
		$('.modalBody').html(modalBody);
		//modal-body
		$('#myModal').modal('show');
	});	


</script>






<jsp:include page="footer.jsp" />


</html>

 
 <%-- <!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<html lang="en">
    

	
    
    <jsp:include page="common/jscss.jsp">
	<jsp:param value="Session Recordings" name="title"/>
    </jsp:include>
    
    
    
    <body>
    
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
								
										<h2 class="red text-capitalize">Session Recordings for ${subject}</h2>
										<div class="clearfix"></div>
		              					<div class="panel-content-wrapper">
											<%@ include file="common/messages.jsp" %>
											
											<c:if test="${rowCount > 0}">
										
											<div class="table-responsive">
											<table class="table table-striped" style="font-size:12px">
																<thead>
																	<tr> 
																		<th>Sr. No.</th>
																		<th>Name</th>
																		<th>Description</th>
																		<th>Actions</th>
																	<!-- 	<th>View/Download</th> -->
																		
																		<%if(roles.indexOf("Acads Admin") != -1){ %>
																		<th>Actions</th>
																		<%} %>
																	</tr>
																</thead>
																<tbody>
																
																<c:forEach var="contentFile" items="${contentList}" varStatus="status">
															        <tr>
															            <td ><c:out value="${status.count}"/></td>
																		<td width="20%"><c:out value="${contentFile.name}"/></td>
																		<td width="50%"><c:out value="${contentFile.description}"/></td>
																		
																		<td width="10%">
																		<c:if test="${not empty contentFile.previewPath}">
																		    <a href="downloadFile?filePath=${contentFile.filePath}">Download</a>
																		   <a href="#" onClick="window.open('<spring:eval expression="@propertyConfigurer.getProperty('CONTENT_PREVIEW_PATH')" />${contentFile.previewPath}')" />Download</a>
																		</c:if>
																		<!-- </td>
																		
																		<td> -->
																		<c:if test="${fn:endsWith(contentFile.previewPath, '.pdf') || fn:endsWith(contentFile.previewPath, '.PDF')
																		|| fn:endsWith(contentFile.previewPath, '.Pdf')}">
																		    / <a href="previewContent?previewPath=${contentFile.previewPath}&name=${contentFile.name}" target="_blank">View</a>
																		</c:if>
																		
																		<c:if test="${not empty contentFile.webFileurl}">
																			<c:if test="${contentFile.urlType == 'View' || contentFile.urlType == '' || empty contentFile.urlType	}">
																		   		<a href="${contentFile.webFileurl}" target="_blank">View</a>
																		   </c:if>
																		   <c:if test="${contentFile.urlType == 'Download'}">
																		   		<a href="${contentFile.webFileurl}" target="_blank">Download</a>
																		   </c:if>
																		</c:if>
																		</td>
																		
																		<%if(roles.indexOf("Acads Admin") != -1){ %>
																		
																		<c:url value="deleteContent" var="deleteurl">
																		  <c:param name="id" value="${contentFile.id}" />
																		  <c:param name="subject" value="${contentFile.subject}" />
																		</c:url>
																		
																		<c:url value="editContent" var="editurl">
																		  <c:param name="id" value="${contentFile.id}" />
																		</c:url>
																	
																		<td>
																		<a href="${editurl}" title="Edit"><i class="fa fa-pencil-square-o fa-lg"></i></a>&nbsp;
																		<a href="${deleteurl}" title="Delete" onclick="return confirm('Are you sure you want to delete this record?')"><i class="fa fa-trash-o fa-lg"></i></a>
																		</td>
																		<%} %>
															            
															        </tr>   
															    </c:forEach>
																	
																	
																</tbody>
															</table>
											</div>
											<br>
										
										</c:if>
										
										<form  action="editCompany" method="post">
										<div class="control-group">
										<button id="edit" name="edit" class="btn btn-primary" formaction="viewApplicableSubjectsForm">Back To Subject List</button>
										</div>
										</form>
											
										</div>
              								
              						</div>
              				</div>
              		
                            
					</div>
            </div>
        </div>
            
  	
        <jsp:include page="common/footer.jsp"/>
            
		
    </body>
</html> --%>