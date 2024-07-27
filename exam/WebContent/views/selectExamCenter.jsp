<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html cselass="no-js lt-ie9"> <![endif]-->
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

<jsp:include page="jscss.jsp">
	<jsp:param value="Select Exam Center" name="title" />
</jsp:include>

<%
	StudentBean student = (StudentBean)session.getAttribute("student");
	String programStructure = student.getPrgmStructApplicable();
%>
<script type="text/javascript">


function validateForm(mode) {

	var centerList = document.getElementsByName('selectedCenters');
	
	for(var i = 0; i < centerList.length; i = i + 2)
	{
		<%
			if("Jul2014".equals(programStructure)){
		%>
				var e1 = centerList[i];
				var e2 = centerList[i + 1];
				//There are two rows per subject to be checked for Online exam
				if(e1.options[e1.selectedIndex].value == "" && e2.options[e2.selectedIndex].value == ""){
					alert("Please select Exam Centers for all subjects.");
			    	return false;
				}
		
	    <%}else{%>
	    		//One row per subject
			    var e = centerList[i];
			    if(e.options[e.selectedIndex].value == "" && e.disabled == false){
			    	alert("Please select Exam Centers for all subjects.");
			    	return false;
			    } 
	    <%}%>
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
	
	//Check if same date time is selected for two subjects
	var cCheck = new Array ();
	for(var i = 0; i < centerList.length; ++i)
	{
		var e = centerList[i];
		if(e.disabled){
			continue;
		}
		
		var optionValue =  e.options[e.selectedIndex].value;
		
		var firstIndex = optionValue.indexOf("|");
		var secondIndex = optionValue.indexOf("|", firstIndex + 1);
		var thirdIndex = optionValue.indexOf("|", secondIndex + 1);
		var forthIndex = optionValue.indexOf("|", thirdIndex + 1);
		
		var dateTime = optionValue.substring(secondIndex, forthIndex);
		
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


function enableExamCenter(index){
	
		var i = index.value;
		var center = document.getElementById('selectedCenter'+i);
		var city = document.getElementById('city'+i);
		
		
		if(index.checked == true){
			center.disabled = false;
			city.disabled = false;
			
			<%
				if("Jul2014".equals(programStructure)){
			%>
				if(i % 2 == 0){
					var nextSelectCheckBox = document.getElementById('index'+(+i + 1)); //Putting + sign before i converts it from string to number
					nextSelectCheckBox.checked = false;
					center = document.getElementById('selectedCenter'+(+i + 1));
					center.disabled = true;
					center.value = "";
					
					city = document.getElementById('city'+(+i + 1));
					city.disabled = true;
					city.value = "";
				}else{
					var previousSelectCheckBox = document.getElementById('index'+(+i - 1)); //Putting + sign before i converts it from string to number
					previousSelectCheckBox.checked = false;
					center = document.getElementById('selectedCenter'+(+i - 1));
					center.disabled = true;
					center.value = "";
					
					city = document.getElementById('city'+(+i - 1));
					city.disabled = true;
					city.value = "";
				}
			<%}%>
		}else{
			center.disabled = true;
			center.value = "";
			
			city.disabled = true;
			city.value = "";
		}
	
	
}

</script>
<body class="inside">


	<%@ include file="header.jsp"%>

			<%
			String sapId = student.getSapid();
			%>
	<section class="content-container login">
		<div class="container-fluid customTheme">

			<div class="row">
			<legend>Select Exam Center</legend>
			</div>
			

			<%@ include file="messages.jsp"%>
			<%
				String hasReleasedSubjects = (String)request.getAttribute("hasReleasedSubjects");
				int examFeesPerSubject = 500;
				int totalFeesForRebooking = 200;
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
				
			%>
				

					
					<div>
					<div class="table-responsive panel-body">
					<form:form  action="makePaymentForm" method="post" modelAttribute="examCenter" >
					<fieldset>
					<table class="table table-striped" style="font-size:12px">
						<thead>
							<tr> 
								<th>Sr. No.</th>
								<th>Subject</th>
								<th>Sem</th>
								<th>Date</th>
								<th>Start Time</th>
								<th align="center">Select Time Slot</th>
								<th>End Time</th>
								<th>Select Exam Center City</th>
								<th width="40%">Select Exam Center 
								<%if("Jul2014".equals(programStructureApplicable)) {%>
									(Available/Capacity)
								<%} %>
								</th>
								
							</tr>
						</thead>
						<tbody>
						
						
						<%
						int count = 0;
						for(int i = 0; i < timeTableList.size(); i++){
							TimetableBean bean = (TimetableBean)timeTableList.get(i);
							SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
							SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, dd-MMM-yyyy");
							Date formattedDate = formatter.parse(bean.getDate());
							String formattedDateString = dateFormatter.format(formattedDate);
							String startTime = bean.getStartTime();
							String date = bean.getDate();
							List<ExamCenterBean> examCenters = subjectAvailableCentersMap.get(bean.getSubject() + bean.getStartTime());
						%>
					        <tr>
					        
					        	<%
								if("Jul2014".equals(programStructure)){
									count = (i/2 + 1);
								%>
									<td><%= (i/2 + 1)%></td>
								<%}else{ %>
									<td><%= ++count%></td>
								<%} %>
					        	
								<td><%= bean.getSubject()%></td>
								<td><%= bean.getSem()%></td>
								<td><%= formattedDateString%></td>
								<td><%= bean.getStartTime()%></td>
								
								<td>
			        				<input type="checkbox" style="width:50px" value="<%=i %>" name="indexes" onClick="enableExamCenter(this);" id="index<%=i%>"/>
		        				</td>
								
								<td><%= bean.getEndTime()%></td>
								<td>
								<%if(examCenters.size() > 0){ 
									//noOfSubjects++;
									ArrayList<String> allCentes = new ArrayList();
								%>
								
								<select name="city" class="cityDummyClass" id="city<%=i %>" disabled="disabled" data-index="<%=i %>">
								<option value="">Select City</option>
									<% for(int j = 0; j < examCenters.size(); j++){ 
										ExamCenterBean center = examCenters.get(j);
										String address = center.getAddress();
										String centerId = center.getCenterId();
										String centerName = center.getExamCenterName();
										String locality = center.getLocality();
										String city = center.getCity();
										int available = center.getAvailable();
										String capacity = center.getCapacity();
										
										if(!allCentes.contains(city.trim())){
											allCentes.add(city.trim());
										}else{
											continue; //Do not display same center again
										}
									%>
									
									<option value="<%=city %>">
										<%=city %>
									</option>
									
									<%}%>
									</select>
									<%}else{%>
										No Exam Center Available
									<%} %>
								</td>
								
								<td>
								<%if(examCenters.size() > 0){ 
									//noOfSubjects++;
								%>
								
								<select name="selectedCenters" id="selectedCenter<%=i %>" disabled="disabled" style="overflow:visible;">
								<option value="">Please Select Exam Center</option>
									<% for(int j = 0; j < examCenters.size(); j++){ 
										ExamCenterBean center = examCenters.get(j);
										String address = center.getAddress();
										String centerId = center.getCenterId();
										String centerName = center.getExamCenterName();
										String locality = center.getLocality();
										String city = center.getCity();
										int available = center.getAvailable();
										String capacity = center.getCapacity();
									%>
									
									<option value="<%=bean.getSubject()%>|<%=centerId%>|<%=startTime %>|<%=date%>|<%=city %>" style="display:none;">
										<%=city %> : <%=centerName %>, <%=address%>
										<%if("Jul2014".equals(programStructureApplicable)) {%>
											(<%=available %>/<%=capacity %>)
										<%} %>
									</option>
									
									<%}%>
									</select>
									<%}else{%>
										No Exam Center Available
									<%} %>
								</td>
								
					        </tr> 
					        <%}  %>  
					        
					        <%
					        if(subjects.contains("Project")){
					        	//noOfSubjects++;
								%>
								<tr>
					            <td><%=++count%></td>
								<td>Project</td>
								<td>4</td>
								<td>NA</td>
								<td>NA</td>
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
					
					<div class="form-group">
						<label class="control-label" for="submit"></label>
						<div class="controls">
						
							<%if("true".equals(ddPaid)) {%>
								<button id="submit" name="submit" onclick="return validateForm();" class="btn btn-large btn-primary" formaction="saveSeatsForDD">Book my seat</button>
								<button id="cancel" name="cancel" class="btn btn-danger" formaction="selectSubjectsForm" formnovalidate="formnovalidate">Back</button>
							<%}else if("true".equals(hasApprovedOnlineTransactions)){ %>
								<button id="submit" name="submit" onclick="return validateForm();" class="btn btn-large btn-primary" formaction="saveSeatsForOnline">Book my seat</button>
								<button id="cancel" name="cancel" class="btn btn-danger" formaction="selectSubjectsForm" formnovalidate="formnovalidate">Back</button>
							<%}else if("true".equals(hasReleasedNoChargeSubjects)){ %>
								<button id="submit" name="submit" onclick="return validateForm();" class="btn btn-large btn-primary" formaction="saveSeatsForReleasedSeatsNoCharges">Book my seat</button>
								<button id="cancel" name="cancel" class="btn btn-danger" formaction="selectSubjectsForm" formnovalidate="formnovalidate">Back</button>
							<%}else if("true".equals(hasFreeSubjects)){ %>
								<button id="submit" name="submit" onclick="return validateForm();" class="btn btn-large btn-primary" formaction="saveSeatsForFree">Book my seat</button>
								<button id="cancel" name="cancel" class="btn btn-danger" formaction="selectSubjectsForm" formnovalidate="formnovalidate">Back</button>
							<%}else if("true".equals(hasReleasedSubjects)){ %>
								<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="goToGateway" 
								onclick="return validateForm('Online');">Proceed to Payment Gateway</button>
								<button id="cancel" name="cancel" class="btn btn-danger" formaction="selectSubjectsForm" formnovalidate="formnovalidate">Back</button>
							<%}else if(totalFees > 0){ %>
								<button id="submit" name="submit" onclick="return validateForm('Online');" class="btn btn-large btn-primary" formaction="goToGateway">Proceed to Payment Gateway</button>
								<button id="cancel" name="cancel" class="btn btn-danger" formaction="selectPaymentMode" formnovalidate="formnovalidate">Back</button>
							<%}else{ %>
								<button id="cancel" name="cancel" class="btn btn-danger" formaction="selectPaymentMode" formnovalidate="formnovalidate">Back</button>
							<%} %>
							
						</div>
					</div>
					 </fieldset>
					</form:form>
				</div>
				</div>
				
	
		
		</div>
	</section>

   <jsp:include page="footer.jsp" />
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
				if (cCheck.indexOf(dateTime) == -1) {
					cCheck.push(dateTime);
				}
				else {
					$('#sameDateTimeModal').modal('show');
				}	
			}
			
	 });

	 

	 
	//When City is changed
	 $('.cityDummyClass').on('change', function() {
		  var city = this.value;
		  index = $(this).attr("data-index");
		  showCitysCenters(index, city.trim());
		 
	});

	 
	 function showCitysCenters(index, city){
		 
		 var elementID = "selectedCenter" + index;
		 
		 $("#" + elementID).val("");
		 
		 $("#" + elementID +" > option").each(function(i, option){
			 
			 if(!this.text.startsWith(city)){
				 option.style.display = 'none';
			 }else{
				 option.style.display = 'block';
			 }
			 
			 if(i == 0){
				 option.style.display = 'block';
			 }
			 
			 
		 });
	 }
	 
    </Script>


	<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
	  <div class="modal-dialog" role="document">
	    <div class="modal-content">
	      <div class="modal-header">
	        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
	        <h4 class="modal-title" id="myModalLabel"><i class="fa-solid fa-triangle-exclamation fa-lg"></i> Alert: Two different cities selected for Exam Centers</h4>
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

<%try{ %>


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
	HashMap<String,String> corporateCenterUserMapping = (HashMap<String,String>)request.getSession().getAttribute("corporateCenterUserMapping");
	List<TimetableBean> timeTableList = (List<TimetableBean>)session.getAttribute("timeTableList");
	Map<String, List<ExamCenterBean>> subjectAvailableCentersMap = (HashMap<String, List<ExamCenterBean>>)session.getAttribute("subjectAvailableCentersMap");
	
	String programStructureApplicable = student.getPrgmStructApplicable();
	
	
	int totalExamFees = (Integer)session.getAttribute("totalExamFees");
%>
	<%
		if(corporateCenterUserMapping.containsKey(sapId)){

	%>
	<jsp:forward page="selectExamCenterCorporate.jsp"/>
	
	<%} %>
	

<%
	if("Online".equals(student.getExamMode())){
//Forward to separate exam center selection page as it is different for Online students
%>
	<jsp:forward page="selectExamCenterForRegularOnlineExam.jsp"/>
<%} %>

<html lang="en">
    
    
    <jsp:include page="common/jscss.jsp">
	<jsp:param value="Select Exam Center" name="title"/>
    </jsp:include>
    
    <script type="text/javascript">
		
		function validateForm(mode) {
			var listOfSelectedSubjects=[];
			var blankStrategicManagementCount= 0;
			var selectedStrategicManagementCount= 0;

			var blankMTPCount= 0;
			var selectedMTPCount= 0;
			
			var currentCenterName="";
			var centerList = document.getElementsByName('selectedCenters');
			
			for(var i = 0; i < centerList.length; i = i + 2)
			{
				<%
					if("Jul2014".equals(programStructure)){
				%>
						var e1 = centerList[i];
						var e2 = centerList[i + 1];
						//There are two rows per subject to be checked for Online exam
						if(e1.options[e1.selectedIndex].value == "" && e2.options[e2.selectedIndex].value == ""){
							alert("Please select Exam Centers for all subjects.");
					    	return false;
						}
				
			    <%}else{%>
			    		//One row per subject
					    var e = centerList[i];
					    var eNext = centerList[i+1];
					    console.log('In for got e for'+i);

					    console.log(e);
					    console.log(e.options[e.selectedIndex].value);
					    
					    currentCenterName = e.options[e.selectedIndex].value;
			    		
					    if(e.options[e.selectedIndex].value == "" && e.disabled == false){
			    			console.log('In  for if blank got e1 for'+i);
						    console.log(e.options[1].value);
						    currentCenterName = e.options[1].value;
				    		
			    			if(currentCenterName.split("|",1)[0] == 'Strategic Management'){

							    blankStrategicManagementCount = blankStrategicManagementCount + 1;
					    		if(blankStrategicManagementCount > 1){
									alert("Subject "+currentCenterName.split("|",1)[0]+" has to be selected only once.");
							    	return false;
					    		}
					    	}
			    			//blankMTPCount
			    			else if(currentCenterName.split("|",1)[0] == 'Management Theory and Practice'){

			    				blankMTPCount = blankMTPCount + 1;
					    		if(blankMTPCount > 1){
									alert("Subject "+currentCenterName.split("|",1)[0]+" has to be selected only once.");
							    	return false;
					    		}
					    	}
					    	else{
			    				alert("Please select Exam Centers for all subjects.");
					    		return false;
					    	}
					    }else{
					    	//check for selected SM and MTP
					    	if(e.options[e.selectedIndex].value.split("|",1)[0] == 'Strategic Management'){
					    		selectedStrategicManagementCount = selectedStrategicManagementCount + 1;
					    		if(selectedStrategicManagementCount > 1){
									alert("Subject "+e.options[e.selectedIndex].value.split("|",1)[0]+" has to be selected only once.");
							    	return false;
					    		}
					    	}
					    	if(e.options[e.selectedIndex].value.split("|",1)[0] == 'Management Theory and Practice'){
					    		selectedMTPCount = selectedMTPCount + 1;
					    		if(selectedMTPCount > 1){
									alert("Subject "+e.options[e.selectedIndex].value.split("|",1)[0]+" has to be selected only once.");
							    	return false;
					    		}
					    	}
					    	//selectedStrategicManagementCount
					    	//listOfSelectedSubjects.push(e.options[e.selectedIndex].value.split("|",1)[0]);    
					    }

					    console.log('In for got eNext for'+(i+1));
					    console.log(eNext.options[eNext.selectedIndex].value);
					    
					    currentCenterName = eNext.options[eNext.selectedIndex].value;
			    		
					    if(eNext != null){
			    		if(eNext.options[eNext.selectedIndex].value == "" && eNext.disabled == false){
			    			console.log('In  for if blank got e1 for'+i);
						    console.log(eNext.options[1].value);
						    currentCenterName = eNext.options[1].value;
				    		
			    			if(currentCenterName.split("|",1)[0] == 'Strategic Management'){
					    		blankStrategicManagementCount = blankStrategicManagementCount + 1;
					    		if(blankStrategicManagementCount > 1){
									alert("Subject "+currentCenterName.split("|",1)[0]+" has to be selected only once.");
							    	return false;
					    		}
					    	}
			    			else if(currentCenterName.split("|",1)[0] == 'Management Theory and Practice'){

			    				blankMTPCount = blankMTPCount + 1;
					    		if(blankMTPCount > 1){
									alert("Subject "+currentCenterName.split("|",1)[0]+" has to be selected only once.");
							    	return false;
					    		}
					    	}
					    	else{
					    		
			    				alert("Please select Exam Centers for all subjects.");
					    		return false;
					    	}
			    		}else{ 
			    			if(eNext.options[eNext.selectedIndex].value.split("|",1)[0] == 'Strategic Management'){
					    		selectedStrategicManagementCount = selectedStrategicManagementCount + 1;
					    		if(selectedStrategicManagementCount > 1){
									alert("Subject "+e.options[eNext.selectedIndex].value.split("|",1)[0]+" has to be selected only once.");
							    	return false;
					    		}
					    	}
			    			if(eNext.options[e.selectedIndex].value.split("|",1)[0] == 'Management Theory and Practice'){
					    		selectedMTPCount = selectedMTPCount + 1;
					    		if(selectedMTPCount > 1){
									alert("Subject "+eNext.options[e.selectedIndex].value.split("|",1)[0]+" has to be selected only once.");
							    	return false;
					    		}
					    	}
					    	//listOfSelectedSubjects.push(eNext.options[eNext.selectedIndex].value.split("|",1)[0]);    
					    }
			    		}
			    <%}%>
			}
    		console.log('listOfSelectedSubjects : ');
    		console.log(listOfSelectedSubjects);
    		//alert("Last extra Please select Exam Centers for all subjects.");
	    	//return false;
	    
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
			
			//Check if same date time is selected for two subjects
			var cCheck = new Array ();
			for(var i = 0; i < centerList.length; ++i)
			{
				var e = centerList[i];
				if(e.disabled){
					continue;
				}
				
				var optionValue =  e.options[e.selectedIndex].value;
				
				var firstIndex = optionValue.indexOf("|");
				var secondIndex = optionValue.indexOf("|", firstIndex + 1);
				var thirdIndex = optionValue.indexOf("|", secondIndex + 1);
				var forthIndex = optionValue.indexOf("|", thirdIndex + 1);
				
				var dateTime = optionValue.substring(secondIndex, forthIndex);
				
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
		
		
		function enableExamCenter(index){
			
				var i = index.value;
				var center = document.getElementById('selectedCenter'+i);
				var city = document.getElementById('city'+i);
				
				
				if(index.checked == true){
					center.disabled = false;
					city.disabled = false;
					
					<%
						if("Jul2014".equals(programStructure)){
					%>
						if(i % 2 == 0){
							var nextSelectCheckBox = document.getElementById('index'+(+i + 1)); //Putting + sign before i converts it from string to number
							nextSelectCheckBox.checked = false;
							center = document.getElementById('selectedCenter'+(+i + 1));
							center.disabled = true;
							center.value = "";
							
							city = document.getElementById('city'+(+i + 1));
							city.disabled = true;
							city.value = "";
						}else{
							var previousSelectCheckBox = document.getElementById('index'+(+i - 1)); //Putting + sign before i converts it from string to number
							previousSelectCheckBox.checked = false;
							center = document.getElementById('selectedCenter'+(+i - 1));
							center.disabled = true;
							center.value = "";
							
							city = document.getElementById('city'+(+i - 1));
							city.disabled = true;
							city.value = "";
						}
					<%}%>
				}else{
					center.disabled = true;
					center.value = "";
					
					city.disabled = true;
					city.value = "";
				}
			
			
		}
		
		</script>
    
    <body>
    
    	<%@ include file="common/header.jsp" %>
    	
    	
        
        <div class="sz-main-content-wrapper">
        
        	<%@ include file="common/breadcrum.jsp" %>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
                           <div id="sticky-sidebar"> 
              				<jsp:include page="common/left-sidebar.jsp">
								<jsp:param value="Exam Registration" name="activeMenu"/>
							</jsp:include>
							</div>
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="common/studentInfoBar.jsp" %>
              						
              						
              						<div class="sz-content">
								
										<h2 class="red text-capitalize">Select Exam Center : Written Exam</h2>
										<div class="clearfix"></div>
		              					<div class="panel-content-wrapper">
		              					
											<%@ include file="common/messages.jsp" %>
											<form:form  action="makePaymentForm" method="post" modelAttribute="examCenter" >
												<fieldset>
												<div class="table-responsive">
												<table class="table table-striped" style="font-size:12px">
													<thead>
														<tr> 
															<th>Sr. No.</th>
															<th>Subject</th>
															<th>Sem</th>
															<th>Date</th>
															<th>Start Time</th>
															<th align="center">Select Time Slot</th>
															<th>End Time</th>
															<th>Select Exam Center City</th>
															<th width="40%">Select Exam Center 
															<%if("Jul2014".equals(programStructureApplicable)) {%>
																(Available/Capacity)
															<%} %>
															</th>
															
														</tr>
													</thead>
													<tbody>
													
													
													<%
													int count = 0;
													for(int i = 0; i < timeTableList.size(); i++){
														TimetableBean bean = (TimetableBean)timeTableList.get(i);
														SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
														SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, dd-MMM-yyyy");
														Date formattedDate = formatter.parse(bean.getDate());
														String formattedDateString = dateFormatter.format(formattedDate);
														String startTime = bean.getStartTime();
														String date = bean.getDate();
														List<ExamCenterBean> examCenters = subjectAvailableCentersMap.get(bean.getSubject() + bean.getStartTime());
													%>
												        <tr>
												        
												        	<%
															if("Jul2014".equals(programStructure)){
																count = (i/2 + 1);
																if(i % 2 == 0){
															%>
															
															<!--  Show serial nuber and subject alternate rows since it spans two rows -->
																	<td class="valignTop" rowspan="2"><%= (i/2 + 1)%></td>
																	<td class="valignTop" rowspan="2"><%= bean.getSubject()%></td>
															<%
																}
															}else{ %>
																<td><%= ++count%></td>
																<td><%= bean.getSubject()%></td>
															<%} %>
												        	
															<td><%= bean.getSem()%></td>
															<td><%= formattedDateString%></td>
															<td><%= bean.getStartTime()%></td>
															
															<td>
										        				<input type="checkbox" style="width:50px" value="<%=i %>" name="indexes" onClick="enableExamCenter(this);" id="index<%=i%>"/>
									        				</td>
															
															<td><%= bean.getEndTime()%></td>
															<td>
															<%if(examCenters.size() > 0){ 
																//noOfSubjects++;
																ArrayList<String> allCentes = new ArrayList();
															%>
															
															<select name="city" class="cityDummyClass" id="city<%=i %>"  data-index="<%=i %>" style="max-width:300px;">
															<option value="">Select City</option>
																<% for(int j = 0; j < examCenters.size(); j++){ 
																	ExamCenterBean center = examCenters.get(j);
																	String address = center.getAddress();
																	String centerId = center.getCenterId();
																	String centerName = center.getExamCenterName();
																	String locality = center.getLocality();
																	String city = center.getCity();
																	int available = center.getAvailable();
																	String capacity = center.getCapacity();
																	
																	if(!allCentes.contains(city.trim())){
																		allCentes.add(city.trim());
																	}else{
																		continue; //Do not display same center again
																	}
																	
																	if(centerId.equalsIgnoreCase("429")&& date.equalsIgnoreCase("2018-12-07")){
																		continue;
																	}
																	
																	if(date.equalsIgnoreCase("2018-12-19")){
																		if(!centerId.equalsIgnoreCase("429")){
																			continue;
																		}
																	}
																%>
																
																<option value="<%=city %>">
																	<%=city %>
																</option>
																
																<%}%>
																</select>
																<%}else{%>
																	No Exam Center Available
																<%} %>
															</td>
															
															<td>
															<%if(examCenters.size() > 0){ 
																//noOfSubjects++;
															%>
															
															<select name="selectedCenters" id="selectedCenter<%=i %>"  class="selectedCentersdummy" style="overflow:visible;">
															<option value="">Please Select Exam Center</option>
																<% for(int j = 0; j < examCenters.size(); j++){
																	ExamCenterBean center = examCenters.get(j);
																	String address = center.getAddress();
																	String centerId = center.getCenterId();
																	String centerName = center.getExamCenterName();
																	String locality = center.getLocality();
																	String city = center.getCity();
																	int available = center.getAvailable();
																	String capacity = center.getCapacity();
																%>
																
																<option value="<%=bean.getSubject()%>|<%=centerId%>|<%=startTime %>|<%=date%>|<%=city %>" style="display:none;">
																	<%=city %> : <%=centerName %>, <%=address%>
																	<%if("Jul2014".equals(programStructureApplicable)) {%>
																		(<%=available %>/<%=capacity %>)
																	<%} %>
																</option>
																
																<%}%>
																</select>
																<%}else{%>
																	No Exam Center Available
																<%} %>
															</td>
															
												        </tr> 
												        <%}  %>  
												        
												        <%
												        if(subjects.contains("Project") || subjects.contains("Module 4 - Project")){
												        	//noOfSubjects++;
															%>
															<tr>
												            <td><%=++count%></td>
															<td>Project</td>
															<td>4</td>
															<td>NA</td>
															<td>NA</td>
															<td>NA</td>
															<td>NA</td>
												        </tr> 
														<%}  %>  
												        
												        	
															
														
													</tbody>
												</table>
												</div>
												<%
												int totalFees = 0;
												if("true".equalsIgnoreCase(hasReleasedSubjects)){
													
													totalFees = totalFeesForRebooking;
												}else{
													//totalFees = examFeesPerSubject * noOfSubjects;
													totalFees = totalExamFees;
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
												
												<div class="form-group">
													<label class="control-label" for="submit"></label>
													<div class="controls">
														<input type="hidden" name="totalFeesAmount" value="<%=totalFees %>" />
														<%if("true".equals(ddPaid)) {%>
															<button id="submit" name="submit" onclick="return validateForm();" class="btn btn-large btn-primary" formaction="saveSeatsForDD">Book my seat</button>
															<button id="cancel" name="cancel" class="btn btn-danger" formaction="selectSubjectsForm" formnovalidate="formnovalidate">Back</button>
														<%}else if("true".equals(hasApprovedOnlineTransactions)){ %>
															<button id="submit" name="submit" onclick="return validateForm();" class="btn btn-large btn-primary" formaction="saveSeatsForOnline">Book my seat</button>
															<button id="cancel" name="cancel" class="btn btn-danger" formaction="selectSubjectsForm" formnovalidate="formnovalidate">Back</button>
														<%}else if("true".equals(hasReleasedNoChargeSubjects)){ %>
															<button id="submit" name="submit" onclick="return validateForm();" class="btn btn-large btn-primary" formaction="saveSeatsForReleasedSeatsNoCharges">Book my seat</button>
															<button id="cancel" name="cancel" class="btn btn-danger" formaction="selectSubjectsForm" formnovalidate="formnovalidate">Back</button>
														<%}else if("true".equals(hasFreeSubjects)){ %>
															<button id="submit" name="submit" onclick="return validateForm();" class="btn btn-large btn-primary" formaction="saveSeatsForFree">Book my seat</button>
															<button id="cancel" name="cancel" class="btn btn-danger" formaction="selectSubjectsForm" formnovalidate="formnovalidate">Back</button>
														<%}else if("true".equals(hasReleasedSubjects)){ %>
															<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="goToGateway" 
															onclick="return validateForm('Online');">Proceed to Payment Gateway</button>
															<button id="cancel" name="cancel" class="btn btn-danger" formaction="selectSubjectsForm" formnovalidate="formnovalidate">Back</button>
														<%}else if(totalFees > 0){ %>
															<button id="submit" name="submit" onclick="return validateForm('Online');" class="btn btn-large btn-primary" formaction="goToGateway">Proceed to Payment Gateway</button>
															<button id="cancel" name="cancel" class="btn btn-danger" formaction="selectPaymentMode" formnovalidate="formnovalidate">Back</button>
														<%}else{ %>
															<button id="cancel" name="cancel" class="btn btn-danger" formaction="selectPaymentMode" formnovalidate="formnovalidate">Back</button>
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
            
  	
        <jsp:include page="common/footer.jsp"/>
        <Script>
     // to avoid back and forth on Jsp
        document.getElementsByName('selectedCenters').length=0;
        $(".selectedCentersdummy").val("");
        document.getElementsByName('city').length=0;
        $(".cityDummyClass").val("");
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
					
					if (cCheck.indexOf(dateTime) == -1) {
						cCheck.push(dateTime);
					}
					else {
						$('#sameDateTimeModal').modal('show');
					}	
				}
				
		 });
	
		 
	
		 
		//When City is changed
		 $('.cityDummyClass').on('change', function() {
			  var city = this.value;
			  index = $(this).attr("data-index");
			  showCitysCenters(index, city.trim());
			 
		});
	
		 
		 function showCitysCenters(index, city){
			 
			 var elementID = "selectedCenter" + index;
			 
			 $("#" + elementID).val("");
			 
			 $("#" + elementID +" > option").each(function(i, option){
				 
				 if(!this.text.startsWith(city)){
					 option.style.display = 'none';
				 }else{
					 option.style.display = 'block';
				 }
				 
				 if(i == 0){
					 option.style.display = 'block';
				 }
				 
				 
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
		        <p> We observed you have selected different cities for Exam Centers opted. We suggest you cross verify one more time before proceeding to Payment Gateway&hellip;</p>
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
		
    </body>
    <%}catch(Exception e){ 
    }%>
    
</html>