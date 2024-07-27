<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>

<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>

<html>
<head>
<meta charset="ISO-8859-1">
</head>
<body>

	<div>
		<div class="ranks">
			<div class="panel panel-default">
				<div class="panel-heading" role="tab" id="">
					<h4 class="panel-title">RANK</h4>
					<ul class="topRightLinks list-inline">
						<li><a class="panel-toggler collapsed" role="button"
							data-toggle="collapse" data-parent="#accordion"
							href="#collapseRank" aria-expanded="false"></a></li>
					</ul>
					<div class="clearfix"></div>
				</div>
				<div id="collapseRank"
					class="panel-collapse collapse in courses-panel-collapse"
					role="tabpanel" aria-labelledby="headingRank">
					<div class="panel-body">

						<div>
							
							<c:choose>
								<c:when test="${ empty homepageRank }">
									<div class="no-data-wrapper">
										<p class="no-data">
											<span class="icon-exams"></span>Rank List
										</p>
									</div>
								</c:when>
								<c:otherwise>
									<div style="margin-left: auto">
									
										<c:forEach var="rank" items="${ homepageRank }">
										
											<div class="alert alert-info" role="alert" style='text-align: center;'>
												<p><strong>Sem : ${ rank.sem } Cycle : ${ rank.month } ${ rank.year }</strong></p>
												<p>
													<c:choose>
														<c:when test="${ empty rank.rank }">
															Not Applicable
														</c:when>
														<c:otherwise>
															${ rank.rank } Rank | Score : ${ rank.total } / ${ rank.outOfMarks }
														</c:otherwise>
													</c:choose>
												</p>
											</div>
											
										</c:forEach>
										
									</div>
								</c:otherwise>
							</c:choose>
							
						</div>
						
						<div class="rankDiv rankStudents" style='text-align: center;'>
							<a href="/studentportal/student/ranks">Click here to see the
								ranks</a>
						</div>

					</div>
				</div>
			</div>
		</div>
	</div>
	
	<script type="text/javascript">
	
		
	
	</script>
</body>
</html>