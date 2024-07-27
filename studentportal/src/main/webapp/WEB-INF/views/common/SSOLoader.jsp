
<%@page import="com.nmims.controllers.BaseController"%>
<%@page import="com.nmims.helpers.PersonStudentPortalBean"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="com.nmims.helpers.AESencrp"%>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')" var="server_path" />

<style>
	#fullPageLoading, .fullPageLoading {
		position : fixed;
		height : 100%;
		width : 100%;
		z-index: 9999;
		display : flex;
	}
	#loader-container {
		margin-top : auto;
		margin-bottom : auto;
		margin-left : auto;
		margin-right : auto;
		background-color : white;
		padding : 20px;
		border-radius : 5px;
		z-index: 11111;
		text-align: center;
	}
	
	#loader {
		border: 16px solid #f3f3f3; /* Light grey */
		border-top: 16px solid #d2232a; /* Blue */
		border-radius: 50%;
		width: 120px;
		height: 120px;
		animation: spin 2s linear infinite;
	}
	
	@keyframes spin {
		0% { transform: rotate(0deg); }
		100% { transform: rotate(360deg); }
	}
</style>

<script>

<%
	BaseController controller = new BaseController();
	String encryptedSapId =  "";
	
	if(controller.checkLead(request, response))
		encryptedSapId = URLEncoder.encode(AESencrp.encrypt((String)session.getAttribute("emailId"))); 
	else
		encryptedSapId = URLEncoder.encode(AESencrp.encrypt((String)session.getAttribute("userId"))); 
	System.out.print("userId jsp" + session.getAttribute("userId"));
	String examAppSSOUrl = (String)pageContext.getAttribute("server_path") + "exam/loginforSSO?uid="+encryptedSapId;
	String acadsAppSSOUrl = (String)pageContext.getAttribute("server_path") + "acads/loginforSSO?uid="+encryptedSapId;
	String ltiAppSSOUrl = (String)pageContext.getAttribute("server_path") + "ltidemo/loginforSSO?uid="+encryptedSapId;
	String csAppSSOUrl = (String)pageContext.getAttribute("server_path") + "careerservices/loginforSSO?uid="+encryptedSapId;
%>

</script>


<div id="examApp"></div>
<div id="acadsApp"></div>
<div id="ltiApp"></div>
<div id="csApp"></div>

<script>
	var examAppLoaded = false;
	var acadsAppLoaded = false;
	var ltiAppLoaded = false;
	var csAppLoaded = false;
	
	$(document).ready(function(){
		$('body').prepend(`
			<div class="fullPageLoading">
				<div class="modal-backdrop fade in"></div>
				<div id="loader-container">
			 		<div id="loader"></div>
					<div> Please wait... </div>
				</div>
			</div>`)
		$( "#examApp" ).load('<%=examAppSSOUrl%>', function() {
			examAppLoaded = true
			checkIfLoadingFinished();
		});
		$( "#acadsApp" ).load('<%=acadsAppSSOUrl%>', function() {
			acadsAppLoaded = true
			checkIfLoadingFinished();
		});
		$( "#ltiApp" ).load('<%=ltiAppSSOUrl%>', function() {
			ltiAppLoaded = true
			// checkIfLoadingFinished();
		});
		
		<%
		//only log into cs if student has the access to it
		boolean loadCS = false;
		if(session.getAttribute("consumerProgramStructureHasCSAccess") != null && (boolean) session.getAttribute("consumerProgramStructureHasCSAccess") ){
			loadCS = true;
		} else {
			// check for admin user
			boolean isExternallyAffiliatedForProducts = false;
			boolean isCSSpeaker = false;
			boolean isCSAdmin = false;
			boolean isCSSessionsAdmin = false;
			boolean isCSProductsAdmin = false;
			
			if(request.getSession().getAttribute("isCSSpeaker") != null){
				isCSSpeaker = (boolean) request.getSession().getAttribute("isCSSpeaker");
			}
			if(request.getSession().getAttribute("isCSAdmin") != null){
				isCSAdmin = (boolean) request.getSession().getAttribute("isCSAdmin");
			}
			if(request.getSession().getAttribute("isCSProductsAdmin") != null){
				isCSProductsAdmin = (boolean) request.getSession().getAttribute("isCSProductsAdmin");
			}
			if(request.getSession().getAttribute("isCSSessionsAdmin") != null){
				isCSSessionsAdmin = (boolean) request.getSession().getAttribute("isCSSessionsAdmin");
			}
			if(request.getSession().getAttribute("isExternallyAffiliatedForProducts") != null){
				isExternallyAffiliatedForProducts = (boolean) request.getSession().getAttribute("isExternallyAffiliatedForProducts");
			}
			loadCS = isCSSpeaker || isCSAdmin || isCSProductsAdmin || isCSSessionsAdmin || isExternallyAffiliatedForProducts;
		}
		
		if(loadCS){ %>	
			$( "#csApp" ).load('<%=csAppSSOUrl%>', function() {
				csAppLoaded = true
				// checkIfLoadingFinished();
			});
		<% } %>
		
		setTimeout(function(){ $('.fullPageLoading').fadeOut(200); }, 15000);
	});
	
	function checkIfLoadingFinished() {
		if(examAppLoaded && acadsAppLoaded) {
			$('.fullPageLoading').fadeOut(200);
		}
	}
</script>