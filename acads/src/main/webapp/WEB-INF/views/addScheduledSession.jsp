
<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 


<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<jsp:include page="jscss.jsp">
<jsp:param value="Add/Edit Session" name="title" />
</jsp:include>
<style>
.panel-title .glyphicon{
        font-size: 14px;
    }
</style>

<script language="JavaScript">
	function validateForm() {
		var mode = document.getElementById('mode').value;
		var capacity = document.getElementById('capacity').value;
		
		if(mode == 'Online'){
			if(capacity == ''){
				alert('Capacity can not be blank if Exam center is set up for Online mode');
				return false;
			}
		}
		return true;
	}
	
	
</script>

<body class="inside">

<%@ include file="header.jsp"%>
	<%
	boolean isEdit = "true".equals((String)request.getSession().getAttribute("edit"));
	String sessionStartDate = (String)request.getAttribute("sessionStartDate");
	String sessionEndDate = (String)request.getAttribute("sessionEndDate");
	%>
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Add/Edit Session</legend></div>
		<%@ include file="messages.jsp"%>
		<form:form  action="addScheduledSession" method="post" modelAttribute="session" >
			<fieldset>
			<div class="panel-body">
			<div class="col-md-7 column">
				<%if(isEdit){ %>
					<form:input type="hidden" path="id" id="id" value="${session.id}"/>
					<form:input type="hidden" path="meetingKey" id="meetingKey" value="${session.meetingKey}"/>
					<form:input type="hidden" path="hasModuleId" id="hasModuleId" value="${session.hasModuleId }"/>
					<form:input type="hidden" path="consumerProgramStructureId" id="consumerProgramStructureId" value="${consumerProgramStructureId }" />
				<%} %>
				
				
				<!-- Form Name -->
					<%if(isEdit){ %>
					
					<div class="form-group">
						<form:select id="corporateName" path="corporateName" type="text" placeholder="corporateName" class="form-control"  
							itemValue="${session.corporateName}">
								<form:option value="">Retail</form:option>
								<form:option value="All">All</form:option>
								<form:option value="Verizon">Verizon</form:option>
								<form:option value="SAS">SAS</form:option>
						<%-- 	<form:option value="M.sc">M.sc</form:option> --%>
						<%--	<form:options items="${consumerTypeList}" /> --%>
						</form:select>
					</div>
					
					<div class="form-group">
						<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control"  required="required" 
							itemValue="${session.year}" disabled="disabled" readonly="readonly"> 
							<form:option value="">Select Acad Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
					
					<div class="form-group">
						<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" required="required" 
							itemValue="${session.month}" disabled="disabled" readonly="readonly">
							<form:option value="">Select Acad Month</form:option>
							<form:option value="Jan">Jan</form:option>
							<form:option value="Jul">Jul</form:option>
						</form:select>
					</div>
					
					<div class="form-group" >
						<form:select id="subject" path="subject" type="text"	placeholder="Subject" class="form-control" required="required" 
 							 itemValue="${session.subject}" disabled="disabled" readonly="readonly"> 
 							<form:option value="">Select Subject</form:option>
 							<form:options items="${subjectList}" />
 						</form:select>
					</div>
					
					<div class="form-group" >
						<form:select id="subjectCode" path="subjectCode" type="text" placeholder="Subject Code" class="form-control" required="required" 
							 itemValue="${session.subjectCode}" disabled="disabled" readonly="readonly"> 
							<form:option value="">Select Subject Code</form:option>
<%-- 						<form:options items="${subjectCodeList}" /> --%>
							<c:forEach items="${subjectCodeMap}" var="subjectMap">
								<form:option value="${subjectMap.key}">${subjectMap.key} ( ${subjectMap.value} )</form:option>
							</c:forEach>
						</form:select>
					</div>
					
					<div class="form-group" >
							<form:select id="facultyLocation" path="facultyLocation" type="text"	placeholder="facultyLocation" class="form-control" required="required" 
								 itemValue="${session.facultyLocation}" > 
								<form:option value="">Select Faculty Location</form:option>
								<form:options items="${locationList}" />
							</form:select>
					</div>
					
					<div class="form-group" >
							<form:select id="track" path="track" type="text"	placeholder="Selet Track" class="form-control"  
								 itemValue="${session.track}" > 
								<form:option value="">Select Track</form:option>
								<form:options items="${trackList}" />
							</form:select>
					</div>
					
					<%}else {%>
					<div class="form-group">
						<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control"  required="required" 
							itemValue="${session.year}" > 
							<form:option value="">Select Acad Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
					
					<div class="form-group">
						<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" required="required" 
							itemValue="${session.month}" >
							<form:option value="">Select Acad Month</form:option>
							<form:option value="Jan">Jan</form:option>
							<form:option value="Jul">Jul</form:option>
						</form:select>
					</div>
					
<!-- 					<div class="form-group" style="overflow:visible;"> -->
<%-- 						<form:select id="subject" path="subject" class="combobox form-control" required="required" itemValue="${session.subject}" >  --%>
<%-- 							<form:option value="">Type OR Select Subject</form:option> --%>
<%-- 							<form:options items="${subjectList}" /> --%>
<%-- 						</form:select> --%>
<!-- 					</div> -->
					
					<label>You can schedule session by Subject Code or Programs. Only Select one option</label>
					
					<div class="form-group" style="overflow: visible;">
						<form:select id="subjectCode" path="subjectCode" class="combobox form-control" itemValue="${session.subjectCode}"> 
							<form:option value="">Select Subject Code</form:option>
<%-- 						<form:options items="${subjectCodeList}" /> --%>
							<c:forEach items="${subjectCodeMap}" var="subjectMap">
								<form:option value="${subjectMap.key}">${subjectMap.key} ( ${subjectMap.value} )</form:option>
							</c:forEach>
						</form:select>
					</div>
					
					<div class="form-group">
						<jsp:include page="multipleMasterKeySelectWidget.jsp"/>
					</div>
					
					<div class="form-group">
						<form:select id="corporateName" path="corporateName" type="text" placeholder="corporateName" class="form-control"  
							itemValue="${session.corporateName}">
							<form:option value="">Retail</form:option>
							<form:option value="All">All</form:option>
							<form:option value="Verizon">Verizon</form:option>
							<form:option value="SAS">SAS</form:option>
							<form:option value="Diageo">Diageo</form:option>
					<%-- 	<form:option value="M.sc">M.sc</form:option> --%>
						</form:select>
					</div>
					
					<div class="form-group" >
							<form:select id="facultyLocation" path="facultyLocation" type="text"	placeholder="facultyLocation" class="form-control" required="required" 
								 itemValue="${session.facultyLocation}" > 
								<form:option value="">Select Faculty Location</form:option>
								<form:options items="${locationList}" />
							</form:select>
					</div>
					
					<div class="form-group" >
							<form:select id="track" path="track" type="text"	placeholder="track" class="form-control"  
								 itemValue="${session.track}" > 
								<form:option value="">Select Track</form:option>
								<form:options items="${trackList}" />
							</form:select>
					</div>
					
					<%} %>
					
					<div class="form-group">
							<form:input id="sessionName" path="sessionName" type="text" placeholder="Session Name" class="form-control"  
							value="${session.sessionName}" disabled="disabled"/>
					</div>
					
					<div class="form-group">
							<form:input id="facultyId" path="facultyId" type="text" placeholder="Faculty ID" class="form-control"  
							value="${session.facultyId}"/>
					</div>
					
					<div class="form-group">
						<form:input id="date" path="date" type="date" placeholder="Session Date" class="form-control" value="${session.date}" />
					</div>
					
					<%if(isEdit){ %>
						<div class="form-group">
							<form:input id="startTime" path="startTime" type="time-local" placeholder="Time" class="form-control" value="${session.startTime}" />
						</div>
					<% } else { %>
						<div class="form-group">
							<form:input id="startTime" path="startTime" type="time" placeholder="Time" class="form-control" value="${session.startTime}" />
						</div>
					<% } %>
					
					
					
					<div class="panel-group" id="accordion">
				        <div class="panel panel-default">
				            <div class="panel-heading">
				                <h4 class="panel-title">
				                    <a data-toggle="collapse" data-parent="#accordion" href="#collapseOne" class="collapsed">
				                    <span class="glyphicon glyphicon-plus"></span>Add Event</a>
				                </h4>
				            </div>
				            <div id="collapseOne" class="panel-collapse collapse ">
				            	<div class="panel-body">
									<div class="form-group">
										<form:input id="startDate" path="startDate" type="date" placeholder="Start Date" class="form-control" value="${session.startDate}" />
										</div>
										<div class="form-group">
										<form:input id="endDate" path="endDate" type="date" placeholder="End Date" class="form-control" value="${session.endDate}" />
									</div>
								</div>              
							</div>
				  		</div>
				 	</div>
					
					<div class="form-group" id="isAdditionalSessionCheck">
						<form:label path="isAdditionalSession">
						<form:checkbox id="isAdditionalSession" path="isAdditionalSession" style="width:15px;height:15px"  class="form-control" value="Y" />Is Additional Session
						</form:label>
					</div>
					
					<div class="form-group">
						<form:label path="byPassChecks">
						<form:checkbox id="byPassChecks" path="byPassChecks" style="width:15px;height:15px"  class="form-control" value="Y" />Bypass All Checks
						</form:label>
					</div>
					
					<div class="form-group">
						<form:label path="byPassFaculty">
							<form:checkbox id="byPassFaculty" path="byPassFaculty" style="width:15px;height:15px"  class="form-control" value="Y" />Bypass All Faculty Checks
						</form:label>
					</div>
									
					<div class="form-group">
					<label class="control-label" for="submit"></label>
					<div class="form-group">
						<%if(isEdit){ %>
							<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="updateScheduledSession">Update Details</button>
							<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="updateSessionName">Update Session Name</button>
							<br><br>
							<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="updateFacultyId">Update Faculty</button>
							<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="updateTrackDetails">Update Track</button>
						<%}else	{%>
							<button id="submit" name="submit" class="btn btn-large btn-primary" onclick="$('#selected_program option').prop('selected', true);"
							formaction="addScheduledSession">Add Session</button>
							<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="getRecommendationSession">Recommendation Session</button>
						<%} %>
					</div>
						<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="verifySessionFeasibility">Verify Feasibility</button>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
					</div>
				</div>
				
				
				<div class="col-md-11 column">
				
				<div class="panel-group">
				    <div class="panel panel-default">
				      <div class="panel-heading">
				        <h4 class="panel-title">
				          <a data-toggle="collapse" href="#recommendationSession">Recommendation Session</a>
				        </h4>
				      </div>
				      <div id="recommendationSession" class="panel-collapse collapse">
				        <div class="panel-body">
							<div class="table-responsive">
								<table class="table table-striped" style="font-size:12px">
									<thead>
									<tr>
										<th>Sr. No.</th>
										<th>Subject</th>
										<th>SubjectCode</th>
										<th>Date</th>
										<th>Day</th>
										<th>Start Time</th>
										<th>Action</th>
									</tr>
									</thead>
									<tbody>
										<c:choose>
											<c:when test="${fn:length(availableSolts) > 0}">
												<c:forEach var="bean" items="${availableSolts}" varStatus="status">
													<tr>
														<%if(isEdit){ %>
															<c:url value="updateScheduledSession" var="updateScheduledSession">
																<c:param name="isRecSession" value="Y" />
																<c:param name="index" value="${status.count}" />
															</c:url>
														<% } else { %>
															<c:url value="addScheduledSession" var="addScheduledSession">
																<c:param name="isRecSession" value="Y" />
																<c:param name="index" value="${status.count}" />
															</c:url>
														<% } %>
														
														<td><c:out value="${status.count}" /></td>
														<td><c:out value="${bean.subject}" /></td>
														<td><c:out value="${bean.subjectCode}" /></td>
														<td><c:out value="${bean.date}" /></td>
														<td><c:out value="${bean.day}" /></td>
														<td><c:out value="${bean.startTime}" /></td>
														<%if(isEdit){ %>
															<td><button id="submit" name="submit" class="btn btn-primary" formaction="${updateScheduledSession }">Update Session</button></td>
														<% } else { %>
															<td><button id="submit" name="submit" class="btn btn-primary" formaction="${addScheduledSession }">Add Session</button></td>
														<% } %>
														
													</tr>
												</c:forEach>
											</c:when>
											<c:otherwise>
												<tr>
													<td colspan="6" style="text-align: center;">
														<i class="fas fa-exclamation"></i> 
															No available slots found from <%=sessionStartDate %> to <%=sessionEndDate %>
													</td>
												</tr>
											</c:otherwise>
										</c:choose>
									</tbody>
								</table>
							</div>
							<c:url value="getRecommendationSession" var="getRecommendationSession">
								<c:param name="viewMore" value="Y"></c:param>
							</c:url>
							<c:if test="${availableSolts.size() < 5}">
							
							</c:if>
							<button name="View More..." class="btn btn-large btn-primary" formaction="${getRecommendationSession }">View More...</button>
						</div>
				      </div>
				    </div>
				  </div>
				
				<legend>&nbsp;Other Scheduled Sessions for Same Subject<font size="2px">  </font></legend>
				<div class="table-responsive">
				<table id="dataTable" class="table table-striped" style="font-size:12px">
						<thead>
						<tr>
							<th>Sr. No.</th>
							<th>Subject</th>
							<th>Session</th>
							<th>Date</th>
							<th>Day</th>
							<th>Start Time</th>
							<th>Faculty ID</th>
							<th>Faculty Name</th>
							<th>Faculty Location</th>
						</tr>
						</thead>
						<tbody>
						
						<c:forEach var="bean" items="${scheduledSessionList}" varStatus="status">
					        <tr>
					            <td><c:out value="${status.count}" /></td>
					            <td><c:out value="${bean.subject}" /></td>
					            <td><c:out value="${bean.sessionName}" /></td>
					            <td><c:out value="${bean.date}" /></td>
								<td><c:out value="${bean.day}" /></td>
								<td><c:out value="${bean.startTime}" /></td>
								<td><c:out value="${bean.facultyId}" /></td>
								<td><c:out value="${bean.firstName} ${bean.lastName}" /></td>
								<td><c:out value="${bean.facultyLocation}" /></td>
					            
					        </tr>   
					    </c:forEach>
							
						</tbody>
					</table>
				</div>
				
				
				</div>
				
				
				</div>
				
				
			</fieldset>
		</form:form>

		</div>
		
	
	</section>
	<div class="content-container ">
    
	<!-- Code to create duplicate Session Start  -->
	
			<%if(isEdit){ %>
									
				<div class=" col-md-6 well">
				  <button type="button" class="btn btn-info" data-toggle="collapse" data-target="#demo">Create Duplicate Session </button>
				  <div id="demo" class="collapse">
					<form:form  action="addDuplicateSession" method="post" modelAttribute="session" >
						<fieldset>
													
							<form:input type="hidden" path="id" value="${session.id}"/>	  		
							
							<div class="form-group" style="overflow:visible;">
							<h5>Subject:</h5>
							
<%-- 								<form:select id="subject" path="subject" class="combobox form-control dupSubject" required="required" itemValue="${session.subject}" >  --%>
<%-- 									<form:option value="">Type OR Select Subject</form:option> --%>
<%-- 									<form:options items="${subjectList}" /> --%>
<%-- 								</form:select> --%>

								<form:select id="subjectCode" path="subjectCode" class="combobox form-control" itemValue="${session.subjectCode}"> 
									<form:option value="">Select Subject Code</form:option>
<%-- 								<form:options items="${subjectCodeList}" /> --%>
									<c:forEach items="${subjectCodeMap}" var="subjectMap">
										<form:option value="${subjectMap.key}">${subjectMap.key} ( ${subjectMap.value} )</form:option>
									</c:forEach>
								</form:select>
							</div>
							
							<div class="form-group">
								<jsp:include page="multipleMasterKeySelectWidget.jsp"/>
							</div>
											
							<div class="form-group">
								<h5>Session Name:</h5>
									<form:input id="sessionName" path="sessionName" type="text" placeholder="Session Name" class="form-control" value="${session.sessionName}" disabled="disabled"/>
							</div>
											
							<div class="form-group">
								<h5>Corporate Name:</h5>
								<form:select id="corporateName" path="corporateName" type="text" placeholder="corporateName" class="form-control"  
									itemValue="${session.corporateName}">
									<form:option value="">Retail</form:option>
									<form:option value="All">All</form:option>
									<form:option value="Verizon">Verizon</form:option>
									<form:option value="SAS">SAS</form:option>
							<%-- 	<form:option value="M.sc">M.sc</form:option> --%>
								</form:select>
							</div>
							
							<div class="form-group">
								<h5>Track:</h5>
								<form:select id="track" path="track" type="text"	placeholder="Selet Track" class="form-control" itemValue="${session.track}" > 
									<form:option value="">Select Track</form:option>
									<form:options items="${trackList}" />
								</form:select>
							</div>
							
							<button id="submit" name="submit" class="btn btn-large btn-primary" onclick="$('#selected_program option').prop('selected', true);">Duplicate Session</button>
						</fieldset>
					</form:form>
				  </div>
				</div>
				
			<% } %>
					
	<!-- Code to create duplicate Session End  -->
		</div>
		<br><br>
	  <jsp:include page="footer.jsp" />

 	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />assets/js/bootstrap.js"></script> 
    <script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/jquery-ui.min.js"></script>  
	<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js" ></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
	<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js" ></script>

    <script>
		$(document).ready(function() {
		    $('#dataTable').DataTable();
		} );
	</script>    

 <script type="text/javascript">
 var moduleId = ${moduleId != null && moduleId != '' ? moduleId : -1};
 if(moduleId != -1){
	 document.getElementById("submitUpdateTrack").style.display = "none"; 
	 document.getElementById("isAdditionalSessionCheck").style.display = "none"; 
	 document.getElementById("accordion").style.display = "none"; 
 }
        
    </script>

</body>

</html>

