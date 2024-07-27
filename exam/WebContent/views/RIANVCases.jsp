<!DOCTYPE html>
<html lang="en">
	
<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>


<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
    <jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Search Student Marks" name="title"/>
    </jsp:include>
    
    
    
    <body>
    
    	<%@ include file="adminCommon/header.jsp" %>
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;Mark for RIA / NV" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="adminCommon/left-sidebar.jsp">
								<jsp:param value="" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="adminCommon/adminInfoBar.jsp" %>
              						<div class="sz-content">
								
											<h2 class="red text-capitalize">Mark for RIA / NV</h2>
											<div class="clearfix"></div>
													<div class="panel-content-wrapper" style="min-height:100%;">
													<div class = "js_result">
													
													</div>
																 
													
											<%@ include file="adminCommon/messages.jsp" %>
											<form:form  action="#" method="post" modelAttribute="studentMarks">
													<fieldset>
													<div class="col-md-4">
															<div class="form-group">
																<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control"  itemValue="${studentMarks.year}" required = "required" >
																	<form:option value="">Select Exam Year</form:option>
																	<form:options items="${yearList}" />
																</form:select>
															</div>
															
															<div class="form-group">
																	<form:input id="sapid" path="sapid" type="text" placeholder="SAP ID" class="form-control" value="${studentMarks.sapid}" required = "required"/>
															</div>
															
															<div class="form-group">
															<label class="control-label" for="submit"></label>
															<div class="controls" style = "margin-top : -40px">
																<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="searchStudentRIANVCase">Search</button>
																<c:if test="${rowCount > 0}">
																<button data-program="${studentMarks.program}" data-sapid="${studentMarks.sapid}" data-year="${studentMarks.year}" 
																        data-month="${studentMarks.month}" data-sem="${studentMarks.sem}" data-status="RIA" 
																        data-studentType="${studentMarks.studentType }"
																        id="ria" type="button" style = "color: white"  
																        class ="markAllRIANV">RIA ALL</button>
																<button  data-program="${studentMarks.program}" data-sapid="${studentMarks.sapid}" data-year="${studentMarks.year}" 
																        data-month="${studentMarks.month}" data-sem="${studentMarks.sem}" data-status="NV" 
																        data-studentType="${studentMarks.studentType }"
																        class ="markAllRIANV" id="nv" type="button" style = "color: white"  >NV ALL</button>
																        <button id="submit" name="submit" class="btn btn-large btn-primary" formaction="enableScoreForNVRIA">Score All</button>
																</c:if>
																
															</div>
														</div>
												</div>
													<div class="col-md-4">
																<div class="form-group">
																<form:select id="month" path="month" type="text" placeholder="Month" class="form-control"  itemValue="${studentMarks.month}" required = "required">
																	<form:option value="">Select Exam Month</form:option>
																	<form:options items="${monthList}" />
																</form:select>
															</div>
															<div class="form-group">
																<form:select id="sem" path="sem" placeholder="Semester" class="form-control"  value="${studentMarks.sem}">
																	<form:option value="">Select Semester</form:option>
																	<form:options items="${semList}" />
																</form:select>
															</div>
												</div>
												
												<div class="col-md-4">
																<div class="form-group">
																<form:select id="program" path="program" type="text"	placeholder="Program" class="form-control"  itemValue="${studentMarks.program}" required = "required">
																	<form:option value="">Select Program</form:option>
																	<form:options items="${programList}" />
																</form:select>
															</div>
															<div class="form-group">
																<form:select id="studentType" path="studentType" type="text"	placeholder="studentType" class="form-control"  itemValue="${studentMarks.studentType}">
																	<form:option value="">Select Student Type</form:option>
																	<form:options items="${studentTypeList}" />
																</form:select>
															</div>			
															
													</div>
											</fieldset>
									</form:form>
								</div>
								<c:choose>
							<c:when test="${rowCount > 0}">
								<h2>&nbsp;Search Results<font size="2px"> (${rowCount} Records Found)&nbsp;<a href="downloadStudentMarksResults">Download to Excel</a></font></h2>
								<div class="clearfix"></div>
									<div class="panel-content-wrapper">
									<div class="table-responsive">
								<table class="table table-striped table-hover" style="font-size:12px">
													<thead>
														<tr> 
															<th>Sr. No.</th>
															<th>Exam Year</th>
															<th>Exam Month</th>
															<th>SAP ID</th>
															<th>Student Name</th>
															<th>Program</th>
															<th>Sem</th>
															<th>Subject</th>
															<th>Written</th>
															<th>Assign</th>
															<th>Student Type</th>
															<th>Actions</th>
														
														</tr>
													</thead>
													<tbody>
													<% try{ %>
													<c:forEach var="studentMarks" items="${studentMarksList}" varStatus="status">
												        <tr>
												            <td><c:out value="${status.count}"/></td>
															<td><c:out value="${studentMarks.year}"/></td>
															<td><c:out value="${studentMarks.month}"/></td>
															<td><c:out value="${studentMarks.sapid}"/></td>
															<td nowrap="nowrap"><c:out value="${studentMarks.studentname}"/></td>
															<td><c:out value="${studentMarks.program}"/></td>
															<td><c:out value="${studentMarks.sem}"/></td>
															<td nowrap="nowrap"><c:out value="${studentMarks.subject}"/></td>
															<td class = "score" data-count = ${status.count }><c:out value="${studentMarks.writenscore}"/></td>
															<td><c:out value="${studentMarks.assignmentscore}"/></td>
															<td><c:out value="${studentMarks.studentType}"/></td>
												            <td> 
												            
												            <c:choose>
															   <c:when test="${studentMarks.writenscore eq 'RIA'}">
															      RIA <input checked   data-program="${studentMarks.program}" data-sapid="${studentMarks.sapid}" 
															      data-year="${studentMarks.year}" data-month="${studentMarks.month}" 
															      data-subject="${studentMarks.subject}"  data-sem="${studentMarks.sem}" 
															      data-studentType="${studentMarks.studentType}"
															      type="radio"  value="RIA" name = "writtenScore${status.count}" class = "markSingleSubjectRIANV" /> <br>
															   </c:when>
															   <c:otherwise>
															       RIA <input data-program="${studentMarks.program}" data-sapid="${studentMarks.sapid}" 
															       data-year="${studentMarks.year}" data-month="${studentMarks.month}" 
															       data-subject="${studentMarks.subject}"  data-sem="${studentMarks.sem}" 
															       data-studentType="${studentMarks.studentType}"
															       type="radio"  value="RIA" name = "writtenScore${status.count}" class = "markSingleSubjectRIANV" /> <br> 
															   </c:otherwise>
															 </c:choose>
															 <c:choose>   
															   <c:when test="${studentMarks.writenscore eq 'NV'}">
															       NV <input checked data-program="${studentMarks.program}" data-sapid="${studentMarks.sapid}" 
															       data-year="${studentMarks.year}" data-month="${studentMarks.month}" 
															       data-subject="${studentMarks.subject}"  data-sem="${studentMarks.sem}" 
															       data-studentType="${studentMarks.studentType}"
															       type="radio" value="NV" name = "writtenScore${status.count}" class = "markSingleSubjectRIANV" /> <br> 
															   </c:when>
															   <c:otherwise>
															       NV <input  data-program="${studentMarks.program}" data-sapid="${studentMarks.sapid}" 
															       data-year="${studentMarks.year}" data-month="${studentMarks.month}" 
															       data-subject="${studentMarks.subject}"  data-sem="${studentMarks.sem}" 
															       data-studentType="${studentMarks.studentType}"
															       type="radio" value="NV" name = "writtenScore${status.count}" class = "markSingleSubjectRIANV" /> <br> 
															   </c:otherwise>
															</c:choose>
															
															<c:set var="numberAsString">${studentMarks.writenscore}</c:set>
															<%--  <c:choose>  
															   <c:when test="${numberAsString.matches('[0-9]+')} ">
															       Score <input checked data-program="${studentMarks.program}" data-sapid="${studentMarks.sapid}" 
															       data-year="${studentMarks.year}" data-month="${studentMarks.month}" 
															       data-subject="${studentMarks.subject}"  data-sem="${studentMarks.sem}" 
															       data-studentType="${studentMarks.studentType}"
															       type="radio" value="Score" name = "writtenScore${status.count}" class = "markSingleSubjectRIANV" />
															   </c:when>
															   <c:otherwise>
															       Score <input  data-program="${studentMarks.program}" data-sapid="${studentMarks.sapid}" 
															       data-year="${studentMarks.year}" data-month="${studentMarks.month}" 
															       data-subject="${studentMarks.subject}"  data-sem="${studentMarks.sem}" 
															       data-studentType="${studentMarks.studentType}"
															       type="radio" value="Score" name = "writtenScore${status.count}" class = "markSingleSubjectRIANV" /> 
															   </c:otherwise>
															</c:choose> --%>
															
															<c:if test="${numberAsString.matches('[0-9]+')}">
															Score <input checked data-program="${studentMarks.program}" data-sapid="${studentMarks.sapid}" 
															       data-year="${studentMarks.year}" data-month="${studentMarks.month}" 
															       data-subject="${studentMarks.subject}"  data-sem="${studentMarks.sem}" 
															       data-studentType="${studentMarks.studentType}"
															       type="radio" value="Score" name = "writtenScore${status.count}" class = "markSingleSubjectRIANV" />
															
															</c:if>
															<c:if test="${!numberAsString.matches('[0-9]+')}">
															Score <input  data-program="${studentMarks.program}" data-sapid="${studentMarks.sapid}" 
															       data-year="${studentMarks.year}" data-month="${studentMarks.month}" 
															       data-subject="${studentMarks.subject}"  data-sem="${studentMarks.sem}" 
															       data-studentType="${studentMarks.studentType}"
															       type="radio" value="Score" name = "writtenScore${status.count}" class = "markSingleSubjectRIANV" />
															
															</c:if>
												            </td>
												        </tr>   
												    </c:forEach>
														<%}catch(Exception e){}	%>
													</tbody>
												</table>
								</div>
								</div>
								<br>
							
							</c:when>
						</c:choose>
								
							</div>
              			</div>
    				</div>
			   </div>
		    </div>
        <jsp:include page="adminCommon/footer.jsp"/>
        
        <script type="text/javascript">
   
        $('.markSingleSubjectRIANV').click(function(){
        	
        	var conf = confirm('Are you sure you want to edit this?');
        	if(conf == true){
        		$('.js_result').html('<h3>loading...</h3>');
        		if(this.checked){
        			var self = $(this);
                $.ajax({
                    type: "POST",
                    url: '/exam/admin/updateSubjectAsRIANV',
                    data: {
                    	'status' : $(this).attr('value'),
                    	'subject' : $(this).attr('data-subject'),
                    	'sem' : $(this).attr('data-sem'),
                    	'program' : $(this).attr('data-program'),
                    	'year' : $(this).attr('data-year'),
                    	'month' : $(this).attr('data-month'),
                    	'sapid' : $(this).attr('data-sapid'),
                    	'studentType' : $(this).attr('data-studentType'),
                    	
                    },
                    success:function(response){
                    	
                    	if(response.Status == "Success"){
                    		if(response.writtenScore == "RIA"){
                    			$('.js_result').html('<div class="alert alert-success alert-dismissible"> <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a> <strong>Success!</strong> updated result status to RIA. </div>');
                    		 self.parents('tr').children('.score').html('RIA');
                    		}
							if(response.writtenScore == "NV"){
								$('.js_result').html('<div class="alert alert-success alert-dismissible"> <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a> <strong>Success!</strong> updated result status to NV. </div>');
								 self.parents('tr').children('.score').html('NV');
                    		}
							if(response.writtenScore != "NV" && response.writtenScore != "RIA"){
								$('.js_result').html('<div class="alert alert-success alert-dismissible"> <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a> <strong>Success!</strong> updated all result status to marks. </div>');
								 self.parents('tr').children('.score').html(response.writtenScore);
                    		}
                    		
                    	}else{
                    		$('.js_result').html('<div class="alert alert-danger alert-dismissible"> <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a> <strong>Failed!</strong> update of result status. </div>');
                    	}
                   },
                   error:function(){
                	   $('.js_result').html('<div class="alert alert-danger alert-dismissible"> <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a> <strong>Failed!</strong> Server Error Found. </div>');
                   }
                });

        		}
        		}else{
        			this.checked = false;
        			var return_score = $(this).parent().parent().find('.score');
        			var status_count = return_score.attr('data-count');
        			$('input[type="radio"][name = "writtenScore"+status_count][value = "'+ return_score.html() +'"]').prop('checked', true);
            		console.log($(this).parent().parent().find('.score').html());
            		
        		}
        });

        
        $('.markAllRIANV').click(function(){
        	var conf = confirm('Are you sure you want to edit these?');
        	if(conf == true){
        	$('.js_result').html('<h3>loading...</h3>');
        		var self = $(this);
                 $.ajax({
                    type: "POST",
                    url: '/exam/admin/updateSubjectAsRIANV',
                    data: {
                    	'status' : $(this).attr('data-status'),
                    	'subject' : '',
                    	'sem' : $(this).attr('data-sem'),
                    	'program' : $(this).attr('data-program'),
                    	'year' : $(this).attr('data-year'),
                    	'month' : $(this).attr('data-month'),
                    	'sapid' : $(this).attr('data-sapid'),
                    	'studentType' : $(this).attr('data-studentType'),
                    }, 
                    success:function(response){
                    	if(response.Status == "Success"){
                    		if(response.writtenScore == "RIA"){
                    			$('.js_result').html('<div class="alert alert-success alert-dismissible"> <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a> <strong>Success!</strong> updated all result status to RIA. </div>');
                    		$('.score').html('RIA');
                    		$("input[type='radio'][value = 'NV']").removeAttr('checked');
                    		$("input[type='radio'][value = 'RIA']").prop('checked',true);
                    		}
							if(response.writtenScore == "NV"){
								$('.js_result').html('<div class="alert alert-success alert-dismissible"> <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a> <strong>Success!</strong> updated all result status to NV. </div>');
								$('.score').html('NV');
								$("input[type='radio'][value = 'RIA']").removeAttr('checked');
								$("input[type='radio'][value = 'NV']").prop('checked',true);
                    		}
                    	}else{
                    		$('.js_result').html('<div class="alert alert-danger alert-dismissible"> <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a> <strong>Failed!</strong> update of all result status. </div>');
                    	}
                   },
                   error:function(){
                	   $('.js_result').html('<div class="alert alert-danger alert-dismissible"> <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a> <strong>Failed!</strong> Server Error Found. </div>');
                   }
                });
        	}else{
        		$(this).parent().children('.score').html();
        		console.log($(this).parent().children('.score').html());
        		this.checked = false;
        		
        	}
        }); 
        
        </script>
		
    </body>
</html>