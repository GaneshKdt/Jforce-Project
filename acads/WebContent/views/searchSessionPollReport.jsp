<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@page import="java.util.Map" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>

<jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Report for Session Polls" name="title" />
</jsp:include>

<body>


		<%@ include file="adminCommon/header.jsp"%>
		<div class="sz-main-content-wrapper">
			
			<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Academics;Report for Session Polls"
				name="breadcrumItems" />
			</jsp:include>
		
			<div class="sz-main-content menu-closed">
				<div class="sz-main-content-inner">
					<jsp:include page="adminCommon/left-sidebar.jsp">
						<jsp:param value="" name="activeMenu" />
					</jsp:include>
					
					<div class="sz-content-wrapper examsPage">
					<%@ include file="adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">
					
					<h2 class="red text-capitalize">Report for Session Polls</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%@ include file="adminCommon/messages.jsp"%>
							<form:form action="sessionPollReport" method="post"
								modelAttribute="searchBean">
								<fieldset style="width:70%;">
									
										<div class="col-md-6 column">
									
									
										<div class="form-group">
											<form:select id="year" path="year" required="required"
												class="form-control filterBy" itemValue="${searchBean.year}">
												<form:option value="">Select Academic Year</form:option>
												<form:options items="${yearList}" />
											</form:select>
										</div>
									
										<div class="form-group">
										<form:select id="month" path="month" type="text" placeholder="Month" class="form-control filterBy" style="width: 450px;" required="required" 
															itemValue="${content.month}" >
															<form:option value="">Select Academic Month</form:option>
											<form:options items="${monthList}" />
											</form:select>
										</div>
										
									
									
									
									<div class="form-group">
											<button id="submit" name="submit"
												class="btn btn-large btn-primary" formaction="sessionPollReport">Generate</button>
											<button id="cancel" name="cancel" class="btn btn-danger"
												formaction="home" >Cancel</button>

										</div>
									</div>
									
									
									<div class="col-md-4 column">	
										<div class="form-group" style="overflow:visible;">
												<form:select id="subject" path="subject" class="form-control  selectSubject filterBySubject" itemValue="${searchBean.subject}"> 
												<option disabled selected value="">Select Subject</option>
											</form:select>
										</div>
										
										
										<c:choose>
										<c:when test="${userId eq 'NMSCEMUADMIN01' ||  userId eq 'nmscemuadmin01'}">
											<div class="form-group">
											<form:input id="facultyId" path="facultyId" type="text"
												class=" form-control" value="${searchBean.facultyId}" placeholder="Enter Faculty Id"/>
											</div>
										</c:when>
										<c:otherwise>
											<form:input id="facultyId" path="facultyId" type="text"
												class=" form-control" value="${userId}" readonly="true"/>
										</c:otherwise>
										</c:choose>
									</div>
								
									
								</fieldset>
								
								<c:if test="${row_count > 0}">
								    <h5>${row_count} records Found</h5>
									<legend>  
										&nbsp;Session Poll Report &nbsp;<font size="2px"> <a
											href="downloadPollReport">Download to Excel</a></font>
									</legend>
									
									         <div class="column">
												<div class="table-responsive">
													<table class="table table-striped table-hover dataTables" style="font-size:12px" id="datafill">
													<thead>
														<tr>
															<th>Sr.No</th>
															<th>Acad Year</th>
															<th>Acad Month</th>
															<th>Date</th>
															<th>Start Time</th>
															<th>Subject</th>
															<th>Subject Code</th>
															<th>Session Name</th>
															<th>Poll Name</th>
															<th>Faculty Id</th>
															<th>Faculty Name</th>								
															<th>MeetingKey</th>
															<th>Zoom Id</th>
															<th>Track</th>
															<th>isLaunched</th>
															<th>Total no. of Quest</th>
														</tr>
													</thead>
							
													<tbody>
														<c:forEach var="bean" items="${sessionPollList }" varStatus="status">
														<tr>
															<td><c:out value="${status.count}" /></td>
															<td><c:out value="${bean.year }" /></td>
															<td><c:out value="${bean.month}" /></td>
															<td><c:out value="${bean.date }" /></td>
															<td><c:out value="${bean.startTime}" /></td>
															<td><c:out value="${bean.subject }" /></td>
															<td><c:out value="${bean.subjectcode }" /></td>
															<td><c:out value="${bean.sessionName }" /></td>
															<td><c:out value="${bean.title }" /></td>
															<td><c:out value="${bean.facultyIdPoll }" /></td>
															<td><c:out value="${bean.facultyName }" /></td>
															<td><c:out value="${bean.webinarId }" /></td>
															<td><c:out value="${bean.hostKey }" /></td>
															<td><c:out value="${bean.track }" /></td>
															<td><c:out value="${bean.isLaunched }" /></td>
															<td><c:out value="${bean.noofQuest }" /></td>
															
														</tr>
														</c:forEach>
													</tbody>
											</table>
										</div> 
									</div>
						
								</c:if>
								
							</form:form>
						</div>
						</div>
					</div>
					</div>
				</div>
			</div>
			<jsp:include page="adminCommon/footer.jsp" />

			<script>
			$(document).ready(function() {
			    $('#datafill').DataTable( {
			        "pagingType": "full_numbers"
			    } );
			} );

			$('.filterBy').on('change', function () {
			 	  
		  		 let options = "<option>Loading... </option>";

		 		
		  
		  		 var data ={
		  	  		    year : $('#year').val(),
		  	  		    month : $('#month').val(), 
						facultyId : $('#facultyId').val()
		  	  		 }
		  	  		
		  	  		 	$.ajax({
		  			   		type : "POST",
		  			   		contentType : "application/json; charset=utf-8",
		  			   		url : "/acads/admin/getSubjectNames", 
		  			  		data : JSON.stringify(data),
		  			  		
		  			  		success :  function(data) {
		  					  
		  					    if(data.length > 0){
		  					    options = "";
		  					    let allOption = "";
		  					    
		  					    for(let i=0;i < data.length;i++) {
		  					    	allOption = allOption + "'"+ data[i] +"',";
		  				      			options = options + "<option value=\"'" + data[i] + "'\"> " + data[i] +"</option>";
		  				      			
		  					    }
		  					    allOption = allOption.substring(0,allOption.length-1);
		  					    options = "<option selected value='"+ allOption +"'>All</option>" + options;


		  					    }else{
		  					    	 options = "<option selected value='No'>No Subject Found</option>" ;
		  						 }
		  						 $('.selectSubject').html(options);
		  					   },
		  			 		error : function(e) {
		  				 		
		  			 			alert("Please Refresh The Page.")
		  					    
		  				
		  			   		}
		  			  }).responseText;

		  	      


			});
					   	  	 
			</script>
			
			     
</body>
</html>