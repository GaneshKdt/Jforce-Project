<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Select Center to get Marksheet" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="limitedAccessHeader.jsp"%>

	<section class="content-container">

		<div class="container-fluid customTheme">
		<div class="row clearfix">
		<legend>&nbsp;Select University Information Center to receive Marksheet</legend>
		<%@ include file="messages.jsp"%>
		<div class="col-md-6 column">
		
			<form:form action="saveStudentCenter" method="post" modelAttribute="center">
				<fieldset>
						<div class="form-group">
							<form:select id="center" path="centerCode" type="text" placeholder="Center Code" class="form-control" itemValue="${center.centerCode}" required="required">
								<form:option value="">Select Center</form:option>
								<form:options items="${centerCodes}" />
							</form:select>
						</div>
						
							<div class="controls">
								<button id="submit" name="submit" class="btn btn-large btn-primary"	formaction="saveStudentCenter">Save as My Center</button>
								<button id="cancel" name="cancel" class="btn btn-danger" formaction="studentHome" formnovalidate="formnovalidate">Cancel</button>
							</div>
						
					
				</fieldset>
				</form:form>
			</div>
			</div>
			<br/>
			<div class="row">
			<div class="col-md-18 column">
			
			<b>Note to the Students:<br/><br/>	
			From June 2014 onwards, it is mandatory for students to get attached to an NGA-SCE Authorized Information Center (IC). </b><br/><br/>
			
			<p align="justify">Students will have to select an IC when the June 2014 results are displayed. Mark sheets of the 
			June 2014 Examination will be dispatched to the selected IC. Students should collect his/her mark sheet from the selected IC. <br/>
			<i>Students are requested not to visit the University office for collecting the June 2014 mark sheet.</i><br/><br/>
			<b>P.S.: </b> The selection of Information Center is applicable to students who have not re-registered with any Information Center. Selection of IC is also not applicable for Corporate Batch students. 
			</p>
			<br/>
			<b>Role of the IC:</b><br/>
			
			<p align="justify">
			NGA-SCE has set up Information Centre's at various locations across India. 
			Information Center means a center set up for the purpose of student support. 
			The prime responsibility of an IC is to facilitate the admission and Re-Registration processes as well as 
			provide student support services. IC's will also act as intermediaries for passing on the study material issued by 
			the University, to the registered students. Going forward, students will have to select their IC during Re-registration process. <br/><br/>
			
			Once the Examination Results are declared, students can collect their Marksheet from their IC within the timeframe set and 
			announced by NGA-SCE. Keeping this in mind, students must be careful in selecting their IC. 
			It is advisable that the student chooses an IC which is convenient and within his/her proximity. 
			This will ensure that the student is able to visit the IC easily in the future for all student related activities.
			</p>
			
			<b>IMPORTANT:  </b>
			<ul>
			<li><b><i>IC's are not authorized to collect any additional fees from the students for NMIMS programs. 
			Any personal dealing with Information Centers will be at the student's risk.</i></b></li>
			<li><b><i>IC's are neither involved in any Academic Process nor do 
			they conduct Personal Contact Programs and/or Examinations for any NMIMS course.</i></b></li>
			<li><b><i>Details of NGA-SCE Authorized Information Centers are available on the school website:</i></b><br/><br/>
			<a href="http://distance.nmims.edu/centers.html" target="_blank">http://distance.nmims.edu/centers.html</a>
			</li>
			</ul>
			</div>
			
		</div>
	</div>
	</section>

	<jsp:include page="footer.jsp" />

</body>
</html>
