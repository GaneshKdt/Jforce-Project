<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html lang="en">
    <jsp:include page="./common/jscss.jsp">
	<jsp:param value="Book VIVA" name="title"/>
    </jsp:include>
    <style>
    	.pending_block,.pending_block:hover,.pending_block:active{
    		padding:8px 15px;
    		background-color:#F4BC01;
    		color:white;
    		border-radius:5px;
    	}
    	.rejected_block{
    		padding:8px 15px;
    		background-color:red;
    		color:white;
    	}
    	.success_block,.success_block:hover,.success_block:active{
    		padding:8px 15px;
    		background-color:#58BC34;
    		color:white;
    		border-radius:5px;
    	}
    </style>
    <body>
    	<%@ include file="./common/header.jsp" %>
        
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="./common/breadcrum.jsp">
				<jsp:param value="Student Zone;Exams;Book VIVA" name="breadcrumItems"/>
			</jsp:include>
			
			<div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="./common/left-sidebar.jsp">
								<jsp:param value="Book VIVA" name="activeMenu"/>
							</jsp:include>
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="./common/studentInfoBar.jsp" %>
              						<div class="sz-content">
              							<h2>Book VIVA</h2>
              							<div class="panel panel-default" style="margin-top:70px;">
	              							<div class="panel-body">
	              							<%@ include file="./common/messages.jsp" %>
	              							
	              							<%if("true".equals( (String)request.getAttribute("alreadyBooked"))) { %>
	              							<div>
	              								<%if(!"true".equals( (String)request.getAttribute("error")) && !"true".equals( (String)request.getAttribute("success"))) { %>
	              									<div class="alert alert-warning">
	              										Already Viva slot booked
	              									</div>
	              								<%} %>
	              								<label style="margin:0px !important;">Date for VIVA</label>
	              								<select style="margin:0px !important;" class="form-control" style="margin-top:15px;" disabled>
	              									<option selected>${alreadyBookedBean.date }</option>
	              								</select>
	              								<label style="margin:0px !important;margin-top:15px !important;">Slot for VIVA</label>
	              								<select style="margin:0px !important;" class="form-control" style="margin-top:15px;" disabled>
	              									<option selected>${alreadyBookedBean.start_time } to ${alreadyBookedBean.end_time }</option>
	              								</select>
	              								<input type="hidden" name="track_id" value="${ trackId }" />
	              							</div>  
	              							<br>  
	              							<div class="row"> 
		              							<section class="col-lg-5 col-md-5 col-sm-12 content-container" style="background-color:#fff;">  
			              							<div class="panel panel-info">   
														<div class="bullets" style="padding-left:5px;">   
							
															<a data-toggle="collapse" href="#instructions" aria-expanded="true" aria-controls="instructions" class=""><i class="fa-regular fa-square-plus" aria-hidden="true"></i> Instructions to attend viva:</a>
															<div id="instructions" class="collapse in" aria-expanded="true" style="">
																<ul> 
																  <li>Join Viva Button will be enabled 30mins before viva on this page.</li>
																 <li>Please keep your Headset ready to attend the viva over Zoom.</li>
																  <li>Please use Google Chrome OR Mozilla Firefox OR Safari browser preferably.</li>
																  <li>Please contact Technical Support Desk +1-888-799-9666 for any Technical Assistance in joining Zoom Webinar </li> 
																</ul>
															</div>
						   
														    <div style="display: none;" id="guestLecture">
															    <p>To join the training session</p>
															   	<ul>
															   		<li> <a href="https://acecloud.webex.com/acecloud/k2/j.php?MTID=t7e4b4fab4ba5a32842c6762ec57caa5c" target="_blank">click here</a> </li>
															   		<li>Enter your name and email address . </li>
															   		<li>Enter the session password: NMIMSNMIMS. </li>
															   		<li>Click "Join Now".</li>
															   	</ul>
														    </div>
													    </div>   
													</div>  
												</section> 
												</div>    
												<div class="row"> 
													<div class="col-md-12">   
													<c:if test="${alreadyBookedBean.meetingLink ne null && enableAttendButton eq 'true'}">
		              							  	<br>  <a class="btn btn-sm btn-info" href="${alreadyBookedBean.meetingLink}">Join Viva</a>  
		              							 	</c:if>
		              							 	</div> 
		              							  </div>   
	              							<% } %>
	              							<%if(!"true".equals( (String)request.getAttribute("alreadyBooked"))) { %>
	              							<div>
	              							<form action="vivaSlotBooking" method="POST">
	              								<label style="margin:0px !important;">Select Date for VIVA</label>
	              								<select style="margin:0px !important;" class="form-control viva_slot_date" style="margin-top:15px;">
	              									<option>Select Date</option>
	              									<c:forEach  items="${remainingSlotsDate}" var="slot">
	              										<option value='<c:out value="${slot.date}"/>'><c:out value="${slot.date}"/></option>
	              									</c:forEach >
	              								</select><br/>
	              								<label style="margin:0px !important;">Select slot for VIVA</label>
	              								<select style="margin:0px !important;" class="form-control viva_slot_timning" name="viva_slots_id" style="margin-top:15px;">
	              									<option>Select Slot</option>
	              								</select><br/>
	              								<input type="hidden" name="track_id" value="${ trackId }" />
	              								<small>Note: Slot format `Start Time` to `End Time` (Booked/Capacity)</small><br/>
	              								<button class="btn btn-primary btn-sm">submit</button>
	              							</form>
	              							</div>
	              							<% } %>
	              							</div>
	              						</div>
	              					</div>
	              			</div>
	              	</div>
	         </div>
		</div>
		<jsp:include page="./common/footer.jsp" />
	  
	  <script>
		  $(document).ready(function(){
			  function loadingTime(){
				  $('.viva_slot_timning').html('<option disabled selected>loading...</option>');
			  }
			  
			  function errorTime(){
				  $('.viva_slot_timning').html('<option selected>Select Slot</option>');
			  }
			  
			$(document).on('change','.viva_slot_date',function(){
				console.log("--->>>>>> document change");
				loadingTime();
				$.ajax({
					url : '/exam/m/getSlotTimeByDate',
					method : 'POST',
					contentType: "application/json",
					data : JSON.stringify({
						'date' : $(this).val()
					}),
					success:function(response){
						try{
							if(response.length > 0){
								var htmlOption = '<option selected>Select Slot</option>';
								for(var i=0;i < response.length;i++){
									htmlOption = htmlOption + '<option value="'+ response[i].id +'">'+ response[i].start_time +' to ' + response[i].end_time +' ('+ response[i].booked +'/'+ response[i].capacity +')</option>';
								}
								$('.viva_slot_timning').html(htmlOption);
							}
						}
						catch(e){
														
						}
					},
					error:function(error){
						console.log(error);	
					}
				});
			});
		  });
	  </script>
	</body>
</html>