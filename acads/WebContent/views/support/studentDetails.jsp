<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 


<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.nmims.beans.SessionDayTimeAcadsBean"%>

<jsp:include page="../jscss.jsp">
<jsp:param value="Student Academic Data" name="title" />
</jsp:include>

<%
String sapId = request.getParameter("sapId");
ArrayList<SessionDayTimeAcadsBean> scheduledSessionList = (ArrayList<SessionDayTimeAcadsBean>)request.getAttribute("studentSessionList");
Date dt = new Date();

SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
String today = sdf.format(dt);

%>


<body class="inside">

	
    <section class="content-container login">
        <div class="container-fluid customTheme">
       
        
		
		<div class="panel-body">
		
			<!-- Calendar Section Start -->
			<div id="accordionCalendar">
				<h3>&nbsp;Session Calendar</h3>
				<div class="row">
        			<%@ include file="../messages.jsp"%>
			        <div class="column col-md-10">
			        <div id='calendar' align="left"></div><br>
			        </div>
			        
			        <div class="column col-md-8">
			        <iframe id="sessionFrame" src="" width="100%" seamless="seamless" height="600" frameborder="0"></iframe>
			        </div>
		        
		        </div>
			</div>
			<!--  Calendar section End -->
			<br/>
		
		
		</div>
	
	
		</div>
	</section>
	
	<!-- <script src="resources_2015/js/vendor/jquery-1.11.2.min.js"></script>
	<script src="resources_2015/js/vendor/bootstrap.min.js"></script>
	<script src="resources_2015/js/vendor/jquery-ui.min.js"></script>
	<script src="resources_2015/js/vendor/jquery.validate.min.js"></script>
	<script src="resources_2015/js/vendor/additional-methods.min.js"></script>
	<script src="resources_2015/js/vendor/fileinput.min.js"></script>
	<script src="resources_2015/js/vendor/bootstrap-datepicker.min.js"></script>
	<script src="resources_2015/js/vendor/scripts.js"></script>
	<script src="resources_2015/js/main.js?id=1"></script>
	<script src="resources_2015/js/vendor/moment.min.js"></script>
	<script src="resources_2015/js/vendor/fullcalendar.min.js"></script> -->
	
	<%@include file="../footer.jsp" %>
 
 
	<script>
	  $(function() {
	    $( "#accordionCalendar" ).accordion({
	      collapsible: true,
	      heightStyle: "content",
	      active:false
	    });
	    
	  });
	  
	  
	  $("#accordionCalendar" ).click(function() {
		  showCalendar();
	  });
	  
	  </script>
	  
	  

 <script>

	function showCalendar(){
		
		$('#calendar').fullCalendar({
			header: {
				left: 'prev,next today',
				center: 'title',
				right: 'month,agendaWeek,agendaDay'
			},
			defaultDate: '<%=today%>',
			editable: false,
			eventLimit: true, // allow "more" link when too many events
			events: [
			     <% for(int i = 0 ; i < scheduledSessionList.size(); i++){
			    	 SessionDayTimeAcadsBean bean = scheduledSessionList.get(i);
			     %>    
			         
			         
				{
					title: '<%=bean.getSubject().replaceAll("'", "") + " - "+ bean.getSessionName()%>',
					start: '<%=bean.getDate()+"T"+ bean.getStartTime()%>',
					url: 'viewScheduledSession?id=<%=bean.getId()%>',
				},
				
				<%}%>
			],
			eventClick: function(event) {
		        if (event.url) {
		        	document.getElementById("sessionFrame").src=event.url;
		            //window.open(event.url);
		            return false;
		        }
		    }
		});
		
	};

	
	
</script>
 
 
</body>
</html>
