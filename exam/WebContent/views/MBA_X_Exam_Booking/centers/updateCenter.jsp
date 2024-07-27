<!DOCTYPE html>

<%@page import="com.nmims.beans.MBACentersBean"%>
<%@page import="com.nmims.beans.AssignmentStatusBean"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>

<html class="no-js">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="../../jscss.jsp">
	<jsp:param value="Upload MBA-X Center list" name="title" />
</jsp:include>
<head>
	<link rel="stylesheet" href="https://cdn.datatables.net/1.10.19/css/jquery.dataTables.min.css">
	<style>
		.dataTables_filter > label > input{
			float:right !important;
		}
		.toggleListWell{
		cursor: pointer !important;
			margin-bottom:0px !important;
		}
		.toggleWell{
			background-color:white !important;
		}
		input[type="radio"]{
			width:auto !important;
			height:auto !important;
			
		}
		.optionsWell{
			padding:0px 10px;
		}
	</style>
</head>
<body class="inside">
	<%@ include file="../../header.jsp"%>
	<section class="content-container login">
		<div class="container-fluid customTheme">
			<fieldset class="row">
				<legend>Update MBA-X Center</legend>
			</fieldset>
			<%@ include file="../../messages.jsp"%>

			<form:form modelAttribute="centerBean" method="post">
				<div class="panel-body">
					<form:hidden path="centerId" id="centerId" />
					<div class="col-md-4 column">
						<div class="form-group">
							<label for="name">Center Name</label>
							<form:input path="name" id="name" />
						</div>
					</div>
					
					<div class="col-md-4 column">
						<div class="form-group">
							<label for="city">City</label>
							<form:input path="city" id="city" />
						</div>
					</div>
					
					<div class="col-md-4 column">
						<div class="form-group">
							<label for="state">State</label>
							<form:input path="state" id="state" />
						</div>
					</div>
					
					<div class="col-md-4 column">
						<div class="form-group">
							<label for="capacity">Capacity</label>
							<form:input path="capacity" id="capacity" />
						</div>
					</div>
					
					<div class="col-md-4 column">
						<div class="form-group">
							<label for="locality">Locality</label>
							<form:input path="locality" id="locality" />
						</div>
					</div>
					
					<div class="col-md-4 column">
						<div class="form-group">
							<label for="address">Address</label>
							<form:input path="address" id="address" />
						</div>
					</div>
					
					<div class="col-md-4 column">
						<div class="form-group">
							<label for="googleMapUrl">Google Map URL</label>
							<form:input path="googleMapUrl" id="googleMapUrl" />
						</div>
					</div>
					
					<div class="col-md-4 column">
						<div class="form-group">
							<label for="active">Active</label>
							<form:select path="active" id="active" >
								<form:option value="Y"/>
								<form:option value="N"/>
							</form:select>
						</div>
					</div>
				</div>
				<br>
				<div class="row">
					<div class="col-md-6 column">
						<a class="btn btn-large btn-danger" href="uploadCentersForm_MBAX">
							Cancel
						</a>
						<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="updateCenter_MBAX">
							Update
						</button>
					</div>
				</div>
			</form:form>
		</div>
		<br/><br/>
	</section>
	<jsp:include page="../../footer.jsp" />
</body>
</html>
