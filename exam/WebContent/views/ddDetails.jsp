<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<%@page import="com.nmims.beans.StudentBean"%>

<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="jscss.jsp">
<jsp:param value="Enter DD Details" name="title" />
</jsp:include>

<script type="text/javascript">
	function isNumberKey(evt){
	    var charCode = (evt.which) ? evt.which : event.keyCode
	    if (charCode > 31 && (charCode < 48 || charCode > 57)){
	    	return false;
	    }
	        
	    return true;
	}

</script>

<body class="inside">

<%@ include file="header.jsp"%>
	<%
			StudentBean student = (StudentBean)session.getAttribute("student");
			String sapId = student.getSapid();
			%>
    <section class="content-container login">
        <div class="container-fluid customTheme">
        
        <div class="row">
			<legend>Enter DD Details</legend>
		</div>
			
			
        
        <%@ include file="messages.jsp"%>
		<div class="panel-body clearfix">
		<form:form  action="saveDDDetails" method="post" modelAttribute="ddDetails" id="ddDetails"  >
			<fieldset>
			<div class="col-md-6 column">
	
					<div class="form-group">
						<form:label path="ddno" for="ddno">Demand Draft Number</form:label>
						<form:input id="ddno" path="ddno" type="text" placeholder="Demand Draft Number" class="form-control" maxlength="6"
						value="${ddDetails.ddno}" onkeypress="return isNumberKey(event)" />
					</div>
					
					<div class="form-group">
						<form:label path="bank" for="bank">Bank Name</form:label>
						<form:input id="bank" path="bank" type="text" placeholder="Bank Name" class="form-control" value="${ddDetails.bank}" />
					</div>
					
					<div class="form-group">
						<form:label path="ddDate" for="ddDate">DD Date</form:label>
						<form:input id="ddDate" path="ddDate" type="date" placeholder="DD Date" class="form-control" value="${ddDetails.ddDate}" />
					</div>
					
					<div class="form-group">
						<form:label path="amount" for="amount">DD Amount</form:label>
						<form:input id="amount" path="amount" type="text" class="form-control" 
						 readonly="true"/>
					</div>
					
						
					<div class="form-group">
						<label class="control-label" for="submit"></label>
							<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="saveDDDetails" 
							onclick="return confirm('Are you sure you want to save these DD details? You will not be allowed to change the same later.')">Save DD</button>
							<button id="cancel" formnovalidate="formnovalidate"  name="cancel" class="btn btn-danger" formaction="selectPaymentMode"  onclick="window.history.back()">Back</button>
							
					</div>
				</div>
				
				<div class="col-sm-9">
				<p align="justify">
					Please be careful when you enter DD details as you will not be allowed to change DD details later. 
					You will not be allowed to select exam center of your choice till DD is received at <b>NMIMS University Head Office (Not LC) and same is approved.</b>
					 After the DD is approved you will be notified to select exam center and complete exam registration process.<br/><br/>
					Exam Fee payment by DD is available only till <spring:eval expression="@propertyConfigurer.getProperty('EXAM_FEE_DD_LAST_DATE')" />. <br/><br/>
					Please send/submit the DD to your respective Learning Center along with printout of Exam Portal DD details page. 
					Demand Draft is to be drawn only in favor of "SVKM's NMIMS", payable at Mumbai.  <br/><br/>
					We encourage you to select Online payment mode for faster Exam Registration.
					For any queries mail only to <a href="mailto:ngasce.exams@nmims.edu" target="_top">ngasce.exams@nmims.edu</a>
					
					
					<br><br>
		
				</p>
			</div>
			
			
			</fieldset>
		</form:form>
		
		</div>
	</div>
	
	


	</section>

	  <jsp:include page="footer.jsp" />

	<script>
		$(function() {
		  $( "#ddDetails" ).validate({
			rules: {
				ddno: {
					required: true,
					minlength: 6},
					bank: {
					required: true},
					ddDate: {
					required: true}
		  }
			  
		  });
		});
	</script>
	
</body>
</html>
 --%>
 <!DOCTYPE html>
<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<%@page import="com.nmims.beans.StudentExamBean"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>


<script type="text/javascript">
	function isNumberKey(evt){
	    var charCode = (evt.which) ? evt.which : event.keyCode
	    if (charCode > 31 && (charCode < 48 || charCode > 57)){
	    	return false;
	    }
	        
	    return true;
	}

</script>
	
<html lang="en">
	
    
    <jsp:include page="common/jscss.jsp">
	<jsp:param value="Enter DD Details" name="title"/>
    </jsp:include>
    
   
    
    <body>
    
    	<%@ include file="common/header.jsp" %>
    	
    	
        
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="common/breadcrum.jsp">
			<jsp:param value="Exam;DD Details" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="common/left-sidebar.jsp">
								<jsp:param value="Exam Registration" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="common/studentInfoBar.jsp" %>
              						
              						
              						<div class="sz-content">
								
										<h2 class="red text-capitalize">Enter DD Details</h2>
										<div class="clearfix"></div>
	             						<div class="panel-content-wrapper">
										<%@ include file="common/messages.jsp" %>
										
										<form:form  action="saveDDDetails" method="post" modelAttribute="ddDetails" id="ddDetails"  >
											<fieldset>
											<div class="col-md-6 column">
									
													<div class="form-group">
														<form:label path="ddno" for="ddno">Demand Draft Number</form:label>
														<form:input id="ddno" path="ddno" type="text" placeholder="Demand Draft Number" class="form-control" maxlength="6"
														value="${ddDetails.ddno}" onkeypress="return isNumberKey(event)" />
													</div>
													
													<div class="form-group">
														<form:label path="bank" for="bank">Bank Name</form:label>
														<form:input id="bank" path="bank" type="text" placeholder="Bank Name" class="form-control" value="${ddDetails.bank}" />
													</div>
													
													<div class="form-group">
														<form:label path="ddDate" for="ddDate">DD Date</form:label>
														<form:input id="ddDate" path="ddDate" type="date" placeholder="DD Date" class="form-control" value="${ddDetails.ddDate}" />
													</div>
													
													<div class="form-group">
														<form:label path="amount" for="amount">DD Amount</form:label>
														<form:input id="amount" path="amount" type="text" class="form-control" 
														 readonly="true"/>
													</div>
													
														
													<div class="form-group">
														<label class="control-label" for="submit"></label>
															<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="saveDDDetails" 
															onclick="return confirm('Are you sure you want to save these DD details? You will not be allowed to change the same later.')">Save DD</button>
															<button id="cancel" formnovalidate="formnovalidate"  name="cancel" class="btn btn-danger" formaction="selectPaymentMode"  onclick="window.history.back()">Back</button>
															
													</div>
												</div>
												
												<div class="col-sm-9">
												<p align="justify">
													Please be careful when you enter DD details as you will not be allowed to change DD details later. 
													You will not be allowed to select exam center of your choice till DD is received at <b>NMIMS University Head Office (Not LC) and same is approved.</b>
													 After the DD is approved you will be notified to select exam center and complete exam registration process.<br/><br/>
													Exam Fee payment by DD is available only till <spring:eval expression="@propertyConfigurer.getProperty('EXAM_FEE_DD_LAST_DATE')" />. <br/><br/>
													Please send/submit the DD to your respective Learning Center along with printout of Exam Portal DD details page. 
													Demand Draft is to be drawn only in favor of "SVKM's NMIMS", payable at Mumbai.  <br/><br/>
													We encourage you to select Online payment mode for faster Exam Registration.
													For any queries mail only to <a href="mailto:ngasce.exams@nmims.edu" target="_top">ngasce.exams@nmims.edu</a>
													<br><br>
											</p>
											</div>
											</fieldset>
									</form:form>
												</div>
												</div>
											</div>
              							<div>
											
										</div>		
              						</div>
              				</div>
              		
                            
					</div>
      
            
  	
        <jsp:include page="common/footer.jsp"/>
            <script>
		$(function() {
		  $( "#ddDetails" ).validate({
			rules: {
				ddno: {
					required: true,
					minlength: 6},
					bank: {
					required: true},
					ddDate: {
					required: true}
		  }
			  
		  });
		});
	</script>
		
    </body>
</html>