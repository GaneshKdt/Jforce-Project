<!DOCTYPE html>


<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>


<html lang="en">

<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')"
	var="server_path" />


<jsp:include page="../common/jscss.jsp">
	<jsp:param value=" Assignment Guidelines " name="title" />
</jsp:include> 

<%

%>
<style>

#parentSpinnerDiv {
	background-color: transparent !important;
	z-index: 999;
	width: 100%;
	height: 100vh;
	position: fixed;
}

#childSpinnerDiv {
	color: black;
	position: absolute;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
}

	.list-group {
    list-style: decimal inside !important
}

.list-group-item {
    display: list-item !important
}
</style>
<body style=" background-color: #ECE9E7;">

		<%-- <%@ include file="../common/header.jsp"%>
 --%>


		<div class="">

			<%-- <jsp:include page="../common/breadcrum.jsp">
				<jsp:param value="Exam;Assignments" name="breadcrumItems" />
			</jsp:include> --%>

			<div class="">
				<div class="">

					<%-- <jsp:include page="../common/left-sidebar.jsp">
						<jsp:param value="Tests" name="activeMenu" />
					</jsp:include> --%>


					<div class="">
						<%-- <%@ include file="../common/studentInfoBar.jsp"%> --%>

						<div class="container">

							
							<div class="clearfix"></div>
							<div class="panel-content-wrapper">
								
								<!-- Code for page goes here start -->
								
								
	<div id="parentSpinnerDiv">

		<div id="childSpinnerDiv">
			<i class="fa-solid fa-rotate fa-spin"
				style="font-size: 50px; color: grey;"></i>
		</div>

	</div>
	
								<div class="jumbotrom">
								<div class="container-fluid">
								<h2 class="red text-capitalize">
									Internal Assignment Guidelines
								</h2>
								</div>
								<div class="container-fluid">
								
								<c:choose>
									<c:when test="${(consumerProgramStructureId eq 142) || (consumerProgramStructureId eq 143) ||(consumerProgramStructureId eq 144) ||(consumerProgramStructureId eq 145) ||(consumerProgramStructureId eq 146) ||(consumerProgramStructureId eq 147) ||(consumerProgramStructureId eq 148) ||(consumerProgramStructureId eq 149)  }">
									<!-- guidelines for PDDM 148 start -->
												
										<%@ include file="pddmIAGuidelines.jsp"%>
									
									<!-- guidelines for PDDM 148 end -->
									</c:when>
									<c:when test="${consumerProgramStructureId eq 131}">
									<!-- guidelines for M.Sc. (AI & ML Ops) 131 start -->
												
										<%@ include file="mscIAGuidelines.jsp"%>
									
									<!-- guidelines for M.Sc. (AI & ML Ops) 131 end -->
									</c:when>
									<c:when test="${consumerProgramStructureId eq 111 or consumerProgramStructureId eq 151}">
									<!-- guidelines for MBA - WX 111 start -->
												
										<%@ include file="mbawxIAGuidelines.jsp"%>
									
									<!-- guidelines for MBA - WX 111 end -->
									</c:when>
									<c:when test="${consumerProgramStructureId eq 119 or consumerProgramStructureId eq 126}">
									<!-- guidelines for MBA - X 119 start -->
												
										<%@ include file="mbaxIAGuidelines.jsp"%>
									
									<!-- guidelines for MBA - X 119 end -->
									</c:when>
									<c:otherwise>
									<!-- common guidelines in case of no other applicable start -->
												
										<%@ include file="commonIAGuidelines.jsp"%>
									
									<!-- common guidelines in case of no other applicable end -->
									</c:otherwise>
								</c:choose>

								
<p style="font-size:16px;">								
</p>							  	
								<div class="row" style="padding-bottom:60px;">
								
								<div class="col-xs-12" >
								<form id="startStudentTestForm" action="/exam/startStudentTestForAllViews"  method="post"  >
				
									<input type="hidden" value="${testIdForUrl}" name="testIdForUrl" />
									<input type="hidden" value="${sapidForUrl}" name="sapidForUrl" />
									<input type="hidden" value="${consumerProgramStructureIdForUrl}" name="consumerProgramStructureIdForUrl" /> 
									
								</form>
								 <!-- 
								<a  id="goToTestPage"
								  class="btn btn-primary"
								  style="white-space: normal; text-align:left;"
								  href="/exam/startStudentTestForAllViews?testIdForUrl=${testIdForUrl}&sapidForUrl=${sapidForUrl}">
								  I have read and understood the guidelines. Start test</a>
								   -->
								   <a  id="goToTestPage"
								  class="btn btn-primary"
								  style="white-space: normal; text-align:left;"
								  href="#">
								  I have read and understood the guidelines. Start test</a>
								  
								  
								</div>
								</div>
								  				
								</div>
								</div>
								
								
								<!-- Code for page goes here end -->
								
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		
		<%-- <jsp:include page="../common/footer.jsp" /> --%>
     		<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery-1.11.3.min.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/bootstrap.js"></script>

	<!-- 

<script src="/exam/assets/js/jquery.tabledit.js"></script>

	<script src="/exam/resources_2015/js/vendor/jquery-ui.min.js"></script>
		
	<script
		src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
	<script src="/exam/resources_2015/js/vendor/dataTables.bootstrap.js"></script>
	<script
		src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js"></script>
        		
       --> 

        <script>
        		$(document).ready(function() {
        			
        		var lastClicked;

	    		var resquest_time = new Date().getTime();
	    		var response_time = new Date().getTime();
	  			
	    		var testId = "${testId}";

	    		var sapid = "${userId}";
	    		
	    		var body = {
	  	    			'sapid' : sapid,
	  	    			'testId' : testId
	  	    		};
	  	    console.log(body);
        		
    			try {
        			
    				$("#goToTestPage").click(function(e){

    					$('#parentSpinnerDiv').show();
    					console.log('Called Submit')
    				    e.preventDefault();
    			        now = new Date().getTime();

    			        if (lastClicked && (now - lastClicked < 3000)) {

        			    } else {
        			    	lastClicked = now;
        			    	//alert('Submitting...')
        				    
        			    	goToIATestPageButtonClicked();
							
        			    	//window.location.href = "/exam/startStudentTestForAllViews?testIdForUrl=${testIdForUrl}&sapidForUrl=${sapidForUrl}";
							//document.getElementById("startStudentTestForm").submit();
							
							setTimeout(function(){
								document.getElementById("startStudentTestForm").submit();
							},2000);
							
    			        }
    					    					
    				});
    			}
    			catch(err) {
        			console.log("Catch error : "+err.message);
					
        			goToIATestPageButtonClicked();
        			
					//window.location.href = "/exam/startStudentTestForAllViews?testIdForUrl=${testIdForUrl}&sapidForUrl=${sapidForUrl}";
        			//document.getElementById("startStudentTestForm").submit();

					setTimeout(function(){
						document.getElementById("startStudentTestForm").submit();
					},2000);
					}
    			
		/*		
	$('.tables').DataTable( {
        initComplete: function () {
            this.api().columns().every( function () {
                var column = this;
                var headerText = $(column.header()).text();
                console.log("header :"+headerText);
                if(headerText == "Subject")
                {
                   var select = $('<select style="width:100%; margin-left:5px;" class="form-control"><option value="">All</option></select>')
                    .appendTo( $(column.header()) )
                    .on( 'change', function () {
                        var val = $.fn.dataTable.util.escapeRegex(
                            $(this).val()
                        );
 
                        column
                            .search( val ? '^'+val+'$' : '', true, false )
                            .draw();
                    } );
 
                column.data().unique().sort().each( function ( d, j ) {
                    select.append( '<option value="'+d+'">'+d+'</option>' )
                } );
              }
                
                if(headerText == "Month")
                {
                   var select = $('<select style="width:100%; margin-left:5px;" class="form-control"><option value="">All</option></select>')
                    .appendTo( $(column.header()) )
                    .on( 'change', function () {
                        var val = $.fn.dataTable.util.escapeRegex(
                            $(this).val()
                        );
 
                        column
                            .search( val ? '^'+val+'$' : '', true, false )
                            .draw();
                    } );
 
                column.data().unique().sort().each( function ( d, j ) {
                    select.append( '<option value="'+d+'">'+d+'</option>' )
                } );
              }
                
                if(headerText == "Year")
                {
                   var select = $('<select style="width:100%; margin-left:5px;" class="form-control"><option value="">All</option></select>')
                    .appendTo( $(column.header()) )
                    .on( 'change', function () {
                        var val = $.fn.dataTable.util.escapeRegex(
                            $(this).val()
                        );
 
                        column
                            .search( val ? '^'+val+'$' : '', true, false )
                            .draw();
                    } );
 
                column.data().unique().sort().each( function ( d, j ) {
                    select.append( '<option value="'+d+'">'+d+'</option>' )
                } );
              }
 	
                
                
 
            } );
        }
    } );
	*/
  	
  	function logPageLoadedEvent(){
  		 		
			//apiLogCall start
			
    	try{
    		let apiUrl = window.location.origin+"/exam/assignmentGuidelines_pageLoaded";
			response_time = new Date().getTime();
			response_payload_size = 0 ;
  			asyncApiLogAjaxCall(sapid,apiUrl,resquest_time,response_time,"Success","",response_payload_size)
			
    	}catch(err){ 
			//////////console.log("Error in apiLogCall :"); 
			//////////console.log(err); 
		}
			//apiLogCall end
  	}
  	function goToIATestPageButtonClicked(){
  		 		
			//apiLogCall start
			
    	try{
    		let apiUrl = window.location.origin+"/exam/startStudentTestPage_goToLinkClicked";
			response_time = new Date().getTime();
			response_payload_size =  0 ;
  			asyncApiLogAjaxCall(sapid,apiUrl,resquest_time,response_time,"Success","",response_payload_size)
			
    	}catch(err){ 
			//////////console.log("Error in apiLogCall :"); 
			//////////console.log(err); 
		}
			//apiLogCall end
  	}

	//asyncApiLogAjaxCall start
	 function asyncApiLogAjaxCall(sapid,apiUrl,resquest_time,response_time,status,error_message,response_payload_size){
		try{
			
			let networkInfoForAsyncApiLogAjaxCall = {}
			try {
				let deviceMemoryInfo ="";
				let platformInfo ="";
				let hardwareConcurrencyInfo ="";
				
				
				try {
						
					deviceMemoryInfo = "This device has at least "+ (navigator.deviceMemory ? navigator.deviceMemory+"":"") + " GiB of RAM.";
				} catch(error) {
					deviceMemoryInfo = "Not Available";
				}
				try {
					
					platformInfo = ""+ (navigator.platform ? navigator.platform:"") + "";
				} catch(error) {
					platformInfo = "Not Available";
				}
				try {
					
					hardwareConcurrencyInfo = ""+ (window.navigator.hardwareConcurrency ? window.navigator.hardwareConcurrency+"":"") + "";
				} catch(error) {
					hardwareConcurrencyInfo = "Not Available";
				}
				
			    networkInfoForAsyncApiLogAjaxCall = {
			        downlink : navigator.connection.downlink,
			        rtt : navigator.connection.rtt,
			        downlinkMax : navigator.connection.downlinkMax,
			        effectiveType : navigator.connection.effectiveType,
			        type : navigator.connection.type,
			        saveData : navigator.connection.saveData,
			        deviceMemoryInfo : deviceMemoryInfo,
			        platformInfo : platformInfo,
			        hardwareConcurrencyInfo : hardwareConcurrencyInfo,
			    }
			} catch(error) {
				networkInfoForAsyncApiLogAjaxCall = { errorMessage : 'Not Available' }
			}	
		let bodyForAsyncApiLogAjaxCall = {
			
			"sapid": sapid ? sapid.toString() : "",
            "api": apiUrl,
            "resquest_time": resquest_time ,
            "response_time": response_time ,
            "response_payload_size": response_payload_size ? response_payload_size.toString() : "0" ,
            "status": status,
            "error_message": error_message,
            "platform": "Web",
            "networkInfo" :JSON.stringify(networkInfoForAsyncApiLogAjaxCall),
			
		};
		//console.log("IN asyncApiLogAjaxCall got bodyForAsyncApiLogAjaxCall : ");
		//console.log(bodyForAsyncApiLogAjaxCall);
		$.ajax({
			type : 'POST',
			url : 'https://ngasce-content.nmims.edu/ltidemo/saveNetworkLogs',
			data: JSON.stringify(bodyForAsyncApiLogAjaxCall),
            contentType: "application/json",
            dataType : "json",
            timeout : 10000,
            
		}).done(function(data) {
			  console.log("iN asyncApiLogAjaxCall AJAX SUCCESS");
          	console.log(data);
          	
		}).fail(function(xhr) {
			console.log("iN asyncApiLogAjaxCall AJAX eRROR",xhr);
			
			
		  });
		
		}catch(err){
			console.log("IN asyncApiLogAjaxCall got Error : ");
			console.log(err);
		}
		
	}
	//asyncApiLogAjaxCall end
	
	

	$('#parentSpinnerDiv').hide();
	logPageLoadedEvent();
	
        		});//doc.ready()
	</script>
	
		
<script>
try{
parent && parent.window.setHideShowHeaderSidebarBreadcrumbs ? parent.window.setHideShowHeaderSidebarBreadcrumbs(false) : null
}catch(err){
	console.log(err);
}
</script>

    </body>


</html>