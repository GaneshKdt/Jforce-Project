 <!DOCTYPE html>
<%@page import="java.util.*"%>
<%@page import="java.text.DateFormat"%>
<html lang="en">
<!--  -->
<style>
.sz-content-wrapper .sz-content {
    padding: 30px 15px 0 15px;
}
</style>
<%@page import="com.nmims.beans.Page"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

    <jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="View TCS Data" name="title"/>
    </jsp:include>
    <body>
    	<%@ include file="../adminCommon/header.jsp" %>
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="TCS Exam Data;Marks" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                <div class="sz-main-content-inner">
					<jsp:include page="../adminCommon/left-sidebar.jsp">
						<jsp:param value="" name="activeMenu"/>
					</jsp:include>
					<div class="sz-content-wrapper examsPage">
   						
   						<div class="sz-content">
							<h2 class="red text-capitalize">View TCS Marks </h2>
							<div class="clearfix"></div>
							<div class="panel-content-wrapper" style="min-height:450px;">
								<%@ include file="../adminCommon/messages.jsp" %>
								<form:form  action="tcsData" method="post" modelAttribute="tcsMarksBean">
									<fieldset>
										<div class="col-md-6 column">
											<div class="form-group">
												<form:select id="year" path="year" type="text" required="required"	placeholder="Exam Year" class="form-control"   itemValue="${tcsMarksBean.year}">
													<form:option value="">(*) Select Exam Year</form:option>
														<form:options items="${yearList}"/>
												</form:select>
											</div>	
											<div class="form-group">
												<form:select id="month" path="month" type="text" required="required" placeholder="Exam Month" class="form-control"  itemValue="${tcsMarksBean.month}">
													<form:option value="">(*) Select Exam Month</form:option>
													<form:options items="${monthList}"/>
												</form:select>
											</div>
											<div class="form-group">
												<form:select id="studentType" path="studentType" type="text"  placeholder="Students Type" class="form-control"  itemValue="${sifyMarksBean.studentType}">
													<form:option value="">Select Students Type</form:option>
													<form:options items="${studentTypeList}"/>
												</form:select>
											</div>
											<div class="form-group">
												<form:select id="subject" path="subject" type="text"  placeholder="Subject Name" class="form-control"  itemValue="${tcsMarksBean.subject}">
													<form:option value="">Select Subject</form:option>
													<form:options items="${subjectList}"/>
												</form:select>
											</div>
											<div class="form-group">
												<form:select id="subjectId" path="subjectId" type="text"  placeholder="Subject Code" class="form-control" >
													<form:option value="0">Select Subject Code</form:option>
													<form:options items="${subjectCodeList}"/>
												</form:select>
											</div>
											<div class="form-group">
												<button id="submit" name="submit" class="btn btn-large btn-primary"
													formaction="tcsData">Generate</button>	
											<c:if test="${rowCount > 0}">	
											<div>
												<button id="submit" name="submit" class="btn btn-large btn-primary"
													formaction="tcsSummaryReport">Generate Summary Report</button>
												<button id="submit" name="submit" class="btn btn-large btn-primary"
													formaction="tcsMarksReport">Generate Marks Report</button>
											</div>			
											</c:if>	
												<button id="cancel" name="cancel" class="btn btn-danger" 
													formaction="home" formnovalidate="formnovalidate">Cancel</button>
											</div>
										</div>
										<div class="clearfix"></div>
										<c:if test="${rowCount > 0}">								 
										<div class="column">
											<legend>&nbsp;TCS Marks Entries<font size="2px">  </font></legend>
											<div class="table-responsive">
												<table class="table table-striped table-hover tables" style="font-size: 12px">
													<thead>
														<tr>
															<th>Sr.No.</th>
															<th>SapId</th>
															<th>Name</th>
															<th>Exam Year</th>
															<th>Exam Month</th>
															<th>Exam Date</th>
															<th>Subject</th>
															<th>Part1 Marks</th>
															<th>Part2 Marks</th>
															<th>Part3 Marks</th>
															<th>Part4 Marks</th>
															<th>Total Marks</th>
														</tr>
													</thead>
													<tbody>
														<c:forEach var="tcsMarksList" items="${tcsMarksList}"   varStatus="status">
														<tr>
															<td><c:out value="${status.count}" /></td>
															<td><c:out value="${tcsMarksList.sapid}" /></td>
															<td><c:out value="${tcsMarksList.name}" /></td>
															<td><c:out value="${tcsMarksList.year}" /></td>
															<td><c:out value="${tcsMarksList.month}" /></td>
															<td><c:out value="${tcsMarksList.examDate}" /></td>
															<td><c:out value="${tcsMarksList.subject}" /></td>
															<td><c:out value="${tcsMarksList.sectionOneMarks}" /></td>	
															<td><c:out value="${tcsMarksList.sectionTwoMarks}" /></td>
															<td><c:out value="${tcsMarksList.sectionThreeMarks}" /></td>
															<td><c:out value="${tcsMarksList.sectionFourMarks}" /></td>
															<td><c:out value="${tcsMarksList.totalScore}" /></td>
														</tr>   
														</c:forEach>
															
													</tbody>
												</table>
											</div>	
										</div> 
										</c:if>
									</fieldset>
								</form:form>	
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<jsp:include page="../adminCommon/footer.jsp"/>
    </body>


   <!-- jQuery (necessary for Bootstrap's JavaScript plugins) --> 
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery-1.11.3.min.js"></script> 
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/bootstrap.js"></script> 
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/jquery-ui.min.js"></script>  
	<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js" ></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
	<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js" ></script>
	<script>
		$('.tables').DataTable( {
			initComplete: function () {
				this.api().columns().every( function () {
					var column = this;
					var headerText = $(column.header()).text();
					console.log("header :"+headerText);
					if(headerText == "Subject")
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
</html>
 