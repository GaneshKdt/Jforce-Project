
<%@page import="com.nmims.controllers.BaseController"%>
<%@page import="com.nmims.helpers.PersonStudentPortalBean"%>
<%@page import="com.nmims.beans.StudentStudentPortalBean"%>
<%@page import="com.nmims.beans.AnnouncementStudentPortalBean"%>
<%@page import="com.nmims.beans.UserAuthorizationStudentPortalBean"%>
<%@page import="com.nmims.beans.ELearnResourcesStudentPortalBean"%>
<%@page import="com.nmims.dto.LoginSSODto"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="com.nmims.helpers.AESencrp"%>
<%@page import="com.google.gson.Gson" %>
<%@page import="java.util.ArrayList" %>
<%@page import="java.util.HashMap" %>
<%@page import="java.util.Map" %>
<%@page import="java.util.List" %>
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
	
	LoginSSODto detail = new LoginSSODto();
	try{
		detail.setUserId((String)session.getAttribute("userId"));
		detail.setStudent((StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal"));
		detail.setAnnouncements((ArrayList<AnnouncementStudentPortalBean>)request.getSession().getAttribute("announcementsPortal"));
		detail.setUserBean((UserAuthorizationStudentPortalBean)request.getSession().getAttribute("userAuthorization_studentportal"));
		detail.setPersonDetails((PersonStudentPortalBean)request.getSession().getAttribute("user_studentportal"));
		detail.setApplicableSubjects((HashMap<String, String>)request.getSession().getAttribute("programSemSubjectIdWithSubjects_studentportal"));
		detail.setRegData((StudentStudentPortalBean)request.getSession().getAttribute("studentRecentReg_studentportal"));
		detail.setHarvard((ELearnResourcesStudentPortalBean)request.getSession().getAttribute("harvard_details"));
		detail.setStukent((ELearnResourcesStudentPortalBean)request.getSession().getAttribute("stukent_details"));
		detail.setValidityExpired((String)request.getSession().getAttribute("validityExpired"));
		detail.setIsLoginAsLead((String)request.getSession().getAttribute("isLoginAsLead"));
		if(detail.getStudent() != null){
			detail.setCurrentOrder((double)request.getSession().getAttribute("current_order"));
			detail.setMaxOrderWhereContentLive((double)request.getSession().getAttribute("acadContentLiveOrder"));
			detail.setRegOrder((double)request.getSession().getAttribute("reg_order"));
			detail.setAcadSessionLiveOrder((double)request.getSession().getAttribute("acadSessionLiveOrder"));
			detail.setCurrentSemPSSId((List<Integer>)request.getSession().getAttribute("currentSemPSSId_studentportal"));
			detail.setEarlyAccess((String)request.getSession().getAttribute("earlyAccess"));
			detail.setLiveSessionPssIdAccess((List<Integer>)request.getSession().getAttribute("liveSessionPssIdAccess_studentportal"));
			detail.setFeatureViseAccess((Map<String, Boolean>)request.getSession().getAttribute("CSFeatureAccess"));
			detail.setConsumerProgramStructureHasCSAccess((boolean)request.getSession().getAttribute("consumerProgramStructureHasCSAccess"));
			detail.setCourseraAccess((boolean)request.getSession().getAttribute("courseraAccess"));
			detail.setValidityEndDate((String)request.getSession().getAttribute("validityEndDate"));
			detail.setSubjectCodeId((List<Integer>)request.getSession().getAttribute("subjectCodeId_studentportal"));
			}else{
	            detail.setCsAdmin((Map<String, Boolean>)request.getSession().getAttribute("csAdmin"));
	    }
	}catch(Exception e) {
		e.printStackTrace();
	}
	
	Gson g = new Gson();  
	
	String str = g.toJson(detail);
	
	if(controller.checkLead(request, response))
		encryptedSapId = URLEncoder.encode(AESencrp.encrypt((String)session.getAttribute("emailId"))); 
	else
		encryptedSapId = URLEncoder.encode(AESencrp.encrypt((String)session.getAttribute("userId"))); 
	
	//String examAppSSOUrl = (String)pageContext.getAttribute("server_path") + "exam/loginforSSO?uid="+encryptedSapId;
	//String acadsAppSSOUrl = (String)pageContext.getAttribute("server_path") + "acads/loginforSSO?uid="+encryptedSapId;
	String ltiAppSSOUrl = (String)pageContext.getAttribute("server_path") + "ltidemo/loginforSSO?uid="+encryptedSapId;
	String csAppSSOUrl = (String)pageContext.getAttribute("server_path") + "careerservices/loginforSSO?uid="+encryptedSapId;
	String defaultAnnouncementUrl = (String)pageContext.getAttribute("server_path") + "announcement/loginforSSO_new?uid="+encryptedSapId;
	String searchAppUrl = "http://localhost:5454/searchapp/loginforSSO?uid="+encryptedSapId;
%>

</script>


<div id="examApp"></div>
<div id="acadsApp"></div>
<div id="ltiApp"></div>
<div id="csApp"></div>
<div id="contentApp"></div>
<div id="announcementApp"></div>
<div id="searchApp"></div>

<script>
	var examAppLoaded = false;
	var acadsAppLoaded = false;
	var ltiAppLoaded = false;
	var csAppLoaded = false;
	var searchApp = false;

	$(document).ready(function(){
		$('body').prepend(`
			<div class="fullPageLoading">
				<div class="modal-backdrop fade in"></div>
				<div id="loader-container">
			 		<div id="loader"></div>
					<div> Please wait... </div>
				</div>
			</div>`)
	<%-- 	$( "#examApp" ).load('<%=examAppSSOUrl%>', function() {
			examAppLoaded = true
			checkIfLoadingFinished();
		});
		$( "#acadsApp" ).load('<%=acadsAppSSOUrl%>', function() {
			acadsAppLoaded = true
			checkIfLoadingFinished();
		}); --%>
		$( "#ltiApp" ).load('<%=ltiAppSSOUrl%>', function() {
			ltiAppLoaded = true
			// checkIfLoadingFinished();
		});

		/* $( "#contentApp" ).load('${defaultContentUrl}', function() {
			
		}); */  
		
		ssoProjectLoading('${defaultAnnouncementUrl}');
		if(ssoProjectLoading('${defaultAcadsUrl}')){
			acadsAppLoaded = true;
		}
		ssoProjectLoading('${defaultknowyourpolicyUrl}');
		if(ssoProjectLoading('${defaultExamUrl}')){
			examAppLoaded = true;
		}
		ssoProjectLoading('${defaultalmashinesyUrl}');
		//ssoKnowYourPolicyLoading();
	//	ssoLoadingAcadsWithAjax();
     //   ssoLoadingExamWithAjax();
        //ssoLoadingLtidemoWithAjax();
        ssoJobSearchLoading();
        ssoSearchAppLoading();
        //ssoAlmashinesLoading();
		ssoIALoading();
		ssoLoadingReportingtoolWithAjax();
		ssoServiceRequestLoading();
        ssoForumLoading();
        checkIfLoadingFinished();
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

	function ssoAnnouncementLoading()
    {
    	var JsonStr = <%=str%>;
            var search = {}
            search["userId"] = JsonStr.userId;
            search["student"] = JsonStr.student ;
            search["personDetails"] = JsonStr.personDetails ;
            search["userBean"] = JsonStr.userBean ;
            search["applicableSubjects"] = JsonStr.applicableSubjects ;
            search["harvard"] = JsonStr.harvard ;
            search["stukent"] = JsonStr.stukent ;
            search["announcements"] = JsonStr.announcements;
            search["validityExpired"] = JsonStr.validityExpired;
            search["courseraAccess"]= JsonStr.courseraAccess;

    	$.ajax({
		   		type : "POST",
		   		url : "/announcement/loginforSSO", 
		   	 	contentType : 'application/json',
		        data: JSON.stringify(search),
		  		success : function(data) {
			  
			   		},
		 		error : function(e) {
		   		}
		  });
    }

	function ssoKnowYourPolicyLoading()
    {
    	var JsonStr = <%=str%>;
            var search = {}
            search["userId"] = JsonStr.userId;
            search["student"] = JsonStr.student ;
            search["personDetails"] = JsonStr.personDetails ;
            search["userBean"] = JsonStr.userBean ;
            search["applicableSubjects"] = JsonStr.applicableSubjects ;
            search["harvard"] = JsonStr.harvard ;
            search["stukent"] = JsonStr.stukent ;
            search["announcements"] = JsonStr.announcements;
            search["validityExpired"] = JsonStr.validityExpired;
            search["courseraAccess"]= JsonStr.courseraAccess;
            
    	$.ajax({
		   		type : "POST",
		   		url : "/knowyourpolicy/loginforSSO", 
		   	 	contentType : 'application/json',
		        data: JSON.stringify(search),
		  		success : function(data) {
			  
			   		},
		 		error : function(e) {
		   		}
		  });
    }

 	function ssoLoadingAcadsWithAjax()
    {
	    
    	var JsonStr = <%=str%>;
    	var userId_encrypted = '<%=encryptedSapId%>';
            var search = {}
            search["userId"] = JsonStr.encryptedUserId;
            search["student"] = JsonStr.student ;
            search["personDetails"] = JsonStr.personDetails ;
            search["userBean"] = JsonStr.userBean ;
            search["applicableSubjects"] = JsonStr.applicableSubjects ;
            search["harvard"] = JsonStr.harvard ;
            search["stukent"] = JsonStr.stukent ;
            search["announcements"] = JsonStr.announcements;
            search["validityExpired"] = JsonStr.validityExpired;
            search["regOrder"] = JsonStr.regOrder;
            search["maxOrderWhereContentLive"] = JsonStr.maxOrderWhereContentLive;
            search["currentOrder"] = JsonStr.currentOrder;
            search["currentSemPSSId"] = JsonStr.currentSemPSSId;
            search["isLoginAsLead"] = JsonStr.isLoginAsLead;
            search["liveSessionPssIdAccess"] = JsonStr.liveSessionPssIdAccess;
            search["featureViseAccess"] = JsonStr.featureViseAccess;
            search["regData"] = JsonStr.regData;
            search["earlyAccess"] = JsonStr.earlyAccess;
            search["consumerProgramStructureHasCSAccess"] = JsonStr.consumerProgramStructureHasCSAccess;
            search["acadSessionLiveOrder"] = JsonStr.acadSessionLiveOrder;
            search["csAdmin"] = JsonStr.csAdmin;
            search["courseraAccess"]= JsonStr.courseraAccess;
            
    	$.ajax({
		   		type : "POST",
		   		url : "/acads/loginforSSO_new?uid="+userId_encrypted, 
		   	 	contentType : 'application/json',
		        data: JSON.stringify(search),
		  		success : function(data) {
		  			acadsAppLoaded = true
					checkIfLoadingFinished();
			  	
			   		},
		 		error : function(e) {
			 		
		   		}
		  });
    } 

 	function ssoLoadingExamWithAjax()
    {
        
        var JsonStr = <%=str%>;
        var userId_encrypted = '<%=encryptedSapId%>';
            var search = {}
            search["userId"] = JsonStr.encryptedUserId;
            search["student"] = JsonStr.student ;
            search["personDetails"] = JsonStr.personDetails ;
            search["userBean"] = JsonStr.userBean ;
            search["announcements"] = JsonStr.announcements;
            search["validityExpired"] = JsonStr.validityExpired;
            search["isLoginAsLead"] = JsonStr.isLoginAsLead;
            search["featureViseAccess"] = JsonStr.featureViseAccess;
            search["earlyAccess"] = JsonStr.earlyAccess;
            search["consumerProgramStructureHasCSAccess"] = JsonStr.consumerProgramStructureHasCSAccess;
            search["csAdmin"] = JsonStr.csAdmin
            search["courseraAccess"]= JsonStr.courseraAccess;

        $.ajax({
                type : "POST",
                url : "/exam/loginforSSO_new?uid="+userId_encrypted, 
                contentType : 'application/json',
                data: JSON.stringify(search),
                success : function(data) {
                    examAppLoaded = true
                    checkIfLoadingFinished();
               
                    },
                error : function(e) {
                   
                }
          });
    } 

    function ssoLoadingLtidemoWithAjax()
    {
        var JsonStr = <%=str%>;
        var userId_encrypted = '<%=encryptedSapId%>';
            var search = {}
            search["userId"] = JsonStr.encryptedUserId;
            search["student"] = JsonStr.student ;
            search["personDetails"] = JsonStr.personDetails ;
            search["earlyAccess"] = JsonStr.earlyAccess;
            search["courseraAccess"]= JsonStr.courseraAccess;
			search["subjectCodeId"] = JsonStr.subjectCodeId;
        $.ajax({
                type : "POST",
                url : "/ltidemo/loginforSSO_new?uid="+userId_encrypted, 
                contentType : 'application/json',
                data: JSON.stringify(search),
                success : function(data) {
                    ltiAppLoaded = true
                    },
                error : function(e) {}
          });
    } 

    function ssoJobSearchLoading(){
    	var JsonStr = <%=str%>;
        var search = {}
        search["userId"] = JsonStr.userId;
        search["student"] = JsonStr.student ;
        search["personDetails"] = JsonStr.personDetails ;
        search["userBean"] = JsonStr.userBean ;
        search["applicableSubjects"] = JsonStr.applicableSubjects ;
        search["harvard"] = JsonStr.harvard ;
        search["stukent"] = JsonStr.stukent ;
        search["announcements"] = JsonStr.announcements;
        search["validityExpired"] = JsonStr.validityExpired;
        search["isLoginAsLead"] = JsonStr.isLoginAsLead;
        search["featureViseAccess"] = JsonStr.featureViseAccess;
        search["consumerProgramStructureHasCSAccess"] = JsonStr.consumerProgramStructureHasCSAccess;
        search["csAdmin"] = JsonStr.csAdmin;
        search["courseraAccess"]= JsonStr.courseraAccess;
        search["validityEndDate"]= JsonStr.validityEndDate;
        search["subjectCodeId"] = JsonStr.subjectCodeId;
    	$.ajax({
		   	type : "POST",
		   	url : "/jobsearch/loginforSSO", 
		   	contentType : 'application/json',
		   	data: JSON.stringify(search),
		   	success : function(data) {
			  
			},
		 	error : function(e) {
			 	
		   	}
		});
    }

function ssoAlmashinesLoading(){
    	var JsonStr = <%=str%>;
    	var userId_encrypted = '<%=encryptedSapId%>';
            var search = {}
            search["userId"] = JsonStr.userId;
            search["student"] = JsonStr.student ;
            search["personDetails"] = JsonStr.personDetails ;
            search["userBean"] = JsonStr.userBean ;
            search["applicableSubjects"] = JsonStr.applicableSubjects ;
            search["harvard"] = JsonStr.harvard ;
            search["stukent"] = JsonStr.stukent ;
            search["announcements"] = JsonStr.announcements;
            search["validityExpired"] = JsonStr.validityExpired;
            search["regOrder"] = JsonStr.regOrder;
            search["maxOrderWhereContentLive"] = JsonStr.maxOrderWhereContentLive;
            search["currentOrder"] = JsonStr.currentOrder;
            search["currentSemPSSId"] = JsonStr.currentSemPSSId;
            search["isLoginAsLead"] = JsonStr.isLoginAsLead;
            search["liveSessionPssIdAccess"] = JsonStr.liveSessionPssIdAccess;
            search["featureViseAccess"] = JsonStr.featureViseAccess;
            search["regData"] = JsonStr.regData;
            search["earlyAccess"] = JsonStr.earlyAccess;
            search["consumerProgramStructureHasCSAccess"] = JsonStr.consumerProgramStructureHasCSAccess;
            search["acadSessionLiveOrder"] = JsonStr.acadSessionLiveOrder;
            search["csAdmin"] = JsonStr.csAdmin;
            search["courseraAccess"]= JsonStr.courseraAccess;
            search["subjectCodeId"] = JsonStr.subjectCodeId;
    	$.ajax({
		   	type : "POST",
		   	url : "/almashines/loginforSSO", 
		   	contentType : 'application/json',
		   	data: JSON.stringify(search),
		   	success : function(data) {
			  
			},
		 	error : function(e) {
			 	
		   	}
		});
    }

	function ssoIALoading() {
		var JsonStr = <%=str%>;
		var search = {};
		search["userId"] = JsonStr.userId;
		search["student"] = JsonStr.student ;
		search["personDetails"] = JsonStr.personDetails ;
		search["userBean"] = JsonStr.userBean ;
		search["applicableSubjects"] = JsonStr.applicableSubjects ;
		search["harvard"] = JsonStr.harvard ;
		search["stukent"] = JsonStr.stukent ;
		search["announcements"] = JsonStr.announcements;
		search["validityExpired"] = JsonStr.validityExpired;
		search["courseraAccess"]= JsonStr.courseraAccess;
		search["subjectCodeId"] = JsonStr.subjectCodeId;
		$.ajax({
			type : "POST",
			url : "/internal-assessment/loginforSSO",
			contentType : 'application/json',
			data: JSON.stringify(search),
			success : function(data) {},
			error : function(e) {}
		});
	}

	function ssoLoadingReportingtoolWithAjax(){
    	var JsonStr = <%=str%>;
        var search = {}
        search["personDetails"] = JsonStr.personDetails;
        search["userBean"] = JsonStr.userBean;
    	$.ajax({
		   	type : "POST",
		   	url : "/reportingtool/loginforSSO", 
		   	contentType : 'application/json',
		   	data: JSON.stringify(search),
		   	success : function(data) {
			  
			},
		 	error : function(e) {
			 	
		   	}
		});
    }
	function ssoServiceRequestLoading(){
    	var JsonStr = <%=str%>;
		console.log('SSO JSON'  + JsonStr);
        var search = {}
        search["userId"] = JsonStr.userId;
        search["student"] = JsonStr.student ;
        search["personDetails"] = JsonStr.personDetails ;
        search["userBean"] = JsonStr.userBean ;
        search["applicableSubjects"] = JsonStr.applicableSubjects ;
        search["harvard"] = JsonStr.harvard ;
        search["stukent"] = JsonStr.stukent ;
        search["announcements"] = JsonStr.announcements;
        search["validityExpired"] = JsonStr.validityExpired;
        search["csAdmin"] = JsonStr.csAdmin
        search["subjectCodeId"] = JsonStr.subjectCodeId;
    	$.ajax({
		   	type : "POST",
		   	url : "/servicerequest/loginforSSO", 
		   	contentType : 'application/json',
		   	data: JSON.stringify(search),
		   	success : function(data) {
			  
			},
		 	error : function(e) {
			 	
		   	}
		});
    }

    function ssoForumLoading(){
    	var JsonStr = <%=str%>;
        var search = {}
        search["userId"] = JsonStr.userId;
        search["student"] = JsonStr.student ;
        search["personDetails"] = JsonStr.personDetails ;
        search["userBean"] = JsonStr.userBean ;
        search["applicableSubjects"] = JsonStr.applicableSubjects ;
        search["harvard"] = JsonStr.harvard ;
        search["stukent"] = JsonStr.stukent ;
        search["announcements"] = JsonStr.announcements;
        search["validityExpired"] = JsonStr.validityExpired;
        search["csAdmin"] = JsonStr.csAdmin
        search["subjectCodeId"] = JsonStr.subjectCodeId;
    	$.ajax({
		   	type : "POST",
		   	url : "/forum/loginforSSO", 
		   	contentType : 'application/json',
		   	data: JSON.stringify(search),
		   	success : function(data) {
			  
			},
		 	error : function(e) {
			 	
		   	}
		});
    }
    
	function ssoProjectLoading(loadUrl)
    {
    	$.ajax({
		   		type : "GET",
		   		url : loadUrl, 
		   	 	contentType : 'application/json',
		  		success : function(data) {
			  
			   		},
		 		error : function(e) {
		   		}
		  });
		  return true;
    }


    function ssoSearchAppLoading(){
    	var JsonStr = <%=str%>;
        var search = {}
        search["userId"] = JsonStr.userId;
    	$.ajax({
		   	type : "POST",
		   	url : "/searchapp/loginforSSO?uid="+'<%=encryptedSapId%>', 
		   	contentType : 'application/json',
		   	data: JSON.stringify(search),
		   	success : function(data) {
			  
			},
		 	error : function(e) {
			 	
		   	}
		});
    }
    
</script>