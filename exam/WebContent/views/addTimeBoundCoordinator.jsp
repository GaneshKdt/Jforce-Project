<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Add TimeBound Coordinator Mapping" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="header.jsp"%>
	<%
		boolean isEdit = "true".equals((String)request.getAttribute("edit"));
	%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
	       	<div class="row"> <legend>Add TimeBound Coordinator Mapping</legend></div>
	        <%@ include file="messages.jsp"%>
        	
        		<form:form modelAttribute="fileBean" method="post" 	enctype="multipart/form-data" action="addTimeBoundCoordinator">
				<div class="panel-body">
					<form:hidden path="timebound_subject_config_id" value="${TimeBoundSubjectConfigId}" />
					<form:hidden path="prgm_sem_subj_id" value="${prgm_sem_subj_id}"/>
					<div class="col-md-6 column">
						
						<%if(isEdit){ %>
							<form:input type="hidden" path="id" id="id" value="${fileBean.id}"/>
							<form:input type="hidden" path="timebound_subject_config_id" id="timebound_subject_config_id" 
										value="${fileBean.timebound_subject_config_id }"/>
							<form:input type="hidden" path="prgm_sem_subj_id" id="prgm_sem_subj_id" value="${fileBean.prgm_sem_subj_id}"/>
						
							<div class="form-group" >
								<form:select id="userId" path="userId" type="text" placeholder="Coordinator Id" class="form-control" 
											 required="required" itemValue="${fileBean.userId}" > 
									<form:option value="">Select Coordinator </form:option>
									<c:forEach items="${coordinatorList}" var="bean">
										<option value="${bean.key }"> ${bean.value}</option>
									</c:forEach>
								</form:select>
							</div>
						
						<%} else { %>

							<% for(int i = 0 ; i < 1 ; i++) {%>
						 
						 		<div class="form-group" style="overflow:visible;">
								<label for="Coordinator">Select Coordinator</label>
								<select data-id="userId" name="coordinators[<%=i%>].userId"  class="combobox form-control" >
									<option value="">Type OR Select CoordinatorId</option>
										<c:forEach items="${coordinatorList}" var="bean">
											<option value="${bean.key }"> ${bean.value}</option>
										</c:forEach>
								</select>
								</div>
							<%} %>
					 <%} %>
												
					</div>
				</div>
				
				<div class="row">
					<div class="col-md-6 column">
					
						<%if(!isEdit){ %>
							<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="addTimeBoundCoordinator">Upload</button>
						<% }else{ %>
							<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="editCoordinatorMapping">Update</button>
						<% } %>
							<button id="cancel" name="cancel" class="btn btn-danger" onClick="history.go(-1); return false;">Back</button>
					
					</div>	
				</div><br><br>
				
				</form:form>
				
				<%if(!isEdit){ %>
				<div class="clearfix"></div>
				<div class="column">
				<legend> &nbsp;Existing Coordinator Mapping Entries <font size="2px"> </font> </legend>
					<div class="table-responsive">
						<table class="table table-striped table-hover tables"
							style="font-size: 12px">
							<thead>
								<tr>
									<th>Sr. No</th>
									<!-- <th>Coordinator Id</th> -->
									<th>Coordinator Name</th>
									<th>Action</th>
								</tr>
							</thead>
							<tbody>

								<c:forEach var="coordinator" items="${coordinators}" varStatus="status">
									<tr value="${coordinator.timebound_subject_config_id}">
										<td><c:out value="${status.count}" /></td>
										<%-- <td><c:out value="${coordinator.userId}" /></td> --%>
										<td><c:out value="${coordinator.coordinatorName}" /></td>
										<td>
											<c:url value="editCoordinatorMappingForm" var="editurl">
												<c:param name="id" value="${coordinator.id}" />
												<c:param name="prgm_sem_subj_id" value="${coordinator.prgm_sem_subj_id }"></c:param>
											</c:url>
											<c:url value="deleteCoordinatorMapping" var="deleteurl">
												<c:param name="id" value="${coordinator.id}" />
												<c:param name="timeBoundId" value="${coordinator.timebound_subject_config_id}" />
												<c:param name="prgm_sem_subj_id" value="${coordinator.prgm_sem_subj_id }"></c:param>
											</c:url>
											
	 										<a href="${editurl}" title="Edit"><i class="fa-solid fa-pen-to-square fa-lg"></i></a>&nbsp;
						 					<a href="${deleteurl}" title="Delete" onclick="return confirm('Are you sure you want to delete this record?')">
						 						<i class="fa fa-trash-o fa-lg"></i>
						 					</a>
										
										</td>
										
									</tr>
								</c:forEach>

							</tbody>
						</table>
					</div>
				</div>
        		<% } %>
        </div>
      </section>
      
      	<br><br><jsp:include page="footer.jsp" />
</body>
</html>