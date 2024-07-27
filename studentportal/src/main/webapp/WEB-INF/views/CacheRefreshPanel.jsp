<!DOCTYPE html>
<html lang="en">
<%@page import="com.nmims.helpers.PersonStudentPortalBean"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="com.nmims.helpers.AESencrp"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Welcome to Student Zone" name="title" />
</jsp:include>
<style>
.jumbotron {
	background-color: #fff;
	padding: 0.5px;
}
</style>
<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')"
	var="server_path" />

<body>

	<%@ include file="adminCommon/header.jsp"%>


	<%

         String encryptedSapId = URLEncoder.encode(AESencrp.encrypt((String)session.getAttribute("userId"))); 
    String examAppSSOUrl = (String)pageContext.getAttribute("server_path") + "exam/loginforSSO?uid="+encryptedSapId;
    String acadsAppSSOUrl = (String)pageContext.getAttribute("server_path") + "acads/loginforSSO?uid="+encryptedSapId;
    
    %>



	<div class="sz-main-content-wrapper">

		<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Student Zone;Home" name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>


				<div class="sz-content-wrapper examsPage">
					<%@ include file="adminCommon/adminInfoBar.jsp"%>


					<div class="sz-content">
						<div style="color: black; font-size: 32px; padding: 20px 0px">
							<img src="assets/images/cache.png" style="height: 100px;" />
							Cache Refresh Panel
						</div>

						<table class="table table-striped table-condensed">
							<thead>
								<tr>
									<td>Project</td>
									<td>Status</td>
									<td>Refresh key</td>
								</tr>
							</thead>
							<tbody>
								<tr>
									<td>Student Portal</td>
									<td class="student_status">active</td>
									<td><button id="studentCacheRefresh_btn"
											class="btn btn-danger">Refresh</button></td>
								</tr>
								<tr>
									<td>Exam Portal</td>
									<td class="exam_status">active</td>
									<td><button id="examCacheRefresh_btn"
											class="btn btn-danger">Refresh</button></td>
								</tr>
								<tr>
									<td>Acads Portal</td>
									<td>none</td>
									<td>Under Development</td>
								</tr>
							</tbody>
						</table>



					</div>
				</div>


			</div>
		</div>
		<!-- Cache refresh panel -->
		<div id="examApp"></div>
		<div id="acadsApp"></div>

		<jsp:include page="adminCommon/footer.jsp" />
		<script>
			$( "#examApp" ).load( "<%=examAppSSOUrl%>" );
			$( "#acadsApp" ).load( "<%=acadsAppSSOUrl%>" );
		</script>

		<script>
		$(document).ready(function(){
			$('#studentCacheRefresh_btn').click(function(){
				$('.student_status').html('loading...');
				console.log('student refresh button click');
				$.ajax({
					type:'GET',
					url:'/studentportal/admin/cacheRefreshToAllServer',
					success:function(response){
						if(response.status == "success"){
							$('.student_status').html('<span style="background-color:green !important">Successfully refresh cache</span>');
						}else{
							$('.student_status').html('<span style="background-color:yellow !important">'+ response.message +'</span>');
						}
					},error:function(){
						$('.student_status').html('<span style="background-color:red !important">Server Error found</span>');
					}
				});
			});
			$('#examCacheRefresh_btn').click(function(){
				$('.exam_status').html('loading...');
				console.log('exam refresh button click');
				$.ajax({
					type:'GET',
					url:'/exam/admin/cacheRefreshToAllServer',
					success:function(response){
						if(response.status == "success"){
							$('.exam_status').html('<span style="background-color:green !important">Successfully refresh cache</span>');
						}else{
							$('.exam_status').html('<span style="background-color:yellow !important">'+ response.message +'</span>');
						}
					},error:function(){
						$('.exam_status').html('<span style="background-color:red !important">Server Error found</span>');
					}
				});
			});
		});
		</script>
</body>
</html>
