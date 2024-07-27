<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.AssignmentStatusBean"%>
<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="jscss.jsp">
<jsp:param value="Search Assignment Files" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="header.jsp"%>

    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Search Assignment Files</legend></div>
        <%@ include file="messages.jsp"%>
 <div class="row clearfix">
 <form:form  action="searchAssignmentFiles" method="post" modelAttribute="searchBean">
  <fieldset>
  <div class="col-md-6 column">

   
    <div class="form-group">
     <form:select id="year" path="year" type="text" placeholder="Year" class="form-control"   itemValue="${searchBean.year}">
      <form:option value="">Select Year</form:option>
      <form:options items="${yearList}" />
     </form:select>
    </div>
   
    <div class="form-group">
     <form:select id="month" path="month" type="text" placeholder="Month" class="form-control"  itemValue="${searchBean.month}">
      <form:option value="">Select Month</form:option>
      <form:option value="Apr">Apr</form:option>
      <form:option value="Jun">Jun</form:option>
      <form:option value="Sep">Sep</form:option>
      <form:option value="Dec">Dec</form:option>
     </form:select>
    </div>
    
    <div class="form-group"  style="overflow:visible;">
      <form:select id="subject" path="subject" class="combobox form-control"   itemValue="${searchBean.subject}">
       <form:option value="">Type OR Select Subject</form:option>
       <form:options items="${subjectList}" />
      </form:select>
    </div>     

   <!-- Button (Double) -->
   <div class="form-group">
    <label class="control-label" for="submit"></label>
    <div class="controls">
     <button id="submit" name="submit" class="btn btn-large btn-primary" formaction="searchAssignmentFiles">Search</button>
     <button id="reset" type="reset" class="btn btn-danger" type="reset">Reset</button>
     <button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
    </div>
   </div>

  

</div>



</fieldset>
 </form:form>
 
 </div>


<c:choose>
<c:when test="${rowCount > 0}">

<legend>&nbsp;Assignment Details & Files<font size="2px"> (${rowCount} Records Found)&nbsp; </font></legend>
<div class="table-responsive">
<table class="table table-striped table-hover" style="font-size:12px">
     <thead>
      <tr> 
       <th>Sr. No.</th>
       <th>Exam Year</th>
       <th>Exam Month</th>
       <th>Program</th>
       <th>Subject</th>
       <th>Start Date</th>
       <th>End Date</th>
       <th>File</th>
       <th>Actions</th>
      </tr>
     </thead>
     <tbody>
     
     <c:forEach var="assignmentFile" items="${assignmentFilesList}" varStatus="status">
            <tr>
                <td><c:out value="${status.count}"/></td>
       <td><c:out value="${assignmentFile.year}"/></td>
       <td><c:out value="${assignmentFile.month}"/></td>
       <td><c:out value="${assignmentFile.program}"/></td>
       <td nowrap="nowrap"><c:out value="${assignmentFile.subject}"/></td>
       <td><c:out value="${assignmentFile.startDate}"/></td>
       <td><c:out value="${assignmentFile.endDate}"/></td>
       <td><a href="#" onClick="window.open('<spring:eval expression="@propertyConfigurer.getProperty('ASSIGNMENT_QUESTION_PREVIEW_PATH')" />${assignmentFile.questionFilePreviewPath}')" />Download</a></td>
       
       <td> 
                 <c:url value="editAssignmentFileForm" var="editurl">
          <c:param name="year" value="${assignmentFile.year}" />
          <c:param name="month" value="${assignmentFile.month}" />
          <c:param name="subject" value="${assignmentFile.subject}" />
          <c:param name="program" value="${assignmentFile.program}" />
        </c:url>

        <%//if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1){ %>
         <a href="${editurl}" title="Edit"><i class="fa-solid fa-pen-to-square fa-lg"></i></a>
        <%//} %>
         
              </td>
                 
            </tr>   
        </c:forEach>
      
      
     </tbody>
    </table>
</div>
<br>

</c:when>
</c:choose>

<c:url var="firstUrl" value="searchAssignmentFilesPage?pageNo=1" />
<c:url var="lastUrl" value="searchAssignmentFilesPage?pageNo=${page.totalPages}" />
<c:url var="prevUrl" value="searchAssignmentFilesPage?pageNo=${page.currentIndex - 1}" />
<c:url var="nextUrl" value="searchAssignmentFilesPage?pageNo=${page.currentIndex + 1}" />


<c:choose>
<c:when test="${page.totalPages > 1}">
<div align="center">
    <ul class="pagination">
        <c:choose>
            <c:when test="${page.currentIndex == 1}">
                <li class="disabled"><a href="#">&lt;&lt;</a></li>
                <li class="disabled"><a href="#">&lt;</a></li>
            </c:when>
            <c:otherwise>
                <li><a href="${firstUrl}">&lt;&lt;</a></li>
                <li><a href="${prevUrl}">&lt;</a></li>
            </c:otherwise>
        </c:choose>
        <c:forEach var="i" begin="${page.beginIndex}" end="${page.endIndex}">
            <c:url var="pageUrl" value="searchAssignmentFilesPage?pageNo=${i}" />
            <c:choose>
                <c:when test="${i == page.currentIndex}">
                    <li class="active"><a href="${pageUrl}"><c:out value="${i}" /></a></li>
                </c:when>
                <c:otherwise>
                    <li><a href="${pageUrl}"><c:out value="${i}" /></a></li>
                </c:otherwise>
            </c:choose>
        </c:forEach>
        <c:choose>
            <c:when test="${page.currentIndex == page.totalPages}">
                <li class="disabled"><a href="#">&gt;</a></li>
                <li class="disabled"><a href="#">&gt;&gt;</a></li>
            </c:when>
            <c:otherwise>
                <li><a href="${nextUrl}">&gt;</a></li>
                <li><a href="${lastUrl}">&gt;&gt;</a></li>
            </c:otherwise>
        </c:choose>
    </ul>
</div>
</c:when>
</c:choose>
</div>

</section>

  <jsp:include page="footer.jsp" />


</body>
</html>
 --%>
 
 <!DOCTYPE html>
<html lang="en">

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.AssignmentStatusBean"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
    <jsp:include page="adminCommon/jscss.jsp">
<jsp:param value="Search Assignment Files" name="title"/>
    </jsp:include>
    <body>
     <%@ include file="adminCommon/header.jsp" %>
        <div class="sz-main-content-wrapper">
        
         <jsp:include page="adminCommon/breadcrum.jsp">
  <jsp:param value="Exam;Search Assignment Files" name="breadcrumItems"/>
  </jsp:include>
         
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
                  <jsp:include page="adminCommon/left-sidebar.jsp">
       <jsp:param value="" name="activeMenu"/>
      </jsp:include>
             <div class="sz-content-wrapper examsPage">
         <%@ include file="adminCommon/adminInfoBar.jsp" %>
         <div class="sz-content">
     <h2 class="red text-capitalize">Search Assignment Files</h2>
     <div class="clearfix"></div>
      <div class="panel-content-wrapper" style="min-height:450px;">
       <%@ include file="adminCommon/messages.jsp" %>
        <form:form  action="searchAssignmentFiles" method="post" modelAttribute="searchBean">
         <fieldset>
          <div class="col-md-4">
            <div class="form-group">
             <form:select id="year" path="year" type="text" placeholder="Year" class="form-control" required="required"  itemValue="${searchBean.year}">
              <form:option value="">Select Year</form:option>
              <form:options items="${yearList}" />
             </form:select>
            </div>
           
            <div class="form-group">
             <form:select id="month" path="month" type="text" placeholder="Month" class="form-control" required="required"  itemValue="${searchBean.month}">
              <form:option value="">Select Month</form:option>
              <form:option value="Apr">Apr</form:option>
              <form:option value="Jun">Jun</form:option>
              <form:option value="Sep">Sep</form:option>
              <form:option value="Dec">Dec</form:option>
             </form:select>
            </div>
            
            <%-- <div class="form-group"  style="overflow:visible;">
              <form:select id="subject" path="subject" class="combobox form-control"   itemValue="${searchBean.subject}">
               <form:option value="">Type OR Select Subject</form:option>
               <form:options items="${subjectList}" />
              </form:select>
            </div>  --%> 
            
                  <div class="form-group">
            <select data-id="consumerTypeDataId" id="consumerTypeId" name="consumerTypeId"  class="selectConsumerType form-control"  required="required">
             <option disabled selected value="">Select Consumer Type</option>
             <c:forEach var="consumerType" items="${consumerType}">
              <c:choose>
               <c:when test="${consumerType.id == searchBean.consumerTypeId}">
                <option selected value="<c:out value="${consumerType.id}"/>">
                              <c:out value="${consumerType.name}"/>
                            </option>
               </c:when>
               <c:otherwise>
                <option value="<c:out value="${consumerType.id}"/>">
                              <c:out value="${consumerType.name}"/>
                            </option>
               </c:otherwise>
              </c:choose>
     
                      </c:forEach>
            </select>
          </div>
          <div class="form-group">
            <select id="programStructureId" name="programStructureId"  class="selectProgramStructure form-control"  required="required">
             <option disabled selected value="">Select Program Structure</option>
            </select>
          </div>
          <div class="form-group">
            <select id="programId" name="programId"  class="selectProgram form-control"  required="required">
             <option disabled selected value="">Select Program</option>
            </select>
          </div> 
          
       <div class="form-group">
      <select id="subjectId" name="subject"  class="selectSubject form-control" >
       <option disabled selected value="">Select Subject</option>
      </select>
    </div>   

           <!-- Button (Double) -->
           <div class="form-group">
            <label class="control-label" for="submit"></label>
             <button id="submit" name="submit" class="btn btn-large btn-primary" formaction="searchAssignmentFiles?searchType=distinct">Search</button>
             <button id="submitAll" name="submitAll" class="btn btn-large btn-primary" formaction="searchAssignmentFiles?searchType=all">Search All</button>
             
             <button id="reset" type="reset" class="btn btn-danger" type="reset">Reset</button>
             <button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
           </div>
          </div> 
         </div>
         </fieldset>
        </form:form>
        </div>
    <c:choose>
    <c:when test="${rowCount > 0}">
<h2 style="margin-left:50px;">&nbsp;&nbsp;Assignment Details & Files<font size="2px"> (${rowCount} Records Found)&nbsp;</font></h2>
<div class="clearfix"></div>
 <div class="panel-content-wrapper">
 <div class="table-responsive">
<table class="table table-striped table-hover" style="font-size:12px">
     <thead>
      <tr> 
       <th>Sr. No.</th>
       <th>Exam Year</th>
       <th>Exam Month</th>
       <th>Consumer Type</th>
       <th>Program Structure</th>
       <th>Program</th>
       <th>Subject</th>
       <th>Start Date</th>
       <th>End Date</th>
       <th>File</th>
       <th>Actions</th>
      </tr>
     </thead>
     <tbody>
     
     <c:forEach var="assignmentFile" items="${assignmentFilesList}" varStatus="status">
            <tr>
                <td><c:out value="${status.count}"/></td>
       <td><c:out value="${assignmentFile.year}"/></td>
       <td><c:out value="${assignmentFile.month}"/></td>
       <c:choose>
         <c:when test="${assignmentFile.count > 1}">
         	<td colspan="3"><center><a href="javascript:void(0)" data-month="${ assignmentFile.month }" data-year="${ assignmentFile.year }" data-filePath="${ assignmentFile.filePath  }" data-consumerProgramStructureId="${assignmentFile.consumerProgramStructureId} " class="commonLinkbtn">Common file for <c:out value="${ assignmentFile.count }" /> programs </a></center></td>
         </c:when>
         <c:otherwise>
         	<td><c:out value="${assignmentFile.consumerType}"/></td>
	     	<td><c:out value="${assignmentFile.programStructure}"/></td>
	     	<td><c:out value="${assignmentFile.program}"/></td>
         </c:otherwise>
       </c:choose>
       <td nowrap="nowrap"><c:out value="${assignmentFile.subject}"/></td>
       <td><c:out value="${assignmentFile.startDate}"/></td>
       <td><c:out value="${assignmentFile.endDate}"/></td>
       <td>
       <%-- <a href="#" onClick="window.open('<spring:eval expression="@propertyConfigurer.getProperty('ASSIGNMENT_QUESTION_PREVIEW_PATH')" />${assignmentFile.questionFilePreviewPath}')" />Download</a>--%>
       <a href="<spring:eval expression="@propertyConfigurer.getProperty('ASSIGNMENT_FILES_PATH')" />${assignmentFile.questionFilePreviewPath}">Download</a>
       </td>
       
       <td> 
                 <c:url value="editAssignmentFileForm" var="editurl">
          <c:param name="year" value="${assignmentFile.year}" />
          <c:param name="month" value="${assignmentFile.month}" />
          <c:param name="subject" value="${assignmentFile.subject}" />
          <c:param name="consumerProgramStructureId" value="${assignmentFile.consumerProgramStructureId}" />
        </c:url>

        <%//if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1){ %>
         <a href="${editurl}" title="Edit"><i class="fa-solid fa-pen-to-square fa-lg"></i></a>
         <a href="javascript:void(0)" data-count="${assignmentFile.count}" data-month="${ assignmentFile.month }" data-year="${ assignmentFile.year }" data-filePath="${ assignmentFile.filePath  }" data-consumerProgramStructureId="${assignmentFile.consumerProgramStructureId} " class="deleteCommonAssignmentFile" title="Delete"><i class="fa-regular fa-trash-can fa-lg"></i></a>
        <%//} %>
         
              </td>
                 
            </tr>   
        </c:forEach>
     </tbody>
    </table>
   </div>
   </div>
   <br>
   </c:when>
    </c:choose>
       <c:url var="firstUrl" value="searchAssignmentFilesPage?pageNo=1&searchType=${searchType} " />
       <c:url var="lastUrl" value="searchAssignmentFilesPage?pageNo=${page.totalPages}&searchType=${searchType}" />
       <c:url var="prevUrl" value="searchAssignmentFilesPage?pageNo=${page.currentIndex - 1}&searchType=${searchType}" />
       <c:url var="nextUrl" value="searchAssignmentFilesPage?pageNo=${page.currentIndex + 1}&searchType=${searchType}" />
     <c:choose>
      <c:when test="${page.totalPages > 1}">
      <div align="center">
       <ul class="pagination">
        <c:choose>
         <c:when test="${page.currentIndex == 1}">
          <li class="disabled"><a href="#">&lt;&lt;</a></li>
          <li class="disabled"><a href="#">&lt;</a></li>
         </c:when>
         <c:otherwise>
          <li><a href="${firstUrl}">&lt;&lt;</a></li>
          <li><a href="${prevUrl}">&lt;</a></li>
         </c:otherwise>
        </c:choose>
        <c:forEach var="i" begin="${page.beginIndex}" end="${page.endIndex}">
         <c:url var="pageUrl" value="searchAssignmentFilesPage?pageNo=${i}" />
         <c:choose>
          <c:when test="${i == page.currentIndex}">
           <li class="active"><a href="${pageUrl}"><c:out value="${i}" /></a></li>
          </c:when>
          <c:otherwise>
           <li><a href="${pageUrl}"><c:out value="${i}" /></a></li>
          </c:otherwise>
         </c:choose>
        </c:forEach>
        <c:choose>
         <c:when test="${page.currentIndex == page.totalPages}">
          <li class="disabled"><a href="#">&gt;</a></li>
          <li class="disabled"><a href="#">&gt;&gt;</a></li>
         </c:when>
         <c:otherwise>
          <li><a href="${nextUrl}">&gt;</a></li>
          <li><a href="${lastUrl}">&gt;&gt;</a></li>
         </c:otherwise>
        </c:choose>
       </ul>
      </div>
      </c:when>
     </c:choose>

      </div>
                 </div>
        </div>
     </div>
     </div>
     <!-- Modal  -->
     <div id="myModal" class="modal fade" role="dialog">
  <div class="modal-dialog">

    <!-- Modal content-->
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">&times;</button>
        <h4 class="modal-title">Common Program List</h4>
      </div>
      <div class="modal-body modalBody">
      	
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
      </div>
    </div>

  </div>
</div>
<!-- End of modal -->
        <jsp:include page="adminCommon/footer.jsp"/>
        
        
        <script type="text/javascript">

$(document).ready(function() {
	
	$('.commonLinkbtn').on('click',function(){
		let filePath = $(this).attr('data-filePath');
		let year = $(this).attr('data-year');
		let month = $(this).attr('data-month');
		
		let consumerProgramStructureId = $(this).attr('data-consumerProgramStructureId');
		
		let modalBody = "<center><h4>Loading...</h4></center>";
		let data = {
			'month':month,
			'year':year,
			'filePath':filePath,
			'consumerProgramStructureId':consumerProgramStructureId
		};
		$.ajax({
			   type : "POST",
			   contentType : "application/json",
			   url : "getCommonAssignmentProgramsList",   
			   data : JSON.stringify(data),
			   success : function(data) {
				   modalBody = '<div class="table-responsive"> <table class="table"> <thead><td>Exam year</td><td>Exam month</td> <td>Consumer Type</td> <td>Program Structure</td> <td>Program</td> <td>Subject</td> <td>Action</td> </thead><tbody>';
				   var modalBody_tmp = "";
				   for(let i=0;i < data.length;i++){
					   modalBody_tmp = modalBody_tmp + '<tr><td>'+ data[i].year +'</td><td>'+ data[i].month +'</td><td>'+ data[i].consumerType +'</td><td>'+ data[i].programStructure +'</td><td>'+ data[i].program +'</td><td>'+ data[i].subject +'</td><td> <a href="editAssignmentFileForm?year='+ data[i].year +'&month='+ data[i].month +'&subject='+ data[i].subject +'&consumerProgramStructureId='+ data[i].consumerProgramStructureId +'" title="Edit"><i class="fa-solid fa-pen-to-square fa-lg"></i></a> <a href="javascript:void(0)" data-year="'+ data[i].year +'" data-month="'+ data[i].month +'" data-subject="'+ data[i].subject +'" data-consumerProgramStructureId="'+ data[i].consumerProgramStructureId +'" class="deleteAssignmentFile" title="Delete"><i class="fa-regular fa-trash-can fa-lg"></i></a></td></tr>';
				   }
				   
				   modalBody = modalBody + modalBody_tmp + '</tbody></table></div>';
				   $('.modalBody').html(modalBody);
			   },
			   error : function(e) {
				   alert("Please Refresh The Page.")
			   }
		});
		$('.modalBody').html(modalBody);
		//modal-body
		$('#myModal').modal('show');
	});
	
	$(document).on('click','.deleteAssignmentFile',function(){
		console.log("====>>>>>>> delete function call...");
		let year = $(this).attr('data-year');
		let month = $(this).attr('data-month');
		let subject = $(this).attr('data-subject');
		let consumerProgramStructureId = $(this).attr('data-consumerProgramStructureId');
		let data = {
			'month':month,
			'year':year,
			'subject':subject,
			'consumerProgramStructureId':consumerProgramStructureId
		};
		let self = $(this);
		let confirmResponse = confirm("Are you sure you want to delete?");                              
        if(confirmResponse){
        	$.ajax({
 			   type : "POST",
 			   contentType : "application/json",
 			   url : "deleteAssignmentRecordByFilter",   
 			   data : JSON.stringify(data),
 			   success : function(data) {
 				   if(data.status == 'success'){
 					   //code to remove html content
 					   self.parent().parent().remove();
 					   alert("Successfully record deleted for month: " + month + " | year: " + year + " | subject: " + subject);
 				   }else{
 					   alert("Failed to delete record, Try after sometime");
 				   }
 			   },
 			   error : function(e) {
 				   alert("Failed to delete record, Try after sometime");
 			   }
 			});
        }
		
	});
	
	$(document).on('click','.deleteCommonAssignmentFile',function(){
		console.log("====>>>>>>> delete function call...");
		let year = $(this).attr('data-year');
		let month = $(this).attr('data-month');
		let filePath = $(this).attr('data-filePath');
		let consumerProgramStructureId = $(this).attr('data-consumerProgramStructureId');
		let count = $(this).attr('data-count');
		let data = {
			'month':month,
			'year':year,
			'filePath':filePath,
			'consumerProgramStructureId':consumerProgramStructureId
		};
		let self = $(this);
		let confirmResponse = confirm("Are you sure you want to delete total: "+ count +" records?");                              
        if(confirmResponse){
        	$.ajax({
 			   type : "POST",
 			   contentType : "application/json",
 			   url : "deleteCommongAssignmentRecordByFilter",   
 			   data : JSON.stringify(data),
 			   success : function(data) {
 				   if(data.status == 'success'){
 					   //code to remove html content
 					   self.parent().parent().remove();
 					   alert(data.message);
 				   }else{
 					   alert("Failed to delete record, Try after sometime");
 				   }
 			   },
 			   error : function(e) {
 				   alert("Failed to delete record, Try after sometime");
 			   }
 			});
        }
		
	});
	
	
 var consumerTypeId = '${ searchBean.consumerTypeId }';
 var programStructureId = '${ searchBean.programStructureId }';
 var programId = '${ searchBean.programId }';
 var g_subject = '${ searchBean.subject }';
 
 function getConsumerTypeData(value,documentReady){
  let options = "<option>Loading... </option>";
  $('#programStructureId').html(options);
  $('#programId').html(options);
  $('.selectSubject').html(options);
  
   
  var data = {
    id:value
  }
 console.log(this.value)
  
  
  $.ajax({
   type : "POST",
   contentType : "application/json",
   url : "getDataByConsumerType",   
   data : JSON.stringify(data),
   success : function(data) {
    console.log("SUCCESS Program Structure: ", data.programStructureData);
    console.log("SUCCESS Program: ", data.programData);
    console.log("SUCCESS Subject: ", data.subjectsData);
    var programData = data.programData;
    var programStructureData = data.programStructureData;
    var subjectsData = data.subjectsData;
    
    options = "";
    let allOption = "";
    
    //Data Insert For Program List
    //Start
    
    
    for(let i=0;i < programData.length;i++){
     allOption = allOption + ""+ programData[i].id +",";
     if(programData[i].id == programId && documentReady){
     
      options = options + "<option selected value='" + programData[i].id + "'> " + programData[i].name + " </option>"; 
     }else{
      options = options + "<option value='" + programData[i].id + "'> " + programData[i].name + " </option>";
     }
     
    }
    allOption = allOption.substring(0,allOption.length-1);
    
    if(documentReady){
     options = "<option value='"+ allOption +"'>All</option>" + options;
    }else{
     options = "<option selected value='"+ allOption +"'>All</option>" + options;
    }
   
    console.log("==========> options\n" + options);
    $('#programId').html(
     options 
    );
    //End
    options = ""; 
    allOption = "";
    //Data Insert For Program Structure List
    //Start
    
    for(let i=0;i < programStructureData.length;i++){
     allOption = allOption + ""+ programStructureData[i].id +",";
     if(programStructureData[i].id == programStructureId && documentReady){
     
      options = options + "<option selected value='" + programStructureData[i].id + "'> " + programStructureData[i].name + " </option>"; 
     }else{
      options = options + "<option value='" + programStructureData[i].id + "'> " + programStructureData[i].name + " </option>";
     }
    }
    allOption = allOption.substring(0,allOption.length-1);
    
    console.log("==========> options\n" + options);
    if(documentReady){
     options = "<option value='"+ allOption +"'>All</option>" + options;
    }else{
     options = "<option selected value='"+ allOption +"'>All</option>" + options;
    }
    
    $('#programStructureId').html(
     options 
    );
    //End
    
    options = []; 
    options.push($('<option />').text('Select Subject').val(''))
    //Data Insert For Subjects List
    //Start
    for(let i=0;i < subjectsData.length;i++){
     if(g_subject == subjectsData[i].name){
      	options.push($('<option />').text(subjectsData[i].name).val(subjectsData[i].name).attr('selected', 'selected')); 
     }else{
    	 options.push($('<option />').text(subjectsData[i].name).val(subjectsData[i].name))
     }
    }
    
    
    console.log("==========> options\n" + options);
    $('.selectSubject').empty()
    $('.selectSubject').append(options);
    //End
    
    if(documentReady){
     console.log("-----------> inside function call...");
     getProgramStructureId(programStructureId,true);
    }
    
   },
   error : function(e) {
    
    alert("Please Refresh The Page.")
    
    console.log("ERROR: ", e);
    display(e);
   }
  });
 }
 
 function getProgramStructureId(value,documentReady){
  let options = "<option>Loading... </option>";
  $('#programId').html(options);
  $('.selectSubject').html(options);
  
   
  var data = {
    programStructureId:value,
    consumerTypeId:$('#consumerTypeId').val()
  }
  console.log(this.value)
  
  console.log("===================> data id : " + $('#consumerTypeId').val());
  $.ajax({
   type : "POST",
   contentType : "application/json",
   url : "getDataByProgramStructure",   
   data : JSON.stringify(data),
   success : function(data) {
    
    console.log("SUCCESS: ", data.programData);
    var programData = data.programData;
    var subjectsData = data.subjectsData;
    
    options = "";
    let allOption = "";
    
    //Data Insert For Program List
    //Start
    
    for(let i=0;i < programData.length;i++){
     allOption = allOption + ""+ programData[i].id +",";
     if(programData[i].id == programId && documentReady){
     
      options = options + "<option selected value='" + programData[i].id + "'> " + programData[i].name + " </option>"; 
     }else{
      options = options + "<option value='" + programData[i].id + "'> " + programData[i].name + " </option>";
     }
     //options = options + "<option value='" + programData[i].id + "'> " + programData[i].name + " </option>";
    }
    allOption = allOption.substring(0,allOption.length-1);
    
    console.log("==========> options\n" + options);
    if(documentReady){
     options = "<option value='"+ allOption +"'>All</option>" + options
    }else{
     options = "<option selected value='"+ allOption +"'>All</option>" + options
    }
    $('#programId').html(
      options
    );
    //End
    
    options = []; 
    options.push($('<option />').text('Select Subject').val(''))
    allOption = "";
    //Data Insert For Subjects List
    //Start
    for(let i=0;i < subjectsData.length;i++){
     if(g_subject == subjectsData[i].name){
    	 options.push($('<option />').text(subjectsData[i].name).val(subjectsData[i].name).attr('selected', 'selected')); 
     }else{
    	 options.push($('<option />').text(subjectsData[i].name).val(subjectsData[i].name))
     }
     //options = options + $('<option />').text(subjectsData[i].name).val(subjectsData[i].name);
    }
    

    console.log("==========> options\n" + options);
    $('.selectSubject').empty()
    $('.selectSubject').append(options);
    //End
    
    if(documentReady){
     console.log("-----------> inside function call...");
     getProgramIdData(programId,true);
    }
    
    
   },
   error : function(e) {
    
    alert("Please Refresh The Page.")
    
    console.log("ERROR: ", e);
    display(e);
   }
  });
 }
 
 function getProgramIdData(value,documentReady){
  let options = "<option>Loading... </option>";
  $('.selectSubject').html(options);
  
   
  var data = {
    programId:value,
    consumerTypeId:$('#consumerTypeId').val(),
    programStructureId:$('#programStructureId').val()
  }
  console.log(this.value)
  
  
  $.ajax({
   type : "POST",
   contentType : "application/json",
   url : "getDataByProgram",   
   data : JSON.stringify(data),
   success : function(data) {
    
    console.log("SUCCESS: ", data.subjectsData);
    
    var subjectsData = data.subjectsData;
    
    
    
    
    options = []; 
    options.push($('<option />').text('Select Subject').val(''))
    //Data Insert For Subjects List
    //Start
    for(let i=0;i < subjectsData.length;i++){
     if(g_subject == subjectsData[i].name){
      	options.push($('<option />').text(subjectsData[i].name).val(subjectsData[i].name).attr('selected', 'selected')); 
     }else{
    	 options.push($('<option />').text(subjectsData[i].name).val(subjectsData[i].name))
     }
    }
    
    
    console.log("==========> options\n" + options);
    $('.selectSubject').empty()
    $('.selectSubject').append(options);
    //End
    
    
    
    
   },
   error : function(e) {
    
    alert("Please Refresh The Page.")
    
    console.log("ERROR: ", e);
    display(e);
   }
  });
 }
 
 if(programStructureId != '' && consumerTypeId != ''){
  getConsumerTypeData(consumerTypeId,true);
 }
 
 
 $('.selectConsumerType').on('change', function(){
  console.log("-----> inside oncall");
  
  let id = $(this).attr('data-id');
  
  consumerTypeId=this.value;
 
  getConsumerTypeData(this.value,false);
  
  
 });
  
  ///////////////////////////////////////////////////////
  
  
   $('.selectProgramStructure').on('change', function(){
  
	   programStructureId=this.value;
    console.log("-----> inside oncall");
    getProgramStructureId(this.value,false);
  
  
  
  
  
 });

 /////////////////////////////////////////////////////////////

  
   $('.selectProgram').on('change', function(){
	   programId=this.value;
    console.log("-----> inside oncall");
    getProgramIdData(this.value,false);
  
  
  
   });
});

</script>
 
    </body>
</html>