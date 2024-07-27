 <!DOCTYPE html>
<html lang="en">
	
<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.OnlineExamMarksBean"%>


<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
    <jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Search Students" name="title"/>
    </jsp:include>
    
    
    
    <body>
    
    	<%@ include file="adminCommon/header.jsp" %>
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;Search Students" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="adminCommon/left-sidebar.jsp">
								<jsp:param value="" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="adminCommon/adminInfoBar.jsp" %>
              						<div class="sz-content">
								
											<h2 class="red text-capitalize">Exam Booking Conflict</h2>
											<div class="clearfix"></div>
											<div class="panel-content-wrapper" style="min-height:450px;">
											<%@ include file="adminCommon/messages.jsp" %>
									<form:form  action="searchExamBookingConflictForm" method="post" modelAttribute="transaction">
										<fieldset>
								<div class="col-md-4">

									<div class="form-group">
										<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control"   required="true" >
											<form:option value="">Select Year</form:option>
											<form:options items="${yearList}" />
										</form:select>
									</div>
								
									<div class="form-group">
										<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" required="true" >
											<form:option value="">Select Month</form:option>
											<form:option value="Jan">Jan</form:option>
											<form:option value="Apr">Apr</form:option>
											<form:option value="Jun">Jun</form:option>
											<form:option value="Sep">Sep</form:option>
											<form:option value="Dec">Dec</form:option>
										</form:select>
									</div>
									
									
											
									<div class="form-group">
											<form:input id="sapid" path="sapid" type="text" placeholder="SAP ID" class="form-control" value="${transaction.sapid}"/>
									</div>
									
										<div class="form-group">
											<label class="control-label" for="button"></label>
											<button id="button" name="button" class="btn btn-large btn-primary" formaction="searchConflictTransaction">Search</button>
												
												<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
										</div>
										</div>
										</fieldset>
								</form:form>
							</div>
							
			
								<c:choose>
								<c:when test="${rowCount > 0}">

									<h2>&nbsp;Student Records <font size="2px"> (${rowCount} Records Found)<!-- &nbsp; <a href="downloadTransactions">Download to Excel</a> --></font></h2>
									<div class="clearfix"></div>
										<div class="panel-content-wrapper">
										<div class="table-responsive">
									<div class="col-sm-12">
												<table class="table table-striped table-hover tables" style="font-size:12px">
										<thead>
											<tr> 
												<th>Sr. No.</th>
												<th>SAP ID</th>
												<th>Email</th>
												<th>Mobile</th>
												<th>Year</th>
												<th>Month</th>
												<th>TrackId</th>
												<th>Amount</th>
												<th>Actions</th>
											</tr>
										</thead>
									<tbody>
						
												<c:forEach var="transaction" items="${transactionList}" varStatus="status">
													<tr>
														<td><c:out value="${status.count}"/></td>
														<td><c:out value="${transaction.sapid}"/></td>
														<td><c:out value="${transaction.emailId}"/></td>
														<td><c:out value="${transaction.mobile}"/></td>
														<td><c:out value="${transaction.year}"/></td>
														<td><c:out value="${transaction.month}"/></td>
														<td><c:out value="${transaction.trackId}"/></td>
														<td><c:out value="${transaction.amount}"/></td>
														<td><c:out value="${transaction.action}"/></td>
														
													
														
													</tr>   
												</c:forEach>
											</tbody>
										</table>
										</div>
									</div>
								</div>
							<br>
										</c:when>
								</c:choose>
				

								
							</div>
              			</div>
    				</div>
			   </div>
		    </div>
        <jsp:include page="adminCommon/footer.jsp"/>
        
		
    </body>
    <script>
    $(document).ready(function(){
   	 
    	
		$('.tables').DataTable( {

	        initComplete: function () {
	        	 this.api().columns().every( function () {
	               var column = this;
	                var headerText = $(column.header()).text();
	                console.log("header :"+headerText);
	         
	              
	                column.data().unique().sort().each( function ( d, j ) {
	                    select.append( '<option value="'+d+'">'+d+'</option>' )
	                } );
	             
	            } );
	        } 
	    } );
 });
    
    
    </script>
    
</html>