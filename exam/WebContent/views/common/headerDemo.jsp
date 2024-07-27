 <%@page import="com.nmims.controllers.BaseController"%>
<%@page import="org.jsoup.Jsoup"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.nmims.beans.UserAuthorizationExamBean"%>
<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.StudentExamBean"%>
<%@page import="com.nmims.beans.ExamAnnouncementBean"%>
<%@page import="java.util.ArrayList"%>

 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
  

<%

	BaseController headerCon = new BaseController();
StudentExamBean lead = (StudentExamBean)session.getAttribute("studentExam");
// 	ArrayList<ExamAnnouncementBean> announcementsInHeader = (ArrayList<ExamAnnouncementBean>)session.getAttribute("announcementsExam");
// 	int noOfAnnouncemntsInHeader = announcementsInHeader != null ? announcementsInHeader.size() : 0;
	SimpleDateFormat formatterHeader = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat dateFormatterHeader = new SimpleDateFormat("dd-MMM-yyyy");
	String hideProfileLink = (String)request.getAttribute("hideProfileLink");
	String mobileVisibleHeaderClass = "";//Use this class to show header on login page only
	String linkedin = "https://blog.linkedin.com/2020/march/26/resources-to-help-you-navigate-the-challenges-of-todays-job-market?trk=lilblog_03-30-20_LiL-free-resources_learning";
	boolean showProfile = true;
	
	if(session.getAttribute("userId") == null){
		//Login page
		mobileVisibleHeaderClass = "visibleHeader";
	}
	
	String link = "", profileHref= "/studentportal/student/updateProfile";
	if(headerCon.checkLead(request, response)){ 
		link = "disabled";
		profileHref = "#";
		showProfile = false;
	}
%>

<head>
	<link rel="stylesheet" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/css/skip-navigation-header.css">

<style>
	#globalSearch{
 	height:70px !important;
} 
#globalSearch[placeholder]{
 	font-size :22px !important;
}
#globalSearch::placeholder {
  	font-size: 17px;
}
#searchResult .list-group .list-group-item{
font-weight: normal;
}
#searchResult .list-group .list-group-item:hover {
  background-color: aliceblue;
}
#searchResult .list-group .list-group-item::before:hover {
  background-color: black;
}
.buttonContrast{
	background-color:blue !important;
	border-color:blue !important;
	border-style: solid;
	color:white !important;
}
.linkContrast {
  color: blue !important;
}
.inputBorder{
	border : 1px solid blue !important;
}
</style>
</head>

<div class="sz-header <%=mobileVisibleHeaderClass%>">
	<div class="container-fluid">
	<input type="hidden" id="subjectCodeIds" value="${sessionScope.subjectCodeId_exam}" />
		<a class="skip-navigation" href="#main-content-info-bar">Skip Navigation</a><!-- Adding Skip Navigation link for keyboard navigation accessibility, card: 17484 -->
		
		<div class="sz-logo">
			<img src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/images/logo.png" class="img-responsive" alt="" />
		</div>
		
			
			<%if(session.getAttribute("userId") != null){ %>
						<ul class="sz-header-menu">
							<c:choose>
					 			<c:when test="${isLoginAsLead eq true}"> 
					 				<li>
					 					<a href="/studentportal/student/getFreeCoursesList"><span class="fa-solid fa-house" aria-hidden="true"></span>Home</a>
					 				</li>
									<li>
										<a href="https://ngasce.secure.force.com/nmcompleteFormRevised?id=<%= lead.getLeadId() %>" target="_blank">
											<span class="fa-solid fa-registered"></span>Register Now</a>
									</li>
									<!-- <li>
										<a href="/studentportal/student/emailCommunicationsForm">
											<span class="glyphicon glyphicon-envelope"></span>My Communications</a>
									</li> -->
			 		 			</c:when>
		                 <c:otherwise>
		                 
		               <%if(!"true".equals(hideProfileLink)){ 
										 if(showProfile){ %>
										<li><a href="#" onClick="focusSearchBar()" data-bs-toggle="modal" data-bs-target="#globalsearchmodel" >
											<span class="fa-solid fa-magnifying-glass"></span>Search</a>
										</li>	
									<%}} %> 
					
												<!-- Button trigger modal -->
						
						<!-- Modal -->
						<div class="modal" id="globalsearchmodel" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
						  <div class="modal-dialog">
						    <div class="modal-content">
						      <div class="modal-header">
						        <h1 class="modal-title fs-5 w-100" id="exampleModalLabel">
						        	<!-- search bar -->
							        <form class="d-flex" role="search" action="/studentportal/student/searchResultPage" >
								      <input class="form-control me-2" type="search" placeholder="Search here" id="globalSearch" aria-label="Search" name="search">
								    </form>
								</h1>
						       
						      </div>
						      <div class="modal-body">
									<a href="#" data-bs-toggle="modal" data-bs-dismiss="modal"
										data-bs-target="#SearchModal"> 
									</a >
									<br>
									<div id="searchResult"></div>
												
						      </div>
						      <div class="modal-footer">
						        <a  class="btn btn-secondary" id="allSearch" >Search</a>
	<!-- 					        <button type="button" class="btn btn-primary">Save changes</button> -->
						      </div>
						    </div>
						  </div>
						</div>
		                 
						  <%Boolean courseraAccess=(Boolean)request.getSession().getAttribute("courseraAccess");%>	
				 				<% if(courseraAccess){%>
								<% if(!"true".equals(hideProfileLink)) { %>
									<li><a href="/acads/student/showCourseraProducts">
										<span class="fa-regular fa-lightbulb"></span>Skillsets</a>
									</li>
					     		 <%} %>
							  <%} %>
									
									<%
										boolean consumerProgramStructureHasCSAccess = false;
										if(session.getAttribute("consumerProgramStructureHasCSAccess") != null){
											consumerProgramStructureHasCSAccess = (boolean) session.getAttribute("consumerProgramStructureHasCSAccess");
										}
										boolean csActiveInHeader = false;
										if(session.getAttribute("studentExam") != null){
											csActiveInHeader = ((StudentExamBean) session.getAttribute("studentExam")).isPurchasedOtherPackages();
										}
										if(!"true".equals(hideProfileLink) && !csActiveInHeader && consumerProgramStructureHasCSAccess){ %>
										<li><a href="/careerservices/showAllProducts"><span class="fa-solid fa-briefcase"></span>Career Services</a></li>
									<%} %>
									
									<li>
										<a href="/studentportal/student/myEmailCommunicationsForm">
											<span class="fa-solid fa-envelope"></span>My Communications</a>
									</li>
										<li class="notification-link">
											<a href="" data-bs-toggle="modal" data-bs-keyboard="true" data-bs-target="#announcements" class="studentAnnouncementCounter"> 
												<span class="icon-notification" id="announcementCounter">
													 <div class="notification-count"></div>
												</span>
											</a>
										</li>
								    
									<%if(!"true".equals(hideProfileLink)){ 
										 if(showProfile){ %>
										<li><a href="<%= profileHref %>" class="<%= link %>" ><span class="fa-regular fa-user"></span>My Profile</a></li>
									<%}} %>
						    	</c:otherwise>
							</c:choose>
						    <li><a href="/logout"><span class="icon-logout"></span>Logout</a></li>
						</ul>
						
						<!-- Announcements Modal -->
					  
			<%}else{ %>
				<div class="pull-right hidden-xs">
				<h2>Welcome to NGASCE Student Zone</h2>
				</div>
			<%} %>
		
		<div class="clearfix"></div>
	</div>
</div>
<!--header ends-->


<!-- Outer Model  -->
<div class="modal" id="announcements" aria-labelledby="exampleModalLabel">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h4 class="modal-title " id="exampleModalLabel">ALL ANNOUNCEMENTS</h4>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div id="loader" class="d-none text-center my-2">
      	<img src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/gifs/loading-29.gif" alt="Loading..." style="height:40px" />
      	<p class="text-danger fw-bold my-1">Loading...</p>
      </div> 
		<div class="modal-body">
			<ul>	
				<div id="announcementbody"></div>
			</ul>
		</div>
		<div class="modal-footer">
          	<button type="button" class="btn btn-default" id="archive">View Archive</button></div>
		</div>
	</div>
  </div>


<!-- Inner Model -->
<div id="announcementInsideModal"></div>
<%-- 		
<%if(noOfAnnouncemntsInHeader > 0){ %>
		<!--MODAL FOR ALL ANNOUNCEMENT-->

<div class="modal rounded-0" id="newAnnouncements" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h4 class="modal-title " id="exampleModalLabel">ALL ANNOUNCEMENTS</h4>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
			<div class="modal-body">
				<ul>

        
        	<%
        	 int count = 0;
	          for(ExamAnnouncementBean announcement : announcementsInHeader){
	        	  count++;
	        	  String announcementWithoutHtmlCharacgters = Jsoup.parse(announcement.getDescription()).text();
	        	  String announcemntBrief = announcementWithoutHtmlCharacgters.length() > 150 ? announcementWithoutHtmlCharacgters.substring(0, 149)+"..."  : announcementWithoutHtmlCharacgters;
	        	  Date formattedDate = formatterHeader.parse(announcement.getStartDate());
	  			  String formattedDateString = dateFormatterHeader.format(formattedDate);
	          %>
				          
				       <a href="" data-bs-toggle="modal"
						data-bs-target="#announcementModal<%=count%>">
				        <!-- <li> <a href="#" onclick="return false;" style="cursor:default;"> -->
				        <p class="card-text text-dark fw-bold"><%=announcement.getSubject()%></p>
				       
							<p class="card-text text-dark "><%=announcemntBrief %></p>

							<p class="card-text text-dark "><%=formattedDateString%>
								by <span class="card-text text-dark "><%=announcement.getCategory()%></span>
							</p></a>
	                   <hr>
	          <%
	          
	          } %>
        
          
        </ul>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" id="archive">View Archive</button>
      </div>
    </div>
  </div>
</div>

<%}%>


<%if(noOfAnnouncemntsInHeader > 0){ %>
<!-- MODAL FOR INDIVIDUAL ANNOUNCEMENTS-->
<%
        	 int count = 0;
	          for(ExamAnnouncementBean announcement : announcementsInHeader){
	        	  count++;
	        	  Date formattedDate = formatterHeader.parse(announcement.getStartDate());
	  			  String formattedDateString = dateFormatterHeader.format(formattedDate);
	          %>
	          
			<div class="modal" id="announcementModal<%=count %>" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
				  <div class="modal-dialog">
				    <div class="modal-content">
				      <div class="modal-header">
				        <h1 class="modal-title fs-5" id="exampleModalLabel">ANNOUNCEMENTS</h1>
				        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
				      </div>
						<div class="modal-body">
			      
				          			
				          			 <h6><%=announcement.getSubject() %></h6>
							        <p><%=announcement.getDescription() %></p>
							         <%if(announcement.getAttachment1() != null){ %>
								        <a target="_blank" href="<spring:eval expression="@propertyConfigurer.getProperty('CONTENT_PREVIEW_PATH')" /><%=announcement.getAttachment1()%>"><%=announcement.getAttachmentFile1Name() %></a><br/>
								        <%} %>
								         <%if(announcement.getAttachment2() != null){ %>
								        <a target="_blank" href="<spring:eval expression="@propertyConfigurer.getProperty('CONTENT_PREVIEW_PATH')" /><%=announcement.getAttachment2()%>"><%=announcement.getAttachmentFile2Name() %></a><br/>
								        <%} %>
								         <%if(announcement.getAttachment3() != null){ %>
								        <a target="_blank" href="<spring:eval expression="@propertyConfigurer.getProperty('CONTENT_PREVIEW_PATH')" /><%=announcement.getAttachment3()%>"><%=announcement.getAttachmentFile3Name() %></a><br/>
								        <%} %>
							        <h4 class="small"><%=formattedDateString%> <span>by</span><a href="#"> <%=announcement.getCategory() %></a></h4>
				         
				          
			       
			      </div>
			      <div class="modal-footer">
			        <button type="button" class="btn btn-default" data-bs-dismiss="modal">DONE</button>
			      </div>
			    </div>
			  </div>
			</div>
 		<% }//End of For loop %>
	          
<%}%>
 --%>
<input type="hidden" id="checkTextToSpeech" value="<%= lead.getTextToSpeech() %>">
<input type="hidden" id="checkHighContrast" value="<%= lead.getHighContrast() %>">
 <script>

window.addEventListener('load', function() {
	loadCount();
	$(".notification-link").click(function(){
	  loadAnnouncements();
	});
		
	});


function loadCount(){
	  var data = {
				 sapid: "${sessionScope.userId}"
				  };
	  $.ajax({
	      url: '/announcement/m/getUnreadAnnouncementCount',
	      type: 'POST',
	      contentType: 'application/json',
	      data: JSON.stringify(data),
	      success: function(response) {
	    	  var count = response;
	    	  var announcementCount = '<div class="notification-count">'+count+'</div>';
	           $('#announcementCounter').html(announcementCount);
	         },
		  error : function(xhr, status, error) {
			  var announcementCount = '<div class="notification-count">0</div>';
	           $('#announcementCounter').html(announcementCount);
		}
	  }); 
	
}


	function isReadStatusInHeader(id, userId) {
		showindividualAnnouncementModal();
		var modal = document.getElementById("announcementModal");
		var data = {
			announcementId : id,
			userId : userId
		};
		var data1 = {
			sapid : userId
		};
		$.ajax({
				url : '/announcement/m/changeAnnouncementStatus',
				method : 'POST',
				contentType : 'application/json',
				data : JSON.stringify(data),
				success : function(response1) {

						$.ajax({
									url : '/announcement/m/getUnreadAnnouncementCount',
									method : 'POST',
									data : JSON.stringify(data1),
									contentType : 'application/json',
									success : function(response2) {
										var count = response2;
										var updatedCounter = '<div class="notification-count">'+count+'</div>';
										$('#announcementCounter').html(updatedCounter);							
									},
									error : function(xhr, status, error){
										 console.error("Error while changing the status" ,error);
									}
									
								});
					},
					error : function(xhr, status, error) {
						console.error("Error while updating  the status" ,error);

					}
				});
	}
</script> 

<script>

	
function loadAnnouncements() {
	  var data = {
			 sapid: "${sessionScope.userId}"
			  };
  $.ajax({
      url: '/announcement/m/getAllActiveAnnouncementsByStudentMapping',
      type: 'POST',
      contentType: 'application/json',
      data: JSON.stringify(data),
      beforeSend: function() {
         	$('#loader').removeClass('d-none');
  			$('#loader').addClass('d-block');
   	  },

      success: function(response) {
      var activeStatus = response.isActiveAnnouncement;
if(activeStatus){
    	  $('#loader').addClass('d-none');
		  $('#loader').removeClass('d-block');
          var announcements = response.announcements;
          var announcementCount = typeof response.announcementCounter === "undefined" ? 0 : response.announcementCounter;
          var optionsList = '<div class="notification-count">'+announcementCount+'</div>';
          $('#announcementCounter').html(optionsList);
          var count=0;
          try {
        	  var announcement='';
  		  	  var individualAnnouncement ='';
              for (let i = 0; i < announcements.length; i++) {
              	singleannouncement = response.announcements[i];
	           	var createdDate = new Date(singleannouncement.createdDate);
				var options = { day: '2-digit', month: 'short', year: 'numeric' };
				var formattedDate = createdDate.toLocaleDateString('en-US', options);
                count++;   
               	var breifAnnouncement = announcements[i].description;
	                  function stripHtmlTags(breifAnnouncement) {
	                	  return breifAnnouncement.replace(/<[^>]*>/g, '');
	                	}
              	var announcementWithTags = announcements[i].description;
              	var announcementWithoutTags  =stripHtmlTags(announcementWithTags);
              	var announcemntBrief = "";        	
              	if (announcementWithoutTags.length > 150) {
              	  announcemntBrief = announcementWithoutTags.substring(0, 149) + "...";
              	} else {
              	  announcemntBrief = announcementWithoutTags;
              	}
					if(announcements[i].readStatus){
		                announcement 		+='<a class="bg-light" href="#" data-bs-toggle="modal" data-bs-dismiss="modal"'
		    								+'onClick="isReadStatusInHeader('+announcements[i].id+',${sessionScope.userId})"'
		    								+'id="isRead" data-bs-target="#announcementModalInheader'+announcements[i].id+'">';           								
		    			announcement		+='<div id="announcementId-'+announcements[i].id+'">'
		    								+'<p class="card-text text-dark fw-bolder">'+announcements[i].subject+'</p>'
		    								+'</div>';
		    			announcement		+='<p class="card-text text-dark fw-bolder">'+announcemntBrief+'</p>';
		    		    announcement		+='<p class="card-text text-dark fw-bolder">'+announcements[i].startDate+''
		    								+'by <span class="card-text text-dark fw-bolder">'+announcements[i].category+'</span>'
		    								+'</p>'
		    								+'</a>';
		    			announcement   		+='<hr>';
		              						}

		              					    if(!announcements[i].readStatus){
		   				announcement		+='<span style="background-color: #f6f6f6;">'   
		      						 		+'<a class="bg-light" href="#" data-bs-toggle="modal" data-bs-dismiss="modal"'
		       								+'onClick="isReadStatusInHeader('+announcements[i].id+',${sessionScope.userId})"'
		       								+'id="isRead" data-bs-target="#announcementModalInheader'+announcements[i].id+'">';   
		     		    announcement		+='<div id="announcementdetails">'
		      								+'<p class="card-text text-secondary">'+announcements[i].subject+'</p>'
		      								+'</div>';
		     		    announcement	    +='<p class="card-text  text-secondary">'+announcemntBrief+'</p>';
		   	    	    announcement		+='<p class="card-text  text-secondary">'+announcements[i].startDate+''
											+' by <span class="card-text text-secondary">'+announcements[i].category+'</span>'
											+'</p>'
											+'</a>'
											+'</span>';       				              		
		              	announcement		+='<hr>';
		              					    }
		              					      
		   			individualAnnouncement  +='<div class="modal fade announcement closButton" id="announcementModalInheader'+singleannouncement.id+'"  aria-labelledby="seprateModel">' 	
							   				+'<div class="modal-dialog">'
											+'<div class="modal-content">'
											+'<div class="modal-header">'
											+'<h1 class="modal-title fs-5" id="seprateModel">ANNOUNCEMENTS</h1>'
											+'<button type="button" class="btn-close" data-bs-dismiss="modal"aria-label="Close"></button>'
											+'</div>';
							
					individualAnnouncement  +='<div class="modal-body">'
											+'<h6>'+singleannouncement.subject+'</h6>'
											+'<p>'+singleannouncement.description+'</p>';	
						
											if(singleannouncement.attachment1 != null){
				    individualAnnouncement	+='<a target="_blank" href="https://announcementfiles.s3.ap-south-1.amazonaws.com/"/>'+singleannouncement.attachment1+singleannouncement.attachmentFile1Name+'</a><br/>';
											}
											if(singleannouncement.attachment2 != null){
					individualAnnouncement	+='<a target="_blank" href="https://announcementfiles.s3.ap-south-1.amazonaws.com/"/>'+singleannouncement.attachment2+singleannouncement.attachmentFile2Name+'</a><br/>';
											}
											if(singleannouncement.attachment3 != null){
				    individualAnnouncement	+='<a target="_blank" href="https://announcementfiles.s3.ap-south-1.amazonaws.com/"/>'+singleannouncement.attachment3+singleannouncement.attachmentFile3Name+'</a><br/>';
											}
																											
				    individualAnnouncement	+='<h4 class="small">' + formattedDate + '<span> by </span><a href="#">'+''+ singleannouncement.category + '</a></h4>';
			
					individualAnnouncement	+='</div>';
						
					individualAnnouncement	+='<div class="modal-footer">'
											+'<button data-bs-dismiss="modal" type="button" class="btn btn-default">DONE</button>'
											+'</div>'
											+'</div>'
											+'</div>'
											+'</div>';	

								}
						  } 
		  
					catch (err) {
							console.log(err);
						}
					
						$('#announcementbody').html(announcement);
						$('#announcementInsideModal').html(individualAnnouncement);
						}

					else{
						var msg = response.announcementMsg;
						$('#loader').addClass('d-none');
						$('#loader').removeClass('d-block');
						var noActiveAnnouncement ='<div id="announcementbody"><p class="card-text text-dark fw-bolder text-center">'+msg+'</p></div>';
						$('#announcementbody').html(noActiveAnnouncement);
							}
					},
					error : function(xhr, status, error) {
						$('#loader').addClass('d-none');
						$('#loader').removeClass('d-block');
						var announcementError ='<div id="announcementbody"><p class="card-text text-dark fw-bolder text-center">Unable to load announcements!</p></div>';
						$('#announcementbody').html(announcementError);
						console.error('Unable to load announcements!', error);
					}
				});
	         }

</script>
<script>

function showindividualAnnouncementModal(){
	var individualAnnouncementModal = new bootstrap.Modal(document.getElementById('announcementInsideModal'))
	individualAnnouncementModal.show();
	$('.announcement').removeClass('fade');
}
</script>

<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery-1.11.3.min.js"></script>
<script type="text/javascript">
    document.getElementById("archive").onclick = function () {
        location.href = "/announcement/student/getAllStudentAnnouncements";
    };
</script>
<!-- Card 10453 disable Portal Experience for Leads and redirect directly to freeCourses page
<script>
$(".header-switch").change(function() {    
	window.location = "/studentportal/student/setPerspectiveForLeads?perspective=toggle";
}); 
</script> -->
<script src="${pageContext.request.contextPath}/assets/js/header.js"></script>"
