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
    <body>

		<%@ include file="../common/header.jsp"%>



		<div class="sz-main-content-wrapper">

			<jsp:include page="../common/breadcrum.jsp">
				<jsp:param value="Exam;Assignments" name="breadcrumItems" />
			</jsp:include>

			<div class="sz-main-content menu-closed">
				<div class="sz-main-content-inner">

					<jsp:include page="../common/left-sidebar.jsp">
						<jsp:param value="Tests" name="activeMenu" />
					</jsp:include>


					<div class="sz-content-wrapper dashBoard demoExam">
						<%@ include file="../common/studentInfoBar.jsp"%>

						<div class="sz-content">

							
							<div class="clearfix"></div>
							<div class="panel-content-wrapper">
								
								<!-- Code for page goes here start -->
								
								
								<div class="jumbotrom">
								<div class="container-fluid">
								<h2 class="red text-capitalize">
									Internal Assignment: Multiple Choice Questions (MCQ) Guidelines
								</h2>
								</div>
								<div class="container-fluid">
									<h4>
									  	W.e.f. December 2018 examination, NGASCE introduces Internal Assignment comprising of  Multiple Choice Questions (MCQs) having 30% credence in place of regular Internal Assignment question paper (PDF format) currently only for the subjects* - Business Communication and Etiquette / Business Economics / Management Theory and Practice / Organisational Behaviour / Corporate Social Responsibility and Information Systems for Managers. 
									</h4>
									<h5>
Pls. Note: For the remaining subjects, Internal Assignment Question Paper (PDF format) for Dec. 2018 exam cycle would be uploaded with regular Internal Assignment preparation guidelines.
									</h5>
								<ul  class="list-group">

<li class="list-group-item">
								  
	Internal Assignment has 30% credence. As per the applicable subject, every student has to attempt and submit the Internal Assignment on or before the last date declared by NGA-SCE for that respective exam cycle. 
</li>
<li class="list-group-item">

	Students need to attempt and submit Internal Assignment online through <b>Student Zone &rarr; Examination Menu &rarr; Internal Assignment (MCQs)</b> module
</li>
<li class="list-group-item">

	Internal Assignment is uploaded under respective subject as applicable*. 
</li>
<li class="list-group-item">

	For each subject, time duration is <b>30 minutes</b>. Once the test has started, student needs to attempt the questions and duly submit the test before the set end time. The Timer is displayed on screen. If the duration of the test is over, it would be auto-submitted with all the answers the student has clicked on &ldquo;Save and Next&ldquo; only.
</li>
<li class="list-group-item">
	Pls. Note: <b>Do not refresh</b> the page/screen once the Test has started.
</li>
<li class="list-group-item">
	Students must ensure there is proper internet connectivity at their side while attempting the tests. 
</li>
<li class="list-group-item">
	In case of some issue student can rejoin the test but it has be within the duration of test (i.e. within 30mins of starting the test) and student can resume from where you were interrupted. However no network disruption /power failure issues faced at the student&rsquo;s side will be considered.
</li>
<li class="list-group-item">
	Total weightage of Internal Assignment is 30 marks. Internal Assignment comprises of 27 questions, where 26 questions would be multiple choice questions MCQs (Either Single Select  i.e. one right option or Multiple Select  i.e. more than one right option) of 1 mark weightage and one question would be Case Study based on which four sub-questions (MCQ) would be asked of 1 mark weightage each. For case study questions, all four sub-questions need to be attempted and click on &ldquo;Save & Next&lsquo;.
</li>
<li class="list-group-item">
	There is no negative marking in MCQs.
</li>
<li class="list-group-item">
	Student can attempt the questions in any order within time frame and complete the online internal test within the set time frame.
</li>
<li class="list-group-item">
	Only one question will be displayed on the screen at a time. Student need to select the right option/s (based on Single Select / Multi-Select) and click on &ldquo;Save & Next&rdquo; button.
</li>
<li class="list-group-item">
	You can skip a question using &ldquo;Skip&rdquo; button and go to next question. 
</li>
<li class="list-group-item">
	You visit previous question using &ldquo;Previous&rdquo; button.
</li>
<li class="list-group-item">
	A question can be Tagged for reviewing later by using button with &ldquo;Bookmark Icon&rdquo;  
</li>
<li class="list-group-item">
	After Saving all the answers, student can click on &ldquo;End Test&rdquo;. System will prompt &ldquo;Are you Sure?&rdquo; click on Yes, I&rsquo;m Sure. If student is still not sure, click on &ldquo;No, Cancel it&rdquo; and cross check once again all attempted and unattempted questions if there is still time and then again click on End Test and Yes, I&rsquo;m Sure.
</li>
<li class="list-group-item">
	Result of Internal Assignment will not be displayed immediately. It would be notified separately when the results will be displayed. 
</li>
<li class="list-group-item">
	In each subject wherever applicable, student will get only one attempt towards Internal Assignment  MCQ format. In case the first online test attempt fails for any reason whatsoever, the <b>2nd attempt will be additionally charged Rs. 500/- per subject</b>. Student needs to send request email for consideration only to ngasce.exams@nmims.edu with the student number and subject. There is no third attempt.  
</li>
<li class="list-group-item">
	Please exercise utmost caution while you take the MCQ format assignment for the applicable subject/s. 
</li>
<li class="list-group-item">
	After every completion of Internal Assignment an auto-generated email is sent from the system to the students registered email id. Student must keep the copy of the same for records. 
</li>
<li class="list-group-item">
	Auto-generated submission email is only the acknowledgement of the assignment attempted by the student in the system (right/wrong/blank) as the case may be and not confirmation from NGA-SCE certifying it is the rightly attempted assignment. 
</li>
<li class="list-group-item">
	Student need to check whether he/she is enrolled to the right courses/subjects (semester wise) as per the program specialization and likewise check the Internal Assignment question paper / MCQ. 
</li>
<li class="list-group-item">
	<b>
	Pls. Note: There could be students who would have combination of both the internal patterns based on the subject: Internal Assignment (PDF) format as well as Internal Assignment (MCQ format). These students will have to attempt both the pattern as applicable to the subject/s for December, 2018 exam cycle.
	</b>
</li>
<li class="list-group-item">
	Students need to attempt and submit the internal assignment well before time and do not wait for the last minute submission. Students who are overseas need to follow Indian Standard Time.
</li>
<li class="list-group-item">
	No request for internal assignment will be considered post the deadline. 
</li>
<li class="list-group-item">
	Incase of any doubt or query regarding Internal Assignment: Students can get in touch by email at ngasce.exams@nmims.edu or call on TOLL FREE No. 1800-1025-136 Mon-Sat: 9.00a.m. to 6.00p.m. before attempting and before the last date declared by NGASCE. No last minute query/request will be accepted. Pls. mention your student number (SAP ID) in all communication with the institute. 
</li>
								</ul>
<p style="font-size:16px;">								
Kindly Note: <br> 
In case the student directly appears for the term end examination without submitting the internal assignment, it cannot be declared as pass. In such cases that subject result will be kept on hold** as aggregate passing is the criteria. Before the completion of program validity, the student will have to submit the assignment/test of the respective subject/s in the next exam cycle to pass the subject.
	</p>							  	
								
								</div>
								<a class="btn btn-primary" href="/exam/startStudentTest?testId=${testId}">I have read and understood the guidelines. Start test</a>
								</div>
								
								
								<!-- Code for page goes here end -->
								
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		
		<jsp:include page="../common/footer.jsp" />
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


    </body>


</html>