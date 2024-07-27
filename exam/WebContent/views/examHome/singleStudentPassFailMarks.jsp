
<%@page import="org.apache.commons.lang3.math.NumberUtils"%>
<%@page import="com.nmims.beans.PassFailExamBean"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.nmims.beans.StudentExamBean"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%try{ %>

<%
ArrayList<PassFailExamBean> studentMarksListForPassFail = (ArrayList)request.getAttribute("studentMarksListForPassFail");
int sizeOfStudentMarkListForPassFail = 0;
if(null != studentMarksListForPassFail) {
	sizeOfStudentMarkListForPassFail = studentMarksListForPassFail.size();
}
int srNumber = 0;
StudentExamBean studentTemp = (StudentExamBean)request.getSession().getAttribute("studentExam");
%>

	
	


<div class="accordion accordion-flush " id="accordionFlushExample">
    <div class="accordion-item row m-0 rounded">
      <h2 class="accordion-header col-12">
        
       <a class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#flush-collapseTwo" aria-expanded="false" aria-controls="flush-collapseTwo">
         
          <h4 class="text-capitalize text-danger me-0 text-wrap ">
            <span class="text-uppercase fw-bold ">Pass Fail Status</span>&nbsp; &nbsp; <span class="text-success"><%=sizeOfStudentMarkListForPassFail %>&nbsp; Records Available</span> 
          </h4>
        </a>
      </h2>

     <div id="flush-collapseTwo" class="accordion-collapse collapse" data-bs-parent="#accordionFlushExample">
        <div class="accordion-body col-12">
           
        
          <%if(sizeOfStudentMarkListForPassFail ==0){ %>
            <div class="no-data-wrapper ">
              <p class="no-data text-center fs-5"><span class="icon-exams"></span>No Pass Fail Records </p>
            </div>
          <%} %>
          <%if(sizeOfStudentMarkListForPassFail > 0){ %>
          
          <div class="data-content">
            <%-- <div class="col-md-12 p-closed"> 
              <i class="icon-exams"></i>
              <h4><span><%=sizeOfStudentMarkListForPassFail %></span> Records Available<span class="expand">Expand to view all records</span></h4>
            </div> --%>
              <div class="table-responsive">
                <table class="table table-striped"  id="examHomePassFailTable">
                  <thead>
                    <tr>
                      <th class="text-center">Sr. No.</th>
                      <th class="text-left" >Subject</th>
                      <th class="text-center">Sem</th>
                      <th class="text-center">TEE Marks</th>
                      
                      <% if ( !"EPBM".equalsIgnoreCase(studentTemp.getProgram()) && !"MPDV".equalsIgnoreCase(studentTemp.getProgram())) {%>
                      <th class="text-center">Assignment Marks</th>
                      <th class="text-center">Grace Marks</th>
                      <% } %>
                      
                      <th class="text-center">Total Marks</th>
                    </tr>
                  </thead>
                  <tbody>
                      <%
                      for(PassFailExamBean bean : studentMarksListForPassFail){
                        srNumber++;
                      %>
                      <tr>
                        <td class="text-center"><%=srNumber %></td>
                        <td class="text-left nowrap"><%=bean.getSubject() %></td>
                        <td class="text-center"><%=bean.getSem() %></td>
                        <td class="text-center"><%=bean.getWrittenscore() %><sub>(<%=bean.getWrittenMonth() %>-<%=bean.getWrittenYear() %>)</sub></td>
                        
                        <% if ( !"EPBM".equalsIgnoreCase(studentTemp.getProgram()) && !"MPDV".equalsIgnoreCase(studentTemp.getProgram())) {%>
                        <td class="text-center"><%=bean.getAssignmentscore() %><sub>(<%=bean.getAssignmentMonth() %>-<%=bean.getAssignmentYear() %>)</sub></td>
                        <td class="text-center"><%=bean.getGracemarks()!=null ? bean.getGracemarks():""  %></td>
                        <% } %>
                      
                        <%if("Y".equals(bean.getIsPass())){ %>
                          <td class="text-center text-success"><b><%=bean.getTotal()%></b></td>
                        <%}else if("ANS".equals(bean.getAssignmentscore()) && NumberUtils.isNumber(bean.getWrittenscore())) {%>
                        <td class="text-center"><b>On Hold (Assignment Not Submitted)</b></td>
                        <%}else if("N".equals(bean.getIsPass()) && "Copy Case".equals(bean.getRemarks())){ %>
                        <td class="text-center text-danger"><b>Copy Case</b></td>
                        <%}else if("N".equals(bean.getIsPass())){ %>
                        <td class="text-center text-danger"><b><%=bean.getTotal()%></b></td>
                        <%} %>
                        
                      </tr>
                    <%} %>
                  </tbody>
                </table>
                </div>
          </div>
          <%} %>
          


        </div><!--end of accordian body-->
      </div><!--end of accordian item-->
    </div>
    
  </div><!--end of accordian-->



	
	
	
	
	

<%}catch(Exception e){
	}%>


	<script>
	<!-- datatable js -->
	$(document).ready(function () {
	    $('#examHomePassFailTable').DataTable();
	});

	</script>
	
