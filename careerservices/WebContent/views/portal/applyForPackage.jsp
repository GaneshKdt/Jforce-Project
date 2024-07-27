<!DOCTYPE html>


<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="com.nmims.helpers.*"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Calendar" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<html lang="en">
	
	<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
	
	
	
	
	<jsp:include page="/views/common/jscss.jsp">
		<jsp:param value="Career Services - Apply for product" name="title"/>
	</jsp:include>
	
			
	<style>
		.custom-file-control:disabled, .form-control pl-2:disabled, .form-control pl-2[readonly], [readonly].custom-file-control {
			background-color: rgb(248,248,248, 1);
		}
	</style>
	<body>
		<jsp:include page="/views/common/header.jsp"/>
		<div class="sz-main-content-wrapper">
			<jsp:include page="/views/common/breadcrum.jsp">
				<jsp:param value="Career Serivces;Apply For Product" name="breadcrumItems"/>
			</jsp:include>
			<div class="sz-main-content menu-closed">
				<div class="sz-main-content-inner">
					<jsp:include page="/views/common/left-sidebar.jsp">
						<jsp:param value="My Courses" name="activeMenu"/>
					</jsp:include>	
					<div class="sz-content-wrapper dashBoard myCoursesPage">
						<jsp:include page="/views/common/studentInfoBar.jsp"/>
						
						<div class="sz-content padding-top">
							<div>
								<div class="card mb-3 wysiwyg-generated">
									<div class="card-body pb-0">
										<div class="col-12">
											<h1>${ Package.packageName }</h1>
										</div>
										<div class="col-12 mt-3 pt-3">
											${ Package.description }
										</div>
									</div>
									<div class="card-body">
										<div class="col-12 mt-3">
										
											<div class=" mt-3">
												<input type="checkbox" id="tncAccepted" name="tncAccepted" value="1" style="display: unset; line-height: unset; padding: unset; width: unset; height: unset; float: unset;" class="form-control">
												I accept the 
												<a class="hover-icon" style="font-weight: bold; text-decoration: none !important; color: #26a9e0; font-size: 0.9rem;" data-toggle="modal" data-target="#TnCModal">
													Terms and Conditions
												</a>
											</div>
											<div class=" mt-3">
												<button id="applyButton" type="button" onclick="confirmApply();" class="btn btn-danger">Apply</button>
											</div>
										</div>
										<div class="px-4 mt-4">
											<jsp:include page="/views/portal/supportCallUsAt.jsp" />
										</div>
									</div>
								</div>
							</div>	
						</div>
					</div>
				</div>
			</div>
		</div>
		
												
			<!-- The Modal -->
		<div class="modal" id="TnCModal" style="display: none;" aria-hidden="true">
			<div class="modal-dialog" style="width:66%; max-width:66%; overflow:auto">
				<div class="modal-content">
			
					<!-- Modal Header -->
					<div class="modal-header">
						<h4 class="modal-title">Terms and Conditions</h4>
						<a class="hover-icon" data-dismiss="modal">x</a>
					</div>
				
					<!-- Modal body -->
					<div class="modal-body" style="overflow: auto">
						<jsp:include page="/views/portal/TermsAndConditions.jsp?packageId=${ Package.packageId }" />
					</div>
				
					<!-- Modal footer -->
					<div class="modal-footer">
						<button type="button" class="btn btn-danger" data-dismiss="modal">Close</button>
					</div>
				</div>
			</div>
		</div>		
		<jsp:include page="/views/common/footer.jsp"/>
				
		<script>
		
			$(document).on('ready', function() {
	
				getTermsAndConditions();
				$("#TnC").on('click', function() {
					return false;
				});
			});
		</script>
		<script>
			function confirmApply(){
				if(!($("#tncAccepted").prop('checked'))){
					$.confirm({
            		    icon: 'fa fa-exclamation-triangle',
            		    rtl: true,
					    title: '',
					    content: 'Please read and accept the terms and conditions before continuing',
					    type: 'red',
					    typeAnimated: true,
					    buttons: {
				        		close: function () {
			       	 			}	
					    }
					});
					return;
				}
				window.location.href='startCheckout?packageId=${ Package.packageId }';
			}
		</script>
	</body>
</html>