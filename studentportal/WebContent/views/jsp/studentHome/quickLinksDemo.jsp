<%@page import="com.nmims.beans.StudentStudentPortalBean"%>
<% StudentStudentPortalBean sbean = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");%>
<div class="links">
	
		<div class="d-flex align-items-center text-nowrap py-1">
		<span class="fw-bold me-3">QUICK LINKS</span>
		<div class="ms-auto">
			<a type="button" data-bs-toggle="collapse" href="#collapseSeven"
				role="button" aria-expanded="true" aria-controls="collapseSeven" class="text-dark"
				id="collapseCard"> <i class="fa-solid fa-square-minus"></i></a>
			</ul>
		</div>
	</div>
	<div id="collapseSeven" class="collapse text-center text-nowrap">
	<div class="card card-body">
				<span class="icon-quick-links ms-2  fw-bold">4 Quick Links</span>
						
				</div>
	</div>
		<div id="collapseSeven" class="collapse show">
			<div class="panel-body">

				
			<div class="list-group" id="list-tab" role="tablist">
					<a href="/exam/selectSubjectsForm" class="list-group-item list-group-item-action text-dark">Register for Exam</a>
					<%-- <%if("Online".equalsIgnoreCase((StudentBean)session.getAttribute("student_studentportal").)){ %>
            	<li><a href="/exam/selectResitSubjectsForm">Register for Resit-Exam</a></li>
            <%} %> --%>
					<!-- <li><a href="/exam/downloadHallTicket">Download Hall Ticket</a></li> -->
					<%if(!sbean.getProgram().contains("EPBM") && !sbean.getProgram().contains("MPDV")){ %>
					<a href="/exam/student/previewHallTicket" class="list-group-item list-group-item-action text-dark">Download Hall Ticket</a>
					<%}else{ %>
				<a href="/exam/downloadExecutiveHallTicket" class="list-group-item list-group-item-action text-dark">Download Hall Ticket</a>
					<%} %>
				<a href="/exam/student/printBookingStatus" class="list-group-item list-group-item-action text-dark">Exam Registration Receipt</a>
					<a href="/studentportal/student/viewFeeReceipt" class="list-group-item list-group-item-action text-dark">Fee Receipt</a>
					<a href="/studentportal/supportOverview" class="list-group-item list-group-item-action text-dark">Support Overview</a>
					<a href="/exam/student/viewModelQuestionForm" class="list-group-item list-group-item-action text-dark">Demo Exam</a>
					<a href="/studentportal/student/getDispatches" class="list-group-item list-group-item-action text-dark">Dispatch Orders</a>
				</ul>
			</div>
		</div>
	
