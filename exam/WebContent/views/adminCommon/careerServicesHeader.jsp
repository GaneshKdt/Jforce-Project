<%@page import="java.util.Arrays"%>
<%@page import="java.util.List"%>
<%
	boolean isExternallyAffiliatedForProducts = false;
	boolean isCSSpeaker = false;
	boolean isCSAdmin = false;
	boolean isCSSessionsAdmin = false;
	boolean isCSProductsAdmin = false;

	if (request.getSession().getAttribute("isCSSpeaker") != null) {
		isCSSpeaker = (boolean) request.getSession().getAttribute("isCSSpeaker");
	}
	if (request.getSession().getAttribute("isCSAdmin") != null) {
		isCSAdmin = (boolean) request.getSession().getAttribute("isCSAdmin");
	}
	if (request.getSession().getAttribute("isCSProductsAdmin") != null) {
		isCSProductsAdmin = (boolean) request.getSession().getAttribute("isCSProductsAdmin");
	}
	if (request.getSession().getAttribute("isCSSessionsAdmin") != null) {
		isCSSessionsAdmin = (boolean) request.getSession().getAttribute("isCSSessionsAdmin");
	}
	if (request.getSession().getAttribute("isExternallyAffiliatedForProducts") != null) {
		isExternallyAffiliatedForProducts = (boolean) request.getSession()
				.getAttribute("isExternallyAffiliatedForProducts");
	}

	if (isExternallyAffiliatedForProducts || isCSSpeaker || isCSAdmin || isCSSessionsAdmin
			|| isCSProductsAdmin) {
%>
<li class="has-sub-menu"><a href="#">Career Services</a>
	<ul class="sz-sub-menu subMenu">
		<%
			if (isCSAdmin || isCSProductsAdmin) {
		%>
		<li><b style="color: white">Package set up</b></li>
		<li><a href="/careerservices/addPackageFamily">View Package
				Families Details</a></li>
		<li><a href="/careerservices/addUpgradePath">View Upgrade
				Paths Details</a></li>
		<li><a href="/careerservices/allFeatures">View Features
				Details</a></li>
		<li><a href="/careerservices/addPackage">View Packages
				Details</a></li>
		<%
			}
		%>

		<%
			if (isCSAdmin || isCSSessionsAdmin) {
		%>
		<li><b style="color: white">Session set up</b></li>
		<li><a href="/careerservices/addScheduledSessionForm">Direct
				Session Creation</a></li>
		<li><a href="/careerservices/batchSessionSchedulingForm">Upload
				Session By Excel</a></li>
		<%
			if (!isExternallyAffiliatedForProducts) {
		%>
		<li><a href="/careerservices/autoScheduleForm">Create Webinar Links</a></li>
		<%
			}
		%>
		<li><a href="/careerservices/searchScheduledSessionForm">Search
				Generated Schedule</a></li>
		<%
			}
		%>

		<%
			if (isCSAdmin || isCSSessionsAdmin) {
		%>
		<li><b style="color: white">Misc. </b></li>
		<li><a href="/careerservices/addVideoContent">Add/View Video
				Content Details</a></li>
		<%
			if (!isExternallyAffiliatedForProducts) {
		%>
		<li><a href="/careerservices/addFeedbackQuestion">Add/View
				Feedback Questions</a></li>
		<%
			}
		%>
		<li><a href="/careerservices/addCSFaculty">Add/View Faculties
				assigned to CS</a></li>
		<%
			}
		%>

		<%
			if (isCSAdmin) {
		%>
		<%
			if (!isExternallyAffiliatedForProducts) {
		%>
		<li><b style="color: white">Administrator Tools. </b></li>
		<li><a href="/careerservices/showAllStudentsAndPackages">Show
				All Students Info</a></li>
		<li><a href="/careerservices/showAllStudentFeedback">Show All
				Session Feedback</a></li>
		<li><a href="/careerservices/showAllSessionQueryStatus">Show
				All Session Query And Status</a></li>
		<%
			}
		%>
		<%
			}
		%>
	</ul></li>
<li class="has-sub-menu"><a href="#">Interview</a>
	<ul class="sz-sub-menu subMenu">
		<%
			if (isCSAdmin || isCSProductsAdmin || isCSSessionsAdmin) {
		%>
		<li><a href="/careerservices/uploadProgressDetailsForm">Upload
				Progress Details</a></li>
		<li><a href="/careerservices/uploadInterviewDetailsForm">Upload
				Interview Details </a></li>
		<li><a href="/careerservices/feedback">Interview Feedback </a></li>
		<%
			}
		%>
	</ul></li>
<%
	}
%>

