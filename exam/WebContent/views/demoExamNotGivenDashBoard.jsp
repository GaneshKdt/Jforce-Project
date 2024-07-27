<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 


<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Demo Exam DashBoard" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Demo Exam DashBoard</legend></div>
				<div class="col-md-4">
					<a href="/exam/admin/demoExamDashBoard">
					<div class="card" style="background-color:#FF5733;color:white;padding:10px 20px">
					  <h3>Attend Student Count</h3> <hr/>
					  <h3>${ attendStudentCount }</h3>
					  <p>Click here to view list</p>
					</div>
					</a>
				</div>
				<div class="col-md-4">
					<a href="/exam/admin/demoExamOutGivenDashBoard">
					<div class="card" style="background-color: #0CBF83;color:white;padding:10px 20px">
					  <h3>Not Attend Student Count</h3> <hr/>
					  <h3>${ notAttendStudentCount }</h3>
					  <p>Click here to view list</p>
					</div>
					</a>
				</div>
		</div>
		<br/>
		<div class="container-fluid">
		<table class="table">
			<thead>
				<tr>
					<td>sapid</td>
					<td>first name</td>
					<td>last name</td>
					<td>email</td>
					<td>mobile</td>
				</tr>
			</thead>
			<tbody>
			<c:forEach var="attendStudent" items="${notAttendStudentList}">
				<tr>
					<td>${attendStudent.sapid }</td>
					<td>${attendStudent.firstName }</td>
					<td>${attendStudent.lastName }</td>
					<td>${attendStudent.emailId }</td>
					<td>${attendStudent.mobile }</td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
		</div>
	
	</section>

	  <jsp:include page="footer.jsp" />
	  
	  <script>
		  $(document).ready(function(){
			$('.table').DataTable();
		  });
	  </script>

</body>
</html>
