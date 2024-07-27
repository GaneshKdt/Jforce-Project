<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>  
        <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
  
<!DOCTYPE html>
<html>
<link rel="stylesheet" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/css/dataTables.bootstrap.css">
	<jsp:include page="jscss.jsp">
		<jsp:param value="Upgrad Live Setting" name="title" />
	</jsp:include>
<body >

	 <%@ include file="header.jsp"%>
	 	 <section class="content-container login">
        <div class="container-fluid customTheme">
          <div class="row">
             <legend>Upgrad Live Setting For MBA-X</legend>
          </div> <!-- /row -->
           
          <%@ include file="messages.jsp"  %>
		  
		  <div class="">          
            <div class="col-xs-18 panel-body" >
	          <div class="bullets">
                <h4>Upgrad Live Setting For MBA-X</h4>
                
              </div>
             
              <br>
              <div class="panel-body">
              	
               
      
                	
             			<div class="table-responsive">
							<table class="table table-striped table-hover tables"  style="font-size:12px">
								<thead>
									<tr> 
										<th>Sr. No.</th>
										<th>Name</th>
										<th>Exam Year</th>
										<th>Exam Month</th>
										<th>Acad Year</th>
										<th>Acad Month</th>
										<th>Consumer Type</th>
										<th>Program Structure</th>
										<th>Program</th>
										<th>Applicable Type</th>
										<th>Batch Name</th>
										<th>Subject</th>
										<th>Module Name</th>
										<th>Start Time</th>
										<th>End Time</th>
										<th>Results</th>
									</tr>
								</thead>
								<tbody >
								<c:forEach var="listMBAX" items="${listMBAXTest}" varStatus="status">
									<tr>
										<td><c:out value="${status.count}" /></td>
										<td><c:out value="${listMBAX.testName}" /></td>
										<td><c:out value="${listMBAX.year}" /></td>
										<td><c:out value="${listMBAX.month}" /></td>
										<td><c:out value="${listMBAX.acadYear}" /></td>
										<td><c:out value="${listMBAX.acadMonth}" /></td>
										<td><c:out value="${listMBAX.consumerType}" /></td>
										<td><c:out value="${listMBAX.programStructure}" /></td>
										<td><c:out value="${listMBAX.program}" /></td>
										
										
																													       
									   	<td><c:out value="${listMBAX.applicableType}"/></td>
									   	<c:choose>
								         <c:when test="${listMBAX.applicableType eq 'module'}">
									     	<td><c:out value="${listMBAX.name}"/></td>
										<td><c:out value="${listMBAX.subject}" /></td> 
										<td><c:out value="${listMBAX.referenceBatchOrModuleName}" /></td> 
								         </c:when>
								         <c:when test="${listMBAX.applicableType eq 'batch'}">
										<td><c:out value="${listMBAX.referenceBatchOrModuleName}" /></td> 
										<td><c:out value="${listMBAX.subject}" /></td> 
										<td>NA</td> 
								         </c:when>
								         <c:when test="${listMBAX.applicableType eq 'old'}">
										<td>NA</td> 
										<td><c:out value="${listMBAX.subject}" /></td> 
										<td>NA</td> 
								         </c:when>
								         <c:otherwise>
								         	<td>Error in the test contact dev team.</td>
										<td>Error</td> 
										<td>Error</td> 
								         </c:otherwise>
								       </c:choose>

										<td><c:out value="${fn:replace(listMBAX.startDate,'T', ' ')}"></c:out></td>
										<td><c:out value="${fn:replace(listMBAX.endDate, 'T', ' ')}"></c:out></td>
										
										<td >											
										<c:if test="${listMBAX.showResultsToStudents == 'N'}">
										<a href="/exam/admin/showResultsForMBAXTest?testid=${listMBAX.testId}&referenceId=${listMBAX.referenceId}" onclick="return confirm('Show Result For ${listMBAX.testName}. Are you sure?')" >
												<span style=" color:#d2232a !important;">Show Results</span>
											</a>
										</c:if>
										
										<c:if test="${listMBAX.showResultsToStudents == 'Y'}">
										<a href="/exam/admin/hideResultsForMBAXTest?testid=${listMBAX.testId}&referenceId=${listMBAX.referenceId}" onclick="return confirm('Hide Result For ${listMBAX.testName}. Are you sure?')" >
												<span style=" color:blue !important;">Hide Results</span>
											</a>
										</c:if>
										
										</td>
										
								</c:forEach>	 
								</tbody>
							</table>
							
						</div>
			
               		<br>
               		
               		
                
                
              </div> <!--/module-box-->
            </div> <!-- /col-xs-6 -->
          </div> <!-- /row -->
          
        </div> <!-- /container -->
    </section>
	 
	 <jsp:include page="footer.jsp" />
   <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery.tabledit.js"></script>
	<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
	<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js"></script>
	
	
	
	<script>
		
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
              
                if(headerText == "Exam Year")
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
                
                if(headerText == "Applicable Type")
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
                
                if(headerText == "Exam Month")
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
                
                if(headerText == "Acad Year")
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
                if(headerText == "Acad Month")
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
                
                if(headerText == "Consumer Type")
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
                if(headerText == "Program Structure")
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
                if(headerText == "Program")
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
                
                if(headerText == "Batch Name")
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
                
                if(headerText == "Module Name")
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
	
	</script>
	
	
	
</body>
</html>