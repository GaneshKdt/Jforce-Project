<html class="no-js"> <!--<![endif]-->

<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.8.2/css/all.css" integrity="sha384-oS3vJWv+0UjzBfQzYUhtDYW+Pj2yciDJxpsK1OYPAYjqT085Qq/1cq5FLXAZQ7Ay" crossorigin="anonymous">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<jsp:include page="../jscss.jsp">
<jsp:param value="Timebound Failed Subject Criteria" name="title" />
</jsp:include>
<body class="inside">

<%@ include file="../header.jsp"%>
<section class="content-container login">
	<div class="container-fluid customTheme">
		<div class="row"><legend>Timebound Failed Subject Criteria</legend></div>
		<form id="form">
		<div class="panel-body">
					<div id='alertTabMsg' class="alert alert-danger" style="display:none">Please DO NOT CLOSE the tab until success or error message is displayed.</div>
					<div id="successMsg" style="display:none"></div>
					<div id="errorMsg" style="display:none"></div>
				<div class="row">
				<div class="col-sm-6">
					<div class="form-group">
					<label for="sel1">Select Program Type:</label>
					<select name="programType" id="programType" class="form-control programType">
					<option value="">-- select Program Type --</option>
					<option value="MBA - WX">MBA - WX</option>
					<option value="M.Sc. (AI & ML Ops)">M.Sc. (AI & ML Ops)</option>
					<option value="M.Sc. (AI)">M.Sc. (AI)</option>
					<option value="Modular PD-DM">Modular PD-DM</option>
					</select>
					</div>
				</div>
				<div class="col-sm-6">
					<div class="form-group">
					<label for="sel1">Select Failed Subject Count Criteria:</label>
					<select name="failedSubjectCount" id="failedSubjectCount" class="form-control failedSubjectCount">
					<option value="">-- Select Failed Subject Count Criteria --</option>
					<option value="1">1</option>
					<option value="2">2</option>
					<option value="3">3</option>
					<option value="4">4</option>
					<option value="5">5</option>
					</select>
					</div>
				</div>
				</div>
				<br>
				<div class="row">
					<div class="col-md-6 column">
						<input type="hidden" id="createdBy" name="createdBy" value="${userId}">
						<input type="hidden" id="lastModifiedBy" name="lastModifiedBy" value="${userId}">
						<button id="submit" name="submit" class="btn btn-large btn-primary" onClick="failedSubjectCriteria(event)">
						 Add Failed Subject Criteria
						</button>
						<div id='theImg' style="display:none">
							<img  src='/exam/resources_2015/gifs/loading-29.gif' alt="Loading..." style="height:40px" />
						</div>
					</div>
				</div>
				</div>
				</form>
				
			
		<div class="clearfix"></div>
	
			<legend>Existing Failed Criteria Entries</legend>
			<div class="column">
			<div class="table-responsive">
				<table class="table table-striped table-hover dataTables" style="font-size:12px; width: 100%">
					<thead>
					<tr>
						<th>Sr.No</th>
						<th>Consumer Program Structure Id</th>
						<th>Failed Subject Count Criteria</th>
					</tr>
					</thead>
					
					<tbody>
						<c:forEach var="bean" items="${failedCriteriaDetails}" varStatus="status">
							<tr value="${bean.consumerProgramStructureId}~${bean.failedSubjectCount}">
								<td><c:out value="${status.count}" /></td>
								<td><c:out value="${bean.consumerProgramStructureId}"></c:out></td>
								<td><c:out value="${bean.failedSubjectCount}"></c:out></td>	
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div> 
    		</div> 
   
    
	</div>
</section>

<jsp:include page="../footer.jsp" />

<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/bootstrap.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery.tabledit.js"></script>

	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/jquery-ui.min.js"></script>
	<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
	<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js"></script>

<!-- 	<script src="https://code.jquery.com/jquery-3.4.1.min.js"></script> -->
	<script src="https://unpkg.com/multiple-select@1.3.1/dist/multiple-select.min.js"></script>

<script>
$(document).ready( function () {
	let id = "";
	var userId=document.getElementById("createdBy").value;
     $(".dataTables").on('click','tr',function(e){
    	 e.preventDefault();
	    str = $(this).attr('value');
	    id=str.split('~');
	}); 
    
    $('.dataTables').Tabledit({
    	columns: {
			  identifier: [0, 'id'],                 
			  editable: [[2, 'failedSubjectCount','{"1":"1","2":"2","3":"3","4":"4","5":"5"}']]
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
			 dangerClass: 'warning',
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
				$('.dataTables').DataTable(); 
			},
			onAjax: function(action, serialize) {
				
				serialize['consumerProgramStructureId'] = id[0];
				serialize['lastModifiedBy'] = userId;
				let body = JSON.stringify(serialize);
				var url = ''
				if (action === 'edit'){
					urlEdit = '/exam/m/updateFailedCriteria'
				}

				$.ajax({
					type : "POST",
					url : urlEdit,
					contentType : "application/json",
					data : body,
					dataType : "json",	 
					success : function(response) {
						alert(response.message)
					},
				error:function(err)
				{
					alert(err)
				}
				});
			}
		});
    $(".tabledit-toolbar.btn-toolbar").removeAttr("style");	
	$(".btn-group.btn-group-sm").removeAttr("style");	
	$(".tabledit-toolbar.btn-toolbar").attr("style","margin-right: 60px"); 
	});
</script>

<script>
function failedSubjectCriteria(e)
{
	e.preventDefault();
	var count=document.getElementById("failedSubjectCount").value;
	var programType=document.getElementById("programType").value;
	var userId=document.getElementById("createdBy").value;
	
	if(count=="" || programType=="")
	{
		alert("Program Type and Failed Subject Criteria is mandatory");
		return;
	}
	
	var dataJson=JSON.stringify({
   	 createdBy:userId,
	 lastModifiedBy:userId,
	 failedSubjectCount:count,
	 programType:programType
     });

	hideSubmitBtn();
	$.ajax({
		url:'/exam/m/failedSubjectCriteria',
		type : 'POST',
		 contentType: 'application/json',
		 cache : false,
	     processData : false,
	     data :dataJson,
		success:function(response){
			showSubmitBtn();
			if(response.flag!=null && response.flag=='error')
			{
				document.getElementById("errorMsg").style.display="block";
				const div='<div class="alert alert-danger alert-dismissible" id="errorMsgDescription"></div>';
				const button='<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times; </button>';
				document.getElementById("errorMsg").innerHTML=div;
				document.getElementById("errorMsgDescription").innerHTML=response.message+button;
			}
			else if(response.flag!=null && response.flag=='success')
			{		
				document.getElementById("successMsg").style.display="block";
				const div='<div class="alert alert-success alert-dismissible" id="successMsgDescription"></div>';
				const button='<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times; </button>';
				document.getElementById("successMsg").innerHTML=div;
				document.getElementById("successMsgDescription").innerHTML=response.message+button;
				window.location.reload(); 
			}
		},
		error:function(err){
			showSubmitBtn();
			console.log(err)
			}
		})
}

function hideSubmitBtn()
{
	document.getElementById("submit").style.display="none";
	document.getElementById("theImg").style.display="block";
	document.getElementById("alertTabMsg").style.display="block";
	document.getElementById("successMsg").style.display="none";
	document.getElementById("errorMsg").style.display="none";
}

function showSubmitBtn()
{
	document.getElementById("submit").style.display="block";
	document.getElementById("theImg").style.display="none";
	document.getElementById("alertTabMsg").style.display="none";
}
</script>
</body>
</html>