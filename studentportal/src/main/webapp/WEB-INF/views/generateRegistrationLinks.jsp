
<!DOCTYPE html>
<%@page import="java.util.*"%>
<%@page import="java.text.DateFormat"%>
<html lang="en">


<%@page import="com.nmims.beans.PageStudentPortal"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Registration Links" name="title" />
</jsp:include>
<body>
	<%@ include file="adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Registration;Registration Links"
				name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>
				<div class="sz-content-wrapper examsPage">
					<%@ include file="adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">
						<h2 class="red text-capitalize">Registration Links</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%@ include file="adminCommon/messages.jsp"%>

							<form:form action="generateRegistrationLinks" method="get"
								modelAttribute="person" id="generateRegistrationLinks">
								<fieldset>

									<div class="col-sm-3">
										<label>SAP ID</label>
										<div class="form-group">
											<form:input id="userId" path="userId" type="text"
												placeholder="SAP ID" class="form-control"
												value="${person.userId}" disabled="disabled" />
										</div>
									</div>

									<div class="col-sm-3">
										<label>Type Of Registration</label>
										<div class="form-group">
											<form:select id="registrationType" path="registrationType"
												type="text" placeholder="Year" class="form-control"
												itemValue="${person.registrationType}" required="required">
												<form:option value="">Select Registration type</form:option>
												<form:option value="PCP Registration">PCP Registration</form:option>
												<form:option value="Exam Registration">Exam Registration</form:option>
											</form:select>
										</div>
									</div>
									<div class="clearfix"></div>
									<div class="col-sm-3">
										<button id="submit" name="submit"
											class="btn btn-large btn-primary">Submit</button>
									</div>
									<div class="clearfix"></div>
									<div id="PCPRegistration" class="random">
										<button onclick="myFunction()" style="margin-left: 15px;">Copy
											text</button>
										<input type="text"
											value="https://studentzone-ngasce.nmims.edu/acads/selectPCPSubjectsForm?eid=${passEnc}"
											id="myInput" style="color: #127eea; width: 60%;">
									</div>


									<div id="ExamRegistration" class="random">
										<button onclick="myFunctionTwo()" style="margin-left: 15px;">Copy
											text</button>
										<input type="text"
											value="https://studentzone-ngasce.nmims.edu/exam/selectSubjectsForm?eid=${passEnc}"
											id="myInputTwo" style="color: #127eea; width: 60%;">
									</div>



								</fieldset>
							</form:form>
						</div>
					</div>
				</div>
			</div>

			<jsp:include page="adminCommon/footer.jsp" />
</body>

<script>

$(document).ready(function(){
	$("#PCPRegistration").hide();
	$("#ExamRegistration").hide();
    $("#generateRegistrationLinks").submit(function(){
    	var type=$('#registrationType').val();
    	if(type=='PCP Registration') {
    		$("#PCPRegistration").show();
        	}
        	 if(type=='Exam Registration') {
        		$("#ExamRegistration").show();
        	} 
    	
});
    var type=$('#registrationType').val();
	if(type=='PCP Registration') {
		$("#PCPRegistration").show();
    	}
    	 if(type=='Exam Registration') {
    		$("#ExamRegistration").show();
    	} 
});
function myFunction() {
	  var copyText = document.getElementById("myInput");
	  copyText.select();
	  document.execCommand("copy");
	  alert('Link Copied!')
	}
function myFunctionTwo() {
	  var copyText = document.getElementById("myInputTwo");
	  copyText.select();
	  document.execCommand("copy");
	  alert('Link Copied!')
	}
</script>
</html>

