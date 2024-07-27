<!DOCTYPE html>


<%@page import="java.util.List"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.nmims.beans.MailStudentPortalBean"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<html lang="en">




<jsp:include page="common/jscss.jsp">
	<jsp:param value="Welcome to Student Zone" name="title" />
</jsp:include>


<%String mailBeanId = ""; %>
<body>

	<%@ include file="common/header.jsp"%>



	<div class="sz-main-content-wrapper">

		<jsp:include page="common/breadcrum.jsp">
			<jsp:param value="Student Zone;Email Communications"
				name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<%@ include file="common/left-sidebar.jsp"%>


				<div class="sz-content-wrapper examsPage">
					<%@ include file="common/studentInfoBar.jsp"%>


					<div class="sz-content">
						<%try{ %>
						<div class="clearfix"></div>
						<div>
							<div class="col-md-6">
								<h2 class="red text-capitalize">
									<i class="fa fa-envelope"></i>My Inbox
								</h2>
							</div>

							<div class="col-md-6" style="margin-top: 20px;">
								<div class="switch" style="float: right">
									<label>Vertical<input type="checkbox" id="layoutId"
										onclick="changeLayout()" id="layout"><span
										class="lever"></span>Horizontal
									</label>
								</div>
							</div>

						</div>
						<div class="clearfix"></div>

						<div class="row">
							<%@include file="common/messages.jsp"%>
							<c:if test="${rowCount > 0 }">
								<div class="col-sm-6" id="emailPanel">
									<div class="panel-content-wrapper">

										<%-- <%@include file="common/messages.jsp"%> --%>


										<div class="table-responsive">
											<div class="col-sm-12">
												<table class="table table-striped table-hover tables"
													style="font-size: 12px">
													<thead>
														<tr>
															<th>From</th>
															<th>Subject</th>
															<th>Communication Date/Time</th>
														</tr>
													</thead>
													<tbody>
														<c:forEach var="mailBean"
															items="${listOfCommunicationMadeToStudent}"
															varStatus="status">
															<fmt:parseDate value="${mailBean.createdDate}"
																var="createdDate" pattern="yyyy-MM-dd HH:mm:ss"
																type="BOTH" />
															<tr>
																<td><c:out value="${mailBean.fromEmailId}" /></td>
																<td><a onclick="load(${mailBean.id})" href="#"><c:out
																			value="${mailBean.subject}" /></a></td>
																<td><fmt:formatDate pattern="dd-MMM-yyyy, HH:mm"
																		value="${createdDate}" /></td>
															</tr>

														</c:forEach>
													</tbody>

												</table>
											</div>

										</div>


									</div>
								</div>


								<div class="col-md-6 col-sm-12" id="emailBody">
									<div class="panel-content-wrapper">
										<iframe id="iframe_a" name="iframe_a"
											src="/studentportal/singleEmailForm?id=${firstMail.id}"
											height="950" width="100%"></iframe>
									</div>
								</div>
							</c:if>
						</div>



					</div>

				</div>
				<c:if test="${rowCount > 0 }">
					<!-- MODAL FOR INDIVIDUAL ANNOUNCEMENTS-->
					<c:forEach var="mailBean"
						items="${listOfCommunicationMadeToStudent}" varStatus="status">

						<div class="modal fade announcement"
							id="emailContentForStudent${status.count}" tabindex="-1"
							role="dialog">
							<div class="modal-dialog" role="document">
								<div class="modal-content modal-md">
									<div class="modal-header">
										<button type="button" class="close" data-dismiss="modal"
											aria-label="Close">
											<span aria-hidden="true">&times;</span>
										</button>
										<h4 class="modal-title">MAIL CONTENT</h4>
									</div>
									<div class="modal-body">
										<p>
											<c:out value="${mailBean.body}" />
										</p>
									</div>
									<div class="modal-footer">
										<button type="button" class="btn btn-default"
											data-dismiss="modal">DONE</button>
									</div>
								</div>
							</div>
						</div>
					</c:forEach>
				</c:if>
			</div>
		</div>

		<%}catch(Exception e){
            	e.printStackTrace();}%>


		<jsp:include page="common/footer.jsp" />


		<script>
         $(document).ready(function(){
        	 
        	
				$('.tables').DataTable( {
					 
					   "searching": false,
					   "ordering": false,
			        initComplete: function () {
			        	 this.api().columns().every( function () {
			               var column = this;
			                var headerText = $(column.header()).text();
			                console.log("header :"+headerText);
			         
			             
			             /*    column.data().unique().sort().each( function ( d, j ) {
			                    select.append( '<option value="'+d+'">'+d+'</option>' )
			                } ); */
			             
			            } );
			        } 
			    } );
         });
 
		 function load(id){
		 	var x =  document.getElementById("iframe_a");
		 	document.getElementById("iframe_a").src = "/studentportal/singleEmailForm?id="+id;
		 	if (x.style.display === "none") {
		    	x.style.display = "block";
		 	}
		 	
		 	return false;
		}
  
         function changeLayout()
         {
		
			 var id = document.getElementById("layoutId");
			 if($(id).is(':checked')){
				 document.getElementById("emailPanel").className = "col-sm-12";
				 document.getElementById("emailBody").className = "col-sm-12";
			 }else{
				 document.getElementById("emailPanel").className = "col-sm-6";
				
				 document.getElementById("emailBody").className = "col-sm-6";
			 }
         }
	</script>
</body>
</html>




<%--  new   <!DOCTYPE html>


<%@page import="java.util.List"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.nmims.beans.MailBean"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>

<html lang="en">
    

	
    
    <jsp:include page="common/jscss.jsp">
	<jsp:param value="Welcome to Student Zone" name="title"/>
    </jsp:include>
    

    <%String mailBeanId = ""; %>
    <body>
    
    	<%@ include file="common/header.jsp" %>
    	
    	
        
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="common/breadcrum.jsp">
		<jsp:param value="Student Zone;Email Communications" name="breadcrumItems"/>
		</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<%@ include file="common/left-sidebar.jsp" %>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="common/studentInfoBar.jsp" %>
              						
              						
              						<div class="sz-content">
								<%try{ %>
										<h2 class="red text-capitalize"><i class="fa fa-envelope" ></i>My Inbox</h2>
									<div class="clearfix"></div>
										<div style="float:right">
											<button type="button" class="btn btn-info btn-sm" onclick="changeLayout('Vertical');">Vertical Reading</button>
											<button type="button" class="btn btn-info btn-sm" onclick="changeLayout('Horizontal');">Horizontal Reading</button>
											</div>
										<div class="clearfix"></div>
										
										<div class="row">
											 
										<div class="col-sm-6" id="emailPanel">
		              					<div class="panel-content-wrapper">
		              					
											<%@ include file="common/messages.jsp" %>
											<c:if test="${rowCount > 0 }" >
											
											<div class="table-responsive">
											<div class="col-sm-12">
												<table class="table table-striped table-hover tables" style="font-size:12px">
													<thead>
														<tr>
															<th>From </th>
															<th>Subject</th>
															<th>Communication Date/Time</th>	
														</tr>
													</thead>
												 <tbody>
												<c:forEach var="mailBean" items="${listOfCommunicationMadeToStudent}" varStatus="status">
												 		<fmt:parseDate value="${mailBean.createdDate}" var="createdDate" pattern="yyyy-MM-dd HH:mm:ss" type="BOTH" />
							       						<tr>
											            <td><c:out value="${mailBean.fromEmailId}"/></td>
											            <td ><a target="iframe_a" onclick="load(${mailBean.id})"><c:out value="${mailBean.subject}" /></a></td>
											            <td>
											            <fmt:formatDate pattern="dd-MMM-yyyy, HH:mm" value = "${createdDate}" />
											            </td>
											             </tr>   
					   						
												 </c:forEach>
												 </tbody>
												
												</table>
											</div>
										
										</div>
											</c:if>
									
									</div>
              						</div>	
              						<div class="col-sm-6" id="emailBody" >
              						<div class="panel-content-wrapper">
              						<p id="verti_side2">Mail Content</p>
              						
              					    <iframe id="iframe_a" style="display:none " name="iframe_a" src="/studentportal/singleEmailForm?id=${mailBean.id}" height="950" width="100%"></iframe>
              					    
              						</div>	
              						</div>
              						 
              						</div>
              						
              					
              		</div>
                            
					</div>
					<c:if test="${rowCount > 0 }" >
<!-- MODAL FOR INDIVIDUAL ANNOUNCEMENTS-->
<c:forEach var="mailBean" items="${listOfCommunicationMadeToStudent}" varStatus="status">

<div class="modal fade announcement" id="emailContentForStudent${status.count}" tabindex="-1" role="dialog">
<div class="modal-dialog" role="document">
<div class="modal-content modal-md">
<div class="modal-header">
<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
<h4 class="modal-title">MAIL CONTENT</h4>
</div>
<div class="modal-body">
<p><c:out value="${mailBean.body}"/></p>
</div>
<div class="modal-footer">
<button type="button" class="btn btn-default" data-dismiss="modal">DONE</button>
</div>
</div>
</div>
</div>
</c:forEach>
</c:if>
            </div>
        </div>
       
            <%}catch(Exception e){
            	e.printStackTrace();}%>
            
  	
        <jsp:include page="common/footer.jsp"/>
         
         
         <script>
         $(document).ready(function(){
        	 
        	
				$('.tables').DataTable( {

			        initComplete: function () {
			        	 this.api().columns().every( function () {
			               var column = this;
			                var headerText = $(column.header()).text();
			                console.log("header :"+headerText);
			         
			              
			                column.data().unique().sort().each( function ( d, j ) {
			                    select.append( '<option value="'+d+'">'+d+'</option>' )
			                } );
			             
			            } );
			        } 
			    } );
         });
 
         function load(id){
         	 var x =  document.getElementById("iframe_a");
         	document.getElementById("iframe_a").src = "/studentportal/singleEmailForm?id="+id;
         	  if (x.style.display === "none") {
  		        x.style.display = "block";
         }}
         
         function changeLayout(value)
         {
			 if(value =='Vertical'){
				 document.getElementById("emailPanel").className = "col-sm-6";
				 document.getElementById("emailBody").className = "col-sm-6";
			 }else if(value =='Horizontal'){
				 document.getElementById("emailPanel").className = "col-sm-12";
				 document.getElementById("emailBody").className = "col-sm-12";
			 }
         }
	</script>   
		
    </body>
</html>

 --%>

<%-- <!DOCTYPE html>


<%@page import="java.util.List"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.nmims.beans.MailBean"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>

<html lang="en">
    

	
    
    <jsp:include page="common/jscss.jsp">
	<jsp:param value="Welcome to Student Zone" name="title"/>
    </jsp:include>
    

    
    <body>
    
    	<%@ include file="common/header.jsp" %>
    	
    	
        
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="common/breadcrum.jsp">
		<jsp:param value="Student Zone;Email Communications" name="breadcrumItems"/>
		</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<%@ include file="common/left-sidebar.jsp" %>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="common/studentInfoBar.jsp" %>
              						
              						
              						<div class="sz-content">
								<%try{ %>
										<h2 class="red text-capitalize"><i class="fa fa-envelope" ></i>My Inbox</h2>
										<div class="clearfix"></div>
		              					<div class="panel-content-wrapper">
											<%@ include file="common/messages.jsp" %>
											<c:if test="${rowCount > 0 }" >
										
								
											<div class="table-responsive">
											
												<table class="table table-striped table-hover tables" style="font-size:12px">
													<thead>
														<tr>
															<th>From </th>
															<th>Subject</th>
															<th>Communication Date/Time</th>
														
															
														</tr>
													</thead>
												 <tbody>
												 	<c:forEach var="mailBean" items="${listOfCommunicationMadeToStudent}" varStatus="status">
												 		<fmt:parseDate value="${mailBean.createdDate}" var="createdDate" pattern="yyyy-MM-dd HH:mm:ss" type="BOTH" />
							       						<tr>
											            <td><c:out value="${mailBean.fromEmailId}"/></td>
											            <td><a href="/studentportal/singleEmailForm?id=${mailBean.id}"><c:out value="${mailBean.subject}" /></a></td>
											            <td>
											            <fmt:formatDate pattern="dd-MMM-yyyy, HH:mm" value = "${createdDate}" />
											            </td>
											             </tr>   
					   						 </c:forEach>
												 
												 </tbody>
												
												</table>
											</div>
										
										
											</c:if>
											</div>
              								
              						</div>
              						
              				</div>
              		
                            
					</div>
					<c:if test="${rowCount > 0 }" >
<!-- MODAL FOR INDIVIDUAL ANNOUNCEMENTS-->
<c:forEach var="mailBean" items="${listOfCommunicationMadeToStudent}" varStatus="status">

<div class="modal fade announcement" id="emailContentForStudent${status.count}" tabindex="-1" role="dialog">
<div class="modal-dialog" role="document">
<div class="modal-content modal-md">
<div class="modal-header">
<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
<h4 class="modal-title">MAIL CONTENT</h4>
</div>
<div class="modal-body">
<p><c:out value="${mailBean.body}"/></p>
</div>
<div class="modal-footer">
<button type="button" class="btn btn-default" data-dismiss="modal">DONE</button>
</div>
</div>
</div>
</div>
</c:forEach>
</c:if>
            </div>
        </div>
       
            <%}catch(Exception e){
            	e.printStackTrace();}%>
            
  	
        <jsp:include page="common/footer.jsp"/>
         
         
         <script>
         $(document).ready(function(){
        	 
        	
				$('.tables').DataTable( {
					   
					
			        initComplete: function () {
			        	
			        
			            this.api().columns().every( function () {
			                var column = this;
			                var headerText = $(column.header()).text();
			                console.log("header :"+headerText);
			             if(headerText == "Subject")
			                {
			                   var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
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
         });
 </script>   
		
    </body>
</html> --%>
