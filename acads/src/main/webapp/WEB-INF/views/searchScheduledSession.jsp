<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.PersonAcads"%>
<%@page import="com.nmims.beans.Page"%>

<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Search Sessions Scheduled" name="title" />
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
        <div class="row"><legend>Search Sessions Scheduled</legend></div>
        <%@ include file="messages.jsp"%>
		
		<form:form  action="searchScheduledSession" method="post" modelAttribute="searchBean">
			<fieldset>
			<div class="panel-body">
			
			<div class="col-md-6 column">
					<div class="form-group">
						<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control" required="true" 
							itemValue="${searchBean.year}">
							<form:option value="">Select Academic Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" required="true" itemValue="${searchBean.month}">
							<form:option value="">Select Academic Month</form:option>
							<form:option value="Jan">Jan</form:option>
							<form:option value="Jul">Jul</form:option>
						</form:select>
					</div>
					
					<div class="form-group">
            			<select data-id="consumerTypeDataId" id="consumerTypeId" name="consumerTypeId" class="selectConsumerType form-control" >
             			<option disabled selected value="">Select Consumer Type</option>
             				<c:forEach var="consumerType" items="${consumerTypeList}">
             					<c:choose>
					               <c:when test="${consumerType.id == searchBean.consumerTypeId}">
						                <option selected value="<c:out value="${consumerType.id}"/>">
						                	<c:out value="${consumerType.name}"/>
										</option>
					               </c:when>
					               <c:otherwise>
										<option value="<c:out value="${consumerType.id}"/>">
			                              	<c:out value="${consumerType.name}"/>
			                            </option>
			                     	</c:otherwise>
			                     </c:choose>
							</c:forEach>
            			</select>
          			</div>
          			
          			<div class="form-group">
			            <select id="programStructureId" name="programStructureId"  class="selectProgramStructure form-control">
			            	<option disabled selected value="">Select Program Structure</option>
			            </select>
			      	</div>
			      	
			        <div class="form-group">
			        	<select id="programId" name="programId"  class="selectProgram form-control">
			        		<option disabled selected value="">Select Program</option>
			        	</select>
			        </div>
					
					<div class="form-group">
				    	<select id="subjectId" name="subject"  class="selectSubject form-control" >
				       		<option disabled selected value="">Select Subject</option>
				      	</select>
				    </div>
					
			</div>
			
			<div class="col-md-6 column">
			
				<%-- <div class="form-group" style="overflow:visible;">
					<form:select id="subject" path="subject"  class="combobox form-control"   itemValue="${searchBean.subject}">
						<form:option value="">Type OR Select Subject</form:option>
						<form:options items="${subjectList}" />
						<form:option value="Assignment">Assignment</form:option>
					</form:select>
				</div> --%>
				
				<div class="form-group" style="overflow: visible;">
					<form:select id="subjectCode" path="subjectCode" class="combobox form-control" itemValue="${session.subjectCode}"> 
						<form:option value="">Select Subject Code</form:option>
						<c:forEach items="${subjectCodeMap}" var="subjectMap">
							<form:option value="${subjectMap.key}">${subjectMap.key} ( ${subjectMap.value} )</form:option>
						</c:forEach>
					</form:select>
				</div>
				
				<div class="form-group" >
					<form:select id="facultyLocation" path="facultyLocation"  class="form-control"  
						 itemValue="${session.facultyLocation}"> 
						<form:option value="">Select Faculty Location</form:option>
						<form:options items="${locationList}" />
					</form:select>
				</div> 
				
				<div class="form-group">
					<form:input id="date" path="date" type="date" placeholder="Session Date" class="form-control" value="${searchBean.date}" />
				</div>
			
				<div class="form-group">
					<form:select id="day" path="day" type="text" placeholder="Day" class="form-control"  itemValue="${searchBean.day}">
						<form:option value="">Select Day</form:option>
						<form:option value="Monday">Monday</form:option>
						<form:option value="Tuesday">Tuesday</form:option>
						<form:option value="Wednesday">Wednesday</form:option>
						<form:option value="Thursday">Thursday</form:option>
						<form:option value="Friday">Friday</form:option>
						<form:option value="Saturday">Saturday</form:option>
						<form:option value="Sunday">Sunday</form:option>
					</form:select>
				</div>
					
				<div class="form-group">
						<form:input id="sessionName" path="sessionName" type="text" placeholder="Session Name" class="form-control"  value="${searchBean.sessionName}"/>
				</div>
				
				<div class="form-group">
						<form:input id="facultyId" path="facultyId" type="text" placeholder="Faculty ID" class="form-control"  value="${searchBean.facultyId}"/>
				</div>
				
									
				<div class="form-group">
					<label class="control-label" for="submit"></label>
					<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="searchScheduledSession?searchType=distinct">Search</button>
					<button id="submitAll" name="submitAll" class="btn btn-large btn-primary" formaction="searchScheduledSession?searchType=all">Search All</button>
					<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
				</div>
					
			</div>
			
			</div>
			</fieldset>
		</form:form>
		
	<c:choose>
	<c:when test="${rowCount > 0}">

	<legend>&nbsp;Scheduled Sessions<font size="2px"> (${rowCount} Records Found) &nbsp; <a href="downloadScheduledSessions">Download to Excel</a> </font></legend>
	<div class="table-responsive">
	<table class="table table-striped" style="font-size:12px">
			<thead>
			<tr>
				<th>Sr. No.</th>
				<th>Year</th>
				<th>Month</th>
				<th>Consumer Type</th>
		       	<th>Program Structure</th>
		       	<th>Program</th>
				<th>Subject</th>
				<th>Subject Code</th>
				<th>Session</th>
				<th>Date</th>
				<th>Day</th>
				<th>Start Time</th>
				<th>Faculty ID</th>
				<th>Faculty Name</th>
				<!-- <th>Alt Faculty1 ID</th>
				<th>Alt Faculty1 Name</th>
				<th>Alt Faculty2 ID</th>
				<th>Alt Faculty2 Name</th>
				<th>Alt Faculty3 ID</th>
				<th>Alt Faculty3 Name</th> -->
				<th>Faculty1 Location</th>
				<%if(roles.indexOf("Acads Admin") != -1  || roles.indexOf("Portal Admin") != -1){%>
				<th>Actions</th>
				<th>Video Details</th>
				 <%}%> 
				<th>HasModuleId<th>
			</tr>
			</thead>
			<tbody>
			
			<c:forEach var="bean" items="${scheduledSessionList}" varStatus="status">
		        <tr>
		            <td><c:out value="${status.count}" /></td>
		            <td><c:out value="${bean.year}" /></td>
		            <td><c:out value="${bean.month}" /></td>
		            
		            <c:choose>
			        	<c:when test="${bean.count > 1}">
			         		<td colspan="3">
			         		<center>
			         			<a href="javascript:void(0)" data-month="${ bean.month }" 
			         										 data-year="${ bean.year }" 
			         										 data-subject="${ bean.subject }" 
			         										 data-date="${bean.date }" 
			         										 data-startTime="${bean.startTime }"
			         										 data-consumerProgramStructureId="${bean.consumerProgramStructureId} " 
			         										 class="commonLinkbtn">Common file for <c:out value="${ bean.count }" /> programs </a>
			         		</center>
			         		</td>
			        	</c:when>
			         	<c:otherwise>
			         		<td><c:out value="${bean.consumerType}"/></td>
					     	<td><c:out value="${bean.programStructure}"/></td>
					     	<td><c:out value="${bean.program}"/></td>
			         	</c:otherwise>
			       </c:choose>
		            
		            <td nowrap="nowrap"><c:out value="${bean.subject}" /></td>
		            <td><c:out value="${bean.subjectCode}" /></td>
		            <td><c:out value="${bean.sessionName}" /></td>
		            <td><c:out value="${bean.date}" /></td>
					<td><c:out value="${bean.day}" /></td>
					<td><c:out value="${bean.startTime}" /></td>
					<td><c:out value="${bean.facultyId}" /></td>
					<td>${bean.firstName } ${bean.lastName } </td>
					<%-- <td><c:out value="${bean.altFacultyId}" /></td>
					<td>${mapOfFacultyIdAndFacultyRecord[bean.altFacultyId].fullName} </td>
					<td><c:out value="${bean.altFacultyId2}" /></td>
					<td>${mapOfFacultyIdAndFacultyRecord[bean.altFacultyId2].fullName}</td>
					<td><c:out value="${bean.altFacultyId3}" /></td>
					<td>${mapOfFacultyIdAndFacultyRecord[bean.altFacultyId3].fullName}</td> --%>
					<td><c:out value="${bean.facultyLocation}" /></td>
 					<%if(roles.indexOf("Acads Admin") != -1  || roles.indexOf("Portal Admin") != -1){%>
					<td> 
			            <c:url value="editScheduledSession" var="editurl">
						  <c:param name="id" value="${bean.id}" />
						  <c:param name="consumerProgramStructureId" value="${bean.consumerProgramStructureId }"></c:param>
						</c:url>
						<c:url value="deleteScheduledSession" var="deleteurl">
						  <c:param name="id" value="${bean.id}" />
						  <c:param name="consumerProgramStructureId" value="${bean.consumerProgramStructureId }"></c:param>
						</c:url>
						
						<c:url value="sessionCancellationForm" var="sessionCancellation">
						  <c:param name="id" value="${bean.id}" />
						  <c:param name="consumerProgramStructureId" value="${bean.consumerProgramStructureId }"></c:param>
						</c:url>
						
						<%-- <%if(roles.indexOf("Acads Admin") != -1  || roles.indexOf("Portal Admin") != -1){ %> --%>
						
							<a href="${editurl}" title="Edit"><i class="fa fa-pencil-square-o fa-lg"></i></a>&nbsp;
						 	<a href="${deleteurl}" title="Delete" onclick="return confirm('Are you sure you want to delete this record?')"><i class="fa fa-trash-o fa-lg"></i></a> 
						  	<c:choose>
			        			<c:when test="${searchType eq 'distinct'}">
			        				<a href="${sessionCancellation}" title="Cancel Session"><i class="glyphicon glyphicon-remove"></i></a>
			        			</c:when>
			        		</c:choose>
			        		
		            </td>
		            <td>
		            	<a href="/acads/admin/uploadSessionWiseVideoContentForm?id=${bean.id}" target="_blank">
		            		<b>
		            			<i class="fa fa-upload"></i>
		            		</b>
		            	</a>
		            </td>
		             <%}%> 
		            <td><c:out value="${bean.hasModuleId}" /></td>
		            
		        </tr>   
		    </c:forEach>
				
			</tbody>
		</table>
	</div>
	<br>

	</c:when>
	</c:choose>

	<c:url var="firstUrl" value="searchScheduledSessionPage?pageNo=1" />
	<c:url var="lastUrl" value="searchScheduledSessionPage?pageNo=${page.totalPages}" />
	<c:url var="prevUrl" value="searchScheduledSessionPage?pageNo=${page.currentIndex - 1}" />
	<c:url var="nextUrl" value="searchScheduledSessionPage?pageNo=${page.currentIndex + 1}" />
	
	
	<c:choose>
	<c:when test="${page.totalPages > 1}">
	<div align="center">
	    <ul class="pagination">
	        <c:choose>
	            <c:when test="${page.currentIndex == 1}">
	                <li class="disabled"><a href="#">&lt;&lt;</a></li>
	                <li class="disabled"><a href="#">&lt;</a></li>
	            </c:when>
	            <c:otherwise>
	                <li><a href="${firstUrl}">&lt;&lt;</a></li>
	                <li><a href="${prevUrl}">&lt;</a></li>
	            </c:otherwise>
	        </c:choose>
	        <c:forEach var="i" begin="${page.beginIndex}" end="${page.endIndex}">
	            <c:url var="pageUrl" value="searchScheduledSessionPage?pageNo=${i}" />
	            <c:choose>
	                <c:when test="${i == page.currentIndex}">
	                    <li class="active"><a href="${pageUrl}"><c:out value="${i}" /></a></li>
	                </c:when>
	                <c:otherwise>
	                    <li><a href="${pageUrl}"><c:out value="${i}" /></a></li>
	                </c:otherwise>
	            </c:choose>
	        </c:forEach>
	        <c:choose>
	            <c:when test="${page.currentIndex == page.totalPages}">
	                <li class="disabled"><a href="#">&gt;</a></li>
	                <li class="disabled"><a href="#">&gt;&gt;</a></li>
	            </c:when>
	            <c:otherwise>
	                <li><a href="${nextUrl}">&gt;</a></li>
	                <li><a href="${lastUrl}">&gt;&gt;</a></li>
	            </c:otherwise>
	        </c:choose>
	    </ul>
	</div>
	</c:when>
	</c:choose>	
	
	</div>
	</section>
	
	<!-- Modal For multiple Programs-->
     <div id="programModal" class="modal fade" role="dialog">
  		<div class="modal-dialog">

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

	<jsp:include page="footer.jsp" />
	
	
	<script type="text/javascript">
		$(document).ready(function() {
			$('.commonLinkbtn').on('click',function(){
				let subject = $(this).attr('data-subject');
				let year = $(this).attr('data-year');
				let month = $(this).attr('data-month');
				let date = $(this).attr('data-date')
				let startTime = $(this).attr('data-startTime');
				let consumerProgramStructureId = $(this).attr('data-consumerProgramStructureId');
				
				let modalBody = "<center><h4>Loading...</h4></center>";
				let data = {
					'month':month,
					'year':year,
					'subject':subject,
					'date':date,
					'startTime':startTime,
					'consumerProgramStructureId':consumerProgramStructureId
				};
				$.ajax({
					   type : "POST",
					   contentType : "application/json",
					   url : "getCommonSessionProgramsList",   
					   data : JSON.stringify(data),
					   success : function(data) {
						   modalBody = '<div class="table-responsive"> <table class="table"> <thead><td>Acad year</td><td>Acad month</td> <td>Consumer Type</td> <td>Program Structure</td> <td>Program</td> <td>Subject</td> <td>Action</td> </thead><tbody>';
						   for(let i=0;i < data.length;i++){
							   modalBody = modalBody + '<tr><td>'
							   				+ data[i].year +'</td><td>'
							   				+ data[i].month +'</td><td>'
							   				+ data[i].consumerType +'</td><td>'
							   				+ data[i].programStructure +'</td><td>'
							   				+ data[i].program +'</td><td>'
							   				+ data[i].subject +'</td>'
							   				+'<td> <a href="editScheduledSession?id='+ data[i].id 
							   						+'&consumerProgramStructureId='+ data[i].consumerProgramStructureId 
							   						+'" title="Edit"><i class="fa fa-pencil-square-o fa-lg"></i></a>'
							   						+'<a href="deleteScheduledSession?id='+ data[i].id 
							   						+'&consumerProgramStructureId='+ data[i].consumerProgramStructureId 
							   						+'" title="Delete" onclick="return confirm(`Are you sure you want to delete this record?`)"><i class="fa fa-trash-o fa-lg"></i></a></td></tr>';
						   }
						   
						   <c:url value="editScheduledSession" var="editurl">
							  <c:param name="id" value="${bean.id}" />
							  <c:param name="consumerProgramStructureId" value="${bean.consumerProgramStructureId }"></c:param>
							</c:url>
						   
						   modalBody = modalBody + '<tbody></table></div>';
						   $('.modalBody').html(modalBody);
					   },
					   error : function(e) {
						   alert("Please Refresh The Page.")
					   }
				});
				$('.modalBody').html(modalBody);
				//modal-body
				$('#programModal').modal('show');
			});
		});
	</script>
	<jsp:include page="ConsumerProgramStructureCommonDropdown.jsp" />

</body>
</html>