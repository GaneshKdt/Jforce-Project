<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Generate Mark Checking Sheet" name="title" />
</jsp:include>

<script type="text/javascript">
	function showExamCenters(){
		mode = document.getElementById('mode').value;
		if(mode == 'Offline'){
			document.getElementById('offlineCenters').style.display = '';
			document.getElementById('onlineCenters').style.display = 'none';
		}else if(mode == 'Online'){
			document.getElementById('onlineCenters').style.display = '';
			document.getElementById('offlineCenters').style.display = 'none';
		}
	}

	function validate(){
		mode = document.getElementById('mode').value;
		if(mode == 'Offline'){
			offlineCenter = document.getElementById('offlineCenters').value;
			if(offlineCenter == ''){
				alert('Please select Exam Center');
				return false;
			}
		}else if(mode == 'Online'){
			offlineCenter = document.getElementById('onlineCenters').value;
			if(offlineCenter == ''){
				alert('Please select Exam Center');
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
        <div class="row"><legend>Generate Mark Check Sheet</legend></div>
        <%@ include file="messages.jsp"%>
		<div class="panel-body">
		<form:form  action="getMarkCheckSheet" method="post" modelAttribute="bean" >
			<fieldset>
			<div class="col-md-6 column">
				
					<div class="form-group">
						<form:select id="year" path="year" type="text" required="required"	placeholder="Written Year" class="form-control"   itemValue="${bean.year}">
							<form:option value="">Select Written Year</form:option>
							<form:options items="${yearList}" />
							<form:option value="2017">2017</form:option>
							<form:option value="2018">2018</form:option>
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="month" path="month" type="text" required="required" placeholder="Written Month" class="form-control"  itemValue="${bean.month}">
							<form:option value="">Select Written Month</form:option>
							<form:option value="Apr">Apr</form:option>
							<form:option value="Jun">Jun</form:option>
							<form:option value="Sep">Sep</form:option>
							<form:option value="Dec">Dec</form:option>
						</form:select>
					</div>
					
					<div class="form-group">
						<form:select id="mode" path="examMode" type="text" required="required" class="form-control"  itemValue="${bean.examMode}" 
						onchange="showExamCenters()">
							<form:option value="">Select Exam Mode</form:option>
							<form:option value="Online">Online</form:option>
							<form:option value="Offline">Offline</form:option>
						</form:select>
					</div>
					
					<div class="form-group">
						<form:select id="offlineCenters" path="offlineCenterId" type="text" 	placeholder="Center" class="form-control"   itemValue="${bean.offlineCenterId}">
							<form:option value="">Select Offline Center</form:option>
							<form:options items="${offlineCentersList}" />
						</form:select>
					</div>
					
					<div class="form-group">
						<form:select id="onlineCenters" path="onlineCenterId" type="text" 	placeholder="Center" class="form-control"   itemValue="${bean.onlineCenterId}">
							<form:option value="">Select Online Center</form:option>
							<form:options items="${onlineCentersList}" />
						</form:select>
					</div>
	
					</div>
					
					<div class="col-md-6 column">

					
					<div class="form-group">
						<form:select id="program" path="program" type="text" placeholder="Program" class="form-control"  itemValue="${bean.program}">
							<form:option value="">Select Program</form:option>
							<form:options items="${programList}" />
						</form:select>
					</div>
					
					<div class="form-group">
						<form:select id="sem" path="sem"  placeholder="Semester" class="form-control"  value="${bean.sem}" >
							<form:option value="">Select Semester</form:option>
							<form:option value="1">1</form:option>
							<form:option value="2">2</form:option>
							<form:option value="3">3</form:option>
							<form:option value="4">4</form:option>
						</form:select>
					</div>
					
					 <div class="form-group">
							<form:select id="subject" path="subject" type="text" required="required"	placeholder="Subject" class="form-control"   itemValue="${bean.subject}">
								<form:option value="">Select Subject</form:option>
								<form:options items="${subjectList}" />
							</form:select>
					</div>
					
					<div class="form-group">
						<label class="control-label" for="submit"></label>
						<!-- <div class="controls"> -->
							<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="getMarkCheckSheet" 
							onclick="return validate()">Generate</button>
							<button id="cancel" name="cancel" class="btn btn-danger" formaction="${pageContext.request.contextPath}/home" formnovalidate="formnovalidate">Cancel</button>
						<!-- </div> -->
					</div>
				</div>
			</fieldset>
		</form:form>
		
		<%if("true".equals((String)request.getAttribute("success"))){ %>
		<a href="/exam/admin/downloadMarkCheckSheet">Download Marks Checking Sheet</a>
		<%} %>
		</div>
	</div>
	

	</section>

	<script type="text/javascript">
		document.getElementById('offlineCenters').style.display = 'none';
		document.getElementById('onlineCenters').style.display = 'none';
		
		mode = document.getElementById('mode').value;
		if(mode == 'Offline'){
			document.getElementById('offlineCenters').style.display = '';
			document.getElementById('onlineCenters').style.display = 'none';
		}else if(mode == 'Online'){
			document.getElementById('onlineCenters').style.display = '';
			document.getElementById('offlineCenters').style.display = 'none';
		}
		
	</script>
	
	  <jsp:include page="footer.jsp" />


</body>
</html>
