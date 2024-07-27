
<!DOCTYPE html>
<html lang="en">
	
<%@page import="com.nmims.beans.Person"%>
  
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
  
    <jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Create Online Test" name="title"/>
    </jsp:include>
    
 
<link
	href="https://gitcdn.github.io/bootstrap-toggle/2.2.2/css/bootstrap-toggle.min.css"
	rel="stylesheet">
<script
  src="https://code.jquery.com/jquery-3.3.1.js"
  integrity="sha256-2Kok7MbOyxpgUVvAk/HJ2jigOSYS2auK4Pfzbm7uH60="
  crossorigin="anonymous"></script>
<script
	src="https://gitcdn.github.io/bootstrap-toggle/2.2.2/js/bootstrap-toggle.min.js"></script>

    
    
    <style>
    	.evaluateQuestionsButton {
		   position:relative;
		}
		.evaluateQuestionsButton[data-badge]:after {
		   content:attr(data-badge);
		   position:absolute;
		   top:-10px;
		   right:-10px;
		   font-size:.7em;
		   background: #D2232A ;
		   color:white;
		   width:18px;height:18px;
		   text-align:center;
		   line-height:18px;
		   border-radius:50%;
		   box-shadow:0 0 1px #333;
		}
    
    </style>
    
    <body>
    
    	<%@ include file="../adminCommon/header.jsp" %>
        <div class="sz-main-content-wrapper">
        
		
        <!-- Custom breadcrumbs as requirement is diff. Start -->
			<div class="sz-breadcrumb-wrapper">
			    <div class="container-fluid">
			        <ul class="sz-breadcrumbs">
			        		<li><a href="/exam/">Exam</a></li>
			        		<li><a href="/exam/viewAllTests">Tests</a></li>
			        	
			        </ul>
			        <ul class="sz-social-icons">
			            <li><a href="https://www.facebook.com/NMIMSSCE" class="icon-facebook" target="_blank"></a></li>
			            <li><a href="https://twitter.com/NMIMS_SCE" class="icon-twitter" target="_blank"></a></li>
			            <!-- <li><a href="https://plus.google.com/u/0/116325782206816676798/posts" class="icon-google-plus" target="_blank"></a></li> -->
						
			        </ul>
			    </div>
			</div>
			<!-- Custom breadcrumbs as requirement is diff. End -->
        	
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="../adminCommon/left-sidebar.jsp">
								<jsp:param value="" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="../adminCommon/adminInfoBar.jsp" %>
              						<div class="sz-content">
								
											<h2 class="red text-capitalize">View All Online Tests </h2>
											<div class="clearfix"></div>
							<div class="panel-content-wrapper" style="min-height:450px;">
								<%@ include file="../adminCommon/messages.jsp" %>
							
							<!-- Code For Page Goes in Here Start -->
							
																<div class="table-responsive">
																
																	<table class="table table-striped table-hover tables"
																		style="font-size: 12px">
																
																		<thead>
																			<tr>
																				<th>Sr. No.</th>
																				<th>Name</th>
																				<th>Year</th>
																				<th>Month</th>

																				<th>Subject</th>
																				<th>Start Time</th>
																				<th>End Time</th>
																				<th>Evaluate</th>
																				<th>Actions</th>
																			</tr>
																		</thead>
																		<tbody>

																			<c:forEach var="test" items="${allTests}"
																				varStatus="status">
																				<tr>
																					<td><c:out value="${status.count}" /></td>
																					<td><c:out value="${test.testName}" /></td>
																					<td><c:out value="${test.year}" /></td>
																					<td><c:out value="${test.month}" /></td>
																					<td><c:out value="${test.subject}" /></td>

																					<td><c:out
																							value="${fn:replace(test.startDate,'T', ' ')}"></c:out></td>
																					<td><c:out
																							value="${fn:replace(test.endDate, 'T', ' ')}"></c:out></td>
																					
																					
																					<td>
																						<a href="/exam/evaluateTestAnswersForm?id=${test.id}"
																							title="Evaluate Test Answers "
																							class="evaluateQuestionsButton"
																							 data-badge="<c:out value="${test.noOfAnswersToEvaluate}" />"
																						>
																							<i class="glyphicon glyphicon-check" style="font-size:24px"></i>										
																						</a>
																						&nbsp;
																						
																					<td>
																						<a href="/exam/addTestQuestionsForFacultyForm?id=${test.id}"
																							title="Add/Edit Question For Test"
																						>
																							<i class="fa-solid fa-pen-to-square" style="font-size:24px"></i>										
																						</a>
																						&nbsp;
																						<%if( roles.indexOf("Insofe") != -1 ){ %>
					
																						|&nbsp;
																						
																						<a href="/exam/IAPreviewQuestionsForFacultyView?id=${test.id}"
																							title="Preview All Questions as shown to students"
																						>
																							<i class="fa-regular fa-eye" style="font-size:24px"></i>										
																						</a>
																						&nbsp;
																						<%} %>
																						
																					</td>
																				</tr>
																			</c:forEach>


																		</tbody>
																	</table>
																</div>	
							
							<!-- Code For Page Goes in Here End -->
							</div>
							
							</div>
              			</div>
    				</div>
			   </div>
		    </div>
        <jsp:include page="../adminCommon/footer.jsp"/>
        
        
        <script>
       
        
</script>

		<script>
		
	$('.tables').DataTable( {
        initComplete: function () {
        	var totalTest = ${getNoOfTestsForFaculty};
        	 var node = document.createElement("label");
        	 node.style.cssText = 'float:left;  font-size: 20px;font-family:bold';

             var textnode = document.createTextNode("Total Test : "+totalTest);
             node.appendChild(textnode);
             document.getElementById("DataTables_Table_0_filter").appendChild(node);
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
	
	</script>
		
    </body>    
</html>