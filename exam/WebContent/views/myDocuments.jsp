<!DOCTYPE html>

<%@page import="com.nmims.beans.StudentExamBean"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.nmims.beans.ExamBookingTransactionBean"%>
<%@page import="com.nmims.beans.ServiceRequestBean"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>



	
<html lang="en">
	
    
    <jsp:include page="common/jscss.jsp">
	<jsp:param value="My Documents" name="title"/>
    </jsp:include>  
    <body>
    
    	<%@ include file="common/header.jsp" %>
    	
    	
        
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="common/breadcrum.jsp">
			<jsp:param value="Student Zone;My Documents" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
                    
                    	<div id="sticky-sidebar">  
	              				<jsp:include page="common/left-sidebar.jsp">
									<jsp:param value="My Documents" name="activeMenu"/>
								</jsp:include>
              				</div>	
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="common/studentInfoBar.jsp" %>
              						
              						
              						<div class="sz-content">
              						<%@ include file="common/messages.jsp" %>
									
										<h2 class="red text-capitalize">My Documents</h2>
										<div class="clearfix"></div>
	             						<div class="panel-content-wrapper">
	             						
	             						<%try{ %>
	             						
	             						<%@include file="myAdmissionFeeReceipt.jsp" %>
	             						
										<%@include file="myFeeReceipts.jsp" %>
										
										<%@include file="mySrFeeReceipts.jsp" %>
										
										<%@include file="myPCPBookings.jsp" %>
										
										<%@include file="assignment/myAssignmentFeeReceipts.jsp" %>
										
										<%@include file="project/myProjectFeeReceipts.jsp" %>
										
										<%-- <%@include file="myHallTickets.jsp" %> --%>
										
										<%@include file="mySrDocuments.jsp" %>
										
										<%}catch(Exception e){
											//e.printStackTrace();
	             						}%>
	             						<%try{ %>
	             							<%@include file="myIdCard.jsp" %>
										<%}catch(Exception e){
	             						}%>
										</div>
              								
              							
              						</div>
              				</div>
              		
                            
					</div>
            </div>
        </div>
            
  	
        <jsp:include page="common/footer.jsp"/>
            
		
    </body>
</html>