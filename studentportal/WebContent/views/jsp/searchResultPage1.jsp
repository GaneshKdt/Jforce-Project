<!DOCTYPE html>

<html lang="en">

 <jsp:include page="common/jscssNew.jsp">
	<jsp:param value="Welcome to Student Zone" name="title" />
</jsp:include>
<style>
@media only screen and (max-width: 768px) {
  .mdHight {
       margin-top: 76px !important;
  }
}
@media only screen and (max-width: 425px) {
  .mdHight {
       margin-top: -105px !important;
  }
}

.nav-tabs .nav-link {
    color: #6C757D;
    background-color: #F2F2F2;
}

</style>
    <body>
   
	<%@ include file="common/headerDemo.jsp"%>

 
	<div class="sz-main-content-wrapper">

		<jsp:include page="common/breadcrum.jsp">
			<jsp:param value="Student Zone;Dashboard" name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<div id="sticky-sidebar">  
					<jsp:include page="common/left-sidebar.jsp">
						<jsp:param value="Dashboard" name="activeMenu" />
					</jsp:include>
               </div> 
				<div class="sz-content-wrapper dashBoard withBgImage">
					<%@ include file="common/studentInfoBar.jsp"%>

					
					<div class="sz-content large-padding-top  ">
						<div class="clearfix mt-md-4 mt-lg-0 mt-sm-0 mdHight" style="padding-bottom: 20px;"></div>
						<%@ include file="common/messageDemo.jsp"%>
						
							
			<div class="row">				
				
					<div class="col-md-12">
				
						 <form action="/studentportal/student/searchResultPage" class="row d-flex justify-content-center align-items-center" role="search"  method="get">
							<div class="row d-flex float-right align-items-center justify-content-end">
								 <div class="col-2">
									 <input id="searchResult" class="form-control searchResult" type="search" placeholder="Search" aria-label="Search" name="search" value="${searchValue}">
								 </div>
							 
								  <div class="col-1">    
										<button class="btn btn-outline-success" type="submit">Search</button>
								 </div>  
							 </div>
						  </form>
						<div class="row d-flex justify-content-center mt-2">
							<div class="col-12">
								
									<ul class="nav nav-tabs nav-justified  d-flex justify-content-center" id="myTab" role="tablist">
										<div class="col-lg-3 col-md-6 col-12">
											  <li class="nav-item" role="presentation">
											    <button class="nav-link active" id="home-tab" data-bs-toggle="tab" data-bs-target="#home-tab-pane" type="button" role="tab" style="height:60px;"
											     aria-controls="home-tab-pane" aria-selected="true">
														<div class="row d-flex justify-content-center align-items-center">
																<div class="">
																	<span><i class="fa-solid fa-magnifying-glass fs-4"></i></span>
																</div>
																<div class="fs-6 fw-bolder">
																	<span>All</span>
																</div>
														</div>
												
												</button>
											  </li>
									  	</div>
									  
									  <div class="col-lg-3 col-md-6 col-12">
										  <li class="nav-item" role="presentation">
										    <button class="nav-link" id="profile-tab" data-bs-toggle="tab" data-bs-target="#profile-tab-pane" type="button" role="tab" style="height:60px;"
										    aria-controls="profile-tab-pane" aria-selected="false">
												<div class="row  d-flex justify-content-center align-items-center">
													<div class="">
														<span><i class="fa-sharp fa-regular fa-circle-play fs-4"></i></span>	
													</div>
													<div class="fs-6 text-wrap fw-bolder">
														<span>Session Videos</span>
													</div>
												</div>
											</button>
										  </li>
									 </div>
									 <div class="col-lg-3 col-md-6 col-12">
										  <li class="nav-item" role="presentation">
											    <button class="nav-link" id="contact-tab" data-bs-toggle="tab" data-bs-target="#contact-tab-pane" type="button" role="tab" style="height:60px;"
											    aria-controls="contact-tab-pane" aria-selected="false">
												<div class="row  d-flex justify-content-center align-items-center">
														<div class="">
															<span><i class="fa-solid fa-clone fa-xl"></i></span>	
														</div>
														<div class="fw-bolder">
															<span>Resources</span>
														</div>
												</div>
												</button>
										  </li>
									  </div>
									  <div class="col-lg-3 col-md-6 col-12">
										  <li class="nav-item" role="presentation">
										    <button class="nav-link" id="disabled-tab" data-bs-toggle="tab" data-bs-target="#disabled-tab-pane" type="button" role="tab" style="height:60px;"
										     aria-controls="disabled-tab-pane" aria-selected="false" >
											<div class="row  d-flex justify-content-center align-items-center">
													<div class="">
														<span><i class="fa-solid fa-circle-question fa-xl"></i></span>	
													</div>
													<div class="fs-6 fw-bolder">
														<span>QnA</span>
													</div>
											</div>
											</button>
										  </li>
									  </div>
									</ul>
								</div>
							</div>
						
						<div class="bg-white container-fluid rounded  float-start">
							
							
						<div class="tab-content mt-2" id="myTabContent">
						  <div class="tab-pane fade show active" id="home-tab-pane" role="tabpanel" aria-labelledby="home-tab" tabindex="0">
							<div id="allDataList">
							<c:if test="${searchValue !=  null}">
							<div class="row">
								<div class="col-6">
									<h5 class="p-2 m-2 text-capitalize">Showing result for&nbsp;<span><strong><em>${searchValue}</em></strong></span></h5>
								</div>
								<div class="col-6 ">
									<h5 class="p-2 m-2 text-end text-capitalize"><em>${allList.size()} Result found</em></h5>
								</div>
							</div>
							</c:if>
									 <c:forEach var="responseBean" items="${allList}">
									
									<c:if test="${responseBean.contentType == 'qna'}">
									
										<div class="card mt-2 mb-2">
											<div class="card-body">
												<div class="row">
													<div class="col-3 d-flex align-items-center justify-content-center fs-1"><i class="fas fa-question-circle "></i></div>
													<div class="col-9">
														
															<div class="row"> 
																<div class="col-8 d-flex justify-content-start align-items-center">
																	<h6 class="card-title"><Strong >Query:</Strong>&nbsp;${responseBean.query}</h6>
																</div>
																<div class="col-4 d-flex justify-content-end align-items-center">
																	<span class="text-end">${responseBean.createdDate}</span>
																</div>
															</div>
														
															<div class="row"> 
																<div class="col-8 d-flex justify-content-start align-items-center">
																	  <h6><Strong >Answer:</Strong> &nbsp;${responseBean.answer}</h6>
																</div>
																<div class="col-4 d-flex justify-content-end align-items-center">
																	<span class="text-end">${responseBean.lastModifiedDate}</span>
																</div>
															</div>
															<div class="d-flex justify-content-start align-items-center">
															   <h6 class="card-title"><Strong >Prof.:</Strong>&nbsp;<span>${responseBean.facultyFirstName}</span> <span>${responseBean.facultyLastName}</span></h6>
															</div>
															<div class="d-flex justify-content-start align-items-center">
															   <h6><Strong >Content Type:</Strong> &nbsp;${responseBean.contentType}</h6>
															</div>
														</div>
													</div>
												</div>
											</div>
									
									</c:if>
									<c:if test="${responseBean.contentType == 'video'}">
									
										<div class="card mt-2">
													<div class="row">
														<div class="col-3">
															<img src="${responseBean.thumbnailUrl}" class="card-img-top" alt="...">
														</div>
														<div class="col-9">
															<div class="card-body">
															
																<h6 class="card-title"><Strong >Name:</Strong>&nbsp;${responseBean.name}</h6>
																<h6 class="card-title"><Strong >Prof.:</Strong>&nbsp;<span>${responseBean.facultyFirstName}</span> <span>${responseBean.facultyLastName}</span></h6>
																<div class="row">
																	<div class="col-6 card-text d-flex justify-content-start align-items-center">
																		<span><Strong> Video content:</Strong>&nbsp;${responseBean.transcriptContent}</span>
																	</div>
																	<div class="col-6 card-text d-flex justify-content-center align-items-center">
																	<span>${responseBean.startTime}</span>
																	</div>
																</div>
																<c:if test="${responseBean.description != null}">
																	<div class="d-flex card-text justify-content-start align-items-center"><Strong> Description :</Strong>&nbsp;${responseBean.description}</div>
																</c:if>
																<div class="card-text"><Strong >Content Type:</Strong>&nbsp;${responseBean.contentType}</div>
																<a class=" btn btn-sm btn-secondary"  onClick="openVideoPage('${responseBean.videoContentId}', '${responseBean.programSemSubjectId}');">Watch Video</a>
															</div>
														</div>
													</div>
												</div>
									</c:if>
									<c:if test="${responseBean.contentType == 'pdf'}">
											<div class="card mt-2 mb-2">
												<div class="card-body">
														<div class="row">
															<div class="col-3 fs-1 d-flex align-items-center justify-content-center"><i class="fa-sharp fa-solid fa-file-pdf"></i></div>
															<div class="col-9">
																<h6 class="card-title"><Strong >Pdf Name:</Strong>&nbsp;${responseBean.name}</h6>
																<div class="card-text"><Strong >Pdf Content:</Strong> &nbsp;${responseBean.pdfContent}</div>
																<c:if test="${responseBean.description != null}">
																	<div><Strong> Discription :</Strong>&nbsp;${responseBean.description}</div>
																</c:if>
																<div><Strong >Content Type:</Strong> &nbsp;${responseBean.contentType}</div>
																<a class="btn btn-sm btn-secondary" onClick="openPdfPage('${responseBean.previewPath}', '${responseBean.name}');">View</a>
															</div>
														</div>
												</div>
											</div>
									
									
									</c:if>
									
									</c:forEach>
							</div>
						  
						  
						  </div>
						  
						  
						  <div class="tab-pane fade" id="profile-tab-pane" role="tabpanel" aria-labelledby="profile-tab" tabindex="0">
							<div id="sessionVideoslist">
							<c:if test="${searchValue !=  null}">
							<div class="row">
								<div class="col-6">
									<h5 class="p-2 m-2 text-capitalize">Showing result for&nbsp;<span><strong><em>${searchValue}</em></strong></span></h5>
								</div>
								<div class="col-6 ">
									<h5 class="p-2 m-2 text-end text-capitalize"><em>${videoList.size()} Result found</em></h5>
								</div>
							</div>
							</c:if>
							
							
										 <c:forEach var="responseBean" items="${videoList}">
													
										<div class="card mt-2">
													<div class="row">
														<div class="col-3">
															<img src="${responseBean.thumbnailUrl}" class="card-img-top" alt="...">
														</div>
														<div class="col-9">
															<div class="card-body">
															
																<h6 class="card-title"><Strong >Name:</Strong>&nbsp;${responseBean.name}</h6>
																<h6 class="card-title"><Strong >Prof.:</Strong>&nbsp;<span>${responseBean.facultyFirstName}</span> <span>${responseBean.facultyLastName}</span></h6>
																<div class="row">
																	<div class="col-6 card-text d-flex justify-content-start align-items-center">
																		<span><Strong> Video content:</Strong>&nbsp;${responseBean.transcriptContent}</span>
																	</div>
																	<div class="col-6 card-text d-flex justify-content-center align-items-center">
																	<span>${responseBean.startTime}</span>
																	</div>
																</div>
																<c:if test="${responseBean.description != null}">
																	<div class="d-flex card-text justify-content-start align-items-center"><Strong> Description :</Strong>&nbsp;${responseBean.description}</div>
																</c:if>
																<div class="card-text"><Strong >Content Type:</Strong>&nbsp;${responseBean.contentType}</div>
																<a class=" btn btn-sm btn-secondary"  onClick="openVideoPage('${responseBean.videoContentId}', '${responseBean.programSemSubjectId}');">Watch Video</a>
															</div>
														</div>
													</div>
												</div>
										</c:forEach>
							</div>
						  
						  </div>
						  
						  
						  <div class="tab-pane fade" id="contact-tab-pane" role="tabpanel" aria-labelledby="contact-tab" tabindex="0">
											<div id="resoucesList">
											
											<c:if test="${searchValue !=  null}">
												<div class="row">
													<div class="col-6">
														<h5 class="p-2 m-2 text-capitalize">Showing result for&nbsp;<span><strong><em>${searchValue}</em></strong></span></h5>
													</div>
													<div class="col-6 ">
														<h5 class="p-2 m-2 text-end text-capitalize"><em>${resourceList.size()} Result found</em></h5>
													</div>
												</div>
												</c:if>
											
												<table class="table">
													<thead>
														<tr>
															<th scope="col">Sr No.</th>
															<th scope="col">Name</th>
															<th scope="col">Pdf Content</th>
															<th scope="col">Description</th>
															<th scope="col">Content Type</th>
															<th scope="col">Action</th>
														</tr>
													</thead>
													<c:forEach var="responseBean" items="${resourceList}" varStatus="status">
														<tbody>
															<tr>
																<th scope="row">${status.count}</th>
																<td>${responseBean.name}</td>
																<td>${responseBean.pdfContent}</td>
																<td>${responseBean.description}</td>
																<td>${responseBean.contentType}</td>
																<td><a class="btn btn-sm btn-secondary" onClick="openPdfPage('${responseBean.previewPath}', '${responseBean.name}');">View</a></td>
															</tr>
														</tbody>
													</c:forEach>

												</table>
										</div>
							</div>
						  <div class="tab-pane fade" id="disabled-tab-pane" role="tabpanel" aria-labelledby="disabled-tab" tabindex="0">
						  
						  <div id="sessionVideoslist">
						  
											<c:if test="${searchValue !=  null}">
												<div class="row">
													<div class="col-6">
														<h5 class="p-2 m-2 text-capitalize">Showing result for&nbsp;<span><strong><em>${searchValue}</em></strong></span></h5>
													</div>
													<div class="col-6 ">
														<h5 class="p-2 m-2 text-end text-capitalize"><em>${qnaList.size()} Result found</em></h5>
													</div>
												</div>
												</c:if>
											
						  
						  
									  <c:forEach var="responseBean" items="${qnaList}">
											<div class="px-2 py-2  ">
											
												<div class="border border-1 p-3">
													<div class="row"> 
														<div class="col-8 d-flex justify-content-start align-items-center">
															<h6 class="card-title"><Strong >Query:</Strong>&nbsp;${responseBean.query}</h6>
														</div>
														<div class="col-4 d-flex justify-content-end align-items-center">
															<span class="text-end">${responseBean.createdDate}</span>
														</div>
													</div>
												</div>
												
												<div class="border border-1 p-3 ">
													<div class="row"> 
														<div class="col-8 d-flex justify-content-start align-items-center">
														 <h6><Strong >Answer:</Strong> &nbsp;${responseBean.answer}</h6>
														</div>
														<div class="col-4 d-flex justify-content-end align-items-center">
															<span class="text-end">${responseBean.lastModifiedDate}</span>
														</div>
													</div>
													<div class="row">
														<h6 class="card-title"><Strong >Prof.:</Strong>&nbsp;<span>${responseBean.facultyFirstName}</span> <span>${responseBean.facultyLastName}</span></h6>
													</div>
												</div>
												
											</div>
									</c:forEach>

							</div>
						  </div>
						</div>
							
						</div>
					
					
					</div>
	
						<div class="col-xxl-6  col-lg-4 col-md-12"></div>	
					
				</div>	

					</div>				
				</div>
			</div>
		</div>
		
	</div>
	 <jsp:include page="common/footerDemo.jsp" />

<script type="text/javascript">

function openPdfPage(priviewPath,name){
	if(priviewPath!=null && name!=null){
		var stringWithoutMarkTags = name.replace(/<\/?mark>/g, '');		
		var replacedName = stringWithoutMarkTags.replace(/ /g, '+');
		window.location.href = "/acads/student/previewContent?previewPath="+priviewPath+"&name="+replacedName+"&type=PDF";
		}
	}



function openVideoPage(videoContentId,pssId){
	if(videoContentId!=null && pssId!=null){
		window.location.href = "/acads/student/watchVideos?id="+videoContentId+"&pssId="+pssId;
	}
	
}


	window.addEventListener('load', function() {

		if("${type}"==="qna"){


	        $('#home-tab, #profile-tab, #contact-tab').removeClass('active');
	        $('#home-tab, #profile-tab, #contact-tab').attr('aria-selected', 'false');

	        $('#home-tab-pane, #profile-tab-pane, #contact-tab-pane').removeClass('active show');

	        $('#disabled-tab').attr('aria-selected', 'true');

	        $('#disabled-tab').addClass('active');
	        $('#disabled-tab-pane').addClass('active show');

	        
		}else if("${type}"==="resource"){
			
			 $('#home-tab, #profile-tab, #disabled-tab').removeClass('active');			
			 $('#home-tab-pane,#disabled-tab-pane,#profile-tab-pane').removeClass('active show');
			 $('#home-tab,#profile-tab,#disabled-tab').attr('aria-selected', 'true');

			 $('#contact-tab').attr('aria-selected', 'false');
			 $('#contact-tab').addClass('active');
			 $('#contact-tab-pane').addClass('active show');
			 
		}else if("${type}"==="video"){


			 $('#home-tab, #contact-tab, #disabled-tab').removeClass('active');			
			 $('#home-tab-pane,#disabled-tab-pane,#contact-tab-pane').removeClass('active show');
			 $('#home-tab,#contact-tab,#disabled-tab').attr('aria-selected', 'true');

			 $('#profile-tab').attr('aria-selected', 'false');
			 $('#profile-tab').addClass('active');
			 $('#profile-tab-pane').addClass('active show');
			
		}


		
	})
	

</script>

</body>
</html>	