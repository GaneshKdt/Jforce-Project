<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Exam Data Management" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container">
		<div class="container-fluid customTheme">
		<%@ include file="messages.jsp"%>
		<div class="row"><legend>&nbsp;Records pending for Pass/Fail Processing	:${pendingRecordsCount}. Processing will take considerable time. </legend></div>
		<form role="form" id="passFailForm" action="processPassFail" method="post">
			<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="processPassFail?passFailLogicType=${passFailLogicType}">Process remaining records for Pass/Fail</button>
			<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button> <br><br>
	<%-- 
		<div class = "col-md-4 column">
		<div class="panel-body">
		
		<div class="row"><h2>&nbsp;Records Count Distribution </h2></div>
		
		<div class="table-responsive">
			<table class="table table-striped" style="font-size: 12px" border="1px">
			<thead>
			  <tr>
				<td>Category</td>
				<td>Count</td>
			  </tr>
			</thead>
			<tbody>
				<tr>
					<td>Project Records
					</td>
					<td>${projectCount }
					</td>
					<td>Project Records Online
					</td>
					<td>${projectCountOnline }
					</td>
				</tr>
				<tr>
					<td>Project Records Offline
					</td>
					<td>${projectCountOffline }
					</td>
				</tr>
				<tr>
					<td>Absent Student Records
					</td>
					<td>${absentCount }
					</td>
				</tr>
				<tr>
					<td> NV / RIA
					</td>
					<td>${nvRiaCount }
					</td>
				</tr>
				<tr>
					<td>ANS Records
					</td>
					<td>${ansCount }
					</td>
				</tr>
				<tr>
					<td>Assignment Submitted Records Online
					</td>
					<td>${assignmentScoreOnlineCount }
					</td>
				</tr>
				<tr>
					<td>Assignment Submitted Records Offline
					</td>
					<td>${assignmentScoreOfflineCount }
					</td>
				</tr>
				<tr>
					<td>Written Score Records Online
					</td>
					<td>${writtenScoreOnlineCount }
					</td>
				</tr>
				<tr>
					<td>Written Score Records Offline
					</td>
					<td>${writtenScoreOfflineCount }
					</td>
				</tr>
				<tr>
					<td>Online 
					</td>
					<td>${onlineCount }
					</td>
				</tr>
				<tr>
					<td>Offline
					</td>
					<td>${offlineCount }
					</td>
				</tr>
			
			</tbody>
			
			</table>
	</div>
		</div>
		</div> --%>
		</form>
		
	</div>
	</section>




	<jsp:include page="footer.jsp" />

</body>
</html>