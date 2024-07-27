 <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.beans.PersonAcads"%>
<%@page import="com.nmims.beans.PageAcads"%>
<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="../jscss.jsp">
	<jsp:param value="Viva Faculty List" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="../header.jsp"%>

	<section class="content-container">

		<div class="container-fluid customTheme">
		<div class="row"><legend>&nbsp;Viva Faculty Slot</legend></div>
		<div class="panel-body">
		<%@ include file="../messages.jsp"%>
		
		<div class="container">
			<table class="table table-bordered  ">
				<thead>
					<tr>
						<td>Exam Cycle</td>
						<td>Start Time</td>
						<td>End Time</td>
						<td>SapId</td>
						<td>Action</td>
					</tr>
				</thead>
				<tbody>
					<c:if test="${vivaSlotBookingBeanList.size() > 0}">
					<c:forEach var="vivaSlotBookingBean" items="${vivaSlotBookingBeanList}" >
					<tr>
						<td>Apr - 2022</td>
						<td>${ vivaSlotBookingBean.start_time }</td>
						<td>${ vivaSlotBookingBean.end_time }</td>
						<td>${ vivaSlotBookingBean.sapid }</td>
						<td><button data-endTime="${ vivaSlotBookingBean.end_time }" data-startTime="${ vivaSlotBookingBean.start_time }" data-meetingkey="${ vivaSlotBookingBean.meetingkey }"  data-hostId="${ vivaSlotBookingBean.hostId }" class="btn btn-primary btn-sm js__start_meeting">Start</button></td>
					</tr>
					</c:forEach>
					</c:if>
					<c:if test="${vivaSlotBookingBeanList.size() <= 0}">
						<tr>
							<td colspan="6"><center>No Record Found</center></td>
						</tr>
					</c:if>
				</tbody>
			</table>
		</div>
		
		</div>
		</div>
	</section>
	<jsp:include page="../footer.jsp" />
	<script>
	$(document).ready(function(){
		$('.js__start_meeting').click(function(){
			var meetingkey = $(this).attr('data-meetingkey');
			var hostId = $(this).attr('data-hostId');
			var startTime = $(this).attr('data-startTime');
			var endTime = $(this).attr('data-endTime');
			var today = new Date();
			var month = (today.getMonth()+1) > 10 ? (today.getMonth()+1) : "0" + (today.getMonth()+1);
			var date = today.getFullYear()+'-'+month+'-'+today.getDate();
			var time = today.getHours() + ":" + today.getMinutes() + ":" + today.getSeconds();
			var dateTime = date+' '+time;
			var bool1 = moment(dateTime).isBetween(startTime,endTime);
			if(!bool1){
				alert("Link is not active");
				return false;
			}
			$(this).attr('disabled',true).html("loading...");
			$.ajax({
				url: "startMeeting",
				method: "POST",
				data: {
					"meetingkey" : meetingkey,
					"hostId" : hostId
				},
				success: function(response){
					if(response.status == "success"){
						window.location.href = response.url;
						return false;
					}else{
						alert("Failed to create link");
					}
				},
				error: function(error){
					alert("Failed to start meeting,try again or refresh these page");
					$(this).attr('disabled',false).html("Start");
				}
			});
		});
	});
	</script>
</body>
</html>