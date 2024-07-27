
 
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
     <h2 class="red text-capitalize">Download Assignment Evaluation Report</h2>
     <div class="clearfix"></div>
      <div class="panel-content-wrapper" style="min-height:450px;">
       <%@ include file="adminCommon/messages.jsp" %>
        <form:form  action="asssignmentEvalReportWithMarksBifurcationNQuestionRemarks" method="post" modelAttribute="assignmentStatus">
         <fieldset>
          <div class="col-md-4">
            <div class="form-group">
             <form:select id="year" path="year" type="text" placeholder="Year" class="form-control" required="required"  >
              <form:option value="">Select Year</form:option>
              <form:options items="${yearList}" />
             </form:select>
            </div>
           
            <div class="form-group">
             <form:select id="month" path="month" type="text" placeholder="Month" class="form-control" required="required"  >
              <form:option value="">Select Month</form:option>
              <form:option value="Apr">Apr</form:option>
              <form:option value="Jun">Jun</form:option>
              <form:option value="Sep">Sep</form:option>
              <form:option value="Dec">Dec</form:option>
             </form:select>
            </div>
             
                
           <!-- Button (Double) -->
           <div class="form-group">
            <label class="control-label" for="submit"></label>
             <button id="submitAll" name="submitAll" class="btn btn-large btn-primary" formaction="asssignmentEvalReportWithMarksBifurcationNQuestionRemarks">Download</button>
              
	           </div>
	          </div>  
	         </fieldset>
	        </form:form>
	        </div> 
	      </div>
	      </div>
         </div>
        </div>
     </div>  
   
<!-- End of modal -->
        <jsp:include page="adminCommon/footer.jsp"/>
         
 
    </body>
</html>