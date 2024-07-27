<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
	<jsp:param value="Auto Sessions Scheduling" name="title" />
</jsp:include>

<link href="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.6-rc.0/css/select2.min.css" rel="stylesheet" />
<!-- <link type="text/css" href="//gyrocode.github.io/jquery-datatables-checkboxes/1.2.12/css/dataTables.checkboxes.css" rel="stylesheet" /> -->
<!-- <link href="https://cdn.datatables.net/1.11.3/css/jquery.dataTables.min.css" rel="stylesheet" /> -->
<!-- <link href="https://cdn.datatables.net/select/1.3.3/css/select.dataTables.min.css" rel="stylesheet" /> -->

<style>
	.select2-search__field {
		color:black;
	}
	
	.dataTables .btn-group {
		width: auto;
	} 

	.customTheme .btn-group {
    	width: auto;
	}
</style>

<body class="inside">

<%@ include file="header.jsp"%>
	<section class="content-container login">
        <div class="container-fluid customTheme">
        	<div class="row"><legend>Auto Sessions Scheduling</legend></div>
        	<%@ include file="messages.jsp"%>
        	<form:form  action="autoSessionScheduling" method="post" modelAttribute="searchBean">
        		<fieldset>
					<div class="panel-body">
						<div class="col-md-6 column">
							
							<div class="form-group">
								<form:select id="year" path="year" type="text" placeholder="Year" class="form-control" required="true" 
									itemValue="${searchBean.year}">
									<form:option value="">Select Academic Year</form:option>
									<form:options items="${yearList}" />
								</form:select>
							</div>
						
							<div class="form-group">
								<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" required="true" itemValue="${searchBean.month}">
									<form:option value="">Select Academic Month</form:option>
									<form:option value="Jan">Jan</form:option>
									<form:option value="Jul">Jul</form:option>
								</form:select>
							</div>
							
							<div class="form-group">
								<form:select id="sessionType" path="sessionType" class="form-control" itemValue="${session.sessionType}"> 
									<form:option value="">Select Session Type</form:option>
									<c:forEach items="${sessionTypesMap}" var="sessionType">
										<form:option value="${sessionType.key}"> ${sessionType.value} </form:option>
									</c:forEach>
								</form:select>
							</div>
					
							<label>Session Start Date</label>
							<div class="form-group">
								<form:input id="fromDate" path="fromDate" type="date" placeholder="Session Start Date" class="form-control" required="true" itemValue="${searchBean.fromDate}" />
							</div>
							
							<label>Session End Date</label>
							<div class="form-group">
								<form:input id="toDate" path="toDate" type="date" placeholder="Session To Date" class="form-control" required="true" itemValue="${searchBean.toDate}" />
							</div>
				
							<div class="form-group" style="overflow: visible;">
								<form:select id="subjectCode" path="subjectCode" class="combobox form-control" required="true" itemValue="${session.subjectCode}"> 
									<form:option value="">Select Subject Code</form:option>
									<c:forEach items="${subjectCodeMap}" var="subjectMap">
										<form:option value="${subjectMap.key}">${subjectMap.key} ( ${subjectMap.value} )</form:option>
									</c:forEach>
								</form:select>
							</div>
							
							<div class="form-group" style="overflow: visible;">
								<form:select id="facultyId" path="facultyId" class="combobox form-control" required="true" itemValue="${session.facultyId}">
									<form:option value="">Type OR Select Faculty</form:option>
									<c:forEach items="${facultyIdMap}" var="facultyMap">
										<form:option value="${facultyMap.key}">${facultyMap.key} ( ${facultyMap.value} )</form:option>
									</c:forEach>
								</form:select>
							</div>
							
							<div class="form-group" >
								<form:select id="track" path="track" type="text" placeholder="Selet Track" class="form-control" itemValue="${session.track}" > 
									<form:option value="">Select Track</form:option>
									<form:options items="${trackList}" />
								</form:select>
							</div>
							
							<div class="form-group" >
								<form:select id="facultyLocation" path="facultyLocation" class="form-control" required="true" itemValue="${session.facultyLocation}"> 
									<form:option value="">Select Faculty Location</form:option>
									<form:options items="${locationList}" />
								</form:select>
							</div>
							
							<div class="form-group">
								<form:select id="corporateName" path="corporateName" type="text" placeholder="corporateName" class="form-control" itemValue="${session.corporateName}">
									<form:option value="Retail">Retail</form:option>
									<form:option value="Verizon">Verizon</form:option>
									<form:option value="SAS">SAS</form:option>
									<form:option value="Diageo">Diageo</form:option>
									<form:option value="All">All</form:option>
								</form:select>
							</div>

							<!-- 						
							<div class="form-group"> 
								<form:select id="day" path="day" type="text" class="multipleSelect form-control" required="true"
									multiple = "multiple" data-placeholder="Select Day" itemValue="${searchBean.day}">
									<form:option value="">Select Day</form:option>
									<form:option value="Monday">Monday</form:option>
									<form:option value="Tuesday">Tuesday</form:option>
									<form:option value="Wednesday">Wednesday</form:option>
									<form:option value="Thursday">Thursday</form:option>
									<form:option value="Friday">Friday</form:option>
									<form:option value="Saturday">Saturday</form:option>
									<form:option value="Sunday">Sunday</form:option>
								</form:select>
							</div>
							-->
							
							<div class="form-group" >
								<form:select id="dayTime" path="dayTime" type="text" class="multipleSelect form-control" required="true" 
									 multiple = "multiple" data-placeholder="Select Start Day Time" itemValue="${searchBean.dayTime}" > 
									<form:option value="">Select Start Day Time</form:option>
									<form:options items="${sessionTimeList}" />
								</form:select>
							</div>
							
							<div class="form-group">
								<form:input id="slots" path="slots" placeholder="Enter No. of Sessions" type="number" 
									class="form-control" required="true" itemValue="${searchBean.slots}"/>
							</div>

							<button id="submit" name="submit" class="btn btn-sm btn-primary" formaction="autoSessionSchedule">Generate Sessions</button>
							
						</div>
					</div>
				</fieldset>
        	</form:form>
        	
        	<c:choose>
				<c:when test="${rowCount > 0}">
				<form:form action="addAllAutoSession" method="post" modelAttribute="searchBean">
					<legend>&nbsp;Scheduled Sessions<font size="2px"> (${rowCount} Records Found) &nbsp; <a href="downloadAutoSessionToBeSchedule">Download to Excel</a> </font></legend>
					<div class="table-responsive">
						<table class="table table-striped" style="font-size:12px">
							<thead>
								<tr>
									<th style="width: 80px;"> <input class="checkAll" type="checkbox" id="selectAll" style="width: 15px; height: 30px;"/>&nbsp;All</th>
									<th>Sr.No</th>
									<th>Year</th>
									<th>Month</th>
									<th>Subject</th>
									<th>SubjectCode</th>
									<th>SessionName</th>
									<th>Date</th>
									<th>Day</th>
									<th>Start Time</th>
									<th>Faculty</th>
									<th>Faculty Location</th>
									<th>Corporate Name</th>
									<th>Track</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="bean" items="${availableSessionsList}" varStatus="status">
						        	<tr value="${status.count}~${bean.year}~${bean.month}~${bean.subject}~${bean.subjectCode}~${bean.sessionName}~${bean.date}~${bean.day}~${bean.startTime}~${bean.facultyId}~${bean.facultyLocation}~${bean.corporateName}~${bean.track}"
						        		class="myDiv"  id="${status.count}">
						        		<td><input class="checkBox" type="checkbox" onclick="setCheckAll()" name="sessionList" value="${status.count}" style="width: 15px; height: 30px;"/></td>
							            <td><c:out value="${status.count}" /></td>
							            <td><c:out value="${bean.year}" /></td>
										<td><c:out value="${bean.month}" /></td>
										<td><c:out value="${bean.subject}" /></td>
										<td><c:out value="${bean.subjectCode}" /></td>
										<td><c:out value="${bean.sessionName}" /></td>
										<td><c:out value="${bean.date}" /></td>
										<td><c:out value="${bean.day}" /></td>
										<td><c:out value="${bean.startTime}" /></td>
										<td><c:out value="${bean.facultyId}" /></td>
										<td><c:out value="${bean.facultyLocation}" /></td>
										<td><c:out value="${bean.corporateName}" /></td>
										<td><c:out value="${bean.track}" /></td>
							      	</tr>
							   	</c:forEach>
							</tbody>
						</table>
					</div>
					<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="addAllAutoSession">Add Session</button>
					</form:form>
				</c:when>
			</c:choose>
        </div>
   	</section>
   	
  	<jsp:include page="footer.jsp" />
  	
  	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.6-rc.0/js/select2.min.js"></script>
  	<script src="${pageContext.request.contextPath}/assets/js/bootstrap.js"></script>
  	<script src="${pageContext.request.contextPath}/assets/js/jquery.tabledit.js"></script> 
    <script src="${pageContext.request.contextPath}/resources_2015/js/vendor/jquery-ui.min.js"></script>  
	<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js" ></script>
	<script src="${pageContext.request.contextPath}/resources_2015/js/vendor/dataTables.bootstrap.js"></script>
	<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js" ></script>
	
	<script>
		$(document).ready(function() {
		    $('.multipleSelect').select2();
		});
	</script>

	
	<script>
	$(document).ready( function () {				
		let id = "";
	   
		$(".dataTable").on('click','tr',function(e){
			//e.preventDefault();	
			var str = $(this).attr('value');
			id = str.split('~');
			console.log(id);
		}); 
	    
	    $('.dataTable').Tabledit({
	    	columns: {
				  identifier: [ [0, 'id'] ],  
				  editable: [					  			
					  			[5, 'sessionName']
				  			]
				},
				// link to server script
				// e.g. 'ajax.php'
				url: "",
				// class for form inputs
				inputClass: 'form-control input-sm',
				// // class for toolbar
				toolbarClass: 'btn-toolbar',
				// class for buttons group
				groupClass: 'btn-group btn-group-sm',
				// class for row when ajax request fails
				 dangerClass: 'success',
				// class for row when save changes
				warningClass: 'warning',
				// class for row when is removed
				mutedClass: 'text-muted',
				// trigger to change for edit mode.
				// e.g. 'dblclick'
				eventType: 'click',
				// change the name of attribute in td element for the row identifier
				rowIdentifier: 'id',
				// activate focus on first input of a row when click in save button
				autoFocus: true,
				// hide the column that has the identifier
				hideIdentifier: false,
				// activate edit button instead of spreadsheet style
				editButton: true,
				// activate delete button
				deleteButton: false,
				// activate save button when click on edit button
				saveButton: true,
				// activate restore button to undo delete action
				restoreButton: true,
				onDraw: function() {
					$('.dataTable').DataTable({
						
					}); 
				},
				
				onAjax: function(action, serialize) {
					
// 					serialize['id'] = id[0];
					serialize['year'] = id[1];
					serialize['month'] = id[2];
					serialize['subject'] = id[3];
					serialize['subjectCode'] = id[4];
					serialize['date'] = id[6];
					serialize['day'] = id[7];
					serialize['startTime'] = id[8];
					serialize['facultyId'] = id[9];
					serialize['facultyLocation'] = id[10];
					serialize['corporateName'] = id[11];
					serialize['track'] = id[12];
					let body = JSON.stringify(serialize);
					
					$.ajax({
						type : "POST",
						url : "addAutoSession",
						contentType : "application/json",
						data : body,
						dataType : "json",
						success : function(response) {
							if (response.status == "Success") {
								alert('Entries Saved Successfully')
							}else {
								alert('Entries Failed to update : ' + response.message)
							}
						}
					});
				}
			});
		    $(".tabledit-toolbar").attr("style","text-align:center;margin-left:-80px;");
		    $(".dataTables_filter").attr("style","display:inline-flex;justify-content:space-between;");
		    $(".dataTables_filter").html("<span style='color: #333333;font-weight: 600;margin-top: 0.6rem;margin-right: 2rem;'>Search:</span><input type='search' class='form-control input-sm' placeholder='' aria-controls='DataTables_Table_0'>");
	    	    
		});		
	</script>
	
	<script type="text/javascript">
		// for select all	
		$('#selectAll').click(function (e) {
		    $(this).closest('table').find('td input:checkbox').prop('checked', this.checked);
		});
	
		$('.checkBox').click(function (e) {
			console.log("checkBox clicked");
			var ischecked= $(this).is(':checked');
			console.log(ischecked);
			if(!ischecked){
				$("#selectAll").prop("checked", false);
			}
		   
		});
	
		function setCheckAll() {
		  document.querySelector('input.checkAll').checked =
		     document.querySelectorAll('.checkBox').length ==
		     document.querySelectorAll('.checkBox:checked').length;
		}
	</script>

</body>
</html>