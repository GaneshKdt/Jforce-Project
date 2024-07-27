<%@page import="com.nmims.beans.IdCardExamBean"%>
<%@page import="java.util.ArrayList"%>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<c:choose>
<c:when test="${not empty idCardBean.fileName }">
<div class="panel-heading" role="tab">
	<h2>My Id Card : [<a href="/studentportal/digitalidcard" target="_blank">View</a>]</h2>
	<div class="clearfix"></div>
</div>
</c:when>
<c:otherwise>
<div class="panel-heading" role="tab" >
	<h2>My ID Card &nbsp;&nbsp;<span style="color:black">[${ idCardBean.message }]</span></h2>
	<div class="clearfix"></div>
</div>
</c:otherwise>
</c:choose>