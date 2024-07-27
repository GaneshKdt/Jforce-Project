<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 


<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Announcement" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="header.jsp"%>
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Sync Passed Sem info with salesforce</legend></div>
         <%@ include file="messages.jsp"%> 
		<div class="panel-body">
			<div class="col-md-18 column">
				<a  class="btn btn-success" href="getPassedSemInfoForPg">Trigger For PG Students</a>    
				
			</div>
		</div>
		<div class="panel-body">
			<div class="col-md-18 column">
				<a  class="btn btn-success" href="getPassedSemInfoForMbawx">Trigger For MBA( WX ) Students</a>    
				
			</div>
		</div>
		<div class="panel-body">
			<div class="col-md-18 column">
				<a  class="btn btn-success" href="getPassedSemInfoForMbax">Trigger For MBA( X ) Students</a>    
				
			</div>
		</div>
	</div>
</section>


<jsp:include page="footer.jsp" />

</body>
</html>