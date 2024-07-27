<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html class="no-js"> <!--<![endif]-->
	
	<jsp:include page="jscss.jsp">
		<jsp:param value="Result Notice" name="title" />
	</jsp:include>
	
	<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')" var="server_path" />
	
	<%
	StudentBean student = (StudentBean)session.getAttribute("student");
	String programStructure = student.getPrgmStructApplicable();
	if("Jul2014".equals(programStructure)){
		response.sendRedirect((String)pageContext.getAttribute("server_path") + "exam/student/getMostRecentResults");
	}
	
	%>
	<body class="inside">
	
   <%@ include file="header.jsp"%>
    
    <section class="content-container login">
        <div class="container-fluid customTheme">
          <div class="row">
             <legend>INSTRUCTIONS TO THE STUDENTS FOR ${mostRecentResultPeriod} OFFLINE EXAMINATION RESULT</legend>
          </div> <!-- /row -->
           
          <%@ include file="messages.jsp" %>
		   
		  <div class="">          
            <div class="col-xs-18 panel-body" >
	          <div class="bullets">
                <p>Dear Student,</p>
                <ul >
                  <li><p align="justify"><b>Please note that this is a provisional result.</b><br/></p></li>
                  <li><p align="justify">Pass Marks: 50 out of 100. The marks displayed are out of 70 for semester-end examination and 
                  out of 30 for Internal Continuous Assessments (i.e. assignment).<br/></p></li>
                  <li><p align="justify">The project marks displayed are out of 100.<br/></p></li>
                  <li><p align="justify">For <b>MAX LIFE INSURANCE students only </b>- Pass marks: 50 out of 100.  The marks displayed are out of 50 for semester-end examination and 
                  out of 50 for Internal Continuous Assessments (i.e. assignment).<br/></p></li>
				  
				  <li><p align="justify"><b>Application for Photocopy/ Revaluation of answer books (under Grievance Redressal Process):</b> 
				  After declaration of September, 2018 result, in case a student is not satisfied with the term end 
				  exam marks awarded to him/ her and who wish to apply for photocopy or revaluation of answer 
				  book/s can login to service request on or before the date announced by NGA-SCE by paying the applicable fees. <br>
				  
				  <b>Pls. Note:</b> Applying for revaluation does not indicate that the marks 
				  would increase than the original score. It could remain same or increase or even decrease than the original score.<br>
				  
				  </p>
				  
				  
				   
					   <p align="justify">Under Grievance Rederessal Process student can apply for:
						   <ul>
						   		<li>
						   			<p align="justify">Only Photo copy of the answer book by paying prescribed fee.</p>
						   		</li>
						   		<li>
						   			<p align="justify">Apply for Photocopy of the answer book as well as apply for Revaluation of answer book by paying the prescribed fees.</p>
						   		</li>
						   		<li>
						   			<p align="justify">Apply only for Revaluation without obtaining photo copy of his/ her answer book/s.</p>
						   		</li>
						   </ul>
						   
						   
						   Obtaining Photo copy of answer book:   Rs. 500/- per subject<br>
						   Written Exam Answer Book Revaluation fee:  Rs. 1000/- per subject

					   </p>
				   </li>
				  
				  <li><p align="justify">Last date to apply for obtaining the photocopies of Answer books:  19th October, 2018 before 23.59 pm  
				  Arrangement to provide photocopies will be done by the NMIMS University Exam Department.<br/></p></li>
				  
				  <li><p align="justify">Last date to apply for revaluation under grievance redressal process: 21st October 2018 before 23.59 p.m.<br/></p></li>
				  
				  <li><p align="justify"><b>December 2018 Offline Examination Time Table</b><br/>
				  The final examination time table for the December 2018 session will be published in the last week of February on NMIMS website.  <br/>
				  </p>
				  <a href="getMostRecentResults" class="btn btn-large btn-primary">View Results</a> 
				  		  
				  </li>
				  </ul>
				  <div class="row">
				  <div class="col-xs-4"></div>
				  <div class="col-xs-8"></div>
				  <div class="col-xs-6"><b>Controller of Examinations</b></div>
				  </div>
                
				  
				  

				  
                
              </div>
              
            </div> 
          </div> 
           
           
           <!-- <div class="row">          
            <div class="col-xs-18">
	          <div class="module-box">
                <p>Dear Student,</p>
                <ol class="policy">
                <li><p align="justify">You may check the outcome of your application for verification of marks by clicking 'View Result' button below.<br/></p></li>
                <li><p align="justify">In case of change in the marks after verification, the revised marks will be reflected in the result page against the subject concerned.<br/></p></li>
                <li><p align="justify">In case there is no change after verification of marks, you will see your original marks.<br/></p></li>
                <br/>
                <a href="getMostRecentResults" class="">View Results</a>
                
                </ol>
	           </div>
	           </div>
           </div> -->
                
        </div> <!-- /container -->
    </section>
    
    <jsp:include page="footer.jsp" />
    
	
    
    
  </body>
</html>
 --%>
 


<!DOCTYPE html>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')" var="server_path" />
<%
StudentExamBean student = (StudentExamBean)session.getAttribute("studentExam");
	String programStructure = student.getPrgmStructApplicable();
	if("Online".equals(student.getExamMode())){
		response.sendRedirect((String)pageContext.getAttribute("server_path") + "exam/student/getMostRecentResults");
	}
%>
	
<html lang="en">  
    <jsp:include page="common/jscss.jsp">
	<jsp:param value="Result Notice" name="title"/>
    </jsp:include> 
    <body>
    
    	<%@ include file="common/header.jsp" %>
    
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="common/breadcrum.jsp">
		<jsp:param value="Student Zone;Exam;Result Notice" name="breadcrumItems"/>
		</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">                     
                            <div id = "sticky-sidebar"> 
              				<jsp:include page="common/left-sidebar.jsp">
								<jsp:param value="Exam Results" name="activeMenu"/>
							</jsp:include>
                           </div>
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="common/studentInfoBar.jsp" %>
              						
              						
              						<div class="sz-content">
								
										<h2 class="red text-capitalize">INSTRUCTIONS TO THE STUDENTS FOR SEPTEMBER 2018 OFFLINE EXAMINATION RESULT</h2>
										<div class="clearfix"></div>
		              					<div class="panel-content-wrapper">
											<%@ include file="common/messages.jsp" %>
											
											
								                <p>Dear Student,</p>
								                <ul >
								                  <li><p align="justify"><b>Please note that this is a provisional result.</b><br/></p></li>
								                  <li><p align="justify">Pass Marks: 50 out of 100. The marks displayed are out of 70 for term-end examination and out of 30 for Internal Continuous Assessments (i.e. assignment).<br/></p></li>
								                  <li><p align="justify">The project marks displayed are out of 100.<br/></p></li>
			
												  
												  <li><p align="justify"><b>Application for Photocopy/ Revaluation of answer books (under Grievance Redressal Process):</b> 
												  After declaration of September, 2018 result, in case a student is not satisfied with the term end exam marks awarded to him/ her 
												  and who wish to apply for photocopy or revaluation of answer book/s can login to service 
												  request on or before the date announced by NGA-SCE by paying the applicable fees.<br> 
												  <b>Pls. Note:</b> Applying for revaluation does not indicate that the marks would increase than the original score. 
												  It could remain same or increase or even decrease than the original score.<br>
												  
												  </p>
												  
												  
												   
													   <p align="justify">Under Grievance Rederessal Process student can apply for:
														   <ul>
														   		<li>
														   			<p align="justify">Only Photo copy of the answer book by paying prescribed fee.</p>
														   		</li>
														   		<li>
														   			<p align="justify">Apply for Photocopy of the answer book as well as apply for Revaluation of answer book by paying the prescribed fees.</p>
														   		</li>
														   		<li>
														   			<p align="justify">Apply only for Revaluation without obtaining photo copy of his/ her answer book/s.</p>
														   		</li>
														   		<li>
														   			<p align="justify">Obtaining Photo copy of answer book:  Rs. 500/- per subject</p>
														   		</li>
														   		<li>
														   			<p align="justify">Written Exam Answer Book Revaluation fee:  Rs. 1000/- per subject</p>
														   		</li>
														   </ul>
														   
								
													   </p>
												   </li>
												  
												  <li><p align="justify">Last date to apply for obtaining the photocopies of Answer books:  19th October, 2018 before 23.59 pm      
												  Arrangement to provide photocopies will be done by the NMIMS University Exam Department.<br/></p></li>
												  
												  <li><p align="justify">Last date to apply for revaluation under grievance redressal process: 21st October 2018 before 23.59 p.m. <br/></p></li>
												  
												  <li><p align="justify"><b>December 2018 Offline Mode Examination Time Table </b><br/>
												  The final examination time table for the December 2018 session will be published shortly on Student Zone of the portal.<br/>
												  </p>
												  <a href="getMostRecentResults" class="btn btn-large btn-primary">View Results</a> 
												  		  
												  </li>
												  </ul>
												  <div class="row">
												  <div class="col-xs-4"></div>
												  <div class="col-xs-8"></div>
												  <div class="col-xs-6"><b>Controller of Examinations</b></div>
												  </div>
								                
												  
												  
								
												  
								                
											
										</div>
              								
              						</div>
              				</div>
              		
                            
					</div>
            </div>
        </div>
            
  	
        <jsp:include page="common/footer.jsp"/>
            
		
    </body>
</html>