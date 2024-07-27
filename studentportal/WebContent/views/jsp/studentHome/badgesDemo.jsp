<!DOCTYPE html>
<html lang="en">
<body>
	<c:if test="${mybadges.earnedBadgeList.size() gt 0 }">
		<div class="d-flex align-items-center text-wrap py-1"
			style="width: 50%;">
			<span class="fw-bold me-1"><small class="fs-5">YOUR
					LATEST BADGE</small></span>
			<div class=" d-flex ms-auto">
				<a href="/studentportal/student/myBadges" class="text-dark me-1"><small class="text-nowrap">SEE
						ALL</small></a> <a type="button" data-bs-toggle="collapse"
					href="#collapseBadge" role="button" aria-expanded="true"
					aria-controls="collapseBadge" id="collapseBadgeCard" class="text-muted ">
					<i class="fa-solid fa-square-minus"></i>
				</a>
			</div>
		</div>
		<div class="mt-md-5 mt-lg-0">
			<div class="collapse show" id="collapseBadge">
				<div class="card" style="width: 50%;">
					<div class="row mb-2">
						<div class="container text-center">
							<div id="newBadges">
								<div class="row justify-content-center">
									<c:forEach var="earnedBadge"
										items="${mybadges.earnedBadgeList}" varStatus="status">
										<div class="col-md-2 mb-2 mx-2 d-flex justify-content-center"
											style="margin-top: 20px;">
											<div class="card" style="min-width: 25rem;">
												<div class="card-body">
													<div class="container-fluid">
														<div class="text-center">
															<img class="img-fluid card-img-top"
																style="align-self: center; padding: 10px;"
																title="${earnedBadge.badgeName}"
																src="${earnedBadge.attachment}">
														</div>
													</div>
												</div>
												<div class="card-footer" title="${earnedBadge.awardedAt}">
													<strong>${earnedBadge.awardedAt}</strong>
												</div>
											</div>
										</div>
									</c:forEach>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="collapse" id="collapseBadge">
				<div class="card" style="width: 50%;">
					<div class="card-body text-center ">
						<h6 type="button" data-bs-toggle="collapse" href="#collapseBadge"
							role="button" aria-expanded="true" aria-controls="collapseBadge"
							id="collapseBadgeCard">
							<i class="fas fa-trophy"></i><small>View Your Latest Earned
								Badge</small>
						</h6>
					</div>
				</div>
			</div>
		</div>
	</c:if>
</body>
</html>