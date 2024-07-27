<style>
.linkFlow {
	color: #404041;
}

.linkFlow:hover {
	color: #3366CC;
	cursor: pointer;
}
</style>

<div class="container-fluid float-start bg-white rounded mb-4">
	<c:choose>
		<c:when test="${not empty sessionPlanPgBean.sessionPlanModuleList}">
			<div class="row mt-2 mb-0" style="margin-bottom: 0.8rem;">
				<c:forEach var="module"
					items="${sessionPlanPgBean.sessionPlanModuleList}">
					<div class="col-md-6 col-sm-6 col-lg-6 col-xl-3  py-2"
						style="margin-top: 20px; margin-bottom: 20px;">
						<div class="d-flex align-items-center justify-content-center">
							<div class="card shadow bg-body" style="width: 20rem">
								<div class="card-header bg-dark text-light text-wrap"
									style="height: 100px; font-size: 1.0rem;">
									<div class="row" style="margin-bottom: 0.8rem;">
										<div class="col-md-4 col-3 text-truncate"
											data-bs-toggle="tooltip" data-bs-placement="bottom"
											title="${module.chapter }">${module.chapter }</div>
										<div class="col-md-1 col-3">|</div>
										<div class="col-md-7 col-3 text-truncate text-start"
											data-bs-toggle="tooltip" data-bs-placement="bottom"
											title="${module.title }">${module.title }</div>
									</div>
									<c:set var="topic"
										value="${fn:length(module.topic) > 70 ? fn:substring(module.topic, 0, 70).concat('...') : module.topic}" />
									<div class="row text-break" style="margin-bottom: 0.8rem;">
										<div class="col-md-12 col-24 d-block" data-bs-toggle="tooltip"
											data-bs-placement="right" title="${module.topic }">${topic }</div>
									</div>
								</div>
								<div class="card-body overflow-hidden" style="height: 72%;">
									<ul class="list-group list-group-flush"
										style="text-decoration: none; border-top: 0; border-bottom: 0;">
										<li class="list-group-item"
											style="border: none; height: 70px;"><h5>
												<a class="linkFlow" style="font-size: 20px"
													onClick="showAllVideos(${module.id})"><i
													class="fa-regular fa-circle-play fa-xl"></i>&nbsp;&nbsp;Videos</a>
											</h5></li>
										<li class="list-group-item"
											style="border: none; height: 70px;"><h5>
												<a class="linkFlow" style="font-size: 20px"
													onClick="showAllQuizes(${module.id})"><i
													class="fa-solid fa-puzzle-piece fa-xl"></i>&nbsp;&nbsp;Quiz</a>
											</h5></li>
										<li class="list-group-item"
											style="font-size: 14px; font-weight: bold; border: none; height: 70px;">
											<div class="row" style="margin-bottom: 0.8rem;">
												<div class="col" id="${module.id }">Watched - N/A</div>
												<div class="col">
													Quiz Taken - <c:out value="${module.attemptStatus == 'Attempted' ? 'Yes' : 'No'}"  /><br />
												</div>
											</div>
											<div class="row" style="margin-bottom: 0.8rem;">
												<div class="col"></div>
												<div class="col">Score - <c:out value="${not empty module.quizScore ? module.quizScore : 'N/A'}" /></div>
											</div>
										</li>
									</ul>
								</div>
							</div>
						</div>
					</div>
				</c:forEach>
			</div>
		</c:when>
		<c:otherwise>
			<div class="no-data-wrapper">
				<h6 class="no-data nodata mb-5 mt-5">
					<span class="fa-solid fa-book-open-reader"></span> No Module
					Available.
				</h6>
			</div>
		</c:otherwise>
	</c:choose>
	<div class="row px-4 py-4">
		<div class="clearfix"></div>
		<c:if test="${not empty sessionPlanPgBean.facultyName}">
			<b>Faculty Name:</b>
			<br>
			<span>Prof. ${sessionPlanPgBean.facultyName } </span>
			<br>
		</c:if>
		<c:if test="${not empty sessionPlanPgBean.objectives}">
			<br>
			<b>Course Objectives: </b>
			<br>
			<span>${sessionPlanPgBean.objectives }</span>
		</c:if>
		<c:if test="${not empty sessionPlanPgBean.learningOutcomes}">
			<div class="clearfix"></div>
			<b>Learning Outcomes: </b>
			<br>
			<span>${sessionPlanPgBean.learningOutcomes }</span>
		</c:if>
		<c:if test="${not empty sessionPlanPgBean.prerequisites}">
			<div class="clearfix"></div>
			<b>Pre-requisite(s): </b>
			<br>
			<span>${sessionPlanPgBean.prerequisites }</span>
		</c:if>
		<c:if test="${not empty sessionPlanPgBean.links}">
			<div class="clearfix"></div>
			<b>Links :</b>
			<br>
			<span>${sessionPlanPgBean.links }</span>
		</c:if>
	</div>
</div>

