<%-- 

<%@page import="com.nmims.beans.ConsumerProgramStructure"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList, java.util.List"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%!
	List<Object> liveSessionPssIdAccessList = null;
	
	public boolean checkIfLiveVideo(String pssId) { 
		boolean isLive = Boolean.FALSE;
		
		//If pssId in the List, it means LIVE session else RECORDED session and appropriate icon displayed.
		if(null != liveSessionPssIdAccessList && !liveSessionPssIdAccessList.isEmpty()) {
			if(liveSessionPssIdAccessList.get(0) instanceof String) {
				isLive = liveSessionPssIdAccessList.contains(pssId);
			} else if(liveSessionPssIdAccessList.get(0) instanceof Integer) {
				isLive = liveSessionPssIdAccessList.contains(toInteger(pssId));
			}
			//System.out.println("liveSessionPssIdAccessList : " + liveSessionPssIdAccessList + ", Searching... "+ pssId + ", found ..."+isLive);
		}
		//System.out.println("isLive : "+ isLive);
		return isLive;
	}
	
	public static Integer toInteger(String arg) {
		return Integer.valueOf(arg);
	}
%>

<%
/*ArrayList<String> subjects = (ArrayList<String>)session.getAttribute("studentCourses_studentportal");
int noOfSubjects = subjects.size();
HashMap<String,String> programSemSubjectIdWithSubject = (HashMap<String,String>)session.getAttribute("programSemSubjectIdWithSubjects_studentportal");
int noOfSubjects = programSemSubjectIdWithSubject.size();
<<<<<<< HEAD
*/

ArrayList<String> subjects = (ArrayList<String>) session.getAttribute("studentCourses_studentportal");
int noOfSubjects = 0;
String type= (String)request.getSession().getAttribute("type");
//int noOfSubjects = subjects.size();
HashMap<String, String> programSemSubjectIds = new HashMap<String, String>();
HashMap<String, String> programSemSubjectIdWithSubjectForBacklog = (HashMap<String, String>) session
		.getAttribute("programSemSubjectIdWithSubjectForBacklog");
HashMap<String, String> programSemSubjectIdWithSubjectForCurrentSem = (HashMap<String, String>) session
		.getAttribute("programSemSubjectIdWithSubjectForCurrentsem");
HashMap<String, String> programSemSubjectIdWithSubject = (HashMap<String, String>) session
		.getAttribute("programSemSubjectIdWithSubjects_studentportal");


%>

<div class="courses">
	<div class="panel panel-default">
		<div class="panel-heading" role="tab" id="">
			<h4 class="panel-title"><%=type%> COURSES</h4>
			<ul class="topRightLinks list-inline">
				<li><a href="student/viewCourseHomePage">SEE ALL</a></li>
				<li><a class="panel-toggler collapsed" role="button"
					data-toggle="collapse" data-parent="#accordion" href="#collapseTwo"
					aria-expanded="true"></a></li>
			</ul>
			<div class="clearfix"></div>
		</div>
		<div id="collapseTwo"
			class="panel-collapse collapse in courses-panel-collapse"
			role="tabpanel">
			<!--panel body-->
			<div class="panel-body no-border">

				<div class="p-closed">
					<div class="no-data-wrapper no-border">
						<p class="no-data">
							<span class="icon-my-courses iconCourse"></span>
							<%=noOfSubjects%>
							<a href="#">Courses</a>
						</p>
					</div>
				</div>


				<%
				int count = 0;
				// for(String subject : subjects){
				for (String key : programSemSubjectIds.keySet()) {
					count++;
				%>





				<c:url value="/student/viewCourseDetails" var="courseUrl">

					
					<c:param name="programSemSubjectId" value="<%=key %>" />
					

				</c:url>

				<div class="media">
					<div class="media-left">
						<span><%=count%></span>
					</div>
					<div class="media-body">
						<a href="${courseUrl}"><%=programSemSubjectIdWithSubjectForCurrentSem.get(key)%></a>
					</div>
				</div>

				<%
				if(programSemSubjectIdWithSubjectForCurrentSem.size()==0)
				{
				if (count == 6) {
					break;//SHow only 6 subjects
				}

				}
				}
				%>


				<%
				if (programSemSubjectIdWithSubjectForCurrentSem.size() == 0) {

					if (noOfSubjects > 6) {
				%>
				<a href="#" data-toggle="modal" data-target="#coursesModalBacklog"
					class="modalBtn media"><%=(noOfSubjects - 6)%> More BackLogs...</a>
				<%
				}
				}else
				{	
				if(programSemSubjectIdWithSubjectForBacklog.size()>0)
				{
				%>
				<a href="#" data-toggle="modal" data-target="#coursesModalBacklog"
					class="modalBtn media"> View BackLogs...</a>
					<%}}%>
				    
				    <!--  if(noOfSubjects > 6)
					{%>
				       <a href="#" data-toggle="modal" data-target="#coursesModal"
					class="modalBtn media">(noOfSubjects - 6) More Ongoings...</a>
				}}  -->


				<div class="clearfix"></div>
			</div>

		</div>
	</div>
</div>





<!--MODAL FOR COURSES-->
<div class="courses modal fade" id="coursesModal" tabindex="-1"
	role="dialog">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title">COURSES</h4>
			</div>
			<div class="modal-body">

				<%
				count = 0;
				// for(String subject : subjects){
				for (String key : programSemSubjectIdWithSubjectForCurrentSem.keySet()) {
					count++;
				%>

				<c:url value="student/viewCourseDetails" var="courseUrl">
					<c:param name="programSemSubjectId" value="<%=key%>" />

				</c:url>

				<div class="media">
					<div class="media-left">
						<span><%=count%></span>
					</div>
					<div class="media-body">
						<a href="${courseUrl }"><%=programSemSubjectIdWithSubjectForCurrentSem.get(key)%></a>
					</div>
				</div>

				<%
				}
				%>

			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Done</button>
			</div>
		</div>
	</div>
</div>

<!--MODAL FOR BACKLOGCOURSES-->
<div class="courses modal fade" id="coursesModalBacklog" tabindex="-1"
	role="dialog">
	
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title">BACKLOG COURSES</h4>
			</div>
			<div class="modal-body">

				<%
				count = 0;
				// for(String subject : subjects){
				for (String key : programSemSubjectIdWithSubjectForBacklog.keySet()) {
					count++;
				%>

				<c:url value="viewCourseDetails" var="courseUrl">
					<c:param name="programSemSubjectId" value="<%=key%>" />

				</c:url>

				<div class="media">
					<div class="media-left">
						<span><%=count%></span>
					</div>
					<div class="media-body">
						<a href="${courseUrl }"><%=programSemSubjectIdWithSubjectForBacklog.get(key)%></a>
					</div>
				</div>

				<%
				}
				%>

			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Done</button>
			</div>
		</div>
	</div>
</div> --%>

<%@page import="java.util.List"%>
<%@page import="com.nmims.beans.ConsumerProgramStructureStudentPortal"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>


<%!
	List<Object> liveSessionPssIdAccessList = null;
	
	public boolean checkIfLiveVideo(String pssId) { 
		boolean isLive = Boolean.FALSE;
		
		//If pssId in the List, it means LIVE session else RECORDED session and appropriate icon displayed.
		if(null != liveSessionPssIdAccessList && !liveSessionPssIdAccessList.isEmpty()) {
			if(liveSessionPssIdAccessList.get(0) instanceof String) {
				isLive = liveSessionPssIdAccessList.contains(pssId);
			} else if(liveSessionPssIdAccessList.get(0) instanceof Integer) {
				isLive = liveSessionPssIdAccessList.contains(toInteger(pssId));
			}
			//System.out.println("liveSessionPssIdAccessList : " + liveSessionPssIdAccessList + ", Searching... "+ pssId + ", found ..."+isLive);
		}
		//System.out.println("isLive : "+ isLive);
		return isLive;
	}
	
	public static Integer toInteger(String arg) {
		return Integer.valueOf(arg);
	}
%>

<%
//ArrayList<String> subjects = (ArrayList<String>)session.getAttribute("studentCourses_studentportal");
//int noOfSubjects = subjects.size();
//HashMap<String,String> programSemSubjectIdWithSubject = (HashMap<String,String>)session.getAttribute("programSemSubjectIdWithSubjects_studentportal");
String type= (String)request.getSession().getAttribute("type");

//HashMap<String, String> programSemSubjectIds = new HashMap<String, String>();
HashMap<String, String> programSemSubjectIdWithSubjectForBacklog = (HashMap<String, String>) session
		.getAttribute("programSemSubjectIdWithSubjectForBacklog");
HashMap<String, String> programSemSubjectIdWithSubjectForCurrentSem = (HashMap<String, String>) session
		.getAttribute("programSemSubjectIdWithSubjectForCurrentsem");

int noOfSubjects = programSemSubjectIdWithSubjectForBacklog.size() + programSemSubjectIdWithSubjectForCurrentSem.size();
liveSessionPssIdAccessList = (List<Object>)session.getAttribute("liveSessionPssIdAccess_studentportal");
%>

<div class="courses">
	<div class="panel panel-default">
		<div class="panel-heading" role="tab" id="">
			<h4 class="panel-title"><%=type %>COURSES</h4>
			<ul class="topRightLinks list-inline">
				<li><a href="${pageContext.request.contextPath}/student/viewCourseHomePage" >SEE ALL</a></li>
				<li><a class="panel-toggler collapsed" role="button"
					data-toggle="collapse" data-parent="#accordion" href="#collapseTwo"
					aria-expanded="true"></a></li>
			</ul>
			<div class="clearfix"></div>
		</div>
		<!-- start of collapse two -->
		
	
		<div id="collapseTwo" 
			class="panel-collapse collapse in courses-panel-collapse"
			role="tabpanel">
			<!--panel body-->
			<div class="panel-body no-border">
	
				<div class="p-closed">
					<div class="no-data-wrapper no-border">
						<p class="no-data">
							<span class="fa-solid fa-book-bookmark iconCourse"></span>
							<%=noOfSubjects %>
							<a href="#">Courses</a>
						</p>
					</div>
				</div>
			<c:choose>
		
		<c:when test="<%=programSemSubjectIdWithSubjectForCurrentSem.size() > 0 %>">

				<%
         int count = 0;
        // for(String subject : subjects){
        for(String key : programSemSubjectIdWithSubjectForCurrentSem.keySet()){
        	 count++;
         %>

				<c:url value="student/viewCourseDetails" var="courseUrl">
					
					<c:param name="programSemSubjectId" value="<%=key %>" />
					
				</c:url>

				<div class="media">
					<div class="media-left">
						<span><%=count %></span>
					</div>
					<div class="media-body">
						<a href="${courseUrl}"><%=programSemSubjectIdWithSubjectForCurrentSem.get(key)%></a>
					</div>
					<div class="media-right">
						<c:set var="isLive" value="<%=checkIfLiveVideo(key) %>" />
						<c:if test="${isLive == true}">
							<span title="Live Subject" style="color: #fff;"><i class="fa-regular fa-eye"></i></span>
						</c:if>
						<c:if test="${isLive == false}">
							<span title="Recorded Subject" style="color: #fff;"><i class="fa-solid fa-video"></i></span>
						</c:if>
					</div>
				</div>

				<%
	         if(count == 6){
	        	 break;//SHow only 6 subjects
	         }
         
       } %>

		</c:when>
		<c:otherwise>
				<%
         int count = 0;
        // for(String subject : subjects){
        for(String key : programSemSubjectIdWithSubjectForBacklog.keySet()){
        	 count++;
         %>

				<c:url value="student/viewCourseDetails" var="courseUrl">
					
					<c:param name="programSemSubjectId" value="<%=key %>" />
					
				</c:url>

				<div class="media">
					<div class="media-left">
						<span><%=count %></span>
					</div>
					<div class="media-body">
						<a href="${courseUrl}"><%=programSemSubjectIdWithSubjectForBacklog.get(key)%></a>
						
					</div>
					<div class="media-right">
						<c:set var="isLive" value="<%=checkIfLiveVideo(key) %>" />
						<c:if test="${isLive == true}">
							<span title="Live Subject" style="color: #fff;"><i class="fa-regular fa-eye"></i></span>
						</c:if>
						<c:if test="${isLive == false}">
							<span title="Recorded Subject" style="color: #fff;"><i class="fa-solid fa-video"></i></span>
						</c:if>
					</div>
				</div>

				<%
	         if(count == 6){
	        	 break;//SHow only 6 subjects
	         }
         
       } %>

		
					</c:otherwise>
		</c:choose>
		<c:if test="<%=(programSemSubjectIdWithSubjectForBacklog.size() > 0  && programSemSubjectIdWithSubjectForCurrentSem.size() != 0)%>">
		<a href="#" data-toggle="modal" data-target="#coursesModal"
					class="modalBtn media">View  Backlog Subjects...</a>
		</c:if>	
		
		 <c:if test='<%=(type.equals("Backlog ") && programSemSubjectIdWithSubjectForBacklog.size() > 6)%>' >
		<a href="#" data-toggle="modal" data-target="#coursesModal"
					class="modalBtn media"> <%=programSemSubjectIdWithSubjectForBacklog.size() - 6 %> More Backlog...</a>
		</c:if>	

				<div class="clearfix"></div>
			</div>
			
			

		</div>
		
	
	</div>
</div>





<!--MODAL FOR COURSES-->
<div class="courses modal fade" id="coursesModal" tabindex="-1"
	role="dialog">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title">BackLog Courses</h4>
			</div>
			<div class="modal-body">

				<%
        int  count = 0;
        // for(String subject : subjects){
        for(String key : programSemSubjectIdWithSubjectForBacklog.keySet()){
        	 count++;
         %>


				<c:url value="/student/viewCourseDetails" var="courseUrl">

					<c:param name="programSemSubjectId" value="<%=key%>" />
				
				</c:url>

				<div class="media">
					<div class="media-left">
						<span><%=count %></span>
					</div>
					<div class="media-body">
						<a href="${courseUrl }"><%=programSemSubjectIdWithSubjectForBacklog.get(key) %></a>
					</div>
					<div class="media-right">
						<c:set var="isLive" value="<%=checkIfLiveVideo(key) %>" />
						<c:if test="${isLive == true}">
							<span title="Live Subject" style="color: #404041;"><i class="fa-regular fa-eye"></i></span>
						</c:if>
						<c:if test="${isLive == false}">
							<span title="Recorded Subject" style="color: #404041;"><i class="fa-solid fa-video"></i></span>
						</c:if>
					</div>
				</div>

				<%} %>

			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Done</button>
			</div>
		</div>
	</div>
</div>
