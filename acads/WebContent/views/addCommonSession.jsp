<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 


<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="jscss.jsp">
<jsp:param value="Add/Edit Common Session" name="title" />
</jsp:include>

<link href="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.6-rc.0/css/select2.min.css" rel="stylesheet" />

<style>
	.select2-search__field {
		color:black;
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
	boolean isEdit = "true".equals((String)request.getAttribute("edit"));
	%>
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Add/Edit Session</legend></div>
		<%@ include file="messages.jsp"%>
		<form:form  action="addCommonSession" method="post" modelAttribute="session" >
			<fieldset>
			<div class="panel-body">
			
			
			<div class="col-md-6 column">
				<%if(isEdit){ %>
				<form:input type="hidden" path="id" value="${session.id}"/>
				<form:input type="hidden" path="sessionType" id="sessionType" value="${session.sessionType}"/>
				<%} %>
				<!-- Form Name -->
					<%if(isEdit){ %>
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
					
					<div class="form-group">
						<form:select id="sessionType" path="sessionType" class="form-control" itemValue="${session.sessionType}"> 
							<form:option value="">Select Session Type</form:option>
							<c:forEach items="${sessionTypesMap}" var="sessionType">
								<form:option value="${sessionType.key}"> ${sessionType.value} </form:option>
							</c:forEach>
						</form:select>
					</div>
					
					<div class="form-group">
					<label for=sem>Select Semesters </label>
						<form:checkboxes style="margin-left:-200px;margin-top:10px;" items="${semList}" path="sem"/>
					</div>
					
					<div class="form-group" >
						<form:select id="subject" path="subject" type="text"	placeholder="Subject" class="form-control" required="required" 
							 itemValue="${session.subject}" disabled="disabled" readonly="readonly"> 
							<form:option value="">Select Subject</form:option>
							<form:option value="Assignment" >Assignment</form:option>
							<form:option value="Project">Project</form:option>
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
						<select data-id="consumerTypeDataId" id="consumerTypeId" name="corporateName" class="selectConsumerType form-control" required="required" >
             			<option disabled selected value="">Select Consumer Type</option>
             				<c:forEach var="consumerType" items="${consumerTypeList}">
             					<c:choose>
					               <c:when test="${consumerType.id == session.consumerTypeId}">
						                <option selected value="<c:out value="${consumerType.name}"/>">
						                	<c:out value="${consumerType.name}"/>
										</option>
					               </c:when>
					               <c:otherwise>
										<option value="<c:out value="${consumerType.name}"/>">
			                              	<c:out value="${consumerType.name}"/>
			                            </option>
			                     	</c:otherwise>
			                     </c:choose>
							</c:forEach>
            			</select>
						<%-- <form:select id="corporateName" path="corporateName" type="text" placeholder="corporateName" class="form-control" required="required" 
							 itemValue="${session.corporateName}" > 
							<form:option value="">Select Corporate Name</form:option>
							<form:options items="${consumerTypeList}" />
						</form:select> --%>
					</div>
					
					<div class="form-group" >
							<form:select id="subject" path="subject" type="text"	placeholder="Subject" class="form-control" required="required" 
								 itemValue="${session.subject}" disabled="disabled" readonly="readonly"> 
								<form:option value="">Select Subject</form:option>
								<form:option value="Assignment" >Assignment</form:option>
								<form:option value="Project">Project</form:option>
								<form:option value="Module 4 - Project">Module 4 - Project</form:option>
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
							<form:select id="corporateName" path="corporateName" type="text" placeholder="corporateName" class="form-control" required="required" 
								 itemValue="${session.corporateName}" > 
								<form:option value="">Select Corporate Name</form:option>
								<form:options items="${consumerTypeList}" />
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
					
					<div class="form-group">
						<form:select id="sessionType" path="sessionType" class="form-control" itemValue="${session.sessionType}" required="required"> 
							<form:option value="">Select Session Type</form:option>
							<c:forEach items="${sessionTypesMap}" var="sessionType">
								<form:option value="${sessionType.key}"> ${sessionType.value} </form:option>
							</c:forEach>
						</form:select>
					</div>
					
					<div class="form-group" >
					<label for=sem>Select Semesters </label>
					
					<form:checkboxes style="margin-left:-200px;margin-top:10px;" items="${semList}" path="sem"/> 
						
				</div>
					
					<div class="form-group" style="overflow:visible;">
						<form:select id="subject" path="subject" class="combobox form-control"
							 itemValue="${session.subject}" required="required" > 
							<form:option value="">Type OR Select Subject</form:option>
							<form:option value="Assignment">Assignment</form:option>
							<form:option value="project">Project</form:option>
							<form:option value="Module 4 - Project">Module 4 - Project</form:option>
							<form:option value="Orientation">Orientation</form:option>
							<form:option value="SASOrientation">SAS-Orientation</form:option>
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
						<select data-id="consumerTypeDataId" id="consumerTypeId" name="corporateName" class="selectConsumerType form-control" required="required" >
             			<option disabled selected value="">Select Consumer Type</option>
             				<c:forEach var="consumerType" items="${consumerTypeList}">
             					<c:choose>
					               <c:when test="${consumerType.id == session.consumerTypeId}">
						                <option selected value="<c:out value="${consumerType.name}"/>">
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
					
						<%-- <form:select id="corporateName" path="corporateName" type="text" placeholder="corporateName" class="form-control" required="required" 
							 itemValue="${session.corporateName}" > 
							<form:option value="">Select Corporate Name</form:option>
							<form:option value="All">All</form:option>
							<form:options items="${consumerTypeList}" />
						</form:select> --%>
					</div>
					
					<%} %>
					
					<%-- Commented by Somesh as added Programs on Consumer type base --%>
					<%-- <div class="form-group" >
						<form:select id="programList" path="programList" type="text"  required="required" 
							   class="multipleSelect" multiple = "multiple" data-placeholder="Select Programs"> 
							<form:option value="All">All</form:option>
							<form:options items = "${programList}" />
						</form:select>
					</div> --%>
					
					<div class="form-group">
			        	<select id="programId" name="programList" class="selectProgram multipleSelect form-control" multiple = "multiple" required="required">
			        		<option disabled value="">Select Program</option>
			        	</select>
			        </div>
			       
			       <div class="form-group">
						<form:label path="selectAllOptions">
							<form:checkbox id="selectAllOptions" path="selectAllOptions" style="width:15px;height:15px" class="form-control" value="" />Select All Programs
						</form:label>
					</div>
					
					<div class="form-group">
						<form:input id="sessionName" path="sessionName" type="text" placeholder="Session Name" class="form-control" value="${session.sessionName}" disabled="disabled" required="required"/>
					</div>
					
					<div class="form-group">
						<form:input id="facultyId" path="facultyId" type="text" placeholder="Faculty ID" class="form-control" value="${session.facultyId}" required="required"/>
					</div>
					
					<div class="form-group">
						<form:input id="date" path="date" type="date" placeholder="Session Date" class="form-control" value="${session.date}" required="required"/>
					</div>
					
					<div class="form-group">
						<form:input id="startTime" path="startTime" type="time" placeholder="Time" class="form-control" value="${session.startTime}" required="required"/>
					</div>
					
					<%-- Commented by Steffi as added new muti-select cdn
					
					<div class="form-group" >
							<form:select id="programList" path="programList" type="text" placeholder="Program" class="form-control" required="required" 
								 multiple="true" > 
								<form:option value="">Select Program</form:option>
								<form:option value="All">All</form:option>
								<c:forEach items="${programList}" var="prog">
								<form:option value="${prog}">${prog}</form:option>
								</c:forEach>
							</form:select>
					</div> --%>
					
					<div class="form-group">
						<form:label path="byPassChecks">
							<form:checkbox id="byPassChecks" path="byPassChecks" style="width:15px;height:15px"  class="form-control" value="Y" />Bypass All Checks
						</form:label>
					</div>
					
					<div class="form-group">
						<form:label path="byPassFaculty">
							<form:checkbox id="byPassFaculty" path="byPassFaculty" style="width:15px;height:15px" 	class="form-control" value="Y" />Bypass All Faculty Checks
						</form:label>
					</div>
					
					<div class="form-group">
						<label class="control-label" for="submit"></label>
						<div class="form-group">
							<%if(isEdit){ %>
								<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="updateScheduledSession">Update Details</button>
								<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="updateSessionName">Update Session Name</button>
								<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="updateFacultyId">Update Faculty</button>
							<%}else	{%>
								<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="addCommonSession">Add Session</button>
							<%} %>
						</div>
					</div>

				</div>
				
				
				<div class="col-md-12 column">
				<legend>&nbsp;Other Scheduled Sessions for Same Subject<font size="2px">  </font></legend>
				<div class="table-responsive">
				<table class="table table-striped" style="font-size:12px">
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

	  <jsp:include page="footer.jsp" />

<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.6-rc.0/js/select2.min.js"></script>
	
	<script>
		$(document).ready(function() {
			$('.multipleSelect').select2({
			placeholder: " Select Program",
			allowClear: true,
			closeOnSelect: true
			});
		});
	</script>
	
	<script type="text/javascript">
	$(document).ready(function() {
		
		$('.selectConsumerType').on('change', function(){
			
			let options = "<option>Loading... </option>";
			$('#programId').html(options);
			
			var data = {
						consumerType:this.value
						}
					 
			 	$.ajax({
			   		type : "POST",
			   		contentType : "application/json",
			   		url : "getProgramListByConsumerType",   
			   		data : JSON.stringify(data),
			   		success : function(data) {

					var programNameAndCodeMap = data;
			  		options = "";
			  		let allOption = "";
			    
			    	//Data Insert For Program List
			    	//Start
			    
			    	for (const [key, value] of Object.entries(programNameAndCodeMap)) {
			     		options = options + "<option value='" + value + "'> " +"[ "+ value + " ]"+"  -  " + key + " </option>"; 
			    	}
			    	
			    	$('#programId').html(options);
			    	
			    	
			       // Commented by Somesh as now added key, value for program name and program code
				   /*  
				   for(let i=0;i < programList.length;i++) {
				    	allOption = allOption + ""+ programList[i] +",";
			     		options = options + "<option value='" + programList[i] + "'> " + programList[i] + " </option>"; 
			     	}
			    
				    allOption = allOption.substring(0,allOption.length-1);
				    
				    $('#programId').html(
			    			"<option value='"+ allOption +"'>All</option>" + options
			    	);
			    	*/
		    	
			    	//End
			  		},
			   		
			  		error : function(e) {
			    	alert("Please Refresh The Page.")
// 			    	console.log("ERROR: ", e);
			   		}
			  });
		})
	})
	</script>
	
	<!-- Added for remove all programs and add all programs -->
	<script type="text/javascript">
		$("#selectAllOptions").click(function(){
		    if($("#selectAllOptions").is(':checked') ){
		        $("#programId > option").prop("selected","selected");
		        $("#programId").trigger("change");
		    }else{
		        $("#programId > option").removeAttr("selected");
		         $("#programId").trigger("change");
		     }
		});
	</script>
	
<%-- <jsp:include page="ConsumerProgramStructureCommonDropdown.jsp" /> --%>
</body>
</html>