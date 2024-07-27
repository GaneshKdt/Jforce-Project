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

String type= (String)request.getSession().getAttribute("type");


HashMap<String, String> programSemSubjectIdWithSubjectForBacklog = (HashMap<String, String>) session
		.getAttribute("programSemSubjectIdWithSubjectForBacklog");
HashMap<String, String> programSemSubjectIdWithSubjectForCurrentSem = (HashMap<String, String>) session
		.getAttribute("programSemSubjectIdWithSubjectForCurrentsem");

int noOfSubjects = programSemSubjectIdWithSubjectForBacklog.size() + programSemSubjectIdWithSubjectForCurrentSem.size();
liveSessionPssIdAccessList = (List<Object>)session.getAttribute("liveSessionPssIdAccess_studentportal");
boolean isRedis = (boolean)request.getSession().getAttribute("isRedis");

%>



<div class="d-flex align-items-center text-wrap">
			<span class="fw-bold me-2 text-uppercase"><small class="fs-5"><%=type %>COURSES</small><c:if test="<%=isRedis%>">:</c:if></span>
				<div class="ms-auto">
					<a href="${pageContext.request.contextPath}/student/viewCourseHomePage" class="text-dark me-1"><small class="text-nowrap">SEE ALL</small></a>	
					<a data-bs-toggle="collapse" href="#collapseFour" role="button" aria-expanded="false" aria-controls="collapseFour" class="text-muted"> 
					<i class="fa-solid fa-square-minus"></i></a>
				</div>
		</div>
		
		<%if(noOfSubjects == 0){ %>
					<div class="card">
						<div class="card-body text-center">
						<h6><i class="fa-solid fa-book-bookmark"></i><small> <%=noOfSubjects%> Courses</small></h6>
						</div>
					</div>	
			
		<%}else{%>
				<div class="collapse mb-2 " id="collapseFour" >			
					<div class="card">
						<div class="card-body text-center ">
						<h6><i class="fa-solid fa-book-bookmark"></i><small> <%=noOfSubjects%>  Courses</small></h6>
						</div>
					</div>	
				</div>	
		
				<div class="collapse show" id="collapseFour" >			
			 		<div class="list-group w-100">
					<c:choose>
						<c:when test="<%=programSemSubjectIdWithSubjectForCurrentSem.size() > 0 %>">
							<% int count = 0;
								for(String key : programSemSubjectIdWithSubjectForCurrentSem.keySet()){
        							 count++; %>

							<c:url value="student/viewCourseDetails" var="courseUrl">
								<c:param name="programSemSubjectId" value="<%=key %>" />
							</c:url>
			
							<a href="${courseUrl} " class=" text-dark fw-semibold list-group-item list-group-item-action">
								<%=count %> <%=programSemSubjectIdWithSubjectForCurrentSem.get(key)%>
						
								<c:set var="isLive" value="<%=checkIfLiveVideo(key) %>" />
									<c:if test="${isLive == true}">
									 	<span  title="Live Subject" class="float-end fs-5" ><i class="fa fa-eye "></i></span>
									</c:if>
									<c:if test="${isLive == false}">
										<span title="Recorded Subject" class="float-end fs-5"><i class="fa fa-video-camera "></i></span>
									</c:if>	
							</a>	
		
				<% if(count == 6){
	        	 break;//SHow only 6 subjects
	         }
       } %>
       				</c:when>
					<c:otherwise>
	
						<% int count = 0;
						      for(String key : programSemSubjectIdWithSubjectForBacklog.keySet()){
        	 					count++;%>

									<c:url value="student/viewCourseDetails" var="courseUrl">
										<c:param name="programSemSubjectId" value="<%=key %>" />
									</c:url>
										
									<a href="${courseUrl} " class=" text-dark list-group-item list-group-item-action"><span class="d-inline-block text-truncate w-50  mt-2">
										<%=count %> <%=programSemSubjectIdWithSubjectForBacklog.get(key)%></span>
											<c:set var="isLive" value="<%=checkIfLiveVideo(key) %>" />
												<c:if test="${isLive == true}">
													<span title="Live Subject" class="float-end fs-5" ><i class="fa fa-eye "></i></span>
												</c:if>
												<c:if test="${isLive == false}">
													<span title="Recorded Subject" class="float-end fs-5"><i class="fa fa-video-camera "></i></span>
												</c:if>		
									</a>
					<%
				         if(count == 6){
				        	 break;//SHow only 6 subjects
				         }		        
			       } %>

					</c:otherwise>
			</c:choose>
		</div>
		 	<li class="list-group-item  d-flex justify-content-end align-items-end ">
				<c:if test="<%=(programSemSubjectIdWithSubjectForBacklog.size() > 0  && programSemSubjectIdWithSubjectForCurrentSem.size() != 0)%>">
					<a href="#" data-bs-toggle="modal" data-bs-target="#modal" class="text-end">View  Backlog Subjects...</a>
			</c:if>	
		
			 <c:if test='<%=(type.equals("Backlog ") && programSemSubjectIdWithSubjectForBacklog.size() > 6)%>' >
					<a href="#"  data-bs-toggle="modal" data-bs-target="#modal" class="text-end "> <%=programSemSubjectIdWithSubjectForBacklog.size() - 6 %> More Backlog...</a>
			</c:if>	
			</li>

		</div>
	<%} %>

<div class="modal fade  modal-dialog-scrollable modal-fullscreen-xl-down" id="modal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="exampleModalLabel">BackLog Courses</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
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
				<p>
				<span class="fs-3 text-muted mx-3"><%=count %></span>
				<a href="${courseUrl} " class=" text-dark  d-inline-block text-truncate w-50"><%=programSemSubjectIdWithSubjectForBacklog.get(key)%></a>
				<span>
						<c:set var="isLive" value="<%=checkIfLiveVideo(key) %>" />
						<c:if test="${isLive == true}">
							<span title="Live Subject"  class="float-end mt-3"><i class="fa fa-eye fa-xl"></i></span>
						</c:if>
						<c:if test="${isLive == false}">
							<span title="Recorded Subject"  class="float-end mt-3"><i class="fa fa-video-camera fa-xl"></i></span>
						</c:if>
					</span>	</p>				
				
				<hr class="hr hr-blurry" />

				<%} %>
      </div>
      <div class="modal-footer">
   		 <button type="button" class="btn bg-danger" data-bs-dismiss="modal">Done</button>
      </div>
    </div>
  </div>
  </div>
