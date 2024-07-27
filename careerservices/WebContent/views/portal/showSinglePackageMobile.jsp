<%@page import="com.google.gson.Gson"%>
<%@page import="java.util.Map"%>
<html>
	<head>
		<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
		<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
		<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
		<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
		<jsp:useBean id="now" class="java.util.Date"/>
		
	
	
		<jsp:include page="/views/common/jscss.jsp">
			<jsp:param value="Session Details" name="title"/>
		</jsp:include>
		<link rel="stylesheet" type="text/css" href="assets/dataTable/RowGroup-1.1.0/css/rowGroup.bootstrap4.css"/>
		<link rel="stylesheet" type="text/css" href="assets/dataTable/Responsive-2.2.2/css/responsive.bootstrap4.css"/>
		
	</head>
	<body>

	<div class="sz-main-content menu-closed">
		<div class="">
			<div class="sz-content-wrapper p-0 m-0">
				<div class="sz-content p-0 m-0" >
					<div class="card card-primary">
						<div class="card-body mx-3">
							<div class="wysiwyg-generated">
								<h2>${ PageData.familyName }</h2>
								<div class="clearfix my-4"> </div>
								<div>${ PageData.description }</div>
								<div class="clearfix my-4"> </div>
								<h5 style="font-size: larger;">Key Highlights of the program: </h5>
								${ PageData.keyHighlights }
								
								<div class="mt-5" id="eligibilityCriteria">
									<h5 style="font-size: larger;">Eligibility Criteria</h5>
									${ PageData.eligibilityCriteria }
								</div>
								<div class="mt-5" id="componentEligibilityCriteria">
									<div class="mt-5">
										<h5 style="font-size: larger;">Program component eligibility criteria and timeline</h5>
										${ PageData.componentEligibilityCriteria }
									</div>
								</div>
							</div>
						</div>
						<jsp:include page="/views/portal/supportCallUsAt.jsp" />
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="d-none">
		<jsp:include page="/views/common/footer.jsp" />
	</div>
	
	<script type="text/javascript" src="assets/dataTable/RowGroup-1.1.0/js/dataTables.rowGroup.js"></script>
	<script type="text/javascript" src="assets/dataTable/Responsive-2.2.2/js/dataTables.responsive.js"></script>
	<script>
		$(document).on('ready', function() {
		
			$('table').addClass("table table-bordered");
			$.each($("table"), function(){
			   		$(this).css("overflow-x","auto");
		  	});
		});
	</script>	
	<script>
	
		$("td").removeAttr("style");
		$("tr").removeAttr("style");
		$("th").removeAttr("style");
		$("th").addClass("text-center text-vertical-enter bg-light");
		$("table").removeAttr("style");
		$(window).on('load', function () {
			$('#eligibilityCriteria').find('table').DataTable({
				dom: 't',
			    ordering: false,
		        responsive: true,
		        orderFixed: [0, 'desc'],
		        columnDefs: [
		            { visible: false, targets: 0 }
		            ],
		        rowGroup: {          
		        	startRender: function ( rows, group ) {
		                return $('<tr/>')
		                    .append( '<td class="text-center bg-light text-capitalize font-weight-bold" colspan="3">'+group+'</td>' );
		            },
		            dataSrc: 0  
		        }
			});

			$('#componentEligibilityCriteria').find('table').DataTable({
				dom: 't',
			    ordering: false,
		        responsive: true
			});
		});
	</script>
	</body>		
</html>