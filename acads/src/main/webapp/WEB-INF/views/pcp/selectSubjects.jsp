<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.PCPBookingTransactionBean"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.nmims.beans.StudentBean"%>

<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="../jscss.jsp">
	<jsp:param value="Select subjets for PCP/VC" name="title" />
</jsp:include>

<script language="JavaScript">
	function validateForm() {
		
		var assignmentSubmittedList = document.getElementsByName('applicableSubjects');
		var atleastOneSelected = false;
		for(var i = 0; i < assignmentSubmittedList.length; ++i)
		{
		    if(assignmentSubmittedList[i].checked){
		    	atleastOneSelected = true;
		    	break;
		    }
		}
		if(!atleastOneSelected){
			alert("Please select at least one subject to proceed.");
			return false;
		}
		
		for(var i = 0; i < assignmentSubmittedList.length; ++i)
		{
		    if(!assignmentSubmittedList[i].checked){
		    	return confirm('You have not selected some of the subjects for PCP/VC Registration. Are you sure you want to proceed to next step?');
		    }
		}
		
		return true;
	}
	
	
	
	function validateFreeSubjectsForm() {
		
		var assignmentSubmittedList = document.getElementsByName('freeApplicableSubjects');
		var atleastOneSelected = false;
		for(var i = 0; i < assignmentSubmittedList.length; ++i)
		{
		    if(assignmentSubmittedList[i].checked){
		    	atleastOneSelected = true;
		    	break;
		    }
		}
		if(!atleastOneSelected){
			alert("Please select at least one subject to proceed.")
			return false;
		}
		document.getElementById("subjectForm").action = 'selectExamCenterForFree';
		document.getElementById("subjectForm").novalidate = 'novalidate';
		for(var i = 0; i < assignmentSubmittedList.length; ++i)
		{
		    if(!assignmentSubmittedList[i].checked){
		    	return submit =  confirm('You have not selected some of the subjects for exam. Are you sure you want to proceed to next step?');
		    }
		}
		
		
		document.getElementById("subjectForm").submit();
		//document.forms["subjectForm"].submit();
		//window.location.href='selectExamCenterForFree';
		//return true;
	}
</script>

<body class="inside">

	<%@ include file="../header.jsp"%>
	<%
	ArrayList<PCPBookingTransactionBean> applicableSubjectsList = (ArrayList<PCPBookingTransactionBean>)request.getAttribute("subjects");
	%>
	<section class="content-container login">
		<div class="container-fluid customTheme">
		

			<div class="row clearfix">
					<legend>Select subjects for PCP/VC Registration </legend>
			</div>
			
			<%@ include file="../messages.jsp"%>
			<div class="panel-body">
			
			<div><a href="#" class="" onClick="window.open('resources_2015/PCP_Guidelines.pdf')"><i class="fa fa-download fa-lg"></i> <b> Download PCP/VC Guidelines </b></a>
			&nbsp;&nbsp;&nbsp;&nbsp;
			<a href="#" class="" onClick="window.open('resources_2015/PCP_Registration_Steps.pdf')"><i class="fa fa-download fa-lg"></i> <b>Download PCP/VC Registration Steps</b></a></div>
			
			<div class="col-md-18 column">
				<c:if test="${subjectsCount > 0}">
				<div>
					<div class="table-responsive">
					<form:form id="subjectForm" action="confirmSubjects" method="post" modelAttribute="pcpBooking" >
						<fieldset>
						
						<form:hidden path="year"/>
						<form:hidden path="month"/>
						
						<table class="table table-striped" style="font-size:12px">
						<thead>
							<tr> 
								<th>Sr. No.</th>
								<th>Program</th>
								<th>Sem</th>
								<th>Subject</th>
								<th>Select to Book</th>
								<th>Booking Status</th>
							</tr>
						</thead>
						<tbody>
						<% int count = 0; 
						
						for(int i = 0; i < applicableSubjectsList.size() ; i++){
							PCPBookingTransactionBean bean = applicableSubjectsList.get(i);
							count++;
							String studentProgram = bean.getProgram();
							String sem = bean.getSem();
							String subject = bean.getSubject();
							String booked = bean.getBooked();
							String center = bean.getCenter();
						 %>
						
						 	<tr>
					            <td><c:out value="<%=count %>"/></td>
					            <td><c:out value="<%=studentProgram %>"/></td>
					            <td><c:out value="<%=sem %>"/></td>
								<td nowrap="nowrap"><c:out value="<%=subject %>"/></td>
								
								
								<td>
								<%if(!"Y".equals(booked)){ %>
									<form:checkbox path="applicableSubjects" value="<%=subject %>"  />
								<%} %>
					            </td>
					            
					            <td>
								<%if("Y".equals(booked)){ %>
									Booked
								<%}else{ %>
									Not Booked
								<%} %>
					            </td>
					        </tr>   
						        
						<%} %>
							
						</tbody>
					</table>
					<div class="form-group">
						<label class="control-label" for="submit"></label>
						<div class="controls">
							<c:if test="${hasBookedSubject == 'true' }">
								<button id="download" name="download" class="btn btn-large btn-primary" formaction="downloadEntryPass">Download Entry Pass</button>
							</c:if>
								
							<c:if test="${isRegistraionLive == 'true' }"> 
							
								<c:if test="${hasSubjectsToBook == 'true' }">
									<button id="submit" name="submit" class="btn btn-large btn-primary" onclick="return validateForm();">Proceed to Payment</button>
								</c:if>
								
								<button id="cancel" name="cancel" type="button" class="btn btn-danger" onclick="window.location.href='/studentportal/home'" formnovalidate="formnovalidate">Back</button>
								
							</c:if>
						</div>
					</div>
				</fieldset>
				</form:form>
				</div>
				</div>
			</c:if>
			</div>
						
			</div>
			
		</div>
	</section>



	<jsp:include page="../footer.jsp" />
</body>
</html>
 --%>
 
 <!DOCTYPE html>
<%@page import="com.nmims.beans.PersonAcads"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.PCPBookingTransactionBean"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.nmims.beans.StudentBean"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%
	ArrayList<PCPBookingTransactionBean> applicableSubjectsList = (ArrayList<PCPBookingTransactionBean>)request.getAttribute("subjects");
%>
	
	
<html lang="en">
    

	
    
    <jsp:include page="../common/jscss.jsp">
	<jsp:param value="Select subjects for PCP" name="title"/>
    </jsp:include>
    
    <script language="JavaScript">
		function validateForm() {
			
			var assignmentSubmittedList = document.getElementsByName('applicableSubjects');
			
			var atleastOneSelected = false;
			for(var i = 0; i < assignmentSubmittedList.length; ++i)
			{
			    if(assignmentSubmittedList[i].checked){
			    	atleastOneSelected = true;
			    	break;
			    }
			}
			if(!atleastOneSelected){
				alert("Please select at least one subject to proceed.");
				return false;
			}
			
			for(var i = 0; i < assignmentSubmittedList.length; ++i)
			{
			    if(!assignmentSubmittedList[i].checked){
			    	return confirm('You have not selected some of the subjects for PCP Registration. Are you sure you want to proceed to next step?');
			    }
			}
			
			return true;
		}
		
		
		
		function validateFreeSubjectsForm() {
			
			var assignmentSubmittedList = document.getElementsByName('freeApplicableSubjects');
			var atleastOneSelected = false;
			for(var i = 0; i < assignmentSubmittedList.length; ++i)
			{
			    if(assignmentSubmittedList[i].checked){
			    	atleastOneSelected = true;
			    	break;
			    }
			}
			if(!atleastOneSelected){
				alert("Please select at least one subject to proceed.")
				return false;
			}
			document.getElementById("subjectForm").action = 'selectExamCenterForFree';
			document.getElementById("subjectForm").novalidate = 'novalidate';
			for(var i = 0; i < assignmentSubmittedList.length; ++i)
			{
			    if(!assignmentSubmittedList[i].checked){
			    	return submit =  confirm('You have not selected some of the subjects for exam. Are you sure you want to proceed to next step?');
			    }
			}
			
			
			document.getElementById("subjectForm").submit();
			//document.forms["subjectForm"].submit();
			//window.location.href='selectExamCenterForFree';
			//return true;
		}
	</script>
    
    
    <body>
    
    	<%@ include file="../common/header.jsp" %>
    	
        
        <div class="sz-main-content-wrapper">
        
        	<%@ include file="../common/breadcrum.jsp" %>
        	<jsp:include page="../common/breadcrum.jsp">
			<jsp:param value="Student Zone;PCP/VC Registration" name="breadcrumItems"/>
			</jsp:include>
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="../common/left-sidebar.jsp">
								<jsp:param value="PCP/VC Registration" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="../common/studentInfoBar.jsp" %>
              						
              						
              						<div class="sz-content">
								
										<h2 class="red text-capitalize">Select subjects for PCP Registration</h2>
										<div class="clearfix"></div>
		              					<div class="panel-content-wrapper">
											<%@ include file="../common/messages.jsp" %>
											
											<div><a href="#" class="" onClick="window.open('resources_2015/PCP_Guidelines_Jan_2017.pdf')"><i class="fa fa-download fa-lg"></i> <b> Download PCP Guidelines </b></a>
											&nbsp;&nbsp;&nbsp;&nbsp;
											<a href="#" class="" onClick="window.open('resources_2015/PCP_Registration_Guide_Jan_2017 .pdf')"><i class="fa fa-download fa-lg"></i> <b>Download PCP Registration Steps</b></a></div>
											<div class="clearfix"></div>
										
											<c:if test="${subjectsCount > 0}">
												<div>
													<div class="table-responsive">
													<form:form id="subjectForm" action="confirmSubjects" method="post" modelAttribute="pcpBooking" >
														<fieldset>
														
														<form:hidden path="year"/>
														<form:hidden path="month"/>
														
														<table class="table table-striped" style="font-size:12px">
														<thead>
															<tr> 
																<th>Sr. No.</th>
																<th>Program</th>
																<th>Sem</th>
																<th>Subject</th>
																<th>Select to Book</th>
																<th>Booking Status</th>
															</tr>
														</thead>
														<tbody>
														<% int count = 0; 
														
														for(int i = 0; i < applicableSubjectsList.size() ; i++){
															PCPBookingTransactionBean bean = applicableSubjectsList.get(i);
															count++;
															String studentProgram = bean.getProgram();
															String sem = bean.getSem();
															String subject = bean.getSubject();
															String booked = bean.getBooked();
															String center = bean.getCenter();
															if("Project".equalsIgnoreCase(bean.getSubject()) || "Module 4 - Project".equalsIgnoreCase(bean.getSubject())){
																continue;
															}
														 %>
														
														 	<tr>
													            <td><c:out value="<%=count %>"/></td>
													            <td><c:out value="<%=studentProgram %>"/></td>
													            <td><c:out value="<%=sem %>"/></td>
																<td nowrap="nowrap"><c:out value="<%=subject %>"/></td>
																
																
																<td>
																<%if(!"Y".equals(booked)){ %>
																	<form:checkbox path="applicableSubjects" value="<%=subject %>" />
																<%} %>
													            </td>
													            
													            <td>
																<%if("Y".equals(booked)){ %>
																	Booked
																<%}else{ %>
																	Not Booked
																<%} %>
													            </td>
													        </tr>   
														        
														<%} %>
															
														</tbody>
													</table>
													<div class="form-group">
														<label class="control-label" for="submit"></label>
														<div class="controls">
															<c:if test="${hasBookedSubject == 'true' }">
																<button id="download" name="download" class="btn btn-large btn-primary" formaction="downloadEntryPass">Download Entry Pass</button>
															</c:if>
																
															<c:if test="${isRegistraionLive == 'true' }"> 
															
																<c:if test="${hasSubjectsToBook == 'true' }">
																	<button id="submit" name="submit" class="btn btn-large btn-primary" onclick="return validateForm();">Proceed to Payment</button>
																</c:if>
																
																<button id="cancel" name="cancel" type="button" class="btn btn-danger" onclick="window.location.href='/studentportal/home'" formnovalidate="formnovalidate">Back</button>
																
															</c:if>
														</div>
													</div>
												</fieldset>
												</form:form>
												</div>
												</div>
											</c:if>
											
										</div>
              								
              						</div>
              				</div>
              		
                            
					</div>
            </div>
        </div>
            
  	
        <jsp:include page="../common/footer.jsp"/>
            
		
    </body>
</html>