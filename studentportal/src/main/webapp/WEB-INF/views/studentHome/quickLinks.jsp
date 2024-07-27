<%@page import="com.nmims.beans.StudentStudentPortalBean"%>
<% StudentStudentPortalBean sbean = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");%>
<div class="links">
	<div class="panel panel-default">
		<div class="panel-heading" role="tab" id="">
			<h4 class="panel-title">QUICK LINKS</h4>
			<ul class="topRightLinks list-inline">
				<li><a class="panel-toggler collapsed" role="button"
					data-toggle="collapse" data-parent="#accordion"
					href="#collapseSeven" aria-expanded="true"></a></li>
			</ul>
			<div class="clearfix"></div>
		</div>
		<div id="collapseSeven"
			class="panel-collapse collapse in courses-panel-collapse"
			role="tabpanel" aria-labelledby="headingSeven">
			<div class="panel-body">

				<div class="p-closed">
					<div class="no-data-wrapper">
						<p class="no-data">
							<span class="icon-quick-links"></span>4 Quick Links
						</p>
					</div>
				</div>
				<ul>
					<li><a href="/exam/selectSubjectsForm">Register for Exam</a></li>
					<%-- <%if("Online".equalsIgnoreCase((StudentBean)session.getAttribute("student_studentportal").)){ %>
            	<li><a href="/exam/selectResitSubjectsForm">Register for Resit-Exam</a></li>
            <%} %> --%>
					<!-- <li><a href="/exam/downloadHallTicket">Download Hall Ticket</a></li> -->
					<%if(!sbean.getProgram().contains("EPBM") && !sbean.getProgram().contains("MPDV")){ %>
					<li><a href="/exam/previewHallTicket">Download Hall Ticket</a></li>
					<%}else{ %>
					<li><a href="/exam/downloadExecutiveHallTicket">Download
							Hall Ticket</a></li>
					<%} %>
					<li><a href="/exam/printBookingStatus">Exam Registration
							Receipt</a></li>
					<li><a href="/studentportal/viewFeeReceipt">Fee Receipt</a></li>
					<li><a href="/studentportal/supportOverview">Support
							Overview</a></li>
					<li><a href="/exam/viewModelQuestionForm">Demo Exam</a></li>
					<li><a href="/studentportal/getDispatches">Dispatch Orders</a></li>
				</ul>
			</div>
		</div>
	</div>
</div>