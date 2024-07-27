<%-- 
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
	StudentBean student = (StudentBean)session.getAttribute("student");
	String programStructure = student.getPrgmStructApplicable();
	String sapId = student.getSapid();
	
	String hasReleasedSubjects = (String)request.getAttribute("hasReleasedSubjects");
	int examFeesPerSubject = 600;
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
	int totalExamFee = (Integer)session.getAttribute("totalExamFees");
	
%>


<html lang="en">
    
    
    <jsp:include page="common/jscss.jsp">
	<jsp:param value="Select Exam Center" name="title"/>
    </jsp:include>
    
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
		}s
		
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
									
									ArrayList<String> allCentes = new ArrayList();
								%>
								
								<select name="city" class="cityDummyClass" id="city<%=i %>" data-index="<%=i %>">
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
								
								<select name="selectedCenters" id="selectedCenter<%=i %>" style="overflow:visible;">
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
</html> --%>



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
<%@page import="com.google.gson.Gson"%>
<%@page import="org.apache.commons.lang.WordUtils"%>
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
	int totalExamFee = (Integer)session.getAttribute("totalExamFees");
	
%>


<html lang="en">
    
    
    <jsp:include page="common/jscss.jsp">
	<jsp:param value="Select Exam Center" name="title"/>
    </jsp:include>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.6-rc.0/css/select2.min.css" rel="stylesheet" />
    <style>
		.select2-search__field{
			color:black;
		}
		.select-readonly {
		    cursor: not-allowed;
		    background-color: #eee;
		    opacity: 1;
		    pointer-events: none;
		}
		.search-icon {
			cursor: pointer;
		    margin-left: 5px;
		    font-size: small !important;
		}
	</style>
    <script type="text/javascript">

	function check() {
		"use strict";
		try { 
			eval("var foo = (x)=>x+1"); 
		} catch (e) {
			alert("For seamless booking, please use chrome (Version 51 and above).");
			return false;
		}
		return true;
	}
	check();
    var bookedDateTimeArray = new Array();
    <% ArrayList<String> dateTimeBookedList =(ArrayList<String>)session.getAttribute("dateTimeBookedList");
    String selectionForReleasedSeats = (String)request.getAttribute("selectionForReleasedSeats");
    
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
			
			/*var centerList = document.getElementsByName('selectedCenters');
			
			
			for(var i = 0; i < centerList.length; i = i + 2)
			{
				
				
					    var e = centerList[i];
					    if(e.options[e.selectedIndex].value == "" && e.disabled == false){
					    	alert("Please select Exam Centers for all subjects.");
					    	return false;
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
			*/
			if(mode == 'Online'){
				return confirm('Please note you will have 5 minutes to complete transaction. Are you sure you want to proceed?');
			}else{
				return confirm('You cannot change Exam Center after this step. Are you sure you want to confirm your bookings for Centers selected?');
			}
			
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
											<form:form id="form1" action="makePaymentForm" method="post" modelAttribute="examCenter" >
												<fieldset>
												<div class="table-responsive">
												<table class="table table-striped" style="font-size:12px">
													<thead>
														<tr> 
															<th>Sr. No.</th>
															<th>Subject</th>
															<!-- <th>Sem</th>
															<th>Date</th>
															<th>Start Time</th>
															<th>End Time</th> -->
															<th> Select Exam Center City </th>
															
															<th> Select Date </th>
															<th> Select Time (Available/Capacity) </th>
															
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
														//Date formattedDate = formatter.parse(bean.getDate());
														//String formattedDateString = dateFormatter.format(formattedDate);
														List<ExamCenterBean> examCenters = subjectAvailableCentersMap.get(subject);
														
														Map<String, Map<String, ExamCenterBean>> stateAndCenterMap = new HashMap<String, Map<String, ExamCenterBean>>();
														
														for(ExamCenterBean center : examCenters) {
															String centerId = center.getCenterId();
															String state = WordUtils.capitalizeFully(center.getState());

															Map<String, ExamCenterBean> centersInState = stateAndCenterMap.get(state);
															
															if(centersInState != null){
																centersInState.put(centerId, center);
															}else{
																centersInState = new HashMap<String, ExamCenterBean>();
															}
															stateAndCenterMap.put(state, centersInState);
														}
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
															
															<select class="center-select" name="city" id="city<%=count%>" required="required" aria-select-id="<%=count %>" onchange="setHiddenFieldValue(<%=count%>);">

																<option value="">Please Select Exam Center</option>
																	<% 
																	stateAndCenterMap = new TreeMap<String, Map<String, ExamCenterBean>>(stateAndCenterMap);
																	for (Map.Entry<String, Map<String, ExamCenterBean>> entry : stateAndCenterMap.entrySet()){
																		String stateName = entry.getKey();
																	%>
																		
																		<optgroup label="<%= stateName %>">
																	<%
																		Map<String, ExamCenterBean> centers = new TreeMap<String, ExamCenterBean>(entry.getValue());
																		centers = new TreeMap<String, ExamCenterBean>(centers);
																		for (Map.Entry<String, ExamCenterBean> centerMap : centers.entrySet()){
																			
																			ExamCenterBean center = centerMap.getValue();
																			String centerId = center.getCenterId();
																			String centerName = center.getExamCenterName();
																			String locality = center.getLocality();
																			String city = center.getCity();
																			String state = center.getState();
																			int available = center.getAvailable();
																			String capacity = center.getCapacity();
																			String date = center.getDate();
																			String startTime = center.getStarttime();
																			String endTime = center.getEndtime();
																			
																			Date formattedDate = formatter.parse(date);
																			String formattedDateString = dateFormatter.format(formattedDate);
																%>
																		<option value="<%=subject%>|<%=centerId%>">
																			<%=city %> , <%=centerName%>, <%=locality %>
																		</option>
																	<% } %>
																	</optgroup>
																	
																	
																<% } %>
																</select>
																<script>
																	if(!window.subjectStateAndCenterMap){
																		window.subjectStateAndCenterMap = {}
																	}
																	if(!window.subjectIdMapping){
																		window.subjectIdMapping = {}
																	}
																	window.subjectIdMapping[<%=count%>] = '<%= subject %>'
																	window.subjectStateAndCenterMap[<%=count%>] = <%= new Gson().toJson(stateAndCenterMap)%>
																</script>
																<i class="fa-solid fa-magnifying-glass search-icon" title="Search for center." onClick="$('#center-selection-modal<%= count %>').modal('show');"></i>
																<div class="modal fade" id="center-selection-modal<%= count %>" role="dialog" aria-labelledby="center-selection-modal<%= count %>_label">
																  	<div class="modal-dialog modal-lg" role="document">
																    	<div class="modal-content">
																	      	<div class="modal-header">
																	        	<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
																	        	<h4 class="modal-title" id="center-selection-modal<%= count %>_label">Select center by state and city</h4>
																	      	</div>
																	      	<div class="modal-body">
																	      		<div class="container">
																		      		<div class="row">
																			      		<div class="col-md-4">
																		      				<label style="width:100%"  for="modal-state-select<%=count%>" >
																							  	State<br/>
																					      		<select 
																					      			style="width:100%"
																					      			class=modal-state-select 
																					      			id="modal-state-select<%=count%>" 
																					      			aria-select-id="<%=count %>"
																				      			>
																					      			<option value="">Select State</option>
																					      			<% 
																					
																										for (Map.Entry<String, Map<String, ExamCenterBean>> entry : stateAndCenterMap.entrySet()){
																											String stateName = entry.getKey();
																									%>
																					      					<option value="<%= stateName %>"><%= stateName %></option>
																			      					<%}%>
																					      		</select>
																							</label>
																			      		</div>
																			      		<div class="col-md-4">
																			      			<label style="width:100%"  for="modal-city-select<%=count%>" >
																			      				City<br/>
																					      		<select 
																					      			style="width:100%"
																					      			class="modal-city-select" 
																					      			id="modal-city-select<%=count%>" 
																					      			aria-select-id="<%=count %>"
																					      			disabled
																				      			>
																					      			<option value="">Select City</option>
																					      		</select>
																							</label>
																			      		</div>
																			      		<div class="col-md-4">
																			      			<label style="width:100%" for="modal-center-select<%=count%>" >
																			      				Center<br/>
																					      		<select 
																					      			style="width:100%"
																					      			class="modal-center-select" 
																					      			id="modal-center-select<%=count%>" 
																					      			aria-select-id="<%=count %>"
																					      			disabled
																				      			>
																					      			<option value="">Select Center</option>
																					      		</select>
																							</label>
																			      		</div>
																		      		</div>
																		      		<hr/>
																		      		<div class="row">
																		      		
																		      			<div class="col-sm-12 col-xs-12">
																			      			<h2 id='modal-center-name-text<%=count%>'>Please select a center.</h2>
																			      		</div>
																		      			<br/>
																		      			<div class="col-sm-4 col-xs-12" id="modal-center-state<%=count%>" style="display: none;">
																		      				<label for='modal-center-state-text<%=count%>'>State :</label>
																		      				<address id='modal-center-state-text<%=count%>'></address>
																		      			</div>
																		      			<div class="col-sm-4 col-xs-12" id="modal-center-city<%=count%>" style="display: none;">
																		      				<label for='modal-center-city-text<%=count%>'>City :</label>
																		      				<address id='modal-center-city-text<%=count%>'></address>
																		      			</div>
																		      			<div class="col-sm-4 col-xs-12" id="modal-center-locality<%=count%>" style="display: none;">
																		      				<label for='modal-center-locality-text<%=count%>'>Locality :</label>
																		      				<address id='modal-center-locality-text<%=count%>'></address>
																		      			</div>
																		      			<div class="col-sm-12 col-xs-12" id="modal-center-address<%=count%>" style="display: none;">
																		      				<label for='modal-center-address-text<%=count%>'> Address : </label>
																		      				<address id='modal-center-address-text<%=count%>'></address>
																		      			</div>
																		      			<div class="col-sm-12 col-xs-12" id="modal-center-maps-link<%=count%>" style="display: none;">
																		      				<label for='modal-center-maps-link-text<%=count%>'></label>
																		      				<address id='modal-center-maps-link-text<%=count%>'></address>
																		      			</div>
																		      		</div>
																	      		</div>
																	      	</div>
																	      	<div class="modal-footer">
																	      		<div class="row">
																		      		<div class="col-xs-6">
																		        		<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
																		      		</div>
																		      		<div class="col-xs-6">
																		        		<button type="button" onClick="updateSelectValue(<%= count %>)" class="btn btn-primary">Save changes</button>
																		      		</div>
																	      		</div>
																	      	</div>
																    	</div>
																  	</div>
																</div>
																
																<input type="hidden" name="selectedCenters" value="" id="selectedCenters<%=count%>">
																<%}else{%>
																	No Exam Center Available
																<%} %>
															</td>
															
															<td>	
																<%if(examCenters.size() > 0){ %>
																	<select class="form-control select-readonly" id="date<%=count%>" aria-select-id="<%=count%>" name="date" required="required">
																		<option value="">Select Date</option>
																	</select>
																<%}else{%>
																	No Exam Center Available
																<%} %>
															</td>
															
															<td>	
																<%if(examCenters.size() > 0){ %>
																	<select class="form-control select-readonly" id="dateTime<%=count%>" name="dateTime" required="required" onchange="setHiddenFieldValue(<%=count%>);">
																		<option value="">Select Time</option>
																	</select>
																<%} %>
															</td>
															
															
												        </tr> 
												        <% } %>  
														<script>
															var count = <%=count%>;
														</script>
												        
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
												        
												   <%  }catch(Exception e){
														
													}  %>  	
															
														
													</tbody>
												</table>
												</div>
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
												
												<%
												if("true".equals(hasReleasedSubjects)){ %>
													<input type="hidden"  name = "hasReleasedSubjects" value = "<%=hasReleasedSubjects%>"/>
												<%} %>
												
												<div class="form-group">
													<label class="control-label" for="submit"></label>
													<div class="controls">
														<input type="hidden" name="totalFeesAmount" value="<%=totalFees %>" />
														<%if("true".equals(ddPaid)) {%>
															<button name="submit" onclick="return validateForm();" formaction="saveSeatsForDD" class="btn btn-large btn-primary">Book my seat</button>
															<button id="cancel" name="cancel" class="btn btn-danger" formaction="selectSubjectsForm" formnovalidate="formnovalidate">Back</button>
														<%}else if("true".equals(hasApprovedOnlineTransactions)){ %>
															<button name="submit" onclick="return validateForm();" class="btn btn-large btn-primary" formaction="saveSeatsForOnline">Book my seat</button>
															<button id="cancel" name="cancel" class="btn btn-danger" formaction="selectSubjectsForm" formnovalidate="formnovalidate">Back</button>
														<%}else if("true".equals(hasReleasedNoChargeSubjects)){ %>
															<button name="submit" onclick="return validateForm();" class="btn btn-large btn-primary" formaction="saveSeatsForReleasedSeatsNoCharges">Book my seat</button>
															<button id="cancel" name="cancel" class="btn btn-danger" formaction="selectSubjectsForm" formnovalidate="formnovalidate">Back</button>
														<%}else if("true".equals(hasFreeSubjects)){ %>
															<button name="submit" onclick="return validateForm();" class="btn btn-large btn-primary" formaction="saveSeatsForFree">Book my seat</button>
															<button id="cancel" name="cancel" class="btn btn-danger" formaction="selectSubjectsForm" formnovalidate="formnovalidate">Back</button>
														<%}else if("true".equals(hasReleasedSubjects)){ %>
															<a href="javascript:void(0)" id="submit" name="submit" class="btn btn-large btn-primary" formaction="goToGateway" 
															>Proceed to Payment Gateway</a>
															<button id="cancel" name="cancel" class="btn btn-danger" formaction="selectSubjectsForm" formnovalidate="formnovalidate">Back</button>
														<%}else if(totalFees > 0){ %>
															<a href="javascript:void(0)" id="submit" name="submit" class="btn btn-large btn-primary" formaction="goToGateway">Proceed to Payment Gateway</a>
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
        <%@ include file="./common/paymentOption.jsp"%>
        <script>
        var formAction = "";
        var formTarget;
        function ChangeFormAction(action){
			$('#form1').attr('action',action);
			console.log("forma action : " + $('#form1').attr('action'));
			$('#form1').submit();
		}
       
    	
		$("#cancel").click(function(){
			ChangeFormAction($(this).attr('formaction'));
		});
		
		$("#submit").click(function(e){
			e.preventDefault();
			if(!validateForm('Online')){
				return false;
			}
			formaction = $(this).attr('formaction');
			for(let i=1;i <= count;i++){
				if($('#city' + i).val().trim() == ""){
					alert("Kindly Select Exam Center For Sr.No " + i);
					return false;
				}
				if($('#dateTime' + i).val().trim() == ""){
					alert("Kindly Select Date Time for Sr.No " + i);
					return false;
				}
			}
			 $("#paymentGatewayOptions").modal();
		}); 
		$(document).on('click','.selectPaymentGateway',function(){
			$("#paymentOption").attr("value",$(this).attr('data-paymentGateway'));
			ChangeFormAction(formaction + '?paymentOption=' + $(this).attr('data-paymentGateway'));
		});
		/* $('#selectPaytm').click(function(){
			$("#paymentOption").attr("value","paytm");
			ChangeFormAction(formaction + '?paymentOption=paytm');
		});
		
		$('#selectHdfc').click(function(){
			$("#paymentOption").attr("value","hdfc");
			ChangeFormAction(formaction + '?paymentOption=hdfc');
		});
		
		$('#selectPayu').click(function(){
			$("#paymentOption").attr("value","payu");
			ChangeFormAction(formaction + '?paymentOption=payu');
		}); */

		
   
        var listOfSubjectsAndCenters = <%= new Gson().toJson(subjectAvailableCentersMap) %>;
        	
		function checkForDifferentCentersSelected() {
			var listOfSelectedCities = {};
			$('select[id^="city"]').each(function () {
				if(this.value) {
					var centerId = this.value.substring(this.value.lastIndexOf("|")+1, this.value.length);
					var subject = this.value.substring(0, this.value.indexOf("|"));
					var centersList = listOfSubjectsAndCenters[subject];

					var center = {};
				
					for(thisCenterNum in centersList) {
						const thisCenter = centersList[thisCenterNum];
						if(thisCenter.centerId == centerId) {
							center = thisCenter;
						}
					}
					listOfSelectedCities[center.city.trim().toLowerCase()] = true;
				}
			});
			
			var numberOfUniqueCenteresSelected = 0;

			for( city in listOfSelectedCities ){
				numberOfUniqueCenteresSelected++;
			}
			
			if(numberOfUniqueCenteresSelected > 1) {
				$('#myModal').modal('show');
			}
		}
    
		$("select[name=selectedCenters]").change(function() {
		 	var centerList = document.getElementsByName('selectedCenters');
			
			/* var firstCity = "";
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
			}	 */

			checkForDifferentCentersSelected();
			
			
			//Check if same date time is selected for two subjects
			var cCheck = new Array ();
			for(var i = 0; i < centerList.length; ++i) {
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
		 
		 
		 $("select[name=city]").change(function() {
			 /* var centerList = document.getElementsByName('city');
			 
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
				} */
				checkForDifferentCentersSelected()
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
					
					if(bookedDateTimeArray.indexOf(dateTime) != -1 && selectionForReleasedSeats != "true" ){ //date time checked if earlier bookings were made. If it was made then throw error.
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
		 
		 
		 
		 for (i = 0; i < 21; i++) {
			 //Cannot have more than 21 failed subjects
				
			window.slotData = [];
			
			$('#city'+i).change(function(e){
				var value = $(this).val();

				var selectId = $(this).attr('aria-select-id');
				
				// empty date and date time objects and add placeholders
				var $dateTime = $("#dateTime"+selectId);
				$dateTime.empty().append('<option value="">Select Time</option>');
				$dateTime.addClass("select-readonly");
				
				var $date = $("#date"+selectId);
				$date.empty().append('<option value="">Loading....</option>');
				$date.addClass("select-readonly");
				
				if(value) {
					var centerId = value.substring(value.lastIndexOf("|")+1, value.length);
					var postData = {
						sapid : <%= sapId %>, 
						centerCode : centerId
					};
					$.ajax({
						type : "POST",
						contentType : "application/json",
						url : '/exam/m/getAvailableCentersForCityForOnlineExam/v2',   
						data : JSON.stringify(postData),
						success : function(data){
							window.slotData[selectId] = data;
							$date.empty().append('<option value="">Select Date</option>');
							$.each(data, function(key, value) {
								$optgroup = $('<optgroup label="Weekend ' + key + '"></optgroup>');
								if(value) {
									$.each(value, function(date, slotsList) {
										$option = $('<option value="' + date + '">' + moment(date).format("dddd, DD-MMM-YYYY") + ' </option>');
										$optgroup.append($option);
									});
									$date.append($optgroup);
							    }
							});

							$('optgroup').each(function() {
								var optgroup = this;
								$( 'option', this ).sort(function(a,b) {
									return $(a).text() > $(b).text();
								}).appendTo(optgroup);
							});


							$date.removeClass("select-readonly");
						},
						error : function(e) {
							
							alert("Please Refresh The Page.");
							
							// console.log("ERROR: ", e);
							$date.empty().append('<option value="">Select Date</option>');
							display(e);
						}
					});
				} else {
					$date.empty().append('<option value="">Select Date</option>');
				}
			 });


			 $('#date'+i).change(function(e){
				var selectedDate = $(this).val();
				
				var selectId = $(this).attr('aria-select-id');
				var $dateTime = $("#dateTime"+selectId);


				if(selectedDate) {
					$dateTime.empty().append('<option value="">Select Time</option>');
					$dateTime.removeClass("select-readonly");
				} else {
					$dateTime.empty().append('<option value="">Select Time</option>');
					$dateTime.addClass("select-readonly");
				}

				
				var weekendsAndSlots = window.slotData[selectId];

				$.each(weekendsAndSlots, function(weekendName, datesAndSlots) {

					$.each(datesAndSlots, function(date, slots) {
						if(date == selectedDate) {
							slots.sort(function(o1, o2) {
								var t1 = o1.starttime.toLowerCase();
								var t2 = o2.starttime.toLowerCase();
								return t1 > t2 ? 1 : t1 < t2 ? -1 : 0;
							});
							slots.forEach(function(slotDetails) {
								var dropDownValue = slotDetails.starttime  + "|" + slotDetails.date + "|" + slotDetails.city;
								var dropDownLabel = slotDetails.starttime + " (" + slotDetails.available + "/" + slotDetails.capacity + ")";
								
								var $option = $('<option value="' + dropDownValue + '">' + dropDownLabel + ' </option>');
								$dateTime.append($option);
							});
						}
					});
				});
			 });
			 /* $("#dateTime"+i).depdrop({
		        url: '/exam/getAvailableCentersForCityForOnlineExam',
		        depends: ['city'+i]
		    }); */
		}

		function updateSelectValue(count) {
			$in = $('#modal-center-select' + count)
			$out = $('#city' + count)
			
			if($in.val()) {
				// change value
				$out.val($in.val())
				
				// update select2 value
				$out.trigger('change');

				// close modal
				$('#center-selection-modal' + count).modal('hide');
			} else {
				alert('Please select a center first!')
			}
		}
	    </script>
		<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.6-rc.0/js/select2.min.js"></script>
		<script>

			$(document).ready(function() {
			    $('.center-select').select2();
			    $('.modal-state-select').select2();
			    $('.modal-city-select').select2();
			    $('.modal-center-select').select2();

				for (i = 0; i < 21; i++) {
					
					//Cannot have more than 21 failed subjects
					
					// modals give issues unless they are at the top level.
					$('#center-selection-modal' + i).appendTo('body');

					
					$("#modal-state-select" + i).change(function() {
						let selectId = $(this).attr('aria-select-id')
						stateChanged(selectId)
					});


					$("#modal-city-select" + i).change(function() {
						let selectId = $(this).attr('aria-select-id')
						cityChanged(selectId)
					});

					$("#modal-center-select" + i).change(function() {
						let selectId = $(this).attr('aria-select-id')
						centerChanged(selectId)
					});

					$("#city" + i).change(function() {
						var selectId = $(this).attr('aria-select-id');
						
						var selectedCenterValue = $(this).val();

						// console.log(selectedCenterValue);
						var selectedCenterId = selectedCenterValue.substring(selectedCenterValue.lastIndexOf("|")+1, selectedCenterValue.length);


						for(var j = 0; j < 23; j++) {
							// if node exists and doesnt have a value already
							if($("#city" + j).length && !$("#city" + j).val()) {
								var subjectName = subjectIdMapping[j];

								if(subjectName) {
									$("#city" + j).val(subjectName + "|" + selectedCenterId);
									if($("#city" + j).val()) {
										$("#city" + j).trigger('change');
									}
								}
							}
						}

						var centersAndState = subjectStateAndCenterMap[selectId];


						var $modalState = $("#modal-state-select" + selectId);
						var $modalCity = $("#modal-city-select" + selectId);
						var $modalCenter = $("#modal-center-select" + selectId);
						var subjectName = subjectIdMapping[selectId];

						if(!selectedCenterValue) {
							$modalState.val('')
							$modalState.trigger('change');
							
							$modalCity.val('')
							$modalCity.trigger('change');
							
							$modalCenter.val('')
							$modalCenter.trigger('change');
						}
						
					    for (key in centersAndState) {
							if(centersAndState[key][selectedCenterId]) {
								var center = centersAndState[key][selectedCenterId];

								$modalState.val(toTitleCase(center.state))
								$modalState.trigger('change');
								
								$modalCity.val(toTitleCase(center.city))
								$modalCity.trigger('change');
								
								$modalCenter.val(subjectName + '|' + center.centerId)
								$modalCenter.trigger('change');
						    }
					    }
					})
				}

				function stateChanged(selectId) {
					var $state = $("#modal-state-select" + selectId);
					var $city = $("#modal-city-select" + selectId);
					var $center = $("#modal-center-select" + selectId);
					var stateAndCenters = subjectStateAndCenterMap[selectId];

					var selectedState = $state.val();
					var centersAndState = stateAndCenters[selectedState];

					$city.empty().append('<option value="">Select City</option>');

					resetCenterSelects(selectId);

					var cityAndCenters = {};
					
				    for (key in centersAndState) {
					    var center = centersAndState[key];
					    
					    var stateEqual = ucEquals(center.state, selectedState);
				        if ( stateEqual ) {
				        	var cityName = toTitleCase(center.city);
							cityAndCenters[cityName] = cityName
				        }
				    }

				    for(cityName in cityAndCenters) {
					    var $option = $('<option value="' + cityName + '">' + cityName + '</option>');
						$city.append($option);
				    }

					if(selectedState) {
						$city.prop('disabled', false);
					} else {
						$city.prop('disabled', true);
					}
				}
				
				function cityChanged(selectId) {
					var $state = $("#modal-state-select" + selectId);
					var $city = $("#modal-city-select" + selectId);
					var $center = $("#modal-center-select" + selectId);
					var stateAndCenters = subjectStateAndCenterMap[selectId];
					var subjectName = subjectIdMapping[selectId];
					
					var selectedState = $state.val();
					var selectedCity = $city.val();
					var centersAndState = stateAndCenters[selectedState];
					
					resetCenterSelects(selectId);

					var centersInCityAndState = [];
				    for (key in centersAndState) {
					    var center = centersAndState[key];
					    var stateEqual = ucEquals(center.state, selectedState);
					    var cityEqual = ucEquals(center.city, selectedCity);
				        if ( stateEqual && cityEqual ) {
				        	centersInCityAndState.push(center);
				        }
				    }
				    
					centersInCityAndState.forEach(function(center) {
						var $option = $('<option value="' + subjectName + '|' + center.centerId + '">' + center.examCenterName + ' </option>');
						$center.append($option);
					});

					if(selectedCity) {
						$center.prop('disabled', false);
					}
				}
				
				function centerChanged(selectId) {
					var $state = $("#modal-state-select" + selectId);
					var $city = $("#modal-city-select" + selectId);
					var $center = $("#modal-center-select" + selectId);
					var stateAndCenters = subjectStateAndCenterMap[selectId];
					var subjectName = subjectIdMapping[selectId];
					
					var selectedState = $state.val();
					var selectedCity = $city.val();
					var selectedCenterValue = $center.val();

					if(selectedCenterValue) {
						var selectedCenterId = selectedCenterValue.substring(selectedCenterValue.lastIndexOf("|")+1, selectedCenterValue.length);
						var centersAndState = stateAndCenters[selectedState];

						for (key in centersAndState) {
						    var center = centersAndState[key];
						    var stateEqual = ucEquals(center.state, selectedState);
						    var cityEqual = ucEquals(center.city, selectedCity);
						    var centerEqual = ucEquals(center.centerId, selectedCenterId);
						    if ( stateEqual && cityEqual && centerEqual ) {
					        	$('#modal-center-name-text'+selectId).html(center.examCenterName);

					        	if(center.state) {
						        	$('#modal-center-state-text'+selectId).html(center.state);
						        	$('#modal-center-state'+selectId).show();
					        	} else {
						        	$('#modal-center-state-text'+selectId).html('');
						        	$('#modal-center-state'+selectId).hide();
					        	}

					        	if(center.city) {
						        	$('#modal-center-city-text'+selectId).html(center.city);
						        	$('#modal-center-city'+selectId).show();
					        	} else {
						        	$('#modal-center-city-text'+selectId).html('');
						        	$('#modal-center-city'+selectId).hide();
					        	}
					        	
					        	if(center.locality) {
						        	$('#modal-center-locality-text'+selectId).html(center.locality);
						        	$('#modal-center-locality'+selectId).show();
					        	} else {
						        	$('#modal-center-locality-text'+selectId).html('');
						        	$('#modal-center-locality'+selectId).hide();
					        	}

					        	if(center.address) {
						        	$('#modal-center-address-text'+selectId).html(center.address);
						        	$('#modal-center-address'+selectId).show();
					        	} else {
						        	$('#modal-center-address-text'+selectId).html('');
						        	$('#modal-center-address'+selectId).hide();
					        	}
					        	
					        	if(center.googleMapUrl) {
						        	$('#modal-center-maps-link-text'+selectId).html('<a href="' + center.googleMapUrl + '" target="_blank">View on Map</a>');
						        	$('#modal-center-maps-link'+selectId).show();
					        	} else {
						        	$('#modal-center-maps-link-text'+selectId).html('');
						        	$('#modal-center-maps-link'+selectId).hide();
					        	}
					        }
					    }
					}
			    }
			});

			function ucEquals(s1, s2) {
				// helper function. just checks if variables exist and are equal in UPPERCASE
				return s1 && s2 && s1.trim().toUpperCase() == s2.trim().toUpperCase();
			}
			
			function resetCenterSelects(selectId) {

	        	$('#modal-center-name-text'+selectId).html('Please select a center.');
	        	$("#modal-center-select" + selectId).empty().append('<option value="">Select Center</option>');
	        	$("#modal-center-select" + selectId).prop('disabled', true);

	        	$('#modal-center-state'+selectId).hide();
	        	$('#modal-center-city'+selectId).hide();
	        	$('#modal-center-locality'+selectId).hide();
	        	$('#modal-center-address'+selectId).hide();
	        	$('#modal-center-maps-link'+selectId).hide();
			}
			function toTitleCase(text) {
				var titlecase = text.toLowerCase().split(' ').map(function(s){
					return s.charAt(0).toUpperCase() + s.substring(1);
				}).join(' ');
				return titlecase;
			}
		</script>
	
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