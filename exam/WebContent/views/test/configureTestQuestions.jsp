
<!DOCTYPE html>
<html lang="en">
	
<%@page import="com.nmims.beans.Person"%>
  
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
  
    <jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Configure Online Test Questions" name="title"/>
    </jsp:include>
    
 
<link
	href="https://gitcdn.github.io/bootstrap-toggle/2.2.2/css/bootstrap-toggle.min.css"
	rel="stylesheet">
<script
  src="https://code.jquery.com/jquery-3.3.1.js"
  integrity="sha256-2Kok7MbOyxpgUVvAk/HJ2jigOSYS2auK4Pfzbm7uH60="
  crossorigin="anonymous"></script>
<script
	src="https://gitcdn.github.io/bootstrap-toggle/2.2.2/js/bootstrap-toggle.min.js"></script>

    <style>
    /* The snackbar - position it at the bottom and in the middle of the screen */
#snackbar {
	visibility: hidden;
	/* Hidden by default. Visible on click */
	min-width: 250px;
	/* Set a default minimum width */
	margin-left: -125px;
	/* Divide value of min-width by 2 */
	background-color: #333;
	/* Black background color */
	color: #fff;
	/* White text color */
	text-align: center;
	/* Centered text */
	border-radius: 2px;
	/* Rounded borders */
	padding: 16px;
	/* Padding */
	position: fixed;
	/* Sit on top of the screen */
	z-index: 1;
	/* Add a z-index if needed */
	left: 50%;
	/* Center the snackbar */
	bottom: 30px;
	/* 30px from the bottom */
}

/* Show the snackbar when clicking on a button (class added with JavaScript) */
#snackbar.show {
	visibility: visible;
	/* Show the snackbar */
	/* Add animation: Take 0.5 seconds to fade in and out the snackbar. 
However, delay the fade out process for 2.5 seconds */
	-webkit-animation: fadein 0.5s, fadeout 0.5s 2.5s;
	animation: fadein 0.5s, fadeout 0.5s 2.5s;
}

/* Animations to fade the snackbar in and out */
@
-webkit-keyframes fadein {from { bottom:0;
	opacity: 0;
}

to {
	bottom: 30px;
	opacity: 1;
}

}
@
keyframes fadein {from { bottom:0;
	opacity: 0;
}

to {
	bottom: 30px;
	opacity: 1;
}

}
@
-webkit-keyframes fadeout {from { bottom:30px;
	opacity: 1;
}

to {
	bottom: 0;
	opacity: 0;
}

}
@
keyframes fadeout {from { bottom:30px;
	opacity: 1;
}

to {
	bottom: 0;
	opacity: 0;
}

}

.redLeftBorder{
	padding-left : 10px;
	border-left : 5px solid #C72033;
	
}
.greenLeftBorder{
	padding-left : 10px;
	border-left : 5px solid #49a54e;
	}
    
    
    </style>
    
    <body>
    
    	<%@ include file="../adminCommon/header.jsp" %>
        <div class="sz-main-content-wrapper">
        
        <!-- Custom breadcrumbs as requirement is diff. Start -->
			<div class="sz-breadcrumb-wrapper">
			    <div class="container-fluid">
			        <ul class="sz-breadcrumbs">
			        		<li><a href="/exam/">Exam</a></li>
			        		<li><a href="/exam/viewAllTests">Tests</a></li>
					<li><a href="/exam/viewTestDetails?id=${test.id}">Test
							Details</a></li>
					<li><a href="#">Configure Test Questions
							</a></li>
			        	
			        </ul>
			        <ul class="sz-social-icons">
			            <li><a href="https://www.facebook.com/NMIMSSCE" class="icon-facebook" target="_blank"></a></li>
			            <li><a href="https://twitter.com/NMIMS_SCE" class="icon-twitter" target="_blank"></a></li>
			            <!-- <li><a href="https://plus.google.com/u/0/116325782206816676798/posts" class="icon-google-plus" target="_blank"></a></li> -->
						
			        </ul>
			    </div>
			</div>
			<!-- Custom breadcrumbs as requirement is diff. End -->
        	
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="../adminCommon/left-sidebar.jsp">
								<jsp:param value="" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="../adminCommon/adminInfoBar.jsp" %>
              						<div class="sz-content">
								
											<h2 class="red text-capitalize">Configure Test Questions </h2>
											<div class="clearfix"></div>
							<div class="panel-content-wrapper" style="min-height:450px;">
								<%@ include file="../adminCommon/messages.jsp" %>
							
							<!-- Code For Page Goes in Here Start -->
							
												<div class="table-responsive">
													<table class="table table-hover table-striped" style="font-size: 12px">
														<thead>
															<tr>
																<th>Sr. No.</th>
																<th>Question Type</th>
																<th>Maximum Questions</th>
																<th>Action </th>
															</tr>
														</thead>
														<tbody>

															<c:forEach var="typeId" items="${applicableTypes}"	varStatus="status">
																<tr id="row-${typeId}" class="redLeftBorder">
																	<td><c:out value="${status.count}" /></td>
																	<td><c:out value="${typeIdNBeanMap[typeId].type}" /></td>
																	<%-- <td>
																	
																	<c:choose>
																		<c:when test="${testIdNConfigMap[typeId] == null}">
																			
																		</c:when>
																		<c:otherwise>
																			<select class="form-control" id="minQuestions-${typeId}">
																			      <option value="${testIdNConfigMap[typeId].minNoOfQuestions}" >${testIdNConfigMap[typeId].minNoOfQuestions}</option>
																			         <c:forEach var = "i" begin = "0" end = "${testIdNConfigMap[typeId].noOfQuestions}">
																			         <option value="${i}">${i}</option>
																			      </c:forEach>
																			 </select>
																		</c:otherwise>
																	</c:choose>
																	
																	</td> --%>
																	
																	<td>
																	
																	<c:choose>
																		<c:when test="${testIdNConfigMap[typeId] == null}">
																			
																		</c:when>
																		<c:otherwise>
																			<select class="form-control" id="maxQuestions-${typeId}">
																			      <option value="${testIdNConfigMap[typeId].maxNoOfQuestions}" >${testIdNConfigMap[typeId].maxNoOfQuestions}</option>
																			       <c:forEach var = "i" begin = "0" end = "${testIdNConfigMap[typeId].noOfQuestions}">
																			         <option value="${i}">${i}</option>
																			      </c:forEach>
																			 </select>
																		</c:otherwise>
																	</c:choose>
																	
																	</td>
																	
																	<td>
																		<button class="btn btn-primary" onclick="saveConfig(${typeId})"> Save </button>
																	</td>
																	
																</tr>
															</c:forEach>
														</tbody>
												</table>
											</div>
													
							<!-- Code For Page Goes in Here End -->
							</div>
							
							</div>
              			</div>
    				</div>
			   </div>
		    </div>
		     
		    
		    <div id="snackbar">Some text some message..</div>
	
        <jsp:include page="../adminCommon/footer.jsp"/>
        
		<script type="text/javascript">
		 $(document).ready(function(){
			 var testId = ${test.id};
			 console.log("testId:"+testId);
			
			 window.saveConfig = function saveConfig(typeId){
				 console.log("IN saveConfig got typeId:"+typeId);
				 
				 //var minQId = "#minQuestions-"+typeId;
				 var maxQId = "#maxQuestions-"+typeId;
				 var minNoOfQuestions = $(maxQId).val();
				 var maxNoOfQuestions = $(maxQId).val();
				 console.log("in saveConfig got minNoOfQuestions:"+minNoOfQuestions +" maxNoOfQuestions: "+ maxNoOfQuestions);
				 
				 saveTestQuestionsConfigAjax(typeId, minNoOfQuestions, maxNoOfQuestions);
				 }
			 
			//saveTestQuestionsConfigAjax start
			 function saveTestQuestionsConfigAjax(type, minNoOfQuestions, maxNoOfQuestions){
				 var promiseObj = new Promise(function(resolve, reject){
				console.log("In saveTestQuestionsConfigAjax() ENTERED...");
				var methodReturns = false;
				//ajax to save question reponse start
	   		var body = {
	   			'testId' : testId,
	   			'type' : type,
	   			'minNoOfQuestions' : minNoOfQuestions,
	   			'maxNoOfQuestions' : maxNoOfQuestions
	   		};
	   		console.log(body);
	   		$.ajax({
	   			type : 'POST',
	   			url : '/exam/m/saveTestQuestionsConfig',
	   			data: JSON.stringify(body),
	               contentType: "application/json",
	               dataType : "json",
	               
	   		}).done(function(data) {
					  console.log("iN AJAX SUCCESS");
	             	console.log(data);
					var rowId = "#row-"+type;
					$(rowId).addClass("greenLeftBorder");
	             	
	             	showSnackBar("Config Saved.");
	   			 
	             	methodReturns= true;
	     			console.log("In saveTestQuestionsConfigAjax() EXIT... got methodReturns: "+methodReturns);
	     			resolve(methodReturns);
	   		}).fail(function(xhr) {
	   			console.log("iN AJAX eRROR");
					console.log(result);
					showSnackBar("Failed to save config.");
					console.log("In saveTestQuestionsConfigAjax() EXIT... got methodReturns: "+methodReturns);
				    console.log('error', xhr);
				    reject(methodReturns);
				  });
				//ajax to save question reponse end
				 })
				 return promiseObj;

	   	}
			//saveTestQuestionsConfigAjax end

			 
				function showSnackBar(message) {
				    // Get the snackbar DIV
				    var x = document.getElementById("snackbar");
					console.log("In showSnackBar() got message "+message);
				    x.innerHTML = message;
				    
				    // Add the "show" class to DIV
				    x.className = "show";

				    // After 3 seconds, remove the show class from DIV
				    setTimeout(function(){ x.className = x.className.replace("show", ""); }, 3000);
					console.log("Exiting showSnackBar()");
				}
			 
		 }); //ready ends
		</script>
    </body>    
</html>