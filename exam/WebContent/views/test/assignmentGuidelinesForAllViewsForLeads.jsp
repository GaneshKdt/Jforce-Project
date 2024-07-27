<!DOCTYPE html>


<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>


<html lang="en">

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')"
	var="server_path" />


<jsp:include page="../common/jscss.jsp">
	<jsp:param value=" Assignment Guidelines " name="title" />
</jsp:include> 

<%

%>
<style>
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
								
								
								<div class="jumbotrom">
								<div class="container-fluid">
								<h2 class="red text-capitalize">
									Internal Assignment Guidelines
								</h2>
								</div>
								<div class="container-fluid">
									
								<ul  class="list-group">





<li class="list-group-item">
	For each subject, time duration of Internal Assessment Test is 30 minutes. Once the test has started, student needs to attempt the questions and duly submit the test before the set end time. The "Timer" is displayed on screen. If the duration of the test is over, it would be auto-submitted with all the answers the student has clicked on "Next" only.
</li>

<li class="list-group-item">
	<b>Pls. Note: Do not refresh the page, press back button or any navigation keys  once the Internal Assessment Test has started.</b>
</li>


<li class="list-group-item">
	Students must ensure there is proper internet connectivity at their side while attempting the tests. No network disruption /power failure/ any other I.T. issues faced at the student"s side will be considered.
</li>

<li class="list-group-item">
	In case of some technical / IT issue student can rejoin the test but it has to be within the duration of assignment (i.e. within 30mins of starting the test) and student can resume from where it was interrupted. However, no network disruption /power failure issues faced at the student"s end will be considered.
</li>

<li class="list-group-item">
 Internal Assessment Test questions would be multiple choice questions MCQs (Either Single Select - i.e. one right option or Multiple Select - i.e. more than one right option) or True/False of 1 mark weightage or descriptive questions.
</li>

<li class="list-group-item">
	There is no negative marking in MCQs.
</li>

<li class="list-group-item">
	Student can attempt the questions in any order within time frame and complete the online internal test within the set time frame.
</li>

<li class="list-group-item">
	Only one question will be displayed on the screen at a time. Student need to select the right option/s (based on Single Select / Multi-Select) and click on "Next" button.
</li>

<!-- li class="list-group-item">
	You visit previous question using "Previous" button & visit next question by clicking "Next" button.
</li!-->

<li class="list-group-item">
	After selecting option it is mandatory to click on "Save" or "Save & Next" button to save answer.<br />
	"Save" button will only save you answer whereas "Save & Next" button will save your answer and go to next question.
</li>

<li class="list-group-item">
	To navigate through questions the status column is to be used, By clicking on a particular question number that question will be visible in main view.
</li>

<li class="list-group-item">
	A question can be Tagged for reviewing later by using button with "Bookmark Icon"  
</li>

<li class="list-group-item">
	After Saving all the answers, student can click on "FINISH ASSIGNMENT". System will prompt "Are you Sure?" click on Yes, I"m Sure. If student is still not sure, click on "No, Cancel it" and cross check once again all attempted and unattempted questions if there is still time and then again click on FINISH ASSIGNMENT and Yes, I"m Sure.
</li>

<li class="list-group-item">
	"FINISH ASSIGNMENT" button will be active to submit only after last question is visited.
</li>

<li class="list-group-item">
	In each subject wherever applicable, student will get only one attempt towards Internal Assessment Test. 
</li>

<li class="list-group-item">
	Please exercise utmost caution while you take the online assessment test for the applicable subject/s. 
</li>

<li class="list-group-item">
	After every completion of Internal Assessment Test an auto-generated email is sent from the system to the students registered email id. Student must keep the copy of the same for records. 
</li>

<li class="list-group-item">
	Auto-generated submission email is only the acknowledgement of the test attempted by the student in the system (right/wrong/blank/) as the case may be and not confirmation from NGA-SCE certifying it is the rightly attempted/completed test. 
</li>

<li class="list-group-item">
	Students need to submit the assessment well before time and do not wait for the last minute submission. Students who are overseas need to follow Indian Standard Time.
</li>

<li class="list-group-item">
	No request for assessment re-submission will be considered.
</li>

<li class="list-group-item">
	In case of any doubt or query regarding assignment: Student can get in touch by email at ngasce.exams@nmims.edu for clarification before last date of assignment submission. Pls. mention your student details in all communication with the institute.

</li>



								</ul>
<p style="font-size:16px;">								
</p>							  	
								<div class="row" style="padding-bottom:60px;">
								
								<div class="col-xs-12" >
								
								<a 
								  class="btn btn-primary"
								  style="white-space: normal; text-align:left;"
								  href="/exam/startStudentTestForAllViewsForLeads?testIdForUrl=${testIdForUrl}&sapidForUrl=${sapidForUrl}">
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
	<script src="/exam/assets/js/jquery-1.11.3.min.js"></script>
	<script src="/exam/assets/js/bootstrap.js"></script>
<script src="/exam/assets/js/jquery.tabledit.js"></script>

	<script src="/exam/resources_2015/js/vendor/jquery-ui.min.js"></script>
	<script
		src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
	<script src="/exam/resources_2015/js/vendor/dataTables.bootstrap.js"></script>
	<script
		src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js"></script>
        		
        		<script>
		
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