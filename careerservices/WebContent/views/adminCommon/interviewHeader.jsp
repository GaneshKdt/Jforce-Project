<%@page import="java.util.Arrays"%>
<%@page import="java.util.List"%>
<%

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

if(
	isExternallyAffiliatedForProducts || isCSSpeaker || isCSAdmin || isCSSessionsAdmin || isCSProductsAdmin
){ %>
	<li class="has-sub-menu">
		<a href="#">CS Interview</a>
		<ul class="sz-sub-menu subMenu">
			<li><b style="color: white">Set up</b></li>
			<li><a href="/careerservices/uploadProgressDetailsForm">Upload Progress Details</a></li>
			<li><a href="/careerservices/uploadInterviewDetailsForm">Upload Interview Details</a></li>
		</ul>
	</li>
<% } %>