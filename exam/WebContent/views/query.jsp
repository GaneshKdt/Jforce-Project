<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Query" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Query</legend></div>
        <%@ include file="messages.jsp"%>
		<div class="row clearfix">
		<form  action="query" method="post" >
			<fieldset>
			<div class="col-md-12 column">
					
					<div class="form-group">
					<textarea rows="3" name="sql" placeholder="Enter Query"></textarea>
							
					</div>

				<!-- Button (Double) -->
				<div class="form-group">
					<label class="control-label" for="submit"></label>
					<div class="controls">
						<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="query">Query</button>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
					</div>
				</div>

			

</div>



</fieldset>
		</form>
		
		</div>
	</div>
	
	</section>

	  <jsp:include page="footer.jsp" />


</body>
</html>