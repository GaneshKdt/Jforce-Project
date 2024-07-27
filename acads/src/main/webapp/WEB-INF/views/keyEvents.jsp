<!DOCTYPE html>


<%@page import="java.util.Date"%> 
<%@page import="java.text.DateFormat"%>
<%@page import="java.text.SimpleDateFormat"%>

<%@page import="com.nmims.beans.EventBean"%>
<%@page import="java.util.ArrayList"%>
<%
	ArrayList<EventBean> eventsList= (ArrayList<EventBean>)request.getSession().getAttribute("eventsList"); 
%>

<html lang="en">
    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
    
    <jsp:include page="common/jscss.jsp">
	<jsp:param value="Welcome to Student Zone" name="title"/>
    </jsp:include>
    
   
    <body>
    	
    	<%@ include file="common/header.jsp" %>
        
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="common/breadcrum.jsp">
		<jsp:param value="Student Zone;Key Events" name="breadcrumItems"/>
		</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<%@ include file="common/left-sidebar.jsp" %>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="common/studentInfoBar.jsp" %>
              						
              						
              						<div class="sz-content">
									
										<h2 class="red text-capitalize" style="margin-bottom:20px;">Key Events</h2><br><br><br> 
									<%
									  	if(!eventsList.isEmpty() && eventsList.size() > 0 && eventsList != null){
									   		for(EventBean event: eventsList){
									   			DateFormat formatForEvents=new SimpleDateFormat("E, dd-MMMM-yyyy  HH:mm");
									   			DateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm");
												Date newStartDate= df.parse(event.getStartDateTime());
												Date newEndDate= df.parse(event.getEndDateTime()); 
									%>
										    	<div class="well" style="background-color:white;"> 
										        	<h4 style=" "><%=event.getEventName() %></h4>
										        	<p style="font-family:allerlight; font-size:18px; font-weight:bold;"><%= formatForEvents.format(newStartDate) %> to <%=formatForEvents.format(newEndDate) %> </p>
										        	<h4 style="font-size:15px;"><%=event.getDescription() %></h4> 
										        </div>
										    <%
										    		}
										    	}
											%> 
									
									<%-- 
									<table class="table table-striped" style="font-size:12px">
										    <thead>
										      <tr>
										        <th>Event Name</th>
										        <th>Event Description </th>
										        <th>Start Date</th>
										        <th>Start Time</th>
										        <th>End Date</th>
										        <th>End Time</th>
										      </tr>
										    </thead>
										    <tbody> 
										    <%
										    	if(!eventsList.isEmpty() && eventsList.size() > 0 && eventsList != null){
										    		for(EventBean event: eventsList){
										    %>
										    	<tr> 
										        	<td><%=event.getEventName() %></td>
										        	<td><%=event.getDescription() %></td>
										        	<td><%=event.getStartDateTime().split(" ")[0] %></td>
										        	<td><%=event.getStartDateTime().split(" ")[1] %></td>
										        	<td><%=event.getEndDateTime().split(" ")[0] %></td>
										        	<td><%=event.getEndDateTime().split(" ")[1] %></td>
										        </tr>
										    <%
										    		}
										    	}
											%>     
										    </tbody>
										  </table>
     --%>          								
              						</div>
              						
              				</div>
              		
                            
					</div>
					 
            </div>
        </div>
        
            
  	
        <jsp:include page="common/footer.jsp"/>
            
		
    </body>
</html>