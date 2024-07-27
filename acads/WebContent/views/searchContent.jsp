<!DOCTYPE html>
<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Search Learning Resources" name="title" />
</jsp:include>

<style>
	.modal-backdrop {
  		z-index: -1;
	}
</style>

<body class="inside">
	<%@ include file="header.jsp"%>
	<% String searchType= (String)request.getSession().getAttribute("searchType");%>
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Search Learning Resources</legend></div>
        <%@ include file="messages.jsp"%>
        	<form:form  action="searchContent" method="post" modelAttribute="searchBean">
        		<fieldset>
					<div class="panel-body">
						<div class="col-md-18 column">
							<div class="row">
								<div class="col-md-4 column">
							<div class="form-group">
								<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control" required="true" 
									itemValue="${searchBean.year}">
									<form:option value="">Select Academic Year</form:option>
									<form:options items="${yearList}" />
								</form:select>
							</div>
							</div>
							<div class="col-md-4 column">
							<div class="form-group">
								<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" required="true" 
									itemValue="${searchBean.month}">
									<form:option value="">Select Academic Month</form:option>
									<form:option value="Jan">Jan</form:option>
									<form:option value="Jul">Jul</form:option>
								</form:select>
							</div>
							</div>
							</div>
						</div>	
					<div class="col-md-18 column">
							<div class="row">
							<div class="col-md-4 column">
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
		          			</div>
		          			<div class="col-md-4 column">
		          			<div class="form-group">
					            <select id="programStructureId" name="programStructureId"  class="selectProgramStructure form-control">
					            	<option disabled selected value="">Select Program Structure</option>
					            </select>
					      	</div>
					      	</div>
					     </div>
					     <div class="row">
					     	<div class="col-md-4 column">
					      	
					        <div class="form-group">
					        	<select id="programId" name="programId"  class="selectProgram form-control">
					        		<option disabled selected value="">Select Program</option>
					        	</select>
					        </div>
							</div>
							<div class="col-md-4 column">
							<div class="form-group">
						    	<select id="subject" name="subject"  class="selectSubject form-control" >
						       		<option disabled selected value="">Select Subject</option>
						      	</select>
						    </div>
							</div>
						</div>
						
						<div class="row">
							<div class="col-md-8 column">
									<div class="form-group" style="overflow:visible;">
											<form:select id="subjectCodeId" path="subjectCodeId" class="combobox form-control" itemValue="${filesSet.subjectCodeId}"> 
												<form:option value="">Select Subject Code</form:option>
												<c:forEach items="${subjectcodes}" var="element">
													<form:option value="${element.subjectCodeId}">${element.subjectcode} ( ${element.subjectName} )</form:option>
												</c:forEach>
											</form:select>
									</div>
							</div>
						</div>
							<div class="form-group">
								<label class="control-label" for="submit"></label>
								<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="searchContent?searchType=distinct">Search</button>
								<button id="submitAll" name="submitAll" class="btn btn-large btn-primary" formaction="searchContent?searchType=all">Search All</button>
								<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
							</div>
							
						</div>
        			
        		</fieldset>
        	</form:form>
        	
        	<c:choose>
				<c:when test="${rowCount > 0}">
					<legend>&nbsp;Learning Resources  <font size="2px"> (${rowCount} Records Found) </font></legend>
				<div class="column">
					<div class="table-responsive">
						<table class="table table-striped  table-hover dataTables" style="font-size:12px" id="datafill">
							<thead>
								<tr>
									<th>Sr. No.</th>
									<th>Subject</th>
									<th>SubjectCode</th>
									<th>Name</th>
									<th>Description</th>
									<th>Consumer Type</th>
							       	<th>Program Structure</th>
							       	<th>Program</th>
									<th>Year</th>
									<th>Month</th>
									<th>Download</th>
									<th>Actions</th>
								</tr>
							</thead>
							
							<tbody>
			
							<c:forEach var="bean" items="${resourcesContentList}" varStatus="status">
								<tr>
						            <td><c:out value="${status.count}" /></td>
						            <td nowrap="nowrap"><c:out value="${bean.subject}" /></td>
						            <td nowrap="nowrap"><c:out value="${bean.subjectcode}" /></td>
						            <td width="20%"><c:out value="${bean.name }" /> </td>
						            <td width="40%"><c:out value="${bean.description }" /> </td>
						             
						             <c:choose>
							        	<c:when test="${bean.count > 1}">
							         		<td colspan="3"><center><a href="javascript:void(0)" data-month="${ bean.month }" data-year="${ bean.year }" data-subject="${ bean.subject  }" data-filePath="${bean.filePath }" data-consumerProgramStructureId="${bean.consumerProgramStructureId} " class="commonLinkbtn">Common file for <c:out value="${ bean.count }" /> programs </a></center></td>
							        		<td style="display: none"></td>
							        		<td style="display: none"></td>
							        	</c:when>
							         	<c:otherwise>
							         		<td><c:out value="${bean.consumerType}"/></td>
									     	<td><c:out value="${bean.programStructure}"/></td>
									     	<td><c:out value="${bean.program}"/></td>
							         	</c:otherwise>
							       </c:choose>
									
									<td><c:out value="${bean.year}" /></td>
						            <td><c:out value="${bean.month}" /></td>
									
									<td>
										<c:if test="${not empty bean.previewPath}">
										   <%--  <a target="_blank" href="downloadFile?filePath=${bean.filePath}">Download</a> /  
										   	<a target="_blank" href="<spring:eval expression="@propertyConfigurer.getProperty('CONTENT_BASE_PATH')" />${bean.previewPath}">View</a>--%>
										   	 <a href="#" onClick="window.open('<spring:eval expression="@propertyConfigurer.getProperty('CONTENT_BASE_PATH')" />${bean.previewPath}')" />Download</a>
										   	 <a href="previewContentForAdmin?previewPath=${bean.previewPath}&name=${bean.name}&type=PDF" target="_blank">View</a>
										</c:if>
									</td>
									
									<td> 
				            			<c:url value="deleteContentsByDistinct" var="deleteurl">
										  <c:param name="id" value="${bean.id}" />
									 	   <c:param name="consumerProgramStructureId" value="${bean.consumerProgramStructureId }"></c:param>
										</c:url>
										
										<c:url value="editcontentsByDistinct" var="editurl">
										  <c:param name="id" value="${bean.id}" />
										    <c:param name="consumerProgramStructureId" value="${bean.consumerProgramStructureId }"></c:param>
										
										</c:url>
										
										<c:url value="deleteContents" var="deleteurlForSingleMapping">
										  <c:param name="id" value="${bean.id}" />
										  <c:param name="consumerProgramStructureId" value="${bean.consumerProgramStructureId }"></c:param>
										</c:url>
										
										<c:url value="editSingleContentMapping" var="editurlForSingleMapping">
										  <c:param name="contentId" value="${bean.id}" />
										  <c:param name="consumerTypeId" value="${bean.consumerTypeId}" />
										  <c:param name="programStructureId" value="${bean.programStructureId}" />
										  <c:param name="programId" value="${bean.programId}" />
										  <c:param name="consumerProgramStructureId" value="${bean.consumerProgramStructureId}" />
										  <c:param name="programSemSubjectId" value="${bean.programSemSubjectId}" />
										</c:url>
										
										<c:choose>
											<c:when test="${searchType eq 'distinct' }">
												<a href="${editurl}" title="Edit"><i class="fa-solid fa-pen-to-square fa-lg"></i></a>&nbsp;	
						 						<a href="${deleteurl}" title="Delete" onclick="return confirm('Are you sure you want to delete this record? ')"><i class="fa-regular fa-trash-can fa-lg"></i></a>
											</c:when>
											<c:when test="${searchType eq 'all' }">
												<a href="${editurlForSingleMapping}" title="Edit"><i class="fa-solid fa-pen-to-square fa-lg"></i></a>&nbsp;	
						 						<a href="${deleteurlForSingleMapping}" title="Delete" onclick="return confirm('Are you sure you want to delete this record? ')"><i class="fa-regular fa-trash-can fa-lg"></i></a>
											</c:when>
										</c:choose>

										
		            				</td>
									</tr>
							</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
				</c:when>
			</c:choose>
			
			
		 	<c:url var="firstUrl" value="searchContentPage?pageNo=1" />
			<c:url var="lastUrl" value="searchContentPage?pageNo=${page.totalPages}" />
			<c:url var="prevUrl" value="searchContentPage?pageNo=${page.currentIndex - 1}" />
			<c:url var="nextUrl" value="searchContentPage?pageNo=${page.currentIndex + 1}" />
			
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
				            <c:url var="pageUrl" value="searchContentPage?pageNo=${i}" />
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
				let filePath = $(this).attr('data-filePath')
				let consumerProgramStructureId = $(this).attr('data-consumerProgramStructureId');
				
				let modalBody = "<center><h4>Loading...</h4></center>";
				let data = {
					'month':month,
					'year':year,
					'subject':subject,
					'filePath':filePath,
					'consumerProgramStructureId':consumerProgramStructureId
				};
				$.ajax({
					   type : "POST",
					   contentType : "application/json",
					   url : "getCommonContentProgramsList",   
					   data : JSON.stringify(data),
					   success : function(data) {
						   modalBody = '<div class="table-responsive"> <table class="table"> <thead><td>Exam year</td><td>Exam month</td> <td>Consumer Type</td> <td>Program Structure</td> <td>Program</td> <td>Subject</td> <td>Action</td> </thead><tbody>';
						   for(let i=0;i < data.length;i++){
							   modalBody = modalBody + '<tr><td>'
							   				+ data[i].year +'</td><td>'
							   				+ data[i].month +'</td><td>'
							   				+ data[i].consumerType +'</td><td>'
							   				+ data[i].programStructure +'</td><td>'
							   				+ data[i].program +'</td><td>'
							   				+ data[i].subject +'</td>'
							   				+'<td> <a href="editSingleContentMapping?'
							   				+'contentId='+ data[i].id 
							   				+'&consumerTypeId='+ data[i].consumerTypeId
							   				+'&programStructureId='+ data[i].programStructureId
							   				+'&programId='+ data[i].programId
							   				+'&consumerProgramStructureId='+ data[i].consumerProgramStructureId
							   				+'&programSemSubjectId='+data[i].programSemSubjectId
							   				+'" title="Edit"><i class="fa-solid fa-pen-to-square fa-lg"></i></a>'
							   				+'<a href="deleteContents?id='+ data[i].id 
							   				+'&consumerProgramStructureId='+ data[i].consumerProgramStructureId 
							   				+'" title="Delete" onclick="return confirm(`Are you sure you want to delete this record?`)"><i class="fa-regular fa-trash-can fa-lg"></i></a></td></tr>';
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
				$('#programModal').modal('show');
			});
		});
	</script>
	
	
	<jsp:include page="ConsumerProgramStructureDropdownNew.jsp" />
	
	
	
</body>
</html>