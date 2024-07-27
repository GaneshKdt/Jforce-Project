<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html class="no-js"> <!--<![endif]-->
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.10.20/css/jquery.dataTables.css">
<jsp:include page="../jscss.jsp">
<jsp:param value="Insert Exam Booking Records for TCS" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="../header.jsp"%>
	
    <section class="content-container">
        <div class="container-fluid customTheme">
       <div class="row"> <legend>Insert Exam Booking Records for TCS</legend></div>
        <%@ include file="../messages.jsp"%>
		<div id="responseMsg"></div>
		<%-- <form:form action= "uploadExamBookingData" modelAttribute="studentMarks"> --%>
		<div class="row">
			<div class="col-md-3">
				<div class="form-group">
				<label >Exam Year</label>
						<select id="year" >
							<option value="">Select Exam Year</option>
							<c:forEach var="examYear" items="${examYearList}">
						         <option value="<c:out value="${examYear}"/>"><c:out value="${examYear}"/></option>
				            </c:forEach>		
						</select>
				</div>
			</div>
			<div class="col-md-3">
				<div class="form-group">
				<label >Exam Month</label>
						<select id="month" >
							<option value="">Select Exam Month</option>
							<c:forEach var="examMonth" items="${examMonthList}">
						    	<option value="<c:out value="${examMonth}"/>"> <c:out value="${examMonth}"/> </option>
				            </c:forEach>
						</select>
				</div>
			</div>
			
			<br>
				<input  type="button" class="btn btn-large btn-primary col-md-3" value="Upload Exam Booking Data To TCS" id="btnSearch" onclick="uploadExamBookingData()" />
			
		</div>	
			
		

		
	
	
<%-- 	<c:choose> --%>
<%-- 	<c:when test="${rowCount > 0}"> --%>
<!-- 			<div class="panel-body"> -->
<%-- 			<legend>&nbsp;Absent Records<font size="2px"> (${rowCount} Records Found) &nbsp; <a href="downloadABReport">Download AB report to verify before insertion</a></font></legend> --%>
<!-- 			<a  class="btn btn-large btn-primary"  href="/exam/admin/insertABReport">Insert AB Records</a>			 -->
<!-- 			</div> -->
<%-- 	</c:when> --%>
<%-- 	</c:choose> --%>
	</div>
	</section>

	  <jsp:include page="../footer.jsp" />
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery.tabledit.js"></script>
	<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
	<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js"></script>
	

</body>

<script>



function hideSearchBtn(){
	console.log('call hide');
	$( "#btnSearch" ).replaceWith( "<img id='theImg' src='/exam/resources_2015/gifs/loading-29.gif' style='height:40px' />" );
}

function showSearchBtn(){
	$( "#theImg" ).replaceWith( '<input  type="button" class="btn btn-large btn-primary col-md-3" value="Upload Exam Booking Data To TCS" id="btnSearch" onclick="uploadExamBookingData()" />' );
}




function uploadExamBookingData(){
	var examYear  = document.getElementById("year").value;		
	var examMonth = document.getElementById("month").value; 
	    if (examYear == "" || examMonth == "" )
	   {
	    alert("Please Select Both Year & Month Field ");
	    return false;
	   }
	hideSearchBtn();
	 $('#searchBody').css("display", "none");
	 var promiseObj = new Promise(function(resolve, reject){
	////console.log("In saveAnswerAjax() ENTERED...");
	var methodRetruns = false;
	//ajax to save question reponse start
	var examYear = $('#year').val();
	var examMonth = $('#month').val();
	console.log(examYear+' '+examMonth);
	var body = {
		'year' : examYear,
		'month' : examMonth
		
	};
	////console.log(body);
	$.ajax({
		type : 'POST',
		url : 'uploadExamBookingData',
		data: JSON.stringify(body),
       contentType: "application/json",
       dataType : "json",
       
	}).done(function(data) {
		  console.log("iN AJAX SUCCESS");
     	
		
		

		if(data.code == 200){
			var messageSuccess = '';
        	messageSuccess += '<div class="alert alert-success alert-dismissible">';
    		messageSuccess += '<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times;  </button>';
    		messageSuccess += data.message;
    		messageSuccess += '</div>';
			$('#responseMsg').html(messageSuccess);
         }

		
     	
     	if(data.code == 422){
         	console.log('error : '+data.message);
         	
     		var messageError = '';
     		messageError += '<div class="alert alert-danger alert-dismissible">';
    		messageError += '<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times;  </button>';
    		messageError += data.message;
    		messageError += '</div>';
			$('#responseMsg').html(messageError);
        }

     	showSearchBtn();
     	
     	methodRetruns= true;
			////console.log("In saveAnswerAjax() EXIT... got methodRetruns: "+methodRetruns);
			resolve(methodRetruns);
	}).fail(function(xhr) {
		////console.log("iN AJAX eRROR");
		
		
		console.log("inside error");
	 })
	 return promiseObj;

});
}

</script>
</html>
