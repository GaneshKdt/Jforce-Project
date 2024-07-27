<!DOCTYPE html>
<%@page import="java.util.List"%>
<html lang="en">

<jsp:include page="../common/jscss.jsp">
	<jsp:param value="Ranks" name="title" />
</jsp:include>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="java.util.concurrent.TimeUnit"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="com.nmims.controllers.BaseController"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.net.URLEncoder"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<head>
<style>
	.rankStudents {
		padding:;
	}
	
	.rankStudents .rankStudent-image {
		height: 2.7em;
		width: 2.7em;
		border: 2px solid #fff;
		margin: 0em 1.5em 0em 0;
		display: block;
		float: left;
		background-size: cover;
		background-position: center;
		border-radius: 50%;
	}
	
	.rankStudents rankStudent-name {
		display: block;
		margin: 0 1em;
		float: none;
		font: 2em "Aller";
	}
	
	.rankStudents rankStudent-info-list {
		list-style: none;
		padding: 0;
		margin: 0;
		line-height: 30px;
		float: left;
	}
	
	.snackbar{
	    background: #5b5a5a;
	    width: 30%;
	    padding: 20px;
	    margin: 25px;
	    text-align: center;
	    border-radius: 4px;
   	 	position: fixed;
    	z-index: 2;
	    left: 50%;
	    transform: translate(-50%, 0);
	    display: none;
	}

	.loader {
	  border: 5px solid #f3f3f3;
	  border-radius: 50%;
	  border-top: 5px solid #3498db;;
	  width: 20px;
	  height: 20px;
	  -webkit-animation: spin 2s linear infinite; /* Safari */
	  animation: spin 2s linear infinite;
	}
	/* Safari */
	@-webkit-keyframes spin {
	  0% { -webkit-transform: rotate(0deg); }
	  100% { -webkit-transform: rotate(360deg); }
	}
	
	@keyframes spin {
	  0% { transform: rotate(0deg); }
	  100% { transform: rotate(360deg); }
	}
</style>

<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>

</head>
<body>

	<%@ include file="../common/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<jsp:include page="../common/breadcrum.jsp">
			<jsp:param value="Student Zone;Ranks" name="breadcrumItems" />
		</jsp:include>

		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
			<div id="sticky-sidebar">  
				<jsp:include page="../common/left-sidebar.jsp">
					<jsp:param value="Ranks" name="activeMenu" />
				</jsp:include>
  				</div>
				<div class="sz-content-wrapper examsPage">
					<%@ include file="../common/studentInfoBar.jsp"%>
					<div class="sz-content">
						<h2 class="red text-capitalize">Ranks</h2>
						<div class="clearfix"></div>
						
						<div id='snackbar' class='snackbar'>
								<p style="color: white;" id="snackbar_details"></p>
						</div>	
							
						<div class="panel-content-wrapper">
							<%@ include file="../common/messages.jsp"%>
							<ul class="nav nav-tabs">
								<li class="active"><a data-toggle="tab" href="#cycle" style="color:#d2232a;">Cycle
										Wise Rank</a></li>
								<li><a data-toggle="tab" href="#subject" style="color:#d2232a;" >Subject Wise
										Rank</a></li>
							</ul>
							
							<div class="tab-content">
								<div id="cycle" class="tab-pane fade in active">
									<div id="collapseFive"
										class=" collapse in courses-panel-collapse panel-content-wrapper accordion-has-content"
										role="tabpanel">
										<div class="ranks">
											<c:choose>
												<c:when test="${cycleWiseRankConfigList.size() > 0 }">
													<c:forEach var="ranks" items="${cycleWiseRankConfigList}"
														varStatus="status">
														<div class="panel panel-default">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a class="accordion-toggle collapsed" id="${ranks.sem}"
																		data-toggle="collapse" data-parent="#accordion"
																		data-program="${ranks.program}"
																		data-month="${ranks.month}" data-year="${ranks.year}"
																		data-masterKey="${ranks.consumerProgramStructureId}"
																		data-subjectCount="${ranks.subjectsCount}"
																		href="#collapse${ranks.sem}" aria-expanded="false"
																		onClick="checkIfExpand(this)"> Sem : ${ranks.sem} |
																		Cycle : ${ranks.month} ${ranks.year}</a>
																	
																	<a onclick='shareCycleWiseRank(${ranks.sem}, `subject`,`${userId}`,`${ranks.program}`,`${ranks.month }`,${ranks.year},${ranks.consumerProgramStructureId},${ ranks.subjectsCount})' 
																		style='float: right;cursor: pointer;'>
																	Share  <i class="fa-solid fa-share-nodes" aria-hidden="true"></i></a>
																</h4>
															</div>
															<div id="collapse${ranks.sem}"
																class="panel-collapse collapse" aria-expanded="false">
																<div class="panel-body" style="padding: 20px;">
																	<div
																		class="rank${ranks.sem} data-content panel-body rankStudents">
																		<div class="no-data-wrapper no-border">
																			<p class="no-data text-center">Loading...</p>
																		</div>
																	</div>
																</div>
															</div>
														</div>
													</c:forEach>
												</c:when>
												<c:otherwise>
													<div class="no-data-wrapper no-border">
														<p class="no-data text-center">None ranks to show</p>
													</div>
												</c:otherwise>
											</c:choose>
											<div id="rankdescription">
												<p style="font-size: 16px;">How Your Rank is Calculated:</p>
												<ul>
													<li>There is one leaderboard for every semester of your program.</li>
													<li>The leaderboard will display the names and scores of the top 5 ranked 
													students across all subjects in that semester.</li>
													<li>The leaderboard will also display where do you stand (your rank) among your 
													fellow students in the same semester of your program.</li>
													<li>You will see your rank only if you have cleared all of your subjects in the 
													very first attempt as per your semester registration month and year. The ranking 
													will not be displayed for the students that pass a subject in backlog.</li>
													<li>The scores are calculated based on the Assignment and TEE marks obtained in 
													each subject, out of 100.</li>
													<li>Group of students having the same semester registration month/year, program 
													and program structure is considered as a batch for rank calculation.</li>
												</ul>
											</div>
										</div>
									</div>
								</div>
								
								<div id="subject" class="tab-pane fade">
									<div id="collapseFive"
										class="collapse in courses-panel-collapse panel-content-wrapper accordion-has-content"
										role="tabpanel">
										<div class="subjectRanks">
											<c:choose>
												<c:when test="${subjectWiseRankConfigList.size() > 0 }">
													<c:forEach var="ranks" items="${subjectWiseRankConfigList}"
														varStatus="status">														
														<div class="panel panel-default">
															<div class="panel-heading">
																<h4 class="panel-title">
																	<a class="${ranks.sem} accordion-toggle collapsed"
																		id="${ranks.subject.replaceAll('[^a-zA-Z0-9]','')}"
																		data-toggle="collapse" data-parent="#accordion"
																		data-program="${ranks.program}"
																		data-sem="${ranks.sem}" data-month="${ranks.month}"
																		data-year="${ranks.year}"
																		data-subject="${ranks.subject}"
																		data-masterKey="${ranks.consumerProgramStructureId}"
																		href="#collapse${ranks.subject.replaceAll('[^a-zA-Z0-9]','')}"
																		aria-expanded="false"
																		onClick="checkIfExpandForSubjectRank(this)">
																		Sem : ${ranks.sem} | Subject : ${ranks.subject}</a>
																	<a onclick='shareSubjectWiseRank(${ranks.sem},${ranks.subjectcodeMappingId},`${userId}`,`${ranks.program}`,`${ranks.month}`,${ranks.year},`${ranks.subject}`,${ranks.consumerProgramStructureId})'
																		style='float: right;cursor: pointer;'>
																	Share  <i class="fa-solid fa-share-nodes" aria-hidden="true"></i></a>
																</h4>
															</div>
															<div id="collapse${ranks.subject.replaceAll('[^a-zA-Z0-9]','')}"
																class="panel-collapse collapse" aria-expanded="false">
																<div class="panel-body" style="padding: 20px;">
																	<div
																		class="${ranks.subject.replaceAll('[^a-zA-Z0-9]','')} data-content panel-body rankStudents">
																		<div class="no-data-wrapper no-border">
																			<p class="no-data text-center">Loading...</p>
																		</div>
																	</div>
																</div>
															</div>
														</div>
													</c:forEach>
												</c:when>
												<c:otherwise>
													<div class="no-data-wrapper no-border">
														<p class="no-data text-center">None ranks to show</p>
													</div>
												</c:otherwise>
											</c:choose>
											
											<div id="rankdescription">
												<p style="font-size: 16px;">How Your Rank is Calculated:</p>
												<ul>
													<li>There is one leaderboard for every subject in the semester of your program.</li>
													<li>This leaderboard will display the names and scores of the top 5 ranked students.</li>
													<li>The leaderboard will also display where do you stand (your rank) among your fellow 
													students in that same subject of the semester.</li>
													<li>You will see your rank only if you have cleared the subject in the very first attempt 
													as per your semester registration month and year.</li>
													<li>The scores are calculated based on the Assignment and TEE marks obtained in the 
													subject, out of 100.</li>
													<li> Group of students having the same subject, semester registration month/year, program 
													and program structure is considered as a batch for rank calculation.</li>
												</ul>
											</div>	
										</div>										
									</div>
								</div>
							</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<script>				
			let rankData={};
			const urlParams = new URLSearchParams(window.location.search);
			const alreadyLinkedAccount = urlParams.get('alreadyLinkedAccount');

			console.log('alreadyLinkedAccount: '+alreadyLinkedAccount)
			
			if( "true" == alreadyLinkedAccount ){
				
				$('#snackbar_details').html( "Error: Your LinkedIn id is already linked with another account." );
	    		$('#snackbar').fadeIn(10);
	    		$('#snackbar').fadeOut(4000);
				
			}else if ( "false" == alreadyLinkedAccount ){

				$('#snackbar_details').html( "Successfully shared rank on LinkedIn." );
	    		$('#snackbar').fadeIn(10);
	    		$('#snackbar').fadeOut(4000);
				
			}
					
			function checkIfExpand(obj){
			
				if($(obj).attr("aria-expanded")=="false"){
					$(".rank"+$(obj).attr("id")).html('<div class="no-data-wrapper no-border">'+
							'<p class="no-data text-center">'+
								'Loading...'+
							'</p>'+
					'</div>');
					
					rankData={
							'sapid':'${ userId}',
							'sem':$(obj).attr("id"),
							'program':$(obj).attr("data-program"),
							'month':$(obj).attr("data-month"),
							'year':$(obj).attr("data-year"),
							'consumerProgramStructureId':$(obj).attr("data-masterKey"),
							'subjectsCount':$(obj).attr("data-subjectCount")
					}				
					
					$.ajax({
						type:'POST',
						url:'/studentportal/m/cycleWiseRank',
						data:JSON.stringify(rankData),
						contentType: "application/json",
						dataType: "json",
						success:function(response){	

							let rankDiv = "";
							let overallRank = response.overAllCycleWiseRank;
							let persoanlRank = response.cycleWiseStudentsRank;
							if( persoanlRank.length!==0 || overallRank.length!==0 ){
									
								for(i=0; i<overallRank.length; i++){
									
									rankDiv += "<div class='row'><div class='col-sm-4'></div>" +
											"<div class='panel-body col-sm-4' style='border-top:none;border-bottom:4px solid #d2232a;'>" +					
											"<span class='rankStudent-image'" +
												"style='background-image:url("+overallRank[i].studentImage+");'></span>" +
											"<h6 class='rankStudent-name'>"+overallRank[i].name+"</h6>" +
											"<span class='rankStudent-info-list'>";
											
									if(overallRank[i].rank == 'null'){
										rankDiv += "<span class='' style='font-weight:bold;'>Not Applicable Rank</span> | ";
									} else{
										rankDiv += "<span class='' style='font-weight:bold;'>"+overallRank[i].rank.split('/')[0]+" Rank</span> | ";
									}	
										
									rankDiv +=	"<span class='' style='font-weight:bold;'>Score : "+overallRank[i].total+"/"+overallRank[i].outOfMarks+"</span>" +
											"</span></div></div>";
										
								}
								
								if( !persoanlRank.total==0 || !persoanlRank.rank == null ){																	
									rankDiv +="<div class='row'><div class='col-sm-4'></div>" + 
										"<div class='panel-body col-sm-4'>" +						
										"<ul class='rankStudent-info-list'>" + 																
										"<li class='' style='font-weight:bold;'>Your rank : " + persoanlRank.rank + " </li>" + 								
										"<li class='' style='font-weight:bold;'>Your score : "+persoanlRank.total+"/"+persoanlRank.outOfMarks+"</li>" +
										"</li></div></div>";												
								}else{
									rankDiv +="<div class='row'><div class='col-sm-4'></div>" + 
									"<div class='panel-body col-sm-4'>" +						
									"<ul class='rankStudent-info-list'>" + 																
									"<li class='' style='font-weight:bold;'>Your rank : Not Applicable</li>" + 								
									"<li class='' style='font-weight:bold;'>Your score : Not Applicable</li>" +
									"</li></div></div>";	
								}

								$(".rank"+$(obj).attr("id")).html('<div class="clearfix"></div>'+rankDiv);
								
							}else{
									$(".rank"+$(obj).attr("id")).html('<div class="no-data-wrapper no-border">'+
											'<p class="no-data text-center">'+
												'None ranks to show'+
											'</p>'+
										'</div>');
								}

							return;
						},error:function(){
							alert("Please refresh the page!");
							return;
						}
					});
				}			
			}					
		
			function checkIfExpandForSubjectRank(obj){	
						
				if($(obj).attr("aria-expanded")=="false"){	
					$("."+$(obj).attr("data-subject").replaceAll(/[^\w]/gi,"")).html('<div class="no-data-wrapper no-border">'+
							'<p class="no-data text-center">'+
								'Loading...'+
							'</p>'+
					'</div>');
					
					rankData={
						'sapid':'${ userId}',
						'sem':$(obj).attr("data-sem"),
						'program':$(obj).attr("data-program"),
						'month':$(obj).attr("data-month"),
						'year':$(obj).attr("data-year"),			
						'subject':$(obj).attr("data-subject"),
						'consumerProgramStructureId':$(obj).attr("data-masterKey")	
					}
					
					$.ajax({
						type:'POST',
						url:'/studentportal/m/subjectWiseRank',
						data:JSON.stringify(rankData),
						contentType: "application/json",
						dataType: "json",
						success:function(response){				
							let persoanlRank = response.subjectWiseStudentsRank;
							let overallRank = response.overAllSubjectWiseRank;
							var rank="";		
							
							for(i=0;i<overallRank.length;i++){
								rank += "<div class='row'><div class='col-sm-4'></div>" +
										"<div class='panel-body col-sm-4' style='border-top:none;border-bottom:4px solid #d2232a;'>" +					
										"<span class='rankStudent-image'" +
											"style='background-image:url("+overallRank[i].studentImage+");'></span>" +
										"<h6 class='rankStudent-name'>"+overallRank[i].name+"</h6>" +
										"<span class='rankStudent-info-list'>";
										
								if(overallRank[i].rank == 'null'){
									rank += "<span class='' style='font-weight:bold;'>Not Applicable Rank</span> | ";
								} else{
									rank += "<span class='' style='font-weight:bold;'>"+overallRank[i].rank.split('/')[0]+" Rank</span> | ";
								}	
									
								rank +=	"<span class='' style='font-weight:bold;'>Score : "+overallRank[i].total+"</span>" +
										"</span></div></div>";
									
								/* "<li>"+response[i].rank+" Rank : "+response[i].name+"</li>"; */
							}
							
							if( !persoanlRank.total==0 || !persoanlRank.rank == null ){																	
								rank +="<div class='row'><div class='col-sm-4'></div>" + 
									"<div class='panel-body col-sm-4'>" +						
									"<ul class='rankStudent-info-list'>" + 																
									"<li class='' style='font-weight:bold;'>Your rank : " + persoanlRank.rank + " </li>" + 								
									"<li class='' style='font-weight:bold;'>Your score : "+persoanlRank.total+"/"+persoanlRank.outOfMarks+"</li>" +
									"</li></div></div>";												
							}else{
								rank +="<div class='row'><div class='col-sm-4'></div>" + 
								"<div class='panel-body col-sm-4'>" +						
								"<ul class='rankStudent-info-list'>" + 																
								"<li class='' style='font-weight:bold;'>Your rank : Not Applicable</li>" + 								
								"<li class='' style='font-weight:bold;'>Your score : Not Applicable</li>" +
								"</li></div></div>";	
							}
							$("."+$(obj).attr("data-subject").replaceAll(/[^\w]/gi,"")).html(rank+'<div class="clearfix"></div>');	

							return;
							
						},error:function(){
							alert("Please refresh the page!");
							return;
						}
					});											
				}
			}
		</script>
		
		<script>
			$(document).ready(function(){							
				
				if( "${cycleWiseRankConfigList.size() > 0}" ){
					$("#${cycleWiseRankConfigList.size()}").click();
					$(".${cycleWiseRankConfigList.size()}").click();
				}
			});
	
			function shareCycleWiseRank( sem, subjectcodeMappingId,userId,program,month,year,masterkey,subjectcount){

				$('#snackbar_details').html( "Sharing cycle wise rank <div style='margin: auto; margin-top: 10px;' class='loader'></div> ");
	    		$('#snackbar').fadeIn(10);
	    		let body = {
							'subjectcodeMappingId':subjectcodeMappingId,
							'sapid':userId,
							'sem':sem,
							'program':program,
							'month':month,
							'year':year,
							'consumerProgramStructureId':masterkey,
							'subjectsCount':subjectcount
	    	    		};
				$.ajax({
					type:'POST',
					url:'/social-media/m/shareCycleWiseRankAsPostOnLinkedIn',
					data:JSON.stringify(body),
					contentType: "application/json",
					dataType: "json",
					success:function(response){			
						
						$('#snackbar_details').html( "Successfully shared rank on LinkedIn");
			    		$('#snackbar').fadeOut(2000);
						return;
						
					},
					error:function(error){
						let responseText = JSON.parse( error.responseText );
						 if(responseText.error){
							$('#snackbar_details').html( "You Are Not Applicable to share rank on linkedIn.");
				    		$('#snackbar').fadeOut(2000);
				    		return;
						}
						else{
						window.location.replace( responseText.linkedInOauthRedirectURL );
						return; 
						}
						
					}
				});											
			}
	
			function shareSubjectWiseRank( sem, subjectcodeMappingId,userId,program,month,year,subject,consumerProgramStructureId){

				$('#snackbar_details').html( "Sharing subject wise rank <div style='margin: auto; margin-top: 10px;' class='loader'></div> ");
	    		$('#snackbar').fadeIn(10);
	    		let body = {
							'subjectcodeMappingId':subjectcodeMappingId,
							'sapid':userId,
							'sem':sem,
							'program':program,
							'month':month,
							'year':year,			
							'subject':subject,
							'consumerProgramStructureId':consumerProgramStructureId	
	    	    		};
				$.ajax({
					type:'POST',
					url:'/social-media/m/shareSubjectWiseRankAsPostOnLinkedIn',
					data:JSON.stringify(body),
					contentType: "application/json",
					dataType: "json",
					success:function(response){				

						$('#snackbar_details').html( "Successfully shared rank on LinkedIn");
			    		$('#snackbar').fadeOut(2000);
						return;
						
					},
					error:function(error){
						let responseText = JSON.parse(error.responseText )
						if(responseText.error){
							$('#snackbar_details').html( "You Are Not Applicable to share rank on linkedIn.");
				    		$('#snackbar').fadeOut(2000);
				    		return;
						}
						else{
						window.location.replace( responseText.linkedInOauthRedirectURL );
						return;
						}
						
					}
				});											
			}
		</script>
											
		<jsp:include page="../common/footer.jsp" />
</body>
</html>
