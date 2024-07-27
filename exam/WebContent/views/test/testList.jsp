<!DOCTYPE html>


<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>


<html lang="en">

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')"
	var="server_path" />


<jsp:include page="../common/jscss.jsp">
	<jsp:param value="View All Assignments" name="title" />
</jsp:include>

<%

%>
    <body>

		<%@ include file="../common/header.jsp"%>



		<div class="sz-main-content-wrapper">

			<jsp:include page="../common/breadcrum.jsp">
				<jsp:param value="Exam;Assignments" name="breadcrumItems" />
			</jsp:include>

			<div class="sz-main-content menu-closed">
				<div class="sz-main-content-inner">

					<jsp:include page="../common/left-sidebar.jsp">
						<jsp:param value="Tests" name="activeMenu" />
					</jsp:include>


					<div class="sz-content-wrapper dashBoard demoExam">
						<%@ include file="../common/studentInfoBar.jsp"%>

						<div class="sz-content">

								
								<h2 class="red text-capitalize">
									Assignments
								</h2>
							<div class="clearfix"></div>
							<div class="panel-content-wrapper">
								
								<!-- Code for page goes here start -->
									
												<div class="">
													<table class="table table-striped table-hover tables" style="font-size: 12px">
														<thead>
															<tr>
																<th>Sr. No.</th>
																<!-- <th>Name</th> -->
																<th>Subject</th>
																<th> Month</th>
																<th> Year</th>
																<th>Start Date</th>
																<th>End Date</th>
																<th>Actions</th>
															</tr>
														</thead>
														<tbody>

															<c:forEach var="test" items="${testsList}"	varStatus="status">
																<tr>
																	<td><c:out value="${status.count}" /></td>
																	<%-- <td><c:out value="${test.testName}" /></td> --%>
																	<td><c:out value="${test.subject}" /></td>
																	<td><c:out value="${test.month}" /></td>
																	<td><c:out value="${test.year}" /></td>
																	<td><c:out value="${fn:replace(test.startDate,'T', ' ')}"></c:out></td>
																	<td><c:out value="${fn:replace(test.endDate,'T', ' ')}"></c:out></td>

																	<td>
																	 <c:url value="viewTestDetails" var="detailsUrl">
																		<c:param name="testId" value="${test.id}" />
																	 </c:url> 
																	<c:choose>
																	<c:when test="${test.attempt <= 0}">
																	
																	<a href="/exam/viewTestDetailsForStudents?id=${test.id}&message=openTestDetails" title="Details"><i
																		class=" fa-solid fa-circle-play"></i>  Start Assignment</a>&nbsp;
																	</c:when>
																	<c:when test="${test.attempt > 0 && test.maxAttempt > test.attempt}">
																	<a href="/exam/viewTestDetailsForStudents?id=${test.id}&message=openTestDetails" title="Details"><i
																		class="fa-solid fa-rotate-right"></i>  Take Test Again</a>&nbsp;
																	</c:when>
																	<c:otherwise>
																	<a href="/exam/viewTestDetailsForStudents?id=${test.id}&message=openTestDetails" title="Details"><i
																		class="fa-solid fa-circle-info"></i>  View Details</a>&nbsp;
																		
																	</c:otherwise>
																	</c:choose>
																	
																	
																	</td>
																</tr>
															</c:forEach>


														</tbody>
													</table>
												</div>
								
								<!-- Code for page goes here end -->
								
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		
		<jsp:include page="../common/footer.jsp" />
     		<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
	<script src="/exam/assets/js/jquery-1.11.3.min.js"></script>
	<script src="/exam/assets/js/bootstrap.js"></script>
<script src="/exam/assets/js/jquery.tabledit.js"></script>

	<script src="/exam/resources_2015/js/vendor/jquery-ui.min.js"></script>
	<script
		src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
	<script src="/exam/resources_2015/js/vendor/dataTables.bootstrap.js"></script>
	<script
		src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js"></script>
        		
        		<script>
		
	$('.tables').DataTable( {
        initComplete: function () {
            this.api().columns().every( function () {
                var column = this;
                var headerText = $(column.header()).text();
                console.log("header :"+headerText);
                if(headerText == "Subject")
                {
                   var select = $('<select style="width:100%; margin-left:5px;" class="form-control"><option value="">All</option></select>')
                    .appendTo( $(column.header()) )
                    .on( 'change', function () {
                        var val = $.fn.dataTable.util.escapeRegex(
                            $(this).val()
                        );
 
                        column
                            .search( val ? '^'+val+'$' : '', true, false )
                            .draw();
                    } );
 
                column.data().unique().sort().each( function ( d, j ) {
                    select.append( '<option value="'+d+'">'+d+'</option>' )
                } );
              }
                
                if(headerText == "Month")
                {
                   var select = $('<select style="width:100%; margin-left:5px;" class="form-control"><option value="">All</option></select>')
                    .appendTo( $(column.header()) )
                    .on( 'change', function () {
                        var val = $.fn.dataTable.util.escapeRegex(
                            $(this).val()
                        );
 
                        column
                            .search( val ? '^'+val+'$' : '', true, false )
                            .draw();
                    } );
 
                column.data().unique().sort().each( function ( d, j ) {
                    select.append( '<option value="'+d+'">'+d+'</option>' )
                } );
              }
                
                if(headerText == "Year")
                {
                   var select = $('<select style="width:100%; margin-left:5px;" class="form-control"><option value="">All</option></select>')
                    .appendTo( $(column.header()) )
                    .on( 'change', function () {
                        var val = $.fn.dataTable.util.escapeRegex(
                            $(this).val()
                        );
 
                        column
                            .search( val ? '^'+val+'$' : '', true, false )
                            .draw();
                    } );
 
                column.data().unique().sort().each( function ( d, j ) {
                    select.append( '<option value="'+d+'">'+d+'</option>' )
                } );
              }
 	
                
                
 
            } );
        }
    } );
	
	</script>


    </body>


</html>