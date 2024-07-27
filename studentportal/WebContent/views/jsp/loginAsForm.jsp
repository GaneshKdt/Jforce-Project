<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix='c' uri='http://java.sun.com/jsp/jstl/core' %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link href="https://fonts.cdnfonts.com/css/aller" rel="stylesheet">
                
<title>LoginAsForm</title>
<style>
body{
	margin:0px !important;
	padding:0px !important;
	background-color:#E4E4E4;
	font-family: 'Aller' !important;
}
.header{
	background-color:#C72027; 
}
.header img{
	max-height:100px;
	border-radius:0px 10px 10px 0px;
}
.sub-header{
	padding:10px 20px;
	background-image: url("https://d5fsf5hqkq44r.cloudfront.net/resources_2015/images/headerBg.jpg");
	background-position:center; 
	background-size:cover;
	background-repeat: no-repeat;
	font-size:30px;
	color:white;  
}
.card{
	background-color:white;
	padding:20px 20px;
	margin:10px;
	border-radius:5px; 
}
.input-box{
	display:block;
	width:100%;
	max-width:300px;
	padding: 0.375rem 0.75rem;
	outline:none; 
	border:1px solid #ced4da;
	margin-top:10px; 
	border-radius: 0.25rem;
	height:30px;
	font-size: 1rem;
	line-height: 1.5;
	color: #495057;
	transition: border-color .15s ease-in-out,box-shadow .15s ease-in-out;
	background-color: #fff;
    background-clip: padding-box;
}   
.btn-primary{
	margin-top:10px;
	padding:10px 20px;
	background-color:#C72027;
	color:white;
	border:none;
	border-radius:3px;
	font-size:15px;
	cursor:pointer;
}
.alert {
    position: relative;
    padding: 0.75rem 1.25rem;
    margin-bottom: 1rem;
    border: 1px solid transparent;
    border-radius: 0.25rem;
}
.alert-danger {
    color: #721c24;
    background-color: #f8d7da;
    border-color: #f5c6cb;
}

</style>
</head>
<body>
<div class="header"> 
	<img src="https://d5fsf5hqkq44r.cloudfront.net/resources_2015/images/logo.jpg" />
</div>
<div class="sub-header">
	Login As Form 
</div>
<div class="card">
<c:if test="${ error == 'true' }">
	<div class="alert alert-danger" role="alert">
		<c:out value = "${ errorMessage }"/>
	</div> 
</c:if>

	<form action="loginAsNew" method="POST">
	<input class="input-box" type="text" placeholder="username" name="username"/>
	<input class="input-box" type="password" placeholder="password" name="password"/>
	<button class="btn-primary" type="submit">Login</button>
</form>
</div>

</body>
</html>