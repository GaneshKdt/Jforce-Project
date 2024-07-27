<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.PersonAcads"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.ForumBean"%>
<%@page import="java.util.List"%>
<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="../jscss.jsp">
<jsp:param value="Queries" name="title" />
</jsp:include>		

<body class="inside">

<style>

.ui-state-hover{
	background:#DED9DA;
}

</style>

<%@ include file="../header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Search Discussion Thread</legend></div>
        <%@ include file="../messages.jsp"%>
		
		<!-- Unanswered Queries section -->
		<div class="panel-body">
			
			
			<form:form action="" method="post" modelAttribute="forumBean">
				<fieldset>
				<div class="panel-body">
				<div class="col-md-9 column">
				<div class="form-group" style="overflow:visible;">
								<form:select id="subject" path="subject"  class="combobox form-control" required="required" itemValue="${forumBean.subject}">
									<form:option value="">Type OR Select Subject</form:option>
									<form:options items="${subjectList}" />
								</form:select>
								</div>
						
					
						<div class="form-group">
							<label class="control-label" for="submit"></label>
							<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="searchForumThread">Search Thread</button>
							<button id="cancel" name="cancel" class="btn btn-danger" formaction="acadsHome" formnovalidate="formnovalidate">Cancel</button>
						</div>
						</div>
					</div>
				</fieldset>
			</form:form>
			</div>
			<c:choose>
			<c:when test="${rowCount > 0}">
				<legend>&nbsp;Active Forum Threads<font size="2px">(${rowCount} Threads Found)</font></legend>
					<div class="table-responsive">
						<table class="table table-striped table-hover" style="font-size:12px">
							<thead>
							<tr> 
								<th>SR. No</th>
								<th>Title</th>
								<th>Subject</th>
								<th>Status Of Forum</th>
								<th>Created Date</th>
								<th>View Discussions</th>
							
							</tr>
						</thead>
						<tbody>
							<c:forEach var="forum" items="${forumsRelatedToSubject}" varStatus="status">
								<tr>
									<td><c:out value="${status.count}"/></td>
									<td><a href="#" class="editable" id="username" data-type="text" data-pk="${forum.id}"  data-url="editForumSubject" data-original-title="Enter Subject">${forum.title}</a></td>
									<td><c:out value="${forum.subject}"/></td>
									<td><a href="#" class="editable" id="requestStatus" data-type="select" data-pk="${forum.id}" 
									data-source="[{value: 'Draft', text: 'Draft'},{value: 'Active', text: 'Active'},{value: 'Delete', text: 'Delete'}]"
									data-url="saveForumStatus" data-title="Select Low Marks Reason">${forum.status}</a></td>
									<td><c:out value="${forum.createdDate}"/></td>
									<td><a href="/acads/admin/viewForumResponse?id=${forum.id}">View Thread Replies</a></td>
								</tr>
							</c:forEach>
						</tbody>
						</table>
					</div>
			</c:when>
			</c:choose>
<c:url var="firstUrl" value="searchForumPage?pageNo=1" />
<c:url var="lastUrl" value="searchForumPage?pageNo=${page.totalPages}" />
<c:url var="prevUrl" value="searchForumPage?pageNo=${page.currentIndex - 1}" />
<c:url var="nextUrl" value="searchForumPage?pageNo=${page.currentIndex + 1}" />
		</div>
		
		
		
		
	</div>
	
	</section>

	<jsp:include page="../footer.jsp" />

<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/bootstrap-editable.js"></script>

<script>
$(function() {
    //toggle `popup` / `inline` mode
    $.fn.editable.defaults.mode = 'inline';     
    
    $('.editable').each(function() {
        $(this).editable({
        	success: function(response, newValue) {
        		obj = JSON.parse(response);
                if(obj.status == 'error') {
                	return obj.msg; //msg will be shown in editable form
                }
            }
        });
    });
    
});
</script>

	  

</body>
</html>
