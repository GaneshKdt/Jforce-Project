<%@page import="com.itextpdf.text.log.SysoCounter"%>
<%@page import="com.nmims.beans.StudentStudentPortalBean"%>
<%@page import="java.util.Map"%>

<% 
	boolean csActiveInSidebar = false;
	if(session.getAttribute("student_studentportal") != null){
		csActiveInSidebar = ((StudentStudentPortalBean) session.getAttribute("student_studentportal")).isPurchasedOtherPackages();
	}

	boolean careerForumAccess = false;
	boolean learningPortalAccess = false;
	boolean careerCounsellingAccess = false;
	boolean practiceInterviewAccess = false;
	boolean jobSearchAccess = false;

	@SuppressWarnings("unchecked")
	Map<String, Boolean> CSFeatureViseAccess = (Map<String, Boolean>) request.getSession().getAttribute("CSFeatureAccess"); 
	System.out.println("CSFeatureViseAccess" + CSFeatureViseAccess);
	
	if(csActiveInSidebar){ 
		if(request.getSession().getAttribute("CSFeatureAccess") != null){
			if(CSFeatureViseAccess.get("Career_Forum") != null){
				careerForumAccess = CSFeatureViseAccess.get("Career_Forum");
			}
			if(CSFeatureViseAccess.get("Learning_Portal") != null){
				learningPortalAccess = CSFeatureViseAccess.get("Learning_Portal");
			}
	
			if(CSFeatureViseAccess.get("Career_Counselling") != null){
				careerCounsellingAccess = CSFeatureViseAccess.get("Career_Counselling");
			}
			if(CSFeatureViseAccess.get("Practice_Interviews") != null){
				practiceInterviewAccess = CSFeatureViseAccess.get("Practice_Interviews");
			}
	
			if(CSFeatureViseAccess.get("Job_Search") != null){
				jobSearchAccess = CSFeatureViseAccess.get("Job_Search");
			}
%>
<li id="csSidebarIcon" class="has-sub-menu"><a
	href="/careerservices/Home"><span class="fa-solid fa-briefcase"></span>
		<p class="toggle-name" style="display: none;">Career Services</p> </a>
	<ul class="sz-sub-menu">
		<li><a href="/careerservices/home?resetSessions=true">
				<p >Dashboard</p>
		</a></li>
		<li>
			<% if(careerForumAccess){ %> <a href="/careerservices/career_forum">
				<p>Career Forum</p>
		</a> <% }else{ %> <a>
				<p style="color: #b9b9b9;">Career Forum</p>
		</a> <% } %>
		</li>
		<li>
			<% if(learningPortalAccess){ %> <a
			href="/careerservices/learning_portal">
				<p>Learning Portal</p>
		</a> <% }else{ %> <a>
				<p style="color: #b9b9b9;">Learning Portal</p>
		</a> <% } %>
		</li>
		<li><a>
				<p style="color: #b9b9b9;">Career Counselling</p>
		</a></li>
		<li><a>
				<p style="color: #b9b9b9;">Practice Interviews</p>
		</a></li>
		<!-- <li>
			<a href="/jobsearch/jobSearch">
				<p>Job Portal</p>
			</a>
		</li> -->
		<li><a>
				<p style="color: #b9b9b9;">Upgrade Package</p>
		</a></li>
	</ul></li>

<% 
		}
	} 
%>