<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.nmims.beans.*"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
	ArrayList<TestStudentPortalBean> quizList = (ArrayList<TestStudentPortalBean>)request.getSession().getAttribute("quizList");
	String SERVER_PATH = (String)request.getSession().getAttribute("SERVER_PATH");
	int noOfQuiz = quizList != null ? quizList.size() : 0;
	String testLink = "";
	try{
		testLink = SERVER_PATH+"exam/viewTestDetailsForStudentsForAllViewsForLeads";
	}catch(Exception e){
		testLink = "";
	}
	
%>
<style>
	.course-sessions-m-wrapper {
		min-height: 40em;
	}
</style>
<div class="course-sessions-m-wrapper">
	<div class="panel-courses-page">
		<% if (noOfQuiz == 0) { %>
			<div class="no-data-wrapper nodata-wrapper">
				<h4 style="text-align: center">
					<i class="fa-solid fa-circle-exclamation" style="font-size: 19px" aria-hidden="true"></i>
					 Test is not available. Please try again !!!
				</h4>
			</div>
		<% } else { %>
		<div class="row data-content panel-body">
			<%
				if (activeMenu.equalsIgnoreCase("finalAssessQuiz")) {
			%>
			<h2>Test</h2>
			<%
				} else {
			%>
			<h2>Quiz</h2>
			<%
				}
			%>
			<br><br>
			<div class="col-md-12 " style="padding-bottom: 20px;">
				<div style="font-size: 12px;margin-bottom:1rem;">
					<div class="panel-body">
							<c:forEach items="<%=quizList %>" var="bean">
								<div class="col-md-12">
									<%if(!StringUtils.isBlank(testLink)) { %>
										<iframe id="sessionFrame" src="<%=testLink%>?id=${bean.id}&userId=${leadId}&message=%27%27&activeMenu=<%=activeMenu%>" width="100%" seamless="seamless" height="550" frameborder="0"></iframe>
									<% } else { %>
										<h4 style="text-align: center">
											<i class="fa-solid fa-circle-exclamation" style="font-size: 19px" aria-hidden="true"></i>
											 Test is not available. Please try again !!!
										</h4>
									<% } %>
								</div>
							</c:forEach>
					</div>
				</div>
			</div>
		</div>
		<% } %>
	</div>
</div>
		
		
<script>
	  $(function() {
	    $( "#accordion1" ).accordion({
	      collapsible: true,
	      heightStyle: "content",
	      active:false
	    });
	  });
</script>