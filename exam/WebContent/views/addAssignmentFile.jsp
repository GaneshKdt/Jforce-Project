<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 


<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Add/Edit Assignment File" name="title" />
</jsp:include>


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
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row">
        	<legend>Add /Edit Assignment File</legend>
        </div>
		<%@ include file="adminCommon/messages.jsp" %>
		<form:form id="formOne" action="addAssignmentFile" method="post" modelAttribute="assignmentFile" enctype="multipart/form-data">
			<fieldset>
			<div class="">
			
			
			<div class="col-md-8 column panel-body">
				<%-- <%if("true".equals((String)request.getAttribute("edit"))){ %>
				<form:input type="hidden" path="centerId" value="${examCenter.centerId}"/>
				<%} %> --%>
				<!-- Form Name -->
					<div class="form-group">
						Year : <input style="background-color:#d9d9db;cursor: no-drop" type="text" name="year" value="${assignmentFile.year}" readonly/>
					</div>
					<div class="form-group">
						Month : <input style="background-color:#d9d9db;cursor: no-drop" type="text" name="month" value="${assignmentFile.month}" readonly/>
					</div>
					
				<%-- 	<div class="form-group">
							<form:select id="subject" path="subject" type="text"	placeholder="Subject" class="form-control"  readonly="readonly" itemValue="${assignmentFile.subject}">
								<form:option value="">Select Subject</form:option>
								<form:options items="${subjectList}" />
							</form:select>
					</div>
					
					<div class="form-group">
							<form:select id="program" path="program" type="text"	placeholder="Program" class="form-control"  readonly="readonly" itemValue="${assignmentFile.program}">
								<form:option value="">Select Program</form:option>
								<form:option value="All">All</form:option>
								<form:option value="ACBM">ACBM</form:option>
							</form:select>
					</div> --%>
												
												<div class="form-group">
													Subject : <input class="" style="background-color:#d9d9db;cursor: no-drop" type="text" name="subject" value="${assignmentFile.subject}" readonly/>
												</div>
											
											
									
					
					<input type="hidden" name="consumerProgramStructureId" value="${assignmentFile.consumerProgramStructureId }" />
					<div class="form-group">
						Start Date :
						<form:input path="startDate" type="datetime-local" itemValue="${assignmentFile.startDate}" class="form-control" /> 
					</div>
					
					<div class="form-group">
						End Date : 
						<form:input path="endDate" type="datetime-local" itemValue="${assignmentFile.endDate}" class="form-control" /> 
					</div>
					
					<div class="form-group">
						<form:input path="fileData" type="file" itemValue="${assignmentFile.fileData}" class="form-control" /> Select New file Only if you wish to override earlier file. 
					</div>

					<div class="form-group">
					<label class="control-label" for="submit"></label>
						<%if("true".equals((String)request.getAttribute("edit"))){ %>
							<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="updateAssignmentFile">Update</button>
						<%}else	{%>
							<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="addExamCenter">Submit</button>
						<%} %>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="examCenterHome" formnovalidate="formnovalidate">Cancel</button>
					</div>
				</div>
				
				</div>
				
				
			</fieldset>
		</form:form>

		</div>
		
	
	</section>

	  <jsp:include page="footer.jsp" />

   <script type="text/javascript">
	
	$(document).ready(function() {
	});
	
	</script>

</body>
</html>
