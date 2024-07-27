<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->


<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@page import="java.util.*"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Report for Date wise Collections" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">

			<div class="row"><legend>Report for Date wise Collections</legend></div>
			
			<%@ include file="messages.jsp"%>

			<%
			Map<String, Double> sortedDateAmountMap = (Map<String, Double>) session.getAttribute("sortedDateAmountMap");
			%>
			<div class="row clearfix">
			<form:form  action="/exam/admin/cumulativeFinanceReport" method="post" modelAttribute="studentMarks">
			<fieldset>
			<div class="col-md-6 column">

					<div class="form-group">
						<form:select id="writtenYear" path="year" type="text" required="required"	placeholder="Written Year" class="form-control"   itemValue="${studentMarks.year}">
							<form:option value="">Select Exam Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
					
					<div class="form-group">
						<form:select id="writtenMonth" path="month" type="text" required="required" placeholder="Written Month" class="form-control"  itemValue="${studentMarks.month}">
							<form:option value="">Select Exam Month</form:option>
							<form:option value="Apr">Apr</form:option>
							<form:option value="Jun">Jun</form:option>
							<form:option value="Sep">Sep</form:option>
							<form:option value="Dec">Dec</form:option>
						</form:select>
					</div>
					
					<div class="form-group">
					<button id="submit" name="submit" class="btn btn-large btn-primary"
						formaction="/exam/admin/cumulativeFinanceReport">Generate</button>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
					</div>

				</div>
				
				</fieldset>
				</form:form>
				
		</div>

		<c:if test="${rowCount > 0}">
			<legend>&nbsp;Date wise Collections Report<font size="2px"> (${rowCount} Records Found) &nbsp; <a href="/exam/admin/downloadCumulativeFinanceReport">Download to Excel</a></font></legend>
			
			<div class="table-responsive">
			<table class="table table-striped" style="font-size:12px">
				<thead>
					<tr> 
						<th>Sr. No.</th>
						<th>Date</th>
						<th>Amount</th>
					</tr>
				</thead>
				<tbody>
			
			<%
			
			int rowNum = 1;
			double totalAmount = 0;
			for (Iterator i = sortedDateAmountMap.keySet().iterator(); i.hasNext(); ) {

				String date = (String)i.next();
				double amount = (Double)sortedDateAmountMap.get(date);
				totalAmount += amount;
			%>
			
			<tr>
			<td><%=rowNum++ %></td>
			<td><%=date %></td>
			<td><%=amount %></td>
			</tr>
			
			<%} %>
			
			<tr>
			<td></td>
			<td></td>
			<td><b>Total: <%=totalAmount %></b></td>
			</tr>
			
			</tbody>
			</table>
		</div>
		</c:if>
	</section>

	<jsp:include page="footer.jsp" />


</body>
</html>
 --%>
 
 <!DOCTYPE html>
<html lang="en">
	
	
	<%@page import="com.nmims.beans.ExamCenterSlotMappingBean"%>
   <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
   <%@page import="java.util.*"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%> 
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>



    <jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Report for Pending Exam Bookings" name="title"/>
    </jsp:include>
    
    <link
	href="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.6-rc.0/css/select2.min.css"
	rel="stylesheet" />
    
    
    <body>
    
    	<%@ include file="adminCommon/header.jsp" %>
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;Report for Date wise Collections" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="adminCommon/left-sidebar.jsp">
								<jsp:param value="" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage" style="min-height:900px;">
              						<%@ include file="adminCommon/adminInfoBar.jsp" %>
              						<div class="sz-content">
								
											<h2 class="red text-capitalize">Report for Date wise Collections</h2>
											<div class="clearfix"></div>
								<div class="panel-content-wrapper" >
								<%@include file="adminCommon/messages.jsp" %>
								<%
							Map<String, Double> sortedDateAmountMap = (Map<String, Double>) session.getAttribute("sortedDateAmountMap");
							%>
													<form:form  action="/exam/admin/cumulativeFinanceReport" method="post" modelAttribute="studentMarks">
														<fieldset>
														<div class="col-md-4">

																<div class="form-group">
																	<form:select id="writtenYear" path="year" type="text" 	placeholder="Written Year" class="form-control"   itemValue="${studentMarks.year}">
																		<form:option value="">Select Exam Year</form:option>
																		<form:options items="${yearList}" />
																	</form:select>
																</div>
																
																<div class="form-group">
																	<form:select id="writtenMonth" path="month" type="text"  placeholder="Written Month" class="form-control"  itemValue="${studentMarks.month}">
																		<form:option value="">Select Exam Month</form:option>
																		<form:option value="Jan">January</form:option>
																		<form:option value="Feb">February</form:option>
																		<form:option value="Mar">March</form:option>
																		<form:option value="Apr">April</form:option>
																		<form:option value="May">May</form:option>
																		<form:option value="Jun">June</form:option>
																		<form:option value="Jul">July</form:option>
																		<form:option value="Aug">August</form:option>
																		<form:option value="Sep">September</form:option>
																		<form:option value="Oct">October</form:option>
																		<form:option value="Nov">November</form:option>
																		<form:option value="Dec">December</form:option>
																	</form:select>
																</div>
																<div class="form-group">
																	<label for=startDate>StartDate</label>
																	<form:input id="startDate" path="startDate" type="date"
																		data-placeholder="Start Date" class="form-control" itemValue="${studentMarks.startDate}"/>
																</div>
						
																<div class="form-group">
																	<label for=endDate>EndDate</label>
																	<form:input id="endDate" path="endDate" type="date"
																		data-placeholder="End Date" class="form-control" itemValue="${studentMarks.endDate}"/>
																</div>
															<%if(!"academic.admin".equalsIgnoreCase((String)session.getAttribute("userId"))){ %>
																<div class="form-group">
																	<label for=lc_list>Select LC</label> <br>
																	<form:select id="lc" path="lc_list" type="text"
																		class="js-lc-basic-multiple form-control"
																		multiple="multiple" placeholder="Select LC"
																		style="width: 100%" itemValue="${studentMarks.lc_list}">
																		<%-- <form:option value="">Select LC</form:option> --%>
																		<%-- 	<form:option value="Other"> Other </form:option> --%>
																		<form:options items="${lclist}"></form:options>
																	</form:select>
																</div>
																<div class="form-group">
																	<label for=ic_list>Select IC</label> <br>
																	<form:select id="ic" path="ic_list" type="text"
																		class="js-ic-basic-multiple form-control"
																		multiple="multiple" data-placeholder="Select IC"
																		style="width: 100%" itemValue="${studentMarks.ic_list}">
																		<%-- <form:option value="">Select IC</form:option> --%>
																		<%-- <form:option value="Other"> Other </form:option> --%>
																		<form:options items="${iclist}"></form:options>
																	</form:select>
																</div>
																<% } %>
																<div class="form-group">
																	<label for=programType>Select Program Type</label> <br>
																	<form:select id="programType" path="programType" type="text"
																		class="form-control" style="width: 100%" itemValue="${studentMarks.programType}">
																		<form:option value="">Program Type</form:option>
																		<form:option value="PG">PG (includes Cert,Diploma)</form:option>
																		<form:option value="MBA - WX">MBAwx</form:option>
																		<form:option value="MBA - X">MBAx</form:option>
																	</form:select>
																</div>
																<div class="form-group">
																	<label for=paymentOption>Select Gateway</label> <br>
																	<form:select id="paymentOption" path="paymentOption"
																		type="text" class="form-control" itemValue="${studentMarks.paymentOption}">
																		<form:option value="">Select Payment Gateway</form:option>
																		<%-- <form:option value="">All</form:option> --%>
																		<form:options items="${paymentOptions}"></form:options>
																	</form:select>
																</div>
																<div class="form-group">
																<button id="submit" name="submit" class="btn btn-large btn-primary"
																	formaction="/exam/admin/cumulativeFinanceReport">Generate</button>
																	<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
																</div>

															</div>
															
															</fieldset>
														</form:form>
								</div>
								<c:if test="${rowCount > 0}">
			<h2>&nbsp;Date wise Collections Report<font size="2px"> (${rowCount} Records Found) &nbsp; <a href="/exam/admin/downloadCumulativeFinanceReport" style="color:blue;">Download to Excel</a></font></h2>
			<div class="clearfix"></div>
			<div class="panel-content-wrapper">
			<div class="table-responsive">
			<table class="table table-striped table-hover" style="font-size:12px">
				<thead>
					<tr> 
						<th>Sr. No.</th>
						<th>Date</th>
						<th>Amount</th>
					</tr>
				</thead>
				<tbody>
			
			<%
			
			int rowNum = 1;
			double totalAmount = 0;
			for (Iterator i = sortedDateAmountMap.keySet().iterator(); i.hasNext(); ) {

				String date = (String)i.next();
				double amount = (Double)sortedDateAmountMap.get(date);
				totalAmount += amount;
			%>
			
			<tr>
			<td><%=rowNum++ %></td>
			<td><%=date %></td>
			<td><%=amount %></td>
			</tr>
			
			<%} %>
			
			<tr>
			<td></td>
			<td></td>
			<td><b>Total: <%=totalAmount %></b></td>
			</tr>
			
			</tbody>
			</table>
		</div>
		</div>
		</c:if>
	          						</div>
              				   </div>
    				       </div>
			           </div>
		           </div>
        <jsp:include page="adminCommon/footer.jsp"/>
        <script type="text/javascript"
		src="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.6-rc.0/js/select2.min.js"></script>
	<script>
	$(document).ready(function() {
		�//�� $('.js-example-basic-multiple').select2();
		
		� $('.js-lc-basic-multiple').select2({
		    placeholder: "Click to select LC"
		});
		
		 $('.js-ic-basic-multiple').select2({
			    placeholder: "Click to select IC"
			});
		
		
		});
</script>
    </body>
</html>