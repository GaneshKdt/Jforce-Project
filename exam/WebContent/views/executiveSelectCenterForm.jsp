<!DOCTYPE html>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.TreeMap"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.Format"%>
<%@page import="com.nmims.beans.*"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%
StudentExamBean student = (StudentExamBean)session.getAttribute("studentExam");
	String programStructure = student.getPrgmStructApplicable();
	String sapId = student.getSapid();
	
	String hasReleasedSubjects = (String)request.getAttribute("hasReleasedSubjects");
	int examFeesPerSubject = 600;
	int totalFeesForRebooking = 500;
	int noOfSubjects = 0;
	
	ArrayList<String> subjects = (ArrayList<String>)request.getSession().getAttribute("subjects");
	noOfSubjects = subjects.size();
	
	String ddPaid = (String)request.getAttribute("ddPaid");
	String hasApprovedOnlineTransactions = (String)request.getAttribute("hasApprovedOnlineTransactions");
	String hasReleasedNoChargeSubjects = (String)request.getAttribute("hasReleasedNoChargeSubjects");
	String hasFreeSubjects = (String)request.getAttribute("hasFreeSubjects");
	
	List<TimetableBean> timeTableList = (List<TimetableBean>)session.getAttribute("timeTableList");
	Map<String, List<ExamCenterBean>> subjectAvailableCentersMap = (HashMap<String, List<ExamCenterBean>>)session.getAttribute("subjectAvailableCentersMap");
	
	String programStructureApplicable = student.getPrgmStructApplicable();
	int totalExamFee = 200;
	
%>


<html lang="en">
    
    
    <jsp:include page="common/jscss.jsp">
	<jsp:param value="Select Exam Center" name="title"/>
    </jsp:include>
    
    <script type="text/javascript">
	
    
    var bookedDateTimeArray = new Array();
    <% 
    String selectionForReleasedSeats = (String)request.getAttribute("selectionForReleasedSeats");
    
    ArrayList<String> dateTimeBookedList =(ArrayList<String>)session.getAttribute("dateTimeBookedList");
    if(dateTimeBookedList !=null){
        for(String dateTimeBooked : dateTimeBookedList){ %>
            bookedDateTimeArray.push('<%= dateTimeBooked%>'); 
        <%}
    }%>
    
    var selectionForReleasedSeats = '<%=selectionForReleasedSeats%>';
    
	    function setHiddenFieldValue(index){
			var subjectCenter = document.getElementById('city' + index).value;
			var dateTime = document.getElementById('dateTime' + index).value;
			document.getElementById('selectedCenters' + index).value = subjectCenter + "|" + dateTime;
			
		}
    
		function validateForm(mode) {
			
			return confirm('Are you sure you want to confirm your bookings for Centers selected?');
			
		}
			
		</script>
    
    <body>
    
    	<%@ include file="common/header.jsp" %>
        
        <div class="sz-main-content-wrapper">
        
        	<%@ include file="common/breadcrum.jsp" %>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="common/left-sidebar.jsp">
								<jsp:param value="Exam Registration" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="common/studentInfoBar.jsp" %>
              						
              						<div class="sz-content">
								
										<h2 class="red text-capitalize">Select Exam Center</h2>
										<div class="clearfix"></div>
		              					<div class="panel-content-wrapper">
											<%@ include file="common/messages.jsp" %>
											<form:form  action="makePaymentForm" method="post" modelAttribute="executiveBean" >
												<fieldset>
												<div class="table-responsive">
												<table class="table table-striped" style="font-size:12px">
													<thead>
														<tr> 
															<th>Sr. No.</th>
															<th>Subject</th>
															<th>Select Exam Center City</th>
															<th>Select Date/Time
																(Available/Capacity)
															</th>
															
														</tr>
													</thead>
													<tbody>
													
													
													<%
													try{
													int count = 0;
													
													for(int i = 0; i < subjects.size(); i++){
														String subject = (String)subjects.get(i);
														if("Project".equals(subject) || "Module 4 - Project".equals(subject)){
															continue;
														}
														SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
														SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, dd-MMM-yyyy");
														List<ExamCenterBean> examCenters = subjectAvailableCentersMap.get(subject);
													%>
												        <tr>
												        	<td><%= ++count%></td>
															<td><%= subject%></td>
															<td>
															<%if(examCenters.size() > 0){ 
																noOfSubjects++;
															%>
															
															<select name="city" id="city<%=count%>" required="required" onchange="setHiddenFieldValue(<%=count%>);">
																<option value="">Please Select Exam Center</option>
																<% 
																ArrayList<String> allCentes = new ArrayList();
																for(int j = 0; j < examCenters.size(); j++){ 
																	ExamCenterBean center = examCenters.get(j);
																	String centerId = center.getCenterId();
																	String centerName = center.getExamCenterName();
																	String locality = center.getLocality();
																	String city = center.getCity();
																	int available = center.getAvailable();
																	String capacity = center.getCapacity();
																	String date = center.getDate();
																	String startTime = center.getStarttime();
																	String endTime = center.getEndtime();
																	
																	Date formattedDate = formatter.parse(date);
																	String formattedDateString = dateFormatter.format(formattedDate);
																	
																	if(!allCentes.contains(centerId)){
																		allCentes.add(centerId);
																	}else{
																		continue; //Do not display same center again
																	}
																%>
																
																<%-- <option value="<%=subject%>|<%=centerId%>|<%=date %>|<%=startTime %>|<%=city%>">
																	<%=city %> : <%=centerName %>, <%=locality %>, <%=formattedDateString %>, <%=startTime %>
																	(<%=available %>/<%=capacity %>)
																</option> --%>
																
																<option value="<%=subject%>|<%=centerId%>">
																	<%=city %> , <%=centerName%>, <%=locality %>
																</option>
																
																
																<%}%>
																</select>
																
																<input type="hidden" name="selectedCenters" value="" id="selectedCenters<%=count%>">
																<%}else{%>
																	No Exam Center Available
																<%} %>
															</td>
															
															<td>	
																<%if(examCenters.size() > 0){ %>
																	<select id="dateTime<%=count%>" name="dateTime" required="required" onchange="setHiddenFieldValue(<%=count%>);">
																	<option value="">Select Date and Time Slot</option>
																	</select>
																	
																<%}else{%>
																	No Exam Center Available
																<%} %>
															</td>
															
												        </tr> 
												        <%}
													
													%>  
												        
												        
												   <%  }catch(Exception e){
														
													}  %>  	
															
														
													</tbody>
												</table>
												</div>
												<%-- 
												<%
												int totalFees = 0;
												if("true".equalsIgnoreCase(hasReleasedSubjects)){
													totalFees = totalFeesForRebooking;
												}else{
													totalFees = totalExamFee;
												}
							
												%>
												<%if( (!"true".equals(ddPaid))  && (!"true".equals(hasApprovedOnlineTransactions)) &&  (!"true".equals(hasFreeSubjects))) {%>
													<div class="form-group">
													<%if("true".equals(hasReleasedSubjects)){ %>
													
													<h3 class="total-fee">
														<span>Total Exam Center Change Fees:</span>
														Rs. <%=totalFees %>/-
													</h3>
													
													<%}else{ %>
													
													<h3 class="total-fee">
														<span>Total Exam Fees:</span>
														Rs. <%=totalFees %>/-
													</h3>
													
													<%} %>
													</div>
												<%} %> 
												
												--%>
												
												<form:hidden path="changeOfCenter"  value="true"/>
												
												<div class="form-group">
													<label class="control-label" for="submit"></label>
													<div class="controls">
														<button id="submit" name="submit" onclick="return validateForm();" formaction="saveSeatsForExecutiveExam" class="btn btn-large btn-primary">Book my seat</button>
														<button id="cancel" name="cancel" class="btn btn-danger" formaction="executiveRegistrationForm" formnovalidate="formnovalidate">Back</button>
														
													</div>
												</div>
												 </fieldset>
												</form:form>
											
										</div>
              								
              						</div>
              				</div>
              		
                            
					</div>
            </div>
        </div>
            
  	
        <jsp:include page="common/footer.jsp"/>
        <Script>
   
		 $("select[name=selectedCenters]").change(function() {
			 	var centerList = document.getElementsByName('selectedCenters');
				
				var firstCity = "";
				for(var i = 0; i < centerList.length; ++i)
				{
					var e = centerList[i];
					if(e.disabled){
						continue;
					}
					
					if(firstCity == ""){
						var optionValue = e.options[e.selectedIndex].value;
						firstCity = optionValue.substring(optionValue.lastIndexOf("|")+1, optionValue.length);
					}
					
					var optionValue =  e.options[e.selectedIndex].value;
					
					if(optionValue == ""){
						continue;
					}
					
					if(optionValue != "" && e.disabled == false){
						var nextCity = optionValue.substring(optionValue.lastIndexOf("|")+1, optionValue.length);
					    if(nextCity != firstCity){
					    	$('#myModal').modal('show');
					    }
					}
				}	
				
				
				
				//Check if same date time is selected for two subjects
				var cCheck = new Array ();
				for(var i = 0; i < centerList.length; ++i)
				{
					var e = centerList[i];
					if(e.disabled){
						continue;
					}
					
					var optionValue =  e.options[e.selectedIndex].value;
					
					if(optionValue == ""){
						continue;
					}
					
					var firstIndex = optionValue.indexOf("|");
					var secondIndex = optionValue.indexOf("|", firstIndex + 1);
					var thirdIndex = optionValue.indexOf("|", secondIndex + 1);
					var forthIndex = optionValue.indexOf("|", thirdIndex + 1);
					
					
					var dateTime = optionValue.substring(secondIndex, forthIndex);
					alert("dateTime"+dateTime);
					if (cCheck.indexOf(dateTime) == -1) {
						cCheck.push(dateTime);
					}
					else {
						$('#sameDateTimeModal').modal('show');
					}	
				}
				
		 });
	
 
		 
		 $("select[name=city]").change(function() {
			 var centerList = document.getElementsByName('city');
			 
			 var firstCity = "";
				for(var i = 0; i < centerList.length; ++i)
				{
					var e = centerList[i];
					if(e.disabled){
						continue;
					}
					
					if(firstCity == ""){
						var optionValue = e.options[e.selectedIndex].value;
						firstCity = optionValue.substring(optionValue.lastIndexOf("|")+1, optionValue.length);
					}
					//alert('First City-->'+firstCity);
					var optionValue =  e.options[e.selectedIndex].value;
					if(optionValue != "" && e.disabled == false){
						var nextCity = optionValue.substring(optionValue.lastIndexOf("|")+1, optionValue.length);
						//alert('Second city-->'+nextCity);
					    if(nextCity != firstCity){
					    	//e.selectedIndex = 0;
					    	$('#myModal').modal('show');
					    }
					}
				}
		 });
		 
$("select[name=dateTime]").change(function(){
			 
			 var centerList = document.getElementsByName('dateTime');
			 
			 var cCheck = new Array ();
			 
				for(var i = 0; i < centerList.length; ++i)
				{
					var e = centerList[i];
					
					if(e.disabled){
						continue;
					}
					
					var optionValue =  e.options[e.selectedIndex].value;
					//alert("Entire option-->"+optionValue);
					if(optionValue == ""){
						continue;
					}
					
					var firstIndex = optionValue.indexOf("|");
					var secondIndex = optionValue.indexOf("|", firstIndex + 1);
					//var thirdIndex = optionValue.indexOf("|", secondIndex + 1);
					//var forthIndex = optionValue.indexOf("|", thirdIndex + 1);
					
					
					var dateTime = optionValue.substring(0, secondIndex);
					
					if(bookedDateTimeArray.indexOf(dateTime) != -1 && selectionForReleasedSeats != "true"){ 
						//date time checked if earlier bookings were made. If it was made then throw error.
						//Do not perform this check if student is coming for selecting centers of released seats
						e.selectedIndex = 0;
						$('#sameDateTimeForEarlierBookedSubjectsModal').modal('show');
					}
					
					//alert(dateTime);
					if (cCheck.indexOf(dateTime) == -1) {
						cCheck.push(dateTime);
					}
					else{
						e.selectedIndex = 0;
						$('#sameDateTimeModal').modal('show');
					}	
				}
			 
		 });
		 
		 
		 
		 for (i = 0; i < 23; i++) {
			 //Cannot have more than 23 failed subjects
			 $("#dateTime"+i).depdrop({
		        url: '/exam/getAvailableCentersForExecutiveExam',
		        depends: ['city'+i]
		    });
		}
		 
	    </Script>
	
	
		<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
		  <div class="modal-dialog" role="document">
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
		        <h4 class="modal-title" id="myModalLabel"><i class="fa-solid fa-triangle-exclamation fa-lg"></i> Alert: Two different cities/Centers selected for Exam Centers</h4>
		      </div>
		      <div class="modal-body">
		        <p> We observed you have selected different cities for Exam Centers opted. We suggest you cross verify one more time before proceeding&hellip;</p>
		      </div>
		      <div class="modal-footer">
		        <button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
		      </div>
		    </div>
		  </div>
		</div>
		
		
		<div class="modal fade" id="sameDateTimeModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
		  <div class="modal-dialog" role="document">
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
		        <h4 class="modal-title" id="myModalLabel"><i class="fa-solid fa-triangle-exclamation fa-lg"></i> Alert: Same Date-Time slot selected for Exam Centers</h4>
		      </div>
		      <div class="modal-body">
		        <p> We observed you have selected same Date & Time slot for two subjects. Please change the Time slot selected&hellip;</p>
		      </div>
		      <div class="modal-footer">
		        <button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
		      </div>
		    </div>
		  </div>
		</div>
		
		<div class="modal fade" id="sameDateTimeForEarlierBookedSubjectsModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
		  <div class="modal-dialog" role="document">
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
		        <h4 class="modal-title" id="myModalLabel"><i class="fa-solid fa-triangle-exclamation fa-lg"></i> Alert: Same Date-Time slot selected for Exam Centers</h4>
		      </div>
		      <div class="modal-body">
		        <p> We observed you have selected same Date & Time slot for subjects booked earlier. Please change the Time slot selected&hellip;</p>
		      </div>
		      <div class="modal-footer">
		        <button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
		      </div>
		    </div>
		  </div>
		</div>
		
    </body>
</html>