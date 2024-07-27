<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 


<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>   
<c:set var="alphabet" value="${fn:split('REVENUE,SF-SZ MISMATCH,SF-R MISMATCH,METRICS,STAGED,FEDEX,MBAWX-PF,MBAX-PF,PG-PF,SF-RGSTRN', ',')}" scope="application" />   
<link rel="stylesheet" href="resources_2015/css/dataTables.bootstrap.css"> 
<jsp:include page="jscss.jsp">  
<jsp:param value="Announcement" name="title" />
</jsp:include>
<%String logsUrl = (String)request.getAttribute("logsUrl"); %> 
<style>
.modal-backdrop.in {
    opacity: 0;
} 
a:hover {
    cursor:pointer;
    text-decoration:underline;
}
</style>
<body class="inside">

<%@ include file="header.jsp"%>
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Salesforce Sync Dashboard</legend></div> 
		<div class="success"></div>
	</div>
	<div class="panel-body">
		<table class="table table-striped "  >
									<thead>
										<tr>
											<th>Sr. No.</th>
											<th>Sync Type</th> 
											<th>Message</th>
											<th>Failed Count</th>
											<th>Last Sync Time</th> 
											<th>Action</th>
											<th>Logs</th>

										</tr>
									</thead>
									<tbody> 
									<c:set var="i" value="0"/>
									<c:forEach items="${apilist}" var="api" varStatus="loop">
									<c:if test="${api.syncType!='Active Re-reg in Salesforce'}">
									<tr>
										<td> ${loop.count}</td>
										<td>${api.syncType} </td> 
										<td style="max-width: 400px;"> 
										<button value="${api.statusUrl}" style="display:none" class="checkStatus">Check Status</button>
										<p style="line-height: 1.6" class="display_msg" ></p>
										 <br> 
										 <a class="view_details_button" style="display:none;">View Details<i class="fa fa-angle-double-down" aria-hidden="true"></i></a>
										<a class="hide_details_button" style="display:none;">Hide Details<i class="fa fa-angle-double-up" aria-hidden="true"></i></a>
										<p class="display_detail_msg" style="display:none; padding: 10px;border: 1px solid #dddddd;margin-top: 10px; line-height: 1.6;">Detailed message</p>
										 </td> 
										 
										<td><p class="display_fail" style="line-height: 1.6"></p></td>  
										<td>${api.lastSync}</td> 
										<td>
										<input class="input" type="hidden" value="${api.id}"/>
										<button value="${api.url}" class="btn btn-sm btn-success syncNow">Sync Now</button></td>
									    <td>
									    <button value="" class="btn btn-sm btn-success showLogs"   data-toggle="modal" data-target="#myModal">View</button>
									    <input type="hidden" value="${alphabet[i]}" class="keyword"/>
									    <c:set var="i" value="${i+1 }"/>      
									    </td>
									</tr> 
									</c:if>
									</c:forEach>
									</tbody>
									</table>	
	</div>
            <div class="students_table" style="display:none">
            	<div class="panel-body">
			        <table class="table table-striped "  > 
						<thead>
							<tr>
							    <th>Mismatch Type</th> 
								<th>Student</th>
								<th>Sem</th> 
								<th>year</th>  
								<th>month</th>
							</tr>
						</thead>
						<tbody class="student-info-table">
						
						</tbody>
				     </table>
				 </div>
			 </div> 
			<div class="log-details container" ></div>
	</section>
<jsp:include page="footer.jsp" />

</body>
<script
              src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
       <script src="resources_2015/js/vendor/dataTables.bootstrap.js"></script>
       <script
              src="resources_2015/js/vendor/dataTables.buttons.min.js"></script>
<script> 
$(document).ready(function(){ 
$( ".checkStatus" ).click();  
});  
$(".view_details_button").click( function() { 
	 $(this).hide();
	 $(this).closest("tr").find(".hide_details_button").show();
	 $(this).parent().find(".display_detail_msg").show(); 
});
$(".hide_details_button").click( function() { 
	 $(this).hide();
	 $(this).closest("tr").find(".view_details_button").show();
	 $(this).parent().find(".display_detail_msg").hide();  
});
 
var sfdcStudentzoneMismatchStudents=[]; 
var sfdcRegMismatchStudents=[];
var matrixMismatchStudents=[];
var mbawxAccountNotSyncedStudents=[];
var mbaxAccountNotSyncedStudents=[]; 
var pgAccountNotSyncedStudents=[];
var regMismatchStudents=[];
$(".checkStatus")
.click(
		function() {   
			var url = $(this).val(); 
			if(url.search("salesforce")==37){
			 url = "http://studentzone-ngasce.nmims.edu:8090/"+url.slice(37);  
			} 
			var display_msg = $(this).closest("tr").find(".display_msg");
			var display_fail  = $(this).closest("tr").find(".display_fail");
			var details_btn  = $(this).closest("tr").find(".view_details_button");
			var display_detail_msg = $(this).closest("tr").find(".display_detail_msg");  
			var keyword= $(this).closest("tr").find(".keyword").val();
			display_msg.html("Loading");
			display_fail.html("Loading");
			display_detail_msg.html("");
			$.ajax({
				url : "runSchedularApiManually",
				type : 'POST',
				data : JSON.stringify({
					"url" : url
				}),
				contentType : "application/json",
				dataType : "json", 
				success : function(response) {    
					display_msg.html(response.message);
					display_fail.html(response.failureCount);   
					if(response.detailedMessage !=null && response.detailedMessage.length>0){
						details_btn.show();   
						jQuery.each( response.detailedMessage, function( i, val ) {
							var arrayOfStrings = val.split(";");
							if(typeof  arrayOfStrings[1]==="undefined" || val[val.length -1]=='0'){     
								display_detail_msg.append('<i class="fa fa-angle-right" style="color: #c72127;"  aria-hidden="true"></i> '+val+'<br>'); 
							}else{
								display_detail_msg.append('<i class="fa fa-angle-right" style="color: #c72127;"  aria-hidden="true"></i> '+arrayOfStrings[1]+' <a class="view_students" data="'+arrayOfStrings[0]+'"> view students</a><br>');	
							}    
						}); 
						if(response.students.length>0){  
						//display_detail_msg.append('<a class="view_students">view students</a>'); 
						}
						switch(keyword) {
						  case "SF-SZ MISMATCH": 
							    sfdcStudentzoneMismatchStudents=response.students;
							    
						    break;
						  case "SF-R MISMATCH":
							    sfdcRegMismatchStudents=response.students;
						    break;
						  case "METRICS":
							    matrixMismatchStudents=response.students;
							    break;
						  case "MBAWX-PF":
							    mbawxAccountNotSyncedStudents=response.students;  
							    break;
						  case "MBAX-PF":
							    mbaxAccountNotSyncedStudents=response.students; 
							    break;
						  case "PG-PF":
							    pgAccountNotSyncedStudents=response.students; 
							    break;
						  case "SF-RGSTRN":
							    regMismatchStudents=response.students;
							    break; 
						}
						
						   
					}  
				},
				error: function(XMLHttpRequest, textStatus, errorThrown) { 
					console.log("Status: " + textStatus); console.log("Error: " + errorThrown); 
			    }       
			}) ;  
});
$(document).on("click",".view_students",function() {
	var filterKey = $(this).attr("data");  
	
	var table = $(".students_table").find("table").DataTable().destroy();  
	$(".students_table").show();  
	$(".student-info-table").empty();
	$(".log-details").empty(); 
	var keyword= $(this).closest("tr").find(".keyword").val(); 
	var students=[];
	switch(keyword) {
	  case "SF-SZ MISMATCH": 
		   students =sfdcStudentzoneMismatchStudents; 
	    break;
	  case "SF-R MISMATCH":
		    students =sfdcRegMismatchStudents;
	    break;
	  case "METRICS":
		    students =matrixMismatchStudents;
		    break;
	  case "MBAWX-PF":
		    students =mbawxAccountNotSyncedStudents;  
		    break;
	  case "MBAX-PF":
		    students =mbaxAccountNotSyncedStudents; 
		    break;
	  case "PG-PF": 
		    students =pgAccountNotSyncedStudents;
		    break;
	  case "SF-RGSTRN":
		    students =regMismatchStudents;
		    break; 
	}    
	$.map( students , function( n ) {  
		var sem = (n.sem!=null)?n.sem:"";
		var year = (n.year!=null)?n.year:"";
		var month = (n.month!=null)?n.month:"";  
		var status = (n.status!=null)?n.status:"";  
		if(n.status==filterKey){
		$(".student-info-table").append('<tr><td>'+status+'</td><td>'+n.sapid+'</td><td>'+sem+'</td><td>'+year+'</td><td>'+month+'</td></tr>');    
		}
	});   
	$(".students_table").find("table").dataTable();    
	$([document.documentElement, document.body]).animate({
        scrollTop: $(".students_table").offset().top
    }, 500);       
});   

	$(".syncNow")
			.click(
					function() {
						var url = $(this).val();
						var id = $(this).parent().find(".input").val();
						$.ajax({
							url : "runSchedularApiManually",
							type : 'POST',
							data : JSON.stringify({
								"url" : url,
								"id" : id
							}),
							contentType : "application/json",
							dataType : "json"
						})
						$(".success")
								.html(
										'<div class="alert alert-success alert-dismissible">'
												+ '<button type="button" class="close" data-dismiss="alert"'+
				'aria-hidden="true">&times;</button>Sync function Initiated.</div>');
					})
	$(".showLogs").click(function() {
		var filter_by=$(this).parent().find(".keyword").val(); 
		$(".log-details").empty();  
		$(".students_table").hide();
		var url = "https://studentzone-ngasce.nmims.edu:8090/salesforce/getLogFiles?key=SF-RGSTRN"; 
		$.ajax({  
			url : "runSchedularApiManually",
			type : 'POST',
			data : JSON.stringify({
				"url" : url
			}),
			contentType : "application/json",
			dataType : "json",
			success : function(data) {
				console.log("data"+data);
				response=data.logFileNameAndContentHashMap;
				if (response.length !== 0) {
				    var j = 0;
				      
					for ( var i in response) {   
						j=j+1; 
						var accordian_div = '<div class="panel panel-default accord-panel" >'   +
						'<div class="panel-heading"> ' +
							'<h4 class="panel-title">'+
								'<a data-toggle="collapse" data-parent="#accordion" '+
									'href="#collapse'+j+'" class="fileName">'+i+' </a>'+
							'</h4>'+
						'</div>'+
						'<div id="collapse'+j+'" class="panel-collapse collapse">'+
							'<div class="panel-body">'+
  
								'<p class="" style="line-height: 1.7;margin-left: 10px;">'+response[i]+'</p>'+

							'</div>'+
						'</div>'+
					'</div> ' ;       
					$(".log-details").append(accordian_div); 
					window.scrollTo(0, 300);            
					}  
				}
			},
			error: function(XMLHttpRequest, textStatus, errorThrown) { 
		        console.log("Status: " + textStatus); console.log("Error: " + errorThrown); 
		    }  
		});
	});
</script>
</html>