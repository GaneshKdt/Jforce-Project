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
							<img src="${pageContext.request.contextPath}/assets/images/systempanel.png" style="height: 100px;" />
							Server Status
						</div>

						<table class="table table-striped table-bordered table-condensed">
							<thead>
								<tr>
									<td>Sr.No</td>
									<td>Server Port</td>
									<td>acads</td>
									<td>studentportal</td>
									<td>exam</td>
									<td></td>
								</tr>

							</thead>
							<tbody>
								<tr>
									<td>1</td>
									<td>8181</td>
									<td id="pingStatus8181acads">loading...</td>
									<td id="pingStatus8181studentportal">loading...</td>
									<td id="pingStatus8181exam">loading...</td>
									<td><button data-port="8181"
											class="js_ping_btn btn btn-danger">refresh</button></td>
								</tr>
								<tr>
									<td>2</td>
									<td>8282</td>
									<td id="pingStatus8282acads">loading...</td>
									<td id="pingStatus8282studentportal">loading...</td>
									<td id="pingStatus8282exam">loading...</td>

									<td><button data-port="8282"
											class="js_ping_btn btn btn-danger">refresh</button></td>
								</tr>
								<tr>
									<td>3</td>
									<td>8383</td>
									<td id="pingStatus8383acads">loading...</td>
									<td id="pingStatus8383studentportal">loading...</td>
									<td id="pingStatus8383exam">loading...</td>
									<td><button data-port="8383"
											class="js_ping_btn btn btn-danger">refresh</button></td>
								</tr>
								<tr>
									<td>4</td>
									<td>8484</td>
									<td id="pingStatus8484acads">loading...</td>
									<td id="pingStatus8484studentportal">loading...</td>
									<td id="pingStatus8484exam">loading...</td>
									<td><button data-port="8484"
											class="js_ping_btn btn btn-danger">refresh</button></td>
								</tr>
								<tr>
									<td>5</td>
									<td>8585</td>
									<td id="pingStatus8585acads">loading...</td>
									<td id="pingStatus8585studentportal">loading...</td>
									<td id="pingStatus8585exam">loading...</td>
									<td><button data-port="8585"
											class="js_ping_btn btn btn-danger">refresh</button></td>
								</tr>
							</tbody>
						</table>



					</div>
				</div>


			</div>
		</div>

		<div id="examApp"></div>
		<div id="acadsApp"></div>

		<jsp:include page="adminCommon/footer.jsp" />
		<script>
			$( "#examApp" ).load( "<%=examAppSSOUrl%>" );
			$( "#acadsApp" ).load( "<%=acadsAppSSOUrl%>" );
		</script>

		<script>
		$(document).ready(function(){
			
			PingServer("8181","acads");
			PingServer("8181","studentportal");
			PingServer("8181","exam");
			
			PingServer("8282","acads");
			PingServer("8282","studentportal");
			PingServer("8282","exam");
			
			PingServer("8383","acads");
			PingServer("8383","studentportal");
			PingServer("8383","exam");
			
			PingServer("8484","acads");
			PingServer("8484","studentportal");
			PingServer("8484","exam");
			
			PingServer("8585","acads");
			PingServer("8585","studentportal");
			PingServer("8585","exam");
			
			
			function PingServer(port,project){
				console.log('Pingserver function inside');
				$('#pingStatus' + port + project).html('loading...');
				console.log("port : " + port + " | project : " + project);
				$.ajax({
					type:'GET',
					data:{
						'port' : port,
						'project' : project
					},
					url:'/studentportal/ServerPing',
					success:function(response){
						if(response.status == "500"){
							$('#pingStatus' + port + project).html('<span style="font-size:14px" class="label label-danger">Inactive</span>');
						}else{
							$('#pingStatus' + port + project).html('<span style="font-size:14px" class="label label-success">active</span>');	
						}
					},
					error:function(){
						$('#pingStatus' + port + project).html('<span style="font-size:14px" class="label label-danger">Inactive</span>');
					}
				});
			}
			
			$('.js_ping_btn').click(function(){
				var port = $(this).attr('data-port');
				PingServer(port,"acads");
				PingServer(port,"studentportal");
				PingServer(port,"exam");				
			});
			
		});
		</script>
</body>
</html>
