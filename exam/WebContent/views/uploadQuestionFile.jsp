<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->


<%@page import="com.nmims.beans.QuestionFileBean"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Upload Question File" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">

			<div class="row"><legend>Upload Question File for Verification</legend></div>
			
		<%@ include file="messages.jsp"%>
		
		<%

		List<QuestionFileBean> errorBeanList = (ArrayList<QuestionFileBean>)request.getAttribute("errorBeanList"); 
		if(errorBeanList != null && errorBeanList.size() > 0){ 
			%>
					
			<div class="alert alert-danger">
		<%	
				for(int i = 0 ; i < errorBeanList.size() ; i++ ){
					QuestionFileBean bean = (QuestionFileBean)errorBeanList.get(i);
					out.println(bean.getErrorMessage());
					out.println("<br/>");
				}//End of for
				out.println("</div>");
			}//End of if
		
		%>
		
		
		
		<form:form modelAttribute="fileBean" method="post"	enctype="multipart/form-data" action="uploadQuestionFile">
			<div class="panel-body">
			<div class="col-md-6 column">
				<!--   -->

				<form:label for="fileData" path="fileData">Select file</form:label>
				<form:input path="fileData" type="file" />
			</div>
			
			
			</div>
			<br>
			<div class="row">
				<div class="col-md-6 column">
					<button id="submit" name="submit" class="btn btn-large btn-primary"
						formaction="uploadQuestionFile">Upload</button>
				</div>

				
			</div>
			</form:form>
		</div>
	</section>

	<jsp:include page="footer.jsp" />


</body>
</html>
