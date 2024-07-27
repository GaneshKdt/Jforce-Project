
<!DOCTYPE html>
<html lang="en">
	
<%@page import="com.nmims.beans.Person"%>
  
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
  
    <jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Evaluate Internal Assessment Answers" name="title"/>
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
    	.evaluateQuestionsButton {
		   position:relative;
		}
		.evaluateQuestionsButton[data-badge]:after {
		   content:attr(data-badge);
		   position:absolute;
		   top:-10px;
		   right:-10px;
		   font-size:.7em;
		   background: #D2232A ;
		   color:white;
		   width:18px;height:18px;
		   text-align:center;
		   line-height:18px;
		   border-radius:50%;
		   box-shadow:0 0 1px #333;
		   border : 2px solid white;
		}
		
		.badge-button {
		   position:relative;
		}
		.badge-button[data-badge]:after {
		   content:attr(data-badge);
		   position:absolute;
		   z-index: 1;		   
		   top:-10px;
		   right:-10px;
		   font-size:.7em;
		   background: #D2232A ;
		   color:white;
		   width:20px;
		   height:20px;
		   text-align:center;
		   line-height:18px;
		   border-radius:50%;
		   box-shadow:0 0 1px #333;
		   border : 2px solid white;
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
			        		<li><a href="/exam/mbax/ia/a/viewAllTests">Internal Assessments</a></li>
			        		<li><a href="/exam/mbax/ia/a/viewTestDetails?id=${test.id}">Internal Assessment Details</a></li>
			        		<li><a href="#">Copy Cases</a></li>
			        	
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
								
											<h2 class="red text-capitalize">Copy Cases For ${test.testName } </h2>
											<div class="clearfix"></div>
							<div class="panel-content-wrapper" style="min-height:450px;">
								<%@ include file="../adminCommon/messages.jsp" %>
							
							<!-- Code For Page Goes in Here Start -->
							<% try{ %>
																<a href="/exam/mbax/ia/a/downloadCopyCasesByTestId?testId=${test.id}"
																	class="btn btn-primary"
																	target="_blank"
																	style="float:right;"
																> Download Copy Case Report</a>
																<div class="table-responsive">
																	<table class="table table-striped table-hover tables"
																		style="font-size: 12px">
																		<thead>
																			<tr>
																				<th>Sr. No.</th>
																				<th>Sapid1  </th>
																				<th>Sapid2  </th>
																				<th>Match Percentage</th>

																				<th>Actions</th>
																				<th></th>
																			</tr>
																		</thead>
																		<tbody>

																			<c:forEach var="copyCase" items="${copyCases}"
																				varStatus="status">
																				<tr>
																					<td><c:out value="${status.count}" /></td>
																					<td><c:out value="${copyCase.sapId1}" /> (<c:out value="${copyCase.firstName1}" /> <c:out value="${copyCase.lastName1}" /> - Status: ${copyCase.sapid1AttemptStatus} ) </td>
																					<td><c:out value="${copyCase.sapId2}" /> (<c:out value="${copyCase.firstName2}" /> <c:out value="${copyCase.lastName2}" />  - Status: ${copyCase.sapid2AttemptStatus} ) </td>
																					<td><c:out value="${copyCase.matching}" /></td>

																					<td>
																						
																					</td>
																					<td>
																					<button type="button" class="btn btn-info" data-toggle="collapse" data-target="#demo-${status.count}">View details</button>
																					
																					</td>
																				</tr>
																				<tr>
																					<td colspan="7" >
																					 
																					  <div id="demo-${status.count}" class="collapse">
																					    <div class="container" >
																					    	<div class="row" >
																					    		<div class="col-xs-6"  style="border-right:2px solid grey">
																					    			<div class="row" >
																							    		<div class="col-xs-12" >
																							    			Sapid1 : ${copyCase.sapId1}
																							    		</div>
																							    		<div class="col-xs-12" >
																							    			FirstName : ${copyCase.firstName1}
																							    		</div>
																							    		<div class="col-xs-12" >
																							    			LastName : ${copyCase.lastName1}
																							    		</div>
																							    		<div class="col-xs-12" >
																							    			Copy Case :  
																											<c:choose>
																											 <c:when test="${copyCase.sapid1AttemptStatus == 'CopyCase' }">
																												<a href="/exam/mbax/ia/a/unMarkIATestsCopyCaseFromAdminView?testId=${test.id}&sapid=${copyCase.sapId1}&attempt=${copyCase.attempt}&questionId=${copyCase.questionId}&markedForCopyCase='${copyCase.sapId2} Marked For Copy Case. '"
																											 		onclick="return confirm('Remove CopyCase Status of ${copyCasesapId1} ,Are you sure?')"
																											 		title="Unmark Copy Case Status"
																													class="btn btn-primary"
																											>
																											Unmark From Copycase
																											
																											</a>
																											 </c:when>
																											 <c:otherwise>
																													<a href="/exam/mbax/ia/a/markIATestsCopyCaseFromAdminView?testId=${test.id}&sapid=${copyCase.sapId1}&attempt=${copyCase.attempt}&questionId=${copyCase.questionId}&markedForCopyCase='${copyCase.sapId1} Marked For Copy Case. '"
																											 onclick="return confirm('Mark CopyCase Status  of ${copyCase.sapId1} ,Are you sure?')"
																											 title="Mark Copy Case Status"
																											 class="btn btn-danger"
																											>
																												Mark For CopyCase
																											</a>
																											 </c:otherwise>
																											</c:choose>
																											
																							    		</div>
																							    		<div class="col-xs-12" >
																							    			 <br>Answer : <br> <b style="white-space: pre-wrap;" >${copyCase.firstTestDescriptiveAnswer}</b>
																							    		</div>
																							    		
																							    	</div>
																					    		</div>
																					    		<div class="col-xs-6" >
																					    			<div class="row" >
																							    		<div class="col-xs-12" >
																							    			Sapid2 : ${copyCase.sapId2}
																							    		</div>
																							    		<div class="col-xs-12" >
																							    			FirstName : ${copyCase.firstName2}
																							    		</div>
																							    		<div class="col-xs-12" >
																							    			LastName : ${copyCase.lastName2}
																							    		</div>
																							    		<div class="col-xs-12" >
																							    			Copy Case : 
																											<c:choose>
																											 <c:when test="${copyCase.sapid2AttemptStatus == 'CopyCase' }">
																												<a href="/exam/mbax/ia/a/unMarkIATestsCopyCaseFromAdminView?testId=${test.id}&sapid=${copyCase.sapId2}&attempt=${copyCase.attempt}&questionId=${copyCase.questionId}&markedForCopyCase='${copyCase.sapId1} Marked For Copy Case. '"
																											 		onclick="return confirm('Remove CopyCase Status of ${copyCasesapId2} ,Are you sure?')"
																											 		title="Unmark Copy Case Status"
																													class="btn btn-primary"
																											>
																												Unmark From Copycase
																											</a>
																											 </c:when>
																											 <c:otherwise>
																													<a href="/exam/mbax/ia/a/markIATestsCopyCaseFromAdminView?testId=${test.id}&sapid=${copyCase.sapId2}&attempt=${copyCase.attempt}&questionId=${copyCase.questionId}&markedForCopyCase='${copyCase.sapId2} Marked For Copy Case. '"
																											 onclick="return confirm('Mark CopyCase Status  of ${copyCase.sapId2} ,Are you sure?')"
																											 title="Mark Copy Case Status"
																											  class="btn btn-danger"
																											>
																												Mark For Copycase.
																											</a>
																											 </c:otherwise>
																											</c:choose>
																											
																							    		</div>
																							    		<div class="col-xs-12" >
																							    			Answer : <br> <b style="white-space: pre-wrap;" >${copyCase.secondTestDescriptiveAnswer}</b>
																							    		</div>
																							    		
																							    	</div>
																					    
																					    		</div>
																					    	</div>
																					    	
																					    	<div class="row" >
																								<div class="col-xs-12" style="text-align: center;" >
																									<a href="/exam/mbax/ia/a/markBothStudentsForIATestsCopyCaseFromAdminView?testId=${test.id}&sapids=${copyCase.sapId1}~${copyCase.sapId2}&attempt=${copyCase.attempt}&questionId=${copyCase.questionId}&markedForCopyCase='${copyCase.sapId2} Marked For Copy Case. '"
																									 onclick="return confirm('Mark CopyCase Status ${copyCase.sapId1} and  ${copyCase.sapId2} ,Are you sure?')"
																									 style="text-align: center;" class="btn btn-warning"
																									>Mark Both Students For Copy Case</a>
																								</div>
																							</div>
																					    	
																					    </div>
																					  </div>
																					</td>
																				
																				</tr>
																				
																			</c:forEach>


																		</tbody>
																	</table>
																</div>	
							<%
							}catch(Exception e ){
								
							}
							%>	
							<!-- Code For Page Goes in Here End -->
							</div>
							
							</div>
              			</div>
    				</div>
			   </div>
		    </div>
        <jsp:include page="../adminCommon/footer.jsp"/>
        
		<script>
		
	$('.tables').DataTable( {
        initComplete: function () {
            this.api().columns().every( function () {
                var column = this;
                var headerText = $(column.header()).text();
                console.log("header :"+headerText);
                if(headerText == "Status")
                {
                   var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
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