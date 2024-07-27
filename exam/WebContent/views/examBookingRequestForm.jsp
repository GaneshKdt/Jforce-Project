
<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<html lang="en">
    <jsp:include page="./common/jscss.jsp">
	<jsp:param value="Exam Booking" name="title"/>
    </jsp:include>
    
    <body>
    	<%@ include file="./common/header.jsp" %>
        
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="./common/breadcrum.jsp">
			<jsp:param value="Student Zone;Exams;Exam Booking" name="breadcrumItems"/>
			</jsp:include>
        	
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="./common/left-sidebar.jsp">
								<jsp:param value="exam booking" name="activeMenu"/>
							</jsp:include>
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="./common/studentInfoBar.jsp" %>
              						<div class="sz-content">
              							<div class="panel panel-default">
	              							<div class="panel-body">
	              							<%@ include file="./common/messages.jsp" %>
	              							<h2 class="red text-capitalize">Exam Booking: Apr-2020(Carry forward/Refund)</h2>
	              							<div class="clearfix"></div>
	              								<div class="alert alert-warning">
														April exam cancelled we are providing students with an option to carry forward fees for June 2020 exam cycle or apply for refund. Please note refund process will only start once we resume work post lock down, refund from then will take 15-20 days.
												</div>
												<div class="clearfix"></div>
												<c:if test="${ examBookingBeansList.size() > 0 }">
												<table class="table">
													<thead>
														<th>Subject</th>
														<th>Sem</th>
														<th>Center</th>
														<th>Exam Date</th>
														<th>Exam Time</th>
														
													</thead>
													<tbody>
														<c:forEach var="examBookingBean" items="${ examBookingBeansList }">
															<tr>
																<td>${ examBookingBean.subject }</td>
																<td>${ examBookingBean.sem }</td>
																<td>${ examBookingBean.examCenterName }</td>
																<td>${ examBookingBean.examDate }</td>
																<td>${ examBookingBean.examTime }</td>
																
															</tr>
														</c:forEach>
													</tbody>
												</table><br/>
												<h2 class="red text-capitalize">Total Amount: ${ requestFormBean.total_amount } /-</h2>
												<div class="clearfix"></div>
												<div class="col-md-4 column">
													
													<form action="examBookingRequest" method="POST">
													<input type="hidden" name="month" value="Apr" />
													<input type="hidden" name="year" value="2020" />
													<div class="radio">
													  <label style="font-weight:bold"><input type="radio" name="request_action" value="except" checked>Add amount to june 2020 booking</label>
													</div>
													<div class="radio">
													  <label style="font-weight:bold"><input type="radio" name="request_action" value="refund">Refund</label>
													</div>
													<button class="btn btn-primary btn-sm">submit</button>
													</form>
													
													<p align="justify" style="margin-top:10px;">
																		
													</p>
												</div>
												</c:if>
											</div>
										</div>
              						</div>
              				</div>
					</div>
            </div>
        </div>
            
  	
        <jsp:include page="./common/footer.jsp"/>
            
		<script>
			console.log(new Date('${uploadProjectSOPBean.endDate}'));
			$('#subjectTimer').countdown({until: new Date('${uploadProjectSOPBean.endDate}'), format: 'dHMS'});
			$('#subjectTimer').countdown('toggle');
		</script>
		
    </body>
</html>