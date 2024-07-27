<!DOCTYPE html>


<%@page import="com.nmims.beans.PacakageAvailabilityBean"%>
<%@page import="com.nmims.beans.PackageBean"%>
<%@page import="com.nmims.helpers.*"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Calendar" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html lang="en">
	
	<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
	
	<jsp:include page="/views/common/jscss.jsp">
		<jsp:param value="Career Services - Product Details" name="title"/>
	</jsp:include>
	
	<style>
	 	.package-details li {
	    	margin-left: 1rem;
		}
		
		.package-details li {
		    list-style: disc;
		}
	
		.package-details .table thead th{
			font-weight: 900;
		}
		
		.package-details .table thead th{
			font-weight: 900;
		}
		
		.package-details h2{
		    font-size: 2rem;
		    padding-bottom: 30px;
		}
		
		.package-details h5{
			color: var(--nm-red);
		}
	</style>
	
	<body>
	
		<jsp:include page="/views/common/header.jsp"/>
		<div class="sz-main-content-wrapper">
			 	<jsp:include page="/views/common/breadcrum.jsp">
				<jsp:param value="Career Services;Show Available Packages" name="breadcrumItems"/>
			</jsp:include>
				
				
			<div class="sz-main-content menu-closed">
				<div class="sz-main-content-inner">
					<jsp:include page="/views/common/left-sidebar.jsp">
						<jsp:param value="" name="activeMenu"/>
					</jsp:include>	
					<div class="sz-content-wrapper">
						<jsp:include page="/views/common/studentInfoBar.jsp"/>
						<div class="sz-content">
							<jsp:include page="/views/common/messages.jsp"/>
					        
			
							<div class="sz-content large-padding-top package-details">
								<div class="panel-content-wrapper">
									<div class="col-lg-9 col-md-12 col-sm-12 mx-auto wysiwyg-generated">
										<h2>${ PageData.familyName }</h2>
										<div class="clearfix"></div>
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
										<div class="row">
											<div class="col-4 ml-3" style="font-size: 1.5rem;">
								    			<h5 style="font-size: larger;color: black;">&#x20B9; <span class="price">${ PageData.price }</span> /-</h5>
											</div>
											<div class="col-4 ml-auto mt-2">
												<% 
													PacakageAvailabilityBean pageData = (PacakageAvailabilityBean) request.getAttribute("PageData");
													if(pageData.isPurchased()){
												%>
													<h5 style="font-size: larger;">Purchased</h5>
												<% }else if(pageData.isAvailable()){ %>
													<button class="mb-0 col-12 btn btn-primary" type="button" onclick="location.href='/careerservices/applyForProduct?packageId=${ PageData.packageId }'">Apply</button>
												<% } %>
												
											</div>
										</div>
										<jsp:include page="/views/portal/supportCallUsAt.jsp" />
									</div>
								</div>
							</div>
						</div>	
					</div>
				</div>
			</div>
			<jsp:include page="/views/common/footer.jsp"/>
		</div>
	<script type="text/javascript" src="assets/dataTable/RowGroup-1.1.0/js/dataTables.rowGroup.js"></script>
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
			});
			$(window).on('load', function () {
				$.each($('.price'), function(){
					var val = commaSeparateNumber($(this).html());
					$(this).html(val);
				});

			});
	
			function commaSeparateNumber(val){
				val=val.split(".")[0];
				while (/(\d+)(\d{3})/.test(val.toString())){
				  val = val.toString().replace(/(\d+)(\d{3})/, '$1'+','+'$2');
				}
				return val;
			}
		</script>
	</body>
</html>