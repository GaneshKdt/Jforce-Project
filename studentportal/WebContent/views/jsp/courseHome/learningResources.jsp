<%-- <%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.nmims.beans.ContentBean"%>
<%@page import="com.nmims.beans.StudentBean"%>
<%@page import="com.nmims.beans.VideoContentBean"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%

ArrayList<ContentBean> contentList =  (ArrayList<ContentBean>)session.getAttribute("contentList");
int noOfLearningResources = contentList != null ? contentList.size() : 0;

ArrayList<ContentBean> lastCycleContentList =  (ArrayList<ContentBean>)session.getAttribute("lastCycleContentList");
int noOfLastCycleRecordings = lastCycleContentList != null ? lastCycleContentList.size() : 0;

StudentBean studentSemCheck = (StudentBean)session.getAttribute("semCheck");

ArrayList<VideoContentBean> videoContentList =  (ArrayList<VideoContentBean>)session.getAttribute("videoContentList");
int noOfVideoContents = videoContentList != null ? videoContentList.size() : 0;
%>



<div class="course-learning-resources-m-wrapper" id="learningResources">
	<div class="panel panel-default panel-courses-page">
		<div class="panel-heading" role="tab" id="">
			<h2>Learning Resources</h2>
			<!---TOP TABS-->
			<div class="custom-clearfix clearfix"></div>
			<ul class="topRightLinks list-inline">
				<li>
					<h3 class=" green"><span><%=noOfLearningResources %></span> Resources Available</h3>
				</li>
				
				<%if(noOfLastCycleRecordings > 0){ %> 
				<li><a href="#" data-toggle="modal" data-target="#lastCycleRecordings"><h3 class="green">Last Cycle Recordings</h3></a></li>
				<%} %>
				<li><a class="panel-toggler collapsed"  role="button" data-toggle="collapse" href="#collapseThree" aria-expanded="true"></a></li>
				<div class="clearfix"></div>
			</ul>
			<div class="clearfix"></div>
		</div>
		<div class="clearfix"></div>
		<%if(noOfLearningResources == 0){ %>
			<div id="collapseThree" class="panel-collapse collapse academic-schedule courses-panel-collapse panel-content-wrapper" role="tabpanel">
		<%}else{ %>
			<div id="collapseThree" class="panel-collapse collapse in academic-schedule courses-panel-collapse panel-content-wrapper" role="tabpanel">
		<%} %>
		
			<div class="panel-body" > 
				<%if(noOfLearningResources == 0){ %>
					<div class="no-data-wrapper">
						<p class="no-data"><span class="icon-icon-pdf"></span>No new Learning Resources</p>
					</div>
				<%}else{ %>
				
				<div class="data-content">
					<div class="col-md-12 p-closed"> 
						<i class="icon-icon-view-submissions"></i>
						<h4><span><%=noOfLearningResources %></span> Resources Available<span class="expand">Expand to view all Sessions</span></h4>
					</div>
						<div class="table-responsive">
							<table class="table table-striped " id="courseHomeLearningResources">
								<thead>
									<tr>
										<th>SI</th>
										<th>Name</th>
										<th>Description</th>
										<th>Action</th>
									</tr>
								</thead>
								<tbody>
									    <!-- Code for Video Content Start -->
							    <% try{ 
							    	if(noOfVideoContents>0){
							    		int count=0;
							    		for(VideoContentBean video:videoContentList){
							    %>
							    	<tr>
							    		<td>
							    			
							    			<a href="/acads/watchVideos?id=<%=video.getId() %>" target="_blank">
							    			<i class="fa fa-play-circle-o" style="font-size:18px;" aria-hidden="true"></i>
							    			</a>
							    		</td>
							    		<td><%=video.getSubject() %> - <%=video.getFileName() %>  </td>
							    		<td></td>
							    		<td>
							    			<a href="/acads/watchVideos?id=<%=video.getId() %>" target="_blank">Watch</a>
							    			 <h4>	&nbsp; / &nbsp;	</h4>
							    			<a id="downloadVideo<%=count %>" href="<%=video.getMobileUrlHd() %>">Download</a>
							    			<span>(Right Click and Save Link As)</span>
							    		</td>
							    	
							    	</tr>
							    
							    <%	count++;
							    		}
							    	}
								}
								catch(Exception e){}
								%> 
							    <!-- Code for Video Content End -->
							    
									
									<c:forEach var="contentFile" items="${contentList}" varStatus="status">
							        <tr>
							            <td ><c:out value="${status.count}"/></td>
										<td ><c:out value="${contentFile.name}"/></td>
										<td >
										<c:out value="${contentFile.description}"/>
     									<c:set var = "string1" value = "${contentFile.description}"/>
										<c:set var = "string2" value = "${fn:toUpperCase(string1)}"/>
										<c:set var = "string3" value = "${fn:toUpperCase(string1)}"/>
										<c:if test="${fn:substring(string2,0,4) eq 'TO V'}"> 
										   &nbsp;
										   <a href="https://akamaicdn.webex.com/client/WBXclient-32.11.0-388/atrecply.msi" >Windows</a> &nbsp;
										   <a href="https://akamaicdn.webex.com/client/WBXclient-32.11.0-388/mac/intel/webexplayer_intel.dmg" >Mac OSX</a>
										</c:if>
										</td>
										
										<td >
										<c:if test="${not empty contentFile.previewPath}">
										   <a href="#" onClick="window.open('<spring:eval expression="@propertyConfigurer.getProperty('CONTENT_PREVIEW_PATH')" />${contentFile.previewPath}')" > Download</a>
										</c:if>
										
										<c:if test="${fn:endsWith(contentFile.previewPath, '.pdf') || fn:endsWith(contentFile.previewPath, '.PDF')
										|| fn:endsWith(contentFile.previewPath, '.Pdf')}">
																	     
											<c:url value="acads/previewContent" var="previewContentLink">
											  <c:param name="previewPath" value="${contentFile.previewPath}" />
											  <c:param name="name" value="${contentFile.name}" />
											</c:url>
										     <a href="/acads/previewContent?previewPath=${contentFile.previewPath}&name=${contentFile.name}" target="_blank">View</a>
										
										      <a href="/${previewContentLink}" target="_blank">View</a>
										
										 </c:if>
										
										<c:if test="${not empty contentFile.webFileurl}">
											<c:if test="${contentFile.urlType == 'View' || contentFile.urlType == '' || empty contentFile.urlType	}">
										   		<a href="${contentFile.webFileurl}" target="_blank">View</a>
										   </c:if>
										   
										   <c:if test="${contentFile.urlType == 'Download'}">
										   		<a href="${contentFile.webFileurl}" target="_blank"> Download</a>
										   </c:if>
										</c:if>
										</td>
										
							        </tr>   
							    </c:forEach>
							
								</tbody>
							</table>
							</div>
							<%if(noOfLearningResources > 5){ %>
							<div class="load-more-table">
								<a>+<%=(noOfLearningResources - 5) %> More Resources <span class="icon-accordion-closed"></span></a>
							</div>
							<%} %>
						
				</div>
				<%} %>
				
			</div>
		</div>
	</div>
</div>


<%if(noOfLastCycleRecordings > 0){ %>
<div class="modal fade assignments" id="lastCycleRecordings" tabindex="-1" role="dialog">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title">Last Cycle Recordings</h4>
      </div>
      <div class="modal-body">
      	
      					<%
      					if(studentSemCheck != null){
      						System.out.println("Got sem in LR.jsp"+studentSemCheck.getSem());
      						if(studentSemCheck.getSem().equals("1") || studentSemCheck.getSem().equals("2")){
      					%>
      						<h4>
      							<b>
      								To view the last cycle recordings please refer to Session videos for now, when the same is updated under &quot;Last cycle recordings&quot; students will be notified via email
      							</b>
      						</h4>
      					<% 
      						}
      					
      					%>
      					
      					<%
      					if(studentSemCheck.getSem().equals("3") || studentSemCheck.getSem().equals("4")){
      					%>
      						<h4>
      							<b>
									Recordings will be uploaded shortly, you will be notified via email
      							</b>
      						</h4>
      					<%	}
      					}
      					%>
						
					
						<div class="table-responsive">
							<table class="table table-striped " id="courseHomeLearningResources">
								<thead>
									<tr>
										<th>SI</th>
										<th>Name</th>
										<th>Description</th>
										<th>Action</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="contentFile" items="${lastCycleContentList}" varStatus="status">
							        <tr>
							            <td ><c:out value="${status.count}"/></td>
										<td ><c:out value="${contentFile.name}"/></td>
										<td ><c:out value="${contentFile.description}"/></td>
										
										<td >
										<c:if test="${not empty contentFile.previewPath}">
										   <a href="#" onClick="window.open('<spring:eval expression="@propertyConfigurer.getProperty('CONTENT_PREVIEW_PATH')" />${contentFile.previewPath}')" /> Download</a>
										</c:if>
										
										<c:if test="${fn:endsWith(contentFile.previewPath, '.pdf') || fn:endsWith(contentFile.previewPath, '.PDF')
										|| fn:endsWith(contentFile.previewPath, '.Pdf')}">
										    <a href= "<c:url value = "../acads/previewContent?previewPath=${contentFile.previewPath}&name=${contentFile.name}" />  target="_blank">View</a>
										</c:if>
										
										<c:if test="${not empty contentFile.webFileurl}">
											<c:if test="${contentFile.urlType == 'View' || contentFile.urlType == '' || empty contentFile.urlType	}">
										   		<a href="${contentFile.webFileurl}" target="_blank">View</a>
										   </c:if>
										   <c:if test="${contentFile.urlType == 'Download'}">
										   		<a href="${contentFile.webFileurl}" target="_blank"> Download</a>
										   </c:if>
										</c:if>
										</td>
										
							        </tr>   
							    </c:forEach>
								</tbody>
							</table>
							</div>
							
						
      
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Done</button>
      </div>
    </div>
  </div>
</div>

<%}%>


<script>
for(var i =  0; i< <%=noOfVideoContents %>; i++){
document.getElementById("downloadVideo"+i).addEventListener("click", function(event){
	
	console.log('Clicked');
	event.preventDefault()
});
}
	/* function downloadInfo(){
		console.log('Clicked');
		event.preventDefault();
		alert('Clicked');    
	
	} */
	
</script> --%>







<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.itextpdf.text.log.SysoCounter"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.nmims.beans.ContentStudentPortalBean"%>
<%@page import="com.nmims.beans.ModuleContentStudentPortalBean"%>
<%@page import="com.nmims.beans.StudentStudentPortalBean"%>
<%@page import="com.nmims.beans.VideoContentStudentPortalBean"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
<style type="text/css">


.disableClick{
	pointer-events: none;
	cursor: default;
}

.modal-open .modal {
    overflow-x: scroll;
    overflow-y: scroll;
}
#style_prevu_kit
{
    position: relative;
    -webkit-transition: top 200ms ease-in;
    -webkit-transform: scale(1); 
    -ms-transition: all 200ms ease-in;
    -ms-transform: scale(1); 
    -moz-transition: all 200ms ease-in;
    -moz-transform: scale(1);
    transition: all 200ms ease-in;
    transform: scale(1);   

}
#style_prevu_kit:hover
{
    box-shadow: 0px 0px 50px #ffccbc  ;
    z-index: 2;
    -webkit-transition: all 200ms ease-in;
    -webkit-transform: scale(1);
    -ms-transition: all 200ms ease-in;
    -ms-transform: scale(1);   
    -moz-transition: all 200ms ease-in;
    -moz-transform: scale(1);
    transition: all 200ms ease-in;
    transform: scale(1);
}    
.panel-heading {
    	padding : 5px 5px 5px 5px;
    }

/* new code */
.panel-group .panel {
        border-radius: 0;
        box-shadow: none;
        border-color: #d7ccc8;
    }

    .panel-default > .panel-heading {
        padding: 0;
        border-radius: 0;
        color: #212121;
        background-color: #ffffff ;
        border-color: #EEEEEE;
    }

    .panel-title {
        font-size: 14px;
    }

    .panel-title > a {
        display: block;
        padding: 15px;
        text-decoration: none;
    }

    .more-less {
        float: right;
        color: #212121;
    }

    .panel-default > .panel-heading + .panel-collapse > .panel-body {
        border-top-color: #EEEEEE;
    }


.tile-progress {

color: #fff;
margin-bottom: 20px;
-webkit-border-radius: 5px;
-moz-border-radius: 5px;
border-radius: 5px;
-webkit-background-clip: padding-box;
-moz-background-clip: padding;
background-clip: padding-box;
-webkit-border-radius: 3px;
-moz-border-radius: 3px;
border-radius: 3px;
}
.tile-progress .tile-header {
    padding: 20px 20px 20px 20px;
background-color: #0288D1;
    border-top-left-radius: 7px;
    border-top-right-radius: 7px;
}


.tile-progress .tile-progressbar {
height: 5px;
background: rgba(0,0,0,0.18);
margin: 0;
}
.tile-progress .tile-progressbar span {
background: #fff;
}
.tile-progress .tile-progressbar span {
display: block;
background: #e25050;
width: 0;
height: 100%;
-webkit-transition: all 1.5s cubic-bezier(0.230,1.000,0.320,1.000);
-moz-transition: all 1.5s cubic-bezier(0.230,1.000,0.320,1.000);
-o-transition: all 1.5s cubic-bezier(0.230,1.000,0.320,1.000);
transition: all 1.5s cubic-bezier(0.230,1.000,0.320,1.000);
}
.tile-progress .tile-footer {
padding: 10px;
text-align: left;
background:white;
color:black;
-webkit-border-radius: 0 0 3px 3px;
-webkit-background-clip: padding-box;
-moz-border-radius: 0 0 3px 3px;
-moz-background-clip: padding;
border-radius: 0 0 3px 3px;
background-clip: padding-box;
-webkit-border-radius: 0 0 3px 3px;
-moz-border-radius: 0 0 3px 3px;
border-radius: 0 0 3px 3px;
min-height:100px;
}
.tile-progress.tile-red {
background-color: #f56954;
color: #fff;
}

.tile-progress.tile-blue {
background-color: #0073b7;
color: #fff;
}
.tile-progress.tile-aqua {
background-color: #00c0ef;
color: #fff;
}
.tile-progress.tile-green {
background-color: #00a65a;
color: #fff;
}
.tile-progress.tile-cyan {
background-color: #00b29e;
color: #fff;
}
.tile-progress.tile-purple {
background-color: #ba79cb;
color: #fff;
}
.tile-progress.tile-pink {
background-color: #ec3b83;
color: #fff;
}
.collapse {
    background-color: #eeeeee;

} 

.tile-header span {
    font-size:1rem;
    float:left;
}  

.modal-footer {
     padding: 0; 
     border-top:none; 
     }

										 		#downloadVideo_Notused {
												  pointer-events: none;
												  cursor: default;
												}
</style>



<%

ArrayList<ModuleContentStudentPortalBean> moduleDocumentList =  (ArrayList<ModuleContentStudentPortalBean>)session.getAttribute("moduleDocumentList");
int noOfLearningResources = moduleDocumentList != null ? moduleDocumentList.size() : 0;

/*
ArrayList<ContentBean> downloadCenter =  (ArrayList<ContentBean>)session.getAttribute("downloadCenter");
ArrayList<ModuleContentBean> downloadCenterLink =  (ArrayList<ModuleContentBean>)session.getAttribute("downloadCenterLink");
ArrayList<ContentBean> lastCycleContentList =  (ArrayList<ContentBean>)session.getAttribute("lastCycleContentList");
int noOfLastCycleRecordings = lastCycleContentList != null ? lastCycleContentList.size() : 0; */

StudentStudentPortalBean studentSemCheck = (StudentStudentPortalBean)session.getAttribute("semCheck");

ArrayList<VideoContentStudentPortalBean> videoContentList =  (ArrayList<VideoContentStudentPortalBean>)session.getAttribute("videoContentList");
int noOfVideoContents = videoContentList != null ? videoContentList.size() : 0;

ArrayList<ContentStudentPortalBean> contentList =  (ArrayList<ContentStudentPortalBean>)session.getAttribute("contentList");
//contentList.addAll(downloadCenter);
int noOfContents = contentList != null ? contentList.size() : 0;

System.out.println("IN JSP got contentList size : "+contentList !=null ? contentList.size() : 0);

%>
<style>
.nodata { 
    vertical-align: middle;
    color: #a6a8ab;
    font: 1.00em "Open Sans";
    text-align: center;
    margin: 0;
}
</style>


<div class="course-learning-resources-m-wrapper" id="learningResources">
	<div class=" panel-courses-page">
		<div class="panel-heading" role="tab" id="">
			<h2>Learning Resources</h2>
			<!---TOP TABS-->
			<!-- 
			Hiding the module count
			<div class="custom-clearfix clearfix"></div>
			<ul class="topRightLinks list-inline">
				<li>
					<h3 class=" green"><span><%=noOfLearningResources %></span> Modules </h3>
				</li>
				
				<%-- <%if(noOfLastCycleRecordings > 0){ %> 
				<li><a href="#" data-toggle="modal" data-target="#lastCycleRecordings"><h3 class="green">Last Cycle Recordings</h3></a></li>
				<%} %> --%>
				<li><a class="panel-toggler collapsed"  role="button" data-toggle="collapse" href="#collapseThree" aria-expanded="true"></a></li>
				<div class="clearfix"></div>
			</ul>
			<div class="clearfix"></div> 
			-->
			
		</div>
		<div class="clearfix"></div>
		<%if(noOfLearningResources == 0 && noOfVideoContents==0 && noOfContents==0){ %>
			<div id="collapseThree" class="panel-collapse collapse academic-schedule courses-panel-collapse panel-content-wrapper" role="tabpanel">
		<%}else{ %>
			<div id="collapseThree" class="panel-collapse collapse in academic-schedule courses-panel-collapse panel-content-wrapper" role="tabpanel">
		<%} %>
		
			<div class="panel-body" style="padding: 20px;"> 
				<%if(noOfLearningResources == 0 && noOfVideoContents==0 && noOfContents==0 ){ %>
					<div class="no-data-wrapper">
						<h6 class="no-data nodata"> <span class="icon-icon-pdf"></span>No new Learning Resources</h6>
					</div>
				<%}else{ %>
				
				<div class="data-content">
					<%-- <div class="col-md-12 p-closed"> 
						<i class="icon-icon-view-submissions"></i>
						<h4><span><%=noOfLearningResources %></span> Modules <span class="expand">Expand to view all Sessions</span></h4>
					</div> --%>
					
					<div style="margin-top:10px; width:14rem;" data-toggle="modal" data-target="#myModal" >
				
					 <div id="style_prevu_kit" class="tile-progress tile-primary" style="cursor:pointer !important;">
                                            <div class="tile-header"  style="font-size: 13px;margin-bottom: -10px;min-height: 80px !important;border-radius: 2px;padding: 5px 5px 5px 5px;">  <br>  
                                               <p style="align-self: center;color:white;padding-left:40px;">DOWNLOAD CENTER</p>
                                                  </div>
                                              <div class="tile-footer" style="background:#0288D1;">
                                           <i class="fa-solid fa-download large" style="font-size:26px;color:white;padding-left:40%;"></i>
 												</div>
                                 </div> 
				</div>
				
				<!--  
				Commenting on 24/01/2020
				Hiding the LR section 
					<c:forEach var="moduleDocumentList" items="${moduleDocumentList}" varStatus="status" >
					 	 <a href="/studentportal/moduleLibraryList?moduleId=${moduleDocumentList.id}&subject=${subject}&type=doc" >
					 	 
					 <div class="col-sm-2" style="margin-top:10px;"  >
					 <div id="style_prevu_kit" class="tile-progress tile-primary" style="cursor:pointer !important;">
                                            <div class="tile-header" id="tileHeader" style="font-size: 13px;margin-bottom: -10px;min-height: 80px !important;border-radius: 2px;padding: 5px 5px 5px 5px;"> ${moduleDocumentList.moduleName} <br>   <%-- ${chapter.title}   --%>                                </div>
                                             <div class="tile-footer" >
                                               
 														<div class="statistics">
 														
 														<div class="col-sm-8" style="padding:0px">
 														<p> Progress:  ${moduleDocumentList.percentageCombined}%   </p>
 														</div>
 														
 														<div class="col-sm-8" style="padding:0px">
 															<div class="col-sm-12" style="height:5px; background-color:white; width:100%; border:10px; padding:0px 0px; ">
 																<div class="col-sm-12" style="height:5px; background-color:#9fa1ab; width:${moduleDocumentList.percentageCombined}%; border:10px; padding:0px 5px;">
 																	
 																</div>
 															</div>
 														</div>
 															
 														</div>
 												</div>
                                 </div> </div>
                                 
                        	</a>         
					</c:forEach>
				 -->
					 
				

  <!-- Modal -->
  <div class="modal fade" id="myModal" role="dialog">
    <div class="modal-dialog">
   
      <!-- Modal content--> 
      <div class="modal-content" style="height:100%;width:160%;margin-left:-25%;">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal">&times;</button>
          <h4 class="modal-title">Download Center</h4>
        </div>
        <div class="modal-body">
          <div class="table-responsive">
          	<!-- Content from old contentList Start -->
          					<table class="table table-striped " id="courseHomeLearningResources">
								<thead>
									<tr>
										<th>Sr No.</th>
										<th>Name</th>
										<th>Description</th>
										<th>Action</th>
									</tr>
								</thead>
								
								<tbody>
									   <!-- Code for Video Content Start -->
							    <% try{ 
							    	if(noOfVideoContents>0){
							    		int count=0;
							    		for(VideoContentStudentPortalBean video:videoContentList){
							    %>
							    	<tr style="display:table-row;">
							    		<td>
							    			
							    			<a href="/acads/student/watchVideos?id=<%=video.getId() %>" target="_blank">
							    			<i class="fa-regular fa-circle-play" style="font-size:18px;" aria-hidden="true"></i>
							    			</a>
							    		</td>
							    		<td><%=video.getSubject() %> - <%=video.getFileName() %>  </td>
							    		<td>
							    			<%if(!StringUtils.isBlank(video.getFacultyName())) {%>
							    				<span>Faculty Name : </span> <%=video.getFacultyName() %> <br>
							    			<% } %>
							    			
							    			<span>Date : </span> <%=video.getSessionDate() %> <br>
							    		
							    			<% if(!StringUtils.isBlank(video.getTrack())){ %>
							    				<span>Track : </span> <%=video.getTrack() %>
							    			<% } %>
							    			
							    		</td>
							    		<td>
							    			<a href="/acads/student/watchVideos?id=<%=video.getId() %>" target="_blank">Watch</a>
							    			
							    			<%
							    				if( !"Enterprise Guide".equals(video.getSubject()) && !"Enterprise Miner".equals(video.getSubject()) && !"Visual Analytics".equals(video.getSubject()) ){
											%>
							    			 <h4>	&nbsp; / &nbsp;	</h4>
							    			<!-- 
										 	<a id="downloadVideo<%=count %>" href="<%=video.getMobileUrlHd() %>">Download</a> 
										 	<span>(Right Click and Save Link As)</span>
										 	 -->
										 	<%--  <a id="downloadVideo<%=count %>" href="#">Download</a>  --%>
							    			<%-- <span id="noticeSpan<%=count %>">Loading...
							    			
							    			<!-- (Right Click and Save Link As) -->
							    			<!-- (Download Disabled, Will be available in due time) -->
							    			
							    			</span> --%>
							    			<%
												}
							    			%>
							    		</td>
							    	
							    	</tr>
							    	
							    	
							    	
							    <%	count++;
							    		}
							    	}
								}
								catch(Exception e){}
								%> 
							    <!-- Code for Video Content End -->
							    
							
									<c:forEach var="contentFile" items="${contentList}" varStatus="status">
							        <tr style="display:table-row;">
							            <td ><c:out value="${status.count}"/></td>
										<td ><c:out value="${contentFile.name}"/></td>
										<td > 
										<c:out value="${contentFile.description}"/>
     									<c:set var = "string1" value = "${contentFile.description}"/>
										<c:set var = "string2" value = "${fn:toUpperCase(string1)}"/>
										<c:set var = "string3" value = "${fn:toUpperCase(string1)}"/>
										<c:if test="${fn:substring(string2,0,4) eq 'TO V'}"> 
										   &nbsp;
										   <a href="https://akamaicdn.webex.com/client/WBXclient-32.11.0-388/atrecply.msi" >Windows</a> &nbsp;
										   <a href="https://akamaicdn.webex.com/client/WBXclient-32.11.0-388/mac/intel/webexplayer_intel.dmg" >Mac OSX</a>
										</c:if>
										</td>
										
										<td >
										
										
										<c:if test="${not empty contentFile.previewPath}">
										<c:if test="${!fn:endsWith(contentFile.previewPath, '.pdf') && !fn:endsWith(contentFile.previewPath, '.PDF')
										&& !fn:endsWith(contentFile.previewPath, '.Pdf')}">
										   <a href="#" onClick="window.open('<spring:eval expression="@propertyConfigurer.getProperty('CONTENT_BASE_PATH')" />${contentFile.previewPath}')" > Download</a>
										</c:if>
										</c:if>
										<c:if test="${not empty contentFile.documentPath}">
									    <c:if test="${!fn:endsWith(contentFile.documentPath, '.pdf') && !fn:endsWith(contentFile.documentPath, '.PDF')
										&& !fn:endsWith(contentFile.documentPath, '.Pdf')}">
										    <a href="/${contentFile.documentPath}" target="_blank" style="margin:0px;padding:0px;width:21px;bottom:50px;left:90%;">
									            		Download
									            	  </a>
										</c:if>
									</c:if>
										<c:if test="${fn:endsWith(contentFile.previewPath, '.pdf') || fn:endsWith(contentFile.previewPath, '.PDF')
										|| fn:endsWith(contentFile.previewPath, '.Pdf')}">
											<c:url value="acads/previewContent" var="previewContentLink">
											  <c:param name="previewPath" value="${contentFile.previewPath}" />
											  <c:param name="name" value="${contentFile.name}" />
											</c:url>
										<%--      <a href="/acads/previewContent?previewPath=${contentFile.previewPath}&name=${contentFile.name}" target="_blank">View</a>
										 --%>
										      <a href="/${previewContentLink}" target="_blank">View</a>
										
										 </c:if>
										
										<c:if test="${not empty contentFile.webFileurl}">
											<c:if test="${contentFile.urlType == 'View' || contentFile.urlType == '' || empty contentFile.urlType	}">
										   		<a href="${contentFile.webFileurl}" target="_blank">View</a>
										   </c:if>
										   
										   <c:if test="${contentFile.urlType == 'Download'}">
										   		<a href="${contentFile.webFileurl}" target="_blank"> Download</a>
										   </c:if>
										</c:if>
										</td>
										
							        </tr>   
							    </c:forEach>
							
								</tbody>
							</table>
				
											
											</div>
        </div>
        <div class="modal-footer" style="padding : 5px 10px;">
          <button type="button" class="btn btn-default" data-dismiss="modal" style="width: 20%;">Close</button>
        </div>
      </div>
      
    </div>
  </div>
				</div>
				<%} %>
				
			</div>
		</div>
	</div>
</div>

<%-- 
<%if(noOfLastCycleRecordings > 0){ %>
<div class="modal fade assignments" id="lastCycleRecordings" tabindex="-1" role="dialog">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title">Last Cycle Recordings</h4>
      </div>
      <div class="modal-body">
      	
      					<%
      					if(studentSemCheck != null){
      						System.out.println("Got sem in LR.jsp"+studentSemCheck.getSem());
      						if(studentSemCheck.getSem().equals("1") || studentSemCheck.getSem().equals("2")){
      					%>
      						<h4>
      							<b>
      								To view the last cycle recordings please refer to Session videos for now, when the same is updated under &quot;Last cycle recordings&quot; students will be notified via email
      							</b>
      						</h4>
      					<% 
      						}
      					
      					%>
      					
      					<%
      					if(studentSemCheck.getSem().equals("3") || studentSemCheck.getSem().equals("4")){
      					%>
      						<h4>
      							<b>
									Recordings will be uploaded shortly, you will be notified via email
      							</b>
      						</h4>
      					<%	}
      					}
      					%>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Done</button>
      </div>
    </div>
  </div>
</div>

<%}%> --%>

<script>
function toggleIcon(e) {
    $(e.target)
        .prev('.panel-heading')
        .find(".more-less")
        .toggleClass('glyphicon glyphicon-menu-down glyphicon glyphicon-menu-up');
}
$('.panel-group').on('hidden.bs.collapse', toggleIcon);
$('.panel-group').on('shown.bs.collapse', toggleIcon);

</script>
    <script>
    $(document).ready(function(){
         var tileHeaderColors = ['#e53935 red darken-1','#3949ab indigo darken-1','#00897b teal darken-1','#fb8c00 orange darken-1',
        	 '#757575 grey darken-1','#5e35b1 deep-purple darken-1 ','#43a047 green darken-1','#ff8f00 amber darken-3'];
         var j=0;
         $('div#tileHeader').each(function(){
        	$(this).css('background-color',tileHeaderColors[j]); 
        	j=(j+1) % tileHeaderColors.length; 
         })
         
         

         function get_filesize(url, callback) {
             var xhr = new XMLHttpRequest();
             xhr.open("HEAD", url, true); // Notice "HEAD" instead of "GET",
                                          //  to get only the header
             xhr.onreadystatechange = function() {
                 if (this.readyState == this.DONE) {
                 	console.log("xhr >>>");
                     console.log(xhr);
                     callback(parseInt(xhr.getResponseHeader("Content-Length")));
                 }
             };
             xhr.send();
         }
		
         <%-- 
         Commented the content server recordings path
         <% try{ 
		    	if(noOfVideoContents>0){
		    		int count=0;
		    		for(VideoContentBean video:videoContentList){
		    %>
	         
	         get_filesize("<spring:eval expression="@propertyConfigurer.getProperty('CONTENT_SERVER_RECORDINGS_PATH')" /><%=video.getMeetingKey()%>.mp4", function(size) { 
	          	console.log("size : "+size);
	          	if(!isNaN(size)){
	          		if(size > 5000000){
	          			document.getElementById("noticeSpan<%=count %>")
	          				.innerHTML="(Right Click on button and Click 'Save Link As') .";
	          		}else{

	          			document.getElementById("noticeSpan<%=count %>")
	          				.innerHTML="( Video is currently unavailable to download, The link will automatically be enabled as soon as video is available ) .";
	          			  var element = document.getElementById("downloadVideo<%=count %>");
	          			  element.classList.add("disableClick");
	          		}	
	          	}else{

	          		document.getElementById("noticeSpan<%=count %>")
	          			.innerHTML="( Video is currently unavailable to download, The link will automatically be enabled as soon as video is available ) .";
	          		  var element = document.getElementById("downloadVideo<%=count %>");
	          		  element.classList.add("disableClick");
	          	}
	          	
	          	
	          });
		<%	count++;
		    		}
		    	}
			}
			catch(Exception e){}
			%>
		 --%>
         

         
         
    });
    
    
    function goToDetails(id){
    	console.log("test");
     	var colorId =  $('div#tileHeader').css('backgroundColor');
     	console.log('color Id '+colorId);
    	window.location.replace("/acads/student/moduleLibraryList?moduleId="+id+"&colorId="+colorId);
    	
    }
</script>

<script>
for(var i =  0; i< <%=noOfVideoContents %>; i++){

	try {
		document.getElementById("downloadVideo"+i).addEventListener("click", function(event){
			
			console.log('Clicked');
			event.preventDefault()
		});
	}
	catch(err) {
	    console.log("Catch error : "+err.message);
	}
	
}
</script> 

