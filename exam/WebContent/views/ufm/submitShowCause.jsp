<!DOCTYPE html>
<%@page import="java.util.ArrayList"%>
<%@page import="com.nmims.beans.StudentExamBean"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<html lang="en">


<jsp:include page="../common/jscss.jsp">
	<jsp:param value="UFM Status" name="title" />
</jsp:include>
<body>
	<%@ include file="../common/header.jsp"%>
	<div class="sz-main-content-wrapper">
		<jsp:include page="../common/breadcrum.jsp">
			<jsp:param value="Student Zone;Exam;Assignment" name="breadcrumItems" />
		</jsp:include>
		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../common/left-sidebar.jsp">
					<jsp:param value="Assignment" name="activeMenu" />
				</jsp:include>

				<div class="sz-content-wrapper examsPage">
					<%@ include file="../common/studentInfoBar.jsp"%>
					<div class="sz-content">
						<h2 class="red text-capitalize" style="width:100%">UFM - Submit Response</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper">
							<%@ include file="../common/messages.jsp"%>
							<form action="submitAssignment" method="post">
								<input type="hidden" name = "year" value="${ showCauseList.year }">
								<input type="hidden" name = "month" value="${ showCauseList.month }">
								<textarea id="reason-text" name="showCauseResponse" style="width:100%" rows="8" maxlength="200" placeholder="Enter Reason Here..."></textarea>
								<span style="font-size: smaller; width: 100%" id="characters-left"></span>
								<div class="clearfix"></div>
								<button id="submit" name="submit" class="btn btn-danger" formaction="/exam/student/submitShowCause">
									Submit
								</button>
							</form>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<jsp:include page="../common/footer.jsp" />
	<script>
		$('#reason-text').on('change keyup', function() {
			var length = $(this).val().length;
			$('#characters-left').html('Characters Left : ' + (200 - length))
		})
	</script>
</body>
</html>