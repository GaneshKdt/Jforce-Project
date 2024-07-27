 <!DOCTYPE html>




<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>


<html lang="en">
    
<style>
.nav-tabs.large li a{
	padding: 0px;
}
.nav-tabs.large li h2{
	font-size: 20px!important; 
}
input{
color: black!important;
}
.table thead tr {
    box-shadow: none!important;
}
</style>
	
    
    <jsp:include page="common/jscss.jsp">
	<jsp:param value="Post a Query" name="title"/>
    </jsp:include>
    
    
    
    <body>
    
    	<%@ include file="common/header.jsp" %>
    	
    	
        
        <div class="sz-main-content-wrapper">
        
        	<%@ include file="common/breadcrum.jsp" %>
        	
        <%try{ %>    
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="common/left-sidebar.jsp">
								<jsp:param value="Academic Calendar" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="common/studentInfoBar.jsp" %>
              						
              						
              						<div class="sz-content">
								
										<h2 class="red text-capitalize">
											<c:if test="${action == 'postCourseQueries' }">
									        	Post ${subject} Related Query 
									        </c:if>
									        
									        <c:if test="${action == 'viewQueries' }">
									        	Queries for ${session.subject }-${session.sessionName }
									        </c:if>
										</h2>
										<div class="clearfix"></div>
										
										<%@ include file="common/messages.jsp" %>
										
										
											<div class="panel-content-wrapper">
											
											<form:form  action="postCourseQuery" method="post" modelAttribute="sessionQuery">
												<fieldset>
													<h2 style="margin-top:0px;">Post a Query</h2>
													<div class="clearfix"></div>
													
													<div class="form-group">
													<div class="row">
<!-- 													<div class="col-xs-2"> -->
<!-- 													<label for="faculty" s> Select Faculty : <span style="color: red"> * </span> </label> -->
<!-- 													</div> -->
													<div class="col-xs-8">
 													<form:select path="facultyId" id="facultyId" class="form-control" required="required" >
														<form:option value=""> Select Faculty</form:option> 
														<c:forEach var="faculty" items="${facultyForQuery}"> 																						
															
															<form:option value="${faculty.facultyId }"> Prof. ${faculty.firstName } ${faculty.lastName } </form:option> 												
														</c:forEach>
													</form:select>
													</div>
													</div>
													</div>
													
													<div class="clearfix"></div>
															<form:hidden path="subject" value="${subject}"/>
													<div class="form-group">
															<form:textarea path="query" id="query" maxlength="500" class="form-control" minlength="5" placeholder="Please note that only Course Content and/or Session related queries have to be posted in Post Query section which will be replied within 48 working hours. Enter query here..." cols="50" required="required" />
													</div>
														
													<div class=" form-group controls">
														<button id="submit" name="submit" class="btn btn-sm btn-primary" formaction="postCourseQuery" onClick="return confirm('Are you sure you want to submit this query?');">Post Query</button>
													</div>
									
												</fieldset>
											</form:form>
											</div>

											<div class="panel-body outer">
												<ul class="nav nav-tabs large">
													<li class="active"><a data-toggle="tab" href="#tab1">
															<h2 >My Queries</h2>
													</a></li>
													<li><a data-toggle="tab" href="#tab2">
															<h2 >Queries by other Students</h2>
													</a></li>
												</ul>
												<div class="tab-content" style="padding-top: 2rem;">
						        					<div id="tab1" class="tab-pane fade in active" >
												        
												        <c:if test="${empty myQueries}">
												        	<div class="nodata-toshow-div">
												        		<h4><i class="fa fa-exclamation-circle"></i> no Unanswered Q&A to show</h4>
												        	</div>
												        </c:if>
							                            <br></br>   
										          		<!-- My Queries section -->
														<c:if test="${not empty myQueries}">
															<table id="example1" class="table table-striped table-bordered" style="width:100%">
															        <thead>
															            <tr>
															           		<th>type</th>
															                <th>Question</th>
															                <th>Posted On</th>
															                <th>Answer</th>
															                <th>Answered On</th>
															            </tr>
															        </thead>
															        <tbody>
															        	<c:forEach var="queryAnswer" items="${myQueries}" varStatus="status">
																																	
															            <tr>
																            <td>
																             <c:if test="${not empty queryAnswer.queryType }"> ${queryAnswer.queryType}</c:if>
																            </td>
															                <td>
															                <h4 class="question" style="font-weight:normal;color:#000">Q. ${queryAnswer.query}</h4>
															                <c:if test="${queryAnswer.isAnswered == 'Y' }"><i class="fa fa-check-square" style="color:green; margin-left: 5px;"></i></c:if>
																			<c:if test="${queryAnswer.isAnswered == 'N' }"><i class="fa fa-hourglass-start" style="color:#d51540; margin-left: 5px;"></i></c:if>
																	
															                </td>
															                <td>
															                ${queryAnswer.createdDate}
															                </td>
															                <td>
															                 ${queryAnswer.answer}
															                </td>
															                <td>
															               <c:if test="${queryAnswer.isAnswered == 'Y' }">${queryAnswer.lastModifiedDate}</c:if>
															                </td>
															            </tr>
															            
															            </c:forEach>
															        </tbody>
														    </table>
																
														</c:if>
					        						</div>
											        <div id="tab2" class="tab-pane fade">
											        	
												        <c:if test="${empty publicQueries}"> 
												        <div class="nodata-toshow-div"><h4><i class="fa fa-exclamation-circle"></i> no answered Q&A to show</h4></div>
												        </c:if>
												        <br></br> 
														<!-- Public Queries section -->
														<c:if test="${not empty publicQueries}">
																<div class="clearfix"></div>
																
																
																<table id="example2" class="table table-striped table-bordered" style="width:100%">
															        <thead>
															            <tr>
															                <th>Question</th>
															                <th>Posted On</th>
															                <th>Answer</th>
															                <th>Answered On</th>
															            </tr>
															        </thead>
															        <tbody>
															        	<c:forEach var="queryAnswer" items="${publicQueries}" varStatus="status">
																																	
															            <tr>
															                <td>
															                <h4 class="question" style="font-weight:normal;color:#000">Q. ${queryAnswer.query}</h4>
															                </td>
															                <td>
															                ${queryAnswer.createdDate}
															                </td>
															                <td>
															                ${queryAnswer.answer}
															                </td>
															                <td>
															                <c:if test="${queryAnswer.isAnswered == 'Y' }">Answered on ${queryAnswer.lastModifiedDate}</c:if>
															                </td>
															            </tr>
															            
															            </c:forEach>
															        </tbody>
														    </table>
														</c:if> 
											        </div>
					      						</div>
											</div>

								<%}catch(Exception e){ %>
							e.printStackTrace();
							<%} %>
										
              		</div>
              	</div>
              </div>
            </div>
        </div>
            
  	
        <jsp:include page="common/footer.jsp"/>
        <script src="https://code.jquery.com/jquery-3.3.1.js"></script>
		<script src="https://cdn.datatables.net/1.10.20/js/jquery.dataTables.min.js"></script>
		<script src="https://cdn.datatables.net/1.10.20/js/dataTables.bootstrap.min.js"></script>
       <script>
	 	 $(document).ready(function() {
	 	    $('#example1').DataTable();
	 	   $('#example2').DataTable(); 
	 	} );
       </script>
    </body>
</html>