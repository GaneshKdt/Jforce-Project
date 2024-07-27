<!DOCTYPE html>
<html class="no-js">
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix = "fn" uri = "http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix = "form" uri = "http://www.springframework.org/tags/form" %>
<jsp:include page="jscss.jsp">
	<jsp:param value="Session Tracks" name="title" />
</jsp:include>

<style>
.accordion {
  background-color: #eee;
  color: #444;
  cursor: pointer;
  padding: 18px;
  width: 100%;
  border: none;
  text-align: center;
  outline:none;
  font-size: 15px;
  transition: 0.4s;
  font-weight:bold;
}

.active, .accordion:hover {
  background-color: #ccc;
}

.panel {
  padding: 0 18px;
  background-color: white;
  max-height: 0;
  overflow: hidden;
  transition: max-height 0.5s ease-out;
}
.accordion:after {
  content: '\02795'; /* Unicode character for "plus" sign (+) */
  font-size: 13px;
  color: #777;
  float: right;
  margin-left: 5px;
}

.active:after {
  content: "\2796"; /* Unicode character for "minus" sign (-) */
}
</style>
<body class="inside">
<%@ include file="header.jsp"%>
	<section class="content-container login">
       <div class="container-fluid customTheme">
        <div class="row"><legend>Session Tracks Color</legend></div>
        <%@ include file="messages.jsp"%>
        
        <!-- start accordian -->
        <button class="accordion">Update Track Details</button>
        <div class="panel">
         <form:form action="setSessionTrackColor" method="post" modelAttribute="sessionTrack">
        	<div class="panel-body">
				<div class="col-md-6 column">
		        	<div class="form-group">
		        		<label>Select Track</label>
						<form:select id="track" path="track"  class="form-control" required="true" itemValue="${session.track}">
							 <form:option value="">Select Track</form:option>
						</form:select>
					</div> 
				
					<div class="form-group">
		        	<label for="colorpicker">Select Track Color</label>
                        <form:input type="color" id="colorpicker" value="#BF2222" path="hexCode" class="form-control" style="height:50px"/>
		        	</div>
	        	
		        	<div class="form-group" >
		        		<label>Select Font Color (Web)</label>
						<form:select id="fontColor" path="fontColor" type="text" class="form-control" required="required" 
 							 itemValue="${session.fontColor}" > 
 							<form:option value="">Select Font Color</form:option>
 							<form:option value="#000000">Default</form:option>
 							<form:option value="#000000">Black</form:option>
 							<form:option value="#FFFFFF">White</form:option>
 						</form:select>
					</div>
					
					<div class="form-group" >
		        		<label>Select Border Color (Web)</label>
						<form:select id="border" path="border" type="text" class="form-control" required="required" 
 							 itemValue="${session.border}" > 
 							<form:option value="">Select Border Color</form:option>
 							<form:option value="#000000">Default</form:option>
 							<form:option value="#000000">Black</form:option>
 							<form:option value="#FFFFFF">White</form:option>
 						</form:select>
					</div>
					
		        	<div class="form-group">
		        		<button id="submit" name="submit" class="btn btn-primary btn-sm" formaction="updateSessionTrackColor">Update Color</button>
		        	</div>
		        </div>
		  	</div>
        	 
        </form:form>
       </div>

		<button class="accordion">Add New track Details </button>
		<div class="panel">
           <form:form action="setSessionTrackColor" method="post" modelAttribute="sessionTrack" onSubmit="return validateForm();">
        	<div class="panel-body">
				<div class="col-md-6 column">
		        	<div class="form-group">
		        		<label>Add Track</label>
						<form:input id="trackForAdd" path="track" type="text" class="form-control" required="true" />
					</div> 
				
					<div class="form-group">
		        	<label for="colorpicker">Select Track Color</label>
                        <form:input type="color" id="colorpicker" value="#76590B" path="hexCode" class="form-control" style="height:50px"/>
		        	</div>
	        	
		        	<div class="form-group" >
		        		<label>Select Font Color (Web)</label>
						<form:select id="fontColor" path="fontColor" type="text" class="form-control" required="required" 
 							 itemValue="${session.fontColor}" > 
 							<form:option value="">Select Font Color</form:option>
 							<form:option value="#000000">Default</form:option>
 							<form:option value="#000000">Black</form:option>
 							<form:option value="#FFFFFF">White</form:option>
 						</form:select>
					</div>
					
					<div class="form-group" >
		        		<label>Select Border Color (Web)</label>
						<form:select id="border" path="border" type="text" class="form-control" required="required" 
 							 itemValue="${session.border}" > 
 							<form:option value="">Select Border Color</form:option>
 							<form:option value="#000000">Default</form:option>
 							<form:option value="#000000">Black</form:option>
 							<form:option value="#FFFFFF">White</form:option>
 						</form:select>
					</div>
					
		        	<div class="form-group">
		        		<button id="submit" name="submit" class="btn btn-primary btn-sm" formaction="setSessionTrackColor">Set Color</button>
		        	</div>
		        </div>
		  	</div>
        	 
        </form:form>
		</div>
        
        <!-- End Accordian -->
       
        <!-- Track details table Start -->
        <c:if test="${fn:length(trackDetails) gt 0}">
			<legend>&nbsp;Existing Tracks<font size="2px"> (${fn:length(trackDetails)} Records Found) </font></legend>
				<div class="table-responsive">
					<table id="dataTable" class="table table-striped" style="font-size:12px">
						<thead>
							<tr>
								<th>Sr. No.</th>
								<th>Track Name</th>
								<th>Hex Code</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="bean" items="${trackDetails}" varStatus="status">
								<tr>
		            				<td><c:out value="${status.count}" /></td>
		            				<td><c:out value="${bean.track}" /></td>
		            				<td style="background-color: ${bean.hexCode} !important ;width:20% ; color: ${bean.fontColor}; border-color:${bean.border}">
		            				<c:out value="${bean.hexCode}" /></td>
		            			</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
		</c:if>
       <!-- track details table end  -->
       </div>
 	</section>
 <!-- footer start -->
	<jsp:include page="footer.jsp" /> 
<!-- footer End -->
	<script src="${pageContext.request.contextPath}/assets/js/sessionTrack.js"></script>
	

	
</body>
</html>