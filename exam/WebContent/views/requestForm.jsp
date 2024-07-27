 <!DOCTYPE html>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>


	
<html lang="en">
	
    
    <jsp:include page="common/jscss.jsp">
	<jsp:param value="Select subjects for Exam" name="title"/>
    </jsp:include>
    
    
    <body>
    
    	<%@ include file="common/header.jsp" %>
    	
    	
        
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="common/breadcrum.jsp">
			<jsp:param value="Student Zone;Exams;Exam Registration" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="common/left-sidebar.jsp">
								<jsp:param value="Exam Registration" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="common/studentInfoBar.jsp" %>
              						
              						
              						<div class="sz-content">
              						<%-- <c:if test="${student.centerName eq 'Verizon'}">
              						   <font color="red" size="5"><b>Exam Registration is not Live currently</b></font>
              						</c:if> --%>
              						<%@ include file="common/messages.jsp" %>
          							<c:if test="${ errorFlag }">
          								<table class="table alert-danger">
          									<thead>
          										<tr>
          										<th>year</th>
          										<th>month</th>
          										<th>subject</th>
          										</tr>
          									</thead>
          									<tbody>
          										<c:forEach var="errorBookingBean" items="${ errorBookingBeanList }">
          										<tr>
          											<td>${ errorBookingBean.year }</td>
          											<td>${ errorBookingBean.month }</td>
          											<td>${ errorBookingBean.subject }</td>
          										</tr>
          										</c:forEach>
          									</tbody>
          								</table>
          								
          								
          							</c:if>    						
          
	             				</div>
	             			</div>
	             		</div>
	             	</div>
	             </div>
	            </body>
	           </html>