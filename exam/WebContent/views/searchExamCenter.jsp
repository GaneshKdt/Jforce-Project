<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Search Exam Centers" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
       <div class="row"> <legend>Search Exam Centers</legend></div>
        <%@ include file="messages.jsp"%>
		
		<form:form  action="searchExamCenter" method="post" modelAttribute="examCenter">
			<fieldset>
			<div class="panel-body clearfix">
			
			<div class="col-md-6 column">
					<div class="form-group">
						<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control"  itemValue="${examCenter.year}">
							<form:option value="">Select Exam Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" itemValue="${examCenter.month}">
							<form:option value="">Select Exam Month</form:option>
							<form:option value="Apr">Apr</form:option>
							<form:option value="Jun">Jun</form:option>
							<form:option value="Sep">Sep</form:option>
							<form:option value="Dec">Dec</form:option>
						</form:select>
					</div>
					
					<div class="form-group">
						<form:select id="mode" path="mode" type="text" placeholder="Exam Mode" class="form-control"  itemValue="${examCenter.mode}">
							<form:option value="">Select Exam Mode</form:option>
							<form:option value="Online">Online</form:option>
							<form:option value="Offline">Offline</form:option>
						</form:select>
					</div>
			</div>
			
			
			<div class="col-md-6 column">
					<div class="form-group">
							<form:input id="examCenterName" path="examCenterName" type="text" placeholder="Exam Center Name" class="form-control" value="${examCenter.examCenterName}"/>
					</div>
					
					<div class="form-group">
							<form:input id="city" path="city" type="text" placeholder="City" class="form-control"  value="${examCenter.city}"/>
					</div>
					
					<div class="form-group">
						<form:select id="state" path="state" placeholder="State" class="form-control"  itemValue="${examCenter.state}">
							<form:option value="">Select State</form:option>
							<form:options items="${stateList}" />
						</form:select>
					</div>
					
					<div class="form-group">
						<label class="control-label" for="submit"></label>
						<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="searchExamCenter">Search</button>
						<button id="reset" type="reset" class="btn btn-danger" type="reset">Reset</button>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="examCenterHome" formnovalidate="formnovalidate">Cancel</button>
					</div>
			</div>
			</div>
			</fieldset>
		</form:form>
		
		
	
	
	<c:choose>
	<c:when test="${rowCount > 0}">

	<h2>&nbsp;Exam Centers<font size="2px"> (${rowCount} Records Found) &nbsp; </h2></legend>
	<div class="panel-body">
		<div class="table-responsive">
		<table class="table table-striped table-hover" style="font-size:12px">
							<thead>
							<tr>
								<th>Sr. No.</th>
								<th>Year</th>
								<th>Month</th>
								<th>Exam Center Name</th>
								<th>Mode</th>
								<th>Locality</th>
								<th>City</th>
								<th>State</th>
								<th>Capacity</th>
								<th>Actions</th>
							</tr>
						</thead>
							<tbody>
							
							<c:forEach var="examCenter" items="${examCentersList}" varStatus="status">
						        <tr>
						            <td><c:out value="${status.count}" /></td>
						            <td><c:out value="${examCenter.year}" /></td>
						            <td><c:out value="${examCenter.month}" /></td>
									<td><c:out value="${examCenter.examCenterName}" /></td>
									<td><c:out value="${examCenter.mode}" /></td>
									<td><c:out value="${examCenter.locality}" /></td>
									<td><c:out value="${examCenter.city}" /></td>
									<td><c:out value="${examCenter.state}"/></td>
									<td><c:out value="${examCenter.capacity}"/></td>
									<td> 
							            <c:url value="editExamCenter" var="editurl">
										  <c:param name="centerId" value="${examCenter.centerId}" />
										</c:url>
										<c:url value="deleteExamCenter" var="deleteurl">
										  <c:param name="centerId" value="${examCenter.centerId}" />
										</c:url>
										<c:url value="viewExamCenterDetails" var="detailsUrl">
										  <c:param name="centerId" value="${examCenter.centerId}" />
										</c:url>
										<c:url value="viewExamCenterSlots" var="SlotsdetailsUrl">
										  <c:param name="centerId" value="${examCenter.centerId}" />
										   <c:param name="year" value="${examCenter.year}" />
										  <c:param name="month" value="${examCenter.month}" />
										</c:url>
										
										<a href="${detailsUrl}" title="Details"><i class="fa fa-info-circle fa-lg"></i></a>&nbsp;
										<c:if test="${examCenter.mode == 'Online'}">
											<a href="${SlotsdetailsUrl}" title="Slot Details/Change Capacity"><i class="fa fa-cog fa-lg fa-spin"></i></a>&nbsp;
										</c:if>
										
										<%if(roles.indexOf("Admin") != -1 ){ %>
										<a href="${editurl}" title="Edit"><i class="fa fa-pencil-square-o fa-lg"></i></a>&nbsp;
										<a href="${deleteurl}" title="Delete" onclick="return confirm('Are you sure you want to delete this record?')"><i class="fa fa-trash-o fa-lg"></i></a> 
										<%} %>
										
						            </td>
						            
						            
						        </tr>   
						    </c:forEach>
								
								
							</tbody>
						</table>
		</div>
	</div>
	<br>

</c:when>
</c:choose>
</div>
<c:url var="firstUrl" value="searchExamCenterPage?pageNo=1" />
<c:url var="lastUrl" value="searchExamCenterPage?pageNo=${page.totalPages}" />
<c:url var="prevUrl" value="searchExamCenterPage?pageNo=${page.currentIndex - 1}" />
<c:url var="nextUrl" value="searchExamCenterPage?pageNo=${page.currentIndex + 1}" />


<c:choose>
<c:when test="${page.totalPages > 1}">
<div align="center">
    <ul class="pagination">
        <c:choose>
            <c:when test="${page.currentIndex == 1}">
                <li class="disabled"><a href="#">&lt;&lt;</a></li>
                <li class="disabled"><a href="#">&lt;</a></li>
            </c:when>
            <c:otherwise>
                <li><a href="${firstUrl}">&lt;&lt;</a></li>
                <li><a href="${prevUrl}">&lt;</a></li>
            </c:otherwise>
        </c:choose>
        <c:forEach var="i" begin="${page.beginIndex}" end="${page.endIndex}">
            <c:url var="pageUrl" value="searchExamCenterPage?pageNo=${i}" />
            <c:choose>
                <c:when test="${i == page.currentIndex}">
                    <li class="active"><a href="${pageUrl}"><c:out value="${i}" /></a></li>
                </c:when>
                <c:otherwise>
                    <li><a href="${pageUrl}"><c:out value="${i}" /></a></li>
                </c:otherwise>
            </c:choose>
        </c:forEach>
        <c:choose>
            <c:when test="${page.currentIndex == page.totalPages}">
                <li class="disabled"><a href="#">&gt;</a></li>
                <li class="disabled"><a href="#">&gt;&gt;</a></li>
            </c:when>
            <c:otherwise>
                <li><a href="${nextUrl}">&gt;</a></li>
                <li><a href="${lastUrl}">&gt;&gt;</a></li>
            </c:otherwise>
        </c:choose>
    </ul>
</div>
</c:when>
</c:choose>


	</section>

	  <jsp:include page="footer.jsp" />


</body>
</html>
 --%>
 <!DOCTYPE html>
<html lang="en">
	
<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
    <jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Search Exam Centers" name="title"/>
    </jsp:include>
    <body>
    	<%@ include file="adminCommon/header.jsp" %>
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;Search Exam Centers" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="adminCommon/left-sidebar.jsp">
								<jsp:param value="" name="activeMenu"/>
							</jsp:include>
            	<div class="sz-content-wrapper examsPage">
   						<%@ include file="adminCommon/adminInfoBar.jsp" %>
   						<div class="sz-content">
						<h2 class="red text-capitalize">Search Exam Centers</h2>
						<div class="clearfix"></div>
							<div class="panel-content-wrapper" style="min-height:450px;">
								<%@ include file="adminCommon/messages.jsp" %>
										<form:form  action="searchExamCenter" method="post" modelAttribute="examCenter">
										<fieldset>
										<div class="col-md-4">
												<div class="form-group">
													<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control" required="true">
														<form:option value="">Select Exam Year</form:option>
														<form:options items="${yearList}" />
													</form:select>
												</div>
											
												<div class="form-group">
													<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" required="true">
														<form:option value="">Select Exam Month</form:option>
														<form:option value="Apr">Apr</form:option>
														<form:option value="Jun">Jun</form:option>
														<form:option value="Sep">Sep</form:option>
														<form:option value="Dec">Dec</form:option>
													</form:select>
												</div>
												
												<div class="form-group">
													<form:select id="mode" path="mode" type="text" placeholder="Exam Mode" class="form-control"  itemValue="${examCenter.mode}">
														<form:option value="">Select Exam Mode</form:option>
														<form:option value="Online">Online</form:option>
														<form:option value="Offline">Offline</form:option>
													</form:select>
												</div>
												<div class="form-group">
														<form:input id="examCenterName" path="examCenterName" type="text" placeholder="Exam Center Name" class="form-control" value="${examCenter.examCenterName}"/>
												</div>
												
												<div class="form-group">
														<form:input id="city" path="city" type="text" placeholder="City" class="form-control"  value="${examCenter.city}"/>
												</div>
												
												<div class="form-group">
													<form:select id="state" path="state" placeholder="State" class="form-control"  itemValue="${examCenter.state}">
														<form:option value="">Select State</form:option>
														<form:options items="${stateList}" />
													</form:select>
												</div>
												
												<div class="form-group">
													<label class="control-label" for="submit"></label>
													<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="searchExamCenter">Search</button>
													<button id="reset" type="reset" class="btn btn-danger" type="reset">Reset</button>
													<button id="cancel" name="cancel" class="btn btn-danger" formaction="examCenterHome" formnovalidate="formnovalidate">Cancel</button>
												</div>
										</div>
										
										</fieldset>
									</form:form>
									</div>
					<c:choose>
					<c:when test="${rowCount > 0}">
	<h2 style="margin-left:50px;">&nbsp;Exam Centers<font size="2px"> (${rowCount} Records Found)&nbsp;</font></h2>
	<div class="clearfix"></div>
		<div class="panel-content-wrapper">
		<div class="table-responsive">
	<table class="table table-striped table-hover" style="font-size:12px">
						<thead>
							<tr>
								<th>Sr. No.</th>
								<th>Year</th>
								<th>Month</th>
								<th>Exam Center Name</th>
								<th>Mode</th>
								<th>Locality</th>
								<th>City</th>
								<th>State</th>
								<th>Capacity</th>
								<th>Actions</th>
							</tr>
						</thead>
							<tbody>
							
							<c:forEach var="examCenter" items="${examCentersList}" varStatus="status">
						        <tr>
						            <td><c:out value="${status.count}" /></td>
						            <td><c:out value="${examCenter.year}" /></td>
						            <td><c:out value="${examCenter.month}" /></td>
									<td><c:out value="${examCenter.examCenterName}" /></td>
									<td><c:out value="${examCenter.mode}" /></td>
									<td><c:out value="${examCenter.locality}" /></td>
									<td><c:out value="${examCenter.city}" /></td>
									<td><c:out value="${examCenter.state}"/></td>
									<td><c:out value="${examCenter.capacity}"/></td>
									<td> 
							            <c:url value="editExamCenter" var="editurl">
										  <c:param name="centerId" value="${examCenter.centerId}" />
										   <c:param name="ic" value="${examCenter.ic}" />
										</c:url>
										<c:url value="deleteExamCenter" var="deleteurl">
										  <c:param name="centerId" value="${examCenter.centerId}" />
										  <c:param name="ic" value="${examCenter.ic}" />
										</c:url>
										<c:url value="viewExamCenterDetails" var="detailsUrl">
										  <c:param name="centerId" value="${examCenter.centerId}" />
										  <c:param name="ic" value="${examCenter.ic}" />
										</c:url>
										<c:url value="viewExamCenterSlots" var="SlotsdetailsUrl">
										  <c:param name="centerId" value="${examCenter.centerId}" />
										   <c:param name="year" value="${examCenter.year}" />
										  <c:param name="month" value="${examCenter.month}" />
										  <c:param name="ic" value="${examCenter.ic}" />
										</c:url>
										
										<a href="${detailsUrl}" title="Details"><i class="fa-solid fa-circle-info fa-lg"></i></a>&nbsp;
										<c:if test="${examCenter.mode == 'Online'}">
											<a href="${SlotsdetailsUrl}" title="Slot Details/Change Capacity"><i class="fa-solid fa-gear fa-lg fa-spin"></i></a>&nbsp;
										</c:if>
										
										<%if(roles.indexOf("Admin") != -1 ){ %>
										<a href="${editurl}" title="Edit"><i class="fa fa-pencil-square-o fa-lg"></i></a>&nbsp;
										<a href="${deleteurl}" title="Delete" onclick="return confirm('Are you sure you want to delete this record?')"><i class="fa-regular fa-trash-can fa-lg"></i></a> 
										<%} %>
						            </td>
						        </tr>   
						    </c:forEach>
						</tbody>
					</table>
				</div>
				</div>
				<br>
				</c:when>
					</c:choose>
										<c:url var="firstUrl" value="searchExamCenterPage?pageNo=1" />
										<c:url var="lastUrl" value="searchExamCenterPage?pageNo=${page.totalPages}" />
										<c:url var="prevUrl" value="searchExamCenterPage?pageNo=${page.currentIndex - 1}" />
										<c:url var="nextUrl" value="searchExamCenterPage?pageNo=${page.currentIndex + 1}" />
						<c:choose>
						<c:when test="${page.totalPages > 1}">
						<div align="center">
							<ul class="pagination">
								<c:choose>
									<c:when test="${page.currentIndex == 1}">
										<li class="disabled"><a href="#">&lt;&lt;</a></li>
										<li class="disabled"><a href="#">&lt;</a></li>
									</c:when>
									<c:otherwise>
										<li><a href="${firstUrl}">&lt;&lt;</a></li>
										<li><a href="${prevUrl}">&lt;</a></li>
									</c:otherwise>
								</c:choose>
								<c:forEach var="i" begin="${page.beginIndex}" end="${page.endIndex}">
									<c:url var="pageUrl" value="searchExamCenterPage?pageNo=${i}" />
									<c:choose>
										<c:when test="${i == page.currentIndex}">
											<li class="active"><a href="${pageUrl}"><c:out value="${i}" /></a></li>
										</c:when>
										<c:otherwise>
											<li><a href="${pageUrl}"><c:out value="${i}" /></a></li>
										</c:otherwise>
									</c:choose>
								</c:forEach>
								<c:choose>
									<c:when test="${page.currentIndex == page.totalPages}">
										<li class="disabled"><a href="#">&gt;</a></li>
										<li class="disabled"><a href="#">&gt;&gt;</a></li>
									</c:when>
									<c:otherwise>
										<li><a href="${nextUrl}">&gt;</a></li>
										<li><a href="${lastUrl}">&gt;&gt;</a></li>
									</c:otherwise>
								</c:choose>
							</ul>
						</div>
							</c:when>
						</c:choose>

							</div>
              			</div>
    				</div>
			   </div>
		    </div>
        <jsp:include page="adminCommon/footer.jsp"/>
        
		
    </body>
</html>
 
 
 