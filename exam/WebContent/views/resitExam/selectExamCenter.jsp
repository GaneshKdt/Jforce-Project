<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.TreeMap"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.Format"%>
<%@page import="com.nmims.beans.*"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>

<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="../jscss.jsp">
	<jsp:param value="Select Exam Center" name="title" />
</jsp:include>

<script type="text/javascript">

function setHiddenFieldValue(index){
	var subjectCenter = document.getElementById('city' + index).value;
	var dateTime = document.getElementById('dateTime' + index).value;
	
	document.getElementById('selectedCenters' + index).value = subjectCenter + "|" + dateTime;
}

function validateForm(mode) {
	
	var centerList = document.getElementsByName('selectedCenters');
	for(var i = 0; i < centerList.length; ++i)
	{
		var e = centerList[i];
		var selectedVal =  e.value;
	    if(e.value == ""){
	    	alert("Please select Exam Centers for all subjects.");
	    	return false;
	    }
	}
	
	var firstCity = "";
	var twoCities = false;
	for(var i = 0; i < centerList.length; ++i)
	{
		var e = centerList[i];
		if(e.disabled){
			continue;
		}
		
		if(firstCity == ""){
			var optionValue = e.value;
			firstCity = optionValue.substring(optionValue.lastIndexOf("|")+1, optionValue.length);
		}
		
		var optionValue =  e.value;
		var nextCity = optionValue.substring(optionValue.lastIndexOf("|")+1, optionValue.length);
	    if(nextCity != firstCity){
	    	var msg = "**** ALERT: Two Different Cities selected for Exam : ****\n\n";
	    	msg += "You have selected different Exam Center cities viz. " + firstCity + " & " + nextCity + ". Are you sure you want to proceed with different cities?";
	    	
	    	twoCities = confirm (msg);
	    	if(twoCities){
	    		break;
	    	}else{
	    		return false;
	    	}
	    }
	}
	
	var cCheck = new Array ();
	for(var i = 0; i < centerList.length; ++i)
	{
		var e = centerList[i];
		var selectedVal =  e.value;
		firstPipeIndex = selectedVal.indexOf('|');

		var dateTime = selectedVal.substring(selectedVal.indexOf('|', firstPipeIndex+1), selectedVal.lastIndexOf('|'));
		if (cCheck.indexOf(dateTime) == -1) {
			cCheck.push(dateTime);
		}
		else {
			alert("You have selected same Exam Date and Time more than once. Please correct it.");
			return false;
		}	
	}

	if(mode == 'Online'){
		return confirm('Please note you will have 5 minutes to complete transaction. Are you sure you want to proceed?');
	}else{
		return confirm('You cannot change Exam Center after this step. Are you sure you want to confirm your bookings for Centers selected?');
	}
	
	/* var centerList = document.getElementsByName('selectedCenters');
	for(var i = 0; i < centerList.length; ++i)
	{
		var e = centerList[i];
		var selectedVal =  e.options[e.selectedIndex].value;
	    if(e.options[e.selectedIndex].value == ""){
	    	alert("Please select Exam Centers for all subjects.");
	    	return false;
	    }
	}
	
	var firstCity = "";
	var twoCities = false;
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
		var nextCity = optionValue.substring(optionValue.lastIndexOf("|")+1, optionValue.length);
	    if(nextCity != firstCity){
	    	var msg = "**** ALERT: Two Different Cities selected for Exam : ****\n\n";
	    	msg += "You have selected different Exam Center cities viz. " + firstCity + " & " + nextCity + ". Are you sure you want to proceed with different cities?";
	    	
	    	twoCities = confirm (msg);
	    	if(twoCities){
	    		break;
	    	}else{
	    		return false;
	    	}
	    }
	}
	
	var cCheck = new Array ();
	for(var i = 0; i < centerList.length; ++i)
	{
		var e = centerList[i];
		var selectedVal =  e.options[e.selectedIndex].value;
		firstPipeIndex = selectedVal.indexOf('|');

		var dateTime = selectedVal.substring(selectedVal.indexOf('|', firstPipeIndex+1), selectedVal.lastIndexOf('|'));
		if (cCheck.indexOf(dateTime) == -1) {
			cCheck.push(dateTime);
		}
		else {
			alert("You have selected same Exam Date and Time more than once. Please correct it.");
			return false;
		}	
	}

	return confirm('Please note you will have 5 minutes to complete transaction. Are you sure you want to proceed?'); */
	
	
	
	
}


</script>
<body class="inside">


	<%@ include file="../header.jsp"%>

			<%
			StudentBean student = (StudentBean)session.getAttribute("student");
			String sapId = student.getSapid();
			%>
	<section class="content-container login">
		<div class="container-fluid customTheme">

			<div class="row">
			<legend>Select Exam Center</legend>
			</div>
			

			<%@ include file="../messages.jsp"%>
			<%
				String hasReleasedSubjects = (String)request.getAttribute("hasReleasedSubjects");
				String hasReleasedNoChargeSubjects = (String)request.getAttribute("hasReleasedNoChargeSubjects");
			
				int examFeesPerSubject = 500;
				int totalFeesForRebooking = 200;
				int noOfSubjects = 0;
				
				ArrayList<String> subjects = (ArrayList<String>)request.getSession().getAttribute("subjects");
				String ddPaid = (String)request.getAttribute("ddPaid");
				String hasApprovedOnlineTransactions = (String)request.getAttribute("hasApprovedOnlineTransactions");
				String hasFreeSubjects = (String)request.getAttribute("hasFreeSubjects");
				
				Map<String, List<ExamCenterBean>> subjectAvailableCentersMap = (HashMap<String, List<ExamCenterBean>>)session.getAttribute("subjectAvailableCentersMap");
				String programStructureApplicable = student.getPrgmStructApplicable();
			%>
				

					
					<div>
					<div class="table-responsive panel-body">
					<form:form  action="makePaymentForm" method="post" modelAttribute="examBooking" >
					<fieldset>
					<table class="table table-striped" style="font-size:12px">
						<thead>
							<tr> 
								<th>Sr. No.</th>
								<th>Subject</th>
								<!-- <th>Sem</th>
								<th>Date</th>
								<th>Start Time</th>
								<th>End Time</th> -->
								<th>Select Exam Center City
									
								</th>
								
								<th>Select Date/Time
									(Available/Capacity)
								</th>
								
							</tr>
						</thead>
						<tbody>
						
						
						<%
						int count = 0;
						for(int i = 0; i < subjects.size(); i++){
							String subject = (String)subjects.get(i);
							SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
							SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, dd-MMM-yyyy");
							//Date formattedDate = formatter.parse(bean.getDate());
							//String formattedDateString = dateFormatter.format(formattedDate);
							List<ExamCenterBean> examCenters = subjectAvailableCentersMap.get(subject);
						%>
					        <tr>
					        	<td><%= ++count%></td>
								<td><%= subject%></td>
								<td><%=subject %></td>
								<td><%=subject %></td>
								<td><%= subject%></td>
								<td><%= subject%></td>
								<td>
								<%if(examCenters.size() > 0){ 
									noOfSubjects++;
								%>
								
								<select name="city" id="city<%=count%>" onchange="setHiddenFieldValue(<%=count%>);">
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
									
									<option value="<%=subject%>|<%=centerId%>|<%=date %>|<%=startTime %>|<%=city%>">
										<%=city %> : <%=centerName %>, <%=locality %>, <%=formattedDateString %>, <%=startTime %>
										(<%=available %>/<%=capacity %>)
									</option>
									
									<option value="<%=subject%>|<%=centerId%>">
										<%=city %> , <%=locality %>
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
										<select id="dateTime<%=count%>" required="required" onchange="setHiddenFieldValue(<%=count%>);">
										<option id="">Select Date and Time Slot</option>
										</select>
									<%}else{%>
										No Exam Center Available
									<%} %>
								</td>
								
								
								
					        </tr> 
					        <%}  %>  
					        
					        <%
					        if(subjects.contains("Project")){
					        	noOfSubjects++;
								%>
								<tr>
					            <td><%=++count%></td>
								<td>Project</td>
								<td>4</td>
								<td>NA</td>
								<td>NA</td>
					        </tr> 
							<%}  %>  
					        
					        	
								
							
						</tbody>
					</table>
					<%
					int totalFees = 0;
					if("true".equalsIgnoreCase(hasReleasedSubjects)){
						totalFees = totalFeesForRebooking;
					}else{
						totalFees = examFeesPerSubject * noOfSubjects;
					}

					%>
					<%if( (!"true".equals(ddPaid))  && (!"true".equals(hasApprovedOnlineTransactions)) &&  (!"true".equals(hasFreeSubjects))) {%>
						<div class="form-group">
						<%if("true".equals(hasReleasedSubjects)){ %>
						<b>Total Exam Center Change Fees: </b><%=totalFees %>/-
						<%}else{ %>
						<b>Total Exam Fees: </b><%=totalFees %>/-
						<%} %>
						</div>
					<%} %>
					
					<%
					if("true".equals(hasReleasedSubjects)){ %>
						<input type="hidden"  name = "hasReleasedSubjects" value = "<%=hasReleasedSubjects%>"/>
					<%} %>
					
					<input type="hidden"  name = "month" value = "${examBooking.month }"/>
					<input type="hidden"  name = "year" value = "${examBooking.year }"/>
					
					<div class="form-group">
						<label class="control-label" for="submit"></label>
						<div class="controls">
						
							<%if("true".equals(hasApprovedOnlineTransactions)){ %>
								<button id="submit" name="submit" onclick="return validateForm();" class="btn btn-large btn-primary" formaction="saveResitSeatsForOnline">Book my seat</button>
								<button id="cancel" name="cancel" class="btn btn-danger" formaction="selectResitSubjectsForm" formnovalidate="formnovalidate">Back</button>
							<%}else if("true".equals(hasReleasedSubjects)){ %>
								<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="resitGoToGateway" 
								onclick="return validateForm('Online');">Proceed to Payment Gateway</button>
								<button id="cancel" name="cancel" class="btn btn-danger" formaction="selectResitSubjectsForm" formnovalidate="formnovalidate">Back</button>
							<%}else if("true".equals(hasReleasedNoChargeSubjects)){ %>
								<button id="submit" name="submit" onclick="return validateForm();" class="btn btn-large btn-primary" formaction="saveResitSeatsForReleasedSeatsNoCharges">Book my seat</button>
								<button id="cancel" name="cancel" class="btn btn-danger" formaction="selectSubjectsForm" formnovalidate="formnovalidate">Back</button>
							<%}else if("true".equals(hasFreeSubjects)){ %>
								<button id="submit" name="submit" onclick="return validateForm();" class="btn btn-large btn-primary" formaction="saveResitSeatsForFree">Book my seat</button>
								<button id="cancel" name="cancel" class="btn btn-danger" formaction="selectResitSubjectsForm" formnovalidate="formnovalidate">Back</button>
							<%}else if(totalFees > 0){ %>
								<button id="submit" name="submit" onclick="return validateForm('Online');" class="btn btn-large btn-primary" formaction="resitGoToGateway">Proceed to Payment Gateway</button>
								<button id="cancel" name="cancel" class="btn btn-danger" formaction="selectResitSubjectsForm" formnovalidate="formnovalidate">Back</button>
							<%}else{ %>
								<button id="cancel" name="cancel" class="btn btn-danger" formaction="selectResitSubjectsForm" formnovalidate="formnovalidate">Back</button>
							<%} %>
							
						</div>
					</div>
					 </fieldset>
					</form:form>
				</div>
				</div>
				
	
		
		</div>
	</section>

   <jsp:include page="../footer.jsp" />


	<Script>
     
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
				
				var optionValue =  e.options[e.selectedIndex].value;
				if(optionValue != "" && e.disabled == false){
					var nextCity = optionValue.substring(optionValue.lastIndexOf("|")+1, optionValue.length);
				    if(nextCity != firstCity){
				    	$('#myModal').modal('show');
				    }
				}
			}
			
		 	/* var centerList = document.getElementsByName('selectedCenters');

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
				if(optionValue != "" && e.disabled == false){
					var nextCity = optionValue.substring(optionValue.lastIndexOf("|")+1, optionValue.length);
				    if(nextCity != firstCity){
				    	$('#myModal').modal('show');
				    }
				}
			}	 */
			
	 });
	 
	 for (i = 0; i < 21; i++) {
		 //Cannot have more than 20 failed subjects
		 $("#dateTime"+i).depdrop({
	        url: '/exam/getAvailableCentersForCity',
	        depends: ['city'+i]
	    });
	}
	 
	 

    </Script>
    
	<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
	  <div class="modal-dialog" role="document">
	    <div class="modal-content">
	      <div class="modal-header">
	        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
	        <h4 class="modal-title" id="myModalLabel"><i class="fa fa-exclamation-triangle fa-lg"></i> Alert: Two different cities selected for Exam Centers</h4>
	      </div>
	      <div class="modal-body">
	        <p> We observed you have selected different cities for Exam Centers opted. We suggest you cross verify one more time before proceeding to Payment Gateway&hellip;</p>
	      </div>
	      <div class="modal-footer">
	        <button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
	      </div>
	    </div>
	  </div>
	</div>
	
</body>
</html>
 --%>
 
 
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
	String sapId = student.getSapid();
	String hasReleasedSubjects = (String)request.getAttribute("hasReleasedSubjects");
	String hasReleasedNoChargeSubjects = (String)request.getAttribute("hasReleasedNoChargeSubjects");

	int examFeesPerSubject = 500;
	int totalFeesForRebooking = 500;
	int noOfSubjects = 0;
	
	ArrayList<String> subjects = (ArrayList<String>)request.getSession().getAttribute("subjects");
	String ddPaid = (String)request.getAttribute("ddPaid");
	String hasApprovedOnlineTransactions = (String)request.getAttribute("hasApprovedOnlineTransactions");
	String hasFreeSubjects = (String)request.getAttribute("hasFreeSubjects");
	
	Map<String, List<ExamCenterBean>> subjectAvailableCentersMap = (HashMap<String, List<ExamCenterBean>>)session.getAttribute("subjectAvailableCentersMap");
	
	
	String programStructureApplicable = student.getPrgmStructApplicable();
%>
			
<html lang="en">
    

	
    
    <jsp:include page="../common/jscss.jsp">
	<jsp:param value="Select Exam Center" name="title"/>
    </jsp:include>
    
    <script type="text/javascript">

		function setHiddenFieldValue(index){
			var subjectCenter = document.getElementById('city' + index).value;
			var dateTime = document.getElementById('dateTime' + index).value;
			
			document.getElementById('selectedCenters' + index).value = subjectCenter + "|" + dateTime;
		}
		
		function validateForm(mode) {
			
			var centerList = document.getElementsByName('selectedCenters');
			for(var i = 0; i < centerList.length; ++i)
			{
				var e = centerList[i];
				var selectedVal =  e.value;
			    if(e.value == ""){
			    	alert("Please select Exam Centers for all subjects.");
			    	return false;
			    }
			}
			
			var firstCity = "";
			var twoCities = false;
			for(var i = 0; i < centerList.length; ++i)
			{
				var e = centerList[i];
				if(e.disabled){
					continue;
				}
				
				if(firstCity == ""){
					var optionValue = e.value;
					firstCity = optionValue.substring(optionValue.lastIndexOf("|")+1, optionValue.length);
				}
				
				var optionValue =  e.value;
				var nextCity = optionValue.substring(optionValue.lastIndexOf("|")+1, optionValue.length);
			    if(nextCity != firstCity){
			    	var msg = "**** ALERT: Two Different Cities/Centers selected for Exam : ****\n\n";
			    	msg += "You have selected different Exam Center cities viz. " + firstCity + " & " + nextCity + ". Are you sure you want to proceed with different cities?";
			    	
			    	twoCities = confirm (msg);
			    	if(twoCities){
			    		break;
			    	}else{
			    		return false;
			    	}
			    }
			}
			
			var cCheck = new Array ();
			for(var i = 0; i < centerList.length; ++i)
			{
				var e = centerList[i];
				var selectedVal =  e.value;
				firstPipeIndex = selectedVal.indexOf('|');
		
				var dateTime = selectedVal.substring(selectedVal.indexOf('|', firstPipeIndex+1), selectedVal.lastIndexOf('|'));
				if (cCheck.indexOf(dateTime) == -1) {
					cCheck.push(dateTime);
				}
				else {
					alert("You have selected same Exam Date and Time more than once. Please correct it.");
					return false;
				}	
			}
		
			if(mode == 'Online'){
				return confirm('Please note you will have 5 minutes to complete transaction. Are you sure you want to proceed?');
			}else{
				return confirm('You cannot change Exam Center after this step. Are you sure you want to confirm your bookings for Centers selected?');
			}
			
		}
		
		
		</script>
    
    <body>
    
    	<%@ include file="../common/header.jsp" %>
    	
    	
        
        <div class="sz-main-content-wrapper">
        
        	<%@ include file="../common/breadcrum.jsp" %>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="../common/left-sidebar.jsp">
								<jsp:param value="Resit-Exam Registration" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="../common/studentInfoBar.jsp" %>
              						
              						
              						<div class="sz-content">
								
										<h2 class="red text-capitalize">Select Exam Center</h2>
										<div class="clearfix"></div>
		              					<div class="panel-content-wrapper">
											<%@ include file="../common/messages.jsp" %>
											
											<div class="table-responsive">
												<form:form  action="makePaymentForm" method="post" modelAttribute="examBooking" >
												<fieldset>
												<table class="table table-striped" style="font-size:12px">
													<thead>
														<tr> 
															<th>Sr. No.</th>
															<th>Subject</th>
															<!-- <th>Sem</th>
															<th>Date</th>
															<th>Start Time</th>
															<th>End Time</th> -->
															<th>Select Exam Center City
																
															</th>
															
															<th>Select Date/Time
																(Available/Capacity)
															</th>
															
														</tr>
													</thead>
													<tbody>
													
													
													<%
													int count = 0;
													for(int i = 0; i < subjects.size(); i++){
														String subject = (String)subjects.get(i);
														if("Project".equals(subject) || "Module 4 - Project".equals(subject)){
															continue;
														}
														SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
														SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, dd-MMM-yyyy");
														//Date formattedDate = formatter.parse(bean.getDate());
														//String formattedDateString = dateFormatter.format(formattedDate);
														List<ExamCenterBean> examCenters = subjectAvailableCentersMap.get(subject);
													%>
												        <tr>
												        	<td><%= ++count%></td>
															<td><%= subject%></td>
															<%-- <td><%=subject %></td>
															<td><%=subject %></td>
															<td><%= subject%></td>
															<td><%= subject%></td> --%>
															<td>
															<%if(examCenters.size() > 0){ 
																noOfSubjects++;
															%>
															
															<select name="city" id="city<%=count%>" onchange="setHiddenFieldValue(<%=count%>);">
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
																	<%=city %> , <%=locality %>
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
																	<select id="dateTime<%=count%>" required="required" onchange="setHiddenFieldValue(<%=count%>);">
																	<option id="">Select Date and Time Slot</option>
																	</select>
																<%}else{%>
																	No Exam Center Available
																<%} %>
															</td>
															
															
															
												        </tr> 
												        <%}  %>  
												        
												        <%
												        if(subjects.contains("Project") || subjects.contains("Module 4 - Project")){
												        	noOfSubjects++;
															%>
															<tr>
												            <td><%=++count%></td>
															<td>Project</td>
															<td>4</td>
															<td>NA</td>
															<td>NA</td>
												        </tr> 
														<%}  %>  
												        
												        	
															
														
													</tbody>
												</table>
												<%
												int totalFees = 0;
												if("true".equalsIgnoreCase(hasReleasedSubjects)){
													totalFees = totalFeesForRebooking;
												}else{
													totalFees = examFeesPerSubject * noOfSubjects;
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
												
												<%
												if("true".equals(hasReleasedSubjects)){ %>
													<input type="hidden"  name = "hasReleasedSubjects" value = "<%=hasReleasedSubjects%>"/>
												<%} %>
												
												<input type="hidden"  name = "month" value = "${examBooking.month }"/>
												<input type="hidden"  name = "year" value = "${examBooking.year }"/>
												
												<div class="form-group">
													<label class="control-label" for="submit"></label>
													<div class="controls">
													
														<%if("true".equals(hasApprovedOnlineTransactions)){ %>
															<button id="submit" name="submit" onclick="return validateForm();" class="btn btn-large btn-primary" formaction="saveResitSeatsForOnline">Book my seat</button>
															<button id="cancel" name="cancel" class="btn btn-danger" formaction="selectResitSubjectsForm" formnovalidate="formnovalidate">Back</button>
														<%}else if("true".equals(hasReleasedSubjects)){ %>
															<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="resitGoToGateway" 
															onclick="return validateForm('Online');">Proceed to Payment Gateway</button>
															<button id="cancel" name="cancel" class="btn btn-danger" formaction="selectResitSubjectsForm" formnovalidate="formnovalidate">Back</button>
														<%}else if("true".equals(hasReleasedNoChargeSubjects)){ %>
															<button id="submit" name="submit" onclick="return validateForm();" class="btn btn-large btn-primary" formaction="saveResitSeatsForReleasedSeatsNoCharges">Book my seat</button>
															<button id="cancel" name="cancel" class="btn btn-danger" formaction="selectSubjectsForm" formnovalidate="formnovalidate">Back</button>
														<%}else if("true".equals(hasFreeSubjects)){ %>
															<button id="submit" name="submit" onclick="return validateForm();" class="btn btn-large btn-primary" formaction="saveResitSeatsForFree">Book my seat</button>
															<button id="cancel" name="cancel" class="btn btn-danger" formaction="selectResitSubjectsForm" formnovalidate="formnovalidate">Back</button>
														<%}else if(totalFees > 0){ %>
															<button id="submit" name="submit" onclick="return validateForm('Online');" class="btn btn-large btn-primary" formaction="resitGoToGateway">Proceed to Payment Gateway</button>
															<button id="cancel" name="cancel" class="btn btn-danger" formaction="selectResitSubjectsForm" formnovalidate="formnovalidate">Back</button>
														<%}else{ %>
															<button id="cancel" name="cancel" class="btn btn-danger" formaction="selectResitSubjectsForm" formnovalidate="formnovalidate">Back</button>
														<%} %>
														
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
        </div>
            
  	
        <jsp:include page="../common/footer.jsp"/>
        
        <Script>
     
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
					
					var optionValue =  e.options[e.selectedIndex].value;
					if(optionValue != "" && e.disabled == false){
						var nextCity = optionValue.substring(optionValue.lastIndexOf("|")+1, optionValue.length);
					    if(nextCity != firstCity){
					    	$('#myModal').modal('show');
					    }
					}
				}
		 });
		 
		 for (i = 0; i < 21; i++) {
			 //Cannot have more than 20 failed subjects
			 $("#dateTime"+i).depdrop({
		        url: '/exam/getAvailableCentersForCity',
		        depends: ['city'+i]
		    });
		}
		 
	
	    </Script>
	    
		<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
		  <div class="modal-dialog" role="document">
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
		        <h4 class="modal-title" id="myModalLabel"><i class="fa fa-exclamation-triangle fa-lg"></i> Alert: Two different cities/Centers selected for Exam Centers</h4>
		      </div>
		      <div class="modal-body">
		        <p> We observed you have selected different cities for Exam Centers opted. We suggest you cross verify one more time before proceeding to Payment Gateway&hellip;</p>
		      </div>
		      <div class="modal-footer">
		        <button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
		      </div>
		    </div>
		  </div>
		</div>
		
    </body>
</html>