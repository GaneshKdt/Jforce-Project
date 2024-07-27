

<!DOCTYPE html>
<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
        <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="jscss.jsp">
<jsp:param value="Add Subjects" name="title" />
</jsp:include>
<style>
.panel-title .glyphicon{
        font-size: 14px;
    }
</style>

<body class="inside">

<%@ include file="header.jsp"%>
	<%
	boolean isEdit = "true".equals((String)request.getAttribute("edit"));
	%>
	<%-- <%Integer subjectId = (Integer)session.getAttribute("subjectId"); %> --%>
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Add Subjects For Exam</legend></div>
		<%@ include file="messages.jsp"%>
		<form:form  action="makeLiveSubjectsEntry" method="post" modelAttribute="executiveExamOrderBean" >
			<fieldset>
			
			
			<div class="panel-body">
			
			 <div class="col-md-6 column">
			
					<div class="form-group">
						<form:select id="acadYear" path="acadYear" type="text"	placeholder="acadYear" class="form-control"  required="required" 
							itemValue="${executiveExamOrderBean.acadYear}" disabled="disabled" > 
							<form:option value="">Select Batch Year</form:option>
							<form:options items="${executiveYearList}" />
						</form:select>
					</div>
					
					<div class="form-group">
						<form:select id="acadMonth" path="acadMonth" type="text" placeholder="acadMonth" class="form-control" required="required" 
							itemValue="${executiveExamOrderBean.acadMonth}" disabled="disabled" >
							<form:option value="">Select Batch Month</form:option>
							<form:options items="${executiveMonthList}" />
						</form:select>
					</div>
					
					<div class="form-group">
						<form:select id="examYear" path="examYear" type="text"	placeholder="examYear" class="form-control"  required="required" 
							itemValue="${executiveExamOrderBean.examYear}" disabled="disabled" > 
							<form:option value="">Select Exam Year</form:option>
							<form:options items="${executiveYearList}" />
						</form:select>
					</div>
					
					<div class="form-group">
						<form:select id="examMonth" path="examMonth" type="text" placeholder="examMonth" class="form-control" required="required" 
							itemValue="${executiveExamOrderBean.examMonth}" disabled="disabled" >
							<form:option value="">Select Exam Month</form:option>
							<form:options items="${executiveMonthList}" />
						</form:select>
					</div>
					
						<div class="form-group">
						<form:select id="prgmStructApplicable" path="prgmStructApplicable" type="text" placeholder="Program Structure" class="form-control" required="required" 
							itemValue="${executiveExamOrderBean.prgmStructApplicable}" disabled="disabled" >
							<form:option value="">Select Program Structure</form:option>
							<form:options items="${SAS_PROGRAM_STRUCTURE_LIST}" />
						</form:select>
					</div>
					
					<div class="form-group">
						<form:select id="program" path="program" type="text" placeholder="Program" class="form-control" required="required" 
							itemValue="${executiveExamOrderBean.program}" disabled="disabled" >
							<form:option value="">Select Program</form:option>
							<form:options items="${SAS_PROGRAM_LIST}" />
						</form:select>
					</div>
					
					<div class="form-group" style="overflow:visible;">
								<form:select id="subject" path="subject" type="text" placeholder="Subject" class="form-control" required="required" 
							itemValue="${executiveExamOrderBean.subject}" disabled="disabled" >
							<form:option value="">Select Subject</form:option>
							<form:options items="${subjectListBasedOnProgram}" />
						</form:select>
					</div>
					
					
					
									
				
					<label class="control-label" for="submit"></label>
					<div class="form-group">
						 <button id="submit" name="submit" class="btn btn-large btn-primary" formaction=makeLiveSubjectsEntry>Submit</button>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
					</div>
						
					
				</div>
				
				
			 <div class="col-md-12 column">
				<legend>&nbsp;Subjects Set - Up<font size="2px">  </font></legend>
				<div class="table-responsive">
				<table class="table table-striped" style="font-size:12px">
						<thead>
						<tr>
							
							<th>Subject</th>
							<th>Program</th>
							<th>Program Structure</th>
							<th>Exam Month-Year</th>
							<th>Batch Month-Year</th>
							<th>Created By</th>
							<th>Last Modified By</th>
							<th>Actions</th> 
						</tr>
						</thead>
						<tbody>
						
					<c:forEach var="bean" items="${subjectsLive}" varStatus="status"> 
					        <tr>
					            <td><c:out value="${bean.subject}" /></td>
					            <td><c:out value="${bean.program}" /></td>
					            <td><c:out value="${bean.prgmStructApplicable}" /></td>
								<td><c:out value="${bean.examMonth} - ${bean.examYear}" /></td>
								<td><c:out value="${bean.acadMonth} - ${bean.acadYear}" /></td>
								<td><c:out value="${bean.createdBy}" /></td>
								<td><c:out value="${bean.lastModifiedBy}" /></td>
					           <td>
					          <!--   <a href="/exam/editSubjectsEntry" title="Edit"><i class="fa fa-pencil-square-o fa-lg"></i></a>&nbsp; -->
								<a href="/exam/deleteSubjectsEntry?id=${bean.id }" title="Delete Content" class="">
									<b>
										<i style="font-size:20px; padding-left:5px" class="fa-regular fa-trash-can" aria-hidden="true"></i>
									</b>
								</a>
							  </td> 
					        </tr>   
			   </c:forEach> 
							
						</tbody>
					</table>
				</div>
				
				
				</div> 
				
				
				</div>
				
				
				
			</fieldset>
		</form:form>
<c:if test="${rowCount > 0}">
	<legend>&nbsp; Subject Entries Report<font size="2px"> (${rowCount} Records Found) &nbsp; <a href=downloadSubjectEntriesReport>Download to Excel</a></font></legend>
</c:if>
		</div>
		
	
	</section>
	
	  <jsp:include page="footer.jsp" />

 <script>
 $("#program")
	.on(
			'change',
			function() {
				var selectedValue = $(this).val();
				var prgmStructApplicable = $("#prgmStructApplicable").val();
				var acadYear = $("#acadYear").val();
				var acadMonth = $("#acadMonth").val();
				var examMonth = $("#examMonth").val();
				var examYear = $("#examYear").val();
				console.log('prgmStructApplicable'+prgmStructApplicable);
				var url = '<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />getSubjectsBasedOnProgram?program='+selectedValue+
						"&prgmStructApplicable="+prgmStructApplicable+"&acadYear="+acadYear+"&acadMonth="+acadMonth+"&examYear="+examYear+"&examMonth="+examMonth;
				console.log('URL :'+url);
				window.location = url;
				
				return false;
			});
 
 function getSubjectSetUp() {
	 var xhttp = new XMLHttpRequest();
		var selectedValue = $("#acadYear").val();
		var acadMonth = $("#acadMonth").val();
		var examYear = $("#examYear").val();
		var examMonth = $("#examMonth").val();
		var prgmStructApplicable = $("#prgmStructApplicable").val();
		var prgm = $("#program").val();
		var sub = $("#subject").val();
		console.log('prgmStructApplicable'+prgmStructApplicable);
		var url = '<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />getSubjectsSetUp?acadYear='+selectedValue+
				" &prgmStructApplicable="+prgmStructApplicable+"&program="+prgm+
				" &acadMonth="+acadMonth+"&examYear="+examYear+"&examMonth="+examMonth+
				" &subject="+sub ;
	
		 xhttp.open("GET", url);
		 xhttp.send();
		 window.location = url;
		return false;
	}
 
 $("#acadYear")
	.on('change',function(){
		getSubjectSetUp();
	});
 $("#acadMonth")
	.on('change',function(){
		getSubjectSetUp();
	});
 $("#examYear")
	.on('change',function(){
		getSubjectSetUp();
	});
 $("#examMonth")
	.on('change',function(){
		getSubjectSetUp();
	});
 $("#prgmStructApplicable")
	.on('change',function(){
		getSubjectSetUp();
	});

 $("#subject")
	.on('change',function(){
		getSubjectSetUp();
	});

 
</script>


</body>

</html>
