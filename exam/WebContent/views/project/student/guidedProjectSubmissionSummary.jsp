<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html lang="en">
	<jsp:include page="../../common/jscss.jsp">
	<jsp:param value="Guided Project Summary" name="title"/>
	</jsp:include>
	<style>
		.pending_block,.pending_block:hover,.pending_block:active{
			padding:8px 15px;
			background-color:#F4BC01;
			color:white;
			border-radius:5px;
		}
		.rejected_block{
			padding:8px 15px;
			background-color:red;
			color:white;
		}
		.success_block,.success_block:hover,.success_block:active{
			padding:8px 15px;
			background-color:#58BC34;
			color:white;
			border-radius:5px;
		}
	</style>
	<body>
		<%@ include file="../../common/header.jsp" %>
		
		<div class="sz-main-content-wrapper">
		
			<jsp:include page="../../common/breadcrum.jsp">
				<jsp:param value="Student Zone;Exams;Guided Project Summary" name="breadcrumItems"/>
			</jsp:include>
			
			<div class="sz-main-content menu-closed">
				<div class="sz-main-content-inner">
					<jsp:include page="../../common/left-sidebar.jsp">
						<jsp:param value="Guided Project Submission" name="activeMenu"/>
					</jsp:include>
							
					<div class="sz-content-wrapper examsPage">
						<%@ include file="../../common/studentInfoBar.jsp" %>
						<div class="sz-content">
							<h2>Project Dashboard for ${ status.examMonth } - ${ status.examYear }</h2>
							<div class="panel panel-default" style="margin-top:70px;">
								<div class="panel-body">
									<%@ include file="../../common/messages.jsp" %>
								</div>
								<c:if test="${ status != null }">
									<div class="data-content">
										<div class="table-responsive">
											<table class="table table-striped">
												<thead>
													<th>Module</th>
													<th>Status</th>
													<th>Due date</th>
													<th>Reason</th>
													<th>Marks</th>
													<th>Action</th>
												</thead>
												<tbody>
													<c:if test="${ status.hasTitle == 'Y' }">
														<tr>
															<td>Title Selection</td>
															<td>
																${ status.titleSelected == 'Y' ? "Selected" : "Not Selected" }
															</td>
															<td>
																${ status.titleSelectionEndDate }
															</td>
															<td>
																<c:choose>
																	<c:when test="${ status.titleSelectionActive == 'Y' }">
																	</c:when>
																	<c:otherwise>
																	</c:otherwise>
																</c:choose>
															</td>
															<td>
																<a href="javascript:void(0)">View</a>
															</td>
														</tr>
													</c:if>
													<c:if test="${ status.hasSOP == 'Y' }">
														<tr>
															<td>SOP Submission</td>
															<td>
															<fmt:formatDate var="endDate" value="<%=new Date()%>" pattern="yyyy-MM-dd HH:mm:ss" /> 
															 <c:choose>     
																	<c:when test="${ status.sopStatus.startDate != null }">
																	    <c:choose>
																		    <c:when test="${ status.sopStatus.submissionsMade > 0 && status.sopStatus.status != null && status.sopStatus.status == 'Rejected' && status.sopStatus.endDate > endDate}">
																		    Re-Submit    
																		    </c:when> 
																		    <c:otherwise> 
																		    	 <c:choose>
																			   		 <c:when test="${ status.sopStatus.submissionsMade > 0 }">
																			    	 	     ${ status.sopStatus.status }  
																			     	</c:when>
																			     	<c:otherwise> 
																				  		Pending        
																				 	</c:otherwise> 
																				  </c:choose>
																			</c:otherwise> 
																	    </c:choose>
																		 
																	</c:when>
																		<c:otherwise>
																			-
																		</c:otherwise>
																</c:choose>
															</td>
															<td>
																<c:choose>
																	<c:when test="${ status.sopStatus.endDate != null }">
																		${ status.sopStatus.endDate }
																	</c:when>
																		<c:otherwise>
																			-
																		</c:otherwise>
																</c:choose>
															</td>
															<td>
																<c:choose>
																	<c:when test="${ status.sopStatus.reason != null }">
																		${ status.sopStatus.reason }
																	</c:when>
																<c:when test="${ status.sopStatus.startDate == null }">
																	-
																</c:when>
																<c:otherwise>
																	${ status.sopStatus.reason == null && status.sopStatus.status == 'Submitted' ? 'Awaiting Faculty Evaluation' : '-' }
																</c:otherwise>
																</c:choose>
															</td>
															<td>
																-
															</td> 
															<td>
																<a href="uploadProjectSOPForm?subject=${ status.subject }">View</a>
															</td>
														</tr>
													</c:if>
													<c:if test="${ status.hasSynopsis == 'Y' }">
														<tr>
															<td>Synopsis Submission</td>
															<td>
																<c:choose>
																	<c:when test="${ status.synopsisStatus.startDate != null }">       
																		${ status.synopsisStatus.submissionsMade > 0 ? status.synopsisStatus.status : "Pending" }
																	</c:when>
																		<c:otherwise>
																			-
																		</c:otherwise>
																</c:choose>
															</td>
															<td>
																<c:choose>
																	<c:when test="${ status.synopsisStatus.endDate != null }">
																		${ status.synopsisStatus.endDate }  
																	</c:when>
																		<c:otherwise>
																			-
																		</c:otherwise>
																</c:choose>
															</td>
															<td>
																<c:choose>
																	<c:when test="${ status.synopsisStatus.reason != null }">
																		${ status.synopsisStatus.reason }
																	</c:when>
																	<c:when test="${ status.synopsisStatus.startDate == null }">
																		-
																	</c:when>
																	<c:otherwise>
																		${ status.synopsisStatus.reason == null && status.synopsisStatus.status == 'Submitted' ? 'Awaiting Faculty Evaluation' : '-' }
																	</c:otherwise>
																</c:choose>
															</td>
															<td>
																<c:choose>
																	<c:when test="${ status.synopsisStatus.score != null }">
																		${ status.synopsisStatus.score }
																	</c:when>
																	<c:when test="${ status.synopsisStatus.score == null }">
																		-
																	</c:when>
																	<c:otherwise>
																		-
																	</c:otherwise>
																</c:choose>
															</td>    
															<td>       
																<c:choose>
																	<c:when test="${ status.synopsisStatus.allowSubmission != true }">   
																		- 
																	</c:when> 
																	<c:when test="${! status.synopsisStatus.allowSubmission  }">
																		Synopsis Submission not active!  
																	</c:when>  
																	<%-- <c:when test="${ status.paymentPending == 'Y' }">
																		Payment Pending
																	</c:when>
																	<c:when test="${ status.paymentPending == 'N' }">
																		<a href="uploadProjectSynopsisForm?subject=${ status.subject }">View</a>
																	</c:when> --%>
																	<c:otherwise>  
																		<a href="uploadProjectSynopsisForm?subject=${ status.subject }">View</a>
																	</c:otherwise>
																</c:choose>
															</td>
														</tr>
													</c:if>
													
													<c:if test="${ status.hasSubmission == 'Y' }">
														<tr>
															<td>Final Project Submission</td>
															<td>
															<c:choose>
																<c:when test="${ status.submissionStatus.allowSubmission }">       
																	Active
																</c:when>
																<c:otherwise>
																	Not active
																</c:otherwise>
															</c:choose>
															</td>
															<td>
																<c:choose>
																	<c:when test="${ status.submissionStatus.allowSubmission }">       
																		${ status.submissionStatus.endDate }
																	</c:when>
																	<c:otherwise>
																		-
																	</c:otherwise>
																</c:choose>
															</td>
															<td></td>
															<td>
																-
															</td>
															<td>
																<c:choose>
																	<c:when test="${ status.submissionStatus.allowSubmission }">       
																		<a href="viewProject?subject=${ status.subject }">View</a>
																	</c:when>
																	<c:otherwise>
																		
																	</c:otherwise>
																</c:choose>
															</td>
														</tr>
													</c:if>
													<c:if test="${ status.hasViva == 'Y' }">
														<tr>
															<td> Book VIVA Slot</td>
															<td>Not active</td>
															<td>
																<c:choose>
																	<c:when test="${ status.vivaStatus.live  == 'Y' }">       
																		${ status.vivaStatus.endDate }
																	</c:when>   
																	<c:otherwise>
																		-
																	</c:otherwise> 
																</c:choose>
															</td>
															<td>-</td>
															<td>
																-
															</td>
															<td>   
															    <c:choose>
																	<c:when test="${ status.vivaStatus.live == 'Y' }">
																	     <a href="vivaSlotBookingForm">View</a>
																	 </c:when>
																	<c:otherwise>
																		 -  
																	</c:otherwise>  
																</c:choose>  
															</td> 
															<td></td>
															<td>
																
															</td>
														</tr>
													</c:if>
												</tbody>
											</table>
										</div>
									</div>
								</c:if>
							</div>
							<%-- 
							<h2>Marks</h2>
							<div class="panel panel-default" style="margin-top:100px;">
								<div class="panel-body">
									<%@ include file="../../common/messages.jsp" %>
								</div>
								<div class="data-content">
									<div class="table-responsive">
										<table class="table table-striped">
											<thead>
												<th>Sr.no</th>
												<th>Year</th>
												<th>Month</th>
												<th>Module</th>
												<th>Max Score</th>
												<th>Score</th>
											</thead>
											<tbody>
												<tr>
													<td>1.</td>
													<td>2020</td>
													<td>Jun</td>
													<td>Synopsis Submission</td>
													<td>
														100
													</td>
													<td>90</td>
													
												</tr>
												<tr>
													<td>2.</td>
													<td>2020</td>
													<td>Jun</td>
													<td>Final Project</td>
													<td>
														100
													</td>
													<td>80</td>
													
												</tr>
												
											</tbody>
										</table>
									</div>
								</div>
							</div> --%>
						</div>
					</div>
				</div>
			</div>
		</div>
        <jsp:include page="../../common/footer.jsp"/>
	</body>
</html>